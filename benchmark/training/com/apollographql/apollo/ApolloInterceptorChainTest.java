package com.apollographql.apollo;


import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.integration.normalizer.EpisodeHeroNameQuery;
import com.apollographql.apollo.interceptor.ApolloInterceptor;
import com.apollographql.apollo.interceptor.ApolloInterceptorChain;
import com.apollographql.apollo.internal.interceptor.RealApolloInterceptorChain;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;


public class ApolloInterceptorChainTest {
    @Test
    public void onProceedAsyncCalled_chainPassesControlToInterceptor() throws InterruptedException, TimeoutException {
        final AtomicInteger counter = new AtomicInteger(1);
        EpisodeHeroNameQuery query = createQuery();
        ApolloInterceptor interceptor = new ApolloInterceptor() {
            @Override
            public void interceptAsync(@NotNull
            InterceptorRequest request, @NotNull
            ApolloInterceptorChain chain, @NotNull
            Executor dispatcher, @NotNull
            CallBack callBack) {
                counter.decrementAndGet();
            }

            @Override
            public void dispose() {
            }
        };
        List<ApolloInterceptor> interceptors = Collections.singletonList(interceptor);
        RealApolloInterceptorChain chain = new RealApolloInterceptorChain(interceptors);
        chain.proceedAsync(ApolloInterceptor.InterceptorRequest.builder(query).fetchFromCache(false).build(), Utils.immediateExecutor(), new CallBack() {
            @Override
            public void onResponse(@NotNull
            InterceptorResponse response) {
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onFetch(ApolloInterceptor.FetchSourceType sourceType) {
            }
        });
        // If counter's count doesn't go down to zero, it means interceptor's interceptAsync wasn't called
        // which means the test should fail.
        if ((counter.get()) != 0) {
            Assert.fail("Control not passed to the interceptor");
        }
    }

    @Test
    public void onProceedAsyncCalled_correctInterceptorResponseIsReceived() throws InterruptedException, TimeoutException {
        final AtomicInteger counter = new AtomicInteger(1);
        EpisodeHeroNameQuery query = createQuery();
        final InterceptorResponse expectedResponse = prepareInterceptorResponse(query);
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
                        callBack.onResponse(expectedResponse);
                    }
                });
            }

            @Override
            public void dispose() {
            }
        };
        List<ApolloInterceptor> interceptors = Collections.singletonList(interceptor);
        RealApolloInterceptorChain chain = new RealApolloInterceptorChain(interceptors);
        chain.proceedAsync(ApolloInterceptor.InterceptorRequest.builder(query).fetchFromCache(false).build(), Utils.immediateExecutor(), new CallBack() {
            @Override
            public void onResponse(@NotNull
            InterceptorResponse response) {
                assertThat(response).isEqualTo(expectedResponse);
                counter.decrementAndGet();
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onFetch(ApolloInterceptor.FetchSourceType sourceType) {
            }
        });
        if ((counter.get()) != 0) {
            Assert.fail("Interceptor's response not received");
        }
    }

    @Test
    public void onProceedAsyncCalled_correctExceptionIsCaught() throws InterruptedException, TimeoutException {
        final AtomicInteger counter = new AtomicInteger(1);
        final String message = "ApolloException";
        EpisodeHeroNameQuery query = createQuery();
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
                        ApolloException apolloException = new ApolloException(message);
                        callBack.onFailure(apolloException);
                    }
                });
            }

            @Override
            public void dispose() {
            }
        };
        List<ApolloInterceptor> interceptors = Collections.singletonList(interceptor);
        RealApolloInterceptorChain chain = new RealApolloInterceptorChain(interceptors);
        chain.proceedAsync(ApolloInterceptor.InterceptorRequest.builder(query).fetchFromCache(false).build(), Utils.immediateExecutor(), new CallBack() {
            @Override
            public void onResponse(@NotNull
            InterceptorResponse response) {
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                assertThat(e.getMessage()).isEqualTo(message);
                counter.decrementAndGet();
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onFetch(ApolloInterceptor.FetchSourceType sourceType) {
            }
        });
        if ((counter.get()) != 0) {
            Assert.fail("Exception thrown by Interceptor not caught");
        }
    }

    @Test
    public void onDisposeCalled_interceptorIsDisposed() {
        final AtomicInteger counter = new AtomicInteger(1);
        ApolloInterceptor interceptor = new ApolloInterceptor() {
            @Override
            public void interceptAsync(@NotNull
            InterceptorRequest request, @NotNull
            ApolloInterceptorChain chain, @NotNull
            Executor dispatcher, @NotNull
            CallBack callBack) {
            }

            @Override
            public void dispose() {
                counter.decrementAndGet();
            }
        };
        List<ApolloInterceptor> interceptors = Collections.singletonList(interceptor);
        RealApolloInterceptorChain chain = new RealApolloInterceptorChain(interceptors);
        chain.dispose();
        if ((counter.get()) != 0) {
            Assert.fail("Interceptor's dispose method not called");
        }
    }
}

