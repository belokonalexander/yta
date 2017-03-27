package ru.belokonalexander.yta.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;


/**
 * Created by Alexander on 16.03.2017.
 */

/**
 *  Описывает основную логику  для перевода текста
 */
public class CustomTexInputView extends RelativeLayout {

    /**
     *  задержка для ленивого поиска
     */
    public static final int DEBOUNCE = 300;

    /**
     *  поле ввода текста
     */
    private EditText editText;

    /**
     * кнопка отчистки поля ввода
     */
    private ImageButton clearButton;

    /**
     * слушатели основных событий
     */
    private OnTextActionListener onTextActionListener;

    /**
     *  последнее значение, которое вводилось в текстовое поле
     */
    private String lastResult = "";

    /**
     *  состояние текстового поля
     */
    private boolean focusState;

    public void setWithoutUpdate(String text){
        lastResult = text;
        editText.setText(text);
        editText.setSelection(text.length());
    }

    public void setText(String text){
        editText.setText(text);
        editText.setSelection(text.length());
        editText.requestFocus();
    }

    public void clearText(){
        if(onTextActionListener!=null){
            onTextActionListener.onTextClear();
        }
        setText("");
    }

    /**
     * перезапуск последнего состояния
     */
    public void reset(){
        String asNewState = lastResult;
        lastResult="";
        setText(asNewState);
    }

    public CustomTexInputView(Context context) {
        super(context);
    }

    public CustomTexInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTexInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    /**
     *  инициализируются представления и слушатели
     *  вся работа организовывается на RxTextView, который после успешного прохождения фильтрации текста
     *  вызывает назначенных слушателей. В данном случае хоть и используется реактивные элементы, общий стиль не является реактивным,
     *  так как метод не возвращает Observable<>, а самостоятельно делегирует обработку событий своим слушателям
     */
    private void defineViews(){

        editText = (EditText) findViewById(R.id.input_text);
        clearButton = (ImageButton) findViewById(R.id.clear_button);
        editText.setOnFocusChangeListener((v, hasFocus) -> focusState = hasFocus ? goFocusState() : goNormalState());

        clearButton.setOnClickListener(v ->  {
            clearText();
        });

        RxTextView.textChanges(editText)
                .skip(1)                    //пропускаю первый (инициализирующий) эммит
                .filter(charSequence -> {
                    if(charSequence.toString().trim().length()==0) {     //поле ввода пустое
                        //вызывается событие отчистки, если поле было до этого не пустым
                        if(!lastResult.equals("")) {
                            lastResult = "";
                            if (onTextActionListener != null) {
                                onTextActionListener.onTextClear();
                            }
                        }
                        return false;
                    }
                    return true;
                })
                .debounce(DEBOUNCE, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(charSequence -> !charSequence.toString().trim().equals(lastResult))
                .subscribe(charSequence -> {
                    lastResult = charSequence.toString().trim();
                    if(onTextActionListener!=null){
                        onTextActionListener.onTextAction(charSequence.toString());
                    }
                });


    }


    /**
     * интерфейс, описывающий процесс обработки запроса и очистки поля ввода
     */
    public interface OnTextActionListener{
        void onTextAction(String text);
        void onTextClear();
    }

    public void setOnTextListener(OnTextActionListener listener){
        this.onTextActionListener = listener;
    }

    private boolean goNormalState() {
        int pad = getResources().getDimensionPixelSize(R.dimen.icon_padding);
        this.setBackgroundResource(R.drawable.input_background);
        this.setPadding(pad,pad,pad,pad);
        return false;
    }

    private boolean goFocusState() {
        int pad = getResources().getDimensionPixelSize(R.dimen.icon_padding);
        this.setBackgroundResource(R.drawable.input_background_focused);
        this.setPadding(pad,pad,pad,pad);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        defineViews();
    }




}
