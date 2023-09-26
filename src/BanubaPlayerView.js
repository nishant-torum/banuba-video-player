import React, {useEffect, useRef, useState} from 'react';
import {Dimensions, StyleSheet, View} from 'react-native';

import {requireNativeComponent} from 'react-native';

const screenWidth = Dimensions?.get('screen').width;

const screenheight = Dimensions?.get('screen').height;

// const VideoEditorView = requireNativeComponent('VideoEditorView');

const BanubaPlayerView = props => {
  const videoPlayerRef = useRef(null);

  return (
    <View style={styles.container}>
      {/* <VideoEditorView ref={videoPlayerRef} style={styles.customText} /> */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    marginVertical: 16,
    alignItems: 'center',
    justifyContent: 'center',
  },
  customText: {
    height: screenheight - 400,
    width: screenWidth,
  },
  button: {
    height: 40,
    width: 40,
  },
});

export default BanubaPlayerView;
