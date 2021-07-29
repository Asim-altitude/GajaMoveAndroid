package customer.gajamove.com.gajamove_customer.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import customer.gajamove.com.gajamove_customer.Create_Order_Screen;
import customer.gajamove.com.gajamove_customer.CurrentJobScreen;
import customer.gajamove.com.gajamove_customer.FindDriverScreen;
import customer.gajamove.com.gajamove_customer.HomeScreen;
import customer.gajamove.com.gajamove_customer.MyApp ;
import customer.gajamove.com.gajamove_customer.OrderDetailScreen;
import customer.gajamove.com.gajamove_customer.Profile_Screen;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.adapter.AdditionalServicesAdapter;
import customer.gajamove.com.gajamove_customer.adapter.BigSidePointAdapter;
import customer.gajamove.com.gajamove_customer.adapter.ScheduledJobAdapter;
import customer.gajamove.com.gajamove_customer.adapter.StopInfoAdapter;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.models.MyOrder;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.RideDirectionPointsDB;
import customer.gajamove.com.gajamove_customer.utils.ShowAdvertisement;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

public class ActiveJobTab extends Fragment {
    private static final String TAG = "ActiveJobTab";

    LinearLayout current_job_lay;
    TextView total_cost_txt,extra_km_label,pickup_location,pickup_location_header,destination_location,destination_location_header,date_text,service_name_txt;
    LinearLayout additional_lay,view_driver_btn;
    ListView services_list_view;
    LinearLayout drop_down_lay,proceed_lay;


    View home_frame;
    ListView side_point_list,stop_list,current_job_list;
    ScheduledJobAdapter scheduledJobAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         home_frame = inflater.inflate(R.layout.active_tab_lay,container,false);

        current_job_lay = home_frame.findViewById(R.id.current_job_lay);

        Constants.enableSSL(asyncHttpClient);
        pickup_location = home_frame.findViewById(R.id.pickup_location_txt);
        destination_location = home_frame.findViewById(R.id.destination_location_txt);
        date_text = home_frame.findViewById(R.id.date_text);
        total_cost_txt = home_frame.findViewById(R.id.total_cost);
        pickup_location_header = home_frame.findViewById(R.id.pickup_header_text);
        destination_location_header = home_frame.findViewById(R.id.destination_header_text);
        service_name_txt = home_frame.findViewById(R.id.service_name);
        extra_km_label = home_frame.findViewById(R.id.extra_km_label);
        current_job_list = home_frame.findViewById(R.id.current_job_list);
        proceed_lay = home_frame.findViewById(R.id.proceed_lay);

        myOrders = new ArrayList<>();
        scheduledJobAdapter = new ScheduledJobAdapter(myOrders,getActivity());
        current_job_list.setAdapter(scheduledJobAdapter);


