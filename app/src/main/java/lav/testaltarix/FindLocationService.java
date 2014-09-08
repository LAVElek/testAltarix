package lav.testaltarix;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.TimeUnit;

public class FindLocationService extends Service implements LocationListener{

    final int DELAY_LOCATION = 30;
    public static final String BROADCAST_ACTION = "lav.testaltarix.newlocation";
    public static final String PARAM_LONGITUDE = "longitude";
    public static final String PARAM_LATITUDE = "latitude";

    private DB insertDB;
    private LocationManager lmLocation;
    private Location location;
    private Thread threadLocation;
    boolean isStop;

    public FindLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        insertDB = new DB(getApplicationContext());
        lmLocation = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lmLocation.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);

        threadLocation = new Thread(new GetLocation(DELAY_LOCATION));
        threadLocation.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isStop = true;
        threadLocation.interrupt();
        lmLocation.removeUpdates(this);
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class GetLocation implements Runnable{
        int delay_ms = 20; // по умолчанию задержка между определениями координат 20с

        GetLocation(int delay_ms){
            this.delay_ms = delay_ms;
        }
        @Override
        public void run() {
            Intent intent = new Intent(BROADCAST_ACTION);
            isStop = false;
            while(!isStop){
                // запрашиваем координаты
                location = lmLocation.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    insertDB.addPoint( location.getLatitude(), location.getLongitude());
                    // сообщаем о новой позиции
                    intent.putExtra(PARAM_LATITUDE, location.getLatitude());
                    intent.putExtra(PARAM_LONGITUDE, location.getLongitude());
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
                // необходимая пауза
                try {
                    TimeUnit.SECONDS.sleep(delay_ms);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopSelf();
        }
    }
}
