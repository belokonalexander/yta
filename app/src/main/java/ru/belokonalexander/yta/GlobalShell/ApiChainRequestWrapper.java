package ru.belokonalexander.yta.GlobalShell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;



/**
 * Created by Alexander on 19.03.2017.
 */

/**
    Цепочка запросов - группирует запросы в один логический элемент и выполняет их группой
    Также содержит список выполняющихся в данный момент запросов, что позволяет отменять одновременное выполнение одинаковых запросов,

*/
public class ApiChainRequestWrapper implements IApiRequest {

    // список с выполняющимися в данный момент запросами
    private static List<ApiChainRequestWrapper> runningRequests = new ArrayList<>();

    //локер, для синхронизации обращений к статическому runningRequests
    private static final Object listLock = new Object();

    List<Observable> chain = new ArrayList<>();

    //ответы на запросы
    private List<Object> results = new ArrayList<>();

    private DisposableObserver subscriber;
    private Observable taskExecutor;

    //слушатель для обработки результата работы цепочки запросов
    private OnApiFailureResponseListener failureListener;
    private OnApiSuccessResponseListener<List> successListener;

    /**
        Идентификатор цепочки, служит для идентификации одноцелевых запросов и их взаимного прерывания,
    */
    private String hash;

    private RunningType runningType;

    private enum RunningType{
        /*
            Все запросы должны выполниться без ошибок,
            если в каком-то запросе произошла ошибка - прерываем всю работу и вызываем слушателя -> атомарная операция
        */

        GROUP,

        /*
            Запросы, результаты которых планируются рассматирвать раздельно,
            противоположность GROUP-типу. Ошибка не прерывает общую работу цепочки, а в результат
            записывается Throwable
        */

        APART
    }


    private ApiChainRequestWrapper(String commonHash, OnApiSuccessResponseListener<List> successListener, OnApiFailureResponseListener failureListener, RunningType type, Observable... taskChain) {

        this.successListener = successListener;
        this.failureListener = failureListener;
        this.hash =  commonHash;
        this.runningType = type;



        if(type==RunningType.GROUP)
            taskExecutor = Observable.mergeArray(taskChain)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        else if(type==RunningType.APART){

            /**
                Задачи:
                1) Не прерывать цепочку при возникновении ошибок
                2) Сохранить порядок ответов, например в @results может быть [..., Exception, ...]
                    - получается, что DelayError обработчики использовать не получится - составить верный порядок не удастся
                    - также не получится использовать методы типа OnError, которые или генерируют новые Observable (мы не знаем на каком именно прервались),
                      или передают управление на OnError или OnComplete
                    -> Решение: генерируем новый Observable в flatMap и вешаем на него обработчик, который заменит оригинал в случае ошибки и выбросит в результат Throwable
                3) Всегда вызывается onComplete и слушателю передается @results
            */

            taskExecutor = Observable.fromArray(taskChain)
                    .flatMap((Function<Observable, ObservableSource<?>>) observable -> observable.onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                        @Override
                        public ObservableSource apply(Throwable throwable) throws Exception {
                            return Observable.just(throwable);
                        }
                    })).subscribeOn(Schedulers.newThread())
                      .observeOn(AndroidSchedulers.mainThread());
        }


        subscriber = new DisposableObserver() {

            @Override
            public void onComplete() {
                unregisterSelf();
                if(successListener!=null)
                    successListener.onSuccess(results);
            }

            @Override
            public void onError(Throwable e) {
                unregisterSelf();
                if (failureListener != null)
                    failureListener.onFailure(e);


            }

            @Override
            public void onNext(Object o) {
                results.add(o);
            }
        };

    }

    public static ApiChainRequestWrapper getGroupInstance(String commonHash,OnApiSuccessResponseListener<List> successListener, OnApiFailureResponseListener failureListener, Observable... taskChain){
        if(taskChain.length==0){
            throw new NullPointerException("Need at least one request parameter");
        }
        return new ApiChainRequestWrapper(commonHash,  successListener, failureListener, RunningType.GROUP, taskChain);
    }

    public static ApiChainRequestWrapper getApartInstance(String commonHash, OnApiSuccessResponseListener<List> successListener, Observable... taskChain){
        if(taskChain.length==0){
            throw new NullPointerException("Need at least one request parameter");
        }
        return new ApiChainRequestWrapper(commonHash,  successListener, null, RunningType.APART, taskChain);
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
