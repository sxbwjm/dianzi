package com.example.dianzi;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dianzi.common.Config;
import com.example.dianzi.db.AppDatabase;

import java.io.File;

public class MainApplication extends Application {
    public static MainApplication instance;
    private static AppDatabase db;
    @Override
    public void onCreate() {
        super.onCreate();
        File folder = getDir(Config.IMAGE_FOLDER, Context.MODE_PRIVATE);
        if(!folder.exists()){
            folder.mkdir();
        }

        instance = this;
    }

    public File getImageFolder() {
        return getDir(Config.IMAGE_FOLDER, Context.MODE_PRIVATE);
    }

    public AppDatabase getDB() {
        if (db == null) {
            RoomDatabase.Builder<AppDatabase> dbBuilder = Room.databaseBuilder(this, AppDatabase.class, Config.DB_NAME);
//            dbBuilder.setQueryCallback(new RoomDatabase.QueryCallback() {
//                @Override
//                public void onQuery(@NonNull String s, @NonNull List<?> list) {
//                    System.out.println("sql:" + s);
//                    System.out.println("param:" + list);
//
//                }
//            }, Executors.newSingleThreadExecutor());
           db = dbBuilder.allowMainThreadQueries().fallbackToDestructiveMigration().build();
           // db = dbBuilder.build();
        }
      //  this.getDataDir()
        return db;
    }

}
