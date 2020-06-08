package com.yx.airhockeytouch.programs;

import android.content.Context;


import com.yx.airhockeytouch.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Author by YX, Date on 2020/6/6.
 */
public class ColorShaderProgram extends ShaderProgram{

    private final int uMatrixLocation;
    private final int uColorLocation;
    private final int aPositionLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader,R.raw.simple_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        uColorLocation = glGetUniformLocation(program,U_COLOR);
        aPositionLocation = glGetAttribLocation(program,A_POSITION);
    }

    public void setUniforms(float[] matrix,float r,float g,float b){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
        glUniform4f(uColorLocation,r,g,b,1f);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

}
