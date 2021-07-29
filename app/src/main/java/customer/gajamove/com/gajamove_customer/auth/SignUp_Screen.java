package customer.gajamove.com.gajamove_customer.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import customer.gajamove.com.gajamove_customer.BaseActivity;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Industry;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.ImageCompressClass;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jkb.vcedittext.VerificationCodeEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SignUp_Screen extends BaseActivity {
    private static final String TAG = "SignUp_Screen";

    LinearLayout redirect_login;
    RelativeLayout profile_lay;
    CheckBox btnAgreedterms;

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect_login.performClick();
            }
        });
    }

    EditText mEmailView,mPhoneView,mNameView,mPasswordView;
    Button sign_up_btn;
    ImageView imageView,camera_icon;
    TextView terms_text,industry_name;
    RelativeLayout industry_lay;

    String selected_industry_name = "",selected_industry_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__screen);

        includeActionBar();
        Constants.enableSSL(asyncHttpClient);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);

        mNameView = findViewById(R.id.name);
        mEmailView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        mPhoneView = findViewById(R.id.phone);
        profile_lay = findViewById(R.id.profile_lay);
        imageView = findViewById(R.id.imageView);
        camera_icon = findViewById(R.id.camera_icon);
        terms_text = findViewById(R.id.terms_text);
        industry_lay = findViewById(R.id.industry_lay);
        industry_name = findViewById(R.id.industry_name);


        industry_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignUp_Screen.this,ChooseIndustryScreen.class);
                startActivityForResult(intent,0011);

            }
        });


        industry_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                industry_lay.performClick();
            }
        });

        profile_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    galleryIntent();

                }
                catch (Exception e){
                    Toast.makeText(SignUp_Screen.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        sign_up_btn = findViewById(R.id.email_sign_in_button);

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean valid = true;
                if (mEmailView.getText().toString().equalsIgnoreCase("")){
                    mEmailView.setError("Enter Email");
                    valid = false;
                }

                if (mPhoneView.getText().toString().equalsIgnoreCase("")){
                    mPhoneView.setError("Enter Phone Number");
                    valid = false;
                }else if (mPhoneView.getText().toString().trim().length()<9){
                    mPhoneView.setError("Incomplete Number(min 9 digits)");
                    valid = false;
                }else if (mPhoneView.getText().toString().trim().startsWith("0")){
                    mPhoneView.setError("Number cannot start with 0");
                    valid = false;
                }

                if (mNameView.getText().toString().equalsIgnoreCase("")){
                    mNameView.setError("Enter Name");
                    valid = false;
                }

                if (mPasswordView.getText().toString().equalsIgnoreCase("")){
                    mPasswordView.setError("Enter Password");
                    valid = false;
                }

                if (mPasswordView.getText().toString().trim().length() < 6){
                    mPasswordView.setError("Min length 6 digits");
                    valid = false;
                }

                if (selected_industry_name.equalsIgnoreCase("")){
                    if (valid) {
                        Toast.makeText(SignUp_Screen.this,"Choose Industry",Toast.LENGTH_SHORT).show();
                        valid = false;
                    }
                }

                if (valid) {
                    if (((CheckBox)findViewById(R.id.btnAgreedterms)).isChecked())
                        saveBasicInfo();
                    else
                        Toast.makeText(SignUp_Screen.this,"Please Confirm Terms and Conditions.",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SignUp_Screen.this,"Enter required information",Toast.LENGTH_SHORT).show();
                }
            }
        });

        redirect_login = findViewById(R.id.redirect_sign_in);
        btnAgreedterms = findViewById(R.id.btnAgreedterms);

        String text = "<font color=#000000>I have read, understood and accept the</font> <a href=\"https://gajamove.com/terms-conditions-user/\" > <font color=#F35049> Terms and Conditions </font></a><font color=#000000>and</font> <a href=\"http://gajamove.com/privacy-policy/\" ><font color=#F35049>Privacy Policy</font></a>";

        setTextViewHTML(terms_text,text);
        terms_text.setClickable(false);
        /*terms_text.setText(Html.fromHtml(text));
        terms_text.setClickable(true);
        terms_text.setMovementMethod(new LinkMovementMethod());*/

        redirect_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp_Screen.this,LoginScreen.class));
            }
        });

        mPasswordView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    sign_up_btn.performClick();
                    // Toast.makeText(HelloFormStuff.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
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

       /* Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_CODE);*/
    }


    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
    {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                // Do something with span.getURL() to handle the link click..

                String url = span.getURL();
                if (url.contains("terms")){
                    openDialoge("Terms and Conditions",url);
                }else {
                    openDialoge("Privacy Policy",url);
                }
                Log.e(TAG, "onClick: "+span.getURL());
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }


    private void openDialoge(String title,String path){

        Dialog dialog = new Dialog(SignUp_Screen.this,R.style.full_screen_dialog);
        dialog.setContentView(R.layout.dialoge_webview);

        TextView titleView = dialog.findViewById(R.id.title);
        Button done = dialog.findViewById(R.id.done_btn);
        WebView webView = dialog.findViewById(R.id.webview);
        CheckBox checkBox = dialog.findViewById(R.id.checkbox);

        checkBox.setText(getResources().getString(R.string.agree)+" "+getResources().getString(R.string.terms_conds));
        done.setBackgroundResource(R.drawable.back_light_btn);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    done.setBackgroundResource(R.drawable.next_btn_drawable);
                }else {
                    done.setBackgroundResource(R.drawable.back_light_btn);
                }
            }
        });

        titleView.setText(title);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(path);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked() || checkBox.getVisibility()==View.GONE)
                    dialog.dismiss();
            }
        });

        dialog.show();


    }

    protected void setTextViewHTML(TextView text, String html)
    {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public static final int PICK_CODE = 0011;
    public static final int REQ_CODE = 0022;
    public void galleryIntent() {

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE);
                } else {
                    // Create intent to Open Image applications like Gallery, Google Photos
              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    dispatchTakePictureIntent();
                }
                else {
                    startCamera(PICK_CODE);
                }*/
                    startCamera(PICK_CODE);
                }
            } else {
                // Create intent to Open Image applications like Gallery, Google Photos
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                dispatchTakePictureIntent();
            }
            else {
                startCamera(PICK_CODE);
            }*/
                startCamera(PICK_CODE);
            }

        }catch (Exception e){
            Toast.makeText(SignUp_Screen.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    private void startCamera(int PICK_CODE) {
       /* Intent intent = new Intent(SignUp_Two.this,FaceDetectRGBActivity.class);
        startActivityForResult(intent,PICK_CODE);*/
       /* Intent intent = new Intent(SignUp_Screen.this, CaptureFaceScreen.class);
        startActivityForResult(intent, PICK_CODE);*/

       captureImage();

    }

    public String  getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private String proile_path;
    boolean isSelected = false;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == CHOOSE && resultCode == RESULT_OK && (imageUri!=null || data!=null)) {
                // Get the Image from data

                // String file = data.getData().toString();//ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                String file = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                // String file = ImageCompressClass.compressImage(getRealPathFromURI(imageUri));
                proile_path = file;

                Log.e(TAG, "onActivityResult: "+proile_path );
                Picasso.with(SignUp_Screen.this).load(new File(proile_path)).into(imageView);
                isSelected = true;
                camera_icon.setVisibility(View.GONE);


            }else if (requestCode==0011 && resultCode == RESULT_OK)
            {
                industry_name.setText(data.getData().toString().split(Pattern.quote("#"))[1]);
                selected_industry_name = data.getData().toString().split(Pattern.quote("#"))[1];
                selected_industry_id = data.getData().toString().split(Pattern.quote("#"))[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    SharedPreferences sharedPreferences;
    private void saveBasicInfo() {
        asyncHttpClient.setConnectTimeout(20000);

        RequestParams params = new RequestParams();
        try
        {

            params.put("email", mEmailView.getText().toString());
            params.put("name", mNameView.getText().toString());
            params.put("industry_id", selected_industry_id);
            params.put("preffered_name", mNameView.getText().toString());
            params.put("phone", "0060"+mPhoneView.getText().toString());
            params.put("profile_pic", new File(proile_path));

        }
        catch (Exception e){
            e.printStackTrace();
        }

        asyncHttpClient.post(SignUp_Screen.this, Constants.Host_Address + "register_new", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    hideDialoge();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    JSONObject jsonObject = new JSONObject(response);

                    int errorcode = jsonObject.getInt("errorCode");
                    if (errorcode==00){

                        String verification_code = jsonObject.getString("otp_code");
                        String mem_id = jsonObject.getString("customer_id");
                        String email = jsonObject.getString("email");

                        sharedPreferences.edit().putString(Constants.PREFS_CUSTOMER_ID,mem_id).apply();
                        sharedPreferences.edit().putString(Constants.PREFS_USER_EMAIL,email).apply();
                        sharedPreferences.edit().putString(Constants.INDUSTRY_ID,selected_industry_id).apply();
                        sharedPreferences.edit().putString(Constants.INDUSTRY_NAME,selected_industry_name).apply();
                        sharedPreferences.edit().putString(Constants.PREFS_USER_MOBILE,"0060"+mPhoneView.getText().toString() ).apply();

                        Intent intent = new Intent(SignUp_Screen.this,VerificationScreen.class);
                        intent.putExtra("code",verification_code);
                        intent.putExtra("pass",mPasswordView.getText().toString());
                        startActivity(intent);
                       // finish();

                    }else {
                        String message = jsonObject.getString("message");
                        Toast.makeText(SignUp_Screen.this, UtilsManager.makeInitialCapital(message),Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    hideDialoge();
                    String response = new String(responseBody);
                     Toast.makeText(SignUp_Screen.this,"Internet/Server Error",Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();

                    Toast.makeText(SignUp_Screen.this,"Server Error",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private ProgressDialog progress;
    private void showProgressDialog()
    {

        progress = new ProgressDialog(SignUp_Screen.this);
        progress.setMessage("Saving info");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void hideDialoge()
    {
        if (progress!=null)
            progress.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
