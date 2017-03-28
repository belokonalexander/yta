package ru.belokonalexander.yta.Views.Recyclers.DataProviders;

import java.util.List;

/**
 * Created by Alexander on 28.03.2017.
 */

public interface SolidProvider<T> {

    List<T> getData(PaginationSlider state);

}
