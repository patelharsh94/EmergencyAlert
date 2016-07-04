package electroniccupcake.EmergencyAlert;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.natasa.progressviews.CircleProgressBar;
import com.natasa.progressviews.utils.OnProgressViewListener;

import electroniccupcake.projectalert.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class main_content extends Fragment {

    private static CircleProgressBar alert_button;
    private boolean isTouching;
    private double final_time;
    private static final String TAG = "TAG";
//    private AlertAnimationProcessingThread alertAnimationProcessingThread;
    private AlertAnimationUpdateReciever animation_reciever;

    public main_content()
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


    // Inflating the view for the fragment.
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.alert_fragment,container,false);
        IntentFilter filter = new IntentFilter(AlertAnimationUpdateReciever.EA_ANIM_REC_ADDR);
        animation_reciever = new AlertAnimationUpdateReciever();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(animation_reciever,filter);

        final_time = 30.0;
        //alertAnimationProcessingThread = new AlertAnimationProcessingThread();
        alert_button = (CircleProgressBar) view.findViewById(R.id.alert_timer);

        alert_button.setText("Alert!", Color.WHITE);


        alert_button.setOnTouchListener(new View.OnTouchListener()
        {
            Intent service_intent;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    isTouching = true;
                    AlertProcessingService.setIsTouching(isTouching);
                    // alertAnimationProcessingThread.execute(final_time);
                    service_intent = new Intent(getActivity(),AlertProcessingService.class);
                    service_intent.putExtra(getContext().getString(R.string.Final_Time), final_time);
                    getActivity().startService(service_intent);
                }
                // finger is not touching
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    isTouching = false;
                    AlertProcessingService.setIsTouching(isTouching);
                }
                return false;
            }
        });

        return view;
    }

    // This method can be used to set progress for the alert button.
    public static void setAlertProgress(float progress)
    {
        alert_button.setProgress(progress);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    public class AlertAnimationUpdateReciever extends BroadcastReceiver
    {
        public static final String EA_ANIM_REC_ADDR = "com.electroniccupcake.EmergencyAlert.AlertAnimationUpdateRecieverAddress";
        double prev_progress = 0.0;
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float progress;
            ObjectAnimator animator;
            progress = intent.getFloatExtra(context.getString(R.string.Progress_Val),0f);

            if(progress > prev_progress)
            {
                Log.i(TAG,"Recieved: " + progress);
                prev_progress = progress;
                alert_button.setProgress(progress);
            }
        }
    }
}
