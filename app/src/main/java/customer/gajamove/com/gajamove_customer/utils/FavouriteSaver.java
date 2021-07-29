package customer.gajamove.com.gajamove_customer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import customer.gajamove.com.gajamove_customer.models.Prediction;

public class FavouriteSaver {

    public static String FAV_TAG = "fav_tag";

    private SharedPreferences prefs;
    public FavouriteSaver(Context context) {

        prefs = context.getSharedPreferences("fav_db_list_item",Context.MODE_PRIVATE);
    }


    public void saveFav(ArrayList<Prediction> list)
    {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(FAV_TAG,json);
        editor.apply();
    }

    public ArrayList<Prediction> getFavList()
    {
        Gson gson = new Gson();
        String json = prefs.getString(FAV_TAG, "");
        if (json.equalsIgnoreCase(""))
            return new ArrayList<>();

        Type type = new TypeToken<ArrayList<Prediction>>(){}.getType();
        ArrayList<Prediction> cart_list= gson.fromJson(json, type);

        return cart_list;

    }

}
