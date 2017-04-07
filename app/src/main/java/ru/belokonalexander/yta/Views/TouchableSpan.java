package ru.belokonalexander.yta.Views;

import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.YtaApplication;


/**
 * span, который реагирует на нажатия (с отрисовкой)
 */
public abstract class TouchableSpan extends ClickableSpan {
    private boolean mIsPressed;
    private int mPressedBackgroundColor;
    private int mNormalTextColor;
    private int mPressedTextColor;

    public TouchableSpan(int normalTextColor, int pressedTextColor, int pressedBackgroundColor) {
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mPressedBackgroundColor = pressedBackgroundColor;
    }

    public void setPressed(boolean isSelected) {
        mIsPressed = isSelected;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mNormalTextColor);
        ds.bgColor = mIsPressed ? mPressedBackgroundColor : 0x00eeeeee;
        ds.setUnderlineText(true);
    }

    public static TouchableSpan getTouchableSpan(View.OnClickListener onClickListener) {
        return new TouchableSpan(ContextCompat.getColor(YtaApplication.getAppContext(), R.color.clickable_span), ContextCompat.getColor(YtaApplication.getAppContext(), R.color.normal_text_color_accent), ContextCompat.getColor(YtaApplication.getAppContext(), R.color.ripple)) {

            @Override
            public void onClick(View widget) {
                onClickListener.onClick(widget);
            }
        };
    }

}

//new TouchableSpan(ContextCompat.getColor(getContext(),R.color.error_color),ContextCompat.getColor(getContext(),R.color.normal_text_color_accent),ContextCompat.getColor(getContext(),R.color.ripple)) {
