package ru.belokonalexander.yta;

import android.app.Application;
import android.content.Context;

import org.greenrobot.greendao.database.Database;


import ru.belokonalexander.yta.Database.DaoMaster;
import ru.belokonalexander.yta.Database.DaoSession;




public class YtaApplication extends Application {


    private static DaoSession daoSession;

    //хранится Application контекст
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

    public static DaoSession getDaoSession() {
        return daoSession;
    }


}
