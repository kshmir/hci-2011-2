package kwik.app.activities;


import kwik.app.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {

	public static final int MILLIS_TIME_TO_WAIT = 1000;
	public static final int STOP = 0;

	private Handler splashHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SplashActivity.STOP:
					Intent intent = new Intent(SplashActivity.this, CategoriesActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
					break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Message message = new Message();
		message.what = SplashActivity.STOP;
		
		
		splashHandler.sendMessageDelayed(message, MILLIS_TIME_TO_WAIT);
	}
	
}