package lk.bsc212.pdsa;

import android.app.Application;
import android.content.Context;

import lk.bsc212.pdsa.room.AppDatabase;
import lk.bsc212.pdsa.room.dao.MinimumConnectorDao;
import lk.bsc212.pdsa.room.dao.QueenPlaceDao;
import lk.bsc212.pdsa.room.dao.ShortestPathDao;
import lk.bsc212.pdsa.room.dao.UserDao;

public class MainApplication extends Application {

    private static MainApplication context;

    public static MainApplication getInstance() {
        return context;
    }


    //Database
    public static AppDatabase appDatabase;
    //DAOs
    public static QueenPlaceDao queenPlaceDao;
    public static UserDao userDao;
    public static ShortestPathDao shortestPathDao;
    public static MinimumConnectorDao minimumConnectorDao;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        appDatabase = AppDatabase.getDatabase(MainApplication.this);
        queenPlaceDao = appDatabase.queenPlaceDao();
        userDao = appDatabase.userDao();
        shortestPathDao = appDatabase.shortestPathDao();
        minimumConnectorDao = appDatabase.minimumConnectorDao();


    }

    public static Context getContext() {
        return context;
    }
}
