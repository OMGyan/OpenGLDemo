package com.yx.airhockeytextured;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.yx.airhockeytextured.util.LoggerConfig;
import com.yx.airhockeytextured.util.MatrixHelper;
import com.yx.airhockeytextured.util.ShaderHelper;
import com.yx.airhockeytextured.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

/**
 * Author by YX, Date on 2020/6/2.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    //记录顶点的又两个分量
    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private Context context;
    private int program;
    private static final String A_COLOR = "a_Color";
    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private int aColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;
    private final float[] modelMatrix = new float[16];

    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
                 //三角形扇(卷曲顺序可以优化性能)
                    0f,   0f,0f,1.5f,  1f,  1f,  1f,
                 -0.5f,-0.8f,0f,  1f,0.7f,0.7f,0.7f,
                  0.5f,-0.8f,0f,  1f,0.7f,0.7f,0.7f,
                  0.5f, 0.8f,0f,  2f,0.7f,0.7f,0.7f,
                 -0.5f, 0.8f,0f,  2f,0.7f,0.7f,0.7f,
                 -0.5f,-0.8f,0f,  1f,0.7f,0.7f,0.7f,
                 //直线
                 -0.5f,0f,0f,1.5f,1f,0f,0f,
                  0.5f,0f,0f,1.5f,1f,0f,0f,
                 //两个点
                 0f,-0.4f,0f,1.25f,0f,0f,1f,
                 0f, 0.4f,0f,1.75f,1f,0f,0f
        };

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f,0.0f,0.0f,0.0f);
        String vertexShaderResource = TextResourceReader.readRawText(context, R.raw.simple_vertex_shader);
        String fragmentShaderResource = TextResourceReader.readRawText(context, R.raw.simple_fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderResource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderResource);
        program = ShaderHelper.linkProgram(vertexShader,fragmentShader);
        if(LoggerConfig.ON){
            ShaderHelper.validateProgram(program);
        }
        //告诉OpenGL在绘制任何东西到屏幕上时要使用这里定义的program
        glUseProgram(program);
        aColorLocation = glGetAttribLocation(program,A_COLOR);
        //获取属性的位置
        aPositionLocation = glGetAttribLocation(program,A_POSITION);
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        //保证数据是从开头开始读取
        vertexData.position(0);
        //关联属性与顶点数据的数组
        glVertexAttribPointer(aPositionLocation,POSITION_COMPONENT_COUNT,GL_FLOAT,false,STRIDE,vertexData);
        //使能顶点数组
        glEnableVertexAttribArray(aPositionLocation);
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation,COLOR_COMPONENT_COUNT,GL_FLOAT,false,STRIDE,vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width,height);
        MatrixHelper.perspectiveM(projectionMatrix,45,(float)width/(float)height,1f,10f);
        setIdentityM(modelMatrix,0);
        translateM(modelMatrix,0,0f,0f,-2.5f);
        rotateM(modelMatrix,0,-60f,1f,0f,0f);
        final float[] temp = new float[16];
        multiplyMM(temp,0,projectionMatrix,0,modelMatrix,0);
        System.arraycopy(temp,0,projectionMatrix,0,temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation,1,false,projectionMatrix,0);
        //绘制桌子
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
        //绘制分割线
        glDrawArrays(GL_LINES,6,2);
        //绘制棒槌点
        glDrawArrays(GL_POINTS,8,1);
        //绘制棒槌点
        glDrawArrays(GL_POINTS,9,1);
    }
}
