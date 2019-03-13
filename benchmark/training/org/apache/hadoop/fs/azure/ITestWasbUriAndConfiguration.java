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
package org.apache.hadoop.fs.azure;


import AzureNativeFileSystemStore.KEY_USE_SECURE_MODE;
import AzureNativeFileSystemStore.USER_AGENT_ID_KEY;
import CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.net.URI;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.AbstractFileSystem;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.security.ProviderUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static SimpleKeyProvider.KEY_ACCOUNT_KEY_PREFIX;
import static org.apache.hadoop.fs.azure.AzureBlobStorageTestAccount.CreateOptions.CreateContainer;
import static org.apache.hadoop.fs.azure.AzureBlobStorageTestAccount.CreateOptions.Readonly;
import static org.apache.hadoop.fs.azure.AzureBlobStorageTestAccount.CreateOptions.UseSas;


public class ITestWasbUriAndConfiguration extends AbstractWasbTestWithTimeout {
    private static final int FILE_SIZE = 4096;

    private static final String PATH_DELIMITER = "/";

    protected String accountName;

    protected String accountKey;

    protected static Configuration conf = null;

    private boolean runningInSASMode = false;

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    private AzureBlobStorageTestAccount testAccount;

    // Positive tests to exercise making a connection with to Azure account using
    // account key.
    @Test
    public void testConnectUsingKey() throws Exception {
        testAccount = AzureBlobStorageTestAccount.create();
        Assume.assumeNotNull(testAccount);
        // Validate input and output on the connection.
        Assert.assertTrue(validateIOStreams(new Path("/wasb_scheme")));
    }

    @Test
    public void testConnectUsingSAS() throws Exception {
        Assume.assumeFalse(runningInSASMode);
        // Create the test account with SAS credentials.
        testAccount = AzureBlobStorageTestAccount.create("", EnumSet.of(UseSas, CreateContainer));
        Assume.assumeNotNull(testAccount);
        // Validate input and output on the connection.
        // NOTE: As of 4/15/2013, Azure Storage has a deficiency that prevents the
        // full scenario from working (CopyFromBlob doesn't work with SAS), so
        // just do a minor check until that is corrected.
        Assert.assertFalse(testAccount.getFileSystem().exists(new Path("/IDontExist")));
        // assertTrue(validateIOStreams(new Path("/sastest.txt")));
    }

