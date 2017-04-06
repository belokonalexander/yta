package ru.belokonalexander.yta.GlobalShell;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.view.View;

import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.YtaApplication;

/**
 * Created by Alexander on 05.04.2017.
 */

public abstract class ClickableSpanTest extends android.text.style.ClickableSpan {


    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(ContextCompat.getColor(YtaApplication.getAppContext(), R.color.click_color));


    }
}
