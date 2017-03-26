package ru.belokonalexander.yta.GlobalShell.ErrorResolver;

import java.io.IOException;

import retrofit2.HttpException;


/**
 * Created by Alexander on 17.03.2017.
 */

public class ApiError extends Throwable {

    String message;
    int code;

    public ApiError(Throwable throwable) {

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
    }
}
