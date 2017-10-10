/**
 * Created by philippefouquet on 07/10/2017.
 */
package com.philippefouquet.stationmeteo.db;

public class THP {
    private long date;
    private int room;
    private double humidityMoy;
    private double humidityMax;
    private double humidityMin;
    private double temperatureMoy;
    private double temperatureMax;
    private double temperatureMin;
    private double pressureMoy;
    private double pressureMax;
    private double pressureMin;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getHumidityMoy() {
        return humidityMoy;
    }

    public void setHumidityMoy(double humidityMoy) {
        this.humidityMoy = humidityMoy;
    }

    public double getHumidityMax() {
        return humidityMax;
    }

    public void setHumidityMax(double humidityMax) {
        this.humidityMax = humidityMax;
    }

    public double getHumidityMin() {
        return humidityMin;
    }

    public void setHumidityMin(double humidityMin) {
        this.humidityMin = humidityMin;
    }

    public double getTemperatureMoy() {
        return temperatureMoy;
    }

    public void setTemperatureMoy(double temperatureMoy) {
        this.temperatureMoy = temperatureMoy;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public double getPressureMoy() {
        return pressureMoy;
    }

    public void setPressureMoy(double pressureMoy) {
        this.pressureMoy = pressureMoy;
    }

    public double getPressureMax() {
        return pressureMax;
    }

    public void setPressureMax(double pressureMax) {
        this.pressureMax = pressureMax;
    }

    public double getPressureMin() {
        return pressureMin;
    }

    public void setPressureMin(double pressureMin) {
        this.pressureMin = pressureMin;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }
}
