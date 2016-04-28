/*
 * Copyright (c) 2016 Krumbs Inc
 * All rights reserved.
 *
 */
package io.krumbs.sdk.starter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import io.krumbs.sdk.starter.db.ProjectMethods;
import io.krumbs.sdk.starter.twitter.TwitterMethods;


public class ValidatePrioritiseActivity extends AppCompatActivity  {

    final Context context = this;

    String message;

    int vote_result;

    boolean suspendStream;

    Handler incomingMessageHandler;

    ArrayList<String> resourcesToValidate;
    HashMap<String, JSONObject> resourcesObjects;

    ListView resourceList;

    ArrayAdapter adapter;
    HashMap<String, Integer> resourceHashtags;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_validate_prioritise);

      resourcesObjects = new HashMap<>();
      resourceHashtags  = new HashMap<>();
      resourcesToValidate = new ArrayList<>();
      suspendStream = false;
      resourceList = (ListView) findViewById((R.id.listView));
      resourceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              validateDialogBox(resourcesToValidate.get(position));
              resourcesToValidate.remove(position);
              adapter.notifyDataSetChanged();
//              if(resourcesToValidate.size()==0){
//                  suspendStream=false;
//                  resourceList.setAdapter(null);
//                  resourceHashtags = new HashMap<String, Integer>();
////                  subscribe(incomingMessageHandler);
//              }
          }
      });

      String project_id = getIntent().getStringExtra("project_id");

      ProjectMethods.get("Task/project?project_id="+project_id, null, new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              // If the response is JSONObject instead of expected JSONArray
              System.out.println(response);
              try {
                  JSONArray task_list = response.getJSONArray("tasks");
                  for(int i=0; i<task_list.length(); i++){
                      JSONObject proj = task_list.getJSONObject(i);
                      resourcesToValidate.add(proj.getString("task_text"));
                      resourcesObjects.put(proj.getString("task_text"), proj);
                  }
                  addDataToUI();
              }catch (Exception e){

              }

          }

          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
              // Pull out the first event on the public timeline
              try {


                  //System.out.println(timeline);
              } catch (Exception e) {

              }
          }

      });


  }


    private void processTweetsForHashtags(String message){

        String[] words = message.split(" ");
        for(int i=0; i< words.length; i++){
            if(words[i].startsWith("#")){
                resourceHashtags.put(words[i],1);
            }
        }


    }

    public void setRelevantHashtagText() {

        TextView relevantTextField = (TextView) findViewById(R.id.validateTranslateTitle);
        String hashtags = "";
        for(Map.Entry<String, Integer> tag: resourceHashtags.entrySet()){
            hashtags = hashtags +" "+tag.getKey();
        }
        relevantTextField.setText("Current Topic(s):"+hashtags);

    }
    public void addDataToUI(){

        adapter = new ArrayAdapter<String>(ValidatePrioritiseActivity.this, android.R.layout.simple_list_item_1,resourcesToValidate);
        resourceList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    private void validateDialogBox(final String textToValidate){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Up/Down Vote Content?");

        // set dialog message
        alertDialogBuilder
                .setMessage(textToValidate)
                .setCancelable(false)
                .setPositiveButton("UpVote",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        try {
                            JSONObject tweet_resource = resourcesObjects.get(textToValidate);
                            String taskID = tweet_resource.getString("task_id");
                            TwitterMethods.PostTweetPRIO(taskID,true,null);
                        }catch(Exception e){

                        }
                        dialog.cancel();


                    }
                })
                .setNegativeButton("DownVote",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                       try {
                           JSONObject tweet_resource = resourcesObjects.get(textToValidate);
                           String taskID = tweet_resource.getString("task_id");
                           TwitterMethods.PostTweetPRIO(taskID, false, null);
                       }catch (Exception e){

                       }
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }







}
