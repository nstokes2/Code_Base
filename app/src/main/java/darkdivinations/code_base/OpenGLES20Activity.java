/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package darkdivinations.code_base;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.R.*;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
//import android.os.*;
//import android.os.AsyncTask;

public class OpenGLES20Activity extends Activity {

    private static MyGLSurfaceView mGLView;
    public boolean gameRun = true;
    public MediaPlayer mediaP;
    public float elapsedTime = 0.0f;
    public long oldTime = 0L;
    public long thisTime = 0L;
    public boolean threadBool = true;
    public class HelloRunnable implements Runnable{

        public void run(){
            Log.d("hello", "Hello");
            while(gameRun) {
                try {
                    Thread.sleep(1000);
                Log.d("HELLO", "sleeping");
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mGLView.mRenderer.cParser != null)
                                mGLView.mRenderer.cParser.elapsedTime += 1000;
                        }
                    });

                }catch(InterruptedException ie) {
                    return;
                }

            }
           /* for (int i = 0; i < importantInfo.length; i++) {
                // Pause for 4 seconds
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    // We've been interrupted: no more messages.
                    return;
                }
                // Print a message
                System.out.println(importantInfo[i]);
            }*/
        }
        public  void main(String args[]) {

            (new Thread(new HelloRunnable())).start();
        }



    }

    HelloRunnable helloRunnable;



         void threadMessage(String message){
            String threadName = Thread.currentThread().getName();
             Log.d(threadName, message);
        }
        private  class MessageLoop
                implements Runnable {
            public void run() {
                String importantInfo[] = {
                        "Mares eat oats",
                        "Does eat oats",
                        "Little lambs eat ivy",
                        "A kid will eat ivy too"
                };
                try {
                    for (int i = 0;
                         i < importantInfo.length;
                         i++) {
                        // Pause for 4 seconds
                        Thread.sleep(4000);
                        // Print a message
                        threadMessage(importantInfo[i]);
                    }
                } catch (InterruptedException e) {
                    threadMessage("I wasn't done!");
                }
            }
        }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
       // savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
       // savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
        public OpenGLES20Activity() throws InterruptedException
        {


        }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String key = b.getString("My Key");
            //txt.setText(txt.getText() + "Item " + key
                    //+System.getProperty("line.separator"));



         //   Log.d("Hello", "Item" + key + System.getProperty("line.separator"));

        }
    };
    @Override
    protected void onStart(){
        super.onStart();
       // helloRunnable.run();
        Thread background = new Thread(new Runnable() {

            @Override
            public void run() {
                int i = 0;
               while (gameRun) {
                    try {
                        Thread.sleep(1000);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (mGLView.mRenderer.cParser != null)
                                    mGLView.mRenderer.cParser.elapsedTime += 1000;
                            }
                        });
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        b.putString("My Key", "My Value: " + String.valueOf(i++));
                        msg.setData(b);
                        // send message to the handler with the current message handler
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        Log.v("Error", e.toString());
                    }
                }
            }
        });
        background.start();




    }

    public class myMedia{
       public int duration;
        public int position;
        public MediaPlayer mediaP;

        public myMedia(MediaPlayer media) {
            duration = 0;
            position = 0;
            mediaP = media;
            duration = mediaP.getDuration();


           // mediaP.
        }


    }
    myMedia jiggy;

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // mediaPlayer.stop();
           mediaP.release();
        gameRun = false;
    }
    //  @Override
    //  public void onUserInteraction()
    // {
    //      super.onUserInteraction();
    //  Log.d("user", "user");


    //  }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mediaP = MediaPlayer.create(this, R.raw.stand_for);
        jiggy = new myMedia(mediaP);
        mGLView = new MyGLSurfaceView(this, jiggy);
        // helloRunnable = new HelloRunnable();

        setContentView(mGLView);
       // firstRun = true;
       // new  RotateTheTarget().
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
        gameRun = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
        gameRun = true;
    }
}