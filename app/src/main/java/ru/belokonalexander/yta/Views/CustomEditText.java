package ru.belokonalexander.yta.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;


/**
 * edit text, который реагирует на события клавиатуры: ON_DONE и BACK_PRESSED
 */
public class CustomEditText extends EditText {


    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onEditorAction(int actionCode) {
        super.onEditorAction(actionCode);
        if (actionCode == EditorInfo.IME_ACTION_DONE) {
            if(onKeyActionListener!=null)
                onKeyActionListener.onAction();
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            if(onKeyActionListener!=null)
                onKeyActionListener.onAction();
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    OnKeyActionListener onKeyActionListener;

    public void setOnKeyActionListener(OnKeyActionListener onKeyActionListener) {
        this.onKeyActionListener = onKeyActionListener;
    }

    public interface OnKeyActionListener{
        void onAction();
    }

}
