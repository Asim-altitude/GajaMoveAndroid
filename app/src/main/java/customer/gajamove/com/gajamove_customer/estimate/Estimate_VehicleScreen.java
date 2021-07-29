package customer.gajamove.com.gajamove_customer.estimate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import customer.gajamove.com.gajamove_customer.BaseActivity;
import customer.gajamove.com.gajamove_customer.MyApp;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.ReviewOrder;
import customer.gajamove.com.gajamove_customer.adapter.PriceServiceListAdapter;
import customer.gajamove.com.gajamove_customer.adapter.VehicleAdapter;
import customer.gajamove.com.gajamove_customer.adapter.VehicleTypeAdapter;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.models.PlaceOrderObj;
import customer.gajamove.com.gajamove_customer.models.PriceService;
import customer.gajamove.com.gajamove_customer.models.Service_Slot;
import customer.gajamove.com.gajamove_customer.models.VehicleType;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

public class Estimate_VehicleScreen extends BaseActivity {
    private static final String TAG = "SelectVehicleScreen";

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.select_vehicle));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    RecyclerView service_list;
    ListView vehicle_list_;
    VehicleAdapter vehicleAdapter;
    VehicleTypeAdapter vehicleTypeAdapter;

    LinearLayout total_price_lay,starting_price_lay;
    Button confirm_btn;
    private PlaceOrderObj placeOrderObj = null;

    private String starting_price,extra_km,order_total;
    private TextView order_total_txt,starting_price_txt,extra_km_txt,description;

    private ListView prices_list_view;
    private PriceServiceListAdapter priceServiceListAdapter;
    private List<PriceService> priceServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estimate_vehicle_screen);

        includeActionBar();

        placeOrderObj = (PlaceOrderObj) getIntent().getSerializableExtra("order");

        order_total_txt = findViewById(R.id.vehicle_price_label);
        starting_price_txt = findViewById(R.id.starting_price_label);
        extra_km_txt = findViewById(R.id.extra_km_label);

        vehicle_list_ = findViewById(R.id.additional_service_list);
        service_list  = findViewById(R.id.vehicle_recycler_list);

        prices_list_view = findViewById(R.id.services_prices_list);

        confirm_btn = findViewById(R.id.confirm_btn);
        total_price_lay = findViewById(R.id.total_price_lay);
        starting_price_lay = findViewById(R.id.starting_price_lay);

        description = findViewById(R.id.description);


        priceServices = new ArrayList<>();
        priceServiceListAdapter = new PriceServiceListAdapter(priceServices, Estimate_VehicleScreen.this);
        prices_list_view.setAdapter(priceServiceListAdapter);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Estimate_VehicleScreen.this, LoginScreen.class));
                finish();
            }
        });

        choosenVehicles = new ArrayList<>();
        vehicleTypeAdapter = new VehicleTypeAdapter(Estimate_VehicleScreen.this,choosenVehicles);

        vehicle_list_.setAdapter(vehicleTypeAdapter);
        vehicle_list_.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (vehicleTypeAdapter.isMulti()){
                        if (choosenVehicles.get(position).isSelected())
                            choosenVehicles.get(position).setSelected(false);
                        else
                            choosenVehicles.get(position).setSelected(true);

                        selected_service_type_id = "";
                        for (int i=0;i<choosenVehicles.size();i++){
                            //  Log.e(TAG, "CHOSEN SERVICE ID "+choosenVehicles.get(i).getV_s_id()+" TYPE ID "+choosenVehicles.get(i).getV_id());

                            if (choosenVehicles.get(i).isSelected()){
                                if (selected_service_type_id.equalsIgnoreCase("")){
                                    selected_service_type_id = choosenVehicles.get(i).getV_id();
                                }else {
                                    selected_service_type_id = selected_service_type_id + ","+ choosenVehicles.get(i).getV_id();
                                }
                                // selected_service_type_id = choosenVehicles.get(i).getV_id() + ",";
                            }
                        }

                        for (int i=0;i<basicVehicleList.size();i++){

                            // Log.e(TAG, "BASIC SERVICE ID "+choosenVehicles.get(i).getV_s_id()+" TYPE ID "+choosenVehicles.get(i).getV_id());

                            if (basicVehicleList.get(i).getV_s_id().equalsIgnoreCase(selected_service_id)){
                                if (selected_service_type_id.equalsIgnoreCase("")){
                                    selected_service_type_id = basicVehicleList.get(i).getV_id();
                                }else {
                                    selected_service_type_id = selected_service_type_id + ","+ basicVehicleList.get(i).getV_id();
                                }
                                // selected_service_type_id = basicVehicleList.get(i).getV_id() + ",";
                            }
                        }

                        vehicleTypeAdapter.notifyDataSetChanged();
                       // UtilsManager.setListViewHeightBasedOnChildren(vehicle_list_);
                            CalculateBumbleRideFare();


                    }else {
                        vehicleTypeAdapter.setSelected(position);
                        selected_service_type_id = choosenVehicles.get(position).getV_id();
                        vehicleTypeAdapter.notifyDataSetChanged();
                        CalculateBumbleRideFare();
                      //  UtilsManager.setListViewHeightBasedOnChildren(vehicle_list_);
                    }



                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        total_price_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prices_list_view.getVisibility()==View.VISIBLE){
                    prices_list_view.setVisibility(View.GONE);
                    ((ImageView)((ViewGroup)total_price_lay.getChildAt(0)).getChildAt(0)).setRotation(0);
                }else {
                    prices_list_view.setVisibility(View.VISIBLE);
                    ((ImageView)((ViewGroup)total_price_lay.getChildAt(0)).getChildAt(0)).setRotation(180);
                }
            }
        });

        getAllDocumentServices();

    }

    ArrayList<Service_Slot> service_slots;
    private String selected_service_type_id = "";
    private String selected_service_id = "";
    private String service_name = "";
    List<VehicleType> vehicleTypeList,basicVehicleList;
    Integer[] images = {R.drawable.bike,R.drawable.car,R.drawable.pickup,R.drawable.van,R.drawable.lorry};
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getAllDocumentServices()
    {

        asyncHttpClient.setConnectTimeout(30000);

        asyncHttpClient.get(Estimate_VehicleScreen.this, Constants.Host_Address + "services/get_services_list/1/"+UtilsManager.getApiKey(Estimate_VehicleScreen.this)+"", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {


                    String response = new String(responseBody);
                    Log.e("SERVICES",response);
                    JSONObject data = new JSONObject(response);
                    JSONObject services = data.getJSONObject("data").getJSONObject("services");
                    JSONArray sub_services = data.getJSONObject("data").getJSONArray("sub_services");


                    service_slots = new ArrayList<>();
                    vehicleTypeList = new ArrayList<>();
                    basicVehicleList = new ArrayList<>();
                    choosenVehicles = new ArrayList<>();
                    Iterator<String> keys = services.keys();
                    while (keys.hasNext())
                    {
                        String key = keys.next();
                        Object value = services.get(key);

                        Service_Slot service_slot = new Service_Slot();
                        service_slot.setSlot_name(value.toString().split("#")[0]);
                        service_slot.setImage(Constants.SERVICE_IMAGE_BASE_PATH+value.toString().split("#")[1]);
                        service_slot.setOrder_format(value.toString().split("#")[2]);

                        if (value.toString().split("#").length > 3)
                            service_slot.setDescription(value.toString().split("#")[3]);
                        else
                            service_slot.setDescription("");

                        service_slot.setService_id(key);
                        service_slot.setActive(false);

                        service_slots.add(service_slot);
                    }


                    for (int i=0;i<sub_services.length();i++)
                    {
                        JSONObject object_ = sub_services.getJSONObject(i);
                        String sub_service_id = object_.getString("id");
                        String service_id = object_.getString("service_id");
                        String service_name = object_.getString("service_name");
                        String basic = object_.getString("basic_service");
                        String fixed_cost = object_.getString("fixed_cost");
                        int order_by = Integer.parseInt(object_.getString("order_by"));


                        VehicleType vehicleType = new VehicleType();
                        vehicleType.setV_id(sub_service_id);
                        vehicleType.setV_s_id(service_id);
                        vehicleType.setV_type(service_name);
                        vehicleType.setSelected(false);
                        vehicleType.setBasic(basic);
                        vehicleType.setPrice(fixed_cost);
                        vehicleType.setOrder_by(order_by);

                        if (basic.equalsIgnoreCase("1")){
                            basicVehicleList.add(vehicleType);
                        }else {
                            vehicleTypeList.add(vehicleType);
                        }

                    }



                    vehicleAdapter = new VehicleAdapter(service_slots, Estimate_VehicleScreen.this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Estimate_VehicleScreen.this);
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    service_list.setLayoutManager(linearLayoutManager);
                    vehicleAdapter.setOnItemClickListener(new VehicleAdapter.OnClickItem() {
                        @Override
                        public void onItemClick(int pos) {
                            try {



                                vehicleAdapter.setSelected(pos);
                                selected_service_id = service_slots.get(pos).getService_id();
                                service_name = service_slots.get(pos).getSlot_name();
                                vehicleAdapter.notifyDataSetChanged();
                                placeOrderObj.setSelected_service_image(service_slots.get(pos).getImage());
                                addVehicleTypes(service_slots.get(pos).getService_id());

                               // UtilsManager.setListViewHeightBasedOnChildren(vehicle_list_);
                                CalculateBumbleRideFare();

                                description.setText(service_slots.get(pos).getDescription());
                                isReady = true;

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });


                   // service_name = service_slots.get(0).getSlot_name();
                  //  placeOrderObj.setSelected_service_image(service_slots.get(0).getImage());


                    selected_service_type_id = "";
                    service_list.setAdapter(vehicleAdapter);
                  //  description.setText(service_slots.get(0).getDescription());
                   // selected_service_id = service_slots.get(0).getService_id();
                   // addVehicleTypes(service_slots.get(0).getService_id());

                   // vehicleTypeAdapter.notifyDataSetChanged();

                  //  CalculateBumbleRideFare();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try
                {
                    // hideDialog();
                    String response = new String(responseBody);
                    Log.e("response_fail",response);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    boolean isReady = false;

    List<VehicleType> choosenVehicles;

    private void addVehicleTypes(String name){

        try {
            if (vehicleTypeList.size() == 0)
                return;

            choosenVehicles.clear();
            for (int i = 0; i < vehicleTypeList.size(); i++) {

                if (vehicleTypeList.get(i).getV_s_id().toLowerCase().equalsIgnoreCase(name)) {
                    vehicleTypeList.get(i).setSelected(false);
                    choosenVehicles.add(vehicleTypeList.get(i));
                }
            }

            //selected_service_type_id = choosenVehicles.get(0).getV_id();
            //vehicleTypeAdapter.setSelected(0);

            selected_service_type_id = "";
            for (int i=0;i<basicVehicleList.size();i++){

                Log.e(TAG, "BASIC SERVICE ID "+basicVehicleList.get(i).getV_s_id()+" TYPE ID "+basicVehicleList.get(i).getV_id());


                if (basicVehicleList.get(i).getV_s_id().equalsIgnoreCase(selected_service_id)){
                    if (selected_service_type_id.equalsIgnoreCase("")){
                        selected_service_type_id = basicVehicleList.get(i).getV_id();
                    }else {
                        selected_service_type_id = selected_service_type_id + ","+ basicVehicleList.get(i).getV_id();
                    }
                    // selected_service_type_id = basicVehicleList.get(i).getV_id() + ",";
                }
            }


            Collections.sort(choosenVehicles, new Comparator<VehicleType>() {
                @Override
                public int compare(VehicleType vehicleType, VehicleType t1) {
                    return vehicleType.getOrder_by()-t1.getOrder_by();
                }
            });

            vehicleTypeAdapter = new VehicleTypeAdapter(Estimate_VehicleScreen.this,choosenVehicles);

            vehicle_list_.setAdapter(vehicleTypeAdapter);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    ProgressDialog progressDialog = null;
    private void hideDialog()
    {
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

    private void showDialog(String message) {

        if (progressDialog!=null)
            progressDialog.dismiss();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);

        if (getApplicationContext()!=null)
            progressDialog.show();
    }

    String customer_balance = "";
    private void CalculateBumbleRideFare()
    {

        asyncHttpClient.setConnectTimeout(20000);
        String customer_id = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getString(Constants.PREFS_CUSTOMER_ID,"20");

        RequestParams params = new RequestParams();
       /* Date date = new Date();
        String formmated_date = custome_date.format(date);
*/
        params.put("service_id", selected_service_id);

        params.put("service_type_id", selected_service_type_id);


       /* Log.e(TAG, "Service ID: "+selected_service_id );
        Log.e(TAG, "Service Type ID: "+selected_service_type_id);
*/
        params.put("key", UtilsManager.getApiKey(Estimate_VehicleScreen.this));
        params.put("customer_id","");

        if (placeOrderObj.isMulti()){
            params.put("multiple_locations", "1");


            for (int i=0;i<placeOrderObj.getStopList().size();i++){
                params.put("stop_name["+i+"]",placeOrderObj.getStopList().get(i).getLocation_name());
                params.put("stop_lat["+i+"]",placeOrderObj.getStopList().get(i).getLat());
                params.put("stop_lng["+i+"]",placeOrderObj.getStopList().get(i).getLng());
            }

        }else {
            params.put("multiple_locations", "2");
        }

        params.put("meetup_datetime",placeOrderObj.getDate());
        params.put("meet_location",placeOrderObj.getPickup().getLocation_name());
        params.put("destination", placeOrderObj.getDestination().getLocation_name());

        final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        prefs.edit().putString(Constants.MEET_LOCATION,placeOrderObj.getPickup().getLocation_name()).apply();
        prefs.edit().putString(Constants.DESTINATION,placeOrderObj.getDestination().getLocation_name()).apply();

        params.put("loc_lat",  Constants.meet.latitude+"");
        params.put("loc_lng",  Constants.meet.longitude+"");
        params.put("dis_lat",  Constants.destination.latitude+"");
        params.put("dis_lng",  Constants.destination.longitude+"");


        asyncHttpClient.post(Estimate_VehicleScreen.this, Constants.Host_Address + "orders/before_place_bumble_order", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Calculating Bill...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG,response);
                    JSONObject response_object = new JSONObject(response);

                    if (response_object.getString("status").equalsIgnoreCase("success")) {
                        // order_id = response_object.getJSONObject("data").getString("order_id");
                        order_total = response_object.getJSONObject("data").getString("order_total");
                      /*  extra_km = response_object.getJSONObject("data").getString("outstation_total");
                        starting_price = response_object.getJSONObject("data").getString("starting_price");
                      */  String total_distance = response_object.getJSONObject("data").getString("total_distance");

                        String basic_price = response_object.getJSONObject("data").getString("basic_price");
                        customer_balance = response_object.getJSONObject("data").getString("customer_balance");

                        prefs.edit().putString(Constants.TOTAL,order_total).apply();
                        // out.setText("RM"+outstation+".00");

                        placeOrderObj.setExtra_km(total_distance);
                        order_total_txt.setText("RM"+order_total);
                        extra_km_txt.setText(total_distance+"KM");

                        try
                        {




                            String name,value;
                            priceServices.clear();


                            PriceService priceService = new PriceService();
                            priceService.setService_name("Base Price");
                            priceService.setService_price(basic_price);
                            priceServices.add(priceService);


                            try {


                                JSONArray array = response_object.getJSONObject("data").getJSONArray("data_services");

                                for (int i = 0; i < array.length(); i++) {
                                    name = array.getJSONObject(i).getString("service_type_name");
                                    value = array.getJSONObject(i).getString("item_total");

                                    priceService = new PriceService();
                                    priceService.setService_name(name);
                                    priceService.setService_price(value);

                                    priceServices.add(priceService);

                                }

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                        priceServiceListAdapter.notifyDataSetChanged();
                        UtilsManager.setListViewHeightBasedOnChildren(prices_list_view);
                       // showBumbleRide();
                       // tvTotalPrice.setText("RM"+grand_total+".00");

                       /*
                        starting_price_txt.setText("RM"+starting_price);
                       */ Log.e(TAG, "onSuccess: DISTANCE "+ Constants.distance(Constants.meet.latitude,Constants.meet.longitude,Constants.destination.latitude,Constants.destination.longitude));


                        }
                        catch (Exception e){
                            e.printStackTrace();
                            priceServices.clear();
                            priceServiceListAdapter.notifyDataSetChanged();
                        }

                    }
                    else
                    {
                        UtilsManager.showAlertMessage(Estimate_VehicleScreen.this,"","Invalid api response");
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e("ERROR_IN_CALCULATION",e.getMessage());
                    priceServices.clear();
                    priceServiceListAdapter.notifyDataSetChanged();
                    order_total_txt.setText("RM0.0");


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("bumble_ride_failed",response);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
