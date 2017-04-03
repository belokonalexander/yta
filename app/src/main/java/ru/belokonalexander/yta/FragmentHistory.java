package ru.belokonalexander.yta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import ru.belokonalexander.yta.Adapters.CommonAdapter;
import ru.belokonalexander.yta.Adapters.CompositeTranslateAdapter;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.Database.CompositeTranslateModelDao;

import ru.belokonalexander.yta.Events.FavoriteClearEvent;
import ru.belokonalexander.yta.Events.ShowWordEvent;
import ru.belokonalexander.yta.Events.WordFavoriteStatusChangedEvent;
import ru.belokonalexander.yta.Events.WordSavedInHistoryEvent;
import ru.belokonalexander.yta.GlobalShell.SimpleAsyncTask;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.Recyclers.ActionRecyclerView;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationProvider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationSlider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SearchInputData;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SearchProvider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SolidProvider;
import ru.belokonalexander.yta.Views.Recyclers.LazyLoadingRecyclerView;
import ru.belokonalexander.yta.Views.Recyclers.SearchRecyclerView;

/**
 * Created by Alexander on 16.03.2017.
 */

public class FragmentHistory extends Fragment{

    @BindView(R.id.recycler_view)
    SearchRecyclerView<CompositeTranslateModel> recyclerView;
    CompositeTranslateAdapter adapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);
        ButterKnife.bind(this, view);

        toolbar.setTitle(getResources().getString(R.string.history_title));

        MenuItem clearHistory = toolbar.getMenu().add(getString(R.string.history_clear));
        clearHistory.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        clearHistory.setIcon(R.drawable.ic_delete_white_36dp);
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
        adapter.setOnClickListener(item -> {
            EventBus.getDefault().post(new ShowWordEvent(item));
            ((MainActivity)getActivity()).openActionFragment();
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());


        recyclerView.setLayoutManager(mLayoutManager);
        //PhantomLastItemAdapter a = new PhantomLastItemAdapter<>(adapter);
        recyclerView.init(adapter, new SearchProvider<>(CompositeTranslateModel.class, new PaginationProvider.PaginationProviderController<CompositeTranslateModel>() {
            @Override
            public List<CompositeTranslateModel> getDate(PaginationSlider state) {


                SearchInputData searchInputData = (SearchInputData) state;
                StaticHelpers.LogThis("Подгрузка: " + state + " / " + searchInputData.getSearchCondition());

                return YtaApplication.getDaoSession().getCompositeTranslateModelDao()
                        .queryBuilder().where(CompositeTranslateModelDao.Properties.History.eq(true), new WhereCondition.StringCondition(searchInputData.getSearchCondition())).limit(state.getPageSize()).offset(state.getOffset())
                        .orderDesc(CompositeTranslateModelDao.Properties.CreateDate).list();
            }
        }));



        return view;
    }

    private void clearHistory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.history_clear);
        builder.setMessage(R.string.history_clear_are_you_sure);
        builder.setNegativeButton(R.string.no, (dialog, which) -> {

        });
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            //userSingleRelation.execute((u, resultWaiter) -> CurrentApi.getInstanse().getApplicationApi().cancelFriendship(u, resultWaiter), v);

            recyclerView.removeAll();
            SimpleAsyncTask.run(new SimpleAsyncTask.InBackground<Object>() {
                @Override
                public Object doInBackground() {
                    YtaApplication.getDaoSession().getCompositeTranslateModelDao().queryBuilder()
                            .where(CompositeTranslateModelDao.Properties.Favorite.eq(false))
                            .buildDelete().executeDeleteWithoutDetachingEntities();

                    for(CompositeTranslateModel item : YtaApplication.getDaoSession().getCompositeTranslateModelDao().loadAll()){
                        item.setHistory(false);
                        YtaApplication.getDaoSession().update(item);
                    }

                    return null;
                }
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
        StaticHelpers.LogThis(" Новое слово: " + translateModel);
        int itemIndex = adapter.getData().indexOf(translateModel);

        StaticHelpers.LogThis("Индекс: " + itemIndex);

        if(itemIndex>=0)
            StaticHelpers.LogThis("Индекс: " + itemIndex + " / " + adapter.getData().get(itemIndex));

        if(itemIndex<0){
            recyclerView.addToTop(translateModel);
        } else {
            recyclerView.moveToTop(itemIndex);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFavoriteStatus(WordFavoriteStatusChangedEvent event){
        CompositeTranslateModel translateModel = event.getTranslateModel();
        int itemIndex = adapter.getData().indexOf(translateModel);
        if(itemIndex<0){
            recyclerView.addToTop(translateModel);
        } else {
            recyclerView.update(translateModel,itemIndex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clearFavorite(FavoriteClearEvent event){
        StaticHelpers.LogThis(" ОЧИЩАЕМ ");
        List<CompositeTranslateModel> data = adapter.getData();
        for(CompositeTranslateModel model : data)
            model.setFavorite(false);

        recyclerView.update();
    }

}
