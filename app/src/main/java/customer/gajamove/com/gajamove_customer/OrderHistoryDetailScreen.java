package customer.gajamove.com.gajamove_customer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.adapter.AdditionalPriceServicesAdapter;
import customer.gajamove.com.gajamove_customer.adapter.BigSidePointAdapter;
import customer.gajamove.com.gajamove_customer.adapter.MultiLocationAdapter;
import customer.gajamove.com.gajamove_customer.adapter.StopInfoAdapter;
import customer.gajamove.com.gajamove_customer.models.MyOrder;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;

public class OrderHistoryDetailScreen extends BaseActivity {

    MyOrder myOrder = null;
    TextView total_cost,cust_name,total_km,basic_price,extra_km_lbl,pick_header,main_service_name,pick_location,dest_header,dest_location;
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
        title.setText(getResources().getString(R.string.order_history));
    }


    LinearLayout back_btn;
    ListView services_listview,stop_list,side_point_list;
    AdditionalPriceServicesAdapter additionalPriceServicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_detail_screen);

        myOrder = (MyOrder) getIntent().getSerializableExtra("order");

        setupToolBar();

        total_cost = findViewById(R.id.total_cost);
        total_km = findViewById(R.id.total_km);
        cust_name = findViewById(R.id.user_name);
        cust_image = findViewById(R.id.user_image);
        pick_header = findViewById(R.id.pickup_header_text);
        pick_location = findViewById(R.id.pickup_location_txt);
        dest_header = findViewById(R.id.destination_header_text);
        dest_location = findViewById(R.id.destination_location_txt);
        extra_km_lbl = findViewById(R.id.extra_km_label);
        main_service_name = findViewById(R.id.main_service_name);
        basic_price = findViewById(R.id.base_price);
        stop_list = findViewById(R.id.stop_list);
        side_point_list = findViewById(R.id.side_point_list);

        services_listview = findViewById(R.id.services_list_view);


        try {

            if (myOrder.getAdditional_services().equalsIgnoreCase("")){
                services_listview.setVisibility(View.GONE);
            }else {
                additionalPriceServicesAdapter = new AdditionalPriceServicesAdapter(myOrder.getAdditional_services().split(","), myOrder.getAdditional_prices().split(","), this);
                services_listview.setAdapter(additionalPriceServicesAdapter);
                UtilsManager.updateListHeight(this, 30, services_listview, myOrder.getAdditional_services().split(",").length);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        // UtilsManager.setListViewHeightBasedOnChildren(services_listview);

        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        total_cost.setText("RM"+myOrder.getOrder_total());
        extra_km_lbl.setText(myOrder.getTotal_distance()+"KM");
        total_km.setText(UtilsManager.parseNewDateToddMMyyyy(myOrder.getOrder_date()));
        cust_name.setText(myOrder.getCust_name());
        pick_location.setText(myOrder.getPick_location());
        dest_location.setText(myOrder.getDest_location());
        main_service_name.setText(myOrder.getMain_service_name());
        basic_price.setText(myOrder.getBasic_price());

        String[] pick_header_arr = myOrder.getPick_location().split("\\s+");
        String[] dest_header_arr = myOrder.getDest_location().split("\\s+");

        pick_header.setText((pick_header_arr[0] + " " + pick_header_arr[1]).replaceAll(",",""));
        dest_header.setText((dest_header_arr[0] + " " + dest_header_arr[1]).replaceAll(",",""));


        try
        {

            Picasso.with(this).load(Constants.SERVICE_IMAGE_BASE_PATH+myOrder.getCust_image()).placeholder(R.drawable.profile_icon).into(cust_image);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        ArrayList<Prediction> predictions = new ArrayList<>();

        String[] header;

        Prediction prediction = new Prediction();
        header = myOrder.getPick_location().split("\\s+");
        prediction.setLocation_title(header[0]+" "+header[1].replaceAll(",",""));
        prediction.setLocation_name(myOrder.getPick_location());
        prediction.setAddress("");
        prediction.setContact("");
        predictions.add(prediction);



        if (myOrder.getPredictionArrayList()!=null){

            for (int i=0;i<myOrder.getPredictionArrayList().size();i++){

                prediction = new Prediction();
                header = myOrder.getPredictionArrayList().get(i).getLocation_name().split("\\s+");
                prediction.setLocation_title(header[0]+" "+header[1].replaceAll(",",""));
                prediction.setLocation_name(myOrder.getPredictionArrayList().get(i).getLocation_name());
                prediction.setContact(myOrder.getPredictionArrayList().get(i).getContact());
                prediction.setAddress(myOrder.getPredictionArrayList().get(i).getAddress());
                predictions.add(prediction);

            }
        }


        prediction = new Prediction();
        header = myOrder.getDest_location().split("\\s+");
        prediction.setLocation_title(header[0]+" "+header[1].replaceAll(",",""));
        prediction.setLocation_name(myOrder.getDest_location());
        prediction.setAddress("");
        prediction.setContact("");
        predictions.add(prediction);


        MultiLocationAdapter multiLocationAdapter = new MultiLocationAdapter(predictions,this);
        stop_list.setAdapter(multiLocationAdapter);
        UtilsManager.setListViewHeightBasedOnItems(stop_list);

        /*StopInfoAdapter stopInfoAdapter = new StopInfoAdapter(this, predictions);
        stop_list.setAdapter(stopInfoAdapter);
        UtilsManager.updateListHeight(this,60,stop_list,predictions.size());

        BigSidePointAdapter sidePointAdapter = new BigSidePointAdapter(predictions.size(), this);
        side_point_list.setAdapter(sidePointAdapter);
        UtilsManager.updateListHeight(this,55,side_point_list,predictions.size());
*/




    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
