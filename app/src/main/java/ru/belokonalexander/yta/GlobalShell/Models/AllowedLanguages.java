package ru.belokonalexander.yta.GlobalShell.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class AllowedLanguages implements Serializable {

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



    public enum TranslateLangType{
        FROM,
        TO,
        BOTH;
    }


    /**
     *  Получить список языков для перевода
      * @param type - тип языка, который необходимо получить
     * @return
     */
    public List<Language> getLanguages(TranslateLangType type){


        /**
         * Set используется для исключения повторений в контейнере,
         * но все же более удобно и обощенно возвращать отсторированный List
         */
        TreeSet<Language> result = new TreeSet<>();

        for(String dir : dirs) {
            if(type==TranslateLangType.FROM || type==TranslateLangType.BOTH){
                String firstStringCode = dir.substring(0, dir.indexOf("-"));
                Language first = new Language(firstStringCode, getDesc(firstStringCode));
                result.add(first);
            }

            if(type==TranslateLangType.TO || type==TranslateLangType.BOTH) {
                String secondStringCode = dir.substring(dir.indexOf("-") + 1, dir.length());
                Language second = new Language(secondStringCode, getDesc(secondStringCode));
                result.add(second);
            }
        }

      return new ArrayList<>(result);
    };



    public String getDesc(String code){
        return langs.get(code);
    }



}
