/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.security.auth;


import KafkaPrincipal.ANONYMOUS;
import KafkaPrincipal.USER_TYPE;
import SaslConfigs.GSSAPI_MECHANISM;
import ScramMechanism.SCRAM_SHA_256;
import SecurityProtocol.PLAINTEXT;
import SecurityProtocol.SASL_PLAINTEXT;
import java.net.InetAddress;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;
import javax.security.sasl.SaslServer;
import org.apache.kafka.common.network.Authenticator;
import org.apache.kafka.common.network.TransportLayer;
import org.apache.kafka.common.security.authenticator.DefaultKafkaPrincipalBuilder;
import org.apache.kafka.common.security.kerberos.KerberosShortNamer;
import org.apache.kafka.common.security.ssl.SslPrincipalMapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static SecurityProtocol.SASL_PLAINTEXT;


public class DefaultKafkaPrincipalBuilderTest {
    @Test
    @SuppressWarnings("deprecation")
    public void testUseOldPrincipalBuilderForPlaintextIfProvided() throws Exception {
        TransportLayer transportLayer = Mockito.mock(TransportLayer.class);
        Authenticator authenticator = Mockito.mock(Authenticator.class);
        PrincipalBuilder oldPrincipalBuilder = Mockito.mock(PrincipalBuilder.class);
        Mockito.when(oldPrincipalBuilder.buildPrincipal(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(new DefaultKafkaPrincipalBuilderTest.DummyPrincipal("foo"));
        DefaultKafkaPrincipalBuilder builder = DefaultKafkaPrincipalBuilder.fromOldPrincipalBuilder(authenticator, transportLayer, oldPrincipalBuilder, null);
        KafkaPrincipal principal = builder.build(new PlaintextAuthenticationContext(InetAddress.getLocalHost(), PLAINTEXT.name()));
        Assert.assertEquals(USER_TYPE, principal.getPrincipalType());
        Assert.assertEquals("foo", principal.getName());
        builder.close();
        Mockito.verify(oldPrincipalBuilder).buildPrincipal(transportLayer, authenticator);
        Mockito.verify(oldPrincipalBuilder).close();
    }

    @Test
    public void testReturnAnonymousPrincipalForPlaintext() throws Exception {
        try (DefaultKafkaPrincipalBuilder builder = new DefaultKafkaPrincipalBuilder(null, null)) {
            Assert.assertEquals(ANONYMOUS, builder.build(new PlaintextAuthenticationContext(InetAddress.getLocalHost(), PLAINTEXT.name())));
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testUseOldPrincipalBuilderForSslIfProvided() throws Exception {
        TransportLayer transportLayer = Mockito.mock(TransportLayer.class);
        Authenticator authenticator = Mockito.mock(Authenticator.class);
        PrincipalBuilder oldPrincipalBuilder = Mockito.mock(PrincipalBuilder.class);
        SSLSession session = Mockito.mock(SSLSession.class);
        Mockito.when(oldPrincipalBuilder.buildPrincipal(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(new DefaultKafkaPrincipalBuilderTest.DummyPrincipal("foo"));
        DefaultKafkaPrincipalBuilder builder = DefaultKafkaPrincipalBuilder.fromOldPrincipalBuilder(authenticator, transportLayer, oldPrincipalBuilder, null);
        KafkaPrincipal principal = builder.build(new SslAuthenticationContext(session, InetAddress.getLocalHost(), PLAINTEXT.name()));
        Assert.assertEquals(USER_TYPE, principal.getPrincipalType());
        Assert.assertEquals("foo", principal.getName());
        builder.close();
        Mockito.verify(oldPrincipalBuilder).buildPrincipal(transportLayer, authenticator);
        Mockito.verify(oldPrincipalBuilder).close();
    }

    @Test
    public void testUseSessionPeerPrincipalForSsl() throws Exception {
        SSLSession session = Mockito.mock(SSLSession.class);
        Mockito.when(session.getPeerPrincipal()).thenReturn(new DefaultKafkaPrincipalBuilderTest.DummyPrincipal("foo"));
        DefaultKafkaPrincipalBuilder builder = new DefaultKafkaPrincipalBuilder(null, null);
        KafkaPrincipal principal = builder.build(new SslAuthenticationContext(session, InetAddress.getLocalHost(), PLAINTEXT.name()));
        Assert.assertEquals(USER_TYPE, principal.getPrincipalType());
        Assert.assertEquals("foo", principal.getName());
        builder.close();
        Mockito.verify(session, Mockito.atLeastOnce()).getPeerPrincipal();
    }

    @Test
    public void testPrincipalIfSSLPeerIsNotAuthenticated() throws Exception {
        SSLSession session = Mockito.mock(SSLSession.class);
        Mockito.when(session.getPeerPrincipal()).thenReturn(ANONYMOUS);
        DefaultKafkaPrincipalBuilder builder = new DefaultKafkaPrincipalBuilder(null, null);
        KafkaPrincipal principal = builder.build(new SslAuthenticationContext(session, InetAddress.getLocalHost(), PLAINTEXT.name()));
        Assert.assertEquals(ANONYMOUS, principal);
        builder.close();
        Mockito.verify(session, Mockito.atLeastOnce()).getPeerPrincipal();
    }

    @Test
    public void testPrincipalWithSslPrincipalMapper() throws Exception {
        SSLSession session = Mockito.mock(SSLSession.class);
        Mockito.when(session.getPeerPrincipal()).thenReturn(new X500Principal("CN=Duke, OU=ServiceUsers, O=Org, C=US")).thenReturn(new X500Principal("CN=Duke, OU=SME, O=mycp, L=Fulton, ST=MD, C=US")).thenReturn(new X500Principal("CN=duke, OU=JavaSoft, O=Sun Microsystems")).thenReturn(new X500Principal("OU=JavaSoft, O=Sun Microsystems, C=US"));
        List<String> rules = Arrays.asList("RULE:^CN=(.*),OU=ServiceUsers.*$/$1/L", "RULE:^CN=(.*),OU=(.*),O=(.*),L=(.*),ST=(.*),C=(.*)$/$1@$2/L", "RULE:^.*[Cc][Nn]=([a-zA-Z0-9.]*).*$/$1/U", "DEFAULT");
        SslPrincipalMapper mapper = SslPrincipalMapper.fromRules(rules);
        DefaultKafkaPrincipalBuilder builder = new DefaultKafkaPrincipalBuilder(null, mapper);
        SslAuthenticationContext sslContext = new SslAuthenticationContext(session, InetAddress.getLocalHost(), PLAINTEXT.name());
        KafkaPrincipal principal = builder.build(sslContext);
        Assert.assertEquals("duke", principal.getName());
        principal = builder.build(sslContext);
        Assert.assertEquals("duke@sme", principal.getName());
        principal = builder.build(sslContext);
        Assert.assertEquals("DUKE", principal.getName());
        principal = builder.build(sslContext);
        Assert.assertEquals("OU=JavaSoft,O=Sun Microsystems,C=US", principal.getName());
        builder.close();
        Mockito.verify(session, Mockito.times(4)).getPeerPrincipal();
    }

    @Test
    public void testPrincipalBuilderScram() throws Exception {
        SaslServer server = Mockito.mock(SaslServer.class);
        Mockito.when(server.getMechanismName()).thenReturn(SCRAM_SHA_256.mechanismName());
        Mockito.when(server.getAuthorizationID()).thenReturn("foo");
        DefaultKafkaPrincipalBuilder builder = new DefaultKafkaPrincipalBuilder(null, null);
        KafkaPrincipal principal = builder.build(new SaslAuthenticationContext(server, SASL_PLAINTEXT, InetAddress.getLocalHost(), SASL_PLAINTEXT.name()));
        Assert.assertEquals(USER_TYPE, principal.getPrincipalType());
        Assert.assertEquals("foo", principal.getName());
        builder.close();
        Mockito.verify(server, Mockito.atLeastOnce()).getMechanismName();
        Mockito.verify(server, Mockito.atLeastOnce()).getAuthorizationID();
    }

    @Test
    public void testPrincipalBuilderGssapi() throws Exception {
        SaslServer server = Mockito.mock(SaslServer.class);
        KerberosShortNamer kerberosShortNamer = Mockito.mock(KerberosShortNamer.class);
        Mockito.when(server.getMechanismName()).thenReturn(GSSAPI_MECHANISM);
        Mockito.when(server.getAuthorizationID()).thenReturn("foo/host@REALM.COM");
        Mockito.when(kerberosShortNamer.shortName(ArgumentMatchers.any())).thenReturn("foo");
        DefaultKafkaPrincipalBuilder builder = new DefaultKafkaPrincipalBuilder(kerberosShortNamer, null);
        KafkaPrincipal principal = builder.build(new SaslAuthenticationContext(server, SASL_PLAINTEXT, InetAddress.getLocalHost(), SASL_PLAINTEXT.name()));
        Assert.assertEquals(USER_TYPE, principal.getPrincipalType());
        Assert.assertEquals("foo", principal.getName());
        builder.close();
        Mockito.verify(server, Mockito.atLeastOnce()).getMechanismName();
        Mockito.verify(server, Mockito.atLeastOnce()).getAuthorizationID();
        Mockito.verify(kerberosShortNamer, Mockito.atLeastOnce()).shortName(ArgumentMatchers.any());
    }

    private static class DummyPrincipal implements Principal {
        private final String name;

        private DummyPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}

