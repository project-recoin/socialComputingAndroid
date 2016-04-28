package io.krumbs.sdk.starter.twitter;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

/**
 * Created by SOCIAM-Demo on 28/04/2016.
 */
public class TwitterMethods {


    public static void PostTweetRESOLVE(String taskID, boolean isRelevant, String additionalContent) {


        String resolveMsg;
        if (isRelevant) {
            resolveMsg = "@YouCompute RESOLVE \"Relevant Content\"";
        } else {
            resolveMsg = "@YouCompute RESOLVE \"Non-Relevant Content\"";
        }

        if(additionalContent!=null) {
            if (additionalContent.length() < 80) {
                resolveMsg = resolveMsg + " NOTE:" + additionalContent;
            }
        }

        resolveMsg = resolveMsg + " #" + taskID;

        if (resolveMsg.length() > 140) {
            System.out.println("Ops, Tweet is too long!");
        }

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        twitterApiClient.getStatusesService().update(resolveMsg, null, null, null, null, null, null, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                System.out.println("Successfully tweeted RESOLVE");
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }


    public static void PostTweetSHARE(String taskID, boolean toShare, String additionalContent) {


        String resolveMsg = "";
        if (toShare) {
            resolveMsg = "@YouCompute SHARE";
        }

        if (additionalContent != null) {
            if (additionalContent.length() < 80) {
                resolveMsg = resolveMsg + " NOTE:" + additionalContent;
            }
        }

        resolveMsg = resolveMsg + " #" + taskID;

        if (resolveMsg.length() > 140) {
            System.out.println("Ops, Tweet is too long!");
        }

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        twitterApiClient.getStatusesService().update(resolveMsg, null, null, null, null, null, null, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                System.out.println("Successfully tweeted SHARE");
            }

            @Override
            public void failure(TwitterException e) {

            }
        });


    }
        public static void PostTweetPRIO(String taskID, boolean upVote, String additionalContent) {


            String priorMsg;
            if (upVote) {
                priorMsg = "@YouCompute PRIO +1";
            } else {
                priorMsg = "@YouCompute PRIO -1";
            }

            if(additionalContent!=null) {
                if (additionalContent.length() < 80) {
                    priorMsg = priorMsg + " NOTE:" + additionalContent;
                }
            }

            priorMsg = priorMsg + " #" + taskID;

            if (priorMsg.length() > 140) {
                System.out.println("Ops, Tweet is too long!");
            }

            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
            twitterApiClient.getStatusesService().update(priorMsg, null, null, null, null, null, null, null, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    System.out.println("Successfully tweeted PRIO");
                }

                @Override
                public void failure(TwitterException e) {

                }
            });
        }

   }




