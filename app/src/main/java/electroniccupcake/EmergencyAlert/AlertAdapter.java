package electroniccupcake.EmergencyAlert;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import electroniccupcake.projectalert.R;

/**
 * Created by harsh on 5/31/15.
 *
 * This is an array adapter, basically the programming for one of the cells in our array of
 * phone numbers.
 */

public class AlertAdapter extends ArrayAdapter
{
        Node [] items = null;           // All the items in the list.
        Context context;                // The place this array is in.

        public AlertAdapter(Context context, Node [] resources)
        // PRE: context and resources must be initialized
        // POST: Initializes an AlertAdapter object with the class member context set to context
        //       and the class member items set to resources.
        {
            super(context, R.layout.row, resources);
            this.context = context;
            this.items = resources;
        }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    // PRE: position, convertView, and parent must be initialized.
    // POST: Returns a converted view, after taking into account the selected phone numbers
    //       by the user.
    {
        /* Declaring and initializing GUI elements. Getting a handle to the current context,
        and layouts.*/
        LayoutInflater customInflater = ((Activity)context).getLayoutInflater();
        convertView = customInflater.inflate(R.layout.row,parent,false);
        TextView number = (TextView) convertView.findViewById(R.id.phoneNumberText);
        final CheckBox checker = (CheckBox) convertView.findViewById(R.id.checker);

        Log.d("TAG", "Position: " + position);

        // If the current item has a value of 0, they are not set to checked else checked.
        //if(items[position].getVal() == 0)
        //    checker.setChecked(false);
        //else
        //    checker.setChecked(true);

        number.setText(items[position].getPhoneNumber());       // set the field number to be the
        // Setting the check box..                              // phone number of the current item.
        checker.setOnCheckedChangeListener(                     // If checked, set value to 1 else 0.
               new CompoundButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                      /* if(isChecked == true)
                           items[position].setVal(1);
                       else
                           items[position].setVal(0);*/
                   }
               }
        );

        return convertView;

    }
}