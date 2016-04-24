/*
 * Copyright (c) 2016 Krumbs Inc
 * All rights reserved.
 *
 */
package io.krumbs.sdk.starter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ValidateVerifyActivity extends AppCompatActivity {

    final Context context = this;

    String message;

    boolean suspendStream;

    Handler incomingMessageHandler;

    ArrayList<String> resourcesToValidate;

    ListView resourceList;

    ArrayAdapter adapter;
    HashMap<String, Integer> resourceHashtags;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_validate_verify);
//    View startCaptureButton = findViewById(R.id.kcapturebutton);
//    startCaptureButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
////            startCapture();
//            Intent intent = new Intent(getApplicationContext(),KrumbsValidateActivity.class);
//            startActivity(intent);
//
//        }
//    });

//      Button exploreButton = (Button) findViewById(R.id.buttonExplore);
//      exploreButton.setOnClickListener(new View.OnClickListener() {
//                                           @Override
//                                           public void onClick(View v) {
//                                               Intent intent = new Intent(getApplicationContext(),MapActivity.class);
//                                               startActivity(intent);
//
//                                           }
//                                       });
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
              if(resourcesToValidate.size()==0){
                  suspendStream=false;
                  resourceList.setAdapter(null);
                  resourceHashtags = new HashMap<String, Integer>();
                  subscribe(incomingMessageHandler);

              }
          }
      });



                    setupConnectionFactory();


                    incomingMessageHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            try {
                                String data = msg.getData().getString("msg");
                                JSONObject obj = new JSONObject(data);
                                message = obj.getString("text");
//                                TextView tv = (TextView) findViewById(R.id.tweetTextToValidate);
//                                tv.setText(message+'\n');
                                if(resourcesToValidate.size()>= 5    ){
                                    suspendStream = true;
                                    setRelevantHashtagText();
                                    addDataToUI();

                                }else {
                                    resourcesToValidate.add(message);
                                    processTweetsForHashtags(message);
                                }
                            }catch (Exception e){
                                    e.printStackTrace();
                            }

                        }
                    };


                    subscribe(incomingMessageHandler);


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


    ConnectionFactory factory = new ConnectionFactory();
    private void setupConnectionFactory() {
        String uri = "socpub.cloudapp.net";
        try {
            factory.setAutomaticRecoveryEnabled(false);
            //factory.setUri(uri);
            factory.setHost(uri);
            factory.setUsername("guest");
            factory.setPassword("sociam2015");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    private void validateDialogBox(String textToValidate){
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
//                        TweetComposer.Builder builder = new TweetComposer.Builder(context)
//                                .text("just setting up my Fabric.");
//                        builder.show();

                        dialog.cancel();

                    }
                })
                .setNegativeButton("Nope",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


//  private void startCapture() {
//    int containerId = R.id.camera_container;
//// SDK usage step 4 - Start the K-Capture component and add a listener to handle returned images and URLs
//    KrumbsSDK.startCapture(containerId, this, new KCaptureCompleteListener() {
//        @Override
//        public void captureCompleted(CompletionState completionState, boolean audioCaptured, Map<String, Object> map) {
//            // DEBUG LOG
//            if (completionState != null) {
//                Log.d("KRUMBS-CALLBACK", "STATUS" + ": " + completionState.toString());
//            }
//            if (completionState == CompletionState.CAPTURE_SUCCESS) {
//// The local image url for your capture
//                String imagePath = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_IMAGE_PATH);
//                if (audioCaptured) {
//// The local audio url for your capture (if user decided to record audio)
//                    String audioPath = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_AUDIO_PATH);
//                }
//// The mediaJSON url for your capture
//                String mediaJSONUrl = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_JSON_URL);
//                Log.i("KRUMBS-CALLBACK", mediaJSONUrl + ", " + imagePath);
//            } else if (completionState == CompletionState.CAPTURE_CANCELLED ||
//                    completionState == CompletionState.SDK_NOT_INITIALIZED) {
//                // code to handle cancellation and not-init states
//            }
//        }
//    });
//  }

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


    Thread subscribeThread;
    Thread publishThread;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        publishThread.interrupt();
        subscribeThread.interrupt();
    }



    void subscribe(final Handler handler)
    {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();
                        channel.basicQos(1);
                        DeclareOk q = channel.queueDeclare();
                        //channel.queueBind(q.getQueue(), "amq.fanout", "twitter_RECOIN");

                        String EXCHANGE_NAME = "twitter_RECOIN";

                        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
                        String queueName = channel.queueDeclare().getQueue();
                        channel.queueBind(queueName, EXCHANGE_NAME, "");

                        QueueingConsumer consumer = new QueueingConsumer(channel);
                        channel.basicConsume(queueName, true, consumer);

//                        QueueingConsumer consumer = new QueueingConsumer(channel);
//                        channel.basicConsume(q.getQueue(), true, consumer);

                        while (!suspendStream) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            String message = new String(delivery.getBody());
                            Log.d("","[r] " + message);
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", message);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e1) {
                        Log.d("", "Connection broken: " + e1.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });
        subscribeThread.start();
    }



}
