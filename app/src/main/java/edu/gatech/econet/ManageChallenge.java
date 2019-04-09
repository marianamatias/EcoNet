package edu.gatech.econet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kosalgeek.android.caching.FileCacher;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class ManageChallenge extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{
    ListView listChallenge = null;
    FileCacher<String []> userIDCacher;
    FileCacher<String []> challengeCacher;
    FileCacher<String []> topicCacher;
    FileCacher<String []> scoreCacher;
    String topicChallenged[] = new String[] {};
    Button addChallenge;
    FileCacher<String []> challengedUserCacher;
    FileCacher<String []> challengeedTopicCacher;
    FileCacher<String []> challengedStatusCacher;
    FileCacher<String []> challengedKeysUserCacher;
    String [] challengedUsers = new String[] {};
    String [] getChallengedUsers = new String [] {};
    String [] challengedTopic = new String[] {};
    String [] challengesStatus = new String[] {};
    private DrawerLayout drawerLayout;
    public static String topicSelected;
    public static String challengerSelected;
    // Implement the fact that :
    //challenge can be pending then display it with no action text view
    //challenge can be acccepted and running for a week (display the time remaining until the end and button access odds
    //challenge can be received and then accepted and declined
    //if declined, remove it from both players challenge maanger screen
    // status can be : pending, received, running, finished, or tobedeleted
    public static String statusSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_challenge);
        userIDCacher = new FileCacher<>(ManageChallenge.this, "userId.txt");
        topicCacher = new FileCacher<>(ManageChallenge.this, "topicCacher.txt");
        scoreCacher = new FileCacher<>(ManageChallenge.this, "scoreCacher.txt");
        challengeCacher = new FileCacher<>(ManageChallenge.this, "challengeCacher.txt");
        challengedUserCacher = new FileCacher<>(ManageChallenge.this, "challengedUserCacher.txt");
        challengeedTopicCacher = new FileCacher<>(ManageChallenge.this, "challengeedTopicCacher.txt");
        challengedStatusCacher = new FileCacher<>(ManageChallenge.this, "challengedStatusCacher.txt");
        challengedKeysUserCacher = new FileCacher<>(ManageChallenge.this, "challengeedKeysUserCacher.txt");
        addChallenge = (Button)findViewById(R.id.addChallenge);
        listChallenge=(ListView) findViewById(R.id.listChallenge);
        final ManageChallenge.ChallengeAdapter challengeAdapter = new ManageChallenge.ChallengeAdapter();
        listChallenge.setAdapter(challengeAdapter);
        retrieveDataChallenge();
        if (challengedUserCacher.hasCache()){
            try{
                challengedUsers=challengedUserCacher.readCache();
                challengedTopic=challengeedTopicCacher.readCache();
                challengesStatus=challengedStatusCacher.readCache();
                getChallengedUsers = challengedKeysUserCacher.readCache();
                for (int i=0; i<challengedTopic.length;i++){
                    challengesStatus = Methods.increaseArray(challengesStatus,"Received");
                }
                challengeAdapter.notifyDataSetChanged();
            } catch (IOException e ){
                e.printStackTrace();
            }
        }

        listChallenge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast toast = Toast.makeText(getApplicationContext(),"Hello I am the challenge ",Toast.LENGTH_LONG);
