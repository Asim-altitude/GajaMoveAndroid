package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.adapter.BigSidePointAdapter;
import customer.gajamove.com.gajamove_customer.adapter.MultiLocationAdapter;
import customer.gajamove.com.gajamove_customer.adapter.PriceServiceListAdapter;
import customer.gajamove.com.gajamove_customer.adapter.SidePointAdapter;
import customer.gajamove.com.gajamove_customer.adapter.StopInfoAdapter;
import customer.gajamove.com.gajamove_customer.models.PlaceOrderObj;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class ReviewOrder extends BaseActivity {
    private static final String TAG = "ReviewOrder";

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.review_order));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    PlaceOrderObj placeOrderObj = null;
    private TextView service_name_txt,selected_date,pickup_location_txt,extra_km_txt,
            destination_location_txt,order_total_txt,pick_header_text,destination_header_text;

    private ImageView vehicle_image;
    private Button pay_now;

    private ListView price_list_view,side_point_list,stop_list;
    private PriceServiceListAdapter priceServiceListAdapter;
    LinearLayout basic_side_point;

    StopInfoAdapter stopInfoAdapter;
    BigSidePointAdapter sidePointAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_order);
        includeActionBar();
        Constants.enableSSL(asyncHttpClient);
        try {
            placeOrderObj = (PlaceOrderObj) getIntent().getSerializableExtra("order");

            service_name_txt = findViewById(R.id.service_name);
            selected_date = findViewById(R.id.selected_date);

            side_point_list = findViewById(R.id.side_point_list);
            stop_list = findViewById(R.id.stop_list);
            basic_side_point = findViewById(R.id.basic_side_point);

            ArrayList<Prediction> predictions = new ArrayList<>();

           // predictions.add(placeOrderObj.getPickup());

            if (placeOrderObj.isMulti()) {
                for (int i = 0; i < placeOrderObj.getStopList().size(); i++) {
                    predictions.add(placeOrderObj.getStopList().get(i));
                }
            }

          //  predictions.add(placeOrderObj.getDestination());

            MultiLocationAdapter multiLocationAdapter = new MultiLocationAdapter(predictions,this);
            stop_list.setAdapter(multiLocationAdapter);
            /*stopInfoAdapter = new StopInfoAdapter(this, predictions);
            stop_list.setAdapter(stopInfoAdapter);
*/
            stop_list.setVisibility(View.VISIBLE);
            UtilsManager.setListViewHeightBasedOnItems(stop_list);


          /*  if (placeOrderObj.isMulti()) {

                sidePointAdapter = new BigSidePointAdapter(placeOrderObj.getStopList().size() + 2, this);
                side_point_list.setAdapter(sidePointAdapter);
                UtilsManager.setListViewHeightBasedOnChildren(side_point_list);
                side_point_list.setVisibility(View.VISIBLE);
                basic_side_point.setVisibility(View.GONE);
            }*/


           /* pickup_location_txt = findViewById(R.id.pickup_location_name);
            destination_location_txt = findViewById(R.id.destination_location_name);
            pick_header_text = findViewById(R.id.pickup_header_text);
            destination_header_text = findViewById(R.id.destination_header_text);
         */   extra_km_txt = findViewById(R.id.extra_km_label);

            order_total_txt = findViewById(R.id.vehicle_price_label);
            price_list_view = findViewById(R.id.services_prices_list);

            vehicle_image = findViewById(R.id.vehicle_image);
            Picasso.with(this).load(placeOrderObj.getSelected_service_image()).placeholder(R.drawable.bike).into(vehicle_image);

            pay_now = findViewById(R.id.place_order_btn);


            priceServiceListAdapter = new PriceServiceListAdapter(placeOrderObj.getPriceServiceList(), ReviewOrder.this);
            price_list_view.setAdapter(priceServiceListAdapter);
            UtilsManager.updateListHeight(this,40,price_list_view,placeOrderObj.getPriceServiceList().size());


            pay_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startActivity(new Intent(ReviewOrder.this,PaymentScreen.class));

                    SaveBumbleRideOrderApi();
                /*startActivity(new Intent(ReviewOrder.this,FindDriverScreen.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();*/
                }
            });

            service_name_txt.setText(placeOrderObj.getService_name());
            selected_date.setText(placeOrderObj.getDisplay_date());
           // pickup_location_txt.setText(placeOrderObj.getPickup().getLocation_name());
          //  destination_location_txt.setText(placeOrderObj.getDestination().getLocation_name());


            /*try {
                String[] pick_header = placeOrderObj.getPickup().getLocation_name().split("\\s+");
                String[] dest_header = placeOrderObj.getDestination().getLocation_name().split("\\s+");


                pick_header_text.setText((pick_header[0] + " " + pick_header[1]).replaceAll(",", ""));
                destination_header_text.setText((dest_header[0] + " " + dest_header[1]).replaceAll(",", ""));

            } catch (Exception e) {
                e.printStackTrace();
            }*/



       /* starting_price_txt.setText("RM"+placeOrderObj.getStarting_price());
        extra_km_txt.setText("RM"+placeOrderObj.getExtra_km());*/
            order_total_txt.setText("RM" + placeOrderObj.getOrder_total());
            extra_km_txt.setText(placeOrderObj.getExtra_km() + "KM");


        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(ReviewOrder.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    private ProgressDialog progressDialog;
    private void showDialog(String message)
    {
        progressDialog = new ProgressDialog(ReviewOrder.this);
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    private void hideDialog()
    {
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private void SaveBumbleRideOrderApi()
    {

        asyncHttpClient.setConnectTimeout(20000);
        asyncHttpClient.setTimeout(20000);
        String customer_id = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getString(Constants.PREFS_CUSTOMER_ID,"20");

        RequestParams params = new RequestParams();



        params.put("service_id", placeOrderObj.getService_id());
        params.put("service_type_id", placeOrderObj.getService_type_id());

        params.put("key", UtilsManager.getApiKey(ReviewOrder.this));
        params.put("customer_id",customer_id);
        params.put("booking_type","Scheduled");
        params.put("payment_type","online");
        params.put("request_type","0");

        params.put("valid_amount",placeOrderObj.isValid());
        params.put("amount",placeOrderObj.getPayment_amount());

        if (placeOrderObj.isImmediate()) {
            params.put("meetup_datetime", placeOrderObj.getDate().toLowerCase().replace("am","").replace("pm",""));
        }else {
            params.put("meetup_datetime", placeOrderObj.getDate());
        }

        params.put("meet_location", placeOrderObj.getStopList().get(0).getLocation_name());
        params.put("destination", placeOrderObj.getStopList().get(placeOrderObj.getStopList().size()-1).getLocation_name());


        if (placeOrderObj.getStopList().size() > 2){
            params.put("multiple_locations", "1");

            int index = 0;
            for (int i=0;i<placeOrderObj.getStopList().size();i++){

                if (i!=0 && i!=placeOrderObj.getStopList().size()-1) {
                    params.put("stop_name[" + index + "]", placeOrderObj.getStopList().get(i).getLocation_name());
                    params.put("stop_lat[" + index + "]", placeOrderObj.getStopList().get(i).getLat());
                    params.put("stop_lng[" + index + "]", placeOrderObj.getStopList().get(i).getLng());
                    params.put("stop_contact[" + index + "]", "0060" + placeOrderObj.getStopList().get(i).getContact());
                    params.put("stop_address[" + index + "]", placeOrderObj.getStopList().get(i).getAddress());
                    index++;
                }
            }

        }else {
            params.put("multiple_locations", "2");
        }

        params.put("loc_lat",  placeOrderObj.getStopList().get(0).getLat()+"");
        params.put("loc_lng",  placeOrderObj.getStopList().get(0).getLng()+"");
        params.put("dis_lat",  placeOrderObj.getStopList().get(placeOrderObj.getStopList().size()-1).getLat()+"");
        params.put("dis_lng",  placeOrderObj.getStopList().get(placeOrderObj.getStopList().size()-1).getLng()+"");

        params.put("pickup_address",  placeOrderObj.getStopList().get(0).getAddress());
        params.put("destination_address", placeOrderObj.getStopList().get(placeOrderObj.getStopList().size()-1).getAddress());
        params.put("pickup_contact",  "0060"+placeOrderObj.getStopList().get(0).getContact());
        params.put("destination_contact",  "0060"+placeOrderObj.getStopList().get(placeOrderObj.getStopList().size()-1).getContact());


        asyncHttpClient.post(ReviewOrder.this, Constants.Host_Address + "orders/place_bumble_order", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Placing Order");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    JSONObject response_object = new JSONObject(response);
                    Log.e(TAG,response);
                    if (response_object.getString("status").equalsIgnoreCase("success")) {
                       String order_id = response_object.getJSONObject("data").getString("order_id");

                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.FCM_PREF),MODE_PRIVATE);
                        String seleted_language = sharedPreferences.getString(Constants.LANGUAGE_CODE,"en");
                        String ipay_url = "";
                        if (response_object.getJSONObject("data").has("url")) {
                            ipay_url = response_object.getJSONObject("data").getString("url");

                            startActivity(new Intent(ReviewOrder.this, PaymentScreen.class)
                                    .putExtra("url", ipay_url+"&lang="+seleted_language+"")
                                    .putExtra("order_id", order_id));
                            return;
                        }else {
                            startActivity(new Intent(ReviewOrder.this, FindDriverScreen.class)
                                    .putExtra("order_id", order_id)
                                    .putExtra("customer_id", customer_id)

                            );

                            finish();

                        }
                    }
                    else
                    {
                        UtilsManager.showAlertMessage(ReviewOrder.this,"","Invalid api response");
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
