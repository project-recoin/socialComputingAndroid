/*
 * Copyright (c) 2016 Krumbs Inc
 * All rights reserved.
 *
 */
package io.krumbs.sdk.starter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;


public class ValidateSelectorActivity extends AppCompatActivity {



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_validate_selection);


      Button verifyTaskButton = (Button) findViewById(R.id.buttonVerifyTask);
      verifyTaskButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Intent intent = new Intent(getApplicationContext(),ValidateVerifyActivity.class);
                                               startActivity(intent);

                                           }
                                       });

      Button translateButton = (Button) findViewById(R.id.buttonTranslateTask);
      translateButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(getApplicationContext(),ValidateTranslateActivity.class);
              startActivity(intent);

          }
      });

      Button annotateButton = (Button) findViewById(R.id.buttonAnnotateTask);
      translateButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(getApplicationContext(),ValidateAnnotateActivity.class);
              startActivity(intent);

          }
      });



      //      setupConnectionFactory();
              //
              //
              //      final Handler incomingMessageHandler = new Handler() {
              //          @Override
              //          public void handleMessage(Message msg) {
              //              String message = msg.getData().getString("msg");
              //              TextView tv = (TextView) findViewById(R.id.textView);
              //              Date now = new Date();
              //              SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
              //              tv.append(ft.format(now) + ' ' + message + '\n');
              //          }
              //      };
              //      subscribe(incomingMessageHandler);


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

                        while (true) {
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
