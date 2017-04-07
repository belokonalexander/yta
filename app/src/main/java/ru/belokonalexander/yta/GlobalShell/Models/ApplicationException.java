package ru.belokonalexander.yta.GlobalShell.Models;

import android.util.SparseArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import retrofit2.HttpException;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.YtaApplication;



/**
 * враппер для ошибок приложения
 */
public class ApplicationException extends Exception {

    private String detailMessage;

    /**
     * в данном случае, обрабатываются ошибки только оп api-переводчика
     */
    public static final HashMap<Integer,String> errorDescription = new HashMap<>();
    static {
        int[] keys = YtaApplication.getAppContext().getResources().getIntArray(R.array.api_error_codes);
        String[] description = YtaApplication.getAppContext().getResources().getStringArray(R.array.api_error_description);

        for(int i =0; i < keys.length; i++)
            errorDescription.put(keys[i],description[i]);

    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    public ApplicationException(Throwable error){

        //значение по умолчанию
        detailMessage = YtaApplication.getAppContext().getString(R.string.unknown_error);

        if(error instanceof UnknownHostException){
            //ошибка соединения
            detailMessage = YtaApplication.getAppContext().getString(R.string.connection_error);
        } else if(error instanceof HttpException) {

            //ошибка Api
            try {
                JSONObject errorAnswer = new JSONObject(((HttpException)error).response().errorBody().string());
                String msg = errorDescription.get(errorAnswer.getInt("code"));
                if(msg!=null)
                    detailMessage = msg;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        StaticHelpers.LogThisFt("ошибка: " + error);
        error.printStackTrace();
    }

}
