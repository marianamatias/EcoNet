package edu.gatech.econet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.design.widget.NavigationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;


public class adviceForum extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    public static String questions[] = new String[] {};
    public static String localTasks[] = new String[] {};
    public static String nbrResponse[] = new String[] {} ;
    public static String localTopic[] = new String[] {};
    ListView listAdvice=null;
    public static String selectedTopic;
    public static int selectedIndex;
    Button filterButton;
    //For the filter feature
    String [] filterList = new String [] {};
    boolean [] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String rawTasks[];
    String rawTopic[];
    String copyTasks[] = new String[] {};
    //For conversation activity
    public static String topicSend;
    public static String taskSend;
    public static String questionSend;
    //For now the questions are chosen manually ...

    String listofquestions[] = new String[]{"Which is better, a steel straw or a bamboo straw?", "What restaurants have good vegan food?", "How can I check which appliances use the most energy?", "How can I set up my house for composting?",
            "How can I reduce my waste?", "What clothing lines are vegan?", "What type of non-animal proteins can I eat?", "Should I wash my glass before recycling?",
    "Do I have to unplug my devices after they're done charging?"};

    String listquestionsvegenism [] = new String[] {"What type of non-animal proteins can I eat?","What's a good grocery store that has vegan products?"};
    String listquestionanimal [] = new String [] {"What clothing lines are vegan?","Do makeup brands still test products on animals?"};
    String listquestionenergy [] = new String [] {"Do I have to unplug my devices after they're done charging to save energy?","How can I check which appliances use the most energy?","Which lightbulb brand is the most energy efficient?"};
    String listquestiontransport [] = new String[] {"I have to drive 1 hour every day to commute to work and I can't get around this. How else can I reduce my carbon footprint through transportation?","What modifications can I make to my car that will make it more eco-friendly?"};
    String listquestionzerowaste [] = new String[] {"Which is better, a steel straw or a bamboo straw?",
            "Should I wash my glass before recycling?",
            "What are some tips to avoid single use plastics?",
            "How can I make it more convenient to compost?",
            "What are some ways I can reuse plastic bags from shopping?",
            "I have a lot of makeup and beauty products and when I finish with them, I have a lot of empty bottles/containers. Is there a brand that reuses these or has more sustainable packaging?",
            "When is it ok to mix paper and plastic for recycling?",
        "Are there any plastics that are more harmful to health/environment than others?"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advice_forum);
        localTasks = new String[] {};
        localTopic = new String[] {};
        nbrResponse = new String[] {};
        questions = new String[] {};
        selectedTopic=null;
        //Depend on where we are from : determine the new topic
        Intent mIntent = getIntent();
        String previousActivity = mIntent.getStringExtra("FROM2");
        if (previousActivity.equals("ForumTopicSelect")){
            selectedTopic = ForumTopicSelect.chosenTopic;
            selectedIndex = ForumTopicSelect.chosenIndex;
        }
        else if (previousActivity.equals("askQuestion")){
            selectedTopic = askQuestion.suggestedTopic;
            selectedIndex = Methods.find(ForumTopicSelect.localTopic,askQuestion.suggestedTopic);
            localTopic = Methods.increaseArray(localTopic,askQuestion.suggestedTopic);
            localTasks = Methods.increaseArray(localTasks,askQuestion.suggestedTask);
            //Here add the length of the history retrieved from the database
            nbrResponse= Methods.increaseArray(nbrResponse,"0");
            questions= Methods.increaseArray(questions,askQuestion.questionSent);
            copyTasks = Methods.increaseArray(copyTasks,askQuestion.suggestedTask);
        }
        rawTasks=AddTaskSearch.rawTasks;
        rawTopic=AddTaskSearch.topicTasks;
        for (int i=0;i<rawTopic.length;i++){
            if(rawTopic[i].equals(selectedTopic)){
                localTopic = Methods.increaseArray(localTopic,selectedTopic);
                localTasks = Methods.increaseArray(localTasks,rawTasks[i]);
                nbrResponse= Methods.increaseArray(nbrResponse,"0");
                //questions= Methods.increaseArray(questions, "Why would I" + rawTasks[i]);
                copyTasks = Methods.increaseArray(copyTasks,rawTasks[i]);
            }
        }

        for (int i =0 ; i<localTasks.length ; i++){
            if (i==0){
                nbrResponse[i] = "3";
            }
            else {
                Random random = new Random();
                int nbr = random.nextInt(20);
                nbrResponse[i] = Integer.toString(nbr);
            }
            if (selectedIndex==0){
                questions = Methods.increaseArray(questions,listquestionsvegenism[i]);
            }
            else if (selectedIndex==1){
                questions = Methods.increaseArray(questions,listquestionanimal[i]);
            }
            else if (selectedIndex==2){
                questions = Methods.increaseArray(questions,listquestionenergy[i]);
            }
            else if (selectedIndex==3){
                questions = Methods.increaseArray(questions,listquestiontransport[i]);
            }
            else if (selectedIndex==4){
                questions = Methods.increaseArray(questions,listquestionzerowaste[i]);
            }
        }

        listAdvice = (ListView) findViewById(R.id.listAdvice);
        adviceForum.AdviceAdapter adviceAdapter = new adviceForum.AdviceAdapter();
        listAdvice.setAdapter(adviceAdapter);

        checkedItems = new boolean[copyTasks.length];
        filterList = copyTasks;
        filterButton = (Button) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder=new AlertDialog.Builder(adviceForum.this);
                mBuilder.setTitle("Filters available in the Forum");
                mBuilder.setMultiChoiceItems(filterList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!mUserItems.contains(position)) {
                                mUserItems.add(position);
                            }
                        }
                        else if (mUserItems.contains(position)){
                            for (int m =0 ;m<mUserItems.size();m++){
                                if (mUserItems.get(m)==position){
                                    mUserItems.remove(m);
                                }
                            }
                        }
                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        localTasks = new String[] {};
                        localTopic = new String[] {};
                        nbrResponse = new String[] {};
                        questions = new String[] {};
                        for (int j =0;j<rawTasks.length; j++){
                            for (int k=0;k<mUserItems.size();k++){
                                //comparison with the tasks selected
                                String cmp = filterList[mUserItems.get(k)];
                                if(rawTasks[j].equals(cmp)){
                                    localTasks = Methods.increaseArray(localTasks,rawTasks[j]);
                                    localTopic = Methods.increaseArray(localTopic,rawTopic[j]);
                                    nbrResponse = Methods.increaseArray(nbrResponse,"0");
                                    questions = Methods.increaseArray(questions,"Why would I"+rawTasks[j]);
                                }
                            }
                        }
                        if (localTasks.length!=0){
                            listAdvice.setVisibility(View.VISIBLE);
                            listAdvice = (ListView) findViewById(R.id.listAdvice);
                            adviceForum.AdviceAdapter adviceAdapter = new adviceForum.AdviceAdapter();
                            listAdvice.setAdapter(adviceAdapter);
                        }
                        else{
                            listAdvice.setVisibility(View.GONE);
                        }
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int k=0; k<checkedItems.length; k++){
                            checkedItems[k]=false;
                        }
                        mUserItems.clear();
