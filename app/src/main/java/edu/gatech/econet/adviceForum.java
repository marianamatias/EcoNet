package edu.gatech.econet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.design.widget.NavigationView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import android.support.v7.widget.Toolbar;

import java.util.ArrayList;


public class adviceForum extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    TextView receive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advice_forum);
        receive = (TextView)findViewById(R.id.questionShowText);
        receive.setText(getIntent().getStringExtra("question text"));


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    //Drawer Menu - Link to Activities
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.habit_tracker){
            Intent intent = new Intent(this, habitTracker.class);

            //just so it doesn't crash, need to fix this...
            intent.putExtra("FROM_ACTIVITY", "ParamNewTask");

            startActivity(intent);
        }


        if (id == R.id.add_goal){
            ArrayList<String> itemsSent = habitTracker.itemsSent;
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
}
