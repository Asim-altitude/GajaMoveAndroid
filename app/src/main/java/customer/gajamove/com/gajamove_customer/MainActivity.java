package customer.gajamove.com.gajamove_customer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.estimate.Create_Estimate_Order;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;

public class MainActivity extends AppCompatActivity {

    boolean skipped = false;
    TextView skip_btn;
    SharedPreferences sharedPreferences;
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        boolean isLogged_in = sharedPreferences.getBoolean(Constants.PREFS_LOGIN_STATE,false);

        skip_btn = findViewById(R.id.skip_btn);


        if (isLogged_in){
            startActivity(new Intent(MainActivity.this, LoginScreen.class));
            finish();
            return;
        }else {

            try {
                videoView = findViewById(R.id.videoview);

                String path = "android.resource://" + getPackageName() + "/" + R.raw.promo;
                videoView.setVideoURI(Uri.parse(path));
                videoView.start();

            } catch (Exception e) {
                e.printStackTrace();
            }


            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    try {
                        SharedPreferences sharedPreferences = getApplicationContext().
                                getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);

                        boolean isSelected = sharedPreferences.getBoolean(Constants.LANGUAGE_SELECTED, false);


                        if (!isSelected) {
                            startActivity(new Intent(MainActivity.this, ChangeLanguage.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, LoginScreen.class));
                        }
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            skip_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (videoView.isPlaying()) {
                        videoView.pause();
                        videoView = null;
                    }

                    skipped = true;

                    startActivity(new Intent(MainActivity.this, LoginScreen.class));
                    finish();

                }
            });
            HomeScreen.stay = false;
            UtilsManager.printHashKey(this);
            getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit().putBoolean(Constants.SHOW_AD, true).apply();


       /* new CountDownTimer(2000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                if (skipped)
                    return;


            }
        }.start();*/


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            videoView.resume();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            videoView.pause();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            String path = "android.resource://" + getPackageName() + "/" + R.raw.promo;
            videoView.setVideoURI(Uri.parse(path));
            videoView.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
