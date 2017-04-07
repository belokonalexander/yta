package ru.belokonalexander.yta.Views.Recyclers.DataProviders;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.belokonalexander.yta.Database.SearchEntity;
import ru.belokonalexander.yta.Database.SearchField;
import ru.belokonalexander.yta.Database.SearchItem;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;

/**
 * элемент для страничного поиска + поиска по фильтру
 */
public class SearchInputData extends PaginationSlider {

    private String key;
    private String value;
    private boolean isFullContains;
    //private List<SearchItem> params;

    public String getSearchCondition(){
        String c = "1=1";
        String fc = isFullContains ? "" : "%";
        if(!value.isEmpty()){
            c = StaticHelpers.camelCaseToUnderscore(key) + " LIKE '" + value + fc + "'";
        }

        return c;
    }

    public boolean isEmpty(){
        return value.isEmpty();
    }

    public SearchInputData(int pageSize) {
        super(pageSize);
        this.value = "";
        this.key = "";
        isFullContains = false;
    }

    public boolean isFullContains() {
        return isFullContains;
    }

    public void setFullContains(boolean fullContains) {
        isFullContains = fullContains;
    }

    public SearchInputData(){
        this(0);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static List<SearchItem> getSearchItems(Class <? extends SearchEntity> home, Context context) {
        List<SearchItem> result = new ArrayList<>();
        for (java.lang.reflect.Field field : home.getDeclaredFields()) {
            if (field.isAnnotationPresent(SearchField.class)) {
                final SearchField annotation = field.getAnnotation(SearchField.class);
                result.add(new SearchItem(field.getName(),annotation.lazySearch(), annotation.fullContains(), getLanguagedAlias(annotation.alias(), context),annotation.order()));
            }
        }

        Collections.sort(result,new Comparator<SearchItem>() {
            @Override
            public int compare(SearchItem o1, SearchItem o2) {
                return  (o1.getOrder() > o2.getOrder()) ?  1 :
                        (o1.getOrder() < o2.getOrder()) ? -1 : 0;
            }
        });

        return result;
    }


    private static String getLanguagedAlias(int alias, Context context) {
        return context.getResources().getString(alias);
    }

    @Override
    public String toString() {
        return "SearchInputData{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
