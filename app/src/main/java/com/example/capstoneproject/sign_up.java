package com.example.capstoneproject;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;


public class sign_up extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[@#$%^&+=])" +     // at least 1 special character
                    "(?=\\S+$)" +            // no white spaces
                    ".{8,}" +                // at least 8 characters
                    "$");

    private EditText editTextEmail, editTextPassword,editTextPassword2;
    private Button buttonRegister,emailverifybutton;
    private Toast toast;
    private TextView toast_text;
    private Typeface toast_font;
    private LayoutInflater inflater,inflater1;
    private View layout,layout1;
    private TextView textViewTitle;



    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String firstName, lastName, phoneNumber, policyNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Bundle mBundle = getIntent().getExtras();
        firstName = mBundle.getString("firstName");
        lastName = mBundle.getString("lastName");
        phoneNumber = mBundle.getString("phoneNumber");
        policyNumber = mBundle.getString("policyNumber");

        //Custom Toast
        //  toast_font = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Cn.otf");
        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) this.findViewById(R.id.toast));
        toast_text = (TextView) layout.findViewById(R.id.tv);
        toast = new Toast(this.getApplicationContext());
        toast_text.setTypeface(toast_font);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        //Initialisation of all the components
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);
        buttonRegister = (Button) findViewById(R.id.btnRegister);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);

        //Changing font of all layout components
     /*   Typeface custom_font = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-UltLtCn.otf");
        editTextEmail.setTypeface(custom_font);
        editTextPassword.setTypeface(custom_font);
        buttonRegister.setTypeface(custom_font, Typeface.BOLD);
        textViewTitle.setTypeface(custom_font, Typeface.BOLD);*/
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


    }

    public void register(View view)
    {

        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final  String password1= editTextPassword2.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            toast_text.setText("No Email Entered");
            toast.show();
            return;
        }


        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            toast_text.setText("Invalid Email,Try again");
            toast.show();
            return;
        }

        else if (TextUtils.isEmpty(password))
        {
            toast_text.setText("No Password Entered");
            toast.show();
            return;
        }
        else if(!password.equals(password1))
        {
            toast_text.setText("Password do not match");
            toast.show();
            return;
        }
       else if(!PASSWORD_PATTERN.matcher(password).matches())
        {
            toast_text.setText("Password must contain special character \n no space \n and must be of length 8");
            toast.show();
            return;
        }

        progressDialog.setMessage("Registering...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Login details
                           // firebaseAuth.signInWithEmailAndPassword(email, password);
                         //   UserInformation userInformation = new UserInformation(firstName, lastName, policyNumber, phoneNumber);
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //databaseReference.child(user.getUid()).setValue(userInformation);
                            progressDialog.dismiss();
                            toast_text.setText("Registered Successfully");
                            toast.show();
                            go();
                           user.sendEmailVerification();
                           Toast.makeText(sign_up.this, "Check your Email for verification, may be in Spam folder", Toast.LENGTH_SHORT).show();

                           /* Intent intent= new Intent(sign_up.this, emailverification.class);
                            startActivity(intent);*/

                        } else {
                            toast_text.setText("Oops!! Try again later!");
                            toast.show();
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(sign_up.this, personal_info.class);
        startActivity(intent);
    }

    public void proceed(View view){

        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                   /* public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Start Dashboard Activity
                            toast_text.setText("Logged In!");
                            toast.show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), dashboard.class));
                        }
                        else
                        {
                            toast_text.setText("Incorrect Credentials or No Network.");
                            toast.show();
                        }
                    }*/
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

                            if(user.isEmailVerified())
                            {
                                Intent i = new Intent(sign_up.this, login_screen.class);
                                startActivity(i);
                                firebaseAuth.signInWithEmailAndPassword(email, password);
                                UserInformation userInformation = new UserInformation(firstName, lastName, policyNumber, phoneNumber);
                                databaseReference.child(user.getUid()).setValue(userInformation);

                            }
                            else{

                                Toast.makeText(sign_up.this, "Verify Your Email First", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            Toast.makeText(sign_up.this, "Incorrect Login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void go(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified())
        {
            Intent i = new Intent(sign_up.this, login_screen.class);
            startActivity(i);

        }
    }
}