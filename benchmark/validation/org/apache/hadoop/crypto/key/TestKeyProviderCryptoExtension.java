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
package org.apache.hadoop.crypto.key;


import KeyProvider.Options;
import KeyProviderCryptoExtension.EEK;
import KeyProviderCryptoExtension.EK;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension.EncryptedKeyVersion;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class TestKeyProviderCryptoExtension {
    private static final String CIPHER = "AES";

    private static final String ENCRYPTION_KEY_NAME = "fooKey";

    private static Configuration conf;

    private static KeyProvider kp;

    private static KeyProviderCryptoExtension kpExt;

    private static Options options;

    private static KeyProvider.KeyVersion encryptionKey;

    @Rule
    public Timeout testTimeout = new Timeout(180000);

    @Test
    public void testGenerateEncryptedKey() throws Exception {
        // Generate a new EEK and check it
        KeyProviderCryptoExtension.EncryptedKeyVersion ek1 = TestKeyProviderCryptoExtension.kpExt.generateEncryptedKey(TestKeyProviderCryptoExtension.encryptionKey.getName());
        Assert.assertEquals("Version name of EEK should be EEK", EEK, ek1.getEncryptedKeyVersion().getVersionName());
        Assert.assertEquals("Name of EEK should be encryption key name", TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME, ek1.getEncryptionKeyName());
        Assert.assertNotNull("Expected encrypted key material", ek1.getEncryptedKeyVersion().getMaterial());
        Assert.assertEquals(("Length of encryption key material and EEK material should " + "be the same"), TestKeyProviderCryptoExtension.encryptionKey.getMaterial().length, ek1.getEncryptedKeyVersion().getMaterial().length);
        // Decrypt EEK into an EK and check it
        KeyProvider.KeyVersion k1 = TestKeyProviderCryptoExtension.kpExt.decryptEncryptedKey(ek1);
        Assert.assertEquals(EK, k1.getVersionName());
        Assert.assertEquals(TestKeyProviderCryptoExtension.encryptionKey.getMaterial().length, k1.getMaterial().length);
        // Decrypt it again and it should be the same
        KeyProvider.KeyVersion k1a = TestKeyProviderCryptoExtension.kpExt.decryptEncryptedKey(ek1);
        Assert.assertArrayEquals(k1.getMaterial(), k1a.getMaterial());
        // Generate another EEK and make sure it's different from the first
        KeyProviderCryptoExtension.EncryptedKeyVersion ek2 = TestKeyProviderCryptoExtension.kpExt.generateEncryptedKey(TestKeyProviderCryptoExtension.encryptionKey.getName());
        KeyProvider.KeyVersion k2 = TestKeyProviderCryptoExtension.kpExt.decryptEncryptedKey(ek2);
        if (Arrays.equals(k1.getMaterial(), k2.getMaterial())) {
            Assert.fail("Generated EEKs should have different material!");
        }
        if (Arrays.equals(ek1.getEncryptedKeyIv(), ek2.getEncryptedKeyIv())) {
            Assert.fail("Generated EEKs should have different IVs!");
        }
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        // Get an EEK
        KeyProviderCryptoExtension.EncryptedKeyVersion eek = TestKeyProviderCryptoExtension.kpExt.generateEncryptedKey(TestKeyProviderCryptoExtension.encryptionKey.getName());
        final byte[] encryptedKeyIv = eek.getEncryptedKeyIv();
        final byte[] encryptedKeyMaterial = eek.getEncryptedKeyVersion().getMaterial();
        // Decrypt it manually
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(TestKeyProviderCryptoExtension.encryptionKey.getMaterial(), "AES"), new IvParameterSpec(KeyProviderCryptoExtension.EncryptedKeyVersion.deriveIV(encryptedKeyIv)));
        final byte[] manualMaterial = cipher.doFinal(encryptedKeyMaterial);
        // Test the createForDecryption factory method
        EncryptedKeyVersion eek2 = EncryptedKeyVersion.createForDecryption(eek.getEncryptionKeyName(), eek.getEncryptionKeyVersionName(), eek.getEncryptedKeyIv(), eek.getEncryptedKeyVersion().getMaterial());
        // Decrypt it with the API
        KeyProvider.KeyVersion decryptedKey = TestKeyProviderCryptoExtension.kpExt.decryptEncryptedKey(eek2);
        final byte[] apiMaterial = decryptedKey.getMaterial();
        Assert.assertArrayEquals("Wrong key material from decryptEncryptedKey", manualMaterial, apiMaterial);
    }

    @Test
    public void testReencryptEncryptedKey() throws Exception {
        // Generate a new EEK
        final KeyProviderCryptoExtension.EncryptedKeyVersion ek1 = TestKeyProviderCryptoExtension.kpExt.generateEncryptedKey(TestKeyProviderCryptoExtension.encryptionKey.getName());
        // Decrypt EEK into an EK and check it
        final KeyProvider.KeyVersion k1 = TestKeyProviderCryptoExtension.kpExt.decryptEncryptedKey(ek1);
        Assert.assertEquals(EK, k1.getVersionName());
        Assert.assertEquals(TestKeyProviderCryptoExtension.encryptionKey.getMaterial().length, k1.getMaterial().length);
        // Roll the EK
        TestKeyProviderCryptoExtension.kpExt.rollNewVersion(ek1.getEncryptionKeyName());
        // Reencrypt ek1
        final KeyProviderCryptoExtension.EncryptedKeyVersion ek2 = TestKeyProviderCryptoExtension.kpExt.reencryptEncryptedKey(ek1);
        Assert.assertEquals("Version name of EEK should be EEK", EEK, ek2.getEncryptedKeyVersion().getVersionName());
        Assert.assertEquals("Name of EEK should be encryption key name", TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME, ek2.getEncryptionKeyName());
        Assert.assertNotNull("Expected encrypted key material", ek2.getEncryptedKeyVersion().getMaterial());
        Assert.assertEquals(("Length of encryption key material and EEK material should " + "be the same"), TestKeyProviderCryptoExtension.encryptionKey.getMaterial().length, ek2.getEncryptedKeyVersion().getMaterial().length);
        if (Arrays.equals(ek2.getEncryptedKeyVersion().getMaterial(), ek1.getEncryptedKeyVersion().getMaterial())) {
            Assert.fail("Re-encrypted EEK should have different material");
        }
        // Decrypt the new EEK into an EK and check it
        final KeyProvider.KeyVersion k2 = TestKeyProviderCryptoExtension.kpExt.decryptEncryptedKey(ek2);
        Assert.assertEquals(EK, k2.getVersionName());
        Assert.assertEquals(TestKeyProviderCryptoExtension.encryptionKey.getMaterial().length, k2.getMaterial().length);
        // Re-encrypting the same EEK with the same EK should be deterministic
        final KeyProviderCryptoExtension.EncryptedKeyVersion ek2a = TestKeyProviderCryptoExtension.kpExt.reencryptEncryptedKey(ek1);
        Assert.assertEquals("Version name of EEK should be EEK", EEK, ek2a.getEncryptedKeyVersion().getVersionName());
        Assert.assertEquals("Name of EEK should be encryption key name", TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME, ek2a.getEncryptionKeyName());
        Assert.assertNotNull("Expected encrypted key material", ek2a.getEncryptedKeyVersion().getMaterial());
        Assert.assertEquals(("Length of encryption key material and EEK material should " + "be the same"), TestKeyProviderCryptoExtension.encryptionKey.getMaterial().length, ek2a.getEncryptedKeyVersion().getMaterial().length);
        if (Arrays.equals(ek2a.getEncryptedKeyVersion().getMaterial(), ek1.getEncryptedKeyVersion().getMaterial())) {
            Assert.fail("Re-encrypted EEK should have different material");
        }
        Assert.assertArrayEquals(ek2.getEncryptedKeyVersion().getMaterial(), ek2a.getEncryptedKeyVersion().getMaterial());
        // Re-encrypting an EEK with the same version EK should be no-op
        final KeyProviderCryptoExtension.EncryptedKeyVersion ek3 = TestKeyProviderCryptoExtension.kpExt.reencryptEncryptedKey(ek2);
        Assert.assertEquals("Version name of EEK should be EEK", EEK, ek3.getEncryptedKeyVersion().getVersionName());
        Assert.assertEquals("Name of EEK should be encryption key name", TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME, ek3.getEncryptionKeyName());
        Assert.assertNotNull("Expected encrypted key material", ek3.getEncryptedKeyVersion().getMaterial());
        Assert.assertEquals(("Length of encryption key material and EEK material should " + "be the same"), TestKeyProviderCryptoExtension.encryptionKey.getMaterial().length, ek3.getEncryptedKeyVersion().getMaterial().length);
        if (Arrays.equals(ek3.getEncryptedKeyVersion().getMaterial(), ek1.getEncryptedKeyVersion().getMaterial())) {
            Assert.fail("Re-encrypted EEK should have different material");
        }
        Assert.assertArrayEquals(ek2.getEncryptedKeyVersion().getMaterial(), ek3.getEncryptedKeyVersion().getMaterial());
    }

    @Test
    public void testReencryptEncryptedKeys() throws Exception {
        List<EncryptedKeyVersion> ekvs = new ArrayList<>(4);
        // Generate 2 new EEKs @v0 and add to the list
        ekvs.add(TestKeyProviderCryptoExtension.kpExt.generateEncryptedKey(TestKeyProviderCryptoExtension.encryptionKey.getName()));
        ekvs.add(TestKeyProviderCryptoExtension.kpExt.generateEncryptedKey(TestKeyProviderCryptoExtension.encryptionKey.getName()));
        // Roll the EK
        TestKeyProviderCryptoExtension.kpExt.rollNewVersion(ekvs.get(0).getEncryptionKeyName());
        // Generate 1 new EEK @v1 add to the list.
        ekvs.add(TestKeyProviderCryptoExtension.kpExt.generateEncryptedKey(TestKeyProviderCryptoExtension.encryptionKey.getName()));
        // Roll the EK again
        TestKeyProviderCryptoExtension.kpExt.rollNewVersion(ekvs.get(0).getEncryptionKeyName());
        // Generate 1 new EEK @v2 add to the list.
        ekvs.add(TestKeyProviderCryptoExtension.kpExt.generateEncryptedKey(TestKeyProviderCryptoExtension.encryptionKey.getName()));
        // leave a deep copy of the original, for verification purpose.
        List<EncryptedKeyVersion> ekvsOrig = new ArrayList(ekvs.size());
        for (EncryptedKeyVersion ekv : ekvs) {
            ekvsOrig.add(new EncryptedKeyVersion(ekv.getEncryptionKeyName(), ekv.getEncryptionKeyVersionName(), ekv.getEncryptedKeyIv(), ekv.getEncryptedKeyVersion()));
        }
        // Reencrypt ekvs
        TestKeyProviderCryptoExtension.kpExt.reencryptEncryptedKeys(ekvs);
        // Verify each ekv
        for (int i = 0; i < (ekvs.size()); ++i) {
            final EncryptedKeyVersion ekv = ekvs.get(i);
            final EncryptedKeyVersion orig = ekvsOrig.get(i);
            Assert.assertEquals("Version name should be EEK", EEK, ekv.getEncryptedKeyVersion().getVersionName());
            Assert.assertEquals(("Encryption key name should be " + (TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME)), TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME, ekv.getEncryptionKeyName());
            Assert.assertNotNull("Expected encrypted key material", ekv.getEncryptedKeyVersion().getMaterial());
            Assert.assertEquals(("Length of encryption key material and EEK material should " + "be the same"), TestKeyProviderCryptoExtension.encryptionKey.getMaterial().length, ekv.getEncryptedKeyVersion().getMaterial().length);
            Assert.assertFalse("Encrypted key material should not equal encryption key material", Arrays.equals(ekv.getEncryptedKeyVersion().getMaterial(), TestKeyProviderCryptoExtension.encryptionKey.getMaterial()));
            if (i < 3) {
                Assert.assertFalse("Re-encrypted EEK should have different material", Arrays.equals(ekv.getEncryptedKeyVersion().getMaterial(), orig.getEncryptedKeyVersion().getMaterial()));
            } else {
                Assert.assertTrue("Re-encrypted EEK should have same material", Arrays.equals(ekv.getEncryptedKeyVersion().getMaterial(), orig.getEncryptedKeyVersion().getMaterial()));
            }
            // Decrypt the new EEK into an EK and check it
            final KeyProvider.KeyVersion kv = TestKeyProviderCryptoExtension.kpExt.decryptEncryptedKey(ekv);
            Assert.assertEquals(EK, kv.getVersionName());
            // Decrypt it again and it should be the same
            KeyProvider.KeyVersion kv1 = TestKeyProviderCryptoExtension.kpExt.decryptEncryptedKey(ekv);
            Assert.assertArrayEquals(kv.getMaterial(), kv1.getMaterial());
            // Verify decrypting the new EEK and orig EEK gives the same material.
            final KeyProvider.KeyVersion origKv = TestKeyProviderCryptoExtension.kpExt.decryptEncryptedKey(orig);
            Assert.assertTrue(("Returned EEK and original EEK should both decrypt to the " + "same kv."), Arrays.equals(origKv.getMaterial(), kv.getMaterial()));
        }
    }

    @Test
    public void testNonDefaultCryptoExtensionSelectionWithCachingKeyProvider() throws Exception {
        Configuration config = new Configuration();
        KeyProvider localKp = new TestKeyProviderCryptoExtension.DummyCryptoExtensionKeyProvider(config);
        localKp = new CachingKeyProvider(localKp, 30000, 30000);
        EncryptedKeyVersion localEkv = getEncryptedKeyVersion(config, localKp);
        Assert.assertEquals("dummyFakeKey@1", localEkv.getEncryptionKeyVersionName());
    }

    @Test
    public void testDefaultCryptoExtensionSelectionWithCachingKeyProvider() throws Exception {
        Configuration config = new Configuration();
        KeyProvider localKp = new UserProvider.Factory().createProvider(new URI("user:///"), config);
        localKp = new CachingKeyProvider(localKp, 30000, 30000);
        EncryptedKeyVersion localEkv = getEncryptedKeyVersion(config, localKp);
        Assert.assertEquals(((TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME) + "@0"), localEkv.getEncryptionKeyVersionName());
    }

    @Test
    public void testNonDefaultCryptoExtensionSelectionOnKeyProviderExtension() throws Exception {
        Configuration config = new Configuration();
        KeyProvider localKp = new UserProvider.Factory().createProvider(new URI("user:///"), config);
        localKp = new TestKeyProviderCryptoExtension.DummyCachingCryptoExtensionKeyProvider(localKp, 30000, 30000);
        EncryptedKeyVersion localEkv = getEncryptedKeyVersion(config, localKp);
        Assert.assertEquals("dummyCachingFakeKey@1", localEkv.getEncryptionKeyVersionName());
    }

    /**
     * Dummy class to test that this key provider is chosen to
     * provide CryptoExtension services over the DefaultCryptoExtension.
     */
    public class DummyCryptoExtensionKeyProvider extends KeyProvider implements KeyProviderCryptoExtension.CryptoExtension {
        private KeyProvider kp;

        private KeyProvider.KeyVersion kv;

        private EncryptedKeyVersion ekv;

        public DummyCryptoExtensionKeyProvider(Configuration conf) {
            super(conf);
            conf = new Configuration();
            try {
                this.kp = new UserProvider.Factory().createProvider(new URI("user:///"), conf);
                this.kv = new KeyProvider.KeyVersion(TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME, "dummyFakeKey@1", new byte[16]);
                this.ekv = new EncryptedKeyVersion(TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME, "dummyFakeKey@1", new byte[16], kv);
            } catch (URISyntaxException e) {
                Assert.fail(e.getMessage());
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
        }

        @Override
        public void warmUpEncryptedKeys(String... keyNames) throws IOException {
        }

        @Override
        public void drain(String keyName) {
        }

        @Override
        public EncryptedKeyVersion generateEncryptedKey(String encryptionKeyName) throws IOException, GeneralSecurityException {
            return this.ekv;
        }

        @Override
        public EncryptedKeyVersion reencryptEncryptedKey(EncryptedKeyVersion ekv) throws IOException, GeneralSecurityException {
            return ekv;
        }

        @Override
        public void reencryptEncryptedKeys(List<EncryptedKeyVersion> ekvs) throws IOException, GeneralSecurityException {
        }

        @Override
        public KeyProvider.KeyVersion decryptEncryptedKey(EncryptedKeyVersion encryptedKeyVersion) throws IOException, GeneralSecurityException {
            return kv;
        }

        @Override
        public KeyProvider.KeyVersion getKeyVersion(String versionName) throws IOException {
            return this.kp.getKeyVersion(versionName);
        }

        @Override
        public List<String> getKeys() throws IOException {
            return this.kp.getKeys();
        }

        @Override
        public List<KeyProvider.KeyVersion> getKeyVersions(String name) throws IOException {
            return this.kp.getKeyVersions(name);
        }

        @Override
        public Metadata getMetadata(String name) throws IOException {
            return this.kp.getMetadata(name);
        }

        @Override
        public KeyProvider.KeyVersion createKey(String name, byte[] material, Options localOptions) throws IOException {
            return this.kp.createKey(name, material, localOptions);
        }

        @Override
        public void deleteKey(String name) throws IOException {
            this.kp.deleteKey(name);
        }

        @Override
        public KeyProvider.KeyVersion rollNewVersion(String name, byte[] material) throws IOException {
            return this.kp.rollNewVersion(name, material);
        }

        @Override
        public void flush() throws IOException {
            this.kp.flush();
        }
    }

    /**
     * Dummy class to verify that CachingKeyProvider is used to
     * provide CryptoExtension services if the CachingKeyProvider itself
     * implements CryptoExtension.
     */
    public class DummyCachingCryptoExtensionKeyProvider extends CachingKeyProvider implements KeyProviderCryptoExtension.CryptoExtension {
        private KeyProvider kp;

        private KeyProvider.KeyVersion kv;

        private EncryptedKeyVersion ekv;

        public DummyCachingCryptoExtensionKeyProvider(KeyProvider keyProvider, long keyTimeoutMillis, long currKeyTimeoutMillis) {
            super(keyProvider, keyTimeoutMillis, currKeyTimeoutMillis);
            TestKeyProviderCryptoExtension.conf = new Configuration();
            try {
                this.kp = new UserProvider.Factory().createProvider(new URI("user:///"), TestKeyProviderCryptoExtension.conf);
                this.kv = new KeyProvider.KeyVersion(TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME, "dummyCachingFakeKey@1", new byte[16]);
                this.ekv = new EncryptedKeyVersion(TestKeyProviderCryptoExtension.ENCRYPTION_KEY_NAME, "dummyCachingFakeKey@1", new byte[16], kv);
            } catch (URISyntaxException e) {
                Assert.fail(e.getMessage());
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
        }

        @Override
        public void warmUpEncryptedKeys(String... keyNames) throws IOException {
        }

        @Override
        public void drain(String keyName) {
        }

        @Override
        public EncryptedKeyVersion generateEncryptedKey(String encryptionKeyName) throws IOException, GeneralSecurityException {
            return this.ekv;
        }

        @Override
        public KeyProvider.KeyVersion decryptEncryptedKey(EncryptedKeyVersion encryptedKeyVersion) throws IOException, GeneralSecurityException {
            return kv;
        }

        @Override
        public EncryptedKeyVersion reencryptEncryptedKey(EncryptedKeyVersion ekv) throws IOException, GeneralSecurityException {
            return ekv;
        }

        @Override
        public void reencryptEncryptedKeys(List<EncryptedKeyVersion> ekvs) throws IOException, GeneralSecurityException {
        }
    }
}

