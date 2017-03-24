package ru.belokonalexander.yta.GlobalShell.Models;

/**
 * Created by Alexander on 24.03.2017.
 */

public class Language {
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
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        return result;
    }
}
