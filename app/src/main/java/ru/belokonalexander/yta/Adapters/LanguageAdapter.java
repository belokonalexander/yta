package ru.belokonalexander.yta.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.belokonalexander.yta.GlobalShell.Models.Language;

import ru.belokonalexander.yta.R;

/**
 * Created by Alexander on 24.03.2017.
 */

public class LanguageAdapter extends CommonAdapter<Language> {



    @Override
    RecyclerView.ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language,parent,false);

        return null;
    }

    @Override
    void onBindVH(RecyclerView.ViewHolder holder, int position) {
        LanguageHolder h = (LanguageHolder) holder;
        Language item = data.get(position);

        h.languageTextView.setText(item.getDesc());
        h.codeTextView.setText(item.getCode());
    }

    private class LanguageHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView languageTextView;
        TextView codeTextView;


        LanguageHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.item_view);
            if(onClickListener!=null){
                cv.setOnClickListener(v -> onClickListener.onClick(getItem(getLayoutPosition())));
            }

        }
    }

}
