package com.philippefouquet.stationmeteo.db;

/**
 * Created by philippefouquet on 07/10/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class THPManager {

    private static final String TABLE_NAME = "THP";
    public static final String KEY_DATE="date";
    public static final String KEY_ROOM="room";

    public static final String KEY_MOY="moy";
    public static final String KEY_MAX="max";
    public static final String KEY_MIN="min";

    public static final String KEY_HUMIDITY="humidity";
    public static final String KEY_TEMPERATURE="temperature";
    public static final String KEY_PRESSURE="PRESSURE";

    public static final String CREATE_TABLE_THP = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_DATE+" LONG NOT NULL," +
            " "+KEY_ROOM+" INTEGER NOT NULL," +
            " "+KEY_HUMIDITY+KEY_MOY+" DOUBLE," +
            " "+KEY_HUMIDITY+KEY_MIN+" DOUBLE," +
            " "+KEY_HUMIDITY+KEY_MAX+" DOUBLE," +
            " "+KEY_TEMPERATURE+KEY_MOY+" DOUBLE," +
            " "+KEY_TEMPERATURE+KEY_MIN+" DOUBLE," +
            " "+KEY_TEMPERATURE+KEY_MAX+" DOUBLE," +
            " "+KEY_PRESSURE+KEY_MOY+" DOUBLE," +
            " "+KEY_PRESSURE+KEY_MIN+" DOUBLE," +
            " "+KEY_PRESSURE+KEY_MAX+" DOUBLE," +
            " PRIMARY KEY("+KEY_DATE+","+KEY_ROOM+")"+
            ");";

    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    // Constructeur
    public THPManager(Context context) {

        maBaseSQLite = MySQLite.getInstance(context);
    }

    public void open()
    {
        //on ouvre la table en lecture/écriture
        db = maBaseSQLite.getWritableDatabase();
    }

    public void close()
    {
        //on ferme l'accès à la BDD
        db.close();
    }

    public long add(THP thp) {
        // Ajout d'un enregistrement dans la table

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, thp.getDate());
        values.put(KEY_ROOM, thp.getRoom());

        values.put(KEY_HUMIDITY+KEY_MOY, thp.getHumidityMoy());
        values.put(KEY_HUMIDITY+KEY_MIN, thp.getHumidityMin());
        values.put(KEY_HUMIDITY+KEY_MAX, thp.getHumidityMax());

        values.put(KEY_TEMPERATURE+KEY_MOY, thp.getTemperatureMoy());
        values.put(KEY_TEMPERATURE+KEY_MIN, thp.getTemperatureMin());
        values.put(KEY_TEMPERATURE+KEY_MAX, thp.getTemperatureMax());

        values.put(KEY_PRESSURE+KEY_MOY, thp.getPressureMoy());
        values.put(KEY_PRESSURE+KEY_MIN, thp.getPressureMin());
        values.put(KEY_PRESSURE+KEY_MAX, thp.getPressureMax());

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(TABLE_NAME,null,values);
    }

    public int modify(THP thp) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put(KEY_HUMIDITY+KEY_MOY, thp.getHumidityMoy());
        values.put(KEY_HUMIDITY+KEY_MIN, thp.getHumidityMin());
        values.put(KEY_HUMIDITY+KEY_MAX, thp.getHumidityMax());

        values.put(KEY_TEMPERATURE+KEY_MOY, thp.getTemperatureMoy());
        values.put(KEY_TEMPERATURE+KEY_MIN, thp.getTemperatureMin());
        values.put(KEY_TEMPERATURE+KEY_MAX, thp.getTemperatureMax());

        values.put(KEY_PRESSURE+KEY_MOY, thp.getPressureMoy());
        values.put(KEY_PRESSURE+KEY_MIN, thp.getPressureMin());
        values.put(KEY_PRESSURE+KEY_MAX, thp.getPressureMax());

        String where = KEY_DATE+" = ? AND "+KEY_ROOM+ "= ?";
        String[] whereArgs = {thp.getDate()+"", thp.getRoom()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int supress(THP thp) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = KEY_DATE+" = ? AND "+KEY_ROOM+ "= ?";
        String[] whereArgs = {thp.getDate()+"", thp.getRoom()+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    public THP get(long date, int room) {
        // Retourne l'animal dont l'id est passé en paramètre

        THP a=new THP();
        a.setDate(date);
        a.setRoom(room);

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_DATE+"="+date
                +" AND "+KEY_ROOM+"="+room, null);
        if (c.moveToFirst()) {
            a.setHumidityMoy(c.getDouble(c.getColumnIndex(KEY_HUMIDITY+KEY_MOY)));
            a.setHumidityMin(c.getDouble(c.getColumnIndex(KEY_HUMIDITY+KEY_MIN)));
            a.setHumidityMax(c.getDouble(c.getColumnIndex(KEY_HUMIDITY+KEY_MAX)));

            a.setTemperatureMoy(c.getDouble(c.getColumnIndex(KEY_TEMPERATURE+KEY_MOY)));
            a.setTemperatureMin(c.getDouble(c.getColumnIndex(KEY_TEMPERATURE+KEY_MIN)));
            a.setTemperatureMax(c.getDouble(c.getColumnIndex(KEY_TEMPERATURE+KEY_MAX)));

            a.setPressureMoy(c.getDouble(c.getColumnIndex(KEY_PRESSURE+KEY_MOY)));
            a.setPressureMin(c.getDouble(c.getColumnIndex(KEY_PRESSURE+KEY_MIN)));
            a.setPressureMax(c.getDouble(c.getColumnIndex(KEY_PRESSURE+KEY_MAX)));
            c.close();
            return a;
        }

        return null;
    }

    public Cursor get(int room) {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_ROOM+"="+room, null);
    }
}
