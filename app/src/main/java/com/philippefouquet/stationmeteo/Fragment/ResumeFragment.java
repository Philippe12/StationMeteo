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
import com.philippefouquet.stationmeteo.Db.Config;
import com.philippefouquet.stationmeteo.Db.ConfigManager;
import com.philippefouquet.stationmeteo.Db.Room;
import com.philippefouquet.stationmeteo.Db.RoomManager;
import com.philippefouquet.stationmeteo.Other.ConfigAcess;
import com.philippefouquet.stationmeteo.Other.MQTTClient;

public class ResumeFragment extends Fragment {

    MQTTClient mqttClientOut;
    MQTTClient mqttClientIn;

    private OnFragmentInteractionListener mListener;

    public ResumeFragment() {
        // Required empty public constructor
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

    @Override
    public void onViewCreated(View view, Bundle sevedInstanceState) {
        openMQTT();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mqttClientOut.Disconnect();
        mqttClientIn.Disconnect();
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

        RoomManager rm = new RoomManager(getContext());
        rm.open();
        mqttClientOut = new MQTTClient("Resume_out");
        Config co = ConfigAcess.getConfig(getContext(), RoomConfigFragment.OUTDOOR_CAPTOR);
        Room ro = rm.get( Integer.parseInt(co.getValue()) );
        String topic = ro.getCapteur()+ "/#";
        TextView tv = getView().findViewById(R.id.textTitleOut);
        tv.setText(ro.getName());
        mqttClientOut.subscribeToTopic(new MQTTClient.MQTTCallback(topic){

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
        mqttClientOut.Connect(getActivity());

        mqttClientIn = new MQTTClient("Resume_in");
        co = ConfigAcess.getConfig(getContext(), RoomConfigFragment.INDOOR_CAPTOR);
        ro = rm.get( Integer.parseInt(co.getValue()) );
        topic = ro.getCapteur()+ "/#";
        tv = getView().findViewById(R.id.textTitle);
        tv.setText(ro.getName());
        mqttClientIn.subscribeToTopic(new MQTTClient.MQTTCallback(topic){

            @Override
            public void ReciveTopic(String topic, String Value){
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        TextView txt;
                        if(topic.contains("temperature")){
                            txt= (TextView)getView().findViewById(R.id.textTemp);
                            txt.setText(String.format("%.2f", new Float(Value)));
                        }
                        if(topic.contains("humidity")){
                            txt= (TextView)getView().findViewById(R.id.textHum);
                            txt.setText(String.format("%.2f", new Float(Value)));
                        }
                        if(topic.contains("pressure")){
                            txt= (TextView)getView().findViewById(R.id.textPres);
                            txt.setText(String.format("%.2f", new Float(Value)));
                        }
                    }
                });
            }
        });
        mqttClientIn.Connect(getActivity());
    }
}

