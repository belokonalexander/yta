package ru.belokonalexander.yta.GlobalShell.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alexander on 19.03.2017.
 */

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
}