        proceed_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_driver_btn.performClick();
            }
        });

        side_point_list = home_frame.findViewById(R.id.side_point_list);
        stop_list = home_frame.findViewById(R.id.stop_list);


        view_driver_btn = home_frame.findViewById(R.id.view_driver_btn);

        drop_down_lay = home_frame.findViewById(R.id.drop_down_lay);

        drop_down_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (additional_lay.getVisibility()==View.VISIBLE){
                    additional_lay.setVisibility(View.GONE);
                    ((ImageView)drop_down_lay.getChildAt(0)).setRotation(0);
                }else {
                    additional_lay.setVisibility(View.VISIBLE);
                    ((ImageView)drop_down_lay.getChildAt(0)).setRotation(180);
                }*/

                Intent intent = new Intent(getActivity(), OrderDetailScreen.class);
                intent.putExtra("order",myOrder);
                startActivity(intent);
            }
        });

        view_driver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new RideDirectionPointsDB(getActivity()).clearSavedPoints();
                    if (m_connected) {

                        if (predictionArrayList.size() > 0){
                            startActivity(new Intent(getActivity(), CurrentJobScreen.class)
                                    .putExtra("order_id", order_id)
                                    .putExtra("customer_id", customer_id)
                                    .putExtra("stops", predictionArrayList)
                            );
                        }else {
                            startActivity(new Intent(getActivity(), CurrentJobScreen.class)
                                    .putExtra("order_id", order_id)
                                    .putExtra("customer_id", customer_id));
                        }


                    } else {
                        startActivity(new Intent(getActivity(), FindDriverScreen.class)
                                .putExtra("order_id", order_id)
                                .putExtra("customer_id", customer_id)

                        );


                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        additional_lay = home_frame.findViewById(R.id.additionalservices_lay);
        services_list_view = home_frame.findViewById(R.id.services_list_view);

        current_job_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return home_frame;

    }

    @Override
    public void onResume() {
        super.onResume();
       // getCurrentOrderInformation();
        getScheduledBookings();
    }

    SharedPreferences sha_prefs;
    String customer_id;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    String order_id;
    boolean haveCurrentJob = false;
    boolean m_connected = false;
    MyOrder myOrder;
    ArrayList<Prediction> predictionArrayList;
    StopInfoAdapter stopInfoAdapter;
    BigSidePointAdapter bigSidePointAdapter;

    ArrayList<MyOrder> myOrders;
    private void getCurrentOrderInformation() {

        sha_prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        customer_id = sha_prefs.getString(Constants.PREFS_CUSTOMER_ID, "0");
        String accessToken = "tgs_appkey_amin";//sha_prefs.getString(Constants.PREFS_ACCESS_TOKEN, "");

        if (!customer_id.equalsIgnoreCase("0")) {

            asyncHttpClient.setConnectTimeout(20000);
            Log.e("current_order", Constants.Host_Address + "customers/my_current_job/" + customer_id + "/"+accessToken+"");
            asyncHttpClient.get(getActivity(), Constants.Host_Address + "customers/my_current_job/" + customer_id + "/"+accessToken+"", new AsyncHttpResponseHandler() {



                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {

                        home_frame.findViewById(R.id.no_data_lay).setVisibility(View.GONE);
                        String response = new String(responseBody);
                        Log.e("RESPONSE", response);

                        JSONObject object = new JSONObject(response);

                        String message = object.getString("message");

                        if (message.toLowerCase().equalsIgnoreCase("invalid key")){
                            getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).edit().clear().apply();
                            startActivity(new Intent(getActivity(), LoginScreen.class));
                            getActivity().finish();
                            return;
                        }


                        String data = object.getString("data");


                        if (!data.equalsIgnoreCase("no")) {
                            order_id = data;
                            current_job_lay.setVisibility(View.VISIBLE);
                            haveCurrentJob = true;

                        }
                        else
                        {
                          /*  curent_location.setText("");
                            etLocation.setText("");
                            curent_location.setVisibility(View.GONE);*/
                            order_id = "0";
                            current_job_lay.setVisibility(View.VISIBLE);
                            home_frame.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);
                            haveCurrentJob = false;

                        }

                        myOrder = new MyOrder();
                        JSONObject order_info = object.getJSONObject("order_info");
                        customer_id = order_info.getString("customer_id");
                        String meet_location = order_info.getString("meet_location");
                        String dest_location = order_info.getString("destination");
                        String order_total = order_info.getString("order_total");


                        String pickup_address = order_info.getString("pickup_address");
                        String pickup_contact = order_info.getString("pickup_contact");
                        String destination_address = order_info.getString("destination_address");
                        String destination_contact = order_info.getString("destination_contact");

                        String meet_lat = order_info.getString("meet_lat");
                        String meet_lon = order_info.getString("meet_long");
                        String datetime = order_info.getString("meet_datetime");
                        String total_distance = order_info.getString("total_distance");
                        String service_id = order_info.getString("service_id");
                        String service_type_name = order_info.getString("service_name");
                        String basic_service = order_info.getString("basic_service");
                        String fixed_cost = order_info.getString("fixed_cost");
                        String basic_price = order_info.getString("basic_price");

                        String multiple_stops = order_info.getString("multiple_stops");


                        if (service_type_name.equalsIgnoreCase("null")){
                            service_type_name="";
                        }

                        //total_distance = order_info.getString("total_distance");
                        //job_state = object.getString("job_state");

                        myOrder.setOrder_id(order_id);
                        myOrder.setPick_location(meet_location);
                        myOrder.setDest_location(dest_location);
                        myOrder.setTotal_distance(total_distance);
                        myOrder.setOrder_date(datetime);
                        myOrder.setOrder_total(order_total);
                        myOrder.setBasic_price(basic_price);

                        try {
                            String driver_id =  object.getJSONObject("driver_info").getString("id");
                            String driver_full_name =  object.getJSONObject("driver_info").getString("full_name");
                            String driver_profile_img =  object.getJSONObject("driver_info").getString("profile_img");

                            myOrder.setDriver_name(driver_full_name);
                            myOrder.setDriver_id(driver_id);
                            myOrder.setDriver_image(driver_profile_img);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }


                        try
                        {
                            String[] pick_header = meet_location.split("\\s+");
                            String[] dest_header = dest_location.split("\\s+");


                            pickup_location_header.setText((pick_header[0] + " " + pick_header[1]).replaceAll(",",""));
                            destination_location_header.setText((dest_header[0] + " " + dest_header[1]).replaceAll(",",""));
                            pickup_location.setText(meet_location);
                            destination_location.setText(dest_location);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                       /* String name_vehicle="Car";

                        if (order_info.has("service_type_name"))
                            name_vehicle = order_info.getString("service_type_name");
*/
                        String service_name;
                       try
                       {
                           service_name = object.getJSONObject("service_name").getString("service_name");

                       }
                       catch (Exception e){
                           e.printStackTrace();
                           service_name = "";
                       }


                        myOrder.setMain_service_name(service_name);
                        myOrder.setService_name(service_name);

                        service_name_txt.setText(service_name);
                        date_text.setText(UtilsManager.parseDateToddMMyyyy(datetime));
                        total_cost_txt.setText("RM"+order_total);
                        extra_km_label.setText(total_distance+"KM");


                        predictionArrayList = new ArrayList<>();

                        String[] pick_header = meet_location.split("\\s+");

                        Prediction prediction;
                        prediction = new Prediction();
                        prediction.setLocation_name(meet_location);
                        prediction.setLocation_title((pick_header[0] + " " + pick_header[1]).replaceAll(",",""));
                        prediction.setAddress(pickup_address);
                        prediction.setContact(pickup_contact);

                        predictionArrayList.add(prediction);


                        if (multiple_stops.equalsIgnoreCase("1")){


                            JSONArray jsonArray = object.getJSONArray("stops_details");

                            for (int j=0;j<jsonArray.length();j++){

                                prediction = new Prediction();

                                String[] header = jsonArray.getJSONObject(j).getString("stop_location").split("\\s+");

                                prediction.setLocation_title((header[0] + " " + header[1]).replaceAll(",",""));

                                prediction.setLocation_name(jsonArray.getJSONObject(j).getString("stop_location"));
                                prediction.setAddress(jsonArray.getJSONObject(j).getString("stop_address"));
                                prediction.setContact(jsonArray.getJSONObject(j).getString("stop_contact"));

                                predictionArrayList.add(prediction);

                            }


                        }

                        String[] dest_header = dest_location.split("\\s+");
                        prediction = new Prediction();
                        prediction.setLocation_name(dest_location);
                        prediction.setLocation_title((dest_header[0] + " " + dest_header[1]).replaceAll(",",""));
                        prediction.setAddress(destination_address);
                        prediction.setContact(destination_contact);

                        predictionArrayList.add(prediction);


                        myOrder.setPredictionArrayList(predictionArrayList);


                        stopInfoAdapter = new StopInfoAdapter(getActivity(), predictionArrayList);
                        stop_list.setAdapter(stopInfoAdapter);
                        UtilsManager.setListViewHeightBasedOnItems(stop_list);
                       // UtilsManager.updateListHeight(getActivity(),60,stop_list,predictionArrayList.size());

                        /*bigSidePointAdapter = new BigSidePointAdapter(predictionArrayList.size(), getActivity());
                        side_point_list.setAdapter(bigSidePointAdapter);
                        UtilsManager.setListViewHeightBasedOnItems(side_point_list);*/
                      //  UtilsManager.updateListHeight(getActivity(),55,side_point_list,predictionArrayList.size());


                        String member_connected = object.getString("is_assigned");


                        if (member_connected.toLowerCase().equalsIgnoreCase("y")){
                            m_connected = true;

                        }else {
                            m_connected = false;
                        }

                        myOrder.setIs_assigned(m_connected);

                        /*if (job_state==1){
                            m_connected = false;
                        }else {
                            m_connected = true;
                        }*/

                        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Constants.MEET_LOCATION, meet_location);
                        editor.putString(Constants.DESTINATION, dest_location);
                        editor.putString(Constants.MEET_LAT, meet_lat);
                        editor.putString(Constants.MEET_LONG, meet_lon);
                        editor.putString(Constants.TOTAL, order_total);
                        editor.putString(Constants.PREFS_TOTAL_DISTANCE, total_distance);
                        editor.putString(Constants.ORDER_ID, order_id);
                        editor.apply();

                        //getMemberSelectionStatus();


                        try {

                            String[] service_type_name_list = service_type_name.split(",");
                            String[] basic_service_list = basic_service.split(",");
                            String[] cost_list = fixed_cost.split(",");


                            String additional_services = "", additional_prices = "";
                            for (int i = 0; i < service_type_name_list.length; i++) {

                                if (!service_type_name_list[i].toString().trim().equalsIgnoreCase("")) {
                                    additional_services = additional_services + service_type_name_list[i] + ",";
                                    additional_prices = additional_prices + cost_list[i] + ",";
                                }

                            }

                            myOrder.setAdditional_prices(additional_prices);
                            myOrder.setAdditional_services(additional_services);

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                       /* if (additional_services.length() > 1){


                            String[] services = additional_services.split(Pattern.quote(","));
                            AdditionalServicesAdapter additionalServicesAdapter = new AdditionalServicesAdapter(services,getActivity());
                            services_list_view.setAdapter(additionalServicesAdapter);

                           *//* LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) services_list_view.getLayoutParams();
                            int height = 60 * services.length;

                            layoutParams.height = height;
                            services_list_view.setLayoutParams(layoutParams);*//*

                            // UtilsManager.setListViewHeightBasedOnChildren(services_list_view);
                             additional_lay.setVisibility(View.GONE);
                        }else {
                            additional_lay.setVisibility(View.GONE);
                        }*/

                        ArrayList<MyOrder> myOrders = new ArrayList<>();
                        myOrders.add(myOrder);
                        scheduledJobAdapter = new ScheduledJobAdapter(myOrders,getActivity());
                        current_job_list.setAdapter(scheduledJobAdapter);



                    } catch (Exception e) {
                        e.printStackTrace();
                        new RideDirectionPointsDB(getActivity()).clearSavedPoints();
                        current_job_lay.setVisibility(View.GONE);
                        home_frame.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {
                        String response = new String(responseBody);
                        JSONObject object = new JSONObject(response);
                        Log.e("response failed", object.toString());
                        current_job_lay.setVisibility(View.GONE);
                        home_frame.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    ShowAdvertisement showAdvertisement;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            showAdvertisement = (ShowAdvertisement) context;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getScheduledBookings()
    {

        String customerId = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).getString(Constants.PREFS_CUSTOMER_ID,"");
        asyncHttpClient.setConnectTimeout(20000);

        Log.e(TAG, "getScheduledBookings: "+ Constants.Host_Address + "orders/customer_scheduled_orders/"+UtilsManager.getApiKey(getActivity())+"/"+customerId+"/1");
        asyncHttpClient.get(getActivity(), Constants.Host_Address + "orders/customer_scheduled_orders/"+UtilsManager.getApiKey(getActivity())+"/"+customerId+"/1", new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
               // Sh("Loading Orders...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {

                  //  hideDialoge();
                    home_frame.findViewById(R.id.no_data_lay).setVisibility(View.GONE);
                    String json = new String(responseBody);

                    UtilsManager.isInvalidKey(getActivity(),json);

                    JSONObject object = new JSONObject(json);
                    Log.e("response",json);

                    if (object.has("data"))
                    {

                        JSONArray array = object.getJSONArray("data");

                        myOrders = new ArrayList<>();

                        if (array.length() > 0)
                            getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).edit().putBoolean(Constants.PROFILE_EDITABLE,false).apply();


                        for (int i=0;i<array.length();i++)
                        {

                            String order_num = array.getJSONObject(i).getString("order_id");
                            String order_meet = array.getJSONObject(i).getString("meet_location");
                            String order_destination = array.getJSONObject(i).getString("destination");
                            String order_datetime = array.getJSONObject(i).getString("datetime_ordered");
                            String meet_datetime = array.getJSONObject(i).getString("meet_datetime");
                            String order_total = array.getJSONObject(i).getString("order_total");


                            String pickup_address = array.getJSONObject(i).getString("pickup_address");
                            String pickup_contact = array.getJSONObject(i).getString("pickup_contact");
                            String destination_address = array.getJSONObject(i).getString("destination_address");
                            String destination_contact = array.getJSONObject(i).getString("destination_contact");


                            String service_name = array.getJSONObject(i).getString("service_id");
                            String service_type_name = array.getJSONObject(i).getString("service_name");

                            if (service_type_name.equalsIgnoreCase("null"))
                                service_type_name = "";

                            String total_distance = array.getJSONObject(i).getString("total_distance");

                            String basic_service = array.getJSONObject(i).getString("basic_service");
                            String fixed_cost = array.getJSONObject(i).getString("fixed_cost");
                            String basic_price = array.getJSONObject(i).getString("basic_price");

                            String member_assigned =  array.getJSONObject(i).getString("is_assigned");


                            ArrayList<Prediction> predictionArrayList = new ArrayList<>();
                            Prediction prediction;

                            String[] pick_header = order_meet.split("\\s+");

                            prediction = new Prediction();
                            prediction.setLocation_name(order_meet);
                            prediction.setLocation_title((pick_header[0] + " " + pick_header[1]).replaceAll(",",""));
                            prediction.setAddress(pickup_address);
                            prediction.setContact(pickup_contact);

                            predictionArrayList.add(prediction);

                            String isMulti = array.getJSONObject(i).getString("multiple_stops");

                            if (isMulti.equalsIgnoreCase("1")){
                                JSONArray jsonArray = array.getJSONObject(i).getJSONArray("stops_details");

                                for (int j=0;j<jsonArray.length();j++){

                                    prediction = new Prediction();
                                    prediction.setLocation_name(jsonArray.getJSONObject(j).getString("stop_location"));

                                    pick_header = jsonArray.getJSONObject(j).getString("stop_location").split("\\s+");
                                    prediction.setLocation_title((pick_header[0] + " " + pick_header[1]).replaceAll(",",""));

                                    prediction.setAddress(jsonArray.getJSONObject(j).getString("stop_address"));
                                    prediction.setContact(jsonArray.getJSONObject(j).getString("stop_contact"));

                                    predictionArrayList.add(prediction);

                                }
                            }


                            pick_header = order_destination.split("\\s+");

                            prediction = new Prediction();
                            prediction.setLocation_name(order_destination);
                            prediction.setLocation_title((pick_header[0] + " " + pick_header[1]).replaceAll(",",""));
                            prediction.setAddress(destination_address);
                            prediction.setContact(destination_contact);

                            predictionArrayList.add(prediction);


                            String driver_id =  array.getJSONObject(i).getString("driver_id");
                            String driver_full_name =  array.getJSONObject(i).getString("driver_full_name");
                            String driver_profile_img =  array.getJSONObject(i).getString("driver_profile_img");
                            String vehicle_plate_number = array.getJSONObject(i).getString("vehicle_plate_number");


                            MyOrder order = new MyOrder();


                            order.setPredictionArrayList(predictionArrayList);
                            order.setOrder_id(order_num);
                            order.setPick_location(order_meet);
                            order.setDest_location(order_destination);
                            order.setOrder_date(meet_datetime);
                            order.setOrder_total(order_total);
                            order.setService_name(service_name);
                            order.setMain_service_name(service_name);
                            order.setTotal_distance(total_distance);
                            order.setBasic_price(basic_price);

                            order.setDriver_id(driver_id);
                            order.setDriver_image(driver_profile_img);
                            order.setDriver_name(driver_full_name);
                            order.setDriver_plate(vehicle_plate_number);

                            if (member_assigned.equalsIgnoreCase("N"))
                                order.setIs_assigned(false);
                            else
                                order.setIs_assigned(true);

                            String additional_services = "",additional_prices="";

                            try {

                                String[] service_names = service_type_name.split(",");
                                String[] service_types = basic_service.split(",");
                                String[] cost_list = fixed_cost.split(",");

                                for (int j=0;j<service_names.length;j++){

                                    if (!service_names[j].trim().toString().equalsIgnoreCase("")) {
                                        additional_services = additional_services + service_names[j] + ",";
                                        additional_prices = additional_prices + cost_list[j] + ",";
                                    }
                                }

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }



                            order.setAdditional_services(additional_services);
                            order.setAdditional_prices(additional_prices);


                          myOrders.add(order);
                        }


                        if (myOrders.size()==0)
                            home_frame.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);


                        scheduledJobAdapter = new ScheduledJobAdapter(myOrders,getActivity());
                        current_job_list.setAdapter(scheduledJobAdapter);



                    }
                    else
                    {

                        getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).edit().putBoolean(Constants.PROFILE_EDITABLE,true).apply();
                        home_frame.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);

                       /* String phone = getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getString(Constants.PREFS_USER_MOBILE,"");
                        if (phone.equalsIgnoreCase("")){
                            startActivity(new Intent(getActivity(), Profile_Screen.class).putExtra("back",true));
                            Toast.makeText(getActivity(),"Add Phone Number",Toast.LENGTH_SHORT).show();
                        }else {
                            boolean show_ad = getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getBoolean(Constants.SHOW_AD,false);
                            startActivity(new Intent(getActivity(), Create_Order_Screen.class).putExtra("show_ads",show_ad));
                        }*/

                    }

                }
                catch (Exception e)
                {
                    getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).edit().putBoolean(Constants.PROFILE_EDITABLE,true).apply();
                    Log.e(TAG," "+e.getMessage());
                    home_frame.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).edit().putBoolean(Constants.PROFILE_EDITABLE,true).apply();

                  //  hideDialoge();
                    String json = new String(responseBody);
                    Log.e("RESPONSE",json);
                    JSONObject object = new JSONObject(json);
                    home_frame.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }


}
