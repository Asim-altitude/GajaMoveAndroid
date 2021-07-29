package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.adapter.LocationAdapter;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.SaveLocationCallBack;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SavedRoutes extends AppCompatActivity {
    private static final String TAG = "SavedRoutes";

    ListView savedList;
    LocationAdapter locationAdapter;
    ArrayList<Prediction> predictionArrayList;

    SharedPreferences sharedPreferences;

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        TextView title = findViewById(R.id.title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirect_login.performClick();
                finish();
            }
        });

        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.saved_rout));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_routes);

        includeActionBar();
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        Constants.enableSSL(asyncHttpClient);
        savedList = findViewById(R.id.saved_location_list);
        predictionArrayList = new ArrayList<>();
        locationAdapter = new LocationAdapter(this,predictionArrayList);
        locationAdapter.setSaveLocationCallBack(new SaveLocationCallBack() {
            @Override
            public void onLocationSaved(int pos) {

            }

            @Override
            public void onRemoveLocation(int pos) {
                try
                {
                    ConfirmCancel(pos);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        savedList.setAdapter(locationAdapter);


    }


    AlertDialog alert;
    private void ConfirmCancel(final int pos) {
        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SavedRoutes.this);
        alertDialogBuilder.setMessage("Are you sure to remove this Address?")
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeLocationAPI(pos);
                            }
                        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
    }




    private ProgressDialog progressDialog = null;

    private void showDialog(String message){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideDialog(){
        if (progressDialog!=null)
            progressDialog.dismiss();
    }


    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private void removeLocationAPI(int pos) {


        asyncHttpClient.get(Constants.Host_Address + "customers/delete_favorite_locations/"+predictionArrayList.get(pos).getId()+"/"+UtilsManager.getApiKey(SavedRoutes.this)+"", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Removing Location");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: DELETE "+response);

                    getFavouriteLocations();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try
                {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onFailure: "+response);
                    Toast.makeText(SavedRoutes.this,"Internet/Server Error",Toast.LENGTH_LONG).show();
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
        getFavouriteLocations();
    }

    private void getFavouriteLocations() {

        String customer_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");


        Log.e(TAG, "getFavouriteLocations: "+Constants.Host_Address + "customers/get_favorite_locations/"+customer_id+"/tgs_appkey_amin" );
        asyncHttpClient.get(Constants.Host_Address + "customers/get_favorite_locations/"+customer_id+"/"+UtilsManager.getApiKey(SavedRoutes.this)+"", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                // showDialog("Getting Fav Location");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {
                    //  hideDialog();
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    predictionArrayList.clear();
                    if (jsonArray.length() > 0){


                        Prediction prediction;


                        for (int i=0;i<jsonArray.length();i++){

                            prediction = new Prediction();
                            String location_name = jsonArray.getJSONObject(i).getString("address");
                            String location_title = jsonArray.getJSONObject(i).getString("location_name");
                            String location_id = jsonArray.getJSONObject(i).getString("location_id");
                            String id_ = jsonArray.getJSONObject(i).getString("id");

                            prediction.setId(id_);
                            prediction.setLocation_id(location_id);
                            prediction.setLocation_name(location_name);
                            prediction.setLocation_title(location_title);
                            prediction.setHeader(false);
                            prediction.setIsfav(true);
                            //  sQliteHelper.insertPrediction(prediction);

                            if (!location_id.equalsIgnoreCase(""))
                                predictionArrayList.add(prediction);

                        }



                        UtilsManager.setListViewHeightBasedOnItems(savedList);

                        locationAdapter.notifyDataSetChanged();
                    }

                    Log.e(TAG, "onSuccess: "+response);

                }
                catch (Exception e){
                    e.printStackTrace();
                    predictionArrayList.clear();
                    locationAdapter.notifyDataSetChanged();
                    findViewById(R.id.empty_lay).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try
                {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onFailure: "+response);
                    Toast.makeText(SavedRoutes.this,"Internet/Server Error",Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

}
