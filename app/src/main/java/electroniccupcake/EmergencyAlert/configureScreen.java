package electroniccupcake.EmergencyAlert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import electroniccupcake.projectalert.R;

/*
* This class holds the code for the configuration screen.
* Written by Harsh Patel.
* */
public class configureScreen extends ActionBarActivity
{

    final int PICK_CONTACT_REQUEST = 1;  // The request code
    private static int i = 0;                           // Incrementer.
    private static Button AddButton;                    // The add button.
    private static Button DeleteButton;                 // The delete button.
    private static Button BrowseButton;                 // The browse button.
    private static EditText AddContact;                 // The field with the contact number.
    private static EditText Message;                    // The emergency message.
    private static StringBuffer  MsgInfo;               // A string buffer for getting the message.
    private static ListView contactList;                // The list with the phone numbers.
    private static Node [] phoneNumberArr;              // The array of the phone numbers.
    String TAG;                                         // Just for log messages.

    /*
     * Name: Initialize
     * Objective: This method basically Initializes everything.
     */
    public void Initialize()
    {
        MsgInfo = new StringBuffer();
        AddButton = (Button)findViewById(R.id.AddButton);
        DeleteButton = (Button)findViewById(R.id.DeleteButton);
        BrowseButton = (Button)findViewById(R.id.browseButton);
        AddContact = (EditText)findViewById(R.id.AddContact);
        Message = (EditText)findViewById(R.id.Message);
    }

