package ru.belokonalexander.yta.Database;

import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * аннотация для полей, по которым может осуществляться поиск
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchField {
    boolean lazySearch();
    boolean fullContains() default false;
    @StringRes int alias();
    int order() default 100;
}