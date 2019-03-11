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
package org.apache.hadoop.hdfs.server.namenode.ha;


import HAServiceState.STANDBY;
import HdfsConstants.HDFS_URI_SCHEME;
import com.google.common.base.Joiner;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.HashSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.AbstractFileSystem;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenSecretManager;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenSelector;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.RetriableException;
import org.apache.hadoop.ipc.StandbyException;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.SecurityUtilTestHelper;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.Whitebox;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test case for client support of delegation tokens in an HA cluster.
 * See HDFS-2904 for more info.
 */
public class TestDelegationTokensWithHA {
    private static final Configuration conf = new Configuration();

    private static final Logger LOG = LoggerFactory.getLogger(TestDelegationTokensWithHA.class);

    private static MiniDFSCluster cluster;

    private static NameNode nn0;

    private static NameNode nn1;

    private static FileSystem fs;

    private static DelegationTokenSecretManager dtSecretManager;

    private static DistributedFileSystem dfs;

    private volatile boolean catchup = false;

    @Test(timeout = 300000)
    public void testDelegationTokenDFSApi() throws Exception {
        final Token<DelegationTokenIdentifier> token = getDelegationToken(TestDelegationTokensWithHA.fs, "JobTracker");
        DelegationTokenIdentifier identifier = new DelegationTokenIdentifier();
        byte[] tokenId = token.getIdentifier();
        identifier.readFields(new DataInputStream(new ByteArrayInputStream(tokenId)));
        // Ensure that it's present in the NN's secret manager and can
        // be renewed directly from there.
        TestDelegationTokensWithHA.LOG.info(("A valid token should have non-null password, " + "and should be renewed successfully"));
        Assert.assertTrue((null != (TestDelegationTokensWithHA.dtSecretManager.retrievePassword(identifier))));
        TestDelegationTokensWithHA.dtSecretManager.renewToken(token, "JobTracker");
        // Use the client conf with the failover info present to check
        // renewal.
        Configuration clientConf = TestDelegationTokensWithHA.dfs.getConf();
        TestDelegationTokensWithHA.doRenewOrCancel(token, clientConf, TestDelegationTokensWithHA.TokenTestAction.RENEW);
        // Using a configuration that doesn't have the logical nameservice
        // configured should result in a reasonable error message.
        Configuration emptyConf = new Configuration();
        try {
            TestDelegationTokensWithHA.doRenewOrCancel(token, emptyConf, TestDelegationTokensWithHA.TokenTestAction.RENEW);
            Assert.fail("Did not throw trying to renew with an empty conf!");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Unable to map logical nameservice URI", ioe);
        }
        // Ensure that the token can be renewed again after a failover.
        TestDelegationTokensWithHA.cluster.transitionToStandby(0);
        TestDelegationTokensWithHA.cluster.transitionToActive(1);
        TestDelegationTokensWithHA.doRenewOrCancel(token, clientConf, TestDelegationTokensWithHA.TokenTestAction.RENEW);
        TestDelegationTokensWithHA.doRenewOrCancel(token, clientConf, TestDelegationTokensWithHA.TokenTestAction.CANCEL);
    }

    private class EditLogTailerForTest extends EditLogTailer {
        public EditLogTailerForTest(FSNamesystem namesystem, Configuration conf) {
            super(namesystem, conf);
        }

        public void catchupDuringFailover() throws IOException {
            synchronized(TestDelegationTokensWithHA.this) {
                while (!(catchup)) {
                    try {
                        TestDelegationTokensWithHA.LOG.info("The editlog tailer is waiting to catchup...");
                        TestDelegationTokensWithHA.this.wait();
                    } catch (InterruptedException e) {
                    }
                } 
            }
            super.catchupDuringFailover();
        }
    }

