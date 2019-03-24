package edu.gatech.econet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ForumTopicSelect extends AppCompatActivity {
    ListView listTopic=null;
    ListView listImage=null;
    ArrayAdapter<String> adapter=null;
    ArrayAdapter<String> adapterImage=null;
    //String topic[]=null;
    public static String localTopic[] = new String[] {"Alimentation","Animal","Energy","Transportation","Zero Waste"};
    private String localImage[] = new String[] {"","","","",""};
    private static String chosenTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_topic_select);
        listTopic = (ListView) findViewById(R.id.listTopic);
        listImage = (ListView) findViewById(R.id.listImage);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, localTopic) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                switch (position) {
                    case 0:
                        //view.setForegroundGravity(Gravity.CENTER);
                        view.setBackgroundResource(R.drawable.alimentation);
                        view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
                        view.getLayoutParams().height=150;
                        break;
                    case 1:
                        //view.setBackgroundResource(R.drawable.animal);
                        view.setForegroundGravity(Gravity.CENTER);
                        view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
                        view.getLayoutParams().height=150;
                        break;
                    case 2:
                        //view.setBackgroundResource(R.drawable.energy);
                        view.setForegroundGravity(Gravity.CENTER);
                        view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
                        view.getLayoutParams().height=150;
                        break;
                    case 3:
                        //view.setBackgroundResource(R.drawable.transportation);
                        view.setForegroundGravity(Gravity.CENTER);
                        view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
                        view.getLayoutParams().height=150;
                        break;
                    case 4:
                        //view.setBackgroundResource(R.drawable.zero_waste);
                        view.setForegroundGravity(Gravity.CENTER);
                        view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
                        view.getLayoutParams().height=150;
                        break;
                }

                //String tmp = localDrawable[position];
                //view.setBackgroundResource(R.drawable.tmp);
                return view;
            }
        };
        listTopic.setAdapter(adapter);

        adapterImage = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, localImage) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                switch (position) {
                    case 0:
                        //view.setForegroundGravity(Gravity.CENTER);
                        view.setBackgroundResource(R.drawable.alimentation);
                        view.getLayoutParams().width=150;
                        view.getLayoutParams().height=150;

                        break;
                    case 1:
                        view.setBackgroundResource(R.drawable.animal);
                        //view.setForegroundGravity(Gravity.CENTER);
                        view.getLayoutParams().width=150;
                        view.getLayoutParams().height=150;

                        break;
                    case 2:
                        view.setBackgroundResource(R.drawable.energy);
                        //view.setForegroundGravity(Gravity.CENTER);
                        view.getLayoutParams().width=150;
                        view.getLayoutParams().height=150;

                        break;
                    case 3:
                        view.setBackgroundResource(R.drawable.transportation);
                        //view.setForegroundGravity(Gravity.CENTER);
                        view.getLayoutParams().width=150;
                        view.getLayoutParams().height=150;

                        break;
                    case 4:
                        view.setBackgroundResource(R.drawable.zero_waste);
                        //view.setForegroundGravity(Gravity.CENTER);
                        view.getLayoutParams().width=150;
                        view.getLayoutParams().height=150;

                        break;
                }

                //String tmp = localDrawable[position];
                //view.setBackgroundResource(R.drawable.tmp);
                return view;
            }
        };
        listImage.setAdapter(adapterImage);

        listTopic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long is) {
                chosenTopic = localTopic[position];
                OpenNewActivity();
            }
        });
    }

    private void OpenNewActivity(){
        Intent intent = new Intent(this, adviceForum.class);
        startActivity(intent);
    }

}
