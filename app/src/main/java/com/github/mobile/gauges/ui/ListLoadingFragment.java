package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.ui.ToastUtil.toastOnUiThread;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.menu;

import java.util.List;

import roboguice.fragment.RoboListFragment;

/**
 * List loading fragment for a specific type
 *
 * @param <E>
 */
public abstract class ListLoadingFragment<E> extends RoboListFragment implements LoaderCallbacks<List<E>> {

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
    public void onCreateOptionsMenu(Menu optionsMenu, MenuInflater inflater) {
        inflater.inflate(menu.gauges, optionsMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case id.refresh:
            refresh();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
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

    /**
     * Show message via a {@link Toast}
     * <p>
     * This method ensures the {@link Toast} is displayed on the UI thread and so it may be called from any thread
     *
     * @param message
     */
    protected void showError(final int message) {
        toastOnUiThread(getActivity(), getString(message));
    }
}
