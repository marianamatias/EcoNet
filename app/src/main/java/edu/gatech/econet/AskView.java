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
    public static String retrievedUser;

    public static String retrievedTopic;
    public static String  retrievedTask;
    public static String retrieveQuestion;
    String authorRespID[] = new String [] {"Billy","Eva","Josh"};
    String nbrPlus[] = new String [] {"20","5","1"};
    String textResponse[] = new String[] {"Amazon has some great ones, I would highly suggest this.","I've tried some bamboo straws but are not very durable... You should use steel straws","There are some styilsh ones on etsy!"};

    public String listquestionsvegenism [] = new String[] {"Beans and high-protein grains are a great source of protein! I really enjoy a quinoa salad with chickpease, carrots, avocados and tomatoes.",
            "Tofu is a good source of protein. You can substitute it for many meat/egg/milk products, you just have to choose silk or firm tofu for the texture."};
    public String listquestionanimal [] = new String [] {"Here are some cool stores that I've found! https://www.peta.org/living/personal-care-fashion/vegan-eco-clothing-belongs-in-your-closet/",
            "For makeup in the US, you can download the Leaping Bunny Cruelty Free app to find the most animal-friendly products!"};
    public String listquestionenergy [] = new String [] {"Depends on your device and brand. For example iPhones usually stop charging for the most part when they reach 100%.", "You definitely have to unplug hairdryers if you're not using them. They are one of the appliances that use the most energy, even if they're turned off."};
    public String listquestiontransport [] = new String[] {"Stopping at the store on your way home from work, for example, combines trips and reduces your footprint. Combining your commute with your errands, while it may take extra time, will ultimately save you money on gas and reduce your emissions.",
            "Car maintenance is also essential for a green commute. Making sure your oil is changed, your filters are new, your car is in good shape, and you don’t keep anything too heavy in your trunk can all help keep your car running efficiently. This ensures that your engine isn’t overworking and increasing your emissions."};
    public String listquestionzerowaste [] = new String[] {"If taken care of, bamboo straws can last for years and years and are biodegradable after you've disposed of them.",
            "I agree! And bamboo is a truly renewable resource, bamboo growth can keep up with the rate of human consumption.",
            "Bamboo takes about 5-10 years to decompose. Steel on the other hand takes 50 years. But if you take care of your product, you could have only a couple of steel straws instead of many more bamboo straws."};

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
        else if (previousActivity.equals("askReplayPop")){
            retrievedTask = askReplayPop.task;
            retrievedTopic = askReplayPop.topic;
            retrieveQuestion = askReplayPop.question;
            retrievedUser = askReplayPop.user;
            authorRespID=Methods.increaseArray(authorRespID,"New User");
            nbrPlus=Methods.increaseArray(nbrPlus,"0");

        }
        if (retrievedTopic.equals(ForumTopicSelect.localTopic[0])){
            textResponse=listquestionsvegenism;
        }
        else if (retrievedTopic.equals(ForumTopicSelect.localTopic[1])){
            textResponse=listquestionanimal;
        }
        else if (retrievedTopic.equals(ForumTopicSelect.localTopic[2])){
            textResponse =listquestionenergy;
        }
        else if (retrievedTopic.equals(ForumTopicSelect.localTopic[3])){
            textResponse =listquestiontransport;
        }
        else if (retrievedTopic.equals(ForumTopicSelect.localTopic[4])){
            textResponse =listquestionzerowaste;
        }
        //textResponse=Methods.increaseArray(textResponse,askReplayPop.responseText);
        askTask.setText("Task : "+retrievedTask);
        askTopic.setText("Topic : "+retrievedTopic);
        askMessage.setText(retrieveQuestion);
        authorID.setText("Anonymous author");
        retrievedUser=authorID.getText().toString();
        listMessages = (ListView) findViewById(R.id.listMessages);
        AskView.AskAdapter forumAdapter = new AskView.AskAdapter();
        listMessages.setAdapter(forumAdapter);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenNewActivity();
            }
        });
    }

    void OpenNewActivity(){
        Intent intent = new Intent(this, askReplayPop.class);
        startActivity(intent);
    }
    class AskAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 2;
//            return authorRespID.length;
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
                    AskView.AskAdapter forumAdapter = new AskView.AskAdapter();
                    listMessages.setAdapter(forumAdapter);
                }
            });
            view.setForegroundGravity(Gravity.CENTER);
            view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
            return view;
        }
    }
}
