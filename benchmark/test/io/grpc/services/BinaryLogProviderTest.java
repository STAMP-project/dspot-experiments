/**
 * Copyright 2017 The gRPC Authors
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
package io.grpc.services;


import BinaryLogProvider.BYTEARRAY_MARSHALLER;
import CallOptions.DEFAULT;
import MethodType.UNARY;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.Marshaller;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerInterceptor;
import io.grpc.ServerMethodDefinition;
import io.grpc.internal.IoUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link BinaryLogProvider}.
 */
@RunWith(JUnit4.class)
public class BinaryLogProviderTest {
    private final BinaryLogProviderTest.InvocationCountMarshaller<String> reqMarshaller = new BinaryLogProviderTest.InvocationCountMarshaller<String>() {
        @Override
        Marshaller<String> delegate() {
            return BinaryLogProviderTest.StringMarshaller.INSTANCE;
        }
    };

    private final BinaryLogProviderTest.InvocationCountMarshaller<Integer> respMarshaller = new BinaryLogProviderTest.InvocationCountMarshaller<Integer>() {
        @Override
        Marshaller<Integer> delegate() {
            return BinaryLogProviderTest.IntegerMarshaller.INSTANCE;
        }
    };

    private final MethodDescriptor<String, Integer> method = MethodDescriptor.newBuilder(reqMarshaller, respMarshaller).setFullMethodName("myservice/mymethod").setType(UNARY).setSchemaDescriptor(new Object()).setIdempotent(true).setSafe(true).setSampledToLocalTracing(true).build();

    private final List<byte[]> binlogReq = new ArrayList<>();

    private final List<byte[]> binlogResp = new ArrayList<>();

    private final BinaryLogProvider binlogProvider = new BinaryLogProvider() {
        @Override
        public ServerInterceptor getServerInterceptor(String fullMethodName) {
            return new BinaryLogProviderTest.TestBinaryLogServerInterceptor();
        }

        @Override
        public ClientInterceptor getClientInterceptor(String fullMethodName, CallOptions callOptions) {
            return new BinaryLogProviderTest.TestBinaryLogClientInterceptor();
        }
    };

