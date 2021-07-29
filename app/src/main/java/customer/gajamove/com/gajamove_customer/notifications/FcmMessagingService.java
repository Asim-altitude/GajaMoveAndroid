package customer.gajamove.com.gajamove_customer.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.core.app.NotificationCompat;
import customer.gajamove.com.gajamove_customer.HomeScreen;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Member;
import customer.gajamove.com.gajamove_customer.models.Notification_Data;
import customer.gajamove.com.gajamove_customer.utils.Constants;

import static android.content.ContentValues.TAG;

/**
 * Created by PC-GetRanked on 12/9/2017.
 */

public class FcmMessagingService extends FirebaseMessagingService {

    Intent intent;

    int badgeCount = 0;


    public static final String INTENT_FILTER = "bodyguard_found_filter";
    public static final String NAME = "name";
    public static final String ORDER_ID = "order_id";
    public static final String BODY = "order_body";

    public static BodyguardFoundListner bodyguardFoundListner;
    public static NotifyDriverReachedAndStarted driverReachedAndStarted;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {

            String body = remoteMessage.getData().get("body");
            String title = remoteMessage.getData().get("title");
            String post_id = remoteMessage.getData().get("post_id");
            String order_id = remoteMessage.getData().get("order_id");
            String meet_loc = remoteMessage.getData().get("meet_location");
            String destination = remoteMessage.getData().get("destination");
            String date = remoteMessage.getData().get("meet_datetime");

           // String job_applied = remoteMessage.getData().get("data_attr");

            Log.e("message",remoteMessage.getMessageId());


            if (title.contains("accepted") && (body.contains("ride") || body.contains("Ride")))
            {
                RideNotification(body, title, post_id);
                jobAppliedNotification(body,title,post_id);
                return;
            }

            if (!post_id.equals(""))
            {
                if (title.contains("Ride") || title.contains("finished") || title.contains("Finished"))
                {
                    RideNotification(body, title, post_id);
                    jobAppliedNotification(body,title,post_id);
                    if (title.contains("Reached")) {
                        if (driverReachedAndStarted != null)
                            driverReachedAndStarted.onDriverReached(title, body, post_id);
                    }
                    else if (title.contains("Started"))
                    {
                        if (driverReachedAndStarted != null)
                            driverReachedAndStarted.onDriverStartedRide(title, body, post_id);
                    }
                    else if (title.contains("finished") || title.contains("Finished"))
                    {
                        if (driverReachedAndStarted != null)
                            driverReachedAndStarted.onDriverStartedRide(title, body, post_id);
                    }
                    return;
                }
            }

            if (post_id==null)
                post_id = "0";

            if (order_id!=null)
                if (order_id.equalsIgnoreCase(""))
                    order_id=null;


            if (title!=null)
            {
                if (title.equalsIgnoreCase("Job Accepted"))
                {
                    // sendNotification(body,title,post_id);
                    savenotification(body, title, post_id, Constants.USER_NOTIFICATION);
                    jobAppliedNotification(body,title,post_id);
                    if (bodyguardFoundListner!=null)
                        bodyguardFoundListner.onBodyguardFound(title, body, post_id,order_id);

                    return;
                }

            }

            if (order_id!=null)
            {
                if (!order_id.equalsIgnoreCase(""))
                {
                   // sendNotification(body,title,post_id);
                    savenotification(body, title, post_id, Constants.USER_NOTIFICATION);
                    sendScheduleNotification(body,title,post_id,order_id,meet_loc,destination,date);
                    return;
                }

            }

            if (body != null) {
                if (post_id.contains("chat"))
                {
                    NotifyUser(body,post_id,title);
                }
                else if (title.equalsIgnoreCase("job accepted")) {
                    savenotification(body, title, post_id, Constants.USER_JOB_NOTIFICATION);

                    Constants.selected_notification_tab = 2;
                    Intent intent = new Intent(INTENT_FILTER);

                    intent.putExtra(NAME,title);
                    intent.putExtra(ORDER_ID,post_id);
                    intent.putExtra(BODY,body);
                  //  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    if (bodyguardFoundListner!=null) {
                        bodyguardFoundListner.onBodyguardFound(title, body, post_id);
                        sendNotification(body,title,post_id);
                    }
                    else
                        sendNotification(body,title,post_id);
                }
                else
                {
                    sendNotification(body,title,post_id);
                    savenotification(body, title, post_id, Constants.USER_NOTIFICATION);
                    Constants.selected_notification_tab = 0;
                }

               // sendNotification(body, title, post_id);

          /*  Intent local = new Intent();

            local.setAction("YourIntentAction").putExtra("notification_count",  db.getCount()+"");

            this.sendBroadcast(local);*/

            }
            else
            {
                //NotifyUser(body,post_id,title);
                GetMemberDetails(post_id);

            }

