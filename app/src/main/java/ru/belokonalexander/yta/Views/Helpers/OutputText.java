package ru.belokonalexander.yta.Views.Helpers;

import static ru.belokonalexander.yta.Views.Helpers.OutputText.Type.AUTOLOAD;

/**
 * текстовый результат, который выдвет CustomTextInputView
 * кроме текста также содержит метод ввода - ручной или программный
 */

public class OutputText {

    private final String value;
    private final Type type;

    public enum Type{
        HANDWRITTEN, AUTOLOAD

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

    public OutputText(String value) {
        this.value = value;
        this.type = AUTOLOAD;
    }
}
