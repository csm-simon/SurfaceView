package com.example.surfaceview.CameraGLSurfaceView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.surfaceview.CameraGLSurfaceView.camera.SimonCamera;
import com.example.surfaceview.CameraGLSurfaceView.camera.SimonCameraController;
import com.example.surfaceview.CameraGLSurfaceView.fragment.CameraBottomFragment;
import com.example.surfaceview.CameraGLSurfaceView.fragment.CameraTopFragment;
import com.example.surfaceview.CameraGLSurfaceView.view.CameraGLSurfaceView;
import com.example.surfaceview.R;

public class CameraActivity extends FragmentActivity implements CameraTopFragment.CameraTopFragmentBehaviorListener, CameraBottomFragment.CameraBottomFragmentBehaviorListener {

    private final String[] mPermissions = new String[]{Manifest.permission.CAMERA};

    @BindView(R.id.glsv_camera)
    CameraGLSurfaceView cameraGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && checkSelfPermission(mPermissions[0]) != PackageManager.PERMISSION_GRANTED) {
            showRequestPermissionDialog();
        } else {
            setContentView(R.layout.activity_camera);
            ButterKnife.bind(this);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            CameraBottomFragment cameraBottomFragment = new CameraBottomFragment();
            CameraTopFragment cameraTopFragment = new CameraTopFragment();
            ft.add(R.id.camera_top, cameraTopFragment);
            ft.add(R.id.camera_bottom, cameraBottomFragment);
            ft.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraGLSurfaceView != null) {
            cameraGLSurfaceView.onDestroy();
        }
    }

    //显示权限需求的dialog
    private void showRequestPermissionDialog() {
        new AlertDialog.Builder(this).setTitle("获取相机与写入内存权限").setMessage("没有相机与写入内存的权限，程序功能将受限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击开启权限按钮所做的操作
                        requestPermissions(mPermissions, 123);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击取消按钮所做的操作
                finish();
            }
        }).setCancelable(false).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {  //判断是不是自己设置的请求码
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断目前的系统是不是大于6.0
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {//是否仍然没有给权限
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        //用户是否选择了不再提醒
                        showDialogTipUserGoToAppSetting();
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(this, "获取权限成功！", Toast.LENGTH_SHORT).show();
                    finish();//获取权限成功 重新加载Activity
                    startActivity(new Intent(CameraActivity.this, CameraActivity.class));
                }
            }
        }
    }

    private void showDialogTipUserGoToAppSetting() {
        new AlertDialog.Builder(this).setTitle("相机权限")
                .setMessage("请在-设置-权限管理中，为绘酷打开相机和读写手机存储权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).create().show();
    }

    //在用户拒绝了授权并不再提醒后  前往手机权限设置页让用户手动开启权限
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }

    /**
     * Override
     * CameraTopFragmentBehaviorListener
     */
    @Override
    public void onClichBack() {
        finish();
    }

    @Override
    public void onClickFlash() {
        SimonCameraController.getInstance().switchFlashMode();
    }

    @Override
    public void onClickSwitch() {
        SimonCameraController.getInstance().switchCamera();
        cameraGLSurfaceView.setUpTextureByteBuffer();
    }

    /**
     * Override
     * CameraBottomFragmentBehaviorListener
     */

    @Override
    public void onClickBeauty() {

    }

    @Override
    public void onClickShutter() {

    }

    @Override
    public void onClickBeautyLevel1() {
        SimonCameraController.getInstance().switchBeautyLevel(SimonCamera.BeautyLevel.BEAUTY_LEVEL_1);
        cameraGLSurfaceView.setmBeautyLevel(SimonCameraController.getInstance().getBeautyLevel().get());
    }

    @Override
    public void onClickBeautyLevel2() {
        SimonCameraController.getInstance().switchBeautyLevel(SimonCamera.BeautyLevel.BEAUTY_LEVEL_2);
        cameraGLSurfaceView.setmBeautyLevel(SimonCameraController.getInstance().getBeautyLevel().get());
    }

    @Override
    public void onClickBeautyLevel3() {
        SimonCameraController.getInstance().switchBeautyLevel(SimonCamera.BeautyLevel.BEAUTY_LEVEL_3);
        cameraGLSurfaceView.setmBeautyLevel(SimonCameraController.getInstance().getBeautyLevel().get());
    }

    @Override
    public void onClickBeautyLevel4() {
        SimonCameraController.getInstance().switchBeautyLevel(SimonCamera.BeautyLevel.BEAUTY_LEVEL_4);
        cameraGLSurfaceView.setmBeautyLevel(SimonCameraController.getInstance().getBeautyLevel().get());
    }

    @Override
    public void onClickBeautyLevel5() {
        SimonCameraController.getInstance().switchBeautyLevel(SimonCamera.BeautyLevel.BEAUTY_LEVEL_5);
        cameraGLSurfaceView.setmBeautyLevel(SimonCameraController.getInstance().getBeautyLevel().get());
    }
}
