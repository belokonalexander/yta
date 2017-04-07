package ru.belokonalexander.yta.GlobalShell.Models;

import java.io.Serializable;

/**
 * Язык
 */

public class Language implements Comparable<Language>, Serializable {
    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public Language(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public  Language(String code){
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language language = (Language) o;

        return code.equals(language.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int compareTo(Language o) {
        if(desc!=null){
            if (o == null)
                return 1;
            else return desc.compareTo(o.getDesc());
        }
        return o.desc==null ? 0 : -1;
    }
}
