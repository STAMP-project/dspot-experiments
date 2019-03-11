/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
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
 */
package org.kaaproject.kaa.client.persistance;


import KaaClientProperties.SDK_TOKEN;
import SubscriptionType.MANDATORY_SUBSCRIPTION;
import SubscriptionType.OPTIONAL_SUBSCRIPTION;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.KaaClientProperties;
import org.kaaproject.kaa.client.exceptions.KaaRuntimeException;
import org.kaaproject.kaa.client.persistence.FilePersistentStorage;
import org.kaaproject.kaa.client.persistence.KaaClientState;
import org.kaaproject.kaa.client.persistence.PersistentStorage;
import org.kaaproject.kaa.client.util.CommonsBase64;
import org.kaaproject.kaa.common.endpoint.gen.Topic;
import org.kaaproject.kaa.common.endpoint.security.KeyUtil;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;


public class KaaClientPropertiesStateTest {
    private static final String WORK_DIR = "work_dir" + (System.getProperty("file.separator"));

    private static final String KEY_PUBLIC = "key.public";

    private static final String KEY_PRIVATE = "key.private";

    private static final String STATE_PROPERTIES = "state.properties";

    private static final String STATE_PROPERTIES_BCKP = "state.properties_bckp";

    @Test(expected = KaaRuntimeException.class)
    public void testInitKeys() throws IOException, InvalidKeyException {
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        Assert.assertNull(state.getPrivateKey());
        Assert.assertNull(state.getPublicKey());
    }

