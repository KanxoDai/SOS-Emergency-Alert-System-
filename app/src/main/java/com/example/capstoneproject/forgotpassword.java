package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button respw;
    private EditText editTextrsemail;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        mAuth = FirebaseAuth.getInstance();
        editTextrsemail = (EditText) findViewById(R.id.editemail);

        progressDialog = new ProgressDialog(this);
        respw=(Button) findViewById(R.id.btnreset);

        respw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                respw();
            }
        });
    }
    private void respw() {

        String email1 = editTextrsemail.getText().toString().trim();

        if (email1.isEmpty()) {
           // editTextrsemail.setError("Email is required");
            //editTextrsemail.requestFocus();
            //return;
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()) {
            //editTextrsemail.setError("Enter a valid email");
            //editTextrsemail.requestFocus();
            //return;
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();


        } else {
            progressDialog.setMessage("Processing..");
            progressDialog.show();

            mAuth.sendPasswordResetEmail(email1).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Intent i = new Intent(forgotpassword.this, login_screen.class);
                        startActivity(i);
                        Toast.makeText(forgotpassword.this, "Reset password link is send to email", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(forgotpassword.this, "Failed..Enter registered Email or Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(forgotpassword.this, login_screen.class);
        startActivity(intent);
    }
}