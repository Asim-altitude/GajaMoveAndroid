package customer.gajamove.com.gajamove_customer.calender;

import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


/**
 * Created by PC-GetRanked on 11/26/2018.
 */

public class Database extends SQLiteAssetHelper {
    private static final String DATABASE_NAMES = "events";
    private static final int DATABASE_VERSION = 3;

    public Database(Context context) {
        super(context, DATABASE_NAMES, null, DATABASE_VERSION);
    }
}