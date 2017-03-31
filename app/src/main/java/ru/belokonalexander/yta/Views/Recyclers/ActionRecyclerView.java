package ru.belokonalexander.yta.Views.Recyclers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import ru.belokonalexander.yta.Adapters.CommonAdapter;
import ru.belokonalexander.yta.GlobalShell.SimpleAsyncTask;
import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationSlider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SolidProvider;

/**
 * Created by Alexander on 28.03.2017.
 */

/**
 * Представляет базовый класс списока с поставщиком контента, который изменяет данные
 * Сразу же получает все данные от поставщика
 * @param <T> тип элемента списка адаптера
 *
 * для сохранения правильной логики работы необходимо обернуть Recycler в RelativeLayout
 */

public class ActionRecyclerView<T> extends RecyclerView {

    /**
     *  адаптер с данными, содержащимися в списке
     */
    protected CommonAdapter<T> adapter;


    /**
     * выполняется ли в данный момент подгрузка
     */
    Boolean loadingInProgress = false;

    /**
     * поставщик данных
     */
    SolidProvider<T> provider;


    /**
     *  надпись, появляющаяся, если источник не имеет данных
     */
    RelativeLayout emptyDataController;


    /**
     * обязательная инициализация
     */
    public void init(CommonAdapter<T> adapter, SolidProvider<T> provider){

        if(!((getParent()) instanceof RelativeLayout))
            throw new UnsupportedOperationException("Recycler should have Relative Wrapper");

        this.adapter = adapter;
        this.provider = provider;
        setAdapter(adapter);

        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        emptyDataController = (RelativeLayout) layoutInflater.inflate(R.layout.item_empty_data, null);
        ((ViewGroup)getParent()).addView(emptyDataController);

        getData(UpdateMode.INITIAL);
    }

    /**
     * метод, отвечающий за подгрузку данных
     * @param updateMode - способ загрузки
     */
    void getData(UpdateMode updateMode){

        beforeUpdating(updateMode);
        SimpleAsyncTask.run(() -> dataLoading(updateMode), result -> dataLoaded(result,updateMode));
    }

    /**
     * процесс запроса данных от поставщика
     * @param updateMode - режим в котором производится запрос
     * @return - данные от поставщика
     */
    protected List<T> dataLoading(UpdateMode updateMode) {
        return provider.getData();
    }

    /**
     * обработка новых результатов
     * @param result - данные, полученные от провайдера
     * @param updateMode - режим, в котором проводилось обновление
     */
    protected void dataLoaded(List<T> result, UpdateMode updateMode) {

        //если подгрузка, то добавляем данные
        if(!result.isEmpty() && updateMode==UpdateMode.ADD) {
            add(result);
        } else {
            //если другие режимы, то данные переписываются
            rewriteAll(result);
        }



        afterUpdating(updateMode, result);
    }


    public void beforeUpdating(UpdateMode updateMode) {
        loadingInProgress = true;
    }

    public void onDataSizeChanged(){
        if(adapter.getRealItems()==0)
            enableEmptyController();
        else disableEmptyController();
    }

    public void afterUpdating(UpdateMode updateMode, List<T> data){
        loadingInProgress = false;
        onDataSizeChanged();
    }

    public void enableEmptyController(){
        emptyDataController.setVisibility(VISIBLE);
    }

    public void disableEmptyController(){
        emptyDataController.setVisibility(INVISIBLE);
    }

    public ActionRecyclerView(Context context) {
        super(context);
    }


    public ActionRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void moveToTop(int index) {
        T object = adapter.getData().get(index);
        adapter.getData().remove(index);
        adapter.getData().add(0,object);
        adapter.notifyItemMoved(index,0);
        onDataSizeChanged();
    }

    public void addToTop(T object){
        adapter.getData().add(0,object);
        adapter.notifyItemInserted(0);
        onDataSizeChanged();
    }

    public void update(T item, int index) {
        T object = adapter.getData().get(index);
        object = item;
        adapter.notifyItemChanged(index);
        onDataSizeChanged();
    }

    public void remove(T object) {
        int index = adapter.getData().indexOf(object);
        if(index>=0){
            adapter.getData().remove(index);
            adapter.notifyItemRemoved(index);
        }
        onDataSizeChanged();
    }

    public void add(List<T> list) {
        int was = adapter.getData().size();
        adapter.getData().addAll(list);
        adapter.notifyItemRangeChanged(was,adapter.getData().size());
        onDataSizeChanged();
    }

    public void rewriteAll(List<T> data){
        adapter.setData(data);
        adapter.notifyDataSetChanged();
        onDataSizeChanged();
    }

}
