package me.app.home;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.app.global.Constant;
import me.app.global.MyApplication;
import me.one.home.R;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tencent.tencentmap.lbssdk.TencentMapLBSApi;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiListener;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;
import com.tencent.tencentmap.mapsdk.map.GeoPoint;

public class MyHomeActivity extends FragmentActivity implements OnClickListener {
	private static final boolean D = Constant.DEBUG;
	private long end_time = 0;
	private Handler myHandler = new MyHandler(this);
	private LocListener mListener = null;
	private WakeLock mWakeLock = null;
	private TencentMapLBSApiResult mLocRes = null;
	private MyApplication mApplication;
	private MyOverlay myOverlay = null;
	private MyItemizedOverlay myItemOverlay = null;

	private static final class MyHandler extends Handler {
		private final WeakReference<MyHomeActivity> myHomeActivity;

		private MyHandler(MyHomeActivity homeActivity) {
			this.myHomeActivity = new WeakReference<MyHomeActivity>(
					homeActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			MyHomeActivity homeActivity = myHomeActivity.get();
			if (homeActivity != null) {
				switch (msg.what) {
				case 0:
					Toast.makeText(homeActivity.getApplicationContext(),
							"上传完成", Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
			}
		}
	};

	public class LocListener extends TencentMapLBSApiListener {

		public LocListener(int reqGeoType, int reqLevel, int reqDelay) {
			super(reqGeoType, reqLevel, reqDelay);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onLocationUpdate(TencentMapLBSApiResult locRes) {
			// TODO Auto-generated method stub
			mLocRes = locRes;
			mApplication.mLocation = resultToString(locRes);
			updateMapView();
		}

		@Override
		public void onStatusUpdate(int arg0) {
			// TODO Auto-generated method stub
			super.onStatusUpdate(arg0);
		}

	}

	public TencentMapLBSApiResult getmLocRes() {
		return mLocRes;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_home);
		overridePendingTransition(R.anim.anim_into, R.anim.anim_back);
		this.mApplication = (MyApplication) this.getApplication();
		this.myOverlay = new MyOverlay();
		this.myItemOverlay = new MyItemizedOverlay(this.getApplicationContext());
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "hzjLBS");
		this.mListener = new LocListener(TencentMapLBSApi.GEO_TYPE_GCJ02,
				TencentMapLBSApi.LEVEL_ADMIN_AREA, 0);
		int req = TencentMapLBSApi.getInstance().requestLocationUpdate(
				MyHomeActivity.this.getApplicationContext(), mListener);
		TencentMapLBSApi.getInstance().setGPSUpdateInterval(10000);
		MySlideTabFragment mySlideTabFragment = new MySlideTabFragment();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.content_frame, mySlideTabFragment).commit();
		if (D) {
			Log.i("map", "req:" + req);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_home, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (System.currentTimeMillis() - this.end_time >= 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				this.end_time = System.currentTimeMillis();
			} else {
				this.finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		TencentMapLBSApi.getInstance().removeLocationUpdate();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.mWakeLock.release();
		// TencentMapLBSApi.getInstance().removeLocationUpdate();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.mWakeLock.acquire();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_test:
			if (null != mLocRes) {
				Toast.makeText(getApplicationContext(),
						resultToString(mLocRes), Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	private String resultToString(TencentMapLBSApiResult result) {
		if (result != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(result.Province).append(result.City)
					.append(result.District).append(result.Town)
					.append(result.Village).append(result.Street)
					.append(result.StreetNo);
			return sb.toString();
		}
		return null;
	}

	public void updateMapView() {
		// TODO Auto-generated method stub
		if (null != mApplication.mMapView && null != mLocRes) {
			mApplication.mMapView.clearAllOverlays();
			mApplication.mMapView.getController().setCenter(
					new GeoPoint((int) (this.mLocRes.Latitude * 1E6),
							(int) (this.mLocRes.Longitude * 1E6)));
			this.myOverlay.setLat(mLocRes.Latitude);
			this.myOverlay.setLng(mLocRes.Longitude);
			mApplication.mMapView.addOverlay(myOverlay);
			List<TencentMapLBSApiResult> results = new ArrayList<TencentMapLBSApiResult>();
			results.add(mLocRes);
			this.myItemOverlay.setLbsRes(results);
			mApplication.mMapView.addOverlay(myItemOverlay);
			if (D) {
				Log.i("map", "I have changed!");
			}
		}
	}
}
