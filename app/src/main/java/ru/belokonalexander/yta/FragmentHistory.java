package ru.belokonalexander.yta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.belokonalexander.yta.Adapters.CommonAdapter;
import ru.belokonalexander.yta.Adapters.CompositeTranslateAdapter;
import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.Database.CompositeTranslateModelDao;
import ru.belokonalexander.yta.GlobalShell.SimpleAsyncTask;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;

/**
 * Created by Alexander on 16.03.2017.
 */

public class FragmentHistory extends Fragment{

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    CompositeTranslateAdapter adapter = new CompositeTranslateAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);
        ButterKnife.bind(this, view);


        adapter.setOnClickListener(item -> StaticHelpers.LogThis(item));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        SimpleAsyncTask.run(() -> YtaApplication.getDaoSession().getCompositeTranslateModelDao()
                .queryBuilder().where(CompositeTranslateModelDao.Properties.History.eq(true)).orderDesc(CompositeTranslateModelDao.Properties.UpdateDate).build().list(),
                result -> adapter.rewriteAll(result));

        return view;
    }
}
