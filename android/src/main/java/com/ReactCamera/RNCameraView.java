package com.ReactCamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.zxing.Result;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;

public class RNCameraView extends RNCameraComponentView implements RNCameraComponentView.ResultHandler {
    private boolean mDrawLaser;
    private ViewFinderView mViewFinderView;
    private int mCameraId = -1;
    private String mPrevCameraType = "";
    private String mTorchMode = "";

    public RNCameraView(ThemedReactContext context) {
        super(context);
        setResultHandler(this);
    }

    @Override
    protected IViewFinder createViewFinderView(Context context) {

        if (mDrawLaser) {
            Log.w("camera", "drawLaser set to true");
            mViewFinderView = new ViewFinderView(context);
        }
        else {
            Log.w("camera", "drawLaser set to false");
            mViewFinderView = new CustomViewFinderView(context);
        }
        return mViewFinderView;
    }

    private static class CustomViewFinderView extends ViewFinderView {

        public CustomViewFinderView(Context context) {
            super(context);
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onDraw(Canvas canvas) {
            Log.w("camera", "drawing custom view finder view");
            if(getFramingRect() == null) {
                return;
            }

            drawViewFinderMask(canvas);
            drawViewFinderBorder(canvas);
        }
    }


    // #AARRGGBB
    public void setMaskColor(String maskColor) {
        mViewFinderView.setMaskColor(Color.parseColor(maskColor));
    }

    // #AARRGGBB
    public void setBorderColor(String borderColor) {
        mViewFinderView.setBorderColor(Color.parseColor(borderColor));
    }

    public void setBorderStrokeWidth(int borderStrokeWidth) {
        mViewFinderView.setBorderStrokeWidth(borderStrokeWidth);
    }

    public void setBorderLineLength(int borderLineLength) {
        mViewFinderView.setBorderLineLength(borderLineLength);
    }

    public void setDrawLaser(boolean drawLaser) {
        mDrawLaser = drawLaser;
    }

    // #AARRGGBB
    public void setLaserColor(String laserColor) {
        mViewFinderView.setLaserColor(Color.parseColor(laserColor));
    }

    public void setTorchMode(String torchMode) {
        mTorchMode = torchMode;
        setFlash(torchModeIsEnabled());
    }

    public boolean torchModeIsEnabled() {
        return mTorchMode.equals("on");
    }

    public int getCameraId() {
        return mCameraId;
    }

    // front, back
    public void setCameraType(String type) {
        if (mPrevCameraType.equals(type)) return;

        stopCamera();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        mCameraId = -1;
        for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (type.equals("back") && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCameraId = cameraId;
                break;
            }
            if (type.equals("front") && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCameraId = cameraId;
                break;
            }
        }
        startCamera(mCameraId);
        if (type.equals("back")) {
            setFlash(torchModeIsEnabled());
        }
        mPrevCameraType = type;
    }

    @Override
    public void handleResult(Result result) {

        // Received Barcode Result!
        WritableMap event = Arguments.createMap();
        event.putString("data", result.getText());
        event.putString("type", result.getBarcodeFormat().toString());

        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topChange",
                event);

        startCamera(mCameraId);
        setFlash(torchModeIsEnabled());
    }



}
