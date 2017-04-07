package ru.belokonalexander.yta.Database;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Содержит извлеченный мета данные о поиске по объекту
 */

public class SearchItem {
    private String name;
    private Boolean isLazyType;
    private Boolean isFullContain;
    private String alias;
    private int order;

    public SearchItem(String name, Boolean isLazyType, Boolean isFullContain, String alias, int order) {
        this.name = name;
        this.isLazyType = isLazyType;
        this.isFullContain = isFullContain;
        this.alias = alias;
        this.order = order;
    }

    @Override
    public String toString() {
        return "SearchItem{" +
                "name='" + name + '\'' +
                ", isLazyType=" + isLazyType +
                ", isFullContain=" + isFullContain +
                ", alias='" + alias + '\'' +
                ", order=" + order +
                '}';
    }

    public int getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    public Boolean getLazyType() {
        return isLazyType;
    }

    public String getAlias(){
        return alias;
    }

    public Boolean getFullContain() {
        return isFullContain;
    }

    /**
     * получает список поисковых параметров из описания класса
     * @param home  класс, реализующий SearchEntity
     * @param context
     * @return
     */
    public static List<SearchItem> getSearchFieldsAndType(Class <? extends SearchEntity> home, Context context) {
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

    /**
     * отображение названия параметра поиска
     * @param alias
     * @param context
     * @return
     */
    private static String getLanguagedAlias(int alias, Context context) {
        return context.getResources().getString(alias);
    }

    /**
     * возвращает количество параметров поиска
     * @param itemType
     * @param <T>
     * @return
     */
    public static <T extends SearchEntity> int getSearchFieldsCount(Class<T> itemType) {
        int count = 0;
        for (java.lang.reflect.Field field : itemType.getDeclaredFields()) {
            if (field.isAnnotationPresent(SearchField.class)) {
                count++;
            }
        }

        return count;
    }
}
