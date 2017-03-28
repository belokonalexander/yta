package ru.belokonalexander.yta.Events;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;

/**
 * Created by Alexander on 27.03.2017.
 */

public class ShowWordEvent {

    CompositeTranslateModel translateModel;

    public ShowWordEvent(CompositeTranslateModel translateModel) {


                this.translateModel = translateModel;


    }

    public CompositeTranslateModel getTranslateModel() {
        return translateModel;
    }




}
