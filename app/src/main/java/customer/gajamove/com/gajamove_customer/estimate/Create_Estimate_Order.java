package customer.gajamove.com.gajamove_customer.estimate;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import customer.gajamove.com.gajamove_customer.BaseActivity;
import customer.gajamove.com.gajamove_customer.FinanceScreen;
import customer.gajamove.com.gajamove_customer.MyApp;
import customer.gajamove.com.gajamove_customer.OrderHistoryScreen;
import customer.gajamove.com.gajamove_customer.PickLocationScreen;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.SelectVehicleScreen;
import customer.gajamove.com.gajamove_customer.SettingsScreen;
import customer.gajamove.com.gajamove_customer.adapter.SidePointAdapter;
import customer.gajamove.com.gajamove_customer.adapter.StopAdapter;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.drag.dslv.DragSortListView;
import customer.gajamove.com.gajamove_customer.fragment.HomeUIMap;
import customer.gajamove.com.gajamove_customer.models.Advertisement;
import customer.gajamove.com.gajamove_customer.models.ChooseStatus;
import customer.gajamove.com.gajamove_customer.models.PlaceOrderObj;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.models.Ride;
import customer.gajamove.com.gajamove_customer.models.RideStatus;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.DeleteCallback;
import customer.gajamove.com.gajamove_customer.utils.GPSTracker;
import customer.gajamove.com.gajamove_customer.utils.RideDirectionPointsDB;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class Create_Estimate_Order extends BaseActivity implements DeleteCallback {
    private static final String TAG = "Create_Order_Screen";


    ImageView cross_btn;
    LinearLayout basic_lay,pickup_lay,drop_lay,pick_date_lay,calender_lay,stop_lay;
    Button confirm_pickup,confirm_drop,next_btn,confirm_date_btn,confirm_stop_btn;
    TextView pickup_text,drop_off_text,pickup_chosen_text,drop_chosen_text,stop_chosen_txt,stop_home,stop_contact,selected_date,
            pickup_home,drop_home,pickup_contact,drop_contact;
    boolean is_confirmed = false;
    private ChooseStatus chooseStatus = ChooseStatus.IDLE;
    private int CODE =0011;

    Prediction pickup,drop;
    ArrayList<Prediction> stopsArray;

    public static boolean isImmediate = false;
    public static String chosen_date = "";
    public static String chosen_time = "";

    public PlaceOrderObj placeOrderObj = null;

    boolean date_selected = false;

    double latitude,longitude;


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getCurrentLocation(){

        GPSTracker gpsTracker = new GPSTracker(Create_Estimate_Order.this);
        if (gpsTracker.canGetLocation())
        {


            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();


            if (latitude==0 || longitude==0){
                getLastLocation();
                return;
            }

            LatLng latLng = new LatLng(latitude,longitude);
            Constants.meet = latLng;


            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
              /*  String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();*/

              pickup_lay.setVisibility(View.VISIBLE);
              basic_lay.setVisibility(View.GONE);
              pickup_chosen_text.setText(address);

            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    boolean shown = false;
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(Create_Estimate_Order.this);

        if (ActivityCompat.checkSelfPermission(Create_Estimate_Order.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Create_Estimate_Order.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location loc) {
                        // GPS location can be null if GPS is switched off

                        try {
                            if (loc != null) {

                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();

                                LatLng latLng = new LatLng(latitude, longitude);
                                Constants.meet = latLng;


                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(Create_Estimate_Order.this, Locale.getDefault());

                                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
              /*  String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();*/

                                pickup_lay.setVisibility(View.VISIBLE);
                                basic_lay.setVisibility(View.GONE);
                                pickup_chosen_text.setText(address);

                            }


                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    ImageView current_location;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    LinearLayout add_stop_lay;

    SidePointAdapter sidePointAdapter;
    ListView side_point_list;
    LinearLayout basice_side_point;
    RecyclerView stop_listview;
    StopAdapter stopAdapter;
    DragSortListView dragListView;
    LinearLayout home_tab,history_tab,help_tab,profile_tab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estimate_create_order);

        placeOrderObj = new PlaceOrderObj();
        placeOrderObj.setDate("");

        cross_btn = findViewById(R.id.cross_btn);
        cross_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Create_Estimate_Order.this, LoginScreen.class));
                finish();
            }
        });

        basice_side_point = findViewById(R.id.basic_side_point);
        side_point_list = findViewById(R.id.side_point_list);

        add_stop_lay = findViewById(R.id.add_stop_lay);
        dragListView = findViewById(R.id.stop_list);

        basic_lay = findViewById(R.id.basic_lay);
        pick_date_lay = findViewById(R.id.pick_time_lay);
        pickup_lay = findViewById(R.id.pickup_lay);
        stop_lay = findViewById(R.id.stop_lay);
        drop_lay = findViewById(R.id.destination_lay);
        calender_lay = findViewById(R.id.calender_lay);
        current_location = findViewById(R.id.current_location_btn);
        current_location.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
               // getCurrentLocation();

                final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE );

                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                     buildAlertMessageNoGps();
                     return;
                }else if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                ){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},00);
                    return;
                }

                startLocationUpdates();
            }
        });

        pickup_home = findViewById(R.id.pickup_home);
        drop_home = findViewById(R.id.drop_home);
        pickup_contact = findViewById(R.id.pickup_contact);
        drop_contact = findViewById(R.id.drop_contact);

        stop_home = findViewById(R.id.stop_home);
        stop_contact = findViewById(R.id.stop_contact);

        pickup_text = findViewById(R.id.pickup_text);
        drop_off_text = findViewById(R.id.destination_text);
        selected_date = findViewById(R.id.selected_date);

        pickup_chosen_text = findViewById(R.id.pickup_chosen_text);
        drop_chosen_text = findViewById(R.id.drop_chosen_text);
        stop_chosen_txt = findViewById(R.id.stop_chosen_text);

        confirm_drop = findViewById(R.id.confirm_drop_btn);
        confirm_pickup = findViewById(R.id.confirm_pickup_btn);
        confirm_stop_btn = findViewById(R.id.confirm_stop_btn);

        next_btn = findViewById(R.id.create_btn);
        confirm_date_btn = findViewById(R.id.confirm_date_btn);

        confirm_date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chosen_date.equalsIgnoreCase("") || chosen_time.equalsIgnoreCase("")){
                    Toast.makeText(Create_Estimate_Order.this,"Invalid date and time",Toast.LENGTH_SHORT).show();
                    return;
                }else if (!UtilsManager.isAfter(chosen_date+" "+chosen_time)){
                    Toast.makeText(Create_Estimate_Order.this,"Invalid date and time",Toast.LENGTH_SHORT).show();
                    return;

                }

                if (isImmediate){
                    if (!chosen_date.equalsIgnoreCase("") && !chosen_time.equalsIgnoreCase("")) {
                        selected_date.setText(UtilsManager.parseDate(chosen_date )+" ASAP");
                        placeOrderObj.setDisplay_date(UtilsManager.parseDate(chosen_date )+" ASAP");
                        calender_lay.setVisibility(View.GONE);
                        basic_lay.setVisibility(View.VISIBLE);
                        date_selected = true;
                    }
                }else {
                    if (!chosen_date.equalsIgnoreCase("") && !chosen_time.equalsIgnoreCase("")) {
                        selected_date.setText(UtilsManager.parseDateToddMMyyyy(chosen_date + " " + chosen_time));
                        placeOrderObj.setDisplay_date(UtilsManager.parseDateToddMMyyyy(chosen_date + " " + chosen_time));
                        calender_lay.setVisibility(View.GONE);
                        basic_lay.setVisibility(View.VISIBLE);
                        date_selected = true;
                    }
                }

            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // calculateTotalDistance();


                if (is_confirmed){
                    placeOrderObj.setPickup(pickup);
                    placeOrderObj.setDestination(drop);
                    if (date_selected) {
                        placeOrderObj.setDate(UtilsManager.convertAMPM(chosen_date + " " + chosen_time));
                        placeOrderObj.setImmediate(false);
                    }

                    if (stopsArray!=null) {
                        if (stopsArray.size() > 0) {
                            placeOrderObj.setStopList(stopsArray);
                            placeOrderObj.setMulti(true);
                        }
                    }else {
                        placeOrderObj.setMulti(false);
                    }

                    startActivity(new Intent(Create_Estimate_Order.this,Estimate_VehicleScreen.class)
                            .putExtra("order",placeOrderObj));
                }else {
                   // Toast.makeText(Create_Order_Screen.this,"Select Valid Date",Toast.LENGTH_SHORT).show();
                }


            }
        });

        pick_date_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calender_lay.setVisibility(View.VISIBLE);
                basic_lay.setVisibility(View.GONE);
            }
        });

        pickup_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseStatus = ChooseStatus.PICKUP;
                Intent intent = new Intent(Create_Estimate_Order.this,PickLocationScreen.class);
                startActivityForResult(intent,CODE);


            }
        });

        drop_off_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseStatus = ChooseStatus.DROP;
                Intent intent = new Intent(Create_Estimate_Order.this,PickLocationScreen.class);
                startActivityForResult(intent,CODE);

            }
        });

        add_stop_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseStatus = ChooseStatus.STOP;
                Intent intent = new Intent(Create_Estimate_Order.this,PickLocationScreen.class);
                startActivityForResult(intent,CODE);
            }
        });


        confirm_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    pickup_text.setText(pickup.getLocation_title());
                    pickup.setAddress(pickup_home.getText().toString());
                    pickup.setContact(pickup_contact.getText().toString());
                    getMEETPlaceDetails(pickup.getLocation_id());
                    pickup_lay.setVisibility(View.GONE);
                    basic_lay.setVisibility(View.VISIBLE);

                    //getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().putString(Constants.MEET_LOCATION, pickup.getLocation_name()).apply();

                    if (drop!=null){

                        createRout();
                    }else {

                        getSupportFragmentManager().beginTransaction().replace(R.id.content_lay, new HomeUIMap()).commit();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        confirm_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                   /* if (drop_contact.getText().toString().trim().equalsIgnoreCase("")){
                        drop_contact.setError("Please add contact number");
                        return;
                    }

                    if (drop_contact.getText().toString().trim().length()<9){
                        drop_contact.setError("Incomplete number(min 9 digits)");
                        return;
                    }else if (drop_contact.getText().toString().trim().startsWith("0")){
                        drop_contact.setError("Number cannot start with 0");
                        return;
                    }*/

                    drop_off_text.setText(drop.getLocation_title());

                    drop.setAddress(drop_home.getText().toString());
                    drop.setContact(drop_contact.getText().toString());

                    drop_lay.setVisibility(View.GONE);
                    basic_lay.setVisibility(View.VISIBLE);
                    getDESTPlaceDetails(drop.getLocation_id());
                    next_btn.setBackgroundResource(R.drawable.next_btn_drawable);
                    is_confirmed = true;


                    createRout();

                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        confirm_stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* if (stop_contact.getText().toString().trim().equalsIgnoreCase("")){
                    stop_contact.setError("Please add contact number");
                    return;
                }

                if (stop_contact.getText().toString().trim().length()<9){
                    stop_contact.setError("Incomplete number(min 9 digits)");
                    return;
                }else if (stop_contact.getText().toString().trim().startsWith("0")){
                    stop_contact.setError("Number cannot start with 0");
                    return;
                }*/

                stop_lay.setVisibility(View.GONE);
                basic_lay.setVisibility(View.VISIBLE);
                prediction.setAddress(stop_home.getText().toString());
                prediction.setContact(stop_contact.getText().toString());
                dragListView.setVisibility(View.VISIBLE);
                getStopPlaceDetails(prediction.getLocation_id());

            }
        });


        showCurrentDatetime();

        getSupportFragmentManager().beginTransaction().replace(R.id.content_lay,new HomeUIMap()).commit();


        home_tab = findViewById(R.id.home_tab);
        history_tab = findViewById(R.id.history_tab);
        help_tab = findViewById(R.id.logout_tab);
        profile_tab = findViewById(R.id.account_tab);



        home_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getSupportFragmentManager().beginTransaction().replace(R.id.content_lay,new HomeFragment()).commit();
                enableTab(home_tab);
                disableTab(history_tab);
                disableTab(help_tab);
                disableTab(profile_tab);

                finish();
            }
        });

        history_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableTab(history_tab);
                disableTab(home_tab);
                disableTab(help_tab);
                disableTab(profile_tab);

                startActivity(new Intent(Create_Estimate_Order.this,OrderHistoryScreen.class));

            }
        });

        help_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableTab(help_tab);
                disableTab(history_tab);
                disableTab(home_tab);
                disableTab(profile_tab);

                startActivity(new Intent(Create_Estimate_Order.this,FinanceScreen.class));
            }
        });

        profile_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableTab(profile_tab);
                disableTab(history_tab);
                disableTab(home_tab);
                disableTab(help_tab);

                startActivity(new Intent(Create_Estimate_Order.this,SettingsScreen.class));
            }
        });


        if (getIntent().getBooleanExtra("show_ads",false)){
            getAdvertisment();
        }

    }


    private void enableTab(LinearLayout home_tab) {
        ((ImageView)home_tab.getChildAt(0)).setColorFilter(ContextCompat.getColor(Create_Estimate_Order.this,R.color.theme_primary));
        ((TextView)home_tab.getChildAt(1)).setTextColor(ContextCompat.getColor(Create_Estimate_Order.this,R.color.theme_primary));

    }

    private void disableTab(LinearLayout home_tab) {
        ((ImageView)home_tab.getChildAt(0)).setColorFilter(ContextCompat.getColor(Create_Estimate_Order.this,R.color.dark_gray_color));
        ((TextView)home_tab.getChildAt(1)).setTextColor(ContextCompat.getColor(Create_Estimate_Order.this,R.color.dark_gray_color));

    }


    boolean inProgress = false;
    int start = 0;
    private void calculateTotalDistance() {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(Constants.meet);
        for (int i=0;i<stopsArray.size();i++){
            latLngs.add(new LatLng(stopsArray.get(i).getLat(),stopsArray.get(i).getLng()));
        }

        latLngs.add(Constants.destination);


         Handler handler = new Handler();
         Runnable repeatingTask = new Runnable() {
            public void run() {

                if (!inProgress){
                    inProgress = true;
                    getDistance(latLngs.get(start).latitude,latLngs.get(start).longitude,latLngs.get(start+1).latitude,latLngs.get(start+1).longitude);
                }

                if (index==3){
                    handler.removeCallbacks(this);
                }
                else {
                    handler.postDelayed(this, 1000);
                }
            }
        };

         repeatingTask.run();

    }


    private void createRout(){

        try {
            new RideDirectionPointsDB(Create_Estimate_Order.this).clearSavedPoints();
            Ride ride = new Ride();

            RideStatus rideStatus;

            rideStatus = RideStatus.STATE_INITIAL;

            ride.setRideStatus(rideStatus);

            ride.setPickup_loc(pickup.getLocation_name());
            ride.setDestination_loc(drop.getLocation_name());


            Bundle bundle = new Bundle();
            bundle.putString("pick_up", pickup.getLocation_name());
            bundle.putBoolean("job_running", true);
            bundle.putString("order_id", "");
            bundle.putInt("bumble", 1);
            bundle.putSerializable("ride", ride);

            HomeUIMap mem_map = new HomeUIMap();
            mem_map.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.content_lay, mem_map).commit();

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showCurrentDatetime() {

        try {
            Date currentTime = Calendar.getInstance().getTime();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.getDefault());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM (EEE) yyyy", Locale.getDefault());
            SimpleDateFormat apidateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.getDefault());


            placeOrderObj.setDate(apidateFormat.format(currentTime));
            placeOrderObj.setImmediate(true);
            selected_date.setText(dateFormat.format(currentTime) + " ASAP");
            placeOrderObj.setDisplay_date(dateFormat.format(currentTime) + " ASAP");
            date_selected = false;


        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    Prediction prediction;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null){
            if (chooseStatus==ChooseStatus.PICKUP){
                pickup_lay.setVisibility(View.VISIBLE);
                basic_lay.setVisibility(View.GONE);
                pickup = new Prediction();
                String[] data_ = data.getData().toString().split(Pattern.quote("#"));
                pickup.setLocation_title(data_[0]);
                pickup.setLocation_name(data_[1]);
                pickup.setLocation_id(data_[2]);

                pickup_chosen_text.setText(data_[0]);

                confirm_pickup.performClick();

            }else if (chooseStatus==ChooseStatus.DROP){
                drop_lay.setVisibility(View.VISIBLE);
                basic_lay.setVisibility(View.GONE);

                drop = new Prediction();
                String[] data_ = data.getData().toString().split(Pattern.quote("#"));
                drop.setLocation_title(data_[0]);
                drop.setLocation_name(data_[1]);
                drop.setLocation_id(data_[2]);
                drop_chosen_text.setText(data_[0]);

                confirm_drop.performClick();

            }else if (chooseStatus==ChooseStatus.STOP){

                stop_lay.setVisibility(View.VISIBLE);
                basic_lay.setVisibility(View.GONE);

                prediction = new Prediction();
                String[] data_ = data.getData().toString().split(Pattern.quote("#"));
                prediction.setLocation_title(data_[0]);
                prediction.setLocation_name(data_[1]);
                prediction.setLocation_id(data_[2]);
                stop_chosen_txt.setText(data_[0]);

                confirm_stop_btn.performClick();
            }
        }
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getMEETPlaceDetails(String id){
        Log.e(TAG, "getPredictionsAPI: ");


        if (id.equalsIgnoreCase("-1"))
            return;

        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id="+id+"&fields=name,geometry&key="+Constants.API_KEY+"";
        //String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+query+"&components=country:mys&types=address&key="+API_KEY+"";
        Constants.enableSSL(asyncHttpClient, Create_Estimate_Order.this);
        asyncHttpClient.get(Create_Estimate_Order.this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.e(TAG, "onSuccess: MEET_DETAILS " + new String(responseBody));
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("OK")) {
                        {
                            double lat = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            double lng = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                            Constants.meet = new LatLng(lat,lng);

                            Log.e(TAG, "onSuccess: PICKUP FOUND" );
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void getDESTPlaceDetails(String id){
        Log.e(TAG, "getPredictionsAPI: ");



        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id="+id+"&fields=name,geometry&key="+Constants.API_KEY+"";
        //String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+query+"&components=country:mys&types=address&key="+API_KEY+"";


        Constants.enableSSL(asyncHttpClient, Create_Estimate_Order.this);
        asyncHttpClient.get(Create_Estimate_Order.this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.e(TAG, "onSuccess: MEET_DETAILS " + new String(responseBody));
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("OK")) {
                        {
                            double lat = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            double lng = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                            Constants.destination = new LatLng(lat,lng);


                         //   getDistance(Constants.meet.latitude,Constants.meet.longitude,Constants.destination.latitude,Constants.destination.longitude);


                            Log.e(TAG, "onSuccess: DESST FOUND" );
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {

                    try {
                        Prediction item = (Prediction) stopAdapter.getItem(from);

                        /*stopAdapter.remove(item);
                        stopAdapter.insert(item, to);
                       */
                        Prediction first = stopsArray.get(from);
                        Prediction last = stopsArray.get(to);

                        stopsArray.set(to,first);
                        stopsArray.set(from,last);

                        stopAdapter.notifyDataSetChanged();

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };

    private DragSortListView.RemoveListener onRemove =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    stopAdapter.remove(stopAdapter.getItem(which));
                }
            };

    private DragSortListView.DragScrollProfile ssProfile =
            new DragSortListView.DragScrollProfile() {
                @Override
                public float getSpeed(float w, long t) {
                    if (w > 0.8f) {
                        // Traverse all views in a millisecond
                        return ((float) stopAdapter.getCount()) / 0.001f;
                    } else {
                        return 10.0f * w;
                    }
                }
            };

    private void getStopPlaceDetails(String id){
        Log.e(TAG, "getPredictionsAPI: ");


        if (id.equalsIgnoreCase("-1"))
            return;

        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id="+id+"&fields=name,geometry&key="+Constants.API_KEY+"";
        //String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+query+"&components=country:mys&types=address&key="+API_KEY+"";
        Constants.enableSSL(asyncHttpClient, Create_Estimate_Order.this);
        asyncHttpClient.get(Create_Estimate_Order.this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.e(TAG, "onSuccess: MEET_DETAILS " + new String(responseBody));
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("OK")) {
                        {
                            double lat = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            double lng = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                            prediction.setLat(lat);
                            prediction.setLng(lng);

                            if (stopsArray==null)
                                stopsArray = new ArrayList<>();

                            stopsArray.add(prediction);

                           // Toast.makeText(Create_Order_Screen.this,""+stopsArray.size()+" name "+prediction.getLocation_name()+" lat "+prediction.getLat()+" lng "+prediction.getLng(),Toast.LENGTH_LONG).show();

                            sidePointAdapter = new SidePointAdapter(stopsArray.size()+2, Create_Estimate_Order.this);
                            side_point_list.setAdapter(sidePointAdapter);
                            UtilsManager.setListViewHeightBasedOnChildren(side_point_list);
                            basice_side_point.setVisibility(View.GONE);
                            side_point_list.setVisibility(View.VISIBLE);



                            stopAdapter = new StopAdapter(Create_Estimate_Order.this,stopsArray);
                            stopAdapter.setDeleteCallback(Create_Estimate_Order.this);
                            dragListView.setDropListener(onDrop);
                            //dragListView.setDragScrollProfile(ssProfile);
                            dragListView.setAdapter(stopAdapter);
                            UtilsManager.setListViewHeightBasedOnItems(dragListView);

                           /* LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Create_Order_Screen.this);
                            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

                           *//* stopAdapter = new StopAdapter(Create_Order_Screen.this,stopsArray);

                            stop_listview.setCheeseList(stopsArray);
                            stop_listview.setAdapter(stopAdapter);

                            stop_listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                         *//*  stopAdapter = new RecyclerListAdapter(Create_Order_Screen.this,stopsArray);
                            stopAdapter.setDeleteCallback(Create_Order_Screen.this);
                            stop_listview.setLayoutManager(linearLayoutManager);
                            stop_listview.setAdapter(stopAdapter);
                            ItemTouchHelper.Callback callback =
                                    new SimpleItemTouchHelperCallback(stopAdapter);
                            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                            touchHelper.attachToRecyclerView(stop_listview);*/
                            stop_listview.setVisibility(View.VISIBLE);

                            Log.e(TAG, "onSuccess: STOP FOUND" );
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @Override
    public void onCurrentLocationReq() {

    }

    @Override
    public void onItemDelete(int pos) {
        try {

            stopsArray.remove(pos);
            if (stopsArray.size() > 0) {
                stopAdapter.notifyDataSetChanged();
                UtilsManager.setListViewHeightBasedOnChildren(dragListView);
                dragListView.setVisibility(View.VISIBLE);
                sidePointAdapter = new SidePointAdapter(stopsArray.size() + 2, Create_Estimate_Order.this);
                side_point_list.setAdapter(sidePointAdapter);
                UtilsManager.setListViewHeightBasedOnChildren(side_point_list);
            }else {
                basice_side_point.setVisibility(View.VISIBLE);
                side_point_list.setVisibility(View.GONE);
                dragListView.setVisibility(View.GONE);

            }



        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationRequest(int pos) {

    }

    @Override
    public void onChangeLocation(int pos) {

    }

    ProgressDialog progressDialog;
    private void showDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private void hideDialoge(){
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

   // SimpleDateFormat server_check_formt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
    private void checkIfdateAvailable(String time) {

        String date = UtilsManager.getServerchekFormat(time);

        String customerId = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getString(Constants.PREFS_CUSTOMER_ID,"0");
        Constants.enableSSL(asyncHttpClient, Create_Estimate_Order.this);

        asyncHttpClient.get(Create_Estimate_Order.this, Constants.Host_Address + "customers/check_schedule_job/" + date + "/"+UtilsManager.getApiKey(Create_Estimate_Order.this)+"/" + customerId + "", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Checking date availability...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e(TAG, "onSuccess: "+response );
                    if (jsonObject.getString("status").equalsIgnoreCase("success")){
                        Toast.makeText(Create_Estimate_Order.this,UtilsManager.makeInitialCapital(jsonObject.getString("message")),Toast.LENGTH_SHORT).show();

                        if (isImmediate){
                            if (!chosen_date.equalsIgnoreCase("") && !chosen_time.equalsIgnoreCase("")) {
                                selected_date.setText(UtilsManager.parseDate(chosen_date )+" ASAP");
                                calender_lay.setVisibility(View.GONE);
                                basic_lay.setVisibility(View.VISIBLE);
                                date_selected = true;
                            }
                        }else {
                            if (!chosen_date.equalsIgnoreCase("") && !chosen_time.equalsIgnoreCase("")) {
                                selected_date.setText(UtilsManager.parseDateToddMMyyyy(chosen_date + " " + chosen_time));
                                calender_lay.setVisibility(View.GONE);
                                basic_lay.setVisibility(View.VISIBLE);
                                date_selected = true;
                            }
                        }

                    }else {
                        Toast.makeText(Create_Estimate_Order.this,UtilsManager.makeInitialCapital(jsonObject.getString("message")),Toast.LENGTH_SHORT).show();

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    if (progressDialog!=null)
                        progressDialog.dismiss();


                    String response = new String(responseBody);
                    Log.e(TAG, "onFailure: "+response);
                    Toast.makeText(Create_Estimate_Order.this,"Server Error "+response,Toast.LENGTH_SHORT).show();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    String parsedDistance;
    String response;

    int index = 1;
    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2){

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric&mode=driving&key=AIzaSyCVq9PoFIgocv01ACZ5bcRugJ1KwIjMfXA");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());

                    BufferedReader br = new BufferedReader(new InputStreamReader(in,
                            "UTF-8"), 1024);
                    String str;
                    while ((str = br.readLine()) != null) {
                        response += str;
                    }

                    Log.e(TAG, "run: RESPONSE "+index+" DATA "+response);
                    index++;
                    start++;
                    inProgress = false;

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance=distance.getString("text");

                    Log.e(TAG, "run: "+parsedDistance);

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance;
    }


    private LocationRequest mLocationRequest = null;
    private long UPDATE_INTERVAL =  5 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 3000; /* 2 sec */

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisonStartfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

       /* Intent intent = new Intent(proximitys);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
     */   //getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest,proximityIntent);


        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest,locationCallback, Looper.myLooper());
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            // do work here

            try {
                if (locationResult.getLastLocation() == null)
                    return;

                Location location = locationResult.getLastLocation();
                Log.e("CURRENT_LOCATION", location.getLatitude() + "-" + location.getLongitude());

                Constants.meet = new LatLng(location.getLatitude(),location.getLongitude());
                String address = Constants.getCompleteAddressString(Create_Estimate_Order.this,location.getLatitude(),location.getLongitude());

                String[] pick_header = address.split("\\s+");


                pickup_lay.setVisibility(View.VISIBLE);
                basic_lay.setVisibility(View.GONE);
                pickup_chosen_text.setText(pick_header[0]+" "+pick_header[1]);

                pickup = new Prediction();
                pickup.setLocation_title(pick_header[0]+" "+pick_header[1]);
                pickup.setLocation_name(address);
                pickup.setLocation_id("-1");


                if (!address.equalsIgnoreCase("")){
                    getFusedLocationProviderClient(Create_Estimate_Order.this).removeLocationUpdates(locationCallback);
                }
            }
            catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "onLocationResult: "+e.getMessage());
            }

        }
    };


    //Advertisment CODE

    List<Advertisement> advertisementList;

    private void getAdvertisment() {

        Log.e(TAG, "getAdvertisment: "+Constants.Host_Address + "members/get_advertisements/tgs_appkey_amin");
        asyncHttpClient.get(Constants.Host_Address + "members/get_advertisements/"+UtilsManager.getApiKey(Create_Estimate_Order.this)+"", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String res = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(res);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    advertisementList = new ArrayList<>();
                    for (int i=0;i<jsonArray.length();i++){
                        String url = jsonArray.getJSONObject(i).getString("url");
                        String image = jsonArray.getJSONObject(i).getString("image");

                        Advertisement advertisement = new Advertisement();
                        advertisement.setAd_url(url);
                        advertisement.setAd_image(Constants.SERVICE_IMAGE_BASE_PATH+"uploads/advertisements/"+image);

                        advertisementList.add(advertisement);
                    }


                    if (advertisementList.size() > 0) {
                        showAdvertismentDialog();
                        getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).edit().putBoolean(Constants.SHOW_AD,false).apply();
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    Dialog dialog = null;
    private void showAdvertismentDialog() {

        if (dialog!=null)
            dialog.dismiss();

        dialog = new Dialog(Create_Estimate_Order.this,R.style.DialogTheme);
        dialog.setContentView(R.layout.advertisment_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView cross_icon = dialog.findViewById(R.id.close_icon);
        cross_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ViewPager viewPager = dialog.findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(Create_Estimate_Order.this));


        dialog.show();





    }


    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            Advertisement modelObject = advertisementList.get(position);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = (View) inflater.inflate(R.layout.advertismnet_list_item, collection, false);
            ImageView imageView = layout.findViewById(R.id.imageView);

            Picasso.with(Create_Estimate_Order.this).load(advertisementList.get(position).getAd_image()).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(advertisementList.get(position).getAd_url()));
                    startActivity(intent);
                }
            });

            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return advertisementList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Advertisement customPagerEnum =advertisementList.get(position);
            return "Advertismnet";
        }

    }


}
