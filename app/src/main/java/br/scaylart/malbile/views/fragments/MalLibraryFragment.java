package br.scaylart.malbile.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.MalbileManager.TaskJob;
import br.scaylart.malbile.views.adapters.pager.MalPagerAdapter;
import br.scaylart.malbile.views.widget.SlidingTabLayout;

public class MalLibraryFragment extends Fragment {
    public static final String TAG = LibraryFragment.class.getSimpleName();

    public static final String USERNAME_ARGUMENT_KEY = TAG + ":" + "UsernameArgumentKey";
    public static final String TASKJOB_ARGUMENT_KEY = TAG + ":" + "TaskJobArgumentKey";

    public TaskJob taskJob = TaskJob.MOSTPOPULAR;
    public String username;

    private ViewPager mViewPager;
    private SlidingTabLayout mTabs;

    MalPagerAdapter mMalPagerAdapter;

    Context context;

    public MalLibraryFragment() {
        // Required empty public constructor
    }

    public static MalLibraryFragment newInstance(TaskJob taskJob, String username) {
        MalLibraryFragment newInstance = new MalLibraryFragment();

        Bundle arguments = new Bundle();
        arguments.putString(TASKJOB_ARGUMENT_KEY, taskJob.name());
        arguments.putString(USERNAME_ARGUMENT_KEY, username);
        newInstance.setArguments(arguments);

        return newInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            handleInitialArguments(getArguments());
        }

        initializeToolbar();
        initializeViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabs = (SlidingTabLayout) view.findViewById(R.id.tabs);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMalPagerAdapter = null;
    }

    public void handleInitialArguments(Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(TASKJOB_ARGUMENT_KEY)) {
                taskJob = TaskJob.valueOf(arguments.getString(TASKJOB_ARGUMENT_KEY));
                arguments.remove(TASKJOB_ARGUMENT_KEY);
            }
            if (arguments.containsKey(USERNAME_ARGUMENT_KEY)) {
                username = arguments.getString(USERNAME_ARGUMENT_KEY);
                arguments.remove(USERNAME_ARGUMENT_KEY);
            }
        }
    }

    public void initializeViews() {
        mMalPagerAdapter = new MalPagerAdapter(getFragmentManager(), taskJob, username);

        mViewPager.setAdapter(mMalPagerAdapter);

        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accentPinkA200);
            }
        });
        mTabs.setViewPager(mViewPager);
    }

    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(TASKJOB_ARGUMENT_KEY)) {
            taskJob = TaskJob.valueOf(savedState.getString(TASKJOB_ARGUMENT_KEY));
            savedState.remove(TASKJOB_ARGUMENT_KEY);
        }
        if (savedState.containsKey(USERNAME_ARGUMENT_KEY)) {
            username = savedState.getString(USERNAME_ARGUMENT_KEY);
            savedState.remove(USERNAME_ARGUMENT_KEY);
        }
    }

    public void initializeToolbar() {
        if (getActivity() instanceof ActionBarActivity) {
            int title = R.string.app_name;
            switch (taskJob) {
                case UPCOMING:
                    title = R.string.fragment_upcoming;
                    break;
                case MOSTPOPULAR:
                    title = R.string.fragment_most_popular;
                    break;
                case TOPRATED:
                    title = R.string.fragment_top_rated;
                    break;
                case JUSTADDED:
                    title = R.string.fragment_just_added;
                    break;
                case SEARCH:
                    title = R.string.fragment_search;
                    break;
            }
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(title);
            ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (taskJob != null) {
            outState.putString(TASKJOB_ARGUMENT_KEY, taskJob.name());
        }
        if (username != null) {
            outState.putString(USERNAME_ARGUMENT_KEY, username);
        }
    }
}
