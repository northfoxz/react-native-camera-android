/**
 * @providesModule CameraViewAndroid
 */
'use strict';

var React = require('react-native');
var Subscribable = require('Subscribable');
var {
  PropTypes,
  requireNativeComponent,
  UIManager,
  DeviceEventEmitter
} = React;

var RN_CAMERA_REF = 'cameraview';

var CameraView = React.createClass({
  mixins: [Subscribable.Mixin],

  onChange: function(event) {
    if (!this.props.onBarCodeRead) return;

    this.props.onBarCodeRead({
      type: event.nativeEvent.type,
      data: event.nativeEvent.data,
    });
  },

  onPictureTaken: function(event) {
    console.log(event);
    if (!this.props.onPictureTaken) return;

    this.props.onPictureTaken({
      type: event.type,
      message: event.message
    })
  },

  componentWillMount: function() {
    this.addListenerOn(
      DeviceEventEmitter,
      'cameraResult',
      this.onPictureTaken
    );
  },

  render: function() {
    return (
      <RNCameraView
        {...this.props}
        ref={RN_CAMERA_REF}
        onChange={this.onChange}
      />
    );
  },

  takePicture: function() {
    UIManager.dispatchViewManagerCommand(
      this._getCameraLayoutHandle(),
      UIManager.RNCameraView.Commands.takePicture,
      null
    );
  },

  _getCameraLayoutHandle: function() {
    return React.findNodeHandle(this.refs[RN_CAMERA_REF]);
  }
})

CameraView.propTypes = {
  autoFocus: PropTypes.bool,
  torchMode: PropTypes.string,
  type: PropTypes.string,
  onLayout: PropTypes.bool,
  onBarCodeRead: PropTypes.func,
  onPictureTaken: PropTypes.func,
  viewFinderDisplay: PropTypes.bool,
  viewFinderBackgroundColor: PropTypes.string,
  viewFinderBorderColor: PropTypes.string,
  viewFinderBorderWidth: PropTypes.number,
  viewFinderBorderLength: PropTypes.number,
  viewFinderDrawLaser: PropTypes.bool,
  viewFinderLaserColor: PropTypes.string,
  rotation: PropTypes.number,
  scaleX: PropTypes.number,
  scaleY: PropTypes.number,
  translateX: PropTypes.number,
  translateY: PropTypes.number,
  importantForAccessibility: PropTypes.string,
  accessibilityLabel: PropTypes.string,
  testID: PropTypes.string,
  renderToHardwareTextureAndroid: PropTypes.string
};

var RNCameraView = requireNativeComponent('RNCameraView', CameraView, {
  nativeOnly: {
    onChange: true,
    accessibilityLiveRegion: 'none',
    accessibilityComponentType: 'button'
  }
});

module.exports = CameraView;
