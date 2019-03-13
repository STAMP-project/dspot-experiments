/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.s3a.auth.delegation;


import java.io.File;
import java.net.URI;
import java.util.Objects;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.fs.s3a.AWSCredentialProviderList;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.S3ATestUtils;
import org.apache.hadoop.fs.s3a.Statistic;
import org.apache.hadoop.hdfs.tools.DelegationTokenFetcher;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.security.TokenCache;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.service.ServiceStateException;
import org.apache.hadoop.util.ExitUtil;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests use of Hadoop delegation tokens within the FS itself.
 * This instantiates a MiniKDC as some of the operations tested require
 * UGI to be initialized with security enabled.
 */
@SuppressWarnings("StaticNonFinalField")
public class ITestSessionDelegationInFileystem extends AbstractDelegationIT {
    private static final Logger LOG = LoggerFactory.getLogger(ITestSessionDelegationInFileystem.class);

    private static MiniKerberizedHadoopCluster cluster;

    private UserGroupInformation bobUser;

    private UserGroupInformation aliceUser;

    private S3ADelegationTokens delegationTokens;

    @Test
    public void testGetDTfromFileSystem() throws Throwable {
        describe("Enable delegation tokens and request one");
        delegationTokens.start();
        S3AFileSystem fs = getFileSystem();
        assertNotNull(("No tokens from " + fs), fs.getCanonicalServiceName());
        S3ATestUtils.MetricDiff invocationDiff = new S3ATestUtils.MetricDiff(fs, Statistic.INVOCATION_GET_DELEGATION_TOKEN);
        S3ATestUtils.MetricDiff issueDiff = new S3ATestUtils.MetricDiff(fs, Statistic.DELEGATION_TOKENS_ISSUED);
        Token<AbstractS3ATokenIdentifier> token = Objects.requireNonNull(fs.getDelegationToken(""), ("no token from filesystem " + fs));
        assertEquals("token kind", getTokenKind(), token.getKind());
        AbstractDelegationIT.assertTokenCreationCount(fs, 1);
        final String fsInfo = fs.toString();
        invocationDiff.assertDiffEquals(("getDelegationToken() in " + fsInfo), 1);
        issueDiff.assertDiffEquals(("DTs issued in " + (delegationTokens)), 1);
        Text service = delegationTokens.getService();
        assertEquals("service name", service, token.getService());
        Credentials creds = new Credentials();
        creds.addToken(service, token);
        assertEquals(("retrieve token from " + creds), token, creds.getToken(service));
    }

    @Test
    public void testAddTokensFromFileSystem() throws Throwable {
        describe("verify FileSystem.addDelegationTokens() collects tokens");
        S3AFileSystem fs = getFileSystem();
        Credentials cred = new Credentials();
        Token<?>[] tokens = fs.addDelegationTokens(AbstractDelegationIT.YARN_RM, cred);
        assertEquals("Number of tokens", 1, tokens.length);
        Token<?> token = Objects.requireNonNull(tokens[0], "token");
        ITestSessionDelegationInFileystem.LOG.info("FS token is {}", token);
        Text service = delegationTokens.getService();
        Token<? extends TokenIdentifier> retrieved = Objects.requireNonNull(cred.getToken(service), ((("retrieved token with key " + service) + "; expected ") + token));
        delegationTokens.start();
        // this only sneaks in because there isn't a state check here
        delegationTokens.resetTokenBindingToDT(((Token<AbstractS3ATokenIdentifier>) (retrieved)));
        assertTrue("bind to existing DT failed", delegationTokens.isBoundToDT());
        AWSCredentialProviderList providerList = Objects.requireNonNull(delegationTokens.getCredentialProviders(), "providers");
        providerList.getCredentials();
    }

    @Test
    public void testCanRetrieveTokenFromCurrentUserCreds() throws Throwable {
        describe(("Create a DT, add it to the current UGI credentials," + " then retrieve"));
        delegationTokens.start();
        Credentials cred = createDelegationTokens();
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        ugi.addCredentials(cred);
        Token<?>[] tokens = cred.getAllTokens().toArray(new Token<?>[0]);
        Token<?> token0 = tokens[0];
        Text service = token0.getService();
        ITestSessionDelegationInFileystem.LOG.info(("Token = " + token0));
        Token<?> token1 = Objects.requireNonNull(ugi.getCredentials().getToken(service), ("Token from " + service));
        assertEquals("retrieved token", token0, token1);
        assertNotNull(("token identifier of " + token1), token1.getIdentifier());
    }

