package me.app.home;

import java.lang.ref.WeakReference;

import me.one.home.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MyHomeActivity extends FragmentActivity implements OnClickListener {
	private long end_time = 0;
	private Handler myHandler = new MyHandler(this);

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_home);
		overridePendingTransition(R.anim.anim_into, R.anim.anim_back);
		MySlideTabFragment mySlideTabFragment = new MySlideTabFragment();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.content_frame, mySlideTabFragment).commit();
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
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_test:
			break;
		}
	}

}
