package com.ReactCamera;

import javax.annotation.Nullable;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.uimanager.SimpleViewManager;

import java.util.Map;

public class RNCameraViewManager extends SimpleViewManager<RNCameraView> {

    public static final String REACT_CLASS = "RNCameraView";

    @Override
    public String getName() {
         return REACT_CLASS;
    }

    @Override
    public RNCameraView createViewInstance(ThemedReactContext context) {
        return new RNCameraView(context);
    }

    @ReactProp(name = "test")
    public void setTest(RNCameraView view, @Nullable String test){
        
    }


}
