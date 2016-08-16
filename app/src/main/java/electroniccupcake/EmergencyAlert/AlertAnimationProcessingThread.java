package electroniccupcake.EmergencyAlert;

import android.os.AsyncTask;
import android.util.Log;

import java.text.DecimalFormat;


/**
 * Created by Harsh on 7/1/2016.
 * This is an Async task that will calculate the progress of the click on the alert button
 */
public class AlertAnimationProcessingThread extends AsyncTask<Double, Double, Double>
{
    private static String TAG = "TAG";          // For log messages
    private double start_time;                  // The start time, current time, and the final time
                                                // of the finger touch
    private double curr_time;
    private double final_time;
    private double progress;                    // Progress of the seek bar..
    private static boolean isTouching;          // For checking to see if the finger is touching.


    public AlertAnimationProcessingThread()
    {

    }

    @Override
    /*
    * Using this method as an initializer constructor..
    * */
    protected void onPreExecute()
    {
        this.start_time = System.currentTimeMillis();
        this.isTouching = true;
        this.curr_time = 0.0;
        this.progress = 0.0;
    }

    @Override
    /*
    * This method will perform the task of incrementing the progress bar,
    * int the background, leaving the UI smooth
    * */
    protected Double doInBackground(Double... params)
    {
        this.final_time = params[0];                // The final time.
        double prev_progress = 0;                   // For setting progress.

        while (isTouching)                          // While the finger is on the Alert Button..
        {

            curr_time = System.currentTimeMillis();
            // if the three second mark is reached
            if ((curr_time - start_time) / 100.0 > final_time)
            {
                Log.i(TAG, "Sending Emergency Message!!");
                if(prev_progress != progress)               // If there is a new time.. then publish the progress.
                {
                    publishProgress(progress,prev_progress);// When done and message is sent, break the loop
                }
                break;
            }
            else        // Update the progress bar
            {
                prev_progress = progress;
                progress = (((curr_time - start_time) / 100) / final_time) * 100.00;    // Calculate the progress and
                if(prev_progress != progress)                // if different progress, publish the progress..
                    publishProgress(progress,prev_progress);
            }
        }



        // winding down.. decrease the progress by a small amound and publish
        while(progress > 0)
        {
            progress-=.05;
            publishProgress(progress, progress+.05);
        }
        return progress;
    }

    /*
    * This method will update the main thread with the progress.
    * */
    @Override
    protected void onProgressUpdate(Double... progress)
    {
        float progressFloat = progress[0].floatValue();
        float prev_progress_float = progress[1].floatValue();
        double time = (final_time) * (progressFloat/1000);

        DecimalFormat time_fmt = new DecimalFormat("0.00");
        main_content.setAlertProgress(progressFloat, prev_progress_float);

        // resetting the time..
        if(time > 0)
        {
            main_content.setAlertText("" + time_fmt.format(time) + " secs");
        }
        else
        {
            main_content.setAlertText("Alert");
        }
    }

    // Sets the is touching variable
    public static void setIsTouching(boolean isTouchingVal)
    {
        isTouching = isTouchingVal;
    }
}

