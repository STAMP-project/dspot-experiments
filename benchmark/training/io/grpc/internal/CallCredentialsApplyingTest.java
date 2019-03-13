/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc.internal;


import Attributes.EMPTY;
import Attributes.Key;
import CallCredentials.MetadataApplier;
import GrpcAttributes.ATTR_SECURITY_LEVEL;
import Metadata.ASCII_STRING_MARSHALLER;
import MethodDescriptor.MethodType.UNKNOWN;
import SecurityLevel.INTEGRITY;
import SecurityLevel.NONE;
import Status.Code.UNAUTHENTICATED;
import Status.FAILED_PRECONDITION;
import io.grpc.Attributes;
import io.grpc.CallCredentials;
import io.grpc.CallCredentials.RequestInfo;
import io.grpc.CallOptions;
import io.grpc.ChannelLogger;
import io.grpc.IntegerMarshaller;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.StringMarshaller;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;


/**
 * Unit test for {@link CallCredentials} applying functionality implemented by {@link CallCredentialsApplyingTransportFactory} and {@link MetadataApplierImpl}.
 */
@RunWith(JUnit4.class)
public class CallCredentialsApplyingTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private ClientTransportFactory mockTransportFactory;

    @Mock
    private ConnectionClientTransport mockTransport;

    @Mock
    private ClientStream mockStream;

    @Mock
    private CallCredentials mockCreds;

    @Mock
    private Executor mockExecutor;

    @Mock
    private SocketAddress address;

    // Noop logger;
    @Mock
    private ChannelLogger channelLogger;

    private static final String AUTHORITY = "testauthority";

    private static final String USER_AGENT = "testuseragent";

    private static final Attributes.Key<String> ATTR_KEY = Key.create("somekey");

    private static final String ATTR_VALUE = "somevalue";

    private static final MethodDescriptor<String, Integer> method = MethodDescriptor.<String, Integer>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new IntegerMarshaller()).build();

    private static final Metadata.Key<String> ORIG_HEADER_KEY = Metadata.Key.of("header1", ASCII_STRING_MARSHALLER);

    private static final String ORIG_HEADER_VALUE = "some original header value";

    private static final Metadata.Key<String> CREDS_KEY = Metadata.Key.of("test-creds", ASCII_STRING_MARSHALLER);

    private static final String CREDS_VALUE = "some credentials";

    private final Metadata origHeaders = new Metadata();

    private ForwardingConnectionClientTransport transport;

    private CallOptions callOptions;

    @Test
    public void parameterPropagation_base() {
        Attributes transportAttrs = Attributes.newBuilder().set(io.grpc.internal.ATTR_KEY, CallCredentialsApplyingTest.ATTR_VALUE).build();
        Mockito.when(mockTransport.getAttributes()).thenReturn(transportAttrs);
        transport.newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions);
        ArgumentCaptor<RequestInfo> infoCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockCreds).applyRequestMetadata(infoCaptor.capture(), ArgumentMatchers.same(mockExecutor), ArgumentMatchers.any(MetadataApplier.class));
        RequestInfo info = infoCaptor.getValue();
        Assert.assertSame(transportAttrs, info.getTransportAttrs());
        Assert.assertSame(CallCredentialsApplyingTest.method, info.getMethodDescriptor());
        Assert.assertSame(CallCredentialsApplyingTest.AUTHORITY, info.getAuthority());
        Assert.assertSame(NONE, info.getSecurityLevel());
    }

    @Test
    public void parameterPropagation_overrideByCallOptions() {
        Attributes transportAttrs = Attributes.newBuilder().set(io.grpc.internal.ATTR_KEY, CallCredentialsApplyingTest.ATTR_VALUE).set(ATTR_SECURITY_LEVEL, INTEGRITY).build();
        Mockito.when(mockTransport.getAttributes()).thenReturn(transportAttrs);
        Executor anotherExecutor = Mockito.mock(Executor.class);
        transport.newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions.withAuthority("calloptions-authority").withExecutor(anotherExecutor));
        ArgumentCaptor<RequestInfo> infoCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockCreds).applyRequestMetadata(infoCaptor.capture(), ArgumentMatchers.same(anotherExecutor), ArgumentMatchers.any(MetadataApplier.class));
        RequestInfo info = infoCaptor.getValue();
        Assert.assertSame(transportAttrs, info.getTransportAttrs());
        Assert.assertSame(CallCredentialsApplyingTest.method, info.getMethodDescriptor());
        Assert.assertEquals("calloptions-authority", info.getAuthority());
        Assert.assertSame(INTEGRITY, info.getSecurityLevel());
    }

    @Test
    public void credentialThrows() {
        final RuntimeException ex = new RuntimeException();
        Mockito.when(mockTransport.getAttributes()).thenReturn(EMPTY);
        Mockito.doThrow(ex).when(mockCreds).applyRequestMetadata(ArgumentMatchers.any(RequestInfo.class), ArgumentMatchers.same(mockExecutor), ArgumentMatchers.any(MetadataApplier.class));
        FailingClientStream stream = ((FailingClientStream) (transport.newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions)));
        Mockito.verify(mockTransport, Mockito.never()).newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions);
        Assert.assertEquals(UNAUTHENTICATED, stream.getError().getCode());
        Assert.assertSame(ex, stream.getError().getCause());
    }

    @Test
    public void applyMetadata_inline() {
        Mockito.when(mockTransport.getAttributes()).thenReturn(EMPTY);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                CallCredentials.MetadataApplier applier = ((CallCredentials.MetadataApplier) (invocation.getArguments()[2]));
                Metadata headers = new Metadata();
                headers.put(io.grpc.internal.CREDS_KEY, CallCredentialsApplyingTest.CREDS_VALUE);
                applier.apply(headers);
                return null;
            }
        }).when(mockCreds).applyRequestMetadata(ArgumentMatchers.any(RequestInfo.class), ArgumentMatchers.same(mockExecutor), ArgumentMatchers.any(MetadataApplier.class));
        ClientStream stream = transport.newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions);
        Mockito.verify(mockTransport).newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions);
        Assert.assertSame(mockStream, stream);
        Assert.assertEquals(CallCredentialsApplyingTest.CREDS_VALUE, origHeaders.get(io.grpc.internal.CREDS_KEY));
        Assert.assertEquals(CallCredentialsApplyingTest.ORIG_HEADER_VALUE, origHeaders.get(io.grpc.internal.ORIG_HEADER_KEY));
    }

    @Test
    public void fail_inline() {
        final Status error = FAILED_PRECONDITION.withDescription("channel not secure for creds");
        Mockito.when(mockTransport.getAttributes()).thenReturn(EMPTY);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                CallCredentials.MetadataApplier applier = ((CallCredentials.MetadataApplier) (invocation.getArguments()[2]));
                applier.fail(error);
                return null;
            }
        }).when(mockCreds).applyRequestMetadata(ArgumentMatchers.any(RequestInfo.class), ArgumentMatchers.same(mockExecutor), ArgumentMatchers.any(MetadataApplier.class));
        FailingClientStream stream = ((FailingClientStream) (transport.newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions)));
        Mockito.verify(mockTransport, Mockito.never()).newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions);
        Assert.assertSame(error, stream.getError());
    }

    @Test
    public void applyMetadata_delayed() {
        Mockito.when(mockTransport.getAttributes()).thenReturn(EMPTY);
        // Will call applyRequestMetadata(), which is no-op.
        DelayedStream stream = ((DelayedStream) (transport.newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions)));
        ArgumentCaptor<CallCredentials.MetadataApplier> applierCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockCreds).applyRequestMetadata(ArgumentMatchers.any(RequestInfo.class), ArgumentMatchers.same(mockExecutor), applierCaptor.capture());
        Mockito.verify(mockTransport, Mockito.never()).newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions);
        Metadata headers = new Metadata();
        headers.put(io.grpc.internal.CREDS_KEY, CallCredentialsApplyingTest.CREDS_VALUE);
        applierCaptor.getValue().apply(headers);
        Mockito.verify(mockTransport).newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions);
        Assert.assertSame(mockStream, stream.getRealStream());
        Assert.assertEquals(CallCredentialsApplyingTest.CREDS_VALUE, origHeaders.get(io.grpc.internal.CREDS_KEY));
        Assert.assertEquals(CallCredentialsApplyingTest.ORIG_HEADER_VALUE, origHeaders.get(io.grpc.internal.ORIG_HEADER_KEY));
    }

    @Test
    public void fail_delayed() {
        Mockito.when(mockTransport.getAttributes()).thenReturn(EMPTY);
        // Will call applyRequestMetadata(), which is no-op.
        DelayedStream stream = ((DelayedStream) (transport.newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions)));
        ArgumentCaptor<CallCredentials.MetadataApplier> applierCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockCreds).applyRequestMetadata(ArgumentMatchers.any(RequestInfo.class), ArgumentMatchers.same(mockExecutor), applierCaptor.capture());
        Status error = FAILED_PRECONDITION.withDescription("channel not secure for creds");
        applierCaptor.getValue().fail(error);
        Mockito.verify(mockTransport, Mockito.never()).newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions);
        FailingClientStream failingStream = ((FailingClientStream) (stream.getRealStream()));
        Assert.assertSame(error, failingStream.getError());
    }

    @Test
    public void noCreds() {
        callOptions = callOptions.withCallCredentials(null);
        ClientStream stream = transport.newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions);
        Mockito.verify(mockTransport).newStream(CallCredentialsApplyingTest.method, origHeaders, callOptions);
        Assert.assertSame(mockStream, stream);
        Assert.assertNull(origHeaders.get(io.grpc.internal.CREDS_KEY));
        Assert.assertEquals(CallCredentialsApplyingTest.ORIG_HEADER_VALUE, origHeaders.get(io.grpc.internal.ORIG_HEADER_KEY));
    }
}

