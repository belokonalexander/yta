package ru.belokonalexander.yta.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import ru.belokonalexander.yta.R;


/**
 * Created by Alexander on 16.03.2017.
 */

//Кастомный элемент для поля ввода текста
public class CustomTexInputView extends RelativeLayout {

    DebouncedEditText editText;
    ImageButton soundButton;
    ImageButton voiceButton;
    ImageButton clearButton;
    ViewGroup wrapper;
    DebouncedEditText.OnTextActionListener onTextActionListener;

    boolean showVoiceButton;
    boolean showSoundButton;

    boolean focusState;

    public CustomTexInputView(Context context) {
        super(context);
    }

    public CustomTexInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomTexInputView,
                0, 0);

        try {
            showVoiceButton = typedArray.getBoolean(R.styleable.CustomTexInputView_showVoice, true);
            showSoundButton = typedArray.getBoolean(R.styleable.CustomTexInputView_showSound, true);
        } finally {
            typedArray.recycle();
        }


    }

    public CustomTexInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void defineViews(Context context){

        editText = (DebouncedEditText) findViewById(R.id.input_text);
        soundButton = (ImageButton) findViewById(R.id.sound_button);
        voiceButton = (ImageButton) findViewById(R.id.voice_button);
        clearButton = (ImageButton) findViewById(R.id.clear_button);
        wrapper = (ViewGroup) findViewById(R.id.wrapper);

        if(!showSoundButton)
            soundButton.setVisibility(GONE);

        if(!showVoiceButton)
            voiceButton.setVisibility(GONE);

        editText.setOnFocusChangeListener((v, hasFocus) -> focusState = hasFocus ? goFocusState() : goNormalState());

        clearButton.setOnClickListener(v -> editText.setText(""));

    }


    public void setOnTextListener(DebouncedEditText.OnTextActionListener listener){
        editText.setOnTextActionListener(listener);
        editText.startWatching();
    }

    private boolean goNormalState() {
        int pad = getResources().getDimensionPixelSize(R.dimen.input_text_padding);
        wrapper.setBackgroundResource(R.drawable.input_background);
        wrapper.setPadding(pad,pad,pad,pad);
        return false;
    }

    private boolean goFocusState() {
        int pad = getResources().getDimensionPixelSize(R.dimen.input_text_padding);
        wrapper.setBackgroundResource(R.drawable.input_background_focused);
        wrapper.setPadding(pad,pad,pad,pad);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        defineViews(getContext());
    }
}
