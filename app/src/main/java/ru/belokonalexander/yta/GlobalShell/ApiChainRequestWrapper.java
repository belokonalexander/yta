package ru.belokonalexander.yta.GlobalShell;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

    private Subscriber subscriber;
    private Observable taskExecutor;

    //слушатель для обработки результата работы цепочки запросов
    private OnApiResponseListener<List> listener;

    //идентификатор цепочки, служит для идентификации одноцелевых запросов и их взаимного прерывания
    private String hash;

    private ApiChainRequestWrapper(String commonHash, OnApiResponseListener<List> listener, Observable... taskChain) {

        this.listener = listener;
        hash =  commonHash;

        taskExecutor = Observable.merge(taskChain)
                .subscribeOn(Schedulers.newThread());

        subscriber = new Subscriber() {
            @Override
            public void onCompleted() {
                unregisterSelf();
                if(listener!=null)
                    listener.onSuccess(results);
            }

            @Override
            public void onError(Throwable e) {
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

    public static ApiChainRequestWrapper getInstance(String commonHash, OnApiResponseListener<List>  listener, Observable... taskChain){
        if(taskChain.length==0){
            throw new NullPointerException("Need at least one request parameter");
        }
        return new ApiChainRequestWrapper(commonHash,  listener, taskChain);
    }

    @Override
    public void execute() {
        registerInList();
        taskExecutor.subscribe(subscriber);
    }

    @Override
    public void cancel() {
            subscriber.unsubscribe();
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
