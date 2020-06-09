package com.yx.airhockeytouch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class AirHockeyActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private AirHockeyRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = manager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if(supportsEs2){
            glSurfaceView.setEGLContextClientVersion(2);
            renderer = new AirHockeyRenderer(this);
            glSurfaceView.setRenderer(renderer);
            rendererSet = true;
        }
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event != null){
                    final float normalizedx = (event.getX() / (float) v.getWidth()) * 2 - 1;
                    final float normalizedy = -((event.getY() / (float)v.getHeight()) * 2 - 1);
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                renderer.handleTouchPress(normalizedx,normalizedy);
                            }
                        });
                    }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                renderer.handleTouchDrag(normalizedx,normalizedy);
                            }
                        });
                    }
                    return true;
                }else {
                    return false;
                }
            }
        });
        setContentView(glSurfaceView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(rendererSet){
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(rendererSet){
            glSurfaceView.onResume();
        }
    }
}
