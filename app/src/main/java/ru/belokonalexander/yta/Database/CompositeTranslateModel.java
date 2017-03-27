package ru.belokonalexander.yta.Database;

import android.os.AsyncTask;
import android.text.SpannableString;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.belokonalexander.yta.GlobalShell.Models.AllowedLanguages;
import ru.belokonalexander.yta.GlobalShell.Models.Language;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.Def;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.Ex;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.LookupResult;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.LookupStyledField;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.Mean;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.Syn;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.Tr;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateLanguage;
import ru.belokonalexander.yta.GlobalShell.SimpleAsyncTask;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.WordList;
import ru.belokonalexander.yta.YtaApplication;

import static ru.belokonalexander.yta.GlobalShell.StaticHelpers.getStringOrEmpty;
import static ru.belokonalexander.yta.GlobalShell.StaticHelpers.getStringOrEmptyBrackets;
import static ru.belokonalexander.yta.GlobalShell.StaticHelpers.getStringOrEmptyDelim;

/**
 * Created by Alexander on 26.03.2017.
 */


/**
 *  описывает таблицу с историей и избранным
 *  является представлением объекта CompositeTranslateModel в базе данных
 */
@Entity
public class CompositeTranslateModel {

    @Id
    private Long Id;

    @NotNull
    @Unique
    private String source;      //исходное значение

    @NotNull
    @Convert(converter = TranslateLanguageConverter.class, columnType = String.class)
    private TranslateLanguage lang;        //в виде en-ru

    @NotNull
    private String translateResult;   //результат перевода

    @NotNull
    Date updateDate;

    private Boolean favorite;   //сохранено в избранном

    private Boolean history;    //хранится в истории

    @Convert(converter = LookupResultConverter.class, columnType = String.class)
    private LookupResult lookup;      //JSON формат с информацией словаря





    @Generated(hash = 2100547212)
    public CompositeTranslateModel(Long Id, @NotNull String source, @NotNull TranslateLanguage lang, @NotNull String translateResult,
            @NotNull Date updateDate, Boolean favorite, Boolean history, LookupResult lookup) {
        this.Id = Id;
        this.source = source;
        this.lang = lang;
        this.translateResult = translateResult;
        this.updateDate = updateDate;
        this.favorite = favorite;
        this.history = history;
        this.lookup = lookup;
    }

    @Generated(hash = 1728432106)
    public CompositeTranslateModel() {
    }





    public void save(){
        saveInDB();
    }

