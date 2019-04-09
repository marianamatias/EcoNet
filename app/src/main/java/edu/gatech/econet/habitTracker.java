package edu.gatech.econet;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import java.net.*;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Button;
import android.graphics.BitmapFactory;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.kosalgeek.android.caching.FileCacher;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

//Picasso
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;


import cz.msebera.android.httpclient.Header;

public class habitTracker extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static String tasksList[] = new String[] {};
    public static String topicList[] = new String[] {};
    public static String scoreList[] = new String[] {};
    public static String challengedList[] = new String[] {};
    public static String keysList[] = new String[] {};
    public static String freqList[] = new String[] {};

    //To retrieve the data from askAdvice screen
    public static String topicSwiped ;
    public static String taskSwiped ;
    private static final String TAG = "habitTracker";
    private DrawerLayout drawerLayout;

    //MenuItem menuItem;
    TextView noTask;
    FileCacher<String []> taskCacher;
    FileCacher<String []> keyCacher;
    FileCacher<String []> topicCacher;
    FileCacher<String []> scoreCacher;
    FileCacher<String []> freqCacher;
    FileCacher<String []> challengeCacher;
    FileCacher<String []> challengesRunningCacher;
    FileCacher<String []> userIDCacher;
    SwipeMenuListView swipeListView;
    final HabitTrackerAdapter habitTrackerListAdapter= new HabitTrackerAdapter();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_tracker);
        userIDCacher = new FileCacher<>(habitTracker.this, "userId.txt");

        //setSupportActionBar(toolbar);
        //By matching the taskretrieved and the tasklist find topic and ID
        taskCacher = new FileCacher<>(habitTracker.this, "taskCacher.txt");
        topicCacher = new FileCacher<>(habitTracker.this, "topicCacher.txt");
        //From the tasklist of the database
        scoreCacher = new FileCacher<>(habitTracker.this, "scoreCacher.txt");
        keyCacher = new FileCacher<>(habitTracker.this, "keys.txt");
        freqCacher = new FileCacher<>(habitTracker.this, "frequency.txt");
        //From the challengelist of the database
        challengeCacher = new FileCacher<>(habitTracker.this, "challengeCacher.txt");
        challengesRunningCacher = new FileCacher<>(habitTracker.this, "challengeedTopicCacher.txt");
        //Retrieve the data of the user if already userID cached
        try {
            quitActivity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent mIntent = getIntent();
        //final HabitTrackerAdapter habitTrackerListAdapter = new HabitTrackerAdapter();
        swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);
        swipeListView.setAdapter(habitTrackerListAdapter);
        try{
            String previousActivity = mIntent.getStringExtra("FROM_ACTIVITY");
            if ((previousActivity.equals("ParamNewTask"))&&(!Methods.isInArray(tasksList,ParamNewTask.receivedTask))){
                tasksList=Methods.increaseArray(tasksList,ParamNewTask.receivedTask);
                topicList=Methods.increaseArray(topicList,ParamNewTask.receivedTopic);
                scoreList=Methods.increaseArray(scoreList,"0");
                challengedList=Methods.increaseArray(challengedList,"false");
                freqList=Methods.increaseArray(freqList,Integer.toString(ParamNewTask.frequency));
                keysList=Methods.increaseArray(keysList,ParamNewTask.key);
                taskCacher.writeCache(tasksList);
                topicCacher.writeCache(topicList);
                scoreCacher.writeCache(scoreList);
                keyCacher.writeCache(keysList);
                freqCacher.writeCache(freqList);
                challengeCacher.writeCache(challengedList);
                Log.d("salut","ow the keys are bigger with param new task "+keysList.length);
                retrieveData();
                quitActivity();
                habitTrackerListAdapter.notifyDataSetChanged();
            }

        }catch (Exception e){

        }
        try {
            quitActivity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        retrieveData();


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        noTask = (TextView) findViewById(R.id.no_task);
        noTask.setText("Your Habit Tracker is empty.\nClick the button to add your first task !");
        noTask.setVisibility(View.GONE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);


        if (taskCacher.hasCache()){
            try{
                tasksList=taskCacher.readCache();
                topicList=topicCacher.readCache();
                scoreList=scoreCacher.readCache();
                keysList=keyCacher.readCache();
                freqList=freqCacher.readCache();
                challengedList=challengeCacher.readCache();

                habitTrackerListAdapter.notifyDataSetChanged();
            } catch (IOException e ){
                e.printStackTrace();
            }
        }

        for (int i=0;i<tasksList.length;i++){
            scoreList = Methods.increaseArray(scoreList,"0");
            challengedList = Methods.increaseArray(challengedList,"false");
        }


//        scoreList=WelcomeActivity.getTaskScoreListUser;
//        freqList=WelcomeActivity.getTaskFreqListUser;
//        challengedList=WelcomeActivity.getChallengersIDListUser;
//        keysList=WelcomeActivity.getTaskKeysListUser;


        //Retrieve data from previous activity

//        try {
//            quitActivity();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (challengesRunningCacher.hasCache()){
            try{
                String challengesLocal [] = challengesRunningCacher.readCache();
                for (int i =0; i<challengesLocal.length;i++){
                    for (int j=0; j<topicList.length;j++){
                        if (topicList[j].equals(challengesLocal[i])){
                            challengedList[j] = "true";
                        }
                    }
                }
            } catch (IOException e ){
                e.printStackTrace();
            }
        }
