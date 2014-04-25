package me.app.home;

import java.util.ArrayList;
import java.util.List;

import me.one.home.R;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.view.sliding.AbSlidingTabView;

public class MySlideTabFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_tab_fragment, null);
		AbSlidingTabView mAbSlidingTabView = (AbSlidingTabView) view
				.findViewById(R.id.mAbSlidingTabView);
		MapFragment shopFrag1 = new MapFragment();
		DetailFragment shopFrag2 = new DetailFragment();
		DetailFragment shopFrag3 = new DetailFragment();
		List<Fragment> mFragments = new ArrayList<Fragment>();
		mFragments.add(shopFrag1);
		mFragments.add(shopFrag2);
		mFragments.add(shopFrag3);
		List<String> tabTitles = new ArrayList<String>();
		tabTitles.add("首页");
		tabTitles.add("发现");
		tabTitles.add("更多...");
		mAbSlidingTabView.setTabTextColor(Color.BLACK);
		mAbSlidingTabView.setTabSelectColor(Color.rgb(30, 168, 131));
		mAbSlidingTabView.setTabBackgroundResource(R.drawable.tab_bg);
		mAbSlidingTabView.setTabLayoutBackgroundResource(R.drawable.slide_top);
		mAbSlidingTabView.addItemViews(tabTitles, mFragments);
		mAbSlidingTabView.setTabPadding(20, 8, 20, 8);
		mAbSlidingTabView.getViewPager().setOffscreenPageLimit(3);
		mAbSlidingTabView.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				// Toast.makeText(MySlideTabFragment.this.getActivity(), "ok",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

}
