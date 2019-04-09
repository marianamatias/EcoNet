package edu.gatech.econet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ForumTopicSelect extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    ListView listTopic=null;
    int[] icons = {R.drawable.alimentation, R.drawable.animal, R.drawable.energy, R.drawable.transportation, R.drawable.zero_waste};
    public static String localTopic[] = new String[] {"Vegetarianism","Animal","Energy","Transportation","Zero Waste"};
    public static String chosenTopic;
    public static int chosenIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_topic_select);
        //final String apiKey = BuildConfig.ApiKey;
        chosenTopic=null;
        chosenIndex = -1;
        listTopic = (ListView) findViewById(R.id.listTopic);
        ForumAdapter forumAdapter = new ForumAdapter();
        listTopic.setAdapter(forumAdapter);
        listTopic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                chosenTopic=localTopic[position];
                chosenIndex = position;
                OpenNewActivity();
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        listTopic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long is) {
                chosenTopic = localTopic[position];
                chosenIndex = position;
                OpenNewActivity();
            }
        });

        // request to database

        //Post on the database
        // To fill our link to the database via api key
        //Be careful api key is a SENSITIVE information : github is dangerous !
        // See that tutorial to get rid of that problem !
        //https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906
//        JSONObject jsonObject = new JSONObject();
//        String jsonString = jsonObject.toString();
//        try {
//            JSONObject jsonObjectr = new JSONObject(jsonString);
//        }
//        catch (JSONException e){
//
//        }
    }

    private void OpenNewActivity(){
        Intent intent = new Intent(this, adviceForum.class);
        intent.putExtra("FROM2", "ForumTopicSelect");
        startActivity(intent);
    }
    class ForumAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return icons.length;
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
            view = getLayoutInflater().inflate(R.layout.forum_listview_layout,null);
            ImageView topicIcon = (ImageView)view.findViewById(R.id.topic_icon);
            TextView topicName = (TextView)view.findViewById(R.id.topic_name);
            topicIcon.setImageResource(icons[i]);
            topicName.setText(localTopic[i]);
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
            Intent intent = new Intent(this, askQuestion.class);
            intent.putExtra("FROM", "habitTracker_menu");
            startActivity(intent);
        }
        if (id == R.id.forum){
            //Already in it
            //Intent intent = new Intent(this, ForumTopicSelect.class);
            //startActivity(intent);
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
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
    }
}
