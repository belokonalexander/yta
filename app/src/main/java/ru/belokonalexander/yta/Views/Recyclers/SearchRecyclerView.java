package ru.belokonalexander.yta.Views.Recyclers;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ru.belokonalexander.yta.Adapters.CommonAdapter;
import ru.belokonalexander.yta.Database.SearchEntity;
import ru.belokonalexander.yta.Database.SearchItem;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.Views.EntitySearchView;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationProvider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SearchInputData;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SearchProvider;

/**
 * Created by Alexander on 31.03.2017.
 */

public class SearchRecyclerView<T extends SearchEntity> extends LazyLoadingRecyclerView<T> {


    EntitySearchView searchView;

    ViewGroup searchFieldController;



    public void init(CommonAdapter<T> adapter, SearchProvider<T> provider) {


        //внедряем EntitySearchView над списком, если правильно установлены параметры поиска
        if(SearchItem.getSearchFieldsCount(provider.getItemType())>0) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            searchFieldController = (ViewGroup) layoutInflater.inflate(R.layout.item_search, null);
            ViewGroup parent = (ViewGroup) getParent();


            parent.addView(searchFieldController);
            RelativeLayout.LayoutParams newParams = (RelativeLayout.LayoutParams) getLayoutParams();
            newParams.addRule(RelativeLayout.BELOW, searchFieldController.getId());

            searchView = (EntitySearchView) searchFieldController.findViewById(R.id.search_view);


            searchView.initSearch(provider.getItemType());
            searchView.setSearchTask(new EntitySearchView.SearchTask() {
                @Override
                public void startSearch(SearchInputData data) {
                    provider.update(data);
                    getData(UpdateMode.REWRITE);
                }

                @Override
                public void startEmpty(SearchInputData data) {
                    provider.update(data);
                    getData(UpdateMode.REWRITE);
                }
            });

        }


        super.init(adapter, provider);
    }

    @Override
    public void init(CommonAdapter<T> adapter, PaginationProvider<T> provider) {
        super.init(adapter, provider);
        throw new UnsupportedOperationException("Initialization only with SearchProvider");
    }


    @Override
    public void enableEmptyController() {
        super.enableEmptyController();
        if(((SearchProvider)provider).stateIsEmpty())
            hideSearchController();
    }



    @Override
    public void disableEmptyController() {
        super.disableEmptyController();
        showSearchController();
    }


    private void showSearchController() {
        if(searchFieldController!=null) {
            searchFieldController.setVisibility(VISIBLE);
        }
    }

    private void hideSearchController() {
        if(searchFieldController!=null) {
            searchFieldController.setVisibility(GONE);
        }
    }

    @Override
    public void afterUpdating(UpdateMode updateMode, List<T> result) {
        super.afterUpdating(updateMode, result);
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


    @Override
    public void addToTop(T object) {
        if(filter(object)!=null)
            super.addToTop(object);
    }


    @Override
    public void add(List<T> list) {
        if(list!=null && filter(list).size()>0)
            super.add(list);
    }

    @Override
    public void rewriteAll(List<T> data) {
        if(data!=null && filter(data).size()>0)
            super.rewriteAll(data);
    }

    @Override
    public void removeAll() {
        getCastProvider().setStateEmpty();
        searchView.clearTextWithoutUpdate();
        super.removeAll();

    }

    /**
     * проверяет, проходит ли значение по фильтру
     * @param item
     * @return
     */
    private T filter(T item){
        if(!getCastProvider().stateIsEmpty()){
            try {
                Field field = item.getClass().getDeclaredField(getCastProvider().getFilterKey());
                field.setAccessible(true);
                Object value = field.get(item);

                if(!getCastProvider().isFilterValue(value))
                    return null;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return item;
    }

    private List<T> filter(List<T> data){

        if(!getCastProvider().stateIsEmpty()){
            List<T> filtered = new ArrayList<T>();
            for(T item : data){
                if(filter(item)!=null){
                    filtered.add(item);
                }
            }
            return filtered;
        }

        return data;
    }


    @SuppressWarnings("unchecked")
    private SearchProvider<T> getCastProvider(){
        return ((SearchProvider)provider);
    }

}
