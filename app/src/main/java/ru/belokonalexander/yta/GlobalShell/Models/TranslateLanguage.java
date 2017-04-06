package ru.belokonalexander.yta.GlobalShell.Models;

import java.io.Serializable;

/**
 * Created by Alexander on 18.03.2017.
 */

//класс, описывающий текущий языковые настройки перевода
public class TranslateLanguage implements  Serializable {


    private Language from;
    private Language to;



    public TranslateLanguage(String langFrom, String langFromDesc, String langTo, String langToDesc) {
        from = new Language(langFrom,langFromDesc);
        to = new Language(langTo, langToDesc);

    }



    public Language getFrom() {
        return from;
    }

    public Language getTo() {
        return to;
    }

    public void setFrom(Language from) {
        this.from = from;
    }

    public void setTo(Language to) {
        this.to = to;
    }

    public TranslateLanguage(String lang) {
        from = new Language(lang.substring(0, lang.indexOf("-")));
        to = new Language(lang.substring(lang.indexOf("-")+1, lang.length()));
    }


    public String getLangFrom() {
        return from.getCode();
    }


    public String getLangFromDesc() {
        return from.getDesc();
    }

    public String getLangTo() {
        return to.getCode();
    }

    public String getLangToDesc() {
        return to.getDesc();
    }

    @Override
    public String toString() {
        return getLangFrom()+"-"+getLangTo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TranslateLanguage that = (TranslateLanguage) o;

        if (!from.equals(that.from)) return false;
        return to.equals(that.to);

    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    public void swapLanguages() {

        Language tmp = new Language(from.getCode(),from.getDesc());
        from = new Language(to.getCode(),to.getDesc());
        to = tmp;

    }


    public boolean descIsEmpty() {
        return getLangFromDesc()==null || getLangFromDesc().length()==0 || getLangToDesc()==null || getLangTo().length()==0;
    }

    public static TranslateLanguage cloneFabric(TranslateLanguage item){
        return new TranslateLanguage(item.getLangFrom(),item.getLangFromDesc(),item.getLangTo(),item.getLangToDesc());
    }


}
