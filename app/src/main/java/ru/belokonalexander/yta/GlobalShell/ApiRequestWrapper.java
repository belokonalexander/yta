package ru.belokonalexander.yta.GlobalShell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.ActionSubscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Alexander on 18.03.2017.
 */


/*  Wrapper-class для api-запросов, для организации планироващика запросов
    Каждому запросу присваивается id значение запроса, которое генерируется в зависимости от контекста запроса
    Сам планировщик хранит список всех запросов, выполняющихся в данный момент.
    При добавлении нового запроса с таким же id, старый запрос прерывается

    @Deprecated на основе логики данного класса описал класс ApiChainRequestWrapper,
    в котором можно, помимо цеочки запросов, так же использовать одиночный запрос (цепочка из одного запроса)
    Решил не удалять данную реализацию для демонстрации
*/
@Deprecated
public class ApiRequestWrapper<T> implements IApiRequest {

    private static  List<ApiRequestWrapper> currentTasksList = new ArrayList<>();
    private Observable<T> query;
    private Subscriber<T> subscrber;
    private static final Object listLock = new Object();
    private OnApiResponseListener<T> onApiResponseListener;
    private String hash;



    private ApiRequestWrapper(Observable<T> query, String hash, OnApiResponseListener<T> listener) {
        this.query = query;
        this.hash = hash;
        this.subscrber = getSubscriber();
        this.onApiResponseListener = listener;
    }

    private ApiRequestWrapper(Observable<T> query, String hash) {
        this.query = query;
        this.hash = hash;
        this.subscrber = getSubscriber();
    }

    //cоздает одиночный асинхронный запрос
    public static<S> ApiRequestWrapper getInstance(Observable<S> query, String hash, OnApiResponseListener<S> listener){
          return new ApiRequestWrapper<>(query, hash, listener);
   };

    //создает одиночный синхронный запрос
    static<S> ApiRequestWrapper getInstance(Observable<S> query, String hash){
           return new ApiRequestWrapper<>(query, hash);
    } ;



    @Override
    public void execute() {
        registerInList();
        query.subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(subscrber);
    }

    @Override
    public void cancel() {
        cancelTask();
        unregisterSelf();
    }

    @Override
    public String getHash() {
        return hash;
    }

    private void registerInList() {
        //закрываем текущие запросы
        unregisterOther();

        //добавляемся сами
        registerSelf();
    }

    private void unregisterSelf(){

        synchronized (listLock){
            currentTasksList.remove(this);
        }

    }

    private void unregisterOther() {
        synchronized (listLock){
            Set<Integer> deletedIndexes = new HashSet<>();
            int index = 0;
            for(ApiRequestWrapper apiRequestWrapper : currentTasksList){
                if(this.hash.equals(apiRequestWrapper.hash)) {
                    deletedIndexes.add(index);
                    apiRequestWrapper.cancelTask();
                }
                index++;
            }

            for(int i : deletedIndexes){
                StaticHelpers.LogThis("Отписываюсь по требованию");
                currentTasksList.remove(i);
            }
        }
    }

    private void registerSelf() {
        synchronized (listLock) {
            currentTasksList.add(this);
        }
    }

    public void setOnApiResponseListener(OnApiResponseListener<T> onApiResponseListener) {
        this.onApiResponseListener = onApiResponseListener;
    }

    private Subscriber<T> getSubscriber() {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                unregisterSelf();
            }

            @Override
            public void onError(Throwable e) {
                unregisterSelf();
                if(onApiResponseListener != null) {
                    onApiResponseListener.onFailure(e);
                }

            }

            @Override
            public void onNext(T t) {
                if(onApiResponseListener != null) {
                    onApiResponseListener.onSuccess(t);
                }
            }
        };
    }

    private void cancelTask(){
        subscrber.unsubscribe();
    }


    public Observable<T> getQuery() {
        return query;
    }
}
