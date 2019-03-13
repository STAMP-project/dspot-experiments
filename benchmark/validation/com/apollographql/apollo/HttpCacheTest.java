package com.apollographql.apollo;


import AllPlanetsQuery.Data;
import HttpCachePolicy.NETWORK_ONLY;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.exception.ApolloHttpException;
import com.apollographql.apollo.integration.httpcache.AllFilmsQuery;
import com.apollographql.apollo.integration.httpcache.AllPlanetsQuery;
import com.apollographql.apollo.integration.httpcache.DroidDetailsQuery;
import com.apollographql.apollo.rx2.Rx2Apollo;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.io.InMemoryFileSystem;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.Rule;
import org.junit.Test;

import static com.apollographql.apollo.FaultyHttpCacheStore.FailStrategy.FAIL_BODY_READ;
import static com.apollographql.apollo.FaultyHttpCacheStore.FailStrategy.FAIL_BODY_WRITE;
import static com.apollographql.apollo.FaultyHttpCacheStore.FailStrategy.FAIL_HEADER_READ;
import static com.apollographql.apollo.FaultyHttpCacheStore.FailStrategy.FAIL_HEADER_WRITE;


@SuppressWarnings("SimpleDateFormatConstant")
public class HttpCacheTest {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private ApolloClient apolloClient;

    private Request lastHttRequest;

    private Response lastHttResponse;

    private MockHttpCacheStore cacheStore;

    private OkHttpClient okHttpClient;

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Rule
    public final InMemoryFileSystem inMemoryFileSystem = new InMemoryFileSystem();

