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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class habitTracker extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "habitTracker";

    private DrawerLayout drawerLayout;
    private ArrayList<String> items=null;
    public static ArrayList<String> itemsSent;

    //MenuItem menuItem;
    String receivedTask = null;
    TextView noTask;

    ArrayAdapter<String> habitTrackerListAdapter;
    ArrayList<String> habitTrackerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_tracker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        noTask = (TextView) findViewById(R.id.no_task);
        setSupportActionBar(toolbar);

        readItems();

         //Retrieve data from add task
        Intent mIntent = getIntent();
        String previousActivity = mIntent.getStringExtra("FROM_ACTIVITY");

//        if (previousActivity.equals("ParamNewTask")){
//            items = ParamNewTask.itemsLoc2;
//        }


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);


        SwipeMenuListView swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);
        habitTrackerList = new ArrayList<>();

        if (previousActivity.equals("ParamNewTask")) {
            if (ParamNewTask.itemsLoc2 != null) {
                habitTrackerList = ParamNewTask.itemsLoc2;
                items = ParamNewTask.itemsLoc2;
            }
        }

        habitTrackerListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, habitTrackerList);

        if (habitTrackerList != null) {
            swipeListView.setAdapter(habitTrackerListAdapter);
        }
        setupSwipeMenuListView(swipeListView);
    }

    //Drawer Menu - Link to Activities
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_goal){
            itemsSent = items;
            Intent intent = new Intent(this, AddTaskSearch.class);
            startActivity(intent);
        }

        if (id == R.id.advice){
            Intent intent = new Intent(this, askQuestion.class);
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
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<>();
        }
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
                // create "ask for advice" item
                SwipeMenuItem askItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                askItem.setBackground(new ColorDrawable(Color.rgb(31, 167, 221)));
                // set item width
                askItem.setWidth(200);
                // set item title
                askItem.setIcon(R.drawable.ic_advice_white);
                // add to menu
                menu.addMenuItem(askItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(206, 26, 10)));
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_white);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        swipeList.setMenuCreator(creator);

        swipeList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //ask question
                        Log.d(TAG, "onMenuItemClick: clicked item" + index);
                        Intent intent = new Intent(habitTracker.this, askQuestion.class);
                        startActivity(intent);
                        break;
                    case 1:
                        //delete
                        Log.d(TAG, "onMenuItemClick: clicked item" + index);
                        String deletedItem = habitTrackerList.get(position);
                        habitTrackerListAdapter.remove(habitTrackerList.get(position));
                        deleteCallback(position, deletedItem);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }


    private void deleteCallback(final int position, final String deletedTask) {
        Snackbar snackbar = Snackbar
                .make(drawerLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                habitTrackerList.add(position, deletedTask);
                habitTrackerListAdapter.notifyDataSetChanged();
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

}