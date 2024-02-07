package com.example.capstoneproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class send_sms extends Activity {

    String hospitalphonenumber;
    String policephonenumber;

    String policelati;
    String policelongi;
    String policenam;

    String hospitallati;
    String hospitallongi;
    String hospitalnam;

    String userlongitude;
    String userlatitude;

    private GPSHandler mGPSHandler;
    private SmsManager mSmsManager;
    private DBEmergency mDatabase;
    private CountDownTimer timer;
    private MediaPlayer mediaPlayeralarm;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String username = "";

    private Button buttonSend, buttonCancel;
    private TextView textViewPhoneNumber, textViewName, textViewTimer, textViewEmergencyMessage;

    private String phoneNumberArray[];
    private List<String> hospitals;
    private ArrayList<EmerContact> add = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        CustomToastActivity.CustomToastActivity(this);

        // Create required objects
        mGPSHandler = new GPSHandler(this);
        mDatabase = new DBEmergency(this);
        mSmsManager = SmsManager.getDefault();
        mediaPlayeralarm = MediaPlayer.create(this, R.raw.rising_swoops);
        hospitals = mGPSHandler.getHospitalAddress();

        setupFirebase();
        setupUI();
        setupTimer();

        SharedPreferences sp= getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
        hospitalphonenumber=sp.getString("SHORTESTHOSPITAL","");

        SharedPreferences hospitallatitute= getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
        hospitallati=hospitallatitute.getString("HOSPITALLATITUDE","");

        SharedPreferences hospitallongitude= getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
        hospitallongi=hospitallongitude.getString("HOSPITALLONGITUDE","");

        SharedPreferences hospitalname= getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
        hospitalnam=hospitalname.getString("HOSPITALNAME","");



        SharedPreferences sp1= getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
        policephonenumber=sp1.getString("SHORTESTPOLICE","");

        SharedPreferences policelatitute= getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
        policelati=policelatitute.getString("POLICELATITUDE","");

        SharedPreferences policelongitude= getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
        policelongi=policelongitude.getString("POLICELONGITUDE","");

        SharedPreferences policename= getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
        policenam=policename.getString("POLICENAME","");

        SharedPreferences userlongitude1= getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
        userlongitude=userlongitude1.getString("USERLONGITUDE","");

        SharedPreferences userlatitude1= getApplicationContext().getSharedPreferences("PHONENO", Context.MODE_PRIVATE);
        userlatitude=userlatitude1.getString("USERLATITUDE","");

    }

    public void cancelAlarm(View view) {
        CustomToastActivity.showCustomToast("Alarm was cancelled");
        mediaPlayeralarm.pause();
        timer.cancel();
        finish();
    }

    public void sendButtonPress(View view) {
        sendSMSMessage();
    }

    /*
     * Private Functions
     */
    private void setupUI() {
        String email = firebaseUser.getEmail();
        List<EmerContact> contact;

        textViewTimer = (TextView) findViewById(R.id.timer);
        textViewEmergencyMessage = (TextView) findViewById(R.id.emergencymessage);
        textViewPhoneNumber = (TextView) findViewById(R.id.phone1);
        textViewName = (TextView) findViewById(R.id.name1);
        buttonSend = (Button) findViewById(R.id.send);
        buttonCancel = (Button) findViewById(R.id.cancel);

        contact = mDatabase.getContact(email);
        for (EmerContact cn : contact) {
            add.add(cn);
        }

        phoneNumberArray = new String[3];
        String nameArray[] = new String[3];
        String names = "", phoneNumbers = "";
        for (int i = 0; i < add.size(); i++){
            EmerContact emerContact = add.get(i);
            nameArray[i] = emerContact._name;
            phoneNumberArray[i] = emerContact._phone;
            names = names + nameArray[i] + '\n';
            phoneNumbers = phoneNumbers + phoneNumberArray[i]+'\n';
        }

        textViewName.setText(names);
        textViewPhoneNumber.setText(phoneNumbers);

        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        ImageView alarm = (ImageView) findViewById(R.id.imageView2);
        alarm.startAnimation(animShake);

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
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();
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

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(send_sms.this, "Could not retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTimer() {
        timer = new CountDownTimer(10000, 1000) {//timer set garne sms ko

            public void onTick(long millisUntilFinished) {
                textViewTimer.setText("seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
                mediaPlayeralarm.start();

                // send.setEnabled(true);
            }

            public void onFinish() {
                sendSMSMessage();

            }
        };

        timer.start();
    }

    /* Function to send a alert text message */
    private void sendSMSMessage() {
        // Stop the media and alarm
        mediaPlayeralarm.pause();
        timer.cancel();

        // Need to divide the message for the SMS handler to handle a long message
        String message = constructMessage();

        String message1 = constructMessage1();

        textViewEmergencyMessage.setText(message);

        ArrayList<String> dividedMessage = mSmsManager.divideMessage(message);

        ArrayList<String> dividedMessage1 = mSmsManager.divideMessage(message1);
        ArrayList<String> dividedMessage2 = mSmsManager.divideMessage(message1);

//        mSmsManager.sendMultipartTextMessage(hospitalphonenumber.replaceAll("[-() ]", "")
//                , null, dividedMessage1, null, null);
//
//
//        mSmsManager.sendMultipartTextMessage(policephonenumber.replaceAll("[-() ]", "")
//                , null, dividedMessage2, null, null);

        // Need to send message to all emergency contacts
        for (int i=0; i < add.size(); i++) {
            try {
                mSmsManager.sendMultipartTextMessage(phoneNumberArray[i].replaceAll("[-() ]", "") // Need to remove special characters
                        , null, dividedMessage, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, "Message Send Successfully \n To Personal Saved Contacts \n Nearest Police Station and \n Nearest Hospital ", Toast.LENGTH_SHORT).show();
        finish();
    }

    /* Function to put together the text message */
    private String constructMessage() {

        String userlocaton="http://maps.google.com/?q="+  userlatitude+","+userlongitude;
        String message = "ALERT! It appears that the " + username
                + "  may have been in an emergency. " +  username
                + "  has chosen you as their emergency contact. " + username
                + "'s current location is " + userlocaton + ".";

//                + " "+" Notified Hospital name is " + hospitalnam +"."
//                + " "+" Notified Hospital location is " +"http://maps.google.com/?q=" + hospitallati+ "," + hospitallongi +"."
//                + " "+" Notified Hospital Phone number is " + hospitalphonenumber +"."
//
//
//                + " "+" Notified Police Station name is " + policenam +"."
//                + " "+" Notified Police Station location is " +"http://maps.google.com/?q=" + policelati + "," +policelongi +"."
//                + " "+" Notified Police Phone number is " + policephonenumber +".";


        return message;
    }

    private String constructMessage1() {

        String userlocaton="http://maps.google.com/?q="+  userlatitude+","+userlongitude;

        String message = "ALERT! It appears that the " + username
                + " may have been in an emergency. " +  username
                + " has chosen you as their emergency contact. " + username
                + "'s current location is " +userlocaton;

     return message;
    }

}