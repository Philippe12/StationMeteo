package com.philippefouquet.stationmeteo.Db;

/**
 * Created by philippefouquet on 10/10/2017.
 */

public class Capteur {
    private String id;
    private int room;

    public Capteur(){
    }

    public Capteur(String id, int room){
        this.id = id;
        this.room = room;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }
}
