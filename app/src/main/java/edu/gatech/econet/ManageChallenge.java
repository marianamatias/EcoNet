package edu.gatech.econet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.IOException;

public class ManageChallenge extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{
    ListView listChallenge = null;
    FileCacher<String []> challengeCacher;
    FileCacher<String []> topicCacher;
    FileCacher<String []> scoreCacher;
    String topicChallenged[] = new String[] {};
    Button addChallenge;
    FileCacher<String []> challengedUserCacher;
    FileCacher<String []> challengeedTopicCacher;
    String [] challengedUsers = new String[] {};
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
        topicCacher = new FileCacher<>(ManageChallenge.this, "topicCacher.txt");
        scoreCacher = new FileCacher<>(ManageChallenge.this, "scoreCacher.txt");
        challengeCacher = new FileCacher<>(ManageChallenge.this, "challengeCacher.txt");
        challengedUserCacher = new FileCacher<>(ManageChallenge.this, "challengedUserCacher.txt");
        challengeedTopicCacher = new FileCacher<>(ManageChallenge.this, "challengeedTopicCacher.txt");
        addChallenge = (Button)findViewById(R.id.addChallenge);
        listChallenge=(ListView) findViewById(R.id.ntm);

        if (challengedUserCacher.hasCache()){
            try{
                challengedUsers=challengedUserCacher.readCache();
                challengedTopic=challengeedTopicCacher.readCache();
                for (int i=0; i<challengedTopic.length;i++){
                    challengesStatus = Methods.increaseArray(challengesStatus,"Received");
                }
            } catch (IOException e ){
                e.printStackTrace();
            }
        }
        ManageChallenge.ChallengeAdapter challengeAdapter = new ManageChallenge.ChallengeAdapter();
        listChallenge.setAdapter(challengeAdapter);
        listChallenge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast toast = Toast.makeText(getApplicationContext(),"Hello I am the challenge ",Toast.LENGTH_LONG);
//                toast.show();
                topicSelected=challengedTopic[position];
                challengerSelected=challengedUsers[position];
                statusSelected=challengesStatus[position];
                quitActivity();
                Intent intent = new Intent (getApplicationContext(), ChallengeView.class);
                startActivity(intent);
            }
        });

        addChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitActivity();
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
            return challengedTopic.length;
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
                    quitActivity();
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
            quitActivity();
            startActivity(intent);
        }
        if (id == R.id.add_goal){
            Intent intent = new Intent(this, AddTaskSearch.class);
            quitActivity();
            startActivity(intent);
        }
        if (id == R.id.challenges){
            //Already in it
        }
        if (id== R.id.advice){
            Intent intent = new Intent(this, askQuestion.class);
            intent.putExtra("FROM", "habitTracker_menu");
            quitActivity();
            startActivity(intent);
        }
        if (id == R.id.forum){
            Intent intent = new Intent(this, ForumTopicSelect.class);
            quitActivity();
            startActivity(intent);
        }
        if (id == R.id.signOut){
            menuSignOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void quitActivity(){
        try {
            challengeedTopicCacher.writeCache(challengedTopic);
            challengedUserCacher.writeCache(challengedUsers);
        } catch (IOException e){
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
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        quitActivity();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
    }
}
