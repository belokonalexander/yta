package ru.belokonalexander.yta;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.belokonalexander.yta.Adapters.CommonAdapter;
import ru.belokonalexander.yta.Adapters.LanguageAdapter;
import ru.belokonalexander.yta.GlobalShell.ApiChainRequestWrapper;
import ru.belokonalexander.yta.GlobalShell.Models.AllowedLanguages;
import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateLanguage;
import ru.belokonalexander.yta.GlobalShell.OnApiSuccessResponseListener;
import ru.belokonalexander.yta.GlobalShell.ServiceGenerator;
import ru.belokonalexander.yta.GlobalShell.SharedAppPrefs;
import ru.belokonalexander.yta.GlobalShell.SimpleRequestsManager;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;


/**
 * Created by Alexander on 24.03.2017.
 */

public class ChooseLanguageDialog extends DialogFragment {

    public static final int INPUT_LANGUAGE_CHANGE_REQUEST_CODE = 100;
    public static final int OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE = 101;
    public static final String LANG_LEY = "_LANG";

    SimpleRequestsManager requestsManager = new SimpleRequestsManager();

    Language part;
    LanguageAdapter languageAdapter;
    Button updateButton;
    ApiChainRequestWrapper getLanguages;

    Button cancel;
    Button updateLanguages;

    public void show(FragmentManager manager){
        StaticHelpers.LogThis(" Show dialog");
        super.show(manager,"changeLanguage");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        requestsManager.addRequest(getLanguages);

        languageAdapter = new LanguageAdapter(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.change_language_dialog, null);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        cancel = (Button) view.findViewById(R.id.cancel);
        updateButton = (Button) view.findViewById(R.id.update);

        cancel.setOnClickListener(v -> dismiss());

        updateButton.setOnClickListener(v -> updateLanguages());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(languageAdapter);

        // присваиваем адаптер списку
        languageAdapter.setOnClickListener(item -> {
            Intent response = new Intent();
            response.putExtra(LANG_LEY,item);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, response);
            dismiss();
        });

        String title = "";

        switch (getTargetRequestCode()) {
            case INPUT_LANGUAGE_CHANGE_REQUEST_CODE:
                title = getResources().getString(R.string.input_language_cahnge);
                break;

            case OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE:
                title = getResources().getString(R.string.output_language_cahnge);
                break;
        }

        titleView.setText(title);

        builder.setView(view);

        String hash = StaticHelpers.getParentHash(this.getClass());
        getLanguages = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            if(!(result.get(0) instanceof Throwable)){
                updateLanguageLLibrary((AllowedLanguages) result.get(0));
            }
            updateButton.setEnabled(true);
        }, ServiceGenerator.getTranslateApi().getLangs(getContext().getResources().getString(R.string.default_app_lang)));



        return builder.create();
    }

    private void updateLanguages(){
        updateButton.setEnabled(false);
        getLanguages.execute();
    }

    public void updateLanguageLLibrary(AllowedLanguages allowedLanguages){
        new AsyncTask<Object,Void,AllowedLanguages>() {
            @Override
            protected AllowedLanguages doInBackground(Object[] params) {
                SharedAppPrefs.getInstance().setLanguageLibrary(allowedLanguages);
                StaticHelpers.LogThis("библиотека обновлена");
                return allowedLanguages;
            }

            @Override
            protected void onPostExecute(AllowedLanguages allowedLanguages) {
                super.onPostExecute(allowedLanguages);
                if(allowedLanguages.getLanguages().size()!=languageAdapter.getData().size()){
                    languageAdapter.rewriteAll(allowedLanguages.getLanguages());
                }
            }
        }.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        requestsManager.clear();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button neutralButton = d.getButton(Dialog.BUTTON_NEUTRAL);
            neutralButton.setOnClickListener(v -> updateLanguages());
        }

        Observable.fromCallable(() -> SharedAppPrefs.getInstance().getLanguageLibrary().getLanguages())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(languages -> {
            languageAdapter.rewriteAll(languages);
        });



    }
}
