package darkdivinations.code_base;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by nathan on 8/7/2015.
 */
public class IntersectableLine {
    private final int mProgram;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mNormalHandle;
    private int mLightPosHandle;
    public FloatBuffer vertexBuffer;

    private final float[] mVMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private final float[] mVPMatrix = new float[16];
    public float [] vertexData;
    public boolean [] boolData;
    public int[] indexData={0, 1, 2, 2, 1, 3};
    public ArrayList<Float> vertices;

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                 //   "uniform mat4 uMVMatrix;" +
                    "uniform vec4 uColor;"+
               //     "attribute vec3 aNormal;"+
                    "attribute vec4 aPosition;" +


               //     "varying vec3 vPosition;" +
                //    "varying vec3 vNormal;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
               //     "vPosition = vec3(uMVMatrix * aPosition);" +
                    "vColor = uColor;" +
                //    "vNormal = vec3(uMVMatrix * vec4(aNormal, 0.0));" +


                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * aPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
             //       "uniform vec3 lightPos;" +
                    "varying vec4 vColor;" +

              //      "varying vec3 vPosition;" +
             //       "varying vec3 vNormal;"+


                    "void main() {" +
            //        "float distance = length(lightPos - vPosition);"+
            //        "vec3 lightVector = normalize(lightPos - vPosition);"+
            //        "float diffuse;" +

             //       " diffuse = max(dot(vNormal, lightVector), 0.1);" +

           //         "diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));" +
            //        " diffuse = diffuse + 0.3; " +


                    "  gl_FragColor = (vColor );" +
                    "}";
    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int BYTES_PER_FLOAT = 4;
    private int program;



    public IntersectableLine(int size, int x, int y )
    {
        //4 to start and 2 for each other one
        vertexData = new float [36];
        boolData = new boolean[4];
        boolData[0] = false;
        boolData[1] = false;
        boolData[2] = false;
        boolData[3] = false;
        vertices = new ArrayList<Float>();
        //if(size>=4) {
           // for (int i = 0; i < 4; i++) {
                vertexData[0] = x+5;
                vertexData[ 1] = y + 1;
                vertexData[2] = -4.0f;

                vertexData[ 3] = x;
                vertexData[ 4] = y+5;
                vertexData[ 5] = -4.0f;

                vertexData[ 6] = x ;
                vertexData[ 7] = y;
                vertexData[ 8] = -4.0f;

            vertexData[ 9] = x;
            vertexData[ 10] = y;
            vertexData[ 11] = -4.0f;


            vertexData[ 12] = x+5;
            vertexData[ 13] = y;
            vertexData[ 14] = -4.0f;
                vertexData[ 15] = x  +5;
                vertexData[ 16] = y+5;
                vertexData[ 17] = -4.0f;


           // }
       // }
     //   for(int i = 6; i <= size; i+=2)
     //   {
            vertexData[6*3] = x + 2 * 1;
            vertexData[6*3+ 1] = y + 1;
            vertexData[6*3+2] = -4.0f;

            vertexData[6*3+ 3] = x + 2 * 1;
            vertexData[6*3+ 4] = y;
            vertexData[6*3+ 5] = -4.0f;

            vertexData[6*3+ 6] = x + 1 * (3);
            vertexData[6*3+ 7] = y + 1;
            vertexData[6*3+ 8] = -4.0f;

            vertexData[6*3+ 9] = x + 1 * (3);
            vertexData[6*3+ 10] = y + 1;
            vertexData[6*3+ 11] = -4.0f;


            vertexData[6*3+ 12] = x + 1 * 2;
            vertexData[6*3+ 13] = y;
            vertexData[6*3+ 14] = -4.0f;
            vertexData[6*3+ 15] = x  + 1 * (3);
            vertexData[6*3+ 16] = y;
            vertexData[6*3+ 17] = -4.0f;


        //}
        Log.d("vertexData size", Integer.toString(vertexData.length));
        for(int i = 0; i<vertexData.length; i++)
            vertices.add(vertexData[i]);

       // for(int i = 0; i<vertices.size(); i+=3) {
        //    vertexData[i] = vertices.get(i);
        //    vertexData[i+1] = vertices.get(i+2);
        //    vertexData[i+2] = vertices.get(i+1);

        //}

        for(int i = 0; i<vertexData.length; i+=3)
            Log.d(Float.toString(vertexData[i]) + " " + Float.toString(vertexData[i + 1]) + " " + Float.toString(vertexData[i + 2]), "coords");


        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                vertexData.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
       vertexBuffer.put(vertexData);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);


    }
    public void loadBuffer()
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                vertices.size() * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexData = new float[vertices.size()];
        //for(int i = 0; i<vertices.size(); i+=3) {
         //   vertexData[i] = vertices.get(i);
          //  vertexData[i+1] = vertices.get(i+2);
          //  vertexData[i+2] = vertices.get(i+1);

        //}
Log.d("Vertexdata length", Integer.toString(vertexData.length));
        for(int i = 0; i<vertexData.length; i+=3)
            Log.d(Float.toString(vertexData[i]) + " " + Float.toString(vertexData[i+1]) + " " + Float.toString(vertexData[i+2]), "coords");
        //vertices = tempVert;


        vertexBuffer.put(vertexData);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

    }
    public void draw(float[] mvpMatrix, float angle, float[] viewMatrix) {

        GLES20.glUseProgram(mProgram);
        Matrix.setIdentityM(modelMatrix, 0);
        float identity[] = new float[16];

        float lightPos[] = new float[4];

        lightPos[0] = 0.0f;
        lightPos[1] = 20.0f;
        lightPos[2] = 20.0f;
        lightPos[3] = 1.0f;
        float lightPos2[] = new float[4];
        Matrix.multiplyMV(lightPos2, 0, viewMatrix, 0, lightPos, 0);
        float tempLightPos[] = new float[3];

        tempLightPos[0] = lightPos2[0]/lightPos2[3];
        tempLightPos[1] = lightPos2[1]/lightPos2[3];
        tempLightPos[2] = lightPos2[2]/lightPos2[3];

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
       // mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "lightPos");
       // mNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
       // mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        float[] temp = new float[16];
        Matrix.setIdentityM(identity, 0);
        Matrix.translateM(identity, 0, -0.0f, -0.0f, -0.0f);
     //   Matrix.translateM(identity, 0, geometries.get(i).translate[0], geometries.get(i).translate[1], geometries.get(i).translate[2]);
     //   Matrix.multiplyMM(worlds.get(i), 0, identity, 0, modelMatrix, 0);
        //  Matrix.multiplyMM(temp, 0, modelMatrix, 0, worlds.get(i), 0);
      //  Matrix.multiplyMM(mVMatrix, 0, viewMatrix, 0, identity, 0);
        Matrix.multiplyMM(mVPMatrix, 0, mvpMatrix, 0, identity, 0);
        GLES20.glVertexAttribPointer(
                mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);
        GLES20.glUniform4f(mColorHandle, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 2* 3);


    }
}
