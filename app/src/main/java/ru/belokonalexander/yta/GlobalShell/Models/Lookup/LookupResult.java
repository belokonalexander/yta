package ru.belokonalexander.yta.GlobalShell.Models.Lookup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alexander on 19.03.2017.
 */

public class LookupResult {

/*
def	Массив словарных статей. В атрибуте ts может указываться транскрипция искомого слова.
tr	Массив переводов.
syn	Массив синонимов.
mean	Массив значений.
ex	Массив примеров.
 */

    @SerializedName("def")
    @Expose
    private List<Def> def = null;


}
