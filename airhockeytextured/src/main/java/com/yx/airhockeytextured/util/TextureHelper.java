package com.yx.airhockeytextured.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

/**
 * Author by YX, Date on 2020/6/5.
 */
public class TextureHelper {

    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context,int resourceId){
        final int[] textureObjectIds = new int[1];
        glGenTextures(1,textureObjectIds,0);
        if(textureObjectIds[0] == GL_FALSE){
            if(LoggerConfig.ON){
                Log.w(TAG,"Could not generate a new OpenGL texture object.");
            }
            return GL_FALSE;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if(bitmap == null){
            if(LoggerConfig.ON){
                Log.w(TAG,"Resource ID "+resourceId+" could not be decoded.");
            }
            glDeleteTextures(1,textureObjectIds,0);
            return GL_FALSE;
        }
        //绑定纹理
        glBindTexture(GL_TEXTURE_2D,textureObjectIds[0]);
        //过滤纹理
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        //加载位图数据到OpenGL
        texImage2D(GL_TEXTURE_2D,0,bitmap,0);
        //回收位图
        bitmap.recycle();
        //生成MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        //解除绑定纹理
        glBindTexture(GL_TEXTURE_2D,0);
        return textureObjectIds[0];
    }

}
