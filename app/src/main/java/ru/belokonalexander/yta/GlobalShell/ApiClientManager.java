package ru.belokonalexander.yta.GlobalShell;

/**
 * Created by Alexander on 24.03.2017.
 */

public interface ApiClientManager {
    void addRequest(IApiRequest request);
    void clear();
}
