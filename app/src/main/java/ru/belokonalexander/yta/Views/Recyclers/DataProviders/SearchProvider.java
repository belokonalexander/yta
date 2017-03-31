package ru.belokonalexander.yta.Views.Recyclers.DataProviders;

import java.util.List;

import ru.belokonalexander.yta.Database.SearchEntity;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;

/**
 * Created by Alexander on 30.03.2017.
 */

/**
 * адаптер для SolidProvider, реализующий поисковой функционал
 * @param <T>
 */
public class SearchProvider<T extends SearchEntity> extends PaginationProvider<T> {

    //SearchInputData searchInputData;
    Class<T> itemType;

    public void update(SearchInputData searchInputData){
        searchInputData.setOffset(0);
        searchInputData.setPageSize(pageSize);
        state = searchInputData;
    }

    public boolean stateIsEmpty(){
        return ((SearchInputData)state).isEmpty();
    }

    public SearchProvider( Class<T> type, PaginationProviderController<T> searchProviderController) {
        super();
        this.state = new SearchInputData(pageSize);
        this.paginationProviderController = searchProviderController;
        this.itemType = type;
    }

    public Class<T> getItemType() {
        return itemType;
    }
}
