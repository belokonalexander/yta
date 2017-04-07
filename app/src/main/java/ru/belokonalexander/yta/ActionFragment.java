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
import android.widget.ProgressBar;
import android.widget.TextView;


import com.jakewharton.rxbinding2.view.RxView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;


import io.reactivex.Observable;
import ru.belokonalexander.yta.Database.CompositeTranslateModel;

import ru.belokonalexander.yta.Events.FavoriteClearEvent;
import ru.belokonalexander.yta.Events.ShowWordEvent;
import ru.belokonalexander.yta.Events.WordFavoriteStatusChangedEvent;
import ru.belokonalexander.yta.GlobalShell.ApiChainRequestWrapper;
import ru.belokonalexander.yta.GlobalShell.HistorySaver;
import ru.belokonalexander.yta.GlobalShell.Models.AllowedLanguages;
import ru.belokonalexander.yta.GlobalShell.Models.ApplicationException;
import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.LookupResult;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateLanguage;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateResult;
import ru.belokonalexander.yta.GlobalShell.ServiceGenerator;
import ru.belokonalexander.yta.GlobalShell.SharedAppPrefs;
import ru.belokonalexander.yta.GlobalShell.SimpleAsyncTask;
import ru.belokonalexander.yta.GlobalShell.SimpleRequestsManager;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.CustomTexInputView;

import ru.belokonalexander.yta.Views.Helpers.ErrorResolver;
import ru.belokonalexander.yta.Views.Helpers.OutputText;
import ru.belokonalexander.yta.Views.WordList;

import static ru.belokonalexander.yta.ChooseLanguageDialog.INPUT_LANGUAGE_CHANGE_REQUEST_CODE;
import static ru.belokonalexander.yta.ChooseLanguageDialog.LANG_LEY;
import static ru.belokonalexander.yta.ChooseLanguageDialog.OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE;


/**
 * фрагмент, реализующий функционал перевода
 */

