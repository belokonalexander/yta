package ru.belokonalexander.yta.GlobalShell;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.belokonalexander.yta.GlobalShell.Models.AllowedLanguages;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateLanguage;
import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.YtaApplication;

/**
 * Хранилище. Содержит библиотеку языков перевода и последний язык перевода
 */

public class SharedAppPrefs {
    private  final String PREFS = "settings";

    private  final String _LANGUAGE_FROM = "_language_from";
    private  final String _LANGUAGE_FROM_DESC = "_language_from_desc";

    private  final String _LANGUAGE_TO = "_language_to";
    private  final String _LANGUAGE_TO_DESC= "_language_to_desc";
    private  final String _LANGUAGE_LIBRARY = "_language_library";


    private static SharedAppPrefs sharedAppPrefs;
    private SharedPreferences sharedPreferences;

    private SharedAppPrefs(Context appContext) {
        sharedPreferences = appContext.getSharedPreferences(PREFS,Context.MODE_PRIVATE);
    }

    public static SharedAppPrefs getInstance(){
        if(sharedAppPrefs==null){
            sharedAppPrefs = new SharedAppPrefs(YtaApplication.getAppContext());
        }
        return sharedAppPrefs;
    }

    public void setLanguage(TranslateLanguage currentLanguage){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(_LANGUAGE_FROM, currentLanguage.getLangFrom());
        editor.putString(_LANGUAGE_FROM_DESC, currentLanguage.getLangFromDesc());
        editor.putString(_LANGUAGE_TO, currentLanguage.getLangTo());
        editor.putString(_LANGUAGE_TO_DESC, currentLanguage.getLangToDesc());
        editor.apply();
    }

    public TranslateLanguage getLanguage(){

       return new TranslateLanguage(sharedPreferences.getString(_LANGUAGE_FROM,"ru"),sharedPreferences.getString(_LANGUAGE_FROM_DESC,"Русский"),
                                       sharedPreferences.getString(_LANGUAGE_TO,"en"),sharedPreferences.getString(_LANGUAGE_TO_DESC,"Английский"));

    }

    public void setLanguageLibrary(AllowedLanguages allowedLanguages){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(_LANGUAGE_LIBRARY, new GsonBuilder().create().toJson(allowedLanguages));
        editor.apply();
    }

    public AllowedLanguages getLanguageLibrary(){
        String resultInJson = sharedPreferences.getString(_LANGUAGE_LIBRARY, StaticHelpers.loadStringFromRawResource(R.raw.yta_library));
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(resultInJson, AllowedLanguages.class);
    }


}
