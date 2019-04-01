package com.example.surfaceview.CameraGLSurfaceView.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.surfaceview.CameraGLSurfaceView.camera.SimonCameraController;
import com.example.surfaceview.R;

public class CameraTopFragment extends Fragment {

    private Context mContext;

    private View mContentView;

    private CameraTopFragmentBehaviorListener mCameraTopFragmentBehaviorListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_camera_top,container,false);
        ButterKnife.bind(this,mContentView);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCameraTopFragmentBehaviorListener = (CameraTopFragmentBehaviorListener) mContext;
    }


    @OnClick(R.id.iv_back)
    public void clickBack() {
        mCameraTopFragmentBehaviorListener.onClichBack();
    }

    @OnClick(R.id.iv_flash)
    public void switchFlash() {
        mCameraTopFragmentBehaviorListener.onClickFlash();
    }

    @OnClick(R.id.iv_switch_camera)
    public void switchCamera() {
        mCameraTopFragmentBehaviorListener.onClickSwitch();
    }

    public interface CameraTopFragmentBehaviorListener {
        void onClichBack();
        void onClickFlash();
        void onClickSwitch();
    }

}
