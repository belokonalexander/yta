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
public class SearchProvider<T extends SearchEntity> extends PaginationProvider<T> implements SolidProvider<T> {

    //SearchInputData searchInputData;
    Class<T> itemType;

    public SearchProvider( Class<T> type, PaginationProviderController<T> searchProviderController) {
        super();
        this.state = new SearchInputData(pageSize);
        this.paginationProviderController = searchProviderController;
    }

}
