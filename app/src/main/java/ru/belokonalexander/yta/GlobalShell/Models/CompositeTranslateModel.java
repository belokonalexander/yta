package ru.belokonalexander.yta.GlobalShell.Models;

import android.text.SpannableString;

import java.util.ArrayList;
import java.util.List;

import ru.belokonalexander.yta.GlobalShell.Models.Lookup.Def;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.Ex;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.LookupResult;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.LookupStyledField;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.Mean;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.Syn;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.Tr;
import ru.belokonalexander.yta.Views.WordList;

/**
 * Created by Alexander on 21.03.2017.
 */

public class CompositeTranslateModel {

    private TranslateResult translateResult;
    private LookupResult lookupResult;
    private String source;
    private TranslateLanguage language;

    public CompositeTranslateModel(Object translateResult, Object lookupResult, String source) {

        this.source = source;

        if(translateResult instanceof TranslateResult) {
            TranslateResult tr = (TranslateResult) translateResult;
            this.translateResult = tr;
            language = new TranslateLanguage(tr.getLang());

            if(lookupResult instanceof LookupResult)
                this.lookupResult = (LookupResult) lookupResult;

        }

    }

    /**
     * результат пришел, но он не результативный, т.е когда input==output, но он есть
     */
    public boolean isUselessTranslate() {
        return  translateResult!=null &&  translateResult.getText().get(0).trim().equals(source.trim())
                && lookupIsDummy();

    }




    public TranslateLanguage getLanguage() {
        return language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public TranslateResult getTranslateResult() {
        return translateResult;
    }


    public LookupResult getLookupResult() {
        return lookupResult;
    }

    public void setTranslateResult(TranslateResult translateResult) {
        this.translateResult = translateResult;
    }

    public void setLookupResult(LookupResult lookupResult) {
        this.lookupResult = lookupResult;
    }


    /*
         формируем из объекта Lookup единую spannable строку и передаем слушателя на клик
     */
    public SpannableString getLookupString(WordList.OnWordClickListener listener) {

        /**
         * парсим json и формируем текстовое представление Lookup-результата
         * также фиксируем каждый элемент разметки и накладываем необходимые Span-стили
         */

        StringBuilder result = new StringBuilder();

        List<LookupStyledField> styled = new ArrayList<>();

        int defs = 0;

        for(Def def: lookupResult.getDef()){



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


        return LookupStyledField.buildSpannableString(result.toString().trim(),styled, language, listener);
    }

    private String getStringOrEmpty(String string){
        return (string==null) ? "" : string;
    }

    private String getStringOrEmptyDelim(String string){
        return (string==null) ? "" : ", " + string;
    }

    private String getStringOrEmptyDelim(String string, int pos){
        return (string==null) ? "" : (pos==0) ? string : ", " + string;
    }

    private String getStringOrEmptyBrackets(String string){
        return (string==null) ? "" : " [" + string + "]";
    }


    public boolean isDummy() {
        return translateResult==null || translateResult.isEmpty();
    }

    public boolean lookupIsDummy() {
        return lookupResult==null || lookupResult.isEmpty();
    }
}
