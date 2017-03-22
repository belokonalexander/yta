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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.SafeObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.belokonalexander.yta.GlobalShell.ApiChainRequestWrapper;
import ru.belokonalexander.yta.GlobalShell.Models.CompositeTranslateModel;
import ru.belokonalexander.yta.GlobalShell.Models.CurrentLanguage;
import ru.belokonalexander.yta.GlobalShell.Models.Rx.CustomDisposableObserver;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateResult;
import ru.belokonalexander.yta.GlobalShell.ServiceGenerator;
import ru.belokonalexander.yta.GlobalShell.SharedAppPrefs;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.CustomTexInputView;
import ru.belokonalexander.yta.Views.DebouncedEditText;
import ru.belokonalexander.yta.Views.WordList;


/**
 * Created by Alexander on 16.03.2017.
 */

public class ActionFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.word_list)
    WordList wordList;

    @BindView(R.id.wrapper)
    CustomTexInputView customTexInputView;

    CurrentLanguage currentLanguage;

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

    CustomDisposableObserver<CurrentLanguage> header = new CustomDisposableObserver<CurrentLanguage>() {
        @Override
        public void init(CurrentLanguage language) {
            LayoutInflater mInflater=LayoutInflater.from(getContext());
            View customView = mInflater.inflate(R.layout.current_languages, null);
            toolbar.addView(customView);

            languageFromTextView = (TextView) customView.findViewById(R.id.language_from);
            languageToTextView = (TextView) customView.findViewById(R.id.language_to);
            swapTextView = customView.findViewById(R.id.swap_language_button);
            RxView.clicks(swapTextView).subscribe(o -> {
                language.swapLanguages();
            });

            disposables.add(language.getChanged().observeOn(AndroidSchedulers.mainThread()).subscribeWith(this));
            onNext(language);
        }

        @Override
        public void onNext(CurrentLanguage value) {
            languageFromTextView.setText(value.getLangFromDesc());
            languageToTextView.setText(value.getLangToDesc());
        }

    };

    void initViews(CurrentLanguage language){

        header.init(language);

        customTexInputView.setOnTextListener(new DebouncedEditText.OnTextActionListener() {

            @Override
            public void onTextAction(String text) {

                String hash = StaticHelpers.getParentHash(this.getClass());




                getTranslete = ApiChainRequestWrapper.getApartInstance(StaticHelpers.getParentHash(this.getClass()), result -> {
                            StaticHelpers.LogThis(" Результат: " + result + " -> " + Thread.currentThread().getName() );

                            /*
                                если ответ со словом вернулся без ошибки, то заполняем wordList +
                                    назначаем ему слушателя на слово-синоним (яндекс.словарь)
                             */
                            if(result.get(0) instanceof TranslateResult) {
                                wordList.setTranslateResult(new CompositeTranslateModel(result.get(0), result.get(1), text),
                                        word -> Observable.fromCallable(() -> {
                                            language.swapLanguages();
                                            return word;
                                        }).subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(o -> {
                                                    customTexInputView.setText(o);
                                                }));

                            } else {
                                //TODO критическая ошибка
                                StaticHelpers.LogThis(" критическая ошибка ");
                            }

                        },
                        ServiceGenerator.getTranslateApi().translate(text, language.getLangFrom() + "-" + language.getLangTo()),
                        ServiceGenerator.getDictionaryApi().lookup(text, language.getLangFrom() + "-" + language.getLangTo()));

                getTranslete.execute();
            }

            @Override
            public void onTextClear() {
                if(getTranslete!=null){
                    getTranslete.cancel();
                }

                wordList.clearView();
            }
        });



    }

}

