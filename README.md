# react-native-camera-android
React native camera view

###A react native camera component inspired by
#####https://github.com/lwansbrough/react-native-camera
#####https://github.com/ideacreation/react-native-barcodescanner

### Installation

```bash
npm i --save react-native-camera-android
```

### Add it to your android project

* In `android/setting.gradle`

```gradle
...
include ':ReactNativeCameraAndroid', ':app'
project(':ReactNativeCameraAndroid').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-camera-android/android')
```

* register module (in MainActivity.java)

```java
import import com.ReactCamera.RNCameraViewPackage;  // <--- import

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  ......

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReactRootView = new ReactRootView(this);

    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage())
      .addPackage(new ReactCameraPackage())              // <------ add here
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mReactRootView.startReactApplication(mReactInstanceManager, "ExampleRN", null);

    setContentView(mReactRootView);
  }

  ......

}
```