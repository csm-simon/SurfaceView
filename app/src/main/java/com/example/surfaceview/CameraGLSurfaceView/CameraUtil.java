package com.example.surfaceview.CameraGLSurfaceView;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CameraUtil implements Camera.AutoFocusCallback {
	
	private static final String TAG = "CameraUtil";

	private Camera mCamera;
	
	private Camera.Parameters mParameters;
	
	private boolean isPreviewing = false;

	private int mDisplayOrientation;

	private int cameraPosition=0;//0代表后置摄像头 1代表前置摄像头

	private String flashMode=Camera.Parameters.FLASH_MODE_AUTO;//闪光模式  初始为自动模式

	private List<Camera.Size> previewSizeList;//保存相机支持的所有预览比例的列表

	private List<Camera.Size> pictureSizeList;//保存相机支持的所有预览比例的列表

	int bestPreviewSizePosition=0;//保存选择出来的预览所能支持的最佳比例在预览分辨率支持列表中的位置

	int bestPictureSizePosition=0;//保存选择出来的保存图片所能支持的最佳比例在图片分辨率支持列表中的位置

	private final int mScreenWidth=0;  //屏幕的宽度

	private final int mScreenHeight=0;  //屏幕的高度

	private SurfaceTexture surfaceTexture;

	private CameraUtil(){

	}

	/**
	 * DCL(Double Check Lock 实现单例)
	 * @return
	 */
	public static synchronized CameraUtil getInstance(){
		return CameraUtilSingletonHolder.cameraUtil;
	}

	private static class CameraUtilSingletonHolder {
		private static CameraUtil cameraUtil = new CameraUtil();
	}

	/**打开相机
	 * @param
	 */
	public void openCamera(){
		Log.i(TAG, "Camera open....");
		if(mCamera == null){
			mCamera = Camera.open();
			mCamera.setDisplayOrientation(mDisplayOrientation);
			Log.i(TAG, "Camera open done....");
		}else{
			Log.i(TAG, "Camera open failed!!!");
			stopCamera();
		}
	}

	/**打开指定相机
	 * @param
	 */
	public void openCamera(int cameraPosition){
		Log.i(TAG, "Camera open....");
		if(mCamera == null){
			mCamera = Camera.open(cameraPosition);
			mCamera.setDisplayOrientation(mDisplayOrientation);
			Log.i(TAG, "Camera open over....");
		}else{
			Log.i(TAG, "Camera open failed!!!");
			stopCamera();
		}
	}

	/**开始相机预览
	 * @param surface
	 */
	public void startPreview(SurfaceTexture surface){
		Log.i(TAG, "doStartPreview...");
		if(isPreviewing){
			mCamera.stopPreview();
			return;
		}
		if(mCamera != null){
			try {
				surfaceTexture = surface;
				mCamera.setPreviewTexture(surface);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setParameters();
			mCamera.startPreview();
			isPreviewing=true;
		}
	}

	/**
	 * 停止相机预览
	 */
	public void stopCamera(){
		if(null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview(); 
			isPreviewing = false; 

			mCamera.release();
			mCamera = null;     
		}
	}
	/**
	 * 拍照
	 */
	public void takePicture(ShutterCallback shutterCallback,PictureCallback mRawCallback,PictureCallback mJpegPictureCallback){
		if(isPreviewing && (mCamera != null)){
			mCamera.takePicture(shutterCallback, mRawCallback, mJpegPictureCallback);
		}
	}

	/**
	 * 对焦
	 * @return
	 */
	public void onFocus(float x,float y) {
		if(mCamera!=null){
			Camera.Parameters parameters=mCamera.getParameters();
			if (parameters.getMaxNumFocusAreas()<=0) {
				mCamera.autoFocus(this);
				return;
			}
			mCamera.cancelAutoFocus();
			List<Camera.Area> areas=new ArrayList<Camera.Area>();
			List<Camera.Area> areasMetrix=new ArrayList<Camera.Area>();
			Rect focusRect = calculateTapArea(x, y, 1.0f);
			Rect metrixRect = calculateTapArea(x, y, 1.5f);
			areas.add(new Camera.Area(focusRect, 1000));
			areasMetrix.add(new Camera.Area(metrixRect,1000));
			parameters.setMeteringAreas(areasMetrix);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			parameters.setFocusAreas(areas);
			try {
				mCamera.setParameters(parameters);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			mCamera.autoFocus(this);
		}
	}
	/***
	 * 设置前后相机
	 * @param cameraPosition
	 */
	public void setCamera(int cameraPosition) {
		stopCamera();
		openCamera(cameraPosition);
		startPreview(surfaceTexture);
	}

	/**
	 * 设置闪光灯模式
	 * @param flashMode
	 */
	public void setFlashMode(String flashMode) {
		mParameters.setFlashMode(flashMode);
		mCamera.setParameters(mParameters);
		mCamera.startPreview();
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {

	}

	/***
	 *   计算点击区域
	 * @param x
	 * @param y
	 * @param coefficient
	 * @return
	 */
	private  Rect calculateTapArea(float x, float y, float coefficient) {
		float focusAreaSize = 300;
		Log.d(TAG, "hello"+x+" "+y);
		int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
		int centerY =0;
		int  centerX=0;
		//在Camera.Area对象中的Rect字段，代表了一个被映射成2000x2000单元格的矩形
		// 坐标（-1000，-1000）代表Camera图像的左上角，（1000,1000）代表Camera图像的右下角
		//以此根据点击的坐标进行换算
		centerY = (int) (x / mScreenWidth*2000 - 1000);
		centerX= (int) (y / mScreenWidth*2000 - 1000);
		int left = clamp(centerX - areaSize / 2, -1000, 1000);
		int top = clamp(centerY - areaSize / 2, -1000, 1000);
		Log.e(TAG, "calculateTapArea: "+mScreenWidth );

		RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
		//Math.round()四舍五入
		return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
	}

	/**
	 * 防止坐标值超出边界的方法
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

	public boolean isPreviewing(){
		return isPreviewing;
	}

	/***
	 * 设置Camera.Parameter
	 */
	public void setParameters(){
		previewSizeList=new LinkedList<>();
		pictureSizeList=new LinkedList<>();
		if (mParameters!=null){
			mParameters=null;
		}
		mParameters=mCamera.getParameters();
		Log.e(TAG, "setParameters: "+cameraPosition);
		if(cameraPosition!=1){
			mParameters.setFlashMode(flashMode);//设置初始的闪光模式
		}
		previewSizeList=mParameters.getSupportedPreviewSizes();//拿到预览支持的宽高列表  宽高为横屏模式
		pictureSizeList=mParameters.getSupportedPictureSizes();//拿到保存图片支持的宽高列表

		bestPreviewSizePosition=getBestPreviewSize(previewSizeList);//获取最佳比例所在列表中的位置
		bestPictureSizePosition=getBestPictureSize(pictureSizeList);//获取最佳比例所在列表中的位置

		Log.e(TAG, "cameraPosition:"+cameraPosition+"  previewSize"+previewSizeList.get(bestPreviewSizePosition).width+"    "+previewSizeList.get(bestPreviewSizePosition).height);
		mParameters.setPreviewFormat(ImageFormat.NV21);
		//设置预览图像的分辨率
		mParameters.setPreviewSize(previewSizeList.get(bestPreviewSizePosition).width,previewSizeList.get(bestPreviewSizePosition).height);
		/*mParameters.setPreviewSize(previewSizeList.get(0).width,previewSizeList.get(0).height);*/
		//设置生成图片的分辨率
		mParameters.setPictureSize(pictureSizeList.get(bestPictureSizePosition).width,pictureSizeList.get(bestPictureSizePosition).height);
		mCamera.setParameters(mParameters);

		mParameters = mCamera.getParameters();
		/*mParameters.setPreviewFpsRange(15*1000, 30*1000);*/
		mCamera.setParameters(mParameters);
	}
	/*
    * previewSizeList 手机所支持预览的大小
    * ratio   用户选择需要显示的比例
    * 返回最佳尺寸所在列表中的位置
    * */
	public int getBestPreviewSize(List<Camera.Size> previewSizeList){
		int result=0;
		double supportRatio;
		if (previewSizeList.get(0).width > previewSizeList.get(previewSizeList.size() - 1).width) {     //如果是从大到小排序
			for (int i = 0; i < previewSizeList.size(); i++) {
				supportRatio = (float) previewSizeList.get(i).width / previewSizeList.get(i).height;
				Log.d(TAG, "getBestPreviewSize: FOUR_TO_THREE:" + supportRatio);
				double m = 4f / 3f;
				if (supportRatio == m) {
					result = i;
					break;
				}
			}
		}else{
			for (int i = previewSizeList.size()-1; i >-1; i--) {
				supportRatio = (float) previewSizeList.get(i).width / previewSizeList.get(i).height;
				Log.d(TAG, "getBestPreviewSize: FOUR_TO_THREE:" + supportRatio);
				double m = 4f / 3f;
				if (supportRatio == m) {
					result = i;
					break;
				}
			}
		}
      /*  mPreviewWidth=previewSizeList.get(bestPreviewSizePosition).width;
        mPreviewHeight=previewSizeList.get(bestPreviewSizePosition).height;*/
		return result;
	}
	//返回预览宽度像素值
	public int getPreviewWidth(){
		return previewSizeList.get(bestPreviewSizePosition).width;
	}
	//返回预览高度像素值
	public int getPreviewHeight(){
		return previewSizeList.get(bestPreviewSizePosition).height;
	}
	/***
	 *
	 * @param pictureSizeList
	 * @param
	 * @return
	 */
	public int getBestPictureSize(List<Camera.Size>pictureSizeList){
		int result=pictureSizeList.size()/2; //初始取中间值
		float supportRatio;
		if (pictureSizeList.get(0).width > pictureSizeList.get(pictureSizeList.size() - 1).width) {     //如果是从大到小排序
			for (int i = 0; i <pictureSizeList.size() ; i++) {
				supportRatio=(float)pictureSizeList.get(i).width/pictureSizeList.get(i).height;
				float m=4f/3;
				if(supportRatio==m){
					return i;
				}
			}
		}else{
			for (int i = pictureSizeList.size()-1; i >-1 ; i--) {
				supportRatio=(float)pictureSizeList.get(i).width/pictureSizeList.get(i).height;
				float m=4f/3;
				if(supportRatio==m){
					return i;
				}
			}
		}
		return result;
	}

	public void setDisplayOrientation(int mDisplayOrientation) {
		this.mDisplayOrientation=mDisplayOrientation;
	}
}
