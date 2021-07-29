package customer.gajamove.com.gajamove_customer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.ContentCaptureCondition;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import customer.gajamove.com.gajamove_customer.HomeScreen;
import customer.gajamove.com.gajamove_customer.MyApp;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.models.Member;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

public class FavDriverAdapter extends BaseAdapter {
    private static final String TAG = "FavDriverAdapter";

    ArrayList<Member> members;
    Context context;

    public FavDriverAdapter(ArrayList<Member> members, Context context) {
        this.members = members;
        this.context = context;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.fav_driver_item,null);

        ImageView user_image = convertView.findViewById(R.id.user_image);
        TextView user_name = convertView.findViewById(R.id.user_name);
        TextView user_email = convertView.findViewById(R.id.user_email);

        ImageView cross = convertView.findViewById(R.id.remove_icon);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmCancel(position);
            }
        });


        Picasso.with(context).load(Constants.SERVICE_IMAGE_BASE_PATH+members.get(position).getMem_image())
                .placeholder(R.drawable.profile_icon).into(user_image);
        user_name.setText(members.get(position).getMem_name());
        user_email.setText(members.get(position).getMem_email());


        return convertView;

    }


    AlertDialog alert;
    private void ConfirmCancel(final int pos) {
        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure to remove this driver?")
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               removeDriver(pos);
                            }
                        });
        alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
    }

    
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void removeDriver(int pos){

        String customer_id = context.getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getString(Constants.PREFS_CUSTOMER_ID,"");
        Log.e(TAG, "getBankInfo: "+ Constants.Host_Address + "customers/delete_favorite_drivers/"+members.get(pos).getItem_id()+"/tgs_appkey_amin");
        asyncHttpClient.get(Constants.Host_Address + "customers/delete_favorite_drivers/"+members.get(pos).getItem_id()+"/"+ UtilsManager.getApiKey(context)+"", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equalsIgnoreCase("success")){
                        members.remove(pos);
                        notifyDataSetChanged();
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String response = new String(responseBody);
                    Log.e(TAG, "onFailure: "+response);

                    Toast.makeText(context,"Internet/Server Erro",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
