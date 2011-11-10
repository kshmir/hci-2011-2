package kwik.services;

import java.io.Serializable;
import java.util.List;

import kwik.remote.api.AbstractCategory;
import kwik.remote.api.Category;
import kwik.remote.api.Product;
import kwik.remote.api.SubCategory;
import kwik.remote.api.exceptions.APIBadResponseException;
import kwik.remote.api.exceptions.HTTPException;
import kwik.remote.api.exceptions.XMLParseException;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class KwikAPIService extends IntentService {
	
	private String TAG = getClass().getSimpleName();

	public static final String GET_CATEGORIES_CMD = "GetCategories";
	public static final String GET_SUBCATEGORIES_CMD = "GetSubCategories";
	public static final String GET_CAT_PRODUCTS_CMD = "GetProductsByCategories";
	public static final String GET_SUBCAT_PRODUCTS_CMD = "GetProductsBySubCategories";
	public static final String GET_PRODUCT_CMD = "GetProductByID";

	public static final int STATUS_CONNECTION_ERROR = -1;
	public static final int STATUS_ERROR = -2;
	public static final int STATUS_ILLEGAL_ARGUMENT = -3;
	public static final int STATUS_OK = 0;

	/*
	 * Se debe crear un constructor sin parametros y asignarle un nombre al
	 * servicio.
	 */
	public KwikAPIService() {
		super("GetCategoriesService");
	}

	/*
	 * Evento que se ejecuta cuando se invocó el servicio por medio de un
	 * Intent.
	 */
	@Override
	protected void onHandleIntent(final Intent intent) {
		final ResultReceiver receiver = intent.getParcelableExtra("receiver");
		final String command = intent.getStringExtra("command");
		final Integer category_id    = intent.getIntExtra("category_id", -1);
		final Integer subcategory_id = intent.getIntExtra("subcategory_id", -1);
		final Integer product_id     = intent.getIntExtra("product_id", -1);

		final Bundle b = new Bundle();
		if (command.equals(GET_CATEGORIES_CMD)) {
			getCategories(receiver, b);
		} else if (command.equals(GET_SUBCATEGORIES_CMD)) {
			getSubCategories(receiver, b, category_id);
		} else if (command.equals(GET_CAT_PRODUCTS_CMD)) {
			getProductsByCategory(receiver, b, category_id);
		} else if (command.equals(GET_SUBCAT_PRODUCTS_CMD)) {
			getProductsBySubCategory(receiver, b, category_id, subcategory_id);
		} else if (command.equals(GET_PRODUCT_CMD)) {
			getProductByID(receiver, b, product_id);
		}


		// Es importante terminar el servicio lo antes posible.
		this.stopSelf();
	}

	private void getProductByID(ResultReceiver receiver, Bundle b, int product_id)  {

		Product p = null;
		
		try {			
			p = Product.getProduct(product_id);
		} catch (APIBadResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HTTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		b.putSerializable("return", (Serializable) p);
		receiver.send(STATUS_OK, b);
	}
	
	private void getProductsByCategory(ResultReceiver receiver, Bundle b, int category_id)  {

		List<Product> products = null;
		
		try {
			Category c = new Category();
			c.id = category_id;
			
			products = c.getProducts(1);
		} catch (APIBadResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HTTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		b.putSerializable("return", (Serializable) products);
		receiver.send(STATUS_OK, b);
	}
	
	private void getProductsBySubCategory(ResultReceiver receiver, Bundle b, int category_id, int subcategory_id)  {

		List<Product> products = null;
		
		try {
			SubCategory c = new SubCategory();
			c.id = subcategory_id;
			c.category_id = category_id;
			
			products = c.getProducts(1);
		} catch (APIBadResponseException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
		} catch (XMLParseException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
		} catch (HTTPException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());
		}
		
		b.putSerializable("return", (Serializable) products);
		receiver.send(STATUS_OK, b);
	}

	private void getSubCategories(ResultReceiver receiver, Bundle b, int category_id)  {

		List<? extends AbstractCategory> subCategories = null;
		
		try {
			Category c = new Category();
			c.id = category_id;
			
			subCategories = c.getSubCategoryList(1);
		} catch (APIBadResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HTTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		b.putSerializable("return", (Serializable) subCategories);
		receiver.send(STATUS_OK, b);
	}
	
	private void getCategories(ResultReceiver receiver, Bundle b)  {

		List<Category> categories = null;
		
		try {
			categories = Category.getCategoryList(1);
		} catch (APIBadResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HTTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		b.putSerializable("return", (Serializable) categories);
		receiver.send(STATUS_OK, b);
	}


}