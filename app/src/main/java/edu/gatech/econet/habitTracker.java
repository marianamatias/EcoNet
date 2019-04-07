package edu.gatech.econet;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.kosalgeek.android.caching.FileCacher;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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

    //String tasksList[] = new String[] {};
    //String topicList[] = new String[] {};
    String tasksList[] = new String[] {};
    String topicList[] = new String[] {};
    String scoreList[] = new String[] {};
    String challengedList[] = new String[] {};
    String keysList[] = new String[] {};
    String freqList[] = new String[] {};
    //new score =
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_tracker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        noTask = (TextView) findViewById(R.id.no_task);
        noTask.setText("Your Habit Tracker is empty, please swipe the menu and add your first task !");
        noTask.setVisibility(View.GONE);

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
        userIDCacher = new FileCacher<>(habitTracker.this, "userId.txt");

        for (int i=0;i<tasksList.length;i++){
            scoreList = Methods.increaseArray(scoreList,"0");
            challengedList = Methods.increaseArray(challengedList,"false");
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);

        swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);
//        if (taskCacher.hasCache()){
//            try{
//                tasksList=taskCacher.readCache();
//                topicList=topicCacher.readCache();
//                scoreList=scoreCacher.readCache();
//                keysList=keyCacher.readCache();
//                freqList=freqCacher.readCache();
//                challengedList=challengeCacher.readCache();
//            } catch (IOException e ){
//                e.printStackTrace();
//            }
//        }

        scoreList=WelcomeActivity.getTaskScoreListUser;
        freqList=WelcomeActivity.getTaskFreqListUser;
        challengedList=WelcomeActivity.getChallengersIDListUser;
        keysList=WelcomeActivity.getTaskKeysListUser;
        for (int i=0;i<keysList.length;i++){
            tasksList=Methods.increaseArray(tasksList,WelcomeActivity.taskList[Methods.find(WelcomeActivity.keysList,keysList[i])]);
            topicList=Methods.increaseArray(topicList,WelcomeActivity.topicList[Methods.find(WelcomeActivity.keysList,keysList[i])]);
        }

        //Retrieve data from previous activity
        Intent mIntent = getIntent();
        try{
            String previousActivity = mIntent.getStringExtra("FROM_ACTIVITY");
            if ((previousActivity.equals("ParamNewTask"))&&(!Methods.isInArray(tasksList,ParamNewTask.receivedTask))){
                tasksList=Methods.increaseArray(tasksList,ParamNewTask.receivedTask);
                topicList=Methods.increaseArray(topicList,ParamNewTask.receivedTopic);
                scoreList=Methods.increaseArray(scoreList,"0");
                challengedList=Methods.increaseArray(challengedList,"false");
                freqList=Methods.increaseArray(freqList,Integer.toString(ParamNewTask.frequency));
                keysList=Methods.increaseArray(keysList,ParamNewTask.key);
            }
        }catch (Exception e){

        }


//        if (challengesRunningCacher.hasCache()){
//            try{
//                String challengesLocal [] = challengesRunningCacher.readCache();
//                for (int i =0; i<challengesLocal.length;i++){
//                    for (int j=0; j<topicList.length;j++){
//                        if (topicList[j].equals(challengesLocal[i])){
//                            challengedList[j] = "true";
//                        }
//                    }
//                }
//            } catch (IOException e ){
//                e.printStackTrace();
//            }
//        }
        HabitTrackerAdapter habitTrackerListAdapter = new HabitTrackerAdapter();
        swipeListView.setAdapter(habitTrackerListAdapter);
        setupSwipeMenuListView(swipeListView);
        swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long is) {
                int anteScore = Integer.parseInt(scoreList[position]);
                anteScore++;
                scoreList[position]=Integer.toString(anteScore);
                //boolean anteChallenged = Boolean.parseBoolean(challengedList[position]);
                ///challengedList[position]=Boolean.toString(!anteChallenged);
                HabitTrackerAdapter habitTrackerListAdapter = new HabitTrackerAdapter();
                swipeListView.setAdapter(habitTrackerListAdapter);
            }
        });

        if(tasksList.length==0){
            noTask.setVisibility(View.VISIBLE);
            swipeListView.setVisibility(View.GONE);
        }

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
            menuSignOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


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

    private void setupSwipeMenuListView(final SwipeMenuListView swipeList) {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem askItem = new SwipeMenuItem(getApplicationContext());
                askItem.setBackground(new ColorDrawable(Color.rgb(31, 167, 221)));
                askItem.setWidth(200);
                askItem.setIcon(R.drawable.ic_advice_white);
                menu.addMenuItem(askItem);
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(206, 26, 10)));
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
            ImageView challengeIcon = (ImageView)view.findViewById(R.id.challengeIcon);
//            if(challengedList.length!=0){
//                if (!Boolean.parseBoolean(challengedList[i])){
//                    challengeIcon.setVisibility(View.GONE);
//                }
//                else{
//                    challengeIcon.setVisibility(View.VISIBLE);
//                }
//            }

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
//        try {
//            taskCacher.writeCache(tasksList);
//            topicCacher.writeCache(topicList);
//            scoreCacher.writeCache(scoreList);
//            keyCacher.writeCache(keysList);
//            freqCacher.writeCache(freqList);
//            challengeCacher.writeCache(challengedList);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
        JSONObject tasklistdata = new JSONObject();
        JSONObject challengedata = new JSONObject();
        JSONObject questionsdata = new JSONObject();
        RequestParams rp3 = new RequestParams();
        rp3.put("api_key", "blurryapikeyseetutorial");
        rp3.put("param", "update");
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
        rp3.put("challenge",URLEncoder.encode(challengedata.toString()));
        rp3.put("followedQuestion", URLEncoder.encode(questionsdata.toString()));
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
            }
        });

    }

}