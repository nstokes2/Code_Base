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

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.opengl.GLU;
import android.util.Log;
import android.media.MediaPlayer;


/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */


public class MyGLSurfaceView extends GLSurfaceView {

    public final MyGLRenderer mRenderer;
    public Context context;
    OpenGLES20Activity.myMedia mediaP;


    public MyGLSurfaceView(Context contexta, OpenGLES20Activity.myMedia mediap) {
        super(contexta);
    context = contexta;
        mediaP = mediap;
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    public static  float smallNum = 0.00000001f;
    public  Boolean play = null;

    public class RotateTheTarget extends AsyncTask<Float, Float, Long>
    {    public boolean firstRun = false;
        long elapsedTime = 0L;
        long thisTime = 0L;
        long oldTime = 0L;

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            Log.d("Et", Float.toString(elapsedTime));
            //  Log.d("thisTime", Float.toString(thisTime));
        }

        @Override
        protected Long doInBackground(Float... params) {
//try {

                while (elapsedTime < 100L) {
                    if(isCancelled())
                        break;
                    thisTime = System.currentTimeMillis();

                    if(oldTime != 0L)
                        elapsedTime += thisTime - oldTime;
                    oldTime = thisTime;
                    publishProgress();

                }
            Log.d("returning", "returning");
            return elapsedTime;
        }

        @Override
        protected void onCancelled(Long aLong) {


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // if(mGLView != null)
             //   if(mGLView.play != null)
               //     if(mGLView.play) {
                 //       firstRun = true;
                        Log.d("fr", "fr");
                    }


        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

          //  if(mGLView != null)
            //    if(mGLView.mRenderer != null )
               //     if(mGLView.mRenderer.cParser != null)
                        if(mRenderer.cParser.elapsedTime != null)
                          mRenderer.cParser.elapsedTime += Float.parseFloat(Long.toString(aLong));
        }
    }

    RotateTheTarget targ;

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        if(play == null)
            play = false;
        for(int i =0; i< mRenderer.cParser.geometries.size(); i++) {
            mRenderer.cParser.geometries.get(i).picked = false;
            //    Log.d("is it false", Boolean.toString( mRenderer.cParser.geometries.get(i).picked));
        }

        for(int i = 0; i< mRenderer.iL.boolData.length; i++)
            mRenderer.iL.boolData[i] = false;
        float x = e.getX();
        float y = e.getY();
    float temp[] = new float[16];
    float temp2[] = new float[16];
        float near[] = new float[3];
        float far[] = new float[3];
       // Log.d("up", "UP");
        GLU.gluUnProject(x, y, 0.0f, mRenderer.viewMatrix, 0, mRenderer.projectionMatrix, 0, mRenderer.viewport, 0, temp, 0);
        Matrix.multiplyMV(temp2, 0, mRenderer.viewMatrix, 0, temp, 0);

        far[0] = temp2[0]/temp2[3];
        far[1] = temp2[2]/temp2[3];
        far[2] = -temp2[1]/temp2[3];


      //  Log.d("up", Float.toString(far[0]) + " " + Float.toString(far[1]) + " " + Float.toString(far[2]));
        GLU.gluUnProject(x, y, 1.0f, mRenderer.viewMatrix, 0, mRenderer.projectionMatrix, 0, mRenderer.viewport, 0, temp, 0);
        Matrix.multiplyMV(temp2, 0, mRenderer.viewMatrix, 0, temp, 0);

        near[0] = temp2[0]/temp2[3];
        near[1] = temp2[2]/temp2[3];
        near[2] = -temp2[1]/temp2[3];
       // Log.d("up 2", Float.toString(near[0]) + " " + Float.toString(near[1]) + " " + Float.toString(near[2]) );
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
               // Log.d("hello", "hello");
                //case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                float[] uT, vT, nT;
                float[] rDir, rW0, rW;
                float r, a, b;
                boolean picked = false;
                float smallestX= 100;
                float biggestX = -100;
                float smallestY = 100;
                float biggestY = -100;
                float smallestZ = 100;
                float biggestZ = -100;
                for(int j = 0; j<mRenderer.cParser.geometries.size(); j++)
                    for(int i = 0; i< mRenderer.cParser.geometries.get(j).triangleCount*3; i+=9) {
                        picked = true;
                        float [] a0 = new float[4];
                        float [] a1 = new float[4];
                        float[] a2 = new float[4];
                        float[] b0 = new float[4];
                        float[] b1 = new float[4];
                        float[] b2 = new float[4];
                        float [] v0 = new float[3];
                        float [] v1 = new float[3];
                        float [] v2 = new float[3];

                        a0[0]= mRenderer.cParser.geometries.get(j).vertices[i];
                        a0[1]= mRenderer.cParser.geometries.get(j).vertices[i+1];
                        a0[2]= mRenderer.cParser.geometries.get(j).vertices[i+2];
                        a0[3] = 1;


                        a1[0]= mRenderer.cParser.geometries.get(j).vertices[i+3];
                        a1[1]= mRenderer.cParser.geometries.get(j).vertices[i+4];
                        a1[2]= mRenderer.cParser.geometries.get(j).vertices[i+5];
                        a1[3] = 1;

                        a2[0]= mRenderer.cParser.geometries.get(j).vertices[i+6];
                        a2[1]= mRenderer.cParser.geometries.get(j).vertices[i+7];
                        a2[2]= mRenderer.cParser.geometries.get(j).vertices[i+8];
                        a2[3] = 1;
                        float [] mixedMatrix  =new float[16];

                        float[] identity = new float[16];
                        Matrix.setIdentityM(identity, 0);
                        float[] mVMatrix = new float[16];

                        Matrix.translateM(identity, 0, mRenderer.cParser.geometries.get(j).translate[0], mRenderer.cParser.geometries.get(j).translate[1], mRenderer.cParser.geometries.get(j).translate[2]);

                        Matrix.multiplyMM(mVMatrix, 0, mRenderer.viewMatrix, 0, mRenderer.cParser.worlds.get(j), 0);
                        Matrix.multiplyMV(b0, 0, mVMatrix, 0, a0, 0);
                        Matrix.multiplyMV(b1, 0, mVMatrix, 0, a1, 0);
                        Matrix.multiplyMV(b2, 0, mVMatrix, 0, a2, 0);
                      //  if(j == 2)
                        //    Log.d("b0", Float.toString(b0[0]) + " " + Float.toString(b0[1]) + " " + Float.toString(b0[2]));
                       // if(j == 2)
                         //   Log.d("b1", Float.toString(b1[0]) + " " + Float.toString(b1[1]) + " " + Float.toString(b1[2]));
                       // if(j == 2)
                         //   Log.d("b2", Float.toString(b2[0]) + " " + Float.toString(b2[1]) + " " + Float.toString(b2[2]));
                        float[] tV = new float[4];
                        tV[0] = 0.0f;
                        tV[1] = 0.0f;
                        tV[2] = 0.0f;
                        tV[3] = 1.0f;
                        float[] tV2 = new float[4];
                        Matrix.multiplyMV(tV2, 0, mVMatrix, 0, tV, 0);
                        float[] tV3 = new float[3];
                        tV3[0] = tV2[0]/tV2[3];
                        tV3[1] = tV2[1]/tV2[3];
                        tV3[2] = tV2[2]/tV2[3];

                        v0[0] = b0[0]/b0[3];
                        v0[1] = b0[1]/b0[3];
                        v0[2] = b0[2]/b0[3];

                        v1[0] = b1[0]/b1[3];
                        v1[1] = b1[1]/b1[3];
                        v1[2] = b1[2]/b1[3];

                        v2[0] = b2[0]/b2[3];
                        v2[1] = b2[1]/b2[3];
                        v2[2] = b2[2]/b2[3];
              
                        uT = myVector.minus(v1, v0);
                        vT = myVector.minus(v2, v0);
                        nT = myVector.crossProduct(uT, vT);

                        if(nT.equals(new float[]{0.0f,  0.0f, 0.0f})) {
                            Log.d("degenerate", "Degenerate");


                        }
                        else
                        {
                            rDir = myVector.minus(near, far);
                            rW0 = myVector.minus(far, v0);
                            a = -myVector.dot(nT, rW0);
                            b = myVector.dot(nT, rDir);

                            if(Math.abs(b) < smallNum){
                                if(a==0)
                                    picked = true;
                                else
                                    picked = false;
                            }

                            r = a/b;
                            if(r<0.0f)
                                picked = false;

                            float[] tempI = myVector.addition(far, myVector.scalarProduct(r, rDir));
                            float []I = new float[3];

                            I[0] = tempI[0];
                            I[1] = tempI[1];
                            I[2] = tempI[2];

                            // Log.d("This is the I", Float.toString(I[0])+ " " + Float.toString(I[1]) + " " + Float.toString(I[2]));

                            float uu, uv, vv, wu, wv, D;
                            uu = myVector.dot(uT,uT);
                            uv = myVector.dot(uT, vT);
                            vv = myVector.dot(vT, vT);
                            rW = myVector.minus(I, v0);
                            wu = myVector.dot(rW, uT);
                            wv = myVector.dot(rW, vT);
                            D = (uv * uv) - (uu * vv);

                            float s, t;
                            s = ((uv * wv) - (vv * wu)) / D;
                            if(s < 0.0f || s > 1.0f)
                                picked = false;
                            t = (uv * wu - uu * wv) / D;
                            if(t < 0.0f || (s + t) > 1.0f)
                                picked = false;

                            if(picked) {

                                if(j==1)
                                {

                                    //GLU.gluUnProject(mRenderer.cParser.geometries.get(j).translate[0], mRenderer.cParser.geometries.get(j).translate[2], 0.0f, mRenderer.viewMatrix, 0, mRenderer.projectionMatrix, 0, mRenderer.viewport, 0, temp, 0);
                                    float vecT [] =new float[3];
                                    vecT[0] = mVMatrix[3];
                                    vecT[1] = mVMatrix[7];
                                    vecT[2] = mVMatrix[11];
                                    myVector vec1 = new myVector(tV3);
                                    myVector vec2 = new myVector(I);
                                    double distance = myVector.distance2(vec1, vec2);
                                    int sector = 0;
                                    Log.d("This is the I", Float.toString(I[0])+ " " + Float.toString(I[1]) + " " + Float.toString(I[2]));

                                    Log.d("This is the translate", Float.toString(tV3[0])+ " " + Float.toString(tV3[1]) + " " + Float.toString(tV3[2]));
                                    Log.d("This is the distance", Double.toString(distance));
                                    if(distance >= 0 && distance <= 1)
                                        sector = 0;
                                    if(distance >= 1 && distance <= 2)
                                        sector = 1;
                                    if(distance >= 2 && distance <= 3)
                                        sector = 2;
                                    if(distance >= 3 && distance <= 4)
                                        sector = 3;
                                    if(distance >= 4 && distance <= 5)
                                        sector = 4;
                            /*if(distance >= 5 && distance <= 6)
                                sector = 5;
                            if(distance >= 6 && distance <= 7)
                                sector = 6;
                            if(distance >= 7 && distance <= 8)
                                sector = 7;
                            if(distance >= 8 && distance <= 9)
                                sector = 8;
                            if(distance >= 9 && distance <= 10)
                                sector = 9;
                            if(distance >= 10 && distance <= 11)
                                sector = 10;
                            if(distance >= 11 && distance <= 12)
                                sector = 11;
                            if(distance >= 12 && distance <= 13)
                                sector = 11;
                                */
                                    float circumference = 2 * 3.14f * sector;
                                    float newV[] = new float[3];
                                    newV[0] = mRenderer.cParser.geometries.get(j).translate[0];
                                    newV[1] = mRenderer.cParser.geometries.get(j).translate[1];
                                    newV[2] = mRenderer.cParser.geometries.get(j).translate[2] + sector;
                                    //float dotP = myVector.dot(mRenderer.cParser.geometries.get(j).translate, I);
                                    float dotP = myVector.dot2(newV, I);
                                    //float length1 = myVector.length(mRenderer.cParser.geometries.get(j).translate);
                                    float length1 = myVector.length2(newV);
                                    float length2 = myVector.length2(I);
                                    double angle = Math.acos((dotP / (length1 * length2)));
                                    //  angle = Math.asin(length2/length1);
                                    Log.d("angle", Double.toString(angle));

                                    double slice = angle/3.14;
                                    // slice = slice;

                                    Log.d("slice", Double.toString(slice));
                                    Log.d("sector", Double.toString(sector));
                                    double portion = mediaP.duration / 5;
                                    double seekTo = portion * sector +   slice * portion;
                                    int seekToI = (int)Math.floor(seekTo);
                                    Log.d("", Integer.toString(seekToI));
                                    mediaP.mediaP.seekTo(seekToI);



                                }
                            /*    if(j==2) {
                                    if (!play & !mRenderer.cParser.geometries.get(j).picked) {


                                        play = true;
                                        mediaP.mediaP.start();

                                        // context.

                                    } else if (play & !mRenderer.cParser.geometries.get(j).picked) {
                                        play = false;
                                        mediaP.mediaP.pause();

                                    }
                                }*/
                                else
                                    mRenderer.cParser.geometries.get(j).picked = true;

                            }
                            else {
                                //if one triangle picked dontunpick
                                if(mRenderer.cParser.geometries.get(j).picked == false)
                                    mRenderer.cParser.geometries.get(j).picked = false;

                            }



                        }




                    }


                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                mRenderer.setAngle(
                        mRenderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
                requestRender();        mPreviousX = x;
                mPreviousY = y;
                return true;


            case MotionEvent.ACTION_DOWN:
/*
                float[] transformed = new float[mRenderer.iL.vertexData.length];
             //   Matrix.multiplyMV(transformed, 0, mRenderer.viewMatrix, 0, mRenderer.iL.vertexData, 0 );
              //  for(int i = 0; i<transformed.length; i+=3)
                  //  Log.d(Float.toString(transformed[i]) + " " + Float.toString(transformed[i+1]) + " " + Float.toString(transformed[i+2]), "coords");
                for(int i = 0; i< mRenderer.iL.vertexData.length; i+=9) {


                    picked = true;
                    boolean rab = false;
                    float [] a0 = new float[4];
                    float [] a1 = new float[4];
                    float[] a2 = new float[4];
                    float[] b0 = new float[4];
                    float[] b1 = new float[4];
                    float[] b2 = new float[4];
                    float [] v0 = new float[3];
                    float [] v1 = new float[3];
                    float [] v2 = new float[3];

                    a0[0]= mRenderer.iL.vertexData[i];
                    a0[1]= mRenderer.iL.vertexData[i+1];
                    a0[2]= mRenderer.iL.vertexData[i+2];
                    a0[3] = 1;


                    a1[0]= mRenderer.iL.vertexData[i+3];
                    a1[1]= mRenderer.iL.vertexData[i+4];
                    a1[2]= mRenderer.iL.vertexData[i+5];
                    a1[3] = 1;

                    a2[0]= mRenderer.iL.vertexData[i+6];
                    a2[1]= mRenderer.iL.vertexData[i+7];
                    a2[2]= mRenderer.iL.vertexData[i+8];
                    a2[3] = 1;
                    float [] mixedMatrix  =new float[16];

                    float[] identity = new float[16];
                    Matrix.setIdentityM(identity, 0);
                    float[] mVMatrix = new float[16];

                    // Matrix.translateM(identity, 0, mRenderer.cParser.geometries.get(j).translate[0], mRenderer.cParser.geometries.get(j).translate[1], mRenderer.cParser.geometries.get(j).translate[2]);
                    Matrix.multiplyMM(mVMatrix, 0, mRenderer.viewMatrix, 0, identity, 0);
                    Matrix.multiplyMV(b0, 0, mVMatrix, 0, a0, 0);
                    Matrix.multiplyMV(b1, 0, mVMatrix, 0, a1, 0);
                    Matrix.multiplyMV(b2, 0, mVMatrix, 0, a2, 0);


                    v0[0] = b0[0]/b0[3];
                    v0[1] = b0[1]/b0[3];
                    v0[2] = b0[2]/b0[3];

                    v1[0] = b1[0]/b1[3];
                    v1[1] = b1[1]/b1[3];
                    v1[2] = b1[2]/b1[3];

                    v2[0] = b2[0]/b2[3];
                    v2[1] = b2[1]/b2[3];
                    v2[2] = b2[2]/b2[3];
Log.d("v0", Float.toString(v0[0]) + " " + Float.toString(v0[1]) + " " + Float.toString(v0[2]));
                    Log.d("v0", Float.toString(v1[0]) + " " + Float.toString(v1[1]) + " " + Float.toString(v1[2]));
                    Log.d("v0", Float.toString(v2[0]) + " " + Float.toString(v2[1]) + " " + Float.toString(v2[2]));

                    uT = myVector.minus(v1, v0);
                    vT = myVector.minus(v2, v0);
                    nT = myVector.crossProduct(uT, vT);

                    if(nT.equals(new float[]{0.0f,  0.0f, 0.0f})) {
                        Log.d("degenerate", "Degenerate");


                    }
                    else
                    {
                        rDir = myVector.minus(near, far);
                        rW0 = myVector.minus(far, v0);
                        a = -myVector.dot(nT, rW0);
                        b = myVector.dot(nT, rDir);

                        if(Math.abs(b) < smallNum){
                            if(a==0) {
                               // picked = true;
                                rab = true;
                                Log.d("rab true", "rab true");
                            }
                            else {
                                rab = false;

                                picked = false;
                            }

                        //    Log.d(" failed a b check" + Integer.toString(i), "fail");
                        }

                        r = a/b;
                        if(r<0.0f) {
                            picked = false;
Log.d("r less fail", "rlessfail");
                        }

                        float[] tempI = myVector.addition(far, myVector.scalarProduct(r, rDir));
                        float []I = new float[3];

                        I[0] = tempI[0];
                        I[1] = tempI[1];
                        I[2] = tempI[2];

                        // Log.d("This is the I", Float.toString(I[0])+ " " + Float.toString(I[1]) + " " + Float.toString(I[2]));

                        float uu, uv, vv, wu, wv, D;
                        uu = myVector.dot(uT,uT);
                        uv = myVector.dot(uT, vT);
                        vv = myVector.dot(vT, vT);
                        rW = myVector.minus(I, v0);
                        wu = myVector.dot(rW, uT);
                        wv = myVector.dot(rW, vT);
                        D = (uv * uv) - (uu * vv);

                        float s, t;
                        s = ((uv * wv) - (vv * wu)) / D;
                        if(s < 0.0f || s > 1.0f) {
                          //  Log.d("fail s", "fail s");
                            picked = false;
                        }
                        t = (uv * wu - uu * wv) / D;
                        if(t < 0.0f || (s + t) > 1.0f) {

                          //  Log.d("fail t", "fail t");
                            picked = false;

                        }

                        if(i<9) {

                            if((picked || rab) &! mRenderer.iL.boolData[0] ) {
                                mRenderer.iL.boolData[0] = true;
                                Log.d("forst", "first");
                                float tris [] = new float[mRenderer.iL.vertices.size()];

                                for(int j = 8; j>=0; j--) {
                                    if(mRenderer.iL.vertices.size() > 8)
                                    mRenderer.iL.vertices.remove(j);
                                    else
                                        mRenderer.iL.vertices.clear();

                                }
                                mRenderer.iL.loadBuffer();
                                break;

                            }
                            if(!picked)
                                Log.d("Not", "picked");
                        }
                        else if(i<18) {

                            if((picked || rab) &! mRenderer.iL.boolData[1]) {
                                mRenderer.iL.boolData[1] = true;

                                Log.d("second", "second");


                                float tris [] = new float[mRenderer.iL.vertices.size()];

                                for(int j = 17; j>=9; j--) {
                                    if(mRenderer.iL.vertices.size() > 8)
                                        mRenderer.iL.vertices.remove(j);

                                }
                                mRenderer.iL.loadBuffer();
                                break;
                            }
                        }
                        else if(i<27){

                            if((picked || rab) &! mRenderer.iL.boolData[2]) {
                                mRenderer.iL.boolData[2] = true;
                                Log.d("third", "third");
                                float tris [] = new float[mRenderer.iL.vertices.size()];

                                for(int j = 26; j>=18; j--) {
                                    if(mRenderer.iL.vertices.size() > 8)
                                        mRenderer.iL.vertices.remove(j);

                                }
                                mRenderer.iL.loadBuffer();
                                break;
                            }
                        }
                        else if(i<36){

                            if((picked || rab) &! mRenderer.iL.boolData[3]) {
                                mRenderer.iL.boolData[3] = true;
                                Log.d("fourth", "fourth");

                                float tris [] = new float[mRenderer.iL.vertices.size()];

                                for(int j = 35; j>=27; j--) {
                                    if(mRenderer.iL.vertices.size() > 8)
                                        mRenderer.iL.vertices.remove(j);

                                }
                                mRenderer.iL.loadBuffer();
                                break;
                            }
                        }



                    }




                }*/

            //    Log.d("hello", "hello");
            //case MotionEvent.ACTION_MOVE:

                 dx = x - mPreviousX;
                 dy = y - mPreviousY;

              //   uT, vT, nT;
                // rDir, rW0, rW;
               // float r, a, b;
              //  boolean picked = false;
             //   float smallestX= 100;
             //   float biggestX = -100;
             ///   float smallestY = 100;
              //  float biggestY = -100;
              //  float smallestZ = 100;
              //  float biggestZ = -100;
                for(int j = 0; j<mRenderer.cParser.geometries.size(); j++)
            for(int i = 0; i< mRenderer.cParser.geometries.get(j).triangleCount*3; i+=9) {
            picked = true;
                boolean rabC = false;
                float [] a0 = new float[4];
                float [] a1 = new float[4];
                float[] a2 = new float[4];
                float[] b0 = new float[4];
                float[] b1 = new float[4];
                float[] b2 = new float[4];
                float [] v0 = new float[3];
                float [] v1 = new float[3];
                float [] v2 = new float[3];

                a0[0]= mRenderer.cParser.geometries.get(j).vertices[i];
                a0[1]= mRenderer.cParser.geometries.get(j).vertices[i+1];
                a0[2]= -mRenderer.cParser.geometries.get(j).vertices[i+2];
                a0[3] = 1;


                a1[0]= mRenderer.cParser.geometries.get(j).vertices[i+3];
                a1[1]= mRenderer.cParser.geometries.get(j).vertices[i+4];
                a1[2]=- mRenderer.cParser.geometries.get(j).vertices[i+5];
                a1[3] = 1;

                a2[0]= mRenderer.cParser.geometries.get(j).vertices[i+6];
                a2[1]= mRenderer.cParser.geometries.get(j).vertices[i+7];
                a2[2]=- mRenderer.cParser.geometries.get(j).vertices[i+8];
                a2[3] = 1;
            float [] mixedMatrix  =new float[16];

                float[] identity = new float[16];
                Matrix.setIdentityM(identity, 0);
                float[] mVMatrix = new float[16];

                Matrix.translateM(identity, 0, mRenderer.cParser.geometries.get(j).translate[0], mRenderer.cParser.geometries.get(j).translate[1], mRenderer.cParser.geometries.get(j).translate[2]);
                Matrix.multiplyMM(mVMatrix, 0, mRenderer.viewMatrix, 0, identity, 0);
                Matrix.multiplyMV(b0, 0, mVMatrix, 0, a0, 0);
                Matrix.multiplyMV(b1, 0, mVMatrix, 0, a1, 0);
                Matrix.multiplyMV(b2, 0, mVMatrix, 0, a2, 0);
                float[] tV = new float[4];
                tV[0] = 0.0f;
                tV[1] = 0.0f;
                tV[2] = 0.0f;
                tV[3] = 1.0f;
                float[] tV2 = new float[4];
                Matrix.multiplyMV(tV2, 0, mVMatrix, 0, tV, 0);
                float[] tV3 = new float[3];
                tV3[0] = tV2[0]/tV2[3];
                tV3[1] = tV2[1]/tV2[3];
                tV3[2] = tV2[2]/tV2[3];

                v0[0] = b0[0]/b0[3];
                v0[1] = b0[1]/b0[3];
                v0[2] = b0[2]/b0[3];

                v1[0] = b1[0]/b1[3];
                v1[1] = b1[1]/b1[3];
                v1[2] = b1[2]/b1[3];

                v2[0] = b2[0]/b2[3];
                v2[1] = b2[1]/b2[3];
                v2[2] = b2[2]/b2[3];



                uT = myVector.minus(v1, v0);
                vT = myVector.minus(v2, v0);
                nT = myVector.crossProduct(uT, vT);

                if(nT.equals(new float[]{0.0f,  0.0f, 0.0f})) {
                   Log.d("degenerate", "Degenerate");


                }
                else
                {
            rDir = myVector.minus(near, far);
                    rW0 = myVector.minus(far, v0);
                    a = -myVector.dot(nT, rW0);
                    b = myVector.dot(nT, rDir);

                    if(Math.abs(b) < smallNum){
                        if(a==0)
                          rabC = true;
                        else
                            picked = false;
                    }

                    r = a/b;
                    if(r<0.0f)
                        picked = false;

            float[] tempI = myVector.addition(far, myVector.scalarProduct(r, rDir));
                    float []I = new float[3];

            I[0] = tempI[0];
                    I[1] = tempI[1];
                    I[2] = tempI[2];

           // Log.d("This is the I", Float.toString(I[0])+ " " + Float.toString(I[1]) + " " + Float.toString(I[2]));

                    float uu, uv, vv, wu, wv, D;
                    uu = myVector.dot(uT,uT);
                    uv = myVector.dot(uT, vT);
                    vv = myVector.dot(vT, vT);
                    rW = myVector.minus(I, v0);
                    wu = myVector.dot(rW, uT);
                    wv = myVector.dot(rW, vT);
                    D = (uv * uv) - (uu * vv);

                    float s, t;
                    s = ((uv * wv) - (vv * wu)) / D;
                    if(s < 0.0f || s > 1.0f)
                    picked = false;
                    t = (uv * wu - uu * wv) / D;
                    if(t < 0.0f || (s + t) > 1.0f)
                       picked = false;

                    if(picked || rabC) {

                        if(j==1)
                        {
                          //  Log.d("v0d", Float.toString(v0[0]) + " " + Float.toString(v0[1]) + " " + Float.toString(v0[2]));
                          //  Log.d("v1d", Float.toString(v1[0]) + " " + Float.toString(v1[1]) + " " + Float.toString(v1[2]));
                          //  Log.d("v2d", Float.toString(v2[0]) + " " + Float.toString(v2[1]) + " " + Float.toString(v2[2]));

                            //GLU.gluUnProject(mRenderer.cParser.geometries.get(j).translate[0], mRenderer.cParser.geometries.get(j).translate[2], 0.0f, mRenderer.viewMatrix, 0, mRenderer.projectionMatrix, 0, mRenderer.viewport, 0, temp, 0);
                            float vecT [] =new float[3];
                            vecT[0] = mVMatrix[3];
                            vecT[1] = mVMatrix[7];
                            vecT[2] = mVMatrix[11];
                            myVector vec1 = new myVector(tV3);
                            myVector vec2 = new myVector(I);
                            double distance = myVector.distance2(vec1, vec2);
                            int sector = 0;
                            Log.d("This is the I", Float.toString(I[0])+ " " + Float.toString(I[1]) + " " + Float.toString(I[2]));

                            Log.d("This is the translate", Float.toString(tV3[0])+ " " + Float.toString(tV3[1]) + " " + Float.toString(tV3[2]));
                            Log.d("This is the distance", Double.toString(distance));
                            if(distance >= 0 && distance <= 1)
                                sector = 0;
                            if(distance >= 1 && distance <= 2)
                                sector = 1;
                            if(distance >= 2 && distance <= 3)
                                sector = 2;
                            if(distance >= 3 && distance <= 4)
                                sector = 3;
                            if(distance >= 4 && distance <= 5)
                                sector = 4;
                            /*if(distance >= 5 && distance <= 6)
                                sector = 5;
                            if(distance >= 6 && distance <= 7)
                                sector = 6;
                            if(distance >= 7 && distance <= 8)
                                sector = 7;
                            if(distance >= 8 && distance <= 9)
                                sector = 8;
                            if(distance >= 9 && distance <= 10)
                                sector = 9;
                            if(distance >= 10 && distance <= 11)
                                sector = 10;
                            if(distance >= 11 && distance <= 12)
                                sector = 11;
                            if(distance >= 12 && distance <= 13)
                                sector = 11;
                                */
float circumference = 2 * 3.14f * sector;
                            float newV[] = new float[3];
                            newV[0] = mRenderer.cParser.geometries.get(j).translate[0];
                            newV[1] = mRenderer.cParser.geometries.get(j).translate[1];
                            newV[2] = mRenderer.cParser.geometries.get(j).translate[2] + sector;
                            //float dotP = myVector.dot(mRenderer.cParser.geometries.get(j).translate, I);
                            float dotP = myVector.dot2(newV, I);
                            //float length1 = myVector.length(mRenderer.cParser.geometries.get(j).translate);
                            float length1 = myVector.length2(newV);
                            float length2 = myVector.length2(I);
                            double angle = Math.acos((dotP / (length1 * length2)));
                          //  angle = Math.asin(length2/length1);
Log.d("angle", Double.toString(angle));

                            double slice = angle/3.14;
                           // slice = slice;

                            Log.d("slice", Double.toString(slice));
                            Log.d("sector", Double.toString(sector));
                            double portion = mediaP.duration / 5;
                            double seekTo = portion * sector +   slice * portion;
                            int seekToI = (int)Math.floor(seekTo);
                            Log.d("", Integer.toString(seekToI));
                            mediaP.mediaP.seekTo(seekToI);



                        }
                        if(j==2)
                        {


                            Log.d("100 points" , "100 points");
                        }
                        if(j==3) {

                            if (!play & !mRenderer.cParser.geometries.get(j).picked ) {

                                mRenderer.cParser.geometries.get(j).picked = true;
                                play = true;
                                mediaP.mediaP.start();
                              //  targ = new RotateTheTarget();
                            //    targ.execute();


                                // context.

                            } else if (play & !mRenderer.cParser.geometries.get(j).picked) {
                                play = false;
                                mediaP.mediaP.pause();
                                boolean attemptToCancel = true;
                             //   targ.cancel(attemptToCancel);
                                mRenderer.cParser.geometries.get(j).picked = true;

                            }
                        }
                        else
                            mRenderer.cParser.geometries.get(j).picked = true;

                    }
                    else {
                        //if one triangle picked dontunpick
                        if(mRenderer.cParser.geometries.get(j).picked == false)
                        mRenderer.cParser.geometries.get(j).picked = false;

                    }



                }




            }
                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                mRenderer.setAngle(
                        mRenderer.getAngle() +
                        ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
                requestRender();        mPreviousX = x;
                mPreviousY = y;
                return true;


            case MotionEvent.ACTION_UP:

              //  Log.d("actionup", "AU");
                mPreviousX = x;
                mPreviousY = y;
                requestRender();
                return true;
        }

return true;
    }




}
