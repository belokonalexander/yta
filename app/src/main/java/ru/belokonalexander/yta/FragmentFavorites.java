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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.belokonalexander.yta.Adapters.CompositeTranslateAdapter;
import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.Database.CompositeTranslateModelDao;
import ru.belokonalexander.yta.Events.FavoriteClearEvent;
import ru.belokonalexander.yta.Events.ShowWordEvent;
import ru.belokonalexander.yta.Events.WordFavoriteStatusChangedEvent;
import ru.belokonalexander.yta.GlobalShell.SimpleAsyncTask;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.Recyclers.ActionRecyclerView;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationProvider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationSlider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SearchInputData;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SearchProvider;

import ru.belokonalexander.yta.Views.Recyclers.SearchRecyclerView;

/**
 * Фрагмент для "избранного"
 */

public class FragmentFavorites extends Fragment {


    @BindView(R.id.recycler_view)
    SearchRecyclerView<CompositeTranslateModel> recyclerView;

    CompositeTranslateAdapter adapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ArrayList<CompositeTranslateModel> listData = new ArrayList<>();

    public final String IS_RECYCLER_DATA = "ListData";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites,container,false);
        ButterKnife.bind(this, view);


        toolbar.setTitle(getResources().getString(R.string.favorites_title));

        MenuItem clearFavorite = toolbar.getMenu().add(getString(R.string.history_clear));
        clearFavorite.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        clearFavorite.setIcon(R.drawable.ic_delete_white_24dp);
        clearFavorite.setOnMenuItemClickListener(item -> {
            clearFavorite();
            return false;
        });


        //обрабатывает появление кнопки "очистить" в зависимости от размера списка
        recyclerView.setOnDataContentChangeListener(new ActionRecyclerView.OnDataContentChangeListener() {
            @Override
            public void onEmpty() {
               clearFavorite.setVisible(false);
            }

            @Override
            public void onFilled() {
                clearFavorite.setVisible(true);
            }
        });




        adapter = new CompositeTranslateAdapter(getContext());

        //главный клик по списку
        adapter.setOnDelayedMainClick(item -> {
            EventBus.getDefault().post(new ShowWordEvent(item));
            ((MainActivity)getActivity()).openActionFragment(2);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Инициализация и определение поставщика данных для списка
        recyclerView.init(adapter, new SearchProvider<>(CompositeTranslateModel.class, state -> YtaApplication.getDaoSession().getCompositeTranslateModelDao()
                .queryBuilder().where(CompositeTranslateModelDao.Properties.Favorite.eq(true), new WhereCondition.StringCondition(((SearchInputData)state).getSearchCondition())).orderDesc(CompositeTranslateModelDao.Properties.SaveFavoriteDate).limit(state.getPageSize()).offset(state.getOffset())
                .list()));


        //восстановление данных
        if(savedInstanceState==null)
            recyclerView.initData();
        else
            recyclerView.setInitialData((List<CompositeTranslateModel>) savedInstanceState.getSerializable(IS_RECYCLER_DATA));

        return view;
    }

    /**
     * очистка избранного
     */
    private void clearFavorite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.favorite_clear);
        builder.setMessage(R.string.favorite_clear_are_you_sure);
        builder.setNegativeButton(R.string.no, (dialog, which) -> {

        });
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                      recyclerView.removeAll();
            EventBus.getDefault().post(new FavoriteClearEvent());
            SimpleAsyncTask.run(() -> {

                for(CompositeTranslateModel item : YtaApplication.getDaoSession().getCompositeTranslateModelDao().loadAll()){
                    item.setFavorite(false);
                    YtaApplication.getDaoSession().update(item);

                }

                YtaApplication.getDaoSession().getCompositeTranslateModelDao().queryBuilder()
                        .where(CompositeTranslateModelDao.Properties.Favorite.eq(false),CompositeTranslateModelDao.Properties.History.eq(false))
                        .buildDelete().executeDeleteWithoutDetachingEntities();

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

    /**
     * слушатель, следящий за изменением favorite
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFavoriteStatus(WordFavoriteStatusChangedEvent event){
        CompositeTranslateModel translateModel = event.getTranslateModel();
        if (translateModel.getFavorite()){
            recyclerView.addToTop(translateModel);
        } else {
            recyclerView.remove(translateModel);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(IS_RECYCLER_DATA,recyclerView.getCurrentData());
    }


}
