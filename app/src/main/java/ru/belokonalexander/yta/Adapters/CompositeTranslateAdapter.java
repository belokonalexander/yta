package ru.belokonalexander.yta.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.belokonalexander.yta.Database.CompositeTranslateModel;
import ru.belokonalexander.yta.R;

/**
 * Created by Alexander on 27.03.2017.
 */

public class CompositeTranslateAdapter extends CommonAdapter<CompositeTranslateModel> {
    @Override
    RecyclerView.ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_composite_translate,parent,false);

        return new CompositeTranslateHolder(v);
    }

    @Override
    void onBindVH(RecyclerView.ViewHolder holder, int position) {
        CompositeTranslateHolder h = (CompositeTranslateHolder) holder;
        CompositeTranslateModel item = data.get(position);

        h.translateText.setText(item.getTranslate());
        h.sourceText.setText(item.getSource());
        h.languageText.setText(item.getLang().toString().toUpperCase());
    }

    public class CompositeTranslateHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_view)
        CardView cv;

        @BindView(R.id.source_text)
        TextView sourceText;

        @BindView(R.id.translate_text)
        TextView translateText;

        @BindView(R.id.language_text)
        TextView languageText;

        @BindView(R.id.save_word)
        ImageButton saveWordButton;

        CompositeTranslateHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            if(onClickListener!=null){
                cv.setOnClickListener(v -> onClickListener.onClick(getItem(getLayoutPosition())));
            }


        }
    }
}
