package com.philippefouquet.stationmeteo.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.philippefouquet.stationmeteo.*;
import com.philippefouquet.stationmeteo.Other.MQTTClient;

public class ResumeFragment extends Fragment {

    MQTTClient mqttClient;
    final String subscriptionTopic = "b4e62d155617/sensor/#";

    private OnFragmentInteractionListener mListener;

    public ResumeFragment() {
        // Required empty public constructor
    }

    IntentFilter filter = new IntentFilter(comi2c.ACTION_NEW_INTER_TEMP);
    private BroadcastReceiver thpBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double t = intent.getDoubleExtra(comi2c.TEMP,0);
            double h = intent.getDoubleExtra(comi2c.HUM,0);
            double p = intent.getDoubleExtra(comi2c.PRES,0);
            boolean s = intent.getBooleanExtra(comi2c.STATUS, false);
            setDatathp(t,h,p,s);
        }
    };

    public void setDatathp(double t, double h, double p, boolean s){
        StringBuilder builder = new StringBuilder();
        TextView txt;
        txt= (TextView)getView().findViewById(R.id.textTemp);
        txt.setText(String.format("%.2f", t));
        txt= (TextView)getView().findViewById(R.id.textHum);
        txt.setText(String.format("%.2f", h));
        txt= (TextView)getView().findViewById(R.id.textPres);
        txt.setText(String.format("%.2f", p));
        txt= (TextView)getView().findViewById(R.id.textTitle);
        if(s){
            txt.setText("Intérieur");
        } else {
            txt.setText("Intérieur (sinul.)");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resume, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
/*        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
        getActivity().registerReceiver(thpBroadcastReceiver, filter);
        openMQTT();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        getActivity().unregisterReceiver(thpBroadcastReceiver);
        mqttClient.Disconnect();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void openMQTT(){

        mqttClient = new MQTTClient();
        mqttClient.subscribeToTopic(new MQTTClient.MQTTCallback(subscriptionTopic){

            @Override
            public void ReciveTopic(String topic, String Value){
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        TextView txt;
                        if(topic.contains("temperature")){
                            txt= (TextView)getView().findViewById(R.id.textExTemp);
                            txt.setText(String.format("%.2f", new Float(Value)));
                        }
                        if(topic.contains("humidity")){
                            txt= (TextView)getView().findViewById(R.id.textExHum);
                            txt.setText(String.format("%.2f", new Float(Value)));
                        }
                    }
                });
            }
        });
        mqttClient.Connect(getActivity());
    }
}