    @Test
    public void testDTCredentialProviderFromCurrentUserCreds() throws Throwable {
        describe(("Add credentials to the current user, " + "then verify that they can be found when S3ADelegationTokens binds"));
        Credentials cred = createDelegationTokens();
        assertThat("Token size", cred.getAllTokens(), hasSize(1));
        UserGroupInformation.getCurrentUser().addCredentials(cred);
        delegationTokens.start();
        assertTrue("bind to existing DT failed", delegationTokens.isBoundToDT());
    }

    /**
     * Create a FS with a delegated token, verify it works as a filesystem,
     * and that you can pick up the same DT from that FS too.
     */
    @Test
    public void testDelegatedFileSystem() throws Throwable {
        describe(("Delegation tokens can be passed to a new filesystem;" + " if role restricted, permissions are tightened."));
        S3AFileSystem fs = getFileSystem();
        readLandsatMetadata(fs);
        URI uri = fs.getUri();
        // create delegation tokens from the test suites FS.
        Credentials creds = createDelegationTokens();
        final Text tokenKind = getTokenKind();
        AbstractS3ATokenIdentifier origTokenId = Objects.requireNonNull(AbstractDelegationIT.lookupToken(creds, uri, tokenKind), "original");
        // attach to the user, so that when tokens are looked for, they get picked
        // up
        final UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
        currentUser.addCredentials(creds);
        // verify that the tokens went over
        Objects.requireNonNull(AbstractDelegationIT.lookupToken(currentUser.getCredentials(), uri, tokenKind), "user credentials");
        Configuration conf = new Configuration(getConfiguration());
        String bucket = fs.getBucket();
        S3ATestUtils.disableFilesystemCaching(conf);
        S3ATestUtils.unsetHadoopCredentialProviders(conf);
        // remove any secrets we don't want the delegated FS to accidentally
        // pick up.
        // this is to simulate better a remote deployment.
        S3ATestUtils.removeBaseAndBucketOverrides(bucket, conf, ACCESS_KEY, SECRET_KEY, SESSION_TOKEN, SERVER_SIDE_ENCRYPTION_ALGORITHM, DELEGATION_TOKEN_ROLE_ARN, DELEGATION_TOKEN_ENDPOINT);
        // this is done to make sure you cannot create an STS session no
        // matter how you pick up credentials.
        conf.set(DELEGATION_TOKEN_ENDPOINT, "http://localhost:8080/");
        bindProviderList(bucket, conf, CountInvocationsProvider.NAME);
        long originalCount = CountInvocationsProvider.getInvocationCount();
        // create a new FS instance, which is expected to pick up the
        // existing token
        Path testPath = path("testDTFileSystemClient");
        try (S3AFileSystem delegatedFS = AbstractDelegationIT.newS3AInstance(uri, conf)) {
            ITestSessionDelegationInFileystem.LOG.info("Delegated filesystem is: {}", delegatedFS);
            AbstractDelegationIT.assertBoundToDT(delegatedFS, tokenKind);
            if (encryptionTestEnabled()) {
                assertEquals("Encryption propagation failed", S3AEncryptionMethods.SSE_S3, delegatedFS.getServerSideEncryptionAlgorithm());
            }
            verifyRestrictedPermissions(delegatedFS);
            executeDelegatedFSOperations(delegatedFS, testPath);
            delegatedFS.mkdirs(testPath);
            S3ATestUtils.MetricDiff issueDiff = new S3ATestUtils.MetricDiff(delegatedFS, Statistic.DELEGATION_TOKENS_ISSUED);
            // verify that the FS returns the existing token when asked
            // so that chained deployments will work
            AbstractS3ATokenIdentifier tokenFromDelegatedFS = Objects.requireNonNull(delegatedFS.getDelegationToken(""), "New token").decodeIdentifier();
            assertEquals("Newly issued token != old one", origTokenId, tokenFromDelegatedFS);
            issueDiff.assertDiffEquals(("DTs issued in " + delegatedFS), 0);
        }
        // the DT auth chain should override the original one.
        assertEquals("invocation count", originalCount, CountInvocationsProvider.getInvocationCount());
        // create a second instance, which will pick up the same value
        try (S3AFileSystem secondDelegate = AbstractDelegationIT.newS3AInstance(uri, conf)) {
            AbstractDelegationIT.assertBoundToDT(secondDelegate, tokenKind);
            if (encryptionTestEnabled()) {
                assertEquals("Encryption propagation failed", S3AEncryptionMethods.SSE_S3, secondDelegate.getServerSideEncryptionAlgorithm());
            }
            ContractTestUtils.assertDeleted(secondDelegate, testPath, true);
            assertNotNull("unbounded DT", secondDelegate.getDelegationToken(""));
        }
    }

