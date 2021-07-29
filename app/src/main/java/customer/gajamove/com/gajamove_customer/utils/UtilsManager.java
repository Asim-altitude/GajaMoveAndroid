package customer.gajamove.com.gajamove_customer.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.auth.LoginScreen;

/**
 * Created by Sohaib on 7/23/2017.
 *
 */

public final class UtilsManager {
   private static final String TAG = "UtilsManager";

   public static String profile_image1 = "https://www.superprof.co.in/images/teachers/teacher-home-selamat-pagi-that-the-only-malay-phrase-that-you-know-can-help-you-tackle-that-malaysian-crush-yours.jpg";
   public static String profile_image2 = "https://i.pinimg.com/originals/0a/cc/c9/0accc9517d71bb1c96c6a64813772cba.jpg";
   public static String profile_image3 = "https://jooinn.com/images/malaysian-man-1.jpg";

   /*public static String parseDateToddMMyyyy(String time) {
      String inputPattern = "yyyy-MM-dd";
      String outputPattern = "dd MMM (EEE) yyyy";
      SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
      SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

      Date date = null;
      String str = null;

      try {
         date = inputFormat.parse(time);
         str = outputFormat.format(date);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return str;
   }*/

   public static String parseNewDateToddMMyyyy(String time) {

      String am_pm = "";
      if (time.toLowerCase().contains("am"))
         am_pm = "AM";
      else
         am_pm = "PM";

      time = time.replace(" AM","").replace(" PM","");

      String inputPattern = "dd/MM/yyyy hh:mm";
      String outputPattern = "dd MMM (EEE) yyyy hh:mm";
      SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
      SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

      Date date = null;
      String str = null;

      try {
         date = inputFormat.parse(time);
         str = outputFormat.format(date);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return str+" "+am_pm;
   }

   public static String parseScheduledDateToddMMyyyy(String time) {

      String am_pm = "";
      if (time.toLowerCase().contains("am"))
         am_pm = "AM";
      else
         am_pm = "PM";

      time = time.replace(" AM","").replace(" PM","");


      String inputPattern = "yyyy-MM-dd hh:mm:ss";
      String outputPattern = "dd MMM (EEE) yyyy hh:mm";
      SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
      SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

      Date date = null;
      String str = null;

      try {
         date = inputFormat.parse(time);
         str = outputFormat.format(date);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return str+" "+am_pm;
   }


   public static String parseDashboardTime(String time) {

      String am_pm = "";
      if (time.toLowerCase().contains("am"))
         am_pm = "AM";
      else
         am_pm = "PM";

      time = time.replace(" AM","").replace(" PM","");


      String inputPattern = "dd/MM/yyyy hh:mm";
      String outputPattern = "dd MMM (EEE) yyyy hh:mm";
      SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
      SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

      Date date = null;
      String str = null;

      try {
         date = inputFormat.parse(time);
         str = outputFormat.format(date);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return str+" "+am_pm;
   }


   public static String parseDateToddMMyyyy(String time) {


      String inputPattern = "yyyy-MM-dd hh:mm";
      String outputPattern = "dd MMM (EEE) yyyy hh:mm a";
      SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
      SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

      Date date = null;
      String str = null;

      try {
         date = inputFormat.parse(time);
         str = outputFormat.format(date);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return str;
   }

   public static String convertAMPM(String time) {
      String inputPattern = "yyyy-MM-dd hh:mm";
      String outputPattern = "yyyy-MM-dd hh:mm a";
      SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
      SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);

      Date date = null;
      String str = null;

      try {
         date = inputFormat.parse(time);
         str = outputFormat.format(date);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return str;
   }

   public static String parseDate(String time) {
      String inputPattern = "yyyy-MM-dd";
      String outputPattern = "dd MMM (EEE) yyyy";
      SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
      SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

      Date date = null;
      String str = null;

      try {
         date = inputFormat.parse(time);
         str = outputFormat.format(date);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return str;
   }

   public static String getServerchekFormat(String time) {
      String inputPattern = "yyyy-MM-dd hh:mm";
      String outputPattern = "yyyy-MM-dd hh:mm:ss";
      SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
      SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

      Date date = null;
      String str = null;

      try {
         date = inputFormat.parse(time);
         str = outputFormat.format(date);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return str;
   }

   public static String parseTime(String time) {
      String inputPattern = "hh:mm a";
      String outputPattern = "HH:mm";


      SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern,Locale.ENGLISH);
      SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,Locale.getDefault());

      Date date = null;
      String str = null;

      try {
         date = inputFormat.parse(time);
         str = outputFormat.format(date);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return str;
   }



   public static boolean isDateValid(Date date1,Date date2){
      /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
      String t1 = simpleDateFormat.format(date1);
      String t2 = simpleDateFormat.format(date2);

      Date d1 = new Date(t1);
      Date d2 = new Date(t2);*/

      Log.e(TAG, "isDateValid: "+date1.toString());
      Log.e(TAG, "isDateValid: "+date2.toString());
      if (printDifference(date1,date2) < 0){
         return true;
      }else if (printDifference(date1,date2) < 15 * 60 * 1000){
         return true;
      }else {
         return false;
      }



      /*int diff = date1.compareTo(date2);
      if (diff > 0)
      {
         Log.i("app", "Date1 is after Date2");
         return true;
      }
      else if (diff < 0)
      {
         Log.i("app", "Date1 is before Date2");
         return false;
      }
      else if (diff == 0)
      {
         Log.i("app", "Date1 is equal to Date2");
         return true;
      }*/

      //return true;
   }

   public static boolean isAfter(String date){

      try {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ENGLISH).parse(date));

         Date currentTime = Calendar.getInstance().getTime();
         Calendar calendar1 = Calendar.getInstance();
        // calendar1.setTime(currentTime);


       /*  Date selected = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ENGLISH).parse(date);
         if (System.currentTimeMillis() < selected.getTime()){

            return true;
         }else {
            return false;
         }*/

         if (calendar.getTime().after(calendar1.getTime())){
            return true;
         }else {
            return false;
         }

      }
      catch (Exception e){
         e.printStackTrace();
      }

      return false;

   }

   public static boolean isCancelable(String date){
      try {
         SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
         Date current = Calendar.getInstance().getTime();
        /* String formattedDate = dateFormater.format(current);
         String formattedSelected = dateFormater.format(date);
*/
         Date date1 = dateFormater.parse(date);
         Date date2 = dateFormater.parse(dateFormater.format(current));

         long diff = printDifference(date2,date1);

         long minutes = ( diff / 1000 ) / 60 ;

         Log.e(TAG, "isCancelable: "+date+" diff "+minutes);

         if (minutes < 0){
            return false;
         }else if (minutes > 120){
            return true;
         }else {
            return false;
         }


      }
      catch (Exception e){
         e.printStackTrace();
      }

      return false;

   }

    public static String makeInitialCapital(String msg){

        try {
            return msg.substring(0, 1).toUpperCase() + msg.substring(1).toLowerCase();
        }
        catch (Exception e){
            return msg;
        }


    }

   public static boolean isTimeValid(String date){
      try {
         SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
         Date current = Calendar.getInstance().getTime();
        /* String formattedDate = dateFormater.format(current);
         String formattedSelected = dateFormater.format(date);
*/
         Date date1 = dateFormater.parse(date);
         Date date2 = dateFormater.parse(dateFormater.format(current));

         if (printDifference(date1,date2) < 0){
            return true;
         }else if (printDifference(date1,date2) < -15 * 60 * 1000){
            return true;
         }else {
            return false;
         }

      }
      catch (Exception e){
         e.printStackTrace();
      }

      return false;

   }


   public static long printDifference(Date startDate, Date endDate) {
      //milliseconds
      long different = endDate.getTime() - startDate.getTime();


      Log.e(TAG, "printDifference: "+different);

      return different;
     /* System.out.println("startDate : " + startDate);
      System.out.println("endDate : "+ endDate);
      System.out.println("different : " + different);

      long secondsInMilli = 1000;
      long minutesInMilli = secondsInMilli * 60;
      long hoursInMilli = minutesInMilli * 60;
      long daysInMilli = hoursInMilli * 24;

      long elapsedDays = different / daysInMilli;
      different = different % daysInMilli;

      long elapsedHours = different / hoursInMilli;
      different = different % hoursInMilli;

      long elapsedMinutes = different / minutesInMilli;
      different = different % minutesInMilli;

      long elapsedSeconds = different / secondsInMilli;

      System.out.printf(
              "%d days, %d hours, %d minutes, %d seconds%n",
              elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);*/
   }

 /*  public static LatLng getLocationFromAddress(Context context, String strAddress) {
      LatLng p1 = null;
      if (strAddress!=null) {
         if (!strAddress.equalsIgnoreCase("")) {
            Geocoder coder = new Geocoder(context);
            List<Address> address;
            try {
               // May throw an IOException
               address = coder.getFromLocationName(strAddress, 5);
               if (address == null) {
                  return null;
               }

               Address location = address.get(0);
               p1 = new LatLng(location.getLatitude(), location.getLongitude());

            } catch (Exception ex) {

               ex.printStackTrace();
            }
         }
      }

      return p1;
   }

   public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
      int Radius = 6371;// radius of earth in Km
      double lat1 = StartP.latitude;
      double lat2 = EndP.latitude;
      double lon1 = StartP.longitude;
      double lon2 = EndP.longitude;
      double dLat = Math.toRadians(lat2 - lat1);
      double dLon = Math.toRadians(lon2 - lon1);
      double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
              + Math.cos(Math.toRadians(lat1))
              * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
              * Math.sin(dLon / 2);
      double c = 2 * Math.asin(Math.sqrt(a));
      double valueResult = Radius * c;
      double km = valueResult / 1;
      DecimalFormat newFormat = new DecimalFormat("####");
      int kmInDec = Integer.valueOf(newFormat.format(km));
      double meter = valueResult % 1000;
      int meterInDec = Integer.valueOf(newFormat.format(meter));
      Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
              + " Meter   " + meterInDec);

      return kmInDec;
   }

   public static void logoutUser(Activity activity){
      try {
         SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE);
         sharedPreferences.edit().clear().apply();
         activity.startActivity(new Intent(activity,ComingSoonScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
         Toast.makeText(activity,"Invalid Key",Toast.LENGTH_LONG).show();
         activity.finish();

      }
      catch (Exception e){
         e.printStackTrace();
      }
   }*/

   public static void showAlertMessage(Context context, String title, String message){

      try {

         AlertDialog.Builder builder = new AlertDialog.Builder(context);
         builder.setTitle(title);
         builder.setMessage(message);
         builder.setNegativeButton("OK", null);
         builder.show();

      }
      catch (Exception e)
      {

      }
   }




   public static void showAlertMessageTopUp(final Context context, String title, String message){

      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setTitle(title);
      builder.setMessage(Html.fromHtml(message));
      builder.setNegativeButton("Top Up", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
          /*  Intent intent = new Intent(context, FinanceScreen.class);
            context.startActivity(intent);*/
         }
      });

      builder.show();
   }

   public static boolean isMyServiceRunning(Context context,Class<?> serviceClass) {
      ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
         if (serviceClass.getName().equals(service.service.getClassName())) {
            return true;
         }
      }
      return false;
   }

