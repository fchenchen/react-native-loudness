/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */
import React from 'react';
import { SafeAreaView, View, Text, } from 'react-native';
import { Loudness } from 'react-native-loudness';

export default class AppView extends React.Component {
  constructor(){
    super();

    this.state = {
      loudness: 0,
    }
    const loudness = new Loudness();
    loudness.start();

    this.loudnessGetter = setInterval(() => {
      loudness.getLoudness((l) => {
        if (!isFinite(l)) return;
        this.setState({loudness: l});
      });
    }, 100);
  }

  render(){
    return(
      <View style={{
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
      }}>
        <Text>{'Loudness: '+ this.state.loudness.toFixed(2)}</Text>
      </View>
      );
  }
}
