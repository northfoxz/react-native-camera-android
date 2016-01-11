# react-native-camera-android
React native camera view

###A react native camera component inspired by
#####https://github.com/lwansbrough/react-native-camera
#####https://github.com/ideacreation/react-native-barcodescanner

![](http://i.imgur.com/xQBGMIs.jpg)

![](http://i.imgur.com/TwhSRg6.jpg)

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
import com.ReactCamera.RNCameraViewPackage;  // <--- import

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

      .addPackage(new RNCameraViewPackage())              // <------ add here
      
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mReactRootView.startReactApplication(mReactInstanceManager, "ExampleRN", null);

    setContentView(mReactRootView);
  }

  ......

}
```

## Example
```javascript
var Camera = require('react-native-camera-android');

var Example = React.createClass({

  render() {
    return (
      <BarcodeScanner
        ref="cam"
        torchMode={"off"}
        cameraType={"back"}
        viewFinderDisplay={true}
        onBarCodeRead={this._barcodeReceived}
      />
    );
  }

  _takePicture() {
    this.refs.cam.takePicture();
  }

  _barcodeReceived(event) {
    console.log('Barcode: ' + e.data);
    console.log('Type: ' + e.type);
  }

  _onPictureTaken(event) {
    if(event.type !== 'error') {
      console.log("file://" + event.message)
    }
  }

});
```

## Usage

All you need is to `require` the `react-native-camera-android` module and then use the
`<Camera/>` tag.

## Actions

#### `takePicture`

1. First add refs tag to your camera: `<Camera ref="cam" />`
2. Then call the function `this.refs.cam.takePicture()` anywhere you like.



## Properties

#### `onPictureTaken`

After the picture is taken, the `<Camera />` component will return an event to `onPictureTaken`, which contains following data payloads.

`onPictureTaken = function(event) {} `

1. `type`: `success` or `error`
2. `message`: the result of the callback

#### `onBarCodeRead`

Will call the specified method when a barcode is detected in the camera's view.
Event contains `data` (barcode value) and `type` (barcode type).
The following barcode types can be recognised:

```java
BarcodeFormat.UPC_A
BarcodeFormat.UPC_E
BarcodeFormat.EAN_13
BarcodeFormat.EAN_8
BarcodeFormat.RSS_14
BarcodeFormat.CODE_39
BarcodeFormat.CODE_93
BarcodeFormat.CODE_128
BarcodeFormat.ITF
BarcodeFormat.CODABAR
BarcodeFormat.QR_CODE
BarcodeFormat.DATA_MATRIX
BarcodeFormat.PDF_417
```

#### `torchMode`

Values:
`on`,
`off` (default)

Use the `torchMode` property to specify the camera torch mode.

#### `cameraType`

Values:
`back` (default),
`front`

Use the `cameraType` property to specify the camera to use. If you specify the front camera, but the device has no front camera the back camera is used.

### Viewfinder properties

The following properties can be used to style the viewfinder:

`viewFinderBackgroundColor`,
`viewFinderBorderColor`,
`viewFinderBorderWidth`,
`viewFinderBorderLength`,
`viewFinderDrawLaser`,
`viewFinderLaserColor`

All color values are strings and must be specified as `#AARRGGBB` (alpha, red, green, blue). `viewFinderBorderWidth` and `viewFinderBorderLength` are numbers, `viewFinderDrawLaser` is either `true` or `false` (default).