    @Test
    public void wrapChannel_methodDescriptor() throws Exception {
        final AtomicReference<MethodDescriptor<?, ?>> methodRef = new AtomicReference<>();
        Channel channel = new Channel() {
            @Override
            public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> method, CallOptions callOptions) {
                methodRef.set(method);
                return new io.grpc.internal.NoopClientCall();
            }

            @Override
            public String authority() {
                throw new UnsupportedOperationException();
            }
        };
        Channel wChannel = binlogProvider.wrapChannel(channel);
        ClientCall<String, Integer> unusedClientCall = wChannel.newCall(method, DEFAULT);
        validateWrappedMethod(methodRef.get());
    }

    @Test
    public void wrapChannel_handler() throws Exception {
        final List<byte[]> serializedReq = new ArrayList<>();
        final AtomicReference<ClientCall.Listener<?>> listener = new AtomicReference<>();
        Channel channel = new Channel() {
            @Override
            public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
                return new io.grpc.internal.NoopClientCall<RequestT, ResponseT>() {
                    @Override
                    public void start(Listener<ResponseT> responseListener, Metadata headers) {
                        listener.set(responseListener);
                    }

                    @Override
                    public void sendMessage(RequestT message) {
                        serializedReq.add(((byte[]) (message)));
                    }
                };
            }

            @Override
            public String authority() {
                throw new UnsupportedOperationException();
            }
        };
        Channel wChannel = binlogProvider.wrapChannel(channel);
        ClientCall<String, Integer> clientCall = wChannel.newCall(method, DEFAULT);
        final List<Integer> observedResponse = new ArrayList<>();
        clientCall.start(new io.grpc.internal.NoopClientCall.NoopClientCallListener<Integer>() {
            @Override
            public void onMessage(Integer message) {
                observedResponse.add(message);
            }
        }, new Metadata());
        String expectedRequest = "hello world";
        assertThat(binlogReq).isEmpty();
        assertThat(serializedReq).isEmpty();
        Assert.assertEquals(0, reqMarshaller.streamInvocations);
        clientCall.sendMessage(expectedRequest);
        // it is unacceptably expensive for the binlog to double parse every logged message
        Assert.assertEquals(1, reqMarshaller.streamInvocations);
        Assert.assertEquals(0, reqMarshaller.parseInvocations);
        assertThat(binlogReq).hasSize(1);
        assertThat(serializedReq).hasSize(1);
        Assert.assertEquals(expectedRequest, BinaryLogProviderTest.StringMarshaller.INSTANCE.parse(new ByteArrayInputStream(binlogReq.get(0))));
        Assert.assertEquals(expectedRequest, BinaryLogProviderTest.StringMarshaller.INSTANCE.parse(new ByteArrayInputStream(serializedReq.get(0))));
        int expectedResponse = 12345;
        assertThat(binlogResp).isEmpty();
        assertThat(observedResponse).isEmpty();
        Assert.assertEquals(0, respMarshaller.parseInvocations);
        BinaryLogProviderTest.onClientMessageHelper(listener.get(), BinaryLogProviderTest.IntegerMarshaller.INSTANCE.stream(expectedResponse));
        // it is unacceptably expensive for the binlog to double parse every logged message
        Assert.assertEquals(1, respMarshaller.parseInvocations);
        Assert.assertEquals(0, respMarshaller.streamInvocations);
        assertThat(binlogResp).hasSize(1);
        assertThat(observedResponse).hasSize(1);
        Assert.assertEquals(expectedResponse, ((int) (BinaryLogProviderTest.IntegerMarshaller.INSTANCE.parse(new ByteArrayInputStream(binlogResp.get(0))))));
        Assert.assertEquals(expectedResponse, ((int) (observedResponse.get(0))));
    }

    @Test
    public void wrapMethodDefinition_methodDescriptor() throws Exception {
        ServerMethodDefinition<String, Integer> methodDef = ServerMethodDefinition.create(method, new io.grpc.ServerCallHandler<String, Integer>() {
            @Override
            public Listener<String> startCall(ServerCall<String, Integer> call, Metadata headers) {
                throw new UnsupportedOperationException();
            }
        });
        ServerMethodDefinition<?, ?> wMethodDef = binlogProvider.wrapMethodDefinition(methodDef);
        validateWrappedMethod(wMethodDef.getMethodDescriptor());
    }

    @Test
    public void wrapMethodDefinition_handler() throws Exception {
        // The request as seen by the user supplied server code
        final List<String> observedRequest = new ArrayList<>();
        final AtomicReference<ServerCall<String, Integer>> serverCall = new AtomicReference<>();
        ServerMethodDefinition<String, Integer> methodDef = ServerMethodDefinition.create(method, new io.grpc.ServerCallHandler<String, Integer>() {
            @Override
            public ServerCall.Listener<String> startCall(ServerCall<String, Integer> call, Metadata headers) {
                serverCall.set(call);
                return new ServerCall.Listener<String>() {
                    @Override
                    public void onMessage(String message) {
                        observedRequest.add(message);
                    }
                };
            }
        });
        ServerMethodDefinition<?, ?> wDef = binlogProvider.wrapMethodDefinition(methodDef);
        List<Object> serializedResp = new ArrayList<>();
        ServerCall.Listener<?> wListener = startServerCallHelper(wDef, serializedResp);
        String expectedRequest = "hello world";
        assertThat(binlogReq).isEmpty();
        assertThat(observedRequest).isEmpty();
        Assert.assertEquals(0, reqMarshaller.parseInvocations);
        BinaryLogProviderTest.onServerMessageHelper(wListener, BinaryLogProviderTest.StringMarshaller.INSTANCE.stream(expectedRequest));
        // it is unacceptably expensive for the binlog to double parse every logged message
        Assert.assertEquals(1, reqMarshaller.parseInvocations);
        Assert.assertEquals(0, reqMarshaller.streamInvocations);
        assertThat(binlogReq).hasSize(1);
        assertThat(observedRequest).hasSize(1);
        Assert.assertEquals(expectedRequest, BinaryLogProviderTest.StringMarshaller.INSTANCE.parse(new ByteArrayInputStream(binlogReq.get(0))));
        Assert.assertEquals(expectedRequest, observedRequest.get(0));
        int expectedResponse = 12345;
        assertThat(binlogResp).isEmpty();
        assertThat(serializedResp).isEmpty();
        Assert.assertEquals(0, respMarshaller.streamInvocations);
        serverCall.get().sendMessage(expectedResponse);
        // it is unacceptably expensive for the binlog to double parse every logged message
        Assert.assertEquals(0, respMarshaller.parseInvocations);
        Assert.assertEquals(1, respMarshaller.streamInvocations);
        assertThat(binlogResp).hasSize(1);
        assertThat(serializedResp).hasSize(1);
        Assert.assertEquals(expectedResponse, ((int) (BinaryLogProviderTest.IntegerMarshaller.INSTANCE.parse(new ByteArrayInputStream(binlogResp.get(0))))));
        Assert.assertEquals(expectedResponse, ((int) (method.parseResponse(new ByteArrayInputStream(((byte[]) (serializedResp.get(0))))))));
    }

    private final class TestBinaryLogClientInterceptor implements ClientInterceptor {
        @Override
        public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
            Assert.assertSame(BYTEARRAY_MARSHALLER, method.getRequestMarshaller());
            Assert.assertSame(BYTEARRAY_MARSHALLER, method.getResponseMarshaller());
            return new io.grpc.ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                @Override
                public void start(Listener<RespT> responseListener, Metadata headers) {
                    super.start(new io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                        @Override
                        public void onMessage(RespT message) {
                            Assert.assertTrue((message instanceof InputStream));
                            try {
                                byte[] bytes = IoUtils.toByteArray(((InputStream) (message)));
                                binlogResp.add(bytes);
                                ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                                RespT dup = method.parseResponse(input);
                                super.onMessage(dup);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, headers);
                }

                @Override
                public void sendMessage(ReqT message) {
                    byte[] bytes = ((byte[]) (message));
                    binlogReq.add(bytes);
                    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                    ReqT dup = method.parseRequest(input);
                    super.sendMessage(dup);
                }
            };
        }
    }

    private final class TestBinaryLogServerInterceptor implements ServerInterceptor {
        @Override
        public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, Metadata headers, io.grpc.ServerCallHandler<ReqT, RespT> next) {
            Assert.assertSame(BYTEARRAY_MARSHALLER, call.getMethodDescriptor().getRequestMarshaller());
            Assert.assertSame(BYTEARRAY_MARSHALLER, call.getMethodDescriptor().getResponseMarshaller());
            ServerCall<ReqT, RespT> wCall = new io.grpc.ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                @Override
                public void sendMessage(RespT message) {
                    byte[] bytes = ((byte[]) (message));
                    binlogResp.add(bytes);
                    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                    RespT dup = call.getMethodDescriptor().parseResponse(input);
                    super.sendMessage(dup);
                }
            };
            final ServerCall.Listener<ReqT> oListener = next.startCall(wCall, headers);
            return new io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(oListener) {
                @Override
                public void onMessage(ReqT message) {
                    Assert.assertTrue((message instanceof InputStream));
                    try {
                        byte[] bytes = IoUtils.toByteArray(((InputStream) (message)));
                        binlogReq.add(bytes);
                        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                        ReqT dup = call.getMethodDescriptor().parseRequest(input);
                        super.onMessage(dup);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }

    private abstract static class InvocationCountMarshaller<T> implements MethodDescriptor.Marshaller<T> {
        private int streamInvocations = 0;

        private int parseInvocations = 0;

        abstract MethodDescriptor.Marshaller<T> delegate();

        @Override
        public InputStream stream(T value) {
            (streamInvocations)++;
            return BinaryLogProviderTest.InvocationCountMarshaller.delegate().stream(value);
        }

        @Override
        public T parse(InputStream stream) {
            (parseInvocations)++;
            return BinaryLogProviderTest.InvocationCountMarshaller.delegate().parse(stream);
        }
    }

    private static class StringMarshaller implements MethodDescriptor.Marshaller<String> {
        public static final BinaryLogProviderTest.StringMarshaller INSTANCE = new BinaryLogProviderTest.StringMarshaller();

        @Override
        public InputStream stream(String value) {
            return new ByteArrayInputStream(value.getBytes(Charsets.UTF_8));
        }

        @Override
        public String parse(InputStream stream) {
            try {
                return new String(ByteStreams.toByteArray(stream), Charsets.UTF_8);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class IntegerMarshaller implements MethodDescriptor.Marshaller<Integer> {
        public static final BinaryLogProviderTest.IntegerMarshaller INSTANCE = new BinaryLogProviderTest.IntegerMarshaller();

        @Override
        public InputStream stream(Integer value) {
            return BinaryLogProviderTest.StringMarshaller.INSTANCE.stream(value.toString());
        }

        @Override
        public Integer parse(InputStream stream) {
            return Integer.valueOf(BinaryLogProviderTest.StringMarshaller.INSTANCE.parse(stream));
        }
    }
}

