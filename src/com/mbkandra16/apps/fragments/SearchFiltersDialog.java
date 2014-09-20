package com.mbkandra16.apps.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mbkandra16.apps.gridimagesearch.R;
import com.mbkandra16.apps.gridimagesearch.models.SearchFilters;

public class SearchFiltersDialog extends DialogFragment {
	private Spinner spImageSize;
	private Spinner spColorFilter;
	private Spinner spImageType;
	private EditText etSiteFilter;
	private Button bSave;
	SearchFilters filters;

	public SearchFiltersDialog() {
		// Empty constructor required for DialogFragment
	}

	public static SearchFiltersDialog newInstance(String title,
			SearchFilters filters) {
		SearchFiltersDialog frag = new SearchFiltersDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putSerializable("currentFilters", filters);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_search_filters,
				container);

		String title = getArguments().getString("title");
		getDialog().setTitle(title);

		SearchFilters prevFilters = (SearchFilters) getArguments()
				.getSerializable("currentFilters");

		setupReferences(view);

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
				filters = new SearchFilters();
				filters.imageSize = String.valueOf(spImageSize
						.getSelectedItem());
				filters.colorFilter = String.valueOf(spColorFilter
						.getSelectedItem());
				filters.imageType = String.valueOf(spImageType
						.getSelectedItem());
				filters.siteFilter = etSiteFilter.getText().toString();

				getDialog().dismiss();
			}
		});

		return view;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		SearchFiltersDialogListener activity = (SearchFiltersDialogListener) getActivity();
		activity.onFinishFiltersDialog(filters);
		this.dismiss();
	}

	public interface SearchFiltersDialogListener {
		void onFinishFiltersDialog(SearchFilters filters);
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

	private void setupReferences(View v) {
		spImageSize = (Spinner) v.findViewById(R.id.spImageSize);
		spColorFilter = (Spinner) v.findViewById(R.id.spColorFilter);
		spImageType = (Spinner) v.findViewById(R.id.spImageType);
		etSiteFilter = (EditText) v.findViewById(R.id.etSiteFilter);
		bSave = (Button) v.findViewById(R.id.bSave);

	}

}
