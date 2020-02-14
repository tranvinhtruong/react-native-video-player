package com.smartengineersinc.RNVideoPlayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

public class FullscreenVideoPlayerModule extends ReactContextBaseJavaModule {
    private static final int PLAY_VIDEO_REQUEST = 7843;

    private Promise mPromise;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            if (requestCode == PLAY_VIDEO_REQUEST && intent != null && mPromise != null) {
                if (mPromise != null) {
                    WritableMap map = Arguments.createMap();
                    map.putInt("currentPosition", intent.getIntExtra("CURRENT_POSITION", 0));

                    mPromise.resolve(map);

                    mPromise = null;
                }
            }
        }
    };

    public FullscreenVideoPlayerModule(ReactApplicationContext reactContext) {
        super(reactContext);

        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "FullscreenVideoPlayerModule";
    }

    @ReactMethod
    public void showFullscreen(final String videoUrl, final int seekTo, final boolean disableSeek, final boolean disableFF, final Promise promise) {
        mPromise = promise;

        Context context = getReactApplicationContext();
        Intent intent = new Intent(context, FullscreenVideoPlayerActivity.class); // mContext got from your overriden constructor
        intent.putExtra("VIDEO_URL", videoUrl);
        intent.putExtra("SEEK_TO", seekTo);
        intent.putExtra("DISABLE_SEEK", disableSeek);
        intent.putExtra("DISABLE_FAST_FORWARD", disableFF);
        getCurrentActivity().startActivityForResult(intent, PLAY_VIDEO_REQUEST);
    }
}
