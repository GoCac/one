package me.app.global;

import com.tencent.tencentmap.mapsdk.map.MapView;

import android.app.Application;

public class MyApplication extends Application {
	public MapView mMapView = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}
