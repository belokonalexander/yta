package ru.belokonalexander.yta.Views.Recyclers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.List;

import ru.belokonalexander.yta.Adapters.CommonAdapter;

import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationProvider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationSlider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SolidProvider;

/**
 * Created by Alexander on 28.03.2017.
 */

public class LazyLoadingRecyclerView<T>  extends ActionRecyclerView<T>{

    private double LOAD_BORDER = 0; //px - количество пикселов до конца списка, перед началом подгрузкой

    /**
     * иногда при быстрой прокрутке, после загрузки данных, скроллер продолжает находиться в старом состоянии (как перед загрузкой)
     * из-за этого подгрузка может выполниться 2 раза подряд, добавление данного счетчика поможет избежать такой ситуации
     */
    int preloadingIterations = 0;

    /**
     *  все данные из источника были получены
     */
    Boolean allDataWasObtained = false;

    public final int MIN_PRELOAD_SCROLL = 5;



    public void init(CommonAdapter<T> adapter, PaginationProvider<T> provider) {
        super.init(adapter, provider);

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onScrollHeightController();
            }
        });

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(LOAD_BORDER==0)
            LOAD_BORDER = .5 * getHeight();
    }

    void onScrollHeightController(){
        //StaticHelpers.LogThis(" ПРБУЮ ОБНОВИТЬ ");
        //максимальная позиция скроллера
        int totalScrollSize = computeVerticalScrollRange()-getHeight();
        if(totalScrollSize<=0){
            //скроллер не инициализирован
            return;
        }
        //текущая позиция скроллера
        int currentScrollSize = computeVerticalScrollOffset();

        //StaticHelpers.LogThis("РАЗМЕР ИЗМЕНЕН: " + " ТЕКУЩАЯ: " + currentScrollSize+ " ВСЕГО: " + totalScrollSize + " ГРАНИЦА: " + LOAD_BORDER);
        if( (!canScrollVertically(1) || currentScrollSize > totalScrollSize - LOAD_BORDER) && !allDataWasObtained && !loadingInProgress && preloadingIterations > MIN_PRELOAD_SCROLL) {
            StaticHelpers.LogThis("Подгрузка пошла");
            getData(UpdateMode.ADD);
        }

        preloadingIterations++;
    }


    @Override
    public void onDataSizeChanged() {

        StaticHelpers.LogThis("РАЗМЕР ИЗМЕНЕН: " + adapter.getData());

        if(/*allDataWasObtained && */adapter.getRealItems()==0)
            enableEmptyController();
        else disableEmptyController();


        //данные обновились и список сдвинулся -> пробуем подгрузить данные и отменяем ограничение по минимальному порогу прокрутки
        preloadingIterations = MIN_PRELOAD_SCROLL + 1;
        onScrollHeightController();

    }

    @Override
    public void afterUpdating(UpdateMode updateMode, List<T> result) {
        preloadingIterations = 0;
        //проверка - все ли данные отдал поставщик
        allDataWasObtained = result.size() < ((PaginationProvider)provider).getPageSize();


        if(allDataWasObtained) {
            adapter.setDecoration(CommonAdapter.Decoration.SIMPLE);
            adapter.notifyDataSetChanged();
        } else if(adapter.getDecoration()!= CommonAdapter.Decoration.FOOTER)
            adapter.setDecoration(CommonAdapter.Decoration.FOOTER);

        super.afterUpdating(updateMode,result);
    }

    @Override
    protected List<T> dataLoading(UpdateMode updateMode) {
        if (updateMode == UpdateMode.ADD)
            ((PaginationProvider)provider).setOffset(adapter.getRealItems());
        return super.dataLoading(updateMode);
    }

    @Override
    public void removeAll() {
        super.removeAll();
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
