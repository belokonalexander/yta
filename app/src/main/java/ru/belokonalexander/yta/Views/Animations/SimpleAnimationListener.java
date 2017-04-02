package ru.belokonalexander.yta.Views.Animations;

import android.view.animation.Animation;

/**
 * Created by Alexander on 02.04.2017.
 */

public class SimpleAnimationListener implements Animation.AnimationListener {

    @Override
    public void onAnimationStart(Animation animation) {
        if(startAnimation!=null)
            startAnimation.onStart(animation);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(endAnimation!=null)
            endAnimation.onEnd(animation);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public static SimpleAnimationListener create(OnStartAnimation startAnimation){
        return new SimpleAnimationListener(startAnimation);
    }

    public static SimpleAnimationListener create(OnStartAnimation startAnimation, OnEndAnimation endAnimation){
        return new SimpleAnimationListener(startAnimation, endAnimation);
    }

    public static SimpleAnimationListener create( OnEndAnimation endAnimation){
        return new SimpleAnimationListener(endAnimation);
    }

    private SimpleAnimationListener(OnStartAnimation startAnimation, OnEndAnimation endAnimation) {
        this.startAnimation = startAnimation;
        this.endAnimation = endAnimation;
    }

    private SimpleAnimationListener(OnStartAnimation startAnimation) {
        this.startAnimation = startAnimation;
    }

    private SimpleAnimationListener(OnEndAnimation endAnimation) {
        this.endAnimation = endAnimation;
    }

    private OnStartAnimation startAnimation;
    private OnEndAnimation endAnimation;

    public interface OnStartAnimation{
        void onStart(Animation animation);
    }

    public interface  OnEndAnimation{
        void onEnd(Animation animation);
    }


}
