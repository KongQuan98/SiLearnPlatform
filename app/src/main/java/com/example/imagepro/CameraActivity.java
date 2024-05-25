package com.example.imagepro;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivity";

    //test frame rate
    int mFPS;
    long startTime = 0;
    long currentTime = 1000;
    String modeText = "Combine";

    FirebaseAuth fAuth;
    BottomNavigationView bottomNavigationView;
    ImageView backbutton, switch_camera, hint;
    TextView back, textRecognition, mode;
    ConstraintLayout constraintLayout;
    float f = 0;
    String videoTitle = "";

    private Mat mRgba;
    private Mat mGray;
    private CameraBridgeViewBase mOpenCvCameraView;
    private int frontBack = 1;
    private ObjectDetectorClass objectDetectorClass;
    private Button clear_button, add_button, start_button;
    private TextView text_change;
    private FrameLayout frameLayout;
    private YouTubePlayerView youTubePlayerView;
    private LinearLayout bottomLayout;
    private List<String> checkList = new ArrayList<>();
    private int count = 0;

    private String model_All = "model_myAlpha2.tflite";
    private String alphabet_model = "model_myAlpha2.tflite";
    private String number_model = "number_model.tflite";
    private String greeting_model = "greeting_model.tflite";


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface
                        .SUCCESS: {
                    Log.i(TAG, "OpenCv Is loaded");
                    mOpenCvCameraView.enableView();
                }
                default: {
                    super.onManagerConnected(status);

                }
                break;
            }
        }
    };

    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (fAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(CameraActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        int MY_PERMISSIONS_REQUEST_CAMERA = 0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCameraIndex(frontBack);
        mOpenCvCameraView.setCvCameraViewListener(this);

        //Recognizing certain words
        Intent intent = getIntent();
        String checkSign = "";
        String videoKey = "";
        String genre = "";
        textRecognition = findViewById(R.id.textRecognition);
        mode = findViewById(R.id.switch_mode);

        //Hint Button
        hint = findViewById(R.id.hint);
        hint.setVisibility(View.VISIBLE);
        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopUpWindow();
            }
        });

        //From Learning
        if (intent.getStringExtra("keys") != null) {
            mode.setVisibility(View.INVISIBLE);
            checkSign = intent.getStringExtra("keys");
            try {
                JSONObject jsonObject = new JSONObject(checkSign);
                videoTitle = jsonObject.getString("Title");
                genre = jsonObject.getString("Genre");
                videoKey = jsonObject.getString("VideoKey");
                if (jsonObject.getString("mode") != null) {
                    modeText = jsonObject.getString("mode");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            textRecognition.setText("Recognizing word - " + videoTitle);
        }

        //From Quiz
        if (intent.getStringArrayListExtra("list") != null) {
            if (intent.getIntExtra("count", 0) != 5) {
                count = intent.getIntExtra("count", 0);
            }
            hint.setVisibility(View.INVISIBLE);
            mode.setVisibility(View.INVISIBLE);
            checkList = intent.getStringArrayListExtra("list");
            textRecognition.setText("Try Your Best!");
        }

        //switch camera
//        switch_camera = findViewById(R.id.switch_camera);
//        String finalCheckSign1 = checkSign;
//        switch_camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(CameraActivity.this, "Changed to Back Camera", Toast.LENGTH_SHORT).show();
//                if (intent.getStringExtra("keys")!=null){
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(intent.getStringExtra("keys"));
//                        jsonObject.put("mode", modeText);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    startActivity(new Intent(CameraActivity.this,CameraActivityBack.class)
//                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                            .putExtra("keys", jsonObject.toString()));
//                }else{
//                    startActivity(new Intent(CameraActivity.this,CameraActivityBack.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
//
//                }
//            }
//        });


        clear_button = findViewById(R.id.clear_button);
        add_button = findViewById(R.id.add_button);
        start_button = findViewById(R.id.start_button);
        text_change = findViewById(R.id.combineText);
//        frameLayout = findViewById(R.id.hintVideoLayout);
//        youTubePlayerView = findViewById(R.id.hintVideo);
        bottomLayout = findViewById(R.id.bottomLayout);

        //Switch mode
        String finalGenre = genre;
        String finalVideoTitle1 = videoTitle;
        String finalVideoKey = videoKey;

        //launch default
        try {
            RecognitionModel rm = new RecognitionModel(null, modeText, finalGenre, finalVideoTitle1, start_button, clear_button,
                    add_button, text_change, bottomLayout, checkList, count);
            objectDetectorClass = new ObjectDetectorClass(rm, getAssets(), "hand_model.tflite", "custom_label.txt", 300,
                    model_All, 96, number_model, greeting_model, alphabet_model);
            Log.d("MainActivity", "Model is successfully loaded");
        } catch (IOException e) {
            Log.d("MainActivity", "Getting some error");
            e.printStackTrace();
        }

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        CameraActivity.this, R.style.BottomSheetDialogTheme
                );
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.layout_bottom_switch, (LinearLayout) findViewById(R.id.bottomSheetContainerSwitch));

                Button oneMode = bottomSheetView.findViewById(R.id.oneMode);
                Button combineMode = bottomSheetView.findViewById(R.id.combineMode);
                TextView switchbutton = bottomSheetView.findViewById(R.id.switch_mode);

                oneMode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            RecognitionModel rm = new RecognitionModel(switchbutton, "One", finalGenre, finalVideoTitle1, start_button,
                                    clear_button, add_button, text_change, bottomLayout, checkList, count);
                            objectDetectorClass = new ObjectDetectorClass(rm, getAssets(), "hand_model.tflite", "custom_label.txt",
                                    300, model_All, 96, number_model, greeting_model, alphabet_model);

                            Toast.makeText(CameraActivity.this, "Currently in One Word Recognition Mode", Toast.LENGTH_SHORT).show();


                            if (bottomSheetDialog.isShowing()) {
                                bottomSheetDialog.dismiss();
                            }

                            Log.d("MainActivity", "Model is successfully loaded");
                        } catch (IOException e) {
                            Log.d("MainActivity", "Getting some error");
                            e.printStackTrace();
                        }
                    }
                });

                combineMode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            RecognitionModel rm = new RecognitionModel(switchbutton, "Combine", finalGenre, finalVideoTitle1, start_button,
                                    clear_button, add_button, text_change, bottomLayout, checkList, count);
                            objectDetectorClass = new ObjectDetectorClass(rm, getAssets(), "hand_model.tflite", "custom_label.txt",
                                    300, model_All, 96, number_model, greeting_model, alphabet_model);

                            Toast.makeText(CameraActivity.this, "Currently in Combine Word Recognition Mode", Toast.LENGTH_SHORT).show();

                            if (bottomSheetDialog.isShowing()) {
                                bottomSheetDialog.dismiss();
                            }

                            Log.d("MainActivity", "Model is successfully loaded");
                        } catch (IOException e) {
                            Log.d("MainActivity", "Getting some error");
                            e.printStackTrace();
                        }
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        //Navigation bottom
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menuCamera);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuCamera:
                        return true;
                    case R.id.menuLearn: {
                        startActivity(new Intent(getApplicationContext(), LearningClass.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    case R.id.menuCommunity: {
                        startActivity(new Intent(getApplicationContext(), CommunityClass.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    case R.id.menuProfile: {
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                }
                return false;
            }
        });

        //animation
        constraintLayout = findViewById(R.id.layoutHeader_Recognition);
        constraintLayout.setTranslationY(-300);
        constraintLayout.setAlpha(f);
        constraintLayout.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(400).start();

        //back navigation
        backbutton = findViewById(R.id.backArrow);
        back = findViewById(R.id.back);

        String finalVideoTitle = videoTitle;
        String finalCheckSign = checkSign;
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkList.isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                    startActivity(intent);
                } else if (finalVideoTitle.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), LearningVideo.class);
                    intent.putExtra("keys", finalCheckSign);
                    startActivity(intent);
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkList.isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                    startActivity(intent);
                } else if (finalVideoTitle.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), LearningVideo.class);
                    intent.putExtra("keys", finalCheckSign);
                    startActivity(intent);
                }
            }
        });
    }

    private void openPopUpWindow() {
        Intent popupwindow = new Intent(CameraActivity.this, PopUpWindow.class);
        popupwindow.putExtra("keys", videoTitle);
        startActivity(popupwindow);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            //if load success
            Log.d(TAG, "Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            //if not loaded
            Log.d(TAG, "Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }

    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (frontBack == 1) {
            Core.flip(mRgba, mRgba, 1);
        }
        
        return objectDetectorClass.recognizeImage(mRgba);
    }

}