package customer.gajamove.com.gajamove_customer.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Pattern;

import customer.gajamove.com.gajamove_customer.CurrentJobScreen;
import customer.gajamove.com.gajamove_customer.FindDriverScreen;
import customer.gajamove.com.gajamove_customer.OrderDetailScreen;
import customer.gajamove.com.gajamove_customer.OrderHistoryDetailScreen;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.MyOrder;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.RideDirectionPointsDB;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

public class ScheduledJobAdapter extends BaseAdapter {

    List<MyOrder> myOrderList;
    Activity context;

    public ScheduledJobAdapter(List<MyOrder> myOrderList, Activity context) {
        this.myOrderList = myOrderList;
        this.context = context;

        Constants.enableSSL(asyncHttp);
    }

    @Override
    public int getCount() {
        return myOrderList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    AlertDialog alertDialog;
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.scheduled_list_item,null);


        TextView service_name_txt = convertView.findViewById(R.id.service_name);
        TextView date_txt = convertView.findViewById(R.id.date_text);
        TextView total_cost = convertView.findViewById(R.id.total_cost);
        TextView pick_location_txt = convertView.findViewById(R.id.pickup_location_txt);
        TextView pick_location_header_txt = convertView.findViewById(R.id.pickup_header_text);
        TextView extra_km_label = convertView.findViewById(R.id.extra_km_label);
        TextView dest_location_txt = convertView.findViewById(R.id.destination_location_txt);
        TextView dest_location_header_txt = convertView.findViewById(R.id.destination_header_text);
        ListView services_list_view = convertView.findViewById(R.id.services_list_view);
        LinearLayout view_driver_btn = convertView.findViewById(R.id.view_driver_btn);
        LinearLayout current_job_lay = convertView.findViewById(R.id.current_job_lay);
        LinearLayout driver_lay = convertView.findViewById(R.id.driver_lay);
        LinearLayout proceed_lay = convertView.findViewById(R.id.proceed_lay);

        TextView driver_plate = convertView.findViewById(R.id.vehicle_plate);

        ImageView driver_image = convertView.findViewById(R.id.driver_image);
        TextView driver_name = convertView.findViewById(R.id.driver_name);

        TextView cancel_btn = convertView.findViewById(R.id.cancel_job);

