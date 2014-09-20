package com.mbkandra16.apps.gridimagesearch.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mbkandra16.apps.gridimagesearch.R;
import com.mbkandra16.apps.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {

	public ImageResultsAdapter(Context context, List<ImageResult> images) {
		super(context, R.layout.item_image_result, images);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		ImageResult imageInfo = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_image_result, parent, false);
		}
		// Lookup view for data population
		final ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
		// TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
		// Clear out image from last time
		ivImage.setImageResource(0);
		// Populate the data into the template view using the data object
		// tvTitle.setText(Html.fromHtml(imageInfo.title));

		Transformation transformation_thumb = new Transformation() {

			@Override
			public String key() {
				// TODO Auto-generated method stub
				return "photo";
			}

			@Override
			public Bitmap transform(Bitmap arg0) {
				DisplayMetrics displayMetrics = getContext().getResources()
						.getDisplayMetrics();
				int deviceWidth = displayMetrics.widthPixels;
				int columns_padding = 12;
				int targetWidth = (deviceWidth - columns_padding) / 3;
				
				double aspectRatio = (double) arg0.getHeight()
						/ (double) arg0.getWidth();
				int targetHeight = (int) (targetWidth * aspectRatio);
				ivImage.getLayoutParams().height = targetHeight;
				ivImage.getLayoutParams().width = targetWidth;
				Bitmap scaled = Bitmap.createScaledBitmap(arg0, targetWidth,
						targetHeight, false);
				if (scaled != arg0) {
					arg0.recycle();
				}

				return scaled;
			}
		};
		Picasso.with(getContext()).load(imageInfo.thumbUrl).into(ivImage);
		//Picasso.with(getContext()).load(imageInfo.thumbUrl)
			//	.transform(transformation_thumb).into(ivImage);
		// Return the completed view to render on screen
		return convertView;
	}

}
