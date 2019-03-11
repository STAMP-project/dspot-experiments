/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.client;


import Config.DEFAULT_CACHE_NAME;
import Config.SERVER;
import SslMode.REQUIRED;
import SslProtocol.TLS;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.function.Function;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.ssl.SslContextFactory;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Thin client security test.
 */
public class SecurityTest {
    /**
     * Per test timeout
     */
    @Rule
    public Timeout globalTimeout = new Timeout(((int) (GridTestUtils.DFLT_TEST_TIMEOUT)));

    /**
     * Ignite home.
     */
    private static final String IGNITE_HOME = U.getIgniteHome();

    /**
     * Test SSL/TLS encryption.
     */
    @Test
    public void testEncryption() throws Exception {
        // Server-side security configuration
        IgniteConfiguration srvCfg = Config.getServerConfiguration();
        SslContextFactory sslCfg = new SslContextFactory();
        Function<String, String> rsrcPath = ( rsrc) -> Paths.get(((SecurityTest.IGNITE_HOME) == null ? "." : SecurityTest.IGNITE_HOME), "modules", "core", "src", "test", "resources", rsrc).toString();
        sslCfg.setKeyStoreFilePath(rsrcPath.apply("/server.jks"));
        sslCfg.setKeyStorePassword("123456".toCharArray());
        sslCfg.setTrustStoreFilePath(rsrcPath.apply("/trust.jks"));
        sslCfg.setTrustStorePassword("123456".toCharArray());
        srvCfg.setClientConnectorConfiguration(new ClientConnectorConfiguration().setSslEnabled(true).setSslClientAuth(true));
        srvCfg.setSslContextFactory(sslCfg);
        // Client-side security configuration
        ClientConfiguration clientCfg = new ClientConfiguration().setAddresses(SERVER);
        try (Ignite ignored = Ignition.start(srvCfg)) {
            boolean failed;
            try (IgniteClient client = Ignition.startClient(clientCfg)) {
                client.<Integer, String>cache(DEFAULT_CACHE_NAME).put(1, "1");
                failed = false;
            } catch (Exception ex) {
                failed = true;
            }
            Assert.assertTrue("Client connection without SSL must fail", failed);
            // Not using user-supplied SSL Context Factory:
            try (IgniteClient client = Ignition.startClient(clientCfg.setSslMode(REQUIRED).setSslClientCertificateKeyStorePath(rsrcPath.apply("/client.jks")).setSslClientCertificateKeyStoreType("JKS").setSslClientCertificateKeyStorePassword("123456").setSslTrustCertificateKeyStorePath(rsrcPath.apply("/trust.jks")).setSslTrustCertificateKeyStoreType("JKS").setSslTrustCertificateKeyStorePassword("123456").setSslKeyAlgorithm("SunX509").setSslTrustAll(false).setSslProtocol(TLS))) {
                client.<Integer, String>cache(DEFAULT_CACHE_NAME).put(1, "1");
            }
            // Using user-supplied SSL Context Factory
            try (IgniteClient client = Ignition.startClient(clientCfg.setSslMode(REQUIRED).setSslContextFactory(sslCfg))) {
                client.<Integer, String>cache(DEFAULT_CACHE_NAME).put(1, "1");
            }
        }
    }

    /**
     * Test valid user authentication.
     */
    @Test
    public void testInvalidUserAuthentication() {
        Exception authError = null;
        try (Ignite ignored = SecurityTest.igniteWithAuthentication();IgniteClient client = Ignition.startClient(new ClientConfiguration().setAddresses(SERVER).setUserName("JOE").setUserPassword("password"))) {
            client.getOrCreateCache("testAuthentication");
        } catch (Exception e) {
            authError = e;
        }
        Assert.assertNotNull("Authentication with invalid credentials succeeded", authError);
        Assert.assertTrue("Invalid type of authentication error", (authError instanceof ClientAuthenticationException));
    }

    /**
     * Test valid user authentication.
     */
    @Test
    public void testValidUserAuthentication() throws Exception {
        final String USER = "joe";
        final String PWD = "password";
        try (Ignite ignored = SecurityTest.igniteWithAuthentication(new AbstractMap.SimpleEntry<>(USER, PWD));IgniteClient client = Ignition.startClient(new ClientConfiguration().setAddresses(SERVER).setUserName(USER).setUserPassword(PWD))) {
            client.getOrCreateCache("testAuthentication");
        }
    }

    /**
     * Test user cannot create user.
     */
    @Test
    public void testUserCannotCreateUser() throws Exception {
        final String USER = "joe";
        final String PWD = "password";
        try (Ignite ignored = SecurityTest.igniteWithAuthentication(new AbstractMap.SimpleEntry<>(USER, PWD));IgniteClient client = Ignition.startClient(new ClientConfiguration().setAddresses(SERVER).setUserName(USER).setUserPassword(PWD))) {
            Exception authError = null;
            try {
                client.query(new SqlFieldsQuery(String.format("CREATE USER \"%s\" WITH PASSWORD \'%s\'", "joe2", "password"))).getAll();
            } catch (Exception e) {
                authError = e;
            }
            Assert.assertNotNull("User created another user", authError);
        }
    }
}

