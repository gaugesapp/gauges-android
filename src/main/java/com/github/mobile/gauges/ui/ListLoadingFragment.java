package com.github.mobile.gauges.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.ListAdapter;

import com.github.mobile.gauges.R;

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
		getLoaderManager().initLoader(0, null, this);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu.findItem(R.id.refresh)==null) {
            inflater.inflate(R.menu.refresh_menu_item, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return super.onOptionsItemSelected(item); // there may be other fragments listening to refresh
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	/**
	 * Refresh the fragment's list
	 */
    public void refresh() {
        Log.d(getClass().getSimpleName(), "refresh() called");
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
