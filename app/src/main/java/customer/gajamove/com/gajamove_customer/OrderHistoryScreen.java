package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import customer.gajamove.com.gajamove_customer.fragment.CancelledOrderFragment;
import customer.gajamove.com.gajamove_customer.fragment.CompleteOrderFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderHistoryScreen extends BaseActivity {

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.History));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    TextView cancelled_tab_btn,complete_tab_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_screen);


        includeActionBar();

        cancelled_tab_btn = findViewById(R.id.cancelled_tab_btn);
        complete_tab_btn = findViewById(R.id.complete_tab_btn);


        cancelled_tab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTab(cancelled_tab_btn);
                disableTab(complete_tab_btn);

                getSupportFragmentManager().beginTransaction().replace(R.id.content_lay,new CancelledOrderFragment()).commit();
            }
        });

        complete_tab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableTab(cancelled_tab_btn);
                enableTab(complete_tab_btn);

                getSupportFragmentManager().beginTransaction().replace(R.id.content_lay,new CompleteOrderFragment()).commit();

            }
        });



      //  getSupportFragmentManager().beginTransaction().replace(R.id.content_lay,new CompleteOrderFragment()).commit();

        complete_tab_btn.performClick();

    }


    private void enableTab(TextView home_tab) {
       home_tab.setTextColor(ContextCompat.getColor(OrderHistoryScreen.this,R.color.white_color));
       home_tab.setBackgroundResource(R.drawable.next_btn_drawable);
    }

    private void disableTab(TextView home_tab) {
        home_tab.setTextColor(ContextCompat.getColor(OrderHistoryScreen.this,R.color.theme_primary));
        home_tab.setBackground(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
