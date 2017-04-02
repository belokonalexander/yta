package ru.belokonalexander.yta.Views.Animations;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import ru.belokonalexander.yta.GlobalShell.Settings;

/**
 * Created by Alexander on 02.04.2017.
 */

public class FadeAnimation extends AlphaAnimation {

    private FadeAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private FadeAnimation(float fromAlpha, float toAlpha) {
        super(fromAlpha, toAlpha);
    }

    public static FadeAnimation createFadeIn(View view){

        FadeAnimation fadeAnimation = new FadeAnimation(0,1);
        fadeAnimation.setDuration(Settings.FADE_ANIMATION_DURATION);
        fadeAnimation.setAnimationListener(SimpleAnimationListener.create(new SimpleAnimationListener.OnStartAnimation() {
            @Override
            public void onStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
        }));

        return fadeAnimation;
    }

    public static FadeAnimation createFadeOut(View view){

        FadeAnimation fadeAnimation = new FadeAnimation(1,0);
        fadeAnimation.setDuration(Settings.FADE_ANIMATION_DURATION);
        fadeAnimation.setAnimationListener(SimpleAnimationListener.create(new SimpleAnimationListener.OnEndAnimation() {
            @Override
            public void onEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
        }));

        return fadeAnimation;
    }

    public static FadeAnimation createFadeOutDestroy(ViewGroup view){

        FadeAnimation fadeAnimation = new FadeAnimation(1,0);
        fadeAnimation.setDuration(Settings.FADE_ANIMATION_DURATION);
        fadeAnimation.setAnimationListener(SimpleAnimationListener.create(new SimpleAnimationListener.OnEndAnimation() {
            @Override
            public void onEnd(Animation animation) {
                view.removeAllViews();
            }
        }));

        return fadeAnimation;
    }

}
