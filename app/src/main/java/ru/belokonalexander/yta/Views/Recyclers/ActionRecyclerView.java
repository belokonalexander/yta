package ru.belokonalexander.yta.Views.Recyclers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.List;

import ru.belokonalexander.yta.Adapters.CommonAdapter;
import ru.belokonalexander.yta.GlobalShell.SimpleAsyncTask;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.PaginationSlider;
import ru.belokonalexander.yta.Views.Recyclers.DataProviders.SolidProvider;

/**
 * Created by Alexander on 28.03.2017.
 */

/**
 * Представляет базовый класс списока с поставщиком контента, который изменяет данные
 * @param <T> тип элемента списка адаптера
 */

public class ActionRecyclerView<T> extends RecyclerView {

    /**
     *  адаптер с данными, содержащимися в списке
     */
    CommonAdapter<T> adapter;

    /**
     * стандартное значение кол-ва элементов в списке
     */
    private int pageSize = 20;

    /**
     * выполняется ли в данный момент подгрузка
     */
    Boolean loadingInProgress = false;

    /**
     * поставщик данных
     */
    SolidProvider<T> provider;

    /**
     *  все данные из источника были получены
     */
    Boolean allDataWasObtained = false;


    /**
     * обязательная инициализация
     */
    public void init(CommonAdapter<T> adapter, SolidProvider<T> provider){

        this.adapter = adapter;
        this.provider = provider;

        setAdapter(adapter);

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
        PaginationSlider caller = new PaginationSlider(pageSize);;

        if (updateMode == UpdateMode.ADD)
            caller.setOffset(adapter.getItemCount());

        return provider.getData(caller);
    }

    /**
     * обработка новых результатов
     * @param result - данные, полученные от провайдера
     * @param updateMode - режим, в котором проводилось обновление
     */
    protected void dataLoaded(List<T> result, UpdateMode updateMode) {

        //если подгрузка, то добавляем данные
        if(!result.isEmpty() && updateMode==UpdateMode.ADD) {
            adapter.add(result);
        } else if (!result.isEmpty()) {
            //если другие режимы, то данные переписываются
            adapter.rewriteAll(result);
        }

        //проверка - все ли данные отдал поставщик
        allDataWasObtained = result.size() < pageSize;

        afterUpdating(updateMode);
    }


    public void beforeUpdating(UpdateMode updateMode) {
        loadingInProgress = true;
    }

    public void afterUpdating(UpdateMode updateMode){
        loadingInProgress = false;
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
}
