package com.philippefouquet.stationmeteo;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import com.philippefouquet.stationmeteo.Db.Room;
import com.philippefouquet.stationmeteo.Db.RoomManager;
import com.philippefouquet.stationmeteo.Db.THP;
import com.philippefouquet.stationmeteo.Db.THPManager;
import com.philippefouquet.stationmeteo.Other.MQTTClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MQTTBroker extends Service {
    final String TAG = "MQTTBroker";
    private List<MQTTClient> mqttClient = new ArrayList<MQTTClient>();
    private THPManager thpManager;
    private RoomManager roomManager;

    public MQTTBroker() {
    }

    private class AcquiTopic extends MQTTClient.MQTTCallback {
        private List<Double> m_temp = new ArrayList<Double>();
        private List<Double> m_pres = new ArrayList<Double>();
        private List<Double> m_hum = new ArrayList<Double>();
        private int lastHour = 0;
        private int ID_ROOM;
        private THPManager thpManager;

        public AcquiTopic(String topic, int id, THPManager thpManager){
            super(topic);
            ID_ROOM = id;
            this.thpManager = thpManager;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(System.currentTimeMillis()));
            lastHour = calendar.get(Calendar.HOUR);
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
        public void ReciveTopic(String topic, String Value) {
            if(topic.contains("temperature")){
                m_temp.add(new Double(Value));
            }
            if(topic.contains("humidity")){
                m_hum.add(new Double(Value));
            }
            if(topic.contains("pressure")){
                m_pres.add(new Double(Value));
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(System.currentTimeMillis()));
            int hours = calendar.get(Calendar.HOUR);
            if( lastHour != hours ) {
                lastHour = hours;
                THP thp = new THP();
                thp.setRoom(ID_ROOM);
                thp.setDate(System.currentTimeMillis());

                if(m_hum.size() > 0) {
                    thp.setHumidityMax(Collections.max(m_hum));
                    thp.setHumidityMin(Collections.min(m_hum));
                    thp.setHumidityMoy(computeMoyen(m_hum));
                }

                if(m_pres.size() > 0) {
                    thp.setPressureMax(Collections.max(m_pres));
                    thp.setPressureMin(Collections.min(m_pres));
                    thp.setPressureMoy(computeMoyen(m_pres));
                }

                if(m_temp.size() > 0) {
                    thp.setTemperatureMax(Collections.max(m_temp));
                    thp.setTemperatureMin(Collections.min(m_temp));
                    thp.setTemperatureMoy(computeMoyen(m_temp));
                }

                thpManager.add(thp);

                m_temp.clear();
                m_hum.clear();
                m_pres.clear();
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thpManager = new THPManager(this);
        thpManager.open();
        roomManager = new RoomManager(this);
        roomManager.open();
        run();
        return flags;
    }

    public void run(){
        Cursor c = roomManager.get();
        if(c.moveToFirst()){
            do{
                String cap = c.getString(c.getColumnIndex(RoomManager.KEY_CAPTOR));
                String name = c.getString(c.getColumnIndex(RoomManager.KEY_NAME));
                int id = c.getInt(c.getColumnIndex(RoomManager.KEY_ID));
                MQTTClient cl = new MQTTClient("aqui_" + id + "_" + name);
                String topic = cap +"/#";
                AcquiTopic to = new AcquiTopic(topic, id, thpManager);
                cl.subscribeToTopic(to);
                cl.Connect(getApplication());
                mqttClient.add(cl);
            }while (c.moveToNext());
        }
    }
}
