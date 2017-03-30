package ru.belokonalexander.yta.Database;

/**
 * Created by Alexander on 30.03.2017.
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
}