   public static String getApiKey(Context context){
      SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE);

      return sharedPreferences.getString(Constants.PREFS_ACCESS_TOKEN,"ABC");

   }


   public static void isInvalidKey(Activity activity,String res){
      try {
         JSONObject jsonObject = new JSONObject(res);

         if (jsonObject.getString("status").equalsIgnoreCase("failed") || jsonObject.getString("status").equalsIgnoreCase("failure")){
            if (jsonObject.getString("message").equalsIgnoreCase("invalid key")) {
               activity.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
               activity.startActivity(new Intent(activity, LoginScreen.class));
               activity.finish();
            }
         }
      }
      catch (Exception e){
         e.printStackTrace();
      }
   }



   public static String getDoubleAmount(String amount)
   {
      DecimalFormat formatter = new DecimalFormat("###.0");
      String comma_seperated_price = formatter.format(Double.parseDouble(amount));
      return comma_seperated_price;
   }

   public static String getAdjustedDistance(String distance)
   {
      DecimalFormat formatter = new DecimalFormat("###.00");
      String comma_seperated_price = formatter.format(Double.parseDouble(distance));
      return comma_seperated_price;
   }
   public static void hideKeyboard(AppCompatActivity activity) {
      InputMethodManager imm = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
      //Find the currently focused view, so we can grab the correct window token from it.
      View view = activity.getCurrentFocus();
      //If no view currently has focus, create a new one, just so we can grab a window token from it
      if (view == null) {
         view = new View(activity);
      }
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
   }

