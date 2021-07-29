package customer.gajamove.com.gajamove_customer.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import customer.gajamove.com.gajamove_customer.Create_Order_Screen;
import customer.gajamove.com.gajamove_customer.CurrentJobScreen;
import customer.gajamove.com.gajamove_customer.FindDriverScreen;
import customer.gajamove.com.gajamove_customer.HomeScreen;
import customer.gajamove.com.gajamove_customer.MyApp;
import customer.gajamove.com.gajamove_customer.OrderHistoryDetailScreen;
import customer.gajamove.com.gajamove_customer.Profile_Screen;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.adapter.ScheduledJobAdapter;
import customer.gajamove.com.gajamove_customer.auth.CreatePassword;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.models.MyOrder;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.RideDirectionPointsDB;
import customer.gajamove.com.gajamove_customer.utils.ShowAdvertisement;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

public class ScheduledJobTab extends Fragment {
    private static final String TAG = "ScheduledJobTab";

    ListView listView;
    ScheduledJobAdapter scheduledJobAdapter;
    List<MyOrder> myOrderList;
    View home_frame;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         home_frame = inflater.inflate(R.layout.scheduled_tab_lay,container,false);

        listView = home_frame.findViewById(R.id.scheduledlistView);

        myOrderList = new ArrayList<>();
        scheduledJobAdapter = new ScheduledJobAdapter(myOrderList,getActivity());
        listView.setAdapter(scheduledJobAdapter);

        Constants.enableSSL(asyncHttpClient);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               /* Intent intent = new Intent(getActivity(), OrderHistoryDetailScreen.class);
                intent.putExtra("order",myOrderList.get(i));
                startActivity(intent);*/
            }
        });




        return home_frame;

    }


    @Override
    public void onResume() {
        super.onResume();

        getCurrentOrderInformation();
    }


    private ProgressDialog progress;
    private void showProgressDialog(String message)
    {

        progress = new ProgressDialog(getActivity());
        progress.setMessage(message);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }

    boolean show_ad= false;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getScheduledBookings()
    {

        String customerId = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).getString(Constants.PREFS_CUSTOMER_ID,"");
        asyncHttpClient.setConnectTimeout(20000);

        Log.e(TAG, "getScheduledBookings: "+ Constants.Host_Address + "orders/customer_scheduled_orders/tgs_appkey_amin/"+customerId+"/0");
        asyncHttpClient.get(getActivity(), Constants.Host_Address + "orders/customer_scheduled_orders/"+UtilsManager.getApiKey(getActivity())+"/"+customerId+"/0", new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("Loading Orders..");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {

                    hideDialoge();
                    home_frame.findViewById(R.id.no_data_lay).setVisibility(View.GONE);
                    String json = new String(responseBody);

                    UtilsManager.isInvalidKey(getActivity(),json);

                    JSONObject object = new JSONObject(json);
                    Log.e("response",json);

                    if (object.has("data"))
                    {

                        JSONArray array = object.getJSONArray("data");

                        myOrderList.clear();

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

                            /*try {
                                JSONArray order_details = array.getJSONObject(i).getJSONArray("order_details");

                                for (int j=0;j<order_details.length();j++){

                                    if (order_details.getJSONObject(j).getString("basic_service").equalsIgnoreCase("2")){
                                        additional_services = additional_services + order_details.getJSONObject(j).getString("service_type_name") + ",";
                                    }

                                }

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }*/

                            order.setAdditional_services(additional_services);
                            order.setAdditional_prices(additional_prices);

                            if (order_id.equalsIgnoreCase("") || !order_id.equalsIgnoreCase(order.getOrder_id()))
                                myOrderList.add(order);
                        }

                        if (myOrderList.size()==0) {
                            home_frame.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);
                            show_ad = true;
                        }


                        scheduledJobAdapter.notifyDataSetChanged();

                        if (showAdvertisement!=null)
                            showAdvertisement.showAds(show_ad);

                    }
                    else
                    {
                        if (showAdvertisement!=null)
                            showAdvertisement.showAds(show_ad);

                        getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).edit().putBoolean(Constants.PROFILE_EDITABLE,true).apply();
                        home_frame.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);

                        String phone = sha_prefs.getString(Constants.PREFS_USER_MOBILE,"");
                        if (phone.equalsIgnoreCase("")){
                            startActivity(new Intent(getActivity(), Profile_Screen.class).putExtra("back",true));
                            Toast.makeText(getActivity(),"Add Phone Number",Toast.LENGTH_SHORT).show();
                        }else {
                            if (!show_ad) {
                                boolean show_ad = sha_prefs.getBoolean(Constants.SHOW_AD, false);
                                startActivity(new Intent(getActivity(), Create_Order_Screen.class).putExtra("show_ads", show_ad));

                            }

                        }

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

                    hideDialoge();
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

    SharedPreferences sha_prefs;
    String customer_id,order_id="";
    private void getCurrentOrderInformation() {

        sha_prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        customer_id = sha_prefs.getString(Constants.PREFS_CUSTOMER_ID, "0");
        String accessToken = sha_prefs.getString(Constants.PREFS_ACCESS_TOKEN, "");

        if (!customer_id.equalsIgnoreCase("0")) {

            asyncHttpClient.setConnectTimeout(20000);
            Log.e("current_order", Constants.Host_Address + "orders/customer_scheduled_orders/"+UtilsManager.getApiKey(getActivity())+"/"+customer_id+"/1");
            asyncHttpClient.get(getActivity(), Constants.Host_Address + "orders/customer_scheduled_orders/"+UtilsManager.getApiKey(getActivity())+"/"+customer_id+"/1", new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {

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

                        try {
                            JSONArray jsonArray = object.getJSONArray("data");
                            if (jsonArray.length() > 0)
                                show_ad = true;
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        getScheduledBookings();


                    } catch (Exception e) {
                        e.printStackTrace();
                        getScheduledBookings();

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {
                        getScheduledBookings();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
