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
package org.apache.hadoop.hdfs.security;


import DFSConfigKeys.DFS_NAMENODE_DELEGATION_KEY_UPDATE_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_SAFEMODE_EXTENSION_KEY;
import NamenodeWebHdfsMethods.LOG;
import StartupOption.REGULAR;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenSecretManager;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.apache.hadoop.hdfs.web.WebHdfsConstants;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


public class TestDelegationToken {
    private MiniDFSCluster cluster;

    private DelegationTokenSecretManager dtSecretManager;

    private Configuration config;

    private static final Logger LOG = LoggerFactory.getLogger(TestDelegationToken.class);

    @Test
    public void testDelegationTokenSecretManager() throws Exception {
        Token<DelegationTokenIdentifier> token = generateDelegationToken("SomeUser", "JobTracker");
        // Fake renewer should not be able to renew
        try {
            dtSecretManager.renewToken(token, "FakeRenewer");
            Assert.fail("should have failed");
        } catch (AccessControlException ace) {
            // PASS
        }
        dtSecretManager.renewToken(token, "JobTracker");
        DelegationTokenIdentifier identifier = new DelegationTokenIdentifier();
        byte[] tokenId = token.getIdentifier();
        identifier.readFields(new DataInputStream(new ByteArrayInputStream(tokenId)));
        Assert.assertTrue((null != (dtSecretManager.retrievePassword(identifier))));
        TestDelegationToken.LOG.info("Sleep to expire the token");
        Thread.sleep(6000);
        // Token should be expired
        try {
            dtSecretManager.retrievePassword(identifier);
            // Should not come here
            Assert.fail("Token should have expired");
        } catch (InvalidToken e) {
            // Success
        }
        dtSecretManager.renewToken(token, "JobTracker");
        TestDelegationToken.LOG.info("Sleep beyond the max lifetime");
        Thread.sleep(5000);
        try {
            dtSecretManager.renewToken(token, "JobTracker");
            Assert.fail("should have been expired");
        } catch (InvalidToken it) {
            // PASS
        }
    }

    @Test
    public void testCancelDelegationToken() throws Exception {
        Token<DelegationTokenIdentifier> token = generateDelegationToken("SomeUser", "JobTracker");
        // Fake renewer should not be able to renew
        try {
            dtSecretManager.cancelToken(token, "FakeCanceller");
            Assert.fail("should have failed");
        } catch (AccessControlException ace) {
            // PASS
        }
        dtSecretManager.cancelToken(token, "JobTracker");
        try {
            dtSecretManager.renewToken(token, "JobTracker");
            Assert.fail("should have failed");
        } catch (InvalidToken it) {
            // PASS
        }
    }

    @Test
    public void testAddDelegationTokensDFSApi() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("JobTracker");
        DistributedFileSystem dfs = cluster.getFileSystem();
        Credentials creds = new Credentials();
        final Token<?>[] tokens = dfs.addDelegationTokens("JobTracker", creds);
        Assert.assertEquals(1, tokens.length);
        Assert.assertEquals(1, creds.numberOfTokens());
        checkTokenIdentifier(ugi, tokens[0]);
        final Token<?>[] tokens2 = dfs.addDelegationTokens("JobTracker", creds);
        Assert.assertEquals(0, tokens2.length);// already have token

