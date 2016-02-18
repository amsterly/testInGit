package com.cutler.androidtest.controller;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.cutler.androidtest.R;
import com.cutler.androidtest.util.DensityUtil;
import com.cutler.androidtest.view.DestView;
import com.cutler.androidtest.view.DragView;

/**
 * Created by cutler on 2015/6/1.
 */
public class WindowController {

    // 用户可以拖拽的飞行员控件
    private static DragView dragView;

    // 当用户拖拽时，显示在屏幕下方的半透明黑色区域
    private static View maskView;

    // 用户需要将飞行员拖拽到的目标区域
    private static DestView destView;

    private static WindowManager windowManager;

    /**
     * 向屏幕中添加拖拽View。
     */
    public static void addDragViewToScreen(Context context) {
        if (dragView == null) {
            WindowManager windowManager = getWindowManager(context);
            dragView = new DragView(context, windowManager);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            params.format = PixelFormat.TRANSLUCENT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.gravity = Gravity.LEFT | Gravity.TOP;
            dragView.setParams(params);
            windowManager.addView(dragView, params);
        }
    }

    public static void removeDragViewFromScreen() {
        if(dragView != null){
            windowManager.removeView(dragView);
            dragView = null;
        }
    }


    public static void addMaskViewToScreen(Context context){
        if (maskView == null) {
            WindowManager windowManager = getWindowManager(context);
            int screenHeight = windowManager.getDefaultDisplay().getHeight();
            maskView = new View(context);
            maskView.setBackgroundResource(R.drawable.airplane_mask);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = DensityUtil.dip2px(context, 240);
            params.y = screenHeight - params.height;
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
            params.format = PixelFormat.TRANSLUCENT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.gravity = Gravity.LEFT | Gravity.TOP;
            windowManager.addView(maskView, params);
        }
    }

    public static void removeMaskViewFromScreen() {
        if(maskView != null){
            windowManager.removeView(maskView);
            maskView = null;
        }
    }

    public static void addDestViewToScreen(Context context){
        if (destView == null) {
            WindowManager windowManager = getWindowManager(context);
            int screenWidth = windowManager.getDefaultDisplay().getWidth();
            int screenHeight = windowManager.getDefaultDisplay().getHeight();
            destView = new DestView(context, windowManager);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.x = screenWidth / 2 - destView.getContentViewSize() / 2;
            params.y = screenHeight - destView.getContentViewSize() - DensityUtil.dip2px(context, 20);
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
            params.format = PixelFormat.TRANSLUCENT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.gravity = Gravity.LEFT | Gravity.TOP;
            destView.setParams(params);
            windowManager.addView(destView, params);
        }
    }

    public static void removeDestViewFromScreen() {
        if(destView != null){
            windowManager.removeView(destView);
            destView = null;
        }
    }

    private static Rect dragRect = new Rect();
    private static Rect destRect = new Rect();
    /**
     * 判断用户用否将宇航员拖动到底部区域
     * @return
     */
    public static boolean isReady() {
        WindowManager.LayoutParams dragViewP = dragView.getParams();
        WindowManager.LayoutParams destViewP = destView.getParams();
        if(dragViewP.x >= destViewP.x && dragViewP.y >= destViewP.y
                && dragViewP.x <= destViewP.x + destView.getContentViewSize()/4){
            return true;
        }
        return false;
    }

    public static void startFly() {
        destView.startFly();
    }

    public static WindowManager.LayoutParams getDestViewParams() {
        return destView.getParams();
    }

    private static WindowManager getWindowManager(Context context) {
        if(windowManager == null){
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return windowManager;
    }
}
