package lav.testaltarix;

import android.app.ActivityManager;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;


public class fragment_pointList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    DB DBPoints;
    ListView lvPoint;
    Button btnStart;
    Button btnStop;
    SimpleCursorAdapter scAdapter;
    BroadcastReceiver brNewLocation;

    public fragment_pointList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBPoints = new DB(getActivity());

        brNewLocation = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getLoaderManager().getLoader(0).forceLoad();
                lvPoint.setSelection(lvPoint.getCount());
                return;
            }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(FindLocationService.BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(brNewLocation, intFilt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_point_list, container, false);

        String[] from = new String[]{DB.POINT_COLUMN_ID, DB.POINT_DATE, DB.POINT_LATITUDE, DB.POINT_LONGITUDE};
        int[] to = new int[]{R.id.tvID, R.id.tvDate, R.id.tvLatitude, R.id.tvLongitude};

        scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, from, to, 0);
        lvPoint = (ListView)view.findViewById(R.id.lvPoint);
        lvPoint.setAdapter(scAdapter);

        btnStart = (Button)view.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(new Intent(getActivity(), FindLocationService.class));
                btnStart.setEnabled(false);
            }
        });

        btnStop = (Button)view.findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().stopService(new Intent(getActivity(), FindLocationService.class));
                btnStart.setEnabled(true);
            }
        });

        getLoaderManager().initLoader(0, null, this);

        // определяем запущен ли сервис
        List<ActivityManager.RunningServiceInfo> rs = ((ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(50);
        for (int i = 0; i < rs.size(); i++){
            if (FindLocationService.class.getPackage().getName().equalsIgnoreCase(rs.get(i).service.getPackageName())){
                btnStart.setEnabled(false);
                break;
            }
        }

        return view;
    }

    @Override
    public void onDetach() {
        //DBPoints.close();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(brNewLocation);
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(getActivity(), DBPoints);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
