package electroniccupcake.EmergencyAlert;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import electroniccupcake.projectalert.R;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // private static TextView Alert;
    private static String TAG;                          // For log messages
    private static StringBuffer MsgInfo;                // The message
    private static StringBuffer PhoneInfo;              // The list of phone numbers.
    private static StringBuffer finalAddress;           // The address of the user.
    private static List<Address> address;               // The list of address given when location found.
    private static Button AlertButton;                  // The alert button.
    private static boolean LOCATION_FOUND = false;      // If the location was found, then true.
    private static GoogleApiClient mGoogleApiClient;    // Handel to the googleApi
    private static int arrSize = 0;                    // The size of the array.
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /*
     * This method basically Initializes everything.
     * */
    public void Initialize() {
        MsgInfo = new StringBuffer();
        PhoneInfo = new StringBuffer();
        finalAddress = new StringBuffer();
        AlertButton = (Button) findViewById(R.id.AlertButton);
    }
    // Starting the geo location service and getting the final address..

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle("Emergency Alert");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        LocationCheck();
        FindAddress();
        // Start the setup activity as soon as the app start, but only when the app is first start.
        // This part was inspired from: http://stackoverflow.com/questions/7238532/how-to-launch-activity-only-once-when-app-is-opened-for-first-time
        // Date: 06/11/2015

        // setting shared preferences for the main activity
        SharedPreferences MainPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean ConfigIsStarted = MainPref.getBoolean("ConfigIsStarted", false);

        // if the configure was not previously started save it in the system preferences.
        if (!ConfigIsStarted) {
            Log.d("TAG", "Note: this is the first run for main.");
            startActivity(new Intent(MainActivity.this, configureScreen.class));
            SharedPreferences.Editor edit = MainPref.edit();
            edit.putBoolean("ConfigIsStarted", Boolean.TRUE);
            edit.commit();
        }
        Initialize();                                       // Initialize everything.
        try {
            readFile();                                     // Get all the data.
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Starting the GeoLocation Service

        /*
        * This is the browse button, all it does is call the pick contact method
        * */
        AlertButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            AlertClicked(AlertButton);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // This method is used to connect to Google's APIs
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    /*
    * Name: LocationCheck
    * Objective: This method basically sends a dialogue to the user asking if all the location/wifi permissions were met.
    * Note: In order to code this method, help was taken from
    * URL:http://www.mkyong.com/android/android-alert-dialog-example/
    * URL:http://stackoverflow.com/questions/6000452/how-can-i-launch-mobile-network-settings-screen-from-my-code
    * URL:http://stackoverflow.com/questions/16001521/launch-location-settings-intent-from-preferences-xml-file*/

    public void LocationCheck() {
        Log.d("TAG", "Note: In Location Check");

        AlertDialog.Builder alertDialogBuilder;                 // Dialog box for enabling location.
        final ConnectivityManager CheckConnection;              // To check for connection.
        NetworkInfo NetWorkConnection;                          // To check for network.
        LocationManager LocationConnection;                     // To check for GPS connection.
        WifiManager WifiConnection;                             // To check for wifi connection.
        boolean Network_location;                               // True if location is enabled.
        boolean Network;                                        // True if network is present.
        boolean Wifi;                                           // True if Wifi is present.

        // Initializing all the values.
        alertDialogBuilder = new AlertDialog.Builder(this);
        CheckConnection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetWorkConnection = CheckConnection.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        LocationConnection = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        WifiConnection = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Network_location = LocationConnection.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Network = NetWorkConnection.isConnected();
        Wifi = WifiConnection.isWifiEnabled();

        Log.d("TAG", "Note: Network = " + Network);

        if (!Network_location)                              // if no location was found, send a message
        {                                                   // asking the user to enable location.
            alertDialogBuilder.setTitle("We can't find you");
            alertDialogBuilder.setMessage("Our monkeys work better when we can find you, " +
                    "would you like to enable location?")
                    .setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {       // if ok, go to the enable screen.
                            // Enable the wifi and network.
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // close the dialogue box.
                            dialog.cancel();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        }

        //if not connected to the internet at all; ask the user to turn it on.
        if (!Network && !Wifi) {
            alertDialogBuilder.setTitle("Not connected?");
            alertDialogBuilder.setMessage("Our monkeys work better when you are connected, " +
                    "would you like to enable mobile?")
                    .setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {          // If ok is pressed
                            // send to the wifi/network page.
                            // Enable the wifi and network.
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName(
                                    "com.android.settings",
                                    "com.android.settings.Settings$DataUsageSummaryActivity"));

                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // close the dialogue box.
                            dialog.cancel();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

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
        mGoogleApiClient.connect();                         // Connect
        String AllInfo = "";                                // All the previously stored info.
        JSONArray data = new JSONArray();                  // The array of all the data.
        JSONObject LastKnownAddress;                        // The last known address.

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        finalAddress = new StringBuffer();

        LastKnownAddress = new JSONObject();
        try {
            // saving the message
            // First reset the info..
            LastKnownAddress.put("LastKnownAddress", "");
            data.put(LastKnownAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Writing the base address of "" to the file.
        AllInfo = data.toString();
        Log.d("TAG", "Note: All Info : " + AllInfo);
        try {
            FileOutputStream fos = openFileOutput("LastKnownAddress", MODE_PRIVATE);
            // rewrite the file..
            getFilesDir();
            fos.write(AllInfo.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mLastLocation != null) {
            Log.d("TAG", "Note: the Long and Lat is: " + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
            Geocoder geo = new Geocoder(this, Locale.getDefault());             // Getting the geo location.
            try {
                Log.d("TAG", "Inside the try");
                address = geo.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (!address.isEmpty()) {
                    LOCATION_FOUND = true;
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
                    Log.d("TAG", "Note: the address is: " + address);
                    Toast.makeText(this, "Location Found!", Toast.LENGTH_SHORT).show();
                    // After you find the address, write to file.
                    LastKnownAddress.put("LastKnownAddress", finalAddress);
                    data.put(LastKnownAddress);
                    AllInfo = data.toString();
                    Log.d("TAG", "Note: All Info : " + AllInfo);
                    try {
                        FileOutputStream fos = openFileOutput("LastKnownAddress", MODE_PRIVATE);
                        // rewrite the file..
                        getFilesDir();
                        fos.write(AllInfo.getBytes());
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

        }

    }

    /*
    * Name: readFile.
    * Objective: This method reads the file that is stored on internal storage and gets the message ready to be sent on the click of a button.
    *            It is called on create, so the data is gotten and is ready to push.
    * **Note some of the code for this method was taken from:http://www.lynda.com/Android-tutorials/Creating-reading-JSON-data-files/112584/121170-4.html
    * Date: 06/10/2015
    * */
    public void readFile() throws IOException, JSONException
    {
        String LastKnownAddress;                        // The last known address.
        String phoneNumbers;                            // The saved phone numbers.
        String Message;                                 // The saved message.
        FileInputStream fis;                            // For reading in the file.
        FileInputStream lis;                            // ""
        BufferedInputStream bis;                        // For reading in the file.
        BufferedInputStream LastBuffer;                 // ""
        StringBuffer b;                                 // To save all the stuff written in the file
        StringBuffer LastAddress;                       // To save the last address
        JSONArray data;                                 // To save the info stored stored in the file.
        JSONArray LastAddressData;                      // To save the address info stored in the file.

        LastKnownAddress = "";
        phoneNumbers = "";
        Message = "";

        if (!LOCATION_FOUND)                     // If the location was not found, find the address.
        {
            FindAddress();
        }
        MsgInfo = new StringBuffer();
        PhoneInfo = new StringBuffer();
        fis = openFileInput("AppDataAlert");
        Log.d(TAG, "Note:file is open");
        bis = new BufferedInputStream(fis);
        b = new StringBuffer();

        // Go until there is stuff avaliable in the input and add it to the string..
        while (bis.available() != 0) {
            char c = (char) bis.read();
            b.append(c);
            Log.d(TAG, "Note: B:" + b);
        }

        bis.close();
        fis.close();
        // This file will store the last known address.
        lis = openFileInput("LastKnownAddress");
        Log.d(TAG, "Note:file is open");
        LastBuffer = new BufferedInputStream(lis);
        LastAddress = new StringBuffer();

        // go until the list has a something, and save the address.
        while (lis.available() != 0) {
            char d = (char) lis.read();
            LastAddress.append(d);
        }

        LastBuffer.close();
        lis.close();

        Log.d(TAG, "Note: buffer and file closed");
        data = new JSONArray(b.toString());
        LastAddressData = new JSONArray(LastAddress.toString());
        LastKnownAddress = LastAddressData.getJSONObject(0).getString("LastKnownAddress");
        Log.d("TAG", "Note: LAST KNOWN ADDRESS: " + LastKnownAddress);

        //Getting the stored message.
        for (int i = 0; i < data.length(); i++) {
            Message = data.getJSONObject(i).getString("Message");
            MsgInfo.append(Message.trim());
            Log.d(TAG, "Note: MsgInfo: " + MsgInfo);
        }

        // Add the location to the message if the location was found.
        MsgInfo.append("\n");
        // If address cant be found, use the last known address.
        if (finalAddress.toString().trim().length() > 0)
        {
            // Win Win Win.
            MsgInfo.append("This person was last seen at: \n" + finalAddress.toString());
        }
        else if (LastKnownAddress.trim().length() > 0)          // if last known address was found.
        {
            // When user never turns on location, but then turns it off.
            finalAddress.append(LastKnownAddress);
            MsgInfo.append("Although we are not completely sure of the location of this person,\n" +
                    "our app last detected him here: " + finalAddress.toString());
        }
        else                                                    // if no address was found.
        {
            // When user never turns on Location.
            MsgInfo.setLength(0);
            MsgInfo.append(Message + "\n");
            MsgInfo.append("No recent Location of this person was found.");
        }
        //Getting the phone numbers.
        for (int i = 0; i < data.length(); i++) {
            phoneNumbers = data.getJSONObject(i).getString("PhoneNumbers");
            PhoneInfo.append(phoneNumbers + "\n");
            Log.d(TAG, "Note: MsgInfo: " + PhoneInfo);
        }
        Log.d("TAG", "Note: phone numbers: " + phoneNumbers);
        Log.d("TAG", "Note: Message: " + Message);
        Log.d(TAG, "Note: got arrsize");
        arrSize = data.getJSONObject(0).getInt("ArraySize");

    }

    /*
    * Name: sendBulkMessages
    * Objective: This method sends messages out to your emergency contact/s
    * */
    public void SendBulkMessages() throws IOException, JSONException
    {
        Log.d("TAG", "Note: in SendinBulkMessages");
        String currNumber = "";
        String Message = "";
        String PhoneNumbers = "";
        readFile();                                         // Read the saved data.

        if (MsgInfo != null)                                // If the message existed, get the message.
        {
            Log.d("TAG", "MsgInfo is not null");
            Message = MsgInfo.toString();
        }

        if (PhoneInfo != null)                              // If phone numbers existed,
        {                                                   // get the phone numbers.
            Log.d("TAG", "PhoneInfo is not null");
            PhoneNumbers = PhoneInfo.toString();
        }

        if (PhoneNumbers.trim().length() > 0)               // If the phone numbers are there, then
        {                                                   // Send the messages.
            Log.d("TAG", "Sending Messages");
            Log.d("TAG", "PhoneNumbers: " + PhoneNumbers);
            for (int k = 0; k < PhoneInfo.length(); k++)    // Find all the phone numbers and send
            {                                               // them an emergency message.
                char ch = PhoneInfo.charAt(k);
                currNumber = currNumber + ch;
                if (ch == '\n')                             // if "\n" new phone number.
                {
                    Log.d(TAG, "Note: Current number = " + currNumber);
                    if (currNumber.trim().length() > 0) {
                        SendSingleMessage(currNumber, Message);
                        currNumber = "";
                        k++;
                    }
                }
            }
            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "No Emergency Contact Found, Please Configure", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * Name: SendSingleMessage
    * Objective: This message sends a single textmessage to your contact*/
    public void SendSingleMessage(String phoneNumber, String Message) {
        SmsManager currSms = SmsManager.getDefault();
        currSms.sendTextMessage(phoneNumber, null, Message, null, null);
    }

    /*
    * This method kicks in when the alert Button is clicked and the people want to send the text messages
    * */
    public void AlertClicked(View view) throws IOException, JSONException {
        SendBulkMessages();
    }

    /*
    * Name: ConfigureClicked
    * Objective: This method kicks in when the configure button is clicked.  It is responsible for moving to
    * the next intent and screen.  Also transfer data from other activities.
    * */
    public void ConfigureClicked(View view)
    {
        Intent ConfigureIntent = new Intent(MainActivity.this, configureScreen.class);
        ConfigureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(new Intent(MainActivity.this, configureScreen.class));
        startActivity(ConfigureIntent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (!LOCATION_FOUND) {
            FindAddress();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}
