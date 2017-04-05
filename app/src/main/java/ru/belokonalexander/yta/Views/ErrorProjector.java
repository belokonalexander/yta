package ru.belokonalexander.yta.Views;

import ru.belokonalexander.yta.GlobalShell.Models.ApplicationException;

/**
 * Created by Alexander on 05.04.2017.
 */

public interface ErrorProjector {
    void displayError(ApplicationException error, ErrorResolver resolver);
}
