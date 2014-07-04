package me.app.home;

import me.one.home.R;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MoreFragment extends Fragment {

	private View view;
	private Button btnOpenBlue, btnSearchBlue, btnSendMsg;
	private EditText etMsg;
	private BluetoothAdapter blueAdapter;
	private SendMsg mSendMsg;

	public interface SendMsg {
		public void SendMsgToDevice(String msg);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.view = inflater.inflate(R.layout.more, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		this.btnOpenBlue = (Button) view.findViewById(R.id.btn_open_blue);
		this.btnSearchBlue = (Button) view.findViewById(R.id.btn_search_blue);
		this.btnSendMsg = (Button) view.findViewById(R.id.btn_send);
		this.etMsg = (EditText) view.findViewById(R.id.et_message);
		this.mSendMsg = (SendMsg) this.getActivity();
		this.btnSearchBlue.setOnClickListener((View.OnClickListener) (this
				.getActivity()));
		this.btnOpenBlue.setOnClickListener((View.OnClickListener) this
				.getActivity());
		this.blueAdapter = BluetoothAdapter.getDefaultAdapter();
		if (blueAdapter != null) {
			if (blueAdapter.isEnabled()) {
				this.btnOpenBlue.setText("关闭蓝牙功能");
			} else {
				this.btnOpenBlue.setText("打开蓝牙功能");
			}
		} else {
			this.btnOpenBlue.setText("本机没有蓝牙功能");
		}
		this.btnSendMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String msg = etMsg.getText().toString();
				if (msg.length() > 0) {
					mSendMsg.SendMsgToDevice(msg);
				} else {
					Toast.makeText(getActivity(), "内容不能为空！", Toast.LENGTH_SHORT)
							.show();
				}
			}

		});
		// this.btnOpenBlue.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (blueAdapter == null) {
		// Toast.makeText(getActivity(), "对不起，本机没有蓝牙功能！",
		// Toast.LENGTH_SHORT).show();
		// } else {
		// if (!blueAdapter.isEnabled()) {
		// blueAdapter.enable();
		// Toast.makeText(getActivity(), "蓝牙功能已成功开启！",
		// Toast.LENGTH_SHORT).show();
		// btnOpenBlue.setText("关闭蓝牙功能");
		// } else {
		// blueAdapter.disable();
		// Toast.makeText(getActivity(), "蓝牙功能已经关闭！",
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		// }
		//
		// });
	}
}
