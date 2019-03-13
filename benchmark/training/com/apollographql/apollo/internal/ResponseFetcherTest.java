package com.apollographql.apollo.internal;


import HttpCachePolicy.CACHE_ONLY;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Query;
import okhttp3.OkHttpClient;
import org.junit.Test;


public class ResponseFetcherTest {
    private OkHttpClient okHttpClient;

    private Query emptyQuery;

    @Test
    public void setDefaultCachePolicy() {
        ApolloClient apolloClient = ApolloClient.builder().serverUrl("http://google.com").okHttpClient(okHttpClient).defaultHttpCachePolicy(CACHE_ONLY).defaultResponseFetcher(NETWORK_ONLY).build();
        RealApolloCall realApolloCall = ((RealApolloCall) (apolloClient.query(emptyQuery)));
        assertThat(realApolloCall.httpCachePolicy.fetchStrategy).isEqualTo(HttpCachePolicy.FetchStrategy.CACHE_ONLY);
        assertThat(realApolloCall.responseFetcher).isEqualTo(NETWORK_ONLY);
    }

    @Test
    public void defaultCacheControl() {
        ApolloClient apolloClient = ApolloClient.builder().serverUrl("http://google.com").okHttpClient(okHttpClient).build();
        RealApolloCall realApolloCall = ((RealApolloCall) (apolloClient.query(emptyQuery)));
        assertThat(realApolloCall.httpCachePolicy.fetchStrategy).isEqualTo(HttpCachePolicy.FetchStrategy.NETWORK_ONLY);
        assertThat(realApolloCall.responseFetcher).isEqualTo(CACHE_FIRST);
    }
}

