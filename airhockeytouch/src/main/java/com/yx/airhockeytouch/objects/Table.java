package com.yx.airhockeytouch.objects;



import com.yx.airhockeytouch.Constants;
import com.yx.airhockeytouch.data.VertexArray;
import com.yx.airhockeytouch.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;


/**
 * Author by YX, Date on 2020/6/5.
 */
public class Table {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            0f,   0f,0.5f,0.5f,
         -0.5f,-0.8f,  0f,0.9f,
          0.5f,-0.8f,  1f,0.9f,
          0.5f, 0.8f,  1f,0.1f,
         -0.5f, 0.8f,  0f,0.1f,
         -0.5f,-0.8f,  0f,0.9f
    };
    private final VertexArray vertexArray;

    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureProgram){
        vertexArray.setVertexAttribPointer(
  0,
             textureProgram.getPositionAttributeLocation(),
             POSITION_COMPONENT_COUNT,
             STRIDE
         );

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw(){
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
    }
}
