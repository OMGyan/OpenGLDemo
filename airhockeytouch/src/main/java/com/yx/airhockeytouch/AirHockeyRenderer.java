package com.yx.airhockeytouch;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.yx.airhockeytouch.programs.TextureShaderProgram;

import com.yx.airhockeytouch.util.Geometry;
import com.yx.airhockeytouch.util.Geometry.Vector;
import com.yx.airhockeytouch.util.Geometry.Plane;
import com.yx.airhockeytouch.util.Geometry.Sphere;
import com.yx.airhockeytouch.util.Geometry.Ray;
import com.yx.airhockeytouch.util.Geometry.Point;
import com.yx.airhockeytouch.util.MatrixHelper;
import com.yx.airhockeytouch.objects.Mallet;
import com.yx.airhockeytouch.objects.Puck;
import com.yx.airhockeytouch.objects.Table;
import com.yx.airhockeytouch.programs.ColorShaderProgram;
import com.yx.airhockeytouch.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

/**
 * Author by YX, Date on 2020/6/2.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];
    private Table table;
    private Mallet mallet;
    private Puck puck;
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;
    private boolean malletPressed = false;
    private Point blueMalletPosition;
    private Point previousBlueMalletPosition;
    //储存冰球得位置，速度，方向
    private Point puckPosition;
    private Vector puckVector;

    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f,0.0f,0.0f,0.0f);
        table = new Table();
        mallet = new Mallet(0.08f,0.15f,32);
        blueMalletPosition = new Point(0f,mallet.height / 2f,0.4f);
        puck = new Puck(0.06f,0.02f,32);
        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context,R.drawable.air_hockey_surface);

        puckPosition = new Point(0f,puck.height/2f,0f);
        puckVector = new Vector(0f,0f,0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width,height);
        MatrixHelper.perspectiveM(projectionMatrix,45,(float)width/(float)height,1f,10f);
        setLookAtM(viewMatrix,0,0f,1.2f,2.2f,0f,0f,0f,0f,1f,0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        glClear(GL_COLOR_BUFFER_BIT);
        puckPosition = puckPosition.translate(puckVector);

        if (puckPosition.x < leftBound + puck.radius
                || puckPosition.x > rightBound - puck.radius) {
            puckVector = new Vector(-puckVector.x, puckVector.y, puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        if (puckPosition.z < farBound + puck.radius
                || puckPosition.z > nearBound - puck.radius) {
            puckVector = new Vector(puckVector.x, puckVector.y, -puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }

        puckPosition = new Point(
                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        );

        puckVector = puckVector.scale(0.99f);
        multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0);
        invertM(invertedViewProjectionMatrix,0,viewProjectionMatrix,0);
        positionTableInScreen();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix,texture);
        table.bindData(textureProgram);
        table.draw();

        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
        puckVector = puckVector.scale(0.99f);

    }


    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix,0);
        translateM(modelMatrix,0,x,y,z);
        multiplyMM(modelViewProjectionMatrix,0,viewProjectionMatrix,0,modelMatrix,0);
    }

    private void positionTableInScreen() {
        setIdentityM(modelMatrix,0);
        rotateM(modelMatrix,0,-90f,1f,0f,0f);
        multiplyMM(modelViewProjectionMatrix,0,viewProjectionMatrix,0,modelMatrix,0);
    }

    public void handleTouchPress(float normalizedX,float normalizedY){
        Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        Sphere malletBoundingSphere = new Sphere(new Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z), mallet.height / 2f);
        malletPressed = Geometry.intersects(malletBoundingSphere,ray);
        Log.v("YX",malletPressed+"");
    }

    public void handleTouchDrag(float normalizedX,float normalizedY){
        if(malletPressed){
            Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
            Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 1, 0));
            Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            //blueMalletPosition = new Point(touchedPoint.x,mallet.height/2f,touchedPoint.z);
            //记录上一次得位置
            previousBlueMalletPosition = blueMalletPosition;
            //限制边界
            blueMalletPosition = new Point(clamp(touchedPoint.x,leftBound + mallet.radius,rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z,0f + mallet.radius,nearBound - mallet.radius)
                    );


            //碰撞检测

            float distance = Geometry.vectorBetween(blueMalletPosition,puckPosition).length();
            if(distance < (puck.radius + mallet.radius)){
                //如果冰锤击中冰球，创建冰球得方向向量
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition,blueMalletPosition);
            }
        }
    }

    private float clamp(float value,float min,float max){
        return Math.min(max,Math.max(value,min));
    }

    private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY){
        final float[] nearPointNdc = {normalizedX,normalizedY,-1,1};
        final float[] farPointNdc = {normalizedX,normalizedY,1,1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        multiplyMV(nearPointWorld,0,invertedViewProjectionMatrix,0,nearPointNdc,0);
        multiplyMV(farPointWorld,0,invertedViewProjectionMatrix,0,farPointNdc,0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Point nearPointRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Point farPointRay = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
        return new Ray(nearPointRay, Geometry.vectorBetween(nearPointRay,farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }
}

