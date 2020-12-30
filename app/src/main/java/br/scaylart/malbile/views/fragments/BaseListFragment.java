package br.scaylart.malbile.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import br.scaylart.malbile.R;
import br.scaylart.malbile.presenters.listeners.LibraryPresenter;
import br.scaylart.malbile.presenters.mapper.LibraryMapper;
import br.scaylart.malbile.views.listeners.LibraryView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseListFragment extends Fragment implements LibraryView, LibraryMapper {
    protected LibraryPresenter mLibraryPresenter;

    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager;
    protected RelativeLayout mEmptyRelativeLayout;

    @InjectView(R.id.listEdit) protected FloatingActionButton mFloatingBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View libraryView = inflater.inflate(R.layout.fragment_library_list, container, false);
        ButterKnife.inject(this, libraryView);

        mRecyclerView = (RecyclerView) libraryView.findViewById(R.id.listView);
        mEmptyRelativeLayout = (RelativeLayout) libraryView.findViewById(R.id.emptyRelativeLayout);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        setFloatButtonVisibility();

        return libraryView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLibraryPresenter.restoreState(savedInstanceState);
        } else {
            mLibraryPresenter.handleInitialArguments(getArguments());
        }

        mLibraryPresenter.initializeViews();

        initializeAbsListView();

        mLibraryPresenter.initializeSearch();

        mLibraryPresenter.initializeData();
    }

    protected void setFloatButtonVisibility() {
        mFloatingBtn.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();

        mLibraryPresenter.registerForEvents();
    }

    @Override
    public void onStop() {
        mLibraryPresenter.unregisterForEvents();

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLibraryPresenter.destroyAllSubscriptions();
        mLibraryPresenter.releaseAllResources();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mLibraryPresenter.saveState(outState);
    }

    // LibraryView:

    @Override
    public void initializeAbsListView() {
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
                    mLibraryPresenter.onRecordClick(recyclerView.getChildPosition(child));
                    return true;
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }
        });
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
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.emptyTextView)).setText(R.string.no_library);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.instructionsTextView)).setText(R.string.library_instructions);
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

    // LibraryMapper:

    @Override
    public void setPositionState(Parcelable state) {
        if (mLayoutManager != null) {
            mLayoutManager.onRestoreInstanceState(state);
        }
    }

    @Override
    public void registerAdapter(RecyclerView.Adapter adapter) {
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void initializeToolbar() {

    }
}
