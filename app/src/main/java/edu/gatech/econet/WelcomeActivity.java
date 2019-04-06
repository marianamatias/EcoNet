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

        JSONObject test = new JSONObject ();
        RequestParams rp = new RequestParams();
        rp.put("api_key","blurryapikeyseetutorial");
        try {
            rp.put("data", URLEncoder.encode("1","UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                        Log.d("salut",keysList[i]);
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
        if (user=="Yes"){
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