    @Test
    public void testDelegationBindingMismatch1() throws Throwable {
        describe(("Verify that when the DT client and remote bindings are different," + " the failure is meaningful"));
        S3AFileSystem fs = getFileSystem();
        URI uri = fs.getUri();
        UserGroupInformation.getCurrentUser().addCredentials(createDelegationTokens());
        // create the remote FS with a full credential binding
        Configuration conf = new Configuration(getConfiguration());
        String bucket = fs.getBucket();
        S3ATestUtils.removeBaseAndBucketOverrides(bucket, conf, ACCESS_KEY, SECRET_KEY, SESSION_TOKEN);
        conf.set(ACCESS_KEY, "aaaaa");
        conf.set(SECRET_KEY, "bbbb");
        bindProviderList(bucket, conf, CountInvocationsProvider.NAME);
        conf.set(DELEGATION_TOKEN_BINDING, DELEGATION_TOKEN_FULL_CREDENTIALS_BINDING);
        ServiceStateException e = intercept(ServiceStateException.class, DelegationTokenIOException.TOKEN_MISMATCH, () -> {
            S3AFileSystem remote = newS3AInstance(uri, conf);
            // if we get this far, provide info for the exception which will
            // be raised.
            String s = remote.toString();
            remote.close();
            return s;
        });
        if (!((e.getCause()) instanceof DelegationTokenIOException)) {
            throw e;
        }
    }

    @Test
    public void testDelegationBindingMismatch2() throws Throwable {
        describe(("assert mismatch reported when client DT is a " + "subclass of the remote one"));
        S3AFileSystem fs = getFileSystem();
        URI uri = fs.getUri();
        // create the remote FS with a full credential binding
        Configuration conf = new Configuration(getConfiguration());
        String bucket = fs.getBucket();
        enableDelegationTokens(conf, DELEGATION_TOKEN_FULL_CREDENTIALS_BINDING);
        // create a new FS with Full tokens
        Credentials fullTokens;
        Token<AbstractS3ATokenIdentifier> firstDT;
        try (S3AFileSystem fullFS = AbstractDelegationIT.newS3AInstance(uri, conf)) {
            // add the tokens to the user group
            fullTokens = AbstractDelegationIT.mkTokens(fullFS);
            AbstractDelegationIT.assertTokenCreationCount(fullFS, 1);
            firstDT = fullFS.getDelegationToken("first");
            AbstractDelegationIT.assertTokenCreationCount(fullFS, 2);
            Token<AbstractS3ATokenIdentifier> secondDT = fullFS.getDelegationToken("second");
            AbstractDelegationIT.assertTokenCreationCount(fullFS, 3);
            assertNotEquals("DT identifiers", firstDT.getIdentifier(), secondDT.getIdentifier());
        }
        // expect a token
        AbstractS3ATokenIdentifier origTokenId = Objects.requireNonNull(AbstractDelegationIT.lookupToken(fullTokens, uri, FULL_TOKEN_KIND), "token from credentials");
        UserGroupInformation.getCurrentUser().addCredentials(fullTokens);
        // a remote FS with those tokens
        try (S3AFileSystem delegatedFS = AbstractDelegationIT.newS3AInstance(uri, conf)) {
            AbstractDelegationIT.assertBoundToDT(delegatedFS, FULL_TOKEN_KIND);
            delegatedFS.getFileStatus(new Path("/"));
            SessionTokenIdentifier tokenFromDelegatedFS = ((SessionTokenIdentifier) (Objects.requireNonNull(delegatedFS.getDelegationToken(""), "New token").decodeIdentifier()));
            AbstractDelegationIT.assertTokenCreationCount(delegatedFS, 0);
            assertEquals("Newly issued token != old one", origTokenId, tokenFromDelegatedFS);
        }
        // now create a configuration which expects a session token.
        Configuration conf2 = new Configuration(getConfiguration());
        S3ATestUtils.removeBaseAndBucketOverrides(bucket, conf2, ACCESS_KEY, SECRET_KEY, SESSION_TOKEN);
        conf.set(DELEGATION_TOKEN_BINDING, getDelegationBinding());
        ServiceStateException e = intercept(ServiceStateException.class, DelegationTokenIOException.TOKEN_MISMATCH, () -> {
            S3AFileSystem remote = newS3AInstance(uri, conf);
            // if we get this far, provide info for the exception which will
            // be raised.
            String s = remote.toString();
            remote.close();
            return s;
        });
        if (!((e.getCause()) instanceof DelegationTokenIOException)) {
            throw e;
        }
    }

