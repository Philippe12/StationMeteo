package com.philippefouquet.stationmeteo.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.philippefouquet.stationmeteo.Db.MySQLite;

/**
 * Created by philippefouquet on 10/10/2017.
 */

public class ConfigManager {
    private static final String TABLE_NAME = "Config";
    public static final String KEY_NAME="name";
    public static final String KEY_VALUE="value";

    public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_NAME+" STRING primary key," +
            " "+KEY_VALUE+" STRING" +
            ");";

    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    // Constructeur
    public ConfigManager(Context context) {

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

    public long add(Config config) {
        // Ajout d'un enregistrement dans la table

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, config.getName());
        values.put(KEY_VALUE, config.getValue());

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(TABLE_NAME,null,values);
    }

    public int modify(Config config) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put(KEY_VALUE, config.getValue());

        String where = KEY_NAME+" = ?";
        String[] whereArgs = {config.getName()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int supress(Config config) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = KEY_NAME+" = ?";
        String[] whereArgs = {config.getName()+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    public Config get(String name) {
        // Retourne l'animal dont l'id est passé en paramètre

        Config a=new Config();
        a.setName(name);

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_NAME+"=\""+name+"\"", null);
        if (c.moveToFirst()) {
            a.setValue(c.getString(c.getColumnIndex(KEY_VALUE)));
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
