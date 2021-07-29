package customer.gajamove.com.gajamove_customer.sos;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import customer.gajamove.com.gajamove_customer.models.EmailContact;
import customer.gajamove.com.gajamove_customer.models.PhoneContact;

/**
 * Created by Asim Shahzad on 12/18/2017.
 */
public class SOS_Settings_PREFS
{
    SharedPreferences prefs;
    Context context;


    public SOS_Settings_PREFS(Context context)
    {
        prefs = context.getSharedPreferences("sos",Context.MODE_PRIVATE);
        this.context = context;
    }

    public void savephoneContact(ArrayList<PhoneContact> list, String tag)
    {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString(tag, json);
        prefsEditor.commit();

    }

    public void saveEmailContact(ArrayList<EmailContact> list, String tag)
    {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString(tag, json);
        prefsEditor.commit();
    }

    public ArrayList<EmailContact> getSOSEmails(String tag)
    {
        Gson gson = new Gson();
        String json = prefs.getString(tag, "");
        java.lang.reflect.Type type = new TypeToken<ArrayList<EmailContact>>(){}.getType();
        ArrayList<EmailContact> list= gson.fromJson(json, type);
        return list;
    }

    public ArrayList<PhoneContact> getSOSPhone(String tag)
    {
        Gson gson = new Gson();
        String json = prefs.getString(tag, "");
        java.lang.reflect.Type type = new TypeToken<ArrayList<PhoneContact>>(){}.getType();
        ArrayList<PhoneContact> list= gson.fromJson(json, type);
        return list;
    }
}
