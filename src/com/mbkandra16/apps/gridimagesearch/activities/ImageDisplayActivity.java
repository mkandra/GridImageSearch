package com.mbkandra16.apps.gridimagesearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import com.mbkandra16.apps.gridimagesearch.R;
import com.mbkandra16.apps.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageDisplayActivity extends Activity {
	private ShareActionProvider miShareAction;
	private ImageView ivImageResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		// remove the actionbar on the image display activity
		// getActionBar().hide();
		// Pull out the url from the intent
		ImageResult result = (ImageResult) getIntent().getSerializableExtra(
				"result");
		// find the image view
		ivImageResult = (ImageView) findViewById(R.id.ivImageResult);
		// Load the image url into the imageview using picasso
		// Picasso.with(this).load(result.fullUrl).into(ivImageResult);
		// Load image async from remote URL, setup share when completed
		Picasso.with(this).load(result.fullUrl)
				.into(ivImageResult, new Callback() {
					@Override
					public void onSuccess() {
						// Setup share intent now that image has loaded
						setupShareIntent();
					}

					@Override
					public void onError() {
						// ...
					}
				});
	}

	// Gets the image URI and setup the associated share intent to hook into the
	// provider
	public void setupShareIntent() {
		Uri bmpUri = getLocalBitmapUri(ivImageResult); // see previous remote
														// images section
		// Create share intent as described above
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
		shareIntent.setType("image/*");
		// Attach share event to the menu item provider
		miShareAction.setShareIntent(shareIntent);
	}

	private Uri getLocalBitmapUri(ImageView ivImageResult2) {
		// Extract Bitmap from ImageView drawable
		Bitmap bmp = ((BitmapDrawable) ivImageResult.getDrawable()).getBitmap();

		String path = MediaStore.Images.Media.insertImage(getContentResolver(),
				bmp, "Image Description", null);

		Uri uri = Uri.parse(path);
		return uri;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_display, menu);
		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.menu_item_share);
		// Fetch reference to the share action provider
		miShareAction = (ShareActionProvider) item.getActionProvider();
		// Return true to display menu
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
