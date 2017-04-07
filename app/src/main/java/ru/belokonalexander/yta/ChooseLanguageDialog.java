package ru.belokonalexander.yta;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.belokonalexander.yta.Adapters.LanguageAdapter;
import ru.belokonalexander.yta.GlobalShell.ApiChainRequestWrapper;
import ru.belokonalexander.yta.GlobalShell.Models.AllowedLanguages;
import ru.belokonalexander.yta.GlobalShell.Models.ApplicationException;
import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.GlobalShell.ServiceGenerator;
import ru.belokonalexander.yta.GlobalShell.SharedAppPrefs;
import ru.belokonalexander.yta.GlobalShell.SimpleRequestsManager;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.Recyclers.ActionRecyclerView;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SolidProvider;


/**
 * диалог, для смены одного из языков перевода
 */

public class ChooseLanguageDialog extends DialogFragment {

    public static final int INPUT_LANGUAGE_CHANGE_REQUEST_CODE = 100;
    public static final int OUTPUT_LANGUAGE_CHANGE_REQUEST_CODE = 101;
    public static final String LANG_LEY = "_LANG";

    SimpleRequestsManager requestsManager = new SimpleRequestsManager();


    LanguageAdapter languageAdapter;
    Button updateButton;
    ApiChainRequestWrapper getLanguages;

    Button cancel;
    Toast toast;

    public final String IS_RECYCLER_DATA = "ListData";


    ActionRecyclerView<Language> recyclerView;



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        requestsManager.addRequest(getLanguages);
        toast = Toast.makeText(getContext(),null,Toast.LENGTH_SHORT);
        languageAdapter = new LanguageAdapter(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.change_language_dialog, null);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        cancel = (Button) view.findViewById(R.id.cancel);
        updateButton = (Button) view.findViewById(R.id.update);

        cancel.setOnClickListener(v -> dismiss());

        updateButton.setOnClickListener(v -> updateLanguages());

        recyclerView = (ActionRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.init(languageAdapter, () -> {
            AllowedLanguages.TranslateLangType type = getTargetRequestCode()==INPUT_LANGUAGE_CHANGE_REQUEST_CODE ?
                    AllowedLanguages.TranslateLangType.TO : AllowedLanguages.TranslateLangType.FROM;
            return SharedAppPrefs.getInstance().getLanguageLibrary().getLanguages(type);
        });


        // обработка клика на элементе списка
        languageAdapter.setOnDelayedMainClick(item -> {
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
                updateLanguageLibrary((AllowedLanguages) result.get(0));
            } else {
                toast.setText(getString(R.string.error)+": "+((ApplicationException)result.get(0)).getDetailMessage());
                toast.show();
            }
            updateButton.setEnabled(true);
        }, ServiceGenerator.getTranslateApiWithoutCache().getLangs(getContext().getResources().getString(R.string.default_app_lang)));


        if(savedInstanceState==null)
            recyclerView.initData();
        else
            recyclerView.setInitialData((List<Language>) savedInstanceState.getSerializable(IS_RECYCLER_DATA));


        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(IS_RECYCLER_DATA,recyclerView.getCurrentData());
    }

    private void updateLanguages(){
        updateButton.setEnabled(false);
        getLanguages.execute();
    }

    /**
     * обновление списка допустимых переводов
     * @param allowedLanguages
     */
    public void updateLanguageLibrary(AllowedLanguages allowedLanguages){
        new AsyncTask<Object,Void,AllowedLanguages>() {
            @Override
            protected AllowedLanguages doInBackground(Object[] params) {
                SharedAppPrefs.getInstance().setLanguageLibrary(allowedLanguages);

                return allowedLanguages;
            }

            @Override
            protected void onPostExecute(AllowedLanguages allowedLanguages) {
                super.onPostExecute(allowedLanguages);

                AllowedLanguages.TranslateLangType type = getTargetRequestCode()==INPUT_LANGUAGE_CHANGE_REQUEST_CODE ?
                        AllowedLanguages.TranslateLangType.TO : AllowedLanguages.TranslateLangType.FROM;

                if(allowedLanguages.getLanguages(type).size()!=languageAdapter.getData().size()){
                    recyclerView.rewriteAll(allowedLanguages.getLanguages(type));
                    toast.setText(getResources().getString(R.string.library_was_updated));
                    toast.show();
                } else
                    toast.setText(getResources().getString(R.string.library_has_no_new_elements));
                    toast.show();
            }
        }.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        requestsManager.clear();
    }


    public void show(FragmentManager manager){
        super.show(manager,"changeLanguage");
    }
}
