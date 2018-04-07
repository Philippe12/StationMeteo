package com.philippefouquet.stationmeteo.Db;

/**
 * Created by philippefouquet on 10/10/2017.
 */

public class Room {
    private int id;
    private String name;
    private String capteur;

    public Room(){
    }

    public Room(int id, String name, String capteur){
        this.id = id;
        this.name = name;
        this.capteur = capteur;
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

    public String getCapteur() {
        return capteur;
    }

    public void setCapteur(String capteur) {
        this.capteur = capteur;
    }
}
