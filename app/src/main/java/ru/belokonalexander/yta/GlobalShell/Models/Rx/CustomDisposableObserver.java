package ru.belokonalexander.yta.GlobalShell.Models.Rx;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Alexander on 22.03.2017.
 */

public abstract class CustomDisposableObserver<T extends ChangedEntity> extends DisposableObserver<T> {


    @Override
    final protected void onStart() {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {

    }

    abstract public void init(T observable);
}
