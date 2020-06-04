package com.yx.airhockey1;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.yx.airhockey1.util.TextResourceReader;
import com.yx.airhockey1.util.LoggerConfig;
import com.yx.airhockey1.util.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.*;

/**
 * Author by YX, Date on 2020/6/2.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    //记录顶点的又两个分量
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private Context context;
    private int program;
    private static final String U_COLOR = "u_Color";
    private int uColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
                //三角形1(卷曲顺序可以优化性能)
                -0.5f,-0.5f,
                 0.5f,0.5f,
                -0.5f,0.5f,
                //三角形2
                -0.5f,-0.5f,
                0.5f,-0.5f,
                0.5f,0.5f,
                //直线
                -0.5f,0f,
                0.5f,0f,
                //两个点
                0f,-0.25f,
                0f,0.25f
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
        //获取uniform的位置
        uColorLocation = glGetUniformLocation(program,U_COLOR);
        //获取属性的位置
        aPositionLocation = glGetAttribLocation(program,A_POSITION);
        //保证数据是从开头开始读取
        vertexData.position(0);
        //关联属性与顶点数据的数组
        glVertexAttribPointer(aPositionLocation,POSITION_COMPONENT_COUNT,GL_FLOAT,false,0,vertexData);
        //使能顶点数组
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        //更新着色器代码中u_Color的值
        glUniform4f(uColorLocation,1.0f,1.0f,1.0f,1.0f);
        //绘制桌子
        glDrawArrays(GL_TRIANGLES,0,6);
        //更新着色器代码中u_Color的值
        glUniform4f(uColorLocation,1.0f,0.0f,0.0f,1.0f);
        //绘制分割线
        glDrawArrays(GL_LINES,6,2);
        //更新着色器代码中u_Color的值
        glUniform4f(uColorLocation,0.0f,0.0f,1.0f,1.0f);
        //绘制棒槌点
        glDrawArrays(GL_POINTS,8,1);
        //更新着色器代码中u_Color的值
        glUniform4f(uColorLocation,1.0f,0.0f,0.0f,1.0f);
        //绘制棒槌点
        glDrawArrays(GL_POINTS,9,1);
    }
}
