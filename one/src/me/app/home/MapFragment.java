package me.app.home;

import me.app.global.Constant;
import me.app.global.MyApplication;
import me.one.home.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.tencentmap.mapsdk.map.MapView;

public class MapFragment extends Fragment {

	private View view;
	private MyApplication mApplication;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.view = (View) inflater.inflate(R.layout.first_fragment, null);
		return this.view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		this.mApplication = (MyApplication) this.getActivity().getApplication();
		this.mApplication.mMapView = (MapView) view.findViewById(R.id.mapview);
		if (Constant.DEBUG) {
			Log.i("map", "first create view activity");
		}
	}
}