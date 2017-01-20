package electroniccupcake.EmergencyAlert;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import electroniccupcake.projectalert.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class message_fragment extends Fragment {

    public EditText message;
    public SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public message_fragment() {
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
        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Inflating the view for the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_fragment,container,false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        message = (EditText) getActivity().findViewById(R.id.emergency_message);

        if(!prefs.getString("com.emerencyAlert.message","none").equals("none"))
        {
            message.setText(prefs.getString("com.emerencyAlert.message","none"));
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(message.getText() != null)
        {
            editor.putString("com.emerencyAlert.message",message.getText().toString());
            editor.commit();
        }
    }
}
