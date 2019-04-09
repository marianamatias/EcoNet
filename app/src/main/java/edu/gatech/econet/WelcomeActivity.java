package edu.gatech.econet;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.caching.FileCacher;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class WelcomeActivity extends AppCompatActivity {

    public interface Callback<T> {
        void onResponse(T response) throws JSONException;
    }
    Timer timer;
    TextView welcome;
    public static String[] topicList = new String[] {};
    public static String[] taskList = new String[]{};
    public static String[] freqList = new String[]{};
    public static String[] keysList = new String[]{};
    public static String[] usernameList = new String[]{};
    public static String[] firstnameList = new String[] {};
    public static String[] userIDList = new String[]{};
    FileCacher<String> userIDCacher;
    //Information retrieved from the API it user already logged in
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        String[] choiceWelcome;
        welcome = (TextView) findViewById(R.id.welcomeText);
        choiceWelcome = new String[] {"Join the community of EcoWarriors","Invite your friends to help the Econet grow","Cannot find inspiration ? Look at new topics !","Discover the eco-world and much more","Save our world, fill your feed.","Ever thought about changing your way of living ?"};
        Random random = new Random();
        int pos = random.nextInt(choiceWelcome.length);
        welcome.setText(choiceWelcome[pos]);
        userIDCacher = new FileCacher<>(WelcomeActivity.this, "userId.txt");

//Retrieve all the tasks
        retriveAllTask(new Callback<JSONObject>() {
            public void onResponse(JSONObject serverResp) throws JSONException {
                Iterator<String> keys = serverResp.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    keysList = Methods.increaseArray(keysList, key);
                    if (serverResp.get(key) instanceof JSONObject) {
                        JSONObject item = serverResp.getJSONObject(key);
                        taskList = Methods.increaseArray(taskList, item.getString("taskName"));
                        topicList = Methods.increaseArray(topicList, item.getString("topicName"));
                        freqList = Methods.increaseArray(freqList, item.getString("freq"));
                    }
                }
            }
        });

//Retrieve all the users of the database
        retriveAllProfiles(new Callback<JSONObject>() {
            public void onResponse(JSONObject serverResp2) throws JSONException {
                    Iterator<String> keys2 = serverResp2.keys();
                    while(keys2.hasNext()) {
                        String key = keys2.next();
                        userIDList = Methods.increaseArray(userIDList,key);
                        if (serverResp2.get(key) instanceof JSONObject) {
                            JSONObject item = serverResp2.getJSONObject(key);
                            usernameList = Methods.increaseArray(usernameList, item.getString("username"));
                            firstnameList = Methods.increaseArray(firstnameList, item.getString("e-mail"));
                        }
                    }
                    for (int i=0;i<usernameList.length;i++){
                    }
            }
        });


        String user = MainActivity.signed;
        //If the cache contains an userID then the account is retrieved and we can move on the habit tracker
        //Otherwise the user has to log in or sign up
        if (userIDCacher.hasCache()){
            retriveMyProfile(new Callback<JSONObject>() {
                public void onResponse(JSONObject serverResp2) throws JSONException {
                        usernameUser = serverResp2.getString("username");
                        firstnameUser = serverResp2.getString("e-mail");
                        if (serverResp2.has("tasklist")){
                            //Need to parse all the tasks, the scoring, the topics, the frequency
                            JSONObject myTaskList = serverResp2.getJSONObject("tasklist");
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
                        if(!serverResp2.has("tasklist")) {
                            Log.d("salut","No task detected");
                        }
                        if (serverResp2.has("challenge")){
                            //Need to parse challenges, status, challengers, tasks
                            JSONObject myChallengeList = serverResp2.getJSONObject("challenge");
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

                        if(!serverResp2.has("challenge")) {
                            Log.d("salut","No challenge detected");
                        }
                        if (serverResp2.has("followed")){
                            //Need to parse followedquestion, id, lastview
                            JSONObject myQuestionList = serverResp2.getJSONObject("challenge");
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
                        if(!serverResp2.has("followed")) {
                            Log.d("salut","No followed question detected");
                        }
                }
            });
            timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override public void run(){

                    Intent intent = new Intent(WelcomeActivity.this,habitTracker.class);
                    intent.putExtra("FROM_ACTIVITY", "WelcomeActivity");
                    startActivity(intent);
                }
            }, 3000);
        }
        else{
            timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override public void run(){
                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }, 3000);
        }

    }
    public void retriveAllTask(final Callback<JSONObject> callback) {
        RequestParams rp = new RequestParams();
        rp.put("api_key", "blurryapikeyseetutorial");
        String URl = "http://www.fir-auth-93d22.appspot.com/task?" + rp.toString();
        HttpUtils.getByUrl(URl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (callback != null) {
                        callback.onResponse(response);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Log.d("salut","received array");
            }
        });
    }
    public void retriveAllProfiles(final Callback<JSONObject> callback) {
        RequestParams rp2 = new RequestParams();
        rp2.put("api_key","blurryapikeyseetutorial");
        rp2.put("param","all");
        String URl2 = "http://www.fir-auth-93d22.appspot.com/user?"+rp2.toString();
        HttpUtils.getByUrl(URl2, rp2, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (callback != null) {
                        callback.onResponse(response);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Log.d("salut","received array");
            }
        });
    }

    public void retriveMyProfile(final Callback<JSONObject> callback) {
        JSONObject data = new JSONObject();
        RequestParams rp3 = new RequestParams();
        rp3.put("api_key", "blurryapikeyseetutorial");
        rp3.put("param", "myaccount");
        rp3.put("wanted","all");
        try {
            data.put("ID", userIDCacher.readCache());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        rp3.put("data", data.toString());
        String URlprofile = "http://www.fir-auth-93d22.appspot.com/user?" + rp3.toString();
        HttpUtils.getByUrl(URlprofile, rp3, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (callback != null) {
                        callback.onResponse(response);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Log.d("salut","received array");
            }
        });
    }
}
