/*
package com.example.capstoneproject;

import static com.example.capstoneproject.MainActivity.editor;
import static com.example.capstoneproject.MainActivity.preferences;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.capstoneproject.Location.BackgroundLocationService_Girls;
import com.example.capstoneproject.Notification.notification_generator;
import com.example.capstoneproject.Retrofit.ApiClient;
import com.example.capstoneproject.model.RetrofitModel.PlaceResult;
import com.example.capstoneproject.model.RetrofitModel.safeLocation;
import com.example.capstoneproject.model.location_model;
import com.example.capstoneproject.model.person_details;
import com.example.capstoneproject.services.foreground_service;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class action_screen extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String NAME = "name";
    public static final String VICINITY = "vicinity";
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;

    private String safe_location_FLAG;
    private LocationManager locationManager;

    private Button alertButton;
    private LinearLayout mapButton;
    private TextView locationText;
    private ImageView gps_icon;
    private LinearLayout policeStationButton;
    private LinearLayout railwayStationButton;
    private LinearLayout airportButton;
    private LinearLayout mallButton;

    private Boolean location_permission_granted = false;

    private GoogleMap mMap;
    private Location myLocation;
    PlaceResult placeResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_screen);


        placeResults = new PlaceResult();

        init();
        requestLocationPermission();


        gps_icon.setOnClickListener(view -> getLocation());

        policeStationButton.setOnClickListener(view -> {
            String type = "police";
            safe_location_FLAG = type;
            getNearByPlaces(type);
            Toast.makeText(getApplicationContext(), "police", Toast.LENGTH_LONG).show();
        });

        railwayStationButton.setOnClickListener(view -> {
            String type = "railway";
            safe_location_FLAG = type;
            getNearByPlaces(type);
            Toast.makeText(getApplicationContext(), "Railway Station", Toast.LENGTH_LONG).show();
        });

        airportButton.setOnClickListener(view -> {
            String type = "airport";
            safe_location_FLAG = type;
            getNearByPlaces(type);
            Toast.makeText(getApplicationContext(), "Airport", Toast.LENGTH_LONG).show();
        });

        mapButton.setOnClickListener(view -> {
            String type = "hospital";
            safe_location_FLAG = type;
            getNearByPlaces(type);
            Toast.makeText(getApplicationContext(), "Hospital", Toast.LENGTH_LONG).show();
        });

    }

    private void init() {


        alertButton = findViewById(R.id.alertButton);
        locationText = findViewById(R.id.locationText);
        gps_icon = findViewById(R.id.action_screen_gps_icon);
        policeStationButton = findViewById(R.id.policeStationBtn);
        railwayStationButton = findViewById(R.id.railwayStationBtn);
        airportButton = findViewById(R.id.AirportBtn);
        mapButton = findViewById(R.id.MallBtn);

    }

    private void initMap() {

        Log.d(TAG, "initMap: initialising map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_actionScreen);
        mapFragment.getMapAsync(action_screen.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: map is ready");
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();


        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (!(marker.getTag() == null)) {
                    popup_window(marker.getTag().toString());
                }
                return false;
            }
        });


        if (location_permission_granted) {
            getLocation();
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
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

        }

    }


    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        Intent intent=new Intent(action_screen.this,MainActivity2.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                location_permission_granted = false;
                Toast.makeText(getApplicationContext(), "Application will not run without location permission",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                location_permission_granted = true;
                initMap();
            }
        }
    }

    private void requestLocationPermission() {

        Log.d(TAG, "requestLocationPermission: requesting location permission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                location_permission_granted = true;
                initMap();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(getApplicationContext(), "Application required to location permission", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            initMap();
        }

    }

    void getLocation() {

        Log.d(TAG, "getLocation: fetching location");

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e(TAG, "getLocation: " + e.getMessage());
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {

        myLocation = location;
        locationText.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());

        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM, "My Location");

        location_model location_data = new location_model();

        location_data.setLatitude(String.valueOf(location.getLatitude()));
        location_data.setLongitude(String.valueOf(location.getLongitude()));


        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            locationText.setText(locationText.getText() + "\n" + addresses.get(0).getAddressLine(0) + "\n"
                    + addresses.get(0).getAddressLine(1) + "\n" + addresses.get(0).getAddressLine(2));

        } catch (Exception e) {

            e.printStackTrace();
            Log.d(TAG, "OnLocationChanged: error" + e.getMessage());

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Please enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {

        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(markerOptions);
        }
    }

    private void getNearByPlaces(String type) {

        ApiClient apiClient = new ApiClient();

        Call<PlaceResult> call = apiClient.getApiinterface().getData(type, "ZSskAK5jn2K7T6stJoJ1fyDXGDChSZuE", String.valueOf(myLocation.getLatitude()), String.valueOf(myLocation.getLongitude()));
        call.enqueue(new Callback<PlaceResult>() {
            @Override

            public void onResponse(Call<PlaceResult> call, Response<PlaceResult> response) {

                if (response.isSuccessful()) {
                    placeResults = response.body();
                    setMaker(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<PlaceResult> call, Throwable t) {

            }
        });
    }

    private void setMaker(ArrayList<safeLocation> result) {

        mMap.clear();
        //addAllPerson();

        for (int a = 0; a < result.size(); a++) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(result.get(a).getPosition().getLat(), result.get(a).getPosition().getLon());
            markerOptions.position(latLng);
            markerOptions.title(result.get(a).getAddress().getStreetName() + " " + result.get(a).getAddress().getCountrySecondarySubDivision() + " " + result.get(a).getAddress().getMunicipality() + " " + result.get(a).getAddress().getCountrySecondarySubDivision() + " " + result.get(a).getAddress().getPostalCode());
            if (safe_location_FLAG == "police") {
                Marker marker = mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_security)));
                marker.setTag(String.valueOf(a));
            } else if (safe_location_FLAG == "railway") {
                Marker marker = mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_train)));
                marker.setTag(String.valueOf(a));
            } else if (safe_location_FLAG == "airport") {
                Marker marker = mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_airportblue)));
                marker.setTag(String.valueOf(a));
                //edited mall to hospital

            } else if (safe_location_FLAG == "hospital") {
                Marker marker = mMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_store_mall)));
                marker.setTag(String.valueOf(a));
            }
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public void popup_window(String counter) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.police_info_popup);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextView name = dialog.findViewById(R.id.action_name);
        TextView address = dialog.findViewById(R.id.action_address);
        TextView rating = dialog.findViewById(R.id.action_rating);
        TextView person = dialog.findViewById(R.id.action_total_no_person);

        name.setText(placeResults.getResults().get(Integer.parseInt(counter)).getPoi().getName());
        address.setText(placeResults.getResults().get(Integer.parseInt(counter)).getAddress().getStreetName() + " " + placeResults.getResults().get(Integer.parseInt(counter)).getAddress().getCountrySecondarySubDivision() + " " + placeResults.getResults().get(Integer.parseInt(counter)).getAddress().getMunicipality() + " " + placeResults.getResults().get(Integer.parseInt(counter)).getAddress().getCountrySecondarySubDivision() + " " + placeResults.getResults().get(Integer.parseInt(counter)).getAddress().getPostalCode());
        rating.setText("4");
        person.setText("1451");

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);


    }
    public  void back(View view)
    {
        onBackPressed();
    }

}*/
