package ru.belokonalexander.yta.Views.Helpers;

import ru.belokonalexander.yta.GlobalShell.Models.ApplicationException;


/**
 * интерфейс для view, которые могут отображать ApplicationException ошибки
 */
public interface ErrorProjector {
    void displayError(ApplicationException error, ErrorResolver resolver);
}
