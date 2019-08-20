package com.dashixiong.baidulocation;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 分享组件
 */

public class LocationModule extends ReactContextBaseJavaModule{
  public LocationClient mLocationClient = null;
  private MyLocationListener myListener = null;
  private BDLocation currentLocation;
  Promise successCallback;
  String errorJson = null;
  @Override
  public String getName() {
    /**
     * return the string name of the NativeModule which represents this class in JavaScript
     * In JS access this module through React.NativeModules.OpenSettings
     */
    return "LocationModule";
  }

  @ReactMethod
  public void getLocation(final Promise successCallback) {
    this.successCallback = successCallback;
    Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      successCallback.resolve(errorJson);
      return;
    }
    returnCurrentLocation();
  }
  @ReactMethod
  public void locationNow(){
    Activity currentActivity = getCurrentActivity();
    if(currentActivity != null) {
      //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
      if (showCheckPermissions())
        if (ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
          || ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          //请求权限
          ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {

        }
    }
    if(mLocationClient != null) {
      mLocationClient.restart();
    }
  }

  /**
   * 是否应该检查权限
   * @return
   */
  public static boolean showCheckPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return true;
    } else {
      return false;
    }
  }


  /* constructor */
  public LocationModule(ReactApplicationContext reactContext) {
    super(reactContext);
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("errorCode", -1);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    errorJson = jsonObject.toString();
    myListener = new MyLocationListener();
    mLocationClient = new LocationClient(reactContext.getApplicationContext());
    //声明LocationClient类
    mLocationClient.registerLocationListener(myListener);
    //注册监听函数

    LocationClientOption option = new LocationClientOption();

    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，设置定位模式，默认高精度
//LocationMode.Hight_Accuracy：高精度；
//LocationMode. Battery_Saving：低功耗；
//LocationMode. Device_Sensors：仅使用设备；

    option.setCoorType("bd09ll");
//可选，设置返回经纬度坐标类型，默认GCJ02
//GCJ02：国测局坐标；
//BD09ll：百度经纬度坐标；
//BD09：百度墨卡托坐标；
//海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

    option.setScanSpan(0);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效

    option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true

    option.setLocationNotify(true);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

    option.setIgnoreKillProcess(false);
//可选，定位SDK内部是一个service，并放到了独立进程。
//设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

    option.SetIgnoreCacheException(false);
//可选，设置是否收集Crash信息，默认收集，即参数为false

    option.setWifiCacheTimeOut(5*60*1000);
//可选，V7.2版本新增能力
//如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

    option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

    mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    mLocationClient.start();

  }


  public class MyLocationListener extends BDAbstractLocationListener {
    @Override
    public void onReceiveLocation(BDLocation location){
      currentLocation = location;
    }
  }

  private void returnCurrentLocation() {
    //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
    //以下只列举部分获取经纬度相关（常用）的结果信息
    //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
    if(currentLocation == null){
      successCallback.resolve(errorJson);
      locationNow();
      return;
    }
    double latitude = currentLocation.getLatitude();    //获取纬度信息
    double longitude = currentLocation.getLongitude();    //获取经度信息
    //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
    JSONObject jsonObject = new JSONObject();
    //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
    if(currentLocation != null && (currentLocation.getLocType() == BDLocation.TypeGpsLocation ||
      currentLocation.getLocType() == BDLocation.TypeOffLineLocation ||
      currentLocation.getLocType() == BDLocation.TypeNetWorkLocation
      )){
      try {
        jsonObject.put("latitude",latitude);
        jsonObject.put("longitude",longitude);
        jsonObject.put("errorCode",0);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      successCallback.resolve(jsonObject.toString());
    }else {
      successCallback.resolve(errorJson);
    }
  }
}
