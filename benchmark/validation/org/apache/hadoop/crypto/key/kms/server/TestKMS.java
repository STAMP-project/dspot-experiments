/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.crypto.key.kms.server;


import CommonConfigurationKeysPublic.KMS_CLIENT_TIMEOUT_SECONDS;
import KMSACLs.Type;
import KMSACLs.Type.CREATE;
import KMSACLs.Type.DECRYPT_EEK;
import KMSACLs.Type.GENERATE_EEK;
import KMSACLs.Type.ROLLOVER;
import KMSClientProvider.AUTH_RETRY;
import KMSConfiguration.LOG;
import KMSConfiguration.METRICS_PROCESS_NAME_KEY;
import KMSDelegationToken.TOKEN_KIND;
import KeyProvider.DEFAULT_BITLENGTH_NAME;
import KeyProvider.Metadata;
import KeyProviderCryptoExtension.EEK;
import KeyProviderCryptoExtension.EK;
import KeyProviderFactory.KEY_PROVIDER_PATH;
import MiniKdc.MAX_TICKET_LIFETIME;
import MiniKdc.MIN_TICKET_LIFETIME;
import com.google.common.cache.LoadingCache;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.apache.hadoop.crypto.key.KeyProvider;
import org.apache.hadoop.crypto.key.KeyProvider.KeyVersion;
import org.apache.hadoop.crypto.key.KeyProvider.Options;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension.CryptoExtension;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension.EncryptedKeyVersion;
import org.apache.hadoop.crypto.key.KeyProviderDelegationTokenExtension;
import org.apache.hadoop.crypto.key.kms.KMSClientProvider;
import org.apache.hadoop.crypto.key.kms.ValueQueue;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.security.ssl.SSLFactory;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.delegation.web.DelegationTokenIdentifier;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.Whitebox;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static KMSConfiguration.DEFAULT_KEY_ACL_PREFIX;
import static KMSConfiguration.WHITELIST_KEY_ACL_PREFIX;
import static KeyAuthorizationKeyProvider.KEY_ACL;
import static javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;


public class TestKMS {
    private static final Logger LOG = LoggerFactory.getLogger(TestKMS.class);

    private static final String SSL_RELOADER_THREAD_NAME = "Truststore reloader thread";

    private SSLFactory sslFactory;

    // Keep track of all key providers created during a test case, so they can be
    // closed at test tearDown.
    private List<KeyProvider> providersCreated = new LinkedList<>();

    @Rule
    public final Timeout testTimeout = new Timeout(180000);

    public abstract static class KMSCallable<T> implements Callable<T> {
        private List<URL> kmsUrl;

        protected URL getKMSUrl() {
            return kmsUrl.get(0);
        }

        protected URL[] getKMSHAUrl() {
            URL[] urls = new URL[kmsUrl.size()];
            return kmsUrl.toArray(urls);
        }

        protected void addKMSUrl(URL url) {
            if ((kmsUrl) == null) {
                kmsUrl = new ArrayList<URL>();
            }
            kmsUrl.add(url);
        }

        /* The format of the returned value will be
        kms://http:kms1.example1.com:port1,kms://http:kms2.example2.com:port2
         */
        protected String generateLoadBalancingKeyProviderUriString() {
            if (((kmsUrl) == null) || ((kmsUrl.size()) == 0)) {
                return null;
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < (kmsUrl.size()); i++) {
                sb.append(((((KMSClientProvider.SCHEME_NAME) + "://") + (kmsUrl.get(0).getProtocol())) + "@"));
                URL url = kmsUrl.get(i);
                sb.append(url.getAuthority());
                if ((url.getPath()) != null) {
                    sb.append(url.getPath());
                }
                if (i < ((kmsUrl.size()) - 1)) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }
    }

    private static class KerberosConfiguration extends Configuration {
        private String principal;

        private String keytab;

        private boolean isInitiator;

        private KerberosConfiguration(String principal, File keytab, boolean client) {
            this.principal = principal;
            this.keytab = keytab.getAbsolutePath();
            this.isInitiator = client;
        }

        public static Configuration createClientConfig(String principal, File keytab) {
            return new TestKMS.KerberosConfiguration(principal, keytab, true);
        }

        private static String getKrb5LoginModuleName() {
            return System.getProperty("java.vendor").contains("IBM") ? "com.ibm.security.auth.module.Krb5LoginModule" : "com.sun.security.auth.module.Krb5LoginModule";
        }

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            Map<String, String> options = new HashMap<String, String>();
            options.put("keyTab", keytab);
            options.put("principal", principal);
            options.put("useKeyTab", "true");
            options.put("storeKey", "true");
            options.put("doNotPrompt", "true");
            options.put("useTicketCache", "true");
            options.put("renewTGT", "true");
            options.put("refreshKrb5Config", "true");
            options.put("isInitiator", Boolean.toString(isInitiator));
            String ticketCache = System.getenv("KRB5CCNAME");
            if (ticketCache != null) {
                options.put("ticketCache", ticketCache);
            }
            options.put("debug", "true");
            return new AppConfigurationEntry[]{ new AppConfigurationEntry(TestKMS.KerberosConfiguration.getKrb5LoginModuleName(), REQUIRED, options) };
        }
    }

    private static MiniKdc kdc;

    private static File keytab;

    @Test
    public void testStartStopHttpPseudo() throws Exception {
        testStartStop(false, false);
    }

    @Test
    public void testStartStopHttpsPseudo() throws Exception {
        testStartStop(true, false);
    }

    @Test
    public void testStartStopHttpKerberos() throws Exception {
        testStartStop(false, true);
    }

    @Test
    public void testStartStopHttpsKerberos() throws Exception {
        testStartStop(true, true);
    }

