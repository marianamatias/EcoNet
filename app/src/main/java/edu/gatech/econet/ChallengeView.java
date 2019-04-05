package edu.gatech.econet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;

public class ChallengeView extends AppCompatActivity {
    TextView topicView;
    TextView challengerView;
    TextView statusView;
    TextView scoreView;
    FileCacher<String []> topicCacher;
    FileCacher<String []> scoreCacher;
    String localScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_view);
        topicView = (TextView)findViewById(R.id.selectedTopic);
        challengerView = (TextView)findViewById(R.id.selectedChallenger);
        statusView = (TextView) findViewById(R.id.selectedStatus);
        scoreView = (TextView) findViewById(R.id.selectedScoring);
        //Cacher from habit tracker
        topicCacher = new FileCacher<>(ChallengeView.this, "topicCacher.txt");
        scoreCacher = new FileCacher<>(ChallengeView.this, "scoreCacher.txt");
        if (topicCacher.hasCache()){
            try{
                String [] topicList=topicCacher.readCache();
                String [] scoreList=scoreCacher.readCache();
                int localScoreInt =0;
                for (int i =0; i< topicList.length; i++){
                    if (topicList[i].equals(ManageChallenge.topicSelected)){
                        localScoreInt += Integer.parseInt(scoreList[i]);
                    }
                }
                localScore=Integer.toString(localScoreInt);
            } catch (IOException e ){
                e.printStackTrace();
                localScore="0";
            }
        }

        topicView.setText(ManageChallenge.topicSelected);
        challengerView.setText(ManageChallenge.challengerSelected);
        statusView.setText(ManageChallenge.statusSelected);
        //Need the calculation of the scoring based on the task on the habit tracker
        //We retreive from the cache all the tasks and topics
        //Then compare the topic : if topic is good we get the scoring and add it to the localScoring

        scoreView.setText("Your actual scoring is " +localScore);
    }
}
