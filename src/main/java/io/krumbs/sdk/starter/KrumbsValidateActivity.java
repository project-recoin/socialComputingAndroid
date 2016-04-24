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
import android.widget.TextView;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONObject;

import java.util.Map;

import io.krumbs.sdk.KrumbsSDK;
import io.krumbs.sdk.krumbscapture.KCaptureCompleteListener;


public class KrumbsValidateActivity extends AppCompatActivity {


    String resource;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_krumbs);
//    View startCaptureButton = findViewById(R.id.kcapturebutton);
//    startCaptureButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
////          startCapture();
//            Intent intent = new Intent(getApplicationContext(),MapActivity.class);
//            startActivity(intent);
//
//        }
//    });
//


      startCapture();

  }




  private void startCapture() {
    int containerId = R.id.camera_container;
// SDK usage step 4 - Start the K-Capture component and add a listener to handle returned images and URLs
    KrumbsSDK.startCapture(containerId, this, new KCaptureCompleteListener() {
        @Override
        public void captureCompleted(CompletionState completionState, boolean audioCaptured, Map<String, Object> map) {
            // DEBUG LOG
            if (completionState != null) {
                Log.d("KRUMBS-CALLBACK", "STATUS" + ": " + completionState.toString());
            }
            if (completionState == CompletionState.CAPTURE_SUCCESS) {
// The local image url for your capture
                String imagePath = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_IMAGE_PATH);
                if (audioCaptured) {
// The local audio url for your capture (if user decided to record audio)
                    String audioPath = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_AUDIO_PATH);
                }
// The mediaJSON url for your capture
                String mediaJSONUrl = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_JSON_URL);
                Log.i("KRUMBS-CALLBACK", mediaJSONUrl + ", " + imagePath);
            } else if (completionState == CompletionState.CAPTURE_CANCELLED ||
                    completionState == CompletionState.SDK_NOT_INITIALIZED) {
                // code to handle cancellation and not-init states
            }
        }
    });
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








}
