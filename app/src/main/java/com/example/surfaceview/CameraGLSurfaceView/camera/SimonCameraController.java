package com.example.surfaceview.CameraGLSurfaceView.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import com.example.surfaceview.CommonUtil.ToastUtil;
import com.example.surfaceview.MyApplication;
import java.util.Arrays;
import static com.example.surfaceview.CameraGLSurfaceView.camera.SimonCamera.CameraRadio.CAMERA_RADIO_9_16;

public class SimonCameraController {
	
	private static final String TAG = "SimonCameraController";

	private SurfaceTexture mSurfaceTexture;

	private SimonCamera mSimonCamera;

	private SimonCamera.FlashMode curFlashMode;

	private SimonCamera.Facing curFacing;

	private SimonCamera.BeautyLevel curBeautyLevel;

	private SimonCameraController(){

	}

	/**Ò
	 * 静态内部类实现单例
	 * @return
	 */
	public static synchronized SimonCameraController getInstance(){
		return CameraUtilSingletonHolder.cameraUtil;
	}

	private static class CameraUtilSingletonHolder {
		private static SimonCameraController cameraUtil = new SimonCameraController();
	}

	public void createCamera () {
		createCamera(SimonCamera.FlashMode.AUTO,CAMERA_RADIO_9_16, SimonCamera.Facing.BACK);
	}

	public void createCamera (SimonCamera.FlashMode cameraFlashMode, SimonCamera.CameraRadio cameraRadio, SimonCamera.Facing layoutID) {
		SimonCamera.Builder cameraBUilder = new SimonCamera.Builder();
		if (cameraFlashMode != null) {
			cameraBUilder.setmCameraFlash(cameraFlashMode);
			curFlashMode = cameraFlashMode;
		} else {
			cameraBUilder.setmCameraFlash(SimonCamera.FlashMode.AUTO);
			curFlashMode = SimonCamera.FlashMode.AUTO;
		}

		if (cameraRadio != null) {
			cameraBUilder.setmCameraRadio(cameraRadio);
		} else {
			cameraBUilder.setmCameraRadio(CAMERA_RADIO_9_16);
		}

		if (layoutID != null) {
			cameraBUilder.setmCameraLayoutId(layoutID);
			curFacing = layoutID;
		} else {
			cameraBUilder.setmCameraLayoutId(SimonCamera.Facing.BACK);
			curFacing = SimonCamera.Facing.BACK;
		}

		mSimonCamera = cameraBUilder.build();
	}

	/**
	 * 设置预览Surface
	 * @param
	 */
	public void setPreviewSurface(SurfaceTexture surfaceTexture) {
		mSurfaceTexture = surfaceTexture;
		mSimonCamera.setPreviewSurface(mSurfaceTexture);
	}

	/**开始相机预览
	 * @param
	 */
	public void startPreview(){
		mSimonCamera.startPreview();
	}

	/**
	 * 停止相机预览
	 */
	public void stopCamera(){
		mSimonCamera.stopPreview();
	}

	/***
	 * 设置前后相机
	 * @param
	 */
	public void switchCamera() {
		mSimonCamera.releaseCamera();
		int i = (Arrays.binarySearch(SimonCamera.Facing.values(),curFacing) + 1) % SimonCamera.Facing.values().length;
		curFacing = SimonCamera.Facing.values()[i];
		createCamera(null,null,curFacing);
		mSimonCamera.setPreviewSurface(mSurfaceTexture);
		mSimonCamera.startPreview();
		ToastUtil.show(MyApplication.getApplication(),"Flash mode:"+SimonCamera.Facing.values()[i]);
	}

	public SimonCamera.Facing getCurFacing() {
		return curFacing;
	}
	/**
	 * 设置闪光灯模式
	 * @param
	 */
	public void switchFlashMode() {
		int i = (Arrays.binarySearch(SimonCamera.FlashMode.values(),curFlashMode) +1) % SimonCamera.FlashMode.values().length;
		curFlashMode = SimonCamera.FlashMode.values()[i];
		mSimonCamera.switchFlashMode(curFlashMode);
		ToastUtil.show(MyApplication.getApplication(),"Flash mode:"+SimonCamera.FlashMode.values()[i]);
	}

	public SimonCamera.FlashMode getCurFlashMode() {
		return curFlashMode;
	}

	/**
	 * 设置美颜等级
	 */
	public void switchBeautyLevel(SimonCamera.BeautyLevel beautyLevel) {
		curBeautyLevel = beautyLevel;
		ToastUtil.show(MyApplication.getApplication(),"Flash mode:"+curBeautyLevel);
	}

	public SimonCamera.BeautyLevel getBeautyLevel () {
		return curBeautyLevel;
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
