package com.ReactCamera;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

import java.util.Map;

import javax.annotation.Nullable;

public class RNCameraViewManager extends ViewGroupManager<RNCameraView> implements LifecycleEventListener {

    private static final String REACT_CLASS = "RNCameraView";

    public static final int TAKE_PICTURE = 1;
    private static final boolean DEFAULT_VIEWFINDER_DISPLAY = false;
    private static final String DEFAULT_VIEWFINDER_BACKGROUND_COLOR = "#60000000";
    private static final String DEFAULT_VIEWFINDER_BORDER_COLOR = "#ffffffff";
    private static final int DEFAULT_VIEWFINDER_BORDER_WIDTH = 4;
    private static final int DEFAULT_VIEWFINDER_BORDER_LENGTH = 60;
    private static final boolean DEFAULT_VIEWFINDER_DRAW_LASER = false;
    private static final String DEFAULT_VIEWFINDER_LASER_COLOR = "#ffcc0000";
    private static final String DEFAULT_TORCH_MODE = "off";
    private static final String DEFAULT_CAMERA_TYPE = "back";

    private RNCameraView mCameraView;
    private boolean mCameraViewVisible;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactProp(name = "viewFinderDisplay")
    public void setViewFinderDisplay(RNCameraView view, @Nullable Boolean viewFinderDisplay) {
        if(viewFinderDisplay !=  null) {
            view.setViewFinderDisplay(viewFinderDisplay);
        }
    }

    @ReactProp(name = "viewFinderBackgroundColor")
    public void setViewFinderBackgroundColor(RNCameraView view, @Nullable String viewFinderBackgroundColor) {
        if (viewFinderBackgroundColor != null) {
            view.setMaskColor(viewFinderBackgroundColor);
        }
    }

    @ReactProp(name = "viewFinderBorderColor")
    public void setViewFinderBorderColor(RNCameraView view, @Nullable String viewFinderBorderColor) {
        if (viewFinderBorderColor != null) {
            view.setBorderColor(viewFinderBorderColor);
        }
    }

    @ReactProp(name = "viewFinderBorderWidth")
    public void setViewFinderBorderWidth(RNCameraView view, @Nullable Integer viewFinderBorderWidth) {
        if (viewFinderBorderWidth != null) {
            view.setBorderStrokeWidth(viewFinderBorderWidth);
        }
    }

    @ReactProp(name = "viewFinderBorderLength")
    public void setViewFinderBorderLength(RNCameraView view, @Nullable Integer viewFinderBorderLength) {
        if (viewFinderBorderLength != null) {
            view.setBorderLineLength(viewFinderBorderLength);
        }
    }

    @ReactProp(name = "viewFinderDrawLaser")
    public void setViewFinderDrawLaser(RNCameraView view, @Nullable Boolean viewFinderDrawLaser) {
        if (viewFinderDrawLaser != null) {
            view.setDrawLaser(viewFinderDrawLaser);
        }
    }

    @ReactProp(name = "viewFinderLaserColor")
    public void setViewFinderLaserColor(RNCameraView view, @Nullable String viewFinderLaserColor) {
        if (viewFinderLaserColor != null) {
            view.setLaserColor(viewFinderLaserColor);
        }
    }

    @ReactProp(name = "type")
    public void setCameraType(RNCameraView view, @Nullable String cameraType) {
        if (cameraType != null) {
            view.setCameraType(cameraType);
        }
    }

    @ReactProp(name = "torchMode")
    public void setTorchMode(RNCameraView view, @Nullable String torchMode) {
        if (torchMode != null) {
            view.setTorchMode(torchMode);
        }
    }

    @ReactProp(name = "autoFocus")
    public void setAutoFocus(RNCameraView view, @Nullable Boolean autoFocus) {
        if (autoFocus != null) {
            view.setAutoFocus(autoFocus);
        }
    }

    @Override
    public @Nullable
    Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("takePicture", TAKE_PICTURE);
    }

    @Override
    public void receiveCommand(
            RNCameraView root,
            int commandId,
            @Nullable ReadableArray args) {
        switch (commandId) {
            case TAKE_PICTURE:
                root.takePicture();
                break;
        }
    }

    @Override
    public RNCameraView createViewInstance(ThemedReactContext context) {
        context.addLifecycleEventListener(this);
        mCameraView = new RNCameraView(context);
        mCameraView.setMaskColor(DEFAULT_VIEWFINDER_BACKGROUND_COLOR);
        mCameraView.setBorderColor(DEFAULT_VIEWFINDER_BORDER_COLOR);
        mCameraView.setBorderStrokeWidth(DEFAULT_VIEWFINDER_BORDER_WIDTH);
        mCameraView.setBorderLineLength(DEFAULT_VIEWFINDER_BORDER_LENGTH);
        mCameraView.setDrawLaser(DEFAULT_VIEWFINDER_DRAW_LASER);
        mCameraView.setLaserColor(DEFAULT_VIEWFINDER_LASER_COLOR);
        mCameraView.setCameraType(DEFAULT_CAMERA_TYPE);
        mCameraView.setTorchMode(DEFAULT_TORCH_MODE);
        mCameraViewVisible = true;
        return mCameraView;
    }

    @Override
    public void onDropViewInstance(ThemedReactContext reactContext, RNCameraView view) {
        mCameraViewVisible = false;
        view.stopCamera();
    }

    @Override
    public void onHostResume() {
        if (mCameraViewVisible) {
            mCameraView.startCamera(mCameraView.getCameraId());
            mCameraView.setFlash(mCameraView.torchModeIsEnabled());
        }
    }

    @Override
    public void onHostPause() {
        mCameraView.stopCamera();
    }

    @Override
    public void onHostDestroy() {
        mCameraView.stopCamera();
    }


}
