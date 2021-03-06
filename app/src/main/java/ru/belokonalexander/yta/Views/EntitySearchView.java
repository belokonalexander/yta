package ru.belokonalexander.yta.Views;

/**
 * Created by Alexander on 31.03.2017.
 */


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.functions.Predicate;
import ru.belokonalexander.yta.Database.SearchEntity;
import ru.belokonalexander.yta.Database.SearchItem;
import ru.belokonalexander.yta.GlobalShell.Settings;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SearchInputData;

/**
 *  SearchView, которое инициализируется SearchEntity объектом
 *  с помощью рефлексси достаются поля (@SearchField), по которым может осуществляться поиск,
 *  инициализируется диалоговое окно
 *  Задача - отдать слушателю объект SearchInputData с значениями key + value
 */
public class EntitySearchView extends android.support.v7.widget.SearchView {

    /**
     * атрибуты xml
     */
    float textSize;
    int textColor;
    int buttonsColor;
    int hintTextColor;
    boolean iconified;
    float elementMargin;


    public EntitySearchView(Context context) {
        super(context);
    }

    public EntitySearchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.EntitySearchView,
                0, 0);

        try {
            textSize = a.getDimension(R.styleable.EntitySearchView_textSize, getContext().getResources().getDimension(R.dimen.text_normal)) / context.getResources().getDisplayMetrics().density;
            textColor = a.getColor(R.styleable.EntitySearchView_textColor, getContext().getResources().getColor(R.color.normal_text_color));
            buttonsColor = a.getColor(R.styleable.EntitySearchView_buttonsColor, getContext().getResources().getColor(R.color.normal_text_color));
            hintTextColor = a.getColor(R.styleable.EntitySearchView_hintTextColor, getContext().getResources().getColor(R.color.normal_text_color_hint));
            iconified  = a.getBoolean(R.styleable.EntitySearchView_iconified,false);
            elementMargin = a.getDimension(R.styleable.EntitySearchView_element_margin, getContext().getResources().getDimension(R.dimen.element_margin)) / context.getResources().getDisplayMetrics().density;
        } finally {
            a.recycle();
        }
    }

    /**
     *  текущая позиция позиция поискового параметра
     */
    private int keyPosition = 0;

    /**
     * диалог с выбором параметра поиска
     */
    AlertDialog.Builder alertDialog;

    /**
     * поле ввода текста поиска
     */
    EditText queryArea;


    /**
     * кнопка очистки формы ввода
     */
    ImageView closeBtn;

    /**
     * view кнопки поиска (лупа при закрытом состоянии)
     */
    View searchButton;

    /**
     *  настройка поиска
     */
    QueryTypeSettings queryTypeSettings = QueryTypeSettings.LOCAL_QUERY;



    /**
     * поисковые параметры полученного класса
     */
    List<SearchItem> searchParams = new ArrayList<>();

    /**
     * класс, описывающий свои поисковые параметры
     */
    Class < ? extends SearchEntity> searchedEntity;

    LinearLayout container;


    SearchInputData currentState = new SearchInputData();


    /**
     * инициализация элемента
     * @param searchedEntity
     */
    public void initSearch(Class < ? extends SearchEntity> searchedEntity) {

        this.searchedEntity = searchedEntity;
        searchParams = SearchItem.getSearchFieldsAndType(searchedEntity, getContext());
        this.initViews();
        this.initSegmentController();

    }

    /**
     * инициализация элемента с заданными настройками
     * @param searchedEntity
     * @param queryTypeSettings
     */
    public void initSearch(Class < ? extends SearchEntity> searchedEntity, QueryTypeSettings queryTypeSettings) {
        this.queryTypeSettings = queryTypeSettings;
        this.initSearch(searchedEntity);
    }

    /**
     * инициализация представлений, в основном тут косметические действия для дефолтного SearchEntity
     */
    private void initViews() {

        //тектовое поле поиска
        EditText searchTextView = (EditText) this.findViewById(R.id.search_src_text);
        queryArea = searchTextView;
        queryArea.setPadding(StaticHelpers.dpToPixels(16),0,0,0);
        ViewGroup searchPlate = (ViewGroup) this.findViewById(R.id.search_plate);
        searchPlate.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.transparent));
        searchButton = this.findViewById(R.id.search_button);
        searchButton.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.icon_left_right_width);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));
        if(searchParams.size()>1)
            imageView.setOnClickListener(v -> onParamsKeyClick());

        int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        imageView.setClickable(true);
        imageView.setBackgroundResource(backgroundResource);
        typedArray.recycle();


        imageView.setLayoutParams(lp);
        imageView.setColorFilter(buttonsColor, PorterDuff.Mode.MULTIPLY);
        imageView.setPadding(StaticHelpers.dpToPixels(14),0,0,0);
        searchPlate.addView(imageView, 0);

        searchTextView.setTextColor(textColor);
        searchTextView.setTextSize(textSize);
        searchTextView.setHintTextColor(hintTextColor);

       //кнопка отмены
        closeBtn = (ImageView) this.findViewById(R.id.search_close_btn);
        closeBtn.setPadding(0,0,StaticHelpers.dpToPixels(8),0);
        hideCloseButton();

        ImageView searchGo = (ImageView) findViewById(R.id.search_go_btn);
        searchGo.setPadding(0,0,StaticHelpers.dpToPixels(8),0);
        ((LinearLayout.LayoutParams)searchGo.getLayoutParams()).setMargins((int) elementMargin,0,0,0);
        //searchGo.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.transparent));
        searchGo.setColorFilter(buttonsColor, PorterDuff.Mode.MULTIPLY);

        View submitLayout = findViewById(R.id.submit_area);
        submitLayout.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.transparent));

        View searchEdit = findViewById(R.id.search_edit_frame);
        ((LinearLayout.LayoutParams)searchEdit.getLayoutParams()).setMargins(0,0,0,0);


        //если режим отображения задан как развернутый (т.е не надо кликать по иконке, чтобы открылся TextView
        if(!iconified){
            setIconified(false);
            clearFocus();
            closeBtn.setOnClickListener(v -> queryArea.setText(""));
        }



        searchTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    showCloseButton();
                else {
                    hideCloseButton();
                }
            }
        });


        queryArea.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                    clearFocus();
                    return true;
                }

                return false;
            }
        });


    }




    private void hideCloseButton(){
        closeBtn.setEnabled(false);
        closeBtn.setColorFilter(ContextCompat.getColor(getContext(),android.R.color.transparent), PorterDuff.Mode.MULTIPLY);
    }
    private void showCloseButton(){
        closeBtn.setEnabled(true);
        closeBtn.setColorFilter(buttonsColor, PorterDuff.Mode.MULTIPLY);
    }


    public void clearTextWithoutUpdate(){
        if(queryArea.getText().length()>0) {
            clearUpdateFlag = true;
            queryArea.setText("");
        }
    }

    /**
     * создание всплывающего окна
     */
    private void initSegmentController() {
        alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(getContext().getResources().getString(R.string.search_params));
        alertDialog.create();
        onChangeSearchParameter(keyPosition);
    }

    private void onParamsKeyClick()
    {
        if(searchParams.size()==0) return;

        String[] array = new String[searchParams.size()];
        for(int i=0; i<searchParams.size();i++)
            array[i] = searchParams.get(i).getAlias().substring(0,1).toUpperCase() + searchParams.get(i).getAlias().substring(1);

        alertDialog.setSingleChoiceItems(array, keyPosition, (dialog, which) -> {

            onChangeSearchParameter(which);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 150);
        });

        alertDialog.show();
    }



    SearchItem getCurrentSearchItem()
    {
        return searchParams.get(keyPosition);
    }


    public void onChangeSearchParameter(int index)
    {

        keyPosition = index;
        queryArea.setHint(searchParams.get(keyPosition).getAlias().substring(0, 1).toUpperCase() + searchParams.get(keyPosition).getAlias().substring(1));
        currentState.setKey(searchParams.get(keyPosition).getName());
        currentState.setFullContains(searchParams.get(keyPosition).getFullContain());
        this.setQuery("", false);
        if(getCurrentSearchItem().getLazyType()) {
            this.setSubmitButtonEnabled(false);
            rxLazy();
        }
        else {
            this.setSubmitButtonEnabled(true);
            rxNotLazy();
        }

    }



    boolean clearUpdateFlag = false;

    /**
     * ленивый поиск
     */
    private void rxLazy()
    {


        RxSearchView.queryTextChanges(this)
                .skip(1)
                .filter(searchViewQueryTextEvent -> {

                    StaticHelpers.LogThisHis("CLEAR FLAG: " + clearUpdateFlag);

                    if(clearUpdateFlag) {
                        clearUpdateFlag = false;
                        return false;
                    }

                    return true;
                })
                .debounce(queryTypeSettings.getDebounce(), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(f -> {
                    if(this.getQuery().length() == 0 ){

                        startEmpty();
                        return false;
                    }
                    return true;
                })
                .subscribe(f -> startSearch());



    }


    /**
     * поиск с подтверждением
     */
    private void rxNotLazy()
    {

        RxSearchView.queryTextChangeEvents(this)

                .skip(1)
                .filter(searchViewQueryTextEvent -> {
                    if(clearUpdateFlag) {
                        clearUpdateFlag = false;
                        return false;
                    }

                    return true;
                })
                .throttleFirst(queryTypeSettings.getThrottle(), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(f -> {
                    if(this.getQuery().length() == 0 ){
                        startEmpty();
                        return false;
                    }
                    return true;
                })
                .filter(f -> (getCurrentSearchItem().getLazyType() || (!getCurrentSearchItem().getLazyType() && f.isSubmitted())))
                .subscribe(f -> startSearch());
    }


    SearchTask searchTask;

    public EntitySearchView setSearchTask(SearchTask searchTask) {
        this.searchTask = searchTask;
        return this;
    }

    void startSearch(){
        currentState.setValue(getQuery().toString().trim());
        if(searchTask!=null) {
            searchTask.startSearch(currentState);
        }
    }

    void startEmpty(){
        currentState.setValue(getQuery().toString().trim());
        if(searchTask!=null) {
            searchTask.startEmpty(currentState);

        }
    }

    public interface SearchTask{
        void startSearch(SearchInputData data);
        void startEmpty(SearchInputData data);
    }


    public enum QueryTypeSettings{
        //для разного поиска - разные значения задержки ввода, например для "удаленного" api поиска значение debounce может быть больше
        LOCAL_QUERY(Settings.SEARCH_DEBOUNCE_LOCAL,Settings.THROTTLE_CLICK_VALUE_LOCAL),
        REMOTE_QUERY(Settings.SEARCH_DEBOUNCE,Settings.THROTTLE_CLICK_VALUE);

        int debounce;
        int throttle;

        public int getDebounce() {
            return debounce;
        }

        public int getThrottle() {
            return throttle;
        }

        QueryTypeSettings(int searchDebounce, int throttleValue) {
            debounce = searchDebounce;
            throttle = throttleValue;
        }
    }


}

