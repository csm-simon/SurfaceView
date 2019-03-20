package com.example.surfaceview.GLSurfaceView;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.example.surfaceview.R;

public class GLSurfaceViewActivity extends Activity {

    private MyGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurfaceview);
        mGLSurfaceView = findViewById(R.id.glsurfaceview);
        mGLSurfaceView.setDrawable(R.drawable.picture,R.drawable.red);
    }
}
