package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;
import customer.gajamove.com.gajamove_customer.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChangeLanguage extends BaseActivity {

    RelativeLayout malay_lay,english_lay;
    Button continue_btn;
    String seleted_language = "en";


    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.s_language));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    boolean redirect = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        includeActionBar();

        malay_lay = findViewById(R.id.malay_lay);
        english_lay = findViewById(R.id.english_lay);
        continue_btn = findViewById(R.id.continue_btn);

        redirect = getIntent().getBooleanExtra("redirect",false);

        SharedPreferences sharedPreferences = getSharedPreferences(this.getResources().getString(R.string.FCM_PREF),MODE_PRIVATE);
        seleted_language = sharedPreferences.getString(Constants.LANGUAGE_CODE,"en");

        if (seleted_language.equalsIgnoreCase("en")){
            ImageView imageView = (ImageView) malay_lay.getChildAt(1);
            ImageView imageView1 = (ImageView) english_lay.getChildAt(1);

            imageView1.setImageResource(R.drawable.tick);
            imageView.setImageResource(R.drawable.circle_bg_light);
        }else {
            ImageView imageView = (ImageView) malay_lay.getChildAt(1);
            ImageView imageView1 = (ImageView) english_lay.getChildAt(1);

            imageView.setImageResource(R.drawable.tick);
            imageView1.setImageResource(R.drawable.circle_bg_light);
        }

        malay_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleted_language = "ms";
                ImageView imageView = (ImageView) malay_lay.getChildAt(1);
                ImageView imageView1 = (ImageView) english_lay.getChildAt(1);

                imageView.setImageResource(R.drawable.tick);
                imageView1.setImageResource(R.drawable.circle_bg_light);
            }
        });

        english_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleted_language = "en";

                ImageView imageView = (ImageView) malay_lay.getChildAt(1);
                ImageView imageView1 = (ImageView) english_lay.getChildAt(1);

                imageView1.setImageResource(R.drawable.tick);
                imageView.setImageResource(R.drawable.circle_bg_light);
            }
        });

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getApplicationContext().
                        getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);

                sharedPreferences.edit().putBoolean(Constants.LANGUAGE_SELECTED,true).apply();
                sharedPreferences.edit().putString(Constants.LANGUAGE_CODE,seleted_language).apply();

                if (redirect){
                    startActivity(new Intent(ChangeLanguage.this, LoginScreen.class));
                    finish();
                }else {
                    startActivity(new Intent(ChangeLanguage.this, LoginScreen.class));
                    finish();
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
