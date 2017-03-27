package ru.belokonalexander.yta.Events;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;

/**
 * Created by Alexander on 27.03.2017.
 */

public class WordFavoriteStatusChangedEvent {
    CompositeTranslateModel translateModel;

    public WordFavoriteStatusChangedEvent(CompositeTranslateModel translateModel, EventCreateType eventCreateType) {
        switch (eventCreateType){
            case COPY:
                this.translateModel = CompositeTranslateModel.copy(translateModel);
                break;
            case LINK:
                this.translateModel = translateModel;
                break;
        }

    }

    public CompositeTranslateModel getTranslateModel() {
        return translateModel;
    }

    public void setTranslateModel(CompositeTranslateModel translateModel) {
        this.translateModel = translateModel;
    }
}
