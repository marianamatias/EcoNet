package edu.gatech.econet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.kosalgeek.android.caching.FileCacher;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.IOException;

public class AddChallengeScreen extends AppCompatActivity {

    Button challengeButton;
    String [] challengerList = new String[] {"Billy","Natasha","Dean","Russo"};
    String [] topicList = ForumTopicSelect.localTopic;
    String topicSelected;
    String challengerSelected;
    FileCacher<String []> challengedUserCacher;
    FileCacher<String []> challengeedTopicCacher;
    String [] challengedUsers = new String[] {};
    String [] challengedTopic = new String[] {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge_screen);
        //Set up the size of the screen
        // Dimensions are hardcoded.... how to do that ??
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width= dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(1000,1200);
        challengeButton=(Button)findViewById(R.id.challengeButton);

        challengedUserCacher = new FileCacher<>(AddChallengeScreen.this, "challengedUserCacher.txt");
        challengeedTopicCacher = new FileCacher<>(AddChallengeScreen.this, "challengeedTopicCacher.txt");
        if (challengedUserCacher.hasCache()){
            try{
                challengedUsers=challengedUserCacher.readCache();
                challengedTopic=challengeedTopicCacher.readCache();
            } catch (IOException e ){
                e.printStackTrace();
            }
        }

        final ArrayAdapter<String> topicAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,topicList);
        MaterialBetterSpinner topicSpinner=(MaterialBetterSpinner)findViewById(R.id.topicScroll);
        topicSpinner.setAdapter(topicAdapter);
        topicSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                topicSelected = topicList[i];
            }
        });
        final ArrayAdapter<String> challengerAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,challengerList);
        MaterialBetterSpinner challengerSpinner=(MaterialBetterSpinner)findViewById(R.id.challengerScroll);
        challengerSpinner.setAdapter(challengerAdapter);
        challengerSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                challengerSelected = challengerList[i];
            }
        });

        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Methods.isInArray(challengedTopic,topicSelected)){
                    Intent intent = new Intent(getApplicationContext(), ManageChallenge.class);
                    try {
                        challengedUsers=Methods.increaseArray(challengedUsers,challengerSelected);
                        challengedTopic=Methods.increaseArray(challengedTopic,topicSelected);
                        challengedUserCacher.writeCache(challengedUsers);
                        challengeedTopicCacher.writeCache(challengedTopic);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Already challenged topic",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

    }
}
