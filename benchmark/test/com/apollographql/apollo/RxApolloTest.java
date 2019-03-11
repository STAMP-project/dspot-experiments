package com.apollographql.apollo;


import EpisodeHeroNameQuery.Data;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.integration.normalizer.EpisodeHeroNameQuery;
import com.apollographql.apollo.rx.RxApollo;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import rx.Subscription;
import rx.functions.Action0;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;


public class RxApolloTest {
    private ApolloClient apolloClient;

    @Rule
    public final MockWebServer server = new MockWebServer();

    private static final String FILE_EPISODE_HERO_NAME_WITH_ID = "EpisodeHeroNameResponseWithId.json";

    private static final String FILE_EPISODE_HERO_NAME_CHANGE = "EpisodeHeroNameResponseNameChange.json";

    @Test
    public void callProducesValue() throws Exception {
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        TestSubscriber<Response<EpisodeHeroNameQuery.Data>> testSubscriber = new TestSubscriber();
        RxApollo.from(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE)))).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        assertThat(testSubscriber.getOnNextEvents().get(0).data().hero().name()).isEqualTo("R2-D2");
    }

    @Test
    public void callIsCanceledWhenUnsubscribe() throws Exception {
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        TestSubscriber<Response<EpisodeHeroNameQuery.Data>> testSubscriber = new TestSubscriber();
        TestScheduler scheduler = new TestScheduler();
        Subscription subscription = RxApollo.from(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE)))).observeOn(scheduler).subscribe(testSubscriber);
        subscription.unsubscribe();
        scheduler.triggerActions();
        testSubscriber.assertNotCompleted();
        testSubscriber.assertValueCount(0);
        testSubscriber.assertNoErrors();
        assertThat(testSubscriber.isUnsubscribed()).isTrue();
    }

    @Test
    public void prefetchCompletes() throws Exception {
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        TestSubscriber<Response<EpisodeHeroNameQuery.Data>> testSubscriber = new TestSubscriber();
        RxApollo.from(apolloClient.prefetch(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE)))).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(0);
        testSubscriber.assertCompleted();
    }

    @Test
    public void prefetchIsCanceledWhenDisposed() throws Exception {
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        final TestSubscriber<Void> testSubscriber = new TestSubscriber();
        TestScheduler scheduler = new TestScheduler();
        Subscription subscription = RxApollo.from(apolloClient.prefetch(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE)))).observeOn(scheduler).subscribe(new Action0() {
            @Override
            public void call() {
                testSubscriber.onCompleted();
            }
        }, new rx.functions.Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                testSubscriber.onError(throwable);
            }
        });
        subscription.unsubscribe();
        scheduler.triggerActions();
        testSubscriber.assertNotCompleted();
        testSubscriber.assertValueCount(0);
        testSubscriber.assertNoErrors();
        assertThat(subscription.isUnsubscribed()).isTrue();
    }

    @Test
    public void queryWatcherUpdatedSameQueryDifferentResults() throws Exception {
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_CHANGE));
        final TestSubscriber<Response<EpisodeHeroNameQuery.Data>> testSubscriber = new TestSubscriber();
        RxApollo.from(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).watcher()).subscribe(testSubscriber);
        apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).responseFetcher(NETWORK_ONLY).enqueue(null);
        testSubscriber.assertValueCount(2);
        assertThat(testSubscriber.getOnNextEvents().get(0).data().hero().name()).isEqualTo("R2-D2");
        assertThat(testSubscriber.getOnNextEvents().get(1).data().hero().name()).isEqualTo("Artoo");
    }

    @Test
    public void queryWatcherNotUpdatedSameQuerySameResults() throws Exception {
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        final TestSubscriber<Response<EpisodeHeroNameQuery.Data>> testSubscriber = new TestSubscriber();
        RxApollo.from(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).watcher()).subscribe(testSubscriber);
        apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).responseFetcher(NETWORK_ONLY);
        testSubscriber.assertValueCount(1);
        assertThat(testSubscriber.getOnNextEvents().get(0).data().hero().name()).isEqualTo("R2-D2");
    }

    @Test
    public void queryWatcherUpdatedDifferentQueryDifferentResults() throws Exception {
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        server.enqueue(Utils.mockResponse("HeroAndFriendsNameWithIdsNameChange.json"));
        final TestSubscriber<Response<EpisodeHeroNameQuery.Data>> testSubscriber = new TestSubscriber();
        RxApollo.from(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).watcher()).subscribe(testSubscriber);
        apolloClient.query(new com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).enqueue(null);
        testSubscriber.assertValueCount(2);
        assertThat(testSubscriber.getOnNextEvents().get(0).data().hero().name()).isEqualTo("R2-D2");
        assertThat(testSubscriber.getOnNextEvents().get(1).data().hero().name()).isEqualTo("Artoo");
    }

    @Test
    public void queryWatcherNotCalledWhenCanceled() throws Exception {
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        server.enqueue(Utils.mockResponse(RxApolloTest.FILE_EPISODE_HERO_NAME_CHANGE));
        final TestSubscriber<Response<EpisodeHeroNameQuery.Data>> testSubscriber = new TestSubscriber();
        TestScheduler scheduler = new TestScheduler();
        Subscription subscription = RxApollo.from(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).watcher()).observeOn(scheduler).subscribe(testSubscriber);
        scheduler.triggerActions();
        apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).responseFetcher(NETWORK_ONLY).enqueue(null);
        subscription.unsubscribe();
        scheduler.triggerActions();
        testSubscriber.assertValueCount(1);
        assertThat(testSubscriber.getOnNextEvents().get(0).data().hero().name()).isEqualTo("R2-D2");
    }
}

