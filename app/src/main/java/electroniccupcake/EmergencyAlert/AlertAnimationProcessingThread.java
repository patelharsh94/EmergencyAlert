package electroniccupcake.EmergencyAlert;

import android.os.AsyncTask;
import android.util.Log;


/**
 * Created by Harsh on 7/1/2016.
 */
public class AlertAnimationProcessingThread extends AsyncTask<Double, Double, Double>
{
    private static String TAG = "TAG";
    private double start_time;
    private static boolean isTouching;
    private double curr_time;
    private double final_time;
    private double progress;

    public AlertAnimationProcessingThread()
    {

    }

    @Override
    protected void onPreExecute()
    {
        this.start_time = System.currentTimeMillis();
        this.isTouching = true;
        this.curr_time = 0.0;
        this.progress = 0.0;
    }

    @Override
    protected Double doInBackground(Double... params)
    {
        this.final_time = params[0];
        while (isTouching) {

            curr_time = System.currentTimeMillis();
            // if the three second mark is reached
            if ((curr_time - start_time) / 100.0 > final_time) {
                Log.i(TAG, "Sending Emergency Message!!");
                publishProgress(progress);
            } else        // Update the progress bar
            {
                progress = (((curr_time - start_time) / 1000) / final_time) * 100.00;
                publishProgress(progress);
                Log.i(TAG, "Progress: " + progress);
            }
        }

        return progress;
    }

    @Override
    protected void onProgressUpdate(Double... progress)
    {
        float progressFloat = progress[0].floatValue();
        main_content.setAlertProgress(progressFloat);
    }


    public static void setIsTouching(boolean isTouchingVal)
    {
        isTouching = isTouchingVal;
    }
}

