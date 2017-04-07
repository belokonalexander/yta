package ru.belokonalexander.yta.GlobalShell;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.belokonalexander.yta.GlobalShell.Models.Lookup.LookupResult;



public interface DictionaryApi {

    @POST("lookup")
    Observable<LookupResult> lookup(@Query("text") String text, @Query("lang") String direction);

}
