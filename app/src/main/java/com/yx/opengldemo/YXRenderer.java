package com.yx.opengldemo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Author by YX, Date on 2020/1/9.
 */
public class YXRenderer implements GLSurfaceView.Renderer{

    private Context context;
//    //创建顶点坐标数组(三角形)
//    private final float[] vertexData = {
//            -1f,0f,
//            0f,1f,
//            1f,0f
//    };

    //创建顶点坐标数组(四边形)要注意图形环绕方向必须一致(GLES20.GL_TRIANGLES)
//    private final float[] vertexData = {
//            -1f,0f,
//            0f,-1f,
//            0f,1f,
//
//            0f,-1f,
//            1f,0f,
//            0f,1f
//    };

    //创建顶点坐标数组(四边形)要注意图形环绕方向必须一致(GLES20.GL_TRIANGLE_STRIP)
    private final float[] vertexData = {
            -1f,0f,
            0f,-1f,
            0f,1f,
            1f,0f
    };

    //创建顶点坐标缓存
    private FloatBuffer vertexBuffer;
    private int program;
    private int av_position;
    private int af_color;

    public YXRenderer(Context context) {
        this.context = context;
        //分配一个新的直接字节缓冲区(每个单位为4字节)。
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                //修改此缓冲区的字节顺序。
                .order(ByteOrder.nativeOrder())
                //创建此字节缓冲区的视图作为浮点缓冲区。
                .asFloatBuffer()
                //把坐标放入
                .put(vertexData);
        //设置此缓冲区的位置。
        vertexBuffer.position(0);
    }

    //在创建或重新创建界面时调用。
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //拿到顶点着色器源码
        String vertexShaderSource = YXShaderUtil.readRawText(context, R.raw.vertex_shader);
        //拿到片面着色器源码
        String fragmentShaderSource = YXShaderUtil.readRawText(context, R.raw.fragment_shader);
        //获取program对象
        program = YXShaderUtil.createProgram(vertexShaderSource,fragmentShaderSource);
        if(program > 0){
            //获取成功之后，拿到内部属性
            av_position = GLES20.glGetAttribLocation(program, "av_Position");
            af_color = GLES20.glGetUniformLocation(program, "af_Color");
        }

    }
    //当表面更改大小时调用。
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //x，y 以像素为单位，指定了视口的左下角位置。
        //width，height 表示这个视口矩形的宽度和高度，根据窗口的实时变化重绘窗口。
        GLES20.glViewport(0,0,width,height);
    }
    //调用以绘制当前帧。
    @Override
    public void onDrawFrame(GL10 gl) {
        //整个窗口清除为黑色
        //参数为相关缓冲区
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //用白色清屏
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        //使用program
        GLES20.glUseProgram(program);
        /**
         * 为当前程序对象指定Uniform变量的值。
         * location:指明要更改的uniform变量的位置
         * v0,v1,v2,v3:指明在指定的uniform变量中要使用的新值
         */
        GLES20.glUniform4f(af_color,1f,0f,0f,1f);//红色
        //把顶点坐标设置成可用状态
        GLES20.glEnableVertexAttribArray(av_position);
        /**
         * 指定渲染时索引值为 index 的顶点属性数组的数据格式和位置
         * index:指定要修改的顶点属性的索引值.
         * size:指定每个顶点属性的组件数量,必须为1、2、3或者4。初始值为4,(如position是由3个(x,y,z)组成，而颜色是4个(r,g,b,a)).
         * type:指定数组中每个组件的数据类型.
         * normalized:指定当被访问时，固定点数据值是否应该被归一化（GL_TRUE）或者直接转换为固定点值（GL_FALSE).
         * stride:指定连续顶点属性之间的偏移量.
         * pointer:指定第一个组件在数组的第一个顶点属性中的偏移量。该数组与GL_ARRAY_BUFFER绑定，储存于缓冲区中。初始值为0.
         */
        GLES20.glVertexAttribPointer(av_position,2,GLES20.GL_FLOAT,false,8,vertexBuffer);
        /**
         * 根据顶点数组中的坐标数据和指定的模式，进行绘制。
         * mode:绘制方式，OpenGL2.0以后提供以下参数：GL_POINTS、GL_LINES、GL_LINE_LOOP、GL_LINE_STRIP、GL_TRIANGLES、GL_TRIANGLE_STRIP、GL_TRIANGLE_FAN。
         * first:从数组缓存中的哪一位开始绘制，一般为0。
         * count:数组中顶点的数量。
         */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
    }
}
