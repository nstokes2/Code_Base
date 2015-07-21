package darkdivinations.code_base;

/**
 * Created by nathan on 7/21/2015.
 */

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import static android.opengl.GLES20.

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import android.opengl.Matrix;
import android.util.Log;

import  darkdivinations.code_base.util.LoggerConfig;
import  darkdivinations.code_base.util.ShaderHelper;
import  darkdivinations.code_base.util.TextResourceReader;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import android.opengl.GLU;

public class ColladaParser {




    public class Geometry {

        String sourceName;
        String name;
        public FloatBuffer vertexBuffer;
        public FloatBuffer normalBuffer;
        public int positionAttribute;
        public ShortBuffer indexBuffer;
        float positions[];
        float normals[];
        float texCoords[];
        float vertices[];
        float normals2[];
        short indices[];
        int triangleCount;
        float world[];
        float translate[];

        Geometry()
        {




        }
    }

    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    ArrayList<float[]> worlds = new ArrayList<float[]>();



    private final float[] modelMatrix = new float[16];

    private final float[] mVPMatrix = new float[16];


    ArrayList<Geometry> geometries = new ArrayList<Geometry>();


    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";


    public float x;
    public float y;

    public void setX(float a){ x = a;}
    public void setY(float a ) { x = a;}

    public float mAngle;
    public float mAngle2;

    public class Node{
        int count;
        String name;
        String id;
        float [] matrix;
        ArrayList<Node> childNodes;
        String type;

        Node()
        {name = "";
            id = "";

            count = 0;
            type = "";
        }
    }


    public Scanner scan;





