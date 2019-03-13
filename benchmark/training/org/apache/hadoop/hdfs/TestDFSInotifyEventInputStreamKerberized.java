/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs;


import java.io.File;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.inotify.EventBatch;
import org.apache.hadoop.hdfs.qjournal.MiniQJMHACluster;
import org.apache.hadoop.ipc.Client;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class for Kerberized test cases for {@link DFSInotifyEventInputStream}.
 */
public class TestDFSInotifyEventInputStreamKerberized {
    private static final Logger LOG = LoggerFactory.getLogger(TestDFSInotifyEventInputStreamKerberized.class);

    private File baseDir;

    private String keystoresDir;

    private String sslConfDir;

    private MiniKdc kdc;

    private Configuration baseConf;

    private Configuration conf;

    private MiniQJMHACluster cluster;

    private File generalHDFSKeytabFile;

    private File nnKeytabFile;

    @Rule
    public Timeout timeout = new Timeout(180000);

    @Test
    public void testWithKerberizedCluster() throws Exception {
        conf = new HdfsConfiguration(baseConf);
        // make sure relogin can happen after tgt expiration.
        conf.setInt(CommonConfigurationKeysPublic.HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN, 3);
        // make sure the rpc connection is not reused
        conf.setInt(CommonConfigurationKeys.IPC_CLIENT_CONNECTION_IDLESCANINTERVAL_KEY, 100);
        conf.setInt(CommonConfigurationKeysPublic.IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY, 2000);
        Client.setConnectTimeout(conf, 2000);
        // force the remote journal to be the only edits dir, so we can test
        // EditLogFileInputStream$URLLog behavior.
        cluster = new MiniQJMHACluster.Builder(conf).setForceRemoteEditsOnly(true).build();
        cluster.getDfsCluster().waitActive();
        cluster.getDfsCluster().transitionToActive(0);
        final UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI("hdfs", generalHDFSKeytabFile.getAbsolutePath());
        UserGroupInformation.setShouldRenewImmediatelyForTests(true);
        ugi.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                TestDFSInotifyEventInputStreamKerberized.LOG.info(((("Current user is: " + (UserGroupInformation.getCurrentUser())) + " login user is:") + (UserGroupInformation.getLoginUser())));
                Configuration clientConf = new Configuration(cluster.getDfsCluster().getConfiguration(0));
                try (DistributedFileSystem clientFs = ((DistributedFileSystem) (FileSystem.get(clientConf)))) {
                    clientFs.mkdirs(new Path("/test"));
                    TestDFSInotifyEventInputStreamKerberized.LOG.info("mkdir /test success");
                    final DFSInotifyEventInputStream eis = clientFs.getInotifyEventStream();
                    // verify we can poll
                    EventBatch batch;
                    while ((batch = eis.poll()) != null) {
                        TestDFSInotifyEventInputStreamKerberized.LOG.info(("txid: " + (batch.getTxid())));
                    } 
                    Assert.assertNull("poll should not return anything", eis.poll());
                    Thread.sleep(6000);
                    TestDFSInotifyEventInputStreamKerberized.LOG.info("Slept 6 seconds to make sure the TGT has expired.");
                    UserGroupInformation.getCurrentUser().checkTGTAndReloginFromKeytab();
                    clientFs.mkdirs(new Path("/test1"));
                    TestDFSInotifyEventInputStreamKerberized.LOG.info("mkdir /test1 success");
                    // verify we can poll after a tgt expiration interval
                    batch = eis.poll();
                    Assert.assertNotNull("poll should return something", batch);
                    Assert.assertEquals(1, batch.getEvents().length);
                    Assert.assertNull("poll should not return anything", eis.poll());
                    return null;
                }
            }
        });
    }
}

