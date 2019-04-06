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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class WelcomeActivity extends AppCompatActivity {
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
        RequestParams rp = new RequestParams();
        rp.put("api_key","blurryapikeyseetutorial");
        String URl = "http://www.fir-auth-93d22.appspot.com/task?"+rp.toString();
        HttpUtils.getByUrl(URl, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode,headers,response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    Iterator<String> keys = serverResp.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        keysList = Methods.increaseArray(keysList,key);
                        if (serverResp.get(key) instanceof JSONObject) {
                            JSONObject item = serverResp.getJSONObject(key);
                            taskList = Methods.increaseArray(taskList, item.getString("taskName"));
                            topicList = Methods.increaseArray(topicList, item.getString("topicName"));
                            freqList = Methods.increaseArray(freqList,item.getString("freq"));
                        }
                    }
                    for (int i=0;i<keysList.length;i++){
                        //Log.d("salut",keysList[i]);
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
//Retrieve all the users of the database
        RequestParams rp2 = new RequestParams();
        rp2.put("api_key","blurryapikeyseetutorial");
        rp2.put("param","all");
        String URl2 = "http://www.fir-auth-93d22.appspot.com/user?"+rp.toString();
        HttpUtils.getByUrl(URl2, rp2, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode,headers,response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    //Log.d("salut",serverResp.toString());
                    Iterator<String> keys2 = serverResp.keys();
                    while(keys2.hasNext()) {
                        String key = keys2.next();
                        userIDList = Methods.increaseArray(userIDList,key);
                        if (serverResp.get(key) instanceof JSONObject) {
                            JSONObject item = serverResp.getJSONObject(key);
                            usernameList = Methods.increaseArray(usernameList, item.getString("username"));
                            firstnameList = Methods.increaseArray(firstnameList, item.getString("firstname"));
                        }
                    }
                    for (int i=0;i<usernameList.length;i++){
                        Log.d("salut",usernameList[i]);
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
        String user = MainActivity.signed;
        //If the cache contains an userID then the account is retrieved and we can move on the habit tracker
        //Otherwise the user has to log in or sign up
        if (userIDCacher.hasCache()){
        //if (user=="Yes"){
            timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override public void run(){
                    Intent intent = new Intent(WelcomeActivity.this,habitTracker.class);
                    intent.putExtra("FROM_ACTIVITY", "WelcomeActivity");
                    startActivity(intent);
                }
            }, 2000);
        }
        else{
            timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override public void run(){
                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }, 2000);
        }

    }
}
