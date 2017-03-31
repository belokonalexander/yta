package ru.belokonalexander.yta.GlobalShell;

/**
 * Created by Alexander on 18.03.2017.
 */

public class Settings {

    /*
        интервал поиска в кэше по времени от текущей даты:
        в кэше ищем значения начиная с ТЕКУЩАЯ_ДАТА - CACHE_INTERVAL
    */
    public static final int CACHE_INTERVAL = 1;
    public static final int HISTORY_WORD_SAVE_DELAY = 2000;
    public static final int IMMEDIATELY = 0;


    public static final int THROTTLE_CLICK_VALUE = 1500;
    public static final int SEARCH_DEBOUNCE = 1500;
    public static final int THROTTLE_CLICK_VALUE_LOCAL = 300;
    public static final int SEARCH_DEBOUNCE_LOCAL = 300;

}
