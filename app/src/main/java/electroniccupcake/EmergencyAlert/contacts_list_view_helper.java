package electroniccupcake.EmergencyAlert;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

/**
 * Created by Harsh on 7/10/2016.
 *
 * This class serves as a middle man between the adapter and the UI.
 */
public class contacts_list_view_helper extends ItemTouchHelper.SimpleCallback
{
    contacts_list_adapter adapter;                  // The adapter.

    public contacts_list_view_helper(contacts_list_adapter adapter)
    {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    // This method calls the adapters implementation of swap.
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
    {
        adapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    // This method calls the adapters implementation of onRemove.
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {
        Snackbar snackbar;
        int pos = viewHolder.getAdapterPosition();
        snackbar = Snackbar.make(viewHolder.itemView, adapter.getItemAtPosition(pos) + " was deleted", Snackbar.LENGTH_LONG);

        snackbar.setAction("Undo", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                adapter.resetList();
            }
        });

        snackbar.show();
        adapter.remove(pos);
    }

}
