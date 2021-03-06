package com.yx.airhockeywithbettermallets.objects;



import com.yx.airhockeywithbettermallets.data.VertexArray;
import com.yx.airhockeywithbettermallets.programs.ColorShaderProgram;
import com.yx.airhockeywithbettermallets.util.Geometry;

import java.util.List;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static com.yx.airhockeywithbettermallets.Constants.BYTES_PER_FLOAT;


/**
 * Author by YX, Date on 2020/6/5.
 */
public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius,height;
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Mallet(float radius, float height, int numPointsAroundMallet) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(new Geometry.Point(0f, 0f, 0f), radius,height, numPointsAroundMallet);
        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorShaderProgram){
        vertexArray.setVertexAttribPointer(0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0);


    }

    public void draw(){
        for (ObjectBuilder.DrawCommand command : drawList) {
            command.draw();
        }
    }
}
