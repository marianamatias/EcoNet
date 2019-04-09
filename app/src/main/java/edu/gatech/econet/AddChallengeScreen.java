package edu.gatech.econet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.kosalgeek.android.caching.FileCacher;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class AddChallengeScreen extends AppCompatActivity {

    Button challengeButton;
    String [] challengerList = new String[] {"Billy","Natasha","Dean","Russo"};
    String [] topicList = ForumTopicSelect.localTopic;
    public static String topicSelected;
    public static String challengerSelected;
    public static String keyUserSelected;
    FileCacher<String []> challengedUserCacher;
    FileCacher<String []> challengeedTopicCacher;
    FileCacher<String []> challengedStatusCacher;
    FileCacher<String []> challengedKeysUserCacher;
    FileCacher<String []> userIDCacher;
    String [] challengedUsers = new String[] {};
    String [] getChallengedUsers = new String [] {};
    String [] challengedTopic = new String[] {};
    String [] challengesStatus = new String[] {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge_screen);
        //Set up the size of the screen
        // Dimensions are hardcoded.... how to do that ??
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width= dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(1000,1200);
        challengeButton=(Button)findViewById(R.id.challengeButton);
        userIDCacher = new FileCacher<>(AddChallengeScreen.this, "userId.txt");
        challengedUserCacher = new FileCacher<>(AddChallengeScreen.this, "challengedUserCacher.txt");
        challengeedTopicCacher = new FileCacher<>(AddChallengeScreen.this, "challengeedTopicCacher.txt");
        challengedStatusCacher = new FileCacher<>(AddChallengeScreen.this, "challengedStatusCacher.txt");
        challengedKeysUserCacher = new FileCacher<>(AddChallengeScreen.this, "challengeedKeysUserCacher.txt");
        if (challengedUserCacher.hasCache()){
            try{
                challengedUsers=challengedUserCacher.readCache();
                challengedTopic=challengeedTopicCacher.readCache();
                challengesStatus=challengedStatusCacher.readCache();
                getChallengedUsers = challengedKeysUserCacher.readCache();
            } catch (IOException e ){
                e.printStackTrace();
            }
        }

        final ArrayAdapter<String> topicAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,topicList);
        MaterialBetterSpinner topicSpinner=(MaterialBetterSpinner)findViewById(R.id.topicScroll);
        topicSpinner.setAdapter(topicAdapter);
        topicSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                topicSelected = topicList[i];
            }
        });
        final ArrayAdapter<String> challengerAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,challengerList);
        MaterialBetterSpinner challengerSpinner=(MaterialBetterSpinner)findViewById(R.id.challengerScroll);
        challengerSpinner.setAdapter(challengerAdapter);
        challengerSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                challengerSelected = challengerList[i];
            }
        });

        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Methods.isInArray(challengedTopic,topicSelected)){
                    Intent intent = new Intent(getApplicationContext(), ManageChallenge.class);
//                    try {
//                        challengedUsers=Methods.increaseArray(challengedUsers,challengerSelected);
//                        challengedTopic=Methods.increaseArray(challengedTopic,topicSelected);
//                        challengedUserCacher.writeCache(challengedUsers);
//                        challengeedTopicCacher.writeCache(challengedTopic);
//                    } catch (IOException e){
//                        e.printStackTrace();
//                    }
                    try {
                        quitActivity();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //create same template as in habit tracker
                    //check previous activity
                    startActivity(intent);
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Already challenged topic",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

    }
    public void quitActivity() throws IOException {
        challengedUsers=challengedUserCacher.readCache();
        challengedTopic=challengeedTopicCacher.readCache();
        challengesStatus=challengedStatusCacher.readCache();
        getChallengedUsers = challengedKeysUserCacher.readCache();
        JSONObject challengedata = new JSONObject();
        keyUserSelected="123123";
        //keyUserSelected=WelcomeActivity.keysList[Methods.find(WelcomeActivity.userIDList,challengerSelected)];
        RequestParams rp3 = new RequestParams();
        //Log.d("salut","The length before writting is "+keysList.length);
        //get the key of the user before writing it !
        rp3.put("api_key", "blurryapikeyseetutorial");
        rp3.put("param", "update");
        rp3.put("wanted","challenge");
        rp3.put("userID",userIDCacher.readCache());
        try {
            //Adding every task as an item like : idtask {scoring + frequency}
            for (int i=0;i<getChallengedUsers.length;i++){
                JSONObject itemChall = new JSONObject();
                itemChall.put("topic", topicSelected);
                itemChall.put("status", "Pending");
                challengedata.put(getChallengedUsers[i],itemChall);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rp3.put("challenge", URLEncoder.encode(challengedata.toString()));
//        rp3.put("challenge",URLEncoder.encode(challengedata.toString()));
//        rp3.put("followedQuestion", URLEncoder.encode(questionsdata.toString()));
        String URlprofile = "http://www.fir-auth-93d22.appspot.com/user?" + rp3.toString();
        Log.d("salut",rp3.toString());
        HttpUtils.patchByUrl(URlprofile, rp3, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    Log.d("salut",serverResp.toString());

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                try {
                    challengedUserCacher.writeCache(challengedUsers);
                    challengeedTopicCacher.writeCache(challengedTopic);
                    challengedStatusCacher.writeCache(challengesStatus);
                    challengedKeysUserCacher.writeCache(getChallengedUsers);

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
