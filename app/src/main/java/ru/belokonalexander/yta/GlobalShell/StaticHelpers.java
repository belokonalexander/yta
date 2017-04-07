package ru.belokonalexander.yta.GlobalShell;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.belokonalexander.yta.R;
import ru.belokonalexander.yta.YtaApplication;

/**
 * Библиотека статических методов
 */

public class StaticHelpers {
    public static void LogThis(Object obj){
        Log.e("TAG", obj.toString());
    }

    public static void LogThisFt(Object obj){
        Log.e("FT", obj.toString());
    }

    public static void LogThisDB(Object obj){
        Log.e("DB", obj.toString());
    }


    /**
     * есть ли доступ к сети
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        } catch (NullPointerException e) {

            //на некоторых устройствах возникает исключение в getState()
            //ниже описанный метод исправляет такое поведение
            //однако не тестировал второй способ отдельно от первого и не могу пока оставить только его

            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) { // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        return true;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return true;
                    }
                } else {
                    return false;
                }
            } catch (Exception e1) {
                return false;
            }
        }
        return false;
    }

    public static String getParentHash(Class c)
    {
        String hash;
        try {
            hash = Thread.currentThread().getStackTrace()[3].getClassName() + "." + Thread.currentThread().getStackTrace()[3].getMethodName();
        } catch (Exception e)
        {
            hash = c.getCanonicalName();
        }
        return hash;
    }




    public static String loadStringFromRawResource(int resId) {
        InputStream rawResource = YtaApplication.getAppContext().getResources().openRawResource(resId);
        String content = streamToString(rawResource);
        try {
            rawResource.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    private static String streamToString(InputStream in) {
        String l;
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder s = new StringBuilder();
        try {
            while ((l = r.readLine()) != null) {
                s.append(l + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    }

    public static int dpToPixels(float dp) {
        return (int) (dp * YtaApplication.getAppContext().getResources().getDisplayMetrics().density);
    }

    public static float pixelsToDp(int px) {
        return (px/ YtaApplication.getAppContext().getResources().getDisplayMetrics().density);
    }



    public static String getStringOrEmptyDelim(String string){
        return (string==null) ? "" : ", " + string;
    }

    public static String getStringOrEmptyDelim(String string, int pos){
        return (string==null) ? "" : (pos==0) ? string : ", " + string;
    }

    public static String getStringOrEmptyBrackets(String string){
        return (string==null) ? "" : " [" + string + "]";
    }

    public static String getStringOrEmpty(String string){
        return (string==null) ? "" : string;
    }

    public static String camelCaseToUnderscore(String string){
        return string.replaceAll("(.)(\\p{Upper})", "$1_$2").toUpperCase();
    }


}
