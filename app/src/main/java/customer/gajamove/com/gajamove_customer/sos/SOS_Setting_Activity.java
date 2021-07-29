package customer.gajamove.com.gajamove_customer.sos;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import customer.gajamove.com.gajamove_customer.R;

public class SOS_Setting_Activity extends AppCompatActivity {

    private FragmentManager fm;
    private FrameLayout content;

    private void setupToolBar(){

        ImageView back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("SOS Settings");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_settings);

        setupToolBar();

        fm = getSupportFragmentManager();
        SosPhoneFragment frag = new SosPhoneFragment();
        fm.beginTransaction().replace(R.id.content_frame,frag).commit();

    }


}
