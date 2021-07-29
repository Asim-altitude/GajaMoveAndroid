package customer.gajamove.com.gajamove_customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.fragment.HomeFragment;
import customer.gajamove.com.gajamove_customer.models.Advertisement;
import customer.gajamove.com.gajamove_customer.models.PhoneContact;
import customer.gajamove.com.gajamove_customer.sos.SOS_Setting_Activity;
import customer.gajamove.com.gajamove_customer.sos.SOS_Settings_PREFS;
import customer.gajamove.com.gajamove_customer.sos.SoS_Call_Screen;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.ShowAdvertisement;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends BaseActivity implements ShowAdvertisement {
    private static final String TAG = "HomeScreen";

    LinearLayout home_tab,history_tab,help_tab,profile_tab;
    FragmentManager fragmentManager;

    static boolean stay = false;

    @Override
    public void onBackPressed() {
       try {
           finishAffinity();
       }
       catch (Exception e){
           e.printStackTrace();
       }
    }

    void checkSOS() {

        SOS_Settings_PREFS settings_prefs = new SOS_Settings_PREFS(HomeScreen.this);
        ArrayList<PhoneContact> contacts = settings_prefs.getSOSPhone(Constants.USER_SOS_PHONE);

        if (contacts == null)
            contacts = new ArrayList<>();


        if (contacts.size() > 0)
            startActivity(new Intent(HomeScreen.this, SoS_Call_Screen.class));
        else
            startActivity(new Intent(HomeScreen.this, SOS_Setting_Activity.class));

    }

    ImageView sos_btn;
    SharedPreferences sharedPreferences;
    boolean show_ad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);

        Constants.enableSSL(asyncHttpClient);
        stay = getIntent().getBooleanExtra("stay",false);

        sos_btn = findViewById(R.id.sos_btn);
        sos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // checkSOS();
                startActivity(new Intent(HomeScreen.this,NotificationsScreen.class));
            }
        });

        fragmentManager = getSupportFragmentManager();

        home_tab = findViewById(R.id.home_tab);
        history_tab = findViewById(R.id.history_tab);
        help_tab = findViewById(R.id.logout_tab);
        profile_tab = findViewById(R.id.account_tab);



        home_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    fragmentManager.beginTransaction().replace(R.id.content_lay, new HomeFragment()).commit();
                    enableTab(home_tab);
                    disableTab(history_tab);
                    disableTab(help_tab);
                    disableTab(profile_tab);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        history_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    enableTab(history_tab);
                    disableTab(home_tab);
                    disableTab(help_tab);
                    disableTab(profile_tab);

                    startActivity(new Intent(HomeScreen.this,OrderHistoryScreen.class));
                }
                catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        help_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    enableTab(help_tab);
                    disableTab(history_tab);
                    disableTab(home_tab);
                    disableTab(profile_tab);

                    startActivity(new Intent(HomeScreen.this,FinanceScreen.class));     }
                catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        profile_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    enableTab(profile_tab);
                    disableTab(history_tab);
                    disableTab(home_tab);
                    disableTab(help_tab);

                    startActivity(new Intent(HomeScreen.this, SettingsScreen.class));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        home_tab.performClick();

    }

    android.app.AlertDialog alert = null;
    GoogleSignInClient googleSignInClient;
    private void ConfirmLogout() {
        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeScreen.this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.logout_prompt))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean social = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getBoolean(Constants.SOCIAL_LOGIN,false);


                                if (social) {
                                    LoginManager.getInstance().logOut();

                                    GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(HomeScreen.this);
                                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestEmail()
                                            .requestId()
                                            .build();


                                    if (googleSignInAccount!=null) {
                                        googleSignInClient = GoogleSignIn.getClient(HomeScreen.this, gso);
                                        googleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
                                                startActivity(new Intent(HomeScreen.this, LoginScreen.class));
                                                finish();


                                            }
                                        });
                                    }else {

                                        getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
                                        startActivity(new Intent(HomeScreen.this, LoginScreen.class));
                                        finish();
                                    }

                                }else {
                                    getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
                                    startActivity(new Intent(HomeScreen.this, LoginScreen.class));
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

    private void enableTab(LinearLayout home_tab) {
        ((ImageView)home_tab.getChildAt(0)).setColorFilter(ContextCompat.getColor(HomeScreen.this,R.color.theme_primary));
        ((TextView)home_tab.getChildAt(1)).setTextColor(ContextCompat.getColor(HomeScreen.this,R.color.theme_primary));

    }

    private void disableTab(LinearLayout home_tab) {
        ((ImageView)home_tab.getChildAt(0)).setColorFilter(ContextCompat.getColor(HomeScreen.this,R.color.dark_gray_color));
        ((TextView)home_tab.getChildAt(1)).setTextColor(ContextCompat.getColor(HomeScreen.this,R.color.dark_gray_color));

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void showAds(boolean show) {
        try {
            show_ad = sharedPreferences.getBoolean(Constants.SHOW_AD,false);
            if (show_ad && show)
                getAdvertisment();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    List<Advertisement> advertisementList;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void getAdvertisment() {

        Log.e(TAG, "getAdvertisment: "+Constants.Host_Address + "members/get_advertisements/tgs_appkey_amin");
        asyncHttpClient.get(Constants.Host_Address + "members/get_advertisements/"+UtilsManager.getApiKey(HomeScreen.this)+"", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String res = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(res);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    advertisementList = new ArrayList<>();
                    for (int i=0;i<jsonArray.length();i++){
                         String url = jsonArray.getJSONObject(i).getString("url");
                         String image = jsonArray.getJSONObject(i).getString("image");

                         Advertisement advertisement = new Advertisement();
                         advertisement.setAd_url(url);
                         advertisement.setAd_image(Constants.SERVICE_IMAGE_BASE_PATH+"uploads/advertisements/"+image);

                         advertisementList.add(advertisement);
                    }


                    if (advertisementList.size() > 0) {
                        showAdvertismentDialog();
                        sharedPreferences.edit().putBoolean(Constants.SHOW_AD,false).apply();
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

    Dialog dialog = null;
    int index = 0;
    CountDownTimer countDownTimer;
    private void showAdvertismentDialog() {

        if (dialog!=null)
            dialog.dismiss();

        dialog = new Dialog(HomeScreen.this,R.style.DialogTheme);
        dialog.setContentView(R.layout.advertisment_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView cross_icon = dialog.findViewById(R.id.close_icon);
        cross_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countDownTimer!=null)
                    countDownTimer.cancel();

                dialog.dismiss();
            }
        });

        ViewPager viewPager = dialog.findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(HomeScreen.this));

        countDownTimer = new CountDownTimer(5*60*1000,2000) {
            @Override
            public void onTick(long l) {
                try {
                    if (index+1 == advertisementList.size()){
                        index = 0;
                    }else {
                        index++;
                    }
                    viewPager.setCurrentItem(index);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish() {
                Log.e(TAG, "onFinish: TIMER FINISHED");
            }
        };

        countDownTimer.start();

        dialog.show();


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer!=null)
            countDownTimer.cancel();
    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            Advertisement modelObject = advertisementList.get(position);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = (View) inflater.inflate(R.layout.advertismnet_list_item, collection, false);
            ImageView imageView = layout.findViewById(R.id.imageView);

            ImageView fwd = layout.findViewById(R.id.fwd_button);
            ImageView bwd = layout.findViewById(R.id.backwrd_button);

            TextView textView = layout.findViewById(R.id.image_counter);

            Picasso.with(HomeScreen.this).load(advertisementList.get(position).getAd_image()).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(advertisementList.get(position).getAd_url()));
                    startActivity(intent);
                }
            });

            textView.setText((position+1)+"/"+advertisementList.size());

           /* if (advertisementList.size() > 1){
                if (position<advertisementList.size()-1){
                    fwd.setVisibility(View.VISIBLE);
                }else {
                    fwd.setVisibility(View.GONE);
                }

                if (position > 0 && position <= advertisementList.size()-1){
                    fwd.setVisibility(View.VISIBLE);
                }else {
                    fwd.setVisibility(View.GONE);
                }

            }*/

            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return advertisementList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Advertisement customPagerEnum =advertisementList.get(position);
            return "Advertismnet";
        }

    }
}
