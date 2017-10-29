package com.philippefouquet.stationmeteo.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.philippefouquet.stationmeteo.R;
import com.philippefouquet.stationmeteo.db.RoomManager;
import com.philippefouquet.stationmeteo.db.THPManager;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GraphicFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GraphicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphicFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ROOM_ID = "room_id";

    // TODO: Rename and change types of parameters
    private int roomId;

    private THPManager thpManager = new THPManager(getContext());

    private OnFragmentInteractionListener mListener;

    public GraphicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param roomId Parameter 1.
     * @return A new instance of fragment GraphicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphicFragment newInstance(int roomId) {
        GraphicFragment fragment = new GraphicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ROOM_ID, roomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.roomId = getArguments().getInt(ARG_ROOM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graphic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle sevedInstanceState){
        GraphView graph = (GraphView) getView().findViewById(R.id.graph);
        List<DataPoint> data_moy = new ArrayList<>();
        List<DataPoint> data_max = new ArrayList<>();
        List<DataPoint> data_min = new ArrayList<>();
        thpManager.open();
        Cursor c = thpManager.getfrom(roomId, System.currentTimeMillis()-(2*24*60*60*1000));
        long min = -1, max = -1;
        if (c.moveToFirst())
        {
            do {
                double temp_moy = c.getDouble(c.getColumnIndex(THPManager.KEY_TEMPERATURE+THPManager.KEY_MOY));
                double temp_min = c.getDouble(c.getColumnIndex(THPManager.KEY_TEMPERATURE+THPManager.KEY_MIN));
                double temp_max = c.getDouble(c.getColumnIndex(THPManager.KEY_TEMPERATURE+THPManager.KEY_MAX));
                long date = c.getLong(c.getColumnIndex(THPManager.KEY_DATE));
                data_moy.add(new DataPoint(date, temp_moy));
                data_min.add(new DataPoint(date, temp_min));
                data_max.add(new DataPoint(date, temp_max));
                if((min == -1) || (date < min)) min = date;
                if((max == -1) || (date > max)) max = date;
            }
            while (c.moveToNext());
        }
        c.close(); // fermeture du curseur

        //DateFormat dt_form = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DateFormat dt_form = new SimpleDateFormat("HH:mm");
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), dt_form));

        // set manual x bounds to have nice steps
        //graph.getViewport().setMinX((new Date(min)).getTime());
        //graph.getViewport().setMaxX((new Date(max)).getTime());
        graph.getViewport().setMinX((new Date(System.currentTimeMillis()-(2*24*60*60*1000))).getTime());
        graph.getViewport().setMaxX((new Date(System.currentTimeMillis())).getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        DataPoint[] tmp = new DataPoint[data_moy.size()];
        tmp = data_moy.toArray(tmp);
        LineGraphSeries<DataPoint> series_moy = new LineGraphSeries<>(tmp);
        series_moy.setThickness(2);
        series_moy.setColor(Color.rgb(36,36,36));

        tmp = new DataPoint[data_min.size()];
        tmp = data_min.toArray(tmp);
        LineGraphSeries<DataPoint> series_min = new LineGraphSeries<>(tmp);
        series_min.setThickness(2);
        series_min.setColor(Color.rgb(0,0,220));

        tmp = new DataPoint[data_moy.size()];
        tmp = data_max.toArray(tmp);
        LineGraphSeries<DataPoint> series_max = new LineGraphSeries<>(tmp);
        series_max.setThickness(2);
        series_max.setColor(Color.rgb(220,0,0));

        graph.addSeries(series_moy);
        graph.addSeries(series_min);
        graph.addSeries(series_max);
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
        /*if (context instanceof OnFragmentInteractionListener) {
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
