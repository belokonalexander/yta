package ru.belokonalexander.yta.GlobalShell.Models.Rx;

import io.reactivex.Observable;

/**
 * Created by Alexander on 22.03.2017.
 */

public interface ChangedEntity<T> {
    Observable<T> getChanged();
}
