package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import customer.gajamove.com.gajamove_customer.adapter.BigSidePointAdapter;
import customer.gajamove.com.gajamove_customer.adapter.StopInfoAdapter;
import customer.gajamove.com.gajamove_customer.chat.ChatActivity;
import customer.gajamove.com.gajamove_customer.fragment.HomeUIMap;
import customer.gajamove.com.gajamove_customer.models.BumbleRideInformation;
import customer.gajamove.com.gajamove_customer.models.Customer;
import customer.gajamove.com.gajamove_customer.models.FireBaseChatHead;
import customer.gajamove.com.gajamove_customer.models.LatLong;
import customer.gajamove.com.gajamove_customer.models.Member;
import customer.gajamove.com.gajamove_customer.models.MyOrder;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.models.Ride;
import customer.gajamove.com.gajamove_customer.models.RideStatus;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.RideDirectionPointsDB;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;

public class CurrentJobScreen extends BaseActivity {

    private static final String TAG = "CurrentJobScreen";

    String order_id,member_id;
    SharedPreferences sharedPreferences;
    TextView pickup,pickup_header_txt,destination,destnation_header_txt,member_name,contact_num,message_text,distance_txt;
    LinearLayout chat_layout,contact_layout,message_layout,member_information_layout,driver_name_image;
    private CircleImageView member_image;
    private TextView cancel_ride_btn;
    private int state = 1;

    public static final String IDLE  = "0";
    public static final String MOVING  = "5";
    public static final String REACHED = "6";
    public static final String STARTED = "2";
    public static final String CANCELLED = "3";
    public static final String PAYMENT = "5";
    public static final String RECEIVE = "6";
    public static final String CASH = "10";
    public static final String PAID = "11";

    public static final String MEMBER_CHAT_TAG = "mem_chat_tag";
    TextView user_name,status_message;
    CircleImageView user_image;

    ImageView call_btn,collapse_expand_btn;
    ImageView back_btn,chat_btn;

    ArrayList<Prediction> predictionArrayList,allstopList;
    boolean isMulti = false;
    ListView stop_list,side_point_list;
    RelativeLayout rating_lay;
    Button rate_btn;
    EditText rate_txt;
    RatingBar ratingBar;
    Switch fav_driver_switch;

