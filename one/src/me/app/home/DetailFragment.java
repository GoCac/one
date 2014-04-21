package me.app.home;

import me.one.home.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DetailFragment extends Fragment {

	private View view;

	private Button btn_test = null;
	private Button btn_test2 = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.second_fragment, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		this.btn_test = (Button) view.findViewById(R.id.btn_test);
		this.btn_test.setOnClickListener((View.OnClickListener) this
				.getActivity());
		this.btn_test2 = (Button) view.findViewById(R.id.bn_test2);
	}

}