/*   public static void setListViewHeightBasedOnChildren(ListView listView) {
      ListAdapter listAdapter = listView.getAdapter();
      if (listAdapter == null) {
         // pre-condition
         return;
      }

      int totalHeight = 20;
      int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
      for (int i = 0; i < listAdapter.getCount(); i++) {
         View listItem = listAdapter.getView(i, null, listView);
         listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
         totalHeight += listItem.getMeasuredHeight();
      }

      ViewGroup.LayoutParams params = listView.getLayoutParams();
      params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
      listView.setLayoutParams(params);
      listView.requestLayout();
   }*/

   /*public static void setListViewHeightBasedOnChildren(ListView listView) {
      ListAdapter listAdapter = listView.getAdapter();
      if (listAdapter == null) {
         // pre-condition
         return;
      }

      int totalHeight = 0;
      for (int i = 0; i < listAdapter.getCount(); i++) {
         View listItem = listAdapter.getView(i, null, listView);
         listItem.measure(0, 0);
         totalHeight += listItem.getMeasuredHeight();
      }

      ViewGroup.LayoutParams params = listView.getLayoutParams();
      params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
      listView.setLayoutParams(params);
      listView.requestLayout();
   }*/

   public static void setListViewHeightBasedOnChildren(ListView listView) {
      ListAdapter listAdapter = listView.getAdapter();
      if (listAdapter == null) {
         // pre-condition
         return;
      }

      int totalHeight = 0;
      int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
      for (int i = 0; i < listAdapter.getCount(); i++) {
         View listItem = listAdapter.getView(i, null, listView);
         listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
         totalHeight += listItem.getMeasuredHeight();
      }

      ViewGroup.LayoutParams params = listView.getLayoutParams();
      params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
      listView.setLayoutParams(params);
      listView.requestLayout();
   }

   public static double getBearingBetweenTwoPoints1(LatLng latLng1, LatLng latLng2) {

      double lat1 = degreesToRadians(latLng1.latitude);
      double long1 = degreesToRadians(latLng1.longitude);
      double lat2 = degreesToRadians(latLng2.latitude);
      double long2 = degreesToRadians(latLng2.longitude);


      double dLon = (long2 - long1);


      double y = Math.sin(dLon) * Math.cos(lat2);
      double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
              * Math.cos(lat2) * Math.cos(dLon);

      double radiansBearing = Math.atan2(y, x);


      return radiansToDegrees(radiansBearing);
   }

   private static double degreesToRadians(double degrees) {
      return degrees * Math.PI / 180.0;
   }

   private static double radiansToDegrees(double radians) {
      return radians * 180.0 / Math.PI;
   }

   public static boolean setListViewHeightBasedOnItems(ListView listView) {

      ListAdapter listAdapter = listView.getAdapter();
      if (listAdapter != null) {

         int numberOfItems = listAdapter.getCount();

         // Get total height of all items.
         int totalItemsHeight = 0;
         for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = listAdapter.getView(itemPos, null, listView);
            float px = 300 * (listView.getResources().getDisplayMetrics().density);
            item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalItemsHeight += item.getMeasuredHeight();
         }

         // Get total height of all item dividers.
         int totalDividersHeight = listView.getDividerHeight() *
                 (numberOfItems - 1);
         // Get padding
         int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

         // Set list height.
         ViewGroup.LayoutParams params = listView.getLayoutParams();
         params.height = totalItemsHeight + totalDividersHeight + totalPadding;
         listView.setLayoutParams(params);
         listView.requestLayout();
         //setDynamicHeight(listView);
         return true;

      } else {
         return false;
      }

   }

   public static void saveUserLocation(AppCompatActivity activity, String lat, String lon){
      SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString(Constants.CURRENT_LATITUDE,lat);
      editor.putString(Constants.CURRENT_LONGITUDE,lon);

      editor.apply();
   }

  /* public static LatLng getUserSavedLocation(AppCompatActivity activity){

      SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE);
      double lat = Double.parseDouble(sharedPreferences.getString(Constants.CURRENT_LATITUDE,"3.1390"));
      double lon = Double.parseDouble(sharedPreferences.getString(Constants.CURRENT_LONGITUDE,"101.6869"));

      return new LatLng(lat,lon);

   }*/


   public static void printHashKey(Context pContext) {
      try {
         PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
         for (Signature signature : info.signatures) {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(signature.toByteArray());
            String hashKey = new String(Base64.encode(md.digest(), 0));
            Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
         }
      } catch (NoSuchAlgorithmException e) {
         Log.e(TAG, "printHashKey()", e);
      } catch (Exception e) {
         Log.e(TAG, "printHashKey()", e);
      }
   }

   public static int convertDipToPixels(Context context,float dips)
   {
      return (int) (dips * context.getResources().getDisplayMetrics().density + 0.5f);
   }

   public static void updateListHeight(Context context, int dpi, ListView listView, int items){
      try {
         int px = convertDipToPixels(context, dpi);
         LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listView.getLayoutParams();
         layoutParams.height = px * items;
         listView.setLayoutParams(layoutParams);
      }
      catch (Exception e){
         e.printStackTrace();
      }
   }

}
