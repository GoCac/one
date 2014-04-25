package me.app.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;
import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.ItemizedOverlay;
import com.tencent.tencentmap.mapsdk.map.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> overlayItems = null;
	private List<TencentMapLBSApiResult> mLbsRes = null;

	public MyItemizedOverlay(Context mContex) {
		super(mContex);
		this.overlayItems = new ArrayList<OverlayItem>();
		// TODO Auto-generated constructor stub
	}

	public List<TencentMapLBSApiResult> getLbsRes() {
		return mLbsRes;
	}

	public void setLbsRes(List<TencentMapLBSApiResult> lbsRes) {
		this.mLbsRes = lbsRes;
		for (TencentMapLBSApiResult res : mLbsRes) {
			overlayItems.add(new OverlayItem(new GeoPoint(
					(int) (res.Latitude * 1E6), (int) (res.Longitude * 1E6)),
					"p", ""));
		}
		populate();
	}

	@Override
	protected OverlayItem createItem(int pos) {
		// TODO Auto-generated method stub
		if (overlayItems == null) {
			return null;
		}
		return overlayItems.get(pos);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		if (overlayItems == null) {
			return 0;
		}
		return overlayItems.size();
	}

}
