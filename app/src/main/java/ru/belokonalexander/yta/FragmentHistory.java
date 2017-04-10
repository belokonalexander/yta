package ru.belokonalexander.yta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.belokonalexander.yta.Adapters.CompositeTranslateAdapter;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.Database.CompositeTranslateModelDao;

import ru.belokonalexander.yta.Events.FavoriteClearEvent;
import ru.belokonalexander.yta.Events.ShowWordEvent;
import ru.belokonalexander.yta.Events.WordFavoriteStatusChangedEvent;
import ru.belokonalexander.yta.Events.WordSavedInHistoryEvent;
import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.GlobalShell.SimpleAsyncTask;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.Recyclers.ActionRecyclerView;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationProvider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationSlider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SearchInputData;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SearchProvider;
import ru.belokonalexander.yta.Views.Recyclers.SearchRecyclerView;

/**
 * Фрагмент для окна "История поиска", по логике аналогичен фрагменту FragmentFavorites
 */

public class FragmentHistory extends Fragment{

    @BindView(R.id.recycler_view)
    SearchRecyclerView<CompositeTranslateModel> recyclerView;
    CompositeTranslateAdapter adapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public final String IS_RECYCLER_DATA = "ListData";



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);
        ButterKnife.bind(this, view);



        toolbar.setTitle(getResources().getString(R.string.history_title));

        MenuItem clearHistory = toolbar.getMenu().add(getString(R.string.history_clear));
        clearHistory.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        clearHistory.setIcon(R.drawable.ic_delete_white_24dp);
        clearHistory.setOnMenuItemClickListener(item -> {
            clearHistory();
            return false;
        });

        recyclerView.setOnDataContentChangeListener(new ActionRecyclerView.OnDataContentChangeListener() {
            @Override
            public void onEmpty() {
                clearHistory.setVisible(false);
            }

            @Override
            public void onFilled() {
                clearHistory.setVisible(true);
            }
        });

        adapter =  new CompositeTranslateAdapter(getContext());
        adapter.setOnDelayedMainClick(item -> {
                    ((MainActivity) getActivity()).openActionFragment(1);
                    EventBus.getDefault().post(new ShowWordEvent(item));
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());


        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.init(adapter, new SearchProvider<>(CompositeTranslateModel.class, state -> {

            SearchInputData searchInputData = (SearchInputData) state;

            return YtaApplication.getDaoSession().getCompositeTranslateModelDao()
                    .queryBuilder().where(CompositeTranslateModelDao.Properties.History.eq(true), new WhereCondition.StringCondition(searchInputData.getSearchCondition())).orderDesc(CompositeTranslateModelDao.Properties.SaveHistoryDate).limit(state.getPageSize()).offset(state.getOffset())
                    .list();
        }));

        if(savedInstanceState==null)
            recyclerView.initData();
        else
            recyclerView.setInitialData((List<CompositeTranslateModel>) savedInstanceState.getSerializable(IS_RECYCLER_DATA));

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(IS_RECYCLER_DATA,recyclerView.getCurrentData());
    }

    private void clearHistory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.history_clear);
        builder.setMessage(R.string.history_clear_are_you_sure);
        builder.setNegativeButton(R.string.no, (dialog, which) -> {

        });
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {

            recyclerView.removeAll();
            SimpleAsyncTask.run(() -> {
                YtaApplication.getDaoSession().getCompositeTranslateModelDao().queryBuilder()
                        .where(CompositeTranslateModelDao.Properties.Favorite.eq(false))
                        .buildDelete().executeDeleteWithoutDetachingEntities();

                for(CompositeTranslateModel item : YtaApplication.getDaoSession().getCompositeTranslateModelDao().loadAll()){
                    item.setHistory(false);
                    item.setSaveHistoryDate(null);
                    YtaApplication.getDaoSession().update(item);
                }

                return null;
            });


            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addNewWordInHistory(WordSavedInHistoryEvent event){

        CompositeTranslateModel translateModel = event.getTranslateModel();
        int itemIndex = adapter.getData().indexOf(translateModel);

        if(itemIndex<0){
            recyclerView.addToTop(translateModel);
        } else {
            recyclerView.moveToTop(itemIndex);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFavoriteStatus(WordFavoriteStatusChangedEvent event){
        CompositeTranslateModel translateModel = event.getTranslateModel();
        if(translateModel.getHistory()) {
            int itemIndex = adapter.getData().indexOf(translateModel);
            if (itemIndex < 0) {
                recyclerView.addToTop(translateModel);
            } else {
                recyclerView.update(translateModel, itemIndex);
            }
        }
    }

    /**
     * когда очищается список с избранным - сбрасываются все зибранные элементы
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clearFavorite(FavoriteClearEvent event){
        List<CompositeTranslateModel> data = adapter.getData();
        for(CompositeTranslateModel model : data)
            model.setFavorite(false);

        recyclerView.update();
    }

}
