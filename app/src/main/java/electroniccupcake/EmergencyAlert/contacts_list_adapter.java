package electroniccupcake.EmergencyAlert;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import electroniccupcake.projectalert.R;


/**
 * Created by Harsh on 7/7/2016.
 *
 * This class will be the view adapter for the contact list.
 */
public class contacts_list_adapter extends RecyclerView.Adapter<contact_item_view_holder>
{

    private List<Node> items = new ArrayList<>();         // The list of contacts
    private contact_item_view_holder holder;                    // Holder for the contacts
    private View view;
    private int deletedPos;                                     // Info about deleted positions.
    private String deletedName;
    private String deletedNumber;
    private Uri deletedUri;
    private DBMInterface contacts_server;                       // The server for contact information.
    private boolean isBound;
    private ServiceConnection connection;

    public contacts_list_adapter()
    {

    }

    // This method creates a view and makes a new view holder, for the recycler view.
    @Override
    public contact_item_view_holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        this.view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contacts,parent,false);
        this.holder =  new contact_item_view_holder(view);
        return holder;
    }

    // This method binds the holder with the data it needs, such as name, picture and phone number.
    @Override
    public void onBindViewHolder(contact_item_view_holder holder, int position)
    {
        holder.setName(items.get(position).getContactName());
        holder.setPhoneNumber(items.get(position).getPhoneNumber());
        holder.setPicture(items.get(position).getContactPhoto());
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }


    // This method is called on swip, and it removes the item at the position pos.
    public void remove(int pos)
    {
        deletedPos = pos;
        deletedName = items.get(pos).getContactName();
        deletedNumber = items.get(pos).getPhoneNumber();
        deletedUri = items.get(pos).getContactPhoto();
        items.remove(pos);
        notifyItemRemoved(pos);
        notifyDataSetChanged();
        // updating the server.
        try
        {
            if(isBound)
            {
                contacts_server.deleteFromDB(deletedName);

                // reset the index.
                for(int i = 0; i < items.size(); i++)
                {
                    contacts_server.setIndex(items.get(i).getContactName(),i);
                }

            }
            else
                Log.i("TAG","Service not bounded, cannot delete from DB");
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }

    }

    public String getItemAtPosition(int pos)
    {
        return items.get(pos).getContactName();
    }

    public void resetList()
    {
        addContactToList(new Node(deletedNumber,deletedName,deletedUri),deletedPos);

        notifyItemInserted(deletedPos);
    }

    // This method is called when the item is moved.
    public void swap(int from, int to)
    {
        Collections.swap(items,to,from);
        notifyItemMoved(to,from);

        // updating the server.
        try
        {
            if(isBound)
            {
                // swapping index in the db
                Log.i("TAG","Swapping: " + items.get(to).getContactName() + " at position: " + from +
                      " with " + items.get(from).getContactName() + " at position " + to);
                contacts_server.setIndex(items.get(to).getContactName(), to);
                contacts_server.setIndex(items.get(from).getContactName(), from);
            }
            else
            {
                Log.i("TAG","Service is not bound, cannot swap index.");
            }
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    // Adds contacts to the list.
    public void addContactToList(Node node, int location)
    {
        if(location != -999)
        {
            items.add(location,node);
            notifyItemInserted(location);
        }
        else
        {
            items.add(getItemCount(),node);
            notifyItemInserted(getItemCount());
        }
        notifyDataSetChanged();

        // inserting into db
        try
        {
            if(isBound)
            {    // inserting into db
                contacts_server.insertIntoDB(node.getContactName(), node.getPhoneNumber(), String.valueOf(node.getContactPhoto()), getItemCount()-1);
            }
            else
            {
                Log.i("TAG","Service is not bound, cannot insert into db");
            }
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    // resets the connection.
    public void resetConnection(Context context)
    {
        if(!isBound)
        {
            Intent service_intent = new Intent(DBMInterface.class.getName());
            ResolveInfo info = context.getPackageManager().resolveService(service_intent, Context.BIND_AUTO_CREATE);
            service_intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            if(connection != null)
            {
                context.startService(service_intent);
                context.bindService(service_intent, connection, Context.BIND_AUTO_CREATE);
                Log.i("TAG","Connection was not null, binding service.");
            }
        }
    }

    public void unBinService(Context context)
    {
        context.unbindService(connection);
    }

    public void setUpConnection()
    {
       connection =  new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                contacts_server = DBMInterface.Stub.asInterface(service);

                if(contacts_server != null)
                {
                    isBound = true;
                    Log.i("TAG", "Connection established, service is bounded");
                    resetListFromDB();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                contacts_server = null;
                isBound = false;
                Log.i("TAG","Service is disconnected");
            }
        };

    }

    public void resetListFromDB()
    {
        Log.i("TAG","List is getting resetted");
        String [] nameList = new String[]{};
        String [] numberList  = new String[]{};
        String [] URIList  = new String[]{};

        int [] index_list = new int [] {};
        try
        {
            nameList = contacts_server.getNameList();
            numberList = contacts_server.getPhoneNumberList();
            URIList = contacts_server.getURIList();
            index_list = contacts_server.getIndexList();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        items = new ArrayList<>();

        for(int i = 0; i < index_list.length; i++)
        {
            items.add(new Node());
        }

        Log.i("TAG","nameList Length: " + nameList.length
                + "\nnumberList Length: " + numberList.length
                + "\nindex_list Length: " + index_list.length
                + "\nURLList Length: " + URIList.length);

        for(int i: index_list)
            Log.i("TAG"," index_list: " + i);

        for(int i = 0; i < nameList.length; i++)
        {
            Uri uri;
            // Check if there is a photo avaliable.
            if(URIList[i] != null)
            {
                uri = Uri.parse(URIList[i]);
                Log.i("URI","Uri: " + uri);
            }
            else
            {
                uri = Uri.parse("android.resource://"+view.getContext().getPackageName()+"/" + R.drawable.ic_face_white_48dp);
            }
            items.set(index_list[i],new Node(numberList[i],nameList[i], uri));
            notifyItemInserted(items.size());
        }

    }
}
