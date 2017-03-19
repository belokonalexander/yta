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
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.GlobalShell.TranslateApi;


/**
 * Created by Alexander on 17.03.2017.
 */

public class YtaApplication extends Application {


    private static TranslateApi translateApi;
    private DaoSession daoSession;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.translate_base_url))
                .client(getBaseInterceptor())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        translateApi = retrofit.create(TranslateApi.class);



    }

    public static Context getAppContext() {
        return context;
    }

    public static TranslateApi getTranslateApi() {
        return translateApi;
    }

    //добавляем ключик к каждому запросу
    private OkHttpClient getBaseInterceptor() {
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("key", getString(R.string.translate_api_key))
                    .build();

            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        return httpClient.addInterceptor(getResponseCacheInterceptor()).build();
    }

    private Interceptor getResponseCacheInterceptor(){
        return new Interceptor(){
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                String signature = request.url().newBuilder().removeAllQueryParameters("key").toString(); //signature - ключ в таблице кеширования, с удаленным параметром api-key

                //проверяем, есть ли интернет соединение
                if(!StaticHelpers.isNetworkAvailable(context)){
                    //если нет соединения, то берем последнюю запись из кэша по сигнатуре и подменяем запрос
                    //TODO предупредить пользователя, что соединения нет
                    CacheModel cacheModel = CacheModel.getTopRow(signature, CacheModel.CacheGetType.ANY);

                    if(cacheModel==null)
                        throw new NullPointerException("No data in cache. Connection is failed");

                    return getCachedRow(request, cacheModel);
                }

                //соединение есть, но вместо запроса мы можем подменить ответ записью из кеша
                //ищем запись
                CacheModel cacheMode = CacheModel.getTopRow(signature, CacheModel.CacheGetType.TIMER);
                if(cacheMode!=null) {
                    StaticHelpers.LogThis("КЕШИРОВАННАЯ ЗАПИСЬ");
                    return getCachedRow(request, cacheMode);
                }

                //выполняем запрос
                Response response = chain.proceed(request);

                StaticHelpers.LogThis("ЗАПРОС!");

                ResponseBody responseBody = response.body();
                String responseBodyString= responseBody.string(); //ответ для кеширования


                //кеширую результат
                CacheModel cacheModel = new CacheModel(null,signature, responseBody.contentType().toString(),responseBodyString, new Date());
                daoSession.getCacheModelDao().insertOrReplace(cacheModel);

                //создадаю новый responce для отправки обработчику
                return response.newBuilder().body(ResponseBody.create(responseBody.contentType(), responseBodyString.getBytes())).build();
            }
        };
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Response getCachedRow(Request request, CacheModel cacheModel) {
        return new Response.Builder().body(ResponseBody.create( MediaType.parse(cacheModel.getMediaType()),cacheModel.getResponse() ))
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .build();
    }
}
