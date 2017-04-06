package ru.belokonalexander.yta.Views;

/**
 * Created by Alexander on 06.04.2017.
 */

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * когда TextView переносится на вторую строку, то WRAP_CONTENT работает некорректно и становится MATCH_PARENT
 * данное решение исправляет эту ошибку
 */
public class WrapWidthTextView extends TextView {

    public WrapWidthTextView(Context context) {
        super(context);
    }

    public WrapWidthTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapWidthTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Layout layout = getLayout();
        if (layout != null) {
            int width = (int) Math.ceil(getMaxLineWidth(layout))
                    + getCompoundPaddingLeft() + getCompoundPaddingRight();
            int height = getMeasuredHeight();
            setMeasuredDimension(width, height);
        }
    }

    private float getMaxLineWidth(Layout layout) {
        float max_width = 0.0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            if (layout.getLineWidth(i) > max_width) {
                max_width = layout.getLineWidth(i);
            }
        }
        return max_width;
    }
}


