package com.smartengineersinc.RNVideoPlayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class FullscreenVideoPlayerActivity extends AppCompatActivity {
    private String mVideoUrl;
    private int mSeekTo;
    private boolean mDisableSeek;
    private boolean mDisableFF;
    private int mStartProgress;

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
            mSeekTo = i.getIntExtra("SEEK_TO", 0);
            mDisableSeek = i.getBooleanExtra("DISABLE_SEEK", false);
            mDisableFF = i.getBooleanExtra("DISABLE_FAST_FORWARD", false);
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
            final MediaController mediaController = new MediaController(FullscreenVideoPlayerActivity.this, !mDisableSeek);
            mediaController.setAnchorView(mVideoView);

            Uri video = Uri.parse(mVideoUrl);
            mVideoView.setMediaController(mediaController);
            mVideoView.setVideoURI(video);
            mVideoView.requestFocus();
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    if (mDisableFF) {
                        final int ffButtonId = getResources().getIdentifier("ffwd", "id", "android");
                        final ImageButton ffButton = (ImageButton) mediaController.findViewById(ffButtonId);
                        if (ffButton != null) {
                            ffButton.setClickable(false);
                            ffButton.setColorFilter(Color.GRAY);
                        }

                        final int mediaControllerProgressId = getResources().getIdentifier("mediacontroller_progress", "id", "android");
                        final SeekBar seekBarVideo = (SeekBar) mediaController.findViewById(mediaControllerProgressId);
                        seekBarVideo.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (fromUser && progress < mStartProgress) {
                                    mVideoView.seekTo(mVideoView.getDuration() * progress / seekBar.getMax());
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                                mStartProgress = seekBar.getProgress();
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }
                        });
                    }

                    if (mDisableSeek) {
                        final int mediaControllerProgressId = getResources().getIdentifier("mediacontroller_progress", "id", "android");
                        final SeekBar seekBarVideo = (SeekBar) mediaController.findViewById(mediaControllerProgressId);
                        seekBarVideo.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return true;
                            }
                        });
                    }

                    mProgressDialog.dismiss();
                    mVideoView.seekTo(mSeekTo * 1000);
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

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("CURRENT_POSITION", mVideoView.getCurrentPosition());
        setResult(RESULT_OK, data);

        super.finish();
    }
}