        Assert.assertEquals(1, creds.numberOfTokens());
    }

    @Test
    public void testDelegationTokenWebHdfsApi() throws Exception {
        GenericTestUtils.setLogLevel(NamenodeWebHdfsMethods.LOG, Level.TRACE);
        final String uri = ((WebHdfsConstants.WEBHDFS_SCHEME) + "://") + (config.get(DFS_NAMENODE_HTTP_ADDRESS_KEY));
        // get file system as JobTracker
        final UserGroupInformation ugi = UserGroupInformation.createUserForTesting("JobTracker", new String[]{ "user" });
        final WebHdfsFileSystem webhdfs = ugi.doAs(new PrivilegedExceptionAction<WebHdfsFileSystem>() {
            @Override
            public WebHdfsFileSystem run() throws Exception {
                return ((WebHdfsFileSystem) (FileSystem.get(new URI(uri), config)));
            }
        });
        {
            // test addDelegationTokens(..)
            Credentials creds = new Credentials();
            final Token<?>[] tokens = webhdfs.addDelegationTokens("JobTracker", creds);
            Assert.assertEquals(1, tokens.length);
            Assert.assertEquals(1, creds.numberOfTokens());
            Assert.assertSame(tokens[0], creds.getAllTokens().iterator().next());
            checkTokenIdentifier(ugi, tokens[0]);
            final Token<?>[] tokens2 = webhdfs.addDelegationTokens("JobTracker", creds);
            Assert.assertEquals(0, tokens2.length);
        }
    }

    @Test
    public void testDelegationTokenWithDoAs() throws Exception {
        final DistributedFileSystem dfs = cluster.getFileSystem();
        final Credentials creds = new Credentials();
        final Token<?>[] tokens = dfs.addDelegationTokens("JobTracker", creds);
        Assert.assertEquals(1, tokens.length);
        @SuppressWarnings("unchecked")
        final Token<DelegationTokenIdentifier> token = ((Token<DelegationTokenIdentifier>) (tokens[0]));
        final UserGroupInformation longUgi = UserGroupInformation.createRemoteUser("JobTracker/foo.com@FOO.COM");
        final UserGroupInformation shortUgi = UserGroupInformation.createRemoteUser("JobTracker");
        longUgi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws IOException {
                try {
                    token.renew(config);
                } catch (Exception e) {
                    Assert.fail(("Could not renew delegation token for user " + longUgi));
                }
                return null;
            }
        });
        shortUgi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                token.renew(config);
                return null;
            }
        });
        longUgi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws IOException {
                try {
                    token.cancel(config);
                } catch (Exception e) {
                    Assert.fail(("Could not cancel delegation token for user " + longUgi));
                }
                return null;
            }
        });
    }

    @Test
    public void testDelegationTokenUgi() throws Exception {
        final DistributedFileSystem dfs = cluster.getFileSystem();
        Token<?>[] tokens = dfs.addDelegationTokens("renewer", null);
        Assert.assertEquals(1, tokens.length);
        Token<?> token1 = tokens[0];
        DelegationTokenIdentifier ident = ((DelegationTokenIdentifier) (token1.decodeIdentifier()));
        UserGroupInformation expectedUgi = ident.getUser();
        // get 2 new instances (clones) of the identifier, query their ugi
        // twice each, all ugi instances should be equivalent
        for (int i = 0; i < 2; i++) {
            DelegationTokenIdentifier identClone = ((DelegationTokenIdentifier) (token1.decodeIdentifier()));
            Assert.assertEquals(ident, identClone);
            Assert.assertNotSame(ident, identClone);
            Assert.assertSame(expectedUgi, identClone.getUser());
            Assert.assertSame(expectedUgi, identClone.getUser());
        }
        // a new token must decode to a different ugi instance than the first token
        tokens = dfs.addDelegationTokens("renewer", null);
        Assert.assertEquals(1, tokens.length);
        Token<?> token2 = tokens[0];
        Assert.assertNotEquals(token1, token2);
        Assert.assertNotSame(expectedUgi, token2.decodeIdentifier().getUser());
    }

    /**
     * Test that the delegation token secret manager only runs when the
     * NN is out of safe mode. This is because the secret manager
     * has to log to the edit log, which should not be written in
     * safe mode. Regression test for HDFS-2579.
     */
    @Test
    public void testDTManagerInSafeMode() throws Exception {
        cluster.startDataNodes(config, 1, true, REGULAR, null);
        FileSystem fs = cluster.getFileSystem();
        for (int i = 0; i < 5; i++) {
            DFSTestUtil.createFile(fs, new Path(("/test-" + i)), 100, ((short) (1)), 1L);
        }
        cluster.getConfiguration(0).setInt(DFS_NAMENODE_DELEGATION_KEY_UPDATE_INTERVAL_KEY, 500);
        cluster.getConfiguration(0).setInt(DFS_NAMENODE_SAFEMODE_EXTENSION_KEY, 30000);
        cluster.setWaitSafeMode(false);
        cluster.restartNameNode();
        NameNode nn = cluster.getNameNode();
        Assert.assertTrue(nn.isInSafeMode());
        DelegationTokenSecretManager sm = NameNodeAdapter.getDtSecretManager(nn.getNamesystem());
        Assert.assertFalse("Secret manager should not run in safe mode", sm.isRunning());
        NameNodeAdapter.leaveSafeMode(nn);
        Assert.assertTrue("Secret manager should start when safe mode is exited", sm.isRunning());
        TestDelegationToken.LOG.info("========= entering safemode again");
        NameNodeAdapter.enterSafeMode(nn, false);
        Assert.assertFalse(("Secret manager should stop again when safe mode " + "is manually entered"), sm.isRunning());
        // Set the cluster to leave safemode quickly on its own.
        cluster.getConfiguration(0).setInt(DFS_NAMENODE_SAFEMODE_EXTENSION_KEY, 0);
        cluster.setWaitSafeMode(true);
        cluster.restartNameNode();
        nn = cluster.getNameNode();
        sm = NameNodeAdapter.getDtSecretManager(nn.getNamesystem());
        Assert.assertFalse(nn.isInSafeMode());
        Assert.assertTrue(sm.isRunning());
    }

    @Test
    public void testDelegationTokenIdentifierToString() throws Exception {
        DelegationTokenIdentifier dtId = new DelegationTokenIdentifier(new Text("SomeUser"), new Text("JobTracker"), null);
        Assert.assertEquals(("HDFS_DELEGATION_TOKEN token 0" + " for SomeUser with renewer JobTracker"), dtId.toStringStable());
    }
}

