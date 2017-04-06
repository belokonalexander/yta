package ru.belokonalexander.yta.GlobalShell.Models.Lookup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Alexander on 19.03.2017.
 */

public class Syn implements Serializable {

    @SerializedName("text")     //значение
    @Expose
    private String text;
    @SerializedName("pos")      //часть речи
    @Expose
    private String pos;
    @SerializedName("gen")      //род
    @Expose
    private String gen;


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

    @Override
    public String toString() {
        return "Syn{" +
                "text='" + text + '\'' +
                ", pos='" + pos + '\'' +
                ", gen='" + gen + '\'' +
                '}';
    }
}
