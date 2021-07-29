package customer.gajamove.com.gajamove_customer.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import customer.gajamove.com.gajamove_customer.AppVersionScreen;
import customer.gajamove.com.gajamove_customer.ChangeLanguage;
import customer.gajamove.com.gajamove_customer.FavouriteDriverScreen;
import customer.gajamove.com.gajamove_customer.FinanceScreen;
import customer.gajamove.com.gajamove_customer.HomeScreen;
import customer.gajamove.com.gajamove_customer.PickLocationScreen;
import customer.gajamove.com.gajamove_customer.Profile_Screen;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.SavedRoutes;
import customer.gajamove.com.gajamove_customer.auth.ChangePassword;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.bank.BankScreen;
import customer.gajamove.com.gajamove_customer.sos.SOS_Setting_Activity;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";



    private void includeActionBar(View root){
        ImageView back = root.findViewById(R.id.backBtn);
        TextView title = root.findViewById(R.id.title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirect_login.performClick();
                getActivity().finish();
            }
        });

        title.setVisibility(View.VISIBLE);
        title.setText(getActivity().getResources().getString(R.string.settings));
    }

    CircleImageView user_pic;
    TextView txtName;

    LinearLayout logout_lay,app_version_lay,document_managment_lay,fav_driver_lay,bank_lay,standard_rates_lay,saved_rout_lay,e_receipt_lay,privacy_lay,report_bug_lay,tos_lay,notifications_lay,profile_lay,sos_lay,finance_lay,change_pass_lay,language_lay;

    SharedPreferences sharedPreferences;

    AlertDialog alert;
    Switch e_recept_switch;
    String e_recept = "0";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home_frame = inflater.inflate(R.layout.settings_frame,container,false);

        includeActionBar(home_frame);
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        e_recept = sharedPreferences.getString(Constants.PREFS_E_RECEIPT,"0");
        Constants.enableSSL(asyncHttpClient);
        user_pic = home_frame.findViewById(R.id.userProfilePic);
        txtName = home_frame.findViewById(R.id.txtName);

        document_managment_lay = home_frame.findViewById(R.id.document_lay);
        notifications_lay = home_frame.findViewById(R.id.notifications_lay);
        language_lay = home_frame.findViewById(R.id.language_lay);
        saved_rout_lay = home_frame.findViewById(R.id.saved_rout_lay);
        standard_rates_lay = home_frame.findViewById(R.id.standard_lay);
        bank_lay = home_frame.findViewById(R.id.my_bank_lay);
        fav_driver_lay = home_frame.findViewById(R.id.my_driver_lay);
        logout_lay = home_frame.findViewById(R.id.logout_lay);
        app_version_lay = home_frame.findViewById(R.id.app_version_lay);

        privacy_lay = home_frame.findViewById(R.id.privacy_lay);
        tos_lay = home_frame.findViewById(R.id.user_agreement_lay);
        report_bug_lay = home_frame.findViewById(R.id.report_bug_lay);
        e_receipt_lay = home_frame.findViewById(R.id.e_receipt_lay);
        e_recept_switch = home_frame.findViewById(R.id.e_receipt_switch);


        logout_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ConfirmLog_out();
            }
        });

        fav_driver_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FavouriteDriverScreen.class));
            }
        });

        app_version_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AppVersionScreen.class));

            }
        });

        saved_rout_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SavedRoutes.class));
            }
        });

        bank_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BankScreen.class));
            }
        });

        if (e_recept.equalsIgnoreCase("0"))
            e_recept_switch.setChecked(false);
        else
            e_recept_switch.setChecked(true);

        e_recept_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    updateReceiptStatus("1");
                else
                    updateReceiptStatus("0");
            }
        });

        e_receipt_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (e_recept_switch.isChecked())
                    e_recept_switch.setChecked(false);
                else
                    e_recept_switch.setChecked(true);
            }
        });


        report_bug_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialoge(getResources().getString(R.string.contact_us),"http://gajamove.com/contact-us/");
            }
        });

        standard_rates_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialoge(getResources().getString(R.string.standard_rate),"https://gajamove.com/get-estimate/");

            }
        });


        privacy_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialoge(getResources().getString(R.string.privacy),"http://gajamove.com/privacy-policy/");

            }
        });

        tos_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialoge("Terms and Conditions","https://gajamove.com/terms-conditions-user/");

            }
        });

        profile_lay = home_frame.findViewById(R.id.profile_lay);
        sos_lay = home_frame.findViewById(R.id.sos_lay);
        finance_lay = home_frame.findViewById(R.id.finance_lay);
        change_pass_lay = home_frame.findViewById(R.id.change_pass_lay);

        change_pass_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean social = getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getBoolean(Constants.SOCIAL_LOGIN,false);

                if (social) {
                    ConfirmLogout();
                } else {
                    startActivity(new Intent(getActivity(), ChangePassword.class));

                }

            }
        });

        finance_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FinanceScreen.class));
            }
        });

        sos_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SOS_Setting_Activity.class));
            }
        });

        profile_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Profile_Screen.class));
            }
        });

        language_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChangeLanguage.class)
                        .putExtra("redirect",true));
                getActivity().finish();
            }
        });

        String name = sharedPreferences.getString(Constants.PREFS_USER_NAME,"");
        String image = sharedPreferences.getString(Constants.PREFS_USER_IMAGE,"");

        Log.e(TAG, "onCreateView: "+image);

        Picasso.with(getActivity()).load(image).into(user_pic);
        txtName.setText(name);

        return home_frame;
    }



    GoogleSignInClient googleSignInClient;
    private void ConfirmLog_out() {
        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getResources().getString(R.string.logout_prompt))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean social = getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE).getBoolean(Constants.SOCIAL_LOGIN,false);


                                if (social) {
                                    LoginManager.getInstance().logOut();

                                    GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
                                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestEmail()
                                            .requestId()
                                            .build();


                                    if (googleSignInAccount!=null) {
                                        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                                        googleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
                                                startActivity(new Intent(getActivity(), LoginScreen.class));
                                                getActivity().finish();


                                            }
                                        });
                                    }else {

                                        getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
                                        startActivity(new Intent(getActivity(), LoginScreen.class));
                                        getActivity().finish();
                                    }

                                }else {
                                    getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
                                    startActivity(new Intent(getActivity(), LoginScreen.class));
                                    getActivity().finish();
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


    private void ConfirmLogout() {
        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getResources().getString(R.string.google_login_warn))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
                                startActivity(new Intent(getActivity(), LoginScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                getActivity().finish();
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



    private void openLink(String url){

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            String name = sharedPreferences.getString(Constants.PREFS_USER_NAME,"");
            String image = sharedPreferences.getString(Constants.PREFS_USER_IMAGE,"");

            Log.e(TAG, "onCreateView: "+image);

            Picasso.with(getActivity()).load(image).into(user_pic);
            txtName.setText(name);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private ProgressDialog progressDialog = null;

    private void showDialog(String message){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideDialog(){
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private void updateReceiptStatus(final String status) {

        String customer_id = sharedPreferences.getString(Constants.PREFS_CUSTOMER_ID,"");

        RequestParams params = new RequestParams();
        params.put("customer_id",customer_id);
        params.put("status",status);
        params.put("key",UtilsManager.getApiKey(getActivity()));


        Log.e(TAG, "updateReceiptStatus: "+Constants.Host_Address + "customers/update_e_receipt_status/"+customer_id+"/"+status+"/tgs_appkey_amin");
        asyncHttpClient.get(Constants.Host_Address + "customers/update_e_receipt_status/"+customer_id+"/"+status+"/"+UtilsManager.getApiKey(getActivity())+"", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showDialog("Update Status");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {
                    hideDialog();
                    String response = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+response);
                    sharedPreferences.edit().putString(Constants.PREFS_E_RECEIPT,status).apply();


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
                    Toast.makeText(getActivity(),"Internet/Server Error",Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    private void openDialoge(String title,String path){

        Dialog dialog = new Dialog(getActivity(),R.style.full_screen_dialog);
        dialog.setContentView(R.layout.dialoge_webview);

        TextView titleView = dialog.findViewById(R.id.title);
        Button done = dialog.findViewById(R.id.done_btn);
        WebView webView = dialog.findViewById(R.id.webview);
        CheckBox checkBox = dialog.findViewById(R.id.checkbox);



        if (title.toLowerCase().contains("contact") || title.toLowerCase().contains("estimate")
                || title.toLowerCase().contains("standard")) {
            checkBox.setVisibility(View.GONE);
        }
        else {
            checkBox.setText(getResources().getString(R.string.agree) +" "+title);
            done.setBackgroundResource(R.drawable.back_light_btn);
        }


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

}
