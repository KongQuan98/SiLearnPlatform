package com.example.imagepro;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ObjectDetectorClass {
    // should start from small letter

    // this is used to load model and predict
    private Interpreter interpreter;
    private Interpreter interpreter2;
    // store all label in array
    private List<String> labelList;
    private int INPUT_SIZE;
    private int PIXEL_SIZE = 3; // for RGB
    private int IMAGE_MEAN = 0;
    private float IMAGE_STD = 255.0f;
    // use to initialize gpu in app
    private GpuDelegate gpuDelegate;
    private int height = 0;
    private int width = 0;
    private int classification_InputSize = 0;

    private String text = "";
    private String current_text = "";

    private String genreNow;

    ObjectDetectorClass(RecognitionModel rm, AssetManager assetManager, String modelPath, String labelPath, int inputSize, String classicfication_model,
                        int classification_inputSize, String number_model, String greeting_model, String alphabet_model) throws IOException {

        INPUT_SIZE = inputSize;
        classification_InputSize = classification_inputSize;
        // use to define gpu or cpu // no. of threads
        Interpreter.Options options = new Interpreter.Options();
        gpuDelegate = new GpuDelegate();
        options.addDelegate(gpuDelegate);
        options.setNumThreads(4); // set it according to your phone
        // loading model
        interpreter = new Interpreter(loadModelFile(assetManager, modelPath), options);
        // load labelmap
        labelList = loadLabelList(assetManager, labelPath);

        Interpreter.Options options2 = new Interpreter.Options();
        options2.setNumThreads(2);

        // get things from camera layout
        TextView switchButton = rm.getSwitchButton();
        String mode = rm.getMode();
        String genre = rm.getGenre();
        String checkSign = rm.getCheckSign();
        Button start_button = rm.getStart_button();
        Button clear_button = rm.getClear_button();
        Button add_button = rm.getAdd_button();
        TextView text_change = rm.getText_change();
//        FrameLayout videoLayout = rm.getFrameLayout();
//        YouTubePlayerView video = rm.getYouTubePlayerView();
        LinearLayout bottomLayout = rm.getBottomLayout();
        List<String> quizList = new ArrayList<>();
        int count = rm.getCount();

//        String finalVideoKey = rm.getVideoKey();

        // set model to use
        if (genre.contains("Numbers")) {
            genreNow = genre;
            interpreter2 = new Interpreter(loadModelFile(assetManager, number_model), options2);
        } else if (genre.contains("Greeting")) {
            genreNow = genre;
            interpreter2 = new Interpreter(loadModelFile(assetManager, greeting_model), options2);
        } else if (genre.contains("Alphabet")) {
            genreNow = genre;
            interpreter2 = new Interpreter(loadModelFile(assetManager, alphabet_model), options2);
        } else {
            if (!rm.getCheckList().isEmpty()) {
                String[] numArray = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                String[] alphaArray = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
                String[] greetArray = new String[]{"Hello", "Thank You", "Good Bye", "Please", "Sorry"};

                List<String> numList = new ArrayList<>(Arrays.asList(numArray));
                List<String> alphaList = new ArrayList<>(Arrays.asList(alphaArray));
                List<String> greetList = new ArrayList<>(Arrays.asList(greetArray));

                if (numList.contains(rm.getCheckList().get(0))) {
                    genreNow = "Numbers";
                    interpreter2 = new Interpreter(loadModelFile(assetManager, number_model), options2);
                } else if (greetList.contains(rm.getCheckList().get(0))) {
                    genreNow = "Greeting";
                    interpreter2 = new Interpreter(loadModelFile(assetManager, greeting_model), options2);
                } else if (alphaList.contains(rm.getCheckList().get(0))) {
                    genreNow = "Alphabet";
                    interpreter2 = new Interpreter(loadModelFile(assetManager, alphabet_model), options2);
                }
            } else {
                genreNow = genre;
                interpreter2 = new Interpreter(loadModelFile(assetManager, classicfication_model), options2);
            }
        }

        // display output of recognition
        if (checkSign.equals("") && rm.getCheckList().isEmpty()) {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    add_button.performClick();
                    handler.postDelayed(this, 3000);
                }
            };

            // one word recognition mode
            if (mode.equals("One")) {
                clear_button.setVisibility(View.INVISIBLE);
                start_button.setVisibility(View.INVISIBLE);

                handler.removeCallbacksAndMessages(runnable);

                add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        text_change.setText(current_text);
                    }
                });

                handler.postDelayed(runnable, 3000);

            }
            // combine word recognition mode
            else {
                clear_button.setVisibility(View.VISIBLE);
                start_button.setVisibility(View.VISIBLE);

                handler.removeCallbacksAndMessages(runnable);

                clear_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Cleared Successfully", Toast.LENGTH_SHORT).show();
                        text = "";
                        text_change.setText(text);
                    }
                });

                add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        text = text + current_text;
                        text_change.setText(text);
                        if (text.equals("LEARN")) {
                            v.getContext().startActivity(new Intent(v.getContext(), LearningClass.class));
                        } else if (text.equals("COMMUNITY")) {
                            v.getContext().startActivity(new Intent(v.getContext(), CommunityClass.class));
                        } else if (text.equals("PROFILE")) {
                            v.getContext().startActivity(new Intent(v.getContext(), ProfileActivity.class));
                        } else if (text.equals("LOGOUT")) {
                            FirebaseAuth.getInstance().signOut();
                            v.getContext().startActivity(new Intent(v.getContext(), LoginActivity.class));
                        }
                    }
                });

                start_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (start_button.getText().equals("Start")) {
                            Toast.makeText(v.getContext(), "Start Recognizing Words", Toast.LENGTH_SHORT).show();
                            start_button.setText("Stop");
                            start_button.setBackgroundResource(R.drawable.button_bg_dark);
                            handler.postDelayed(runnable, 3000);
                        } else {
                            Toast.makeText(v.getContext(), "Stop Recognizing Words", Toast.LENGTH_SHORT).show();
                            start_button.setText("Start");
                            start_button.setBackgroundResource(R.drawable.button_bg);
                            handler.removeCallbacks(runnable);
                        }

                    }
                });
            }
        }
        // learning function output
        else {
            clear_button.setVisibility(View.INVISIBLE);
            start_button.setVisibility(View.INVISIBLE);
            bottomLayout.setVisibility(View.INVISIBLE);
//            videoLayout.setVisibility(View.VISIBLE);
//
//            video.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//                @Override
//                public void onReady(YouTubePlayer youTubePlayer) {
//                    super.onReady(youTubePlayer);
//                    String id = finalVideoKey;
//                    youTubePlayer.loadVideo(id, 0);
//                }
//            });

            if (switchButton != null) {
                switchButton.setVisibility(View.INVISIBLE);
            }

            if (!rm.getCheckList().isEmpty()) {

                quizList = rm.getCheckList();
                checkSign = quizList.get(count);

                text_change.setTextSize(15);
                text_change.setText("Quiz " + (count + 1) + ": Please wave the sign of " + checkSign);

                // 3 seconds handler
                final Boolean[] endPostDelayed = {false};
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        add_button.performClick();
                        if (endPostDelayed[0] == true) {
                            handler.removeCallbacks(this);
                        } else {
                            handler.postDelayed(this, 3000);
                        }
                    }
                };
                List<String> checkList = new ArrayList<>();
                checkList.add(null);

                // do the checking
                String finalCheckSign = checkSign;
                List<String> finalQuizList = quizList;
                add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String before = current_text;
                        if (checkList.get(checkList.size() - 1) != null) {
                            if (before.equals(checkList.get(checkList.size() - 1))) {
                                text_change.setTextSize(15);
                                text_change.setText("Congratulations! You are correct!");
                                endPostDelayed[0] = true;
                                final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int passCount = count + 1;

                                        if (passCount < 5) {
                                            Intent intent = new Intent(v.getContext().getApplicationContext(), CameraActivity.class);
                                            intent.putExtra("count", passCount);
                                            intent.putStringArrayListExtra("list", (ArrayList<String>) finalQuizList);
                                            v.getContext().startActivity(intent);
                                        } else {
                                            text_change.setTextSize(15);
                                            text_change.setText("Congratulations! You have completed the Quizzes!");

                                            add_button.setVisibility(View.VISIBLE);
                                            bottomLayout.setVisibility(View.VISIBLE);
                                            add_button.setText("Done");
                                            add_button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(v.getContext(), QuizActivity.class);
                                                    v.getContext().startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                }, 1500);
                            } else {
                                text_change.setTextSize(15);
                                text_change.setText("Quiz " + (count + 1) + ": Please wave the sign of " + finalCheckSign);
                                checkList.clear();
                                checkList.add(null);
                            }
                        } else {
                            if (before.equals(finalCheckSign)) {
                                text_change.setTextSize(15);
                                text_change.setText("Please maintain the sign for 3 seconds");
                                checkList.add(before);
                            } else {
                                text_change.setTextSize(15);
                                text_change.setText("Quiz " + (count + 1) + ": Please wave the sign of " + finalCheckSign);
                            }
                        }
                    }
                });

                handler.postDelayed(runnable, 3000);

            } else {
                text_change.setText("Please Wave Your Sign");

                // 3 seconds handler
                final Boolean[] endPostDelayed = {false};
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        add_button.performClick();
                        if (endPostDelayed[0] == true) {
                            handler.removeCallbacks(this);
                        } else {
                            handler.postDelayed(this, 3000);
                        }
                    }
                };
                List<String> checkList = new ArrayList<>();
                checkList.add(null);

                String finalCheckSign1 = checkSign;
                add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String before = current_text;
                        if (checkList.get(checkList.size() - 1) != null) {
                            if (before.equals(checkList.get(checkList.size() - 1))) {
                                text_change.setTextSize(15);
                                text_change.setText("Congratulations! You are correct!");
                                endPostDelayed[0] = true;
                                final Handler handler2 = new Handler(Looper.getMainLooper());
                                handler2.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(v.getContext().getApplicationContext(), LearningClass.class);
                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("alphabet", finalCheckSign1);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        intent.putExtra("checkSign", jsonObject.toString());
                                        v.getContext().startActivity(intent);
                                    }
                                }, 1500);
                            } else {
                                text_change.setTextSize(20);
                                text_change.setText("Please Wave Your Sign");
                                checkList.clear();
                                checkList.add(null);
                            }
                        } else {
                            if (before.equals(finalCheckSign1)) {
                                text_change.setTextSize(15);
                                text_change.setText("Please maintain the sign for 3 seconds");
                                checkList.add(before);
                            } else {
                                text_change.setTextSize(20);
                                text_change.setText("Please Wave Your Sign");
                            }
                        }
                    }
                });

                handler.postDelayed(runnable, 3000);
            }
        }
    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        // to store label
        List<String> labelList = new ArrayList<>();
        // create a new reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        // loop through each line and store it to labelList
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private ByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        // use to get description of file
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // create new Mat function
    public Mat recognizeImage(Mat mat_image) {
        // Rotate original image by 90 degree get get portrait frame

        // This change was done in video: Does Your App Keep Crashing? | Watch This Video For Solution.
        // This will fix crashing problem of the app

        Mat rotated_mat_image = new Mat();

        Mat a = mat_image.t();
        Core.flip(a, rotated_mat_image, 1);
        // Release mat
        a.release();

        // if you do not do this process you will get improper prediction, less no. of object
        // now convert it to bitmap
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(rotated_mat_image.cols(), rotated_mat_image.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rotated_mat_image, bitmap);
        // define height and width
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        // scale the bitmap to input size of model
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

        // convert bitmap to bytebuffer as model input should be in it
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaledBitmap);

        // defining output
        // 10: top 10 object detected
        // 4: there coordinate in image
        //  float[][][]result=new float[1][10][4];
        Object[] input = new Object[1];
        input[0] = byteBuffer;

        Map<Integer, Object> output_map = new TreeMap<>();
        // we are not going to use this method of output
        // instead we create treemap of three array (boxes,score,classes)

        float[][][] boxes = new float[1][10][4];
        // 10: top 10 object detected
        // 4: there coordinate in image
        float[][] scores = new float[1][10];
        // stores scores of 10 object
        float[][] classes = new float[1][10];
        // stores class of object

        // add it to object_map;
        output_map.put(0, boxes);
        output_map.put(1, classes);
        output_map.put(2, scores);

        // now predict
        new Thread(() -> {
            interpreter.runForMultipleInputsOutputs(input, output_map);
        });

        // Before watching this video please watch my previous 2 video of
        //      1. Loading tensorflow lite model
        //      2. Predicting object
        // In this video we will draw boxes and label it with it's name

        Object value = output_map.get(0);
        Object Object_class = output_map.get(1);
        Object score = output_map.get(2);

        // loop through each object
        // as output has only 10 boxes
        for (int i = 0; i < 10; i++) {
            float class_value = (float) Array.get(Array.get(Object_class, 0), i);
            float score_value = (float) Array.get(Array.get(score, 0), i);
            // define threshold for score

            // Here you can change threshold according to your model
            // Now we will do some change to improve app
            if (score_value > 0.5) {
                Object box1 = Array.get(Array.get(value, 0), i);
                // we are multiplying it with Original height and width of frame

                float y1 = (float) Array.get(box1, 0) * height;
                float x1 = (float) Array.get(box1, 1) * width;
                float y2 = (float) Array.get(box1, 2) * height;
                float x2 = (float) Array.get(box1, 3) * width;

                if (y1 < 0) {
                    y1 = 0;
                }
                if (x1 < 0) {
                    x1 = 0;
                }
                if (x2 > width) {
                    x2 = width;
                }
                if (y2 > height) {
                    y2 = height;
                }

                float w1 = x2 - x1;
                float h1 = y2 - y1;

                Rect cropped_roi = new Rect((int) x1, (int) y1, (int) w1, (int) h1);
                Mat cropped = new Mat(rotated_mat_image, cropped_roi).clone();

                Bitmap bitmap1 = null;
                bitmap1 = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(cropped, bitmap1);

                Bitmap scaledBitmap1 = Bitmap.createScaledBitmap(bitmap1, classification_InputSize, classification_InputSize, false);
                ByteBuffer byteBuffer1 = convertBitmapToByteBuffer1(scaledBitmap1);

                float[][] output_class_value = new float[1][1];

                interpreter2.run(byteBuffer1, output_class_value);

                Log.d("objectDetectionClass", "outputclassvalue: " + output_class_value[0][0]);

                String sign_val = "";
                if (genreNow.contains("Numbers")) {
                    sign_val = get_number(output_class_value[0][0]);
                } else if (genreNow.contains("Greeting")) {
                    sign_val = get_greetings(output_class_value[0][0]);
                } else if (genreNow.contains("Alphabet")) {
                    sign_val = get_alphabets(output_class_value[0][0]);
                } else {
                    sign_val = get_all(output_class_value[0][0]);
                }
                current_text = sign_val;
                Imgproc.putText(rotated_mat_image, "" + sign_val, new Point(x1 + 10, y1 + 40), 2, 1.5, new Scalar(255, 0, 0, 0), 2);

                // draw rectangle in Original frame //  starting point    // ending point of box  // color of box       thickness
                Imgproc.rectangle(rotated_mat_image, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 255, 0, 255), 2);

            }

        }

        Mat b = rotated_mat_image.t();
        Core.flip(b, mat_image, 0);
        b.release();
        // Now for second change go to CameraBridgeViewBase
        return mat_image;
    }

    private String get_greetings(float v) {
        String val = "";
        if (v >= -0.5 & v < 0.5) {
            val = "Good Bye";
        } else if (v >= 0.5 & v < 1.5) {
            val = "Hello";
        } else if (v >= 1.5 & v < 2.5) {
            val = "Please";
        } else if (v >= 2.5 & v < 3.5) {
            val = "Sorry";
        } else if (v >= 3.5 & v < 4.5) {
            val = "Thank You";
        } else {
            val = "No sign detected";
        }
        return val;
    }

    private String get_number(float v) {
        String val = "";
        if (v >= -0.5 & v < 0.5) {
            val = "1";
        } else if (v >= 0.5 & v < 1.5) {
            val = "2";
        } else if (v >= 1.5 & v < 2.5) {
            val = "3";
        } else if (v >= 2.5 & v < 3.5) {
            val = "4";
        } else if (v >= 3.5 & v < 4.5) {
            val = "5";
        } else if (v >= 4.5 & v < 5.5) {
            val = "6";
        } else if (v >= 5.5 & v < 6.5) {
            val = "7";
        } else if (v >= 6.5 & v < 7.5) {
            val = "8";
        } else if (v >= 7.5 & v < 8.5) {
            val = "9";
        } else if (v >= 8.5 & v < 9.5) {
            val = "10";
        } else {
            val = "No sign detected";
        }
        return val;
    }

    private String get_alphabets(float v) {
        String val = "";
        if (v >= -0.5 & v < 0.5) {
            val = "A";
        } else if (v >= 0.5 & v < 1.5) {
            val = "B";
        } else if (v >= 1.5 & v < 2.5) {
            val = "C";
        } else if (v >= 2.5 & v < 3.5) {
            val = "D";
        } else if (v >= 3.5 & v < 4.5) {
            val = "E";
        } else if (v >= 4.5 & v < 5.5) {
            val = "F";
        } else if (v >= 5.5 & v < 6.5) {
            val = "G";
        } else if (v >= 6.5 & v < 7.5) {
            val = "H";
        } else if (v >= 7.5 & v < 8.5) {
            val = "I";
        } else if (v >= 8.5 & v < 9.5) {
            val = "J";
        } else if (v >= 9.5 & v < 10.5) {
            val = "K";
        } else if (v >= 10.5 & v < 11.5) {
            val = "L";
        } else if (v >= 11.5 & v < 12.5) {
            val = "M";
        } else if (v >= 12.5 & v < 13.5) {
            val = "N";
        } else if (v >= 13.5 & v < 14.5) {
            val = "O";
        } else if (v >= 14.5 & v < 15.5) {
            val = "P";
        } else if (v >= 15.5 & v < 16.5) {
            val = "Q";
        } else if (v >= 16.5 & v < 17.5) {
            val = "R";
        } else if (v >= 17.5 & v < 18.5) {
            val = "S";
        } else if (v >= 18.5 & v < 19.5) {
            val = "T";
        } else if (v >= 19.5 & v < 20.5) {
            val = "U";
        } else if (v >= 20.5 & v < 21.5) {
            val = "V";
        } else if (v >= 21.5 & v < 22.5) {
            val = "W";
        } else if (v >= 22.5 & v < 23.5) {
            val = "X";
        } else if (v >= 23.5 & v < 24.5) {
            val = "Y";
        } else if (v >= 24.5 & v < 25.5) {
            val = "Z";
        } else if (v >= 25.5 & v < 26.5) {
            val = " ";
        } else {
            val = "No sign detected";
        }

        return val;
    }

    private String get_all(float v) {
        String val = "";
        if (v >= -0.5 & v < 0.5) {
            val = "A";
        } else if (v >= 0.5 & v < 1.5) {
            val = "B";
        } else if (v >= 1.5 & v < 2.5) {
            val = "C";
        } else if (v >= 2.5 & v < 3.5) {
            val = "D";
        } else if (v >= 3.5 & v < 4.5) {
            val = "E";
        } else if (v >= 4.5 & v < 5.5) {
            val = "F";
        } else if (v >= 5.5 & v < 6.5) {
            val = "G";
        } else if (v >= 6.5 & v < 7.5) {
            val = "H";
        } else if (v >= 7.5 & v < 8.5) {
            val = "I";
        } else if (v >= 8.5 & v < 9.5) {
            val = "J";
        } else if (v >= 9.5 & v < 10.5) {
            val = "K";
        } else if (v >= 10.5 & v < 11.5) {
            val = "L";
        } else if (v >= 11.5 & v < 12.5) {
            val = "M";
        } else if (v >= 12.5 & v < 13.5) {
            val = "N";
        } else if (v >= 13.5 & v < 14.5) {
            val = "O";
        } else if (v >= 14.5 & v < 15.5) {
            val = "P";
        } else if (v >= 15.5 & v < 16.5) {
            val = "Q";
        } else if (v >= 16.5 & v < 17.5) {
            val = "R";
        } else if (v >= 17.5 & v < 18.5) {
            val = "S";
        } else if (v >= 18.5 & v < 19.5) {
            val = "T";
        } else if (v >= 19.5 & v < 20.5) {
            val = "U";
        } else if (v >= 20.5 & v < 21.5) {
            val = "V";
        } else if (v >= 21.5 & v < 22.5) {
            val = "W";
        } else if (v >= 22.5 & v < 23.5) {
            val = "X";
        } else if (v >= 23.5 & v < 24.5) {
            val = "Y";
        } else if (v >= 24.5 & v < 25.5) {
            val = "Z";
        } else if (v >= 25.5 & v < 26.5) {
            val = " ";
        } else {
            val = "No sign detected";
        }

        return val;
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        // some model input should be quant=0  for some quant=1
        // for this quant=0
        // Change quant=1
        // As we are scaling image from 0-255 to 0-1
        int quant = 1;
        int size_images = INPUT_SIZE;
        if (quant == 0) {
            byteBuffer = ByteBuffer.allocateDirect(1 * size_images * size_images * 3);
        } else {
            byteBuffer = ByteBuffer.allocateDirect(4 * 1 * size_images * size_images * 3);
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[size_images * size_images];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;

        // some error
        //now run
        for (int i = 0; i < size_images; ++i) {
            for (int j = 0; j < size_images; ++j) {
                final int val = intValues[pixel++];
                if (quant == 0) {
                    byteBuffer.put((byte) ((val >> 16) & 0xFF));
                    byteBuffer.put((byte) ((val >> 8) & 0xFF));
                    byteBuffer.put((byte) (val & 0xFF));
                } else {
                    // paste this
                    byteBuffer.putFloat((((val >> 16) & 0xFF)) / 255.0f);
                    byteBuffer.putFloat((((val >> 8) & 0xFF)) / 255.0f);
                    byteBuffer.putFloat((((val) & 0xFF)) / 255.0f);
                }
            }
        }
        return byteBuffer;
    }

    private ByteBuffer convertBitmapToByteBuffer1(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        int quant = 1;
        int size_images = classification_InputSize;
        if (quant == 0) {
            byteBuffer = ByteBuffer.allocateDirect(1 * size_images * size_images * 3);
        } else {
            byteBuffer = ByteBuffer.allocateDirect(4 * 1 * size_images * size_images * 3);
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[size_images * size_images];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < size_images; ++i) {
            for (int j = 0; j < size_images; ++j) {
                final int val = intValues[pixel++];
                if (quant == 0) {
                    byteBuffer.put((byte) ((val >> 16) & 0xFF));
                    byteBuffer.put((byte) ((val >> 8) & 0xFF));
                    byteBuffer.put((byte) (val & 0xFF));
                } else {
                    byteBuffer.putFloat((((val >> 16) & 0xFF)));
                    byteBuffer.putFloat((((val >> 8) & 0xFF)));
                    byteBuffer.putFloat((((val) & 0xFF)));
                }
            }
        }
        return byteBuffer;
    }
}