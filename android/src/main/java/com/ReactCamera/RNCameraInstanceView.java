package com.ReactCamera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.facebook.react.uimanager.ThemedReactContext;

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
    private RelativeLayout mCameraView;
    private CameraPreview mPreview;
    private IViewFinder mViewFinderView;
    private Rect mFramingRectInPreview;
    private ThemedReactContext mContext;
    private boolean mViewFinderDisplay;

    public RNCameraInstanceView(ThemedReactContext context) {
        super(context);
        mContext = context;
        this.setupLayout();
    }

    public final void setupLayout() {
        this.mPreview = new CameraPreview(this.getContext());
        RelativeLayout mCameraView = new RelativeLayout(this.getContext());
        mCameraView.setGravity(17);
        mCameraView.setBackgroundColor(-16777216);
        mCameraView.addView(this.mPreview);
        this.addView(mCameraView);
        this.mViewFinderView = this.createViewFinderView(this.getContext());
        if(this.mViewFinderView instanceof View) {
            if(mViewFinderDisplay)
                this.addView((View)this.mViewFinderView);
        } else {
            throw new IllegalArgumentException("IViewFinder object returned by \'createViewFinderView()\' should be instance of android.view.View");
        }
    }

    public void setViewFinderDisplay(boolean display) {
        if(display) {
            Log.w("camera", "setting view finder display as true");
            this.addView((View)this.mViewFinderView);
        }else
            Log.w("camera", "setting view finder display as false");
        mViewFinderDisplay = display;
    }

    protected IViewFinder createViewFinderView(Context context) {
        return new ViewFinderView(context);
    }

    public void startCamera(int cameraId) {
        this.startCamera(CameraUtils.getCameraInstance(cameraId));
    }
    public void startCamera() {
        this.startCamera(CameraUtils.getCameraInstance());
    }
    public void startCamera(Camera camera) {
        this.mCamera = camera;
        if(this.mCamera != null) {
            this.mViewFinderView.setupViewFinder();
            this.mPreview.setCamera(this.mCamera, this);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.set("orientation", "portrait");
            mCamera.setParameters(parameters);
            this.mPreview.initCameraPreview();
        }

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
                returnPictureTakenResult("error", "directory error");
                mCamera.startPreview();
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
//                Log.v("camera", FileOutputStream.getFileStreamPath(pictureFile.getName()));
                fos.write(data);
                fos.close();

                // Restart the camera preview.
                returnPictureTakenResult("success", pictureFile.getAbsolutePath());
//                mCamera.startPreview();
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
        try {

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/rncamera");
            myDir.mkdirs();
            if(myDir.exists())
                Log.v("camera", "directory created");
            else
                Log.v("camera", "directory still not created");
            File mediaFile;
            mediaFile = new File(myDir, "IMG_"+ timeStamp + ".jpg");
            mediaFile.createNewFile( );
            if(mediaFile.exists())
                Log.v("camera", "file created now");
            else
                Log.v("camera", "file still not created");

            if(mediaFile.isDirectory())
                Log.v("camera", "is directory");
            else
                Log.v("camera", "is file");
            Log.v("camera", mediaFile.getAbsolutePath());
            return mediaFile;
        }
        catch(SecurityException e) {
            Log.v("camera", e.getMessage());
            return null;
        }
        catch(IOException e) {
            Log.v("camera", e.getMessage());
            return null;
        }
    }

    public void returnPictureTakenResult(String resultType, String resultMessage) {

    }
}
