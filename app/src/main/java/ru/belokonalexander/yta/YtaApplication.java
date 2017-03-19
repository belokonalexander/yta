package ru.belokonalexander.yta;

import android.app.Application;
import android.content.Context;

import org.greenrobot.greendao.database.Database;

import java.io.IOException;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.belokonalexander.yta.Database.CacheModel;
import ru.belokonalexander.yta.Database.DaoMaster;
import ru.belokonalexander.yta.Database.DaoSession;
import ru.belokonalexander.yta.GlobalShell.DictionaryApi;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.GlobalShell.TranslateApi;


/**
 * Created by Alexander on 17.03.2017.
 */

public class YtaApplication extends Application {


    private static TranslateApi translateApi;
    private static DictionaryApi dictionaryApi;
    private static DaoSession daoSession;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

    }

    public static Context getAppContext() {
        return context;
    }

    public static TranslateApi getTranslateApi() {
        return translateApi;
    }

    public static DictionaryApi getDictionaryApi() {
        return dictionaryApi;
    }

    //добавляем ключик к каждому запросу


    public static DaoSession getDaoSession() {
        return daoSession;
    }


}
