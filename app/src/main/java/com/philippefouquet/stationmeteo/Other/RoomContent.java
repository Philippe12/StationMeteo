package com.philippefouquet.stationmeteo.Other;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.philippefouquet.stationmeteo.Db.Room;
import com.philippefouquet.stationmeteo.Db.RoomManager;
import com.philippefouquet.stationmeteo.Db.THPManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class RoomContent {

    /**
     * An array of sample (dummy) items.
     */
    public final List<RoomItem> ITEMS = new ArrayList<RoomItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public final Map<String, RoomItem> ITEM_MAP = new HashMap<String, RoomItem>();

    public RoomContent(Context context){
        RoomManager ma = new RoomManager(context);
        ma.open();
        Cursor c = ma.get();
        if(c.moveToFirst()){
            do{
                Room r = new Room();
                r.setId( c.getInt(c.getColumnIndex(RoomManager.KEY_ID)));
                r.setName( c.getString(c.getColumnIndex(RoomManager.KEY_NAME)));
                r.setCapteur( c.getString(c.getColumnIndex(RoomManager.KEY_CAPTOR)));
                addItem(r);
            }while (c.moveToNext());
        }
        c.close();
        ma.close();
    }

    public int getMaxId(){
        int ret = 0;
        for (int id = 0; id < ITEMS.size(); id++) {
            RoomItem it = ITEMS.get(id);
            if(it.getId() > ret ) {
                ret = it.getId();
            }
        }
        return ret;
    }

    private void addItem(Room item) {
        RoomItem it = makeItem(item);
        ITEMS.add(it);
        ITEM_MAP.put(String.valueOf(item.getId()), it);
    }

    @NonNull
    public final static RoomItem makeItem(Room room){
        return new RoomItem(room);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class RoomItem {
        private Room mRoom;

        public RoomItem(Room room) {
            this.mRoom = room;
        }

        public int getId(){
            return mRoom.getId();
        }

        public String getName(){
            return mRoom.getName();
        }

        @Override
        public String toString() {
            return mRoom.getName();
        }

        public Room getRoom() {
            return mRoom;
        }
    }
}
