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
package org.apache.hadoop.fs.s3a;


import Constants.ACCESS_KEY;
import Constants.BUFFER_DIR;
import Constants.ENDPOINT;
import Constants.FS_S3A_BLOCK_SIZE;
import Constants.MAX_ERROR_RETRIES;
import Constants.PATH_STYLE_ACCESS;
import Constants.PROXY_HOST;
import Constants.PROXY_PASSWORD;
import Constants.PROXY_PORT;
import Constants.PROXY_USERNAME;
import Constants.READAHEAD_RANGE;
import Constants.SECRET_KEY;
import Constants.SECURE_CONNECTIONS;
import Constants.USER_AGENT_PREFIX;
import CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH;
import S3xLoginHelper.Login;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.S3ClientOptions;
import java.io.File;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.fs.s3native.S3xLoginHelper;
import org.apache.hadoop.security.ProviderUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.alias.CredentialProvider;
import org.apache.hadoop.security.alias.CredentialProviderFactory;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.VersionInfo;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * S3A tests for configuration, especially credentials.
 */
public class ITestS3AConfiguration {
    private static final String EXAMPLE_ID = "AKASOMEACCESSKEY";

    private static final String EXAMPLE_KEY = "RGV0cm9pdCBSZ/WQgY2xl/YW5lZCB1cAEXAMPLE";

    private Configuration conf;

    private S3AFileSystem fs;

    private static final Logger LOG = LoggerFactory.getLogger(ITestS3AConfiguration.class);

    @Rule
    public Timeout testTimeout = new Timeout(S3ATestConstants.S3A_TEST_TIMEOUT);

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    /**
     * Test if custom endpoint is picked up.
     * <p>
     * The test expects {@link S3ATestConstants#CONFIGURATION_TEST_ENDPOINT}
     * to be defined in the Configuration
     * describing the endpoint of the bucket to which TEST_FS_S3A_NAME points
     * (i.e. "s3-eu-west-1.amazonaws.com" if the bucket is located in Ireland).
     * Evidently, the bucket has to be hosted in the region denoted by the
     * endpoint for the test to succeed.
     * <p>
     * More info and the list of endpoint identifiers:
     *
     * @see <a href="http://docs.aws.amazon.com/general/latest/gr/rande.html#s3_region">endpoint list</a>.
     * @throws Exception
     * 		
     */
    @Test
    public void testEndpoint() throws Exception {
        conf = new Configuration();
        String endpoint = conf.getTrimmed(S3ATestConstants.CONFIGURATION_TEST_ENDPOINT, "");
        if (endpoint.isEmpty()) {
            ITestS3AConfiguration.LOG.warn(((("Custom endpoint test skipped as " + (S3ATestConstants.CONFIGURATION_TEST_ENDPOINT)) + "config ") + "setting was not detected"));
        } else {
            conf.set(ENDPOINT, endpoint);
            fs = S3ATestUtils.createTestFileSystem(conf);
            AmazonS3 s3 = fs.getAmazonS3ClientForTesting("test endpoint");
            String endPointRegion = "";
            // Differentiate handling of "s3-" and "s3." based endpoint identifiers
            String[] endpointParts = StringUtils.split(endpoint, '.');
            if ((endpointParts.length) == 3) {
                endPointRegion = endpointParts[0].substring(3);
            } else
                if ((endpointParts.length) == 4) {
                    endPointRegion = endpointParts[1];
                } else {
                    Assert.fail("Unexpected endpoint");
                }

            Assert.assertEquals("Endpoint config setting and bucket location differ: ", endPointRegion, s3.getBucketLocation(fs.getUri().getHost()));
        }
    }

    @Test
    public void testProxyConnection() throws Exception {
        conf = new Configuration();
        conf.setInt(MAX_ERROR_RETRIES, 2);
        conf.set(PROXY_HOST, "127.0.0.1");
        conf.setInt(PROXY_PORT, 1);
        String proxy = ((conf.get(PROXY_HOST)) + ":") + (conf.get(PROXY_PORT));
        expectFSCreateFailure(AWSClientIOException.class, conf, ("when using proxy " + proxy));
    }

