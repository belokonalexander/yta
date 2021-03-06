package ru.belokonalexander.yta.GlobalShell.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;



public class TranslateResult implements Serializable{


        @SerializedName("code")
        @Expose
        private Integer code;
        @SerializedName("lang")
        @Expose
        private String lang;
        @SerializedName("text")
        @Expose
        private List<String> text = null;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public List<String> getText() {
            return text;
        }

        public void setText(List<String> text) {
            this.text = text;
        }

    @Override
    public String toString() {
        return code + " / " + lang + " " + text;
    }


    public boolean isEmpty() {
        return !(text.size() > 0 && text.get(0).trim().length() > 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TranslateResult that = (TranslateResult) o;

        if (!code.equals(that.code)) return false;
        if (!lang.equals(that.lang)) return false;
        return text.equals(that.text);

    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + lang.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }
}
