/**
 * -
 * -\-\-
 * Helios Client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.client;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.spotify.sshagentproxy.AgentProxy;
import com.spotify.sshagentproxy.Identity;
import com.spotify.sshagenttls.HttpsHandler;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AuthenticatingHttpConnectorTest {
    private static final String USER = "user";

    private static final Path CERTIFICATE_PATH = Paths.get(Resources.getResource("UIDCACert.pem").getPath());

    private static final Path KEY_PATH = Paths.get(Resources.getResource("UIDCACert.key").getPath());

    private final DefaultHttpConnector connector = Mockito.mock(DefaultHttpConnector.class);

    private final String method = "GET";

    private final byte[] entity = new byte[0];

    private final Map<String, List<String>> headers = new HashMap<>();

    private List<Endpoint> endpoints;

    @Test
    public void testNoIdentities_ResponseIsOk() throws Exception {
        final AuthenticatingHttpConnector authConnector = createAuthenticatingConnector(Optional.<AgentProxy>absent(), ImmutableList.<Identity>of());
        final String path = "/foo/bar";
        final HttpsURLConnection connection = Mockito.mock(HttpsURLConnection.class);
        Mockito.when(connector.connect(ArgumentMatchers.argThat(matchesAnyEndpoint(path)), ArgumentMatchers.eq(method), ArgumentMatchers.eq(entity), ArgumentMatchers.eq(headers))).thenReturn(connection);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        final URI uri = new URI(("https://helios" + path));
        authConnector.connect(uri, method, entity, headers);
        Mockito.verify(connector, Mockito.never()).setExtraHttpsHandler(ArgumentMatchers.any(HttpsHandler.class));
    }

    @Test
    public void testAccessToken_ResponseIsOk() throws Exception {
        final AuthenticatingHttpConnector authConnector = createAuthenticatingConnectorWithAccessToken(Optional.<AgentProxy>absent(), ImmutableList.<Identity>of());
        final String path = "/foo/bar";
        final HttpsURLConnection connection = Mockito.mock(HttpsURLConnection.class);
        Mockito.when(connector.connect(ArgumentMatchers.argThat(matchesAnyEndpoint(path)), ArgumentMatchers.eq(method), ArgumentMatchers.eq(entity), ArgumentMatchers.argThat(hasKeys(Collections.singletonList("Authorization"))))).thenReturn(connection);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        final URI uri = new URI(("https://helios" + path));
        final HttpURLConnection returnedConnection = authConnector.connect(uri, method, entity, headers);
        Assert.assertSame(returnedConnection, connection);
    }

    @Test
    public void testAccessToken_UsesAgentIdentities() throws Exception {
        final AgentProxy proxy = Mockito.mock(AgentProxy.class);
        final Identity identity = mockIdentity();
        final AuthenticatingHttpConnector authConnector = createAuthenticatingConnectorWithAccessToken(Optional.of(proxy), ImmutableList.of(identity));
        final String path = "/foo/bar";
        final HttpsURLConnection connection = Mockito.mock(HttpsURLConnection.class);
        Mockito.when(connector.connect(ArgumentMatchers.argThat(matchesAnyEndpoint(path)), ArgumentMatchers.eq(method), ArgumentMatchers.eq(entity), ArgumentMatchers.argThat(hasKeys(Collections.singletonList("Authorization"))))).thenReturn(connection);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        final URI uri = new URI(("https://helios" + path));
        final HttpURLConnection returnedConnection = authConnector.connect(uri, method, entity, headers);
        Assert.assertSame(returnedConnection, connection);
        Mockito.verify(connector).setExtraHttpsHandler(ArgumentMatchers.isA(HttpsHandler.class));
    }

    @Test
    public void testCertFile_ResponseIsOk() throws Exception {
        final AuthenticatingHttpConnector authConnector = createAuthenticatingConnectorWithCertFile();
        final String path = "/foo/bar";
        final HttpsURLConnection connection = Mockito.mock(HttpsURLConnection.class);
        Mockito.when(connector.connect(ArgumentMatchers.argThat(matchesAnyEndpoint(path)), ArgumentMatchers.eq(method), ArgumentMatchers.eq(entity), ArgumentMatchers.eq(headers))).thenReturn(connection);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        final URI uri = new URI(("https://helios" + path));
        authConnector.connect(uri, method, entity, headers);
        Mockito.verify(connector).setExtraHttpsHandler(ArgumentMatchers.isA(HttpsHandler.class));
    }

    @Test
    public void testOneIdentity_ResponseIsOk() throws Exception {
        final AgentProxy proxy = Mockito.mock(AgentProxy.class);
        final Identity identity = mockIdentity();
        final AuthenticatingHttpConnector authConnector = createAuthenticatingConnector(Optional.of(proxy), ImmutableList.of(identity));
        final String path = "/another/one";
        final HttpsURLConnection connection = Mockito.mock(HttpsURLConnection.class);
        Mockito.when(connector.connect(ArgumentMatchers.argThat(matchesAnyEndpoint(path)), ArgumentMatchers.eq(method), ArgumentMatchers.eq(entity), ArgumentMatchers.eq(headers))).thenReturn(connection);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        final URI uri = new URI(("https://helios" + path));
        authConnector.connect(uri, method, entity, headers);
        Mockito.verify(connector).setExtraHttpsHandler(ArgumentMatchers.isA(HttpsHandler.class));
    }

    @Test
    public void testOneIdentity_ResponseIsUnauthorized() throws Exception {
        final AgentProxy proxy = Mockito.mock(AgentProxy.class);
        final Identity identity = mockIdentity();
        final AuthenticatingHttpConnector authConnector = createAuthenticatingConnector(Optional.of(proxy), ImmutableList.of(identity));
        final String path = "/another/one";
        final HttpsURLConnection connection = Mockito.mock(HttpsURLConnection.class);
        Mockito.when(connector.connect(ArgumentMatchers.argThat(matchesAnyEndpoint(path)), ArgumentMatchers.eq(method), ArgumentMatchers.eq(entity), ArgumentMatchers.eq(headers))).thenReturn(connection);
        Mockito.when(connection.getResponseCode()).thenReturn(401);
        final URI uri = new URI(("https://helios" + path));
        final HttpURLConnection returnedConnection = authConnector.connect(uri, method, entity, headers);
        Mockito.verify(connector).setExtraHttpsHandler(ArgumentMatchers.isA(HttpsHandler.class));
        Assert.assertSame(("If there is only one identity do not expect any additional endpoints to " + "be called after the first returns Unauthorized"), returnedConnection, connection);
    }

    @Test
    public void testTwoIdentities_ResponseIsUnauthorized() throws Exception {
        final AgentProxy proxy = Mockito.mock(AgentProxy.class);
        final Identity id1 = mockIdentity();
        final Identity id2 = mockIdentity();
        final AuthenticatingHttpConnector authConnector = createAuthenticatingConnector(Optional.of(proxy), ImmutableList.of(id1, id2));
        final String path = "/another/one";
        // set up two seperate connect() calls - the first returns 401 and the second 200 OK
        final HttpsURLConnection connection1 = Mockito.mock(HttpsURLConnection.class);
        Mockito.when(connection1.getResponseCode()).thenReturn(401);
        final HttpsURLConnection connection2 = Mockito.mock(HttpsURLConnection.class);
        Mockito.when(connection2.getResponseCode()).thenReturn(200);
        Mockito.when(connector.connect(ArgumentMatchers.argThat(matchesAnyEndpoint(path)), ArgumentMatchers.eq(method), ArgumentMatchers.eq(entity), ArgumentMatchers.eq(headers))).thenReturn(connection1, connection2);
        final URI uri = new URI(("https://helios" + path));
        final HttpURLConnection returnedConnection = authConnector.connect(uri, method, entity, headers);
        Mockito.verify(connector, Mockito.times(2)).setExtraHttpsHandler(ArgumentMatchers.isA(HttpsHandler.class));
        Assert.assertSame("Expect returned connection to be the second one, with successful response code", returnedConnection, connection2);
    }

    @Test
    public void testOneIdentity_ServerReturns502BadGateway() throws Exception {
        final AgentProxy proxy = Mockito.mock(AgentProxy.class);
        final Identity identity = mockIdentity();
        final AuthenticatingHttpConnector authConnector = createAuthenticatingConnector(Optional.of(proxy), ImmutableList.of(identity));
        final String path = "/foobar";
        final HttpsURLConnection connection = Mockito.mock(HttpsURLConnection.class);
        Mockito.when(connector.connect(ArgumentMatchers.argThat(matchesAnyEndpoint(path)), ArgumentMatchers.eq(method), ArgumentMatchers.eq(entity), ArgumentMatchers.eq(headers))).thenReturn(connection);
        Mockito.when(connection.getResponseCode()).thenReturn(502);
        final URI uri = new URI(("https://helios" + path));
        final HttpURLConnection returnedConnection = authConnector.connect(uri, method, entity, headers);
        Assert.assertSame(("Expect client to forego making additional connections when " + "server returns 502 Bad Gateway"), returnedConnection, connection);
    }
}