        try
        {
            if (myOrderList.get(position).isIs_assigned()){
                driver_lay.setVisibility(View.VISIBLE);
                driver_name.setText(myOrderList.get(position).getDriver_name());
                Picasso.with(context).load(Constants.SERVICE_IMAGE_BASE_PATH+myOrderList.get(position).getDriver_image()).into(driver_image);
                driver_plate.setText(myOrderList.get(position).getDriver_plate()+"");
            }else {
                driver_lay.setVisibility(View.GONE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        if (UtilsManager.isCancelable(myOrderList.get(position).getOrder_date())){
            cancel_btn.setVisibility(View.VISIBLE);
        }else {
            cancel_btn.setVisibility(View.GONE);
        }


        proceed_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_driver_btn.performClick();
            }
        });



        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Cancel Job")
                        .setMessage("Are you sure you want to cancel job?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                CancelOrderApi(myOrderList.get(position).getOrder_id(),position);
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

        ListView stop_list = convertView.findViewById(R.id.stop_list);
        ListView side_point_list = convertView.findViewById(R.id.side_point_list);

        side_point_list.setVisibility(View.GONE);

        StopInfoAdapter stopInfoAdapter = new StopInfoAdapter(context, myOrderList.get(position).getPredictionArrayList());
        stop_list.setAdapter(stopInfoAdapter);
       // UtilsManager.updateListHeight(context,60,stop_list,myOrderList.get(position).getPredictionArrayList().size());
        UtilsManager.setListViewHeightBasedOnItems(stop_list);

        BigSidePointAdapter bigSidePointAdapter = new BigSidePointAdapter(myOrderList.get(position).getPredictionArrayList().size(), context);
        side_point_list.setAdapter(bigSidePointAdapter);
       // UtilsManager.updateListHeight(context,55,side_point_list,myOrderList.get(position).getPredictionArrayList().size());
        UtilsManager.setListViewHeightBasedOnItems(side_point_list);


        current_job_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        LinearLayout drop_down_lay = convertView.findViewById(R.id.drop_down_lay);

        drop_down_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  if (myOrderList.get(position).isExpanded()){
                    myOrderList.get(position).setExpanded(false);
                }else {
                    myOrderList.get(position).setExpanded(true);
                }

                notifyDataSetChanged();*/

                Intent intent = new Intent(context, OrderDetailScreen.class);
                intent.putExtra("order",myOrderList.get(position));
                context.startActivity(intent);
            }
        });



        view_driver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Constants.MEET_LOCATION, myOrderList.get(position).getPick_location());
                    editor.putString(Constants.DESTINATION, myOrderList.get(position).getDest_location());
                    editor.putString(Constants.TOTAL, myOrderList.get(position).getOrder_total());
                    editor.putString(Constants.ORDER_ID, myOrderList.get(position).getOrder_id());
                    editor.apply();

                    String customer_id = prefs.getString(Constants.PREFS_CUSTOMER_ID, "");

                    new RideDirectionPointsDB(context).clearSavedPoints();

                    if (myOrderList.get(position).isIs_assigned()) {
                        context.startActivity(new Intent(context, CurrentJobScreen.class)
                                .putExtra("order",myOrderList.get(position))
                                .putExtra("order_id", myOrderList.get(position).getOrder_id())
                                .putExtra("customer_id", customer_id)
                                .putExtra("stops",myOrderList.get(position).getPredictionArrayList())

                        );
                    } else {
                        context.startActivity(new Intent(context, FindDriverScreen.class)
                                .putExtra("order_id", myOrderList.get(position).getOrder_id())
                                .putExtra("customer_id", customer_id)

                        );
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


     /*   if (myOrderList.get(position).getAdditional_services().length() > 1){
            convertView.findViewById(R.id.additionalservices_lay).setVisibility(View.GONE);

            String[] services = myOrderList.get(position).getAdditional_services().split(Pattern.quote(","));
            AdditionalServicesAdapter additionalServicesAdapter = new AdditionalServicesAdapter(services,context);
            services_list_view.setAdapter(additionalServicesAdapter);

           *//* int height = 40 * services.length;

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) services_list_view.getLayoutParams();
            layoutParams.height = height;
            services_list_view.setLayoutParams(layoutParams);*//*

            UtilsManager.setListViewHeightBasedOnChildren(services_list_view);
        }else {
            convertView.findViewById(R.id.additionalservices_lay).setVisibility(View.GONE);
        }
*/

        total_cost.setText("RM"+myOrderList.get(position).getOrder_total());
        extra_km_label.setText(myOrderList.get(position).getTotal_distance()+"KM");
        service_name_txt.setText(myOrderList.get(position).getService_name());
        date_txt.setText(UtilsManager.parseDashboardTime(myOrderList.get(position).getOrder_date()));

        pick_location_txt.setText(myOrderList.get(position).getPick_location());
        dest_location_txt.setText(myOrderList.get(position).getDest_location());


        try {
            String[] pick_header = myOrderList.get(position).getPick_location().split("\\s+");
            String[] dest_header = myOrderList.get(position).getDest_location().split("\\s+");


            pick_location_header_txt.setText((pick_header[0] + " " + pick_header[1]).replaceAll(",",""));
            dest_location_header_txt.setText((dest_header[0] + " " + dest_header[1]).replaceAll(",",""));

        }
        catch (Exception e){
            e.printStackTrace();
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constants.MEET_LOCATION, myOrderList.get(position).getPick_location());
                editor.putString(Constants.DESTINATION, myOrderList.get(position).getDest_location());
                editor.putString(Constants.TOTAL, myOrderList.get(position).getOrder_total());
                editor.putString(Constants.ORDER_ID, myOrderList.get(position).getOrder_id());
                editor.apply();

                String customer_id = prefs.getString(Constants.PREFS_CUSTOMER_ID,"");

                if (myOrderList.get(position).isIs_assigned()){
                    context.startActivity(new Intent(context, CurrentJobScreen.class)
                            .putExtra("order_id", myOrderList.get(position).getOrder_id())
                            .putExtra("customer_id", customer_id)

                    );
                }else {
                    context.startActivity(new Intent(context, FindDriverScreen.class)
                            .putExtra("order_id", myOrderList.get(position).getOrder_id())
                            .putExtra("customer_id", customer_id)

                    );
                }
            }
        });

        return convertView;
    }


    AsyncHttpClient asyncHttp = new AsyncHttpClient();
    private void CancelOrderApi(String order_number,final int pos)
    {
        asyncHttp.setConnectTimeout(20000);
        Log.e("CANCEL_CURRENT_JOB", Constants.Host_Address + "orders/cancel_current_order/tgs_appkey_amin/" + order_number);
        asyncHttp.get(context, Constants.Host_Address + "orders/cancel_current_order/"+UtilsManager.getApiKey(context)+"/" + order_number, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Cancelling Ride");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    hideLoader();
                    myOrderList.remove(pos);
                    notifyDataSetChanged();


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
                    UtilsManager.showAlertMessage(context,"","Could not cancel order.");
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
        progress = new ProgressDialog(context);
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
