package edu.gatech.econet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
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

public class habitTracker extends AppCompatActivity implements
        View.OnClickListener {

    private ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_tracker);


        Button button = (Button)findViewById(R.id.signOutButton);
        button.setOnClickListener(this);


        lvItems = findViewById(R.id.lvItems);
        items = new ArrayList<>();
        readItems();
        lvItems.setAdapter(itemsAdapter);
//        items.add("Make a vegetarian shopping list");
//        items.add("Cook a vegetarian meal for friends");

        recyclerView = findViewById(R.id.recyclerView);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        enableSwipeToDeleteAndUndo();
        mAdapter = new RecyclerViewAdapter(items);
        recyclerView.setAdapter(mAdapter);

    }

//    public void onAddItem(View v) {
//        EditText etNewItem = findViewById(R.id.etNewItem);
//        String itemText = etNewItem.getText().toString();
//        itemsAdapter.add(itemText);
//        etNewItem.setText("");
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

//    private void writeItems() {
//        File filesDir = getFilesDir();
//        File todoFile = new File(filesDir, "todo.txt");
//        try {
//            FileUtils.writeLines(todoFile, items);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void signOut(View v) {
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

    // Implement the OnClickListener callback
    public void onClick(View v) {
        // do something when the button is clicked
        signOut(v);
    }

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
                        .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
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

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
}