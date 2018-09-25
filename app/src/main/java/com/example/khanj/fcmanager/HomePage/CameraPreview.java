package com.example.khanj.fcmanager.HomePage;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by khanj on 2018-09-26.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera mCamera;
    public List<Camera.Size> prSupportedPreviewSizes;
    private Camera.Size prPreviewSize;

    public CameraPreview(Context context) {
        super(context);
        Log.v("jyp@@@","CameraPreview(Context context) 생성자호출");
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.v("jyp@@@", "CameraPreview(Context context, AttributeSet attrs) 생성자호출");
        mCamera = Camera.open();
        prSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
    }

    public boolean takePhoto(Camera.PictureCallback handler){
        if(mCamera != null){
            mCamera.takePicture(null,null,handler);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("jyp@@@","surfaceCreated ");
        // Surface가 생성되었으니 프리뷰를 어디에 띄울지 지정해준다. (holder 로 받은 SurfaceHolder에 뿌려준다.
        try {

            Camera.Parameters parameters = mCamera.getParameters();
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }
            mCamera.setParameters(parameters);

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("jyp@@@","surfaceDestroyed ");
        // 프리뷰 제거시 카메라 사용도 끝났다고 간주하여 리소스를 전부 반환한다
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    private Camera.Size getBestPreviewSize(int width, int height)
    {
        Camera.Size result=null;
        Camera.Parameters p = mCamera.getParameters();
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                } else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.v("jyp@@@","surfaceChanged ");
        // 프리뷰를 회전시키거나 변경시 처리를 여기서 해준다.
        // 프리뷰 변경시에는 먼저 프리뷰를 멈춘다음 변경해야한다.
        if (holder.getSurface() == null){
            // 프리뷰가 존재하지 않을때
            return;
        }

        // 우선 멈춘다
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // 프리뷰가 존재조차 하지 않는 경우다
        }

        // 프리뷰 변경, 처리 등을 여기서 해준다.
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
//
//        Camera.Size size = getBestPreviewSize(w, h);
        Log.v("jyp@@@","surfaceChanged  width: "+prPreviewSize.width);
        Log.v("jyp@@@","surfaceChanged  height: "+prPreviewSize.height);

        parameters.setPreviewSize(prPreviewSize.width, prPreviewSize.height);
        mCamera.setParameters(parameters);
        // 새로 변경된 설정으로 프리뷰를 재생성한다
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v("jyp@@@","onMeasure()");

        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        Log.v("jyp@@@","width: "+width);
        Log.v("jyp@@@","height: "+height);
        setMeasuredDimension(width, height);

        if (prSupportedPreviewSizes != null) {
            prPreviewSize = getOptimalPreviewSize(prSupportedPreviewSizes, width, height);
            Log.v("jyp@@@","prPreviewSize.width: "+prPreviewSize.width);
            Log.v("jyp@@@","prPreviewSize.height: "+prPreviewSize.height);
        }
    }

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

}