    static final int BYTES_PER_SHORT = 2;
    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int BYTES_PER_FLOAT = 4;
    private  Context context;
    private int program;


    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };


    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    private static final String A_POSITION = "a_Position";

    public void processTriangles(){
        int index = 0;
        int count = 0;
        String type = "";
        boolean closedHeader = false;
        //  Log.d("process triangles", "process triangles");
        String aString="";
        while (scan.hasNext()) {
            if(aString.contains("done"))
                break;
            aString = scan.next();
            //just a breaker do it better later


            //    Log.d("processwhile", aString);
            //time to exit letting system know that this
            //master node is complete
            if (aString.contains("/p")) {
                break;
            }
            // aString = aString.replaceAll("<", " <");
            //aString = aString.replaceAll(">", "> ");
            // Log.d("processed Astring", aString);
            String contents[] = aString.split("<|>");

            for (int i = 0; i < contents.length; i++) {
                //this begins a node object
                //Read the ids for the node container
                //specific for each one
                //  Log.d("triangle contents", contents[i]);
                if (!contents[i].isEmpty()) {
                    if (contents[i].charAt(0) == '<') {
                        contents[i] = contents[i].substring(1);

                        //if closing a node
                        if (contents[i].charAt(contents[i].length() - 1) == '>') {
                            contents[i] = contents[i].substring(0, contents[i].length() - 1);
                            closedHeader = true;
                        }


                    }
                    if (contents[i].charAt(contents[i].length() - 1) == '>') {
                        contents[i] = contents[i].substring(0, contents[i].length() - 1);
                        closedHeader = true;
                    }
                    if (contents[i].contains("count")) {
                        //Just a marker. really need to iterate the contents thing over the input do it later
                        aString = "done";
                        //       Log.d("if ", "triangle");
                        //     Log.d("contents", contents[i]);

                        //What is wrong with the next line?
                        count = Integer.parseInt(contents[i].substring(7, contents[i].length() - 1));
                        //         Log.d("triangle count = ", Integer.toString(count));
                        geometries.get(geometries.size() - 1).triangleCount = count;
                        //       Log.d("getting new vert", " ");
                        /*


                        Was triangleCount * 3 * 9. changed it 842 71615 when we added normals because
                        we t ried to count according to the loop

                        */

                        geometries.get(geometries.size() - 1).vertices = new float[geometries.get(geometries.size() - 1).triangleCount * 3 * 3];
                        geometries.get(geometries.size() - 1).normals2 = new float[geometries.get(geometries.size() - 1).triangleCount * 3 * 3];
                        //     Log.d("Triangles count", Integer.toString(count));
                        for (int p = 0; p < 10; p++)
                            scan.next();
                        //    Log.d("Past the 10 ps", "ps");
                        //     geometries.get(geometries.size() - 1).indices = new short[count * 3 * 6];
                        int k = 0;
                        int z = 0;
                        //changed this because it was leaving one triangle out.... 7/11/15 640
                        //  Log.d("Entering the for loop", "triangles");
                        for (int t = 0; t < (count) * 3; t++) {
                            //geometries.get(geometries.size() - 1).indices[t] = Short.parseShort(scan.next());
                            int id = Integer.parseInt(scan.next());
                            int id2 = Integer.parseInt(scan.next());
                            //normal then tex coord
                            //     Log.d("accepted the short", Integer.toString(geometries.get(geometries.size() - 1).indices[t]));
                            //int j = geometries.get(geometries.size() - 1).indices[t];
                            //int j  = id;
                            //   Log.d("j is " , Integer.toString(j));
                            geometries.get(geometries.size() - 1).vertices[k++] = geometries.get(geometries.size() - 1).positions[id * 3];
                            geometries.get(geometries.size() - 1).vertices[k++] = geometries.get(geometries.size() - 1).positions[id * 3 + 1];
                            geometries.get(geometries.size() - 1).vertices[k++] = geometries.get(geometries.size() - 1).positions[id * 3 + 2];
                            geometries.get(geometries.size() - 1).normals2[z++] = geometries.get(geometries.size() -1 ).normals[id2 * 3];
                            geometries.get(geometries.size() - 1).normals2[z++] = geometries.get(geometries.size() -1 ).normals[id2 * 3 + 1];
                            geometries.get(geometries.size() - 1).normals2[z++] = geometries.get(geometries.size() -1 ).normals[id2 * 3 + 2];
                            //id = Integer.parseInt(scan.next());
                            //  Log.d("j is " , Integer.toString(count * 9) + " " +  Integer.toString(id) + " " + Integer.toString(geometries.size()) + " " + Integer.toString(geometries.get(geometries.size() - 1).vertices.length) + " " + Integer.toString(geometries.get(geometries.size() - 1).positions.length));

                            // scan.next();
                            //  geometries.get(geometries.size()-1).vertices[]
                            scan.next();
                            //  br.next();
                        }

                    }
                }
            }
        }


    }
    public void processSource(){
        while (scan.hasNext()) {
            String aString = scan.next();

            //time to exit letting system know that this
            //master node is complete

            aString = aString.replaceAll("<", " <");
            aString = aString.replaceAll(">", "> ");
            String contents[] = aString.split("\\s");

            for (int i = 0; i < contents.length; i++) {
                //this begins a node object
                //Read the ids for the node container
                //specific for each one
                if (contents[i].charAt(0) == '<') {
                    contents[i] = contents[i].substring(1);

                    //if closing a node
                    if (contents[i].charAt(contents[i].length() - 1) == '>') {
                        contents[i] = contents[i].substring(0, contents[i].length() - 1);
                        //  closedHeader = true;
                    }
                }

                if (contents[i].charAt(contents[i].length() - 1) == '>') {
                    contents[i] = contents[i].substring(0, contents[i].length() - 1);
                    //  closedHeader = true;
                }
                if (contents[i].contains("id")) {
                    geometries.get(geometries.size() - 1).sourceName = contents[i].substring(4, contents[i].length() - 1);
                    //  Log.d("source name", contents[i].substring(4, contents[i].length() - 1));
                    break;
                }

            }
        }


    }
    public void processFloatArray() {

        int index = 0;
        int count = 0;
        String type = "";
        boolean gotCount = false;
        //    Log.d("proc float array", " ");
        while (scan.hasNext()) {
            String aString = scan.next();

            //time to exit letting system know that this
            //master node is complete
            if (aString.contains("float_array")) {
                break;
            }
            //unimplemented
            // if(aString.contains("Normal"))
            //      break;
            if(aString.contains("UV"))
                break;
            // aString =aString.replaceAll("<", " <");
            // aString =aString.replaceAll(">", "> ");
            String contents[] = aString.split("<|>");
            //       Log.d("aString", aString);
            for (int i = 0; i < contents.length; i++) {
                //this begins a node object
                //Read the ids for the node container
                //specific for each one
                //            Log.d("contents" + Integer.toString(i), contents[i]);
                if (contents[i].charAt(0) == '<') {
                    contents[i] = contents[i].substring(1);

                    //if closing a node
                    if (contents[i].charAt(contents[i].length() - 1) == '>') {
                        contents[i] = contents[i].substring(0, contents[i].length() - 1);
                        // closedHeader = true;
                    }

                }

                if (gotCount && index < count) {
                    //           Log.d("afloat", contents[i]);
                    //         Log.d("index", Integer.toString(index));
                    if(type.equalsIgnoreCase("position"))
                        geometries.get(geometries.size() - 1).positions[index++] = Float.parseFloat(contents[i]);

                    if(type.equalsIgnoreCase("normal"))
                        geometries.get(geometries.size()-1).normals[index++] = Float.parseFloat(contents[i]);

                    //shouldnt get the last split added becauseof count
                }
                if (contents[i].charAt(contents[i].length() - 1) == '>') {
                    contents[i] = contents[i].substring(0, contents[i].length() - 1);
                    //  closedHeader = true;
                }
                //  if(contents[i].contains("NORMAL"))
                // {
                //not implmeneted
                //   break;

                //}
                if(contents[i].contains("Normal"))
                {
                    gotCount =false;
                    type = "normal";
                }
                if (contents[i].contains("POSITION")) {
                    type = "position";
                    //          Log.d("Type", "Position");
                }
                if (contents[i].contains("count")) {
                    //         Log.d("processedCount", contents[i]);
                    count = Integer.parseInt(contents[i].substring(7, contents[i].length() - 1));
                    //         Log.d("float array count = ", Integer.toString(count));
                    if (type.equalsIgnoreCase("position")) {
                        geometries.get(geometries.size() - 1).positions = new float[count];
                        //       Log.d("initalizin ", "ho");
                        gotCount = true;

                    }
                    if(type.equalsIgnoreCase("normal")){
                        geometries.get(geometries.size()-1).normals = new float[count];
                        gotCount = true;
                    }
                }

            }
            //  return theseFloats;


        }
    }
    public void processNodeMatrix(int id, String floater)
    {
        geometries.get(id).world = new float[16];
        geometries.get(id).world[0] = Float.parseFloat(floater);
        int index = 1;
        int count = 0;
        String type = "";
        boolean gotCount = false;
        //  Log.d("procmatrix", " ");
        String aString = "";
        while (scan.hasNext()) {
            if(aString.contains("/matrix")) {
                //  Log.d("making the translation", "");
                geometries.get(id).translate = new float[4];
                //  Matrix.translateM( geometries.get(id).translate, 0, geometries.get(id).world[3], geometries.get(id).world[7], geometries.get(id).world[11] );
                geometries.get(id).translate[0] = geometries.get(id).world[3];
                geometries.get(id).translate[1] = geometries.get(id).world[7];
                geometries.get(id).translate[2] = geometries.get(id).world[11];
                geometries.get(id).translate[3] = geometries.get(id).world[15];
                break;

            }
            aString = scan.next();


            String contents[] = aString.split("<|>");
            //    Log.d("aString", aString);
            for (int i = 0; i < contents.length; i++) {
                //    Log.d("contents " + Integer.toString(i), contents[i]);
                if(contents[i].contains("/matrix"))
                {
                    aString = "/matrix";
                    break;
                }
                //this begins a node object
                //Read the ids for the node container
                //specific for each one
                //      Log.d("contents" + Integer.toString(i), contents[i]);
                if (contents[i].charAt(0) == '<') {

                    contents[i] = contents[i].substring(1);

                    //if closing a node
                    if (contents[i].charAt(contents[i].length() - 1) == '>') {
                        contents[i] = contents[i].substring(0, contents[i].length() - 1);
                        // closedHeader = true;
                    }

                }
                //  if(index == -1) {
                //    index++;
                // }

                if (index >= 1 && index < 16) {
                    Float aF = Float.parseFloat(contents[i]);
                    //Log.d("matrix", Integer.toString(index) + " " + Float.toString(aF));

                    geometries.get(id).world[index++] = aF;

                    //shouldnt get the last split added becauseof count
                }
                else if(index == 16)
                    aString = "done";

                //  closedHeader = true;
            }



        }



    }
    public int processNode(String id)
    {
        //get total length of node type Node
//        aString.replaceAll("<", " <");
        //      aString.replaceAll(">", " >");
        int nodeMatCount = 0;
        Node thisNode = new Node();
        String aString;
        //Log.d("Process Node", id);
        while(scan.hasNext())
        {
            aString = scan.next();

            //time to exit letting system know that this
            //master node is complete
            if(aString.contains(id))
                return 1;

            String contents[] = aString.split("<|>");


            // Log.d("contentssize", Integer.toString(contents.length));


            for (int i = 0; i < contents.length; i++) {

                if(id.contains("library_visual_scenes"))
                {

                    if(contents[i].equalsIgnoreCase("sid=\"matrix\""))
                    {
                        processNodeMatrix(nodeMatCount++, contents[i+1]);


                    }



                }
                if (id.contains("library_geometries")) {

                    if (contents[i].equalsIgnoreCase("geometry")) {
                        thisNode.type = "geometry";
                        geometries.add(new Geometry());

                    }
                    if (contents[i].contains("triangles")) {
                        //Log.d("process in node", "processin node");
                        processTriangles();
                    }
                    //  if (contents[i].contains("<source"))
                    //      processSource();
                    if (contents[i].contains("name")) {
                        thisNode.name = contents[i].substring(6, contents[i].length() - 1);
                        //Log.d("node name = ", thisNode.name);

                    }
                    if (contents[i].contains("id")) {
                        thisNode.id = contents[i].substring(4, contents[i].length() - 1);
                        //Log.d("node id = ", thisNode.id);

                    }
                    if (contents[i].contains("count")) {

                        thisNode.count = Integer.parseInt(contents[i].substring(7, contents[i].length() - 1));
//                      Log.d("node count = ", Integer.toString(thisNode.count));
                    }
                    if (thisNode.type.contains("geometry")) {
//changed to stop /float_array from triggering
                        if (contents[i].equalsIgnoreCase("float_array")) {
                            processFloatArray();

                        }

                    }

                }
            }



        }
        return 0;
    }

    public ColladaParser(Context context) {


        try {
            InputStream in = context.getAssets().open("mydj.DAE");

            scan = new Scanner((in));

            String line;
            int count;
            //          Log.d("hello", "hello");

            while (scan.hasNext()) {
                line = scan.next();
                //Feed the node
                if (line.contains("library_geometries")) {
                    while (scan.hasNext()) {
                        //     Log.d("processing lib Geometry", "1");
                        if (processNode("library_geometries") == 1)
                            // processNode("library_geometries");
                            break;

                    }

                }
                if(line.contains("library_visual_scenes")){

                    while(scan.hasNext()){
                        //         Log.d("processing lib visual", "1");
                        if(processNode("library_visual_scenes") == 1)
                            break;
                    }



                }
            }


            // Log.d("myTag", line);
        } catch (Exception e) {
            Log.d("MYTag", "nothing is there");
        }

        // Log.d("Vertices length", Integer.toString(geometries.get(0).vertices.length));

//Log.d("GOt geometry", "geometry");
        for(int i = 0; i<geometries.size(); i++) {
            //   Log.d("vB", Integer.toString(geometries.get(i).vertices.length));
            //     for(int j = 0; j < 3; j++)
            //      Log.d("vB", Float.toString(geometries.get(i).vertices[j]));
            ByteBuffer bb = ByteBuffer.allocateDirect(
                    // (number of coordinate values * 4 bytes per float)
                    geometries.get(i).vertices.length * 4);
            // use the device hardware's native byte order
            bb.order(ByteOrder.nativeOrder());

            // create a floating point buffer from the ByteBuffer
            geometries.get(i).vertexBuffer = bb.asFloatBuffer();
            // add the coordinates to the FloatBuffer
            geometries.get(i).vertexBuffer.put(geometries.get(i).vertices);
            // set the buffer to read the first coordinate
            geometries.get(i).vertexBuffer.position(0);

            ByteBuffer bb2 = ByteBuffer.allocateDirect(geometries.get(i).normals2.length * 4);
            geometries.get(i).normalBuffer = bb2.asFloatBuffer();
            geometries.get(i).normalBuffer.put(geometries.get(i).normals2);
            geometries.get(i).normalBuffer.position(0);


        }


        //this.context = context;

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);


    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }
    public float getAngle2() {return mAngle2;}
    public void setAngle2(float angle) {mAngle2 = angle;}

    public void draw(float[] mvpMatrix, float angle) {
        mAngle = angle;
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);
        Matrix.setRotateM(modelMatrix, 0, mAngle, 0f, 1f, 1f);
        float identity[] = new float[16];

        for (int i = 0; i < geometries.size(); i++)
            worlds.add(new   float[16]);
        for (int i = 0; i < geometries.size(); i++)
        {
            Matrix.setIdentityM(identity, 0);
            Matrix.translateM(identity, 0, geometries.get(i).translate[0], geometries.get(i).translate[1], geometries.get(i).translate[2]);
            Matrix.multiplyMM(worlds.get(i), 0, identity, 0, modelMatrix, 0);
            Matrix.multiplyMM(mVPMatrix, 0, mvpMatrix, 0, worlds.get(i), 0);


        }
      //  Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);


        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        for(int i = 0; i < geometries.size(); i++) {
            GLES20.glVertexAttribPointer(
                    mPositionHandle, 3,
                    GLES20.GL_FLOAT, false,
                    0, geometries.get(i).vertexBuffer);

            // get handle to fragment shader's vColor member
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

            // Set color for drawing the triangle
            if(i == 0)
                GLES20.glUniform4f(mColorHandle, 1.0f, 0.0f, 0.0f, 1.0f);
            if (i == 1)
                GLES20.glUniform4f(mColorHandle, 1.0f, 1.0f, 0.0f, 1.0f);
            if(i == 2)
                GLES20.glUniform4f(mColorHandle, 0.4f, 0.2f, 0.7f, 1.0f);
            if(i == 3)
                GLES20.glUniform4f(mColorHandle, 1.0f, 0.0f, 1.0f, 1.0f);
            if(i == 4)
                GLES20.glUniform4f(mColorHandle, 0.0f, 1.0f, 0.0f, 1.0f);
            if(i == 5)
                GLES20.glUniform4f(mColorHandle, 1.0f, 1.0f, 1.0f, 1.0f);
            if(i == 6)
                GLES20.glUniform4f(mColorHandle, 1.0f, 0.5f, 0.0f, 1.0f);
            if(i == 7)
                GLES20.glUniform4f(mColorHandle, 1.0f, 1.0f, 0.5f, 1.0f);
            if(i == 8)
                GLES20.glUniform4f(mColorHandle, 1.0f, 0.5f, 0.5f, 1.0f);
            if(i == 9)
                GLES20.glUniform4f(mColorHandle, 1.0f, 0.5f, 1.0f, 1.0f);
            if(i == 10)
                GLES20.glUniform4f(mColorHandle, 0.5f, 1.0f, 0.0f, 1.0f);
            if(i == 11)
                GLES20.glUniform4f(mColorHandle, 0.5f, 0.5f, 1.0f, 1.0f);

           // GLES20.glUniform4fv(mColorHandle, 1, color, 0);

            // get handle to shape's transformation matrix
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            MyGLRenderer.checkGlError("glGetUniformLocation");

            // Apply the projection and view transformation
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mVPMatrix, 0);
            MyGLRenderer.checkGlError("glUniformMatrix4fv");

            // Draw the triangle
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, geometries.get(i).triangleCount * 3);
        }

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }








}
