/*
package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.model.RetrofitModel.PlaceResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fastaccess extends AppCompatActivity {
    CarouselView carouselView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DBHelpersecond mDatabase;
    SupportMapFragment smf;
    Button button;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    ExpandableListAdapter listAdapter;
    ExpandableListView mDrawerexpList;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    int[] login_icons = new int[]{
            R.drawable.familyalert,
            R.drawable.myaccount,
            R.drawable.logout,
    };
    int[] sampleImages = {R.drawable.w11, R.drawable.w44, R.drawable.w22, R.drawable.w33};
    Toast toast;
    TextView toast_text;
    Typeface toast_font;
    LayoutInflater inflater;
    View layout2;
    private Typeface custom_font;

    FusedLocationProviderClient client;
    private GoogleMap mMap;
    private LatLng latLng;
    Double userlat, userlongi;
    private String username = "";
    private String phoneNumberArray[];
    private ArrayList<EmerContactsecond> add = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastaccess);

//        SharedPreferences sp1 = getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
//        userlat = Double.valueOf(sp1.getString("USERLATITUDE", ""));
//
//        SharedPreferences sp = getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
//        userlongi = Double.valueOf(sp.getString("USERLONGITUDE", ""));

        firebaseAuth = FirebaseAuth.getInstance();

        //calling database
        mDatabase = new DBHelpersecond(this);
        setupFirebase();

        prepareListDataSignin();
        listAdapter = new ExpandableListAdapter1(this, listDataHeader, login_icons, custom_font, custom_font);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout2 = inflater.inflate(R.layout.custom_toast, (ViewGroup) this.findViewById(R.id.toast));
        toast_text = (TextView) layout2.findViewById(R.id.tv);
        toast = new Toast(this.getApplicationContext());

        //Toast variables initialisation
        toast_text.setTypeface(toast_font);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout2);
        mTitle = mDrawerTitle = getTitle();
        mDrawerexpList = (ExpandableListView) findViewById(R.id.left_drawer);
        mDrawerexpList.setGroupIndicator(null);
        //  custom_font = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-MediumCn.otf");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);
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
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {

                        if(location !=null)
                        {
                          userlat=location.getLatitude();
                          userlongi= location.getLongitude();
                        }
            }
        });


        //set drawer expandable list adapter
        mDrawerexpList.setAdapter(listAdapter);

        //Creation of expandable listView
        mDrawerexpList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                if (groupPosition == 0) {
                    mDrawerLayout.closeDrawer(mDrawerexpList);
                }
                if (groupPosition == 1) {
                    finish();
                    Intent intent=new Intent(Fastaccess.this, my_contactsecond.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(mDrawerexpList);
                }

                if (groupPosition==2)
                {
                    try {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Fastaccess.this, login_screen.class));
                    } catch (Exception e) {
                        toast.setText("Unsuccessful");
                        toast.show();
                        e.printStackTrace();
                    }
                }
                return false;

            }
        });

        // Listview Group expanded listener
        mDrawerexpList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        // Listview Group collasped listener
        mDrawerexpList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });
        // enable ActionBar app icon to behave as action to toggle nav drawer
        final androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close)
        {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().getThemedContext();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
        button = findViewById(R.id.card1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasContact())
                {
                    sendsms();
                }
                else
                {
                    return;
                }

            }
        });
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(sampleImages[position]);
        }
    };
    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Intent intent= new Intent(Fastaccess.this, FirstPage.class);
        startActivity(intent);
    }

    // Expandable list items initialisation
    private void prepareListDataSignin() {
        listDataHeader =new  ArrayList<String>();
        //listDataChild = new HashMap<String, List<String>>();

        // Adding group data
        listDataHeader.add("Home");
        listDataHeader.add("Emergency Contacts");
        listDataHeader.add("Log Out");

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            //case R.id.search:
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    */
/**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     *//*


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    public void firstpage(View view){
        Intent intent= new Intent(Fastaccess.this, FirstPage.class);
        startActivity(intent);
    }

    private boolean hasContact() {
        String email = firebaseUser.getEmail();
        List<EmerContactsecond> contact = mDatabase.getContact(email);
        if (contact.isEmpty()) {
            //CustomToastActivity.showCustomToast("Please add at least 1 Emergency Contact");
            Toast.makeText(Fastaccess.this, "Please add at least 1 Emergency Contact", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    private void setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            finish();
            startActivity(new Intent(this, login_screen.class));
        }
        getUsername();
    }
    private void getUsername() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> values = new ArrayList<String>(4);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    values.add(child.getValue().toString());
                }

                if (!values.isEmpty()) {
                    username = values.get(0) + " " + values.get(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Fastaccess.this, "Could not retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sendsms(){

        String email = firebaseUser.getEmail();
        List<EmerContactsecond> contact;
        contact = mDatabase.getContact(email);
        for (EmerContactsecond cn : contact) {
            add.add(cn);
        }
        phoneNumberArray = new String[3];
        String nameArray[] = new String[3];
        String names = "", phoneNumbers = "";
        for (int i = 0; i < add.size(); i++){
            EmerContactsecond emerContactsecond = add.get(i);
            nameArray[i] = emerContactsecond._name;
            phoneNumberArray[i] = emerContactsecond._phone;
            names = names + nameArray[i] + '\n';
            phoneNumbers = phoneNumbers + phoneNumberArray[i]+'\n';
        }

        DBHelpersecond db = new DBHelpersecond(Fastaccess.this);
//        latLng = new LatLng(userlat,userlongi);

        String message = "EMERGENCY ALERT \n " +
                "Victim is:- "+ username + "."
                + username + " Current Location is:- "+"http://maps.google.com/?q=" + userlat + "," + userlongi;
        SmsManager mSmsManager = SmsManager.getDefault();

        ArrayList<String> dividedMessage = mSmsManager.divideMessage(message);

        // Need to send message to all emergency contacts
        for (int i=0; i < add.size(); i++) {
            try {
                mSmsManager.sendMultipartTextMessage(phoneNumberArray[i].replaceAll("[-() ]", "") // Need to remove special characters
                        , null, dividedMessage, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(Fastaccess.this, "Successfully Send Notification", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getApplicationContext(), MapActivitysecond.class);
//                    startActivity(intent);
    }
}
*/