    @Test
    public void testGenerateKeys() throws IOException, InvalidKeyException {
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties(), true);
        Assert.assertNotNull(state.getPrivateKey());
        Assert.assertNotNull(state.getPublicKey());
    }

    @Test
    public void testDefaultStrategyKeys() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        PersistentStorage storage = new FilePersistentStorage();
        String clientPrivateKeyFileLocation = KaaClientPropertiesStateTest.getProperties().getPrivateKeyFileFullName();
        String clientPublicKeyFileLocation = KaaClientPropertiesStateTest.getProperties().getPublicKeyFileFullName();
        OutputStream privateKeyOutput = storage.openForWrite(clientPrivateKeyFileLocation);
        OutputStream publicKeyOutput = storage.openForWrite(clientPublicKeyFileLocation);
        KeyPair keyPair = KeyUtil.generateKeyPair(privateKeyOutput, publicKeyOutput);
        Assert.assertArrayEquals(keyPair.getPrivate().getEncoded(), state.getPrivateKey().getEncoded());
        Assert.assertArrayEquals(keyPair.getPublic().getEncoded(), state.getPublicKey().getEncoded());
        // clean
        new File(((KaaClientPropertiesStateTest.WORK_DIR) + (KaaClientPropertiesStateTest.KEY_PUBLIC))).delete();
        new File(((KaaClientPropertiesStateTest.WORK_DIR) + (KaaClientPropertiesStateTest.KEY_PRIVATE))).delete();
    }

    @Test(expected = KaaRuntimeException.class)
    public void testDefaultStrategyRecreateKeys() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        PersistentStorage storage = new FilePersistentStorage();
        String clientPrivateKeyFileLocation = KaaClientPropertiesStateTest.getProperties().getPrivateKeyFileFullName();
        String clientPublicKeyFileLocation = KaaClientPropertiesStateTest.getProperties().getPublicKeyFileFullName();
        OutputStream privateKeyOutput = storage.openForWrite(clientPrivateKeyFileLocation);
        OutputStream publicKeyOutput = storage.openForWrite(clientPublicKeyFileLocation);
        KeyPair keyPair = KeyUtil.generateKeyPair(privateKeyOutput, publicKeyOutput);
        Assert.assertArrayEquals(keyPair.getPrivate().getEncoded(), state.getPrivateKey().getEncoded());
        Assert.assertArrayEquals(keyPair.getPublic().getEncoded(), state.getPublicKey().getEncoded());
        File pub = new File(((KaaClientPropertiesStateTest.WORK_DIR) + (KaaClientPropertiesStateTest.KEY_PUBLIC)));
        File priv = new File(((KaaClientPropertiesStateTest.WORK_DIR) + (KaaClientPropertiesStateTest.KEY_PRIVATE)));
        // clean
        Files.delete(Paths.get(((KaaClientPropertiesStateTest.WORK_DIR) + (KaaClientPropertiesStateTest.KEY_PUBLIC))));
        new File(((KaaClientPropertiesStateTest.WORK_DIR) + (KaaClientPropertiesStateTest.KEY_PRIVATE))).delete();
        state.clean();
        state.getPublicKey();
        state.getPrivateKey();
    }

    @Test(expected = KaaRuntimeException.class)
    public void testInitKeys2() throws IOException, InvalidKeyException {
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        Assert.assertNull(state.getPrivateKey());
        Assert.assertNull(state.getPublicKey());
    }

    @Test
    public void testRecreateKeys() throws IOException, InvalidKeyException {
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties(), true);
        state.getPublicKey();
        state.getPrivateKey();
        File pub = new File(((KaaClientPropertiesStateTest.WORK_DIR) + (KaaClientPropertiesStateTest.KEY_PUBLIC)));
        File priv = new File(((KaaClientPropertiesStateTest.WORK_DIR) + (KaaClientPropertiesStateTest.KEY_PRIVATE)));
        Assert.assertArrayEquals(KeyUtil.getPrivate(priv).getEncoded(), state.getPrivateKey().getEncoded());
        Assert.assertArrayEquals(KeyUtil.getPublic(pub).getEncoded(), state.getPublicKey().getEncoded());
        pub.delete();
        priv.delete();
        Assert.assertNotNull(state.getPublicKey());
        Assert.assertNotNull(state.getPrivateKey());
    }

    @Test
    public void testProfileHash() throws IOException {
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        EndpointObjectHash hash = EndpointObjectHash.fromSha1(new byte[]{ 1, 2, 3 });
        state.setProfileHash(hash);
        Assert.assertEquals(hash, state.getProfileHash());
    }

    @Test
    public void testNfSubscription() throws IOException {
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        Topic topic1 = Topic.newBuilder().setId(1234).setName("testName").setSubscriptionType(OPTIONAL_SUBSCRIPTION).build();
        Topic topic2 = Topic.newBuilder().setId(4321).setName("testName").setSubscriptionType(MANDATORY_SUBSCRIPTION).build();
        state.addTopic(topic1);
        state.addTopic(topic2);
        state.updateTopicSubscriptionInfo(topic2.getId(), 1);
        state.updateTopicSubscriptionInfo(topic1.getId(), 0);
        state.updateTopicSubscriptionInfo(topic1.getId(), 1);
        Map<Long, Integer> expected = new HashMap<>();
        expected.put(topic2.getId(), 1);
        Assert.assertEquals(expected, state.getNfSubscriptions());
        state.persist();
        state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        Assert.assertEquals(expected, state.getNfSubscriptions());
        state.addTopicSubscription(topic1.getId());
        expected.put(topic1.getId(), 0);
        Assert.assertEquals(expected, state.getNfSubscriptions());
        state.updateTopicSubscriptionInfo(topic1.getId(), 5);
        expected.put(topic1.getId(), 5);
        Assert.assertEquals(expected, state.getNfSubscriptions());
        state.removeTopic(topic1.getId());
        state.persist();
        state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        expected.remove(topic1.getId());
        Assert.assertEquals(expected, state.getNfSubscriptions());
    }

    @Test
    public void testSDKPropertiesUpdate() throws IOException {
        KaaClientProperties props = KaaClientPropertiesStateTest.getProperties();
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), props);
        Assert.assertFalse(state.isRegistered());
        state.setRegistered(true);
        state.persist();
        Assert.assertTrue(state.isRegistered());
        KaaClientProperties newProps = KaaClientPropertiesStateTest.getProperties();
        newProps.setProperty(SDK_TOKEN, "SDK_TOKEN_100500");
        KaaClientState newState = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), newProps);
        Assert.assertFalse(newState.isRegistered());
    }

    @Test
    public void testNeedProfileResync() throws Exception {
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        Assert.assertFalse(state.isNeedProfileResync());
        state.setIfNeedProfileResync(true);
        Assert.assertTrue(state.isNeedProfileResync());
        state.persist();
        state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        Assert.assertTrue(state.isNeedProfileResync());
        state.setIfNeedProfileResync(false);
        Assert.assertFalse(state.isNeedProfileResync());
    }

    @Test
    public void testClean() throws Exception {
        KaaClientState state = new org.kaaproject.kaa.client.persistence.KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        File stateProps = new File(((KaaClientPropertiesStateTest.WORK_DIR) + (KaaClientPropertiesStateTest.STATE_PROPERTIES)));
        File statePropsBckp = new File(((KaaClientPropertiesStateTest.WORK_DIR) + (KaaClientPropertiesStateTest.STATE_PROPERTIES_BCKP)));
        statePropsBckp.deleteOnExit();
        state.persist();
        state.setRegistered(true);
        state.persist();
        Assert.assertTrue(stateProps.exists());
        Assert.assertTrue(statePropsBckp.exists());
        state.clean();
        Assert.assertFalse(stateProps.exists());
        Assert.assertFalse(statePropsBckp.exists());
    }
}

