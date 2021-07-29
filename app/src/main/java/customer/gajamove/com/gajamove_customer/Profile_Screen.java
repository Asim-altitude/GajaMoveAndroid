package customer.gajamove.com.gajamove_customer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import customer.gajamove.com.gajamove_customer.auth.CaptureFaceScreen;
import customer.gajamove.com.gajamove_customer.auth.ChooseIndustryScreen;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.auth.SignUp_Screen;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.ImageCompressClass;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Screen extends BaseActivity {
    private static final String TAG = "Profile_Screen";

    ImageView back_btn,logout_icon;
    CircleImageView user_pic;
    EditText user_name_txt,display_name_txt,phone_number_txt,email_txt;
    SharedPreferences sharedPreferences;
    TextView txtName,industry_name;

    Button update_profile;

    String name,display_name,image,phone_number,email,mem_id;

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.profile));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    boolean enabled = true;
    boolean back_ = false;

    String selected_industry_id = "",selected_industry_name="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__screen);

        includeActionBar();
        Constants.enableSSL(asyncHttpClient);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);

        selected_industry_id = sharedPreferences.getString(Constants.INDUSTRY_ID,"");
        selected_industry_name = sharedPreferences.getString(Constants.INDUSTRY_NAME,"Choose Industry");

        enabled = sharedPreferences.getBoolean(Constants.PROFILE_EDITABLE,true);
        user_name_txt = findViewById(R.id.user_name);
        phone_number_txt = findViewById(R.id.phone_number_txt);
        email_txt = findViewById(R.id.email_txt);
        display_name_txt = findViewById(R.id.user_display_name);
        industry_name = findViewById(R.id.industry_name_txt);
        update_profile = findViewById(R.id.update_btn);

        industry_name.setText(selected_industry_name);

        industry_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile_Screen.this, ChooseIndustryScreen.class);
                startActivityForResult(intent,0001);

            }
        });

        logout_icon = findViewById(R.id.logout_icon);
        logout_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmLog_out();
            }
        });

        back_ = getIntent().getBooleanExtra("back",false);


        if (!enabled){
            ((ViewGroup)user_name_txt.getParent()).getChildAt(1).setVisibility(View.VISIBLE);
            findViewById(R.id.number_overlay).setVisibility(View.VISIBLE);
            findViewById(R.id.email_overlay).setVisibility(View.VISIBLE);
            update_profile.setVisibility(View.GONE);
            findViewById(R.id.message).setVisibility(View.VISIBLE);
        }else {

            ((ViewGroup)user_name_txt.getParent()).getChildAt(1).setVisibility(View.GONE);
            findViewById(R.id.number_overlay).setVisibility(View.GONE);
            findViewById(R.id.message).setVisibility(View.GONE);
            update_profile.setVisibility(View.VISIBLE);
        }


        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (!user_name_txt.getText().toString().equalsIgnoreCase("")
                && !phone_number_txt.getText().toString().equalsIgnoreCase("") &&
                        !email_txt.getText().toString().equalsIgnoreCase(""))
                {


                    if (findViewById(R.id.prefix_number).getVisibility()==View.VISIBLE){
                        if (phone_number_txt.getText().toString().trim().startsWith("0")){
                            phone_number_txt.setError("Number cannot start from 0");
                            return;
                        }

                        if (phone_number_txt.getText().toString().trim().length()<9){
                            phone_number_txt.setError("Incomplete Number (min 9 digits)");
                            return;
                        }

                    }else {
                        if (!phone_number_txt.getText().toString().trim().startsWith("0060")){
                            phone_number_txt.setError("Number should start from 0060");
                            return;
                        }else if (phone_number_txt.getText().toString().trim().length()<13){
                            phone_number_txt.setError("Incomplete number (min 13 digits)");
                            return;
                        }
                    }

                    if (selected_industry_id.equalsIgnoreCase("")){
                        Toast.makeText(Profile_Screen.this,"Choose industry name",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    saveProfileInfo();
                }else {
                    Toast.makeText(Profile_Screen.this,"All Fields are required",Toast.LENGTH_SHORT).show();
                }
            }
        });

        back_btn = findViewById(R.id.backBtn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        user_pic = findViewById(R.id.userProfilePic);
        txtName = findViewById(R.id.txtName);

        user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enabled)
                    startCamera(CHOOSE);
            }
        });

        mem_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");
        name = sharedPreferences.getString(Constants.PREFS_USER_NAME,"");
        display_name = sharedPreferences.getString(Constants.PREFS_USER_FULL_NAME,"");
        image = sharedPreferences.getString(Constants.PREFS_USER_IMAGE,"");
        email = sharedPreferences.getString(Constants.PREFS_USER_EMAIL,"");
        phone_number = sharedPreferences.getString(Constants.PREFS_USER_MOBILE,"");

        Log.e(TAG, "onCreateView: "+image);

        if (!image.equalsIgnoreCase(""))
           Picasso.with(Profile_Screen.this).load(image).into(user_pic);


        if (!phone_number.equalsIgnoreCase("")){
            if (phone_number.startsWith("00"))
                phone_number.replace("00","+");
        }

        txtName.setText(name);

        user_name_txt.setText(name);
        email_txt.setText(email);
        phone_number_txt.setText(phone_number);
        display_name_txt.setText(display_name);

        if (phone_number.trim().equalsIgnoreCase(""))
            findViewById(R.id.prefix_number).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.prefix_number).setVisibility(View.GONE);

    }


    public static void setReadOnly(final EditText view, final boolean readOnly) {
        view.setFocusable(!readOnly);
        view.setFocusableInTouchMode(!readOnly);
        view.setClickable(!readOnly);
        view.setLongClickable(!readOnly);
        view.setCursorVisible(!readOnly);
    }

    ProgressDialog progressDialog;

    private void showDialog() {
        progressDialog = new ProgressDialog(Profile_Screen.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }


    private void hideDialog(){
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

    boolean isImageChosen = false;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public void saveProfileInfo(){



        RequestParams params = new RequestParams();
        try {

            String phonenumber = phone_number_txt.getText().toString().trim();

            if (!phonenumber.startsWith("0060"))
                 phonenumber = "0060"+phonenumber;

            if (!phonenumber.startsWith("+60"))
                phonenumber.replace("+","00");

            if (isImageChosen){
                params.put("id",mem_id);
                params.put("customer_name", user_name_txt.getText().toString());
                params.put("customer_email", email_txt.getText().toString());
                params.put("customer_mobile", phonenumber);
                params.put("industry_id", selected_industry_id);
                params.put("profile_img", new FileInputStream(profileImageString));
                params.put("key", UtilsManager.getApiKey(Profile_Screen.this));
            } else {
                params.put("id",mem_id);
                params.put("industry_id", selected_industry_id);
                params.put("customer_name", user_name_txt.getText().toString());
                params.put("customer_email", email_txt.getText().toString());
                params.put("customer_mobile", phonenumber);
                params.put("key", UtilsManager.getApiKey(Profile_Screen.this));
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }

        asyncHttpClient.setTimeout(30*1000);
        String url_val = Constants.Host_Address + "customers/update_profile";
        asyncHttpClient.post(url_val, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    hideDialog();
                    String res = new String(responseBody);
                    JSONObject responseObject = new JSONObject(res);
                    String status = responseObject.getString("status");
                    String errorCode = responseObject.getString("errorCode");
                    String message = responseObject.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        JSONArray data = responseObject.getJSONArray("data");
                        JSONObject json = data.getJSONObject(0);


                        if (json!=null) {

                            try {

                                String customer_name_value = json.getString("customer_name");
                                String customer_mobile_value = json.getString("customer_mobile");
                                String customer_email_value = json.getString("customer_email");
                                String customer_full_img_value = json.getString("customer_full_img");

                                txtName.setText(customer_name_value);
                                phone_number_txt.setText(customer_mobile_value);
                                email_txt.setText(customer_email_value);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.PREFS_USER_MOBILE, customer_mobile_value);
                                editor.putString(Constants.PREFS_USER_IMAGE, customer_full_img_value);
                                editor.putString(Constants.PREFS_USER_NAME, customer_name_value);
                                editor.putString(Constants.PREFS_USER_EMAIL, customer_email_value);
                                editor.putString(Constants.INDUSTRY_NAME, selected_industry_name);
                                editor.putString(Constants.INDUSTRY_ID,selected_industry_id);

                                editor.apply();


                                if (back_) {
                                    Toast.makeText(Profile_Screen.this,"Profile Updated",Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else {
                                    UtilsManager.showAlertMessage(Profile_Screen.this, "", "Profile Updated Successfully");

                                }
                            }
                            catch (Exception e) {
                                Log.e("error occured",e.getMessage());
                                //  UtilsManager.showAlertMessage(getContext(), "", "Profile Updated Successfully");
                            }
                        }
                        else {
                            UtilsManager.showAlertMessage(Profile_Screen.this, "", "Profile could not be updated");
                        }



                        //   UtilsManager.showAlertMessage(getContext(),"","Data Updated");

                    }
                    else{
                        progressDialog.dismiss();
                        UtilsManager.showAlertMessage(Profile_Screen.this, "", message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onFailure: "+response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private static final int REQ_CODE = 0011;
    public void galleryIntent() {

        if (ContextCompat.checkSelfPermission(Profile_Screen.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_CODE);
            }
        }
        else
        {
            // Create intent to Open Image applications like Gallery, Google Photos
            startCamera(CHOOSE);
        }

    }

    Uri imageUri = null;
    private static final int CHOOSE = 1011;
    private void captureImage() {

      /*Intent intent = new Intent(SignUp_Step_One.this,CaptureFaceScreen.class);
      startActivityForResult(intent,PICK_CODE);*/

        Constants.createImageDir();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "pick_image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Picked from member app");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(intent, CHOOSE);


    }

    private void startCamera(int PICK_CODE) {

      /*Intent intent = new Intent(SignUp_Step_One.this,CaptureFaceScreen.class);
      startActivityForResult(intent,PICK_CODE);*/

      try {
          if (ContextCompat.checkSelfPermission(Profile_Screen.this, Manifest.permission.CAMERA)
                  != PackageManager.PERMISSION_GRANTED
                  ||
                  ContextCompat.checkSelfPermission(Profile_Screen.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                          !=PackageManager.PERMISSION_GRANTED)
          {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                  requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQ_CODE);
              }
          }
          else
          {
              try
              {
                 /* Intent intent = new Intent(Profile_Screen.this, CaptureFaceScreen.class);
                  startActivityForResult(intent, CHOOSE);*/
                 captureImage();

              }
              catch (Exception e){
                  e.printStackTrace();
              }
          }


      }
      catch (Exception e){
          e.printStackTrace();
          Toast.makeText(Profile_Screen.this,e.getMessage(),Toast.LENGTH_LONG).show();
      }


       /* Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_CODE);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try
        {
            if (requestCode == REQ_CODE)
            {
                if (grantResults[0]>0)
                {
                    startCamera(CHOOSE);
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(Profile_Screen.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    String profileImageString = "";
    private static int RESULT_LOAD_IMG = 1;
    private static String OLD_IMAGE="old_image";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode==0001 && resultCode == RESULT_OK)
            {
                industry_name.setText(data.getData().toString().split(Pattern.quote("#"))[1]);
                selected_industry_name = data.getData().toString().split(Pattern.quote("#"))[1];
                selected_industry_id = data.getData().toString().split(Pattern.quote("#"))[0];

            }else {

                String file = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                // String file = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                profileImageString = file;

                Log.e(TAG, "onActivityResult: " + file);
                Picasso.with(Profile_Screen.this).load(new File(profileImageString)).into(user_pic);
                isImageChosen = true;
                sharedPreferences.edit().putString(OLD_IMAGE, profileImageString).apply();
            }


        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public String  getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    GoogleSignInClient googleSignInClient;
    private void ConfirmLog_out() {
        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_Screen.this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.logout_prompt))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean social = Profile_Screen.this.getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getBoolean(Constants.SOCIAL_LOGIN,false);


                                if (social) {
                                    LoginManager.getInstance().logOut();

                                    GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(Profile_Screen.this);
                                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestEmail()
                                            .requestId()
                                            .build();


                                    if (googleSignInAccount!=null) {
                                        googleSignInClient = GoogleSignIn.getClient(Profile_Screen.this, gso);
                                        googleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
                                                startActivity(new Intent(Profile_Screen.this, LoginScreen.class));
                                                finish();


                                            }
                                        });
                                    }else {

                                        getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
                                        startActivity(new Intent(Profile_Screen.this, LoginScreen.class));
                                        finish();
                                    }

                                }else {
                                    getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
                                    startActivity(new Intent(Profile_Screen.this, LoginScreen.class));
                                    finish();
                                }
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

    AlertDialog alert = null;

    @Override
    protected void onResume() {
        super.onResume();
    }
}
