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
 * Created by Alexander on 16.03.2017.
 */

public class StaticHelpers {
    public static void LogThis(Object obj){
        Log.e("TAG", obj.toString());
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        } catch (NullPointerException e) {

            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) { // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        return true;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connected to the mobile provider's data plan
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

    public static String getParentHash(Class c, String additionalId)
    {
        String hash;
        try {
            hash = Thread.currentThread().getStackTrace()[3].getClassName() + "." + Thread.currentThread().getStackTrace()[3].getMethodName();
        } catch (Exception e)
        {
            hash = c.getCanonicalName();
        }
        //TODO add hash function
        return hash+additionalId;
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


}
