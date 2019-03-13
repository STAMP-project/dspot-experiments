package com.israelferrer.effectiveandroid.presenters;


import com.israelferrer.effectiveandroid.BuildConfig;
import com.israelferrer.effectiveandroid.entities.Article;
import com.israelferrer.effectiveandroid.models.TopArticleListModel;
import com.israelferrer.effectiveandroid.ui.views.TopArticleListView;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


/**
 * Created by icamacho on 8/15/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TopArticleListPresenterTest {
    private static final Result<List<Article>> ANY_RESULT = new Result(Collections.<Article>emptyList(), null);

    private static final TwitterException ANY_EXCEPTION = new TwitterException("Random Exception");

    private ArgumentCaptor<Callback> callbackCaptor;

    private TopArticleListPresenterImpl presenter;

    private TopArticleListModel model;

    private TopArticleListView view;

    @SuppressWarnings("unchecked")
    @Test
    public void testCreate_success() throws Exception {
        presenter.create();
        Mockito.verify(model).getMostRtArticles(callbackCaptor.capture());
        Callback<List<Article>> callback = callbackCaptor.getValue();
        callback.success(TopArticleListPresenterTest.ANY_RESULT);
        Mockito.verify(view).setArticles(TopArticleListPresenterTest.ANY_RESULT.data);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreate_failure() throws Exception {
        presenter.create();
        Mockito.verify(model).getMostRtArticles(callbackCaptor.capture());
        Callback<List<Article>> callback = callbackCaptor.getValue();
        callback.failure(TopArticleListPresenterTest.ANY_EXCEPTION);
        Mockito.verify(view).logout();
    }
}

