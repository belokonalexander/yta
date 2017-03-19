package ru.belokonalexander.yta.GlobalShell.Models.Lookup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alexander on 19.03.2017.
 */

public class Def {

    @SerializedName("text")
    @Expose
    private String text;            //текст примера

    @SerializedName("pos")
    @Expose
    private String pos;             //часть речи

    @SerializedName("ts")
    @Expose
    private String ts;              //транскрипция

    @SerializedName("tr")
    @Expose
    private List<Tr> tr = null;     //массив переводов

}
