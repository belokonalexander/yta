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

    @SerializedName("anm")
    @Expose
    private String anm;                 //одушевленный-неодушевленный

    public String getAnm() {
        return anm;
    }

    public void setAnm(String anm) {
        this.anm = anm;
    }

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

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public List<Tr> getTr() {
        return tr;
    }

    public void setTr(List<Tr> tr) {
        this.tr = tr;
    }

    @Override
    public String toString() {
        return "Def{" +
                "text='" + text + '\'' +
                ", pos='" + pos + '\'' +
                ", anm='" + anm + '\'' +
                ", ts='" + ts + '\'' +
                ", tr=" + tr +
                '}';
    }
}
