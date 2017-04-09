package ru.belokonalexander.yta;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.belokonalexander.yta.Database.CacheModel;
import ru.belokonalexander.yta.Database.CacheModelDao;
import ru.belokonalexander.yta.GlobalShell.ApiChainRequestWrapper;
import ru.belokonalexander.yta.GlobalShell.Models.AllowedLanguages;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateResult;
import ru.belokonalexander.yta.GlobalShell.OnApiSuccessResponseListener;
import ru.belokonalexander.yta.GlobalShell.ServiceGenerator;
import ru.belokonalexander.yta.GlobalShell.Settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 08.04.2017.
 */

@RunWith(AndroidJUnit4.class)
public class ApiAndCachingTest {

    private List response;
    private int counter;

    @Before
    public void clearCommon(){
        response = null;
        counter = 0;
        //очищать перед каждым тестом кеш
        YtaApplication.getDaoSession().getCacheModelDao().deleteAll();
    }

    @Test
    public void is_Result() throws Exception {
        String hash = "1";
        final CountDownLatch signal = new CountDownLatch(1);

        ApiChainRequestWrapper request = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response  = result;
            signal.countDown();
        }, ServiceGenerator.getTranslateApi().translate("привет","ru-en"));

        request.execute();
        signal.await(5, TimeUnit.SECONDS);
        assertEquals(response.get(0).toString(), response.size(),1);
    }


    @Test
    public void is_ResultIncorrect() throws Exception {
        String hash = "1";
        final CountDownLatch signal = new CountDownLatch(1);

        ApiChainRequestWrapper request = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response  = result;
            signal.countDown();
        }, ServiceGenerator.getTranslateApi().translate("привет","rus-en"));

        request.execute();
        signal.await(5, TimeUnit.SECONDS);

        assertTrue(response.get(0).toString(), response.get(0) instanceof Throwable);
    }

    @Test
    public void is_ResultCorrect() throws Exception {
        String hash = "1";
        final CountDownLatch signal = new CountDownLatch(1);

        ApiChainRequestWrapper request = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response  = result;
            signal.countDown();
        }, ServiceGenerator.getTranslateApi().translate("привет","ru-en"));

        request.execute();
        signal.await(5, TimeUnit.SECONDS);

        assertTrue(response.get(0).toString(), !(response.get(0) instanceof Throwable));
    }

    @Test
    public void is_ReExecutedResultCorrect() throws Exception {
        String hash = "1";
        final CountDownLatch signal = new CountDownLatch(1);
        final CountDownLatch resignal = new CountDownLatch(1);

        response = new ArrayList<String>();

        ApiChainRequestWrapper request = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response.add(result);
            if(signal.getCount()==0)
                resignal.countDown();
            else signal.countDown();

        }, ServiceGenerator.getTranslateApi().translate("привет","ru-en"));

        request.execute();

        signal.await(5, TimeUnit.SECONDS);

        request.execute();

        resignal.await(5, TimeUnit.SECONDS);

        assertTrue(response.toString(), response.get(0).equals(response.get(1)));
    }

    @Test
    public void is_CancelableApiRequestCorrect() throws Exception {
        String hash = "1";
        final CountDownLatch signal = new CountDownLatch(1);

        response = new ArrayList<String>();

        ApiChainRequestWrapper request = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response = result;
            signal.countDown();
        }, ServiceGenerator.getTranslateApiWithoutCache().translate("привет","ru-en"));

        ApiChainRequestWrapper request2 = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response = result;
            signal.countDown();
        }, ServiceGenerator.getTranslateApiWithoutCache().translate("привет","rus-en"));


        request.execute();

        Thread.sleep(20);

        request2.execute();

        signal.await(5, TimeUnit.SECONDS);

        assertTrue(response.toString(), response.get(0) instanceof Throwable);
    }

    @Test
    public void is_CancelableApiRequestWithDifferentHashCorrect() throws Exception {
        String hash = "1";
        final CountDownLatch signal = new CountDownLatch(1);

        response = new ArrayList<String>();

        ApiChainRequestWrapper request = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response = result;
            signal.countDown();
        }, ServiceGenerator.getTranslateApiWithoutCache().translate("привет","ru-en"));

        ApiChainRequestWrapper request2 = ApiChainRequestWrapper.getApartInstance(hash+1, result -> {
            response = result;
            signal.countDown();
        }, ServiceGenerator.getTranslateApiWithoutCache().translate("привет","rus-en"));


        request.execute();

        Thread.sleep(20);

        request2.execute();

        signal.await(5, TimeUnit.SECONDS);

        assertFalse(response.toString(), response.get(0) instanceof Throwable);
    }

    @Test
    public void is_SequenceApiRequestCorrect() throws Exception {
        String hash = "1";
        final CountDownLatch signal = new CountDownLatch(1);

        response = new ArrayList<String>();

        ApiChainRequestWrapper request2 = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response.add(result.get(0));
            signal.countDown();
        }, ServiceGenerator.getTranslateApiWithoutCache().translate("привет","rus-en"));

        ApiChainRequestWrapper request = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response.add(result.get(0));
            request2.execute();

        }, ServiceGenerator.getTranslateApiWithoutCache().translate("привет","ru-en"));


        request.execute();

        signal.await(5, TimeUnit.SECONDS);

        assertTrue(response.toString(), !(response.get(0) instanceof Throwable) && response.get(1) instanceof Throwable);
    }


    @Test
    public void is_SavedToCacheCorrect() throws Exception {
        String hash = "1";
        final CountDownLatch signal = new CountDownLatch(1);

        response = new ArrayList<String>();

        ApiChainRequestWrapper request = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response.add(result.get(0));
            signal.countDown();

        }, ServiceGenerator.getTranslateApiWithoutCache().translate("привет","ru-en"));

        request.execute();

        signal.await(5, TimeUnit.SECONDS);

        CacheModel topCachedValue = YtaApplication.getDaoSession().getCacheModelDao().queryBuilder().orderDesc(CacheModelDao.Properties.UpdateDate).limit(1).unique();

        TranslateResult cachedResult = new GsonBuilder().create().fromJson(topCachedValue.getResponse(), TranslateResult.class);
        TranslateResult apiResult = (TranslateResult) response.get(0);

        assertEquals(apiResult + " / " + cachedResult, apiResult,cachedResult);
    }


    @Test
    public void is_FromCacheCorrect() throws Exception {
        String hash = "1";
        final CountDownLatch signal = new CountDownLatch(1);
        final CountDownLatch resignal = new CountDownLatch(1);

        response = new ArrayList<String>();

        ApiChainRequestWrapper request = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response.add(result.get(0));
            if(signal.getCount()==0)
                resignal.countDown();
            else signal.countDown();

        }, ServiceGenerator.getTranslateApi().translate("привет","ru-en"));

        request.execute();

        signal.await(5, TimeUnit.SECONDS);

        request.execute();

        resignal.await(20, TimeUnit.MILLISECONDS);


        assertEquals(response.toString(), response.get(0),response.get(1));
    }

    @Test
    public void is_FromCacheWithoutCacheCorrect() throws Exception {
        String hash = "1";
        final CountDownLatch signal = new CountDownLatch(1);
        final CountDownLatch resignal = new CountDownLatch(1);

        response = new ArrayList<String>();


        ApiChainRequestWrapper request = ApiChainRequestWrapper.getApartInstance(hash, result -> {
            response.add(result.get(0));
            if(signal.getCount()==0)
                resignal.countDown();
            else signal.countDown();

        }, ServiceGenerator.getTranslateApiWithoutCache().translate("привет","ru-en"));

        request.execute();

        signal.await(5, TimeUnit.SECONDS);

        request.execute();

        resignal.await(20, TimeUnit.MILLISECONDS);

        assertEquals(response.toString(), response.size(),1);
    }


    @Test
    public void is_CacheClearsCorrect() throws Exception {


        for(int i =0; i < Settings.CACHE_MAX_SIZE * 1.5; i++){
            CacheModel.saveInCache(new CacheModel(null,"tst","tst","item: " + i, new Date()));
        }

        int count = (int) YtaApplication.getDaoSession().getCacheModelDao().count();

        assertTrue(String.valueOf(Settings.CACHE_MAX_SIZE), count <  Settings.CACHE_MAX_SIZE);
    }






}
