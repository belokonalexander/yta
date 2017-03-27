package ru.belokonalexander.yta.Events;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;

/**
 * Created by Alexander on 27.03.2017.
 */

public class SmoneWantToShowWordEvent {

    CompositeTranslateModel translateModel;

    public SmoneWantToShowWordEvent(CompositeTranslateModel translateModel) {
        this.translateModel = translateModel;
    }

    public CompositeTranslateModel getTranslateModel() {
        return translateModel;
    }

    public void setTranslateModel(CompositeTranslateModel translateModel) {
        this.translateModel = translateModel;
    }
}
