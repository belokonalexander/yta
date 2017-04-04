package ru.belokonalexander.yta.Views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateLanguage;
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
    private CompositeTranslateModel translate;
    private OnWordClickListener onWordClick;
    ImageButton favorite;
    Toast toastNotification = Toast.makeText(getContext(), null, Toast.LENGTH_SHORT);

    /**
     *
     * @param translateResult модель, которая будет тображаться
     * @param onClickListener слушатель, реагирующий на клик по варианту второстепенного перевода
     */
    public void setTranslateResult(CompositeTranslateModel translateResult, OnWordClickListener onClickListener) {
        this.translate = translateResult;
        this.onWordClick = onClickListener;

        inflateContent();
    }

    @Override
    public void inflateYandexLicenceLabel(ViewGroup container) {


        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView label = (TextView) layoutInflater.inflate(R.layout.word_lookup, null);
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
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(),R.color.yandex_accent)),start, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    public void tryToUpdateFavoriteStatus(CompositeTranslateModel translateModel) {
        if(translateModel.equals(translate)){
            updateFavoriteButton();
        }
    }



    /**
     *  интерфейс, обрабатывающий события для второстепенных переводов
     */
    public interface OnWordClickListener{
        void onWordClick(String word, TranslateLanguage language);
    }

    /**
     *  метод, отображающий модель
     */
    private void inflateContent() {

            clearView();


            LinearLayout lastContainer = inflateTranslateResult();

            if(!translate.lookupIsDummy())
                lastContainer = inflateLookupResult(lastContainer);

            if(!translate.isUselessTranslate())
                inflateYandexLicenceLabel(lastContainer);

    }

    private void inflateHelpPanel(ViewGroup layout) {

    }


    /**
     * заполнение результатов словаря
     * @param lastContainer контейнер, в который будут заполняться данные
     */
    private LinearLayout inflateLookupResult(LinearLayout lastContainer) {

        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView scrollView = new ScrollView(getContext());

        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        TextView textView = (TextView) layoutInflater.inflate(R.layout.word_lookup, null);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setClickable(true);

        textView.setText(translate.getLookupString((word, language) -> onWordClick.onWordClick(word,language)));
        textView.setOnTouchListener((v, event) -> {
            requestFocus();
            return false;
        });


        //textView.setVerticalScrollBarEnabled(true);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(VERTICAL);

        linearLayout.addView(textView);

        scrollView.addView(linearLayout);

        lastContainer.addView(scrollView);



        return linearLayout;
    }

    public void clearFavoriteStatus(){
        translate.setFavorite(false);
        updateFavoriteButton();
    }

    private void updateFavoriteButton(){
        if(translate.getFavorite()){
            favorite.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
            favorite.setColorFilter(getContext().getResources().getColor(R.color.tint_color_active), PorterDuff.Mode.SRC_IN);
        } else {
            favorite.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_bookmark_border_white_24dp));
            favorite.setColorFilter(getContext().getResources().getColor(R.color.tint_color_accent), PorterDuff.Mode.SRC_IN);
        }

    }

    /**
     * заполнение результатов перевода
     * @return последний созданный контейнер
     */
    private LinearLayout inflateTranslateResult() {

        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(R.layout.word_layout,null);

        LinearLayout wordListContainer = (LinearLayout) layout.findViewById(R.id.text_list);
        favorite = (ImageButton) layout.findViewById(R.id.save_word);

        updateFavoriteButton();

        favorite.setOnClickListener(v -> {
            requestFocus();
            translate.changeFavoriteStatus();
        });

            String text = translate.getTranslate();
            ViewGroup wordItem = (ViewGroup) layoutInflater.inflate(R.layout.word_item, null);
            wordItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(null, text);
                    clipboard.setPrimaryClip(clip);
                    requestFocus();

                    toastNotification.setText(getResources().getString(R.string.translate_result_saved));
                    toastNotification.show();

                }
            });
            TextView translateResult = (TextView) wordItem.findViewById(R.id.t_word);
            translateResult.setText(text);

            //оригинальный текст, который будет отобрадаться только под первым вариантом перевода
            TextView original = (TextView) wordItem.findViewById(R.id.original_word);
            original.setText(translate.getSource());
            wordListContainer.addView(wordItem);

        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.addView(layout);
        this.addView(scrollView);

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
