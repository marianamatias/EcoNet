package edu.gatech.econet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;


//package com.google.firebase.quickstart.auth.java;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
//import com.google.firebase.quickstart.auth.R;
import android.content.Context;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kosalgeek.android.caching.FileCacher;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    EditText firstnameText;
    EditText usernameText;
    public static String userID;
    FileCacher<String> userIDCacher;


    // [START declare_auth]
    public FirebaseAuth mAuth;
    public static  String signed = "No";
    // [END declare_auth]

    public GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstnameText = (EditText) findViewById(R.id.firstnameText);
        usernameText = (EditText) findViewById(R.id.usernameText);
        userIDCacher = new FileCacher<>(MainActivity.this, "userId.txt");
        if (signed.equals("No")){
            try {
                userIDCacher.clearCache();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        // Button listeners
        findViewById(R.id.signInButton).setOnClickListener(this);
//        findViewById(R.id.signOutButton).setOnClickListener(this);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1057246002930-8bp2uv0v2sjesp7iin4dkcp35uv3vlas.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //listen for google's sign in button to register user
        findViewById(R.id.signInButton).setOnClickListener(this);

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    public void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (signed.equals("No")) {
                //Success in log in so post of the new user into the database
                JSONObject test = new JSONObject();
                RequestParams rp = new RequestParams();
                rp.put("api_key", "blurryapikeyseetutorial");
                rp.put("param", "new");
                try {
                    test.put("firstname", firstnameText.getText().toString());
                    test.put("username", usernameText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rp.put("data", URLEncoder.encode(test.toString()));
                String URl = "http://www.fir-auth-93d22.appspot.com/user?" + rp.toString();
                HttpUtils.postByUrl(URl, rp, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            JSONObject serverResp = new JSONObject(response.toString());
                            //JSONObject item = serverResp.getJSONObject("name");
                            userID = serverResp.getString("name");
                            userIDCacher.writeCache(userID);
                            //Log.d("salut",serverResp.toString());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                        // Log.d("salut","received array");
                    }
                });
                signed = "Yes";
            }
            //switch screens
            Intent intent = new Intent(this, habitTracker.class);
            intent.putExtra("FROM_ACTIVITY", "MainActivity");
            startActivity(intent);
        } else {
            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if ((i == R.id.signInButton)&&(!usernameText.getText().toString().equals(""))&&(!firstnameText.getText().toString().equals(""))
        && (!Methods.isInArray(WelcomeActivity.usernameList,usernameText.getText().toString()))) {
            signIn();
        }
        else if ((Methods.isInArray(WelcomeActivity.usernameList,usernameText.getText().toString()))&&
                (Methods.isInArray(WelcomeActivity.firstnameList,firstnameText.getText().toString()))){
            userID = WelcomeActivity.userIDList[Methods.find(WelcomeActivity.usernameList,usernameText.getText().toString())];
            try {
                userIDCacher.writeCache(userID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            retrieveProfile();
            signed = "Yes";
            signIn();
        }
        else {
            Toast.makeText(getApplicationContext(),"Please enter a firstname and username before login",Toast.LENGTH_LONG).show();
        }
    }
    public void retrieveProfile(){
        //on get the profile
    }
}
