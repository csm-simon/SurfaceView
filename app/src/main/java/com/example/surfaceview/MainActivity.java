package com.example.surfaceview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import com.example.surfaceview.CameraGLSurfaceView.CameraActivity;
import com.example.surfaceview.GLSurfaceView.GLSurfaceViewActivity;
import com.example.surfaceview.SurfaceView.SurfaceViewActivity;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //Text To Bitmap
//        mET = findViewById(R.id.et);
//        mIv = findViewById(R.id.iv);
//        mDTV = new DrawTextUtil();
//        mIv.setImageBitmap(mDTV.drawText("Simon",100,100,100,100));

        findViewById(R.id.btn_CAMERA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_GLSurfaceView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GLSurfaceViewActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_SurfaceView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SurfaceViewActivity.class);
                startActivity(intent);
            }
        });
    }

}
