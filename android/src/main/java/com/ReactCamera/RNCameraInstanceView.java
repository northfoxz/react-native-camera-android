package com.ReactCamera;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import me.dm7.barcodescanner.core.CameraPreview;
import me.dm7.barcodescanner.core.CameraUtils;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;

/**
 * Created by northfoxz on 2016/1/7.
 */
public abstract class RNCameraInstanceView extends FrameLayout implements Camera.PreviewCallback {
    private Camera mCamera;
    private CameraPreview mPreview;
    private IViewFinder mViewFinderView;
    private Rect mFramingRectInPreview;

    public RNCameraInstanceView(Context context) {
        super(context);
        this.setupLayout();
    }

    public RNCameraInstanceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setupLayout();
    }

    public final void setupLayout() {
        this.mPreview = new CameraPreview(this.getContext());
        RelativeLayout relativeLayout = new RelativeLayout(this.getContext());
        relativeLayout.setGravity(17);
        relativeLayout.setBackgroundColor(-16777216);
        relativeLayout.addView(this.mPreview);
        this.addView(relativeLayout);
        this.mViewFinderView = this.createViewFinderView(this.getContext());
        if(this.mViewFinderView instanceof View) {
            this.addView((View)this.mViewFinderView);
        } else {
            throw new IllegalArgumentException("IViewFinder object returned by \'createViewFinderView()\' should be instance of android.view.View");
        }
    }

    protected IViewFinder createViewFinderView(Context context) {
        return new ViewFinderView(context);
    }

    public void startCamera(int cameraId) {
        this.startCamera(CameraUtils.getCameraInstance(cameraId));
    }

    public void startCamera(Camera camera) {
        this.mCamera = camera;
        if(this.mCamera != null) {
            this.mViewFinderView.setupViewFinder();
            this.mPreview.setCamera(this.mCamera, this);
            this.mPreview.initCameraPreview();
        }

    }

    public void startCamera() {
        this.startCamera(CameraUtils.getCameraInstance());
    }

    public void stopCamera() {
        if(this.mCamera != null) {
            this.mPreview.stopCameraPreview();
            this.mPreview.setCamera((Camera)null, (Camera.PreviewCallback)null);
            this.mCamera.release();
            this.mCamera = null;
        }

    }

    public synchronized Rect getFramingRectInPreview(int previewWidth, int previewHeight) {
        if(this.mFramingRectInPreview == null) {
            Rect framingRect = this.mViewFinderView.getFramingRect();
            int viewFinderViewWidth = this.mViewFinderView.getWidth();
            int viewFinderViewHeight = this.mViewFinderView.getHeight();
            if(framingRect == null || viewFinderViewWidth == 0 || viewFinderViewHeight == 0) {
                return null;
            }

            Rect rect = new Rect(framingRect);
            rect.left = rect.left * previewWidth / viewFinderViewWidth;
            rect.right = rect.right * previewWidth / viewFinderViewWidth;
            rect.top = rect.top * previewHeight / viewFinderViewHeight;
            rect.bottom = rect.bottom * previewHeight / viewFinderViewHeight;
            this.mFramingRectInPreview = rect;
        }

        return this.mFramingRectInPreview;
    }

    public void setFlash(boolean flag) {
        if(this.mCamera != null && CameraUtils.isFlashSupported(this.mCamera)) {
            Camera.Parameters parameters = this.mCamera.getParameters();
            if(flag) {
                if(parameters.getFlashMode().equals("torch")) {
                    return;
                }

                parameters.setFlashMode("torch");
            } else {
                if(parameters.getFlashMode().equals("off")) {
                    return;
                }

                parameters.setFlashMode("off");
            }

            this.mCamera.setParameters(parameters);
        }

    }

    public boolean getFlash() {
        if(this.mCamera != null && CameraUtils.isFlashSupported(this.mCamera)) {
            Camera.Parameters parameters = this.mCamera.getParameters();
            return parameters.getFlashMode().equals("torch");
        } else {
            return false;
        }
    }

    public void toggleFlash() {
        if(this.mCamera != null && CameraUtils.isFlashSupported(this.mCamera)) {
            Camera.Parameters parameters = this.mCamera.getParameters();
            if(parameters.getFlashMode().equals("torch")) {
                parameters.setFlashMode("off");
            } else {
                parameters.setFlashMode("torch");
            }

            this.mCamera.setParameters(parameters);
        }

    }

    public void setAutoFocus(boolean state) {
        if(this.mPreview != null) {
            this.mPreview.setAutoFocus(state);
        }

    }

    public void takePicture() {
        // get an image from the camera
        mCamera.takePicture(null, null, mPicture);
    }
    /**
     * Picture Callback for handling a picture capture and saving it out to a file.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null){
                // Take picture android error;
//                Toast.makeText(getActivity(), "Image retrieval failed.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Log.v("camera", "capture success");
                returnPictureTakenResult("success", pictureFile.getAbsolutePath());

                // Restart the camera preview.
//                safeCameraOpenInView(mCameraView);
            } catch (FileNotFoundException e) {
                returnPictureTakenResult("error", "file not found");
                e.printStackTrace();
            } catch (IOException e) {
                returnPictureTakenResult("error", "an exception error");
                e.printStackTrace();
            }
        }
    };

    /**
     * Used to return the camera File output.
     * @return
     */
    private File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "RNCameraAndroid");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Camera Guide", "Required media storage does not exist");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        Log.v("camera", mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        return mediaFile;
    }

    public void returnPictureTakenResult(String resultType, String resultMessage) {

    }
}
