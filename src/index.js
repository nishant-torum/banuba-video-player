import React, {Component, useRef} from 'react';
import {
  StyleSheet,
  Text,
  Button,
  View,
  Platform,
  NativeModules,
  Dimensions,
  requireNativeComponent,
} from 'react-native';
const {VideoEditorModule} = NativeModules;

const VideoEditorView = requireNativeComponent('VideoEditorView');
// Set Banuba license token for Video Editor SDK
const LICENSE_TOKEN =
  'TMdbt1gDGcXFRD4STgk6qDRKfl6KoEI4M7W1GH3HmBnIrkvZ5UFkfyXBArfdDPJ+ruILLhDjOrIbQji4RQLoFqZ6zIvTZOOVAcdrM/qGgzdNiv1jLHq12mexlUOOm7mxDBeuccYFsN5AggiYDzhEQAD42AxMTvFOvMP+3tmO8h9yOzUbFjK4AlOFL0jWE703NrxoOfEs6tHcfo7q3XvOMVZ6cFD8E7rsWUKBmXDS90jsSKo6i42uapUBtoxZp2Pp2Hs/r+Id2/7WwqUx4N3+g75l5B1UwBsQv73urcNXlx4AeW+3p5opSq9L4TGg0+ZrRBvzffK5uUkZyaDTNmyca7Bxn4Xq9RAcNUtdijPckDB9Z1kGxCTsnEtYif1xEk0tEfAfowi5yzbo7N2XajwXILQu8/PoWpTnRxZ4o59cfcl41AUOhwmc0o7Tek5pHZ/RK0LO4rdBK9vtONV+2gcoxYn3skld5smlyGtEI+M88Eq55ldrV3XreiUyyuUMtVMXCHYJAYd371r/WqrZ3zrEJuHFXR9pLUVBPpdJ';

const ERR_SDK_NOT_INITIALIZED_CODE = 'ERR_VIDEO_EDITOR_NOT_INITIALIZED';
const ERR_SDK_NOT_INITIALIZED_MESSAGE =
  'Banuba Video Editor SDK is not initialized: license token is unknown or incorrect.\nPlease check your license token or contact Banuba';

const ERR_LICENSE_REVOKED_CODE = 'ERR_VIDEO_EDITOR_LICENSE_REVOKED';
const ERR_LICENSE_REVOKED_MESSAGE =
  'License is revoked or expired. Please contact Banuba https://www.banuba.com/faq/kb-tickets/new';

function initVideoEditor() {
  VideoEditorModule.initVideoEditor(LICENSE_TOKEN);
}

async function startIosVideoEditor() {
  initVideoEditor();
  return await VideoEditorModule.openVideoEditor();
}

async function startAndroidVideoEditor() {
  initVideoEditor();
}

export default class App extends Component {
  constructor() {
    super();
    this.state = {
      errorText: '',
      banubaPlayerView: false,
    };
  }

  handleExportException(e) {
    var message = '';
    switch (e.code) {
      case ERR_SDK_NOT_INITIALIZED_CODE:
        message = ERR_SDK_NOT_INITIALIZED_MESSAGE;
        break;
      case ERR_LICENSE_REVOKED_CODE:
        message = ERR_LICENSE_REVOKED_MESSAGE;
        break;
      default:
        message = '';
        console.log(
          'Banuba ' +
            Platform.OS.toUpperCase() +
            ' Video Editor export video failed = ' +
            e,
        );
        break;
    }
    this.setState({errorText: message, banubaPlayerView: false});
  }

  render() {
    return (
      <View style={styles.container}>
        {this.state.banubaPlayerView && (
          <VideoEditorView style={styles.customText} />
        )}
        <Text style={{padding: 16, textAlign: 'center'}}>
          Integration Native (Android) banuba video player in React Native
        </Text>

        <Text
          style={{
            padding: 16,
            textAlign: 'center',
            color: '#ff0000',
            fontSize: 16,
            fontWeight: '800',
          }}>
          {this.state.errorText}
        </Text>

        <View style={{marginVertical: 8}}>
          <Button
            title="Setup Banuba token"
            onPress={async () => {
              if (Platform.OS === 'android') {
                startAndroidVideoEditor()
                  .then(videoUri => {
                    console.log(
                      'Banuba Android Video Editor export video completed successfully. Video uri = ' +
                        videoUri,
                    );
                  })
                  .catch(e => {
                    this.handleExportException(e);
                  });
              } else {
                startIosVideoEditor()
                  .then(response => {
                    const exportedVideoUri = response?.videoUri;
                    console.log(
                      'Banuba iOS Video Editor export video completed successfully. Video uri = ' +
                        exportedVideoUri,
                    );
                  })
                  .catch(e => {
                    this.handleExportException(e);
                  });
              }
            }}
          />
        </View>

        <View style={{marginVertical: 8}}>
          <Button
            title="Play video"
            color="#ff0000"
            onPress={() => this.setState({banubaPlayerView: true})}
          />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    marginVertical: 16,
    alignItems: 'center',
    justifyContent: 'center',
  },
  customText: {
    height: 400,
    width: 400,
  },
});
