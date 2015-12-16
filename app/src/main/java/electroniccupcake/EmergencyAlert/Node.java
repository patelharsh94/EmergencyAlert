package electroniccupcake.EmergencyAlert;

import android.util.Log;

/*
* Created by harsh on 5/31/15.
* This class holds all the info about a node(one cell of the phone number list in the configure page.
* */
public class Node
{
    String phoneNumber = "";                // The phone number
    String TAG;                             // The TAG for log messages.
    int val = 0;                            // if val = 0, non-checked, else checked..

    Node(String phoneNumber, int val)
    // PRE: phoneNumber and val must be initialized.
    // POST: Initialized a node object with the class member phoneNumber set to phoneNumber and
    //       class member val set to val.
    {
        this.phoneNumber = phoneNumber;
        this.val = val;
        Log.d(TAG,"Note: the node was created!!!");
    }

    public String getPhoneNumber()
    // POST: FCTVAL == phoneNumber.
    {
        return phoneNumber;
    }

    public int getVal()
    // POST: FCTVAL == val.
    {
        return val;
    }

    public void setVal(int value)
    // PRE: value must be initialized.
    // POST: sets the class member val to be value.
    {
        val = value;
    }
}
