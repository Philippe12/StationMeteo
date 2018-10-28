package com.philippefouquet.stationmeteo.Fragment;

import android.app.Activity;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.philippefouquet.stationmeteo.Other.CaptorItem;
import com.philippefouquet.stationmeteo.R;
import com.philippefouquet.stationmeteo.Fragment.CapteurFragment.OnListFragmentInteractionListener;
import com.philippefouquet.stationmeteo.Other.RoomContent.RoomItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RoomItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyCapteurRecyclerViewAdapter extends RecyclerView.Adapter<MyCapteurRecyclerViewAdapter.ViewHolder> {

    private final List<CaptorItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyCapteurRecyclerViewAdapter(List<CaptorItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_capteur, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mCapteurId.setText(mValues.get(position).getId());
        if(mValues.get(position).getStatus()) {
            holder.mImageSatus.setImageResource(android.R.drawable.presence_online);
        }else{
            holder.mImageSatus.setImageResource(android.R.drawable.presence_offline);
        }
        holder.mTemp.setText(String.format("%.2f", mValues.get(position).getTemp()));
        holder.mHum.setText(String.format("%.2f", mValues.get(position).getHum()));
        holder.mPres.setText(String.format("%.2f", mValues.get(position).getPres()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCapteurId;
        public final ImageView mImageSatus;
        public final TextView mTemp;
        public final TextView mHum;
        public final TextView mPres;
        public CaptorItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCapteurId = (TextView) view.findViewById(R.id.capteurId);
            mImageSatus = (ImageView) view.findViewById(R.id.imageStatus);
            mTemp = (TextView) view.findViewById(R.id.temp);
            mHum = (TextView) view.findViewById(R.id.hum);
            mPres = (TextView) view.findViewById(R.id.pres);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCapteurId.getText() + "'";
        }
    }

    public void refresh(Activity ac)
    {
        if(ac == null) return;
        ac.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                MyCapteurRecyclerViewAdapter.this.notifyDataSetChanged();
            }
        });

    }

}
