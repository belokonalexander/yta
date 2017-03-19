package ru.belokonalexander.yta.GlobalShell.Models.Lookup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Alexander on 19.03.2017.
 */

public class Syn {

    @SerializedName("text")     //значение
    @Expose
    private String text;
    @SerializedName("pos")      //часть речи
    @Expose
    private String pos;
    @SerializedName("gen")      //род
    @Expose
    private String gen;


}
