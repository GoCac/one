package me.app.blue;

import java.util.List;

import me.one.home.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BlueAdapter extends BaseAdapter {

	private List<BluetoothInfo> mBlueInfos;
	private LayoutInflater inflater;

	public BlueAdapter(List<BluetoothInfo> mBlueInfos, LayoutInflater inflater) {
		this.mBlueInfos = mBlueInfos;
		this.inflater = inflater;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mBlueInfos.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return mBlueInfos.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.bluelist, null);
			viewHolder = new ViewHolder();
			viewHolder.blueName = (TextView) convertView
					.findViewById(R.id.tv_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.blueName.setText(mBlueInfos.get(pos).getName());
		return convertView;
	}

	public static final class ViewHolder {
		private TextView blueName;
	}

}
