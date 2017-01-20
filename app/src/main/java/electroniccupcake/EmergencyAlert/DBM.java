package electroniccupcake.EmergencyAlert;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DBM extends Service
{

    private EmergencyAlertDB emergencyAlertDB;
    private String [] data;

    public DBM()
    {
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        emergencyAlertDB = new EmergencyAlertDB(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        emergencyAlertDB.close();
    }

    // the server interface, for communication between service and app.
    private final DBMInterface.Stub binder = new DBMInterface.Stub()
    {
        @Override
        public int [] getIndexList()
        {
            int [] URLList;
            data = readColumnFromDB(3);
            URLList = new int[data.length];

            for(int i = 0; i < URLList.length; i++)
            {
                URLList[i] = Integer.parseInt(data[i]);
            }

            return URLList;
        }

        @Override
        public void setIndex(final String name, final int index)
        {
            // Start a thread because inserting into DB is a long running process.
            new Thread(new Runnable() {
                public void run() {
                    try
                    {
                        updateIndex(name,index);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        @Override
        public void deleteFromDB(final String name)
        {
            // Start a thread because inserting into DB is a long running process.
            new Thread(new Runnable() {
                public void run() {
                    try
                    {
                        removeFromDB(name);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void insertIntoDB(final String name, final String phoneNumber, final String URI, final int index)
        {
            // Start a thread because inserting into DB is a long running process.
            new Thread(new Runnable() {
                public void run() {
                    try
                    {
                        insertDataIntoDB(name,phoneNumber,URI,index);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public String[] getPhoneNumberList()
        {
            data = readColumnFromDB(1);
            return data;
        }

        @Override
        public String[] getURIList()
        {
            data = readColumnFromDB(2);

            return data;
        }

        @Override
        public String[] getNameList()
        {
            data = readColumnFromDB(0);
            return data;
        }
    };

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    // This method reads data from the DB
    public String [] readColumnFromDB(int col)
    {
        DBHelperThread runner = new DBHelperThread(emergencyAlertDB,col);
        Thread thread = new Thread(runner);
        thread.start();
        // Wait for the thread to finish running.
        try
        {
            thread.join();
        }
        catch (Exception e)
        {
            Log.i("TAG","Exception while waiting for a thread to run..: " + e);
        }

        return runner.getData();
    }

    // This method inserts into db.
    public void insertDataIntoDB(String name, String number, String URI, int index)
    {
        ContentValues values = new ContentValues();

        // adding the values to the query.. and inserting into DB.
        values.put(EmergencyAlertDB.COLUMNS[0],name);
        values.put(EmergencyAlertDB.COLUMNS[1],number);
        values.put(EmergencyAlertDB.COLUMNS[2],URI);
        values.put(EmergencyAlertDB.COLUMNS[3],index);
        emergencyAlertDB.getWritableDatabase().insert(EmergencyAlertDB.getTableName(), null, values);

        values.clear();
    }

    // This method deleted from db
    public void removeFromDB(String name)
    {
        emergencyAlertDB.getWritableDatabase().delete(EmergencyAlertDB.getTableName(),EmergencyAlertDB.COLUMNS[0] + "=?" , new String[]{name});

    }

    // This method updates the index in the db
    public void updateIndex(String name, int index)
    {
        ContentValues values = new ContentValues();

        Log.i("TAG","Changing the index of : " + name + " to " + index);
        values.put(EmergencyAlertDB.COLUMNS[3], index);
        emergencyAlertDB.getWritableDatabase().update(EmergencyAlertDB.getTableName(), values, EmergencyAlertDB.COLUMNS[0] + "=?", new String[]{name});
    }

}
