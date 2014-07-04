package me.app.home;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;

import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;
import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.ItemizedOverlay;
import com.tencent.tencentmap.mapsdk.map.OverlayItem;
import com.tencent.tencentmap.mapsdk.search.GeocoderSearch;
import com.tencent.tencentmap.mapsdk.search.ReGeocoderResult;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> overlayItems = null;
	private List<TencentMapLBSApiResult> mLbsRes = null;
	private Context context = null;

	public MyItemizedOverlay(final Context mContext) {
		super(mContext);
		this.context = mContext;
		this.overlayItems = new ArrayList<OverlayItem>();
		this.setOnFocusChangeListener(new com.tencent.tencentmap.mapsdk.map.ItemizedOverlay.OnFocusChangeListener() {

			@Override
			public void onFocusChanged(ItemizedOverlay<?> oldlay,
					OverlayItem newlay) {
				// TODO Auto-generated method stub
				if (newlay != null) {
					// Toast.makeText(mContext, newlay.getSnippet(),
					// Toast.LENGTH_SHORT).show();
					new AlertDialog.Builder(mContext).setIcon(null)
							.setTitle("位置信息").setMessage(newlay.getSnippet())
							.setCancelable(true).setPositiveButton("知道啦", null)
							.show();
				}
			}

		});
		// TODO Auto-generated constructor stub
	}

	public List<TencentMapLBSApiResult> getLbsRes() {
		return mLbsRes;
	}

	public void setLbsRes(List<TencentMapLBSApiResult> lbsRes) {
		this.mLbsRes = lbsRes;
		this.overlayItems.clear();
		for (TencentMapLBSApiResult res : mLbsRes) {
			overlayItems.add(new OverlayItem(new GeoPoint(
					(int) (res.Latitude * 1E6), (int) (res.Longitude * 1E6)),
					"p", "您的位置:" + MyHomeActivity.resultToString(res)));
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

	public ReGeocoderResult searchFromGeo(GeoPoint geoPoint) {
		GeocoderSearch geocoderSearch = new GeocoderSearch(this.context);
		try {
			return geocoderSearch.searchFromLocation(geoPoint);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
