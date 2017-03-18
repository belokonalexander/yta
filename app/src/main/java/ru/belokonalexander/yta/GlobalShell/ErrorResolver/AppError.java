package ru.belokonalexander.yta.GlobalShell.ErrorResolver;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

/**
 * Created by Alexander on 17.03.2017.
 */

public class AppError extends Throwable {

    String message;
    int code;

    public AppError(Throwable throwable) {

        if (throwable instanceof HttpException) {
            // We had non-2XX http error
            HttpException e = (HttpException) throwable;
            message = e.message();
            code = e.code();
        }
        if (throwable instanceof IOException) {
            // A network or conversion error happened
            IOException e = (IOException) throwable;
            message = e.getMessage();
            code = -1;
        }

        StaticHelpers.LogThis(" TH: " + throwable);
        StaticHelpers.LogThis(" ERROR: " + message + " / code = " + code);

    }
}
