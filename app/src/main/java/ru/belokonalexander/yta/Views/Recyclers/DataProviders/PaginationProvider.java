package ru.belokonalexander.yta.Views.Recyclers.DataProviders;

import java.util.List;

/**
 * Created by Alexander on 30.03.2017.
 */

public class PaginationProvider<T> implements SolidProvider<T> {

    private PaginationSlider state;
    private PaginationProviderController<T> paginationProviderController;

    public PaginationProvider(PaginationProviderController<T> paginationProviderController, int pageSize) {
        this.paginationProviderController = paginationProviderController;
        this.state = new PaginationSlider(pageSize);
    }

    @Override
    public List<T> getData() {
        return paginationProviderController.getDate(state);
    }

    public void setOffset(int offset) {
        state.setOffset(offset);
    }

    public interface PaginationProviderController<T>{
        List<T> getDate(PaginationSlider state);
    }


}