    /**
     * Test if correct exception (StandbyException or RetriableException) can be
     * thrown during the NN failover.
     */
    @Test(timeout = 300000)
    public void testDelegationTokenDuringNNFailover() throws Exception {
        EditLogTailer editLogTailer = TestDelegationTokensWithHA.nn1.getNamesystem().getEditLogTailer();
        // stop the editLogTailer of nn1
        editLogTailer.stop();
        Configuration conf = ((Configuration) (Whitebox.getInternalState(editLogTailer, "conf")));
        TestDelegationTokensWithHA.nn1.getNamesystem().setEditLogTailerForTests(new TestDelegationTokensWithHA.EditLogTailerForTest(TestDelegationTokensWithHA.nn1.getNamesystem(), conf));
        // create token
        final Token<DelegationTokenIdentifier> token = getDelegationToken(TestDelegationTokensWithHA.fs, "JobTracker");
        DelegationTokenIdentifier identifier = new DelegationTokenIdentifier();
        byte[] tokenId = token.getIdentifier();
        identifier.readFields(new DataInputStream(new ByteArrayInputStream(tokenId)));
        // Ensure that it's present in the nn0 secret manager and can
        // be renewed directly from there.
        TestDelegationTokensWithHA.LOG.info(("A valid token should have non-null password, " + "and should be renewed successfully"));
        Assert.assertTrue((null != (TestDelegationTokensWithHA.dtSecretManager.retrievePassword(identifier))));
        TestDelegationTokensWithHA.dtSecretManager.renewToken(token, "JobTracker");
        // transition nn0 to standby
        TestDelegationTokensWithHA.cluster.transitionToStandby(0);
        try {
            TestDelegationTokensWithHA.cluster.getNameNodeRpc(0).renewDelegationToken(token);
            Assert.fail("StandbyException is expected since nn0 is in standby state");
        } catch (StandbyException e) {
            GenericTestUtils.assertExceptionContains(STANDBY.toString(), e);
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    TestDelegationTokensWithHA.cluster.transitionToActive(1);
                } catch (Exception e) {
                    TestDelegationTokensWithHA.LOG.error("Transition nn1 to active failed", e);
                }
            }
        }.start();
        Thread.sleep(1000);
        try {
            TestDelegationTokensWithHA.nn1.getNamesystem().verifyToken(token.decodeIdentifier(), token.getPassword());
            Assert.fail("RetriableException/StandbyException is expected since nn1 is in transition");
        } catch (IOException e) {
            Assert.assertTrue(((e instanceof StandbyException) || (e instanceof RetriableException)));
            TestDelegationTokensWithHA.LOG.info("Got expected exception", e);
        }
        catchup = true;
        synchronized(this) {
            this.notifyAll();
        }
        Configuration clientConf = TestDelegationTokensWithHA.dfs.getConf();
        TestDelegationTokensWithHA.doRenewOrCancel(token, clientConf, TestDelegationTokensWithHA.TokenTestAction.RENEW);
        TestDelegationTokensWithHA.doRenewOrCancel(token, clientConf, TestDelegationTokensWithHA.TokenTestAction.CANCEL);
    }

    @Test(timeout = 300000)
    public void testDelegationTokenWithDoAs() throws Exception {
        final Token<DelegationTokenIdentifier> token = getDelegationToken(TestDelegationTokensWithHA.fs, "JobTracker");
        final UserGroupInformation longUgi = UserGroupInformation.createRemoteUser("JobTracker/foo.com@FOO.COM");
        final UserGroupInformation shortUgi = UserGroupInformation.createRemoteUser("JobTracker");
        longUgi.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                // try renew with long name
                token.renew(TestDelegationTokensWithHA.conf);
                return null;
            }
        });
        shortUgi.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                token.renew(TestDelegationTokensWithHA.conf);
                return null;
            }
        });
        longUgi.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                token.cancel(TestDelegationTokensWithHA.conf);
                return null;
            }
        });
    }

    @Test(timeout = 300000)
    public void testHAUtilClonesDelegationTokens() throws Exception {
        final Token<DelegationTokenIdentifier> token = getDelegationToken(TestDelegationTokensWithHA.fs, "JobTracker");
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test");
        URI haUri = new URI("hdfs://my-ha-uri/");
        token.setService(HAUtilClient.buildTokenServiceForLogicalUri(haUri, HDFS_URI_SCHEME));
        ugi.addToken(token);
        Collection<InetSocketAddress> nnAddrs = new HashSet<InetSocketAddress>();
        nnAddrs.add(new InetSocketAddress("localhost", TestDelegationTokensWithHA.nn0.getNameNodeAddress().getPort()));
        nnAddrs.add(new InetSocketAddress("localhost", TestDelegationTokensWithHA.nn1.getNameNodeAddress().getPort()));
        HAUtilClient.cloneDelegationTokenForLogicalUri(ugi, haUri, nnAddrs);
        Collection<Token<? extends TokenIdentifier>> tokens = ugi.getTokens();
        Assert.assertEquals(3, tokens.size());
        TestDelegationTokensWithHA.LOG.info(("Tokens:\n" + (Joiner.on("\n").join(tokens))));
        DelegationTokenSelector dts = new DelegationTokenSelector();
        // check that the token selected for one of the physical IPC addresses
        // matches the one we received
        for (InetSocketAddress addr : nnAddrs) {
            Text ipcDtService = SecurityUtil.buildTokenService(addr);
            Token<DelegationTokenIdentifier> token2 = dts.selectToken(ipcDtService, ugi.getTokens());
            Assert.assertNotNull(token2);
            Assert.assertArrayEquals(token.getIdentifier(), token2.getIdentifier());
            Assert.assertArrayEquals(token.getPassword(), token2.getPassword());
        }
        // switch to host-based tokens, shouldn't match existing tokens
        SecurityUtilTestHelper.setTokenServiceUseIp(false);
        for (InetSocketAddress addr : nnAddrs) {
            Text ipcDtService = SecurityUtil.buildTokenService(addr);
            Token<DelegationTokenIdentifier> token2 = dts.selectToken(ipcDtService, ugi.getTokens());
            Assert.assertNull(token2);
        }
        // reclone the tokens, and see if they match now
        HAUtilClient.cloneDelegationTokenForLogicalUri(ugi, haUri, nnAddrs);
        for (InetSocketAddress addr : nnAddrs) {
            Text ipcDtService = SecurityUtil.buildTokenService(addr);
            Token<DelegationTokenIdentifier> token2 = dts.selectToken(ipcDtService, ugi.getTokens());
            Assert.assertNotNull(token2);
            Assert.assertArrayEquals(token.getIdentifier(), token2.getIdentifier());
            Assert.assertArrayEquals(token.getPassword(), token2.getPassword());
        }
    }

    /**
     * HDFS-3062: DistributedFileSystem.getCanonicalServiceName() throws an
     * exception if the URI is a logical URI. This bug fails the combination of
     * ha + mapred + security.
     */
    @Test(timeout = 300000)
    public void testDFSGetCanonicalServiceName() throws Exception {
        URI hAUri = HATestUtil.getLogicalUri(TestDelegationTokensWithHA.cluster);
        String haService = HAUtilClient.buildTokenServiceForLogicalUri(hAUri, HDFS_URI_SCHEME).toString();
        Assert.assertEquals(haService, TestDelegationTokensWithHA.dfs.getCanonicalServiceName());
        final String renewer = UserGroupInformation.getCurrentUser().getShortUserName();
        final Token<DelegationTokenIdentifier> token = getDelegationToken(TestDelegationTokensWithHA.dfs, renewer);
        Assert.assertEquals(haService, token.getService().toString());
        // make sure the logical uri is handled correctly
        token.renew(TestDelegationTokensWithHA.dfs.getConf());
        token.cancel(TestDelegationTokensWithHA.dfs.getConf());
    }

    @Test(timeout = 300000)
    public void testHdfsGetCanonicalServiceName() throws Exception {
        Configuration conf = TestDelegationTokensWithHA.dfs.getConf();
        URI haUri = HATestUtil.getLogicalUri(TestDelegationTokensWithHA.cluster);
        AbstractFileSystem afs = AbstractFileSystem.createFileSystem(haUri, conf);
        String haService = HAUtilClient.buildTokenServiceForLogicalUri(haUri, HDFS_URI_SCHEME).toString();
        Assert.assertEquals(haService, afs.getCanonicalServiceName());
        Token<?> token = afs.getDelegationTokens(UserGroupInformation.getCurrentUser().getShortUserName()).get(0);
        Assert.assertEquals(haService, token.getService().toString());
        // make sure the logical uri is handled correctly
        token.renew(conf);
        token.cancel(conf);
    }

    @Test(timeout = 300000)
    public void testCancelAndUpdateDelegationTokens() throws Exception {
        // Create UGI with token1
        String user = UserGroupInformation.getCurrentUser().getShortUserName();
        UserGroupInformation ugi1 = UserGroupInformation.createRemoteUser(user);
        ugi1.doAs(new PrivilegedExceptionAction<Void>() {
            public Void run() throws Exception {
                final Token<DelegationTokenIdentifier> token1 = getDelegationToken(TestDelegationTokensWithHA.fs, "JobTracker");
                UserGroupInformation.getCurrentUser().addToken(token1.getService(), token1);
                FileSystem fs1 = HATestUtil.configureFailoverFs(TestDelegationTokensWithHA.cluster, TestDelegationTokensWithHA.conf);
                // Cancel token1
                TestDelegationTokensWithHA.doRenewOrCancel(token1, TestDelegationTokensWithHA.conf, TestDelegationTokensWithHA.TokenTestAction.CANCEL);
                // Update UGI with token2
                final Token<DelegationTokenIdentifier> token2 = getDelegationToken(TestDelegationTokensWithHA.fs, "JobTracker");
                UserGroupInformation.getCurrentUser().addToken(token2.getService(), token2);
                // Check whether token2 works
                fs1.listFiles(new Path("/"), false);
                return null;
            }
        });
    }

    enum TokenTestAction {

        RENEW,
        CANCEL;}
}