    public Long getId() {
        return this.Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public TranslateLanguage getLang() {
        return this.lang;
    }

    public void setLang(TranslateLanguage lang) {
        this.lang = lang;
    }

    public String getTranslate() {
        return this.translateResult;
    }

    public void setTranslate(String translate) {
        this.translateResult = translate;
    }

    public Boolean getFavorite() {
        return this.favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public LookupResult getLookup() {
        return this.lookup;
    }

    public void setLookup(LookupResult lookup) {
        this.lookup = lookup;
    }



    public static class TranslateLanguageConverter implements PropertyConverter<TranslateLanguage, String> {

        @Override
        public TranslateLanguage convertToEntityProperty(String databaseValue) {
            return new TranslateLanguage(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(TranslateLanguage entityProperty) {
            return entityProperty.toString();
        }
    }

    public static class LookupResultConverter implements PropertyConverter<LookupResult, String> {

        @Override
        public LookupResult convertToEntityProperty(String databaseValue) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            return gson.fromJson(databaseValue, LookupResult.class);
        }

        @Override
        public String convertToDatabaseValue(LookupResult entityProperty) {
            return new GsonBuilder().create().toJson(entityProperty);
        }
    }

    /**
     * ответ есть, но он не результативный, т.е когда input==output
     */
    public boolean isUselessTranslate() {
        return  translateResult.trim().equals(source.trim());

    }


    public boolean lookupIsDummy() {
        return lookup==null || lookup.isEmpty();
    }


    public void saveAsFavorite() {
        favorite = true;
        saveInDB();

    }

    public void saveInDB(){

        SimpleAsyncTask.run(new SimpleAsyncTask.InBackground() {
            @Override
            public Object doInBackground() {
                YtaApplication.getDaoSession().getCompositeTranslateModelDao().insertOrReplace(CompositeTranslateModel.this);
                showDB();
                return null;
            }
        });

    }


    public void showDB(){
        List<CompositeTranslateModel> data = YtaApplication.getDaoSession().getCompositeTranslateModelDao().loadAll();
        StaticHelpers.LogThisDB("----> elements - " + data.size());
        for(CompositeTranslateModel model : data){
            StaticHelpers.LogThisDB("\n-> " + model );
        }
        StaticHelpers.LogThisDB("---->");
    }

    public void removeFromFavorite() {
        favorite = false;
        saveInDB();
    }




    public SpannableString getLookupString(WordList.OnWordClickListener listener) {

        /**
         * парсим json и формируем текстовое представление Lookup-результата
         * также фиксируем каждый элемент разметки и накладываем необходимые Span-стили
         */

        StringBuilder result = new StringBuilder();

        List<LookupStyledField> styled = new ArrayList<>();

        int defs = 0;

        for(Def def: lookup.getDef()){



            /**
             заголовок с информацией о слове
             */

            LookupStyledField about = new LookupStyledField(result, LookupStyledField.Type.ABOUT);

            if(defs>0) result.append("\n");

            result.append(getStringOrEmpty(def.getPos())).append(getStringOrEmptyDelim(def.getAnm())).append(getStringOrEmptyBrackets(def.getTs()))
                    .append("\n");

            about.setFinish(result);
            styled.add(about);

            /**
             примеры
             */
            int trnum = 1;

            if(def.getTr()!=null)
                for(Tr tr : def.getTr()){

                /*LookupStyledField num = new LookupStyledField(result, LookupStyledField.Type.NUM);
                result.append(trnum);
                num.setFinish(result);
                styled.add(num);*/

                    LookupStyledField samples = new LookupStyledField(result, LookupStyledField.Type.SYNONYMS_AREA);

                    LookupStyledField syn = new LookupStyledField(result, LookupStyledField.Type.SYNONYM);
                    result.append(tr.getText());
                    syn.setFinish(result);
                    styled.add(syn);

                    if(tr.getSyn()!=null)
                        for(int i =0; i < tr.getSyn().size(); i ++){
                            Syn other = tr.getSyn().get(i);
                            String text = getStringOrEmpty(other.getText());
                            if (text.trim().length()>0) {
                                result.append(", ");
                                LookupStyledField s = new LookupStyledField(result, LookupStyledField.Type.SYNONYM);
                                result.append(text);
                                s.setFinish(result);
                                styled.add(s);
                            }

                        }

                    samples.setFinish(result);
                    styled.add(samples);

                    result.append(" ");

                    /**
                     значения
                     */
                    if(tr.getMean()!=null) {

                        LookupStyledField means = new LookupStyledField(result, LookupStyledField.Type.MEAN);

                        for (int i = 0; i < tr.getMean().size(); i++) {
                            Mean mean = tr.getMean().get(i);



                            if (i == 0) {

                                result.append("(");
                            }

                            result.append(getStringOrEmptyDelim(mean.getText(), i));


                            if (i == tr.getMean().size() - 1) {
                                result.append(")");
                            }
                        }

                        means.setFinish(result);
                        styled.add(means);

                    }

                    result.append("\n");
                /*
                    примеры
                 */
                    if(tr.getEx()!=null) {
                        LookupStyledField example = new LookupStyledField(result, LookupStyledField.Type.EXAMPLE);
                        for (Ex ex : tr.getEx()) {
                            result.append("   ").append(ex.getText());

                            if(ex.getTr()!=null){
                                result.append(" - ");
                                for(int i = 0; i < ex.getTr().size(); i++){
                                    Tr exTr = ex.getTr().get(i);
                                    result.append(getStringOrEmptyDelim(exTr.getText(),i));
                                }
                            }

                            example.setFinish(result);
                            styled.add(example);

                            result.append("\n");
                        }

                    }

                    trnum++;
                }

            defs++;
        }


        return LookupStyledField.buildSpannableString(result.toString().trim(),styled, lang, listener);
    }

    public String getTranslateResult() {
        return this.translateResult;
    }

    public void setTranslateResult(String translateResult) {
        this.translateResult = translateResult;
    }

    @Override
    public String toString() {
        return "CompositeTranslateModel{" +
                "Id=" + Id +
                ", source='" + source + '\'' +
                ", lang=" + lang +
                ", translateResult='" + translateResult + '\'' +
                ", favorite=" + favorite +
                ", lookup=" + lookup +
                '}';
    }

    public Boolean getHistory() {
        return this.history;
    }

    public void setHistory(Boolean history) {
        this.history = history;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }





}

