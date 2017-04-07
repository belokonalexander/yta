package ru.belokonalexander.yta.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
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
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;

/**
 * Адаптер для CompositeTranslate элементов
 */

public class CompositeTranslateAdapter extends CommonAdapter<CompositeTranslateModel> {

    public CompositeTranslateAdapter(Context context) {
        super(context);
    }

    public CompositeTranslateAdapter(Context context, Decoration decoration){
        super(context,decoration);
    }

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



        if(item.getFavorite()){
            h.saveWordButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
            h.saveWordButton.setColorFilter(context.getResources().getColor(R.color.tint_color_active), PorterDuff.Mode.SRC_IN);
        } else {
            h.saveWordButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_white_24dp));
            h.saveWordButton.setColorFilter(context.getResources().getColor(R.color.tint_color_accent), PorterDuff.Mode.SRC_IN);
        }

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

            saveWordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItem(getLayoutPosition()).changeFavoriteStatus();
                }
            });
        }
    }
}
