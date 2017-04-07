package ru.belokonalexander.yta.Events;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;

/**
 * Событие - изменение статуса слова
 */

public class WordFavoriteStatusChangedEvent {
    private CompositeTranslateModel translateModel;

    public WordFavoriteStatusChangedEvent(CompositeTranslateModel translateModel) {

                this.translateModel = translateModel;


    }

    public CompositeTranslateModel getTranslateModel() {
        return translateModel;
    }

}
