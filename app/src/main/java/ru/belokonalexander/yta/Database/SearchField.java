package ru.belokonalexander.yta.Database;

import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Alexander on 30.03.2017.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface SearchField {
    boolean lazySearch();
    boolean fullContains() default false;
    @StringRes int alias();
    int order() default 100;
}