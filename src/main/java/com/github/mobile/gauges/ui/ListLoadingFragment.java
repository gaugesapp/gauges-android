package com.github.mobile.gauges.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.ListAdapter;

import java.util.List;

import roboguice.fragment.RoboListFragment;

/**
 * List loading fragment for a specific type
 * 
 * @param <E>
 */
public abstract class ListLoadingFragment<E> extends RoboListFragment implements
		LoaderCallbacks<List<E>> {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListShown(false);
		refresh();
	}

	/**
	 * Refresh the fragment's list
	 */
	public void refresh() {
		getLoaderManager().restartLoader(0, null, this);
	}

	public void onLoadFinished(Loader<List<E>> loader, List<E> items) {
		setListAdapter(adapterFor(items));

		if (isResumed())
			setListShown(true);
		else
			setListShownNoAnimation(true);
	}

	/**
	 * Create adapter for list of items
	 * 
	 * @param items
	 * @return list adapter
	 */
	protected abstract ListAdapter adapterFor(List<E> items);

	@Override
	public void onLoaderReset(Loader<List<E>> listLoader) {
	}
}
