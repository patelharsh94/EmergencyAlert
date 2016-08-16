package electroniccupcake.EmergencyAlert;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import electroniccupcake.projectalert.R;

/**
 * Created by Harsh on 7/2/2016.
 */
public class AlertProcessingService extends IntentService
{

    private static String TAG = "TAG";
    private double start_time;
    private static boolean isTouching;
    private double curr_time;
    private double final_time;
    private double progress;
    private Intent progressUpdateIntent;

    public AlertProcessingService()
    {
        super("com.electroniccupcake.EmergencyAlert.AlertProcessingService");
        this.start_time = System.currentTimeMillis();
        this.curr_time = 0.0;
        this.progress = 0.0;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        this.final_time = intent.getDoubleExtra(getApplicationContext().getString(R.string.Final_Time),0.0);

        start_time = System.currentTimeMillis();
        Log.i(TAG,"In on handle intent is touching : " + isTouching);
        while (isTouching)
        {
            curr_time = System.currentTimeMillis();
            // if the three second mark is reached
            if ((curr_time - start_time) / 100.0 > final_time)
            {
                Log.i(TAG, "Sending Emergency Message!!");
                break;
            }
            else        // Update the progress bar
            {
                progress = ((((curr_time - start_time)/100)/ final_time)*100);
                Log.i(TAG,"Current Time: " + curr_time);
                Log.i(TAG,"Start Time: " + start_time);
                Log.i(TAG,"Final Time: " + final_time);
                Log.i(TAG,"(Current time - Start time)/100: " + (curr_time - start_time)/100);
                Log.i(TAG, "Progress: " + progress);

                progressUpdateIntent = new Intent(main_content.AlertAnimationUpdateReciever.EA_ANIM_REC_ADDR);
                progressUpdateIntent.putExtra(getApplicationContext().getString(R.string.Progress_Val),(float)progress);
                LocalBroadcastManager.getInstance(this).sendBroadcast(progressUpdateIntent);
            }
        }
        /*
        curr_time = 0.0;
        start_time = System.currentTimeMillis();
        // winding down
        while(curr_time - start_time < final_time)
        {
            curr_time =  System.currentTimeMillis();
            progress = (((start_time - curr_time) / 1000) / final_time) * 100.00;
            progressUpdateIntent = new Intent();
            progressUpdateIntent.setAction(main_content.AlertAnimationUpdateReciever.EA_ANIM_REC_ADDR);
            progressUpdateIntent.addCategory(Intent.CATEGORY_DEFAULT);
            progressUpdateIntent.putExtra(getApplicationContext().getString(R.string.Progress_Val),(float)progress);
            LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(progressUpdateIntent);
        }
        */
    }

    public static void setIsTouching(boolean touching)
    {
        isTouching = touching;
    }
}
