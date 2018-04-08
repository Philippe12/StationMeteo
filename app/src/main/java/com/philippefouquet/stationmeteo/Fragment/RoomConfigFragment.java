package com.philippefouquet.stationmeteo.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.philippefouquet.stationmeteo.Db.Capteur;
import com.philippefouquet.stationmeteo.Db.CapteurManager;
import com.philippefouquet.stationmeteo.Db.Config;
import com.philippefouquet.stationmeteo.Db.Room;
import com.philippefouquet.stationmeteo.Db.RoomManager;
import com.philippefouquet.stationmeteo.Other.CaptorContent;
import com.philippefouquet.stationmeteo.Other.CaptorItem;
import com.philippefouquet.stationmeteo.Other.ConfigAcess;
import com.philippefouquet.stationmeteo.R;
import com.philippefouquet.stationmeteo.comi2c;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoomConfigFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RoomConfigFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String INDOOR_CAPTOR = "indoorcaptor";
    private static final String OUTDOOR_CAPTOR = "outdoorcaptor";

    private OnFragmentInteractionListener mListener;
    private Room mRoom;
    private CaptorContent mLstCapteur = new CaptorContent();
    private RoomManager mRoomManager;
    private ArrayAdapter<CaptorItem> spinnerArrayAdapter;
    private Boolean firstRecive = true;

    public RoomConfigFragment(){
        mRoom = new Room();
    }

    @SuppressLint("ValidFragment")
    public RoomConfigFragment(Room room) {
        super();
        mRoom = room;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_config, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle sevedInstanceState) {

        ImageButton bp = view.findViewById(R.id.deleteButton);
        if(mRoom.getId()== comi2c.ID_ROOM){
            bp.setVisibility(ImageButton.INVISIBLE);
        }else{
            bp.setVisibility(ImageButton.VISIBLE);
        }
        bp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomManager ma = new RoomManager(getContext());
                ma.open();
                ma.supress(mRoom);
                ma.close();

                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFragmentInteraction(true);
                }
            }
        });

        EditText text = view.findViewById(R.id.editName);
        text.setText(mRoom.getName());
        text.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!s.equals("") ) {
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                RoomManager ma = new RoomManager(getContext());
                ma.open();
                mRoom.setName(s.toString());
                ma.modify(mRoom);
                ma.close();
                if (mListener != null) {
                    mListener.onFragmentInteraction(false);
                }
            }
        });

        CheckBox ch = getView().findViewById(R.id.useForIndoor);
        Config co = ConfigAcess.getConfig(getContext(), INDOOR_CAPTOR);
        if(co.getValue().isEmpty()){
            ch.setChecked(false);
        }else {
            ch.setChecked( mRoom.getId() == Integer.parseInt(co.getValue()) );
        }
        ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ConfigAcess.setConfig(getContext(), INDOOR_CAPTOR, String.valueOf(mRoom.getId()));
            }
        });

        ch = getView().findViewById(R.id.useForOutdoor);
        co = ConfigAcess.getConfig(getContext(), OUTDOOR_CAPTOR);
        if(co.getValue().isEmpty()){
            ch.setChecked(false);
        }else {
            ch.setChecked( mRoom.getId() == Integer.parseInt(co.getValue()) );
        }
        ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ConfigAcess.setConfig(getContext(), OUTDOOR_CAPTOR, String.valueOf(mRoom.getId()));
            }
        });

        Spinner sp = getView().findViewById(R.id.configCapteur);
        //Spinner sp = getView().findViewById(R.id.configCapteur);
        spinnerArrayAdapter = new ArrayAdapter<CaptorItem>
                (getActivity(), android.R.layout.simple_spinner_item, mLstCapteur.ITEMS); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp.setAdapter(spinnerArrayAdapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!firstRecive) {
                    String cap = mLstCapteur.ITEMS.get(i).getId();
                    mRoom.setCapteur(cap);
                    mRoomManager.modify(mRoom);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mRoomManager = new RoomManager(getContext());
        mRoomManager.open();

        mLstCapteur.Run(getActivity(), "RoomCfg", new CaptorContent.CaptorContentRefrech() {
            @Override
            public void onCaptorContentRefrech() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if(spinnerArrayAdapter != null){
                            spinnerArrayAdapter.notifyDataSetChanged();
                        }

                        Spinner sp = getView().findViewById(R.id.configCapteur);
                        String cap = mRoom.getCapteur();
                        int id = mLstCapteur.Find(cap);
                        sp.setSelection(id);
                        if( (id >= 0) || (cap==null) || cap.equals("") ) {
                            firstRecive = false;
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mLstCapteur.Close();
        mRoomManager.close();
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
        void onFragmentInteraction(boolean back);
    }
}
