package customer.gajamove.com.gajamove_customer.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;
import android.util.Log;


import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.MySSLSocketFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Sohaib on 7/23/2017.
 *
 */

public final class Constants {


   public static final String Host_Address = "https://staging.gajamove.com/webservices/v1/";
   // https://gajamove.com/gm/
  // public static final String Host_Address = "https://gajamove.com/gm/webservices/v1/";

   public static final String MEMBER_BASE_IMAGE_URL = "http://kogha.my/system/uploads/profile_images/";

   public static final String SERVICE_IMAGE_BASE_PATH = "https://staging.gajamove.com/";
  // public static final String SERVICE_IMAGE_BASE_PATH = "https://gajamove.com/gm/";

   public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

   // Static Tags
   public static final String PREFS_NAME = "com.getranked.teamguard.prefs";
   public static final String PREFS_ACCESS_TOKEN = "prefs_access_token";
   public static final String PREFS_USER_NAME = "prefs_user_name";
   public static final String PREFS_USER_EMAIL = "prefs_user_email";
   public static final String PREFS_USER_MOBILE = "prefs_user_mobile";
   public static final String PREFS_USER_PASSWORD = "prefs_user_password";
   public static final String PREFS_E_RECEIPT = "prefs_e_receipt";
   public static final String PROFILE_EDITABLE = "prefs_profile_editable";
   public static final String PREFS_CUSTOMER_ID = "prefs_customer_id";
   public static final String PREFS_LOGIN_STATE = "prefs_login_state";
   public static final String PREFS_USER_IMAGE = "prefs_user_image";
   public static final String PREFS_TOTAL_DISTANCE = "prefs_total_distance";
   public static final String PREFS_SOS_STATUS = "prefs_sos_status";
   public static final String COMMUNICATION_BETWEEN_FRAGMENTS = "communication_between_fragments";
   public static final String MEMBER_CHAT_TAG = "mem_chat_tag";
   public static final String LANGUAGE_SELECTED = "prefs_language_selected";
   public static final String LANGUAGE_CODE = "prefs_language_code";
   public static final String SOCIAL_LOGIN = "is_social_login";
   public static final String SHOW_AD = "show_ads";

   public static final String IS_IN_REGISTRATION = "is_in_registration";
   public static final String REGISTRATION_STEP = "registration_step";
   public static final String INDUSTRY_ID = "industry_id";
   public static final String INDUSTRY_NAME = "industry_name";

   public static final String GOOGLE_CLIENT_ID = "807353367789-725f1rk916b0enuoqatd36shmuu968ru.apps.googleusercontent.com";
   public static final String GOOGLE_CLIENT_SECRET = "BIHCuZBUcTLuQaMwjwSejmZR";


   public static String API_KEY = "AIzaSyDikPi2yA0ZmAVeIN_cy1eelwGx84IU08M";
   public static final String CURRENT_LATITUDE = "latitude";
   public static final String CURRENT_LONGITUDE = "longitude";
    public static final String DELETE_BANK_INFO = "";
    public static final String PREFS_USER_FULL_NAME = "full_name";
    public static String  DIRECTION_API_KEY = "";

   public static final String GOOGLE_SIGN_IN_ENABLED = "g_sign_in_enabled";
   public static final String FACEBOOK_SIGN_IN_ENABLED = "f_sign_in_enabled";

   public static final String ORDER_ID = "order_id";
   public static final String CURRENT_ORDER = "current_order";
   public static final String JOB_STATUS = "job_status";
   public static final String CURRENT_JOB_MEMBERS = "current_job_members";
   public static final String MEET_LOCATION = "meet_location";
   public static final String DESTINATION = "destination";
   public static final String TOTAL = "order_total_";
   public static final String MEET_LAT = "meet_lat";
   public static final String MEET_LONG = "meet_long";
   public static final String MEET_DATE_TIME = "meet_time";

   public static final String JOB_FINISHED = "job_finished";

   public static final String USER_LOCATION = "location";

   public static final String FOUND = "found";

   public static final String USER_SOS_EMAILS = "user_sos_emails";
   public static final String USER_SOS_PHONE = "user_sos_phone";
   public static final String MAX_DISTANCE_RANGE = "max_distance_range";
   public static final String SELECTED_SOS_MESSAGE = "sos_message";
   public static final String PAYMENT_STATUS = "pay_status";

   public static final String USER_NOTIFICATION = "user_notifications";
   public static final String USER_CHAT_NOTIFICATION = "user_chat_notifications";
   public static final String USER_JOB_NOTIFICATION = "user_job_notifications";

   public static final int NOTICIATION_ID = 01011;
   public static final int CANCEL_MEMBER_NOTICIATION_ID = 01111;
   public static boolean isChatting = false;

   public static String INAVALID_KEY_FILTER = "INVALID_KEY";

