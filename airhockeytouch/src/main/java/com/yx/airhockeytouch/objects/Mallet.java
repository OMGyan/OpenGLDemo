package com.yx.airhockeytouch.objects;



import com.yx.airhockeytouch.data.VertexArray;
import com.yx.airhockeytouch.util.Geometry;
import com.yx.airhockeytouch.programs.ColorShaderProgram;

import java.util.List;


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
