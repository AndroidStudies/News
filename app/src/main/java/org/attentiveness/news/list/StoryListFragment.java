package org.attentiveness.news.list;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.attentiveness.news.R;
import org.attentiveness.news.base.BaseFragment;
import org.attentiveness.news.data.Story;
import org.attentiveness.news.detail.StoryDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * News list fragment.
 */
public class StoryListFragment extends BaseFragment implements StoryListContract.View, StoryListAdapter.OnItemClickListener {

    public static final String EXTRA_ID = "id";

    @BindView(R.id.rv_news_list)
    RecyclerView mNewsListView;
    @BindView(R.id.rl_swipe_fresh_view)
    ScrollChildSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_loading_error)
    TextView mLoadingErrorView;

    private StoryListContract.Presenter mPresenter;
    private StoryListAdapter mNewsListAdapter;

    public static StoryListFragment newInstance() {
        return new StoryListFragment();
    }

    public StoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mNewsListAdapter = new StoryListAdapter();
        this.mNewsListAdapter.setOnItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_story_list, container, false);
        ButterKnife.bind(this, rootView);

        this.mNewsListView.setAdapter(mNewsListAdapter);
        this.mNewsListView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        this.mSwipeRefreshLayout.setChildView(this.mNewsListView);
        this.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadNewsList(false);
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void setPresenter(@NonNull StoryListContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mPresenter.unsubscribe();
    }


    @Override
    public void showRetry() {
        this.mLoadingErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRetry() {
        this.mLoadingErrorView.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        showMessage(this.mSwipeRefreshLayout, message);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }

        // Make sure setRefreshing() is called after the layout is done with everything else.
        this.mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void showStoryList(List<Story> storyList) {
        this.mNewsListView.setVisibility(View.VISIBLE);
        this.mLoadingErrorView.setVisibility(View.GONE);
        this.mNewsListAdapter.setItemList(storyList);
    }

    @Override
    public void showNewsMarkedRead() {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onStoryClicked(Story story) {
        if (story != null && story.getId() > 0) {
            Intent intent = new Intent(this.getActivity(), StoryDetailActivity.class);
            intent.putExtra(EXTRA_ID, story.getId());
            startActivity(intent);
        }
    }
}