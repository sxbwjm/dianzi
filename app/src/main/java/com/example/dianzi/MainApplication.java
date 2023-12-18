package com.example.dianzi;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dianzi.common.Config;
import com.example.dianzi.db.AppDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

        copyFiles();

        instance = this;
    }

    public File getImageFolder() {
        return getDir(Config.IMAGE_FOLDER, Context.MODE_PRIVATE);
    }

    public void copyFiles() {
        String destPath = getFilesDir() + "/tessdata/";
        File dstDir = new File(destPath);
        if(!dstDir.exists()) {
            dstDir.mkdir();
        }
        String srcFileName = "chi_sim.traineddata";
        try {
            InputStream input = getAssets().open("tessdata/" + srcFileName);
            OutputStream output = new FileOutputStream(new File(destPath + "/" + srcFileName));
            byte[] buffer = new byte[input.available()];
            int length;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
