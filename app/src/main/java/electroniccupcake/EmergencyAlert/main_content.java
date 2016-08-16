package electroniccupcake.EmergencyAlert;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.natasa.progressviews.CircleProgressBar;
import com.natasa.progressviews.utils.OnProgressViewListener;
import electroniccupcake.projectalert.R;

// The main alert screen
public class main_content extends Fragment {

    private static CircleProgressBar alert_button;          // The main alert button
    private double final_time;
    private static final String TAG = "TAG";
    private AlertAnimationProcessingThread alertAnimationProcessingThread;
    private TextView alert_inst;

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

    /*
    * The alert button is an object written in the library progressViewLib
    * library was offered for free on: https://android-arsenal.com/details/1/3186
    * Source for the library can be found on: https://github.com/natasam/DemoProgressViewsLibApp
    * ** Some modifications to the library were made for better and smoother animations.
    *
    * By Harsh Patel,
    * 7/7/2016
    * */

    // Inflating the view for the fragment.
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.alert_fragment,container,false);
        alert_inst =(TextView)view.findViewById(R.id.alert_inst);
        alert_inst.setVisibility(view.INVISIBLE);
        final_time = 10.0;                  // 1 sec..
        alert_button = (CircleProgressBar) view.findViewById(R.id.alert_timer);
        alert_button.setTextSize(100);
        alert_button.setRoundEdgeProgress(true);
        alert_button.setStartPositionInDegrees(270);
        alertAnimationProcessingThread = new AlertAnimationProcessingThread();
        alert_button.setText("Alert!", Color.WHITE);


        alert_button.setOnTouchListener(new View.OnTouchListener()
        {
           // Intent service_intent;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    alert_inst.setVisibility(view.VISIBLE);
                    alertAnimationProcessingThread = new AlertAnimationProcessingThread();
                    alertAnimationProcessingThread.execute(final_time);

                }
                // finger is not touching
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    alert_inst.setVisibility(view.INVISIBLE);
                    alertAnimationProcessingThread.setIsTouching(false);
                }
                return false;
            }
        });


        // Just to check for change and respond correctly.
        alert_button.setOnProgressViewListener(new OnProgressViewListener()
        {
            @Override
            public void onFinish()
            {
                Toast.makeText(getActivity(),"Message Sent!",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"Toast sent on finish");
                sendMessage();
            }

            @Override
            public void onProgressUpdate(float progress)
            {
            }
        });
        return view;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void sendMessage()
    {
        Intent message = new Intent();
        IntentFilter intentFilter = new IntentFilter(getString(R.string.MessageSenderIntentName));
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        message.setAction(getString(R.string.MessageSenderIntentName));
        message.putExtra(MessageSender.SEND_MESSAGE_STR,"Hello Message");
        this.getContext().sendBroadcast(message);
        this.getActivity().registerReceiver(new MessageSender(), intentFilter);
    }

    // This method can be used to set progress for the alert button.
    public static void setAlertProgress(float progress, float prev_progress)
    {
        alert_button.setProgress(progress, prev_progress);
    }

    // This method can be used to set the text on the alert button.
    public static void setAlertText(String time)
    {
        alert_button.setText(time);
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
            progress = intent.getFloatExtra(context.getString(R.string.Progress_Val),0f);

            if(progress > prev_progress)
            {
                Log.i(TAG,"Recieved: " + progress);
                prev_progress = progress;
                alert_button.setProgress(progress, (float)prev_progress);
                // send message sent toast if the progress is sufficient

            }
        }
    }
}
