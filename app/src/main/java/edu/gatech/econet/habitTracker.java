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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

public class habitTracker extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    //String tasksList[] = new String[] {};
    //String topicList[] = new String[] {};
    String tasksList[] = AddTaskSearch.rawTasks;
    String topicList[] = AddTaskSearch.topicTasks;
    String scoreList[] = new String[] {};
    boolean challengedList[] = new boolean[] {};
    //new score =
    //To retrieve the data from askAdvice screen
    public static String topicSwiped ;
    public static String taskSwiped ;
    private static final String TAG = "habitTracker";
    private DrawerLayout drawerLayout;

    //MenuItem menuItem;
    TextView noTask;
    //FileCacher<ArrayList<String>> stringCacher;
    SwipeMenuListView swipeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_tracker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        noTask = (TextView) findViewById(R.id.no_task);
        noTask.setText("Your Habit Tracker is empty, please swipe the menu and add your first task !");
        noTask.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        //stringCacher = new FileCacher<>(habitTracker.this, "habitTrackerCache.txt");

        for (int i=0;i<tasksList.length;i++){
            scoreList = Methods.increaseArray(scoreList,"0");
            challengedList = Methods.increaseArrayBool(challengedList,false);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);

        swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);
//        if (stringCacher.hasCache()){
//            try{
//                ArrayList<String> text= stringCacher.readCache();
//                fullList=text;
//            } catch (IOException e ){
//                e.printStackTrace();
//            }
//        }
//Retrieve data from previous activity
//        Intent mIntent = getIntent();
//        String previousActivity = mIntent.getStringExtra("FROM_ACTIVITY");
//        if (previousActivity.equals("ParamNewTask")){
//            fullList.add("0");
//            fullList.add(ParamNewTask.receivedTask);
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
                HabitTrackerAdapter habitTrackerListAdapter = new HabitTrackerAdapter();
                swipeListView.setAdapter(habitTrackerListAdapter);
//                try {
//                    stringCacher.writeCache(fullList);
//                } catch (IOException e){
//                    e.printStackTrace();
//                }
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
        }
        if (id == R.id.add_goal){
            Intent intent = new Intent(this, AddTaskSearch.class);
            startActivity(intent);
        }
        if (id == R.id.challenges){
            Toast toast = Toast.makeText(getApplicationContext(),"Could you please implement the challenge activities ?",Toast.LENGTH_LONG);
            toast.show();
        }
        if (id== R.id.advice){
            Intent intent = new Intent(this, askQuestion.class);
            intent.putExtra("FROM", "habitTracker_menu");
            startActivity(intent);
        }
        if (id == R.id.forum){
            Intent intent = new Intent(this, ForumTopicSelect.class);
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
                        startActivity(intent);
                        break;
                    case 1:
                        //delete
                        Log.d(TAG, "onMenuItemClick: clicked item" + index);
                        //String deletedItem = habitTrackerList.get(position);
                        String deletedTask = tasksList[position];
                        String deletedTopic = topicList[position];
                        String deletecScore = scoreList[position];
                        boolean deletedChallenge = challengedList[position];
                        tasksList = Methods.deleteString(tasksList,position);
                        topicList = Methods.deleteString(topicList,position);
                        scoreList = Methods.deleteString(scoreList,position);
                        challengedList = Methods.deleteStringBool(challengedList,position);
                        deleteCallback(position,deletedTask,deletedTopic,deletecScore,deletedChallenge);
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

    private void deleteCallback(final int position, final String deletedTask, final String deletedTopic, final String deletedScore, final boolean deletedChallenge) {
        Snackbar snackbar = Snackbar
                .make(drawerLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tasksList = Methods.increaseArray(tasksList,deletedTask);
                topicList = Methods.increaseArray(topicList,deletedTopic);
                scoreList = Methods.increaseArray(scoreList,deletedScore);
                challengedList = Methods.increaseArrayBool(challengedList,deletedChallenge);
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
//        try {
//            stringCacher.writeCache(fullList);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
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
//            if (challengedList[i]){
                ImageView challengeIcon = (ImageView)view.findViewById(R.id.challengeIcon);
//            }
//            if (newScore[i]){
//                ImageView greatIcon = (ImageView)view.findViewById(R.id.greatIcon);
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

}