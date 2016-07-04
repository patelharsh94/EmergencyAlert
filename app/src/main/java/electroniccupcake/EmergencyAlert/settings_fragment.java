package electroniccupcake.EmergencyAlert;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import electroniccupcake.projectalert.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class settings_fragment extends Fragment {


    public settings_fragment() {
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

    // Inflating the view for the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
}
