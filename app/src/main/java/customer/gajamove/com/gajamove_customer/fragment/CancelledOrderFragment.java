package customer.gajamove.com.gajamove_customer.fragment;

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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import customer.gajamove.com.gajamove_customer.OrderHistoryDetailScreen;
import customer.gajamove.com.gajamove_customer.OrderHistoryScreen;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.adapter.OrderAdapter;
import customer.gajamove.com.gajamove_customer.models.MyOrder;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import cz.msebera.android.httpclient.Header;

public class CancelledOrderFragment extends Fragment
{
    private static final String TAG = "CompleteOrderFragment";

    SharedPreferences sharedPreferences;
    List<MyOrder> myOrdersList;
    ListView listView;
    OrderAdapter orderAdapter;
    View rootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.complete_order_lay,container,false);
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        customerId = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");
        accessToken = sharedPreferences.getString(Constants.PREFS_ACCESS_TOKEN,"");

        Constants.enableSSL(asyncHttpClient);
        listView = rootView.findViewById(R.id.order_list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), OrderHistoryDetailScreen.class);
                intent.putExtra("order",myOrdersList.get(i));
                startActivity(intent);
            }
        });

        getOrders();

        return rootView;
    }


    String accessToken,customerId;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public void getOrders(){

        String requestUrl = Constants.Host_Address+"orders/get_cancelled_orders/"+accessToken+"/"+customerId;
        Log.e(TAG, "getOrders: "+requestUrl);
        asyncHttpClient.get(requestUrl, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {

                    String json = new String(responseBody);
                    JSONObject object = new JSONObject(json);
                    Log.e("response",json);

                    if (true)
                    {
                        myOrdersList = new ArrayList<>();
                        JSONArray array = object.getJSONArray("data");


                        for (int i=0;i<array.length();i++)
                        {

                            String order_num = array.getJSONObject(i).getString("order_id");
                            String order_meet = array.getJSONObject(i).getString("meet_location");
                            String order_destination = array.getJSONObject(i).getString("destination");
                           // String order_datetime = array.getJSONObject(i).getString("datetime_ordered");
                            String meet_datetime = array.getJSONObject(i).getString("meet_datetime");
                          /*  String datetime_completed = array.getJSONObject(i).getString("datetime_completed");
                            String datetime_started = array.getJSONObject(i).getString("start_time");
                         */   String order_total = array.getJSONObject(i).getString("order_total");
                            String order_status = array.getJSONObject(i).getString("status");
                             String item_total = array.getJSONObject(i).getString("item_total");
                            String main_service_name = array.getJSONObject(i).getString("main_service_name");

                            String basic_price = array.getJSONObject(i).getString("basic_price");

                            String service_name = array.getJSONObject(i).getString("service_name");
                            String quantity = array.getJSONObject(i).getString("quantity");
                            String distance = array.getJSONObject(i).getString("total_distance");
                            if (service_name.equalsIgnoreCase("null")){
                                service_name = "";
                            }


                            String member_name = array.getJSONObject(i).getString("member_name");
                            String member_image = array.getJSONObject(i).getString("member_img");


                            String multiple_stops = array.getJSONObject(i).getString("multiple_stops");
                            MyOrder order = new MyOrder();
                            if (multiple_stops.equalsIgnoreCase("1")){
                                JSONArray jsonArray = array.getJSONObject(i).getJSONArray("stops_details");

                                ArrayList<Prediction> predictionArrayList = new ArrayList<>();
                                Prediction prediction;
                                for (int j=0;j<jsonArray.length();j++){

                                    prediction = new Prediction();
                                    prediction.setLocation_name(jsonArray.getJSONObject(j).getString("stop_location"));
                                    prediction.setAddress(jsonArray.getJSONObject(j).getString("stop_address"));
                                    prediction.setContact(jsonArray.getJSONObject(j).getString("stop_contact"));

                                    predictionArrayList.add(prediction);

                                }

                                order.setPredictionArrayList(predictionArrayList);

                            }



                            order.setTotal_distance(distance);
                            order.setMain_service_name(main_service_name);
                            order.setBasic_price(basic_price);
                            /*MyOrder order = new MyOrder();

                            String[] services = service_name.split(Pattern.quote(","));
                            String[] quantities = quantity.split(Pattern.quote(","));

                            for (int j=0;j<services.length;j++)
                            {
                                ServiceData data = new ServiceData();
                                if (!services[j].trim().equalsIgnoreCase(""))
                                {
                                    data.setService_name(services[j]);
                                    data.setService_quantity(quantities[j]);

                                    order.getService_data().add(data);
                                }
                            }*/

                            order.setOrder_id(order_num);
                            order.setPick_location(order_meet);
                            order.setDest_location(order_destination);
                            order.setOrder_total(order_total);
                            order.setStatus(order_status);
                            order.setOrder_date(meet_datetime);

                            order.setAdditional_services(service_name);
                            order.setAdditional_prices(quantity);
                            order.setCust_image(member_image);
                            order.setCust_name(member_name);

                            order.setAdditional_prices(item_total);


                            if (order.getStatus().toLowerCase().equalsIgnoreCase("cancelled"))
                                myOrdersList.add(order);
                        }



                        orderAdapter = new OrderAdapter(myOrdersList,getActivity());
                        listView.setAdapter(orderAdapter);

                    }
                    else
                    {
                        rootView.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);
                       // empty_orders_accepted.setVisibility(View.VISIBLE);
                    }

                }
                catch (Exception e)
                {
                    Log.e(TAG," "+e.getMessage());
                    rootView.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                rootView.findViewById(R.id.no_data_lay).setVisibility(View.VISIBLE);
            }
        });

    }


}
