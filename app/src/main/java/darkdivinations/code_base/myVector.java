package darkdivinations.code_base;

/**
 * Created by nathan on 7/24/2015.
 */
public class  myVector {
    public static double distance2(myVector v1, myVector v2)
    {

        double distance2= Math.sqrt(((v1.x - v2.x) * (v1.x - v2.x)) + ((v1.z - v2.z)* (v1.z - v2.z)));

        return distance2;

    }
    public static float length2(float[] u){
        return (float) Math.abs(Math.sqrt((u[X] *u[X]) + (u[Z] *u[Z])));
    }
    public static double distance(myVector v1, myVector v2)
    {
       double distance2= Math.sqrt(((v1.x - v2.x) * (v1.x - v2.x)) + ((v1.y - v2.y)* (v1.y - v2.y)) + ((v1.z - v2.z) * (v1.z - v2.z)));

return distance2;

    }
    public static float dot2(float[] u,float[] v) {
        return ((u[X] * v[X]) +  (u[Z] * v[Z]));
    }
    public myVector(float [] che)
    {

        x = che[0];
        y = che[1];
        z = che[2];



    }
    public myVector(float x1, float y1, float z1)
    {

        x = x1;
        y = y1;
        z = z1;



    }
    // dot product (3D) which allows vector operations in arguments
    public static float dot(float[] u,float[] v) {
        return ((u[X] * v[X]) + (u[Y] * v[Y]) + (u[Z] * v[Z]));
    }
    public static float[] minus(float[] u, float[] v){
        return new float[]{u[X]-v[X],u[Y]-v[Y],u[Z]-v[Z]};
    }
    public static float[] addition(float[] u, float[] v){
        return new float[]{u[X]+v[X],u[Y]+v[Y],u[Z]+v[Z]};
    }
    //scalar product
    public static float[] scalarProduct(float r, float[] u){
        return new float[]{u[X]*r,u[Y]*r,u[Z]*r};
    }
    // (cross product)
    public static float[] crossProduct(float[] u, float[] v){
        return new float[]{(u[Y]*v[Z]) - (u[Z]*v[Y]),(u[Z]*v[X]) - (u[X]*v[Z]),(u[X]*v[Y]) - (u[Y]*v[X])};
    }
    //mangnatude or length
    public static float length(float[] u){
        return (float) Math.abs(Math.sqrt((u[X] *u[X]) + (u[Y] *u[Y]) + (u[Z] *u[Z])));
    }

    public float x;
    public float y;
    public float z;

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
}
