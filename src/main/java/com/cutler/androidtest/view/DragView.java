package com.cutler.androidtest.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cutler.androidtest.R;
import com.cutler.androidtest.controller.WindowController;
import com.cutler.androidtest.util.DensityUtil;
import com.cutler.androidtest.util.VibratorUtil;

/**
 * Created by cutler on 2015/5/28.
 */
public class DragView extends LinearLayout {

    public static final int  DRAG_PERSON_W_D = 90;

    // 当前是否正在处于拖拽
    private boolean isShowPerson;
    // 当前是否拖拽到目标区域
    private boolean isReady;

    // 用来更新DragView
    private WindowManager mWindowManager;

    // 当前View的内容
    private View mContentView;

    // 当前View在屏幕中的位置参数
    private WindowManager.LayoutParams mParams;

    // 上一次点击的位置
    private float preX, preY;

    // 用来显示内存当前值、宇航员
    private TextView mTextView;
    private ImageView mImageView;

    public DragView(Context context, WindowManager windowManager) {
        super(context);
        this.mWindowManager = windowManager;
        mContentView = LayoutInflater.from(context).inflate(R.layout.inflate_drag_view, null);
        mTextView = (TextView) mContentView.findViewById(R.id.text);
        mImageView = (ImageView) mContentView.findViewById(R.id.img);
        ViewGroup.LayoutParams p = mImageView.getLayoutParams();
        p.width = DensityUtil.dip2px(context, DRAG_PERSON_W_D);
        p.height = DensityUtil.dip2px(context, DRAG_PERSON_W_D);
        mImageView.setLayoutParams(p);
        addView(mContentView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX = event.getRawX();
                preY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float nowX = event.getRawX(), nowY = event.getRawY();
                if (!isShowPerson && (nowX >= 30 || nowY >= 30)) {
                    mTextView.setVisibility(View.GONE);
                    playRotateAnimation(mImageView);
                    isShowPerson = true;
                    WindowController.addMaskViewToScreen(getContext());
                    WindowController.addDestViewToScreen(getContext());
                }
                checkIsReady();
                mParams.x += (int) (nowX - preX);
                mParams.y += (int) (nowY - preY);
                if(!WindowController.isReady()){
                    mWindowManager.updateViewLayout(this, mParams);
                }
                preX = nowX;
                preY = nowY;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isShowPerson = false;
                mImageView.clearAnimation();
                // 如果是在目标区域上抬起的手指，则开始播放起飞动画
                if (isReady) {
                    VibratorUtil.stop();
                    ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        public void onAnimationStart(Animation animation) { }
                        public void onAnimationEnd(Animation animation) {
                            WindowController.removeDragViewFromScreen();
                        }
                        public void onAnimationRepeat(Animation animation) { }
                    });
                    anim.setDuration(250);
                    mImageView.startAnimation(anim);
                    WindowController.startFly();
                } else {
                    mTextView.setVisibility(View.VISIBLE);
                    WindowController.removeMaskViewFromScreen();
                    WindowController.removeDestViewFromScreen();
                    mImageView.setVisibility(View.GONE);
                }
                isReady = false;
                break;
        }
        return true;
    }

    // 检测当前是否处于可发射状态
    private void checkIsReady() {
        if (isShowPerson) {
            if (WindowController.isReady()) {
                if(!isReady){
                    mImageView.clearAnimation();
                    WindowManager.LayoutParams params = WindowController.getDestViewParams();
                    mParams.x = params.x + DensityUtil.dip2px(getContext(), 10);
                    mParams.y = params.y + DensityUtil.dip2px(getContext(), 6);
                    mWindowManager.updateViewLayout(this, mParams);
                    isReady = true;
                    VibratorUtil.start(getContext());
                }
            } else if(isReady){
                isReady = false;
                VibratorUtil.stop();
                playRotateAnimation(mImageView);
            }
        }
    }

    // 为指定的View播放旋转动画
    private void playRotateAnimation(final ImageView view){
        RotateAnimation anim = new RotateAnimation(0, 359, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(850);
        anim.setRepeatCount(RotateAnimation.INFINITE);
        anim.setInterpolator(new LinearInterpolator());
        view.setVisibility(View.VISIBLE);
        view.startAnimation(anim);
    }

    public WindowManager.LayoutParams getParams() {
        return mParams;
    }

    public int getCurrentWidth(){
        return isShowPerson? mImageView.getLayoutParams().width : WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public int getCurrentHeight(){
        return isShowPerson? mImageView.getLayoutParams().height : WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public void setParams(WindowManager.LayoutParams mParams) {
        this.mParams = mParams;
    }

}
