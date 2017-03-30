package ru.belokonalexander.yta.Views.Recyclers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.List;

import ru.belokonalexander.yta.Adapters.CommonAdapter;
import ru.belokonalexander.yta.Database.SearchEntity;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationProvider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SearchProvider;

/**
 * Created by Alexander on 31.03.2017.
 */

public class SearchRecyclerView<T extends SearchEntity> extends LazyLoadingRecyclerView<T> {


    public void init(CommonAdapter<T> adapter, PaginationProvider<T> provider) {
        super.init(adapter, provider);
        //доп инициализация
    }

    public SearchRecyclerView(Context context) {
        super(context);
    }

    public SearchRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    //protected List<T> dataLoading(UpdateMode updateMode) {
    //    if (updateMode == UpdateMode.ADD)
   //         ((SearchProvider)provider).setOffset(adapter.getRealItems());
    //    return super.dataLoading(updateMode);
    //}

}
