package kwik.app.activities;

import kwik.app.R;
import kwik.app.activities.custom.KwikFragmentActivity;
import kwik.remote.api.Product;
import kwik.remote.util.DrawableManager;
import kwik.services.KwikAPIService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ProductActivity extends KwikFragmentActivity {
	
	
	private static DrawableManager imageManager = new DrawableManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Activity self=this;
		Intent localIntent = this.getIntent();
		
		Bundle extras = localIntent.getExtras();
		
		
		final Integer product_id = extras.getInt("product_id", -1);
		final String  product_name = extras.getString("product_name");
		
		setTitle(product_name);
		
		setContentView(R.layout.product);
		
		Intent intent = new Intent(Intent.ACTION_SYNC, null, this, KwikAPIService.class);
		
		intent.putExtra("command", KwikAPIService.GET_PRODUCT_CMD);
		intent.putExtra("product_id", product_id);
		
		
		intent.putExtra("receiver", new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				super.onReceiveResult(resultCode, resultData);
				if (resultCode == KwikAPIService.STATUS_OK) {
					
					Product prod = (Product) resultData.getSerializable("return");
					
					View progressBar = ((View)findViewById(R.id.progressbar));
					progressBar.setVisibility(View.GONE);
					
					
					View p = ((View)findViewById(R.id.product_view));
					p.setVisibility(View.VISIBLE);
					
					ImageView image = ((ImageView)findViewById(R.id.product_image));
					
					imageManager.fetchDrawableOnThread(prod.image_url, image);
					
				
			
				} else if (resultCode == KwikAPIService.STATUS_CONNECTION_ERROR) {
					Log.d(TAG, "Connection error.");
					Toast.makeText(self, getResources().getString(R.string.HTML_error), Toast.LENGTH_SHORT).show();
				} else {
					Log.d(TAG, "Unknown error.");
					Toast.makeText(self, getResources().getString(R.string.API_bad_response), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		
		startService(intent);
	}

}
