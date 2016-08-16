package electroniccupcake.EmergencyAlert;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.RemoteException;

/**
 * Created by Harsh on 7/23/2016.
 */
public class EmergencyAlertDB extends SQLiteOpenHelper
{

    private static String NAME = "EmergencyAlertDB";        // DB info
    private static int VERSION = 1;
    private static String TABLE_NAME = "CONTACTS_LOG";
    public  static String [] COLUMNS = {"CONTACT_NAME","PHONE_NUMBER","PHOTO_URI","POS"};
    private String createTableQuery;

    public EmergencyAlertDB(Context context)
    {
        super(context,NAME,null,VERSION);;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Creating the table.
        createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + " ( "
                + COLUMNS[0] + " TEXT , "
                + COLUMNS[1] + " TEXT , "
                + COLUMNS[2] + " TEXT , "
                + COLUMNS[3] + " INT "
                + " ) ";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        VERSION = newVersion;
    }

    public String getDBName()
    {
        return NAME;
    }

    public static String getTableName()
    {
        return TABLE_NAME;
    }

}