            //   Log.d(TAG, "From: " + remoteMessage.getFrom());
            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            }
            super.onMessageReceived(remoteMessage);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    SharedPreferences sharedPreferences;
    private void GetMemberDetails(String post_id) {
        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        String json = sharedPreferences.getString(Constants.CURRENT_JOB_MEMBERS,"");
        if (!json.equalsIgnoreCase("")) {
            Type type = new TypeToken<ArrayList<Member>>() {
            }.getType();
            ArrayList<Member> list = gson.fromJson(json, type);
            if (list.size()>0)
            {
                String[] array = post_id.split(Pattern.quote("_"));
                for (int i=0;i<list.size();i++)
                {
                    if (list.get(i).getMem_id().equalsIgnoreCase(array[1]))
                    {
                        if (notifyCancel!=null) {
                            notifyCancel.onCancelRequestReceived(list.get(i),array[0]);
                            generateLocalNotification(list.get(i),array[0]);
                            StartTimer(list.get(i),array[0]);
                            break;
                        }
                        else
                        {
                            generateLocalNotification(list.get(i),array[0]);
                            StartTimer(list.get(i),array[0]);
                            break;
                        }
                    }
                }
            }
        }
    }

    private CountDownTimer timer;
    private void StartTimer(final Member member,final String order_id)
    {

      timer = new CountDownTimer(1000,50*1000) {

          @Override
          public void onTick(long millisUntilFinished) {
              try {
                  Log.e("timer count",""+millisUntilFinished/1000);
                  if ((millisUntilFinished/1000)/15==0) {

                      if (notifyCancel != null) {
                          notifyCancel.onCancelRequestReceived(member, order_id);
                          generateLocalNotification(member, order_id);
                      } else
                          generateLocalNotification(member, order_id);
                  }
              }
              catch (Exception e)
              {

              }
          }

          @Override
          public void onFinish() {
                try {
                    Log.e("finished","time up");
                    Log.e("cancelling","calling api .....");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
          }
      };

      timer.start();

    }

    private void generateLocalNotification(Member member,String order_id)
    {

        Intent  cancel_intent = new Intent(this, HomeScreen.class);

        cancel_intent.putExtra("member",member);
        cancel_intent.putExtra("order_id",order_id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, cancel_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
            notificationBuilder.setColor(getResources().getColor(R.color.reddish));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
        }
        notificationBuilder
                .setLargeIcon(bm)
                .setContentTitle("Cancel Request is pending")
                .setContentText(member.getMem_name()+" requested to mark his job as complete.")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.CANCEL_MEMBER_NOTICIATION_ID /* ID of notification */, notificationBuilder.build());
    }

    public static NotifyCancelRequest notifyCancel;

    private void savenotification(String body, String title, String post_id,String prefs_name)
    {
        settings = getSharedPreferences(prefs_name,MODE_PRIVATE);

        Gson gson = new Gson();
        String notification_type;
        String json;
        // String json = gson.toJson(notifications);
        json = settings.getString("gen_notifications", "");
        notification_type = "gen_notifications";


        notifications = new ArrayList<>();

        if (json==null || json.isEmpty())
        {


            Notification_Data notification = new Notification_Data();
            notification.setBody(body);
            notification.setTitle(title);
            notification.setExtra("");
            notification.setId(post_id);
            notification.setDate_time(getCurrentTime());
            notification.setShown(false);

            notifications.add(notification);

            sendNotification(notification.getBody(),notification.getTitle(),notification.getId());

        }
        else
        {

            Type type = new TypeToken<List<Notification_Data>>() {}.getType();
            notifications = gson.fromJson(json, type);

            if (notifications!=null)
            {
                int extra_id = notifications.size();
                extra_id++;

                Notification_Data notification = new Notification_Data();
                notification.setBody(body);
                notification.setTitle(title);
                notification.setExtra("");
                notification.setId(extra_id + "");
                notification.setDate_time(getCurrentTime());
                notification.setShown(false);

                notifications.add(notification);


            }


        }

        String data = gson.toJson(notifications);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(notification_type,data);
        editor.apply();


    }

    private String getCurrentTime()
    {
        try {
            Date date = new Date();
            SimpleDateFormat format2 = new SimpleDateFormat("dd MMM (EEE) yyyy hh:mm a", Locale.ENGLISH);
            String curr_date = format2.format(date);
            String am_pm = "AM";
            if (date.getHours()>=12)
                am_pm = "PM";

            return curr_date+am_pm;
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public void jobAppliedNotification(String messageBody, String title_,String order_id) {

        if (!title_.contains("Ride")) {
            intent = new Intent(this, HomeScreen.class);
        }
        else
        {
            intent = new Intent(this, HomeScreen.class);
        }
        intent.putExtra(Constants.ORDER_ID,order_id);
        intent.putExtra("booking_type",0);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
           // notificationBuilder.setColor(getResources().getColor(R.color.reddish));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
        }
        notificationBuilder
                .setLargeIcon(bm)
                .setContentTitle(title_)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTICIATION_ID /* ID of notification */, notificationBuilder.build());


    }

    public void RideNotification(String messageBody, String title_,String order_id) {

        intent =new Intent(this, HomeScreen.class)
                .putExtra("order_id", order_id);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
            // notificationBuilder.setColor(getResources().getColor(R.color.reddish));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
        }
        notificationBuilder
                .setLargeIcon(bm)
                .setContentTitle(title_)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTICIATION_ID /* ID of notification */, notificationBuilder.build());


    }

    public void sendScheduleNotification(String messageBody, String title_, String post_id
            ,String order_id,String meet_location,String destination,String time) {

        intent = new Intent(this, HomeScreen.class);

        intent.putExtra("title",title_);
        intent.putExtra("body",messageBody);
        intent.putExtra("post_id",post_id);
        intent.putExtra("meet_location",meet_location);
        intent.putExtra("destination",meet_location);
        intent.putExtra("time",time);
        intent.putExtra("order_id",order_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
            notificationBuilder.setColor(getResources().getColor(R.color.reddish));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
        }
        notificationBuilder
                .setLargeIcon(bm)
                .setContentTitle(title_)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTICIATION_ID /* ID of notification */, notificationBuilder.build());


    }


    public void sendNotification(String messageBody, String title_, String post_id) {

        intent = new Intent(this, HomeScreen.class);

        intent.putExtra("title",title_);
        intent.putExtra("body",messageBody);
        intent.putExtra("post_id",post_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
           // notificationBuilder.setColor(getResources().getColor(R.color.reddish));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.app_icon);
        }
        notificationBuilder
                .setLargeIcon(bm)
                .setContentTitle(title_)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTICIATION_ID /* ID of notification */, notificationBuilder.build());


    }


    ArrayList<String> messege_list = new ArrayList<>();
    public static int count = 0;
    String messeges="";
    private void NotifyUser(String message, String chat_id,String title)
    {

        if (count==0)
        {
            messege_list.clear();
            messeges = "";
        }
        count++;
        messege_list.add(message);

        Intent intent = new Intent(this, HomeScreen.class);
        intent.putExtra("chat_id",chat_id);

        //     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
        if (count>1)
        {
            messeges = "";
            for (int i=0;i<messege_list.size();i++)
            {
                messeges += "\n"+messege_list.get(i);
            }
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.app_icon);;

        notificationBuilder.setContentTitle(title);
        if (count>1)
        {
            notificationBuilder.setContentTitle(count+" chat messeges");
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messeges));
            notificationBuilder.setContentText(messeges);
            messeges = "";
        }
        else
        {
            notificationBuilder.setContentText(message);
        }
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setLargeIcon(bm);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        saveChatNotification(message,title,chat_id);
        notificationManager.notify(10 /* ID of notification */, notificationBuilder.build());
    }

    ArrayList<Notification_Data> notifications;
    SharedPreferences settings;
    private void saveChatNotification(String message,String title,String chat_id)
    {
        settings = getSharedPreferences(Constants.USER_CHAT_NOTIFICATION,MODE_PRIVATE);

        Gson gson = new Gson();
        String notification_type;
        String json;
        // String json = gson.toJson(notifications);

        json = settings.getString("chat_notifications", "");
        notification_type = "chat_notifications";


        notifications = new ArrayList<>();

        if (json==null || json.isEmpty())
        {


            Notification_Data notification = new Notification_Data();
            notification.setBody(message);
            notification.setTitle(title);
            notification.setExtra("");
            notification.setId("1");
            notification.setChat_id(chat_id);
            notification.setDate_time(getCurrentTime());
            notification.setShown(false);

            notifications.add(notification);


        }
        else
        {

            Type type = new TypeToken<List<Notification_Data>>() {}.getType();
            notifications = gson.fromJson(json, type);

            if (notifications!=null)
            {
                int extra_id = notifications.size();
                extra_id++;

                Notification_Data notification = new Notification_Data();
                notification.setBody(message);
                notification.setTitle(title);
                notification.setExtra("");
                notification.setId(extra_id + "");
                notification.setShown(false);
                notification.setChat_id(chat_id);
                notification.setDate_time(getCurrentTime());
                notifications.add(notification);

            }


        }

        String data = gson.toJson(notifications);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(notification_type,data);
        editor.apply();

    }



}