    @Test
    public void prematureDisconnect() throws Exception {
        MockResponse mockResponse = mockResponse("/HttpCacheTestAllPlanets.json");
        Buffer truncatedBody = new Buffer();
        truncatedBody.write(mockResponse.getBody(), 16);
        mockResponse.setBody(truncatedBody);
        server.enqueue(mockResponse);
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(NETWORK_ONLY)).test().assertError(ApolloException.class);
        checkNoCachedResponse();
    }

    @Test
    public void cacheDefault() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
    }

    @Test
    public void cacheSeveralResponses() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
        enqueueResponse("/HttpCacheTestDroidDetails.json");
        Rx2Apollo.from(apolloClient.query(new DroidDetailsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<DroidDetailsQuery.Data>>() {
            @Override
            public boolean test(Response<DroidDetailsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkCachedResponse("/HttpCacheTestDroidDetails.json");
        enqueueResponse("/HttpCacheTestAllFilms.json");
        Rx2Apollo.from(apolloClient.query(new AllFilmsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllFilmsQuery.Data>>() {
            @Override
            public boolean test(Response<AllFilmsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkCachedResponse("/HttpCacheTestAllFilms.json");
    }

    @Test
    public void noCacheStore() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        ApolloClient apolloClient = ApolloClient.builder().serverUrl(server.url("/")).okHttpClient(new OkHttpClient.Builder().addInterceptor(new HttpCacheTest.TrackingInterceptor()).dispatcher(new okhttp3.Dispatcher(Utils.immediateExecutorService())).build()).dispatcher(Utils.immediateExecutor()).build();
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkNoCachedResponse();
    }

    @Test
    public void networkOnly() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(NETWORK_ONLY)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(lastHttResponse.networkResponse()).isNotNull();
        assertThat(lastHttResponse.cacheResponse()).isNull();
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
    }

    @Test
    public void networkOnly_DoNotStore() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(NETWORK_ONLY).cacheHeaders(com.apollographql.apollo.cache.CacheHeaders.builder().addHeader(ApolloCacheHeaders.DO_NOT_STORE, "true").build())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(lastHttResponse.networkResponse()).isNotNull();
        assertThat(lastHttResponse.cacheResponse()).isNull();
        checkNoCachedResponse();
    }

    @Test
    public void networkOnly_responseWithGraphError_noCached() throws Exception {
        enqueueResponse("/ResponseError.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(NETWORK_ONLY)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return response.hasErrors();
            }
        });
        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(lastHttResponse.networkResponse()).isNotNull();
        assertThat(lastHttResponse.cacheResponse()).isNull();
        checkNoCachedResponse();
    }

    @Test
    public void cacheOnlyHit() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        assertThat(server.takeRequest()).isNotNull();
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_ONLY)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(lastHttResponse.networkResponse()).isNull();
        assertThat(lastHttResponse.cacheResponse()).isNotNull();
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
    }

    @Test
    public void cacheOnlyMiss() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_ONLY)).test().assertError(ApolloHttpException.class);
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void cacheNonStale() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test();
        assertThat(server.takeRequest()).isNotNull();
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_FIRST)).test();
        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(lastHttResponse.networkResponse()).isNull();
        assertThat(lastHttResponse.cacheResponse()).isNotNull();
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void cacheAfterDelete() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test();
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
        cacheStore.delegate.delete();
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_FIRST)).test();
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void cacheStale() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test();
        assertThat(server.getRequestCount()).isEqualTo(1);
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test();
        assertThat(server.getRequestCount()).isEqualTo(2);
        assertThat(lastHttResponse.networkResponse()).isNotNull();
        assertThat(lastHttResponse.cacheResponse()).isNull();
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void cacheStaleBeforeNetwork() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test();
        assertThat(server.getRequestCount()).isEqualTo(1);
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)).test();
        assertThat(server.getRequestCount()).isEqualTo(2);
        assertThat(lastHttResponse.networkResponse()).isNotNull();
        assertThat(lastHttResponse.cacheResponse()).isNull();
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void cacheStaleBeforeNetworkError() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test();
        assertThat(server.getRequestCount()).isEqualTo(1);
        server.enqueue(new MockResponse().setResponseCode(504).setBody(""));
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)).test();
        assertThat(server.getRequestCount()).isEqualTo(2);
        assertThat(lastHttResponse.networkResponse()).isNotNull();
        assertThat(lastHttResponse.cacheResponse()).isNotNull();
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void cacheUpdate() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test();
        assertThat(server.getRequestCount()).isEqualTo(1);
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
        enqueueResponse("/HttpCacheTestAllPlanets2.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test();
        assertThat(server.getRequestCount()).isEqualTo(2);
        checkCachedResponse("/HttpCacheTestAllPlanets2.json");
        assertThat(lastHttResponse.networkResponse()).isNotNull();
        assertThat(lastHttResponse.cacheResponse()).isNull();
        enqueueResponse("/HttpCacheTestAllPlanets2.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_FIRST)).test();
        assertThat(server.getRequestCount()).isEqualTo(2);
        assertThat(lastHttResponse.networkResponse()).isNull();
        assertThat(lastHttResponse.cacheResponse()).isNotNull();
        checkCachedResponse("/HttpCacheTestAllPlanets2.json");
    }

    @Test
    public void fileSystemUnavailable() throws ApolloException, IOException {
        cacheStore.delegate = new DiskLruHttpCacheStore(new NoFileSystem(), new File("/cache/"), Integer.MAX_VALUE);
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkNoCachedResponse();
    }

    @Test
    public void fileSystemWriteFailure() throws ApolloException, IOException {
        FaultyHttpCacheStore faultyCacheStore = new FaultyHttpCacheStore(FileSystem.SYSTEM);
        cacheStore.delegate = faultyCacheStore;
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        faultyCacheStore.failStrategy(FAIL_HEADER_WRITE);
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkNoCachedResponse();
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        faultyCacheStore.failStrategy(FAIL_BODY_WRITE);
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkNoCachedResponse();
    }

    @Test
    public void fileSystemReadFailure() throws ApolloException, IOException {
        FaultyHttpCacheStore faultyCacheStore = new FaultyHttpCacheStore(inMemoryFileSystem);
        cacheStore.delegate = faultyCacheStore;
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        faultyCacheStore.failStrategy(FAIL_HEADER_READ);
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_FIRST)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        assertThat(server.getRequestCount()).isEqualTo(2);
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        faultyCacheStore.failStrategy(FAIL_BODY_READ);
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_FIRST)).test().assertError(Exception.class);
        assertThat(server.getRequestCount()).isEqualTo(2);
    }

    @Test
    public void expireAfterRead() throws ApolloException, IOException {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_ONLY.expireAfterRead())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkNoCachedResponse();
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_ONLY)).test().assertError(Exception.class);
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
    }

    @Test
    public void cacheNetworkError() throws ApolloException, IOException {
        server.enqueue(new MockResponse().setResponseCode(504).setBody(""));
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertError(Exception.class);
        checkNoCachedResponse();
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_ONLY)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
    }

    @Test
    public void networkFirst() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery())).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        assertThat(server.getRequestCount()).isEqualTo(1);
        assertThat(lastHttResponse.networkResponse()).isNotNull();
        assertThat(lastHttResponse.cacheResponse()).isNull();
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        assertThat(server.getRequestCount()).isEqualTo(2);
        assertThat(lastHttResponse.networkResponse()).isNotNull();
        assertThat(lastHttResponse.cacheResponse()).isNull();
        checkCachedResponse("/HttpCacheTestAllPlanets.json");
    }

    @Test
    public void fromCacheFlag() throws Exception {
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return (!(response.hasErrors())) && (!(response.fromCache()));
            }
        });
        enqueueResponse("/HttpCacheTestAllPlanets.json");
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(NETWORK_ONLY)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return (!(response.hasErrors())) && (!(response.fromCache()));
            }
        });
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_ONLY)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return (!(response.hasErrors())) && (response.fromCache());
            }
        });
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_FIRST)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return (!(response.hasErrors())) && (response.fromCache());
            }
        });
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.CACHE_FIRST)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return (!(response.hasErrors())) && (response.fromCache());
            }
        });
        Rx2Apollo.from(apolloClient.query(new AllPlanetsQuery()).httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)).test().assertValue(new io.reactivex.functions.Predicate<Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(Response<AllPlanetsQuery.Data> response) throws Exception {
                return (!(response.hasErrors())) && (response.fromCache());
            }
        });
    }

    private class TrackingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            lastHttRequest = chain.request();
            lastHttResponse = chain.proceed(lastHttRequest);
            return lastHttResponse;
        }
    }
}

