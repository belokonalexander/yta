package ru.belokonalexander.yta.GlobalShell.Models;

import ru.belokonalexander.yta.GlobalShell.Models.Lookup.LookupResult;

/**
 * Created by Alexander on 21.03.2017.
 */

public class CompositeTranslateModel {

    TranslateResult translateResult;
    LookupResult lookupResult;
    String source;

    public CompositeTranslateModel(TranslateResult translateResult, LookupResult lookupResult, String source) {
        this.translateResult = translateResult;
        this.lookupResult = lookupResult;
        this.source = source;
    }

    public TranslateResult getTranslateResult() {
        return translateResult;
    }

    public String getSource() {
        return source;
    }

    public LookupResult getLookupResult() {
        return lookupResult;
    }

    public void setTranslateResult(TranslateResult translateResult) {
        this.translateResult = translateResult;
    }

    public void setLookupResult(LookupResult lookupResult) {
        this.lookupResult = lookupResult;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