   public static int selected_notification_tab = 0;


   public static final String MERCHANT_KEY = "G46Y7h5gCK";
   public static final String MERCHANT_CODE = "M14633";


   public static LatLng meet,destination;

   public static void cancelNotification(Context ctx, int notifyId) {
      String ns = Context.NOTIFICATION_SERVICE;
      NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
      nMgr.cancel(notifyId);
   }


  // public static LatLng meet,destination;

   public static double distance(double lat1, double lon1, double lat2, double lon2) {
      double theta = lon1 - lon2;
      double dist = Math.sin(deg2rad(lat1))
              * Math.sin(deg2rad(lat2))
              + Math.cos(deg2rad(lat1))
              * Math.cos(deg2rad(lat2))
              * Math.cos(deg2rad(theta));
      dist = Math.acos(dist);
      dist = rad2deg(dist);
      dist = dist * 60 * 1.1515;
      return (dist);
   }
   private static double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
   }

   private static double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
   }

   public static void enableSSL(AsyncHttpClient client){
      MySSLSocketFactory sf;
      try {
         KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
         trustStore.load(null, null);
         sf = new MySSLSocketFactory(trustStore);
         sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
         client.setSSLSocketFactory(sf);
      }
      catch (Exception e) {
      }
   }

   public static void createImageDir(){
      try {
         File sdcard = Environment.getExternalStorageDirectory();
         if (sdcard != null) {
            File mediaDir = new File(sdcard, "DCIM/Camera");
            if (!mediaDir.exists()) {
               mediaDir.mkdirs();
            }
         }
      }
      catch (Exception e){
         e.printStackTrace();
      }
   }

   public static LatLng getLocationFromAddress(final Context context,final String strAddress) {

      Geocoder coder = new Geocoder(context);
      List<Address> address;
      LatLng p1 = null;

      try {
         // May throw an IOException
         address = coder.getFromLocationName(strAddress, 5);
         if (address == null) {
            return null;
         }
         Address location = address.get(0);
         location.getLatitude();
         location.getLongitude();

         p1 = new LatLng(location.getLatitude(), location.getLongitude() );

      } catch (IOException ex) {

         ex.printStackTrace();
      }

     return p1;
   }

   public static String getCompleteAddressString(Context context,double LATITUDE, double LONGITUDE) {
      String strAdd = "";
      Geocoder geocoder = new Geocoder(context, Locale.getDefault());
      try {
         List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
         if (addresses != null) {
            Address returnedAddress = addresses.get(0);
            StringBuilder strReturnedAddress = new StringBuilder("");

            for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
               strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
            }
            strAdd = strReturnedAddress.toString();
            Log.w("Current loction", strReturnedAddress.toString());
         } else {
            Log.w("Current loction", "No Address returned!");
         }
      } catch (Exception e) {
         e.printStackTrace();
         Log.w("Current loction", "Canont get Address!");
      }
      return strAdd;
   }

   public static void enableSSL(AsyncHttpClient asyncHttpClient, Context context){
      try {

         MySSLSocketFactory sf = new MySSLSocketFactory(getTrustedKeystore(context));
         sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
         asyncHttpClient.setSSLSocketFactory(sf);
      }
      catch (Exception e){
         e.printStackTrace();
      }
   }


   public static KeyStore getTrustedKeystore(Context context){
      BufferedReader reader;
      String data = "";
      try
      {

         reader = new BufferedReader(
                 new InputStreamReader(context.getAssets().open("pink.cer")));

         // do reading, usually loop until end of file reading
         String mLine;
         while ((mLine = reader.readLine()) != null) {
            data = data + mLine;
         }

         // Load CAs from an InputStream
         InputStream stream = context.getAssets().open("pink.cer");

         int size = stream.available();
         byte[] buffer = new byte[size];
         stream.read(buffer);
         stream.close();

         CertificateFactory cf = CertificateFactory.getInstance("X.509");
         InputStream caInput = new BufferedInputStream(new ByteArrayInputStream(buffer));
         // InputStream caInput = new BufferedInputStream(new FileInputStream("pink.crt"));
         Certificate ca;
         try {
            ca = cf.generateCertificate(caInput);
            Log.d("JavaSSLHelper", "ca=" + ((X509Certificate) ca).getSubjectDN());
            Log.d("JavaSSLHelper", "Certificate successfully created");
         } finally
         {
            caInput.close();
         }
         // Create a KeyStore containing our trusted CAs
         String keyStoreType = KeyStore.getDefaultType();
         KeyStore keyStore = KeyStore.getInstance(keyStoreType);
         keyStore.load(null, null);
         keyStore.setCertificateEntry("ca", ca);
         // Create a TrustManager that trusts the CAs in our KeyStore
         String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
         TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
         tmf.init(keyStore);

         return keyStore;
      }catch(Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}
