package electroniccupcake.EmergencyAlert;

import android.net.Uri;

import electroniccupcake.projectalert.R;

/*
* Created by harsh on 5/31/15.
* This class holds all the info about a node(one cell of the phone number list in the configure page.
* */
public class Node
{
    String phoneNumber;                 // The phone number
    String contactName;                 // The contact name.
    Uri contactPhoto;                   // The contact photo.

    Node()
    {

    }

    Node(String phoneNumber, String contactName, Uri contactPhoto)
    // PRE: phoneNumber and val must be initialized.
    // POST: Initialized a node object with the class member phoneNumber set to phoneNumber and
    //       class member val set to val.
    {
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
        this.contactPhoto = contactPhoto;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getContactName()
    {
        return contactName;
    }

    public Uri getContactPhoto()
    {
        return contactPhoto;
    }

}
