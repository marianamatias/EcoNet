package edu.gatech.econet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

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
        //final String apiKey = BuildConfig.ApiKey;
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
                //chosenTopic = localTopic[position];
                //OpenNewActivity();
                JSONObject test = new JSONObject ();
                String[] tagList = new String[] {"Energy","Animal"};
                try {
                    test.put("task", new JSONArray(tagList));
                    test.put("tag","Energy");
                }
                catch (JSONException e){
                }
                //Log.d("salut",test.toString());
                RequestParams rp = new RequestParams();
                //rp.add("task", "recycle"); rp.add("tag", "recycle bottles");
                //rp.put("api_key","False");
                rp.put("data",1);
                Log.d("salut",rp.toString());
                HttpUtils.postByUrl("http://www.fir-auth-93d22.appspot.com/example", rp, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        //Log.d("asd", "---------------- this is response : " + response);
                        try {
                            JSONObject serverResp = new JSONObject(response.toString());
                            Log.d("salut","yo");
                            //String jsonString = serverResp.toString();
                            //Toast.makeText(getApplicationContext(),jsonString,Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Log.d("salut","yomauvais");
                        }
                    }
                    //Only to fill if something to do ONCE THE JSON file is received
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                        Log.d("salut","yo");
                    }
                });
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
        startActivity(intent);
    }

}
