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

    ///////////////////////////////////////////////
    ////New variables to consider for improving the habit tracker
    //// New layout for the listview available at habit_tracker_adapter_layout.xml
    ////////////////////////////////////////////////
    String tasksList[] = AddTaskSearch.rawTasks;
    String topicList[] = AddTaskSearch.topicTasks;
    String scoreList[] = new String[] {};
    //boolean challengedList[] = new boolean[] {};
    //new score =
    //all of this should be written into cacher thing !
    ///////////////////////////////////////////////

    private static final String TAG = "habitTracker";
    private DrawerLayout drawerLayout;
    //private ArrayList<String> items=null;
    //public static ArrayList<String> itemsSent;

    //MenuItem menuItem;
    //TextView noTask;
    ArrayAdapter<String> habitTrackerListAdapter;
    //ArrayList<String> habitTrackerList;
    //public static ArrayList<String> fullList;
    //ArrayList<String> taskList =new ArrayList<>();
    //FileCacher<ArrayList<String>> stringCacher;
    SwipeMenuListView swipeListView;
    int indexEdited;
    //public static String taskSwiped;
    //public static String topicSwiped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_tracker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //noTask = (TextView) findViewById(R.id.no_task);
        setSupportActionBar(toolbar);
        //stringCacher = new FileCacher<>(habitTracker.this, "habitTrackerCache.txt");
        readItems();

        for (int i=0;i<tasksList.length;i++){
            scoreList = increaseArray(scoreList,"0");
            //increase the challengedlist too with method
            //build new method to put the shitty method in it
            //increaseArray for boolean and integer !
        }
//        //Retrieve data from previous activity
//        Intent mIntent = getIntent();
//        String previousActivity = mIntent.getStringExtra("FROM_ACTIVITY");


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);

        swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);
//        fullList = new ArrayList<>();
//        habitTrackerList = new ArrayList<>();

//        if (stringCacher.hasCache()){
//            try{
//                ArrayList<String> text= stringCacher.readCache();
//                fullList=text;
//            } catch (IOException e ){
//                e.printStackTrace();
//            }

//        }
//        if (previousActivity.equals("ParamNewTask")){
//            fullList.add("0");
//            fullList.add(ParamNewTask.receivedTask);
//        }
//        if (fullList.size()%2==0){
//            for (int i=0;i<fullList.size()/2;i++){
//                habitTrackerList.add(fullList.get(2*i)+"   "+fullList.get(2*i+1));
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
                indexEdited = position;
                scoreList[position]=Integer.toString(anteScore);
                HabitTrackerAdapter habitTrackerListAdapter = new HabitTrackerAdapter();
                swipeListView.setAdapter(habitTrackerListAdapter);
                //habitTrackerList.set(position,fullList.get(2*position)+"   "+fullList.get(2*position+1));
                //habitTrackerListAdapter = new ArrayAdapter<String>(habitTracker.this, android.R.layout.simple_list_item_1, habitTrackerList);
                //swipeListView.setAdapter(habitTrackerListAdapter);
//                try {
//                    stringCacher.writeCache(fullList);
//                } catch (IOException e){
//                    e.printStackTrace();
//                }
//                habitTrackerListAdapter = new ArrayAdapter<String>(habitTracker.this, android.R.layout.simple_list_item_1, habitTrackerList){
//                    @Override
//                    public View getView(int position, View convertView, ViewGroup parent) {
//                        View view = super.getView(position, convertView, parent);
//                        return view;
//                    }
//                };
//                swipeListView.setAdapter(habitTrackerListAdapter);
//                setupSwipeMenuListView(swipeListView);
            }
        });
