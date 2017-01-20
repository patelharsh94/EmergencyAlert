package electroniccupcake.EmergencyAlert;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

/*
* This is the broadcast reciever for sending messages and recieving message send brodcasts and
* volume button broadcast
* */
class MessageSender extends BroadcastReceiver
{
    public static String SEND_MESSAGE_STR = "SEND_MESSAGE";
    public static String PHONE_DATA_STR = "ALL_PHONE_NUMBERS";

    /*
    * Name: SendSingleMessage
    * Objective: This message sends a single textmessage to your contact
    * */

    public void SendSingleMessage(String phoneNumber, String Message) {
        SmsManager currSms = SmsManager.getDefault();
        currSms.sendTextMessage(phoneNumber, null, Message, null, null);
    }

    public MessageSender()
    {
        super();
    }

    public String[] phoneNumbers;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        String message = "";


        if (bundle != null)// && !MessageSenderServiceHolder.screenState)
        {
            Log.i("TAG", "Bundle was not null: " + bundle.getString(SEND_MESSAGE_STR));
            if (bundle.getString(SEND_MESSAGE_STR) != null)
            {
                Log.i("TAG", "Got extra string : " + bundle.getString(SEND_MESSAGE_STR));
                message = bundle.getString(SEND_MESSAGE_STR);
                //sendRequestForData(context);
            }

            if (bundle.getStringArray(PHONE_DATA_STR) != null)
            {
                phoneNumbers = bundle.getStringArray(PHONE_DATA_STR);
                Log.i("TAG", "Got phone numbers: " + phoneNumbers.length);
                for(String phone_number : phoneNumbers)
                {
                    SendSingleMessage(phone_number,message);
                }
            }

        }
        else
        {
            Log.i("TAG", "Bundle was null");
        }

        context.unregisterReceiver(this);
    }



}