    @Test
    public void testProxyPortWithoutHost() throws Exception {
        conf = new Configuration();
        conf.unset(PROXY_HOST);
        conf.setInt(MAX_ERROR_RETRIES, 2);
        conf.setInt(PROXY_PORT, 1);
        IllegalArgumentException e = expectFSCreateFailure(IllegalArgumentException.class, conf, "Expected a connection error for proxy server");
        String msg = e.toString();
        if ((!(msg.contains(PROXY_HOST))) && (!(msg.contains(PROXY_PORT)))) {
            throw e;
        }
    }

    @Test
    public void testAutomaticProxyPortSelection() throws Exception {
        conf = new Configuration();
        conf.unset(PROXY_PORT);
        conf.setInt(MAX_ERROR_RETRIES, 2);
        conf.set(PROXY_HOST, "127.0.0.1");
        conf.set(SECURE_CONNECTIONS, "true");
        expectFSCreateFailure(AWSClientIOException.class, conf, "Expected a connection error for proxy server");
        conf.set(SECURE_CONNECTIONS, "false");
        expectFSCreateFailure(AWSClientIOException.class, conf, "Expected a connection error for proxy server");
    }

    @Test
    public void testUsernameInconsistentWithPassword() throws Exception {
        conf = new Configuration();
        conf.setInt(MAX_ERROR_RETRIES, 2);
        conf.set(PROXY_HOST, "127.0.0.1");
        conf.setInt(PROXY_PORT, 1);
        conf.set(PROXY_USERNAME, "user");
        IllegalArgumentException e = expectFSCreateFailure(IllegalArgumentException.class, conf, "Expected a connection error for proxy server");
        assertIsProxyUsernameError(e);
    }

    @Test
    public void testUsernameInconsistentWithPassword2() throws Exception {
        conf = new Configuration();
        conf.setInt(MAX_ERROR_RETRIES, 2);
        conf.set(PROXY_HOST, "127.0.0.1");
        conf.setInt(PROXY_PORT, 1);
        conf.set(PROXY_PASSWORD, "password");
        IllegalArgumentException e = expectFSCreateFailure(IllegalArgumentException.class, conf, "Expected a connection error for proxy server");
        assertIsProxyUsernameError(e);
    }

    @Test
    public void testCredsFromCredentialProvider() throws Exception {
        // set up conf to have a cred provider
        final Configuration conf = new Configuration();
        final File file = tempDir.newFile("test.jks");
        final URI jks = ProviderUtils.nestURIForLocalJavaKeyStoreProvider(file.toURI());
        conf.set(CREDENTIAL_PROVIDER_PATH, jks.toString());
        provisionAccessKeys(conf);
        conf.set(ACCESS_KEY, ((ITestS3AConfiguration.EXAMPLE_ID) + "LJM"));
        S3xLoginHelper.Login creds = S3AUtils.S3AUtils.getAWSAccessKeys(new URI("s3a://foobar"), conf);
        Assert.assertEquals("AccessKey incorrect.", ITestS3AConfiguration.EXAMPLE_ID, creds.getUser());
        Assert.assertEquals("SecretKey incorrect.", ITestS3AConfiguration.EXAMPLE_KEY, creds.getPassword());
    }

