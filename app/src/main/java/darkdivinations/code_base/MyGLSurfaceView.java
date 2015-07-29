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
import android.view.MotionEvent;
import android.opengl.GLU;
import android.util.Log;


/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */


public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;


    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    public static  float smallNum = 0.00000001f;

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

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


     //   Log.d("up", Float.toString(far[0]) + " " + Float.toString(far[1]) + " " + Float.toString(far[2]));
        GLU.gluUnProject(x, y, 1.0f, mRenderer.viewMatrix, 0, mRenderer.projectionMatrix, 0, mRenderer.viewport, 0, temp, 0);
        Matrix.multiplyMV(temp2, 0, mRenderer.viewMatrix, 0, temp, 0);

        near[0] = temp2[0]/temp2[3];
        near[1] = temp2[2]/temp2[3];
        near[2] = -temp2[1]/temp2[3];
        Log.d("up 2", Float.toString(near[0]) + " " + Float.toString(near[1]) + " " + Float.toString(near[2]) );
        switch (e.getAction()) {

            case MotionEvent.ACTION_DOWN:
            //case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                float[] uT, vT, nT;
                float[] rDir, rW0, rW;
                float r, a, b;
                boolean picked = false;
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

         //           Log.d("v0", Float.toString(v0[0]) + " " + Float.toString(v0[1]) + " " + Float.toString(v0[2]));
          //      Log.d("v1", Float.toString(v1[0]) + " " + Float.toString(v1[1]) + " " + Float.toString(v1[2]));
           //     Log.d("v2", Float.toString(v2[0]) + " " + Float.toString(v2[1]) + " " + Float.toString(v2[2]));


                uT = myVector.minus(v1, v0);
                vT = myVector.minus(v2, v0);
                nT = myVector.crossProduct(uT, vT);

                if(nT.equals(new float[]{0.0f,  0.0f, 0.0f})) {
                  //  Log.d("degenerate", "Degenerate");


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

            Log.d("This is the I", Float.toString(I[0])+ " " + Float.toString(I[1]) + " " + Float.toString(I[2]));

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

                    if(picked)
                    mRenderer.cParser.geometries.get(j).picked = true;
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
                for(int i =0; i< mRenderer.cParser.geometries.size(); i++) {
                    mRenderer.cParser.geometries.get(i).picked = false;
                //    Log.d("is it false", Boolean.toString( mRenderer.cParser.geometries.get(i).picked));
                }
                Log.d("actionup", "AU");
                mPreviousX = x;
                mPreviousY = y;
                requestRender();
                return true;
        }

return true;
    }




}
