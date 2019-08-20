
# @1ziton/react-native-baidulocation
# 百度定位
## Getting started

`$ npm install @1ziton/react-native-baidulocation --save`

### Mostly automatic installation

`$ react-native link @1ziton/react-native-baidulocation`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-baidulocation` and add `RNbaidulocation.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNbaidulocation.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.dashixiong.baidulocation.LocationReactPackage;` to the imports at the top of the file
  - Add `new LocationReactPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':@1ziton_react-native-baidulocation'
    project(':@1ziton_react-native-baidulocation').projectDir = new File(rootProject.projectDir, 	'../node_modules/@1ziton/react-native-baidulocation/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      implementation project(':@1ziton_react-native-baidulocation')
      1.在主项目的build.gradle 配置key
          defaultConfig {
        manifestPlaceholders = [
                BAIDU_APPKEY:'yourkey'
        ]

    }
    2.在主项目的AndroidManifest.xml配置
          <meta-data
        android:name="com.baidu.lbsapi.API_KEY"
        android:value="${BAIDU_APPKEY}" />
  	```



## Usage
```javascript
import {
  NativeModules
} from 'react-native';
const { LocationModule } = NativeModules;

      componentDidMount() {
    if (Platform.OS === ANDROID) {
      LocationModule.locationNow();
    }
  }

    LocationModule.getLocation()
        .then((result) => {
          const locationData = JSON.parse(result);
          if (locationData.errorCode === 0) {
            let { longitude, latitude } = locationData;
          } else ToastUtil.center('定位失败，请重试');
        })
        .catch(() => {
          ToastUtil.center('定位失败，请重试');
        });
    }
```

```