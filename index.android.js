/**
 * @providesModule CameraViewAndroid
 */
'use strict';

var React = require('react-native');
var {
  PropTypes,
  requireNativeComponent,
  UIManager
} = React;

var RN_CAMERA_REF = 'cameraview';

class CameraView extends React.Component {
  constructor() {
    super();
    this.onChange = this.onChange.bind(this);
  }

  onChange(event) {
    if (!this.props.onBarCodeRead) {
      return;
    }

    this.props.onBarCodeRead({
      type: event.nativeEvent.type,
      data: event.nativeEvent.data,
    });
  }

  receiveImage() {
    
  }

  render() {
    return (
      <RNCameraView
        {...this.props}
        ref={RN_CAMERA_REF}
        onChange={this.onChange}
      />
    );
  }

  takePicture() {
    UIManager.dispatchViewManagerCommand(
      this._getCameraLayoutHandle(),
      UIManager.RNCameraView.Commands.takePicture,
      null
    );
  }

  _getCameraLayoutHandle() {
    return React.findNodeHandle(this.refs[RN_CAMERA_REF]);
  }
}

CameraView.propTypes = {
  viewFinderBackgroundColor: PropTypes.string,
  viewFinderBorderColor: PropTypes.string,
  viewFinderBorderWidth: PropTypes.number,
  viewFinderBorderLength: PropTypes.number,
  viewFinderDrawLaser: PropTypes.bool,
  viewFinderLaserColor: PropTypes.string,
  torchMode: PropTypes.string,
  cameraType: PropTypes.string,
  onBarCodeRead: PropTypes.func,
  rotation: PropTypes.number,
  scaleX: PropTypes.number,
  scaleY: PropTypes.number,
  translateX: PropTypes.number,
  translateY: PropTypes.number,
  importantForAccessibility: PropTypes.string,
  accessibilityLabel: PropTypes.string,
  testID: PropTypes.string,
  renderToHardwareTextureAndroid: PropTypes.string,
  onLayout: PropTypes.bool
};

var RNCameraView = requireNativeComponent('RNCameraView', CameraView, {
  nativeOnly: {
    onChange: true,
    accessibilityLiveRegion: 'none',
    accessibilityComponentType: 'button'
  }
});

module.exports = CameraView;
