package electroniccupcake.EmergencyAlert;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import electroniccupcake.projectalert.R;

// The emergency contact screen.
public class emergency_contacts_fragment extends Fragment
{
    private static final int PICK_CONTACT_REQUEST = 1;      // the code for picking contacts..
    private FloatingActionButton addContacts;               // Floating action button for contacts.
    private contacts_list_adapter adapter;

    public emergency_contacts_fragment()
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

    // Inflating the view for the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.emergency_contacts_fragment,container,false);
        addContacts = (FloatingActionButton) view.findViewById(R.id.add_contacts_fab);

        addContacts.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    pickContact();
                }
            }
        );
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
        // Setting the recycler view, the adapter and the item callback.
        RecyclerView contact_list = (RecyclerView)view.findViewById(R.id.recycler_list);
        adapter = new contacts_list_adapter();
        adapter.setUpConnection();

        ItemTouchHelper.Callback callback;
        ItemTouchHelper helper;

        contact_list.hasFixedSize();
        contact_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        contact_list.setAdapter(adapter);

        callback = new contacts_list_view_helper(adapter);
        helper = new ItemTouchHelper(callback);

        helper.attachToRecyclerView(contact_list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        adapter.resetConnection(this.getContext());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        adapter.unBinService(this.getContext());
    }

    @Override
      /*
    * This code takes phone numbers from the users phone and puts the mobile phone number on the Add contact edit text.
    * ** Note: A small part of the code for this method is inspired from
    *    : http://developer.android.com/training/basics/intents/result.html
    *    Date: 06/08/2015
    *    By Harsh Patel
    * */

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        String number;
        String name;
        Uri photoURI;

        int numberCol;
        int nameCol;
        int photoCol;

        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST)
        {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK)
            {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();

                // To query the contacts app for the Display name, the photo thumb nail and the phone number.
                String[] projection =
                                    {ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor cursor = getActivity().getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                numberCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                nameCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                photoCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);

                number = cursor.getString(numberCol);
                name = cursor.getString(nameCol);
                if(cursor.getString(photoCol) != null)
                {
                    photoURI  = Uri.parse(cursor.getString(photoCol));
                }
                else
                {
                    photoURI =  Uri.parse("android.resource://"+getView().getContext().getPackageName()+"/" + R.drawable.ic_face_white_48dp);
                }
                adapter.addContactToList(new Node(number,name,photoURI), -999);
                Log.i("TAG","Photo: " + photoURI);

            }
        }
    }

}
