package com.example.imagepro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

public class LearningVideo extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ConstraintLayout layoutheader;
    TextView title;
    YouTubePlayerView youTubePlayerView;
    ImageView backArrow;
    Button checkSign;
    float f = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_video);

        //get intent from learning class
        Intent i = getIntent();
        JSONObject keys = null;
        String genre = null, videoTitle = null, videoKey = null;
        CharSequence titleNameCombine = null;

        try {
            keys = new JSONObject(i.getStringExtra("keys"));
            genre = keys.getString("Genre");
            videoTitle = keys.getString("Title");
            titleNameCombine = genre + " - " + videoTitle;
            videoKey = keys.getString("VideoKey");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        title = findViewById(R.id.video_title);
        title.setText(titleNameCombine);
        youTubePlayerView = findViewById(R.id.nestedVideo);
        String finalVideoKey = videoKey;

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                String id = finalVideoKey;
                youTubePlayer.loadVideo(id, 0);
            }
        });

        //check sign function
        checkSign = findViewById(R.id.checkSign);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Genre", genre);
            jsonObject.put("Title", videoTitle);
            jsonObject.put("VideoKey", videoKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        checkSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayerView.release();
                startActivity(new Intent(LearningVideo.this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra("keys", jsonObject.toString()));
            }
        });

        //back to mainActivity
        backArrow = findViewById(R.id.backArrow_learn);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayerView.release();
                Intent intent = new Intent(getApplicationContext(), LearningClass.class);
                startActivity(intent);
            }
        });


        //Navigation bottom
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.menuLearn);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuCamera:
                        startActivity(new Intent(LearningVideo.this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        overridePendingTransition(0, 0);
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
        //        layoutheader.setTranslationY(-300);
        //        layoutheader.setAlpha(f);
        //        layoutheader.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(400).start();
    }
}