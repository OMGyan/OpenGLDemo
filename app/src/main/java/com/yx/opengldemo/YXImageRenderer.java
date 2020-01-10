package com.yx.opengldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



/**
 * Author by YX, Date on 2020/1/10.
 */
public class YXImageRenderer implements GLSurfaceView.Renderer{

    private Context context;
    //创建顶点坐标缓存
    private FloatBuffer vertexBuffer;
    //创建纹理坐标缓存
    private FloatBuffer textureBuffer;
    private int program;
    private int av_position;
    private int af_position;
    private int textureId;

    //创建顶点坐标数组(全屏)(GLES20.GL_TRIANGLE_STRIP)
    private final float[] vertexData = {
            -1f,-1f,
            1f,-1f,
            -1f,1f,
            1f,1f,
    };

    //创建纹理坐标数组(对应顶点坐标系)
    private final float[] textureData = {
             0f,1f,
             1f,1f,
             0f,0f,
             1f,0f
             //倒序
//             1f,0f,
//             0f,0f,
//             1f,1f,
//             0f,1f,
    };

    public YXImageRenderer(Context context) {
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

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //拿到顶点着色器源码
        String vertexShaderSource = YXShaderUtil.readRawText(context, R.raw.texture_vertex_shader);
        //拿到片面着色器源码
        String fragmentShaderSource = YXShaderUtil.readRawText(context, R.raw.texture_fragment_shader);
        //获取program对象
        program = YXShaderUtil.createProgram(vertexShaderSource,fragmentShaderSource);
        if(program > 0){
            //获取成功之后，拿到内部属性
            av_position = GLES20.glGetAttribLocation(program, "av_Position");
            af_position = GLES20.glGetAttribLocation(program, "af_Position");
            //创建纹理数量数组
            int[] textureIds = new int[1];
            //生成纹理
            GLES20.glGenTextures(1,textureIds,0);
            //等于0生成失败
            if(textureIds[0]==0){
                return;
            }
            textureId = textureIds[0];
            //绑定这个纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
            //设置环绕和过滤方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.og);
            //绘制bitmap(定义一个二维纹理映射)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //x，y 以像素为单位，指定了视口的左下角位置。
        //width，height 表示这个视口矩形的宽度和高度，根据窗口的实时变化重绘窗口。
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //整个窗口清除为黑色
        //参数为相关缓冲区
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //用白色清屏
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        //使用program
        GLES20.glUseProgram(program);
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
        GLES20.glEnableVertexAttribArray(af_position);
        GLES20.glVertexAttribPointer(af_position,2,GLES20.GL_FLOAT,false,8,textureBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
    }
}
