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
package org.apache.hadoop.fs.azurebfs.extensions;


import java.io.File;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.azurebfs.AbstractAbfsIntegrationTest;
import org.apache.hadoop.fs.azurebfs.AzureBlobFileSystem;
import org.apache.hadoop.mapreduce.security.TokenCache;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test custom DT support in ABFS.
 * This brings up a mini KDC in class setup/teardown, as the FS checks
 * for that when it enables security.
 *
 * Much of this code is copied from
 * {@code org.apache.hadoop.fs.s3a.auth.delegation.AbstractDelegationIT}
 */
public class ITestAbfsDelegationTokens extends AbstractAbfsIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(ITestAbfsDelegationTokens.class);

    /**
     * Created in static {@link #setupCluster()} call.
     */
    @SuppressWarnings("StaticNonFinalField")
    private static KerberizedAbfsCluster cluster;

    private UserGroupInformation aliceUser;

    public ITestAbfsDelegationTokens() throws Exception {
    }

    @Test
    public void testTokenManagerBinding() throws Throwable {
        StubDelegationTokenManager instance = getStubDTManager();
        Assert.assertNotNull("No StubDelegationTokenManager created in filesystem init", instance);
        Assert.assertTrue(("token manager not initialized: " + instance), instance.isInitialized());
    }

    /**
     * When bound to a custom DT manager, it provides the service name.
     * The stub returns the URI by default.
     */
    @Test
    public void testCanonicalization() throws Throwable {
        String service = getCanonicalServiceName();
        Assert.assertNotNull(("No canonical service name from filesystem " + (getFileSystem())), service);
        Assert.assertEquals("canonical URI and service name mismatch", getFilesystemURI(), new URI(service));
    }

    /**
     * Checks here to catch any regressions in canonicalization
     * logic.
     */
    @Test
    public void testDefaultCanonicalization() throws Throwable {
        FileSystem fs = getFileSystem();
        clearTokenServiceName();
        Assert.assertEquals("canonicalServiceName is not the default", getDefaultServiceName(fs), getCanonicalServiceName());
    }

    /**
     * Request a token; this tests the collection workflow.
     */
    @Test
    public void testRequestToken() throws Throwable {
        AzureBlobFileSystem fs = getFileSystem();
        Credentials credentials = ITestAbfsDelegationTokens.mkTokens(fs);
        Assert.assertEquals("Number of collected tokens", 1, credentials.numberOfTokens());
        verifyCredentialsContainsToken(credentials, fs);
    }

    /**
     * Request a token; this tests the collection workflow.
     */
    @Test
    public void testRequestTokenDefault() throws Throwable {
        clearTokenServiceName();
        AzureBlobFileSystem fs = getFileSystem();
        Assert.assertEquals("canonicalServiceName is not the default", getDefaultServiceName(fs), fs.getCanonicalServiceName());
        Credentials credentials = ITestAbfsDelegationTokens.mkTokens(fs);
        Assert.assertEquals("Number of collected tokens", 1, credentials.numberOfTokens());
        verifyCredentialsContainsToken(credentials, getDefaultServiceName(fs), getFilesystemURI().toString());
    }

    /**
     * This mimics the DT collection performed inside FileInputFormat to
     * collect DTs for a job.
     *
     * @throws Throwable
     * 		on failure.
     */
    @Test
    public void testJobsCollectTokens() throws Throwable {
        // get tokens for all the required FileSystems..
        AzureBlobFileSystem fs = getFileSystem();
        Credentials credentials = new Credentials();
        Path root = fs.makeQualified(new Path("/"));
        Path[] paths = new Path[]{ root };
        Configuration conf = fs.getConf();
        TokenCache.obtainTokensForNamenodes(credentials, paths, conf);
        verifyCredentialsContainsToken(credentials, fs);
    }

    /**
     * Verify the dtutil shell command can fetch tokens
     */
    @Test
    public void testDTUtilShell() throws Throwable {
        File tokenfile = ITestAbfsDelegationTokens.cluster.createTempTokenFile();
        String tfs = tokenfile.toString();
        String fsURI = getFileSystem().getUri().toString();
        dtutil(0, getRawConfiguration(), "get", fsURI, "-format", "protobuf", tfs);
        Assert.assertTrue(("not created: " + tokenfile), tokenfile.exists());
        Assert.assertTrue(("File is empty " + tokenfile), ((tokenfile.length()) > 0));
        Assert.assertTrue(("File only contains header " + tokenfile), ((tokenfile.length()) > 6));
        String printed = dtutil(0, getRawConfiguration(), "print", tfs);
        Assert.assertTrue(((("no " + fsURI) + " in ") + printed), printed.contains(fsURI));
        Assert.assertTrue(((("no " + (StubAbfsTokenIdentifier.ID)) + " in ") + printed), printed.contains(StubAbfsTokenIdentifier.ID));
    }

    /**
     * Creates a new FS instance with the simplest binding lifecycle;
     * get a token.
     * This verifies the classic binding mechanism works.
     */
    @Test
    public void testBaseDTLifecycle() throws Throwable {
        Configuration conf = new Configuration(getRawConfiguration());
        ClassicDelegationTokenManager.useClassicDTManager(conf);
        try (FileSystem fs = FileSystem.newInstance(getFilesystemURI(), conf)) {
            Credentials credentials = ITestAbfsDelegationTokens.mkTokens(fs);
            Assert.assertEquals("Number of collected tokens", 1, credentials.numberOfTokens());
            verifyCredentialsContainsToken(credentials, fs.getCanonicalServiceName(), ClassicDelegationTokenManager.UNSET);
        }
    }
}