//                toast.show();
                topicSelected=challengedTopic[position];
                challengerSelected=challengedUsers[position];
                statusSelected=challengesStatus[position];
                try {
                    quitActivity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                challengeAdapter.notifyDataSetChanged();
                Intent intent = new Intent (getApplicationContext(), ChallengeView.class);
                startActivity(intent);
            }
        });

        addChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    quitActivity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                challengeAdapter.notifyDataSetChanged();
                Intent intent = new Intent(getApplicationContext(), AddChallengeScreen.class);
                startActivity(intent);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
    }
    class ChallengeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return challengedUsers.length;
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.design_adapter_challenge_manage_activity,null);
            TextView challengeTitle = (TextView) view.findViewById(R.id.challengeTitle);
            TextView challengerID = (TextView)view.findViewById(R.id.challengerID);
            Button acceptButton = (Button) view.findViewById(R.id.acceptButton);
            Button declineButton = (Button) view.findViewById(R.id.declineButton);
            Button oddViewButton = (Button) view.findViewById(R.id.oddViewButton);
            TextView statusText = (TextView) view.findViewById(R.id.textChallengeStatus);

            challengeTitle.setText(challengedTopic[i]);
            challengerID.setText(challengedUsers[i]);
            statusText.setText(challengesStatus[i]);
            //conditions on the pending or not buttons
            if (challengesStatus[i].equals("Received")){
                statusText.setVisibility(View.GONE);
                oddViewButton.setVisibility(View.GONE);
                declineButton.setVisibility(View.VISIBLE);
                acceptButton.setVisibility(View.VISIBLE);
            }
            else if (challengesStatus[i].equals("Pending")){
                statusText.setVisibility(View.VISIBLE);
                oddViewButton.setVisibility(View.GONE);
                declineButton.setVisibility(View.GONE);
                acceptButton.setVisibility(View.GONE);
            }
            else if (challengesStatus[i].equals("Running")){
                statusText.setVisibility(View.GONE);
                oddViewButton.setVisibility(View.VISIBLE);
                declineButton.setVisibility(View.GONE);
                acceptButton.setVisibility(View.GONE);
            }
            else if (challengesStatus[i].equals("Finished")){
                statusText.setVisibility(View.GONE);
                oddViewButton.setVisibility(View.VISIBLE);
                declineButton.setVisibility(View.GONE);
                acceptButton.setVisibility(View.GONE);
            }
            //oddViewButton.setVisibility(View.GONE);
            //topicIcon.setImageResource(icons[i]);
            //topicName.setText(localTopic[i]);
            final int u=i;
            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    challengedTopic=Methods.deleteString(challengedTopic,u);
                    challengedUsers=Methods.deleteString(challengedUsers,u);
                    challengesStatus = Methods.deleteString(challengesStatus,u);
                    ManageChallenge.ChallengeAdapter challengeAdapter = new ManageChallenge.ChallengeAdapter();
                    listChallenge.setAdapter(challengeAdapter);
                }
            });
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    challengesStatus[u]="Running";
                    ManageChallenge.ChallengeAdapter challengeAdapter = new ManageChallenge.ChallengeAdapter();
                    listChallenge.setAdapter(challengeAdapter);
                }
            });
            oddViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    topicSelected=challengedTopic[u];
                    challengerSelected=challengedUsers[u];
                    statusSelected=challengesStatus[u];
                    try {
                        quitActivity();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent (getApplicationContext(), ChallengeView.class);
                    startActivity(intent);
                }
            });
            view.setForegroundGravity(Gravity.CENTER);
            //view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
            return view;
        }
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.habit_tracker){
            Intent intent = new Intent(this, habitTracker.class);
            try {
                quitActivity();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }
        if (id == R.id.add_goal){
            Intent intent = new Intent(this, AddTaskSearch.class);
            try {
                quitActivity();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }
        if (id == R.id.challenges){
            //Already in it
        }
        if (id== R.id.advice){
            Intent intent = new Intent(this, askQuestion.class);
            intent.putExtra("FROM", "habitTracker_menu");
            try {
                quitActivity();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }
        if (id == R.id.forum){
            Intent intent = new Intent(this, ForumTopicSelect.class);
            try {
                quitActivity();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }
        if (id == R.id.signOut){
            menuSignOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
//    public void quitActivity(){
//        try {
//            challengeedTopicCacher.writeCache(challengedTopic);
//            challengedUserCacher.writeCache(challengedUsers);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }
    private void menuSignOut() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1057246002930-8bp2uv0v2sjesp7iin4dkcp35uv3vlas.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        try {
                            quitActivity();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
    }
    public void quitActivity() throws IOException {
//        tasksList=taskCacher.readCache();
//        topicList=topicCacher.readCache();
//        scoreList=scoreCacher.readCache();
//        keysList=keyCacher.readCache();
//        freqList=freqCacher.readCache();
//        challengedList=challengeCacher.readCache();
//        JSONObject tasklistdata = new JSONObject();
//        JSONObject challengedata = new JSONObject();
//        JSONObject questionsdata = new JSONObject();
//        RequestParams rp3 = new RequestParams();
//        Log.d("salut","The length before writting is "+keysList.length);
//        rp3.put("api_key", "blurryapikeyseetutorial");
//        rp3.put("param", "update");
//        rp3.put("wanted","tasklist");
//        rp3.put("userID",userIDCacher.readCache());
//        try {
//            //Adding every task as an item like : idtask {scoring + frequency}
//            for (int i=0;i<keysList.length;i++){
//                JSONObject itemTask = new JSONObject();
//                itemTask.put("scoring", scoreList[i]);
//                itemTask.put("frequency", freqList[i]);
//                tasklistdata.put(keysList[i],itemTask);
//            }
//            //challengedata.put("challenge", userIDCacher.readCache());
//            //questionsdata.put("followedQuestion", userIDCacher.readCache());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        rp3.put("tasklist", URLEncoder.encode(tasklistdata.toString()));
////        rp3.put("challenge",URLEncoder.encode(challengedata.toString()));
////        rp3.put("followedQuestion", URLEncoder.encode(questionsdata.toString()));
//        String URlprofile = "http://www.fir-auth-93d22.appspot.com/user?" + rp3.toString();
//        Log.d("salut",rp3.toString());
//        HttpUtils.patchByUrl(URlprofile, rp3, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                try {
//                    JSONObject serverResp = new JSONObject(response.toString());
//                    Log.d("salut",serverResp.toString());
//
//                } catch (JSONException e1) {
//                    e1.printStackTrace();
//                }
//                try {
//                    taskCacher.writeCache(tasksList);
//                    topicCacher.writeCache(topicList);
//                    scoreCacher.writeCache(scoreList);
//                    keyCacher.writeCache(keysList);
//                    freqCacher.writeCache(freqList);
//                    challengeCacher.writeCache(challengedList);
//
//                } catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
//        });
    }
    public void retrieveDataChallenge(){
        JSONObject data = new JSONObject();
        RequestParams rp3 = new RequestParams();
        rp3.put("api_key", "blurryapikeyseetutorial");
        rp3.put("param", "myaccount");
        rp3.put("wanted","challenge");
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
                    JSONObject serverResp = new JSONObject(response.toString());
                    Log.d("salut",serverResp.toString());
                    //Need to parse all the tasks, the scoring, the topics, the frequency
                    //JSONObject myTaskList = serverResp.getJSONObject("tasklist");
                    Iterator<String> keysTaskList = serverResp.keys();
                    getChallengedUsers = new String[]{};
                    while(keysTaskList.hasNext()) {
                        String keyTask = keysTaskList.next();
                        getChallengedUsers = Methods.increaseArray(getChallengedUsers,keyTask);
                        if (serverResp.get(keyTask) instanceof JSONObject) {
                            JSONObject item2 = serverResp.getJSONObject(keyTask);
                            challengesStatus = Methods.increaseArray(challengesStatus, item2.getString("status"));
                            challengedTopic = Methods.increaseArray(challengedTopic, item2.getString("topic"));
                        }

                        Log.d("salut","the number key is "+getChallengedUsers.length);
                        challengedUsers = new String []{};
                        for (int i=0;i<getChallengedUsers.length;i++){
                            challengedUsers=Methods.increaseArray(challengedUsers,WelcomeActivity.usernameList[Methods.find(WelcomeActivity.userIDList,getChallengedUsers[i])]);
                            Log.d("salut","I just wrote "+challengedUsers[i]);
                        }
                    }
                    try {
                        challengedUserCacher.writeCache(challengedUsers);
                        challengeedTopicCacher.writeCache(challengedTopic);
                        challengedStatusCacher.writeCache(challengesStatus);
                        challengedKeysUserCacher.writeCache(getChallengedUsers);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                for (int i=0;i<getChallengedUsers.length;i++){
                    Log.d("salut","keys of the challenges are "+getChallengedUsers[i]);
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Log.d("salut","received array");
            }
        });

    }
    public void deleteTask(String selectedTaskKey) throws IOException {
//        tasksList=taskCacher.readCache();
//        topicList=topicCacher.readCache();
//        scoreList=scoreCacher.readCache();
//        keysList=keyCacher.readCache();
//        freqList=freqCacher.readCache();
//        challengedList=challengeCacher.readCache();
//        RequestParams rp3 = new RequestParams();
//        Log.d("salut","The length before writting is "+keysList.length);
//        rp3.put("api_key", "blurryapikeyseetutorial");
//        rp3.put("param", "task");
//        rp3.put("userID",userIDCacher.readCache());
//        rp3.put("taskID",selectedTaskKey);
//        String URlprofile = "http://www.fir-auth-93d22.appspot.com/user?" + rp3.toString();
//        Log.d("salut",rp3.toString());
//        HttpUtils.deleteByUrl(URlprofile, rp3, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                try {
//                    JSONObject serverResp = new JSONObject(response.toString());
//                    Log.d("salut",serverResp.toString());
//
//                } catch (JSONException e1) {
//                    e1.printStackTrace();
//                }
//
//            }
//        });
    }
}
