package ru.belokonalexander.yta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.jakewharton.rxbinding2.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.belokonalexander.yta.GlobalShell.ApiChainRequestWrapper;
import ru.belokonalexander.yta.GlobalShell.Models.CompositeTranslateModel;
import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.GlobalShell.Models.Rx.CustomDisposableObserver;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateResult;
import ru.belokonalexander.yta.GlobalShell.ServiceGenerator;
import ru.belokonalexander.yta.GlobalShell.SharedAppPrefs;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.CustomTexInputView;

import ru.belokonalexander.yta.Views.WordList;


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

    Language currentLanguage;

    ApiChainRequestWrapper getTranslete;
    TextView languageFromTextView;
    TextView languageToTextView;
    View swapTextView;
    CompositeDisposable disposables = new CompositeDisposable();


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



        return view;
    }




    CustomDisposableObserver<Language> header = new CustomDisposableObserver<Language>() {
        @Override
        public void init(Language language) {
            LayoutInflater mInflater=LayoutInflater.from(getContext());
            View customView = mInflater.inflate(R.layout.current_languages, null);
            toolbar.addView(customView);

            languageFromTextView = (TextView) customView.findViewById(R.id.language_from);
            languageToTextView = (TextView) customView.findViewById(R.id.language_to);
            swapTextView = customView.findViewById(R.id.swap_language_button);
            RxView.clicks(swapTextView).subscribe(o -> {
                language.swapLanguages();
                customTexInputView.reset();
            });


            disposables.add(language.getChanged().observeOn(AndroidSchedulers.mainThread()).subscribeWith(this));
            onNext(language);
        }

        @Override
        public void onNext(Language value) {
            languageFromTextView.setText(value.getLangFromDesc());
            languageToTextView.setText(value.getLangToDesc());
        }

    };

    void initViews(Language language){
        currentLanguage = language;
        header.init(language);
        customTexInputView.setOnTextListener(this);
        customTexInputView.setText("привет");
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.dispose();
    }

    @Override
    public void onTextAction(String text) {

        Observable[] requests = { ServiceGenerator.getTranslateApi().translate(text, currentLanguage.getLangFrom() + "-" + currentLanguage.getLangTo()),
                                  ServiceGenerator.getDictionaryApi().lookup(text, currentLanguage.getLangFrom() + "-" + currentLanguage.getLangTo())};

        String hash = StaticHelpers.getParentHash(this.getClass());

        getTranslete = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            /*
             *   если ответ со словом вернулся без ошибки, то заполняем wordList +
             *      назначаем ему слушателя на слово-синоним (яндекс.словарь)
            */
            if(result.get(0) instanceof TranslateResult) {
                CompositeTranslateModel model = new CompositeTranslateModel(result.get(0),result.get(1), text);

                wordList.setTranslateResult(model, (word, inputLang) -> {
                            if(currentLanguage.equals(inputLang)){
                                currentLanguage.swapLanguages();
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
}

