package com.philippefouquet.stationmeteo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResumeFragment extends Fragment {

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
            setDatathp(t,h,p);
        }
    };

    public void setDatathp(double t, double h, double p){
        StringBuilder builder = new StringBuilder();
        TextView txt;
        txt= (TextView)getView().findViewById(R.id.textTemp);
        txt.setText(String.format("%.2f", t));
        txt= (TextView)getView().findViewById(R.id.textHum);
        txt.setText(String.format("%.2f", h));
        txt= (TextView)getView().findViewById(R.id.textPres);
        txt.setText(String.format("%.2f", p));
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        getActivity().unregisterReceiver(thpBroadcastReceiver);
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
}
