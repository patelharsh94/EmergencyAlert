package electroniccupcake.EmergencyAlert;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.LocationServices;
import com.natasa.progressviews.CircleProgressBar;
import com.natasa.progressviews.utils.OnProgressViewListener;

import org.json.JSONArray;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import electroniccupcake.projectalert.R;

// The main alert screen
public class main_content extends Fragment {

    private static CircleProgressBar alert_button;          // The main alert button
    private double final_time;
    private static final String TAG = "TAG";
    private AlertAnimationProcessingThread alertAnimationProcessingThread;
    private TextView alert_inst;
    public static String [] phoneNumbers;
    public SharedPreferences prefs;
    private static List<Address> address;               // The list of address given when location found.
    private static StringBuffer finalAddress;           // The address of the user\.
    public MessageSender messageSender;

    public main_content()
    {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity a)
    {
        super.onAttach(a);
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);

    }

    /*
    * The alert button is an object written in the library progressViewLib
    * library was offered for free on: https://android-arsenal.com/details/1/3186
    * Source for the library can be found on: https://github.com/natasam/DemoProgressViewsLibApp
    * ** Some modifications to the library were made for better and smoother animations.
    *
    * By Harsh Patel,
    * 7/7/2016
    * */

    // Inflating the view for the fragment.
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.alert_fragment,container,false);
        alert_inst =(TextView)view.findViewById(R.id.alert_inst);
        alert_inst.setVisibility(view.INVISIBLE);
        final_time = 10.0;                  // 1 sec..
        alert_button = (CircleProgressBar) view.findViewById(R.id.alert_timer);
        alert_button.setTextSize(100);
        alert_button.setRoundEdgeProgress(true);
        alert_button.setStartPositionInDegrees(270);
        alertAnimationProcessingThread = new AlertAnimationProcessingThread();
        alert_button.setText("Alert!", Color.WHITE);
        initializeMessageSender();
        sendRequestForData(this.getContext());

        try
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    FindAddress();
                }
            }).start();

            Toast.makeText(this.getContext(), "Location Found!", Toast.LENGTH_SHORT).show();

        }
        catch(Exception e)
        {
            Log.e("TAG","Got an exception while trying to find address, Exception: " + e);
        }


        alert_button.setOnTouchListener(new View.OnTouchListener()
        {
           // Intent service_intent;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    alert_inst.setVisibility(view.VISIBLE);
                    alertAnimationProcessingThread = new AlertAnimationProcessingThread();
                    alertAnimationProcessingThread.execute(final_time);
                }
                // finger is not touching
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    alert_inst.setVisibility(view.INVISIBLE);
                    alertAnimationProcessingThread.setIsTouching(false);
                }
                return false;
            }
        });


        // Just to check for change and respond correctly.
        alert_button.setOnProgressViewListener(new OnProgressViewListener()
        {
            @Override
            public void onFinish()
            {
                Toast.makeText(getActivity(),"Message Sent!",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"Toast sent on finish");
                sendMessage();
            }

            @Override
            public void onProgressUpdate(float progress)
            {
            }
        });
        return view;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void initializeMessageSender()
    {
        IntentFilter intentFilter = new IntentFilter(getString(R.string.MessageSenderIntentName));
        messageSender = new MessageSender();
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        this.getActivity().registerReceiver(messageSender, intentFilter);
    }

    public void sendRequestForData(Context context)
    {
        Intent i = new Intent(context, DBDataGetter.class);
        context.startService(i);
    }

    public void sendMessage()
    {
        Intent message = new Intent();
        String address = finalAddress.toString();
        String savedMessage = prefs.getString("com.emerencyAlert.message","none");
        String currMsg;


        if(savedMessage.equals("") || savedMessage.equals("none"))
        {
            currMsg = "Save Me!! I need your help, I am currently located at: " + address;
        }
        else
        {
            currMsg = savedMessage + " I am currently located at: " + address;
        }

        message.setAction(getString(R.string.MessageSenderIntentName));
        message.putExtra(MessageSender.SEND_MESSAGE_STR,currMsg);
        message.putExtra(MessageSender.PHONE_DATA_STR,phoneNumbers);
        Log.i("TAG","MAIN CONTENT Sending phone numbers: " + phoneNumbers.length);
        this.getContext().sendBroadcast(message);
    }

    // This method can be used to set progress for the alert button.
    public static void setAlertProgress(float progress, float prev_progress)
    {
        alert_button.setProgress(progress, prev_progress);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(messageSender.isInitialStickyBroadcast())
        {
            this.getContext().unregisterReceiver(messageSender);
        }

    }

    // This method can be used to set the text on the alert button.
    public static void setAlertText(String time)
    {
        alert_button.setText(time);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    public class AlertAnimationUpdateReciever extends BroadcastReceiver
    {
        public static final String EA_ANIM_REC_ADDR = "com.electroniccupcake.EmergencyAlert.AlertAnimationUpdateRecieverAddress";
        double prev_progress = 0.0;
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float progress;
            progress = intent.getFloatExtra(context.getString(R.string.Progress_Val),0f);

            if(progress > prev_progress)
            {
                Log.i(TAG,"Recieved: " + progress);
                prev_progress = progress;
                alert_button.setProgress(progress, (float)prev_progress);
                // send message sent toast if the progress is sufficient

            }
        }
    }

    /*
* Name: FindAddress
* Objective: This method finds the current address of the user and saves the address.
*
* NOTE: A small part of this method was taken from,
* URL:http://stackoverflow.com/questions/12102570/how-to-convert-gps-coordinates-to-locality
* Date: 07/04/2015
* Time: 5:54 PM
* */
    public void FindAddress() {
        Log.d("TAG", "Note: in FindAddress.");
        MainActivity.mGoogleApiClient.connect();                         // Connect
        String AllInfo = "";                                // All the previously stored info.
        JSONArray data = new JSONArray();                  // The array of all the data.
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(MainActivity.mGoogleApiClient);
        finalAddress = new StringBuffer();


        // Writing the base address of "" to the file.
        AllInfo = data.toString();
        Log.d("TAG", "Note: All Info : " + AllInfo);

        if (mLastLocation != null) {
            Log.d("TAG", "Note: the Long and Lat is: " + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
            Geocoder geo = new Geocoder(this.getContext(), Locale.getDefault());             // Getting the geo location.
            try {
                Log.d("TAG", "Inside the try");
                address = geo.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (!address.isEmpty()) {
                    Log.d("TAG", "Note: Feature = " + address.get(0).getFeatureName());
                    Log.d("TAG", "Note: Locality = " + address.get(0).getLocality());
                    Log.d("TAG", "Note: Admin Area = " + address.get(0).getAdminArea());
                    Log.d("TAG", "Note: Postal Code = " + address.get(0).getPostalCode());
                    Log.d("TAG", "Note: Country Name = " + address.get(0).getCountryName());

                    // saving the address of the user.
                    finalAddress.append(address.get(0).getFeatureName() + " " +   // getting feature name
                            address.get(0).getLocality() + " " +      // getting locality
                            address.get(0).getAdminArea() + " " +     // get the area
                            address.get(0).getPostalCode() + " " +    // get the zip
                            address.get(0).getCountryName());         // get the country.
                    //Log.d("TAG", "Note: the address is: " + address);
                    // After you find the address, write to file.
                    AllInfo = data.toString();
                    Log.d("TAG", "Note: All Info : " + AllInfo);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}
