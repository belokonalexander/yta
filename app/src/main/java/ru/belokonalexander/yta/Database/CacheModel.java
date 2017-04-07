package ru.belokonalexander.yta.Database;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ru.belokonalexander.yta.GlobalShell.Settings;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
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

        CacheModel model;
        //добавляем доп.условие для выборки свежих данных
        if(type==CacheGetType.TIMER) {
            model = YtaApplication.getDaoSession().getCacheModelDao().queryBuilder()
                    .where(CacheModelDao.Properties.Signature.eq(signature), (CacheModelDao.Properties.UpdateDate.gt(getCacheTimeBorder()))).orderDesc(CacheModelDao.Properties.UpdateDate)
                    .unique();


        } else model = YtaApplication.getDaoSession().getCacheModelDao().queryBuilder().where(CacheModelDao.Properties.Signature.eq(signature)).orderDesc(CacheModelDao.Properties.UpdateDate).unique();

        if(model!=null) {
            model.setUpdateDate(new Date());
            YtaApplication.getDaoSession().getCacheModelDao().save(model);
        }


        return model;
    }

    /**
     * удаляет все неактуальные данные или сокращает размер записей в кеше
     */
    public static void clearCache(){

        YtaApplication.getDaoSession().getCacheModelDao().queryBuilder()
                .where(CacheModelDao.Properties.UpdateDate.lt(getCacheTimeBorder()))
                .buildDelete().executeDeleteWithoutDetachingEntities();

        //сокращаем размер записей
        int count = (int) YtaApplication.getDaoSession().getCacheModelDao().count();
        if(count>Settings.CACHE_MAX_SIZE){
            List<CacheModel> deletedModels = YtaApplication.getDaoSession().getCacheModelDao().queryBuilder().orderAsc(CacheModelDao.Properties.UpdateDate).limit(count-Settings.CACHE_MAX_SIZE +Settings.CACHE_MAX_SIZE /2 ).list();
            YtaApplication.getDaoSession().getCacheModelDao().deleteInTx(deletedModels);
        }

    }

    public static boolean saveInCache(CacheModel model){

        YtaApplication.getDaoSession().getCacheModelDao().insertOrReplace(model);
        if(new Random().nextInt(Settings.CACHE_DELETE_PROB)==0){
            clearCache();
            return true;
        }
        return false;
    }

    public static Date getCacheTimeBorder(){
        Calendar current = Calendar.getInstance();
        current.setTime(new Date());
        current.add(Calendar.SECOND, -Settings.CACHE_INTERVAL);
        return current.getTime();
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public static void print(String title){

        StaticHelpers.LogThisDB(title + ": \n");

        for(CacheModel item : YtaApplication.getDaoSession().getCacheModelDao().queryBuilder().orderAsc(CacheModelDao.Properties.UpdateDate).list()){

            StaticHelpers.LogThisDB("---> " + item);
        }
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
