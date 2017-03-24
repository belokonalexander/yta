package ru.belokonalexander.yta;

import android.app.Dialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

import ru.belokonalexander.yta.Adapters.LanguageAdapter;
import ru.belokonalexander.yta.GlobalShell.ApiChainRequestWrapper;
import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateLanguage;
import ru.belokonalexander.yta.GlobalShell.OnApiSuccessResponseListener;
import ru.belokonalexander.yta.GlobalShell.ServiceGenerator;
import ru.belokonalexander.yta.GlobalShell.SimpleRequestsManager;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;


/**
 * Created by Alexander on 24.03.2017.
 */

public class ChooseLanguageDialog extends DialogFragment {

    public static final int INPUT_LANGUAGE_CHANGE_REQUEST_CODE = 100;
    public static final int OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE = 101;

    SimpleRequestsManager requestsManager = new SimpleRequestsManager();

    TranslateLanguage currentLanguage;
    LanguageAdapter languageAdapter = new LanguageAdapter();
    Button updateButton;
    ApiChainRequestWrapper getLanguages;

    public void show(FragmentManager manager, TranslateLanguage currentLanguage){
        this.currentLanguage = currentLanguage;
        StaticHelpers.LogThis(" Show dialog");
        super.show(manager,"changeLanguage");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        requestsManager.addRequest(getLanguages);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.change_language_dialog, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(languageAdapter);
        // присваиваем адаптер списку


        String title = "";

        switch (getTargetRequestCode()) {
            case INPUT_LANGUAGE_CHANGE_REQUEST_CODE:
                title = getResources().getString(R.string.input_language_cahnge);
                break;

            case OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE:
                title = getResources().getString(R.string.output_language_cahnge);
                break;
        }


        builder.setView(view)
                .setMessage(title)
                .setNeutralButton(getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //переопределен в onStart()
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                        //по умолчанию
                })
        ;

        String hash = StaticHelpers.getParentHash(this.getClass());
        getLanguages = ApiChainRequestWrapper.getApartInstance(hash, result -> StaticHelpers.LogThis(" ANSWER: " + result), ServiceGenerator.getTranslateApi().getLangs(getContext().getResources().getString(R.string.default_app_lang)));



        return builder.create();
    }

    private void updateLanguages(){
        getLanguages.execute();
        StaticHelpers.LogThis("input: " + currentLanguage);
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

        getLanguages.execute();
    }
}
