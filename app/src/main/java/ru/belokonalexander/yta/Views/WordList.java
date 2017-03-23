package ru.belokonalexander.yta.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import ru.belokonalexander.yta.GlobalShell.Models.CompositeTranslateModel;
import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.YtaApplication;

/**
 * Created by Alexander on 21.03.2017.
 */

/**
 *  ViewGroup, который представляет результаты перевода
 */
public class WordList extends LinearLayout implements YandexLicenseLabelView {

    /**
     *  главная модель
     */
    private CompositeTranslateModel translateResult;
    private OnWordClickListener onWordClick;


    /**
     *
     * @param translateResult модель, которая будет тображаться
     * @param onClickListener слушатель, реагирующий на клик по варианту второстепенного перевода
     */
    public void setTranslateResult(CompositeTranslateModel translateResult, OnWordClickListener onClickListener) {
        this.translateResult = translateResult;
        this.onWordClick = onClickListener;
        inflateContent();
    }

    @Override
    public void inflateYandexLicenceLabel(ViewGroup container) {

        //ViewGroup target = container;

        /*for(int i =0; i < container.getChildCount(); i++){
            View view = container.getChildAt(i);
            if(view instanceof ScrollView)
                target = (ViewGroup) view;
        }
*/
        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView label = (TextView) layoutInflater.inflate(R.layout.word_lookup, null);

        //StaticHelpers.LogThis(YtaApplication.getAppContext().getResources().getString(R.string.ya_license));
        SpannableString ss = new SpannableString(YtaApplication.getAppContext().getResources().getString(R.string.ya_license));

        int start = ss.toString().indexOf("«");
        int to = ss.toString().indexOf("»")+1;

        ss.setSpan(new StyleSpan(Typeface.ITALIC),0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String url = getContext().getResources().getString(R.string.ya_url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getContext().startActivity(i);
            }
        },start, to , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(),R.color.yandex)),start, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.ITALIC),0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        StaticHelpers.LogThis(ss);

        label.setMovementMethod(LinkMovementMethod.getInstance());
        label.setClickable(true);
        label.setText(ss);
        label.setGravity(Gravity.CENTER);
        //label.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardview_dark_background));
        label.setPadding(StaticHelpers.dpToPixels(16), StaticHelpers.dpToPixels(16),StaticHelpers.dpToPixels(16),StaticHelpers.dpToPixels(16));
        container.addView(label);
    }

    /**
     *  интерфейс, обрабатывающий события для второстепенных переводов
     */
    public interface OnWordClickListener{
        void onWordClick(String word, Language language);
    }

    /**
     *  метод, отображающий модель
     */
    private void inflateContent() {

        clearView();

        // исключается отображение пустой модели
        if(translateResult.getTranslateResult()!=null && !translateResult.getTranslateResult().isEmptyContent()) {
            LinearLayout lastContainer = inflateTranslateResult();

            if(translateResult.getLookupResult()!=null && !translateResult.getLookupResult().isEmpty()) {
                lastContainer = inflateLookupResult(lastContainer);
            }

            if(!translateResult.isEmptyTranslate())
                inflateYandexLicenceLabel(lastContainer);
            else inflateHelpPanel(lastContainer);

        }

    }

    private void inflateHelpPanel(ViewGroup layout) {

    }


    /**
     * Метод заполняет результаты словаря
     * @param lastContainer контейнер, в который будут заполняться данные
     */
    private LinearLayout inflateLookupResult(LinearLayout lastContainer) {

        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView scrollView = new ScrollView(getContext());

        TextView textView = (TextView) layoutInflater.inflate(R.layout.word_lookup, null);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setClickable(true);

        textView.setText(translateResult.getLookupString(onWordClick));
        //textView.setVerticalScrollBarEnabled(true);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(VERTICAL);

        linearLayout.addView(textView);

        scrollView.addView(linearLayout);

        lastContainer.addView(scrollView);



        return linearLayout;
    }

    /**
     *
     * @return последний созданный контейнер
     */
    private LinearLayout inflateTranslateResult() {

        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(R.layout.word_layout,null);
        LinearLayout wordListContainer = (LinearLayout) layout.findViewById(R.id.text_list);


        for(int i = 0; i <  translateResult.getTranslateResult().getText().size(); i++){

            String text = translateResult.getTranslateResult().getText().get(i);
            ViewGroup wordItem = (ViewGroup) layoutInflater.inflate(R.layout.word_item, null);
            TextView translate = (TextView) wordItem.findViewById(R.id.t_word);
            translate.setText(text);

            //оригинальный текст, который будет отобрадаться только под первым вариантом перевода
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



    /**
     *  очистка представления
     */
    public void clearView() {
        if(this.getChildCount()>0){
            this.removeAllViews();
        }
    }

    public WordList(Context context) {
        super(context);
    }

    public WordList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WordList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



}
