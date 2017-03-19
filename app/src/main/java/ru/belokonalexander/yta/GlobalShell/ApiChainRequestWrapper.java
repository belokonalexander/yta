package ru.belokonalexander.yta.GlobalShell;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Alexander on 19.03.2017.
 */

//работает с цепочкой одиночных запросов
public class ApiChainRequestWrapper implements IApiRequest {

    List<Observable> chain = new ArrayList<>();
    List<Object> results = new ArrayList<>();
    Subscriber subscriber;
    Observable taskExecutor;
    ApiRequestWrapper.OnApiResponseListener listener;
    String hash;

    private ApiChainRequestWrapper(String commonHash, ApiRequestWrapper.OnApiResponseListener listener, Observable... taskChain) {

        this.listener = listener;
        hash =  commonHash;

        taskExecutor = Observable.merge(taskChain)
                .subscribeOn(Schedulers.newThread());




        subscriber = new Subscriber() {
            @Override
            public void onCompleted() {
                StaticHelpers.LogThis(" ON COMPLETED" );
            }

            @Override
            public void onError(Throwable e) {
                StaticHelpers.LogThis(" ON ERROR: " + e);
            }

            @Override
            public void onNext(Object o) {
                StaticHelpers.LogThis(" ON NEXT: " + o);
                results.add(o);
            }
        };

    }

    public static ApiChainRequestWrapper getInstance(String commonHash, ApiRequestWrapper.OnApiResponseListener listener, Observable... taskChain){
        return new ApiChainRequestWrapper(commonHash,  listener, taskChain);
    }

    @Override
    public void execute() {
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
}