    /**
     * YARN job submission uses
     * {@link TokenCache#obtainTokensForNamenodes(Credentials, Path[], Configuration)}
     * for token retrieval: call it here to verify it works.
     */
    @Test
    public void testYarnCredentialPickup() throws Throwable {
        describe(("Verify tokens are picked up by the YARN" + " TokenCache.obtainTokensForNamenodes() API Call"));
        Credentials cred = new Credentials();
        Path yarnPath = path("testYarnCredentialPickup");
        Path[] paths = new Path[]{ yarnPath };
        Configuration conf = getConfiguration();
        S3AFileSystem fs = getFileSystem();
        TokenCache.obtainTokensForNamenodes(cred, paths, conf);
        assertNotNull("No Token in credentials file", AbstractDelegationIT.lookupToken(cred, fs.getUri(), getTokenKind()));
    }

    /**
     * Test the {@code hdfs fetchdt} command works with S3A tokens.
     */
    @Test
    public void testHDFSFetchDTCommand() throws Throwable {
        describe("Use the HDFS fetchdt CLI to fetch a token");
        ExitUtil.disableSystemExit();
        S3AFileSystem fs = getFileSystem();
        Configuration conf = fs.getConf();
        URI fsUri = fs.getUri();
        String fsurl = fsUri.toString();
        File tokenfile = createTempTokenFile();
        // this will create (& leak) a new FS instance as caching is disabled.
        // but as teardown destroys all filesystems for this user, it
        // gets cleaned up at the end of the test
        String tokenFilePath = tokenfile.getAbsolutePath();
        // create the tokens as Bob.
        doAs(bobUser, () -> DelegationTokenFetcher.main(conf, args("--webservice", fsurl, tokenFilePath)));
        assertTrue(("token file was not created: " + tokenfile), tokenfile.exists());
        // print to stdout
        String s = DelegationTokenFetcher.printTokensToString(conf, new Path(tokenfile.toURI()), false);
        ITestSessionDelegationInFileystem.LOG.info("Tokens: {}", s);
        DelegationTokenFetcher.main(conf, args("--print", tokenFilePath));
        DelegationTokenFetcher.main(conf, args("--print", "--verbose", tokenFilePath));
        // read in and retrieve token
        Credentials creds = Credentials.readTokenStorageFile(tokenfile, conf);
        AbstractS3ATokenIdentifier identifier = Objects.requireNonNull(AbstractDelegationIT.lookupToken(creds, fsUri, getTokenKind()), "Token lookup");
        assertEquals("encryption secrets", fs.getEncryptionSecrets(), identifier.getEncryptionSecrets());
        assertEquals("Username of decoded token", bobUser.getUserName(), identifier.getUser().getUserName());
        // renew
        DelegationTokenFetcher.main(conf, args("--renew", tokenFilePath));
        // cancel
        DelegationTokenFetcher.main(conf, args("--cancel", tokenFilePath));
    }

    /**
     * This test looks at the identity which goes with a DT.
     * It assumes that the username of a token == the user who created it.
     * Some tokens may change that in future (maybe use Role ARN?).
     */
    @Test
    public void testFileSystemBoundToCreator() throws Throwable {
        describe("Run tests to verify the DT Setup is bound to the creator");
        // quick sanity check to make sure alice and bob are different
        assertNotEquals("Alice and Bob logins", aliceUser.getUserName(), bobUser.getUserName());
        final S3AFileSystem fs = getFileSystem();
        assertEquals("FS username in doAs()", MiniKerberizedHadoopCluster.ALICE, doAs(bobUser, () -> fs.getUsername()));
        UserGroupInformation fsOwner = doAs(bobUser, () -> fs.getDelegationTokens().get().getOwner());
        assertEquals("username mismatch", aliceUser.getUserName(), fsOwner.getUserName());
        Token<AbstractS3ATokenIdentifier> dt = fs.getDelegationToken(MiniKerberizedHadoopCluster.ALICE);
        AbstractS3ATokenIdentifier identifier = dt.decodeIdentifier();
        UserGroupInformation user = identifier.getUser();
        assertEquals("User in DT", aliceUser.getUserName(), user.getUserName());
    }

    @Test
    public void testDTUtilShell() throws Throwable {
        describe("Verify the dtutil shell command can fetch tokens");
        File tokenfile = createTempTokenFile();
        String tfs = tokenfile.toString();
        String fsURI = getFileSystem().getCanonicalUri().toString();
        dtutil(0, "get", fsURI, "-format", "protobuf", tfs);
        assertTrue(("not created: " + tokenfile), tokenfile.exists());
        assertTrue(("File is empty" + tokenfile), ((tokenfile.length()) > 0));
        assertTrue(("File only contains header" + tokenfile), ((tokenfile.length()) > 6));
        String printed = dtutil(0, "print", tfs);
        assertThat(printed, Matchers.containsString(fsURI));
        assertThat(printed, Matchers.containsString(getTokenKind().toString()));
    }
}