    @Test
    public void testConnectUsingSASReadonly() throws Exception {
        Assume.assumeFalse(runningInSASMode);
        // Create the test account with SAS credentials.
        testAccount = AzureBlobStorageTestAccount.create("", EnumSet.of(UseSas, CreateContainer, Readonly));
        Assume.assumeNotNull(testAccount);
        // Create a blob in there
        final String blobKey = "blobForReadonly";
        CloudBlobContainer container = testAccount.getRealContainer();
        CloudBlockBlob blob = container.getBlockBlobReference(blobKey);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{ 1, 2, 3 });
        blob.upload(inputStream, 3);
        inputStream.close();
        // Make sure we can read it from the file system
        Path filePath = new Path(("/" + blobKey));
        FileSystem fs = testAccount.getFileSystem();
        Assert.assertTrue(fs.exists(filePath));
        byte[] obtained = new byte[3];
        DataInputStream obtainedInputStream = fs.open(filePath);
        obtainedInputStream.readFully(obtained);
        obtainedInputStream.close();
        Assert.assertEquals(3, obtained[2]);
    }

    /**
     * Use secure mode, which will automatically switch to SAS,
     */
    @Test
    public void testConnectUsingSecureSAS() throws Exception {
        // Create the test account with SAS credentials.
        Configuration conf = new Configuration();
        conf.setBoolean(KEY_USE_SECURE_MODE, true);
        testAccount = AzureBlobStorageTestAccount.create("", EnumSet.of(UseSas), conf);
        Assume.assumeNotNull(testAccount);
        NativeAzureFileSystem fs = testAccount.getFileSystem();
        AzureException ex = intercept(AzureException.class, SR.ENUMERATION_ERROR, () -> ContractTestUtils.writeTextFile(fs, new Path("/testConnectUsingSecureSAS"), "testConnectUsingSecureSAS", true));
        StorageException cause = getCause(StorageException.class, getCause(NoSuchElementException.class, ex));
        GenericTestUtils.assertExceptionContains("The specified container does not exist", cause);
    }

    @Test
    public void testConnectUsingAnonymous() throws Exception {
        // Create test account with anonymous credentials
        testAccount = AzureBlobStorageTestAccount.createAnonymous("testWasb.txt", ITestWasbUriAndConfiguration.FILE_SIZE);
        Assume.assumeNotNull(testAccount);
        // Read the file from the public folder using anonymous credentials.
        Assert.assertEquals(ITestWasbUriAndConfiguration.FILE_SIZE, readInputStream(new Path("/testWasb.txt")));
    }

    @Test
    public void testConnectToEmulator() throws Exception {
        testAccount = AzureBlobStorageTestAccount.createForEmulator();
        Assume.assumeNotNull(testAccount);
        Assert.assertTrue(validateIOStreams(new Path("/testFile")));
    }

    /**
     * Tests that we can connect to fully qualified accounts outside of
     * blob.core.windows.net
     */
    @Test
    public void testConnectToFullyQualifiedAccountMock() throws Exception {
        Configuration conf = new Configuration();
        AzureBlobStorageTestAccount.setMockAccountKey(conf, "mockAccount.mock.authority.net");
        AzureNativeFileSystemStore store = new AzureNativeFileSystemStore();
        MockStorageInterface mockStorage = new MockStorageInterface();
        store.setAzureStorageInteractionLayer(mockStorage);
        NativeAzureFileSystem fs = new NativeAzureFileSystem(store);
        fs.initialize(new URI("wasb://mockContainer@mockAccount.mock.authority.net"), conf);
        fs.createNewFile(new Path("/x"));
        Assert.assertTrue(mockStorage.getBackingStore().exists("http://mockAccount.mock.authority.net/mockContainer/x"));
        fs.close();
    }

    @Test
    public void testMultipleContainers() throws Exception {
        AzureBlobStorageTestAccount firstAccount = AzureBlobStorageTestAccount.create("first");
        AzureBlobStorageTestAccount secondAccount = AzureBlobStorageTestAccount.create("second");
        Assume.assumeNotNull(firstAccount);
        Assume.assumeNotNull(secondAccount);
        try {
            FileSystem firstFs = firstAccount.getFileSystem();
            FileSystem secondFs = secondAccount.getFileSystem();
            Path testFile = new Path("/testWasb");
            Assert.assertTrue(validateIOStreams(firstFs, testFile));
            Assert.assertTrue(validateIOStreams(secondFs, testFile));
            // Make sure that we're really dealing with two file systems here.
            ITestWasbUriAndConfiguration.writeSingleByte(firstFs, testFile, 5);
            ITestWasbUriAndConfiguration.writeSingleByte(secondFs, testFile, 7);
            ITestWasbUriAndConfiguration.assertSingleByteValue(firstFs, testFile, 5);
            ITestWasbUriAndConfiguration.assertSingleByteValue(secondFs, testFile, 7);
        } finally {
            firstAccount.cleanup();
            secondAccount.cleanup();
        }
    }

    @Test
    public void testDefaultKeyProvider() throws Exception {
        Configuration conf = new Configuration();
        String account = "testacct";
        String key = "testkey";
        conf.set(((KEY_ACCOUNT_KEY_PREFIX) + account), key);
        String result = AzureNativeFileSystemStore.getAccountKeyFromConfiguration(account, conf);
        Assert.assertEquals(key, result);
    }

    @Test
    public void testCredsFromCredentialProvider() throws Exception {
        Assume.assumeFalse(runningInSASMode);
        String account = "testacct";
        String key = "testkey";
        // set up conf to have a cred provider
        final Configuration conf = new Configuration();
        final File file = tempDir.newFile("test.jks");
        final URI jks = ProviderUtils.nestURIForLocalJavaKeyStoreProvider(file.toURI());
        conf.set(CREDENTIAL_PROVIDER_PATH, jks.toString());
        provisionAccountKey(conf, account, key);
        // also add to configuration as clear text that should be overridden
        conf.set(((KEY_ACCOUNT_KEY_PREFIX) + account), (key + "cleartext"));
        String result = AzureNativeFileSystemStore.getAccountKeyFromConfiguration(account, conf);
        // result should contain the credential provider key not the config key
        Assert.assertEquals("AccountKey incorrect.", key, result);
    }

    @Test
    public void testValidKeyProvider() throws Exception {
        Configuration conf = new Configuration();
        String account = "testacct";
        String key = "testkey";
        conf.set(((KEY_ACCOUNT_KEY_PREFIX) + account), key);
        conf.setClass(("fs.azure.account.keyprovider." + account), SimpleKeyProvider.class, KeyProvider.class);
        String result = AzureNativeFileSystemStore.getAccountKeyFromConfiguration(account, conf);
        Assert.assertEquals(key, result);
    }

    @Test
    public void testInvalidKeyProviderNonexistantClass() throws Exception {
        Configuration conf = new Configuration();
        String account = "testacct";
        conf.set(("fs.azure.account.keyprovider." + account), "org.apache.Nonexistant.Class");
        try {
            AzureNativeFileSystemStore.getAccountKeyFromConfiguration(account, conf);
            Assert.fail(("Nonexistant key provider class should have thrown a " + "KeyProviderException"));
        } catch (KeyProviderException e) {
        }
    }

    @Test
    public void testInvalidKeyProviderWrongClass() throws Exception {
        Configuration conf = new Configuration();
        String account = "testacct";
        conf.set(("fs.azure.account.keyprovider." + account), "java.lang.String");
        try {
            AzureNativeFileSystemStore.getAccountKeyFromConfiguration(account, conf);
            Assert.fail(("Key provider class that doesn't implement KeyProvider " + "should have thrown a KeyProviderException"));
        } catch (KeyProviderException e) {
        }
    }

    /**
     * Tests the cases when the URI is specified with no authority, i.e.
     * wasb:///path/to/file.
     */
    @Test
    public void testNoUriAuthority() throws Exception {
        // For any combination of default FS being asv(s)/wasb(s)://c@a/ and
        // the actual URI being asv(s)/wasb(s):///, it should work.
        String[] wasbAliases = new String[]{ "wasb", "wasbs" };
        for (String defaultScheme : wasbAliases) {
            for (String wantedScheme : wasbAliases) {
                testAccount = AzureBlobStorageTestAccount.createMock();
                Configuration conf = testAccount.getFileSystem().getConf();
                String authority = testAccount.getFileSystem().getUri().getAuthority();
                URI defaultUri = new URI(defaultScheme, authority, null, null, null);
                conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, defaultUri.toString());
                // Add references to file system implementations for wasb and wasbs.
                conf.addResource("azure-test.xml");
                URI wantedUri = new URI((wantedScheme + ":///random/path"));
                NativeAzureFileSystem obtained = ((NativeAzureFileSystem) (FileSystem.get(wantedUri, conf)));
                Assert.assertNotNull(obtained);
                Assert.assertEquals(new URI(wantedScheme, authority, null, null, null), obtained.getUri());
                // Make sure makeQualified works as expected
                Path qualified = obtained.makeQualified(new Path(wantedUri));
                Assert.assertEquals(new URI(wantedScheme, authority, wantedUri.getPath(), null, null), qualified.toUri());
                // Cleanup for the next iteration to not cache anything in FS
                testAccount.cleanup();
                FileSystem.closeAll();
            }
        }
        // If the default FS is not a WASB FS, then specifying a URI without
        // authority for the Azure file system should throw.
        testAccount = AzureBlobStorageTestAccount.createMock();
        Configuration conf = testAccount.getFileSystem().getConf();
        conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, "file:///");
        try {
            FileSystem.get(new URI("wasb:///random/path"), conf);
            Assert.fail("Should've thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testWasbAsDefaultFileSystemHasNoPort() throws Exception {
        try {
            testAccount = AzureBlobStorageTestAccount.createMock();
            Configuration conf = testAccount.getFileSystem().getConf();
            String authority = testAccount.getFileSystem().getUri().getAuthority();
            URI defaultUri = new URI("wasb", authority, null, null, null);
            conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, defaultUri.toString());
            conf.addResource("azure-test.xml");
            FileSystem fs = FileSystem.get(conf);
            Assert.assertTrue((fs instanceof NativeAzureFileSystem));
            Assert.assertEquals((-1), fs.getUri().getPort());
            AbstractFileSystem afs = FileContext.getFileContext(conf).getDefaultFileSystem();
            Assert.assertTrue((afs instanceof Wasb));
            Assert.assertEquals((-1), afs.getUri().getPort());
        } finally {
            testAccount.cleanup();
            FileSystem.closeAll();
        }
    }

    /**
     * Tests the cases when the scheme specified is 'wasbs'.
     */
    @Test
    public void testAbstractFileSystemImplementationForWasbsScheme() throws Exception {
        try {
            testAccount = AzureBlobStorageTestAccount.createMock();
            Configuration conf = testAccount.getFileSystem().getConf();
            String authority = testAccount.getFileSystem().getUri().getAuthority();
            URI defaultUri = new URI("wasbs", authority, null, null, null);
            conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, defaultUri.toString());
            conf.set("fs.AbstractFileSystem.wasbs.impl", "org.apache.hadoop.fs.azure.Wasbs");
            conf.addResource("azure-test.xml");
            FileSystem fs = FileSystem.get(conf);
            Assert.assertTrue((fs instanceof NativeAzureFileSystem));
            Assert.assertEquals("wasbs", fs.getScheme());
            AbstractFileSystem afs = FileContext.getFileContext(conf).getDefaultFileSystem();
            Assert.assertTrue((afs instanceof Wasbs));
            Assert.assertEquals((-1), afs.getUri().getPort());
            Assert.assertEquals("wasbs", afs.getUri().getScheme());
        } finally {
            testAccount.cleanup();
            FileSystem.closeAll();
        }
    }

    @Test
    public void testCredentialProviderPathExclusions() throws Exception {
        String providerPath = "user:///,jceks://wasb/user/hrt_qa/sqoopdbpasswd.jceks," + "jceks://hdfs@nn1.example.com/my/path/test.jceks";
        Configuration config = new Configuration();
        config.set(CREDENTIAL_PROVIDER_PATH, providerPath);
        String newPath = "user:///,jceks://hdfs@nn1.example.com/my/path/test.jceks";
        excludeAndTestExpectations(config, newPath);
    }

    @Test
    public void testExcludeAllProviderTypesFromConfig() throws Exception {
        String providerPath = "jceks://wasb/tmp/test.jceks," + "jceks://wasb@/my/path/test.jceks";
        Configuration config = new Configuration();
        config.set(CREDENTIAL_PROVIDER_PATH, providerPath);
        String newPath = null;
        excludeAndTestExpectations(config, newPath);
    }

    @Test
    public void testUserAgentConfig() throws Exception {
        // Set the user agent
        try {
            testAccount = AzureBlobStorageTestAccount.createMock();
            Configuration conf = testAccount.getFileSystem().getConf();
            String authority = testAccount.getFileSystem().getUri().getAuthority();
            URI defaultUri = new URI("wasbs", authority, null, null, null);
            conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, defaultUri.toString());
            conf.set("fs.AbstractFileSystem.wasbs.impl", "org.apache.hadoop.fs.azure.Wasbs");
            conf.set(USER_AGENT_ID_KEY, "TestClient");
            FileSystem fs = FileSystem.get(conf);
            AbstractFileSystem afs = FileContext.getFileContext(conf).getDefaultFileSystem();
            Assert.assertTrue((afs instanceof Wasbs));
            Assert.assertEquals((-1), afs.getUri().getPort());
            Assert.assertEquals("wasbs", afs.getUri().getScheme());
        } finally {
            testAccount.cleanup();
            FileSystem.closeAll();
        }
        // Unset the user agent
        try {
            testAccount = AzureBlobStorageTestAccount.createMock();
            Configuration conf = testAccount.getFileSystem().getConf();
            String authority = testAccount.getFileSystem().getUri().getAuthority();
            URI defaultUri = new URI("wasbs", authority, null, null, null);
            conf.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, defaultUri.toString());
            conf.set("fs.AbstractFileSystem.wasbs.impl", "org.apache.hadoop.fs.azure.Wasbs");
            conf.unset(USER_AGENT_ID_KEY);
            FileSystem fs = FileSystem.get(conf);
            AbstractFileSystem afs = FileContext.getFileContext(conf).getDefaultFileSystem();
            Assert.assertTrue((afs instanceof Wasbs));
            Assert.assertEquals((-1), afs.getUri().getPort());
            Assert.assertEquals("wasbs", afs.getUri().getScheme());
        } finally {
            testAccount.cleanup();
            FileSystem.closeAll();
        }
    }
}

