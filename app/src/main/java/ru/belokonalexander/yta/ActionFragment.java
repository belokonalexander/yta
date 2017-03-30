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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.Events.EventCreateType;
import ru.belokonalexander.yta.Events.ShowWordEvent;
import ru.belokonalexander.yta.Events.WordFavoriteStatusChangedEvent;
import ru.belokonalexander.yta.Events.WordSavedInHistoryEvent;
import ru.belokonalexander.yta.GlobalShell.ApiChainRequestWrapper;
import ru.belokonalexander.yta.GlobalShell.Models.AllowedLanguages;
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

import ru.belokonalexander.yta.Views.OutputText;
import ru.belokonalexander.yta.Views.WordList;

import static ru.belokonalexander.yta.ChooseLanguageDialog.INPUT_LANGUAGE_CHANGE_REQUEST_CODE;
import static ru.belokonalexander.yta.ChooseLanguageDialog.LANG_LEY;
import static ru.belokonalexander.yta.ChooseLanguageDialog.OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE;
import static ru.belokonalexander.yta.GlobalShell.Settings.HISTORY_WORD_SAVE_DELAY;


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

    Timer delayedHistorySaveTimer = new Timer();

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

        YtaApplication.getDaoSession().getCompositeTranslateModelDao().deleteAll();


        for(int i = 0 ; i < 57; i++){
            YtaApplication.getDaoSession().getCompositeTranslateModelDao().save(new CompositeTranslateModel(null, "item " + i,
                    new TranslateLanguage("ru-en"), "translate " + i,new Date(), false, true, new LookupResult()));
        }

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
            swapLanguages(true);
        });
    }


    void initViews(TranslateLanguage language){
        currentLanguage = language;
        initHeader();
        customTexInputView.setOnTextListener(this);
        //customTexInputView.setText("пока");
    }


    @Override
    public void onTextAction(OutputText outputText) {

        StaticHelpers.LogThis(" Output -> " + outputText.getType());

        if(getTranslete!=null)
            getTranslete.cancel();
        //проверяю значение в истории
        SimpleAsyncTask.run(() -> CompositeTranslateModel.getBySource(outputText.getValue(),currentLanguage), result -> {
            StaticHelpers.LogThis(" Результат в базе: " + result);
            if(result!=null) {
                delayedSavingWord(result, outputText.getType());
                fillWordList(result);
            }
            else {
                StaticHelpers.LogThis(" ЗАПРООООООООООС: " + outputText.getValue());
                requestTranslateFromApi(outputText);
            }
        });


    }

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

                CompositeTranslateModel model = new CompositeTranslateModel(null, text.getValue(), TranslateLanguage.cloneFabric(currentLanguage), textResult, new Date(), false, true, lookupResult);

                delayedSavingWord(model, text.getType());
                fillWordList(model);


            }  else {
                //TODO критическая ошибка при запросе
                StaticHelpers.LogThis(" критическая ошибка при запросе ");
            }
        }, requests);

        getTranslete.execute();

    }



    private void fillWordList(CompositeTranslateModel compositeTranslateModel){



        wordList.setTranslateResult(compositeTranslateModel, (word, inputLang) -> {
                    if(currentLanguage.equals(inputLang)){
                        swapLanguages(false);
                    }
                    customTexInputView.setText(word);
                }
        );
    }



    /**
     * сохранение слова в историю поиска
     * задержка нужна для того, чтобы исключить сохранение части по ходу ввода пользователя
     * Пример, когда пользователь вводит слово 'Привет':
     * -> 'При' -> debounce-интервал прошел -> Выведен результат для 'при' -> ввод продолжается -> 'вет'
     * без задержки в истории будет 2 слова: 'При' и 'Привет', с задержкой добавляется интервал, исключающий такое поведение
     * @param compositeTranslateModel сохраняемое слово
     */
    private void delayedSavingWord(CompositeTranslateModel compositeTranslateModel, OutputText.Type type) {

        int delay = type.getDelay();

        delayedHistorySaveTimer.cancel();
        delayedHistorySaveTimer = new Timer();
        delayedHistorySaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                StaticHelpers.LogThis("SAVE IN DB");
                compositeTranslateModel.save();

                EventBus.getDefault().post(new WordSavedInHistoryEvent(compositeTranslateModel));
            }
        },delay);
    }


    @Override
    public void onTextClear() {
        if(getTranslete!=null){
            getTranslete.cancel();
        }
        delayedHistorySaveTimer.cancel();
        wordList.clearView();
    }


    private void swapLanguages(boolean reset){
        currentLanguage.swapLanguages();
        languageWasChanged(reset);
    }

    @Override
    public void onStop() {
        super.onStop();
        requestsManager.clear();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

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

    private void showNewWordsView(CompositeTranslateModel item){

        OutputText outputText = customTexInputView.setWithoutUpdate(item.getSource());
        if(!currentLanguage.equals(item.getLang())){
            //создается новый объект, чтобы не связываться с прилетевшим объектом
            currentLanguage = TranslateLanguage.cloneFabric(item.getLang());
            languageWasChanged(false);
        }
        delayedSavingWord(item,outputText.getType());
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
            StaticHelpers.LogThis("CHANGED: " + data.getExtras());

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

    private void languageWasChanged(boolean reset){
        languageToTextView.setText(currentLanguage.getLangToDesc());
        languageFromTextView.setText(currentLanguage.getLangFromDesc());
        if(reset)
            customTexInputView.reset();
    }



    private void changeLanguage(int directionCode){
        ChooseLanguageDialog dialog = new ChooseLanguageDialog();
        dialog.setTargetFragment(this,directionCode);
        dialog.show(getActivity().getSupportFragmentManager());
    }
}

