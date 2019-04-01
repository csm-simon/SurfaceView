package com.example.surfaceview.CameraGLSurfaceView.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import com.example.surfaceview.CameraGLSurfaceView.camera.SimonCamera;
import com.example.surfaceview.CameraGLSurfaceView.camera.SimonCameraController;
import com.example.surfaceview.CommonUtil.TextResourceReader;
import com.example.surfaceview.CommonUtil.TextureUtil;
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

    private int mBeautyLevelPosition = -1;

    private int mAttrPosition = -1;

    private int mAttrTexcoord = -1;

    private int mTextureId = -1;

    private SurfaceTexture mSurfaceTexture;

    private FloatBuffer mVertexBuffer;

    private FloatBuffer mTextureVertexesBuffer;

    private Float mBeautyLevel = 0.0f;//0~1

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

    private float[] textureVertexes_back = {
            0,1,
            0,0,
            1,1,
            1,0
    };

    private float[] textureVertexes_front = {
            1,1,
            1,0,
            0,1,
            0,0
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

    public void setUpVertexByteBuffer() {
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertexes.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexByteBuffer.asFloatBuffer();
        mVertexBuffer.put(vertexes);
        mVertexBuffer.position(0);
    }

    public void setUpTextureByteBuffer() {
        if (SimonCameraController.getInstance().getCurFacing() == SimonCamera.Facing.BACK) {
            ByteBuffer textureByteBuffer = ByteBuffer.allocateDirect(textureVertexes_back.length * 4);
            textureByteBuffer.order(ByteOrder.nativeOrder());
            mTextureVertexesBuffer = textureByteBuffer.asFloatBuffer();
            mTextureVertexesBuffer.put(textureVertexes_back);
            mTextureVertexesBuffer.position(0);
        } else {
            ByteBuffer textureByteBuffer = ByteBuffer.allocateDirect(textureVertexes_front.length * 4);
            textureByteBuffer.order(ByteOrder.nativeOrder());
            mTextureVertexesBuffer = textureByteBuffer.asFloatBuffer();
            mTextureVertexesBuffer.put(textureVertexes_front);
            mTextureVertexesBuffer.position(0);
        }
    }

    public void setmBeautyLevel(Float beautyLevel) {
        mBeautyLevel = beautyLevel;
    }

    public void onDestroy() {
        SimonCameraController.getInstance().stopCamera();
    }

    class MyCameraRenderer implements Renderer, SurfaceTexture.OnFrameAvailableListener {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            mVertexShader = loadShader(GL_VERTEX_SHADER, TextResourceReader.readTextFileFromResource(mContext, R.raw.vertex_shader));
            mFragmentShader = loadShader(GL_FRAGMENT_SHADER,TextResourceReader.readTextFileFromResource(mContext,R.raw.fragment_shader_camera));

            mProgram = glCreateProgram();
            glAttachShader(mProgram,mVertexShader);
            glAttachShader(mProgram,mFragmentShader);
            glLinkProgram(mProgram);

            mTextureId = TextureUtil.createTexture(GL_TEXTURE_EXTERNAL_OES);

            mSurfaceTexture = new SurfaceTexture(mTextureId);
            mSurfaceTexture.setOnFrameAvailableListener(this);

            SimonCameraController.getInstance().createCamera();

            setUpVertexByteBuffer();
            setUpTextureByteBuffer();

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            glViewport(0,0,width,height);

            SimonCameraController.getInstance().setPreviewSurface(mSurfaceTexture);
            SimonCameraController.getInstance().startPreview();

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            glClearColor(1.0f,1.0f,1.0f,1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BITS);

            glUseProgram(mProgram);

            //获取一帧数据
            mSurfaceTexture.updateTexImage();

            glActiveTexture(mTextureId);
            glBindTexture(GL_TEXTURE_EXTERNAL_OES,mTextureId);

            mAttrPosition = glGetAttribLocation(mProgram,"position");
            glEnableVertexAttribArray(mAttrPosition);
            glVertexAttribPointer(mAttrPosition,COORDS_PER_VERTEX,GL_FLOAT,false,vertexStride,mVertexBuffer);

            mAttrTexcoord = glGetAttribLocation(mProgram, "texcoord");
            glEnableVertexAttribArray(mAttrTexcoord);
            glVertexAttribPointer(mAttrTexcoord,COORDS_PER_VERTEX,GL_FLOAT,false,vertexStride,mTextureVertexesBuffer);

            mBeautyLevelPosition = glGetUniformLocation(mProgram,"beautyLevel");
            glUniform1f(mBeautyLevelPosition,mBeautyLevel);

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
