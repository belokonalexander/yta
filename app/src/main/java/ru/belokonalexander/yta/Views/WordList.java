package ru.belokonalexander.yta.Views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import ru.belokonalexander.yta.GlobalShell.Models.CompositeTranslateModel;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;

/**
 * Created by Alexander on 21.03.2017.
 */

public class WordList extends LinearLayout {

    CompositeTranslateModel translateResult;


    public void setTranslateResult(CompositeTranslateModel translateResult) {
        this.translateResult = translateResult;
        inflateContent();
    }


    public void setTranslateResult(CompositeTranslateModel translateResult, OnWordClickListener onClickListener) {
        this.translateResult = translateResult;
        this.onWordClick = onClickListener;
        inflateContent();
    }

    public interface OnWordClickListener{
        void onWordClick(String word);
    }

    private void inflateContent() {

        StaticHelpers.LogThis(" INFLATE ");

        clearView();

        if(!translateResult.getTranslateResult().isEmptyContent()) {
            LinearLayout lastContainer = inflateTranslateResult();

            if(translateResult.getLookupResult()!=null) {
                inflateLookupResult(lastContainer);
            }
        }

    }

    OnWordClickListener onWordClick;

    public void setOnWordClick(OnWordClickListener onWordClick) {
        this.onWordClick = onWordClick;
    }

    private void inflateLookupResult(LinearLayout lastContainer) {
        //String text = translateResult.getLookupString();
        long start = System.currentTimeMillis();
        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ScrollView scrollView = new ScrollView(getContext());

        TextView textView = (TextView) layoutInflater.inflate(R.layout.word_lookup, null);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setClickable(true);

        textView.setText(translateResult.getLookupString(onWordClick));
        textView.setVerticalScrollBarEnabled(true);
       // textView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.cardview_dark_background));


        scrollView.addView(textView);
        lastContainer.addView(scrollView);

        StaticHelpers.LogThis("past: " + (System.currentTimeMillis() - start));
    }

    private LinearLayout inflateTranslateResult() {

        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(R.layout.word_layout,null);

        LinearLayout wordListContainer = (LinearLayout) layout.findViewById(R.id.text_list);


        for(int i = 0; i <  translateResult.getTranslateResult().getText().size(); i++){

            String text = translateResult.getTranslateResult().getText().get(i);


            ViewGroup wordItem = (ViewGroup) layoutInflater.inflate(R.layout.word_item, null);
            TextView translate = (TextView) wordItem.findViewById(R.id.t_word);
            translate.setText(text);
            TextView original = (TextView) wordItem.findViewById(R.id.original_word);
            original.setText(translateResult.getSource());

            if(i==0){
                original.setVisibility(VISIBLE);
            } else setVisibility(GONE);
            wordListContainer.addView(wordItem);
        }

        this.addView(layout);

        return wordListContainer;
    }

    public void clearView() {
        //очищаем прошлый результат
        if(this.getChildCount()>0){
            this.removeAllViews();
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
        //setBackgroundColor(ContextCompat.getColor(getContext(),R.color.cardview_shadow_start_color));
        setPadding(0,StaticHelpers.dpToPixels(4),0, StaticHelpers.dpToPixels(4));
    }

}
