package ru.belokonalexander.yta.GlobalShell.Models.Lookup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Alexander on 19.03.2017.
 */

public class Mean implements Serializable {

    @SerializedName("text")
    @Expose
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Mean{" +
                "text='" + text + '\'' +
                '}';
    }
}