//        final HabitTrackerAdapter habitTrackerListAdapter = new HabitTrackerAdapter();
//        swipeListView.setAdapter(habitTrackerListAdapter);
        setupSwipeMenuListView(swipeListView);
        swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long is) {
                try {
                    quitActivity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    scoreList=scoreCacher.readCache();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int anteScore = Integer.parseInt(scoreList[position]);
                anteScore++;
                scoreList[position]=Integer.toString(anteScore);
                try {
                    scoreCacher.writeCache(scoreList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                habitTrackerListAdapter.notifyDataSetChanged();
                //boolean anteChallenged = Boolean.parseBoolean(challengedList[position]);
                ///challengedList[position]=Boolean.toString(!anteChallenged);
                try {
                    quitActivity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    quitActivity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HabitTrackerAdapter habitTrackerListAdapter = new HabitTrackerAdapter();
                swipeListView.setAdapter(habitTrackerListAdapter);
            }
        });




        setGoogleProfilePic();

        Button addNewGoalButton = (Button) findViewById(R.id.add_button);

        addNewGoalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                nextActivity();
            }
        });



    }

    private void nextActivity() {
        Intent intent = new Intent(this, AddTaskSearch.class);
        try {
            quitActivity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.habit_tracker){
            //Already in it
            //Intent intent = new Intent(this, habitTracker.class);
            //startActivity(intent);
            try {
                Toast.makeText(getApplicationContext(),"your user id is "+userIDCacher.readCache(),Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            //Toast toast = Toast.makeText(getApplicationContext(),"Could you please implement the challenge activities ?",Toast.LENGTH_LONG);
            //toast.show();
            Intent intent = new Intent(this, ManageChallenge.class);
            try {
                quitActivity();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(intent);
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
            try {
                menuSignOut();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void setGoogleProfilePic() {
        if (FirebaseAuth.getInstance().getCurrentUser()!= null) {
            //get profile data from Firebase Authentication
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = auth.getCurrentUser();

            Uri userPhotoUri = firebaseUser.getPhotoUrl();
            String displayName = firebaseUser.getDisplayName();

            Log.e("SET GOOGLE PROFILE PIC", userPhotoUri.toString());
            String userPhotoURLString = "";

            if (userPhotoUri == null) {
                userPhotoURLString = "http://pronksiapartments.ee/wp-content/uploads/2015/10/placeholder-face-big.png";
            } else {
                userPhotoURLString = userPhotoUri.toString();
            }

            //Get Header view from Navigation View
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);

            //set profile pic using Picasso
            ImageView profilePic = (ImageView) header.findViewById(R.id.profilePic);
            Picasso.get().load(userPhotoURLString).transform(new CircleTransform()).into(profilePic);

            //set Name to Google account name
            TextView profileName = (TextView) header.findViewById(R.id.profileName);
            profileName.setText(displayName);
        }
    }


    private void menuSignOut() throws IOException {
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
        taskCacher.clearCache();
        topicCacher.clearCache();
        scoreCacher.clearCache();
        keyCacher.clearCache();
        freqCacher.clearCache();
        challengeCacher.clearCache();
        challengesRunningCacher.clearCache();
    }

    private void setupSwipeMenuListView(final SwipeMenuListView swipeList) {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem askItem = new SwipeMenuItem(getApplicationContext());
                askItem.setBackground(new ColorDrawable(getResources().getColor(R.color.adviceColor)));
                askItem.setWidth(200);
                askItem.setIcon(R.drawable.ic_advice_white);
                menu.addMenuItem(askItem);
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.deleteColor)));
                deleteItem.setWidth(200);
                deleteItem.setIcon(R.drawable.ic_delete_white);
                menu.addMenuItem(deleteItem);
            }
        };

        swipeList.setMenuCreator(creator);
        swipeList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //ask question
                        Log.d(TAG, "onMenuItemClick: clicked item" + index);
                        topicSwiped = topicList[position];
                        taskSwiped = tasksList[position];
                        Intent intent = new Intent(habitTracker.this, askQuestion.class);
                        intent.putExtra("FROM", "habitTracker_swipe");
                        try {
                            quitActivity();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                        break;
                    case 1:
                        //delete
                        Log.d(TAG, "onMenuItemClick: clicked item" + index);
                        //String deletedItem = habitTrackerList.get(position);
                        String deletedTask = tasksList[position];
                        String deletedTopic = topicList[position];
                        String deletecScore = scoreList[position];
                        String deletedChallenge = challengedList[position];
                        String deletedKey = keysList[position];
                        String deletedFreq = freqList[position];
                        tasksList = Methods.deleteString(tasksList,position);
                        topicList = Methods.deleteString(topicList,position);
                        scoreList = Methods.deleteString(scoreList,position);
                        challengedList = Methods.deleteString(challengedList,position);
                        keysList = Methods.deleteString(keysList,position);
                        freqList = Methods.deleteString(freqList,position);
                        try {
                            taskCacher.writeCache(tasksList);
                            topicCacher.writeCache(topicList);
                            scoreCacher.writeCache(scoreList);
                            keyCacher.writeCache(keysList);
                            freqCacher.writeCache(freqList);
                            challengeCacher.writeCache(challengedList);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        //Read write new habit tracker with the task off it
                        try {
                            deleteTask(deletedKey);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        deleteCallback(position,deletedTask,deletedTopic,deletecScore,deletedChallenge,deletedKey,deletedFreq);
                        if(tasksList.length==0){
                            noTask.setVisibility(View.VISIBLE);
                            swipeListView.setVisibility(View.GONE);
                        }
                        HabitTrackerAdapter habitTrackerListAdapter = new HabitTrackerAdapter();
                        swipeListView.setAdapter(habitTrackerListAdapter);

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void deleteCallback(final int position, final String deletedTask, final String deletedTopic, final String deletedScore, final String deletedChallenge, final String deletedKey, final String deletedFreq) {
        Snackbar snackbar = Snackbar
                .make(drawerLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tasksList = Methods.increaseArray(tasksList,deletedTask);
                topicList = Methods.increaseArray(topicList,deletedTopic);
                scoreList = Methods.increaseArray(scoreList,deletedScore);
                challengedList = Methods.increaseArray(challengedList,deletedChallenge);
                keysList = Methods.increaseArray(keysList,deletedKey);
                freqList = Methods.increaseArray(freqList,deletedFreq);
                if(tasksList.length==0){
                    noTask.setVisibility(View.VISIBLE);
                    swipeListView.setVisibility(View.GONE);
                }
                else{
                    swipeListView.setVisibility(View.VISIBLE);
                    noTask.setVisibility(View.GONE);
                }
                HabitTrackerAdapter habitTrackerListAdapter = new HabitTrackerAdapter();
                swipeListView.setAdapter(habitTrackerListAdapter);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }


    class HabitTrackerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            try {
                tasksList=taskCacher.readCache();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                taskCacher.writeCache(tasksList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tasksList.length;

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
            view = getLayoutInflater().inflate(R.layout.habit_tracker_adapter_layout,null);

            try{
                tasksList=taskCacher.readCache();
                topicList=topicCacher.readCache();
                scoreList=scoreCacher.readCache();
                keysList=keyCacher.readCache();
                freqList=freqCacher.readCache();
                challengedList=challengeCacher.readCache();

                habitTrackerListAdapter.notifyDataSetChanged();
            } catch (IOException e ){
                e.printStackTrace();
            }

            ImageView challengeIcon = (ImageView)view.findViewById(R.id.challengeIcon);
            if(challengedList.length!=0){
                if (!Boolean.parseBoolean(challengedList[i])){
                    challengeIcon.setVisibility(View.GONE);
                }
                else{
                    challengeIcon.setVisibility(View.VISIBLE);
                }
            }

            if(!taskCacher.hasCache()){
                noTask.setVisibility(View.VISIBLE);
                swipeListView.setVisibility(View.GONE);
            }
//            if (newScore[i]){
            ImageView greatIcon = (ImageView)view.findViewById(R.id.greatIcon);
            greatIcon.setVisibility(View.GONE);
//            }
            TextView showScore = (TextView)view.findViewById(R.id.cntScore);
            TextView taskName = (TextView)view.findViewById(R.id.taskName);
            taskName.setText(tasksList[i]);
            showScore.setText(scoreList[i]);

            view.setForegroundGravity(Gravity.CENTER);
            int myScore = Integer.parseInt(scoreList[i]);
            if (scoreList[i].equals("0")) {
            } else if ((myScore < 4) && (myScore > 0)) {
                view.setBackgroundColor(getResources().getColor(R.color.green1));
            } else if ((myScore < 10) && (myScore > 3)) {
                view.setBackgroundColor(getResources().getColor(R.color.green2));
            } else if (((myScore < 1000000) && (myScore > 9))) {
                view.setBackgroundColor(getResources().getColor(R.color.green3));
            }
            return view;
        }
    }
    public void quitActivity() throws IOException {
        tasksList=taskCacher.readCache();
        topicList=topicCacher.readCache();
        scoreList=scoreCacher.readCache();
        keysList=keyCacher.readCache();
        freqList=freqCacher.readCache();
        challengedList=challengeCacher.readCache();
        JSONObject tasklistdata = new JSONObject();
        JSONObject challengedata = new JSONObject();
        JSONObject questionsdata = new JSONObject();
        RequestParams rp3 = new RequestParams();
        Log.d("salut","The length before writting is "+keysList.length);
        rp3.put("api_key", "blurryapikeyseetutorial");
        rp3.put("param", "update");
        rp3.put("wanted","tasklist");
        rp3.put("userID",userIDCacher.readCache());
        try {
            //Adding every task as an item like : idtask {scoring + frequency}
            for (int i=0;i<keysList.length;i++){
                JSONObject itemTask = new JSONObject();
                itemTask.put("scoring", scoreList[i]);
                itemTask.put("frequency", freqList[i]);
                tasklistdata.put(keysList[i],itemTask);
            }
            //challengedata.put("challenge", userIDCacher.readCache());
            //questionsdata.put("followedQuestion", userIDCacher.readCache());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rp3.put("tasklist",URLEncoder.encode(tasklistdata.toString()));
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
                    taskCacher.writeCache(tasksList);
                    topicCacher.writeCache(topicList);
                    scoreCacher.writeCache(scoreList);
                    keyCacher.writeCache(keysList);
                    freqCacher.writeCache(freqList);
                    challengeCacher.writeCache(challengedList);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
    public void retrieveData(){
        JSONObject data = new JSONObject();
        RequestParams rp3 = new RequestParams();
        rp3.put("api_key", "blurryapikeyseetutorial");
        rp3.put("param", "myaccount");
        rp3.put("wanted","tasklist");
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
                    String usernameUser = serverResp.getString("username");
                    String firstnameUser = serverResp.getString("firstname");
                    Log.d("salut",serverResp.toString());
                        //Need to parse all the tasks, the scoring, the topics, the frequency
                        //JSONObject myTaskList = serverResp.getJSONObject("tasklist");
                        Iterator<String> keysTaskList = serverResp.keys();
                        keysList = new String[]{};
                        while(keysTaskList.hasNext()) {
                            String keyTask = keysTaskList.next();
                            keysList = Methods.increaseArray(keysList,keyTask);

                            if (serverResp.get(keyTask) instanceof JSONObject) {
                                JSONObject item2 = serverResp.getJSONObject(keyTask);
                                scoreList = Methods.increaseArray(scoreList, item2.getString("scoring"));
                                freqList = Methods.increaseArray(freqList, item2.getString("frequency"));
                            }

                            Log.d("salut","the number key is "+keysList.length);
                            tasksList = new String []{};
                            topicList = new String[]{} ;
                            for (int i=0;i<keysList.length;i++){
                                tasksList=Methods.increaseArray(tasksList,WelcomeActivity.taskList[Methods.find(WelcomeActivity.keysList,keysList[i])]);
                                topicList=Methods.increaseArray(topicList,WelcomeActivity.topicList[Methods.find(WelcomeActivity.keysList,keysList[i])]);
                                Log.d("salut","I just wrote "+tasksList[i]);
                            }

                    }
                    habitTrackerListAdapter.notifyDataSetChanged();

                    if(!serverResp.has("followed")) {
                        Log.d("salut","No followed question detected");
                    }


                    try {
                        taskCacher.writeCache(tasksList);
                        topicCacher.writeCache(topicList);
                        scoreCacher.writeCache(scoreList);
                        keyCacher.writeCache(keysList);
                        freqCacher.writeCache(freqList);
                        challengeCacher.writeCache(challengedList);
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                for (int i=0;i<keysList.length;i++){
                    Log.d("salut","keys are "+keysList[i]);
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Log.d("salut","received array");
            }
        });

    }
    public void deleteTask(String selectedTaskKey) throws IOException {
        tasksList=taskCacher.readCache();
        topicList=topicCacher.readCache();
        scoreList=scoreCacher.readCache();
        keysList=keyCacher.readCache();
        freqList=freqCacher.readCache();
        challengedList=challengeCacher.readCache();
        RequestParams rp3 = new RequestParams();
        Log.d("salut","The length before writting is "+keysList.length);
        rp3.put("api_key", "blurryapikeyseetutorial");
        rp3.put("param", "task");
        rp3.put("userID",userIDCacher.readCache());
        rp3.put("taskID",selectedTaskKey);
        String URlprofile = "http://www.fir-auth-93d22.appspot.com/user?" + rp3.toString();
        Log.d("salut",rp3.toString());
        HttpUtils.deleteByUrl(URlprofile, rp3, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    Log.d("salut",serverResp.toString());

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }
}