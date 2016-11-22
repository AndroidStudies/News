package org.attentiveness.news.list;


import org.attentiveness.news.base.DefaultSubscriber;
import org.attentiveness.news.data.News;

import java.util.List;

class NewsListPresenter implements NewsListContract.Presenter {

    private NewsListContract.View mView;
    private NewsListUseCase mUseCase;
    private boolean mFirstLoad = true;

    NewsListPresenter(NewsListContract.View view) {
        this.mView = view;
        this.mUseCase = new NewsListUseCase(view.getContext());
    }

    @Override
    public void getTotalPage(String channelId) {
        mUseCase.getTotalPage(channelId, new GetTotalPageSubscriber());
    }

    @Override
    public void getNewsList(boolean forceRefresh, String channelId, int currentPage, int needContent, int needHtml) {
        mView.showLoading();
        if (forceRefresh || mFirstLoad) {
            mUseCase.refresh();
            mUseCase.getNewsList(channelId, currentPage, needContent, needHtml, new FirstPageSubscriber());
        } else {
            mUseCase.getNewsList(channelId, currentPage, needContent, needHtml, new AppendPageSubscriber());
        }
        mFirstLoad = false;
    }

    @Override
    public void destroy() {
        mUseCase.unsubscribe();
    }

    private class GetTotalPageSubscriber extends DefaultSubscriber<Integer> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mView.showError(e.getMessage());
        }

        @Override
        public void onNext(Integer totalPage) {
            mView.saveTotalPage(totalPage);
        }
    }


    private class FirstPageSubscriber extends DefaultSubscriber<List<News>> {
        @Override
        public void onCompleted() {
            mView.hideLoading();
            mView.hideRetry();
        }

        @Override
        public void onError(Throwable e) {
            mView.hideLoading();
            mView.showRetry();
            mView.showError(e.getMessage());
        }

        @Override
        public void onNext(List<News> newsList) {
            mView.renderFirstPage(newsList);
        }
    }

    private class AppendPageSubscriber extends DefaultSubscriber<List<News>> {
        @Override
        public void onCompleted() {
            mView.hideLoading();
            mView.hideRetry();
        }

        @Override
        public void onError(Throwable e) {
            mView.hideLoading();
            mView.showRetry();
            mView.showError(e.getMessage());
        }

        @Override
        public void onNext(List<News> newsList) {
            mView.appendPage(newsList);
        }
    }

}
