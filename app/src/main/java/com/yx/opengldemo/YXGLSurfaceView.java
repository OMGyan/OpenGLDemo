package com.yx.opengldemo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Author by YX, Date on 2020/1/9.
 *
 * 用于OpenGL渲染
 */
public class YXGLSurfaceView extends GLSurfaceView{
    public YXGLSurfaceView(Context context) {
        this(context,null);
    }

    public YXGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //通知默认的EGLContextFactory和默认的EGLConfigChooser,选择哪个EGLContext客户端版本
        setEGLContextClientVersion(2);
        //设置与此视图关联的渲染器。同时启动那个线程,将调用渲染器，这又将导致渲染开始.
        //setRenderer(new YXRenderer(context));
        setRenderer(new YXImageRenderer(context));
    }
}
