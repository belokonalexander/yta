package ru.belokonalexander.yta.GlobalShell;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.LookupResult;


/**
 * Created by Alexander on 17.03.2017.
 */

public interface DictionaryApi {

    @POST("lookup")
    Observable<LookupResult> lookup(@Query("text") String text, @Query("lang") String direction);

}
