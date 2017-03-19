package ru.belokonalexander.yta.GlobalShell;

/**
 * Created by Alexander on 19.03.2017.
 */

public interface IApiRequest {

    //Работает в том потоке, в котором был вызван -> не должен вызываться из Main потока
    void execute();
    void cancel();
    String getHash();
}
