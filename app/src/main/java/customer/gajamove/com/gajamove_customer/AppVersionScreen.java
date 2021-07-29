package customer.gajamove.com.gajamove_customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class AppVersionScreen extends AppCompatActivity {

    TextView app_version_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_version_screen);

        app_version_txt = findViewById(R.id.version_txt);

        try
        {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            app_version_txt.setText("Version "+version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}
