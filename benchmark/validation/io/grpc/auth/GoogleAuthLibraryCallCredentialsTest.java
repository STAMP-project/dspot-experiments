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
package io.grpc.auth;


import CallCredentials.RequestInfo;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.BINARY_BYTE_MARSHALLER;
import Metadata.Key;
import MethodDescriptor.MethodType.UNKNOWN;
import Status.Code.UNAUTHENTICATED;
import Status.Code.UNAVAILABLE;
import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import io.grpc.Attributes;
import io.grpc.CallCredentials;
import io.grpc.CallCredentials.MetadataApplier;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.SecurityLevel;
import io.grpc.Status;
import io.grpc.testing.TestMethodDescriptors;
import java.io.IOException;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Tests for {@link GoogleAuthLibraryCallCredentials}.
 */
@RunWith(JUnit4.class)
public class GoogleAuthLibraryCallCredentialsTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    private static final Metadata.Key<String> AUTHORIZATION = Key.of("Authorization", ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<byte[]> EXTRA_AUTHORIZATION = Key.of("Extra-Authorization-bin", BINARY_BYTE_MARSHALLER);

    @Mock
    private Credentials credentials;

    @Mock
    private MetadataApplier applier;

    private Executor executor = new Executor() {
        @Override
        public void execute(Runnable r) {
            pendingRunnables.add(r);
        }
    };

    @Captor
    private ArgumentCaptor<Metadata> headersCaptor;

    @Captor
    private ArgumentCaptor<Status> statusCaptor;

    private MethodDescriptor<Void, Void> method = MethodDescriptor.<Void, Void>newBuilder().setType(UNKNOWN).setFullMethodName("a.service/method").setRequestMarshaller(TestMethodDescriptors.voidMarshaller()).setResponseMarshaller(TestMethodDescriptors.voidMarshaller()).build();

    private URI expectedUri = URI.create("https://testauthority/a.service");

    private static final String AUTHORITY = "testauthority";

    private static final SecurityLevel SECURITY_LEVEL = SecurityLevel.PRIVACY_AND_INTEGRITY;

    private ArrayList<Runnable> pendingRunnables = new ArrayList<>();

    @Test
    public void copyCredentialsToHeaders() throws Exception {
        ListMultimap<String, String> values = LinkedListMultimap.create();
        values.put("Authorization", "token1");
        values.put("Authorization", "token2");
        values.put("Extra-Authorization-bin", "dG9rZW4z");// bytes "token3" in base64

        values.put("Extra-Authorization-bin", "dG9rZW40");// bytes "token4" in base64

        Mockito.when(credentials.getRequestMetadata(ArgumentMatchers.eq(expectedUri))).thenReturn(Multimaps.asMap(values));
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(), executor, applier);
        Mockito.verify(credentials).getRequestMetadata(ArgumentMatchers.eq(expectedUri));
        Mockito.verify(applier).apply(headersCaptor.capture());
        Metadata headers = headersCaptor.getValue();
        Iterable<String> authorization = headers.getAll(io.grpc.auth.AUTHORIZATION);
        Assert.assertArrayEquals(new String[]{ "token1", "token2" }, Iterables.toArray(authorization, String.class));
        Iterable<byte[]> extraAuthorization = headers.getAll(io.grpc.auth.EXTRA_AUTHORIZATION);
        Assert.assertEquals(2, Iterables.size(extraAuthorization));
        Assert.assertArrayEquals("token3".getBytes(Charsets.US_ASCII), Iterables.get(extraAuthorization, 0));
        Assert.assertArrayEquals("token4".getBytes(Charsets.US_ASCII), Iterables.get(extraAuthorization, 1));
    }

    @Test
    public void invalidBase64() throws Exception {
        ListMultimap<String, String> values = LinkedListMultimap.create();
        values.put("Extra-Authorization-bin", "dG9rZW4z1");// invalid base64

        Mockito.when(credentials.getRequestMetadata(ArgumentMatchers.eq(expectedUri))).thenReturn(Multimaps.asMap(values));
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(), executor, applier);
        Mockito.verify(credentials).getRequestMetadata(ArgumentMatchers.eq(expectedUri));
        Mockito.verify(applier).fail(statusCaptor.capture());
        Status status = statusCaptor.getValue();
        Assert.assertEquals(UNAUTHENTICATED, status.getCode());
        Assert.assertEquals(IllegalArgumentException.class, status.getCause().getClass());
    }

    @Test
    public void credentialsFailsWithIoException() throws Exception {
        Exception exception = new IOException("Broken");
        Mockito.when(credentials.getRequestMetadata(ArgumentMatchers.eq(expectedUri))).thenThrow(exception);
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(), executor, applier);
        Mockito.verify(credentials).getRequestMetadata(ArgumentMatchers.eq(expectedUri));
        Mockito.verify(applier).fail(statusCaptor.capture());
        Status status = statusCaptor.getValue();
        Assert.assertEquals(UNAVAILABLE, status.getCode());
        Assert.assertEquals(exception, status.getCause());
    }

    @Test
    public void credentialsFailsWithRuntimeException() throws Exception {
        Exception exception = new RuntimeException("Broken");
        Mockito.when(credentials.getRequestMetadata(ArgumentMatchers.eq(expectedUri))).thenThrow(exception);
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(), executor, applier);
        Mockito.verify(credentials).getRequestMetadata(ArgumentMatchers.eq(expectedUri));
        Mockito.verify(applier).fail(statusCaptor.capture());
        Status status = statusCaptor.getValue();
        Assert.assertEquals(UNAUTHENTICATED, status.getCode());
        Assert.assertEquals(exception, status.getCause());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void credentialsReturnNullMetadata() throws Exception {
        ListMultimap<String, String> values = LinkedListMultimap.create();
        values.put("Authorization", "token1");
        Mockito.when(credentials.getRequestMetadata(ArgumentMatchers.eq(expectedUri))).thenReturn(null, Multimaps.asMap(values), null);
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        for (int i = 0; i < 3; i++) {
            callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(), executor, applier);
        }
        Mockito.verify(credentials, Mockito.times(3)).getRequestMetadata(ArgumentMatchers.eq(expectedUri));
        Mockito.verify(applier, Mockito.times(3)).apply(headersCaptor.capture());
        List<Metadata> headerList = headersCaptor.getAllValues();
        Assert.assertEquals(3, headerList.size());
        Assert.assertEquals(0, headerList.get(0).keys().size());
        Iterable<String> authorization = headerList.get(1).getAll(io.grpc.auth.AUTHORIZATION);
        Assert.assertArrayEquals(new String[]{ "token1" }, Iterables.toArray(authorization, String.class));
        Assert.assertEquals(0, headerList.get(2).keys().size());
    }

    @Test
    public void oauth2Credential() {
        final AccessToken token = new AccessToken("allyourbase", new Date(Long.MAX_VALUE));
        final OAuth2Credentials credentials = new OAuth2Credentials() {
            @Override
            public AccessToken refreshAccessToken() throws IOException {
                return token;
            }
        };
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(SecurityLevel.NONE), executor, applier);
        Assert.assertEquals(1, runPendingRunnables());
        Mockito.verify(applier).apply(headersCaptor.capture());
        Metadata headers = headersCaptor.getValue();
        Iterable<String> authorization = headers.getAll(io.grpc.auth.AUTHORIZATION);
        Assert.assertArrayEquals(new String[]{ "Bearer allyourbase" }, Iterables.toArray(authorization, String.class));
    }

    @Test
    public void googleCredential_privacyAndIntegrityAllowed() {
        final AccessToken token = new AccessToken("allyourbase", new Date(Long.MAX_VALUE));
        final Credentials credentials = GoogleCredentials.create(token);
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(SecurityLevel.PRIVACY_AND_INTEGRITY), executor, applier);
        runPendingRunnables();
        Mockito.verify(applier).apply(headersCaptor.capture());
        Metadata headers = headersCaptor.getValue();
        Iterable<String> authorization = headers.getAll(io.grpc.auth.AUTHORIZATION);
        Assert.assertArrayEquals(new String[]{ "Bearer allyourbase" }, Iterables.toArray(authorization, String.class));
    }

    @Test
    public void googleCredential_integrityDenied() {
        final AccessToken token = new AccessToken("allyourbase", new Date(Long.MAX_VALUE));
        final Credentials credentials = GoogleCredentials.create(token);
        // Anything less than PRIVACY_AND_INTEGRITY should fail
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(SecurityLevel.INTEGRITY), executor, applier);
        runPendingRunnables();
        Mockito.verify(applier).fail(statusCaptor.capture());
        Status status = statusCaptor.getValue();
        Assert.assertEquals(UNAUTHENTICATED, status.getCode());
    }

    @Test
    public void serviceUri() throws Exception {
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl("example.com:443"), executor, applier);
        Mockito.verify(credentials).getRequestMetadata(ArgumentMatchers.eq(new URI("https://example.com/a.service")));
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl("example.com:123"), executor, applier);
        Mockito.verify(credentials).getRequestMetadata(ArgumentMatchers.eq(new URI("https://example.com:123/a.service")));
    }

    @Test
    public void serviceAccountToJwt() throws Exception {
        KeyPair pair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        @SuppressWarnings("deprecation")
        ServiceAccountCredentials credentials = new ServiceAccountCredentials(null, "email@example.com", pair.getPrivate(), null, null) {
            @Override
            public AccessToken refreshAccessToken() {
                throw new AssertionError();
            }
        };
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(), executor, applier);
        Assert.assertEquals(0, runPendingRunnables());
        Mockito.verify(applier).apply(headersCaptor.capture());
        Metadata headers = headersCaptor.getValue();
        String[] authorization = Iterables.toArray(headers.getAll(io.grpc.auth.AUTHORIZATION), String.class);
        Assert.assertEquals(1, authorization.length);
        Assert.assertTrue(authorization[0], authorization[0].startsWith("Bearer "));
        // JWT is reasonably long. Normal tokens aren't.
        Assert.assertTrue(authorization[0], ((authorization[0].length()) > 300));
    }

    @Test
    public void serviceAccountWithScopeNotToJwt() throws Exception {
        final AccessToken token = new AccessToken("allyourbase", new Date(Long.MAX_VALUE));
        KeyPair pair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        @SuppressWarnings("deprecation")
        ServiceAccountCredentials credentials = new ServiceAccountCredentials(null, "email@example.com", pair.getPrivate(), null, Arrays.asList("somescope")) {
            @Override
            public AccessToken refreshAccessToken() {
                return token;
            }
        };
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(), executor, applier);
        Assert.assertEquals(1, runPendingRunnables());
        Mockito.verify(applier).apply(headersCaptor.capture());
        Metadata headers = headersCaptor.getValue();
        Iterable<String> authorization = headers.getAll(io.grpc.auth.AUTHORIZATION);
        Assert.assertArrayEquals(new String[]{ "Bearer allyourbase" }, Iterables.toArray(authorization, String.class));
    }

    @Test
    public void oauthClassesNotInClassPath() throws Exception {
        ListMultimap<String, String> values = LinkedListMultimap.create();
        values.put("Authorization", "token1");
        Mockito.when(credentials.getRequestMetadata(ArgumentMatchers.eq(expectedUri))).thenReturn(Multimaps.asMap(values));
        Assert.assertNull(GoogleAuthLibraryCallCredentials.createJwtHelperOrNull(null));
        GoogleAuthLibraryCallCredentials callCredentials = new GoogleAuthLibraryCallCredentials(credentials, null);
        callCredentials.applyRequestMetadata(new GoogleAuthLibraryCallCredentialsTest.RequestInfoImpl(), executor, applier);
        Mockito.verify(credentials).getRequestMetadata(ArgumentMatchers.eq(expectedUri));
        Mockito.verify(applier).apply(headersCaptor.capture());
        Metadata headers = headersCaptor.getValue();
        Iterable<String> authorization = headers.getAll(io.grpc.auth.AUTHORIZATION);
        Assert.assertArrayEquals(new String[]{ "token1" }, Iterables.toArray(authorization, String.class));
    }

    private final class RequestInfoImpl extends CallCredentials.RequestInfo {
        final String authority;

        final SecurityLevel securityLevel;

        RequestInfoImpl() {
            this(GoogleAuthLibraryCallCredentialsTest.AUTHORITY, GoogleAuthLibraryCallCredentialsTest.SECURITY_LEVEL);
        }

        RequestInfoImpl(SecurityLevel securityLevel) {
            this(GoogleAuthLibraryCallCredentialsTest.AUTHORITY, securityLevel);
        }

        RequestInfoImpl(String authority) {
            this(authority, GoogleAuthLibraryCallCredentialsTest.SECURITY_LEVEL);
        }

        RequestInfoImpl(String authority, SecurityLevel securityLevel) {
            this.authority = authority;
            this.securityLevel = securityLevel;
        }

        @Override
        public MethodDescriptor<?, ?> getMethodDescriptor() {
            return method;
        }

        @Override
        public SecurityLevel getSecurityLevel() {
            return securityLevel;
        }

        @Override
        public String getAuthority() {
            return authority;
        }

        @Override
        public Attributes getTransportAttrs() {
            return Attributes.EMPTY;
        }
    }
}

