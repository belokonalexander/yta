package ru.belokonalexander.yta.GlobalShell.Models;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;
import ru.belokonalexander.yta.GlobalShell.Models.Rx.ChangedEntity;
import ru.belokonalexander.yta.GlobalShell.SharedAppPrefs;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;

/**
 * Created by Alexander on 18.03.2017.
 */

//класс, описывающий текущий, выбранный язык
public class CurrentLanguage implements ChangedEntity<CurrentLanguage> {

    private String langFrom;
    private String langFromDesc;

    private String langTo;
    private String langToDesc;


    public CurrentLanguage(String langFrom, String langFromDesc, String langTo, String langToDesc) {
        this.langFrom = langFrom;
        this.langFromDesc = langFromDesc;
        this.langTo = langTo;
        this.langToDesc = langToDesc;
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


    private PublishSubject<CurrentLanguage> changeObservable = PublishSubject.create();


    @Override
    public Observable<CurrentLanguage> getChanged() {
        return changeObservable;
    }

    public void swapLanguages() {

        StaticHelpers.LogThis("1) " +  this);

        String tmp = langFromDesc;
        langFromDesc = langToDesc;
        langToDesc = tmp;

        tmp = langFrom;
        langFrom = langTo;
        langTo = tmp;

        StaticHelpers.LogThis("2) " + this);

        changeObservable.onNext(this);
    }
}
