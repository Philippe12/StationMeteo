package com.philippefouquet.stationmeteo.Db;

/**
 * Created by philippefouquet on 10/10/2017.
 */

public class Room {
    private int id;
    private String name;

    public Room(){
    }

    public Room(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}