package com.philippefouquet.stationmeteo.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by philippefouquet on 10/10/2017.
 */

public class RoomManager {
    private static final String TABLE_NAME = "Room";
    public static final String KEY_ID="id";
    public static final String KEY_NAME="name";
    public static final String KEY_CAPTOR="captor";

    public static final String CREATE_TABLE_ROOM = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID+" INTEGER primary key," +
            " "+KEY_NAME+" STRING" +
            ");";

    public static final String UPDATE_TABLE_V2_TO_V3 = "ALTER TABLE "+TABLE_NAME+
            " ADD" +
            " "+KEY_CAPTOR+" STRING" +
            ";";

    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    // Constructeur
    public RoomManager(Context context) {

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

    public long add(Room room) {
        // Ajout d'un enregistrement dans la table

        ContentValues values = new ContentValues();
        values.put(KEY_ID, room.getId());
        values.put(KEY_NAME, room.getName());
        values.put(KEY_CAPTOR, room.getCapteur());

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(TABLE_NAME,null,values);
    }

    public int modify(Room room) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, room.getName());
        values.put(KEY_CAPTOR, room.getCapteur());

        String where = KEY_ID+" = ?";
        String[] whereArgs = {room.getId()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int supress(Room room) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = KEY_ID+" = ?";
        String[] whereArgs = {room.getId()+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    public Room get(int id) {
        // Retourne l'animal dont l'id est passé en paramètre

        Room a=new Room();
        a.setId(id);

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_ID+"="+id, null);
        if (c.moveToFirst()) {
            a.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            a.setCapteur(c.getString(c.getColumnIndex(KEY_CAPTOR)));
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
