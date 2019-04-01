package com.example.surfaceview.CommonUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.text.TextUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import java.nio.ByteBuffer;

public class TextureUtil {

    private final static String TAG  = ToastUtil.class.getSimpleName();

    // 从初始化失败
    public static final int GL_NOT_INIT = -1;

    // 没有Texture
    public static final int GL_NOT_TEXTURE = -1;

    /**
     * 创建Texture对象
     * @param textureType
     * @return
     */
    public static int createTexture(int textureType) {
        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        checkGlError("glGenTextures");
        int textureId = textures[0];
        GLES30.glBindTexture(textureType, textureId);
        checkGlError("glBindTexture " + textureId);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        checkGlError("glTexParameter");
        return textureId;
    }

    /**
     * 加载mipmap纹理
     * @param bitmap bitmap图片
     * @return
     */
    public static int createTexture(Bitmap bitmap) {
        int[] texture = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            //生成纹理
            GLES30.glGenTextures(1, texture, 0);
            checkGlError("glGenTexture");
            //生成纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }

    /**
     * 使用旧的Texture 创建新的Texture (宽高不能大于旧Texture的宽高，主要用于贴纸不断切换图片)
     * @param bitmap
     * @param texture
     * @return
     */
    public static int createTexture(Bitmap bitmap, int texture) {
        int[] result = new int[1];
        if (texture == GL_NOT_TEXTURE) {
            result[0] = createTexture(bitmap);
        } else {
            result[0] = texture;
            if (bitmap != null && !bitmap.isRecycled()) {
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, result[0]);
                GLUtils.texSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, bitmap);
            }
        }
        return result[0];
    }

    /**
     * 创建Texture
     * @param bytes
     * @param width
     * @param height
     * @return
     */
    public static int createTexture(byte[] bytes, int width, int height) {
        return createTexture(bytes, width, height, GL_NOT_TEXTURE);
    }

    /**
     * 创建纹理
     * @param bytes
     * @param width
     * @param height
     * @param texture
     * @return
     */
    public static int createTexture(byte[] bytes, int width, int height, int texture) {
        if (bytes.length != width * height * 4) {
            throw new RuntimeException("Illegal byte array");
        }
        return createTexture(ByteBuffer.wrap(bytes), width, height, texture);
    }


    /**
     * 创建Texture
     * @param byteBuffer
     * @param width
     * @param height
     * @return
     */
    public static int createTexture(ByteBuffer byteBuffer, int width, int height) {
        if (byteBuffer.array().length != width * height * 4) {
            throw new RuntimeException("Illegal byte array");
        }
        final int[] texture = new int[1];
        GLES30.glGenTextures(1, texture, 0);
        if (texture[0] == 0) {
            Log.d(TAG,"Failed at glGenTextures");
            return 0;
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0]);

        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA,
                width,height, 0,
                GLES30.GL_RGBA,
                GLES30.GL_UNSIGNED_BYTE,
                byteBuffer);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
        return texture[0];
    }

    /**
     * 使用旧的Texture 创建新的Texture (宽高不能大于旧Texture的宽高，主要用于贴纸不断切换图片)
     * @param byteBuffer
     * @param width
     * @param height
     * @param texture
     * @return
     */
    public static int createTexture(ByteBuffer byteBuffer, int width, int height, int texture) {

        if (byteBuffer.array().length != width * height * 4) {
            throw new RuntimeException("Illegal byte array");
        }
        int result[] = new int[1];
        if (texture == GL_NOT_TEXTURE) {
            return createTexture(byteBuffer, width, height);
        } else {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
            GLES30.glTexSubImage2D(GLES30.GL_TEXTURE_2D,0,0,0,
                    width, height,
                    GLES30.GL_RGBA,
                    GLES30.GL_UNSIGNED_BYTE,
                    byteBuffer);
            result[0] = texture;
        }
        return result[0];
    }


    /**
     * 使用绝对路径创建纹理
     * @param filePath  mipmap图片路径
     * @return  纹理Id，失败则返回GL_NO_TEXTURE;
     */
    public static int createTexture(String filePath) {
        int[] textureHandle = new int[1];
        textureHandle[0] = GL_NOT_TEXTURE;
        if (TextUtils.isEmpty(filePath)) {
            return GL_NOT_TEXTURE;
        }
        GLES30.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        Log.d("createTextureFromAssets", "filePath:" + filePath
                + ", texture = " + textureHandle[0]);
        return textureHandle[0];
    }

    /**
     * 加载mipmap纹理
     * @param context
     * @param name
     * @return
     */
    public static int createTextureFromAssets(Context context, String name) {
        int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            Bitmap bitmap = BitmapUtils.getImageFromAssetsFile(context, name);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];
    }

    /**
     * 创建OES 类型的Texture
     * @return
     */
    public static int createOESTexture() {
        return createTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
    }

    /**
     * 删除纹理
     * @param texture
     */
    public static void deleteTexture(int texture) {
        int[] textures = new int[1];
        textures[0] = texture;
        GLES30.glDeleteTextures(1, textures, 0);
    }

    /**
     * 绑定纹理
     * @param location  句柄
     * @param texture   纹理id
     * @param index     索引
     */
    public static void bindTexture(int location, int texture, int index) {
        bindTexture(location, texture, index, GLES30.GL_TEXTURE_2D);
    }

    /**
     * 绑定纹理
     * @param location  句柄
     * @param texture   纹理值
     * @param index     绑定的位置
     * @param textureType 纹理类型
     */
    public static void bindTexture(int location, int texture, int index, int textureType) {
        // 最多支持绑定32个纹理
        if (index > 31) {
            throw new IllegalArgumentException("index must be no more than 31!");
        }
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + index);
        GLES30.glBindTexture(textureType, texture);
        GLES30.glUniform1i(location, index);
    }

    /**
     * 检查是否出错
     * @param op
     */
    public static void checkGlError(String op) {
        int error = GLES30.glGetError();
        if (error != GLES30.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * 获取出错信息
     * @param error
     * @return
     */
    public static String getErrorString(int error) {
        switch (error) {
            case EGL10.EGL_SUCCESS:
                return "EGL_SUCCESS";
            case EGL10.EGL_NOT_INITIALIZED:
                return "EGL_NOT_INITIALIZED";
            case EGL10.EGL_BAD_ACCESS:
                return "EGL_BAD_ACCESS";
            case EGL10.EGL_BAD_ALLOC:
                return "EGL_BAD_ALLOC";
            case EGL10.EGL_BAD_ATTRIBUTE:
                return "EGL_BAD_ATTRIBUTE";
            case EGL10.EGL_BAD_CONFIG:
                return "EGL_BAD_CONFIG";
            case EGL10.EGL_BAD_CONTEXT:
                return "EGL_BAD_CONTEXT";
            case EGL10.EGL_BAD_CURRENT_SURFACE:
                return "EGL_BAD_CURRENT_SURFACE";
            case EGL10.EGL_BAD_DISPLAY:
                return "EGL_BAD_DISPLAY";
            case EGL10.EGL_BAD_MATCH:
                return "EGL_BAD_MATCH";
            case EGL10.EGL_BAD_NATIVE_PIXMAP:
                return "EGL_BAD_NATIVE_PIXMAP";
            case EGL10.EGL_BAD_NATIVE_WINDOW:
                return "EGL_BAD_NATIVE_WINDOW";
            case EGL10.EGL_BAD_PARAMETER:
                return "EGL_BAD_PARAMETER";
            case EGL10.EGL_BAD_SURFACE:
                return "EGL_BAD_SURFACE";
            case EGL11.EGL_CONTEXT_LOST:
                return "EGL_CONTEXT_LOST";
            default:
                return getHex(error);
        }
    }

    private static String getHex(int value) {
        return "0x" + Integer.toHexString(value);
    }

}
