package com.mbkandra16.apps.gridimagesearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mbkandra16.apps.gridimagesearch.R;
import com.mbkandra16.apps.gridimagesearch.models.SearchFilters;

public class SearchFiltersActivity extends Activity {
	Spinner spImageSize;
	Spinner spColorFilter;
	Spinner spImageType;
	EditText etSiteFilter;
	Button bSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_filters);

		SearchFilters prevFilters = (SearchFilters) getIntent()
				.getSerializableExtra("currentFilters");

		setupReferences();

		// Set the filters to existing values
		spImageSize.setSelection(getIndex(spImageSize, prevFilters.imageSize));
		spColorFilter.setSelection(getIndex(spColorFilter,
				prevFilters.colorFilter));
		spImageType.setSelection(getIndex(spImageType, prevFilters.imageType));
		etSiteFilter.setText(prevFilters.siteFilter);
		etSiteFilter.setSelection(etSiteFilter.getText().length());

		bSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SearchFilters filters = new SearchFilters();
				filters.imageSize = String.valueOf(spImageSize
						.getSelectedItem());
				filters.colorFilter = String.valueOf(spColorFilter
						.getSelectedItem());
				filters.imageType = String.valueOf(spImageType
						.getSelectedItem());
				filters.siteFilter = etSiteFilter.getText().toString();

				// Pass the expense as a result
				Intent data = new Intent();
				// Add data
				data.putExtra("filters", filters);

				setResult(RESULT_OK, data);
				// Dismiss form
				finish();
			}
		});
	}

	private int getIndex(Spinner spinner, String myString) {

		int index = 0;

		for (int i = 0; i < spinner.getCount(); i++) {
			if (spinner.getItemAtPosition(i).equals(myString)) {
				index = i;
			}
		}
		return index;
	}

	private void setupReferences() {
		spImageSize = (Spinner) findViewById(R.id.spImageSize);
		spColorFilter = (Spinner) findViewById(R.id.spColorFilter);
		spImageType = (Spinner) findViewById(R.id.spImageType);
		etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
		bSave = (Button) findViewById(R.id.bSave);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_filters, menu);
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
