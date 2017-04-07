package ru.belokonalexander.yta.GlobalShell;


public interface IApiRequest {

    //Работает в том потоке, в котором был вызван -> не должен вызываться из Main потока
    void execute();
    boolean cancel();
    String getHash();
    boolean isRunning();
}
