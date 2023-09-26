package com.videoplayerbanuba;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;

import com.banuba.sdk.playback.VideoPlayer;
import com.banuba.sdk.ve.domain.VideoRecordRange;
import com.banuba.sdk.ve.ext.VideoEditorUtils;
import com.facebook.react.uimanager.ThemedReactContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class BanubaPlayerSurface extends FrameLayout implements SurfaceHolder.Callback, LifecycleObserver {
    SurfaceView surface;


    private boolean started = false;
    private Context reactContext;

    private VideoPlayer videoPlayer;
    private SurfaceHolder surfaceHolder;



    // new
    public BanubaPlayerSurface(@NonNull ThemedReactContext context, VideoPlayer videoPlayerInit) {
        super(context);
        reactContext = context;
        videoPlayer = videoPlayerInit;
        setupUI();
    }

    private void setupUI() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.playerview, this, true); // Inflate the XML layout

        // Find and configure the SurfaceView
        surface = findViewById(R.id.surface);
        surfaceHolder = surface.getHolder();
        surfaceHolder.addCallback(this);

        // Configure Banuba player
        setupBanubaPlayer();
    }

    private void setupBanubaPlayer() {
        if (!videoPlayer.isPrepared() && videoPlayer.prepare(new Size(200, 200))) {
            Log.e("setupBanubaPlayer here","here");
            videoPlayer.setScaleType(com.banuba.sdk.playback.PlayerScaleType.CENTER_INSIDE);
            videoPlayer.setVideoSize(new Size(200, 200));
            videoPlayer.setVolume(1f);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Surface is created, configure the Banuba player
        videoPlayer.setSurfaceHolder(holder);
        attachVideoToPlayer();
        Log.e("setSurfaceHolder here","here");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Surface dimensions changed, you can adjust video scaling or other properties here
        Log.e("surfaceChanged here","here");
        if(started){
            Log.e("videoPlayer.play here","here");
            videoPlayer.play(true);
            invalidate();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface is destroyed, clear the surface from the Banuba player
        videoPlayer.clearSurfaceHolder(holder);
    }





    private void attachVideoToPlayer() {

        // Specify the playback range if needed (playFromMs and playToMs)
        long playFromMs = 0;
        Long playToMs = null;

        // Your existing String URI
        String stringUri = "https://www.w3schools.com/html/mov_bbb.mp4";

        // Convert the String URI to a Uri object
        Uri uri = Uri.parse(stringUri);

        try {
            VideoRecordRange videoRecordRange = VideoEditorUtils.createVideoRecordRange(
                    uri,
                    reactContext,
                    playFromMs,
                    playToMs,
                    true,
                    true
            );

            if (videoRecordRange != null) {
                // The videoRecordRange object is successfully created
                // You can use it as needed, for example, add it to a playlist
                Log.e("addVideoContent here", "VideoRecordRange here");

                // Assuming videoPlayer is an instance of com.banuba.sdk.playback.VideoPlayer
                List<VideoRecordRange> videoRanges = Collections.singletonList(videoRecordRange);
                int seekTotalPositionMs = 2; // Set your desired total position in milliseconds
                Log.e("videoRanges here", videoRanges.toString());
                videoPlayer.setVideoRanges(videoRanges, seekTotalPositionMs);
                started= true;
                Log.e("videoRecordRange here","started");

            } else {
                // Handle the case where the VideoRecordRange creation failed
                // This might happen if the URI is invalid or the video file doesn't exist
                Log.e("addVideoContent", "VideoRecordRange is null");
            }
        } catch (Exception e) {
            // Handle any exceptions that occur during video loading or playback
            Log.e("addVideoContent", "Error loading or playing video: " + e.getMessage(), e);
        }
    }


}