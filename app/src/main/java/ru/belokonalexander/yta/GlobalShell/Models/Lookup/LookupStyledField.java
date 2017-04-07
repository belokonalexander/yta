package ru.belokonalexander.yta.GlobalShell.Models.Lookup;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import ru.belokonalexander.yta.GlobalShell.Models.TranslateLanguage;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.Views.TouchableSpan;
import ru.belokonalexander.yta.Views.WordList;
import ru.belokonalexander.yta.YtaApplication;

/**
 * стилизирует Lookup результат
 */

public class LookupStyledField implements Serializable {

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

    public static SpannableString buildSpannableString(String source, List<LookupStyledField> values, TranslateLanguage language, WordList.OnWordClickListener clickableSpan){

        SpannableString result = new SpannableString(source);

        for(LookupStyledField value : values){
            switch (value.type){
                case ABOUT:
                        result.setSpan(new StyleSpan(Typeface.ITALIC),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        result.setSpan(new ForegroundColorSpan(ContextCompat.getColor(YtaApplication.getAppContext(), R.color.about)),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                case NUM:
                        result.setSpan(new ForegroundColorSpan(ContextCompat.getColor(YtaApplication.getAppContext(), R.color.about)),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                case SYNONYM:
                    if(clickableSpan!=null)
                        result.setSpan(TouchableSpan.getTouchableSpan(widget -> {
                            StaticHelpers.LogThis("lang: " + language);
                            clickableSpan.onWordClick(((TextView)widget).getText().toString().substring(value.start,value.finish), language);
                        }), value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                case SYNONYMS_AREA:

                    //result.setSpan(new BackgroundColorSpan(Color.parseColor("#aa696cf5")), value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    result.setSpan(new StyleSpan(Typeface.ITALIC),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    result.setSpan(new ForegroundColorSpan(ContextCompat.getColor(YtaApplication.getAppContext(), R.color.clickable_span)),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                case MEAN:
                        result.setSpan(new ForegroundColorSpan(ContextCompat.getColor(YtaApplication.getAppContext(), R.color.mean)),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

                case SOURCE:
                        result.setSpan(new StyleSpan(Typeface.ITALIC),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case EXAMPLE:
                        result.setSpan(new ForegroundColorSpan(ContextCompat.getColor(YtaApplication.getAppContext(), R.color.example)),value.start, value.finish, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;

            }
        }

        return result;
    }

}
