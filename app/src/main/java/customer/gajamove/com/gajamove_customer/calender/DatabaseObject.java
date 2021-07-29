package customer.gajamove.com.gajamove_customer.calender;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by PC-GetRanked on 11/26/2018.
 */

public class DatabaseObject {
    private static Database dbHelper;
    private SQLiteDatabase db;
    public DatabaseObject(Context context) {
        dbHelper = new Database(context);
        this.dbHelper.getWritableDatabase();
        this.db = dbHelper.getReadableDatabase();
    }
    public SQLiteDatabase getDbConnection(){
        return this.db;
    }
    public void closeDbConnection(){
        if(this.db != null){
            this.db.close();
        }
    }
}
