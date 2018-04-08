package com.philippefouquet.stationmeteo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.philippefouquet.stationmeteo.Db.Room;
import com.philippefouquet.stationmeteo.Db.RoomManager;
import com.philippefouquet.stationmeteo.Db.THP;
import com.philippefouquet.stationmeteo.Db.THPManager;
import com.philippefouquet.stationmeteo.Jni.i2c;
import com.philippefouquet.stationmeteo.Other.MQTTClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class comi2c extends Service {
    final String TAG = "Comi2c";
    private final String ID = "1";
    public final static String STATUS = "I2C_STATUS";

    public final static int ID_ROOM = 0;
    private int m_fd;
    private Thread m_threadService = null;
    private MQTTClient mqttClient;

    private boolean status;

    private double randomValue(double dc, double noise){
        status = false;
        return dc + ((Math.random()-0.5)*noise);
    }

    public comi2c() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        m_fd = i2c.init("/dev/i2c-0");
        if(m_fd < 0)
            Log.e(TAG, "Can't open /dev/i2c-0");
        init_f();
    }

    private double temperture_sht25(){
        int[] buf = new int[10];
        if( m_fd < 0 ){
            return randomValue(20, 2);
        }
        if(i2c.open(m_fd, 0x40) < 0)
            return randomValue(20, 2);
        buf[0] = 0xF3;
        if( i2c.write(m_fd, buf, 1) < 0 )
            return randomValue(20, 2);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if( i2c.read(m_fd, buf, 3) < 0 )
            return randomValue(20, 2);
        return (-46.85 + 175.72 * ((buf[1] + (buf[0] << 8)) / 65536.0));

    }

    private double humidity_sht25(){
        int[] buf = new int[10];
        if( m_fd < 0 ){
            return randomValue(60, 5);
        }
        if(i2c.open(m_fd, 0x40) < 0)
            return randomValue(60, 5);
        buf[0] = 0xF5;
        if( i2c.write(m_fd, buf, 1) < 0 )
            return randomValue(60, 5);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if( i2c.read(m_fd, buf, 3) < 0 )
            return randomValue(60, 5);
        return (-6.0 + 125.0 * ((buf[1] + (buf[0] << 8)) / 65536.0));
    }

    private void init_f(){
        int[] buf = new int[10];
        if( m_fd < 0 ){
            return;
        }
        if( i2c.open(m_fd, 0x60) < 0 )
            return;
        buf[0] = 0x26;
        buf[1] = 0b00111000;
        if(i2c.write(m_fd, buf, 2)< 0 )
            return;
        buf[0] = 0x13;
        buf[1] = 0b00000111;
        if(i2c.write(m_fd, buf, 2) < 0)
            return;
        buf[0] = 0x26;
        buf[1] = 0b00111001;
        if(i2c.write(m_fd, buf, 2) < 0 )
            return;;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        buf[0] = 0x00;
        /*do {
            buf[0] = 0x00;
            i2c.write(fd, buf, 1);
            i2c.read(fd, buf, 1);
        }while((buf[0]&0x08)!=0x08);*/
    }

    private double pressure_f(){
        int[] buf = new int[10];
        if( m_fd < 0 ){
            return randomValue(1010, 5);
        }
        if( i2c.open(m_fd, 0x60) < 0 )
            return randomValue(1010, 5);

        buf[0] = 0x00;
        if(i2c.write(m_fd, buf, 1) < 0)
            return randomValue(1010, 5);
        if(i2c.read(m_fd, buf, 7) < 0)
            return randomValue(1010, 5);
        double val = (buf[1]<<10)+(buf[2]<<2)+((buf[3]&0xC0)>>5)+(((buf[3]&0x30)>>4)/8.0);
        return (val/100.0);

        //double temp = buf[4]+((buf[5]>>4)/8.0);
    }

    private double computeMoyen(List<Double> e){
        double ret = 0;
        if(e.size() == 0 ) return 0;
        for( Double v: e ){
            ret += v;
        }
        return ret / e.size();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mqttClient = new MQTTClient("com_i2c");
        /*mqttClient.subscribeToTopic(new MQTTClient.MQTTCallback("") {
            @Override
            public void ReciveTopic(String topic, String Value) {

            }
        });*/
        mqttClient.Connect(getApplication());
        m_threadService = new Thread(){
            @Override
            public void run() {
                int count = 0;
                while(true){
                    double t, h, p;
                    status = true;
                    t = temperture_sht25();
                    h = humidity_sht25();
                    p = pressure_f();
                    Log.i(TAG, "New measure");
                    if( m_fd >= 0 ){
                        mqttClient.publishMessage(ID+ "/sensor/temperature", String.valueOf(t));
                        mqttClient.publishMessage(ID+ "/sensor/humidity", String.valueOf(h));
                        mqttClient.publishMessage(ID+ "/sensor/pressure", String.valueOf(p));
                        mqttClient.publishMessage("count/"+ID, String.valueOf(count));
                    }
                    count++;

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        m_threadService.start();
        return flags;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
