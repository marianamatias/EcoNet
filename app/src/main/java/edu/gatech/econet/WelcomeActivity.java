package edu.gatech.econet;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {
    Timer timer;
    TextView welcome;
   // AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Choice of the text displayed on the welcome screen by random choice
        String[] choiceWelcome;
        welcome = (TextView) findViewById(R.id.welcomeText);
        choiceWelcome = new String[] {"Join the community of EcoWarriors","Invite your friends to help the Econet grow","Cannot find inspiration ? Look at new topics !","Discover the eco-world and much more","Save our world, fill your feed.","Ever thought about changing your way of living ?"};
        Random random = new Random();
        int pos = random.nextInt(choiceWelcome.length);
        welcome.setText(choiceWelcome[pos]);

        //Animation of the loading display
        //ImageView loading = (ImageView)findViewById(R.id.loadingImg);
        //animation = (AnimationDrawable)loading.getDrawable();
        //animation.start();
        //app:srcCompat="@drawable/loading" //

        String user = MainActivity.signed;
        if (user=="Yes"){
            timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override public void run(){
                    Intent intent = new Intent(WelcomeActivity.this,habitTracker.class);
                    intent.putExtra("FROM_ACTIVITY", "WelcomeActivity");
                    startActivity(intent);
                }
            }, 2000);
        }

        else{
            timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override public void run(){
                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }, 2000);
        }

    }
}
