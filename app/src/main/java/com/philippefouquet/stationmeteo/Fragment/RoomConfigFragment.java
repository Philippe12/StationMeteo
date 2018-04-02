package com.philippefouquet.stationmeteo.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.philippefouquet.stationmeteo.Db.Room;
import com.philippefouquet.stationmeteo.Db.RoomManager;
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

    private OnFragmentInteractionListener mListener;
    private Room mRoom;

    public RoomConfigFragment(){
        mRoom = new Room();
    }

    public RoomConfigFragment(Room room) {
        mRoom = room;
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
        void onFragmentInteraction(boolean back);
    }
}
