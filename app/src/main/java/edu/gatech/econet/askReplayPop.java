package edu.gatech.econet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class askReplayPop extends AppCompatActivity {
    TextView questionToReply;
    EditText questionResponse;
    Button sendResponse;
    public static String responseText;
    public static String user;
    public static String question;
    public static String topic;
    public static String task;
    //Need to retrieve the data from the responses : bundle ? public static ? cache ? wait for communication ?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_replay_pop);
        //Set up the size of the screen
        // Dimensions are hardcoded.... how to do that ??
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width= dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(1200,1200);
        //Retrieval of all the data from the previous screen
        task=AskView.retrievedTask;
        topic=AskView.retrievedTopic;
        user=AskView.retrievedUser;
        question=AskView.retrieveQuestion;
        questionToReply = (TextView)findViewById(R.id.questionToReply);
        questionResponse = (EditText)findViewById(R.id.questionResponse);
        sendResponse = (Button)findViewById(R.id.sendResponse);

        questionToReply.setText(AskView.retrieveQuestion);
        sendResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responseText = questionResponse.getText().toString();
                OpenNewActivity();
            }
        });


        }
    void OpenNewActivity(){
        Intent intent = new Intent(this, AskView.class);
        intent.putExtra("FROM3", "askReplayPop");
        startActivity(intent);
    }
}