    /*
    * Name: pickContact()
    * Objective: This function just initialized the contacts app.
    * **Note: a major portion of the code was taken from: http://developer.android.com/training/basics/intents/result.html
    *   Date: 06/08/2015
    * */
    private void pickContact()
    {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.setTitle("Configure");

        super.onCreate(savedInstanceState);
        SharedPreferences ConfigPref;
        boolean InfoSaved;

        i = 1;
        setContentView(R.layout.activity_configure_screen);
        Initialize();                                              // Initialize all the GUI elements.
        // Setting the preference for this activity.
        // setting shared preferences for the main activity

        ConfigPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        InfoSaved = ConfigPref.getBoolean("ConfigIsStarted", false);
        contactList = (ListView)findViewById(R.id.contactList);

        phoneNumberArr = new Node[1];
        phoneNumberArr[0] = new Node("",0);


        /*
        * This is the code for the Add button, it is responsible for re-programming the list, and
        * dynamic array allocation.
        * */
        AddButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if(AddContact.length() > 0)
                            addClicked(AddButton);
                    }
                }
        );
        /*
        * This is the function for the delete button
        * it would clear the data and resize the array
        * */
        DeleteButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        deleteClicked(DeleteButton);
                    }
                }

        );

        /*
        * This is the browse button, all it does is call the pick contact method
        * */
        BrowseButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        pickContact();
                    }
                }
        );

        final AlertAdapter newAdapter = new AlertAdapter(this,phoneNumberArr);
        contactList.setAdapter(newAdapter);
        // if the configure was not previously started...
        if (!InfoSaved) {

        }
        else
        {
            try {
                resetInfo();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    /*
    * Name:addClicked
    * Objective: This method is responsible for re-programming the list and dynamic array allocation.
    * */
    public void addClicked(View view)
    {
        final AlertAdapter newAdapter;                      // Array adapter handel for the list.
        Log.d(TAG,"Note: in addClicked");
        int k = 0;
        // resetting i
        if(i == 0)
        {
            i = 1;
        }

        newAdapter = new AlertAdapter(configureScreen.this, phoneNumberArr);
        contactList.setAdapter(newAdapter);
        Log.d(TAG, "Note: adapter has been set");

        if (!AddContact.getText().toString().matches(""))           // If there is something in the
        {                                                           // phoneNumber box.

            String val = AddContact.getText().toString();           // The new phone number.
            Node[] newArr;                                          // The new array of phonenumbers.
            AddContact.setText("");
            Log.d(TAG,"Note: i = " +i );
            Log.d(TAG,"Note: val = "+ val);
            phoneNumberArr[i-1] = new Node(val, 0);                // Allocating the new node.
            Log.d(TAG,"Note: phoneNumberArr has been created.");
            //i++;
            k++;
            // Dynamically allocate more space and store the new phone number along with the old ones.
            if (k == 1) {
                newArr = new Node[i];
                for (int j = 0; j < i; j++) {           // copying the old array into the new array.
                    newArr[j] = phoneNumberArr[j];
                }
                phoneNumberArr = new Node[i + 1];
                for (int j = 0; j < i; j++) {
                    phoneNumberArr[j] = newArr[j];      // restoring the old array with the newValues.
                }
                i++;
                k = 0;
            }


        }
        else                                          // If there is no phone numbers written.
        {
            AddContact.setError("You need to write a phone number.");
        }
    }

    /*
    * Name: deleteClicked
    * Objective: This method runs when the delete button is clicked.  It delete the selected information, de-allocates
    * and re-allocates the array, and re-programs the list.
    * */
    public void deleteClicked(View view)
    {
        int j;
        int k = 0;
        int newArrSize = 0;
        Node[] newArr;
        Log.d("TAG", "Note: Arr length = " + phoneNumberArr.length);

        // Go through the array and find the size based on the number of phone numbers that the user
        // does not want to delete.
        for(j = 0;j < i-1;j++)
        {
            Log.d("TAG","Note: in the first loop j = "+j);
            // find the size of the new array.
            if(phoneNumberArr[j].getVal() == 0)
            {
                newArrSize++;
            }
        }

        newArr = new Node[newArrSize];                      // New array has the new size.

        // populate the new array..
        for(j = 0; j< i-1; j++)
        {
            Log.d("TAG","Note: in the second loop j = "+j);
            if(phoneNumberArr[j].getVal() == 0)
            {
                newArr[k] = phoneNumberArr[j];
                k++;
            }
        }

        // re populate the older array..
        phoneNumberArr = new Node[newArrSize+1];
        Log.d("TAG","the new array length is: " + phoneNumberArr.length);

        for(j = 0; j < newArrSize; j++)
        {
            Log.d("TAG","Note: in the third loop j = "+j);
            phoneNumberArr[j] = newArr[j];
        }
        // resetting the array size
        i = newArrSize+1;
        phoneNumberArr[newArrSize] = new Node("",0);
        if(i == 0)      // if the size is 0.
        {
            phoneNumberArr = new Node[1];
            phoneNumberArr[0] = new Node("",0);

        }
        // make a new adapter and set the adapter.
        final AlertAdapter newAdapter = new AlertAdapter(configureScreen.this, phoneNumberArr);
        contactList.setAdapter(newAdapter);
    }
    /*
    * Name: savingInfo()
    * Objective: It saves all the phone numbers in the list view,
    * and it saves the message in a jason array(On Internal Memory).
    * **Note: part of this method was taken from: http://www.lynda.com/Android-tutorials/Creating-reading-JSON-data-files/112584/121170-4.html
    *   Date: 06/10/2015
    * */
    public void savingInfo()
    {
        JSONArray data;                                 // Array of all the phone numbers
        JSONObject currentMsgInfo;                      // The message
        String currentNumber;                           // The current number.

        currentMsgInfo = new JSONObject();
        data = new  JSONArray();

        try
        {
            // saving the message
            // First reset the info..
            currentMsgInfo.put("Message", "");
            currentMsgInfo.put("PhoneNumbers","");
            currentMsgInfo.put("Message", Message.getText().toString());
            StringBuffer PhoneNumberArrInfo = new StringBuffer();

            for(int j = 0; j < i-1; j++)                    // Appending all the phone numbers in
            {                                               // the string buffer.
                // saving the phone numbers
                currentNumber = phoneNumberArr[j].getPhoneNumber() + "\n";
                PhoneNumberArrInfo.append(currentNumber);
            }

            currentMsgInfo.put("PhoneNumbers",PhoneNumberArrInfo);
            // saving the array size;
            currentMsgInfo.put("ArraySize",i);
            data.put(currentMsgInfo);                       // Storing all the phone numbers in the JSON array.
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String AllInfo = data.toString();                   // Re-writing the file.
        Log.d("TAG","Note: Just saved " + AllInfo);
        try {
            FileOutputStream fos = openFileOutput("AppDataAlert", MODE_PRIVATE);
            // rewrite the file..
            getFilesDir();
            fos.write(AllInfo.getBytes());
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    /*
    * Name: ReturnClicked
    * Objective: This method runs when the return button is clicked.  It is suppose to return to main activity and
    * also return some information to the main activity.
    * */
    public void ReturnClicked(View view)
    {
        savingInfo();
        Intent ReturnIntent = new Intent(configureScreen.this,MainActivity.class);
        ReturnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(ReturnIntent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

    }

    /*
    * Name: resetInfo()
    * Objective: This method basically retrieves all the data saved on the file, and resets it, such that it seems
    * as if the view has been recalled. It is launched on create if the save button has been pressed once.
    */
    public void resetInfo() throws IOException, JSONException
    {
        FileInputStream fis = openFileInput("AppDataAlert");        // For reading info from the saved file.
        Log.d(TAG,"Note:file is open");
        BufferedInputStream bis = new BufferedInputStream(fis);     // The buffer for the input from the file.
        StringBuffer b = new StringBuffer();                        // stores all the phone numbers.
        String currNumber= new String();                            // The current phone number.
        String Msg;                                                 // The message
        String phoneNumbers;                                        // The list of phone numbers.
        JSONArray data;                                             // The data previously stored.
        int currIndex = 0;                                          // Current index of the array.
                                                                    // resetting the array.
        phoneNumberArr = new Node[i+1];
        phoneNumberArr[i] = new Node("",0);

        // Go until there is stuff avaliable in the input and add it to the string..
        while (bis.available() != 0)
        {
            char c = (char) bis.read();
            b.append(c);
        }
        bis.close();
        fis.close();
        Log.d(TAG, "Note: buffer and file closed");
        data = new JSONArray(b.toString());

        Msg = data.getJSONObject(0).getString("Message");               // Get all the info from the field Message.
        MsgInfo = new StringBuffer();
        MsgInfo.append(Msg + "\n");
        Log.d(TAG,"Note: MsgInfo: "+MsgInfo);
        //Getting the stored message.
        Message.setText(MsgInfo);
        phoneNumbers = data.getJSONObject(0).getString("PhoneNumbers");// Get all the info from the field PhoneNumbers.

        //Getting the phone numbers.
        for(int k = 0; k < phoneNumbers.length();k++)
        {
            char ch = phoneNumbers.charAt(k);
            currNumber = currNumber + ch;
            if(ch == '\n')                  // If you see a \n, then we have a new phone number.
            {
                phoneNumberArr[currIndex] = new Node("",0);
                Log.d(TAG,"Note: Current number = " + currNumber);
                AddContact.setText(currNumber);
                currIndex++;
                addClicked(AddButton);
                currNumber = "";
                k++;
            }
        }
        currIndex = 0;

    }

    /*
    * This code takes phone numbers from the users phone and puts the mobile phone number on the Add contact edit text.
    * ** Note: a major part of the code was taken from: http://developer.android.com/training/basics/intents/result.html
    *    Date: 06/08/2015
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result

                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                AddContact.setText(number);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configure_screen, menu);
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
}