//        try {
//            stringCacher.writeCache(fullList);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
    }

    //Drawer Menu - Link to Activities
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_goal){
            //itemsSent = items;
            Intent intent = new Intent(this, AddTaskSearch.class);
            startActivity(intent);
        }
        if (id == R.id.challenges){
            Toast toast = Toast.makeText(getApplicationContext(),"Could you please implement the challenge activities ?",Toast.LENGTH_LONG);
            toast.show();
        }
        if (id== R.id.forum){
            Intent intent = new Intent(this, askQuestion.class);
            intent.putExtra("FROM", "habitTracker_menu");
            startActivity(intent);
        }
        if (id == R.id.challenges){
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

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
//        try {
//            //items = new ArrayList<String>(FileUtils.readLines(todoFile));
//        } catch (IOException e) {
//            //items = new ArrayList<>();
//        }
    }
    private void menuSignOut() {
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1057246002930-8bp2uv0v2sjesp7iin4dkcp35uv3vlas.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI();
                    }
                });
    }

    public void updateUI() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void setupSwipeMenuListView(SwipeMenuListView swipeList) {
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

        // set creator
        swipeList.setMenuCreator(creator);

        swipeList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
//                    case 0:
//                        //ask question
//                        Log.d(TAG, "onMenuItemClick: clicked item" + index);
//                        String rawTasks[] = AddTaskSearch.rawTasks;
//                        for (int i =0; i<fullList.size();i++){
//                            if (isInArray(rawTasks,fullList.get(i))){
//                                taskList.add(fullList.get(i));
//                            }
//                        }
//                        taskSwiped = taskList.get(index);
//                        String topicTask[] = AddTaskSearch.topicTasks;
//                        int indexTopic = find(rawTasks,taskSwiped);
//                        topicSwiped = topicTask[indexTopic];
//                        Intent intent = new Intent(habitTracker.this, askQuestion.class);
//                        intent.putExtra("FROM", "habitTracker_swipe");
//                        startActivity(intent);
//                        break;
//                    case 1:
//                        //delete
//                        Log.d(TAG, "onMenuItemClick: clicked item" + index);
//                        String deletedItem = habitTrackerList.get(position);
//                        habitTrackerListAdapter.remove(habitTrackerList.get(position));
//                        try {
//                            fullList.remove(fullList.get(2 * position + 1));
//                            fullList.remove(fullList.get(2 * position));
//                        } catch (Exception e)
//                        {
//                            fullList.remove(fullList.get(2*position));
//                        }
//                        deleteCallback(position, deletedItem);
//                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

//    private void deleteCallback(final int position, final String deletedTask) {
//        Snackbar snackbar = Snackbar
//                .make(drawerLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
//        snackbar.setAction("UNDO", new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //habitTrackerList.add(position, deletedTask);
//                //fullList.add(2*position,deletedTask);
//                //fullList.add(2*position+1,"0");
//                habitTrackerListAdapter.notifyDataSetChanged();
//            }
//        });
//        snackbar.setActionTextColor(Color.YELLOW);
//        snackbar.show();
//        try {
//            stringCacher.writeCache(fullList);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }
    public static int find(String[] inputList, String target){
        for (int i = 0; i < inputList.length; i++) {
            if (inputList[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
    public static boolean isInArray(String[] inputList, String target){
        int found= 0;
        for (int i =0; i< inputList.length;i++){
            if (inputList[i].equals(target)){
                found=1;
            }
        }
        if(found==1){
            return true;
        }
        else{ return false;}
    }
    ///////////////////////////////////////////////////
    ///New adapter for the habit tracker coming from layout defined
    //// To update ? Here is a basic strcture
    ///////////////////////////////////////////////////
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
//                ImageView challengeIcon = (ImageView)view.findViewById(R.id.challengeIcon);
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
                //view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
            } else if ((myScore < 4) && (myScore > 0)) {
                view.setBackgroundColor(getResources().getColor(R.color.yellow));
            } else if ((myScore < 10) && (myScore > 3)) {
                view.setBackgroundColor(getResources().getColor(R.color.green));
            } else if (((myScore < 1000000) && (myScore > 9))) {
                view.setBackgroundColor(getResources().getColor(R.color.azur));
            }
            //view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
            return view;
        }
    }
    public String[] increaseArray(String[] input, String newElem){
        String [] nullList = new String []{};
        if (input!=nullList) {
            int i = input.length;
            String[] newArray = new String[i + 1];
            for (int cnt = 0; cnt < i; cnt++) {
                newArray[cnt] = input[cnt];
            }
            newArray[i] = newElem;
            return newArray;
        }
        else {
            String [] returnList = new String[] {newElem};
            return returnList;
        }
    }
}