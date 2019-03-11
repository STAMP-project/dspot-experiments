package com.apollographql.apollo;


import Episode.EMPIRE;
import EpisodeHeroNameQuery.Data;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.apollographql.apollo.integration.normalizer.EpisodeHeroNameQuery;
import com.apollographql.apollo.rx2.Rx2Apollo;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;


public class AsyncNormalizedCacheTestCase {
    private ApolloClient apolloClient;

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void testAsync() throws ApolloException, IOException, InterruptedException {
        EpisodeHeroNameQuery query = EpisodeHeroNameQuery.builder().episode(EMPIRE).build();
        for (int i = 0; i < 500; i++) {
            server.enqueue(mockResponse("HeroNameResponse.json"));
        }
        List<Observable<Response<EpisodeHeroNameQuery.Data>>> calls = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ApolloQueryCall<EpisodeHeroNameQuery.Data> queryCall = apolloClient.query(query).responseFetcher(((i % 2) == 0 ? ApolloResponseFetchers.NETWORK_FIRST : ApolloResponseFetchers.CACHE_ONLY));
            calls.add(Rx2Apollo.from(queryCall));
        }
        TestObserver<Response<EpisodeHeroNameQuery.Data>> observer = new TestObserver();
        Observable.merge(calls).subscribe(observer);
        observer.awaitTerminalEvent();
        observer.assertNoErrors();
        observer.assertValueCount(1000);
        observer.assertNever(new io.reactivex.functions.Predicate<Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(Response<EpisodeHeroNameQuery.Data> dataResponse) throws Exception {
                return dataResponse.hasErrors();
            }
        });
    }
}

