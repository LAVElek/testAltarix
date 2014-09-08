package lav.testaltarix;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

public class MyCursorLoader extends CursorLoader {

    DB db;

    public MyCursorLoader(Context context, DB db) {
        super(context);
        this.db = db;
    }

    @Override
    public Cursor loadInBackground() {
        return db.getAllPoints();
    }

}
