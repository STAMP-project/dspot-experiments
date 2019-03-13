package com.apollographql.apollo.internal.interceptor;


import ApolloInterceptor.CallBack;
import ApolloInterceptor.InterceptorRequest;
import ApolloInterceptor.InterceptorResponse;
import com.apollographql.apollo.Logger;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ResponseFieldMarshaller;
import com.apollographql.apollo.api.internal.Optional;
import com.apollographql.apollo.cache.normalized.Record;
import com.apollographql.apollo.interceptor.ApolloInterceptor;
import com.apollographql.apollo.interceptor.ApolloInterceptorChain;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ApolloAutoPersistedQueryInterceptorTest {
    private ApolloAutoPersistedQueryInterceptor interceptor = new ApolloAutoPersistedQueryInterceptor(new com.apollographql.apollo.internal.ApolloLogger(Optional.<Logger>absent()));

    private InterceptorRequest request = InterceptorRequest.builder(new ApolloAutoPersistedQueryInterceptorTest.MockOperation()).build();

    @Test
    public void initialRequestWithoutQueryDocument() {
        ApolloInterceptorChain chain = Mockito.mock(ApolloInterceptorChain.class);
        interceptor.interceptAsync(request, chain, new ApolloAutoPersistedQueryInterceptorTest.TrampolineExecutor(), Mockito.mock(CallBack.class));
        ArgumentCaptor<ApolloInterceptor.InterceptorRequest> requestArgumentCaptor = ArgumentCaptor.forClass(InterceptorRequest.class);
        Mockito.verify(chain).proceedAsync(requestArgumentCaptor.capture(), ArgumentMatchers.any(Executor.class), ArgumentMatchers.any(CallBack.class));
        assertThat(requestArgumentCaptor.getValue().sendQueryDocument).isFalse();
    }

    @Test
    public void onPersistedQueryNotFoundErrorRequestWithQueryDocument() {
        ApolloAutoPersistedQueryInterceptorTest.ApolloInterceptorChainAdapter chain = new ApolloAutoPersistedQueryInterceptorTest.ApolloInterceptorChainAdapter() {
            @Override
            public void proceedAsync(@NotNull
            ApolloInterceptor.InterceptorRequest request, @NotNull
            Executor dispatcher, @NotNull
            ApolloInterceptor.CallBack callBack) {
                super.proceedAsync(request, dispatcher, callBack);
                if ((proceedAsyncInvocationCount) == 1) {
                    assertThat(request.sendQueryDocument).isFalse();
                    callBack.onResponse(new ApolloInterceptor.InterceptorResponse(mockHttpResponse(), Response.<ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data>builder(new ApolloAutoPersistedQueryInterceptorTest.MockOperation()).errors(Collections.singletonList(new Error("PersistedQueryNotFound", null, null))).build(), Collections.<Record>emptyList()));
                } else
                    if ((proceedAsyncInvocationCount) == 2) {
                        assertThat(request.sendQueryDocument).isTrue();
                        callBack.onResponse(new ApolloInterceptor.InterceptorResponse(mockHttpResponse(), Response.<ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data>builder(new ApolloAutoPersistedQueryInterceptorTest.MockOperation()).data(new ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data()).build(), Collections.<Record>emptyList()));
                    } else {
                        Assert.fail("expected only 2 invocation first without query document, second with it");
                    }

            }
        };
        ApolloInterceptor.CallBack interceptorCallBack = Mockito.mock(CallBack.class);
        interceptor.interceptAsync(request, chain, new ApolloAutoPersistedQueryInterceptorTest.TrampolineExecutor(), interceptorCallBack);
        assertThat(chain.proceedAsyncInvocationCount).isEqualTo(2);
        ArgumentCaptor<ApolloInterceptor.InterceptorResponse> interceptorResponseArgumentCaptor = ArgumentCaptor.forClass(InterceptorResponse.class);
        Mockito.verify(interceptorCallBack).onResponse(interceptorResponseArgumentCaptor.capture());
        assertThat(interceptorResponseArgumentCaptor.getValue().parsedResponse.get().hasErrors()).isFalse();
        assertThat(interceptorResponseArgumentCaptor.getValue().parsedResponse.get().data()).isNotNull();
    }

    @Test
    public void onPersistedQueryNotSupportedErrorRequestWithQueryDocument() {
        ApolloAutoPersistedQueryInterceptorTest.ApolloInterceptorChainAdapter chain = new ApolloAutoPersistedQueryInterceptorTest.ApolloInterceptorChainAdapter() {
            @Override
            public void proceedAsync(@NotNull
            ApolloInterceptor.InterceptorRequest request, @NotNull
            Executor dispatcher, @NotNull
            ApolloInterceptor.CallBack callBack) {
                super.proceedAsync(request, dispatcher, callBack);
                if ((proceedAsyncInvocationCount) == 1) {
                    assertThat(request.sendQueryDocument).isFalse();
                    callBack.onResponse(new ApolloInterceptor.InterceptorResponse(mockHttpResponse(), Response.<ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data>builder(new ApolloAutoPersistedQueryInterceptorTest.MockOperation()).errors(Collections.singletonList(new Error("PersistedQueryNotSupported", null, null))).build(), Collections.<Record>emptyList()));
                } else
                    if ((proceedAsyncInvocationCount) == 2) {
                        assertThat(request.sendQueryDocument).isTrue();
                        callBack.onResponse(new ApolloInterceptor.InterceptorResponse(mockHttpResponse(), Response.<ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data>builder(new ApolloAutoPersistedQueryInterceptorTest.MockOperation()).data(new ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data()).build(), Collections.<Record>emptyList()));
                    } else {
                        Assert.fail("expected only 2 invocation first without query document, second with it");
                    }

            }
        };
        ApolloInterceptor.CallBack interceptorCallBack = Mockito.mock(CallBack.class);
        interceptor.interceptAsync(request, chain, new ApolloAutoPersistedQueryInterceptorTest.TrampolineExecutor(), interceptorCallBack);
        assertThat(chain.proceedAsyncInvocationCount).isEqualTo(2);
        ArgumentCaptor<ApolloInterceptor.InterceptorResponse> interceptorResponseArgumentCaptor = ArgumentCaptor.forClass(InterceptorResponse.class);
        Mockito.verify(interceptorCallBack).onResponse(interceptorResponseArgumentCaptor.capture());
        assertThat(interceptorResponseArgumentCaptor.getValue().parsedResponse.get().hasErrors()).isFalse();
        assertThat(interceptorResponseArgumentCaptor.getValue().parsedResponse.get().data()).isNotNull();
    }

    @Test
    public void onNonPersistedQueryErrorOriginalCallbackCalled() {
        ApolloInterceptorChain chain = Mockito.mock(ApolloInterceptorChain.class);
        Mockito.doAnswer(new org.mockito.stubbing.Answer() {
            @Override
            public Object answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
                onResponse(new ApolloInterceptor.InterceptorResponse(mockHttpResponse(), Response.<ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data>builder(new ApolloAutoPersistedQueryInterceptorTest.MockOperation()).errors(Collections.singletonList(new Error("SomeOtherError", null, null))).build(), Collections.<Record>emptyList()));
                return null;
            }
        }).when(chain).proceedAsync(ArgumentMatchers.any(InterceptorRequest.class), ArgumentMatchers.any(Executor.class), ArgumentMatchers.any(CallBack.class));
        ApolloInterceptor.CallBack interceptorCallBack = Mockito.mock(CallBack.class);
        interceptor.interceptAsync(request, chain, new ApolloAutoPersistedQueryInterceptorTest.TrampolineExecutor(), interceptorCallBack);
        Mockito.verify(chain).proceedAsync(ArgumentMatchers.any(InterceptorRequest.class), ArgumentMatchers.any(Executor.class), ArgumentMatchers.any(CallBack.class));
        ArgumentCaptor<ApolloInterceptor.InterceptorResponse> interceptorResponseArgumentCaptor = ArgumentCaptor.forClass(InterceptorResponse.class);
        Mockito.verify(interceptorCallBack).onResponse(interceptorResponseArgumentCaptor.capture());
        assertThat(interceptorResponseArgumentCaptor.getValue().parsedResponse.get().hasErrors()).isTrue();
    }

    @Test
    public void onPersistedQueryFoundCallbackCalled() {
        ApolloInterceptorChain chain = Mockito.mock(ApolloInterceptorChain.class);
        Mockito.doAnswer(new org.mockito.stubbing.Answer() {
            @Override
            public Object answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
                onResponse(new ApolloInterceptor.InterceptorResponse(mockHttpResponse(), Response.<ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data>builder(new ApolloAutoPersistedQueryInterceptorTest.MockOperation()).data(new ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data()).build(), Collections.<Record>emptyList()));
                return null;
            }
        }).when(chain).proceedAsync(ArgumentMatchers.any(InterceptorRequest.class), ArgumentMatchers.any(Executor.class), ArgumentMatchers.any(CallBack.class));
        ApolloInterceptor.CallBack interceptorCallBack = Mockito.mock(CallBack.class);
        interceptor.interceptAsync(request, chain, new ApolloAutoPersistedQueryInterceptorTest.TrampolineExecutor(), interceptorCallBack);
        Mockito.verify(chain).proceedAsync(ArgumentMatchers.any(InterceptorRequest.class), ArgumentMatchers.any(Executor.class), ArgumentMatchers.any(CallBack.class));
        ArgumentCaptor<ApolloInterceptor.InterceptorResponse> interceptorResponseArgumentCaptor = ArgumentCaptor.forClass(InterceptorResponse.class);
        Mockito.verify(interceptorCallBack).onResponse(interceptorResponseArgumentCaptor.capture());
        assertThat(interceptorResponseArgumentCaptor.getValue().parsedResponse.get().data()).isNotNull();
        assertThat(interceptorResponseArgumentCaptor.getValue().parsedResponse.get().hasErrors()).isFalse();
    }

    static class MockOperation implements Operation<ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data, ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data, Operation.Variables> {
        @Override
        public String queryDocument() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Variables variables() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ResponseFieldMapper<ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data> responseFieldMapper() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data wrapData(ApolloAutoPersistedQueryInterceptorTest.MockOperation.Data data) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public OperationName name() {
            return new OperationName() {
                @Override
                public String name() {
                    return "MockOperation";
                }
            };
        }

        @NotNull
        @Override
        public String operationId() {
            return UUID.randomUUID().toString();
        }

        static class Data implements Operation.Data {
            @Override
            public ResponseFieldMarshaller marshaller() {
                throw new UnsupportedOperationException();
            }
        }
    }

    static class TrampolineExecutor extends AbstractExecutorService {
        @Override
        public void shutdown() {
        }

        @Override
        public List<Runnable> shutdownNow() {
            return null;
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long l, TimeUnit timeUnit) {
            return false;
        }

        @Override
        public void execute(Runnable runnable) {
            runnable.run();
        }
    }

    static class ApolloInterceptorChainAdapter implements ApolloInterceptorChain {
        int proceedAsyncInvocationCount;

        @Override
        public void proceedAsync(@NotNull
        ApolloInterceptor.InterceptorRequest request, @NotNull
        Executor dispatcher, @NotNull
        ApolloInterceptor.CallBack callBack) {
            (proceedAsyncInvocationCount)++;
        }

        @Override
        public void dispose() {
        }
    }
}

