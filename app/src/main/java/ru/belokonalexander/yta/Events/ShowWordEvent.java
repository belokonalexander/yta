package ru.belokonalexander.yta.Events;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;

/**
 * Событие - просмотреть слово
 */

public class ShowWordEvent {

    private CompositeTranslateModel translateModel;

    public ShowWordEvent(CompositeTranslateModel translateModel) {


                this.translateModel = translateModel;


    }

    public CompositeTranslateModel getTranslateModel() {
        return translateModel;
    }




}
