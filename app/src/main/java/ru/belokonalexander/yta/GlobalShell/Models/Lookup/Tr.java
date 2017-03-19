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


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getGen() {
        return gen;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

    public List<Syn> getSyn() {
        return syn;
    }

    public void setSyn(List<Syn> syn) {
        this.syn = syn;
    }

    public List<Mean> getMean() {
        return mean;
    }

    public void setMean(List<Mean> mean) {
        this.mean = mean;
    }

    public List<Ex> getEx() {
        return ex;
    }

    public void setEx(List<Ex> ex) {
        this.ex = ex;
    }

    public String getAsp() {
        return asp;
    }

    public void setAsp(String asp) {
        this.asp = asp;
    }
}
