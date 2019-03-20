package com.example.surfaceview.CameraGLSurfaceView;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import com.example.surfaceview.GLSurfaceView.TextResourceReader;
import com.example.surfaceview.R;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glCompileShader;

public class CameraGLSurfaceView extends GLSurfaceView {
    private static final String TAG = "CameraGLSurfaceView";

    private Context mContext;

    private int mProgram = -1;

    private int mVertexShader = -1;

    private int mFragmentShader = -1;

    private int mPreviewTextureUniformLocation = -1;

    private int mAttrPosition = -1;

    private int mAttrTexcoord = -1;

    private int[] mTextures = new int[1];

    private SurfaceTexture mSurfaceTexture;

    private FloatBuffer mVertexBuffer;

    private FloatBuffer mTextureVertexesBuffer;

    //每个顶点由几个数字来确定
    private static final int COORDS_PER_VERTEX = 2;

    // 4 bytes per vertex
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    private float[] vertexes = {
            -1,1,
            1,1,
            -1,-1,
            1,-1
    };

    private float[] textureVertexes = {
            0,1,
            0,0,
            1,1,
            1,0
    };

    public CameraGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        this.setEGLContextClientVersion(2);
        this.setRenderer(new MyCameraRenderer());
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    class MyCameraRenderer implements Renderer, SurfaceTexture.OnFrameAvailableListener {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertexes.length * 4);
            vertexByteBuffer.order(ByteOrder.nativeOrder());
            mVertexBuffer = vertexByteBuffer.asFloatBuffer();
            mVertexBuffer.put(vertexes);
            mVertexBuffer.position(0);

            ByteBuffer textureByteBuffer = ByteBuffer.allocateDirect(textureVertexes.length * 4);
            textureByteBuffer.order(ByteOrder.nativeOrder());
            mTextureVertexesBuffer = textureByteBuffer.asFloatBuffer();
            mTextureVertexesBuffer.put(textureVertexes);
            mTextureVertexesBuffer.position(0);

            mVertexShader = loadShader(GL_VERTEX_SHADER, TextResourceReader.readTextFileFromResource(mContext, R.raw.vertex_shader));
            mFragmentShader = loadShader(GL_FRAGMENT_SHADER,TextResourceReader.readTextFileFromResource(mContext,R.raw.fragment_shader_camera));

            glGenTextures(1,mTextures,0);
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextures[0]); //将第一个纹理单元保存到mTexture数组的第一个元素
            GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);//设置纹理缩小情况下使用双线性过滤
            GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);//设置纹理放大情况下使用双线性过滤
            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);//设置纹理在横向上平铺
            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);//设置纹理在纵向上平铺

            mSurfaceTexture = new SurfaceTexture(mTextures[0]);
            mSurfaceTexture.setOnFrameAvailableListener(this);

            CameraUtil.getInstance().openCamera();

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            glViewport(0,0,width,height);

            if(!CameraUtil.getInstance().isPreviewing()){
                //开始预览
                CameraUtil.getInstance().startPreview(mSurfaceTexture);
            }

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            glClearColor(1.0f,1.0f,1.0f,1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BITS);

            mProgram = glCreateProgram();
            glAttachShader(mProgram,mVertexShader);
            glAttachShader(mProgram,mFragmentShader);
            glLinkProgram(mProgram);
            glUseProgram(mProgram);

            //获取一帧数据
            mSurfaceTexture.updateTexImage();
            float[] mtx = new float[16];
            mSurfaceTexture.getTransformMatrix(mtx);

            mPreviewTextureUniformLocation = glGetUniformLocation(mProgram,"previewTexture");
            glUniform1f(mPreviewTextureUniformLocation,0);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_EXTERNAL_OES,mTextures[0]);

            mAttrPosition = glGetAttribLocation(mProgram,"position");
            glEnableVertexAttribArray(mAttrPosition);
            glVertexAttribPointer(mAttrPosition,COORDS_PER_VERTEX,GL_FLOAT,false,vertexStride,mVertexBuffer);

            mAttrTexcoord = glGetAttribLocation(mProgram, "texcoord");
            glEnableVertexAttribArray(mAttrTexcoord);
            glVertexAttribPointer(mAttrTexcoord,COORDS_PER_VERTEX,GL_FLOAT,false,vertexStride,mTextureVertexesBuffer);

            glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        }

        private int loadShader (int shaderType,String shaderString) {
            int shader = glCreateShader(shaderType);
            glShaderSource(shader,shaderString);
            glCompileShader(shader);
            return shader;
        }

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            requestRender();
        }
    }
}
