package ru.belokonalexander.yta.GlobalShell.Models.Lookup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alexander on 19.03.2017.
 */

public class Tr {

    @SerializedName("text")
    @Expose
    private String text;                //значение

    @SerializedName("pos")
    @Expose
    private String pos;                 //часть речи

    @SerializedName("gen")
    @Expose
    private String gen;                 //род

    @SerializedName("syn")
    @Expose
    private List<Syn> syn = null;       //синонимы

    @SerializedName("mean")
    @Expose
    private List<Mean> mean = null;     //массив значений

    @SerializedName("ex")
    @Expose
    private List<Ex> ex = null;         //примеры

    @SerializedName("asp")
    @Expose
    private String asp;                 //вид


}
