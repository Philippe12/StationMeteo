package com.philippefouquet.stationmeteo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.philippefouquet.stationmeteo.jni.i2c;

public class comi2c extends Service {
    final String TAG = "Comi2c";
    final static String ACTION_NEW_INTER_TEMP = "ACTION_NEW_INTER_TEMP";
    final static String TEMP = "I2C_TEMP";
    final static String HUM = "I2C_HUM";
    final static String PRES = "I2C_PRES";
    private int m_fd;
    private double m_temp;
    private double m_pres;
    private double m_hum;
    private Thread m_threadService = null;

    public comi2c() {
        m_fd = i2c.init("/dev/i2c-0");
        if(m_fd < 0)
            Log.e(TAG, "Can't open /dev/i2c-0");
        init_f();
        //super("comi2c");
    }

    double temperture_sht25(){
        int[] buf = new int[10];
        if( m_fd < 0 ){
            return Math.random();
        }
        if(i2c.open(m_fd, 0x40) < 0)
            return 0;
        buf[0] = 0xF3;
        if( i2c.write(m_fd, buf, 1) < 0 )
            return 0;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if( i2c.read(m_fd, buf, 3) < 0 )
            return 0;
        return (-46.85 + 175.72 * ((buf[1] + (buf[0] << 8)) / 65536.0));

    }

    double humidity_sht25(){
        int[] buf = new int[10];
        if( m_fd < 0 ){
            return Math.random();
        }
        if(i2c.open(m_fd, 0x40) < 0)
            return 0;
        buf[0] = 0xF5;
        if( i2c.write(m_fd, buf, 1) < 0 )
            return 0;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if( i2c.read(m_fd, buf, 3) < 0 )
            return 0;
        return (-6.0 + 125.0 * ((buf[1] + (buf[0] << 8)) / 65536.0));
    }

    void init_f(){
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

    double pressure_f(){
        int[] buf = new int[10];
        if( m_fd < 0 ){
            return Math.random();
        }
        if( i2c.open(m_fd, 0x60) < 0 )
            return 0;

        buf[0] = 0x00;
        if(i2c.write(m_fd, buf, 1) < 0)
            return 0;
        if(i2c.read(m_fd, buf, 7) < 0)
            return 0;
        double val = (buf[1]<<10)+(buf[2]<<2)+((buf[3]&0xC0)>>5)+(((buf[3]&0x30)>>4)/8.0);
        return (val/100.0);

        //double temp = buf[4]+((buf[5]>>4)/8.0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        m_threadService = new Thread(){
            @Override
            public void run() {
                while(true){
                    m_temp = temperture_sht25();
                    m_hum = humidity_sht25();
                    m_pres = pressure_f();
                    Intent intent = new Intent();
                    intent.setAction(ACTION_NEW_INTER_TEMP);

                    intent.putExtra(TEMP, m_temp);
                    intent.putExtra(HUM, m_hum);
                    intent.putExtra(PRES, m_pres);

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