    @Test
    public void testSecretFromCredentialProviderIDFromConfig() throws Exception {
        // set up conf to have a cred provider
        final Configuration conf = new Configuration();
        final File file = tempDir.newFile("test.jks");
        final URI jks = ProviderUtils.nestURIForLocalJavaKeyStoreProvider(file.toURI());
        conf.set(CREDENTIAL_PROVIDER_PATH, jks.toString());
        // add our creds to the provider
        final CredentialProvider provider = CredentialProviderFactory.getProviders(conf).get(0);
        provider.createCredentialEntry(SECRET_KEY, ITestS3AConfiguration.EXAMPLE_KEY.toCharArray());
        provider.flush();
        conf.set(ACCESS_KEY, ITestS3AConfiguration.EXAMPLE_ID);
        S3xLoginHelper.Login creds = S3AUtils.S3AUtils.getAWSAccessKeys(new URI("s3a://foobar"), conf);
        Assert.assertEquals("AccessKey incorrect.", ITestS3AConfiguration.EXAMPLE_ID, creds.getUser());
        Assert.assertEquals("SecretKey incorrect.", ITestS3AConfiguration.EXAMPLE_KEY, creds.getPassword());
    }

    @Test
    public void testIDFromCredentialProviderSecretFromConfig() throws Exception {
        // set up conf to have a cred provider
        final Configuration conf = new Configuration();
        final File file = tempDir.newFile("test.jks");
        final URI jks = ProviderUtils.nestURIForLocalJavaKeyStoreProvider(file.toURI());
        conf.set(CREDENTIAL_PROVIDER_PATH, jks.toString());
        // add our creds to the provider
        final CredentialProvider provider = CredentialProviderFactory.getProviders(conf).get(0);
        provider.createCredentialEntry(ACCESS_KEY, ITestS3AConfiguration.EXAMPLE_ID.toCharArray());
        provider.flush();
        conf.set(SECRET_KEY, ITestS3AConfiguration.EXAMPLE_KEY);
        S3xLoginHelper.Login creds = S3AUtils.S3AUtils.getAWSAccessKeys(new URI("s3a://foobar"), conf);
        Assert.assertEquals("AccessKey incorrect.", ITestS3AConfiguration.EXAMPLE_ID, creds.getUser());
        Assert.assertEquals("SecretKey incorrect.", ITestS3AConfiguration.EXAMPLE_KEY, creds.getPassword());
    }

    @Test
    public void testExcludingS3ACredentialProvider() throws Exception {
        // set up conf to have a cred provider
        final Configuration conf = new Configuration();
        final File file = tempDir.newFile("test.jks");
        final URI jks = ProviderUtils.nestURIForLocalJavaKeyStoreProvider(file.toURI());
        conf.set(CREDENTIAL_PROVIDER_PATH, ("jceks://s3a/foobar," + (jks.toString())));
        // first make sure that the s3a based provider is removed
        Configuration c = ProviderUtils.excludeIncompatibleCredentialProviders(conf, S3AFileSystem.class);
        String newPath = conf.get(CREDENTIAL_PROVIDER_PATH);
        Assert.assertFalse("Provider Path incorrect", newPath.contains("s3a://"));
        // now let's make sure the new path is created by the S3AFileSystem
        // and the integration still works. Let's provision the keys through
        // the altered configuration instance and then try and access them
        // using the original config with the s3a provider in the path.
        provisionAccessKeys(c);
        conf.set(ACCESS_KEY, ((ITestS3AConfiguration.EXAMPLE_ID) + "LJM"));
        URI uri2 = new URI("s3a://foobar");
        S3xLoginHelper.Login creds = S3AUtils.S3AUtils.getAWSAccessKeys(uri2, conf);
        Assert.assertEquals("AccessKey incorrect.", ITestS3AConfiguration.EXAMPLE_ID, creds.getUser());
        Assert.assertEquals("SecretKey incorrect.", ITestS3AConfiguration.EXAMPLE_KEY, creds.getPassword());
    }

