package ru.belokonalexander.yta.GlobalShell.Models;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import ru.belokonalexander.yta.GlobalShell.Models.Rx.ChangedEntity;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;

/**
 * Created by Alexander on 18.03.2017.
 */

//класс, описывающий текущий языковые настройки перевода
public class Language implements ChangedEntity<Language> {

    private String langFrom;
    private String langFromDesc;

    private String langTo;
    private String langToDesc;


    public Language(String langFrom, String langFromDesc, String langTo, String langToDesc) {
        this.langFrom = langFrom;
        this.langFromDesc = langFromDesc;
        this.langTo = langTo;
        this.langToDesc = langToDesc;
        changeObservable.onNext(this);
    }

    public Language(String lang) {
        this.langFrom = lang.substring(0, lang.indexOf("-"));
        this.langTo = lang.substring(lang.indexOf("-")+1, lang.length());;

        StaticHelpers.LogThis("NOW: " + this);

        changeObservable.onNext(this);
    }


    public String getLangFrom() {
        return langFrom;
    }

    public void setLangFrom(String langFrom) {
        this.langFrom = langFrom;
        changeObservable.onNext(this);
    }

    public String getLangFromDesc() {
        return langFromDesc;
    }

    public void setLangFromDesc(String langFromDesc) {
        this.langFromDesc = langFromDesc;
        changeObservable.onNext(this);
    }

    public String getLangTo() {
        return langTo;
    }

    public void setLangTo(String langTo) {
        this.langTo = langTo;
        changeObservable.onNext(this);
    }

    public String getLangToDesc() {
        return langToDesc;
    }

    public void setLangToDesc(String langToDesc) {
        this.langToDesc = langToDesc;
        changeObservable.onNext(this);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language that = (Language) o;

        return langFrom.equals(that.langFrom) && langTo.equals(that.langTo);

    }

    @Override
    public int hashCode() {
        int result = langFrom.hashCode();
        result = 31 * result + langTo.hashCode();
        return result;
    }

    private PublishSubject<Language> changeObservable = PublishSubject.create();


    @Override
    public Observable<Language> getChanged() {
        return changeObservable;
    }

    public void swapLanguages() {

        String tmp;
        if(langFromDesc!=null && langToDesc!=null) {
            tmp = langFromDesc;
            langFromDesc = langToDesc;
            langToDesc = tmp;
        }

        tmp = langFrom;
        langFrom = langTo;
        langTo = tmp;


        changeObservable.onNext(this);
    }
}
