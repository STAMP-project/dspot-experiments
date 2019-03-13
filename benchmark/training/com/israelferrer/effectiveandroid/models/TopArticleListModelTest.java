package com.israelferrer.effectiveandroid.models;


import com.israelferrer.effectiveandroid.BuildConfig;
import com.israelferrer.effectiveandroid.entities.Article;
import com.israelferrer.effectiveandroid.service.CustomApiClient;
import com.israelferrer.effectiveandroid.service.TimelineService;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


/**
 * Created by icamacho on 8/15/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TopArticleListModelTest {
    private static final TwitterException ANY_EXCEPTION = new TwitterException("Random Exception");

    private static final long TWEET_ID = 582295217115541505L;

    @Mock
    private CustomApiClient client;

    @Mock
    private Callback<List<Article>> callback;

    @Mock
    private TimelineService timelineService;

    @Captor
    private ArgumentCaptor<Callback<List<Tweet>>> callbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<Result<List<Article>>> articleArgumentCaptor;

    private TopArticleListModelImpl model;

    @Test
    public void testGetMostRtArticles_failure() throws Exception {
        model.getMostRtArticles(callback);
        Mockito.verify(timelineService).homeTimeline(ArgumentMatchers.eq(200), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true), callbackArgumentCaptor.capture());
        Callback<List<Tweet>> callbackApi = callbackArgumentCaptor.getValue();
        callbackApi.failure(TopArticleListModelTest.ANY_EXCEPTION);
        Mockito.verify(callback).failure(TopArticleListModelTest.ANY_EXCEPTION);
    }

    @Test
    public void testGetMostRtArticles_success() throws Exception {
        model.getMostRtArticles(callback);
        Mockito.verify(timelineService).homeTimeline(ArgumentMatchers.eq(200), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true), callbackArgumentCaptor.capture());
        Callback<List<Tweet>> callbackApi = callbackArgumentCaptor.getValue();
        callbackApi.success(new Result(createTweets(), null));
        List<Article> expectedArticles = new ArrayList<>();
        expectedArticles.add(Article.create(TopArticleListModelTest.createUrlTweet(TopArticleListModelTest.TWEET_ID, null, "", "", "israelferrer.com")));
        Mockito.verify(callback).success(articleArgumentCaptor.capture());
        Result<List<Article>> articles = articleArgumentCaptor.getValue();
        Assert.assertEquals(articles.data, expectedArticles);
    }
}

