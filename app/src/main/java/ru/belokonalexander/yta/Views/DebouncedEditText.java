package ru.belokonalexander.yta.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Alexander on 18.03.2017.
 */

public class DebouncedEditText extends EditText {

    public static final int DEBOUNCE_VALUE = 600;
    OnTextActionListener onTextActionListener;
    boolean updateOnEmpty = false;

    public DebouncedEditText(Context context) {
        super(context);
    }

    public DebouncedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DebouncedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    public void setOnTextActionListener(OnTextActionListener onTextActionListener) {
        this.onTextActionListener = onTextActionListener;
    }

    public interface OnTextActionListener{
        void onTextAction(String text);
    }

    public void startWatching(){
        RxTextView.textChanges(this)
                .debounce(DEBOUNCE_VALUE, TimeUnit.MILLISECONDS)
                .filter(charSequence -> length()>0 || updateOnEmpty)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(onTextActionListener!=null){
                            onTextActionListener.onTextAction(getText().toString());
                    } else  throw new NullPointerException("OnTextActionListener is null");
                });
    }

}
