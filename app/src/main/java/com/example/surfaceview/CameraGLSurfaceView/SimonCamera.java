package com.example.surfaceview.CameraGLSurfaceView;

import android.hardware.Camera;


public class SimonCamera {

    private Camera mCamera;

    private static SimonCamera mSingleInstance;

    private SimonCamera () {}

    public static SimonCamera getInstance() {
        if (mSingleInstance == null) {
            synchronized (SimonCamera.class) {
                if (mSingleInstance == null) {
                    mSingleInstance = new SimonCamera();
                }
            }
        }
        return mSingleInstance;
    }

    enum CameraRadio {
        CAMERA_RADIO_1_1,
        CAMERA_RADIO_3_4,
        CAMERA_RADIO_9_16
    }

    enum CameraFlash {
        CAMERA_FLASH_AUTO,
        CAMERA_FLASH_LIGHT,
        CAMERA_FLASH_CLOSE
    }

    enum CameraLayoutID{
        CAMERA_LAYOUT_ID_BACK,
        CAMERA_LAYOUT_ID_FRONT
    }

    public static class Builder{

        CameraRadio mCameraRadio;

        CameraFlash mCameraFlash;

        CameraLayoutID mCameraLayoutId;

        Size mCameraPreViewSize;

        Size mCameraPictureSize;

        public Builder setmCameraRadio(CameraRadio mCameraRadio) {
            this.mCameraRadio = mCameraRadio;
            return this;
        }

        public void setmCameraFlash(CameraFlash mCameraFlash) {
            this.mCameraFlash = mCameraFlash;
        }

        public Builder setmCameraLayoutId(CameraLayoutID mCameraLayoutId) {
            this.mCameraLayoutId = mCameraLayoutId;
            return this;
        }

        public Builder setmCameraPreViewSize(Size mCameraPreViewSize) {
            this.mCameraPreViewSize = mCameraPreViewSize;
            return this;
        }

        public Builder setmCameraPictureSize(Size mCameraPictureSize) {
            this.mCameraPictureSize = mCameraPictureSize;
            return this;
        }

        public SimonCamera build () {
            SimonCamera camera = SimonCamera.getInstance();
            if (camera.mCamera == null) {
                if (mCameraLayoutId != null) {
                    switch (mCameraLayoutId) {
                        case CAMERA_LAYOUT_ID_BACK:
                            camera.mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                            break;
                        case CAMERA_LAYOUT_ID_FRONT:
                            camera.mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                            break;
                    }
                } else {
                    camera.mCamera = Camera.open();
                }

                Camera.Parameters mParameters = camera.mCamera.getParameters();
                setUpCameraFlash(mParameters);
                setUpCameraRadio(mParameters);
                setUPPreviewSize(mParameters);
                setUPPicturzeSize(mParameters);
                camera.mCamera.setParameters(mParameters);
            }
            return camera;
        }

        private void setUPPicturzeSize(Camera.Parameters parameters) {
            if (mCameraPictureSize != null) {
                parameters.setPictureSize(mCameraPictureSize.width,mCameraPictureSize.height);
            }
        }

        private void setUPPreviewSize(Camera.Parameters parameters) {
            if (mCameraPreViewSize != null) {
                parameters.setPreviewSize(mCameraPreViewSize.width,mCameraPreViewSize.height);
            }
        }

        private void setUpCameraRadio(Camera.Parameters mParameters) {
            if (mCameraRadio != null) {
                switch (mCameraRadio) {
                    case CAMERA_RADIO_1_1:
                        break;
                    case CAMERA_RADIO_3_4:
                        break;
                    case CAMERA_RADIO_9_16:
                        break;
                }
            }
        }

        private void setUpCameraFlash(Camera.Parameters parameters) {
            if (mCameraFlash != null) {
                switch (mCameraFlash) {
                    case CAMERA_FLASH_AUTO:
                        parameters.setFlashMode("FLASH_MODE_AUTO");
                        break;
                    case CAMERA_FLASH_CLOSE:
                        parameters.setFlashMode("FLASH_MODE_OFF");
                        break;
                    case CAMERA_FLASH_LIGHT:
                        parameters.setFlashMode("FLASH_MODE_ON");
                        break;
                }
            }
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
            super(w,h);
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
            super(w,h);
        }
    }

}
