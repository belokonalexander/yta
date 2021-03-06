package ru.belokonalexander.yta.Database;

import android.text.SpannableString;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import ru.belokonalexander.yta.Events.WordFavoriteStatusChangedEvent;
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
import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.Views.WordList;
import ru.belokonalexander.yta.YtaApplication;

import static ru.belokonalexander.yta.GlobalShell.StaticHelpers.getStringOrEmpty;
import static ru.belokonalexander.yta.GlobalShell.StaticHelpers.getStringOrEmptyBrackets;
import static ru.belokonalexander.yta.GlobalShell.StaticHelpers.getStringOrEmptyDelim;


/**
 *  описывает таблицу с историей и избранным
 *  является представлением объекта CompositeTranslateModel в базе данных
 */
@Entity(indexes = {@Index(value = "source,lang", unique = true)})
public class CompositeTranslateModel implements SearchEntity, Serializable{

    @Id
    private Long Id;

    @SearchField(lazySearch = true, alias = R.string.source_word, order = 1)
    @NotNull
    private String source;      //исходное значение

    @NotNull
    @Convert(converter = TranslateLanguageConverter.class, columnType = String.class)
    private TranslateLanguage lang;        //в виде en-ru

    @SearchField(lazySearch = true, alias = R.string.translate_word, order = 2)
    @NotNull
    private String translateResult;   //результат перевода

    @NotNull
    private Date updateDate;

    @NotNull
    private Date createDate;

    private Date saveFavoriteDate;

    private Date saveHistoryDate;

    private Boolean favorite;   //сохранено в избранном

    private Boolean history;    //хранится в истории

    @Convert(converter = LookupResultConverter.class, columnType = String.class)
    private LookupResult lookup;      //JSON формат с информацией словаря






    @Generated(hash = 1728432106)
    public CompositeTranslateModel() {
    }



    @Keep
    public CompositeTranslateModel(Long Id, @NotNull String source, @NotNull TranslateLanguage lang, @NotNull String translateResult, @NotNull Date updateDate, @NotNull Date createDate, Date saveFavoriteDate,
            Date saveHistoryDate, Boolean favorite, Boolean history, LookupResult lookup) {
        this.Id = Id;
        this.source = source.trim();
        this.lang = lang;
        this.translateResult = translateResult.trim();
        this.updateDate = updateDate;
        this.favorite = favorite;
        this.history = history;
        this.lookup = lookup;
        this.createDate = createDate;
        this.saveFavoriteDate = saveFavoriteDate;
        this.saveHistoryDate = saveHistoryDate;
    }



    public static CompositeTranslateModel getBySource(String source, TranslateLanguage translateLanguage) {
        return YtaApplication.getDaoSession().getCompositeTranslateModelDao().queryBuilder()
                .where(CompositeTranslateModelDao.Properties.Source.eq(source.trim()),CompositeTranslateModelDao.Properties.Lang.eq(translateLanguage.toString()))
                .unique();
    }

    public static CompositeTranslateModel copy(CompositeTranslateModel model){
        return new CompositeTranslateModel(null, model.getSource(), new TranslateLanguage(model.getLang().getLangFrom(),model.getLang().getLangFromDesc(),
                model.getLang().getLangTo(),model.getLang().getLangToDesc()), model.getTranslateResult(), model.getCreateDate(), model.getSaveFavoriteDate(), model.getSaveHistoryDate(), model.getUpdateDate(),model.getFavorite(), model.getHistory(),
                model.getLookup()
                );
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
        return this.favorite!=null && this.favorite;
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

    public void changeFavoriteStatus() {
        if(getFavorite())
            removeFromFavorite();
        else saveAsFavorite();

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

    public static CompositeTranslateModel getDummyInstance(){
        return new CompositeTranslateModel(null,"dummySource", TranslateLanguage.getDummyInsstance(), "dummyResult",null,null,null, null,false,false,null);

    }

    public static CompositeTranslateModel getDummyInstance(int ind){
        return new CompositeTranslateModel(null,"dummySource:" + ind, TranslateLanguage.getDummyInsstance(), "dummyResult:"+ind,new Date(),new Date(), new Date(), new Date(),true,true,null);

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
        saveFavoriteDate = new Date();
        saveInDB();
        EventBus.getDefault().post(new WordFavoriteStatusChangedEvent(this));
    }

    public void saveInDB(){

        if(this.createDate==null)
            createDate = new Date();

        this.updateDate = new Date();

        SimpleAsyncTask.run(new SimpleAsyncTask.InBackground() {
            @Override
            public Object doInBackground() {
                YtaApplication.getDaoSession().getCompositeTranslateModelDao().insertOrReplace(CompositeTranslateModel.this);
                showTable();
                return null;
            }
        });

    }


    public void showTable(){
        List<CompositeTranslateModel> data = YtaApplication.getDaoSession().getCompositeTranslateModelDao().queryBuilder().orderDesc(CompositeTranslateModelDao.Properties.UpdateDate).list();
        StaticHelpers.LogThisDB("----> elements - " + data.size());
        for(CompositeTranslateModel model : data){
            StaticHelpers.LogThisDB("\n-> " + model );
        }
        StaticHelpers.LogThisDB("---->");
    }

    public void removeFromFavorite() {
        favorite = false;
        saveFavoriteDate = null;
        if(history) {
            saveInDB();
        } else
            removeFromDB();
        EventBus.getDefault().post(new WordFavoriteStatusChangedEvent(this));
    }

    private void removeFromDB() {
        YtaApplication.getDaoSession().getCompositeTranslateModelDao().delete(this);
    }


    /**
     * преобразование Lookup результата в SpannableString
     * @param listener
     * @return
     */
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositeTranslateModel that = (CompositeTranslateModel) o;

        if (!source.equals(that.source)) return false;
        return lang.equals(that.lang);

    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + lang.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CompositeTranslateModel{" +
                "Id=" + Id +
                ", source='" + source + '\'' +
                ", lang=" + lang +
                ", translateResult='" + translateResult + '\'' +
                ", updateDate=" + updateDate +
                ", createDate=" + createDate +
                ", favorite=" + favorite +
                ", history=" + history +
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

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getSaveFavoriteDate() {
        return this.saveFavoriteDate;
    }

    public void setSaveFavoriteDate(Date saveFavoriteDate) {
        this.saveFavoriteDate = saveFavoriteDate;
    }

    public Date getSaveHistoryDate() {
        return this.saveHistoryDate;
    }

    public void setSaveHistoryDate(Date saveHistoryDate) {
        this.saveHistoryDate = saveHistoryDate;
    }





}

