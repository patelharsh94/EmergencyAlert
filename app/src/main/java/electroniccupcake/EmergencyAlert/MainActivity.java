package electroniccupcake.EmergencyAlert;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import electroniccupcake.projectalert.R;
import old_code.configureScreen;

/*
        Help for creating this file was taken from:
        URL:https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer
        by: Harsh Patel, 6/19/2016

        Author: Harsh Patel
*/

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // private static TextView Alert;
    private static String TAG;                          // For log messages
    private static StringBuffer MsgInfo;                // The message
    private static StringBuffer PhoneInfo;              // The list of phone numbers.

    public static GoogleApiClient mGoogleApiClient;    // Handel to the googleApi
    private static int arrSize = 0;                     // The size of the array.
    private DrawerLayout drawer_layout;                 // To setup the drawer
    private Toolbar toolbar;                            // toolbar
    private NavigationView nav_view;                    // Drawer navigation
    private ActionBarDrawerToggle drawerToggler;        // to toggle the drawer
    private GoogleApiClient client;
    private Fragment fragment = null;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private int currentMenuItem;
    public static boolean isConnected = false;
    /*
     * This method basically Initializes everything.
     * */
    public void Initialize() {
        MsgInfo = new StringBuffer();
        PhoneInfo = new StringBuffer();
   //     AlertButton = (Button) findViewById(R.id.AlertButton);

        toolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
        setSupportActionBar(toolbar);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_view = (NavigationView) findViewById(R.id.navigation_drawer_main);
        setUpDrawer(nav_view);
    // Setting up the toggler and adding it to the layout.
        drawerToggler = new ActionBarDrawerToggle(this,drawer_layout,toolbar,R.string.app_name,R.string.app_name);
        drawer_layout.addDrawerListener(drawerToggler);
    }


    // This method will work towards setting up the drawer and the client that
    // will switch out the drawer views.
    private void setUpDrawer(NavigationView view)
    {
        view.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item)
                    {
                        switchViewsWithSelectedItem(item);
                        return true;
                    }
                }
        );
    }

    // This method will switch the fragments based on the fragments selected.
    public void switchViewsWithSelectedItem(MenuItem item)
    {
        Class frag_class;
        int id = item.getItemId();
        currentMenuItem = id;
        Log.i("TAG","GOT ID: " + id);
        switch (id)
        {
            // switching based on the id's selected.
            case R.id.home:
                frag_class = main_content.class;
                break;

            case R.id.emergencyMessageFragment:
                frag_class = message_fragment.class;
                break;
            case R.id.emergency_contacts:
                frag_class = emergency_contacts_fragment.class;
                break;

            default:
                frag_class = main_content.class;
        }

        try
        {
            fragment = (Fragment) frag_class.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // replacing the fragments
        fragmentManager.beginTransaction().replace(R.id.main_content_fl,fragment).commit();

        item.setChecked(true);

        // close the drawer.
        drawer_layout.closeDrawers();
    }
    // Starting the geo location service and getting the final address.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle("Emergency Alert");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        LocationCheck();

        Class main_class = main_content.class;
        // Start the setup activity as soon as the app start&, but only when the app is first start.
        // This part was inspired from: http://stackoverflow.com/questions/7238532/how-to-launch-activity-only-once-when-app-is-opened-for-first-time
        // Date: 06/11/2015

        // setting shared preferences for the main activity
        SharedPreferences MainPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean ConfigIsStarted = MainPref.getBoolean("ConfigIsStarted", false);
        /*Intent messageService = new Intent(this,MessageSenderService.class);
        startService(messageService);
        */

        // if the configure was not previously started save it in the system preferences.
        if (!ConfigIsStarted) {
            Log.d("TAG", "Note: this is the first run for main.");
            startActivity(new Intent(MainActivity.this, configureScreen.class));
            SharedPreferences.Editor edit = MainPref.edit();
            edit.putBoolean("ConfigIsStarted", Boolean.TRUE);
            edit.commit();
        }
        Initialize();                                       // Initialize everything.

        switchViewsWithSelectedItem(nav_view.getMenu().getItem(0));     // Initially set to home.

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // This method is used to connect to Google's APIs
    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    // on back pressed, go back to home.
    public void onBackPressed()
    {
        // if on alert screen and drawer is closed, open the drawer.
        if(currentMenuItem == R.id.home && !drawer_layout.isDrawerOpen(nav_view))
        {
            drawer_layout.openDrawer(nav_view);
        }
        // if the drawer is open, close the drawer
        else if (drawer_layout.isDrawerOpen(nav_view))
        {
            drawer_layout.closeDrawer(nav_view);
        }
        // if pressed back and not one alert screen go to the alert screen.
        else
        {
            switchViewsWithSelectedItem(nav_view.getMenu().getItem(0));
        }
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerToggler.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggler.syncState();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConfigurationChanged(Configuration config)
    {
        super.onConfigurationChanged(config);
        drawerToggler.onConfigurationChanged(config);
    }

    @Override
    public void onConnected(Bundle bundle) {
        isConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}
