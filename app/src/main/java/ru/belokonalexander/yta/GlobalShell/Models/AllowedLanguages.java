package ru.belokonalexander.yta.GlobalShell.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

    /**
     * Set используется для исключения повторений в контейнере,
     * но все же более удобно и обощенно возвращать отсторированный List
     * @return список с доступными для перевода языками
     */
    public List<Language> getLanguages(){

        /**
         *  не знаю, для всех ли языков в langs есть переводы,
         *  поэтому достаю фактические значения из dirs и их описание
         */
        TreeSet<Language> result = new TreeSet<>();

        for(String dir : dirs) {
            String firstStringCode = dir.substring(0, dir.indexOf("-"));
            String secondStringCode = dir.substring(dir.indexOf("-")+1, dir.length());
            Language first = new Language(firstStringCode, getDesc(firstStringCode));
            Language second = new Language(secondStringCode, getDesc(secondStringCode));
            result.add(first);
            result.add(second);
        }

      return new ArrayList<>(result);
    };


    public String getDesc(String code){
        return langs.get(code);
    }

}
