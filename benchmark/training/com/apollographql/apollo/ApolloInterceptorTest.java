package com.apollographql.apollo;


import EpisodeHeroNameQuery.Data;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.exception.ApolloParseException;
import com.apollographql.apollo.integration.normalizer.EpisodeHeroNameQuery;
import com.apollographql.apollo.interceptor.ApolloInterceptor;
import com.apollographql.apollo.interceptor.ApolloInterceptorChain;
import com.apollographql.apollo.rx2.Rx2Apollo;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;


public class ApolloInterceptorTest {
    private static final String FILE_EPISODE_HERO_NAME_WITH_ID = "EpisodeHeroNameResponseWithId.json";

    private static final String FILE_EPISODE_HERO_NAME_CHANGE = "EpisodeHeroNameResponseNameChange.json";

    private ApolloClient client;

    @Rule
    public final MockWebServer server = new MockWebServer();

    private OkHttpClient okHttpClient;

    @Test
    public void asyncApplicationInterceptorCanShortCircuitResponses() throws Exception {
        server.enqueue(mockResponse(ApolloInterceptorTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        EpisodeHeroNameQuery query = createHeroNameQuery();
        final InterceptorResponse expectedResponse = prepareInterceptorResponse(query);
        ApolloInterceptor interceptor = ApolloInterceptorTest.createShortcutInterceptor(expectedResponse);
        client = createApolloClient(interceptor);
        Rx2Apollo.from(client.query(query)).test().assertValue(expectedResponse.parsedResponse.get());
    }

    @Test
    public void asyncApplicationInterceptorRewritesResponsesFromServer() throws Exception {
        server.enqueue(mockResponse(ApolloInterceptorTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        EpisodeHeroNameQuery query = createHeroNameQuery();
        final InterceptorResponse rewrittenResponse = prepareInterceptorResponse(query);
        ApolloInterceptor interceptor = new ApolloInterceptor() {
            @Override
            public void interceptAsync(@NotNull
            InterceptorRequest request, @NotNull
            ApolloInterceptorChain chain, @NotNull
            Executor dispatcher, @NotNull
            final CallBack callBack) {
                chain.proceedAsync(request, dispatcher, new CallBack() {
                    @Override
                    public void onResponse(@NotNull
                    InterceptorResponse response) {
                        callBack.onResponse(rewrittenResponse);
                    }

                    @Override
                    public void onFailure(@NotNull
                    ApolloException e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onCompleted() {
                        callBack.onCompleted();
                    }

                    @Override
                    public void onFetch(FetchSourceType sourceType) {
                        callBack.onFetch(sourceType);
                    }
                });
            }

            @Override
            public void dispose() {
            }
        };
        client = createApolloClient(interceptor);
        Rx2Apollo.from(client.query(query)).test().assertValue(rewrittenResponse.parsedResponse.get());
    }

    @Test
    public void asyncApplicationInterceptorThrowsApolloException() throws Exception {
        final String message = "ApolloException";
        EpisodeHeroNameQuery query = createHeroNameQuery();
        ApolloInterceptor interceptor = new ApolloInterceptor() {
            @Override
            public void interceptAsync(@NotNull
            InterceptorRequest request, @NotNull
            ApolloInterceptorChain chain, @NotNull
            Executor dispatcher, @NotNull
            CallBack callBack) {
                ApolloException apolloException = new ApolloParseException(message);
                callBack.onFailure(apolloException);
            }

            @Override
            public void dispose() {
            }
        };
        client = createApolloClient(interceptor);
        Rx2Apollo.from(client.query(query)).test().assertError(new io.reactivex.functions.Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                return (message.equals(throwable.getMessage())) && (throwable instanceof ApolloParseException);
            }
        });
    }

    @Test
    public void asyncApplicationInterceptorThrowsRuntimeException() throws InterruptedException, TimeoutException {
        final String message = "RuntimeException";
        EpisodeHeroNameQuery query = createHeroNameQuery();
        ApolloInterceptor interceptor = new ApolloInterceptor() {
            @Override
            public void interceptAsync(@NotNull
            InterceptorRequest request, @NotNull
            ApolloInterceptorChain chain, @NotNull
            Executor dispatcher, @NotNull
            CallBack callBack) {
                dispatcher.execute(new Runnable() {
                    @Override
                    public void run() {
                        throw new RuntimeException(message);
                    }
                });
            }

            @Override
            public void dispose() {
            }
        };
        client = createApolloClient(interceptor);
        Rx2Apollo.from(client.query(query)).test().assertError(new io.reactivex.functions.Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                return (throwable instanceof RuntimeException) && (message.equals(throwable.getMessage()));
            }
        });
    }

    @Test
    public void asyncApplicationInterceptorReturnsNull() throws InterruptedException, TimeoutException {
        EpisodeHeroNameQuery query = createHeroNameQuery();
        ApolloInterceptor interceptor = new ApolloInterceptor() {
            @Override
            public void interceptAsync(@NotNull
            InterceptorRequest request, @NotNull
            ApolloInterceptorChain chain, @NotNull
            Executor dispatcher, @NotNull
            final CallBack callBack) {
                dispatcher.execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onResponse(null);
                    }
                });
            }

