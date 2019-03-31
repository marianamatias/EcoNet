package edu.gatech.econet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AskView extends AppCompatActivity {
    Button replyButton;
    TextView askTopic;
    TextView askTask;
    TextView askMessage;
    TextView authorID;
    ListView listMessages;
    String retrievedTopic;
    String retrievedTask;
    String retrieveQuestion;
    String authorRespID[] = new String [] {"Billy","Eva","Josh"};
    String nbrPlus[] = new String [] {"0","0","0"};
    String textResponse[] = new String[] {"Hey what's up ?","Really good question !","I will answer asap !"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_view);
        replyButton = (Button) findViewById(R.id.replyButton);
        askTask = (TextView) findViewById(R.id.askTask);
        askTopic = (TextView) findViewById(R.id.askTopic);
        askMessage = (TextView) findViewById(R.id.askMessage);
        authorID = (TextView) findViewById(R.id.authorID);
        listMessages = (ListView) findViewById(R.id.listMessages);
        //Retrieve the task and topic from previous activity
        Intent mIntent = getIntent();
        String previousActivity = mIntent.getStringExtra("FROM3");
        if (previousActivity.equals("askQuestion")){
            retrievedTask = askQuestion.suggestedTask;
            retrievedTopic = askQuestion.suggestedTopic;
            retrieveQuestion = askQuestion.questionSent;
        }
        else if (previousActivity.equals("adviceForum")){
            retrievedTask = adviceForum.taskSend;
            retrievedTopic = adviceForum.topicSend;
            retrieveQuestion = adviceForum.questionSend;
        }
        askTask.setText("Task : "+retrievedTask);
        askTopic.setText("Topic : "+retrievedTopic);
        askMessage.setText(retrieveQuestion);
        authorID.setText("Anonymous author");
        listMessages = (ListView) findViewById(R.id.listMessages);
        AskView.AskAdapter forumAdapter = new AskView.AskAdapter();
        listMessages.setAdapter(forumAdapter);
    }

    class AskAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return authorRespID.length;
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
            view = getLayoutInflater().inflate(R.layout.question_forum_adapter_layout,null);
            TextView nbrPlusItem = (TextView)view.findViewById(R.id.nbrPlus);
            TextView textResponseItem = (TextView)view.findViewById(R.id.textResponse);
            TextView authorRespIDItem = (TextView)view.findViewById(R.id.authorRespID);
            Button upButtonItem = (Button)view.findViewById(R.id.upButton);

            final int u = i;
            nbrPlusItem.setText(nbrPlus[i]);
            textResponseItem.setText(textResponse[i]);
            authorRespIDItem.setText(authorRespID[i]);
            upButtonItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int cnt = Integer.parseInt(nbrPlus[u]);
                    cnt++ ;
                    nbrPlus[u]=Integer.toString(cnt);
                }
            });
            view.setForegroundGravity(Gravity.CENTER);
            view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
            return view;
        }
    }
}
