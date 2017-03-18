package ru.belokonalexander.yta.GlobalShell.Models;

import ru.belokonalexander.yta.GlobalShell.SharedAppPrefs;

/**
 * Created by Alexander on 18.03.2017.
 */

//класс, описывающий текущий, выбранный язык
public class CurrentLanguage {

    private String langFrom;
    private String langFromDesc;

    private String langTo;
    private String langToDesc;


    public CurrentLanguage(String langFrom, String langFromDesc, String langTo, String langToDesc) {
        this.langFrom = langFrom;
        this.langFromDesc = langFromDesc;
        this.langTo = langTo;
        this.langToDesc = langToDesc;
    }


    public String getLangFrom() {
        return langFrom;
    }

    public void setLangFrom(String langFrom) {
        this.langFrom = langFrom;
    }

    public String getLangFromDesc() {
        return langFromDesc;
    }

    public void setLangFromDesc(String langFromDesc) {
        this.langFromDesc = langFromDesc;
    }

    public String getLangTo() {
        return langTo;
    }

    public void setLangTo(String langTo) {
        this.langTo = langTo;
    }

    public String getLangToDesc() {
        return langToDesc;
    }

    public void setLangToDesc(String langToDesc) {
        this.langToDesc = langToDesc;
    }

    @Override
    public String toString() {
        return "CurrentLanguage{" +
                "langFrom='" + langFrom + '\'' +
                ", langFromDesc='" + langFromDesc + '\'' +
                ", langTo='" + langTo + '\'' +
                ", langToDesc='" + langToDesc + '\'' +
                '}';
    }
}
