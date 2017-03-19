package ru.belokonalexander.yta.GlobalShell;

/**
 * Created by Alexander on 19.03.2017.
 */

public interface OnApiResponseListener<T> {
        void onSuccess(T result);
        void onFailure(Throwable failure);
}
