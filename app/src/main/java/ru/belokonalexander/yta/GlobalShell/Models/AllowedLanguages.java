package ru.belokonalexander.yta.GlobalShell.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 17.03.2017.
 */

public class AllowedLanguages {

    @SerializedName("dirs")
    @Expose
    private List<String> dirs = null;

    @SerializedName("langs")
    @Expose
    private Map<String,String> langs;

    public List<String> getDirs() {
        return dirs;
    }

    public void setDirs(List<String> dirs) {
        this.dirs = dirs;
    }

    public Map<String,String> getLangs() {
        return langs;
    }

    public void setLangs(Map<String,String> langs) {
        this.langs = langs;
    }


    @Override
    public String toString() {
        return "AllowedLanguages{" +
                "dirs=" + dirs +
                ", langs=" + langs +
                '}';
    }

}
