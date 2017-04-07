package ru.belokonalexander.yta.GlobalShell;

/**
 * Глобальные настройки приложения
 */

public class Settings {

    /*
        интервал поиска в кэше по времени от текущей даты:
        в кэше ищем значения начиная с ТЕКУЩАЯ_ДАТА - CACHE_INTERVAL
    */
    public static final int CACHE_INTERVAL = 1;
    public static final int FADE_ANIMATION_DURATION = 200;

    public static final int THROTTLE_CLICK_VALUE = 1500;
    public static final int SEARCH_DEBOUNCE = 1500;
    public static final int THROTTLE_CLICK_VALUE_LOCAL = 300;
    public static final int SEARCH_DEBOUNCE_LOCAL = 300;

    public static final int CLICK_DELAY = 150;

}
