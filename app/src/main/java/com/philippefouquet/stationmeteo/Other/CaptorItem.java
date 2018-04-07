package com.philippefouquet.stationmeteo.Other;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class CaptorItem {
    final private long TIMEOUT = 10000;
    private int mCpt = 0;
    private Timer mTimer;

    private String Id = "";
    private Boolean Status = false;
    private String Name = "";

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Boolean getStatus() {
        return Status;
    }

    public String getName() {
        return Name;
    }

    private class TaskStatus extends TimerTask{
        @Override
        public void run() {
            Status = false;
        }
    }

    public CaptorItem(){
    }

    @Override
    public boolean equals(Object obj) {
        return ((CaptorItem)obj).Id.equals( this.Id );
    }

    public void SetCpt(int v){
        if(mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
        Status = true;
        mCpt = v;
        if(Status){
            mTimer = new Timer();
            mTimer.schedule( new TaskStatus(),TIMEOUT );
        }
    }

    @Override
    public String toString(){
        return Id;
    }
}
