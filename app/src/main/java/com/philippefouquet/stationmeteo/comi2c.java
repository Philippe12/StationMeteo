package com.philippefouquet.stationmeteo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TimeUtils;

import com.philippefouquet.stationmeteo.db.Room;
import com.philippefouquet.stationmeteo.db.RoomManager;
import com.philippefouquet.stationmeteo.db.THP;
import com.philippefouquet.stationmeteo.db.THPManager;
import com.philippefouquet.stationmeteo.jni.i2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class comi2c extends Service {
    final String TAG = "Comi2c";
    public final static String ACTION_NEW_INTER_TEMP = "ACTION_NEW_INTER_TEMP";
    public final static String TEMP = "I2C_TEMP";
    public final static String HUM = "I2C_HUM";
    public final static String PRES = "I2C_PRES";
    public final static String STATUS = "I2C_STATUS";

    final static int ID_ROOM = 0;
    private int m_fd;
    private List<Double> m_temp = new ArrayList<Double>();
    private List<Double> m_pres = new ArrayList<Double>();
    private List<Double> m_hum = new ArrayList<Double>();
    private int lastHour = (new Date(System.currentTimeMillis())).getHours();
    private Thread m_threadService = null;

    private RoomManager roomManager;
    private THPManager thpManager;

    private boolean status;

    private double randomValue(double dc, double noise){
        status = false;
        return dc + ((Math.random()-0.5)*noise);
    }

    public comi2c() {
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

        roomManager = new RoomManager(this);
        roomManager.open();
        if( roomManager.get(ID_ROOM) == null ) {
            roomManager.add(new Room(ID_ROOM, "in"));
        }
        thpManager = new THPManager(this);
        thpManager.open();

        m_threadService = new Thread(){
            @Override
            public void run() {
                while(true){
                    double t, h, p;
                    status = true;
                    t = temperture_sht25();
                    h = humidity_sht25();
                    p = pressure_f();

                    m_temp.add( t );
                    m_hum.add( h );
                    m_pres.add( p );

                    if( lastHour != (new Date(System.currentTimeMillis())).getHours() ) {
                        lastHour = (new Date(System.currentTimeMillis())).getHours();
                        THP thp = new THP();
                        thp.setRoom(ID_ROOM);
                        thp.setDate(System.currentTimeMillis());
                        thp.setHumidityMax(Collections.max( m_hum ));
                        thp.setHumidityMin(Collections.min( m_hum ));
                        thp.setHumidityMoy(computeMoyen( m_hum ));

                        thp.setPressureMax(Collections.max( m_pres ));
                        thp.setPressureMin(Collections.min( m_pres ));
                        thp.setPressureMoy(computeMoyen( m_pres ));

                        thp.setTemperatureMax(Collections.max( m_temp ));
                        thp.setTemperatureMin(Collections.min( m_temp ));
                        thp.setTemperatureMoy(computeMoyen( m_temp ));
                        thpManager.add(thp);
                    }

                    Intent intent = new Intent();
                    intent.setAction(ACTION_NEW_INTER_TEMP);

                    intent.putExtra(TEMP, t);
                    intent.putExtra(HUM, h);
                    intent.putExtra(PRES, p);
                    intent.putExtra(STATUS, status);

                    sendBroadcast(intent);
                    try {
                        Thread.sleep(10000);
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
