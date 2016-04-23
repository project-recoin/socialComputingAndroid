/*
 * Copyright (c) 2016 Krumbs Inc
 * All rights reserved.
 *
 */
package io.krumbs.sdk.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.*;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.*;


import java.util.Map;

import io.krumbs.sdk.KrumbsSDK;
import io.krumbs.sdk.krumbscapture.KCaptureCompleteListener;


public class MainActivity extends AppCompatActivity {

    private TwitterLoginButton loginButton;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
//    View startCaptureButton = findViewById(R.id.kcapturebutton);
//    startCaptureButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            startCapture();
//        }
//    });

      loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
      loginButton.setCallback(new Callback<TwitterSession>() {
          @Override
          public void success(Result<TwitterSession> result) {
              // The TwitterSession is also available through:
              // Twitter.getInstance().core.getSessionManager().getActiveSession()
              TwitterSession session = result.data;
              // TODO: Remove toast and use the TwitterSession's userID
              // with your app's user model
              String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
              Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();



          }
          @Override
          public void failure(TwitterException exception) {
              Log.d("TwitterKit", "Login with Twitter failure", exception);
          }
      });


        //polymer!

      // TODO: Use a more specific parent
      final ViewGroup parentView = (ViewGroup) getWindow().getDecorView().getRootView();
      // TODO: Base this Tweet ID on some data from elsewhere in your app
      long tweetId = 631879971628183552L;
      TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
          @Override
          public void success(Result<Tweet> result) {
              TweetView tweetView = new TweetView(MainActivity.this, result.data);
              //parentView.addView(tweetView);
          }
          @Override
          public void failure(TwitterException exception) {
              Log.d("TwitterKit", "Load Tweet failure", exception);
          }
      });





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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any`
        // Activity that it triggered.
        Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
        startActivity(intent);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


}
