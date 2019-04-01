package com.example.surfaceview.CameraGLSurfaceView.camera;

import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import com.example.surfaceview.CommonUtil.DeviceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SimonCamera {

    private Camera mCamera;

    private SimonCamera() {
    }

    enum CameraRadio {
        CAMERA_RADIO_1_1,
        CAMERA_RADIO_3_4,
        CAMERA_RADIO_9_16
    }

    /**
     * 设置预览Surface
     *
     * @param texture
     */
    public void setPreviewSurface(SurfaceTexture texture) {
        if (mCamera == null) {
            throw new IllegalStateException("Camera must be set when start preview");
        }
        try {
            mCamera.setPreviewTexture(texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始预览
     *
     * @param
     */
    public void startPreview() {
        if (mCamera == null) {
            throw new IllegalStateException("Camera must be set when start preview");
        }
        mCamera.startPreview();
    }

    /**
     * 结束预览
     */
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    /**
     * 释放相机
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.addCallbackBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 设置闪光灯模式
     *
     * @param flashMode
     */
    public void switchFlashMode(FlashMode flashMode) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(flashMode.value);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    /**
     * 对焦
     *
     * @return
     */
    public void onFocus(float x, float y, Camera.AutoFocusCallback cb) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumFocusAreas() <= 0) {
                mCamera.autoFocus(cb);
                return;
            }
            mCamera.cancelAutoFocus();
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            List<Camera.Area> areasMetrix = new ArrayList<Camera.Area>();
            Rect focusRect = calculateTapArea(x, y, 1.0f);
            Rect metrixRect = calculateTapArea(x, y, 1.5f);
            areas.add(new Camera.Area(focusRect, 1000));
            areasMetrix.add(new Camera.Area(metrixRect, 1000));
            parameters.setMeteringAreas(areasMetrix);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            parameters.setFocusAreas(areas);
            try {
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            mCamera.autoFocus(cb);
        }
    }

    public static class Builder {

        private CameraRadio mCameraRadio;

        private FlashMode mCameraFlash;

        private Facing mCameraLayoutId;

        private PreviewSize mCameraPreViewSize;

        private PictureSize mCameraPictureSize;

        public Builder setmCameraRadio(CameraRadio mCameraRadio) {
            this.mCameraRadio = mCameraRadio;
            return this;
        }

        public void setmCameraFlash(FlashMode mCameraFlash) {
            this.mCameraFlash = mCameraFlash;
        }

        public Builder setmCameraLayoutId(Facing mCameraLayoutId) {
            this.mCameraLayoutId = mCameraLayoutId;
            return this;
        }

        public SimonCamera build() {
            SimonCamera camera = new SimonCamera();
            if (camera.mCamera == null) {
                if (mCameraLayoutId != null) {
                    camera.mCamera = Camera.open(mCameraLayoutId.value);
                } else {
                    camera.mCamera = Camera.open();
                }

                Camera.Parameters mParameters = camera.mCamera.getParameters();
                setUpCameraFlash(mParameters);
                setUpCameraRadio(mParameters);
                setUpPreviewSize(mParameters);
                setUpPicturzeSize(mParameters);
                camera.mCamera.setParameters(mParameters);
            }
            return camera;
        }

        private void setUpPicturzeSize(Camera.Parameters parameters) {
            if (mCameraPictureSize != null) {
                parameters.setPictureSize(mCameraPictureSize.width, mCameraPictureSize.height);
            }
        }

        private void setUpPreviewSize(Camera.Parameters parameters) {
            if (mCameraPreViewSize != null) {
                parameters.setPreviewSize(mCameraPreViewSize.width, mCameraPreViewSize.height);
            }
        }

        private void setUpCameraRadio(Camera.Parameters mParameters) {
            if (mCameraRadio != null) {
                switch (mCameraRadio) {
                    case CAMERA_RADIO_1_1:
                        mCameraPreViewSize = CameraSizeSelector.getCameraPreviewSizes(getPreviewSize(mParameters),
                                1.f, CameraSizeSelector.HEIGHT);
                        mCameraPictureSize = CameraSizeSelector.getCameraPictureSize(getPictureSize(mParameters),
                                getPreviewSize(mParameters), 1.f, 1.f, CameraSizeSelector.HEIGHT);
                        break;
                    case CAMERA_RADIO_3_4:
                        mCameraPreViewSize = CameraSizeSelector.getCameraPreviewSizes(getPreviewSize(mParameters),
                                4.f / 3.f, CameraSizeSelector.HEIGHT);
                        mCameraPictureSize = CameraSizeSelector.getCameraPictureSize(getPictureSize(mParameters),
                                getPreviewSize(mParameters), 4.f / 3.f, 4.f / 3.f, CameraSizeSelector.HEIGHT);

                        break;
                    case CAMERA_RADIO_9_16:
                        mCameraPreViewSize = CameraSizeSelector.getCameraPreviewSizes(getPreviewSize(mParameters),
                                16.f / 9.f, CameraSizeSelector.HEIGHT);
                        mCameraPictureSize = CameraSizeSelector.getCameraPictureSize(getPictureSize(mParameters),
                                getPreviewSize(mParameters), 16.f / 9.f, 16.f / 9.f, CameraSizeSelector.HEIGHT);

                        break;
                }
            }
        }

        private void setUpCameraFlash(Camera.Parameters parameters) {
            if (mCameraFlash != null) {
                parameters.setFlashMode(mCameraFlash.value);
            }
        }

        private List<PreviewSize> getPreviewSize(Camera.Parameters mParameters) {
            if (mParameters != null) {
                List<PreviewSize> previewSizeList = new LinkedList<>();
                List<Camera.Size> originalList = mParameters.getSupportedPreviewSizes();
                for (Camera.Size size : originalList) {
                    previewSizeList.add(new PreviewSize(size.width, size.height));
                }
                return previewSizeList;
            }
            return null;
        }

        private List<PictureSize> getPictureSize(Camera.Parameters mParameters) {
            if (mParameters != null) {
                List<PictureSize> pictureSizeList = new LinkedList<>();
                List<Camera.Size> originalList = mParameters.getSupportedPictureSizes();
                for (Camera.Size size : originalList) {
                    pictureSizeList.add(new PictureSize(size.width, size.height));
                }
                return pictureSizeList;
            }
            return null;
        }

    }

    /**
     * 相机图像尺寸类。
     */
    public static class Size {

        public final int width;

        public final int height;

        public Size(int w, int h) {
            this.width = w;
            this.height = h;
        }
    }

    /**
     * 预览尺寸。
     */
    public static class PreviewSize extends Size {

        /**
         * 640 x 480 是我们认为大部分手机都支持的尺寸，如果你找不到任何你要的尺寸是，可以考虑使用该尺寸。
         */
        public static final PreviewSize SILVER_BULLET_SIZE = new PreviewSize(640, 480);

        public PreviewSize(int w, int h) {
            super(w, h);
        }
    }

    /**
     * 照片尺寸。
     */
    public static class PictureSize extends Size {

        /**
         * 640 x 480 是我们认为大部分手机都支持的尺寸，如果你找不到任何你要的尺寸是，可以考虑使用该尺寸。
         */
        public static final PictureSize SILVER_BULLET_SIZE = new PictureSize(640, 480);

        public PictureSize(int w, int h) {
            super(w, h);
        }
    }

    /**
     * 摄像头类型。
     */
    public enum Facing {
        /**
         * 前置摄像头。
         */
        FRONT(Camera.CameraInfo.CAMERA_FACING_FRONT),
        /**
         * 后置摄像头。
         */
        BACK(Camera.CameraInfo.CAMERA_FACING_BACK);

        Facing(int value) {
            this.value = value;
        }

        public int get() {
            return value;
        }

        private final int value;
    }

    /**
     * 闪光灯模式。
     */
    public enum FlashMode {

        OFF(Camera.Parameters.FLASH_MODE_OFF), AUTO(Camera.Parameters.FLASH_MODE_AUTO), ON(Camera.Parameters.FLASH_MODE_ON), TORCH(Camera.Parameters.FLASH_MODE_TORCH);

        FlashMode(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }

        private final String value;
    }

    public enum BeautyLevel {
        BEAUTY_LEVEL_1(10.f),
        BEAUTY_LEVEL_2(5.f),
        BEAUTY_LEVEL_3(2.0f),
        BEAUTY_LEVEL_4(1.0f),
        BEAUTY_LEVEL_5(0.1f),
        ;

        BeautyLevel(float v) {
            this.value = v;
        }

        public float get() {
            return value;
        }

        private final float value;
    }

    /**
     * 对焦模式
     */
    public enum FocusMode {

        AUTO(Camera.Parameters.FOCUS_MODE_AUTO), CONTINUOUS_PICTURE(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE), CONTINUOUS_VIDEO(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO), FIXED(Camera.Parameters.FOCUS_MODE_FIXED), INFINITY(Camera.Parameters.FOCUS_MODE_INFINITY), MACRO(Camera.Parameters.FOCUS_MODE_MACRO), EDOF(Camera.Parameters.FOCUS_MODE_EDOF);

        FocusMode(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }

        private final String value;
    }

    /***
     *   计算点击区域
     * @param x
     * @param y
     * @param coefficient
     * @return
     */
    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerY = 0;
        int centerX = 0;
        //在Camera.Area对象中的Rect字段，代表了一个被映射成2000x2000单元格的矩形
        // 坐标（-1000，-1000）代表Camera图像的左上角，（1000,1000）代表Camera图像的右下角
        //以此根据点击的坐标进行换算
        centerY = (int) (x / DeviceUtil.getScreenWidth() * 2000 - 1000);
        centerX = (int) (y / DeviceUtil.getScreenWidth() * 2000 - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        //Math.round()四舍五入
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    /**
     * 防止坐标值超出边界的方法
     *
     * @param x
     * @param min
     * @param max
     * @return
     */
    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

}
