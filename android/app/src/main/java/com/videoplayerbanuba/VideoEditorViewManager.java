package com.videoplayerbanuba;


import android.util.Log;

import androidx.annotation.NonNull;

import com.banuba.sdk.playback.VideoPlayer;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import org.koin.java.KoinJavaComponent;

public class VideoEditorViewManager extends SimpleViewManager<BanubaPlayerSurface> {
    // Inject your VideoPlayer instance using Koin

    private BanubaPlayerSurface originalInstance; // Maintain reference to the original instance


    @NonNull
    @Override
    public String getName() {
        return "VideoEditorView";
    }

    @NonNull
    @Override
    protected BanubaPlayerSurface createViewInstance(ThemedReactContext reactContext) {
        if (originalInstance == null) {
            VideoPlayer videoPlayer = KoinJavaComponent.get(VideoPlayer.class);
            Log.e("videoPlayer view", videoPlayer.toString());
            originalInstance = new BanubaPlayerSurface(reactContext,videoPlayer);
        }
        return originalInstance;
    }

    // Define a native method that can be called from JavaScript

}
