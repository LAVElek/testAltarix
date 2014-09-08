package lav.testaltarix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;


public class fragment_PointMap extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    BroadcastReceiver brNewLocation;
    MapView map;
    MapController mapController; // управление картой
    OverlayManager overManager; // менеджер слоев
    Overlay point_layer; // слой, где будут отображаться точки
    DB DBPoints;


    public fragment_PointMap() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_point_map, container, false);

        map = (MapView)view.findViewById(R.id.map);
        mapController = map.getMapController();
        overManager = mapController.getOverlayManager();
        point_layer = new Overlay(mapController);
        overManager.addOverlay(point_layer);

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBPoints = new DB(getActivity());

        brNewLocation = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                drawMapPoint(intent.getDoubleExtra(FindLocationService.PARAM_LATITUDE, 0),
                             intent.getDoubleExtra(FindLocationService.PARAM_LONGITUDE, 0));
                return;
            }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(FindLocationService.BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brNewLocation, intFilt);
    }

    @Override
    public void onDetach() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brNewLocation);
        super.onDetach();
    }

    private void drawMapPoint(double latitude, double longitude){

        GeoPoint gp = new GeoPoint(latitude, longitude);
        // создаем объект на карте
        OverlayItem over_item = new OverlayItem(gp, getResources().getDrawable(R.drawable.map_item));;
        point_layer.addOverlayItem(over_item);

        // переходим к последней точке
        mapController.setPositionAnimationTo(gp, mapController.getZoomCurrent());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(getActivity(), DBPoints);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()){
            do {
                drawMapPoint(data.getDouble(data.getColumnIndex(DB.POINT_LATITUDE)),
                             data.getDouble(data.getColumnIndex(DB.POINT_LONGITUDE)));
            }
            while (data.moveToNext());
        }
        DBPoints.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
