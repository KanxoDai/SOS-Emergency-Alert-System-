package com.example.capstoneproject;

import static com.example.capstoneproject.MainActivity.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.capstoneproject.Location.BackgroundLocationService_Girls;

import com.example.capstoneproject.model.Place;
import com.example.capstoneproject.model.PlaceUserRating;
import com.example.capstoneproject.ui.SplashScreenActivity;
import com.example.capstoneproject.utils.AppController;
import com.example.capstoneproject.utils.GoogleApiUrl;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FirstPage extends AppCompatActivity {
    CarouselView carouselView;
    float x1, x2, y1, y2;
    private String mCurrentPlaceDetailUrl;
    private String mCurrentPlaceDetailUrl2;
    private String mCurrentPlaceDetailUrl22;
    private String mCurrentPlaceDetailUrl11;

    SharedPreferences sharedPreferences;

    int[] sampleImages = {R.drawable.w1, R.drawable.w4, R.drawable.w5, R.drawable.w3};

    String currentPlaceId;
    String currentPlaceId2;
    LocationManager locationManager;
    String latitude, longitude;
    Double computed;
    Double computed1;
    Double computed2;
    Double computed22;
    private ArrayList<Double> locationlist= new ArrayList <Double>();
    private ArrayList<Double> locationlist2= new ArrayList <Double>();

    double lat;
    double longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
        startBackgroundLocationService_Girls();

        sharedPreferences=getSharedPreferences("PHONENO", Context.MODE_PRIVATE);

        // for finding hospitals name,and distance between my location.
        String locationName = "Hospital";

        String currentLocation = getSharedPreferences(
                GoogleApiUrl.CURRENT_LOCATION_SHARED_PREFERENCE_KEY, 0)
                .getString(GoogleApiUrl.CURRENT_LOCATION_DATA_KEY, null);

        String locationQueryStringUrl = GoogleApiUrl.BASE_URL + GoogleApiUrl.NEARBY_SEARCH_TAG + "/" +
                GoogleApiUrl.JSON_FORMAT_TAG + "?" + GoogleApiUrl.LOCATION_TAG + "=" +
                currentLocation + "&" + GoogleApiUrl.RANK_BY_TAG + "=" + GoogleApiUrl.DISTANCE_TAG +
                "&" + GoogleApiUrl.KEYWORD_TAG + "=" + locationName.replace(" ", "%20") + "&" +
                GoogleApiUrl.API_KEY_TAG + "=" + GoogleApiUrl.API_KEY;
        getLocationDetails(locationQueryStringUrl);


        //for finding police station name and distance between my location.
        String locationName2="Police";
        String locationQueryStringUrl2 = GoogleApiUrl.BASE_URL + GoogleApiUrl.NEARBY_SEARCH_TAG + "/" +
                GoogleApiUrl.JSON_FORMAT_TAG + "?" + GoogleApiUrl.LOCATION_TAG + "=" +
                currentLocation + "&" + GoogleApiUrl.RANK_BY_TAG + "=" + GoogleApiUrl.DISTANCE_TAG +
                "&" + GoogleApiUrl.KEYWORD_TAG + "=" +locationName2.replace(" ", "%20") + "&" +
                GoogleApiUrl.API_KEY_TAG + "=" + GoogleApiUrl.API_KEY;
        getLocationDetails2(locationQueryStringUrl2);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (locationGPS != null) {
            lat = locationGPS.getLatitude();
            longi = locationGPS.getLongitude();
            latitude = String.valueOf(lat);
            longitude = String.valueOf(longi);

            System.out.println("My lat:"+lat);
            System.out.println("My long:"+longi);
        }
    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
/*                if (x1 < x2) {
                    Intent i = new Intent(FirstPage.this, Fastaccess.class);
                    startActivity(i);
                }*/
                if (x1 > x2) {
                    Intent i = new Intent(FirstPage.this, MainActivity2.class);
                    startActivity(i);
                }
                //sweep up ward
                if (y1 > y2) {
                    Intent i = new Intent(FirstPage.this, SplashScreenActivity.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(sampleImages[position]);
        }
    };

    private void startBackgroundLocationService_Girls() {

        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, BackgroundLocationService_Girls.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                FirstPage.this.startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.capstoneproject.services.BackgroundLocationService_Girls".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    // for finding hospital and all other details
    private void getLocationDetails(String locationQueryStringUrl) {
        String jsonArrayTag = "jsonArrayTag";
        JsonObjectRequest placeJsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                locationQueryStringUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray rootJsonArray = response.getJSONArray("results");
                            for (int i = 0; i < rootJsonArray.length(); i++) {
                                JSONObject singlePlaceJsonObject = (JSONObject) rootJsonArray.get(i);

                                currentPlaceId = singlePlaceJsonObject.getString("place_id");

                                Double currentPlaceLatitude = singlePlaceJsonObject
                                        .getJSONObject("geometry").getJSONObject("location")
                                        .getDouble("lat");

                                Double currentPlaceLongitude = singlePlaceJsonObject
                                        .getJSONObject("geometry").getJSONObject("location")
                                        .getDouble("lng");

                                String currentPlaceName = singlePlaceJsonObject.getString("name");

                                //Printing the data of Hospitals
                                System.out.println("Name of Hospital is :" + currentPlaceName);
                                System.out.println("Current Hospital ID is :" + currentPlaceId);
                                System.out.println("Current Hospital Latitude is :" + currentPlaceLatitude);
                                System.out.println("Current Hospital Longitude :" + currentPlaceLongitude);


                                distance(lat,longi, currentPlaceLongitude,currentPlaceLatitude);


                                locationlist.add(computed);
                                int e=locationlist.size();

                                System.out.println("size is :"+e);
                                System.out.println("available Hospital locations are:"+locationlist);

                                Double minimim=locationlist.get(0);

                                for(int l=0; l<e; l++){
                                    if (minimim > locationlist.get(i))
                                    {
                                        minimim=locationlist.get(i);
                                    }
                                }

                                System.out.println("Minimim distance is:"+minimim);

                                if(computed == minimim)
                                {
                                    //print the hospital data which is nearest to the victim
                                    System.out.println("Shortest distance Hospital is :"+computed + " " +"KM ");
                                    System.out.println("Shortest distance Hospital Name is :" + currentPlaceName);
                                    System.out.println("Shortest distance Hospital ID is :" + currentPlaceId);

                                    // Get nearest hospital id
                                    String id=currentPlaceId;

                                    //for finding the contacts and other details of the particular place of selected locationName.
                                    //assigning the nearest place id

                                    String currentPlaceDetailId = id;
                                    mCurrentPlaceDetailUrl = GoogleApiUrl.BASE_URL + GoogleApiUrl.LOCATION_DETAIL_TAG + "/" +
                                            GoogleApiUrl.JSON_FORMAT_TAG + "?" + GoogleApiUrl.LOCATION_PLACE_ID_TAG + "=" +
                                            currentPlaceDetailId + "&" + GoogleApiUrl.API_KEY_TAG + "=" + GoogleApiUrl.API_KEY;
                                    getCurrentPlaceAllDetails(mCurrentPlaceDetailUrl);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.getMessage());
                    }
                });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(placeJsonObjectRequest, jsonArrayTag);
    }
    //for finding the nearest hospital phone no
    private void getCurrentPlaceAllDetails(final String currentPlaceDetailUrl) {
        String jsonArrayTag = "jsonArrayTag";
        JsonObjectRequest placeJsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                currentPlaceDetailUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject rootJsonObject = response.getJSONObject("result");


                            String currentPlaceId1 = rootJsonObject.getString("place_id");


                            Double currentPlaceLatitude = rootJsonObject
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            Double currentPlaceLongitude = rootJsonObject
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");


                            String currentPlaceName = rootJsonObject.getString("name");

                            String currentPlaceAddress = rootJsonObject.has("formatted_address") ?
                                    rootJsonObject.getString("formatted_address") :
                                    "Address Not Available";
                            String currentPlacePhoneNumber = rootJsonObject
                                    .has("international_phone_number") ? rootJsonObject
                                    .getString("international_phone_number") :
                                    "Phone Number Not Registered";

                            //printing the nearest hospital details
                            System.out.println("Current Hospital Phone No is:" + currentPlacePhoneNumber);
                            System.out.println("Current Hospital Phone No Name is:" + currentPlaceName);
                            System.out.println("Current Hospital Phone No Address is:" + currentPlaceAddress);


                            //Run if the previous phone no is not registered

                            Double minimimsecond=0.0;

                            if(currentPlacePhoneNumber.equals("Phone Number Not Registered"))
                            {
                                System.out.println("Phone no is not registered");
                                minimimsecond = locationlist.get(1);
                                System.out.println("Second smallest is 1:" + minimimsecond);


                                distance11(lat, longi, currentPlaceLongitude, currentPlaceLatitude);


                                String idsecond = currentPlaceId;

                                String currentPlaceDetailId = idsecond;

                                mCurrentPlaceDetailUrl11 = GoogleApiUrl.BASE_URL + GoogleApiUrl.LOCATION_DETAIL_TAG + "/" +
                                        GoogleApiUrl.JSON_FORMAT_TAG + "?" + GoogleApiUrl.LOCATION_PLACE_ID_TAG + "=" +
                                        currentPlaceDetailId + "&" + GoogleApiUrl.API_KEY_TAG + "=" + GoogleApiUrl.API_KEY;
                                getCurrentPlaceAllDetails(mCurrentPlaceDetailUrl11);


                                System.out.println("secondminimum hospital distance is:" + idsecond);
                                System.out.println("secondminimum hospital name is:" + currentPlaceName);

                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("SHORTESTHOSPITAL", currentPlacePhoneNumber);
                                editor.commit();

                                SharedPreferences.Editor editorhospital=sharedPreferences.edit();
                                editorhospital.putString("HOSPITALLATITUDE", String.valueOf(currentPlaceLatitude));
                                editorhospital.commit();
                                SharedPreferences.Editor editorhospital1=sharedPreferences.edit();
                                editorhospital1.putString("HOSPITALLONGITUDE", String.valueOf(currentPlaceLongitude ));
                                editorhospital1.commit();
                                SharedPreferences.Editor editorhospitalname=sharedPreferences.edit();
                                editorhospitalname.putString("HOSPITALNAME",currentPlaceName);
                                editorhospitalname.commit();
                                SharedPreferences.Editor editorhospitalphone=sharedPreferences.edit();
                                editorhospitalphone.putString("HOSPITALPHONE",currentPlacePhoneNumber);
                                editorhospitalphone.commit();
                                SharedPreferences.Editor userlocationlat=sharedPreferences.edit();
                                userlocationlat.putString("USERLATITUDE", String.valueOf(lat));
                                userlocationlat.commit();
                                SharedPreferences.Editor userlocationlong=sharedPreferences.edit();
                                userlocationlong.putString("USERLONGITUDE", String.valueOf(longi));
                                userlocationlong.commit();

                            }
                            else
                            {
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("SHORTESTHOSPITAL", currentPlacePhoneNumber);
                                editor.commit();
                                SharedPreferences.Editor editorhospital=sharedPreferences.edit();
                                editorhospital.putString("HOSPITALLATITUDE", String.valueOf(currentPlaceLatitude));
                                editorhospital.commit();
                                SharedPreferences.Editor editorhospital1=sharedPreferences.edit();
                                editorhospital1.putString("HOSPITALLONGITUDE", String.valueOf(currentPlaceLongitude));
                                editorhospital1.commit();
                                SharedPreferences.Editor editorhospitalname=sharedPreferences.edit();
                                editorhospitalname.putString("HOSPITALNAME",currentPlaceName);
                                editorhospitalname.commit();
                                SharedPreferences.Editor editorhospitalphone=sharedPreferences.edit();
                                editorhospitalphone.putString("HOSPITALPHONE",currentPlacePhoneNumber);
                                editorhospitalphone.commit();

                                SharedPreferences.Editor userlocationlat=sharedPreferences.edit();
                                userlocationlat.putString("USERLATITUDE", String.valueOf(lat));
                                userlocationlat.commit();

                                SharedPreferences.Editor userlocationlong=sharedPreferences.edit();
                                userlocationlong.putString("USERLONGITUDE", String.valueOf(longi));
                                userlocationlong.commit();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.getMessage());
                    }
                });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(placeJsonObjectRequest, jsonArrayTag);
    }

    private double distance(double lat, double longi, double currentPlaceLongitude, double currentPlaceLatitude) {
        LatLng user= new LatLng(lat,longi);
        LatLng nearbylocations = new LatLng( currentPlaceLatitude, currentPlaceLongitude);
        Double distance;
        distance = SphericalUtil.computeDistanceBetween(user, nearbylocations);
        computed=distance/1000;
        System.out.println("Distance Compute is:"+computed);
        return computed;

    }
    private double distance11(double lat, double longi, double currentPlaceLongitude, double currentPlaceLatitude) {
        LatLng user= new LatLng(lat,longi);
        LatLng nearbylocations = new LatLng( currentPlaceLatitude, currentPlaceLongitude);
        Double distance;
        distance = SphericalUtil.computeDistanceBetween(user, nearbylocations);
        computed1=distance/1000;
        System.out.println("Distance Compute is:"+computed1);
        return computed1;

    }

    //start section for finding police stations and nearest police station contacts details
    private void getLocationDetails2(String locationQueryStringUrl2) {
        //Tag to cancel request
        String jsonArrayTag = "jsonArrayTag";
        JsonObjectRequest placeJsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                locationQueryStringUrl2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray rootJsonArray = response.getJSONArray("results");
                            for (int i = 0; i < rootJsonArray.length(); i++) {
                                JSONObject singlePlaceJsonObject = (JSONObject) rootJsonArray.get(i);

                                currentPlaceId2 = singlePlaceJsonObject.getString("place_id");
                                Double currentPlaceLatitude2 = singlePlaceJsonObject
                                        .getJSONObject("geometry").getJSONObject("location")
                                        .getDouble("lat");
                                Double currentPlaceLongitude2 = singlePlaceJsonObject
                                        .getJSONObject("geometry").getJSONObject("location")
                                        .getDouble("lng");
                                String currentPlaceName2 = singlePlaceJsonObject.getString("name");


                                //printing the data of policestation available
                                System.out.println("Name of PoliceStation is :" + currentPlaceName2);
                                System.out.println("Current PoliceStation ID is :" + currentPlaceId2);
                                System.out.println("Current PoliceStation  Latitude is :" + currentPlaceLatitude2);
                                System.out.println("Current PoliceStation Longitude :" + currentPlaceLongitude2);

                                distance2(lat,longi, currentPlaceLongitude2,currentPlaceLatitude2);

                                locationlist2.add(computed2);
                                int e=locationlist2.size();

                                System.out.println("police array size is : "+e);
                                System.out.println("available police locations are"+locationlist2);

                                Double minimim2=locationlist2.get(0);
                                for(int l=0; l<e; l++){
                                    if (minimim2 > locationlist2.get(i))
                                    {
                                        minimim2=locationlist2.get(i);
                                    }
                                }
                                System.out.println("Police Minimim distance is:"+minimim2);


                                if(computed2 == minimim2)
                                {

                                    //print the police data which is nearest to the victim
                                    System.out.println("Shortest distance to police is :"+computed2 + " " +"KM ");
                                    System.out.println("Shortest distance to police name is :" + currentPlaceName2);
                                    System.out.println("Shortest distance to police name ID is :" + currentPlaceId2);


                                    // Get nearest police id
                                    String id2=currentPlaceId2;

                                    //for finding the contacts and other details of the particular place of selected locationName.

                                    String currentPlaceDetailId2 = id2;
                                    mCurrentPlaceDetailUrl2 = GoogleApiUrl.BASE_URL + GoogleApiUrl.LOCATION_DETAIL_TAG + "/" +
                                            GoogleApiUrl.JSON_FORMAT_TAG + "?" + GoogleApiUrl.LOCATION_PLACE_ID_TAG + "=" +
                                            currentPlaceDetailId2 + "&" + GoogleApiUrl.API_KEY_TAG + "=" + GoogleApiUrl.API_KEY;
                                    getCurrentPlaceAllDetails2(mCurrentPlaceDetailUrl2);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.getMessage());
                    }
                });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(placeJsonObjectRequest, jsonArrayTag);
    }


    //for finding the phone number of nearest police station
    private void getCurrentPlaceAllDetails2(final String currentPlaceDetailUrl2) {
        String jsonArrayTag = "jsonArrayTag";
        JsonObjectRequest placeJsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                currentPlaceDetailUrl2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject rootJsonObject = response.getJSONObject("result");


                            Double currentPlaceLatitude2 = rootJsonObject
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            Double currentPlaceLongitude2 = rootJsonObject
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");


                            String currentPlaceName2 = rootJsonObject.getString("name");



                            String currentPlaceAddress2 = rootJsonObject.has("formatted_address") ?
                                    rootJsonObject.getString("formatted_address") :
                                    "Address Not Available";
                            String currentPlacePhoneNumber2 = rootJsonObject
                                    .has("international_phone_number") ? rootJsonObject
                                    .getString("international_phone_number") :
                                    "Phone Number Not Registered";


                            //printing the data of nearest police station
                            System.out.println("Current Police Place Phone No is:"+currentPlacePhoneNumber2);
                            System.out.println("Current Police Place Phone No Name is:"+currentPlaceName2);
                            System.out.println("Current Police Place Phone No Address is:"+currentPlaceAddress2);


                            Double minimimsecond2=0.0;

                            if(currentPlacePhoneNumber2.equals("Phone Number Not Registered")) {
                                System.out.println("Police Phone no is not registered");
                                minimimsecond2 = locationlist2.get(1);
                                System.out.println("Second smallest police distance is 1:" + minimimsecond2);

                                distance22(lat, longi, currentPlaceLongitude2, currentPlaceLatitude2);

                                String idsecond = currentPlaceId2;

                                String currentPlaceDetailId = idsecond;

                                mCurrentPlaceDetailUrl22 = GoogleApiUrl.BASE_URL + GoogleApiUrl.LOCATION_DETAIL_TAG + "/" +
                                        GoogleApiUrl.JSON_FORMAT_TAG + "?" + GoogleApiUrl.LOCATION_PLACE_ID_TAG + "=" +
                                        currentPlaceDetailId + "&" + GoogleApiUrl.API_KEY_TAG + "=" + GoogleApiUrl.API_KEY;
                                getCurrentPlaceAllDetails2(mCurrentPlaceDetailUrl22);

                                System.out.println("secondminimum police distance is:" + idsecond);
                                System.out.println("secondminimum police name is:" + currentPlaceName2);

                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("SHORTESTPOLICE", currentPlacePhoneNumber2);
                                editor.commit();

                                SharedPreferences.Editor editorpolice=sharedPreferences.edit();
                                editorpolice.putString("POLICELATITUDE", String.valueOf(currentPlaceLatitude2));
                                editorpolice.commit();

                                SharedPreferences.Editor editorpolice1=sharedPreferences.edit();
                                editorpolice1.putString("POLICELONGITUDE", String.valueOf(currentPlaceLongitude2));
                                editorpolice1.commit();

                                SharedPreferences.Editor editorpolicename=sharedPreferences.edit();
                                editorpolicename.putString("POLICENAME",currentPlaceName2);
                                editorpolicename.commit();

                                SharedPreferences.Editor editorpolicphone=sharedPreferences.edit();
                                editorpolicphone.putString("POLICEPHONE",currentPlacePhoneNumber2);
                                editorpolicphone.commit();

                                SharedPreferences.Editor userlocationlat=sharedPreferences.edit();
                                userlocationlat.putString("USERLATITUDE", String.valueOf(lat));
                                userlocationlat.commit();

                                SharedPreferences.Editor userlocationlong=sharedPreferences.edit();
                                userlocationlong.putString("USERLONGITUDE", String.valueOf(longi));
                                userlocationlong.commit();



                            }else{

                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("SHORTESTPOLICE",currentPlacePhoneNumber2);
                                editor.commit();

                                SharedPreferences.Editor editorpolice=sharedPreferences.edit();
                                editorpolice.putString("POLICELATITUDE", String.valueOf(currentPlaceLatitude2));
                                editorpolice.commit();

                                SharedPreferences.Editor editorpolice1=sharedPreferences.edit();
                                editorpolice1.putString("POLICELONGITUDE", String.valueOf(currentPlaceLongitude2));
                                editorpolice1.commit();

                                SharedPreferences.Editor editorpolicename=sharedPreferences.edit();
                                editorpolicename.putString("POLICENAME",currentPlaceName2);
                                editorpolicename.commit();

                                SharedPreferences.Editor editorpolicphone=sharedPreferences.edit();
                                editorpolicphone.putString("POLICEPHONE",currentPlacePhoneNumber2);
                                editorpolicphone.commit();

                                SharedPreferences.Editor userlocationlat=sharedPreferences.edit();
                                userlocationlat.putString("USERLATITUDE", String.valueOf(lat));
                                userlocationlat.commit();

                                SharedPreferences.Editor userlocationlong=sharedPreferences.edit();
                                userlocationlong.putString("USERLONGITUDE", String.valueOf(longi));
                                userlocationlong.commit();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.getMessage());
                    }
                });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(placeJsonObjectRequest, jsonArrayTag);
    }
    private double distance2(double lat, double longi, double currentPlaceLongitude, double currentPlaceLatitude) {

        LatLng user= new LatLng(lat,longi);
        LatLng nearbylocations = new LatLng( currentPlaceLatitude, currentPlaceLongitude);
        Double distance;
        distance = SphericalUtil.computeDistanceBetween(user, nearbylocations);
        computed2=distance/1000;
        System.out.println("Distance Compute is:"+computed2);
        return computed2;

    }

    private double distance22(double lat, double longi, double currentPlaceLongitude, double currentPlaceLatitude) {

        LatLng user= new LatLng(lat,longi);
        LatLng nearbylocations = new LatLng( currentPlaceLatitude, currentPlaceLongitude);
        Double distance;
        distance = SphericalUtil.computeDistanceBetween(user, nearbylocations);
        computed22=distance/1000;
        System.out.println("Distance Compute is:"+computed22);
        return computed22;

    }
}