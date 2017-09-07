package com.xuhong.csdn_bluetooth_master;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * 项目名：   CSDN_BlueTooth-master
 * 包名：     com.xuhong.csdn_bluetooth_master
 * 文件名：   RippleView
 * 创建者：   xuhong
 * 创建时间： 2017/9/7 10:48
 * 描述：   TODO
 */

public class RippleView extends RelativeLayout {

    private static final int DEFAULT_FILL_TYPE = 0;

    private int rippleColor = getResources().getColor(R.color.yellow);
    private float rippleStrokeWidth = getResources().getDimension(R.dimen.rippleStrokeWidth);
    private float rippleRadius = getResources().getDimension(R.dimen.rippleRadius);

    private AnimationProgressListener mAnimationProgressListener;

    private Paint paint;
    private boolean animationRunning = false;
    private AnimatorSet animatorSet;
    private ArrayList<Animator> animatorList;
    private LayoutParams rippleParams;
    private ArrayList<mRipplView> rippleViewList = new ArrayList<>();


    private static final int HANDLER_CODE_START = 101;
    private static final int HANDLER_CODE_PAUSE = 102;
    //动画时间进度
    private int AnimationTimeFlag = 0;
    //动画持续的 时间
    private boolean isAnimationRunning =true ;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case HANDLER_CODE_START:
                    AnimationTimeFlag = AnimationTimeFlag + 4;
                    if (mAnimationProgressListener != null) {
                        mAnimationProgressListener.updataProgress(AnimationTimeFlag);
                    }
                    if (isAnimationRunning&&AnimationTimeFlag<100) {
                        mHandler.sendEmptyMessageDelayed(HANDLER_CODE_START, 1000);
                    }
                    break;

                case HANDLER_CODE_PAUSE:
                    isAnimationRunning = false;
                    break;

            }
        }
    };

    public RippleView(Context context) {
        super(context);
        init();
    }


    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {

        int rippleDurationTime = 4000;
        int rippleAmount = 4;
        int rippleDelay = rippleDurationTime / rippleAmount;
        paint = new Paint();
        paint.setAntiAlias(true);
        int rippleType = 0;

        if (rippleType == DEFAULT_FILL_TYPE) {
            rippleStrokeWidth = 0;
            paint.setStyle(Paint.Style.FILL);
        } else
            paint.setStyle(Paint.Style.STROKE);
        paint.setColor(rippleColor);

        rippleParams = new LayoutParams((int) (2 * (rippleRadius + rippleStrokeWidth)), (int) (2 * (rippleRadius + rippleStrokeWidth)));
        rippleParams.addRule(CENTER_IN_PARENT, TRUE);

        animatorSet = new AnimatorSet();
        animatorSet.setDuration(rippleDurationTime);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorList = new ArrayList<>();

        for (int i = 0; i < rippleAmount; i++) {

            mRipplView rippleView = new mRipplView(getContext());
            addView(rippleView, rippleParams);
            rippleViewList.add(rippleView);

            float rippleScale = 6.0f;
            final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale);
            scaleXAnimator.setRepeatCount(5);
            scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleXAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(scaleXAnimator);

            final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale);
            scaleYAnimator.setRepeatCount(5);
            scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleYAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(scaleYAnimator);

            final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f);
            alphaAnimator.setRepeatCount(5);
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(alphaAnimator);

        }
        animatorSet.playTogether(animatorList);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mHandler.sendEmptyMessageDelayed(HANDLER_CODE_START, 1000);
                Log.e("==w", "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mHandler.sendEmptyMessage(HANDLER_CODE_PAUSE);
                Log.e("==w", "onAnimationEnd");
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                Log.e("==w", "onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    private class mRipplView extends View {

        public mRipplView(Context context) {
            super(context);
            this.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius = (Math.min(getWidth(), getHeight())) / 2;
            canvas.drawCircle(radius, radius, radius - rippleStrokeWidth, paint);
        }
    }

    public void startRippleAnimation() {
        if (!isRippleAnimationRunning()) {
            for (mRipplView rippleView : rippleViewList) {
                rippleView.setVisibility(VISIBLE);
            }
            animatorSet.start();
            animationRunning = true;
        }
    }

    public void stopRippleAnimation() {
        if (isRippleAnimationRunning()) {
            animatorSet.end();
            animationRunning = false;
        }
    }

    private boolean isRippleAnimationRunning() {
        return animationRunning;
    }

    public interface AnimationProgressListener {
        void updataProgress(int progress);
    }

    public void setAnimationProgressListener(AnimationProgressListener mAnimationProgressListener) {
        this.mAnimationProgressListener = mAnimationProgressListener;
    }

}