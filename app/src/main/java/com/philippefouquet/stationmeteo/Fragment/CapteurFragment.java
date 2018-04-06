package com.philippefouquet.stationmeteo.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philippefouquet.stationmeteo.Other.CaptorItem;
import com.philippefouquet.stationmeteo.Other.MQTTClient;
import com.philippefouquet.stationmeteo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CapteurFragment extends Fragment {

    MQTTClient mqttClient;
    final String subscriptionTopic = "count/#";

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<CaptorItem> mListe = new ArrayList<CaptorItem>();
    MyCapteurRecyclerViewAdapter mMyCapteurRecyclerViewAdapter;
    Timer mTimer;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CapteurFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CapteurFragment newInstance(int columnCount) {
        CapteurFragment fragment = new CapteurFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capteur_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mMyCapteurRecyclerViewAdapter = new MyCapteurRecyclerViewAdapter(mListe, mListener);
            recyclerView.setAdapter(mMyCapteurRecyclerViewAdapter);
        }
        return view;
    }

    private void openMQTT(){

        mqttClient = new MQTTClient();
        mqttClient.subscribeToTopic(new MQTTClient.MQTTCallback(subscriptionTopic){

            @Override
            public void ReciveTopic(String topic, String Value){
                CaptorItem cap = new CaptorItem();
                cap.setId( topic.replace("count/", ""));
                int pos = mListe.indexOf(cap);
                if( pos < 0 ) {
                    mListe.add(cap);
                }else{
                    mListe.get(pos).SetCpt(new Integer(Value));
                }

                if (mMyCapteurRecyclerViewAdapter != null)
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mMyCapteurRecyclerViewAdapter.refresh(getActivity());
                        }
                    });
            }
        });
        mqttClient.Connect(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        openMQTT();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mqttClient.Disconnect();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(CaptorItem item);
    }
}
