package com.example.imagepro;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.w3c.dom.Text;

import java.util.List;

public class RecognitionModel {
    private TextView switchButton;
    private String mode;
    private String genre;
    private String checkSign;
    private Button start_button;
    private Button clear_button;
    private Button add_button;
    private TextView text_change;

    private FrameLayout frameLayout;
    private YouTubePlayerView youTubePlayerView;
    private LinearLayout bottomLayout;
    private String videoKey;
    private List<String> checkList;
    private int count;

    public RecognitionModel(TextView switchButton, String mode, String genre, String checkSign, Button start_button, Button clear_button,
                            Button add_button, TextView text_change, LinearLayout bottomLayout, List<String> checkList, int count) {
        this.switchButton = switchButton;
        this.mode = mode;
        this.genre = genre;
        this.checkSign = checkSign;
        this.start_button = start_button;
        this.clear_button = clear_button;
        this.add_button = add_button;
        this.text_change = text_change;
//        this.frameLayout = frameLayout;
//        this.youTubePlayerView = youTubePlayerView;
        this.bottomLayout = bottomLayout;
//        this.videoKey = videoKey;
        this.checkList = checkList;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<String> checkList) {
        this.checkList = checkList;
    }

    //    public String getVideoKey() {
//        return videoKey;
//    }
//
//    public void setVideoKey(String videoKey) {
//        this.videoKey = videoKey;
//    }

    public LinearLayout getBottomLayout() {
        return bottomLayout;
    }

    public void setBottomLayout(LinearLayout bottomLayout) {
        this.bottomLayout = bottomLayout;
    }

//    public FrameLayout getFrameLayout() {
//        return frameLayout;
//    }
//
//    public void setFrameLayout(FrameLayout frameLayout) {
//        this.frameLayout = frameLayout;
//    }
//
//    public YouTubePlayerView getYouTubePlayerView() {
//        return youTubePlayerView;
//    }
//
//    public void setYouTubePlayerView(YouTubePlayerView youTubePlayerView) {
//        this.youTubePlayerView = youTubePlayerView;
//    }

    public TextView getSwitchButton() {
        return switchButton;
    }

    public void setSwitchButton(TextView switchButton) {
        this.switchButton = switchButton;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCheckSign() {
        return checkSign;
    }

    public void setCheckSign(String checkSign) {
        this.checkSign = checkSign;
    }

    public Button getStart_button() {
        return start_button;
    }

    public void setStart_button(Button start_button) {
        this.start_button = start_button;
    }

    public Button getClear_button() {
        return clear_button;
    }

    public void setClear_button(Button clear_button) {
        this.clear_button = clear_button;
    }

    public Button getAdd_button() {
        return add_button;
    }

    public void setAdd_button(Button add_button) {
        this.add_button = add_button;
    }

    public TextView getText_change() {
        return text_change;
    }

    public void setText_change(TextView text_change) {
        this.text_change = text_change;
    }


}
