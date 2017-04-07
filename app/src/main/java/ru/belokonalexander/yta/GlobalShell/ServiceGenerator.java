package ru.belokonalexander.yta.GlobalShell;

import java.io.IOException;
import java.net.UnknownHostException;
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
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.belokonalexander.yta.Database.CacheModel;
import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.YtaApplication;

/**
 * Генерирует сервис для API запросов, т.к используются 2 url'а: для переводчика и для словаря
 */

public class ServiceGenerator {


    private ServiceGenerator() {
    }


    public static <T> T getService(Class<T> service, boolean cache){

        String[] meta = (service == TranslateApi.class) ? YtaApplication.getAppContext().getResources().getStringArray(R.array.translate_api) :
                                                       YtaApplication.getAppContext().getResources().getStringArray(R.array.dictionary_api);

        return createService(service, cache, meta);
    }

    public static TranslateApi getTranslateApi(){
        return getService(TranslateApi.class, true);
    }

    public static DictionaryApi getDictionaryApi(){
        return getService(DictionaryApi.class, true);
    }

    public static TranslateApi getTranslateApiWithoutCache(){
        return getService(TranslateApi.class, false);
    }

    public static DictionaryApi getDictionaryApiWithoutCache(){
        return getService(DictionaryApi.class, false);
    }



    private static <T> T createService(Class<T> service, boolean cache,String[] meta) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(meta[0])
                .client(getBaseInterceptor(meta[1],cache))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(service);
    }




    private static OkHttpClient getBaseInterceptor(String key, boolean cache) {
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("key", key)
                    .build();

            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        return httpClient.addInterceptor(getResponseCacheInterceptor(cache)).build();
    }

    private static Interceptor getResponseCacheInterceptor(boolean cache){
        return new Interceptor(){
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();
                String signature = request.url().newBuilder().removeAllQueryParameters("key").toString(); //signature - ключ в таблице кеширования, с удаленным параметром api-key

                //проверяем, есть ли интернет соединение и разрешено ли брать записи из кеша при ошибке
                if(!StaticHelpers.isNetworkAvailable(YtaApplication.getAppContext()) && cache){

                    //если нет соединения, то берем последнюю запись из кэша по сигнатуре и подменяем запрос
                    CacheModel cacheModel = CacheModel.getTopRow(signature, CacheModel.CacheGetType.ANY);
                    if(cacheModel==null)
                        throw new UnknownHostException("Unable to resolve host \""+request.url().host()+"\"");
                    else {
                        //можно предупредить пользователя, что соединения нет и берется запись из кеша
                    }

                    return getCachedRow(request, cacheModel);
                }

                //соединение есть, но вместо запроса мы можем подменить ответ записью из кеша
                //ищем запись
                if(cache) {
                    CacheModel cacheMode = CacheModel.getTopRow(signature, CacheModel.CacheGetType.TIMER);
                    if (cacheMode != null) {
                        return getCachedRow(request, cacheMode);
                    }
                }

                //выполняю реальный запрос к api
                Response response = null;

                response = chain.proceed(request);


                ResponseBody responseBody = response.body();
                String responseBodyString= responseBody.string(); //ответ для кеширования


                //кеширую результат
                if(response.code()==200) {
                    CacheModel cacheModel = new CacheModel(null, signature, responseBody.contentType().toString(), responseBodyString, new Date());
                    CacheModel.saveInCache(cacheModel);

                }

                //создадаю новый response для отправки обработчику
                return response.newBuilder().body(ResponseBody.create(responseBody.contentType(), responseBodyString.getBytes())).build();
            }
        };
    }

    /*
        метод генерирует ответ на основе записи из кэша
    */
    private static Response getCachedRow(Request request, CacheModel cacheModel) {
        return new Response.Builder().body(ResponseBody.create( MediaType.parse(cacheModel.getMediaType()),cacheModel.getResponse() ))
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .build();
    }
}