    @Test(timeout = 30000)
    public void testSpecialKeyNames() throws Exception {
        final String specialKey = "key %^[\n{]}|\"<>\\";
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        File confDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(confDir, conf);
        conf.set((((KEY_ACL) + specialKey) + ".ALL"), "*");
        TestKMS.writeConf(confDir, conf);
        runServer(null, null, confDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                URI uri = TestKMS.createKMSUri(getKMSUrl());
                KeyProvider kp = createProvider(uri, conf);
                Assert.assertTrue(kp.getKeys().isEmpty());
                Assert.assertEquals(0, kp.getKeysMetadata().length);
                KeyProvider.Options options = new KeyProvider.Options(conf);
                options.setCipher("AES/CTR/NoPadding");
                options.setBitLength(128);
                options.setDescription("l1");
                TestKMS.LOG.info("Creating key with name '{}'", specialKey);
                KeyProvider.KeyVersion kv0 = kp.createKey(specialKey, options);
                Assert.assertNotNull(kv0);
                Assert.assertEquals(specialKey, kv0.getName());
                Assert.assertNotNull(kv0.getVersionName());
                Assert.assertNotNull(kv0.getMaterial());
                return null;
            }
        });
    }

    @Test
    @SuppressWarnings("checkstyle:methodlength")
    public void testKMSProvider() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        File confDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(confDir, conf);
        conf.set(((KEY_ACL) + "k1.ALL"), "*");
        conf.set(((KEY_ACL) + "k2.MANAGEMENT"), "*");
        conf.set(((KEY_ACL) + "k2.READ"), "*");
        conf.set(((KEY_ACL) + "k3.ALL"), "*");
        conf.set(((KEY_ACL) + "k4.ALL"), "*");
        conf.set(((KEY_ACL) + "k5.ALL"), "*");
        conf.set(((KEY_ACL) + "k6.ALL"), "*");
        TestKMS.writeConf(confDir, conf);
        runServer(null, null, confDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                Date started = new Date();
                org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                URI uri = TestKMS.createKMSUri(getKMSUrl());
                KeyProvider kp = createProvider(uri, conf);
                // getKeys() empty
                Assert.assertTrue(kp.getKeys().isEmpty());
                // getKeysMetadata() empty
                Assert.assertEquals(0, kp.getKeysMetadata().length);
                // createKey()
                KeyProvider.Options options = new KeyProvider.Options(conf);
                options.setCipher("AES/CTR/NoPadding");
                options.setBitLength(128);
                options.setDescription("l1");
                KeyProvider.KeyVersion kv0 = kp.createKey("k1", options);
                Assert.assertNotNull(kv0);
                Assert.assertNotNull(kv0.getVersionName());
                Assert.assertNotNull(kv0.getMaterial());
                // getKeyVersion()
                KeyProvider.KeyVersion kv1 = kp.getKeyVersion(kv0.getVersionName());
                Assert.assertEquals(kv0.getVersionName(), kv1.getVersionName());
                Assert.assertNotNull(kv1.getMaterial());
                // getCurrent()
                KeyProvider.KeyVersion cv1 = kp.getCurrentKey("k1");
                Assert.assertEquals(kv0.getVersionName(), cv1.getVersionName());
                Assert.assertNotNull(cv1.getMaterial());
                // getKeyMetadata() 1 version
                KeyProvider.Metadata m1 = kp.getMetadata("k1");
                Assert.assertEquals("AES/CTR/NoPadding", m1.getCipher());
                Assert.assertEquals("AES", m1.getAlgorithm());
                Assert.assertEquals(128, m1.getBitLength());
                Assert.assertEquals(1, m1.getVersions());
                Assert.assertNotNull(m1.getCreated());
                Assert.assertTrue(started.before(m1.getCreated()));
                // getKeyVersions() 1 version
                List<KeyProvider.KeyVersion> lkv1 = kp.getKeyVersions("k1");
                Assert.assertEquals(1, lkv1.size());
                Assert.assertEquals(kv0.getVersionName(), lkv1.get(0).getVersionName());
                Assert.assertNotNull(kv1.getMaterial());
                // rollNewVersion()
                KeyProvider.KeyVersion kv2 = kp.rollNewVersion("k1");
                Assert.assertNotSame(kv0.getVersionName(), kv2.getVersionName());
                Assert.assertNotNull(kv2.getMaterial());
                // getKeyVersion()
                kv2 = kp.getKeyVersion(kv2.getVersionName());
                boolean eq = true;
                for (int i = 0; i < (kv1.getMaterial().length); i++) {
                    eq = eq && ((kv1.getMaterial()[i]) == (kv2.getMaterial()[i]));
                }
                Assert.assertFalse(eq);
                // getCurrent()
                KeyProvider.KeyVersion cv2 = kp.getCurrentKey("k1");
                Assert.assertEquals(kv2.getVersionName(), cv2.getVersionName());
                Assert.assertNotNull(cv2.getMaterial());
                eq = true;
                for (int i = 0; i < (kv1.getMaterial().length); i++) {
                    eq = eq && ((cv2.getMaterial()[i]) == (kv2.getMaterial()[i]));
                }
                Assert.assertTrue(eq);
                // getKeyVersions() 2 versions
                List<KeyProvider.KeyVersion> lkv2 = kp.getKeyVersions("k1");
                Assert.assertEquals(2, lkv2.size());
                Assert.assertEquals(kv1.getVersionName(), lkv2.get(0).getVersionName());
                Assert.assertNotNull(lkv2.get(0).getMaterial());
                Assert.assertEquals(kv2.getVersionName(), lkv2.get(1).getVersionName());
                Assert.assertNotNull(lkv2.get(1).getMaterial());
                // getKeyMetadata() 2 version
                KeyProvider.Metadata m2 = kp.getMetadata("k1");
                Assert.assertEquals("AES/CTR/NoPadding", m2.getCipher());
                Assert.assertEquals("AES", m2.getAlgorithm());
                Assert.assertEquals(128, m2.getBitLength());
                Assert.assertEquals(2, m2.getVersions());
                Assert.assertNotNull(m2.getCreated());
                Assert.assertTrue(started.before(m2.getCreated()));
                // getKeys() 1 key
                List<String> ks1 = kp.getKeys();
                Assert.assertEquals(1, ks1.size());
                Assert.assertEquals("k1", ks1.get(0));
                // getKeysMetadata() 1 key 2 versions
                KeyProvider[] kms1 = kp.getKeysMetadata("k1");
                Assert.assertEquals(1, kms1.length);
                Assert.assertEquals("AES/CTR/NoPadding", kms1[0].getCipher());
                Assert.assertEquals("AES", kms1[0].getAlgorithm());
                Assert.assertEquals(128, kms1[0].getBitLength());
                Assert.assertEquals(2, kms1[0].getVersions());
                Assert.assertNotNull(kms1[0].getCreated());
                Assert.assertTrue(started.before(kms1[0].getCreated()));
                // test generate and decryption of EEK
                KeyProvider.KeyVersion kv = kp.getCurrentKey("k1");
                KeyProviderCryptoExtension kpExt = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                EncryptedKeyVersion ek1 = kpExt.generateEncryptedKey(kv.getName());
                Assert.assertEquals(EEK, ek1.getEncryptedKeyVersion().getVersionName());
                Assert.assertNotNull(ek1.getEncryptedKeyVersion().getMaterial());
                Assert.assertEquals(kv.getMaterial().length, ek1.getEncryptedKeyVersion().getMaterial().length);
                KeyProvider.KeyVersion k1 = kpExt.decryptEncryptedKey(ek1);
                Assert.assertEquals(EK, k1.getVersionName());
                KeyProvider.KeyVersion k1a = kpExt.decryptEncryptedKey(ek1);
                Assert.assertArrayEquals(k1.getMaterial(), k1a.getMaterial());
                Assert.assertEquals(kv.getMaterial().length, k1.getMaterial().length);
                EncryptedKeyVersion ek2 = kpExt.generateEncryptedKey(kv.getName());
                KeyProvider.KeyVersion k2 = kpExt.decryptEncryptedKey(ek2);
                boolean isEq = true;
                for (int i = 0; isEq && (i < (ek2.getEncryptedKeyVersion().getMaterial().length)); i++) {
                    isEq = (k2.getMaterial()[i]) == (k1.getMaterial()[i]);
                }
                Assert.assertFalse(isEq);
                // test re-encrypt
                kpExt.rollNewVersion(ek1.getEncryptionKeyName());
                EncryptedKeyVersion ek1r = kpExt.reencryptEncryptedKey(ek1);
                Assert.assertEquals(EEK, ek1r.getEncryptedKeyVersion().getVersionName());
                Assert.assertFalse(Arrays.equals(ek1.getEncryptedKeyVersion().getMaterial(), ek1r.getEncryptedKeyVersion().getMaterial()));
                Assert.assertEquals(kv.getMaterial().length, ek1r.getEncryptedKeyVersion().getMaterial().length);
                Assert.assertEquals(ek1.getEncryptionKeyName(), ek1r.getEncryptionKeyName());
                Assert.assertArrayEquals(ek1.getEncryptedKeyIv(), ek1r.getEncryptedKeyIv());
                Assert.assertNotEquals(ek1.getEncryptionKeyVersionName(), ek1r.getEncryptionKeyVersionName());
                KeyProvider.KeyVersion k1r = kpExt.decryptEncryptedKey(ek1r);
                Assert.assertEquals(EK, k1r.getVersionName());
                Assert.assertArrayEquals(k1.getMaterial(), k1r.getMaterial());
                Assert.assertEquals(kv.getMaterial().length, k1r.getMaterial().length);
                // test re-encrypt batch
                EncryptedKeyVersion ek3 = kpExt.generateEncryptedKey(kv.getName());
                KeyVersion latest = kpExt.rollNewVersion(kv.getName());
                List<EncryptedKeyVersion> ekvs = new ArrayList<>(3);
                ekvs.add(ek1);
                ekvs.add(ek2);
                ekvs.add(ek3);
                ekvs.add(ek1);
                ekvs.add(ek2);
                ekvs.add(ek3);
                kpExt.reencryptEncryptedKeys(ekvs);
                for (EncryptedKeyVersion ekv : ekvs) {
                    Assert.assertEquals(latest.getVersionName(), ekv.getEncryptionKeyVersionName());
                }
                // deleteKey()
                kp.deleteKey("k1");
                // Check decryption after Key deletion
                try {
                    kpExt.decryptEncryptedKey(ek1);
                    Assert.fail("Should not be allowed !!");
                } catch (Exception e) {
                    Assert.assertTrue(e.getMessage().contains("'k1@1' not found"));
                }
                // getKey()
                Assert.assertNull(kp.getKeyVersion("k1"));
                // getKeyVersions()
                Assert.assertNull(kp.getKeyVersions("k1"));
                // getMetadata()
                Assert.assertNull(kp.getMetadata("k1"));
                // getKeys() empty
                Assert.assertTrue(kp.getKeys().isEmpty());
                // getKeysMetadata() empty
                Assert.assertEquals(0, kp.getKeysMetadata().length);
                // createKey() no description, no tags
                options = new KeyProvider.Options(conf);
                options.setCipher("AES/CTR/NoPadding");
                options.setBitLength(128);
                KeyVersion kVer2 = kp.createKey("k2", options);
                KeyProvider.Metadata meta = kp.getMetadata("k2");
                Assert.assertNull(meta.getDescription());
                Assert.assertEquals("k2", meta.getAttributes().get("key.acl.name"));
                // test key ACL.. k2 is granted only MANAGEMENT Op access
                try {
                    kpExt = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                    kpExt.generateEncryptedKey(kVer2.getName());
                    Assert.fail("User should not be allowed to encrypt !!");
                } catch (Exception ex) {
                    // 
                }
                // createKey() description, no tags
                options = new KeyProvider.Options(conf);
                options.setCipher("AES/CTR/NoPadding");
                options.setBitLength(128);
                options.setDescription("d");
                kp.createKey("k3", options);
                meta = kp.getMetadata("k3");
                Assert.assertEquals("d", meta.getDescription());
                Assert.assertEquals("k3", meta.getAttributes().get("key.acl.name"));
                Map<String, String> attributes = new HashMap<String, String>();
                attributes.put("a", "A");
                // createKey() no description, tags
                options = new KeyProvider.Options(conf);
                options.setCipher("AES/CTR/NoPadding");
                options.setBitLength(128);
                attributes.put("key.acl.name", "k4");
                options.setAttributes(attributes);
                kp.createKey("k4", options);
                meta = kp.getMetadata("k4");
                Assert.assertNull(meta.getDescription());
                Assert.assertEquals(attributes, meta.getAttributes());
                // createKey() description, tags
                options = new KeyProvider.Options(conf);
                options.setCipher("AES/CTR/NoPadding");
                options.setBitLength(128);
                options.setDescription("d");
                attributes.put("key.acl.name", "k5");
                options.setAttributes(attributes);
                kp.createKey("k5", options);
                meta = kp.getMetadata("k5");
                Assert.assertEquals("d", meta.getDescription());
                Assert.assertEquals(attributes, meta.getAttributes());
                // test rollover draining
                KeyProviderCryptoExtension kpce = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                options = new KeyProvider.Options(conf);
                options.setCipher("AES/CTR/NoPadding");
                options.setBitLength(128);
                kpce.createKey("k6", options);
                EncryptedKeyVersion ekv1 = kpce.generateEncryptedKey("k6");
                kpce.rollNewVersion("k6");
                kpce.invalidateCache("k6");
                EncryptedKeyVersion ekv2 = kpce.generateEncryptedKey("k6");
                Assert.assertNotEquals(("rollover did not generate a new key even after" + " queue is drained"), ekv1.getEncryptionKeyVersionName(), ekv2.getEncryptionKeyVersionName());
                return null;
            }
        });
    }

    @Test
    public void testKMSProviderCaching() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        File confDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(confDir, conf);
        conf.set(((KEY_ACL) + "k1.ALL"), "*");
        TestKMS.writeConf(confDir, conf);
        runServer(null, null, confDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final String keyName = "k1";
                final String mockVersionName = "mock";
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                KMSClientProvider kmscp = createKMSClientProvider(uri, conf);
                // get the reference to the internal cache, to test invalidation.
                ValueQueue vq = ((ValueQueue) (Whitebox.getInternalState(kmscp, "encKeyVersionQueue")));
                LoadingCache<String, LinkedBlockingQueue<EncryptedKeyVersion>> kq = ((LoadingCache<String, LinkedBlockingQueue<EncryptedKeyVersion>>) (Whitebox.getInternalState(vq, "keyQueues")));
                EncryptedKeyVersion mockEKV = Mockito.mock(EncryptedKeyVersion.class);
                Mockito.when(mockEKV.getEncryptionKeyName()).thenReturn(keyName);
                Mockito.when(mockEKV.getEncryptionKeyVersionName()).thenReturn(mockVersionName);
                // createKey()
                KeyProvider.Options options = new KeyProvider.Options(conf);
                options.setCipher("AES/CTR/NoPadding");
                options.setBitLength(128);
                options.setDescription("l1");
                KeyProvider.KeyVersion kv0 = kmscp.createKey(keyName, options);
                Assert.assertNotNull(kv0.getVersionName());
                Assert.assertEquals("Default key version name is incorrect.", "k1@0", kmscp.generateEncryptedKey(keyName).getEncryptionKeyVersionName());
                kmscp.invalidateCache(keyName);
                kq.get(keyName).put(mockEKV);
                Assert.assertEquals(("Key version incorrect after invalidating cache + putting" + " mock key."), mockVersionName, kmscp.generateEncryptedKey(keyName).getEncryptionKeyVersionName());
                // test new version is returned after invalidation.
                for (int i = 0; i < 100; ++i) {
                    kq.get(keyName).put(mockEKV);
                    kmscp.invalidateCache(keyName);
                    Assert.assertEquals("Cache invalidation guarantee failed.", "k1@0", kmscp.generateEncryptedKey(keyName).getEncryptionKeyVersionName());
                }
                return null;
            }
        });
    }

    @Test
    @SuppressWarnings("checkstyle:methodlength")
    public void testKeyACLs() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        final File testDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(testDir, conf);
        conf.set("hadoop.kms.authentication.type", "kerberos");
        conf.set("hadoop.kms.authentication.kerberos.keytab", TestKMS.keytab.getAbsolutePath());
        conf.set("hadoop.kms.authentication.kerberos.principal", "HTTP/localhost");
        conf.set("hadoop.kms.authentication.kerberos.name.rules", "DEFAULT");
        for (KMSACLs.Type type : Type.values()) {
            conf.set(type.getAclConfigKey(), type.toString());
        }
        conf.set(CREATE.getAclConfigKey(), "CREATE,ROLLOVER,GET,SET_KEY_MATERIAL,GENERATE_EEK,DECRYPT_EEK");
        conf.set(ROLLOVER.getAclConfigKey(), "CREATE,ROLLOVER,GET,SET_KEY_MATERIAL,GENERATE_EEK,DECRYPT_EEK");
        conf.set(GENERATE_EEK.getAclConfigKey(), "CREATE,ROLLOVER,GET,SET_KEY_MATERIAL,GENERATE_EEK,DECRYPT_EEK");
        conf.set(DECRYPT_EEK.getAclConfigKey(), "CREATE,ROLLOVER,GET,SET_KEY_MATERIAL,GENERATE_EEK");
        conf.set(((KEY_ACL) + "test_key.MANAGEMENT"), "CREATE");
        conf.set(((KEY_ACL) + "some_key.MANAGEMENT"), "ROLLOVER");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "MANAGEMENT"), "DECRYPT_EEK");
        conf.set(((WHITELIST_KEY_ACL_PREFIX) + "ALL"), "DECRYPT_EEK");
        conf.set(((KEY_ACL) + "all_access.ALL"), "GENERATE_EEK");
        conf.set(((KEY_ACL) + "all_access.DECRYPT_EEK"), "ROLLOVER");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), "ROLLOVER");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "GENERATE_EEK"), "SOMEBODY");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "ALL"), "ROLLOVER");
        TestKMS.writeConf(testDir, conf);
        runServer(null, null, testDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                conf.setInt(DEFAULT_BITLENGTH_NAME, 128);
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                doAs("CREATE", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            Options options = new KeyProvider.Options(conf);
                            Map<String, String> attributes = options.getAttributes();
                            HashMap<String, String> newAttribs = new HashMap<String, String>(attributes);
                            newAttribs.put("key.acl.name", "test_key");
                            options.setAttributes(newAttribs);
                            KeyProvider.KeyVersion kv = kp.createKey("k0", options);
                            Assert.assertNull(kv.getMaterial());
                            KeyVersion rollVersion = kp.rollNewVersion("k0");
                            Assert.assertNull(rollVersion.getMaterial());
                            KeyProviderCryptoExtension kpce = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                            try {
                                kpce.generateEncryptedKey("k0");
                                Assert.fail("User [CREATE] should not be allowed to generate_eek on k0");
                            } catch (Exception e) {
                                // Ignore
                            }
                            newAttribs = new HashMap<String, String>(attributes);
                            newAttribs.put("key.acl.name", "all_access");
                            options.setAttributes(newAttribs);
                            try {
                                kp.createKey("kx", options);
                                Assert.fail("User [CREATE] should not be allowed to create kx");
                            } catch (Exception e) {
                                // Ignore
                            }
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                // Test whitelist key access..
                // DECRYPT_EEK is whitelisted for MANAGEMENT operations only
                doAs("DECRYPT_EEK", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            Options options = new KeyProvider.Options(conf);
                            Map<String, String> attributes = options.getAttributes();
                            HashMap<String, String> newAttribs = new HashMap<String, String>(attributes);
                            newAttribs.put("key.acl.name", "some_key");
                            options.setAttributes(newAttribs);
                            KeyProvider.KeyVersion kv = kp.createKey("kk0", options);
                            Assert.assertNull(kv.getMaterial());
                            KeyVersion rollVersion = kp.rollNewVersion("kk0");
                            Assert.assertNull(rollVersion.getMaterial());
                            KeyProviderCryptoExtension kpce = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                            try {
                                kpce.generateEncryptedKey("kk0");
                                Assert.fail("User [DECRYPT_EEK] should not be allowed to generate_eek on kk0");
                            } catch (Exception e) {
                                // Ignore
                            }
                            newAttribs = new HashMap<String, String>(attributes);
                            newAttribs.put("key.acl.name", "all_access");
                            options.setAttributes(newAttribs);
                            kp.createKey("kkx", options);
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("ROLLOVER", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            Options options = new KeyProvider.Options(conf);
                            Map<String, String> attributes = options.getAttributes();
                            HashMap<String, String> newAttribs = new HashMap<String, String>(attributes);
                            newAttribs.put("key.acl.name", "test_key2");
                            options.setAttributes(newAttribs);
                            KeyProvider.KeyVersion kv = kp.createKey("k1", options);
                            Assert.assertNull(kv.getMaterial());
                            KeyVersion rollVersion = kp.rollNewVersion("k1");
                            Assert.assertNull(rollVersion.getMaterial());
                            try {
                                kp.rollNewVersion("k0");
                                Assert.fail("User [ROLLOVER] should not be allowed to rollover k0");
                            } catch (Exception e) {
                                // Ignore
                            }
                            KeyProviderCryptoExtension kpce = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                            try {
                                kpce.generateEncryptedKey("k1");
                                Assert.fail("User [ROLLOVER] should not be allowed to generate_eek on k1");
                            } catch (Exception e) {
                                // Ignore
                            }
                            newAttribs = new HashMap<String, String>(attributes);
                            newAttribs.put("key.acl.name", "all_access");
                            options.setAttributes(newAttribs);
                            try {
                                kp.createKey("kx", options);
                                Assert.fail("User [ROLLOVER] should not be allowed to create kx");
                            } catch (Exception e) {
                                // Ignore
                            }
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("GET", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            Options options = new KeyProvider.Options(conf);
                            Map<String, String> attributes = options.getAttributes();
                            HashMap<String, String> newAttribs = new HashMap<String, String>(attributes);
                            newAttribs.put("key.acl.name", "test_key");
                            options.setAttributes(newAttribs);
                            try {
                                kp.createKey("k2", options);
                                Assert.fail("User [GET] should not be allowed to create key..");
                            } catch (Exception e) {
                                // Ignore
                            }
                            newAttribs = new HashMap<String, String>(attributes);
                            newAttribs.put("key.acl.name", "all_access");
                            options.setAttributes(newAttribs);
                            try {
                                kp.createKey("kx", options);
                                Assert.fail("User [GET] should not be allowed to create kx");
                            } catch (Exception e) {
                                // Ignore
                            }
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                final EncryptedKeyVersion ekv = doAs("GENERATE_EEK", new PrivilegedExceptionAction<EncryptedKeyVersion>() {
                    @Override
                    public EncryptedKeyVersion run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            Options options = new KeyProvider.Options(conf);
                            Map<String, String> attributes = options.getAttributes();
                            HashMap<String, String> newAttribs = new HashMap<String, String>(attributes);
                            newAttribs.put("key.acl.name", "all_access");
                            options.setAttributes(newAttribs);
                            kp.createKey("kx", options);
                            KeyProviderCryptoExtension kpce = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                            try {
                                return kpce.generateEncryptedKey("kx");
                            } catch (Exception e) {
                                Assert.fail("User [GENERATE_EEK] should be allowed to generate_eek on kx");
                            }
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("ROLLOVER", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            KeyProviderCryptoExtension kpce = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                            kpce.decryptEncryptedKey(ekv);
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                return null;
            }
        });
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), "");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "GENERATE_EEK"), "*");
        TestKMS.writeConf(testDir, conf);
        runServer(null, null, testDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                conf.setInt(DEFAULT_BITLENGTH_NAME, 128);
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                doAs("GENERATE_EEK", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        KeyProviderCryptoExtension kpce = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                        EncryptedKeyVersion ekv = kpce.generateEncryptedKey("k1");
                        kpce.reencryptEncryptedKey(ekv);
                        List<EncryptedKeyVersion> ekvs = new ArrayList<>(2);
                        ekvs.add(ekv);
                        ekvs.add(ekv);
                        kpce.reencryptEncryptedKeys(ekvs);
                        return null;
                    }
                });
                return null;
            }
        });
    }

    @Test
    public void testKMSRestartKerberosAuth() throws Exception {
        doKMSRestart(true);
    }

    @Test
    public void testKMSRestartSimpleAuth() throws Exception {
        doKMSRestart(false);
    }

    @Test
    public void testKMSAuthFailureRetry() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        final File testDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(testDir, conf);
        conf.set("hadoop.kms.authentication.kerberos.keytab", TestKMS.keytab.getAbsolutePath());
        conf.set("hadoop.kms.authentication.kerberos.principal", "HTTP/localhost");
        conf.set("hadoop.kms.authentication.kerberos.name.rules", "DEFAULT");
        conf.set("hadoop.kms.authentication.token.validity", "1");
        for (KMSACLs.Type type : Type.values()) {
            conf.set(type.getAclConfigKey(), type.toString());
        }
        conf.set(CREATE.getAclConfigKey(), ((CREATE.toString()) + ",SET_KEY_MATERIAL"));
        conf.set(ROLLOVER.getAclConfigKey(), ((ROLLOVER.toString()) + ",SET_KEY_MATERIAL"));
        conf.set(((KEY_ACL) + "k0.ALL"), "*");
        conf.set(((KEY_ACL) + "k1.ALL"), "*");
        conf.set(((KEY_ACL) + "k2.ALL"), "*");
        conf.set(((KEY_ACL) + "k3.ALL"), "*");
        conf.set(((KEY_ACL) + "k4.ALL"), "*");
        TestKMS.writeConf(testDir, conf);
        runServer(null, null, testDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                conf.setInt(DEFAULT_BITLENGTH_NAME, 128);
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                doAs("SET_KEY_MATERIAL", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        kp.createKey("k0", new byte[16], new KeyProvider.Options(conf));
                        // This happens before rollover
                        kp.createKey("k1", new byte[16], new KeyProvider.Options(conf));
                        // Atleast 2 rollovers.. so should induce signer Exception
                        Thread.sleep(3500);
                        kp.createKey("k2", new byte[16], new KeyProvider.Options(conf));
                        return null;
                    }
                });
                return null;
            }
        });
        // Test retry count
        runServer(null, null, testDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                conf.setInt(DEFAULT_BITLENGTH_NAME, 128);
                conf.setInt(AUTH_RETRY, 0);
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                doAs("SET_KEY_MATERIAL", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        kp.createKey("k3", new byte[16], new KeyProvider.Options(conf));
                        // Atleast 2 rollovers.. so should induce signer Exception
                        Thread.sleep(3500);
                        try {
                            kp.createKey("k4", new byte[16], new KeyProvider.Options(conf));
                            Assert.fail("This should not succeed..");
                        } catch (IOException e) {
                            Assert.assertTrue(("HTTP exception must be a 401 : " + (e.getMessage())), e.getMessage().contains("401"));
                        }
                        return null;
                    }
                });
                return null;
            }
        });
    }

    @Test
    @SuppressWarnings("checkstyle:methodlength")
    public void testACLs() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        final File testDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(testDir, conf);
        conf.set("hadoop.kms.authentication.type", "kerberos");
        conf.set("hadoop.kms.authentication.kerberos.keytab", TestKMS.keytab.getAbsolutePath());
        conf.set("hadoop.kms.authentication.kerberos.principal", "HTTP/localhost");
        conf.set("hadoop.kms.authentication.kerberos.name.rules", "DEFAULT");
        for (KMSACLs.Type type : Type.values()) {
            conf.set(type.getAclConfigKey(), type.toString());
        }
        conf.set(CREATE.getAclConfigKey(), ((CREATE.toString()) + ",SET_KEY_MATERIAL"));
        conf.set(ROLLOVER.getAclConfigKey(), ((ROLLOVER.toString()) + ",SET_KEY_MATERIAL"));
        conf.set(((KEY_ACL) + "k0.ALL"), "*");
        conf.set(((KEY_ACL) + "k1.ALL"), "*");
        TestKMS.writeConf(testDir, conf);
        runServer(null, null, testDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                conf.setInt(DEFAULT_BITLENGTH_NAME, 128);
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                // nothing allowed
                doAs("client", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            kp.createKey("k", new KeyProvider.Options(conf));
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        try {
                            kp.createKey("k", new byte[16], new KeyProvider.Options(conf));
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        try {
                            kp.rollNewVersion("k");
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        try {
                            kp.rollNewVersion("k", new byte[16]);
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        try {
                            kp.getKeys();
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        try {
                            kp.getKeysMetadata("k");
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        try {
                            // we are using JavaKeyStoreProvider for testing, so we know how
                            // the keyversion is created.
                            kp.getKeyVersion("k@0");
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        try {
                            kp.getCurrentKey("k");
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        try {
                            kp.getMetadata("k");
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        try {
                            kp.getKeyVersions("k");
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("CREATE", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            KeyProvider.KeyVersion kv = kp.createKey("k0", new KeyProvider.Options(conf));
                            Assert.assertNull(kv.getMaterial());
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("DELETE", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            kp.deleteKey("k0");
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("SET_KEY_MATERIAL", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            KeyProvider.KeyVersion kv = kp.createKey("k1", new byte[16], new KeyProvider.Options(conf));
                            Assert.assertNull(kv.getMaterial());
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("ROLLOVER", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            KeyProvider.KeyVersion kv = kp.rollNewVersion("k1");
                            Assert.assertNull(kv.getMaterial());
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("SET_KEY_MATERIAL", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            KeyProvider.KeyVersion kv = kp.rollNewVersion("k1", new byte[16]);
                            Assert.assertNull(kv.getMaterial());
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                final KeyVersion currKv = doAs("GET", new PrivilegedExceptionAction<KeyVersion>() {
                    @Override
                    public KeyVersion run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            kp.getKeyVersion("k1@0");
                            KeyVersion kv = kp.getCurrentKey("k1");
                            return kv;
                        } catch (Exception ex) {
                            Assert.fail(ex.toString());
                        }
                        return null;
                    }
                });
                final EncryptedKeyVersion encKv = doAs("GENERATE_EEK", new PrivilegedExceptionAction<EncryptedKeyVersion>() {
                    @Override
                    public EncryptedKeyVersion run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            KeyProviderCryptoExtension kpCE = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                            EncryptedKeyVersion ek1 = kpCE.generateEncryptedKey(currKv.getName());
                            return ek1;
                        } catch (Exception ex) {
                            Assert.fail(ex.toString());
                        }
                        return null;
                    }
                });
                doAs("GENERATE_EEK", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        KeyProviderCryptoExtension kpCE = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                        kpCE.reencryptEncryptedKey(encKv);
                        List<EncryptedKeyVersion> ekvs = new ArrayList<>(2);
                        ekvs.add(encKv);
                        ekvs.add(encKv);
                        kpCE.reencryptEncryptedKeys(ekvs);
                        return null;
                    }
                });
                doAs("DECRYPT_EEK", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            KeyProviderCryptoExtension kpCE = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                            kpCE.decryptEncryptedKey(encKv);
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("GET_KEYS", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            kp.getKeys();
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("GET_METADATA", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            kp.getMetadata("k1");
                            kp.getKeysMetadata("k1");
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                // stop the reloader, to avoid running while we are writing the new file
                KMSWebApp.getACLs().stopReloader();
                GenericTestUtils.setLogLevel(KMSConfiguration.LOG, Level.TRACE);
                // test ACL reloading
                conf.set(CREATE.getAclConfigKey(), "foo");
                conf.set(GENERATE_EEK.getAclConfigKey(), "foo");
                TestKMS.writeConf(testDir, conf);
                KMSWebApp.getACLs().forceNextReloadForTesting();
                KMSWebApp.getACLs().run();// forcing a reload by hand.

                // should not be able to create a key now
                doAs("CREATE", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        try {
                            KeyProvider kp = createProvider(uri, conf);
                            KeyProvider.KeyVersion kv = kp.createKey("k2", new KeyProvider.Options(conf));
                            Assert.fail();
                        } catch (AuthorizationException ex) {
                            // NOP
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("GENERATE_EEK", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            KeyProviderCryptoExtension kpCE = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                            kpCE.generateEncryptedKey("k1");
                        } catch (IOException ex) {
                            // This isn't an AuthorizationException because generate goes
                            // through the ValueQueue. See KMSCP#generateEncryptedKey.
                            if ((ex.getCause().getCause()) instanceof AuthorizationException) {
                                TestKMS.LOG.info("Caught expected exception.", ex);
                            } else {
                                throw ex;
                            }
                        }
                        return null;
                    }
                });
                doAs("GENERATE_EEK", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            KeyProviderCryptoExtension kpCE = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                            kpCE.reencryptEncryptedKey(encKv);
                            Assert.fail("Should not have been able to reencryptEncryptedKey");
                        } catch (AuthorizationException ex) {
                            TestKMS.LOG.info("reencryptEncryptedKey caught expected exception.", ex);
                        }
                        return null;
                    }
                });
                doAs("GENERATE_EEK", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        try {
                            KeyProviderCryptoExtension kpCE = KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp);
                            List<EncryptedKeyVersion> ekvs = new ArrayList<>(2);
                            ekvs.add(encKv);
                            ekvs.add(encKv);
                            kpCE.reencryptEncryptedKeys(ekvs);
                            Assert.fail("Should not have been able to reencryptEncryptedKeys");
                        } catch (AuthorizationException ex) {
                            TestKMS.LOG.info("reencryptEncryptedKeys caught expected exception.", ex);
                        }
                        return null;
                    }
                });
                return null;
            }
        });
    }

    @Test
    public void testKMSBlackList() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        File testDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(testDir, conf);
        conf.set("hadoop.kms.authentication.type", "kerberos");
        conf.set("hadoop.kms.authentication.kerberos.keytab", TestKMS.keytab.getAbsolutePath());
        conf.set("hadoop.kms.authentication.kerberos.principal", "HTTP/localhost");
        conf.set("hadoop.kms.authentication.kerberos.name.rules", "DEFAULT");
        for (KMSACLs.Type type : Type.values()) {
            conf.set(type.getAclConfigKey(), " ");
        }
        conf.set(CREATE.getAclConfigKey(), "client,hdfs,otheradmin");
        conf.set(GENERATE_EEK.getAclConfigKey(), "client,hdfs,otheradmin");
        conf.set(DECRYPT_EEK.getAclConfigKey(), "client,hdfs,otheradmin");
        conf.set(DECRYPT_EEK.getBlacklistConfigKey(), "hdfs,otheradmin");
        conf.set(((KEY_ACL) + "ck0.ALL"), "*");
        conf.set(((KEY_ACL) + "ck1.ALL"), "*");
        TestKMS.writeConf(testDir, conf);
        runServer(null, null, testDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                conf.setInt(DEFAULT_BITLENGTH_NAME, 128);
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                doAs("client", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        try {
                            KeyProvider kp = createProvider(uri, conf);
                            KeyProvider.KeyVersion kv = kp.createKey("ck0", new KeyProvider.Options(conf));
                            EncryptedKeyVersion eek = generateEncryptedKey("ck0");
                            ((CryptoExtension) (kp)).decryptEncryptedKey(eek);
                            Assert.assertNull(kv.getMaterial());
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("hdfs", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        try {
                            KeyProvider kp = createProvider(uri, conf);
                            KeyProvider.KeyVersion kv = kp.createKey("ck1", new KeyProvider.Options(conf));
                            EncryptedKeyVersion eek = generateEncryptedKey("ck1");
                            ((CryptoExtension) (kp)).decryptEncryptedKey(eek);
                            Assert.fail("admin user must not be allowed to decrypt !!");
                        } catch (Exception ex) {
                        }
                        return null;
                    }
                });
                doAs("otheradmin", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        try {
                            KeyProvider kp = createProvider(uri, conf);
                            KeyProvider.KeyVersion kv = kp.createKey("ck2", new KeyProvider.Options(conf));
                            EncryptedKeyVersion eek = generateEncryptedKey("ck2");
                            ((CryptoExtension) (kp)).decryptEncryptedKey(eek);
                            Assert.fail("admin user must not be allowed to decrypt !!");
                        } catch (Exception ex) {
                        }
                        return null;
                    }
                });
                return null;
            }
        });
    }

    @Test
    public void testServicePrincipalACLs() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        File testDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(testDir, conf);
        conf.set("hadoop.kms.authentication.type", "kerberos");
        conf.set("hadoop.kms.authentication.kerberos.keytab", TestKMS.keytab.getAbsolutePath());
        conf.set("hadoop.kms.authentication.kerberos.principal", "HTTP/localhost");
        conf.set("hadoop.kms.authentication.kerberos.name.rules", "DEFAULT");
        for (KMSACLs.Type type : Type.values()) {
            conf.set(type.getAclConfigKey(), " ");
        }
        conf.set(CREATE.getAclConfigKey(), "client");
        conf.set(((DEFAULT_KEY_ACL_PREFIX) + "MANAGEMENT"), "client,client/host");
        TestKMS.writeConf(testDir, conf);
        runServer(null, null, testDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                conf.setInt(DEFAULT_BITLENGTH_NAME, 128);
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                doAs("client", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        try {
                            KeyProvider kp = createProvider(uri, conf);
                            KeyProvider.KeyVersion kv = kp.createKey("ck0", new KeyProvider.Options(conf));
                            Assert.assertNull(kv.getMaterial());
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                doAs("client/host", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        try {
                            KeyProvider kp = createProvider(uri, conf);
                            KeyProvider.KeyVersion kv = kp.createKey("ck1", new KeyProvider.Options(conf));
                            Assert.assertNull(kv.getMaterial());
                        } catch (Exception ex) {
                            Assert.fail(ex.getMessage());
                        }
                        return null;
                    }
                });
                return null;
            }
        });
    }

    /**
     * Test the configurable timeout in the KMSClientProvider.  Open up a
     * socket, but don't accept connections for it.  This leads to a timeout
     * when the KMS client attempts to connect.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testKMSTimeout() throws Exception {
        File confDir = TestKMS.getTestDir();
        org.apache.hadoop.conf.Configuration conf = createBaseKMSConf(confDir);
        conf.setInt(KMS_CLIENT_TIMEOUT_SECONDS, 1);
        TestKMS.writeConf(confDir, conf);
        ServerSocket sock;
        int port;
        try {
            sock = new ServerSocket(0, 50, InetAddress.getByName("localhost"));
            port = sock.getLocalPort();
        } catch (Exception e) {
            /* Problem creating socket?  Just bail. */
            return;
        }
        URL url = new URL((("http://localhost:" + port) + "/kms"));
        URI uri = TestKMS.createKMSUri(url);
        boolean caughtTimeout = false;
        try {
            KeyProvider kp = createProvider(uri, conf);
            kp.getKeys();
        } catch (SocketTimeoutException e) {
            caughtTimeout = true;
        } catch (IOException e) {
            Assert.assertTrue(("Caught unexpected exception" + (e.toString())), false);
        }
        caughtTimeout = false;
        try {
            KeyProvider kp = createProvider(uri, conf);
            KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp).generateEncryptedKey("a");
        } catch (SocketTimeoutException e) {
            caughtTimeout = true;
        } catch (IOException e) {
            Assert.assertTrue(("Caught unexpected exception" + (e.toString())), false);
        }
        caughtTimeout = false;
        try {
            KeyProvider kp = createProvider(uri, conf);
            KeyProviderCryptoExtension.createKeyProviderCryptoExtension(kp).decryptEncryptedKey(new KMSClientProvider.KMSEncryptedKeyVersion("a", "a", new byte[]{ 1, 2 }, "EEK", new byte[]{ 1, 2 }));
        } catch (SocketTimeoutException e) {
            caughtTimeout = true;
        } catch (IOException e) {
            Assert.assertTrue(("Caught unexpected exception" + (e.toString())), false);
        }
        Assert.assertTrue(caughtTimeout);
        sock.close();
    }

    @Test
    public void testDelegationTokenAccess() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        final File testDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(testDir, conf);
        conf.set("hadoop.kms.authentication.type", "kerberos");
        conf.set("hadoop.kms.authentication.kerberos.keytab", TestKMS.keytab.getAbsolutePath());
        conf.set("hadoop.kms.authentication.kerberos.principal", "HTTP/localhost");
        conf.set("hadoop.kms.authentication.kerberos.name.rules", "DEFAULT");
        final String keyA = "key_a";
        final String keyD = "key_d";
        conf.set((((KEY_ACL) + keyA) + ".ALL"), "*");
        conf.set((((KEY_ACL) + keyD) + ".ALL"), "*");
        TestKMS.writeConf(testDir, conf);
        runServer(null, null, testDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                conf.setInt(DEFAULT_BITLENGTH_NAME, 128);
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                final Credentials credentials = new Credentials();
                final UserGroupInformation nonKerberosUgi = UserGroupInformation.getCurrentUser();
                try {
                    KeyProvider kp = createProvider(uri, conf);
                    kp.createKey(keyA, new KeyProvider.Options(conf));
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                doAs("client", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        KeyProviderDelegationTokenExtension kpdte = KeyProviderDelegationTokenExtension.createKeyProviderDelegationTokenExtension(kp);
                        kpdte.addDelegationTokens("foo", credentials);
                        return null;
                    }
                });
                nonKerberosUgi.addCredentials(credentials);
                try {
                    KeyProvider kp = createProvider(uri, conf);
                    kp.createKey(keyA, new KeyProvider.Options(conf));
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                nonKerberosUgi.doAs(new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        KeyProvider kp = createProvider(uri, conf);
                        kp.createKey(keyD, new KeyProvider.Options(conf));
                        return null;
                    }
                });
                return null;
            }
        });
    }

    @Test
    public void testGetDelegationTokenByProxyUser() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.auth_to_local.mechanism", "mit");
        conf.set("hadoop.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(conf);
        final File testDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(testDir, conf);
        conf.set("hadoop.kms.authentication.type", "kerberos");
        conf.set("hadoop.kms.authentication.kerberos.keytab", TestKMS.keytab.getAbsolutePath());
        conf.set("hadoop.kms.authentication.kerberos.principal", "HTTP/localhost");
        conf.set("hadoop.kms.proxyuser.client.users", "foo/localhost");
        conf.set("hadoop.kms.proxyuser.client.hosts", "localhost");
        conf.set(((KEY_ACL) + "kcc.ALL"), "foo/localhost");
        TestKMS.writeConf(testDir, conf);
        runServer(null, null, testDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                // proxyuser client using kerberos credentials
                UserGroupInformation proxyUgi = UserGroupInformation.loginUserFromKeytabAndReturnUGI("client/host", TestKMS.keytab.getAbsolutePath());
                UserGroupInformation foo = UserGroupInformation.createProxyUser("foo/localhost", proxyUgi);
                final Credentials credentials = new Credentials();
                foo.doAs(new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        final KeyProvider kp = createProvider(uri, conf);
                        KeyProviderDelegationTokenExtension keyProviderDelegationTokenExtension = KeyProviderDelegationTokenExtension.createKeyProviderDelegationTokenExtension(kp);
                        keyProviderDelegationTokenExtension.addDelegationTokens("client", credentials);
                        Assert.assertNotNull(kp.createKey("kcc", new KeyProvider.Options(conf)));
                        return null;
                    }
                });
                // current user client using token credentials for proxy user
                UserGroupInformation nonKerberosUgi = UserGroupInformation.getCurrentUser();
                nonKerberosUgi.addCredentials(credentials);
                nonKerberosUgi.doAs(new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        final KeyProvider kp = createProvider(uri, conf);
                        Assert.assertNotNull(kp.getMetadata("kcc"));
                        return null;
                    }
                });
                return null;
            }
        });
    }

    @Test
    public void testDelegationTokensOpsHttpPseudo() throws Exception {
        testDelegationTokensOps(false, false);
    }

    @Test
    public void testDelegationTokensOpsHttpKerberized() throws Exception {
        testDelegationTokensOps(false, true);
    }

    @Test
    public void testDelegationTokensOpsHttpsPseudo() throws Exception {
        testDelegationTokensOps(true, false);
    }

    @Test
    public void testDelegationTokensOpsHttpsKerberized() throws Exception {
        testDelegationTokensOps(true, true);
    }

    @Test
    public void testDelegationTokensUpdatedInUGI() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        File confDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(confDir, conf);
        conf.set("hadoop.kms.authentication.delegation-token.max-lifetime.sec", "5");
        conf.set("hadoop.kms.authentication.delegation-token.renew-interval.sec", "5");
        TestKMS.writeConf(confDir, conf);
        // Running as a service (e.g. YARN in practice).
        runServer(null, null, confDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration clientConf = new org.apache.hadoop.conf.Configuration();
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                clientConf.set(KEY_PROVIDER_PATH, TestKMS.createKMSUri(getKMSUrl()).toString());
                final KeyProvider kp = createProvider(uri, clientConf);
                final KeyProviderDelegationTokenExtension kpdte = KeyProviderDelegationTokenExtension.createKeyProviderDelegationTokenExtension(kp);
                final InetSocketAddress kmsAddr = new InetSocketAddress(getKMSUrl().getHost(), getKMSUrl().getPort());
                // Job 1 (e.g. YARN log aggregation job), with user DT.
                final Collection<Token<?>> job1Token = new HashSet<>();
                doAs("client", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        // Get a DT and use it.
                        final Credentials credentials = new Credentials();
                        kpdte.addDelegationTokens("client", credentials);
                        Text tokenService = getTokenService(kp);
                        Assert.assertEquals(1, credentials.getAllTokens().size());
                        Assert.assertEquals(TOKEN_KIND, credentials.getToken(tokenService).getKind());
                        UserGroupInformation.getCurrentUser().addCredentials(credentials);
                        TestKMS.LOG.info("Added kms dt to credentials: {}", UserGroupInformation.getCurrentUser().getCredentials().getAllTokens());
                        Token<?> token = UserGroupInformation.getCurrentUser().getCredentials().getToken(tokenService);
                        Assert.assertNotNull(token);
                        job1Token.add(token);
                        // Decode the token to get max time.
                        ByteArrayInputStream buf = new ByteArrayInputStream(token.getIdentifier());
                        DataInputStream dis = new DataInputStream(buf);
                        DelegationTokenIdentifier id = new DelegationTokenIdentifier(token.getKind());
                        id.readFields(dis);
                        dis.close();
                        final long maxTime = id.getMaxDate();
                        // wait for token to expire.
                        Thread.sleep(5100);
                        Assert.assertTrue((("maxTime " + maxTime) + " is not less than now."), ((maxTime > 0) && (maxTime < (Time.now()))));
                        try {
                            kp.getKeys();
                            Assert.fail("Operation should fail since dt is expired.");
                        } catch (Exception e) {
                            TestKMS.LOG.info("Expected error.", e);
                        }
                        return null;
                    }
                });
                Assert.assertFalse(job1Token.isEmpty());
                // job 2 (e.g. Another YARN log aggregation job, with user DT.
                doAs("client", new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        // Get a new DT, but don't use it yet.
                        final Credentials newCreds = new Credentials();
                        kpdte.addDelegationTokens("client", newCreds);
                        Text tokenService = getTokenService(kp);
                        Assert.assertEquals(1, newCreds.getAllTokens().size());
                        Assert.assertEquals(TOKEN_KIND, newCreds.getToken(tokenService).getKind());
                        // Using job 1's DT should fail.
                        final Credentials oldCreds = new Credentials();
                        for (Token<?> token : job1Token) {
                            if (token.getKind().equals(TOKEN_KIND)) {
                                oldCreds.addToken(tokenService, token);
                            }
                        }
                        UserGroupInformation.getCurrentUser().addCredentials(oldCreds);
                        TestKMS.LOG.info("Added old kms dt to credentials: {}", UserGroupInformation.getCurrentUser().getCredentials().getAllTokens());
                        try {
                            kp.getKeys();
                            Assert.fail("Operation should fail since dt is expired.");
                        } catch (Exception e) {
                            TestKMS.LOG.info("Expected error.", e);
                        }
                        // Using the new DT should succeed.
                        Assert.assertEquals(1, newCreds.getAllTokens().size());
                        Assert.assertEquals(TOKEN_KIND, newCreds.getToken(tokenService).getKind());
                        UserGroupInformation.getCurrentUser().addCredentials(newCreds);
                        TestKMS.LOG.info("Credetials now are: {}", UserGroupInformation.getCurrentUser().getCredentials().getAllTokens());
                        kp.getKeys();
                        return null;
                    }
                });
                return null;
            }
        });
    }

    @Test
    public void testKMSWithZKSigner() throws Exception {
        doKMSWithZK(true, false);
    }

    @Test
    public void testKMSWithZKDTSM() throws Exception {
        doKMSWithZK(false, true);
    }

    @Test
    public void testKMSWithZKSignerAndDTSM() throws Exception {
        doKMSWithZK(true, true);
    }

    @Test
    public void testKMSHAZooKeeperDelegationToken() throws Exception {
        final int kmsSize = 2;
        doKMSWithZKWithDelegationToken(true, true, kmsSize);
    }

    @Test
    public void testProxyUserKerb() throws Exception {
        doProxyUserTest(true);
    }

    @Test
    public void testProxyUserSimple() throws Exception {
        doProxyUserTest(false);
    }

    @Test
    public void testWebHDFSProxyUserKerb() throws Exception {
        doWebHDFSProxyUserTest(true);
    }

    @Test
    public void testWebHDFSProxyUserSimple() throws Exception {
        doWebHDFSProxyUserTest(false);
    }

    @Test
    public void testTGTRenewal() throws Exception {
        Properties kdcConf = MiniKdc.createConf();
        kdcConf.setProperty(MAX_TICKET_LIFETIME, "3");
        kdcConf.setProperty(MIN_TICKET_LIFETIME, "3");
        TestKMS.setUpMiniKdc(kdcConf);
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        final File testDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(testDir, conf);
        conf.set("hadoop.kms.authentication.type", "kerberos");
        conf.set("hadoop.kms.authentication.kerberos.keytab", TestKMS.keytab.getAbsolutePath());
        conf.set("hadoop.kms.authentication.kerberos.principal", "HTTP/localhost");
        conf.set("hadoop.kms.authentication.kerberos.name.rules", "DEFAULT");
        conf.set("hadoop.kms.proxyuser.client.users", "*");
        conf.set("hadoop.kms.proxyuser.client.hosts", "*");
        TestKMS.writeConf(testDir, conf);
        runServer(null, null, testDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                final URI uri = TestKMS.createKMSUri(getKMSUrl());
                UserGroupInformation.setShouldRenewImmediatelyForTests(true);
                UserGroupInformation.loginUserFromKeytab("client", TestKMS.keytab.getAbsolutePath());
                final UserGroupInformation clientUgi = UserGroupInformation.getCurrentUser();
                clientUgi.doAs(new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        // Verify getKeys can relogin
                        Thread.sleep(3100);
                        KeyProvider kp = createProvider(uri, conf);
                        kp.getKeys();
                        // Verify addDelegationTokens can relogin
                        // (different code path inside KMSClientProvider than getKeys)
                        Thread.sleep(3100);
                        kp = createProvider(uri, conf);
                        ((KeyProviderDelegationTokenExtension.DelegationTokenExtension) (kp)).addDelegationTokens("myuser", new Credentials());
                        // Verify getKeys can relogin with proxy user
                        UserGroupInformation anotherUgi = UserGroupInformation.createProxyUser("client1", clientUgi);
                        anotherUgi.doAs(new PrivilegedExceptionAction<Void>() {
                            @Override
                            public Void run() throws Exception {
                                Thread.sleep(3100);
                                KeyProvider kp = createProvider(uri, conf);
                                kp.getKeys();
                                return null;
                            }
                        });
                        return null;
                    }
                });
                return null;
            }
        });
    }

    /* Test the jmx page can return, and contains the basic JvmMetrics. Only
    testing in simple mode since the page content is the same, kerberized
    or not.
     */
    @Test
    public void testKMSJMX() throws Exception {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        final File confDir = TestKMS.getTestDir();
        conf = createBaseKMSConf(confDir, conf);
        final String processName = "testkmsjmx";
        conf.set(METRICS_PROCESS_NAME_KEY, processName);
        TestKMS.writeConf(confDir, conf);
        runServer(null, null, confDir, new TestKMS.KMSCallable<Void>() {
            @Override
            public Void call() throws Exception {
                final URL jmxUrl = new URL(((((getKMSUrl()) + "/jmx?user.name=whatever&qry=Hadoop:service=") + processName) + ",name=JvmMetrics"));
                TestKMS.LOG.info(("Requesting jmx from " + jmxUrl));
                final StringBuilder sb = new StringBuilder();
                final InputStream in = jmxUrl.openConnection().getInputStream();
                final byte[] buffer = new byte[64 * 1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    sb.append(new String(buffer, 0, len));
                } 
                TestKMS.LOG.info(("jmx returned: " + (sb.toString())));
                Assert.assertTrue(sb.toString().contains("JvmMetrics"));
                return null;
            }
        });
    }
}

