package com.cutler.androidtest.util;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by cutler on 2015/6/1.
 */
public class VibratorUtil {
    private static Vibrator vibrator;

    public static void start(Context context) {
        if(vibrator == null){
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        long[] pattern = {80, 50};
        vibrator.vibrate(pattern, 0);
    }

    public static void stop(){
        vibrator.cancel();
    }
}
