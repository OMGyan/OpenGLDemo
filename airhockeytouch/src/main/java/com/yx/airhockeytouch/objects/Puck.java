package com.yx.airhockeytouch.objects;

import com.yx.airhockeytouch.data.VertexArray;

import com.yx.airhockeytouch.programs.ColorShaderProgram;
import com.yx.airhockeytouch.util.Geometry.Point;
import com.yx.airhockeytouch.util.Geometry.Cylinder;

import java.util.List;

/**
 * Author by YX, Date on 2020/6/6.
 */
public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius,height;
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createPuck(new Cylinder(new Point(0f, 0f, 0f), radius, (int) height), numPointsAroundPuck);
        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorShaderProgram){
        vertexArray.setVertexAttribPointer(0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,0
                );
    }

    public void draw(){
        for (ObjectBuilder.DrawCommand command : drawList) {
            command.draw();
        }
    }
}
