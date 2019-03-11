/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.security.authentication;


import AuthType.CUSTOM;
import AuthType.KERBEROS;
import AuthType.SIMPLE;
import PlainSaslServerProvider.MECHANISM;
import SaslParticipantProvider.Factory;
import alluxio.ConfigurationTestUtils;
import alluxio.conf.AlluxioConfiguration;
import alluxio.exception.status.UnauthenticatedException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests {@link SaslParticipantProvider} and {@link SaslParticipantProviderPlain}.
 */
public class SaslParticipantProviderTest {
    /**
     * The exception expected to be thrown.
     */
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    private AlluxioConfiguration mConfiguration = ConfigurationTestUtils.defaults();

    @Test
    public void testCreateUnsupportedProvider() throws UnauthenticatedException {
        mThrown.expect(UnauthenticatedException.class);
        mThrown.expectMessage(("Unsupported AuthType: " + (KERBEROS.getAuthName())));
        Factory.create(KERBEROS);
    }

    @Test
    public void testCreateSupportedProviders() throws UnauthenticatedException {
        Factory.create(SIMPLE);
        Factory.create(CUSTOM);
    }

    @Test
    public void testCreateClientSimpleNullSubject() throws UnauthenticatedException {
        SaslParticipantProvider simpleProvider = Factory.create(SIMPLE);
        Assert.assertNotNull(simpleProvider);
        // Test allow null subject
        SaslClient client = simpleProvider.createSaslClient(null, mConfiguration);
        Assert.assertNotNull(client);
        Assert.assertEquals(MECHANISM, client.getMechanismName());
    }

    @Test
    public void testCreateClientSimpleNullUser() throws UnauthenticatedException {
        SaslParticipantProvider simpleProvider = Factory.create(SIMPLE);
        Assert.assertNotNull(simpleProvider);
        // Test null user
        mThrown.expect(UnauthenticatedException.class);
        mThrown.expectMessage("PLAIN: authorization ID and password must be specified");
        SaslClient client = simpleProvider.createSaslClient(null, null, null);
    }

    @Test
    public void testCreateClientSimpleNullPasword() throws UnauthenticatedException {
        SaslParticipantProvider simpleProvider = Factory.create(SIMPLE);
        Assert.assertNotNull(simpleProvider);
        // Test null user
        mThrown.expect(UnauthenticatedException.class);
        mThrown.expectMessage("PLAIN: authorization ID and password must be specified");
        SaslClient client = simpleProvider.createSaslClient("test", null, null);
    }

    @Test
    public void testCreateClientCustomNullUser() throws UnauthenticatedException {
        SaslParticipantProvider simpleProvider = Factory.create(CUSTOM);
        Assert.assertNotNull(simpleProvider);
        // Test null user
        mThrown.expect(UnauthenticatedException.class);
        mThrown.expectMessage("PLAIN: authorization ID and password must be specified");
        SaslClient client = simpleProvider.createSaslClient(null, null, null);
    }

    @Test
    public void testCreateClientCustomNullPasword() throws UnauthenticatedException {
        SaslParticipantProvider simpleProvider = Factory.create(CUSTOM);
        Assert.assertNotNull(simpleProvider);
        // Test null user
        mThrown.expect(UnauthenticatedException.class);
        mThrown.expectMessage("PLAIN: authorization ID and password must be specified");
        SaslClient client = simpleProvider.createSaslClient("test", null, null);
    }

    @Test
    public void testCreateServerSimple() throws UnauthenticatedException, SaslException {
        SaslParticipantProvider simpleProvider = Factory.create(SIMPLE);
        Assert.assertNotNull(simpleProvider);
        SaslServer server = simpleProvider.createSaslServer("test", mConfiguration);
        Assert.assertNotNull(server);
        Assert.assertEquals(MECHANISM, server.getMechanismName());
    }
}

