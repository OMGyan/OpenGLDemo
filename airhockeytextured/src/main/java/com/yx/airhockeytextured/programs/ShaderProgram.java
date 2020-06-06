package com.yx.airhockeytextured.programs;

import android.content.Context;

import com.yx.airhockeytextured.util.ShaderHelper;
import com.yx.airhockeytextured.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Author by YX, Date on 2020/6/5.
 */
public class ShaderProgram {
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    protected final int program;
    protected ShaderProgram(Context context,int vertexShaderResourceId,int fragmentShaderResourceId){
        program = ShaderHelper.buildProgram(TextResourceReader.readRawText(context,vertexShaderResourceId),
                TextResourceReader.readRawText(context,fragmentShaderResourceId));
    }

    public void useProgram(){
        glUseProgram(program);
    }
}
