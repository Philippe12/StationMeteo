package com.philippefouquet.stationmeteo.Other;

import android.content.Context;
import android.util.Log;
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
import java.util.List;

public class MQTTClient {

    private final String TAG = "MQTTClient";

    private MqttAndroidClient mqttAndroidClient;
    private final String serverUri = "tcp://192.168.1.21:1883";
    private String clientId;
    private List<MQTTCallback> mTopicList = new ArrayList<MQTTCallback>();

    public abstract static class MQTTCallback{
        private final String mTopic;

        public MQTTCallback(String topic){
            mTopic = topic;
        }

        public String getTopic() {
            return mTopic;
        }

        private void setTopic(String val) {
            //mTopic = val;
        }

        public abstract void ReciveTopic(String topic, String Value);
    }

//    public MQTTClient(){
//        clientId = "AndroidClient_" + System.nanoTime();
//    }

    public MQTTClient(String client){
        clientId = "AndroidClient_" + client;
    }

    private void Log(String msg){
        Log.i(TAG, clientId+ " => " + msg);
    }

    public  void subscribeToTopic( MQTTCallback mqttCallback){
        mTopicList.add(mqttCallback);
        //subscribeToTopic(mqttCallback.getTopic(), mqttCallback);
    }

    public  void subscribeToTopic(){
        for (int id = 0; id < mTopicList.size(); id++){
            subscribeToTopic(mTopicList.get(id).getTopic(), mTopicList.get(id));
        }
    }

    public  void unSubscribeToTopic(){
        for (int id = 0; id < mTopicList.size(); id++){
            try {
                mqttAndroidClient.unsubscribe(mTopicList.get(id).getTopic());
            }catch (Exception e){

            }
        }
    }

    private void subscribeToTopic(String subscriptionTopic, MQTTCallback mqttCallback){
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log("Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log("Failed to subscribe");
                }
            });

            // THIS DOES NOT WORK!
            mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // message Arrived!
                    //System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
                    mqttCallback.ReciveTopic(topic, new String(message.getPayload()));
                }
            });

        } catch (MqttException ex){
            Log("Exception whilst subscribing");
            Log(ex.toString());
        }
    }

    public void Connect(Context context){
        try {
            mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
            mqttAndroidClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {

                    if (reconnect) {
                        Log("Reconnected to : " + serverURI);
                        // Because Clean Session is true, we need to re-subscribe
                        subscribeToTopic();
                    } else {
                        Log("Connected to: " + serverURI);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log("The Connection was lost.");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(false);

            Log("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log("Failed to connect to: " + serverUri);
                }
            });
        } catch (MqttException ex){
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Disconnect(){
        if(mqttAndroidClient!=null) {
            try {
                unSubscribeToTopic();
                mqttAndroidClient.disconnect();
                mqttAndroidClient.unregisterResources();
                mqttAndroidClient = null;
            } catch (Exception e){
                Log (e.toString() );
            }
        }
    }

    public void publishMessage(String topic, String msg){

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes());
            message.setQos(0);
            mqttAndroidClient.publish(topic, message);
            if(!mqttAndroidClient.isConnected()){
                Log(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (Exception e) {
            Log("Error Publishing: " + e.getMessage());
        }
    }


}
