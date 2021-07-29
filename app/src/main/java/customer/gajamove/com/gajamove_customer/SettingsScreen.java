package customer.gajamove.com.gajamove_customer;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;



import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.fragment.SettingsFragment;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;

public class SettingsScreen extends BaseActivity {

    private void setupToolbar(){
        // Show menu icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.black_color), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

       // setupToolbar();

        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings_frame, new SettingsFragment()).commit();

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
