package com.apollographql.apollo.test.espresso;


import android.support.test.espresso.IdlingResource;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ResponseReader;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.rx2.Rx2Apollo;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class ApolloIdlingResourceTest {
    private ApolloIdlingResource idlingResource;

    private ApolloClient apolloClient;

    @Rule
    public final MockWebServer server = new MockWebServer();

    private OkHttpClient okHttpClient;

    private static final long TIME_OUT_SECONDS = 3;

    private static final String IDLING_RESOURCE_NAME = "apolloIdlingResource";

    private static final Query EMPTY_QUERY = new Query() {
        OperationName operationName = new OperationName() {
            @Override
            public String name() {
                return "EmptyQuery";
            }
        };

        @Override
        public String queryDocument() {
            return "";
        }

        @Override
        public Variables variables() {
            return EMPTY_VARIABLES;
        }

        @Override
        public ResponseFieldMapper<Data> responseFieldMapper() {
            return new ResponseFieldMapper<Data>() {
                @Override
                public Data map(ResponseReader responseReader) {
                    return null;
                }
            };
        }

        @Override
        public Object wrapData(Data data) {
            return data;
        }

        @NotNull
        @Override
        public OperationName name() {
            return operationName;
        }

        @NotNull
        @Override
        public String operationId() {
            return "";
        }
    };

    @Test
    public void onNullNamePassed_NullPointerExceptionIsThrown() {
        apolloClient = ApolloClient.builder().okHttpClient(okHttpClient).serverUrl(server.url("/")).build();
        try {
            idlingResource = ApolloIdlingResource.create(null, apolloClient);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
            assertThat(e.getMessage()).isEqualTo("name == null");
        }
    }

    @Test
    public void onNullApolloClientPassed_NullPointerExceptionIsThrown() {
        try {
            idlingResource = ApolloIdlingResource.create(ApolloIdlingResourceTest.IDLING_RESOURCE_NAME, null);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
            assertThat(e.getMessage()).isEqualTo("apolloClient == null");
        }
    }

    @Test
    public void checkValidIdlingResourceNameIsRegistered() {
        apolloClient = ApolloClient.builder().okHttpClient(okHttpClient).serverUrl(server.url("/")).build();
        idlingResource = ApolloIdlingResource.create(ApolloIdlingResourceTest.IDLING_RESOURCE_NAME, apolloClient);
        assertThat(idlingResource.getName()).isEqualTo(ApolloIdlingResourceTest.IDLING_RESOURCE_NAME);
    }

    @Test
    public void checkIsIdleNow_whenCallIsQueued() throws IOException, InterruptedException, TimeoutException {
        server.enqueue(mockResponse());
        final CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        apolloClient = ApolloClient.builder().okHttpClient(okHttpClient).dispatcher(executorService).serverUrl(server.url("/")).build();
        idlingResource = ApolloIdlingResource.create(ApolloIdlingResourceTest.IDLING_RESOURCE_NAME, apolloClient);
        assertThat(idlingResource.isIdleNow()).isTrue();
        apolloClient.query(ApolloIdlingResourceTest.EMPTY_QUERY).enqueue(new ApolloCall.Callback<Object>() {
            @Override
            public void onResponse(@NotNull
            Response<Object> response) {
                latch.countDown();
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                throw new AssertionError("This callback can't be called.");
            }
        });
        assertThat(idlingResource.isIdleNow()).isFalse();
        latch.await(ApolloIdlingResourceTest.TIME_OUT_SECONDS, TimeUnit.SECONDS);
        executorService.shutdown();
        executorService.awaitTermination(ApolloIdlingResourceTest.TIME_OUT_SECONDS, TimeUnit.SECONDS);
        Thread.sleep(100);
        assertThat(idlingResource.isIdleNow()).isTrue();
    }

    @SuppressWarnings("CheckReturnValue")
    @Test
    public void checkIdlingResourceTransition_whenCallIsQueued() throws ApolloException, IOException {
        server.enqueue(mockResponse());
        apolloClient = ApolloClient.builder().okHttpClient(okHttpClient).dispatcher(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        }).serverUrl(server.url("/")).build();
        final AtomicInteger counter = new AtomicInteger(1);
        idlingResource = ApolloIdlingResource.create(ApolloIdlingResourceTest.IDLING_RESOURCE_NAME, apolloClient);
        idlingResource.registerIdleTransitionCallback(new IdlingResource.ResourceCallback() {
            @Override
            public void onTransitionToIdle() {
                counter.decrementAndGet();
            }
        });
        assertThat(counter.get()).isEqualTo(1);
        Rx2Apollo.from(apolloClient.query(ApolloIdlingResourceTest.EMPTY_QUERY)).blockingFirst();
        assertThat(counter.get()).isEqualTo(0);
    }
}

