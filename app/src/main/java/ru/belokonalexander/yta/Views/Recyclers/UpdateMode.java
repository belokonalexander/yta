package ru.belokonalexander.yta.Views.Recyclers;

/**
 * режим обновления для ActionRecyclerView
 */

public enum UpdateMode {
    ADD,        //данные добавляются в конец
    REWRITE,    //переписывает данные, производя заполнение с самого начала
    REFRESH,    //обновляет данные
    INITIAL;    //инициализация данных (первая подгрузка, например, если нужно реализовать кеширование)
}
