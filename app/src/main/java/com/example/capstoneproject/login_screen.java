package com.example.capstoneproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstoneproject.NewSplashSOS.SOSSplash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class login_screen extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin;
    Toast toast;
    TextView toast_text;
    Typeface toast_font;
    LayoutInflater inflater;
    View layout;
    public TextView textViewRegister,textView1;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_login_screen);

        ImageView changeLang= findViewById(R.id.changelanguage);

        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });

        //Custom Toast

        // toast_font = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextLTPro-Cn.otf");
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
        btnLogin = (Button) findViewById(R.id.btnLogin);
        textViewRegister = (TextView)findViewById(R.id.textViewRegister);
        textView1 = (TextView) findViewById(R.id.textView1);


        //Changing font of all layout components
        /*Typeface custom_font = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-UltLtCn.otf");
        editTextEmail.setTypeface(custom_font);
        editTextPassword.setTypeface(custom_font);
        textViewRegister.setTypeface(custom_font);
        btnLogin.setTypeface(custom_font, Typeface.BOLD);
        textView1.setTypeface(custom_font, Typeface.BOLD);*/

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        auto();
    }

    public void login(View view)
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            toast_text.setText("No Email Entered");
            toast.show();
            return;
        }

        if (TextUtils.isEmpty(password))
        {
            toast_text.setText("No Password Entered");
            toast.show();
            return;
        }

        progressDialog.setMessage("Logging In...");
        progressDialog.show();

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
                               // auto();
                                Intent i = new Intent(login_screen.this, SOSSplash.class);
                                startActivity(i);
                                Toast.makeText(login_screen.this, "Login successfully", Toast.LENGTH_SHORT).show();


                            }
                            else{
                                user.sendEmailVerification();
                                Toast.makeText(login_screen.this, "Check your Email for verification, may be in Spam folder", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            Toast.makeText(login_screen.this, "Incorrect Login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goToRegister(View view)
    {
        finish();
        startActivity(new Intent(this,personal_info.class));
    }
    public void forgotpw(View view)
    {
        finish();
        startActivity(new Intent(this,forgotpassword.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showChangeLanguageDialog() {
        //array of languages to display in alert dialog
        final String[] listenItems ={"हिन्दी",""+"English"};

        AlertDialog.Builder mbuilder = new AlertDialog.Builder(login_screen.this);
        mbuilder.setTitle("Choose Language");
        mbuilder.setSingleChoiceItems(listenItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if(i == 0) {
                    //french
                    setLocale("hi");
                    recreate();
                }
                else if(i == 1) {
                    //english
                    setLocale("en");
                    recreate();
                }

                //dismiss alert dialog when language selected
                dialog.dismiss();

            }
        });

        mbuilder.create();
        //show alert dialog
        mbuilder.show();

    }

    private void setLocale(String lang) {
        Locale locale =new Locale(lang);
        Locale.setDefault(locale);
        Configuration config= new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext()
                .getResources().getDisplayMetrics());

        //save data to shared preference
        SharedPreferences.Editor editor= getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("my lang", lang);
        editor.apply();

    }
    //load language saved in shared preferences
    public void loadLocale(){
        SharedPreferences prefs= getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language= prefs. getString("my lang", " ");
        setLocale(language);
    }

    public void auto() {
             if (firebaseAuth.getCurrentUser() != null) {
            // Start Dashboard Activity
            finish();
            startActivity(new Intent(this, SOSSplash.class));
        }
    }
}