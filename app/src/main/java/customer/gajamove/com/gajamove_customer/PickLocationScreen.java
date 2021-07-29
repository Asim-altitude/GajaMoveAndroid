package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.adapter.LocationAdapter;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.FavouriteSaver;
import customer.gajamove.com.gajamove_customer.utils.SaveLocationCallBack;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PickLocationScreen extends BaseActivity implements SaveLocationCallBack {
    private static final String TAG = "PickLocationScreen";

    EditText search_field;
    ListView location_list,saved_location_list;
    LocationAdapter locationAdapter,savedLocationAdapter;

    FavouriteSaver favouriteSaver;

    ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location_screen);

        Constants.enableSSL(asyncHttpClient);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        location_list = findViewById(R.id.location_list);
        saved_location_list = findViewById(R.id.saved_location_list);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        predictionList = new ArrayList<>();
        savedPredictionList = new ArrayList<>();
       /* favouriteSaver = new FavouriteSaver(this);
        addFavLocations();
*/

       savedLocationAdapter = new LocationAdapter(this,savedPredictionList);
       savedLocationAdapter.setSaveLocationCallBack(this);
       saved_location_list.setAdapter(savedLocationAdapter);
       saved_location_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               try {
                   if (!savedPredictionList.get(i).isHeader()) {
                       // Toast.makeText(PickLocationScreen.this,predictionList.get(position).getLocation_name(),Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent();
                       intent.setData(Uri.parse(savedPredictionList.get(i).getLocation_title() + "#" + savedPredictionList.get(i).getLocation_name() + "#" + savedPredictionList.get(i).getLocation_id()));
                       setResult(RESULT_OK, intent);
                       finish();
                   }
               }
               catch (Exception e){
                   e.printStackTrace();
               }
           }
       });

        locationAdapter = new LocationAdapter(this,predictionList);
        locationAdapter.setSaveLocationCallBack(this);
        location_list.setAdapter(locationAdapter);
        location_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!predictionList.get(position).isHeader()){
                   // Toast.makeText(PickLocationScreen.this,predictionList.get(position).getLocation_name(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(predictionList.get(position).getLocation_title()+"#"+predictionList.get(position).getLocation_name()+"#"+predictionList.get(position).getLocation_id()));
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });



      //  index = predictionList.size()-1;

        search_field = findViewById(R.id.search_text_field);
        search_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "onTextChanged: "+s.toString());
                if (s.length() > 3)
                  getPredictionsAPI(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void addFavLocations(){
        ArrayList<Prediction> fav_list = favouriteSaver.getFavList();
        predictionList.clear();
        if (fav_list.size() > 0){
            Prediction prediction = new Prediction();
            prediction.setHeader(true);
            prediction.setHeading("Favourite Locations");
            predictionList.add(prediction);
        }

        for (int i=0;i<fav_list.size();i++){
            predictionList.add(fav_list.get(i));
        }
    }

    ArrayList<Prediction> predictionList = null,savedPredictionList,search_list;

    boolean added = false;
    int index = 0;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getPredictionsAPI(String query){
        Log.e(TAG, "getPredictionsAPI: ");


        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+query+"&components=country:mys&key="+Constants.API_KEY+"";

        asyncHttpClient.get(PickLocationScreen.this, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.e(TAG, "onSuccess: "+new String(responseBody));
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String status = jsonObject.getString("status");

                    if (search_list==null)
                        search_list = new ArrayList<>();

                    search_list.clear();
                    predictionList.clear();

                   // addFavLocations();

                    Prediction prediction = new Prediction();
                    prediction.setHeader(true);
                    prediction.setHeading("Nearby Locations");
                    predictionList.add(prediction);

                    if (status.equalsIgnoreCase("OK")){
                        JSONArray array = jsonObject.getJSONArray("predictions");


                        for (int i=0;i<array.length();i++){
                            prediction = new Prediction();
                            String location_name = array.getJSONObject(i).getString("description");
                            String location_title = array.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text");
                            String id = array.getJSONObject(i).getString("place_id");
                            prediction.setLocation_id(id);
                            prediction.setLocation_name(location_name);
                            prediction.setLocation_title(location_title);
                            prediction.setHeader(false);
                            //  sQliteHelper.insertPrediction(prediction);

                            search_list.add(prediction);

                        }

                        for (int i=0;i<search_list.size();i++){
                            predictionList.add(search_list.get(i));
                        }


                        locationAdapter = new LocationAdapter(PickLocationScreen.this,predictionList);
                        locationAdapter.setSaveLocationCallBack(PickLocationScreen.this);
                        location_list.setAdapter(locationAdapter);

                        UtilsManager.setListViewHeightBasedOnItems(location_list);

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
    protected void onResume() {
        super.onResume();
        getFavouriteLocations();
    }


    @Override
    public void onLocationSaved(int pos) {
        try
        {
            showChooseTitleDialoge(pos);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    Dialog dialog;
    LatLng latLng;
    private void showChooseTitleDialoge(int pos) {

        dialog = new Dialog(PickLocationScreen.this);
        dialog.setContentView(R.layout.save_location_layout);

        EditText location_title = dialog.findViewById(R.id.location_name);
        TextView location_address = dialog.findViewById(R.id.full_location_name);

        location_address.setText(predictionList.get(pos).getLocation_name());

        Button save_location = dialog.findViewById(R.id.save_location_btn);

        save_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!location_title.getText().toString().isEmpty()){
                    saveLocationAPI(predictionList.get(pos).getLocation_id(),location_title.getText().toString(),location_address.getText().toString(),latLng);
                    dialog.dismiss();
                }
            }
        });



        latLng = Constants.getLocationFromAddress(PickLocationScreen.this,location_address.getText().toString());


        dialog.show();



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

    SharedPreferences sharedPreferences;
    private void saveLocationAPI(String location_id,String toString, String toString1, LatLng latLng) {

        String customer_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");

        RequestParams params = new RequestParams();
        params.put("customer_id",customer_id);
        params.put("location_name",toString);
        params.put("location_lat",latLng.latitude);
        params.put("location_long",latLng.longitude);
        params.put("address",toString1);
        params.put("location_id",location_id);
        params.put("key",UtilsManager.getApiKey(PickLocationScreen.this));


        asyncHttpClient.post(Constants.Host_Address + "customers/my_favorite_locations", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Saving Location");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
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
                    Toast.makeText(PickLocationScreen.this,"Internet/Server Error",Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    private void getFavouriteLocations() {

        String customer_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");


        Log.e(TAG, "getFavouriteLocations: "+Constants.Host_Address + "customers/get_favorite_locations/"+customer_id+"/tgs_appkey_amin" );
        asyncHttpClient.get(Constants.Host_Address + "customers/get_favorite_locations/"+customer_id+"/"+UtilsManager.getApiKey(PickLocationScreen.this)+"", new AsyncHttpResponseHandler() {

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

                    if (jsonArray.length() > 0){

                        savedPredictionList.clear();
                        Prediction prediction = new Prediction();
                        prediction.setHeader(true);
                        prediction.setHeading("Saved Locations");
                        savedPredictionList.add(prediction);

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
                                savedPredictionList.add(prediction);

                        }


                        UtilsManager.setListViewHeightBasedOnItems(saved_location_list);

                        savedLocationAdapter.notifyDataSetChanged();
                    }

                    Log.e(TAG, "onSuccess: "+response);

                }
                catch (Exception e){
                    e.printStackTrace();
                    savedPredictionList.clear();
                    savedLocationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try
                {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onFailure: "+response);
                    Toast.makeText(PickLocationScreen.this,"Internet/Server Error",Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


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


    AlertDialog alert;
    private void ConfirmCancel(final int pos) {
        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PickLocationScreen.this);
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

    private void removeLocationAPI(int pos) {

        String customer_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");

        RequestParams params = new RequestParams();
        params.put("id",savedPredictionList.get(pos).getId());
        params.put("key","tgs_appkey_amin");

        asyncHttpClient.get(Constants.Host_Address + "customers/delete_favorite_locations/"+savedPredictionList.get(pos).getId()+"/"+UtilsManager.getApiKey(PickLocationScreen.this)+"", new AsyncHttpResponseHandler() {

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
                    Toast.makeText(PickLocationScreen.this,"Internet/Server Error",Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
}
