package me.app.home;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.app.blue.BluetoothInfo;
import me.app.global.Constant;
import me.app.global.MyApplication;
import me.app.home.MoreFragment.SendMsg;
import me.one.home.R;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.tencentmap.lbssdk.TencentMapLBSApi;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiListener;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;
import com.tencent.tencentmap.mapsdk.map.GeoPoint;

public class MyHomeActivity extends FragmentActivity implements
		OnClickListener, SendMsg {
	private static final boolean D = Constant.DEBUG;
	private static final int BLUE = 3;
	private long end_time = 0;
	private Handler myHandler = new MyHandler(this);
	private LocListener mListener = null;
	private WakeLock mWakeLock = null;
	private TencentMapLBSApiResult mLocRes = null;
	private MyApplication mApplication;
	private MyOverlay myOverlay = null;
	private MyItemizedOverlay myItemOverlay = null;
	private BluetoothAdapter blueAdapter;
	private List<BluetoothDevice> deviceList;
	private BluetoothReceiver receiver;
	private ExecutorService pool;
	private InputStream mInStream;
	private OutputStream mOutStream;
	private List<BluetoothInfo> blueInfos;
	private Map<String, BluetoothInfo> blueInfoMap;
	private LayoutInflater inflater;
	private String[] mBlueNames;
	private int mPos = 0;

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
				case 3:
					if (msg.obj != null) {
						String str = (String) msg.obj;
						Toast.makeText(homeActivity, str, Toast.LENGTH_SHORT)
								.show();
					}
					break;
				default:
					break;
				}
			}
		}
	};

	// public byte[] toByteArray(Object obj) {
	// byte[] bytes = null;
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// try {
	// ObjectOutputStream oos = new ObjectOutputStream(bos);
	// oos.writeObject(obj);
	// oos.flush();
	// bytes = bos.toByteArray();
	// oos.close();
	// bos.close();
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// }
	// return bytes;
	// }
	//
	// public Object toObject(byte[] bytes) {
	// Object obj = null;
	// try {
	// ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	// ObjectInputStream ois = new ObjectInputStream(bis);
	// obj = ois.readObject();
	// ois.close();
	// bis.close();
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// } catch (ClassNotFoundException ex) {
	// ex.printStackTrace();
	// }
	// return obj;
	// }

	public class LocListener extends TencentMapLBSApiListener {

		public LocListener(int reqGeoType, int reqLevel, int reqDelay) {
			super(reqGeoType, reqLevel, reqDelay);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onLocationUpdate(TencentMapLBSApiResult locRes) {
			// TODO Auto-generated method stub
			mLocRes = locRes;
			updateMapView();
		}

		@Override
		public void onStatusUpdate(int arg0) {
			// TODO Auto-generated method stub
			super.onStatusUpdate(arg0);
		}

	}

	// 获取位置结果
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
		this.myItemOverlay = new MyItemizedOverlay(this);
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
		this.blueInfos = new ArrayList<BluetoothInfo>();
		this.blueInfoMap = new HashMap<String, BluetoothInfo>();
		this.pool = Executors.newFixedThreadPool(3);
		this.blueAdapter = BluetoothAdapter.getDefaultAdapter();
		this.inflater = this.getLayoutInflater();
		this.deviceList = new ArrayList<BluetoothDevice>();
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);// 使得蓝牙处于可发现模式，持续时间150s
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 150);
		startActivity(discoverableIntent);
		this.blueAdapter.startDiscovery();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		receiver = new BluetoothReceiver();
		this.registerReceiver(receiver, filter);
		this.pool.execute(new AcceptThread());
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
		unregisterReceiver(receiver);
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
		case R.id.btn_open_blue:
			if (blueAdapter == null) {
				Toast.makeText(this, "对不起，本机没有蓝牙功能！", Toast.LENGTH_SHORT)
						.show();
			} else {
				if (!blueAdapter.isEnabled()) {
					blueAdapter.enable();
					Toast.makeText(this, "蓝牙功能已成功开启！", Toast.LENGTH_SHORT)
							.show();
					((Button) view).setText("关闭蓝牙功能");
				} else {
					blueAdapter.disable();
					Toast.makeText(this, "蓝牙功能已经关闭！", Toast.LENGTH_SHORT)
							.show();
					((Button) view).setText("开启蓝牙功能");
				}
			}
			break;
		case R.id.btn_search_blue:
			if (blueAdapter == null) {
				Toast.makeText(this, "对不起，本机没有蓝牙功能！", Toast.LENGTH_SHORT)
						.show();
			} else {
				if (!blueAdapter.isEnabled()) {
					Toast.makeText(this, "请先开启蓝牙功能！！", Toast.LENGTH_SHORT)
							.show();
				} else {
					blueAdapter.startDiscovery();
					mBlueNames = mapToArray();
					if (mBlueNames == null || blueInfoMap.size() <= 0) {
						Toast.makeText(MyHomeActivity.this, "暂时没有可用的蓝牙信号！",
								Toast.LENGTH_SHORT).show();
					} else {
						new AlertDialog.Builder(MyHomeActivity.this)
								.setTitle("周围蓝牙列表")
								.setSingleChoiceItems(mBlueNames, 0,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface arg0,
													int pos) {
												mPos = pos;
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												dialog.dismiss();
											}
										})
								.setPositiveButton("连接",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int pos) {
												pool.execute(new ConnectThread(
														deviceList.get(mPos)));
												Toast.makeText(
														MyHomeActivity.this,
														"连接到蓝牙："
																+ mBlueNames[mPos],
														Toast.LENGTH_SHORT)
														.show();
												mPos = 0;
											}

										}).show();
					}

				}
			}
			break;
		default:
			break;
		}

	}

	private String[] mapToArray() {
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, BluetoothInfo> entry : blueInfoMap.entrySet()) {
			list.add(blueInfoMap.get(entry.getKey()).getName());
		}
		return list.toArray(new String[1]);
	}

	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			try {
				tmp = blueAdapter
						.listenUsingRfcommWithServiceRecord(
								"me",
								UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			mServerSocket = tmp;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			BluetoothSocket socket = null;
			while (true) {
				try {
					socket = mServerSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				if (socket != null) {
					pool.execute(new ConnectedThread(socket));
				}
			}
		}

	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mSocket;
		private final BluetoothDevice mDevice;

		public ConnectThread(BluetoothDevice device) {
			this.mDevice = device;
			BluetoothSocket tmp = null;
			try {
				tmp = device.createRfcommSocketToServiceRecord(UUID
						.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSocket = tmp;
		}

		public void run() {
			blueAdapter.cancelDiscovery();
			try {
				mSocket.connect();
				Log.i("connect", "连接成功" + mDevice.getName());
			} catch (IOException e) {
				try {
					mSocket.close();
				} catch (IOException ee) {
					ee.printStackTrace();
				}
				return;
			}
			pool.execute(new ConnectedThread(mSocket));
		}

		public void cancel() {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mSocket;

		// private final InputStream mInStream;
		// private final OutputStream mOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mInStream = tmpIn;
			mOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int bytes;
			while (true) {
				try {
					bytes = mInStream.read(buffer);
					String str = new String(buffer, 0, bytes, "UTF-8");
					Log.i("hs", str);
					Message msg = myHandler.obtainMessage(BLUE);
					msg.obj = str;
					msg.arg1 = bytes;
					myHandler.sendMessage(msg);
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}

		public void write(byte[] bytes) {
			try {
				mOutStream.write(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void cancel() {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class BluetoothReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (!blueInfoMap.containsKey(device.getAddress())) {
					blueInfoMap.put(device.getAddress(), new BluetoothInfo(
							device.getName(), device.getAddress()));
					deviceList.add(device);
					Log.i("blue name", device.getName());
				}
			}
		}

	}

	public static String resultToString(TencentMapLBSApiResult result) {
		StringBuilder sb = new StringBuilder();
		if (result != null) {
			if (result.Province != null && !result.Province.equals("Unknown")) {
				sb.append(result.Province);
			}
			if (result.City != null && !result.City.equals("Unknown")) {
				sb.append(result.City);
			}
			if (result.District != null && !result.District.equals("Unknown")) {
				sb.append(result.District);
			}
			if (result.Town != null && !result.Town.equals("Unknown")) {
				sb.append(result.Town);
			}
			if (result.Village != null && !result.Village.equals("UnKnown")) {
				sb.append(result.Village);
			}
			if (result.Street != null && !result.Street.equals("Unkonwn")) {
				sb.append(result.Street);
			}
			if (result.StreetNo != null && !result.StreetNo.equals("Unknown")) {
				sb.append(result.StreetNo);
			}
		}
		return sb.toString();
	}

	private void updateMapView() {
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

	@Override
	public void SendMsgToDevice(String msg) {
		// TODO Auto-generated method stub
		if (this.mOutStream != null) {
			try {
				this.mOutStream.write(msg.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