    @Test
    public void shouldBeAbleToSwitchOnS3PathStyleAccessViaConfigProperty() throws Exception {
        conf = new Configuration();
        conf.set(PATH_STYLE_ACCESS, Boolean.toString(true));
        Assert.assertTrue(conf.getBoolean(PATH_STYLE_ACCESS, false));
        try {
            fs = S3ATestUtils.createTestFileSystem(conf);
            Assert.assertNotNull(fs);
            AmazonS3 s3 = fs.getAmazonS3ClientForTesting("configuration");
            Assert.assertNotNull(s3);
            S3ClientOptions clientOptions = ITestS3AConfiguration.getField(s3, S3ClientOptions.class, "clientOptions");
            Assert.assertTrue("Expected to find path style access to be switched on!", clientOptions.isPathStyleAccess());
            byte[] file = ContractTestUtils.toAsciiByteArray("test file");
            ContractTestUtils.writeAndRead(fs, new Path("/path/style/access/testFile"), file, file.length, ((int) (conf.getLongBytes(FS_S3A_BLOCK_SIZE, file.length))), false, true);
        } catch (final AWSS3IOException e) {
            ITestS3AConfiguration.LOG.error("Caught exception: ", e);
            // Catch/pass standard path style access behaviour when live bucket
            // isn't in the same region as the s3 client default. See
            // http://docs.aws.amazon.com/AmazonS3/latest/dev/VirtualHosting.html
            Assert.assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, e.getStatusCode());
        }
    }

    @Test
    public void testDefaultUserAgent() throws Exception {
        conf = new Configuration();
        fs = S3ATestUtils.createTestFileSystem(conf);
        Assert.assertNotNull(fs);
        AmazonS3 s3 = fs.getAmazonS3ClientForTesting("User Agent");
        Assert.assertNotNull(s3);
        ClientConfiguration awsConf = ITestS3AConfiguration.getField(s3, ClientConfiguration.class, "clientConfiguration");
        Assert.assertEquals(("Hadoop " + (VersionInfo.getVersion())), awsConf.getUserAgentPrefix());
    }

    @Test
    public void testCustomUserAgent() throws Exception {
        conf = new Configuration();
        conf.set(USER_AGENT_PREFIX, "MyApp");
        fs = S3ATestUtils.createTestFileSystem(conf);
        Assert.assertNotNull(fs);
        AmazonS3 s3 = fs.getAmazonS3ClientForTesting("User agent");
        Assert.assertNotNull(s3);
        ClientConfiguration awsConf = ITestS3AConfiguration.getField(s3, ClientConfiguration.class, "clientConfiguration");
        Assert.assertEquals(("MyApp, Hadoop " + (VersionInfo.getVersion())), awsConf.getUserAgentPrefix());
    }

    @Test
    public void testCloseIdempotent() throws Throwable {
        conf = new Configuration();
        fs = S3ATestUtils.createTestFileSystem(conf);
        AWSCredentialProviderList credentials = fs.shareCredentials("testCloseIdempotent");
        credentials.close();
        fs.close();
        Assert.assertTrue(("Closing FS didn't close credentials " + credentials), credentials.isClosed());
        Assert.assertEquals(("refcount not zero in " + credentials), 0, credentials.getRefCount());
        fs.close();
        // and the numbers should not change
        Assert.assertEquals(("refcount not zero in " + credentials), 0, credentials.getRefCount());
    }

    @Test
    public void testDirectoryAllocatorDefval() throws Throwable {
        conf = new Configuration();
        conf.unset(BUFFER_DIR);
        fs = S3ATestUtils.createTestFileSystem(conf);
        File tmp = fs.createTmpFileForWrite("out-", 1024, conf);
        Assert.assertTrue(("not found: " + tmp), tmp.exists());
        tmp.delete();
    }

    @Test
    public void testDirectoryAllocatorRR() throws Throwable {
        File dir1 = GenericTestUtils.getRandomizedTestDir();
        File dir2 = GenericTestUtils.getRandomizedTestDir();
        dir1.mkdirs();
        dir2.mkdirs();
        conf = new Configuration();
        conf.set(BUFFER_DIR, ((dir1 + ", ") + dir2));
        fs = S3ATestUtils.createTestFileSystem(conf);
        File tmp1 = fs.createTmpFileForWrite("out-", 1024, conf);
        tmp1.delete();
        File tmp2 = fs.createTmpFileForWrite("out-", 1024, conf);
        tmp2.delete();
        Assert.assertNotEquals("round robin not working", tmp1.getParent(), tmp2.getParent());
    }

    @Test
    public void testReadAheadRange() throws Exception {
        conf = new Configuration();
        conf.set(READAHEAD_RANGE, "300K");
        fs = S3ATestUtils.createTestFileSystem(conf);
        Assert.assertNotNull(fs);
        long readAheadRange = fs.getReadAheadRange();
        Assert.assertNotNull(readAheadRange);
        Assert.assertEquals("Read Ahead Range Incorrect.", (300 * 1024), readAheadRange);
    }

    @Test
    public void testUsernameFromUGI() throws Throwable {
        final String alice = "alice";
        UserGroupInformation fakeUser = UserGroupInformation.createUserForTesting(alice, new String[]{ "users", "administrators" });
        conf = new Configuration();
        fs = fakeUser.doAs(new PrivilegedExceptionAction<S3AFileSystem>() {
            @Override
            public S3AFileSystem run() throws Exception {
                return S3ATestUtils.createTestFileSystem(conf);
            }
        });
        Assert.assertEquals("username", alice, fs.getUsername());
        FileStatus status = fs.getFileStatus(new Path("/"));
        Assert.assertEquals(("owner in " + status), alice, status.getOwner());
        Assert.assertEquals(("group in " + status), alice, status.getGroup());
    }

    @Test
    public void testBucketConfigurationPropagation() throws Throwable {
        Configuration config = new Configuration(false);
        setBucketOption(config, "b", "base", "1024");
        String basekey = "fs.s3a.base";
        S3ATestUtils.assertOptionEquals(config, basekey, null);
        String bucketKey = "fs.s3a.bucket.b.base";
        S3ATestUtils.assertOptionEquals(config, bucketKey, "1024");
        Configuration updated = propagateBucketOptions(config, "b");
        S3ATestUtils.assertOptionEquals(updated, basekey, "1024");
        // original conf is not updated
        S3ATestUtils.assertOptionEquals(config, basekey, null);
        String[] sources = updated.getPropertySources(basekey);
        Assert.assertEquals(1, sources.length);
        String sourceInfo = sources[0];
        Assert.assertTrue(("Wrong source " + sourceInfo), sourceInfo.contains(bucketKey));
    }

    @Test
    public void testBucketConfigurationPropagationResolution() throws Throwable {
        Configuration config = new Configuration(false);
        String basekey = "fs.s3a.base";
        String baseref = "fs.s3a.baseref";
        String baseref2 = "fs.s3a.baseref2";
        config.set(basekey, "orig");
        config.set(baseref2, "${fs.s3a.base}");
        setBucketOption(config, "b", basekey, "1024");
        setBucketOption(config, "b", baseref, "${fs.s3a.base}");
        Configuration updated = propagateBucketOptions(config, "b");
        S3ATestUtils.assertOptionEquals(updated, basekey, "1024");
        S3ATestUtils.assertOptionEquals(updated, baseref, "1024");
        S3ATestUtils.assertOptionEquals(updated, baseref2, "1024");
    }

    @Test
    public void testMultipleBucketConfigurations() throws Throwable {
        Configuration config = new Configuration(false);
        setBucketOption(config, "b", USER_AGENT_PREFIX, "UA-b");
        setBucketOption(config, "c", USER_AGENT_PREFIX, "UA-c");
        config.set(USER_AGENT_PREFIX, "UA-orig");
        Configuration updated = propagateBucketOptions(config, "c");
        S3ATestUtils.assertOptionEquals(updated, USER_AGENT_PREFIX, "UA-c");
    }

    @Test
    public void testClearBucketOption() throws Throwable {
        Configuration config = new Configuration();
        config.set(USER_AGENT_PREFIX, "base");
        setBucketOption(config, "bucket", USER_AGENT_PREFIX, "overridden");
        clearBucketOption(config, "bucket", USER_AGENT_PREFIX);
        Configuration updated = propagateBucketOptions(config, "c");
        S3ATestUtils.assertOptionEquals(updated, USER_AGENT_PREFIX, "base");
    }

    @Test
    public void testBucketConfigurationSkipsUnmodifiable() throws Throwable {
        Configuration config = new Configuration(false);
        String impl = "fs.s3a.impl";
        config.set(impl, "orig");
        setBucketOption(config, "b", impl, "b");
        String metastoreImpl = "fs.s3a.metadatastore.impl";
        String ddb = "org.apache.hadoop.fs.s3a.s3guard.DynamoDBMetadataStore";
        setBucketOption(config, "b", metastoreImpl, ddb);
        setBucketOption(config, "b", "impl2", "b2");
        setBucketOption(config, "b", "bucket.b.loop", "b3");
        S3ATestUtils.assertOptionEquals(config, "fs.s3a.bucket.b.impl", "b");
        Configuration updated = propagateBucketOptions(config, "b");
        S3ATestUtils.assertOptionEquals(updated, impl, "orig");
        S3ATestUtils.assertOptionEquals(updated, "fs.s3a.impl2", "b2");
        S3ATestUtils.assertOptionEquals(updated, metastoreImpl, ddb);
        S3ATestUtils.assertOptionEquals(updated, "fs.s3a.bucket.b.loop", null);
    }

    @Test
    public void testConfOptionPropagationToFS() throws Exception {
        Configuration config = new Configuration();
        String testFSName = config.getTrimmed(S3ATestConstants.TEST_FS_S3A_NAME, "");
        String bucket = new URI(testFSName).getHost();
        setBucketOption(config, bucket, "propagation", "propagated");
        fs = S3ATestUtils.createTestFileSystem(config);
        Configuration updated = fs.getConf();
        S3ATestUtils.assertOptionEquals(updated, "fs.s3a.propagation", "propagated");
    }

    @Test
    public void testSecurityCredentialPropagationNoOverride() throws Exception {
        Configuration config = new Configuration();
        config.set(CREDENTIAL_PROVIDER_PATH, "base");
        patchSecurityCredentialProviders(config);
        S3ATestUtils.assertOptionEquals(config, CREDENTIAL_PROVIDER_PATH, "base");
    }

    @Test
    public void testSecurityCredentialPropagationOverrideNoBase() throws Exception {
        Configuration config = new Configuration();
        config.unset(CREDENTIAL_PROVIDER_PATH);
        config.set(S3A_SECURITY_CREDENTIAL_PROVIDER_PATH, "override");
        patchSecurityCredentialProviders(config);
        S3ATestUtils.assertOptionEquals(config, CREDENTIAL_PROVIDER_PATH, "override");
    }

    @Test
    public void testSecurityCredentialPropagationOverride() throws Exception {
        Configuration config = new Configuration();
        config.set(CREDENTIAL_PROVIDER_PATH, "base");
        config.set(S3A_SECURITY_CREDENTIAL_PROVIDER_PATH, "override");
        patchSecurityCredentialProviders(config);
        S3ATestUtils.assertOptionEquals(config, CREDENTIAL_PROVIDER_PATH, "override,base");
        Collection<String> all = config.getStringCollection(CREDENTIAL_PROVIDER_PATH);
        Assert.assertTrue(all.contains("override"));
        Assert.assertTrue(all.contains("base"));
    }

    @Test
    public void testSecurityCredentialPropagationEndToEnd() throws Exception {
        Configuration config = new Configuration();
        config.set(CREDENTIAL_PROVIDER_PATH, "base");
        setBucketOption(config, "b", S3A_SECURITY_CREDENTIAL_PROVIDER_PATH, "override");
        Configuration updated = propagateBucketOptions(config, "b");
        patchSecurityCredentialProviders(updated);
        S3ATestUtils.assertOptionEquals(updated, CREDENTIAL_PROVIDER_PATH, "override,base");
    }
}

