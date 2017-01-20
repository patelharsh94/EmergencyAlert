package electroniccupcake.EmergencyAlert;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import electroniccupcake.projectalert.R;

public class DBDataGetter extends IntentService
{
    EmergencyAlertDB db;
    DBHelperThread runner;

    public DBDataGetter()
    {
        super("com.electroniccupcake.EmergencyAlert.DBDataGetter");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            db = new EmergencyAlertDB(this);
            runner = new DBHelperThread(db,1);  // to get the phone numbers
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

            main_content.phoneNumbers = runner.getData();

        }


    }




}
