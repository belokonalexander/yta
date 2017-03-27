package ru.belokonalexander.yta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.belokonalexander.yta.Adapters.CompositeTranslateAdapter;
import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.Database.CompositeTranslateModelDao;
import ru.belokonalexander.yta.Events.EventCreateType;
import ru.belokonalexander.yta.Events.ShowWordEvent;
import ru.belokonalexander.yta.Events.WordFavoriteStatusChangedEvent;
import ru.belokonalexander.yta.Events.WordSavedInHistoryEvent;
import ru.belokonalexander.yta.GlobalShell.SimpleAsyncTask;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;

/**
 * Created by Alexander on 16.03.2017.
 */

public class FragmentHistory extends Fragment{

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    CompositeTranslateAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);
        ButterKnife.bind(this, view);

        adapter =  new CompositeTranslateAdapter(getContext());
        adapter.setOnClickListener(item -> {
            EventBus.getDefault().post(new ShowWordEvent(item, EventCreateType.COPY));
            ((MainActivity)getActivity()).openActionFragment();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        SimpleAsyncTask.run(() -> YtaApplication.getDaoSession().getCompositeTranslateModelDao()
                .queryBuilder().where(CompositeTranslateModelDao.Properties.History.eq(true)).orderDesc(CompositeTranslateModelDao.Properties.UpdateDate).build().list(),
                result -> adapter.rewriteAll(result));

        return view;
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
            adapter.addToTop(translateModel);
        } else {
            adapter.moveToTop(itemIndex);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFavoriteStatus(WordFavoriteStatusChangedEvent event){
        CompositeTranslateModel translateModel = event.getTranslateModel();
        int itemIndex = adapter.getData().indexOf(translateModel);
        if(itemIndex<0){
            adapter.addToTop(translateModel);
        } else {
            adapter.update(translateModel,itemIndex);
        }
    }


}
