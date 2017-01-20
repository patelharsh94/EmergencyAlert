package electroniccupcake.EmergencyAlert;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class MessageSenderService extends Service
{
    public static int count;
    public static float startTime;
    public static int curVol;
    public static int prevVol;
    public static long curTime;
    public static long prevTime;

    public static boolean screenState = true;

    public MessageSenderService()
    {

    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        count = 0;
        curVol = 0;
        prevVol = 0;
        curTime = System.currentTimeMillis();
        prevTime = System.currentTimeMillis();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
       return null;
    }
}

