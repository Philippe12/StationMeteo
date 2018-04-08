package com.philippefouquet.stationmeteo.Db;

/**
 * Created by philippefouquet on 10/10/2017.
 */

public class Config {
    private String name;
    private String value;

    public Config(){
    }

    public Config(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
