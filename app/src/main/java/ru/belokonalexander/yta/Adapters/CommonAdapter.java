package ru.belokonalexander.yta.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 24.03.2017.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<T> data;
    Context context;


    abstract  RecyclerView.ViewHolder onCreateVH(ViewGroup parent, int viewType);
    abstract  void onBindVH(RecyclerView.ViewHolder holder, int position);


    public CommonAdapter() {

        data = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateVH(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindVH(holder, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<T> getData(){
        return data;
    }

    public T getItem(int pos){
        return data.get(pos);
    }

    OnClickListener<T> onClickListener;

    public void setOnClickListener(OnClickListener<T> onClickListenet) {
        this.onClickListener = onClickListenet;
    }

    public interface OnClickListener<T>{
        void onClick(T item);
    }

}