//                        proposedTasks=rawTasks;
//                        localTasks=rawTasks;
//                        proposedTopic = topicTasks;
                        rawTasks=AddTaskSearch.rawTasks;
                        rawTopic=AddTaskSearch.topicTasks;
                        for (int m=0;m<rawTopic.length;m++){
                            if(rawTopic[m].equals(selectedTopic)){
                                localTopic = Methods.increaseArray(localTopic,selectedTopic);
                                localTasks = Methods.increaseArray(localTasks,rawTasks[m]);
                                nbrResponse= Methods.increaseArray(nbrResponse,"0");
                                questions= Methods.increaseArray(questions,"Why would I "+rawTasks[m]);
                            }
                        }
                        listAdvice = (ListView) findViewById(R.id.listAdvice);
                        adviceForum.AdviceAdapter adviceAdapter = new adviceForum.AdviceAdapter();
                        listAdvice.setAdapter(adviceAdapter);
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        listAdvice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                topicSend=localTopic[position];
                taskSend=localTasks[position];
                questionSend=questions[position];
                OpenConversation();
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }



    class AdviceAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return localTasks.length;
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
            view = getLayoutInflater().inflate(R.layout.advice_forum_layout,null);
            TextView questionID = (TextView)view.findViewById(R.id.questionID);
            TextView relatedTask = (TextView)view.findViewById(R.id.relatedTask);
            TextView nbrRep = (TextView)view.findViewById(R.id.nbrRep);
            questionID.setText(questions[i]);
            relatedTask.setText("Related task : "+localTasks[i]);
            nbrRep.setText("Responses : "+nbrResponse[i].toString());
            view.setForegroundGravity(Gravity.CENTER);
            view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
            return view;
        }
    }
    //Drawer Menu - Link to Activities
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.habit_tracker){
            Intent intent = new Intent(this, habitTracker.class);
            startActivity(intent);
        }
        if (id == R.id.add_goal){
            Intent intent = new Intent(this, AddTaskSearch.class);
            startActivity(intent);
        }
        if (id == R.id.challenges){
            Toast toast = Toast.makeText(getApplicationContext(),"Could you please implement the challenge activities ?",Toast.LENGTH_LONG);
            toast.show();
        }
        if (id== R.id.advice){
            OpenNewActivity();
        }
        if (id == R.id.forum){
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
    public void OpenNewActivity(){
        Intent intent = new Intent(this, askQuestion.class);
        intent.putExtra("FROM", "adviceForum");
        startActivity(intent);
    }
    public void OpenConversation(){
        Intent intent = new Intent(this, AskView.class);
        intent.putExtra("FROM3", "adviceForum");
        startActivity(intent);
    }
}
