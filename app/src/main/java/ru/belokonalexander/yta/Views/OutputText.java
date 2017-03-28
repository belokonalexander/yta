package ru.belokonalexander.yta.Views;

import ru.belokonalexander.yta.GlobalShell.Settings;

/**
 * Created by Alexander on 28.03.2017.
 */

public class OutputText {

    final String value;
    final Type type;

    public enum Type{
        HANDWRITTEN(Settings.HISTORY_WORD_SAVE_DELAY), AUTOLOAD(Settings.IMMEDIATELY);

        private int delay;

        Type(int d) {
            this.delay = d;
        }

        public int getDelay() {
            return delay;
        }
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public OutputText(String value, Type type) {
        this.value = value;
        this.type = type;
    }
}
