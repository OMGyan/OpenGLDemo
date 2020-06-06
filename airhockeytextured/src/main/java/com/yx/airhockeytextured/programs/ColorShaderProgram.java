package com.yx.airhockeytextured.programs;

import android.content.Context;

import com.yx.airhockeytextured.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Author by YX, Date on 2020/6/6.
 */
public class ColorShaderProgram extends ShaderProgram{

    private final int uMatrixLocation;
    private final int aColorLocation;
    private final int aPositionLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader,R.raw.simple_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        aColorLocation = glGetAttribLocation(program,A_COLOR);
        aPositionLocation = glGetAttribLocation(program,A_POSITION);
    }

    public void setUniforms(float[] matrix){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

    public int getColorAttributeLocation(){
        return aColorLocation;
    }


}
