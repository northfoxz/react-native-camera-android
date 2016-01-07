/**
 * @providesModule CameraViewAndroid
 */
'use strict';

var React = require('react-native');
var { requireNativeComponent, PropTypes } = React;
var RCTUIManager = React.NativeModules.UIManager;

var WEBVIEW_REF = 'androidCameraView';

var CameraViewAndroid = React.createClass({
  propTypes: {
    url: PropTypes.string,
    baseUrl: PropTypes.string,
    html: PropTypes.string,
    htmlCharset: PropTypes.string,
    userAgent: PropTypes.string,
    injectedJavaScript: PropTypes.string,
    disablePlugins: PropTypes.bool,
    disableCookies: PropTypes.bool,
    javaScriptEnabled: PropTypes.bool,
    geolocationEnabled: PropTypes.bool,
    builtInZoomControls: PropTypes.bool,
    onNavigationStateChange: PropTypes.func
  },
  _onNavigationStateChange: function(event) {
    if (this.props.onNavigationStateChange) {
      this.props.onNavigationStateChange(event.nativeEvent);
    }
  },
  goBack: function() {
    RCTUIManager.dispatchViewManagerCommand(
      this._getWebViewHandle(),
      RCTUIManager.RNCameraViewAndroid.Commands.goBack,
      null
    );
  },
  goForward: function() {
    RCTUIManager.dispatchViewManagerCommand(
      this._getWebViewHandle(),
      RCTUIManager.RNCameraViewAndroid.Commands.goForward,
      null
    );
  },
  reload: function() {
    RCTUIManager.dispatchViewManagerCommand(
      this._getWebViewHandle(),
      RCTUIManager.RNCameraViewAndroid.Commands.reload,
      null
    );
  },
  render: function() {
    return <RNCameraView ref={WEBVIEW_REF} {...this.props} onNavigationStateChange={this._onNavigationStateChange} />;
  },
  _getWebViewHandle: function() {
    return React.findNodeHandle(this.refs[WEBVIEW_REF]);
  },
});

var RNCameraView= requireNativeComponent('RNCameraView', null);

module.exports = CameraViewAndroid;