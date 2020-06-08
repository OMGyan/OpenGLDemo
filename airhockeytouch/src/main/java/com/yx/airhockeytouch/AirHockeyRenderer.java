package com.yx.airhockeytouch;

import android.content.Context;
import android.opengl.GLSurfaceView;


import com.yx.airhockeytouch.programs.TextureShaderProgram;
import com.yx.airhockeytouch.util.MatrixHelper;
import com.yx.airhockeytouch.objects.Mallet;
import com.yx.airhockeytouch.objects.Puck;
import com.yx.airhockeytouch.objects.Table;
import com.yx.airhockeytouch.programs.ColorShaderProgram;
import com.yx.airhockeytouch.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;


/**
 * Author by YX, Date on 2020/6/2.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private Table table;
    private Mallet mallet;
    private Puck puck;
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f,0.0f,0.0f,0.0f);
        table = new Table();
        mallet = new Mallet(0.08f,0.15f,32);
        puck = new Puck(0.06f,0.02f,32);
        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context,R.drawable.air_hockey_surface);
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
        multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0);
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

        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
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
}

