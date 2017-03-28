package ru.belokonalexander.yta.Events;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;

/**
 * Created by Alexander on 27.03.2017.
 */

public class WordSavedInHistoryEvent {

    CompositeTranslateModel translateModel;

    public WordSavedInHistoryEvent(CompositeTranslateModel translateModel) {

        this.translateModel = translateModel;
    }

    public CompositeTranslateModel getTranslateModel() {
        return translateModel;
    }


}
