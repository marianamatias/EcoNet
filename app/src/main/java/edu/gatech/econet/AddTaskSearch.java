package edu.gatech.econet;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static android.view.View.VISIBLE;

public class AddTaskSearch extends AppCompatActivity {
    SearchView searchView;
    public static ArrayList<String> itemsLoc;
    ListView listTasks;
    TextView hint;
    EditText searchLabel;
    Button searchButton;
    // Hard coded listview to get to use the bundle and retrieve information to next activity
    String proposedTasks[] = new String [] {"Use a steel straw","Eat vegetarian","Avoid useless wastes","Recycle !","Avoid using plastic bottles","Limit personal commuting","Car pooling for the atmosphere",""};
    String localTasks[] = proposedTasks;


    public void show(View view){
        listTasks.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_search);
        for (int i=0;i<proposedTasks.length;i++){
            if(itemsLoc!=null){
                if (itemsLoc.contains(proposedTasks[i])){
                    localTasks= removeTheElement(localTasks,proposedTasks[i]);
                }
            }
        }


        //final Bundle bundleIn = getIntent().getExtras();
        hint = (TextView) findViewById(R.id.hintSearch);
        searchLabel = (EditText)findViewById(R.id.searchLabel);
        listTasks = (ListView) findViewById(R.id.listFound);
        listTasks.getBackground().setAlpha(80);
        searchButton=(Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localTasks=search(searchLabel,localTasks);
            }
        });

        searchLabel.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = searchLabel.getInputType(); // backup the input type
                searchLabel.setInputType(InputType.TYPE_NULL); // disable soft input
                searchLabel.onTouchEvent(event); // call native handler
                searchLabel.setInputType(inType); // restore input type
                v.performClick();
                return true; // consume touch even
            }
        });
        //searchView = (SearchView) findViewById(R.id.tasksearch);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,localTasks);


        //for (int i=0;i<proposedTasks.length;i++){
//            if (itemsLoc.contains(proposedTasks[i])){
//                listTasks[i].setBackgroud
//                setOnTiemTextColor(getTextColors().withAlpha(alpha));
//            }
        //}

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,proposedTasks) {
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//
//                View view = super.getView(position, convertView, parent);
//                TextView text = (TextView) view.findViewById(android.R.id.text1);
//                if(itemsLoc!=null){
//                    if (itemsLoc.contains(proposedTasks[position])) {
//                        text.setTextColor((Color.BLACK));
//                    }
//                }
//
//
//                return view;
//            }
//        };
//        for (int i=0;i<proposedTasks.length;i++){
//            adapter.getView(i,listTasks,listTasks);
//        }
        //adapter.getView();
        listTasks.setAdapter(adapter);

        listTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long is) {
                //Toast.makeText(AddTaskSearch.this,proposedTasks[position],Toast.LENGTH_SHORT).show();
                if (itemsLoc==null){
                    OpenNewActivityWithParam(localTasks[position]);
                }
                else if(itemsLoc.contains(localTasks[position])){
                    Toast toast = Toast.makeText(getApplicationContext(),"Task already in your habit tracker",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    OpenNewActivityWithParam(localTasks[position]);
                }

            }
        });
    }

    private void OpenNewActivityWithParam(String data1){
        itemsLoc = habitTracker.itemsSent;
        Intent intent = new Intent(this, ParamNewTask.class);
        Bundle bundleOut = new Bundle();
        bundleOut.putString("new_task",data1);
        intent.putExtras(bundleOut);
        startActivity(intent);
    }
    public static String[] removeTheElement(String[] arr, String seek){
        if (arr == null) {
            return arr;
        }
        String[] anotherArray = new String[arr.length - 1];
        for (int i = 0, k = 0; i < arr.length; i++) {
            if (arr[i].equals(seek)) {
                continue;
            }
            anotherArray[k++] = arr[i];
        }
        return anotherArray;
    }
    private static String[] search(TextView searchLabel, String localTasks[] ){
        if((searchLabel.getText()!=null)&&(localTasks!=null)){
            for (int i = 0;i<localTasks.length;i++){
                if((searchLabel.getText().toString()).equals(localTasks[i])){
                    localTasks=removeTheElement(localTasks,localTasks[i]);
                }
            }
        }
        return localTasks;
    }
}

