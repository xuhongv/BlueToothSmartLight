package com.xuhong.csdn_bluetooth_master;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

/**
 * 项目名：   CSDN_BlueTooth-master
 * 包名：     com.xuhong.csdn_bluetooth_master
 * 文件名：   RippleView
 * 创建者：   xuhong
 * 创建时间： 2017/9/7 10:48
 * 描述：   TODO
 */

public class RippleView extends RelativeLayout {

    private static final int DEFAULT_FILL_TYPE=0;

    private int rippleColor = getResources().getColor(R.color.yellow);
    private float rippleStrokeWidth= getResources().getDimension(R.dimen.rippleStrokeWidth);
    private float rippleRadius =getResources().getDimension(R.dimen.rippleRadius);

    private int rippleDurationTime =4000;
    private int rippleAmount =4;
    private int rippleDelay;
    private float rippleScale=6.0f;
    private int rippleType=0;
    private Paint paint;
    private boolean animationRunning=false;
    private AnimatorSet animatorSet;
    private ArrayList<Animator> animatorList;
    private LayoutParams rippleParams;
    private ArrayList<mRipplView> rippleViewList=new ArrayList<>();

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

        rippleDelay=rippleDurationTime/rippleAmount;
        paint = new Paint();
        paint.setAntiAlias(true);
        if(rippleType==DEFAULT_FILL_TYPE){
            rippleStrokeWidth=0;
            paint.setStyle(Paint.Style.FILL);
        }else
            paint.setStyle(Paint.Style.STROKE);
        paint.setColor(rippleColor);

        rippleParams=new LayoutParams((int)(2*(rippleRadius+rippleStrokeWidth)),(int)(2*(rippleRadius+rippleStrokeWidth)));
        rippleParams.addRule(CENTER_IN_PARENT, TRUE);

        animatorSet = new AnimatorSet();
        animatorSet.setDuration(rippleDurationTime);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorList=new ArrayList<Animator>();

        for(int i=0;i<rippleAmount;i++){
            mRipplView rippleView=new mRipplView(getContext());
            addView(rippleView,rippleParams);
            rippleViewList.add(rippleView);

            final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale);
            scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleXAnimator.setStartDelay(i*rippleDelay);
            animatorList.add(scaleXAnimator);

            final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale);
            scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleYAnimator.setStartDelay(i*rippleDelay);
            animatorList.add(scaleYAnimator);

            final ObjectAnimator alphaAnimator= ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f);
            alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(alphaAnimator);

        }

        animatorSet.playTogether(animatorList);
    }

    private class mRipplView extends View{

        public mRipplView(Context context) {
            super(context);
            this.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius=(Math.min(getWidth(),getHeight()))/2;
            canvas.drawCircle(radius,radius,radius-rippleStrokeWidth,paint);
        }
    }

    public void startRippleAnimation(){
        if(!isRippleAnimationRunning()){
            for(mRipplView rippleView:rippleViewList){
                rippleView.setVisibility(VISIBLE);
            }
            animatorSet.start();
            animationRunning=true;
        }
    }

    public void stopRippleAnimation(){
        if(isRippleAnimationRunning()){
            animatorSet.end();
            animationRunning=false;
        }
    }

    public boolean isRippleAnimationRunning(){
        return animationRunning;
    }

}