package ru.belokonalexander.yta.GlobalShell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Alexander on 19.03.2017.
 */

//работает с цепочкой одиночных запросов, можно также использовать и один запрос
public class ApiChainRequestWrapper implements IApiRequest {

    // список с выполняющимися в данный момент запросами
    public static List<ApiChainRequestWrapper> runningRequests = new ArrayList<>();

    //локер, для синхронизации обращений к статическому runningRequests
    private static final Object listLock = new Object();

    List<Observable> chain = new ArrayList<>();

    //ответы на запросы
    private List<Object> results = new ArrayList<>();

    private DisposableObserver subscriber;
    private Observable taskExecutor;

    //слушатель для обработки результата работы цепочки запросов
    private OnApiResponseListener<List> listener;

    //идентификатор цепочки, служит для идентификации одноцелевых запросов и их взаимного прерывания
    private String hash;

    private RunningType runningType;

    private enum RunningType{
        /*
            Все запросы должны выполниться без ошибок,
            если в каком-то запросе произошла ошибка - прерываем всю работу и вызываем слушателя, т.е атомарная операция
        */

        GROUP,

        /*
            Запросы, результаты которых планируются рассматирвать раздельно,
            противоположность GROUP-типу. Ошибка не прерывает общую работу цепочки, а в результат
            записывается ApiError
        */

        APART
    }


    private ApiChainRequestWrapper(String commonHash, OnApiResponseListener<List> listener, RunningType type, Observable... taskChain) {

        this.listener = listener;
        this.hash =  commonHash;
        this.runningType = type;

        if(type==RunningType.GROUP)
            taskExecutor = Observable.mergeArray(taskChain)
                    .subscribeOn(Schedulers.newThread());
        else if(type==RunningType.APART){
            taskExecutor = Observable.mergeArray(taskChain)
                    .mergeArrayDelayError(taskChain)
                    .subscribeOn(Schedulers.newThread());
        }


        subscriber = new DisposableObserver() {

            @Override
            public void onComplete() {
                unregisterSelf();
                if(listener!=null)
                    listener.onSuccess(results);
            }

            @Override
            public void onError(Throwable e) {
                StaticHelpers.LogThis(" ON ERROR NEXT");
                unregisterSelf();
                if(listener!=null)
                    listener.onFailure(e);

            }

            @Override
            public void onNext(Object o) {
                StaticHelpers.LogThis(" ON NEXT: " + o);
                results.add(o);
            }
        };

    }

    public static ApiChainRequestWrapper getGroupInstance(String commonHash, OnApiResponseListener<List>  listener, Observable... taskChain){
        if(taskChain.length==0){
            throw new NullPointerException("Need at least one request parameter");
        }
        return new ApiChainRequestWrapper(commonHash,  listener, RunningType.GROUP, taskChain);
    }

    public static ApiChainRequestWrapper getApartInstance(String commonHash, OnApiResponseListener<List>  listener, Observable... taskChain){
        if(taskChain.length==0){
            throw new NullPointerException("Need at least one request parameter");
        }
        return new ApiChainRequestWrapper(commonHash,  listener, RunningType.APART, taskChain);
    }



    @Override
    public void execute() {
        registerInList();
        taskExecutor.subscribe(subscriber);
    }

    @Override
    public void cancel() {
            subscriber.dispose();
    }

    @Override
    public String getHash() {
        return hash;
    }


    private void registerInList() {
        unregisterOther();
        registerSelf();
    }

    //регистрирую запрос в общем списке запущенных запросов
    private void unregisterSelf(){

        synchronized (listLock){
            runningRequests.remove(this);
        }

    }

    //проверяю, есть ли такой запрос в обработке и отменяю его
    private void unregisterOther() {
        synchronized (listLock){
            Set<Integer> deletedIndexes = new HashSet<>();
            int index = 0;
            for(ApiChainRequestWrapper apiRequestWrapper : runningRequests){
                if(this.hash.equals(apiRequestWrapper.hash)) {
                    deletedIndexes.add(index);
                    apiRequestWrapper.cancel();
                }
                index++;
            }

            for(int i : deletedIndexes){
                StaticHelpers.LogThis("Отписываюсь по требованию");
                runningRequests.remove(i);
            }
        }
    }

    //регистрируюсь в общем списке запущенных запросов
    private void registerSelf() {
        synchronized (listLock) {
            runningRequests.add(this);
        }
    }

}
