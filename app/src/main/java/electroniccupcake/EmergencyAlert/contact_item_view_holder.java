package electroniccupcake.EmergencyAlert;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;
import electroniccupcake.projectalert.R;

/**
 * Created by Harsh on 7/7/2016.
 *
 * This class is a holder for the contact list, it's purpose is to set the name, phone number
 * and the picture in the view.
 */
public class contact_item_view_holder extends RecyclerView.ViewHolder
{
    private TextView contact_name;              // The contact name.
    private TextView phoneNumber;               // The phone number.
    private CircleImageView picture;            // The picture.

    public contact_item_view_holder(View itemView)
    {
        super(itemView);
        contact_name = (TextView) itemView.getRootView().findViewById(R.id.user_phone_name);
        phoneNumber = (TextView) itemView.getRootView().findViewById(R.id.user_phone_number);
        picture = (CircleImageView) itemView.getRootView().findViewById(R.id.user_photo);
    }

    public void setName(String name)
    {
        this.contact_name.setText(name);
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber.setText(phoneNumber);
    }

    public void setPicture(Uri picture)
    {
        if(picture != null)
        {
            this.picture.setImageURI(picture);
        }
        else
        {
            this.picture.setImageResource(R.drawable.ic_face_white_48dp);
        }
    }

}
