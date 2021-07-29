package customer.gajamove.com.gajamove_customer.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;



import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.BaseActivity;
import customer.gajamove.com.gajamove_customer.R;

public class ThankYouScreen extends BaseActivity {

    TextView sign_in_redirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you_screen);

        sign_in_redirect = findViewById(R.id.sign_in_redirect);
        sign_in_redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ThankYouScreen.this,LoginScreen.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
