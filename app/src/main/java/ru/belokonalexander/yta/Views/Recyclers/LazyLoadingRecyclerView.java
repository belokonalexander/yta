package ru.belokonalexander.yta.Views.Recyclers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import ru.belokonalexander.yta.Adapters.CommonAdapter;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SolidProvider;

/**
 * Created by Alexander on 28.03.2017.
 */

public class LazyLoadingRecyclerView<T>  extends ActionRecyclerView<T>{


    @Override
    public void init(CommonAdapter<T> adapter, SolidProvider<T> provider) {
        super.init(adapter, provider);

        this.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onScrollHeightController(dy);
            }
        });
    }



    void onScrollHeightController(int dy){

        //максимальная позиция скроллера
        int totalScrollSize = computeVerticalScrollRange()-getHeight();

        //текущая позиция скроллера
        int currentScrollSize = computeVerticalScrollOffset();

        float scrollPercent = ((float)currentScrollSize/totalScrollSize); // [0;1]

        if((!canScrollVertically(1) || scrollPercent>.7) && !allDataWasObtained && !loadingInProgress) { //todo border
            getData(UpdateMode.ADD);
        }
    }


    public LazyLoadingRecyclerView(Context context) {
        super(context);
    }

    public LazyLoadingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LazyLoadingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }





}
