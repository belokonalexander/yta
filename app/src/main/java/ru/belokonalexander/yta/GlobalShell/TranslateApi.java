package ru.belokonalexander.yta.GlobalShell;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.belokonalexander.yta.GlobalShell.Models.AllowedLanguages;
import ru.belokonalexander.yta.GlobalShell.Models.TranslateResult;
import rx.Observable;

/**
 * Created by Alexander on 17.03.2017.
 */

public interface TranslateApi {
    @POST("getLangs")
    Observable<AllowedLanguages> getLangs(@Query("ui") String languageCode);

    @POST("translate")
    Observable<TranslateResult> translate(@Query("text") String text, @Query("lang") String direction);

}
