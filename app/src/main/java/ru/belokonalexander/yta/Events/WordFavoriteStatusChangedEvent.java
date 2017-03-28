package ru.belokonalexander.yta.Events;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;

/**
 * Created by Alexander on 27.03.2017.
 */

public class WordFavoriteStatusChangedEvent {
    CompositeTranslateModel translateModel;

    public WordFavoriteStatusChangedEvent(CompositeTranslateModel translateModel) {

                this.translateModel = translateModel;


    }

    public CompositeTranslateModel getTranslateModel() {
        return translateModel;
    }

}