    ImageView rating_imageView;
    TextView rating_user,plate_txt;
    MyOrder myOrder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_job_screen);

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        order_id =sharedPreferences.getString(Constants.ORDER_ID,"0");
        customer_id = getIntent().getStringExtra("customer_id");
        myOrder = (MyOrder) getIntent().getSerializableExtra("order");
        Constants.enableSSL(asyncHttpClient);
        predictionArrayList = (ArrayList<Prediction>) getIntent().getSerializableExtra("stops");

        if (predictionArrayList!=null){
            if (predictionArrayList.size() > 0){
                isMulti = true;
            }
        }

        rating_lay = findViewById(R.id.rating_lay);
        rate_btn = findViewById(R.id.rate_btn);
        rate_txt = findViewById(R.id.rating_text);
        ratingBar = findViewById(R.id.rating_bar);
        fav_driver_switch = findViewById(R.id.fav_switch);
        rating_imageView = findViewById(R.id.rating_user_image);
        rating_user = findViewById(R.id.rating_user_name);
        plate_txt = findViewById(R.id.plate_txt);

        ImageView close_rating = findViewById(R.id.close_rating_btn);
        close_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        plate_txt.setText(myOrder.getDriver_plate()+"");




        rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingBar.getRating()!=0){

                    saveReview(ratingBar.getRating()+"",rate_txt.getText().toString()+"");
                }else {
                    Toast.makeText(CurrentJobScreen.this,"Provide Your Review",Toast.LENGTH_SHORT).show();
                }
            }
        });

        stop_list = findViewById(R.id.stop_list);
        side_point_list = findViewById(R.id.side_point_list);
        pickup = findViewById(R.id.pickup_location_txt);
        destination = findViewById(R.id.destination_location_txt);
        pickup_header_txt = findViewById(R.id.pickup_header_text);
        destnation_header_txt = findViewById(R.id.destination_header_text);
        distance_txt = findViewById(R.id.distance_txt);
        status_message = findViewById(R.id.status_message);
        collapse_expand_btn = findViewById(R.id.collapse_expand_btn);

        collapse_expand_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stop_list.getVisibility()==View.VISIBLE) {
                    stop_list.setVisibility(View.GONE);
                    collapse_expand_btn.setRotation(180);
                }
                else {
                    stop_list.setVisibility(View.VISIBLE);
                    collapse_expand_btn.setRotation(0);
                }
            }
        });

        cancel_ride_btn = findViewById(R.id.cancel_button);

        call_btn =  findViewById(R.id.phone_lay);
        chat_btn = findViewById(R.id.chat_btn);

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CurrentJobScreen.this, ChatActivity.class);
                intent.putExtra("chat_id",chat_id);
                intent.putExtra("order_id",order_id);
                startActivity(intent);
            }
        });

        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CurrentJobScreen.this,HomeScreen.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finishAffinity();
            }
        });


        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+members.get(0).getMem_phone()));
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });


        cancel_ride_btn.setVisibility(View.GONE);
        cancel_ride_btn.setOnClickListener(cancel_Listner);


        user_name = findViewById(R.id.user_name);
        user_image = findViewById(R.id.user_image);

        try {
            if (myOrder.isIs_assigned()
                    && UtilsManager.isCancelable(myOrder.getOrder_date())){
                cancel_ride_btn.setVisibility(View.VISIBLE);
            }else if (!myOrder.isIs_assigned()){
                cancel_ride_btn.setVisibility(View.VISIBLE);
            }else if (myOrder.isIs_assigned()
                    && !UtilsManager.isCancelable(myOrder.getOrder_date())){
                cancel_ride_btn.setVisibility(View.GONE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        ShowLocations();
        //getSettings();
        getMembers();

    }

    private void saveReview(String ratings, String s)
    {
        asyncHttp.setConnectTimeout(20000);

        RequestParams params = new RequestParams();

        params.put("member_id",member_id);
        params.put("customer_id",customer_id);
        params.put("key",UtilsManager.getApiKey(CurrentJobScreen.this));
        params.put("order_id",order_id);
        params.put("review_text",s);
        if (fav_driver_switch.isChecked()){
            params.put("favorite_drivers","1");
        }else {
            params.put("favorite_drivers","0");
        }
        params.put("review_stars",ratings);

        asyncHttp.post(CurrentJobScreen.this, Constants.Host_Address + "customers/give_review", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                try {
                    showDialog("Saving Review..");
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideLoader();

                    JSONObject js = new JSONObject(new String(responseBody));
                    Log.e("response", js.toString());
                    Toast.makeText(CurrentJobScreen.this, "Thanks for your Review", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(CurrentJobScreen.this, HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    JSONObject js = new JSONObject(new String(responseBody));
                    Log.e("failure response",js.toString());
                    Toast.makeText(CurrentJobScreen.this,"Unable to add Review",Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }

    String total = "";
    private void ShowLocations()
    {

        try {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            total = sharedPreferences.getString(Constants.TOTAL, "");
            String pick = sharedPreferences.getString(Constants.MEET_LOCATION, "");
            String distance = sharedPreferences.getString(Constants.PREFS_TOTAL_DISTANCE, "0");
            String destination_ = sharedPreferences.getString(Constants.DESTINATION, "");

            pickup.setText(pick);
            destination.setText(destination_);
            if (!distance.equalsIgnoreCase("0"))
                distance_txt.setText(distance+"KM");
            else
                distance_txt.setVisibility(View.GONE);

            try {
                String[] pick_header;  pick.split("\\s+");
                /*String[] pick_header = pick.split("\\s+");
                String[] dest_header = destination_.split("\\s+");


                pickup_header_txt.setText((pick_header[0] + " " + pick_header[1]).replaceAll(",",""));
                destnation_header_txt.setText((dest_header[0] + " " + dest_header[1]).replaceAll(",",""));
*/

                allstopList = new ArrayList<>();

                Prediction prediction;

                if (predictionArrayList!=null){
                    for (int i=0;i<predictionArrayList.size();i++){

                        prediction = new Prediction();
                        prediction.setLocation_name(predictionArrayList.get(i).getLocation_name());

                        pick_header = predictionArrayList.get(i).getLocation_name().split("\\s+");
                        prediction.setLocation_title((pick_header[0] + " " + pick_header[1]).replaceAll(",",""));


                        allstopList.add(prediction);
                    }
                }


                if (allstopList.size() > 2){
                    isMulti = true;
                }else {
                    isMulti = false;
                }

                StopInfoAdapter stopInfoAdapter = new StopInfoAdapter(this, allstopList);
                stop_list.setAdapter(stopInfoAdapter);
                UtilsManager.setListViewHeightBasedOnItems(stop_list);


                side_point_list.setVisibility(View.GONE);
              //  loadMap(0);
              /*  BigSidePointAdapter sidePointAdapter = new BigSidePointAdapter(allstopList.size(), this);
                side_point_list.setAdapter(sidePointAdapter);
                UtilsManager.updateListHeight(this,55,side_point_list,allstopList.size());

*/
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    AlertDialog alertDialog = null;
    View.OnClickListener cancel_Listner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //  CancelOrderApi(order_id);
            alertDialog = new AlertDialog.Builder(CurrentJobScreen.this)
                    .setTitle("Cancel Job")
                    .setMessage("Are you sure you want to cancel job?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            CancelOrderApi(order_id);
                            upDateRideStatus(CANCELLED);
                            alertDialog.dismiss();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    })
                    .show();
        }
    };

    private void upDateRideStatus(String status)
    {
        try {

            BumbleRideInformation bum_info = new BumbleRideInformation();
            bum_info.setCustomer_id(customer_id);
            bum_info.setMember_id(member_id);
            bum_info.setOrder_id(order_id);
            bum_info.setUpdated_by("cust");
            bum_info.setRide_status(status);

            FirebaseDatabase.getInstance().getReference().child(FireBaseChatHead.BUMBLE_RIDE).child(ride_id).setValue(bum_info);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private ProgressDialog progress;

    private void showDialog(String message)
    {
        progress = new ProgressDialog(CurrentJobScreen.this);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage(message);
        progress.show();
    }

    private void hideLoader()
    {
        if (progress!=null)
            if (progress.isShowing())
                progress.dismiss();
    }

    String customer_id,ride_id;
    AsyncHttpClient asyncHttp = new AsyncHttpClient();
    private void CancelOrderApi(String order_number)
    {
        asyncHttp.setConnectTimeout(20000);
        Log.e("CANCEL_CURRENT_JOB",Constants.Host_Address + "orders/cancel_current_order/tgs_appkey_amin/" + order_number);
        asyncHttp.get(CurrentJobScreen.this, Constants.Host_Address + "orders/cancel_current_order/"+UtilsManager.getApiKey(CurrentJobScreen.this)+"/" + order_number, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Cancelling Ride");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    hideLoader();
                    BumbleRideInformation bum_info = new BumbleRideInformation();
                    bum_info.setCustomer_id(customer_id);
                    bum_info.setMember_id(member_id);
                    bum_info.setOrder_id(order_id);
                    bum_info.setRide_status(CANCELLED);
                    bum_info.setUpdated_by("cust");
                    FirebaseDatabase.getInstance().getReference()
                            .child(FireBaseChatHead.BUMBLE_RIDE).child(ride_id)
                            .setValue(bum_info);

                    String response = new String(responseBody);
                    JSONObject object = new JSONObject(response);
                    Log.e("success response",response);
                    new RideDirectionPointsDB(CurrentJobScreen.this).clearSavedPoints();
                    finish();
                   /* if (object.getString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(JobProgressScreen.this, "Your order cancelled successfully", Toast.LENGTH_LONG).show();
                        RemoveCurrentJobDetails();
                        startActivity(new Intent(JobProgressScreen.this,HomePage_.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    }
                    else
                    {
                        UtilsManager.showAlertMessage(JobProgressScreen.this,"",object.getString("message"));
                    }*/


                    removeSavedMemberData();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    removeSavedMemberData();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {

                    hideLoader();
                    String response = new String(responseBody);
                    Log.e("failure response",response);
                    UtilsManager.showAlertMessage(CurrentJobScreen.this,"","Could not cancel order.");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void removeSavedMemberData()
    {
        getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE)
                .edit().putString(Constants.CURRENT_JOB_MEMBERS,"")
                .apply();
    }


    SharedPreferences sha_prefs;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    Customer customer;
    ArrayList<Member> members;
    String chat_id = "";

    private void getSettings() {
        sha_prefs = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        customer_id = sha_prefs.getString(Constants.PREFS_CUSTOMER_ID,"");
        String customer_name = sha_prefs.getString(Constants.PREFS_USER_NAME,"");
        String customer_lat = sha_prefs.getString(Constants.CURRENT_LATITUDE,"");
        String customer_lon = sha_prefs.getString(Constants.CURRENT_LONGITUDE,"");
        String customer_image = sha_prefs.getString(Constants.PREFS_USER_IMAGE,"");


        customer = new Customer(customer_name,customer_id,customer_image,customer_lat,customer_lon);
        members = new ArrayList<>();
        asyncHttpClient.setConnectTimeout(30000);
        Log.e("SELECTED_MEMBERS", Constants.Host_Address + "orders/get_selected_members/tgs_appkey_amin/"+ order_id);

        asyncHttpClient.get(CurrentJobScreen.this, Constants.Host_Address + "orders/get_selected_members/"+UtilsManager.getApiKey(CurrentJobScreen.this)+"/"+ order_id, new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                // showDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);

                    sharedPreferences.edit().putString(Constants.CURRENT_JOB_MEMBERS,response).apply();

                    // stopLoading();

                    JSONObject object = new JSONObject(new String(responseBody));
                    JSONArray array = object.getJSONArray("members");

                    Log.e(TAG, "onSuccess: "+array.toString());

                    members = new ArrayList<>();
                    for (int i=0;i<array.length();i++)
                    {
                        JSONObject member = array.getJSONObject(i);
                        String id = member.getString("id");
                        String name = member.getString("display_name");
                        //  String email = member.getString("email");
                        //  String dob = member.getString("dob");
                        String ratimg = member.getString("avg_review");

                        String number = member.getString("mobile_number");
                        String image = member.getString("profile_img");
                        String lat = member.getString("lat");
                        String lon = member.getString("lng");
                        //  String passport = member.getString("ic_passport");

                        Log.e("current order image",image);
                        Member mem_obj = new Member(id,name,"",image,lat,lon,"","","");
                        mem_obj.setMem_phone(number);
                        mem_obj.setRating(ratimg);
                        members.add(mem_obj);

                    }

                    if (members.size()>0)
                    {
                        member_name.setText(members.get(0).getMem_name());
                        if (members.get(0).getMem_phone().equalsIgnoreCase(""))
                            contact_layout.setVisibility(View.GONE);
                        else
                            contact_num.setText(members.get(0).getMem_phone());

                        message_text.setText("Driver on the way");


                        Log.e(TAG, "onSuccess: "+Constants.MEMBER_BASE_IMAGE_URL + members.get(0).getMem_image());
                        Picasso.with(CurrentJobScreen.this)
                                .load(Constants.MEMBER_BASE_IMAGE_URL + members.get(0).getMem_image())
                                .resize(100,100)
                                .placeholder(R.drawable.app_icon)
                                .into(member_image);

                        float rating =Float.parseFloat(members.get(0).getRating())/2;

                        // member_information_layout.setVisibility(View.VISIBLE);
                    }
                    else
                    {

                    }

                    ride_id = FireBaseChatHead.getUniqueRideId(members.get(0).getMem_id(),customer_id,order_id);
                    chat_id = FireBaseChatHead.getUniqueChatId(members.get(0).getMem_id(),customer_id,order_id);
                    member_id = members.get(0).getMem_id();
                    SetUpRideStateListner(FirebaseDatabase.getInstance());
                    // loadCurrentRide();
                    loadCurrentOrder();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    // stopLoading();
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    Log.e("response",jsonObject.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


    private void getMembers(){
        asyncHttpClient.setConnectTimeout(30000);
        Log.e("url", Constants.Host_Address + "members/get_members/" + customer_id + "/" + order_id + "/tgs_appkey_amin");


        asyncHttpClient.get(CurrentJobScreen.this, Constants.Host_Address + "members/get_members/" + customer_id + "/" + order_id + "/"+UtilsManager.getApiKey(CurrentJobScreen.this)+"", new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    String response = new String(responseBody);

                    Log.e(TAG, "onSuccess: "+response);
                    if (sharedPreferences==null)
                        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);

                    sharedPreferences.edit().putString(Constants.CURRENT_JOB_MEMBERS,response).apply();


                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject data = object.getJSONObject("data");
                        String path = data.getString("image_path");
                        JSONArray array = data.getJSONArray("members");

                        members = new ArrayList<>();
                        for (int i=0;i<array.length();i++)
                        {
                            JSONObject member = array.getJSONObject(i);
                            String id = member.getString("id");
                            String name = member.getString("full_name");
                            String image = Constants.SERVICE_IMAGE_BASE_PATH +path+ member.getString("profile_img");
                            String lat = member.getString("lat");
                            String lon = member.getString("lng");

                            String mobile_number = member.getString("mobile_number");

                            Log.e("current order image",image);
                            Member mem_obj = new Member(id,name,"",image,lat,lon,"","","");
                            mem_obj.setMem_phone(mobile_number);
                            members.add(mem_obj);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    member_id = members.get(0).getMem_id();
                    user_name.setText(members.get(0).getMem_name());
                    Picasso.with(CurrentJobScreen.this).load(members.get(0).getMem_image())
                            .placeholder(R.drawable.profile_icon).into(user_image);

                    rating_user.setText(members.get(0).getMem_name());
                    Picasso.with(CurrentJobScreen.this).load(members.get(0).getMem_image())
                            .placeholder(R.drawable.profile_icon).into(rating_imageView);

                    ride_id = FireBaseChatHead.getUniqueRideId(members.get(0).getMem_id(),customer_id,order_id);
                    chat_id = FireBaseChatHead.getUniqueChatId(members.get(0).getMem_id(),customer_id,order_id);
                    member_id = members.get(0).getMem_id();
                    SetUpRideStateListner(FirebaseDatabase.getInstance());
                    // loadCurrentRide();
                    loadCurrentOrder();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    loadCurrentOrder();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    loadCurrentOrder();
                    String response = new String(responseBody);
                    Log.e("response",response);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }


    BumbleRideInformation bum_info = null;

    private void SetUpRideStateListner(FirebaseDatabase firebaseDatabase)
    {
        ride_id = FireBaseChatHead.getUniqueRideId(member_id,customer_id,order_id);

        //Query query = firebaseDatabase.getReference().child(FireBaseChatHead.BUMBLE_RIDE)
        firebaseDatabase.getReference().child(FireBaseChatHead.BUMBLE_RIDE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {

                    bum_info = dataSnapshot.getValue(BumbleRideInformation.class);

                    if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase("finished")) {
                        //showReviewaMemberDialog(members.get(0).getMem_name(),members.get(0).getMem_image());
                        //  finish();
                        new RideDirectionPointsDB(CurrentJobScreen.this).clearSavedPoints();
                     /*   finish();
                        Toast.makeText(CurrentJobScreen.this,"Job finished",Toast.LENGTH_LONG).show();
                */    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase(CANCELLED)
                            && bum_info.getUpdated_by().equalsIgnoreCase("mem")
                    )
                    {
                        // Notification("Ride cancelled","Ride cancelled by member");
                      //  showNotificationPopup();
                        new RideDirectionPointsDB(CurrentJobScreen.this).clearSavedPoints();
                        finish();
                        Toast.makeText(CurrentJobScreen.this,"Job Cancelled",Toast.LENGTH_LONG).show();

                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase(RECEIVE)
                            && bum_info.getUpdated_by().equalsIgnoreCase("mem")
                    )
                    {
                        // Notification("Ride cancelled","Ride cancelled by member");
                        //showPaymentMode();
                        Toast.makeText(CurrentJobScreen.this,"Thanks for your ride with us.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)){

                    }
                       // loadCurrentOrder();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {

                    bum_info = dataSnapshot.getValue(BumbleRideInformation.class);

                    if (bum_info.getOrder_id().equalsIgnoreCase(order_id) && bum_info.getRide_status().equalsIgnoreCase("finished")) {
                        //showReviewaMemberDialog(members.get(0).getMem_name(),members.get(0).getMem_image());
                        //  finish();

                        rating_lay.setVisibility(View.VISIBLE);
                        loadMap(0);

                        Toast.makeText(CurrentJobScreen.this,"Job finished",Toast.LENGTH_LONG).show();
                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase(CANCELLED)
                            && bum_info.getUpdated_by().equalsIgnoreCase("mem")
                    )
                    {
                        Notification("Ride cancelled","Ride cancelled by member");
                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)
                            && bum_info.getRide_status().equalsIgnoreCase(RECEIVE)
                            && bum_info.getUpdated_by().equalsIgnoreCase("mem")
                    )
                    {
                        // Notification("Ride cancelled","Ride cancelled by member");
                        Toast.makeText(CurrentJobScreen.this,"Thanks for your ride with us.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else if (bum_info.getOrder_id().equalsIgnoreCase(order_id)){

                    }
                      //  loadCurrentOrder();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private int NOTIFICATION_ID = 101;
    public void Notification(String title,String message)
    {
        Intent  cancel_intent = new Intent(this, HomeScreen.class);

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
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID /* ID of notification */, notificationBuilder.build());

    }

    boolean isAlready_reached = false, isAlready_started= false;
    private void loadCurrentOrder()
    {
        asyncHttpClient.setConnectTimeout(20000);

        Log.e(TAG, "loadCurrentOrder: "+Constants.Host_Address + "customers/my_bumble_ride_job_status/" + order_id + "/tgs_appkey_amin");
        asyncHttpClient.get(CurrentJobScreen.this, Constants.Host_Address + "customers/my_bumble_ride_job_status/" + order_id + "/"+UtilsManager.getApiKey(CurrentJobScreen.this)+"", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject js = new JSONObject(new String(responseBody));
                    Log.e("STATUS_REFRESH",js.toString());

                    JSONObject data = js.getJSONObject("data");
                    customer_id = data.getString("customer_id");
                    member_id = data.getString("member_id");
                    String status = data.getString("status");
                    ride_id = FireBaseChatHead.getUniqueRideId(member_id,customer_id,order_id);
                    chat_id = FireBaseChatHead.getUniqueChatId(member_id,customer_id,order_id);
                    //float ratings = Float.parseFloat(members.get(0).getRating())/2;


                    if (status.equalsIgnoreCase(IDLE)){
                        loadMap(0);

                    }

                    if (status.equalsIgnoreCase(MOVING))
                    {
                        loadMap(1);

                        status_message.setText("Driver is on the way");
                    }

                    if (status.equalsIgnoreCase(REACHED))
                    {
                        loadMap(2);


                        status_message.setText("Driver is reached at pickup");
                    }

                    if (status.equalsIgnoreCase(STARTED))
                    {
                        loadMap(3);


                        status_message.setText("Job Started");

                    }

                    if (status.equalsIgnoreCase(CANCELLED))
                    {
                        Notification("Ride Cancelled","Your ride has been cancelled");
                        finish();

                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    JSONObject js = new JSONObject(new String(responseBody));
                    Log.e("response_failure",js.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    LatLng pickup_lat_lng,destination_lat_lng;


    boolean isLoaded = false;
    int prev = 0;
    private void loadMap(int state) {
        try {

            if (isLoaded && prev == state)
            {
                return;
            }else {
                prev = state;
            }

            Ride ride = new Ride();

            ride.setOrder_id(order_id);
            RideStatus rideStatus;
            if (state==0){
                rideStatus = RideStatus.IDLE;
            }
            else if (state == 1)
                rideStatus = RideStatus.STATE_INITIAL;
            else if (state == 2)
                rideStatus = RideStatus.STATE_REACHED;
            else if (state == 3)
                rideStatus = RideStatus.STATE_JOB_STARTED;
            else
                rideStatus = RideStatus.STATE_INITIAL;

            ride.setRideStatus(rideStatus);

            ride.setPickup_loc(pickup.getText().toString());
            ride.setDestination_loc(destination.getText().toString());

            if (isMulti){
                ride.setMulti(true);
                ride.setPredictionArrayList(allstopList);
            }

            if (members!=null){
                if (members.size()>0){
                    ride.setMemberLocationObject(members.get(0));
                    ride.setHasMember(true);
                }else{
                    ride.setHasMember(false);
                }
            }else{
                ride.setHasMember(false);
            }

            Bundle bundle = new Bundle();
            bundle.putString("pick_up", pickup.getText().toString());
            bundle.putBoolean("job_running", true);
            bundle.putString("order_id", order_id);
            bundle.putInt("bumble", 1);
            bundle.putSerializable("ride", ride);

            HomeUIMap mem_map = new HomeUIMap();
            mem_map.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mem_map).commit();

            isLoaded = true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(CurrentJobScreen.this,e.getMessage()+"",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try
        {
           startRefresher();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    CountDownTimer countDownTimer;
    int min = 15;
    private void startRefresher(){

        if (countDownTimer!=null)
            countDownTimer.cancel();

        countDownTimer = new CountDownTimer(min * 60 * 1000,5000) {
            @Override
            public void onTick(long l) {
                try
                {
                    if (members!=null)
                    {
                        if (members.size() > 0){
                            loadCurrentOrder();
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                startRefresher();
            }
        };


        countDownTimer.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer!=null)
            countDownTimer.cancel();
    }
}
