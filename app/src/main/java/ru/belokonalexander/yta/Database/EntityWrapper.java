package ru.belokonalexander.yta.Database;

/**
 * Created by Alexander on 26.03.2017.
 */

public interface EntityWrapper<T,S> {
    T toEntity();
    S fromEntity(T entity);
}
