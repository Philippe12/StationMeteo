package com.philippefouquet.stationmeteo.Other;

import android.app.Activity;

import com.philippefouquet.stationmeteo.Db.Capteur;

import java.util.ArrayList;
import java.util.List;

public class CaptorContent {
    private MQTTClient mqttClient;
    private final String subscriptionTopic = "count/#";

    private CaptorContentRefrech mListen;

    public List<CaptorItem> ITEMS = new ArrayList<CaptorItem>();
    private List<MQTTClient> ListMQTT = new ArrayList<MQTTClient>();

    public static abstract class CaptorContentRefrech{
        public abstract void onCaptorContentRefrech();
    }

    public CaptorContent(){

    }

    public void Run(Activity activity, String name, CaptorContentRefrech captorContent){
        mListen = captorContent;
        openMQTT(activity, name);
    }

    public void Close(){
        mqttClient.Disconnect();
        for( int id = 0; id < ListMQTT.size(); id++){
            ListMQTT.get(id).Disconnect();
        }
    }

    public int Find(String captorItem){
        if(captorItem == null){
            return -1;
        }
        for( int id = 0; id < ITEMS.size(); id++){
            if( ITEMS.get(id).getId().equals(captorItem) ){
                return id;
            }
        }
        return -1;
    }

    private void openMQTT(Activity activity, String name){

        mqttClient = new MQTTClient(name);
        mqttClient.subscribeToTopic(new MQTTClient.MQTTCallback(subscriptionTopic){

            @Override
            public void ReciveTopic(String topic, String Value){
                CaptorItem cap = new CaptorItem();
                cap.setId( topic.replace("count/", ""));
                int pos = ITEMS.indexOf(cap);
                if( pos < 0 ) {
                    ITEMS.add(cap);
                    NewMQTT(activity, ITEMS.get(ITEMS.indexOf(cap)));
                }else{
                    ITEMS.get(pos).SetCpt(new Integer(Value));
                }

                mListen.onCaptorContentRefrech();
            }
        });
        mqttClient.Connect(activity);
    }

    private void NewMQTT(Activity activity, CaptorItem it){
        String cap = it.getId();
        MQTTClient cl = new MQTTClient("ListCapteur_"+cap);
        cl.subscribeToTopic(new MQTTClient.MQTTCallback(cap + "/#"){
            CaptorItem item = it;

            @Override
            public void ReciveTopic(String topic, String Value){
                if(topic.contains("temperature")){
                    item.setTemp(new Double(Value));
                }
                if(topic.contains("humidity")){
                    item.setHum(new Double(Value));
                }
                if(topic.contains("pressure")){
                    item.setPres(new Double(Value));
                }
                mListen.onCaptorContentRefrech();
            }
        });
        cl.Connect(activity);
        ListMQTT.add(cl);
    }
}
