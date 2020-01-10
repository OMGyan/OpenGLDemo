package com.yx.opengldemo;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Author by YX, Date on 2020/1/9.
 */
public class YXShaderUtil {

    /**
     * 读取raw文件
     * @param context
     * @param rawId 文件id
     * @return
     */
    public static String readRawText(Context context,int rawId){
        InputStream inputStream = context.getResources().openRawResource(rawId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer sb = new StringBuffer();
        String line;
        try {
            while ((line = bufferedReader.readLine())!=null){
                sb.append(line).append("\n");
            }
            bufferedReader.close();
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 加载着色器
     * @param shaderType 着色器类型
     * @param source 着色器源码
     * @return
     */
    public static int loadShader(int shaderType,String source){
        //拿到着色器
        int shader = GLES20.glCreateShader(shaderType);
        if(shader!=0){
            //把源码与shader关联起来
            GLES20.glShaderSource(shader,source);
            //进行编译
            GLES20.glCompileShader(shader);
            //定义接收编译状态的数组
            int[] compile = new int[1];
            //检查编译状态
            GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,compile,0);
            if(compile[0]!=GLES20.GL_TRUE){
                //如果编译失败,释放shader
                GLES20.glDeleteShader(shader);
                shader = 0;
                Log.d("YX","shader compile error");
            }
        }
        return shader;
    }

    /**
     * 创建program对象
     * @param vertexSource 顶点着色器源码
     * @param fragmentSource 片面着色器源码
     * @return
     */
    public static int createProgram(String vertexSource,String fragmentSource){
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if(vertexShader == 0){
            return 0;
        }
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if(fragmentShader == 0){
            return 0;
        }
        //获得一个program对象
        int glCreateProgram = GLES20.glCreateProgram();
        if(glCreateProgram!=0){
            //把两个shader依附在program
            GLES20.glAttachShader(glCreateProgram,vertexShader);
            GLES20.glAttachShader(glCreateProgram,fragmentShader);
            //连接这个program
            GLES20.glLinkProgram(glCreateProgram);
            //定义一个接受连接状态的数组
            int linkStatus[] = new int[1];
            //检查连接状态
            GLES20.glGetProgramiv(glCreateProgram,GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]!=GLES20.GL_TRUE){
                //如果连接失败释放program
               Log.d("YX","Link program error");
               GLES20.glDeleteProgram(glCreateProgram);
               glCreateProgram = 0;
            }
        }
        return glCreateProgram;
    }
}