public class ActionFragment extends Fragment implements CustomTexInputView.OnTextActionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.word_list)
    WordList wordList;

    @BindView(R.id.wrapper)
    CustomTexInputView customTexInputView;

    @BindView(R.id.loading_bar)
    ProgressBar loadingBar;

    TranslateLanguage currentLanguage;
    ApiChainRequestWrapper getTranslete;
    TextView languageFromTextView;
    TextView languageToTextView;
    View swapTextView;
    SimpleRequestsManager requestsManager = new SimpleRequestsManager();
    HistorySaver historySaver = new HistorySaver();


    public final String IS_WORD_LIST = "WordListData";
    public final String IS_LANGUAGE = "Language";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_action,container,false);
        ButterKnife.bind(this, view);


        if(savedInstanceState==null)
            //инициализации представления фрагмента
            SimpleAsyncTask.run(() -> SharedAppPrefs.getInstance().getLanguage(), this::initViews);
        else {
            //восстановление состояния фрагмента

            TranslateLanguage language = (TranslateLanguage) savedInstanceState.getSerializable(IS_LANGUAGE);
            initViews(language);

            Object lastStateWordList = savedInstanceState.getSerializable(IS_WORD_LIST);
                if(!(lastStateWordList instanceof ApplicationException)) {
                    CompositeTranslateModel compositeTranslateModel = (CompositeTranslateModel) lastStateWordList;
                    fillWordList(compositeTranslateModel);
                }
        }

        requestsManager.addRequest(getTranslete);


        return view;
    }


    private void initHeader(){
        LayoutInflater mInflater=LayoutInflater.from(getContext());
        View customView = mInflater.inflate(R.layout.current_languages, null);
        toolbar.addView(customView);
        toolbar.setContentInsetsAbsolute(0,0);
        languageFromTextView = (TextView) customView.findViewById(R.id.language_from);
        languageToTextView = (TextView) customView.findViewById(R.id.language_to);
        swapTextView = customView.findViewById(R.id.swap_language_button);
        languageFromTextView.setText(currentLanguage.getLangFromDesc());
        languageFromTextView.setOnClickListener(v -> changeLanguage(INPUT_LANGUAGE_CHANGE_REQUEST_CODE));
        languageToTextView.setText(currentLanguage.getLangToDesc());
        languageToTextView.setOnClickListener(v -> changeLanguage(OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE));
        RxView.clicks(swapTextView).subscribe(o -> {
            swapLanguages(true);
        });
    }


    void initViews(TranslateLanguage language){
        currentLanguage = language;
        initHeader();
        customTexInputView.setOnTextListener(this);
    }


    /**
     * ОБработка события: в текстовом поле был изменен текст
     * @param outputText
     */
    @Override
    public void onTextAction(OutputText outputText) {


        loadingBar.setVisibility(View.VISIBLE);

        //прерываем предыдущий запрос
        if(getTranslete!=null)
            getTranslete.cancel();

        //проверяю значение в истории
        SimpleAsyncTask.run(() -> CompositeTranslateModel.getBySource(outputText.getValue(),currentLanguage), result -> {

            if(result!=null) {
                historySaver.delayedSavingWord(result, outputText.getType());
                fillWordList(result);
            }
            else {

                requestTranslateFromApi(outputText);
            }
        });


    }

    /**
     * запрос пеервода с сервера
     * @param text
     */
    private void requestTranslateFromApi(OutputText text){

        //отправляю запрос
        Observable[] requests = { ServiceGenerator.getTranslateApi().translate(text.getValue(), currentLanguage.getLangFrom() + "-" + currentLanguage.getLangTo()),
                ServiceGenerator.getDictionaryApi().lookup(text.getValue(), currentLanguage.getLangFrom() + "-" + currentLanguage.getLangTo())};

        String hash = StaticHelpers.getParentHash(this.getClass());

        getTranslete = ApiChainRequestWrapper.getApartInstance(hash, result -> {

            if(result.get(0) instanceof TranslateResult) {

                String textResult = ((TranslateResult)result.get(0)).getText().get(0);
                LookupResult lookupResult = null;
                if(result.get(1) instanceof LookupResult){
                    lookupResult = (LookupResult) result.get(1);
                }

                CompositeTranslateModel model = new CompositeTranslateModel(null, text.getValue(), TranslateLanguage.cloneFabric(currentLanguage), textResult, null, null, false, true, lookupResult);

                historySaver.delayedSavingWord(model, text.getType());
                fillWordList(model);


            }  else {
                //критическая ошибка при запросе
                wordList.displayError((ApplicationException) result.get(0), new ErrorResolver() {
                    @Override
                    public void resolve() {
                        historySaver.setIntentSaver(text.getValue(),currentLanguage);
                        getTranslete.execute();
                    }
                });
                loadingBar.setVisibility(View.INVISIBLE);
            }
        }, requests);

        getTranslete.execute();

    }


    /**
     * заполнение результата перевода
     * @param compositeTranslateModel
     */
    private void fillWordList(CompositeTranslateModel compositeTranslateModel){

        loadingBar.setVisibility(View.INVISIBLE);

        wordList.setTranslateResult(compositeTranslateModel, (word, inputLang) -> {
                    if(currentLanguage.equals(inputLang)){
                        swapLanguages(false);
                    }
                    customTexInputView.setText(word);
                }
        );
    }


    /**
     * текстовое поле было очищено
     */
    @Override
    public void onTextClear() {
        //запрос прерывается
        if(getTranslete!=null){
            getTranslete.cancel();
            loadingBar.setVisibility(View.INVISIBLE);
        }
        //очищается предыдущий результат и сохраняется в историю
        wordList.clearState();
        historySaver.pushLast();
    }

    /**
     * слово было подтверждено для перевода, т.е оно сохранится в историю
     * т.е пользователь скрыл клавиатуру или нажал DONE
     * @param done
     */
    @Override
    public void onTextDone(OutputText done) {
        historySaver.setIntentSaver(done.getValue(),currentLanguage);
    }


    /**
     * смена языка
     * @param reset необходимо ли перевести слово, находящееся в поле ввода, с применением нового языка
     */
    private void swapLanguages(boolean reset){
        currentLanguage.swapLanguages();
        languageWasChanged(reset);
    }

    @Override
    public void onStop() {
        super.onStop();
        requestsManager.clear();
        EventBus.getDefault().unregister(this);;
    }



    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        //если есть несохраненное в истории слово, то сохраняем его
        historySaver.pushLast();
    }

    /**
     * обработка события для показа слова
     * @param translateWord
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showWord(ShowWordEvent translateWord){
        CompositeTranslateModel translateModel = translateWord.getTranslateModel();
        if(translateModel.getLang().descIsEmpty()) {

            SimpleAsyncTask.run(() -> {
                AllowedLanguages allowedLanguages = SharedAppPrefs.getInstance().getLanguageLibrary();
                Language from = translateModel.getLang().getFrom();
                Language to = translateModel.getLang().getTo();
                from.setDesc(allowedLanguages.getDesc(from.getCode()));
                to.setDesc(allowedLanguages.getDesc(to.getCode()));
                translateModel.getLang().setFrom(from);
                translateModel.getLang().setTo(to);
                return translateModel;
            }, this::showNewWordsView);
        } else {

            showNewWordsView(translateModel);
        }

    }

    /**
     * обработка события для отмены избранного
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clearFavorite(FavoriteClearEvent event){
        wordList.clearFavoriteStatus();
    }


    private void showNewWordsView(CompositeTranslateModel item){

        OutputText outputText = customTexInputView.setWithoutUpdate(item.getSource());
        if(!currentLanguage.equals(item.getLang())){
            //создается новый объект, чтобы не связываться с прилетевшим объектом
            currentLanguage = TranslateLanguage.cloneFabric(item.getLang());
            languageWasChanged(false);
        }
        historySaver.delayedSavingWord(item,outputText.getType());
        fillWordList(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFavoriteState(WordFavoriteStatusChangedEvent event){
        CompositeTranslateModel translateModel = event.getTranslateModel();
        wordList.tryToUpdateFavoriteStatus(translateModel);
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

            switch (requestCode) {
                case INPUT_LANGUAGE_CHANGE_REQUEST_CODE:
                    currentLanguage.setFrom((Language) data.getSerializableExtra(LANG_LEY));
                    break;

                case OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE:
                    currentLanguage.setTo((Language) data.getSerializableExtra(LANG_LEY));
                    break;
            }

            languageWasChanged(true);

        }
    }

    /**
     * реакция на смену языка
     * @param reset необходимо ли повторять последнюю операцию, но с новым языком
     */
    private void languageWasChanged(boolean reset){
        languageToTextView.setText(currentLanguage.getLangToDesc());
        languageFromTextView.setText(currentLanguage.getLangFromDesc());
        SimpleAsyncTask.run(() -> {
            SharedAppPrefs.getInstance().setLanguage(currentLanguage);
            return null;
        });

        if(reset)
            customTexInputView.reset();
    }



    private void changeLanguage(int directionCode){
        ChooseLanguageDialog dialog = new ChooseLanguageDialog();
        dialog.setTargetFragment(this,directionCode);
        dialog.show(getActivity().getSupportFragmentManager());
    }

    /**
     * сохранение состояния фрагмента
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        CompositeTranslateModel result = wordList.getTranslate();
        if(result!=null)
            outState.putSerializable(IS_WORD_LIST, wordList.getTranslate());
        else if(wordList.getLastError()!=null) {
            outState.putSerializable(IS_WORD_LIST, wordList.getLastError());
        }

        outState.putSerializable(IS_LANGUAGE,currentLanguage);
    }
}

