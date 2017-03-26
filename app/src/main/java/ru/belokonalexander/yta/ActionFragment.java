package ru.belokonalexander.yta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.belokonalexander.yta.Database.CacheModel;
import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.GlobalShell.ApiChainRequestWrapper;
import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.LookupResult;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateLanguage;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateResult;
import ru.belokonalexander.yta.GlobalShell.ServiceGenerator;
import ru.belokonalexander.yta.GlobalShell.SharedAppPrefs;
import ru.belokonalexander.yta.GlobalShell.SimpleRequestsManager;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.CustomTexInputView;

import ru.belokonalexander.yta.Views.WordList;

import static ru.belokonalexander.yta.ChooseLanguageDialog.INPUT_LANGUAGE_CHANGE_REQUEST_CODE;
import static ru.belokonalexander.yta.ChooseLanguageDialog.LANG_LEY;
import static ru.belokonalexander.yta.ChooseLanguageDialog.OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE;


/**
 * Created by Alexander on 16.03.2017.
 */

public class ActionFragment extends Fragment implements CustomTexInputView.OnTextActionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.word_list)
    WordList wordList;

    @BindView(R.id.wrapper)
    CustomTexInputView customTexInputView;

    TranslateLanguage currentLanguage;

    ApiChainRequestWrapper getTranslete;
    TextView languageFromTextView;
    TextView languageToTextView;
    View swapTextView;
    SimpleRequestsManager requestsManager = new SimpleRequestsManager();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_action,container,false);
        ButterKnife.bind(this, view);

        //инициализации представления фрагмента
        Observable.fromCallable(() -> SharedAppPrefs.getInstance().getLanguage())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initViews);

        requestsManager.addRequest(getTranslete);

        List<CacheModel> cache = YtaApplication.getDaoSession().getCacheModelDao().loadAll();

        StaticHelpers.LogThis(" ->>> " + cache.size() + "\n");
        StaticHelpers.LogThis(cache);
        StaticHelpers.LogThis(" ->>> " + cache.size() + "\n");

        return view;
    }


    private void initHeader(){
        LayoutInflater mInflater=LayoutInflater.from(getContext());
        View customView = mInflater.inflate(R.layout.current_languages, null);
        toolbar.addView(customView);

        languageFromTextView = (TextView) customView.findViewById(R.id.language_from);
        languageToTextView = (TextView) customView.findViewById(R.id.language_to);
        swapTextView = customView.findViewById(R.id.swap_language_button);
        languageFromTextView.setText(currentLanguage.getLangFromDesc());
        languageFromTextView.setOnClickListener(v -> changeLanguage(INPUT_LANGUAGE_CHANGE_REQUEST_CODE));
        languageToTextView.setText(currentLanguage.getLangToDesc());
        languageToTextView.setOnClickListener(v -> changeLanguage(OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE));
        RxView.clicks(swapTextView).subscribe(o -> {
            swapLanguages();
        });
    }


    void initViews(TranslateLanguage language){
        currentLanguage = language;
        initHeader();
        customTexInputView.setOnTextListener(this);
        customTexInputView.setText("пока");
    }


    @Override
    public void onTextAction(String text) {

        Observable[] requests = { ServiceGenerator.getTranslateApi().translate(text, currentLanguage.getLangFrom() + "-" + currentLanguage.getLangTo()),
                                  ServiceGenerator.getDictionaryApi().lookup(text, currentLanguage.getLangFrom() + "-" + currentLanguage.getLangTo())};

        String hash = StaticHelpers.getParentHash(this.getClass());


        getTranslete = ApiChainRequestWrapper.getApartInstance(hash, result -> {
                /*
                 *   если ответ со словом вернулся без ошибки, то сохраняем его в историю поиска, заполняем wordList +
                 *      назначаем ему слушателя на слово-синоним (яндекс.словарь)
                */
                if(result.get(0) instanceof TranslateResult) {

                    String textResult = ((TranslateResult)result.get(0)).getText().get(0);
                    LookupResult lookupResult = null;
                    if(result.get(1) instanceof LookupResult){
                        lookupResult = (LookupResult) result.get(1);
                    }

                    CompositeTranslateModel model = //new CompositeTranslateModel(null, text, currentLanguage, textResult, false, lookupResult);
                            YtaApplication.getDaoSession().getCompositeTranslateModelDao().loadByRowId(2);
                    //model.saveInHistory();
                    wordList.setTranslateResult(model, (word, inputLang) -> {
                                if(currentLanguage.equals(inputLang)){
                                   swapLanguages();
                                }
                                customTexInputView.setText(word);
                            }
                    );
                }  else {
                    //TODO критическая ошибка при запросе
                    StaticHelpers.LogThis(" критическая ошибка ");
                }
            }, requests);


        getTranslete.execute();
    }


    @Override
    public void onTextClear() {
        if(getTranslete!=null){
            getTranslete.cancel();
        }
        wordList.clearView();
    }


    private void swapLanguages(){
        currentLanguage.swapLanguages();
        languageWasChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        requestsManager.clear();
    }

    /**
     * Обработка результата работы фрагмента по смене языка ->
     *      ожидаем смены языка
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            StaticHelpers.LogThis("CHANGED: " + data.getExtras());

            switch (requestCode) {
                case INPUT_LANGUAGE_CHANGE_REQUEST_CODE:
                    currentLanguage.setFrom((Language) data.getSerializableExtra(LANG_LEY));
                    break;

                case OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE:
                    currentLanguage.setTo((Language) data.getSerializableExtra(LANG_LEY));
                    break;
            }

            languageWasChanged();

        }
    }

    private void languageWasChanged(){
        StaticHelpers.LogThis("lang: " + currentLanguage);
        languageToTextView.setText(currentLanguage.getLangToDesc());
        languageFromTextView.setText(currentLanguage.getLangFromDesc());
        customTexInputView.reset();
    }

    private void changeLanguage(int directionCode){

        ChooseLanguageDialog dialog = new ChooseLanguageDialog();
        dialog.setTargetFragment(this,directionCode);
        dialog.show(getActivity().getSupportFragmentManager());

    }
}

