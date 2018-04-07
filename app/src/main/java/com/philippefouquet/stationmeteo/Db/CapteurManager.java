package com.philippefouquet.stationmeteo.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by philippefouquet on 10/10/2017.
 */

public class CapteurManager {
    private static final String TABLE_NAME = "Capteur";
    public static final String KEY_ID="id";
    public static final String KEY_ROOM="room";

    public static final String CREATE_TABLE_CAPTEUR = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID+" STRING primary key," +
            " "+KEY_ROOM+" INTEGER" +
            ");";

    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    // Constructeur
    public CapteurManager(Context context) {

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

    public long add(Capteur capteur) {
        // Ajout d'un enregistrement dans la table

        ContentValues values = new ContentValues();
        values.put(KEY_ID, capteur.getId());
        values.put(KEY_ROOM, capteur.getRoom());

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(TABLE_NAME,null,values);
    }

    public int modify(Capteur capteur) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put(KEY_ROOM, capteur.getRoom());

        String where = KEY_ID+" = ?";
        String[] whereArgs = {capteur.getId()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int supress(Capteur capteur) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = KEY_ID+" = ?";
        String[] whereArgs = {capteur.getId()+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    public Capteur get(String id) {
        // Retourne l'animal dont l'id est passé en paramètre

        Capteur a=new Capteur();
        a.setId(id);

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_ID+"="+id, null);
        if (c.moveToFirst()) {
            a.setRoom(c.getInt(c.getColumnIndex(KEY_ROOM)));
            c.close();
            return a;
        }

        return null;
    }

    public Capteur getForRoom(int id) {
        // Retourne l'animal dont l'id est passé en paramètre

        Capteur a=new Capteur();

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_ROOM+"="+id, null);
        if (c.moveToFirst()) {
            a.setId(c.getString(c.getColumnIndex(KEY_ID)));
            a.setRoom(c.getInt(c.getColumnIndex(KEY_ROOM)));
            c.close();
            return a;
        }

        return null;
    }

    public Cursor get() {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
    }
}
