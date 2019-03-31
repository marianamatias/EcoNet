package edu.gatech.econet;

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
import android.widget.BaseAdapter;
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


public class adviceForum extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    public static String questions[] = new String[] {"?","??","???","????","?????","??????"};
    public static String localTasks[] = new String[] {"Alimentation","Animal","Energy","Transportation","Zero Waste","Other"};
    public static Integer nbrResponse[] = new Integer[] {0,0,0,0,0,0};
    ListView listAdvice=null;
    public static String selectedTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advice_forum);
        selectedTopic = ForumTopicSelect.chosenTopic;
        listAdvice = (ListView) findViewById(R.id.listAdvice);
        adviceForum.AdviceAdapter adviceAdapter = new adviceForum.AdviceAdapter();
        listAdvice.setAdapter(adviceAdapter);
        listAdvice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast toast = Toast.makeText(adviceForum.this,"You selected "+selectedTopic,Toast.LENGTH_LONG);
                //toast.show();

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    class AdviceAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return questions.length;
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
            relatedTask.setText(localTasks[i]);
            nbrRep.setText(nbrResponse[i].toString());
            view.setForegroundGravity(Gravity.CENTER);
            view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
            return view;
        }
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
            OpenNewActivity();
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
}
