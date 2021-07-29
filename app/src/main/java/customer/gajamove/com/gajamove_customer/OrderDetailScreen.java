package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.adapter.AdditionalPriceServicesAdapter;
import customer.gajamove.com.gajamove_customer.adapter.BigSidePointAdapter;
import customer.gajamove.com.gajamove_customer.adapter.MultiLocationAdapter;
import customer.gajamove.com.gajamove_customer.adapter.StopInfoAdapter;
import customer.gajamove.com.gajamove_customer.auth.CreatePassword;
import customer.gajamove.com.gajamove_customer.models.BumbleRideInformation;
import customer.gajamove.com.gajamove_customer.models.FireBaseChatHead;
import customer.gajamove.com.gajamove_customer.models.MyOrder;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.RideDirectionPointsDB;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class OrderDetailScreen extends BaseActivity {

    MyOrder myOrder = null;
    TextView total_cost,cust_name,base_price,total_km,extra_km_lbl,pick_header,main_service_name,pick_location,dest_header,dest_location;
    ImageView cust_image;


    private void setupToolBar(){

        ImageView back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.order_details));
    }


    LinearLayout back_btn;
    ListView services_listview,stop_list,side_point_list;
    AdditionalPriceServicesAdapter additionalPriceServicesAdapter;

    Button cancel_btn;
    AlertDialog alertDialog;
    LinearLayout driver_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_screen);

        myOrder = (MyOrder) getIntent().getSerializableExtra("order");

        setupToolBar();
        Constants.enableSSL(asyncHttp);
        total_cost = findViewById(R.id.total_cost);
        total_km = findViewById(R.id.total_km);

        pick_header = findViewById(R.id.pickup_header_text);
        pick_location = findViewById(R.id.pickup_location_txt);
        dest_header = findViewById(R.id.destination_header_text);
        dest_location = findViewById(R.id.destination_location_txt);
        extra_km_lbl = findViewById(R.id.extra_km_label);
        base_price = findViewById(R.id.base_price);
        main_service_name = findViewById(R.id.main_service_name);
        services_listview = findViewById(R.id.services_list_view);
        stop_list = findViewById(R.id.stop_list);
        side_point_list = findViewById(R.id.side_point_list);
        cancel_btn = findViewById(R.id.cancel_order_btn);
        driver_lay = findViewById(R.id.driver_lay);

        driver_lay.setVisibility(View.VISIBLE);

        if (myOrder.isIs_assigned()){
            driver_lay.setVisibility(View.VISIBLE);
            ((TextView) driver_lay.getChildAt(1)).setText(myOrder.getDriver_name());
            Picasso.with(this).load(Constants.SERVICE_IMAGE_BASE_PATH+myOrder.getDriver_image()).into(((ImageView) driver_lay.getChildAt(0)));
        }else {
            driver_lay.setVisibility(View.GONE);
        }


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog = new AlertDialog.Builder(OrderDetailScreen.this)
                        .setTitle("Cancel Job")
                        .setMessage("Are you sure you want to cancel job?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                CancelOrderApi(myOrder.getOrder_id());
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
        });

        try {
            if (myOrder.getAdditional_services().equalsIgnoreCase("")){
                services_listview.setVisibility(View.GONE);

            }else {
                additionalPriceServicesAdapter = new AdditionalPriceServicesAdapter(myOrder.getAdditional_services().split(","), myOrder.getAdditional_prices().split(","), this);
                services_listview.setAdapter(additionalPriceServicesAdapter);
                UtilsManager.updateListHeight(this, 30, services_listview, myOrder.getAdditional_services().split(",").length);
                // UtilsManager.setListViewHeightBasedOnChildren(services_listview);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            services_listview.setVisibility(View.GONE);
        }



        total_cost.setText("RM"+myOrder.getOrder_total());
        total_km.setText(myOrder.getTotal_distance()+"KM");
        pick_location.setText(myOrder.getPick_location());
        dest_location.setText(myOrder.getDest_location());
        main_service_name.setText(myOrder.getMain_service_name());
        base_price.setText("RM"+myOrder.getBasic_price());

        String[] pick_header_arr = myOrder.getPick_location().split("\\s+");
        String[] dest_header_arr = myOrder.getDest_location().split("\\s+");


        pick_header.setText((pick_header_arr[0] + " " + pick_header_arr[1]).replaceAll(",",""));
        dest_header.setText((dest_header_arr[0] + " " + dest_header_arr[1]).replaceAll(",",""));


        ArrayList<Prediction> predictions = new ArrayList<>();

        String[] header;
        Prediction prediction;


        if (myOrder.getPredictionArrayList()!=null){

            for (int i=0;i<myOrder.getPredictionArrayList().size();i++){

                prediction = new Prediction();
                header = myOrder.getPredictionArrayList().get(i).getLocation_name().split("\\s+");
                prediction.setLocation_title(header[0]+" "+header[1].replaceAll(",",""));
                prediction.setLocation_name(myOrder.getPredictionArrayList().get(i).getLocation_name());
                prediction.setAddress(myOrder.getPredictionArrayList().get(i).getAddress());
                prediction.setContact(myOrder.getPredictionArrayList().get(i).getContact());
                predictions.add(prediction);

            }
        }


        MultiLocationAdapter stopInfoAdapter = new MultiLocationAdapter(predictions,this);
        stop_list.setAdapter(stopInfoAdapter);
        UtilsManager.setListViewHeightBasedOnItems(stop_list);
        //UtilsManager.updateListHeight(this,60,stop_list,predictions.size());

        /*BigSidePointAdapter sidePointAdapter = new BigSidePointAdapter(predictions.size(), this);
        side_point_list.setAdapter(sidePointAdapter);
        UtilsManager.updateListHeight(this,55,side_point_list,predictions.size());
*/

        if (myOrder.isIs_assigned()
                && UtilsManager.isCancelable(myOrder.getOrder_date())){
            cancel_btn.setVisibility(View.VISIBLE);
        }else if (!myOrder.isIs_assigned()){
            cancel_btn.setVisibility(View.VISIBLE);
        }else if (myOrder.isIs_assigned()
                && !UtilsManager.isCancelable(myOrder.getOrder_date())){
            cancel_btn.setVisibility(View.GONE);
        }

    }


    AsyncHttpClient asyncHttp = new AsyncHttpClient();
    private void CancelOrderApi(String order_number)
    {
        asyncHttp.setConnectTimeout(20000);
        Log.e("CANCEL_CURRENT_JOB", Constants.Host_Address + "orders/cancel_current_order/"+UtilsManager.getApiKey(OrderDetailScreen.this)+"/" + order_number);
        asyncHttp.get(OrderDetailScreen.this, Constants.Host_Address + "orders/cancel_current_order/"+UtilsManager.getApiKey(OrderDetailScreen.this)+"/" + order_number, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Cancelling Ride");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    hideLoader();

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

                    hideLoader();
                    String response = new String(responseBody);
                    Log.e("failure response",response);
                    UtilsManager.showAlertMessage(OrderDetailScreen.this,"","Could not cancel order.");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private ProgressDialog progress;

    private void showDialog(String message)
    {
        progress = new ProgressDialog(OrderDetailScreen.this);
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
}
