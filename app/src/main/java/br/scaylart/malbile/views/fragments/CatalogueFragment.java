package br.scaylart.malbile.views.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import br.scaylart.malbile.MalbileApplication;
import br.scaylart.malbile.R;
import br.scaylart.malbile.presenters.CataloguePresenterImpl;
import br.scaylart.malbile.presenters.listeners.CataloguePresenter;
import br.scaylart.malbile.presenters.mapper.CatalogueMapper;
import br.scaylart.malbile.views.listeners.CatalogueView;

public class CatalogueFragment extends Fragment implements CatalogueView, CatalogueMapper {
    public static final String TAG = CatalogueFragment.class.getSimpleName();

    private CataloguePresenter mCataloguePresenter;

    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private RelativeLayout mEmptyRelativeLayout;
    private FloatingActionButton mPreviousButton;
    private FloatingActionButton mNextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mCataloguePresenter = new CataloguePresenterImpl(this, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View catalogueView = inflater.inflate(R.layout.fragment_catalogue, container, false);

        mRecyclerView = (RecyclerView) catalogueView.findViewById(R.id.gridView);
        mEmptyRelativeLayout = (RelativeLayout) catalogueView.findViewById(R.id.emptyRelativeLayout);
        mPreviousButton = (FloatingActionButton) catalogueView.findViewById(R.id.previousButton);
        mNextButton = (FloatingActionButton) catalogueView.findViewById(R.id.nextButton);

        mLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return catalogueView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mCataloguePresenter.restoreState(savedInstanceState);
        }

        mCataloguePresenter.initializeViews();

        mCataloguePresenter.initializeSearch();

        mCataloguePresenter.initializeDataFromPreferenceSource();
    }

    @Override
    public void onStart() {
        super.onStart();

        mCataloguePresenter.registerForEvents();
    }

    @Override
    public void onStop() {
        mCataloguePresenter.unregisterForEvents();

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mCataloguePresenter.destroyAllSubscriptions();
        mCataloguePresenter.releaseAllResources();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mCataloguePresenter.saveState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_catalogue, menu);
        final SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                InputMethodManager searchKeyboard = (InputMethodManager) MalbileApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                searchKeyboard.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mCataloguePresenter.onQueryTextChange(query);

                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                mCataloguePresenter.onOptionFilter();
                return true;
            case R.id.action_to_top:
                mCataloguePresenter.onOptionToTop();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void registerAdapter(RecyclerView.Adapter adapter) {
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void initializeButtons() {
        if (mPreviousButton != null) {
            mPreviousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCataloguePresenter.onPreviousClick();
                }
            });
        }
        if (mNextButton != null) {
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCataloguePresenter.onNextClick();
                }
            });
        }
        if (mRecyclerView != null) {
            if (mPreviousButton != null && mNextButton != null) {
                mRecyclerView.setOnScrollListener(new FloatingActionButtonsOnScrollListenerImpl());
            }

        }
    }

    @Override
    public void setSubtitlePositionText(int position) {
        ActionBar supportActionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();

        if (supportActionBar != null) {
            if (mRecyclerView.getAdapter() != null) {
                supportActionBar.setSubtitle(getString(R.string.catalogue_subtitle_page) + " " + position);
            }
        }
    }

    @Override
    public void toastNoPreviousPage() {
        Toast.makeText(getActivity(), R.string.toast_no_previous_page, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastNoNextPage() {
        Toast.makeText(getActivity(), R.string.toast_no_next_page, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initializeAbsListView() {
        if (mRecyclerView != null) {
            final GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
            mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                        mCataloguePresenter.onMangaClick(recyclerView.getChildPosition(child));
                        return true;
                    }

                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                }
            });
        }
    }

    @Override
    public void scrollToTop() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void initializeEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            ((ImageView) mEmptyRelativeLayout.findViewById(R.id.emptyImageView)).setImageResource(R.drawable.ic_photo_library_white_48dp);
            ((ImageView) mEmptyRelativeLayout.findViewById(R.id.emptyImageView)).setColorFilter(getResources().getColor(R.color.accentPinkA200), PorterDuff.Mode.MULTIPLY);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.emptyTextView)).setText(R.string.no_catalogue);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.instructionsTextView)).setText(R.string.catalogue_instructions);
        }
    }

    @Override
    public void hideEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            mEmptyRelativeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void showEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            mEmptyRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Parcelable getPositionState() {
        if (mLayoutManager != null) {
            return mLayoutManager.onSaveInstanceState();
        } else {
            return null;
        }
    }

    @Override
    public void setPositionState(Parcelable state) {
        if (mLayoutManager != null) {
            mLayoutManager.onRestoreInstanceState(state);
        }
    }

    @Override
    public void initializeToolbar() {
        if (getActivity() instanceof ActionBarActivity) {
            ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(R.string.fragment_catalogue);
            ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(null);
        }
    }

    // OnScrollListener():

    private class FloatingActionButtonsOnScrollListenerImpl extends OnScrollListener {
        private int mLastScrollY;
        private int mPreviousFirstVisibleItem;
        private int mScrollThreshold;

        private int previousTotal = 0;
        private boolean loading = true;
        private int visibleThreshold = 5;
        int firstVisibleItem, visibleItemCount, totalItemCount;

        public FloatingActionButtonsOnScrollListenerImpl() {
            mScrollThreshold = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        }

        @Override
        public void onScrollStateChanged(RecyclerView view, int scrollState) {
            // Do Nothing.
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = mRecyclerView.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

            if(totalItemCount == 0) {
                return;
            }

            if (firstVisibleItem == mPreviousFirstVisibleItem) {
                int newScrollY = getTopItemScrollY();

                boolean isSignificantDelta = Math.abs(mLastScrollY - newScrollY) > mScrollThreshold;
                if (isSignificantDelta) {
                    if (mLastScrollY > newScrollY) {
                        hideFloatinActionButtons();
                    } else {
                        showFloatinActionButtons();
                    }
                }

                mLastScrollY = newScrollY;
            } else {
                if (firstVisibleItem > mPreviousFirstVisibleItem) {
                    hideFloatinActionButtons();
                } else {
                    showFloatinActionButtons();
                }

                mLastScrollY = getTopItemScrollY();

                mPreviousFirstVisibleItem = firstVisibleItem;
            }
        }

        private int getTopItemScrollY() {
            if (mRecyclerView == null || mRecyclerView.getChildAt(0) == null) {
                return 0;
            }

            View topChild = mRecyclerView.getChildAt(0);

            return topChild.getTop();
        }

        private void showFloatinActionButtons() {
            if (mPreviousButton != null && mNextButton != null) {
                mPreviousButton.show();
                mNextButton.show();
            }
        }

        private void hideFloatinActionButtons() {
            if (mPreviousButton != null && mNextButton != null) {
                mPreviousButton.hide();
                mNextButton.hide();
            }
        }
    }
}
