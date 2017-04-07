package ru.belokonalexander.yta.Database;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Calendar;
import java.util.Date;
import org.greenrobot.greendao.query.Query;

import ru.belokonalexander.yta.GlobalShell.Settings;
import ru.belokonalexander.yta.YtaApplication;

/**
 * Все запросы к API кешируются и хранятся в данной таблице
 */

@Entity
public class CacheModel {

    @Id
    private Long Id;

    @NotNull
    @Unique
    private String signature;

    @NotNull
    private String mediaType;

    @NotNull
    private String response;

    @NotNull
    Date updateDate;


    @Generated(hash = 260203277)
    public CacheModel(Long Id, @NotNull String signature, @NotNull String mediaType, @NotNull String response, @NotNull Date updateDate) {
        this.Id = Id;
        this.signature = signature;
        this.mediaType = mediaType;
        this.response = response;
        this.updateDate = updateDate;
    }


    @Generated(hash = 666297882)
    public CacheModel() {
    }


    @Override
    public String toString() {
        return "CacheModel{" +
                "Id=" + Id +
                ", signature='" + signature + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", response='" + response + '\'' +
                '}';
    }

    public Long getId() {
        return this.Id;
    }


    public void setId(Long Id) {
        this.Id = Id;
    }


    public String getSignature() {
        return this.signature;
    }


    public void setSignature(String signature) {
        this.signature = signature;
    }


    public String getResponse() {
        return this.response;
    }


    public void setResponse(String response) {
        this.response = response;
    }


    public String getMediaType() {
        return this.mediaType;
    }


    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    public static CacheModel getTopRow(String signature, CacheGetType type) {


        //добавляем доп.условие для выборки свежих данных
        if(type==CacheGetType.TIMER) {
            Calendar current = Calendar.getInstance();
            current.setTime(new Date());
            current.add(Calendar.SECOND, -Settings.CACHE_INTERVAL);
            Date currentDate = current.getTime();
            return ((YtaApplication)YtaApplication.getAppContext()).getDaoSession().getCacheModelDao().queryBuilder()
                    .where(CacheModelDao.Properties.Signature.eq(signature), (CacheModelDao.Properties.UpdateDate.gt(currentDate)))
                    .unique();
        }

        return ((YtaApplication)YtaApplication.getAppContext()).getDaoSession().getCacheModelDao().queryBuilder().where(CacheModelDao.Properties.Signature.eq(signature)).unique();
    }


    public Date getUpdateDate() {
        return this.updateDate;
    }


    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /*
        ANY - любая кешированная запись по сигнатуре,
        TIMER - свежая кешированная дата по сигнатуре (интервал определяется в Settings.class)
     */
    public enum CacheGetType{
        ANY, TIMER
    }
}
