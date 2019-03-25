package com.example.surfaceview.CameraGLSurfaceView;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import static com.example.surfaceview.CameraGLSurfaceView.SimonCamera.CameraFlash.CAMERA_FLASH_AUTO;
import static com.example.surfaceview.CameraGLSurfaceView.SimonCamera.CameraFlash.CAMERA_FLASH_LIGHT;
import static com.example.surfaceview.CameraGLSurfaceView.SimonCamera.CameraLayoutID.CAMERA_LAYOUT_ID_BACK;
import static com.example.surfaceview.CameraGLSurfaceView.SimonCamera.CameraLayoutID.CAMERA_LAYOUT_ID_FRONT;
import static com.example.surfaceview.CameraGLSurfaceView.SimonCamera.CameraRadio.CAMERA_RADIO_1_1;
import static com.example.surfaceview.CameraGLSurfaceView.SimonCamera.CameraRadio.CAMERA_RADIO_9_16;

public class SimonCameraController {
	
	private static final String TAG = "SimonCameraController";

	private SimonCamera mSimonCamera;

	private SurfaceTexture mSurfaceTexture;

	private SimonCameraController(){

	}

	/**Ò
	 * DCL(Double Check Lock 实现单例)
	 * @return
	 */
	public static synchronized SimonCameraController getInstance(){
		return CameraUtilSingletonHolder.cameraUtil;
	}

	private static class CameraUtilSingletonHolder {
		private static SimonCameraController cameraUtil = new SimonCameraController();
	}

	public void createCamera () {
		createCamera(CAMERA_FLASH_AUTO,CAMERA_RADIO_9_16,CAMERA_LAYOUT_ID_BACK);
	}

	public void createCamera (SimonCamera.CameraFlash cameraFlashMode, SimonCamera.CameraRadio cameraRadio, SimonCamera.CameraLayoutID layoutID) {
		SimonCamera.Builder cameraBUilder = new SimonCamera.Builder();
		if (cameraFlashMode != null) {
			cameraBUilder.setmCameraFlash(cameraFlashMode);
		} else {
			cameraBUilder.setmCameraFlash(CAMERA_FLASH_AUTO);
		}

		if (cameraRadio != null) {
			cameraBUilder.setmCameraRadio(cameraRadio);
		} else {
			cameraBUilder.setmCameraRadio(CAMERA_RADIO_9_16);
		}

		if (layoutID != null) {
			cameraBUilder.setmCameraLayoutId(layoutID);
		} else {
			cameraBUilder.setmCameraLayoutId(CAMERA_LAYOUT_ID_BACK);
		}

		mSimonCamera = cameraBUilder.build();
	}

	/**开始相机预览
	 * @param
	 */
	public void startPreview(SurfaceTexture surfaceTexture){
		mSurfaceTexture = surfaceTexture;
		mSimonCamera.startPreview(mSurfaceTexture);
	}

	/**
	 * 停止相机预览
	 */
	public void stopCamera(){
		mSimonCamera.stopPreview();
	}

	/***
	 * 设置前后相机
	 * @param cameraPosition
	 */
	public void switchCamera(SimonCamera.CameraLayoutID cameraPosition) {
		mSimonCamera.stopPreview();
		mSimonCamera = null;
		createCamera(null,null,cameraPosition);
		startPreview(mSurfaceTexture);
	}

	/**
	 * 设置闪光灯模式
	 * @param flashMode
	 */
	public void switchFlashMode(SimonCamera.FlashMode flashMode) {
		mSimonCamera.switchFlashMode(flashMode);
	}

	/**
	 * 对焦
	 * @return
	 */
	public void onFocus(float x,float y) {
		mSimonCamera.onFocus(x, y, new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {

			}
		});
	}

}
