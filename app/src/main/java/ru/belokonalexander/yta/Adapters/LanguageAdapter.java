package ru.belokonalexander.yta.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.belokonalexander.yta.GlobalShell.Models.Language;

import ru.belokonalexander.yta.R;

/**
 * Адаптер для списка языков
 */

public class LanguageAdapter extends CommonAdapter<Language> {


    public LanguageAdapter(Context context) {
        super(context);
    }

    @Override
    RecyclerView.ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language,parent,false);

        return new LanguageHolder(v);
    }

    @Override
    void onBindVH(RecyclerView.ViewHolder holder, int position) {
        LanguageHolder h = (LanguageHolder) holder;
        Language item = data.get(position);

        h.languageTextView.setText(item.getDesc());
        h.codeTextView.setText(item.getCode());
    }

    public class LanguageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_view)
        CardView cv;

        @BindView(R.id.main_text)
        TextView languageTextView;

        @BindView(R.id.addition_text)
        TextView codeTextView;


        LanguageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            if(onClickListener!=null){
                cv.setOnClickListener(v -> onClickListener.onClick(getItem(getLayoutPosition())));
            }


        }
    }

}
