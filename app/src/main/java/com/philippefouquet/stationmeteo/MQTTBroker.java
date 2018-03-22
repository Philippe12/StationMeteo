package com.philippefouquet.stationmeteo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//import io.moquette.BrokerConstants;
//import io.moquette.server.config.MemoryConfig;

public class MQTTBroker extends Service {
    final String TAG = "MQTTBroker";
    //io.moquette.server.Server server = null;

    public MQTTBroker() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
/*            server = new io.moquette.server.Server();
            MemoryConfig memoryConfig = new MemoryConfig(new Properties());
            memoryConfig.setProperty(BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME,
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                            + File.separator
                            + BrokerConstants.DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME);
            server.startServer(memoryConfig);
            // server.startServer();//is not working due to DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME;
            Log.d(TAG,"Server Started");
        } catch (IOException e) {
            e.printStackTrace();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flags;
    }
}
