package customer.gajamove.com.gajamove_customer.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Member;
import customer.gajamove.com.gajamove_customer.models.MemberLocationObject;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.models.Ride;
import customer.gajamove.com.gajamove_customer.models.RideStatus;
import customer.gajamove.com.gajamove_customer.utils.AnimationUtils;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.RideDirectionPointsDB;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Asim Shahzad on 2/18/2018.
 */
public class HomeUIMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener
{
    private static final String TAG = "HomeUIMap";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    View rootview;
    boolean isJobRunning = false;
    SharedPreferences settings;
    View mapView;
    ImageView recenter_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_main_map,container ,false);
        settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
        SupportMapFragment mapfrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);//lower APIS ma getFragmentManager use karna ha

        recenter_btn = rootview.findViewById(R.id.recenter_btn);

        recenter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap!=null){
                    if (lastLocation!=null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(lastLocation));
                        zoom = 10;
                    }
                }

            }
        });
        mapView = mapfrag.getView();
        mapfrag.getMapAsync(this);

        try
        {
            isJobRunning = getArguments().getBoolean("job_running");
            ride = (Ride) getArguments().getSerializable("ride");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return rootview;
    }

    boolean isZoomed = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }




    @Override
    public void onCameraMove() {

    }


    class handleUILoadingTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {

            }
            catch (Exception e)
            {
             e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }


  ;

    LatLngBounds bounds = null;
    GoogleMap mMap;
    LatLng pickup_lat_lng,destination_lat_lng;
    Ride ride = null;
    LatLng malaysiaLatLng = new LatLng(4.2105, 101.9758);
    SharedPreferences sharedPreferences;

    RideStatus current_ride_state = RideStatus.IDLE;
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setPadding(30, 0, 0, 0);

       // googleMap.setMyLocationEnabled(true);

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
      //  mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);




        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {



                CameraPosition cameraPosition = null;

                try {

                   /* sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME,MODE_PRIVATE);
                    LatLng latLng = Constants.getLocationFromAddress(getActivity(),sharedPreferences.getString(Constants.MEET_LOCATION,""));
                    cameraPosition = new CameraPosition.Builder()
                            .target(latLng)      // Sets the center of the map to Mountain View
                            .zoom(18)                   // Sets the zoom
                            // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latLng.latitude,
                                    latLng.longitude))
                            .title(sharedPreferences.getString(Constants.MEET_LOCATION,""))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dest)));
*/

                   if (ride!=null) {
                       if (ride.getRideStatus() == RideStatus.STATE_INITIAL) {
                           current_ride_state = RideStatus.STATE_INITIAL;

                       }else {
                           if (isJobRunning) {

                               if (initial_polyline!=null)
                                   initial_polyline.remove();

                               if (ride.isMulti()) {
                                   drawLineWithMultiStop(ride.getPredictionArrayList());
                               } else {
                                   pickup_lat_lng = Constants.getLocationFromAddress(getActivity(), ride.getPickup_loc());
                                   destination_lat_lng = Constants.getLocationFromAddress(getActivity(), ride.getDestination_loc());

                                   // getLocationOnZoom(pickup_lat_lng.latitude,pickup_lat_lng.longitude);
                                   mMap.addMarker(new MarkerOptions()
                                           .position(new LatLng(pickup_lat_lng.latitude,
                                                   pickup_lat_lng.longitude))
                                           .title(ride.getPickup_loc())
                                           .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pick)));

                                   mMap.addMarker(new MarkerOptions()
                                           .position(new LatLng(destination_lat_lng.latitude,
                                                   destination_lat_lng.longitude))
                                           .title(ride.getDestination_loc())
                                           .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dest)));

                                   LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                   builder.include(pickup_lat_lng);
                                   builder.include(destination_lat_lng);

                                   mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));

                                   drawLine(pickup_lat_lng, destination_lat_lng);

                               }
                           } else {

                               sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                               LatLng latLng = Constants.getLocationFromAddress(getActivity(), sharedPreferences.getString(Constants.MEET_LOCATION, ""));
                               cameraPosition = new CameraPosition.Builder()
                                       .target(latLng)      // Sets the center of the map to Mountain View
                                       .zoom(18)                   // Sets the zoom
                                       // Sets the tilt of the camera to 30 degrees
                                       .build();                   // Creates a CameraPosition from the builder
                               mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                               mMap.addMarker(new MarkerOptions()
                                       .position(new LatLng(latLng.latitude,
                                               latLng.longitude))
                                       .title(sharedPreferences.getString(Constants.MEET_LOCATION, ""))
                                       .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dest)));
                           }
                       }
                   }

                }
                catch (Exception e){
                    e.printStackTrace();

                    cameraPosition = new CameraPosition.Builder()
                            .target(malaysiaLatLng)      // Sets the center of the map to Mountain View
                            .zoom(8)                   // Sets the zoom
                            // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



                }


                if (ride!=null) {
                    if (ride.isHasMember()) {
                        initTracking();
                    }
                }

            }
        });
        googleMap.setMyLocationEnabled(false);


    }


    Marker driverMarker = null;
    LatLng lastLocation = null;
    boolean isInitialRoutDrawn = false,isCarMoving = false;
    private void initTracking(){

       DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("driver");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                try{


                MemberLocationObject memberLocationObject = dataSnapshot.getValue(MemberLocationObject.class);
                if (memberLocationObject.getCurrent_job().equalsIgnoreCase(ride.getOrder_id())){

                    if (driverMarker==null) {
                        lastLocation = new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),Double.parseDouble(memberLocationObject.getMem_lng()));
                        driverMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                        Double.parseDouble(memberLocationObject.getMem_lng())))
                                .title(memberLocationObject.getMem_name())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mycar))

                        );
                        driverMarker.setTag(memberLocationObject.getMem_id());

                    }


                    if (!isInitialRoutDrawn && current_ride_state == RideStatus.STATE_INITIAL) {

                        pickup_lat_lng = Constants.getLocationFromAddress(getActivity(), ride.getPickup_loc());
                        createInitialRout(new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                Double.parseDouble(memberLocationObject.getMem_lng())), pickup_lat_lng);

                    }
                }


                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                try {


                MemberLocationObject memberLocationObject = dataSnapshot.getValue(MemberLocationObject.class);
                if (memberLocationObject.getCurrent_job().equalsIgnoreCase(ride.getOrder_id())){


                    if (driverMarker==null) {
                        lastLocation = new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),Double.parseDouble(memberLocationObject.getMem_lng()));
                        driverMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                        Double.parseDouble(memberLocationObject.getMem_lng())))
                                .title(memberLocationObject.getMem_name())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mycar))

                        );
                        driverMarker.setTag(memberLocationObject.getMem_id());

                        pickup_lat_lng = Constants.getLocationFromAddress(getActivity(), ride.getPickup_loc());
                        createInitialRout(new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                Double.parseDouble(memberLocationObject.getMem_lng())),pickup_lat_lng);

                    }else {

                       /* driverMarker.setPosition(new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                Double.parseDouble(memberLocationObject.getMem_lng())));
*/
                        if (lastLocation!=null){
                            Location location = new Location("");
                            location.setLatitude(Double.parseDouble(memberLocationObject.getMem_lat()));
                            location.setLongitude(Double.parseDouble(memberLocationObject.getMem_lng()));

                             double distance =   Constants.distance(lastLocation.latitude,lastLocation.longitude,Double.parseDouble(memberLocationObject.getMem_lat()),Double.parseDouble(memberLocationObject.getMem_lng())) * 1000;

                            Log.e(TAG, "onChildChanged: DISTANCE "+distance);
                             if (distance >= 3 && !isCarMoving) {
                                 updateCarLocation(new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                         Double.parseDouble(memberLocationObject.getMem_lng())));
                             }

                             //animateMarkerNew(location,driverMarker);
                            // driverMarker.setRotation((float) UtilsManager.getBearingBetweenTwoPoints1(new LatLng(lastLocation.latitude, lastLocation.longitude), new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()), Double.parseDouble(memberLocationObject.getMem_lng()))));
                        }


                        lastLocation = new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),Double.parseDouble(memberLocationObject.getMem_lng()));

                    }


                    if (!isInitialRoutDrawn && current_ride_state == RideStatus.STATE_INITIAL) {
                        pickup_lat_lng = Constants.getLocationFromAddress(getActivity(), ride.getPickup_loc());
                        createInitialRout(new LatLng(Double.parseDouble(memberLocationObject.getMem_lat()),
                                Double.parseDouble(memberLocationObject.getMem_lng())), pickup_lat_lng);
                    }
                }


                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void createInitialRout(LatLng start, LatLng end){

        try {
            if (ride.getRideStatus() == RideStatus.STATE_INITIAL) {

                drawLine(start, end);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(start);
                builder.include(end);

                isInitialRoutDrawn = true;

                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    private void animateMarkerNew(final Location destination, final Marker marker) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(15.5f)
                                .build()));

                        marker.setRotation(getBearing(startPosition, new LatLng(destination.getLatitude(), destination.getLongitude())));
                    } catch (Exception ex) {
                        //I don't care atm.
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                }
            });
            valueAnimator.start();
        }
    }



    LatLng previousLatLng,currentLatLng;
    int zoom = 10;
    private void updateCarLocation(LatLng latLng) {

        if (previousLatLng == null) {
            currentLatLng = latLng;
            previousLatLng = currentLatLng;
            driverMarker.setPosition(currentLatLng);
            driverMarker.setAnchor(0.5f,0.5f);


        } else {
            previousLatLng = currentLatLng;
            currentLatLng = latLng;
            ValueAnimator valueAnimator = AnimationUtils.carAnimator();

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (currentLatLng != null && previousLatLng != null) {
                        float multiplier = valueAnimator.getAnimatedFraction();
                        LatLng nextLocation = new  LatLng(
                                multiplier * currentLatLng.latitude + (1 - multiplier) * previousLatLng.latitude,
                                multiplier * currentLatLng.longitude + (1 - multiplier) * previousLatLng.longitude);
                        driverMarker.setPosition(nextLocation);
                        double rotation = AnimationUtils.getRotation(previousLatLng, nextLocation);
                        if (!(rotation == -1f)) {
                            driverMarker.setRotation((float) rotation);
                        }
                        driverMarker.setAnchor(0.5f, 0.5f);

                        if (mMap.getCameraPosition().zoom == 10) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(nextLocation));

                        }

                        isCarMoving = false;
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    //Method for finding bearing between two points
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }


    private void drawLineWithMultiStop(ArrayList<Prediction> predictionArrayList) {

        LatLng pos1,pos2;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        try {

            for (int i=0;i<predictionArrayList.size();i++) {

                if (i < predictionArrayList.size() - 1) {
                    pos1 = Constants.getLocationFromAddress(getActivity(), predictionArrayList.get(i).getLocation_name());
                    pos2 = Constants.getLocationFromAddress(getActivity(), predictionArrayList.get(i + 1).getLocation_name());

                    builder.include(pos1);
                    builder.include(pos2);

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(pos1.latitude,
                                    pos1.longitude))
                            .title(predictionArrayList.get(i).getLocation_name())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pick)));

                    if (i+1 == predictionArrayList.size()-1){
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(pos2.latitude,
                                        pos2.longitude))
                                .title(predictionArrayList.get(i+1).getLocation_name())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dest)));


                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));

                    }else {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(pos2.latitude,
                                        pos2.longitude))
                                .title(predictionArrayList.get(i+1).getLocation_name())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pick)));
                    }


                    drawLine(pos1, pos2);

                }

            }



        }
        catch (Exception e){
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();


    }


    // Checking if Google Play Services Available or not
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }



    //draw a line bw two paths

    boolean isLineDrawn = false;


   /* private void showPolyLineDirection()
    {
        PolylineOptions polyLineOptions = null;
        polyLineOptions = new PolylineOptions();

        polyLineOptions.addAll(points);
        polyLineOptions.width(5);
        polyLineOptions.color(Color.BLACK);
        polyLineOptions.startCap(new SquareCap());
        polyLineOptions.endCap(new SquareCap());
        polyLineOptions.jointType(JointType.ROUND);

        if (polyLineOptions!=null)
            mMap.addPolyline(polyLineOptions);
    }*/

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            return add;

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        return "";
    }




    RideDirectionPointsDB directionPointsDB = null;
    ArrayList<LatLng> points = null;
    private void drawLine(LatLng start_, LatLng end_)
    { try {

        String url = getMapsApiDirectionsUrl(start_, end_);
        ReadTask downloadTask = new ReadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
        /*directionPointsDB = new RideDirectionPointsDB(getActivity());
        points = directionPointsDB.getDirectionPointsList();
        if (points!=null)
        {
            if (points.size()>0)
            {
                showPolyLineDirection();
               *//* if (smoothMovementThread.getStatus()!= AsyncTask.Status.RUNNING)
                    smoothMovementThread.execute();*//*
            }
            else
            {
                String url = getMapsApiDirectionsUrl(start_, end_);
                ReadTask downloadTask = new ReadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        }
        else {
            String url = getMapsApiDirectionsUrl(start_, end_);
            ReadTask downloadTask = new ReadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }*/

    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
        //  addDropInLocationMarker();
    }

    private boolean added =false;


    private class ReadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);

            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }

    public class MapHttpConnection {
        public String readUr(String mapsApiDirectionsUrl) throws IOException {
            String data = "";
            InputStream istream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(mapsApiDirectionsUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                istream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(istream));
                StringBuffer sb = new StringBuffer();
                String line ="";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();


            }
            catch (Exception e) {
                Log.d("Exception reading url", e.toString());
            } finally {
                istream.close();
                urlConnection.disconnect();
            }
            return data;

        }
    }



    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {


        String str_origin = "origin="+origin.latitude+","+origin.longitude;//.replaceAll("\\s+","").replaceAll("&","");;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;//.replaceAll("\\s+","").replaceAll("&","");


        // Sensor enabled
        String sensor = "sensor=false";
        String optimize ="optimize:true";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+optimize+"&"+sensor;

        // Output format
        String output = "json";

        String API_KEY;
        if (Constants.DIRECTION_API_KEY.equalsIgnoreCase(""))
            API_KEY = getActivity().getResources().getString(R.string.DIRECTION_API_KEY);
        else
            API_KEY = Constants.DIRECTION_API_KEY;

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+"&key="+API_KEY+"";
        Log.e("request url",url);

        return url;

    }



    public class PathJSONParser {

        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;
            try {
                jRoutes = jObject.getJSONArray("routes");
                for (int i=0 ; i < jRoutes.length() ; i ++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
                    for(int j = 0 ; j < jLegs.length() ; j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        for(int k = 0 ; k < jSteps.length() ; k ++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            for(int l = 0 ; l < list.size() ; l ++){
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;

        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }




    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            // traversing through routes
            points = new ArrayList<LatLng>();
            for (int i = 0; i < routes.size(); i++) {

                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                break;
            }

            if (points.size()>0) {
                showPolyLineDirection();

              /*  if (smoothMovementThread.getStatus()!= AsyncTask.Status.RUNNING)
                    smoothMovementThread.execute();*/
               /* if (directionPointsDB==null)
                    directionPointsDB = new RideDirectionPointsDB(getActivity());

                directionPointsDB.saveDirectionPoints(points);*/
            }

        }
    }

    Polyline initial_polyline = null;
    private void showPolyLineDirection()
    {
        PolylineOptions polyLineOptions = null;
        polyLineOptions = new PolylineOptions();

        polyLineOptions.addAll(points);
        polyLineOptions.width(5);
        polyLineOptions.color(ContextCompat.getColor(getActivity(),R.color.theme_primary));
        polyLineOptions.startCap(new SquareCap());
        polyLineOptions.endCap(new SquareCap());
        polyLineOptions.jointType(JointType.ROUND);

        if (polyLineOptions!=null) {
             initial_polyline = mMap.addPolyline(polyLineOptions);
        }
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
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

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    class SmoothMovementThread extends AsyncTask<Boolean, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Boolean... params) {

            try {
                startSmoothMovement();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            isMoving = false;
            Toast.makeText(getActivity(),"Done", Toast.LENGTH_LONG).show();
        }
    }

    boolean isMoving = false;
    ArrayList<MemberLocationObject> members_saved = null;
    private void startSmoothMovement() {
        if (isMoving)
            return;

        isMoving = true;
        String mem_id = members_saved.get(0).getMem_id();
        String mem_name = members_saved.get(0).getMem_name();
        for (int i=0;i<points.size();i++)
        {
            String lat =points.get(i).latitude+""; //intent.getStringExtra(LocationListnerServices.SERIVICE_LATITUDE);
            String lng =points.get(i).longitude+""; //intent.getStringExtra(LocationListnerServices.SERIVICE_LONGITUDE);
            Log.e("location_changed",lat+"-"+lng);


            final MemberLocationObject member = new MemberLocationObject(mem_id, mem_name, "driver", lat + "", lng + "");
            member.setCurrent_job("21");

            String key = mem_id + "_member";
            if (!mem_id.equalsIgnoreCase("")) {
                FirebaseDatabase.getInstance().getReference().child("members").child(key).setValue(member);
                Log.e("member_position","member position updated on firebase");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }





}
