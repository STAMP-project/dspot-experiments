package com.apollographql.apollo;


import ApolloCacheHeaders.DO_NOT_STORE;
import com.apollographql.apollo.cache.CacheHeaders;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.NormalizedCache;
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.Record;
import com.apollographql.apollo.cache.normalized.RecordFieldJsonAdapter;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.rx2.Rx2Apollo;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Rule;
import org.junit.Test;


public class CacheHeadersTest {
    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void testHeadersReceived() throws ApolloException, IOException {
        final AtomicBoolean hasHeader = new AtomicBoolean();
        final NormalizedCache normalizedCache = new NormalizedCache() {
            @Nullable
            @Override
            public Record loadRecord(@NotNull
            String key, @NotNull
            CacheHeaders cacheHeaders) {
                hasHeader.set(cacheHeaders.hasHeader(DO_NOT_STORE));
                return null;
            }

            @NotNull
            @Override
            public Set<String> merge(@NotNull
            Record record, @NotNull
            CacheHeaders cacheHeaders) {
                hasHeader.set(cacheHeaders.hasHeader(DO_NOT_STORE));
                return Collections.emptySet();
            }

            @Override
            public void clearAll() {
            }

            @Override
            public boolean remove(@NotNull
            CacheKey cacheKey, boolean cascade) {
                return false;
            }

            @NotNull
            @Override
            protected Set<String> performMerge(@NotNull
            Record apolloRecord, @NotNull
            CacheHeaders cacheHeaders) {
                return Collections.emptySet();
            }
        };
        final NormalizedCacheFactory<NormalizedCache> cacheFactory = new NormalizedCacheFactory<NormalizedCache>() {
            @Override
            public NormalizedCache create(RecordFieldJsonAdapter recordFieldAdapter) {
                return normalizedCache;
            }
        };
        ApolloClient apolloClient = ApolloClient.builder().normalizedCache(cacheFactory, new IdFieldCacheKeyResolver()).serverUrl(server.url("/")).okHttpClient(new OkHttpClient.Builder().dispatcher(new Dispatcher(Utils.immediateExecutorService())).build()).dispatcher(Utils.immediateExecutor()).build();
        server.enqueue(mockResponse("HeroAndFriendsNameResponse.json"));
        CacheHeaders cacheHeaders = CacheHeaders.builder().addHeader(DO_NOT_STORE, "true").build();
        Rx2Apollo.from(apolloClient.query(new com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesQuery(com.apollographql.apollo.api.Input.fromNullable(Episode.NEWHOPE))).cacheHeaders(cacheHeaders)).test();
        assertThat(hasHeader.get()).isTrue();
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void testDefaultHeadersReceived() throws Exception {
        final AtomicBoolean hasHeader = new AtomicBoolean();
        final NormalizedCache normalizedCache = new NormalizedCache() {
            @Nullable
            @Override
            public Record loadRecord(@NotNull
            String key, @NotNull
            CacheHeaders cacheHeaders) {
                hasHeader.set(cacheHeaders.hasHeader(DO_NOT_STORE));
                return null;
            }

            @NotNull
            @Override
            public Set<String> merge(@NotNull
            Record record, @NotNull
            CacheHeaders cacheHeaders) {
                hasHeader.set(cacheHeaders.hasHeader(DO_NOT_STORE));
                return Collections.emptySet();
            }

            @Override
            public void clearAll() {
            }

            @Override
            public boolean remove(@NotNull
            CacheKey cacheKey, boolean cascade) {
                return false;
            }

            @NotNull
            @Override
            protected Set<String> performMerge(@NotNull
            Record apolloRecord, @NotNull
            CacheHeaders cacheHeaders) {
                return Collections.emptySet();
            }
        };
        final NormalizedCacheFactory<NormalizedCache> cacheFactory = new NormalizedCacheFactory<NormalizedCache>() {
            @Override
            public NormalizedCache create(RecordFieldJsonAdapter recordFieldAdapter) {
                return normalizedCache;
            }
        };
        CacheHeaders cacheHeaders = CacheHeaders.builder().addHeader(DO_NOT_STORE, "true").build();
        ApolloClient apolloClient = ApolloClient.builder().normalizedCache(cacheFactory, new IdFieldCacheKeyResolver()).serverUrl(server.url("/")).okHttpClient(new OkHttpClient.Builder().dispatcher(new Dispatcher(Utils.immediateExecutorService())).build()).dispatcher(Utils.immediateExecutor()).defaultCacheHeaders(cacheHeaders).build();
        server.enqueue(mockResponse("HeroAndFriendsNameResponse.json"));
        Rx2Apollo.from(apolloClient.query(new com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesQuery(com.apollographql.apollo.api.Input.fromNullable(Episode.NEWHOPE))).cacheHeaders(cacheHeaders)).test();
        assertThat(hasHeader.get()).isTrue();
    }
}

