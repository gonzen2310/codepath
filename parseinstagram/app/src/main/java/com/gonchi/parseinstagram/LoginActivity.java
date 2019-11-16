package com.gonchi.parseinstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    ConstraintLayout view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        } 

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btLogin);
        btnSignup = findViewById(R.id.btSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                login(username, password);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signup(username, password);
            }
        });
    }

    private void signup(String username, String password) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(username + "@email.com");
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with signup");
                    e.printStackTrace();
                    view = findViewById(R.id.loginContainer);
                    Snackbar.make(view, "Oosp! Something went wrong", Snackbar.LENGTH_LONG).show();
                    return;
                }
                goToMainActivity();
            }
        });
    }

    private void login(String username, String password) {
        Log.e(TAG, "U " + username + " P " + password);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login");
                    e.printStackTrace();
                    view = findViewById(R.id.loginContainer);
                    Snackbar.make(view, "Oosp! Something went wrong", Snackbar.LENGTH_LONG).show();
                    return;
                }
                // Navigate to new activity
                goToMainActivity();
            }
        });
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
