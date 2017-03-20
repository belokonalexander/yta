package ru.belokonalexander.yta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.belokonalexander.yta.GlobalShell.ApiChainRequestWrapper;
import ru.belokonalexander.yta.GlobalShell.Models.CurrentLanguage;
import ru.belokonalexander.yta.GlobalShell.OnApiFailureResponseListener;
import ru.belokonalexander.yta.GlobalShell.OnApiSuccessResponseListener;
import ru.belokonalexander.yta.GlobalShell.ServiceGenerator;
import ru.belokonalexander.yta.GlobalShell.SharedAppPrefs;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.CustomTexInputView;
import ru.belokonalexander.yta.Views.DebouncedEditText;


/**
 * Created by Alexander on 16.03.2017.
 */

public class ActionFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.wrapper)
    CustomTexInputView customTexInputView;

    CurrentLanguage currentLanguage;


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



    TextView languageFromTextView;
    TextView languageToTextView;

    void initViews(CurrentLanguage language){
        LayoutInflater mInflater=LayoutInflater.from(getContext());
        View customView = mInflater.inflate(R.layout.current_languages, null);
        toolbar.addView(customView);

        languageFromTextView = (TextView) customView.findViewById(R.id.language_from);
        languageToTextView = (TextView) customView.findViewById(R.id.language_to);

        languageFromTextView.setText(language.getLangFromDesc());
        languageToTextView.setText(language.getLangToDesc());

        customTexInputView.setOnTextListener(new DebouncedEditText.OnTextActionListener() {
            @Override
            public void onTextAction(String text) {

                String hash = StaticHelpers.getParentHash(this.getClass());
                StaticHelpers.LogThis(" Hash: " + hash);

                ApiChainRequestWrapper.getApartInstance(StaticHelpers.getParentHash(this.getClass()), result -> {
                    //
                    StaticHelpers.LogThis(" Результат: " + result + " -> " + Thread.currentThread().getName() );
                },
                        ServiceGenerator.getTranslateApi().translate(text, language.getLangFrom() + "-" + language.getLangTo()),
                        ServiceGenerator.getDictionaryApi().lookup(text, language.getLangFrom() + "-" + language.getLangTo())).execute();

            }
        });



    }

}

