package edu.gatech.econet;

import android.os.CountDownTimer;
import android.os.Handler;
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
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    EditText firstnameText;
    EditText usernameText;
    public static String userID;
    FileCacher<String> userIDCacher;
    FileCacher<String []> taskCacher;
    FileCacher<String []> keyCacher;
    FileCacher<String []> topicCacher;
    FileCacher<String []> scoreCacher;
    FileCacher<String []> freqCacher;
    FileCacher<String []> challengeCacher;
    FileCacher<String []> challengesRunningCacher;
    FileCacher<String []> challengedStatusCacher;
    FileCacher<String []> challengedKeysUserCacher;
    FileCacher<String []> challengedUserCacher;
    FileCacher<String []> challengeedTopicCacher;
    public static String usernameUser;
    public static String firstnameUser;
    //If task list detected
    public static String[] getTaskKeysListUser = new String[] {};
    public static String[] getTaskScoreListUser = new String[] {};
    public static String[] getTaskFreqListUser = new String[] {};
    //If challenge list detected
    public static String[] getChallengersIDListUser = new String[] {};
    public static String[] getChallengersStatusListUser = new String[] {};
    public static String[] getChallengersTopicListUser = new String[] {};
    //If followed question list detected
    public static String[] getFollowedQuestionIDListUser = new String[] {};
    public static String[] getFollowedQuestionLastViewListUser = new String[] {};
    public static String [] tasksList = new String[]{};
    public  static String[] topicList = new String[]{};
    // [START declare_auth]
    public FirebaseAuth mAuth;
    public static  String signed = "No";
    // [END declare_auth]

    public GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //By matching the taskretrieved and the tasklist find topic and ID
        taskCacher = new FileCacher<>(MainActivity.this, "taskCacher.txt");
        topicCacher = new FileCacher<>(MainActivity.this, "topicCacher.txt");
        //From the tasklist of the database
        scoreCacher = new FileCacher<>(MainActivity.this, "scoreCacher.txt");
        keyCacher = new FileCacher<>(MainActivity.this, "keys.txt");
        freqCacher = new FileCacher<>(MainActivity.this, "frequency.txt");
        //From the challengelist of the database
        challengeCacher = new FileCacher<>(MainActivity.this, "challengeCacher.txt");
        challengesRunningCacher = new FileCacher<>(MainActivity.this, "challengeedTopicCacher.txt");
        challengeCacher = new FileCacher<>(MainActivity.this, "challengeCacher.txt");
        challengedUserCacher = new FileCacher<>(MainActivity.this, "challengedUserCacher.txt");
        challengeedTopicCacher = new FileCacher<>(MainActivity.this, "challengeedTopicCacher.txt");
        challengedStatusCacher = new FileCacher<>(MainActivity.this, "challengedStatusCacher.txt");
        challengedKeysUserCacher = new FileCacher<>(MainActivity.this, "challengeedKeysUserCacher.txt");
        try {
            taskCacher.clearCache();
            topicCacher.clearCache();
            scoreCacher.clearCache();
            keyCacher.clearCache();
            freqCacher.clearCache();
            challengeCacher.clearCache();
            challengedKeysUserCacher.clearCache();
            challengedStatusCacher.clearCache();
            challengedUserCacher.clearCache();
            challengeedTopicCacher.clearCache();
            challengesRunningCacher.clearCache();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //firstnameText = (EditText) findViewById(R.id.firstnameText);
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
        try {
            updateUI(currentUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                try {
                    updateUI(null);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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
                            try {
                                updateUI(user);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            try {
                                updateUI(null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
                        try {
                            updateUI(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) throws IOException {
        if ((user != null)) {
            //Need to create a new user because no email in the database
            String mail = mAuth.getCurrentUser().getEmail();
            if (signed.equals("No")&&(!Methods.isInArray(WelcomeActivity.firstnameList,mail))){
                //Success in log in so post of the new user into the database

                Toast.makeText(getApplicationContext(),"I am creating a new account",Toast.LENGTH_LONG).show();
                try {
                    taskCacher.clearCache();
                    topicCacher.clearCache();
                    scoreCacher.clearCache();
                    keyCacher.clearCache();
                    freqCacher.clearCache();
                    challengeCacher.clearCache();
                    challengedKeysUserCacher.clearCache();
                    challengedStatusCacher.clearCache();
                    challengedUserCacher.clearCache();
                    challengeedTopicCacher.clearCache();
                    challengesRunningCacher.clearCache();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject test = new JSONObject();
                RequestParams rp = new RequestParams();
                rp.put("api_key", "blurryapikeyseetutorial");
                rp.put("param", "new");
                try {
                    test.put("e-mail",mAuth.getCurrentUser().getEmail()); //get the mail here);
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
                            userID = serverResp.getString("name");
                            userIDCacher.writeCache(userID);
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
            else {
                Toast.makeText(getApplicationContext(),"Mail already in database",Toast.LENGTH_LONG).show();
                if (WelcomeActivity.userIDList.length!=0){
                    String mail2 = mAuth.getCurrentUser().getEmail();
                    userID = WelcomeActivity.userIDList[Methods.find(WelcomeActivity.firstnameList,mail2)];
                    Log.d("salut","my mail is "+mail2+" "+userID);
                    userIDCacher.writeCache(userID);
                }
            }
            try {
                taskCacher.clearCache();
                topicCacher.clearCache();
                scoreCacher.clearCache();
                keyCacher.clearCache();
                freqCacher.clearCache();
                challengeCacher.clearCache();
                challengedKeysUserCacher.clearCache();
                challengedStatusCacher.clearCache();
                challengedUserCacher.clearCache();
                challengeedTopicCacher.clearCache();
                challengesRunningCacher.clearCache();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Retrieve the profile of the user if mail is in database
            JSONObject data = new JSONObject();
            RequestParams rp3 = new RequestParams();
            rp3.put("api_key", "blurryapikeyseetutorial");
            rp3.put("param", "myaccount");
            rp3.put("wanted","all");
            try {
                data.put("ID", userID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            rp3.put("data", data.toString());
            String URl = "http://www.fir-auth-93d22.appspot.com/user?" + rp3.toString();
            HttpUtils.getByUrl(URl, rp3, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        JSONObject serverResp = new JSONObject(response.toString());
                        usernameUser = serverResp.getString("username");
                        firstnameUser = serverResp.getString("e-mail");
                        if (serverResp.has("tasklist")){
                            //Need to parse all the tasks, the scoring, the topics, the frequency
                            JSONObject myTaskList = serverResp.getJSONObject("tasklist");
                            Iterator<String> keysTaskList = myTaskList.keys();
                            while(keysTaskList.hasNext()) {
                                String keyTask = keysTaskList.next();
                                getTaskKeysListUser = Methods.increaseArray(getTaskKeysListUser,keyTask);
                                if (myTaskList.get(keyTask) instanceof JSONObject) {
                                    JSONObject item2 = myTaskList.getJSONObject(keyTask);
                                    getTaskScoreListUser = Methods.increaseArray(getTaskScoreListUser, item2.getString("scoring"));
                                    getTaskFreqListUser = Methods.increaseArray(getTaskFreqListUser, item2.getString("frequency"));
                                }
                            }
                        }
                        if(!serverResp.has("tasklist")) {
                            Log.d("salut","No task detected");
                        }
                        if (serverResp.has("challenge")){
                            //Need to parse challenges, status, challengers, tasks
                            JSONObject myChallengeList = serverResp.getJSONObject("challenge");
                            Iterator<String> keysChallengeList = myChallengeList.keys();
                            while(keysChallengeList.hasNext()) {
                                String keyChallenge = keysChallengeList.next();
                                getChallengersIDListUser = Methods.increaseArray(getChallengersIDListUser,keyChallenge);
                                if (myChallengeList.get(keyChallenge) instanceof JSONObject) {
                                    JSONObject item = myChallengeList.getJSONObject(keyChallenge);
                                    getChallengersStatusListUser = Methods.increaseArray(getChallengersStatusListUser, item.getString("status"));
                                    getChallengersTopicListUser = Methods.increaseArray(getChallengersTopicListUser, item.getString("topic"));
                                }
                            }
                        }

                        if(!serverResp.has("challenge")) {
                            Log.d("salut","No challenge detected");
                        }
                        if (serverResp.has("followed")){
                            //Need to parse followedquestion, id, lastview
                            JSONObject myQuestionList = serverResp.getJSONObject("challenge");
                            Iterator<String> keysQuestionList = myQuestionList.keys();
                            while(keysQuestionList.hasNext()) {
                                String keyQuestion = keysQuestionList.next();
                                getFollowedQuestionIDListUser = Methods.increaseArray(getFollowedQuestionIDListUser,keyQuestion);
                                if (myQuestionList.get(keyQuestion) instanceof JSONObject) {
                                    JSONObject item = myQuestionList.getJSONObject(keyQuestion);
                                    getFollowedQuestionLastViewListUser = Methods.increaseArray(getFollowedQuestionLastViewListUser, item.getString("last"));
                                }
                            }
                        }
                        if(!serverResp.has("followed")) {
                            Log.d("salut","No followed question detected");
                        }
                        try {
                            scoreCacher.writeCache(getTaskScoreListUser);
                            keyCacher.writeCache(getTaskKeysListUser);
                            freqCacher.writeCache(getTaskFreqListUser);
                            for (int i=0;i<getTaskKeysListUser.length;i++){
                                tasksList=Methods.increaseArray(tasksList,WelcomeActivity.taskList[Methods.find(WelcomeActivity.keysList,getTaskKeysListUser[i])]);
                                topicList=Methods.increaseArray(topicList,WelcomeActivity.topicList[Methods.find(WelcomeActivity.keysList,getTaskKeysListUser[i])]);
                                Log.d("salut","I just wrote "+tasksList[i]);
                            }
                            taskCacher.writeCache(tasksList);
                            topicCacher.writeCache(topicList);
                        } catch (IOException e){
                            e.printStackTrace();
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }

            });

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
//        if ((i == R.id.signInButton)&&(!Methods.isInArray(WelcomeActivity.usernameList,usernameText.getText().toString()))) {
//            signIn();
//        }
//        else if ((Methods.isInArray(WelcomeActivity.usernameList,usernameText.getText().toString()))&&
//                (Methods.isInArray(WelcomeActivity.firstnameList,firstnameText.getText().toString()))){
//            userID = WelcomeActivity.userIDList[Methods.find(WelcomeActivity.usernameList,usernameText.getText().toString())];
//            try {
//                userIDCacher.writeCache(userID);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            retrieveProfile();
//            signed = "Yes";
//            signIn();
//        }
//        else {
//            Toast.makeText(getApplicationContext(),"Please enter a firstname and username before login",Toast.LENGTH_LONG).show();
//        }
        signIn();
    }
    public void retrieveProfile(){
        //on get the profile
    }
    public void waitResponse(){
        new Handler().postDelayed(new Runnable() {
            public void run(){

            }
        },3000);
    }
}
