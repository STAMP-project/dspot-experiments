/**
 * Copyright 2015 The gRPC Authors
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
package io.grpc.auth;


import CallOptions.DEFAULT;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import Status.Code.UNAUTHENTICATED;
import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.Marshaller;
import io.grpc.Status;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
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
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Tests for {@link ClientAuthInterceptor}.
 */
@RunWith(JUnit4.class)
@Deprecated
public class ClientAuthInterceptorTest {
    private static final Metadata.Key<String> AUTHORIZATION = Key.of("Authorization", ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> EXTRA_AUTHORIZATION = Key.of("Extra-Authorization", ASCII_STRING_MARSHALLER);

    private final Executor executor = MoreExecutors.directExecutor();

    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    Credentials credentials;

    @Mock
    Marshaller<String> stringMarshaller;

    @Mock
    Marshaller<Integer> intMarshaller;

    MethodDescriptor<String, Integer> descriptor;

    @Mock
    ClientCall.Listener<Integer> listener;

    @Mock
    Channel channel;

    ClientAuthInterceptorTest.ClientCallRecorder call = new ClientAuthInterceptorTest.ClientCallRecorder();

    ClientAuthInterceptor interceptor;

    @Test
    public void testCopyCredentialToHeaders() throws IOException {
        ListMultimap<String, String> values = LinkedListMultimap.create();
        values.put("Authorization", "token1");
        values.put("Authorization", "token2");
        values.put("Extra-Authorization", "token3");
        values.put("Extra-Authorization", "token4");
        Mockito.when(credentials.getRequestMetadata(ArgumentMatchers.any(URI.class))).thenReturn(Multimaps.asMap(values));
        ClientCall<String, Integer> interceptedCall = interceptor.interceptCall(descriptor, DEFAULT, channel);
        Metadata headers = new Metadata();
        interceptedCall.start(listener, headers);
        Assert.assertEquals(listener, call.responseListener);
        Assert.assertEquals(headers, call.headers);
        Iterable<String> authorization = headers.getAll(io.grpc.auth.AUTHORIZATION);
        Assert.assertArrayEquals(new String[]{ "token1", "token2" }, Iterables.toArray(authorization, String.class));
        Iterable<String> extraAuthorization = headers.getAll(io.grpc.auth.EXTRA_AUTHORIZATION);
        Assert.assertArrayEquals(new String[]{ "token3", "token4" }, Iterables.toArray(extraAuthorization, String.class));
    }

    @Test
    public void testCredentialsThrows() throws IOException {
        Mockito.when(credentials.getRequestMetadata(ArgumentMatchers.any(URI.class))).thenThrow(new IOException("Broken"));
        ClientCall<String, Integer> interceptedCall = interceptor.interceptCall(descriptor, DEFAULT, channel);
        Metadata headers = new Metadata();
        interceptedCall.start(listener, headers);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(listener).onClose(statusCaptor.capture(), ArgumentMatchers.isA(Metadata.class));
        Assert.assertNull(headers.getAll(io.grpc.auth.AUTHORIZATION));
        Assert.assertNull(call.responseListener);
        Assert.assertNull(call.headers);
        Assert.assertEquals(UNAUTHENTICATED, statusCaptor.getValue().getCode());
        Assert.assertNotNull(statusCaptor.getValue().getCause());
    }

    @Test
    public void testWithOAuth2Credential() {
        final AccessToken token = new AccessToken("allyourbase", new Date(Long.MAX_VALUE));
        final OAuth2Credentials oAuth2Credentials = new OAuth2Credentials() {
            @Override
            public AccessToken refreshAccessToken() throws IOException {
                return token;
            }
        };
        interceptor = new ClientAuthInterceptor(oAuth2Credentials, executor);
        ClientCall<String, Integer> interceptedCall = interceptor.interceptCall(descriptor, DEFAULT, channel);
        Metadata headers = new Metadata();
        interceptedCall.start(listener, headers);
        Assert.assertEquals(listener, call.responseListener);
        Assert.assertEquals(headers, call.headers);
        Iterable<String> authorization = headers.getAll(io.grpc.auth.AUTHORIZATION);
        Assert.assertArrayEquals(new String[]{ "Bearer allyourbase" }, Iterables.toArray(authorization, String.class));
    }

    @Test
    public void verifyServiceUri() throws IOException {
        ClientCall<String, Integer> interceptedCall;
        Mockito.doReturn("example.com:443").when(channel).authority();
        interceptedCall = interceptor.interceptCall(descriptor, DEFAULT, channel);
        interceptedCall.start(listener, new Metadata());
        Mockito.verify(credentials).getRequestMetadata(URI.create("https://example.com/a.service"));
        interceptedCall.cancel("Cancel for test", null);
        Mockito.doReturn("example.com:123").when(channel).authority();
        interceptedCall = interceptor.interceptCall(descriptor, DEFAULT, channel);
        interceptedCall.start(listener, new Metadata());
        Mockito.verify(credentials).getRequestMetadata(URI.create("https://example.com:123/a.service"));
        interceptedCall.cancel("Cancel for test", null);
    }

    private static final class ClientCallRecorder extends ClientCall<String, Integer> {
        private ClientCall.Listener<Integer> responseListener;

        private Metadata headers;

        private int numMessages;

        private String cancelMessage;

        private Throwable cancelCause;

        private boolean halfClosed;

        private String sentMessage;

        @Override
        public void start(ClientCall.Listener<Integer> responseListener, Metadata headers) {
            this.responseListener = responseListener;
            this.headers = headers;
        }

        @Override
        public void request(int numMessages) {
            this.numMessages = numMessages;
        }

        @Override
        public void cancel(String message, Throwable cause) {
            this.cancelMessage = message;
            this.cancelCause = cause;
        }

        @Override
        public void halfClose() {
            halfClosed = true;
        }

        @Override
        public void sendMessage(String message) {
            sentMessage = message;
        }
    }
}

