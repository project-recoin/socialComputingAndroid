/*
 * Copyright (c) 2016 Krumbs Inc
 * All rights reserved.
 *
 */
package io.krumbs.sdk.starter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    final Context context = this;
    private GoogleMap mMap;
    TextView tv;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setupConnectionFactory();
        tv = (TextView) findViewById(R.id.textTwitterStream);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateDialogBox();
            }
        });

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

//                Date now = new Date();
//                SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                try {

                    message = msg.getData().getString("msg");
                    JSONObject obj = new JSONObject(message);
                    tv.setText(obj.getString("text") + '\n');
                    try {
                       JSONObject geos = obj.getJSONObject("geo");
                       LatLng msg_point = new LatLng(geos.getDouble("lat"), geos.getDouble("lng"));
                       mMap.addMarker(new MarkerOptions().position(msg_point).title(obj.getString("text")).snippet(message));
                       mMap.moveCamera(CameraUpdateFactory.newLatLng(msg_point));
                   }catch (Exception e1){

                   }


                }catch (Exception e){

                }


            }
        };

        subscribe(incomingMessageHandler);

    }

    private void validateDialogBox(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Validate this resource?");

        // set dialog message
        alertDialogBuilder
                .setMessage("Go Validate?")
                .setCancelable(false)
                .setPositiveButton("Yep",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent intent = new Intent(getApplicationContext(),KrumbsValidateActivity.class);
                        intent.putExtra("resource",message);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Maybe Later",new DialogInterface.OnClickListener() {
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               try {
                   validateDialogBox();

               }catch (Exception  e2){

               }
                return true;
            }
        });
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
