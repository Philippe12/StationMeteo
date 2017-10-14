package com.philippefouquet.stationmeteo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by philippefouquet on 08/10/2017.
 */

public class MySQLite extends SQLiteOpenHelper {
    private static final String DATABASE_NAME_ESA = "/esa/db.sqlite";
    private static final String DATABASE_NAME_QEMU = "/storage/sdcard/db.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static MySQLite sInstance;

    public static synchronized MySQLite getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MySQLite(context, getDbName());
        }
        return sInstance;
    }


    private static boolean isGenymotionEmulator(String buildManufacturer) {
        return buildManufacturer != null &&
                (buildManufacturer.contains("Genymotion") || buildManufacturer.equals("unknown"));
    }

    private static boolean buildModelContainsEmulatorHints(String buildModel) {
        return buildModel.startsWith("sdk")
                || "google_sdk".equals(buildModel)
                || buildModel.contains("Emulator")
                || buildModel.contains("Android SDK");
    }

    public static String getDbName(){
        String db = DATABASE_NAME_ESA;
        if( isGenymotionEmulator(Build.MANUFACTURER) || buildModelContainsEmulatorHints(Build.MODEL) )
            db = DATABASE_NAME_QEMU;
        return db;
    }

    private MySQLite(Context context, String db) {

        super(context, db, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Création de la base de données
        // on exécute ici les requêtes de création des tables
        sqLiteDatabase.execSQL(THPManager.CREATE_TABLE_THP);
        sqLiteDatabase.execSQL(RoomManager.CREATE_TABLE_ROOM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // Mise à jour de la base de données
        // méthode appelée sur incrémentation de DATABASE_VERSION
        // on peut faire ce qu'on veut ici, comme recréer la base :
        onCreate(sqLiteDatabase);
    }
}
