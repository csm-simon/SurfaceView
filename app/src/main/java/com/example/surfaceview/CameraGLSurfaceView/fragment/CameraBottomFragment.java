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
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.surfaceview.R;

public class CameraBottomFragment extends Fragment {

    private Context mContext;

    private View mContentView;

    private CameraBottomFragmentBehaviorListener mCameraBottomFragmentBehaviorListener;

    @BindView(R.id.iv_beauty)
    ImageView ivBeauty;

    @BindView(R.id.iv_shutter)
    ImageView ivTakePicture;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mCameraBottomFragmentBehaviorListener = (CameraBottomFragmentBehaviorListener) mContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_camera_bottom,container,false);
        ButterKnife.bind(this,mContentView);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.iv_beauty)
    public void onClickBeauty(){
        mCameraBottomFragmentBehaviorListener.onClickBeauty();
    }

    @OnClick(R.id.iv_shutter)
    public void onClickShutter(){
        mCameraBottomFragmentBehaviorListener.onClickShutter();
    }

    @OnClick(R.id.iv_beauty_level_1)
    public void onClickBeautyLevel1(){
        mCameraBottomFragmentBehaviorListener.onClickBeautyLevel1();
    }

    @OnClick(R.id.iv_beauty_level_2)
    public void onClickBeautyLevel2(){
        mCameraBottomFragmentBehaviorListener.onClickBeautyLevel2();
    }

    @OnClick(R.id.iv_beauty_level_3)
    public void onClickBeautyLevel3(){
        mCameraBottomFragmentBehaviorListener.onClickBeautyLevel3();
    }

    @OnClick(R.id.iv_beauty_level_4)
    public void onClickBeautyLevel4(){
        mCameraBottomFragmentBehaviorListener.onClickBeautyLevel4();
    }

    @OnClick(R.id.iv_beauty_level_5)
    public void onClickBeautyLevel5(){
        mCameraBottomFragmentBehaviorListener.onClickBeautyLevel5();
    }

    public interface CameraBottomFragmentBehaviorListener {
        void onClickBeauty();
        void onClickShutter();
        void onClickBeautyLevel1();
        void onClickBeautyLevel2();
        void onClickBeautyLevel3();
        void onClickBeautyLevel4();
        void onClickBeautyLevel5();
    }

}
