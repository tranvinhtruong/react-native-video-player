package com.my.package;

import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class FullscreenVideoPlayerModule extends ReactContextBaseJavaModule{
    public FullscreenVideoPlayerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "FullscreenVideoPlayerModule";
    }

    @ReactMethod
    public void showFullscreen(String videoUrl) {
        Context context = getReactApplicationContext();
        Intent intent = new Intent(context, FullscreenVideoPlayerActivity.class); // mContext got from your overriden constructor
        intent.putExtra("VIDEO_URL", videoUrl);
        getCurrentActivity().startActivity(intent);
    }
}
