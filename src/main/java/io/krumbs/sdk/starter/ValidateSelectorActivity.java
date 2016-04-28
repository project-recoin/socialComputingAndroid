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
import android.widget.TextView;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;


public class ValidateSelectorActivity extends AppCompatActivity {

  String project_id;
    String project_text;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_validate_selection);

      project_id = getIntent().getStringExtra("project_id");
      project_text = getIntent().getStringExtra("project_text");

      TextView contributeToProjectText = (TextView) findViewById(R.id.contToProjText);
      contributeToProjectText.setText("Contributing to Active Project '"+project_text+"'");


      Button verifyTaskButton = (Button) findViewById(R.id.buttonVerifyTask);
      verifyTaskButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Intent intent = new Intent(getApplicationContext(),ValidateVerifyActivity.class);
                                               intent.putExtra("project_id",project_id);
                                               startActivity(intent);

                                           }
                                       });

      Button translateButton = (Button) findViewById(R.id.buttonTranslateTask);
      translateButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(getApplicationContext(),ValidateTranslateActivity.class);
              intent.putExtra("project_id",project_id);
              startActivity(intent);

          }
      });

      Button annotateButton = (Button) findViewById(R.id.buttonAnnotateTask);
      annotateButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(getApplicationContext(),ValidateAnnotateActivity.class);
              intent.putExtra("project_id",project_id);
              startActivity(intent);

          }
      });


      Button voteButton = (Button) findViewById(R.id.buttonVoteTask);
      voteButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(getApplicationContext(),ValidatePrioritiseActivity.class);
              intent.putExtra("project_id",project_id);
              startActivity(intent);

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }







}
