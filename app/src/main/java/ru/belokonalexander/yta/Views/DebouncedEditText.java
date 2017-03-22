package ru.belokonalexander.yta.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;


import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;


/**
 * Created by Alexander on 18.03.2017.
 */

public class DebouncedEditText extends EditText {

    public static final int DEBOUNCE_VALUE = 300;
    OnTextActionListener onTextActionListener;

    String lastResult="";
    boolean created = false;

    public DebouncedEditText(Context context) {
        super(context);
    }

    public DebouncedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DebouncedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void clear(){
        setText("");
        this.requestFocus();
    }



    public void setOnTextActionListener(OnTextActionListener onTextActionListener) {
        this.onTextActionListener = onTextActionListener;
    }

    public interface OnTextActionListener{
        void onTextAction(String text);
        void onTextClear();
    }

    public void startWatching(){
        RxTextView.textChanges(this)
                .filter(charSequence -> {
                    if(!created) {                                               //первый "инициализирующий" эммит оставляем без реакции
                        created = true;
                        return false;
                    } else if(charSequence.toString().trim().length()==0) {     //поле ввода было очищено
                        lastResult = "";
                        if(onTextActionListener!=null)
                            onTextActionListener.onTextClear();
                        return false;

                    }
                    return true;
                })
                .debounce(DEBOUNCE_VALUE, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(charSequence -> !charSequence.toString().trim().equals(lastResult))
                .subscribe(charSequence -> {
                    lastResult = charSequence.toString().trim();
                    if(onTextActionListener!=null){
                            onTextActionListener.onTextAction(getText().toString());
                    } else  throw new NullPointerException("OnTextActionListener is null");
                });
    }




}
