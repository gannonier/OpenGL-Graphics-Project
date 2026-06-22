package a2;

import org.joml.*;
import static java.lang.Math.*;

public class Bowl {
    private int numVertices, numIndices;
    private int precHorizontal, precVertical;
    private int[] indices;
    private Vector3f[] vertices;
    private Vector2f[] texCoords;
    private Vector3f[] normals;
    private Vector3f[] tangents;

    private float radius;
    private float maxAngle;
    private float thickness = 0.08f;

    public Bowl() {
        this(48, 24, 1.0f, 90.0f);
    }

    public Bowl(int precHorizontal, int precVertical, float radius, float maxAngle) {
        this.precHorizontal = precHorizontal;
        this.precVertical = precVertical;
        this.radius = radius;
        this.maxAngle = maxAngle;
        initBowl();
    }

    private void initBowl() {
        int shellVerts = (precVertical + 1) * (precHorizontal + 1);
        numVertices = shellVerts * 2; //inner and outer shell

        numIndices = precVertical * precHorizontal * 6 * 2;
        indices = new int[numIndices];
        vertices = new Vector3f[numVertices];
        texCoords = new Vector2f[numVertices];
        normals = new Vector3f[numVertices];
        tangents = new Vector3f[numVertices];

        for (int i = 0; i < numVertices; i++) {
            vertices[i] = new Vector3f();
            texCoords[i] = new Vector2f();
            normals[i] = new Vector3f();
            tangents[i] = new Vector3f();
        }

        //vertical angle
        float startTheta = 180.0f - maxAngle; //top (where rim of bowl is)
        float endTheta = 180.0f; //bottom of sphere
        float innerRadius = radius - thickness;

        // calculate triangle vertices
        for (int i = 0; i <= precVertical; i++) { //vertical loop
            float degreeTheta = startTheta+i*(endTheta-startTheta)/precVertical;
            float y = (float)cos(toRadians(degreeTheta));
            float r = (float)sin(toRadians(degreeTheta)); //same thing as abs(cos(asin(y))). radius of current horizontal ring

            for (int j = 0; j <= precHorizontal; j++) { //horizontal loop
                float degreePhi = j*360.0f/precHorizontal;
                float x = -(float)cos(toRadians(degreePhi)) * r;
                float z =  (float)sin(toRadians(degreePhi)) * r;

                int outerIndex = i*(precHorizontal+1)+j;
                int innerIndex = shellVerts + outerIndex;

                vertices[outerIndex].set(x*radius, y*radius, z*radius);
                texCoords[outerIndex].set((float)j/precHorizontal, (float)i/precVertical);
                normals[outerIndex].set(x,y,z);

                vertices[innerIndex].set(x * innerRadius, y * innerRadius, z * innerRadius);
                texCoords[innerIndex].set((float) j / precHorizontal, (float) i / precVertical);
                normals[innerIndex].set(-x, -y, -z);

                //calculate tangent vector
                if ((x == 0 && z == 0)){
                    tangents[outerIndex].set(0.0f, 0.0f, -1.0f);
                    tangents[innerIndex].set(0.0f, 0.0f, -1.0f);
                } else{
                    tangents[outerIndex] = (new Vector3f(0,1,0)).cross(new Vector3f(x,y,z));
                    tangents[innerIndex] = (new Vector3f(0,1,0)).cross(new Vector3f(x,y,z));
                }
            }
        }

        //calculate triangle indices
        //outer shell
        int k = 0;
        for (int i = 0; i < precVertical; i++) {
            for (int j = 0; j < precHorizontal; j++) {
                int corner1 = i * (precHorizontal+1)+j;
                int corner2 = corner1 + 1;
                int corner3 = (i + 1) * (precHorizontal+1) + j;
                int corner4 = corner3 + 1;

                indices[k++] = corner1;
                indices[k++] = corner2;
                indices[k++] = corner3;
                indices[k++] = corner2;
                indices[k++] = corner4;
                indices[k++] = corner3;
            }
        }

        //inner shell
        for (int i = 0; i < precVertical; i++) {
            for (int j = 0; j < precHorizontal; j++) {
                int corner1 = shellVerts + i * (precHorizontal + 1) + j;
                int corner2 = corner1 + 1;
                int corner3 = shellVerts + (i + 1) * (precHorizontal + 1) + j;
                int corner4 = corner3 + 1;

                indices[k++] = corner1;
                indices[k++] = corner3;
                indices[k++] = corner2;
                indices[k++] = corner2;
                indices[k++] = corner3;
                indices[k++] = corner4;
            }
        }
    }
    

    public int getNumIndices() { return numIndices; }
    public int getNumVertices() { return numVertices; }
    public int[] getIndices() { return indices; }
    public Vector3f[] getVertices() { return vertices; }
    public Vector2f[] getTexCoords() { return texCoords; }
    public Vector3f[] getNormals() { return normals; }
    public Vector3f[] getTangents() { return tangents; }
}