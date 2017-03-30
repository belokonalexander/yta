package ru.belokonalexander.yta.Views.Recyclers.DataProviders;

/**
 * Created by Alexander on 28.03.2017.
 */

public class PaginationSlider {

    protected int pageSize;
    protected int offset;

    public void nextStep(){
        offset+=pageSize;
    }


    public int getPageSize() {


        return pageSize;
    }

    public Integer getOffset() {
        return offset;
    }


    public PaginationSlider(){
        pageSize = 100;
        offset = 0;
    }

    public PaginationSlider setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public PaginationSlider setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public PaginationSlider(int pageSize, int offset) {
        this.pageSize = pageSize;
        this.offset = offset;
    }

    public PaginationSlider(int pageSize) {
        this.pageSize = pageSize;
        this.offset=0;
    }

    public void reset(){
        offset=0;
    }

    @Override
    public String toString() {
        return "PaginationSlider{" +
                "pageSize=" + pageSize +
                ", offset=" + offset +
                '}';
    }

    public void addOffset(int val){
        offset+=val;
    }


}
