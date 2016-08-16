package electroniccupcake.EmergencyAlert;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harsh on 7/24/2016.
 *
 * A helper thread to run long running operations on the DB.
 */
public class DBHelperThread implements Runnable
{
    List<String> data;
    int col;
    String query;

    EmergencyAlertDB db;


    public DBHelperThread(EmergencyAlertDB db, int col)
    {
        this.db = db;
        this.col = col;
        this.data = new ArrayList<>();
    }

    @Override
    public void run()
    {
        query = "SELECT " + EmergencyAlertDB.COLUMNS[col] +  " FROM " + EmergencyAlertDB.getTableName();

        // getting the result
        Cursor cursor = db.getWritableDatabase().rawQuery(query, null);
        // returning the result
        if(cursor.moveToFirst())
        {
            do
            {
               data.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
    }

    // This gets the data after the run has run.
    public String [] getData()
    {
        return data.toArray(new String[data.size()]);
    }
}
