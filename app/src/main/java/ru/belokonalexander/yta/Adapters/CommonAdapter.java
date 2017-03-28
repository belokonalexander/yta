package ru.belokonalexander.yta.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;

/**
 * Created by Alexander on 24.03.2017.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<T> data;
    Context context;


    abstract  RecyclerView.ViewHolder onCreateVH(ViewGroup parent, int viewType);
    abstract  void onBindVH(RecyclerView.ViewHolder holder, int position);

    public void rewriteAll(List<T> data){
        this.data = data;
        notifyDataSetChanged();
    }

    CommonAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
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

    public void moveToTop(int index) {
        T object = data.get(index);
        data.remove(index);
        data.add(0,object);
        notifyItemMoved(index,0);
    }

    public void addToTop(T object){
        data.add(0,object);
        notifyItemInserted(0);
    }

    public void update(T item, int index) {
        T object = data.get(index);
        object = item;
        notifyItemChanged(index);
    }

    public void remove(T object) {
        int index = data.indexOf(object);
        if(index>=0){
            data.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void add(List<T> list) {
        int was = data.size();
        data.addAll(list);
        notifyItemRangeInserted(was,data.size());
    }


    public interface OnClickListener<T>{
        void onClick(T item);
    }

}
