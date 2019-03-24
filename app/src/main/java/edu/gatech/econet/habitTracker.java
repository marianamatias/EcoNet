package edu.gatech.econet;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


    private DrawerLayout drawerLayout;
    private ArrayList<String> items=null;
    public static ArrayList<String> itemsSent;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    //MenuItem menuItem;
    String receivedTask = null;
    TextView noTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_tracker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        noTask = (TextView) findViewById(R.id.no_task);
        setSupportActionBar(toolbar);

        //Sign out
//        Button button = (Button)findViewById(R.id.signOutButton);
//        button.setOnClickListener(this);
        lvItems = findViewById(R.id.lvItems);
        readItems();
        lvItems.setAdapter(itemsAdapter);
        //Bundle bundleIn = getIntent().getExtras();
         //Retrieve data from add task

        //if (bundleIn!=null){
            //for (int i=0; i< bundleIn.size();i++){
            //    if (bundleIn.getString("Task_List"+Integer.toString(i))!=null){
            //        items.add(bundleIn.getString("Task_List" + Integer.toString(i)));
            //    }
            //}
            //receivedTask = bundleIn.getString("new_task");
            //items.add(receivedTask);
        //}
        Intent mIntent = getIntent();
        String previousActivity= mIntent.getStringExtra("FROM_ACTIVITY");
        if (previousActivity.equals("ParamNewTask")){
            items = ParamNewTask.itemsLoc2;
        }


        //items.add("salut!");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //itemsAdapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, items);

//        items.add("Cook a vegetarian meal for friends");
        setupListViewListener();
        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.drawer_layout);
        enableSwipeToDeleteAndUndo();
        //enableSwipeToQuestion();
        mAdapter = new RecyclerViewAdapter(items);
        recyclerView.setAdapter(mAdapter);
//        if(items==null){
//            noTask.setText("Add a new task to your Habit Tracker ! Go to the side menu's Add Task section.");
//        }
        //onNavigationItemSelected(menuItem);
    }
    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // Remove the item within array at position
                        items.remove(pos);
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }

                });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_goal) {
            return true;
        }
        if (id == R.id.advice) {
            return true;
        }
        if (id == R.id.challenges) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Drawer Menu - Link to Activities
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.add_goal){
            itemsSent = items;
            Intent intent = new Intent(this, AddTaskSearch.class);
            //Bundle bundleAdd = new Bundle();
            //for (String str : items){
            //    int i =0;
            //    bundleAdd.putString("Task_List"+Integer.toString(i),str);
            //}
            //intent.putExtras(bundleAdd);
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

//        else {}
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    public void onAddItem(View v) {
//        itemsAdapter.add(receivedTask);
//        writeItems();
//    }


    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
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


//    private void signOut(View v) {
//        FirebaseAuth.getInstance().signOut();
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("1057246002930-8bp2uv0v2sjesp7iin4dkcp35uv3vlas.apps.googleusercontent.com")
//                .requestEmail()
//                .build();
//
//        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        // Google sign out
//        mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI();
//                    }
//                });
//
//    }

    // Implement the OnClickListener callback
//    public void onClick(View v) {
//        // do something when the button is clicked
//        signOut(v);
//    }

    public void updateUI() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final String item = mAdapter.getData().get(position);
                mAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(drawerLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

//        private void enableSwipeToQuestion(){
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewholder, int direction){
//            }
//        }

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

}