            @Override
            public void dispose() {
            }
        };
        client = createApolloClient(interceptor);
        Rx2Apollo.from(client.query(query)).test().assertError(new io.reactivex.functions.Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                return throwable instanceof NullPointerException;
            }
        });
    }

    @Test
    public void applicationInterceptorCanMakeMultipleRequestsToServer() throws Exception {
        server.enqueue(mockResponse(ApolloInterceptorTest.FILE_EPISODE_HERO_NAME_CHANGE));
        EpisodeHeroNameQuery query = createHeroNameQuery();
        ApolloInterceptor interceptor = ApolloInterceptorTest.createChainInterceptor();
        client = createApolloClient(interceptor);
        Utils.enqueueAndAssertResponse(server, ApolloInterceptorTest.FILE_EPISODE_HERO_NAME_WITH_ID, client.query(query), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                assertThat(response.data().hero().name()).isEqualTo("Artoo");
                return true;
            }
        });
    }

    @Test
    public void onShortCircuitingResponseSubsequentInterceptorsAreNotCalled() throws ApolloException, IOException {
        EpisodeHeroNameQuery query = createHeroNameQuery();
        final InterceptorResponse expectedResponse = prepareInterceptorResponse(query);
        ApolloInterceptor firstInterceptor = ApolloInterceptorTest.createShortcutInterceptor(expectedResponse);
        ApolloInterceptor secondInterceptor = ApolloInterceptorTest.createChainInterceptor();
        client = ApolloClient.builder().serverUrl(server.url("/")).okHttpClient(okHttpClient).addApplicationInterceptor(firstInterceptor).addApplicationInterceptor(secondInterceptor).build();
        Utils.assertResponse(client.query(query), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                assertThat(expectedResponse.parsedResponse.get()).isEqualTo(response);
                return true;
            }
        });
    }

    @Test
    public void onApolloCallCanceledAsyncApolloInterceptorIsDisposed() throws ApolloException, IOException, InterruptedException, TimeoutException {
        server.enqueue(mockResponse(ApolloInterceptorTest.FILE_EPISODE_HERO_NAME_WITH_ID));
        EpisodeHeroNameQuery query = createHeroNameQuery();
        ApolloInterceptorTest.SpyingApolloInterceptor interceptor = new ApolloInterceptorTest.SpyingApolloInterceptor();
        Utils.TestExecutor testExecutor = new Utils.TestExecutor();
        client = createApolloClient(interceptor, testExecutor);
        ApolloCall<EpisodeHeroNameQuery.Data> apolloCall = client.query(query);
        apolloCall.enqueue(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<EpisodeHeroNameQuery.Data> response) {
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
            }
        });
        apolloCall.cancel();
        testExecutor.triggerActions();
        assertThat(interceptor.isDisposed).isTrue();
    }

    private static class SpyingApolloInterceptor implements ApolloInterceptor {
        volatile boolean isDisposed = false;

        @Override
        public void interceptAsync(@NotNull
        InterceptorRequest request, @NotNull
        ApolloInterceptorChain chain, @NotNull
        Executor dispatcher, @NotNull
        CallBack callBack) {
            chain.proceedAsync(request, dispatcher, callBack);
        }

        @Override
        public void dispose() {
            isDisposed = true;
        }
    }
}

