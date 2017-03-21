package ru.belokonalexander.yta.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.belokonalexander.yta.GlobalShell.Models.CompositeTranslateModel;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;

/**
 * Created by Alexander on 21.03.2017.
 */

public class WordList extends LinearLayout {

    CompositeTranslateModel translateResult;


    public void setTranslateResult(CompositeTranslateModel translateResult) {
        this.translateResult = translateResult;
        inflateContent();
    }

    private void inflateContent() {

        StaticHelpers.LogThis(" INFLATE ");

        clearView();

        LinearLayout lastContainer = inflateTranslateResult();

        inflateLookupResult(lastContainer);
    }

    private void inflateLookupResult(LinearLayout lastContainer) {

    }

    private LinearLayout inflateTranslateResult() {
        int orientation = translateResult.getTranslateResult().getText().size() > 1 ? HORIZONTAL : VERTICAL;

        LayoutInflater layoutInflater = (LayoutInflater ) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i =0; i <  translateResult.getTranslateResult().getText().size(); i++){
            LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.word_layout,null);
            LinearLayout controllerGroup = (LinearLayout) layout.findViewById(R.id.word_controller);
            controllerGroup.setOrientation(orientation);
            TextView source = (TextView) layout.findViewById(R.id.original_word);

            ((TextView)layout.findViewById(R.id.t_word)).setText(translateResult.getTranslateResult().getText().get(i));
            source.setText(translateResult.getSource());
            source.setVisibility(VISIBLE);


            if(orientation==HORIZONTAL){

                for(int j =0; j < controllerGroup.getChildCount(); j++) {
                    if (controllerGroup.getChildAt(j).getLayoutParams() instanceof MarginLayoutParams) {
                        ((MarginLayoutParams)controllerGroup.getChildAt(j).getLayoutParams() ).topMargin = StaticHelpers.dpToPixels(6);
                        if(j > 0)  ((MarginLayoutParams)controllerGroup.getChildAt(j).getLayoutParams() ).leftMargin = StaticHelpers.dpToPixels(4);
                    }
                }

                source.setVisibility(GONE);
            }

            addView(layout);

            if(i==translateResult.getTranslateResult().getText().size()-1)
                return layout;

        }


        return null;
    }

    public void clearView() {
        //очищаем прошлый результат
        if(this.getChildCount()>0){
            this.removeAllViews();
        }
    }

    public WordList(Context context) {
        super(context);
        startInit();
    }

    public WordList(Context context, AttributeSet attrs) {
        super(context, attrs);
        startInit();
    }

    public WordList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        startInit();
    }

    private void startInit() {
        setOrientation(VERTICAL);
        //setBackgroundColor(ContextCompat.getColor(getContext(),R.color.cardview_shadow_start_color));
        setPadding(0,StaticHelpers.dpToPixels(4),0, StaticHelpers.dpToPixels(4));
    }

}
