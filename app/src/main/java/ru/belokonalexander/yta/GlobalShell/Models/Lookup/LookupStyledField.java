package ru.belokonalexander.yta.GlobalShell.Models.Lookup;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.WordList;

/**
 * Created by Alexander on 22.03.2017.
 */

public class LookupStyledField {

    private int start;
    private int finish;

    public LookupStyledField(StringBuilder stringBuilder, Type type) {
        this.start = stringBuilder.length();

        this.type = type;
    }

    public void setFinish(StringBuilder stringBuilder) {
        this.finish = stringBuilder.length();
    }

    private Type type;

    public enum Type {
        NUM, ABOUT, SYNONYMS_AREA, SYNONYM, MEAN, SOURCE, EXAMPLE;
    }

    public static SpannableString buildSpannableString(String source, List<LookupStyledField> values, Language language, WordList.OnWordClickListener clickableSpan){

        SpannableString result = new SpannableString(source);

        for(LookupStyledField value : values){
            switch (value.type){
                case ABOUT:
                        result.setSpan(new StyleSpan(Typeface.ITALIC),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        result.setSpan(new ForegroundColorSpan(Color.parseColor("#da6cda")),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                case NUM:
                        result.setSpan(new ForegroundColorSpan(Color.parseColor("#c2c2c2")),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                case SYNONYM:
                    if(clickableSpan!=null)
                        result.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                StaticHelpers.LogThis("lang: " + language);
                                clickableSpan.onWordClick(((TextView)widget).getText().toString().substring(value.start,value.finish), language);
                            }
                        }, value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                case SYNONYMS_AREA:

                    //result.setSpan(new BackgroundColorSpan(Color.parseColor("#aa696cf5")), value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    result.setSpan(new StyleSpan(Typeface.ITALIC),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    result.setSpan(new ForegroundColorSpan(Color.parseColor("#696cf5")),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                case MEAN:
                        result.setSpan(new ForegroundColorSpan(Color.parseColor("#ab4855")),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                case SOURCE:
                        result.setSpan(new StyleSpan(Typeface.ITALIC),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case EXAMPLE:
                        result.setSpan(new ForegroundColorSpan(Color.parseColor("#696cf5")),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

            }
        }

        return result;
    }

}
