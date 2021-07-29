package customer.gajamove.com.gajamove_customer.notifications;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.legacy.content.WakefulBroadcastReceiver;


/**
 * Created by Asim Shahzad on 1/11/2018.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.

        try {
            /*ComponentName comp = new ComponentName(context.getPackageName(),
                    FcmMessagingService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
            Toast.makeText(context, "Service started", Toast.LENGTH_SHORT);*/

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
