package com.longrise.zhaojue.baidumapdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity implements RadarSearchListener {


    private BaiduMap mMap;
    private MapStatusUpdate mLng;
    private TextureMapView mMapView;
    private LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    double longitude;
    double latitude;
    private RadarSearchManager RadarSearchManagermManager;
    private RadarSearchManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        Log.i("DEMO1", latitude + "latitude  " + longitude + "longitude");
        //定位声明
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        initLocation();
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        //开启定位
        mLocationClient.start();
        mLocationClient.requestLocation();
        //获取地图控件引用
        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        mMap = mMapView.getMap();
    }

    private void singleUpload(LatLng pt) {
        //周边雷达
        //在使用位置信息上传和检索周边位置信息之前，需要对周边雷达功能模块进行初始化操作。初始化的核心代码如下：
        RadarSearchManagermManager = RadarSearchManager.getInstance();
        //   周边雷达功能模块，支持将用户的位置等信息上传到百度LBS云服务，从而实现应用内部及应用之间的位置信息查看。
        //   目前支持单次位置信息上传和位置信息连续自动上传两种模式。
        //单次位置信息上传的核心代码如下：
        //周边雷达设置监听
        mManager.addNearbyInfoListener(this);
        //周边雷达设置用户身份标识，id为空默认是设备标识
        mManager.setUserID(null);
        //上传位置
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = "用户备注信息";
        info.pt = pt;
        mManager.uploadInfoRequest(info);
        //监听上传结果
    }

    //定位到指定的坐标
    private void locationByPoint(LatLng pt) {
        MapStatus status = new MapStatus.Builder().target(pt).zoom(18).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(status);
        mMap.setMapStatus(mapStatusUpdate);
    }

    //对地图设置定位
    private void location(double latitude1, double longitude1) {
        // 开启定位图层
        mMap.setMyLocationEnabled(true);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(100)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(latitude1)
                .longitude(longitude1).build();
        // 设置定位数据
        mMap.setMyLocationData(locData);
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        // mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        // MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
        //mBaiduMap.setMyLocationConfiguration();
        // 当不需要定位图层时关闭定位图层
        //  mMap.setMyLocationEnabled(true);
        LatLng pt = new LatLng(latitude, longitude);
        locationByPoint(pt);

        //周边地图
        singleUpload(pt);

        //定义Marker坐标点
        LatLng latLng = new LatLng(latitude, longitude);
        Log.i("DEMO2", latitude + "latitude  " + longitude + "longitude");
        //构建Marker图标
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        //构建MrkerOption,用于在地图上添加Marker
        OverlayOptions options = new MarkerOptions()
                .position(latLng)
                .icon(bitmapDescriptor);
        //在地图上添加marker并显示
        mMap.addOverlay(options);
//        mMap.setMapStatus(mLng);
        mMap.setBuildingsEnabled(true);
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    //周边雷达Begin
    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {
        List<RadarNearbyInfo> list = radarNearbyResult.infoList;
        Log.i("Nearby",list.size()+"");
    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {
        // TODO Auto-generated method stub
        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            //上传成功
            Toast.makeText(MainActivity.this, "单次上传位置成功", Toast.LENGTH_LONG)
                    .show();
            Log.i("UpLoad","单次上传位置成功");
        } else {
            //上传失败
            Toast.makeText(MainActivity.this, "单次上传位置失败", Toast.LENGTH_LONG)
                    .show();
            Log.i("UpLoad","单次上传位置失败");
        }
    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

    }
    //周边雷达END

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取定位结果

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            location(latitude, longitude);
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度

            if (location.getLocType() == BDLocation.TypeGpsLocation) {

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {

                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }

            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息

            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            Log.i("Bai", s);
        }
    }
}
