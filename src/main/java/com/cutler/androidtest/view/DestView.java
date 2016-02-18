package com.cutler.androidtest.view;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cutler.androidtest.R;
import com.cutler.androidtest.controller.WindowController;
import com.cutler.androidtest.util.DensityUtil;

/**
 * Created by cutler on 2015/6/1.
 */
public class DestView extends LinearLayout {

    // 用来更新DragView
    private WindowManager mWindowManager;

    // 当前View的内容
    private View mContentView;

    // 当前View在屏幕中的位置参数
    private WindowManager.LayoutParams mParams;

    // 中间的控件，用来显示蓝色的飞行员、飞机
    private ImageView mCenterView;

    private boolean isReadyFly;

    public DestView(Context context, WindowManager windowManager) {
        super(context);

        this.mWindowManager = windowManager;
        mContentView = LayoutInflater.from(context).inflate(R.layout.inflate_dest_view, null);
        mCenterView = (ImageView) mContentView.findViewById(R.id.paperView);
        // 播放透明动画
        AlphaAnimation anim = new AlphaAnimation(0,1);
        anim.setDuration(300);
        mCenterView.startAnimation(anim);
        addView(mContentView);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public int getContentViewSize() {
        return DensityUtil.dip2px(getContext(), DragView.DRAG_PERSON_W_D + 30);
    }

    // 开始起飞
    public void startFly() {
        isReadyFly = true;
        // 修改位置
        int dimens = DensityUtil.dip2px(getContext(), 350) / 2;
        ViewGroup.LayoutParams lp = mCenterView.getLayoutParams();
        lp.height = lp.width = dimens;
        mCenterView.setLayoutParams(lp);
        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
        mParams.x = screenWidth / 2 - dimens / 2;
        mParams.y = screenHeight - dimens - DensityUtil.dip2px(getContext(), 20);
        mWindowManager.updateViewLayout(this, mParams);
        mCenterView.setImageResource(R.drawable.airplane_paper_transformed);
        ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(250);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) { }
            public void onAnimationEnd(Animation animation) {
                new AsyncTask<Object,Object,Object>(){
                    protected Object doInBackground(Object[] params) {
                        for (int i = mParams.y; i >= 0; i -= 20) {
                            publishProgress(i);
                            sleepTime(8);
                        }
                        return null;
                    }
                    protected void onProgressUpdate(Object[] values) {
                        mParams.y = (Integer) values[0];
                        mWindowManager.updateViewLayout(DestView.this, mParams);
                    }
                    protected void onPostExecute(Object o) {
                        WindowController.removeMaskViewFromScreen();
                        WindowController.removeDestViewFromScreen();
                        WindowController.addDragViewToScreen(getContext());
                    }
                }.execute();
            }
            public void onAnimationRepeat(Animation animation) { }
        });
        mCenterView.startAnimation(anim);


    }

    private void sleepTime(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public WindowManager.LayoutParams getParams() {
        return mParams;
    }

    public void setParams(WindowManager.LayoutParams mParams) {
        this.mParams = mParams;
    }
}
