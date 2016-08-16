package electroniccupcake.EmergencyAlert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Harsh on 8/7/2016.
 */
public class MessageSender extends BroadcastReceiver
{
    public static String SEND_MESSAGE_STR = "SEND_MESSAGE";
    public static String PHONE_DATA_STR = "ALL_PHONE_NUMBERS";
    private static int curVol;
    private static int prevVol;
    private static int count;
    private static long curTime;
    private static long prevTime;
    private static float startTime;
    public MessageSender()
    {
        super();
        curVol = 0;
        prevVol = 0;
        count = 0;
        curTime = System.currentTimeMillis();
        prevTime = System.currentTimeMillis();
    }

    public String[] phoneNumbers;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();

        if (bundle != null)
        {
            Log.i("TAG", "Bundle was not null: " + bundle.getString(SEND_MESSAGE_STR));
            if (bundle.getString(SEND_MESSAGE_STR) != null)
            {
                Log.i("TAG", "Got extra string");
                sendRequestForData(context);
            }
            else if (bundle.getStringArray(PHONE_DATA_STR) != null)
            {
                phoneNumbers = bundle.getStringArray(PHONE_DATA_STR);
                Log.i("TAG", "Got phone numbers: " + phoneNumbers.length);

            }
            else if (bundle.get("android.media.EXTRA_VOLUME_STREAM_VALUE") != null)
            {
                prevTime = curTime;
                // saving the old volume
                prevVol = curVol;
                // getting the new volume
                curVol = bundle.getInt("android.media.EXTRA_VOLUME_STREAM_VALUE");
                // getting new time
                curTime = System.currentTimeMillis();
                Log.i("TAG", "volume: " + curVol);

                if(curVol > prevVol)
                    Log.i("TAG","Current volume is bigger than previous volume");
                else
                    Log.i("TAG","Current volume is smaller than previous volume");

                Log.i("TAG","Current time: " + curTime + "\nPrevious Time: " + prevTime);
                Log.i("TAG","Time difference: " + (curTime - prevTime));

            } else
            {
                Log.i("TAG", "Bundle was not null, but no string found");
            }
        } else
        {
            Log.i("TAG", "Bundle was null");
        }
    }

    public void sendRequestForData(Context context)
    {
        Intent i = new Intent(context, DBDataGetter.class);
        context.startService(i);
    }
}


