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


import JavaKeyStoreProvider.KEYSTORE_PASSWORD_FILE_KEY;
import JavaKeyStoreProvider.SCHEME_NAME;
import KeyProviderFactory.KEY_PROVIDER_PATH;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.ProviderUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;

import static KeyProviderFactory.KEY_PROVIDER_PATH;
import static UserProvider.SCHEME_NAME;


public class TestKeyProviderFactory {
    private FileSystemTestHelper fsHelper;

    private File testRootDir;

    @Test
    public void testFactory() throws Exception {
        Configuration conf = new Configuration();
        final String userUri = (SCHEME_NAME) + ":///";
        final Path jksPath = new Path(testRootDir.toString(), "test.jks");
        final String jksUri = ((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri().toString());
        conf.set(KEY_PROVIDER_PATH, ((userUri + ",") + jksUri));
        List<KeyProvider> providers = KeyProviderFactory.getProviders(conf);
        Assert.assertEquals(2, providers.size());
        Assert.assertEquals(UserProvider.class, providers.get(0).getClass());
        Assert.assertEquals(JavaKeyStoreProvider.class, providers.get(1).getClass());
        Assert.assertEquals(userUri, providers.get(0).toString());
        Assert.assertEquals(jksUri, providers.get(1).toString());
    }

    @Test
    public void testFactoryErrors() throws Exception {
        Configuration conf = new Configuration();
        conf.set(KEY_PROVIDER_PATH, "unknown:///");
        try {
            List<KeyProvider> providers = KeyProviderFactory.getProviders(conf);
            Assert.assertTrue("should throw!", false);
        } catch (IOException e) {
            Assert.assertEquals(("No KeyProviderFactory for unknown:/// in " + (KEY_PROVIDER_PATH)), e.getMessage());
        }
    }

    @Test
    public void testUriErrors() throws Exception {
        Configuration conf = new Configuration();
        conf.set(KEY_PROVIDER_PATH, "unkn@own:/x/y");
        try {
            List<KeyProvider> providers = KeyProviderFactory.getProviders(conf);
            Assert.assertTrue("should throw!", false);
        } catch (IOException e) {
            Assert.assertEquals((("Bad configuration of " + (KEY_PROVIDER_PATH)) + " at unkn@own:/x/y"), e.getMessage());
        }
    }

    @Test
    public void testUserProvider() throws Exception {
        Configuration conf = new Configuration();
        final String ourUrl = (SCHEME_NAME) + ":///";
        conf.set(KEY_PROVIDER_PATH, ourUrl);
        TestKeyProviderFactory.checkSpecificProvider(conf, ourUrl);
        // see if the credentials are actually in the UGI
        Credentials credentials = UserGroupInformation.getCurrentUser().getCredentials();
        Assert.assertArrayEquals(new byte[]{ 1 }, credentials.getSecretKey(new Text("key4@0")));
        Assert.assertArrayEquals(new byte[]{ 2 }, credentials.getSecretKey(new Text("key4@1")));
    }

    @Test
    public void testJksProvider() throws Exception {
        Configuration conf = new Configuration();
        final Path jksPath = new Path(testRootDir.toString(), "test.jks");
        final String ourUrl = ((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri());
        File file = new File(testRootDir, "test.jks");
        file.delete();
        conf.set(KEY_PROVIDER_PATH, ourUrl);
        TestKeyProviderFactory.checkSpecificProvider(conf, ourUrl);
        // START : Test flush error by failure injection
        conf.set(KEY_PROVIDER_PATH, ourUrl.replace(SCHEME_NAME, FailureInjectingJavaKeyStoreProvider.SCHEME_NAME));
        // get a new instance of the provider to ensure it was saved correctly
        KeyProvider provider = KeyProviderFactory.getProviders(conf).get(0);
        // inject failure during keystore write
        FailureInjectingJavaKeyStoreProvider fProvider = ((FailureInjectingJavaKeyStoreProvider) (provider));
        fProvider.setWriteFail(true);
        provider.createKey("key5", new byte[]{ 1 }, KeyProvider.options(conf).setBitLength(8));
        Assert.assertNotNull(provider.getCurrentKey("key5"));
        try {
            provider.flush();
            Assert.fail("Should not succeed");
        } catch (Exception e) {
            // Ignore
        }
        // SHould be reset to pre-flush state
        Assert.assertNull(provider.getCurrentKey("key5"));
        // Un-inject last failure and
        // inject failure during keystore backup
        fProvider.setWriteFail(false);
        fProvider.setBackupFail(true);
        provider.createKey("key6", new byte[]{ 1 }, KeyProvider.options(conf).setBitLength(8));
        Assert.assertNotNull(provider.getCurrentKey("key6"));
        try {
            provider.flush();
            Assert.fail("Should not succeed");
        } catch (Exception e) {
            // Ignore
        }
        // SHould be reset to pre-flush state
        Assert.assertNull(provider.getCurrentKey("key6"));
        // END : Test flush error by failure injection
        conf.set(KEY_PROVIDER_PATH, ourUrl.replace(FailureInjectingJavaKeyStoreProvider.SCHEME_NAME, SCHEME_NAME));
        Path path = ProviderUtils.unnestUri(new URI(ourUrl));
        FileSystem fs = path.getFileSystem(conf);
        FileStatus s = fs.getFileStatus(path);
        Assert.assertEquals("rw-------", s.getPermission().toString());
        Assert.assertTrue((file + " should exist"), file.isFile());
        // Corrupt file and Check if JKS can reload from _OLD file
        File oldFile = new File(((file.getPath()) + "_OLD"));
        file.renameTo(oldFile);
        file.delete();
        file.createNewFile();
        Assert.assertTrue(oldFile.exists());
        provider = KeyProviderFactory.getProviders(conf).get(0);
        Assert.assertTrue(file.exists());
        Assert.assertTrue((oldFile + "should be deleted"), (!(oldFile.exists())));
        verifyAfterReload(file, provider);
        Assert.assertTrue((!(oldFile.exists())));
        // _NEW and current file should not exist together
        File newFile = new File(((file.getPath()) + "_NEW"));
        newFile.createNewFile();
        try {
            provider = KeyProviderFactory.getProviders(conf).get(0);
            Assert.fail("_NEW and current file should not exist together !!");
        } catch (Exception e) {
            // Ignore
        } finally {
            if (newFile.exists()) {
                newFile.delete();
            }
        }
        // Load from _NEW file
        file.renameTo(newFile);
        file.delete();
        try {
            provider = KeyProviderFactory.getProviders(conf).get(0);
            Assert.assertFalse(newFile.exists());
            Assert.assertFalse(oldFile.exists());
        } catch (Exception e) {
            Assert.fail("JKS should load from _NEW file !!");
            // Ignore
        }
        verifyAfterReload(file, provider);
        // _NEW exists but corrupt.. must load from _OLD
        newFile.createNewFile();
        file.renameTo(oldFile);
        file.delete();
        try {
            provider = KeyProviderFactory.getProviders(conf).get(0);
            Assert.assertFalse(newFile.exists());
            Assert.assertFalse(oldFile.exists());
        } catch (Exception e) {
            Assert.fail("JKS should load from _OLD file !!");
            // Ignore
        } finally {
            if (newFile.exists()) {
                newFile.delete();
            }
        }
        verifyAfterReload(file, provider);
        // check permission retention after explicit change
        fs.setPermission(path, new FsPermission("777"));
        checkPermissionRetention(conf, ourUrl, path);
        // Check that an uppercase keyname results in an error
        provider = KeyProviderFactory.getProviders(conf).get(0);
        try {
            provider.createKey("UPPERCASE", KeyProvider.options(conf));
            Assert.fail(("Expected failure on creating key name with uppercase " + "characters"));
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Uppercase key names", e);
        }
    }

    @Test
    public void testJksProviderPasswordViaConfig() throws Exception {
        Configuration conf = new Configuration();
        final Path jksPath = new Path(testRootDir.toString(), "test.jks");
        final String ourUrl = ((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri());
        File file = new File(testRootDir, "test.jks");
        file.delete();
        try {
            conf.set(KEY_PROVIDER_PATH, ourUrl);
            conf.set(KEYSTORE_PASSWORD_FILE_KEY, "javakeystoreprovider.password");
            KeyProvider provider = KeyProviderFactory.getProviders(conf).get(0);
            provider.createKey("key3", new byte[16], KeyProvider.options(conf));
            provider.flush();
        } catch (Exception ex) {
            Assert.fail("could not create keystore with password file");
        }
        KeyProvider provider = KeyProviderFactory.getProviders(conf).get(0);
        Assert.assertNotNull(provider.getCurrentKey("key3"));
        try {
            conf.set(KEYSTORE_PASSWORD_FILE_KEY, "bar");
            KeyProviderFactory.getProviders(conf).get(0);
            Assert.fail("using non existing password file, it should fail");
        } catch (IOException ex) {
            // NOP
        }
        try {
            conf.set(KEYSTORE_PASSWORD_FILE_KEY, "core-site.xml");
            KeyProviderFactory.getProviders(conf).get(0);
            Assert.fail("using different password file, it should fail");
        } catch (IOException ex) {
            // NOP
        }
        try {
            conf.unset(KEYSTORE_PASSWORD_FILE_KEY);
            KeyProviderFactory.getProviders(conf).get(0);
            Assert.fail("No password file property, env not set, it should fail");
        } catch (IOException ex) {
            // NOP
        }
    }

    @Test
    public void testGetProviderViaURI() throws Exception {
        Configuration conf = new Configuration(false);
        final Path jksPath = new Path(testRootDir.toString(), "test.jks");
        URI uri = new URI((((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri())));
        KeyProvider kp = KeyProviderFactory.get(uri, conf);
        Assert.assertNotNull(kp);
        Assert.assertEquals(JavaKeyStoreProvider.class, kp.getClass());
        uri = new URI("foo://bar");
        kp = KeyProviderFactory.get(uri, conf);
        Assert.assertNull(kp);
    }

    @Test
    public void testJksProviderWithKeytoolKeys() throws Exception {
        final Configuration conf = new Configuration();
        final String keystoreDirAbsolutePath = conf.getResource("hdfs7067.keystore").getPath();
        final String ourUrl = ((JavaKeyStoreProvider.SCHEME_NAME) + "://file@/") + keystoreDirAbsolutePath;
        conf.set(KEY_PROVIDER_PATH, ourUrl);
        final KeyProvider provider = KeyProviderFactory.getProviders(conf).get(0);
        // Sanity check that we are using the right keystore
        @SuppressWarnings("unused")
        final KeyProvider.KeyVersion keyVersion = provider.getKeyVersion("testkey5@0");
        try {
            @SuppressWarnings("unused")
            final KeyProvider.KeyVersion keyVersionWrongKeyNameFormat = provider.getKeyVersion("testkey2");
            Assert.fail("should have thrown an exception");
        } catch (IOException e) {
            // No version in key path testkey2/
            GenericTestUtils.assertExceptionContains("No version in key path", e);
        }
        try {
            @SuppressWarnings("unused")
            final KeyProvider.KeyVersion keyVersionCurrentKeyNotWrongKeyNameFormat = provider.getCurrentKey("testkey5@0");
            Assert.fail("should have thrown an exception getting testkey5@0");
        } catch (IOException e) {
            // javax.crypto.spec.SecretKeySpec cannot be cast to
            // org.apache.hadoop.crypto.key.JavaKeyStoreProvider$KeyMetadata
            GenericTestUtils.assertExceptionContains("other non-Hadoop method", e);
        }
        try {
            @SuppressWarnings("unused")
            KeyProvider.KeyVersion keyVersionCurrentKeyNotReally = provider.getCurrentKey("testkey2");
            Assert.fail("should have thrown an exception getting testkey2");
        } catch (IOException e) {
            // javax.crypto.spec.SecretKeySpec cannot be cast to
            // org.apache.hadoop.crypto.key.JavaKeyStoreProvider$KeyMetadata
            GenericTestUtils.assertExceptionContains("other non-Hadoop method", e);
        }
    }
}

