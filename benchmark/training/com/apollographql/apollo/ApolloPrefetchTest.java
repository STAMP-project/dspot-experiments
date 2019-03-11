package com.apollographql.apollo;


import AllPlanetsQuery.Data;
import HttpCachePolicy.CACHE_ONLY;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.integration.httpcache.AllPlanetsQuery;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.io.InMemoryFileSystem;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;

import static com.apollographql.apollo.FaultyHttpCacheStore.FailStrategy.FAIL_BODY_WRITE;
import static com.apollographql.apollo.FaultyHttpCacheStore.FailStrategy.FAIL_HEADER_WRITE;


public class ApolloPrefetchTest {
    private ApolloClient apolloClient;

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Rule
    public final InMemoryFileSystem inMemoryFileSystem = new InMemoryFileSystem();

    Request lastHttRequest;

    Response lastHttResponse;

    private MockHttpCacheStore cacheStore;

    private OkHttpClient okHttpClient;

    @Test
    public void prefetchDefault() throws ApolloException, IOException {
        server.enqueue(Utils.mockResponse("HttpCacheTestAllPlanets.json"));
        ApolloPrefetchTest.prefetch(apolloClient.prefetch(new AllPlanetsQuery()));
        checkCachedResponse("HttpCacheTestAllPlanets.json");
        Utils.assertResponse(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(CACHE_ONLY.expireAfter(2, TimeUnit.SECONDS)), new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> dataResponse) throws Exception {
                return !(dataResponse.hasErrors());
            }
        });
    }

    @Test
    public void prefetchNoCacheStore() throws Exception {
        ApolloClient apolloClient = ApolloClient.builder().serverUrl(server.url("/")).okHttpClient(okHttpClient).dispatcher(Utils.immediateExecutor()).build();
        server.enqueue(Utils.mockResponse("HttpCacheTestAllPlanets.json"));
        ApolloPrefetchTest.prefetch(apolloClient.prefetch(new AllPlanetsQuery()));
        Utils.enqueueAndAssertResponse(server, "HttpCacheTestAllPlanets.json", apolloClient.query(new AllPlanetsQuery()), new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
    }

    @Test
    public void prefetchFileSystemWriteFailure() throws IOException {
        FaultyHttpCacheStore faultyCacheStore = new FaultyHttpCacheStore(FileSystem.SYSTEM);
        cacheStore.delegate = faultyCacheStore;
        faultyCacheStore.failStrategy(FAIL_HEADER_WRITE);
        server.enqueue(Utils.mockResponse("HttpCacheTestAllPlanets.json"));
        com.apollographql.apollo.rx2.Rx2Apollo.from(apolloClient.prefetch(new AllPlanetsQuery())).test().assertError(Exception.class);
        checkNoCachedResponse();
        server.enqueue(Utils.mockResponse("HttpCacheTestAllPlanets.json"));
        faultyCacheStore.failStrategy(FAIL_BODY_WRITE);
        com.apollographql.apollo.rx2.Rx2Apollo.from(apolloClient.prefetch(new AllPlanetsQuery())).test().assertError(Exception.class);
        checkNoCachedResponse();
    }
}

