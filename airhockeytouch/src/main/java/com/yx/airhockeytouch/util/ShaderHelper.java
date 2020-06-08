package com.yx.airhockeytouch.util;

import android.util.Log;



import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Author by YX, Date on 2020/6/4.
 */
public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER,shaderCode);
    }

    public static int compileFragmentShader(String shaderCode){
        return compileShader(GL_FRAGMENT_SHADER,shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        //创建一个新的着色器对象，并把这个对象的ID存入shaderObjectId变量
        final int shaderObjectId = glCreateShader(type);
        //如果对象创建失败，就会返回0
        if(shaderObjectId == 0){
            if(LoggerConfig.ON){
                Log.w(TAG,"Could not create new shader.");
            }
            return 0;
        }
        //告诉OpenGL读入字符串shaderCode定义的源代码,并把它与shaderObjectId所引用的着色器对象关联起来
        glShaderSource(shaderObjectId,shaderCode);
        //编译这个着色器，这个调用告诉OpenGL编译前先上传到shaderObjectId的源代码
        glCompileShader(shaderObjectId);
        //创建一个记录编译状态的int数组
        final int[] compileStatus = new int[1];
        //检查是否编译成功，调用该函数，就告诉OpenGL读取与shaderObjectId关联的编译状态，并把它写入compileStatus
        glGetShaderiv(shaderObjectId,GL_COMPILE_STATUS,compileStatus,0);
        //取出着色器信息日志
        if(LoggerConfig.ON){
            Log.v(TAG,"Results of compiling source:"+"\n"+shaderCode+"\n:"+glGetProgramInfoLog(shaderObjectId));
        }
        if(compileStatus[0] == 0){
            //如果编译失败就告诉OpenGL删除对象并返回0
            glDeleteShader(shaderObjectId);
            if(LoggerConfig.ON){
                Log.w(TAG,"Compilation of shader failed.");
            }
            return 0;
        }
        //最终编译成功便把着色器ID返回
        return shaderObjectId;
    }


    public static int linkProgram(int vertexShaderId,int fragmentShaderId){
        //新建程序(指OpenGL程序中的一个组件)对象，并把ID存入到变量programObjectId
        final int programObjectId = glCreateProgram();
        if(programObjectId == 0){
            //对象创建失败返回0
            if(LoggerConfig.ON){
                Log.w(TAG,"Could not create new program");
            }
            return 0;
        }
        //把着色器附加在程序对象上
        glAttachShader(programObjectId,vertexShaderId);
        glAttachShader(programObjectId,fragmentShaderId);
        //链接程序
        glLinkProgram(programObjectId);
        //检查链接成功还是失败
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId,GL_LINK_STATUS,linkStatus,0);
        if(LoggerConfig.ON){
            Log.v(TAG,"Results of linking program:\n"+glGetProgramInfoLog(programObjectId));
        }
        //如果链接失败，该程序将无法使用，所以要删除并返回0给调用者
        if(linkStatus[0] == 0){
            glDeleteShader(programObjectId);
            if(LoggerConfig.ON){
                Log.w(TAG,"Linking of program failed.");
            }
            return 0;
        }
        //链接成功，就返回这个新程序对象
        return programObjectId;
    }

    //验证OpenGL程序的对象
    public static boolean validateProgram(int programObjectId){
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId,GL_VALIDATE_STATUS,validateStatus,0);
        Log.v(TAG,"Results of validating program:"+ validateStatus[0]+"\nLog:"+glGetProgramInfoLog(programObjectId));
        return validateStatus[0]!=0;
    }

    public static int buildProgram(String vertexShaderSource,String fragmentShaderSource){
        int program;
        //编译shader
        int fragmentShader = compileFragmentShader(fragmentShaderSource);
        int vertexShader = compileVertexShader(vertexShaderSource);
        program = linkProgram(vertexShader,fragmentShader);
        if(LoggerConfig.ON){
            validateProgram(program);
        }
        return program;
    }
}
