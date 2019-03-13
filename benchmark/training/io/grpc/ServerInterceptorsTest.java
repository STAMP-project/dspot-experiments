/**
 * Copyright 2014 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc;


import ForwardingServerCall.SimpleForwardingServerCall;
import MethodType.UNKNOWN;
import io.grpc.MethodDescriptor.Marshaller;
import io.grpc.ServerCall.Listener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Unit tests for {@link ServerInterceptors}.
 */
@RunWith(JUnit4.class)
public class ServerInterceptorsTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private Marshaller<String> requestMarshaller;

    @Mock
    private Marshaller<Integer> responseMarshaller;

    @Mock
    private ServerCallHandler<String, Integer> handler;

    @Mock
    private ServerCall.Listener<String> listener;

    private MethodDescriptor<String, Integer> flowMethod;

    private ServerCall<String, Integer> call = new io.grpc.internal.NoopServerCall();

    private ServerServiceDefinition serviceDefinition;

    private final Metadata headers = new Metadata();

    @Test(expected = NullPointerException.class)
    public void npeForNullServiceDefinition() {
        ServerServiceDefinition serviceDef = null;
        ServerInterceptors.intercept(serviceDef, Arrays.<ServerInterceptor>asList());
    }

    @Test(expected = NullPointerException.class)
    public void npeForNullInterceptorList() {
        ServerInterceptors.intercept(serviceDefinition, ((List<ServerInterceptor>) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void npeForNullInterceptor() {
        ServerInterceptors.intercept(serviceDefinition, Arrays.asList(((ServerInterceptor) (null))));
    }

    @Test
    public void noop() {
        Assert.assertSame(serviceDefinition, ServerInterceptors.intercept(serviceDefinition, Arrays.<ServerInterceptor>asList()));
    }

    @Test
    public void multipleInvocationsOfHandler() {
        ServerInterceptor interceptor = Mockito.mock(ServerInterceptor.class, AdditionalAnswers.delegatesTo(new ServerInterceptorsTest.NoopInterceptor()));
        ServerServiceDefinition intercepted = ServerInterceptors.intercept(serviceDefinition, Arrays.asList(interceptor));
        Assert.assertSame(listener, ServerInterceptorsTest.getSoleMethod(intercepted).getServerCallHandler().startCall(call, headers));
        Mockito.verify(interceptor).interceptCall(ArgumentMatchers.same(call), ArgumentMatchers.same(headers), anyCallHandler());
        Mockito.verify(handler).startCall(call, headers);
        Mockito.verifyNoMoreInteractions(interceptor, handler);
        Assert.assertSame(listener, ServerInterceptorsTest.getSoleMethod(intercepted).getServerCallHandler().startCall(call, headers));
        Mockito.verify(interceptor, Mockito.times(2)).interceptCall(ArgumentMatchers.same(call), ArgumentMatchers.same(headers), anyCallHandler());
        Mockito.verify(handler, Mockito.times(2)).startCall(call, headers);
        Mockito.verifyNoMoreInteractions(interceptor, handler);
    }

    @Test
    public void correctHandlerCalled() {
        @SuppressWarnings("unchecked")
        ServerCallHandler<String, Integer> handler2 = Mockito.mock(ServerCallHandler.class);
        MethodDescriptor<String, Integer> flowMethod2 = flowMethod.toBuilder().setFullMethodName("basic/flow2").build();
        serviceDefinition = ServerServiceDefinition.builder(new ServiceDescriptor("basic", flowMethod, flowMethod2)).addMethod(flowMethod, handler).addMethod(flowMethod2, handler2).build();
        ServerServiceDefinition intercepted = ServerInterceptors.intercept(serviceDefinition, Arrays.<ServerInterceptor>asList(new ServerInterceptorsTest.NoopInterceptor()));
        ServerInterceptorsTest.getMethod(intercepted, "basic/flow").getServerCallHandler().startCall(call, headers);
        Mockito.verify(handler).startCall(call, headers);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(handler2);
        ServerInterceptorsTest.getMethod(intercepted, "basic/flow2").getServerCallHandler().startCall(call, headers);
        Mockito.verify(handler2).startCall(call, headers);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(handler2);
    }

    @Test
    public void callNextTwice() {
        ServerInterceptor interceptor = new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                // Calling next twice is permitted, although should only rarely be useful.
                Assert.assertSame(listener, next.startCall(call, headers));
                return next.startCall(call, headers);
            }
        };
        ServerServiceDefinition intercepted = ServerInterceptors.intercept(serviceDefinition, interceptor);
        Assert.assertSame(listener, ServerInterceptorsTest.getSoleMethod(intercepted).getServerCallHandler().startCall(call, headers));
        Mockito.verify(handler, Mockito.times(2)).startCall(ArgumentMatchers.same(call), ArgumentMatchers.same(headers));
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void ordered() {
        final List<String> order = new ArrayList<>();
        handler = new ServerCallHandler<String, Integer>() {
            @Override
            public ServerCall.Listener<String> startCall(ServerCall<String, Integer> call, Metadata headers) {
                order.add("handler");
                return listener;
            }
        };
        ServerInterceptor interceptor1 = new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                order.add("i1");
                return next.startCall(call, headers);
            }
        };
        ServerInterceptor interceptor2 = new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                order.add("i2");
                return next.startCall(call, headers);
            }
        };
        ServerServiceDefinition serviceDefinition = ServerServiceDefinition.builder(new ServiceDescriptor("basic", flowMethod)).addMethod(flowMethod, handler).build();
        ServerServiceDefinition intercepted = ServerInterceptors.intercept(serviceDefinition, Arrays.asList(interceptor1, interceptor2));
        Assert.assertSame(listener, ServerInterceptorsTest.getSoleMethod(intercepted).getServerCallHandler().startCall(call, headers));
        Assert.assertEquals(Arrays.asList("i2", "i1", "handler"), order);
    }

    @Test
    public void orderedForward() {
        final List<String> order = new ArrayList<>();
        handler = new ServerCallHandler<String, Integer>() {
            @Override
            public ServerCall.Listener<String> startCall(ServerCall<String, Integer> call, Metadata headers) {
                order.add("handler");
                return listener;
            }
        };
        ServerInterceptor interceptor1 = new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                order.add("i1");
                return next.startCall(call, headers);
            }
        };
        ServerInterceptor interceptor2 = new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                order.add("i2");
                return next.startCall(call, headers);
            }
        };
        ServerServiceDefinition serviceDefinition = ServerServiceDefinition.builder(new ServiceDescriptor("basic", flowMethod)).addMethod(flowMethod, handler).build();
        ServerServiceDefinition intercepted = ServerInterceptors.interceptForward(serviceDefinition, interceptor1, interceptor2);
        Assert.assertSame(listener, ServerInterceptorsTest.getSoleMethod(intercepted).getServerCallHandler().startCall(call, headers));
        Assert.assertEquals(Arrays.asList("i1", "i2", "handler"), order);
    }

    @Test
    public void argumentsPassed() {
        @SuppressWarnings("unchecked")
        final ServerCall<String, Integer> call2 = new io.grpc.internal.NoopServerCall();
        @SuppressWarnings("unchecked")
        final ServerCall.Listener<String> listener2 = Mockito.mock(Listener.class);
        ServerInterceptor interceptor = new ServerInterceptor() {
            // Lot's of casting for no benefit.  Not intended use.
            @SuppressWarnings("unchecked")
            @Override
            public <R1, R2> ServerCall.Listener<R1> interceptCall(ServerCall<R1, R2> call, Metadata headers, ServerCallHandler<R1, R2> next) {
                Assert.assertSame(call, ServerInterceptorsTest.this.call);
                Assert.assertSame(listener, next.startCall(((ServerCall<R1, R2>) (call2)), headers));
                return ((ServerCall.Listener<R1>) (listener2));
            }
        };
        ServerServiceDefinition intercepted = ServerInterceptors.intercept(serviceDefinition, Arrays.asList(interceptor));
        Assert.assertSame(listener2, ServerInterceptorsTest.getSoleMethod(intercepted).getServerCallHandler().startCall(call, headers));
        Mockito.verify(handler).startCall(call2, headers);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void typedMarshalledMessages() {
        final List<String> order = new ArrayList<>();
        Marshaller<ServerInterceptorsTest.Holder> marshaller = new Marshaller<ServerInterceptorsTest.Holder>() {
            @Override
            public InputStream stream(ServerInterceptorsTest.Holder value) {
                return value.get();
            }

            @Override
            public ServerInterceptorsTest.Holder parse(InputStream stream) {
                return new ServerInterceptorsTest.Holder(stream);
            }
        };
        ServerCallHandler<ServerInterceptorsTest.Holder, ServerInterceptorsTest.Holder> handler2 = new ServerCallHandler<ServerInterceptorsTest.Holder, ServerInterceptorsTest.Holder>() {
            @Override
            public Listener<ServerInterceptorsTest.Holder> startCall(final ServerCall<ServerInterceptorsTest.Holder, ServerInterceptorsTest.Holder> call, final Metadata headers) {
                return new Listener<ServerInterceptorsTest.Holder>() {
                    @Override
                    public void onMessage(ServerInterceptorsTest.Holder message) {
                        order.add("handler");
                        call.sendMessage(message);
                    }
                };
            }
        };
        MethodDescriptor<ServerInterceptorsTest.Holder, ServerInterceptorsTest.Holder> wrappedMethod = MethodDescriptor.<ServerInterceptorsTest.Holder, ServerInterceptorsTest.Holder>newBuilder().setType(UNKNOWN).setFullMethodName("basic/wrapped").setRequestMarshaller(marshaller).setResponseMarshaller(marshaller).build();
        ServerServiceDefinition serviceDef = ServerServiceDefinition.builder(new ServiceDescriptor("basic", wrappedMethod)).addMethod(wrappedMethod, handler2).build();
        ServerInterceptor interceptor1 = new ServerInterceptor() {
            @Override
            public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                ServerCall<ReqT, RespT> interceptedCall = new SimpleForwardingServerCall<ReqT, RespT>(call) {
                    @Override
                    public void sendMessage(RespT message) {
                        order.add("i1sendMessage");
                        assertTrue((message instanceof io.grpc.Holder));
                        super.sendMessage(message);
                    }
                };
                ServerCall.Listener<ReqT> originalListener = next.startCall(interceptedCall, headers);
                return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(originalListener) {
                    @Override
                    public void onMessage(ReqT message) {
                        order.add("i1onMessage");
                        assertTrue((message instanceof io.grpc.Holder));
                        super.onMessage(message);
                    }
                };
            }
        };
        ServerInterceptor interceptor2 = new ServerInterceptor() {
            @Override
            public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                ServerCall<ReqT, RespT> interceptedCall = new SimpleForwardingServerCall<ReqT, RespT>(call) {
                    @Override
                    public void sendMessage(RespT message) {
                        order.add("i2sendMessage");
                        assertTrue((message instanceof InputStream));
                        super.sendMessage(message);
                    }
                };
                ServerCall.Listener<ReqT> originalListener = next.startCall(interceptedCall, headers);
                return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(originalListener) {
                    @Override
                    public void onMessage(ReqT message) {
                        order.add("i2onMessage");
                        assertTrue((message instanceof InputStream));
                        super.onMessage(message);
                    }
                };
            }
        };
        ServerServiceDefinition intercepted = ServerInterceptors.intercept(serviceDef, interceptor1);
        ServerServiceDefinition inputStreamMessageService = ServerInterceptors.useInputStreamMessages(intercepted);
        ServerServiceDefinition intercepted2 = ServerInterceptors.intercept(inputStreamMessageService, interceptor2);
        ServerMethodDefinition<InputStream, InputStream> serverMethod = ((ServerMethodDefinition<InputStream, InputStream>) (intercepted2.getMethod("basic/wrapped")));
        ServerCall<InputStream, InputStream> call2 = new io.grpc.internal.NoopServerCall();
        byte[] bytes = new byte[]{  };
        serverMethod.getServerCallHandler().startCall(call2, headers).onMessage(new java.io.ByteArrayInputStream(bytes));
        Assert.assertEquals(Arrays.asList("i2onMessage", "i1onMessage", "handler", "i1sendMessage", "i2sendMessage"), order);
    }

    private static class NoopInterceptor implements ServerInterceptor {
        @Override
        public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
            return next.startCall(call, headers);
        }
    }

    private static class Holder {
        private final InputStream inputStream;

        Holder(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public InputStream get() {
            return inputStream;
        }
    }
}

