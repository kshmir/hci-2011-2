package kwik.app.activities;

import java.util.HashMap;
import java.util.List;

import kwik.app.R;
import kwik.app.activities.custom.KwikFragmentActivity;
import kwik.remote.api.Category;
import kwik.services.KwikAPIService;
import kwik.util.Util;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ProductsActivity extends KwikFragmentActivity implements OnItemClickListener {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent localIntent = this.getIntent();
		
		Bundle extras = localIntent.getExtras();
		final Integer category_id = extras.getInt("category_id", -1);
		final Integer subcategory_id = extras.getInt("subcategory_id", -1);
		final String category_name = extras.getString("category_name");
		
		if (category_name != null) {
			this.setTitle(String.format(getResources().getString(R.string.products_title), category_name));
		} else {
			this.setTitle(String.format(getResources().getString(R.string.search_result), category_name));
		}
		
		/* Asociamos la vista del search list con la activity */
		setContentView(R.layout.item_list);
		
		Intent intent = new Intent(Intent.ACTION_SYNC, null, this, KwikAPIService.class);
		
		if (Intent.ACTION_SEARCH.equals(localIntent.getAction())) {
			String query = localIntent.getStringExtra(SearchManager.QUERY);
			intent.putExtra("command", KwikAPIService.GET_PRODUCTS_CMD);
			intent.putExtra("criteria", query);
		} else if (subcategory_id != -1) {
			intent.putExtra("command", KwikAPIService.GET_SUBCAT_PRODUCTS_CMD);
			intent.putExtra("category_id", category_id);
			intent.putExtra("subcategory_id", subcategory_id);
		} else {
			intent.putExtra("command", KwikAPIService.GET_CAT_PRODUCTS_CMD);
			intent.putExtra("category_id", category_id);
		}
		
		intent.putExtra("receiver", new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				super.onReceiveResult(resultCode, resultData);
				if (resultCode == KwikAPIService.STATUS_OK) {
					boolean no_data_fetched = false;
					
					@SuppressWarnings("unchecked")
					List<Category> prodList = (List<Category>) resultData.getSerializable("return");
					populateProdList(prodList);
					no_data_fetched = prodList.size() == 0;
					if (!no_data_fetched) {
						ListView vi = (ListView) findViewById(R.id.listview);
						vi.setVisibility(View.VISIBLE);
					} else {
						View vi = (View) findViewById(R.id.textview);
						vi.setVisibility(View.VISIBLE);
					}
					View pg = (View) findViewById(R.id.progressbar);
					pg.setVisibility(View.GONE);
				}
			}
		});
		
		ListView vi = (ListView) findViewById(R.id.listview);
		
		vi.setOnItemClickListener(this);
		startService(intent);
	}
	
	private void populateProdList(List<Category> products) {
		String[] map_fields = { "name", "id" };
		String[] desired_fields = { "name" };
		
		ListAdapter adapter = new SimpleAdapter(this, Util.getMapped(products, map_fields), R.layout.item_list_item,
				desired_fields, new int[] { R.id.title });
		ListView vi = (ListView) findViewById(R.id.listview);
		vi.setAdapter(adapter);
	}
	
	@Override
	public void onItemClick(AdapterView<?> view, View v, int position, long arg3) {
		ListView vi = (ListView) view;
		@SuppressWarnings("unchecked")
		HashMap<String, Object> map = (HashMap<String, Object>) vi.getItemAtPosition(position);
		
		final Integer product_id = (Integer) map.get("id");
		final String product_name = (String) map.get("name");
		
		Intent intent = new Intent(v.getContext(), ProductActivity.class);
		intent.putExtra("product_id", product_id);
		intent.putExtra("product_name", product_name);
		startActivityForResult(intent, 0);
		
	}
	
}
