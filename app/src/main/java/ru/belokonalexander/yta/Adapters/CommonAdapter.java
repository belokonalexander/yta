package ru.belokonalexander.yta.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;

import static ru.belokonalexander.yta.Adapters.CommonAdapter.Decoration.FOOTER;
import static ru.belokonalexander.yta.Adapters.CommonAdapter.Decoration.SIMPLE;

/**
 * Created by Alexander on 24.03.2017.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<T> data;
    Context context;


    abstract  RecyclerView.ViewHolder onCreateVH(ViewGroup parent, int viewType);
    abstract  void onBindVH(RecyclerView.ViewHolder holder, int position);

    public void setData(List<T> data) {
        this.data = data;
    }

    public enum Decoration {
        SIMPLE(0),FOOTER(10);

        final int viewType;

        Decoration(int viewType) {
            this.viewType = viewType;
        }
    }

    public void setDecoration(Decoration decoration) {
        this.decoration = decoration;
    }

    private Decoration decoration = Decoration.SIMPLE;

    CommonAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    CommonAdapter(Context context, Decoration decorations) {
        this(context);
        this.decoration = decorations;
    }



    @Override
    public int getItemViewType(int position) {

        if(decoration==FOOTER && position == getItemCount() - 1) {
                return FOOTER.viewType;
        }

        return SIMPLE.viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==FOOTER.viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_view,parent,false);
            return new DummyViewHolder(v);
        } else
            return onCreateVH(parent, viewType);

    }

    public Decoration getDecoration() {
        return decoration;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(!(holder instanceof DummyViewHolder)){
            onBindVH(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        if(decoration==FOOTER){
            return data.size()+1;
        }
        return data.size();
    }

    public int getRealItems(){
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
