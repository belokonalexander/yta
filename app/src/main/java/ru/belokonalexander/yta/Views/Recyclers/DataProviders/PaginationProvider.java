package ru.belokonalexander.yta.Views.Recyclers.DataProviders;

import java.util.List;

/**
 * Created by Alexander on 30.03.2017.
 */

public class PaginationProvider<T> implements SolidProvider<T> {

    protected PaginationSlider state;
    protected PaginationProviderController<T> paginationProviderController;

    protected int pageSize = 20;

    public PaginationProvider(PaginationProviderController<T> paginationProviderController) {
        this.paginationProviderController = paginationProviderController;
        this.state = new PaginationSlider(pageSize);
    }

    public PaginationProvider(PaginationProviderController<T> paginationProviderController, PaginationSlider state){
        this.paginationProviderController = paginationProviderController;
        this.state = state;
    }

    public PaginationProvider() {
    }

    @Override
    public List<T> getData() {
        return paginationProviderController.getDate(state);
    }

    public void setOffset(int offset) {
        state.setOffset(offset);
    }

    public int getPageSize(){
        return pageSize;
    }

    public interface PaginationProviderController<T>{
        List<T> getDate(PaginationSlider state);
    }


}
