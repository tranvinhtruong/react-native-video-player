package com.my.package;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class FullscreenVideoPlayerActivity extends AppCompatActivity {
    private String mVideoUrl;

    private static ProgressDialog mProgressDialog;
    VideoView mVideoView;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_fullscreen);

        Intent i = getIntent();
        if (i != null) {
            mVideoView = (VideoView) findViewById(R.id.videoView);
            mVideoUrl = i.getStringExtra("VIDEO_URL");
            mProgressDialog = ProgressDialog.show(FullscreenVideoPlayerActivity.this, "", "Buffering video...", true);
            mProgressDialog.setCancelable(true);
            playVideo();
        } else {
            Toast.makeText(FullscreenVideoPlayerActivity.this, "VideoURL not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void playVideo() {
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(FullscreenVideoPlayerActivity.this);
            mediaController.setAnchorView(mVideoView);

            Uri video = Uri.parse(mVideoUrl);
            mVideoView.setMediaController(mediaController);
            mVideoView.setVideoURI(video);
            mVideoView.requestFocus();
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mProgressDialog.dismiss();
                    mVideoView.start();
                }
            });
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    finish();
                }
            });
        } catch (Exception e) {
            mProgressDialog.dismiss();
            System.out.println("Video Play Error :" + e.toString());
            finish();
        }
    }
}
