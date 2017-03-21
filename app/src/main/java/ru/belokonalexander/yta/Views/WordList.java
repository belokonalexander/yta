package ru.belokonalexander.yta.Views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.zip.Inflater;

import ru.belokonalexander.yta.GlobalShell.Models.TranslateResult;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;

/**
 * Created by Alexander on 21.03.2017.
 */

public class WordList extends LinearLayout {

    TranslateResult translateResult;



    public void setTranslateResult(TranslateResult translateResult) {
        this.translateResult = translateResult;
        inflateContent();
    }

    private void inflateContent() {

        StaticHelpers.LogThis(" INFLATE ");

        //очищаем прошлый результат
        if(this.getChildCount()>0){
            this.removeAllViews();
        }

        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(String text : translateResult.getText()){
            ViewGroup layout = (ViewGroup) layoutInflater.inflate(R.layout.word_layout,null);
            ((TextView)layout.findViewById(R.id.t_word)).setText(text);
            addView(layout);
        }
    }

    public WordList(Context context) {
        super(context);
        startInit();
    }

    public WordList(Context context, AttributeSet attrs) {
        super(context, attrs);
        startInit();
    }

    public WordList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        startInit();
    }

    private void startInit() {
        setOrientation(VERTICAL);
        setBackgroundColor(ContextCompat.getColor(getContext(),R.color.cardview_shadow_start_color));
        setPadding(0,StaticHelpers.dpToPixels(4),0, StaticHelpers.dpToPixels(4));
    }

}
