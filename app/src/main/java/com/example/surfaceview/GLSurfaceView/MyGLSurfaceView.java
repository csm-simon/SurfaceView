package com.example.surfaceview.GLSurfaceView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.util.Log;
import com.example.surfaceview.R;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

public class MyGLSurfaceView extends GLSurfaceView {
    private static String TAG = "MyGLSurfaceView";

    private Context mContext;

    private int mVertexShader = -1;

    private int mFragmentShader = -1;

    private int mAttrPosition = -1;

    private int mAttrTexcoord = -1;

    private int mUniformOriginalTexture = -1;
    
    private int mUniformEffectTexture = -1;

    private int mProgram = -1;

    private int[] mTextureId = new int[2];

    private Bitmap mBitmap;

    private Bitmap effectBitmap;

    private FloatBuffer vertexBuffer;

    private FloatBuffer mImageTextureVertexesBuffer;

    //每个顶点由几个数字来确定
    private static final int COORDS_PER_VERTEX = 2;

    // 4 bytes per vertex
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    //顶点坐标
    static float vertexes[] = {
            -1,  1,
            1,  1,
            -1, -1,
            1, -1
    };

    //图片纹理坐标
    static float[] mImageTextureVertexes=new float[]{
            0.0f, 0,
            1, 0,
            0.0f, 1.0f,
            1, 1.0f
    };

    public MyGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        this.setEGLContextClientVersion(2);
        this.setRenderer(new MyRenderer());
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setDrawable (int originalDrawableID,int effectDrawableID) {
        mBitmap = BitmapFactory.decodeResource(getResources(),originalDrawableID);
        effectBitmap = BitmapFactory.decodeResource(getResources(),effectDrawableID);
    }

    class MyRenderer implements Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertexes.length * 4);
            vertexByteBuffer.order(ByteOrder.nativeOrder());
            vertexBuffer = vertexByteBuffer.asFloatBuffer();
            vertexBuffer.put(vertexes);
            vertexBuffer.position(0);

            ByteBuffer textureByteBuffer = ByteBuffer.allocateDirect(mImageTextureVertexes.length * 4);
            textureByteBuffer.order(ByteOrder.nativeOrder());
            mImageTextureVertexesBuffer = textureByteBuffer.asFloatBuffer();
            mImageTextureVertexesBuffer.put(mImageTextureVertexes);
            mImageTextureVertexesBuffer.position(0);

            mVertexShader = loadShader(GL_VERTEX_SHADER,TextResourceReader.readTextFileFromResource(mContext, R.raw.vertex_shader));
            mFragmentShader = loadShader(GL_FRAGMENT_SHADER,TextResourceReader.readTextFileFromResource(mContext,R.raw.fragment_shader_skin));

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            glViewport(0,0,width,height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            glClearColor(1.0f,1.0f,1.0f,1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (mBitmap != null) {

                mProgram = glCreateProgram();
                glAttachShader(mProgram,mVertexShader);
                glAttachShader(mProgram,mFragmentShader);
                glLinkProgram(mProgram);
                glUseProgram(mProgram);

                glGenTextures(2,mTextureId,0);

                mUniformOriginalTexture = glGetUniformLocation(mProgram,"originalTexture");//获取shader里面uniform变量的地址
                glUniform1i(mUniformOriginalTexture,0);

                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D,mTextureId[0]);

                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                GLUtils.texImage2D(GL_TEXTURE_2D,0,mBitmap,0);

                mUniformEffectTexture = glGetUniformLocation(mProgram,"effectTexture");
                glUniform1i(mUniformEffectTexture,1);

                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D,mTextureId[1]);

                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                GLUtils.texImage2D(GL_TEXTURE_2D,0,effectBitmap,0);

//
//                int radiusUniform = glGetUniformLocation(mProgram,"radius");
//                glUniform1f(radiusUniform,0.01f);
//
//                int typeUniform = glGetUniformLocation(mProgram,"type");
//                glUniform1i(typeUniform,0);

                mAttrPosition = glGetAttribLocation(mProgram,"position");
                glEnableVertexAttribArray(mAttrPosition);
                glVertexAttribPointer(mAttrPosition,COORDS_PER_VERTEX,GL_FLOAT,false,vertexStride,vertexBuffer);

                mAttrTexcoord = glGetAttribLocation(mProgram,"texcoord");
                glEnableVertexAttribArray(mAttrTexcoord);
                glVertexAttribPointer(mAttrTexcoord,COORDS_PER_VERTEX,GL_FLOAT,false,vertexStride,mImageTextureVertexesBuffer);

                glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

            } else {
                Log.e(TAG, "onDrawFrame: mBitmap is null");
            }
        }

        private int loadShader (int shaderType,String shaderString) {
            int shader = glCreateShader(shaderType);
            glShaderSource(shader,shaderString);
            glCompileShader(shader);
            return shader;
        }
    }

}
