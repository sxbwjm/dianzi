package com.example.dianzi;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.ajts.androidmads.library.SQLiteToExcel;
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

    public void exportToExcel() {
        System.out.println("export entered");
        String path = getDataDir().getPath();
        SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(this, Config.DB_NAME, path);
        sqLiteToExcel.exportAllTables("tables.xls", new SQLiteToExcel.ExportListener() {

            @Override
            public void onStart() {
                System.out.println("export start:" );
            }

            @Override
            public void onCompleted(String filePath) {
                System.out.println("export done:" + filePath);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void importFromExcel(String path) {
        System.out.println("export entered");

        ExcelToSQLite excelToSQLite = new ExcelToSQLite(this, Config.DB_NAME, true);
        excelToSQLite.importFromFile( getDataDir().getPath() + "/tables.xls", new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {
                System.out.println("import start:" );
            }

            @Override
            public void onCompleted(String filePath) {
                System.out.println("import done:" + filePath);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
