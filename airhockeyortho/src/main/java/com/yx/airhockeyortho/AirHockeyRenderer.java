package com.yx.airhockeyortho;

import android.content.Context;
import android.opengl.GLSurfaceView;


import com.yx.airhockeyortho.util.LoggerConfig;
import com.yx.airhockeyortho.util.ShaderHelper;
import com.yx.airhockeyortho.util.TextResourceReader;

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
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private Context context;
    private int program;
    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private int aColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
                //三角形扇(卷曲顺序可以优化性能)
                 0,0,1f,1f,1f,
                 -0.5f,-0.5f,0.7f,0.7f,0.7f,
                 0.5f,-0.5f,0.7f,0.7f,0.7f,
                 0.5f,0.5f,0.7f,0.7f,0.7f,
                -0.5f,0.5f,0.7f,0.7f,0.7f,
                -0.5f,-0.5f,0.7f,0.7f,0.7f,
                //直线
                -0.5f,0f,1f,0f,0f,
                0.5f,0f,1f,0f,0f,
                //两个点
                0f,-0.25f,0f,0f,1f,
                0f,0.25f,1f,0f,0f
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
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

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
