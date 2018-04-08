package com.philippefouquet.stationmeteo.Other;

import android.content.Context;

import com.philippefouquet.stationmeteo.Db.Config;
import com.philippefouquet.stationmeteo.Db.ConfigManager;

public class ConfigAcess {

    public static Config getConfig(Context context, String key){
        ConfigManager cm = new ConfigManager(context);
        cm.open();
        Config co = cm.get(key);
        if(co == null ){
            co = new Config(key, "");
            cm.add(co);
        }
        //cm.close();
        return co;
    }

    public static Config setConfig(Context context, String key, String value){
        ConfigManager cm = new ConfigManager(context);
        cm.open();
        Config co = cm.get(key);
        if(co == null ){
            co = new Config(key, value);
            cm.add(co);
        }else{
            co.setValue(value);
            cm.modify(co);
        }
        //cm.close();
        return co;
    }
}
