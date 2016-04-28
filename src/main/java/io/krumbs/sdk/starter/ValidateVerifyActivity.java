/*
 * Copyright (c) 2016 Krumbs Inc
 * All rights reserved.
 *
 */
package io.krumbs.sdk.starter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.test.ActivityTestCase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.Card;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import io.krumbs.sdk.starter.db.ProjectMethods;
import io.krumbs.sdk.starter.twitter.TwitterMethods;


public class ValidateVerifyActivity extends Activity {

    final Context context = this;

    String message;

    boolean suspendStream;

    Handler incomingMessageHandler;

    ArrayList<String> resourcesToValidate;
    HashMap<String, JSONObject> resourcesObjects;

    ListView resourceList;

    ArrayAdapter adapter;
    HashMap<String, Integer> resourceHashtags;

    int TWEET_COMPOSER_RESULT;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_validate_verify);

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

//                    setupConnectionFactory();
//
//
//                    incomingMessageHandler = new Handler() {
//                        @Override
//                        public void handleMessage(Message msg) {
//                            try {
//                                String data = msg.getData().getString("msg");
//                                JSONObject obj = new JSONObject(data);
//                                message = obj.getString("text");
////                                TextView tv = (TextView) findViewById(R.id.tweetTextToValidate);
////                                tv.setText(message+'\n');
//                                if(resourcesToValidate.size()>= 5    ){
//                                    suspendStream = true;
//                                    setRelevantHashtagText();
//                                    addDataToUI();
//
//                                }else {
//                                    resourcesToValidate.add(message);
//                                    processTweetsForHashtags(message);
//                                }
//                            }catch (Exception e){
//                                    e.printStackTrace();
//                            }
//
//                        }
//                    };
//
//
//                    subscribe(incomingMessageHandler);


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

        adapter = new ArrayAdapter<String>(ValidateVerifyActivity.this, android.R.layout.simple_list_item_1,resourcesToValidate);
        resourceList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


//    ConnectionFactory factory = new ConnectionFactory();
//    private void setupConnectionFactory() {
//        String uri = "socpub.cloudapp.net";
//        try {
//            factory.setAutomaticRecoveryEnabled(false);
//            //factory.setUri(uri);
//            factory.setHost(uri);
//            factory.setUsername("guest");
//            factory.setPassword("sociam2015");
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//    }


    private void validateDialogBox(final String textToValidate){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Relevant Resource?");

        // set dialog message
        alertDialogBuilder
                .setMessage(textToValidate)
                .setCancelable(false)
                .setPositiveButton("Yep",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
//                        Intent intent = new Intent(getApplicationContext(),KrumbsValidateActivity.class);
//                        intent.putExtra("resource",message);
//                        startActivity(intent);
//                        suspendStream=false;
//                        subscribe(incomingMessageHandler);

//                        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
//                                .getActiveSession();
//                        final Intent intent = new ComposerActivity.Builder().Builder(context)
//                                .session(session)
//                                .createIntent();
//                        startActivity(intent);
                        try {
                            JSONObject tweet_resource = resourcesObjects.get(textToValidate);
                            String taskID = tweet_resource.getString("task_id");
//                            TweetComposer.Builder builder = new TweetComposer.Builder(context)
//                                    .text("@YouCompute RESOLVE \"Relevant Content\" #"+taskID);
//                            builder.show();
//                            builder.createIntent();
//                            Intent intent = new TweetComposer.Builder(context)
//                                    .text("@YouCompute RESOLVE \"Relevant Content\" #"+taskID)
//                                    .createIntent();
//                            startActivityForResult(intent, TWEET_COMPOSER_RESULT);
//                            setResult(Activity.RESULT_OK, intent);

//                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
//                                twitterApiClient.getStatusesService().update("@YouCompute RESOLVE \"Relevant Content\" #"+taskID,  null, null, null, null, null, null, null, new Callback<Tweet>() {
//                                    @Override
//                                    public void success(Result<Tweet> result) {
//                                        System.out.println("Successfully tweeted result");
//
//                                    }
//
//                                    @Override
//                                    public void failure(TwitterException e) {
//
//                                    }
//                            });

                            TwitterMethods.PostTweetRESOLVE(taskID,true,null);
                            dialog.cancel();

                        }catch(Exception e){

                        }
//                     final TwitterSession session = TwitterCore.getInstance().getSessionManager()
//                                .getActiveSession();
//                        final Intent intent = new ComposerActivity.Builder(ValidateVerifyActivity.this)
//                                .session(session)
//                                .createIntent();
//                        startActivity(intent);

                    }
                })
                .setNegativeButton("Nope",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        try {
                            JSONObject tweet_resource = resourcesObjects.get(textToValidate);
                            String taskID = tweet_resource.getString("task_id");
                            TwitterMethods.PostTweetRESOLVE(taskID, false, null);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        System.out.println("TWEET Sent TWEET_COMPOSER_RESULT: "+requestCode+" ResultCode: "+resultCode+" data: "+data.toString());
        if (requestCode == TWEET_COMPOSER_RESULT) {
            // Make sure the request was successful
            System.out.println("TWEET Sent TWEET_COMPOSER_RESULT: "+requestCode);
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
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



//    void subscribe(final Handler handler)
//    {
//        subscribeThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true) {
//                    try {
//                        Connection connection = factory.newConnection();
//                        Channel channel = connection.createChannel();
//                        channel.basicQos(1);
//                        DeclareOk q = channel.queueDeclare();
//                        //channel.queueBind(q.getQueue(), "amq.fanout", "twitter_RECOIN");
//
//                        String EXCHANGE_NAME = "twitter_RECOIN";
//
//                        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
//                        String queueName = channel.queueDeclare().getQueue();
//                        channel.queueBind(queueName, EXCHANGE_NAME, "");
//
//                        QueueingConsumer consumer = new QueueingConsumer(channel);
//                        channel.basicConsume(queueName, true, consumer);
//
////                        QueueingConsumer consumer = new QueueingConsumer(channel);
////                        channel.basicConsume(q.getQueue(), true, consumer);
//
//                        while (!suspendStream) {
//                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//                            String message = new String(delivery.getBody());
//                            Log.d("","[r] " + message);
//                            Message msg = handler.obtainMessage();
//                            Bundle bundle = new Bundle();
//                            bundle.putString("msg", message);
//                            msg.setData(bundle);
//                            handler.sendMessage(msg);
//                        }
//                    } catch (InterruptedException e) {
//                        break;
//                    } catch (Exception e1) {
//                        Log.d("", "Connection broken: " + e1.getClass().getName());
//                        try {
//                            Thread.sleep(5000); //sleep and then try again
//                        } catch (InterruptedException e) {
//                            break;
//                        }
//                    }
//                }
//            }
//        });
//        subscribeThread.start();
//    }



}
