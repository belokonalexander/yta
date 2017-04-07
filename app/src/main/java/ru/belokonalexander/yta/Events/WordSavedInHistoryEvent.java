package ru.belokonalexander.yta.Events;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;

/**
 * Событие - сохранение слова в истории
 */

public class WordSavedInHistoryEvent {

    private CompositeTranslateModel translateModel;

    public WordSavedInHistoryEvent(CompositeTranslateModel translateModel) {

        this.translateModel = translateModel;
    }

    public CompositeTranslateModel getTranslateModel() {
        return translateModel;
    }


}
