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
package org.apache.hadoop.mapreduce.v2.hs;


import JHAdminConfig.MR_HS_FS_STATE_STORE_URI;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.v2.api.MRDelegationTokenIdentifier;
import org.apache.hadoop.mapreduce.v2.hs.HistoryServerStateStoreService.HistoryServerState;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestHistoryServerFileSystemStateStoreService {
    private static final File testDir = new File(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), "TestHistoryServerFileSystemStateStoreService");

    private Configuration conf;

    @Test
    public void testTokenStore() throws IOException {
        testTokenStore(TestHistoryServerFileSystemStateStoreService.testDir.getAbsoluteFile().toURI().toString());
    }

    @Test
    public void testTokenStoreHdfs() throws IOException {
        MiniDFSCluster cluster = build();
        conf = cluster.getConfiguration(0);
        try {
            testTokenStore("/tmp/historystore");
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testUpdatedTokenRecovery() throws IOException {
        IOException intentionalErr = new IOException("intentional error");
        FileSystem fs = FileSystem.getLocal(conf);
        final FileSystem spyfs = Mockito.spy(fs);
        // make the update token process fail halfway through where we're left
        // with just the temporary update file and no token file
        ArgumentMatcher<Path> updateTmpMatcher = ( arg) -> arg.getName().startsWith("update");
        Mockito.doThrow(intentionalErr).when(spyfs).rename(ArgumentMatchers.argThat(updateTmpMatcher), ArgumentMatchers.isA(Path.class));
        conf.set(MR_HS_FS_STATE_STORE_URI, TestHistoryServerFileSystemStateStoreService.testDir.getAbsoluteFile().toURI().toString());
        HistoryServerStateStoreService store = new HistoryServerFileSystemStateStoreService() {
            @Override
            FileSystem createFileSystem() throws IOException {
                return spyfs;
            }
        };
        store.init(conf);
        store.start();
        final MRDelegationTokenIdentifier token1 = new MRDelegationTokenIdentifier(new Text("tokenOwner1"), new Text("tokenRenewer1"), new Text("tokenUser1"));
        token1.setSequenceNumber(1);
        final Long tokenDate1 = 1L;
        store.storeToken(token1, tokenDate1);
        final Long newTokenDate1 = 975318642L;
        try {
            store.updateToken(token1, newTokenDate1);
            Assert.fail("intentional error not thrown");
        } catch (IOException e) {
            Assert.assertEquals(intentionalErr, e);
        }
        store.close();
        // verify the update file is seen and parsed upon recovery when
        // original token file is missing
        store = createAndStartStore();
        HistoryServerState state = store.loadState();
        Assert.assertEquals("incorrect loaded token count", 1, state.tokenState.size());
        Assert.assertTrue("missing token 1", state.tokenState.containsKey(token1));
        Assert.assertEquals("incorrect token 1 date", newTokenDate1, state.tokenState.get(token1));
        store.close();
    }
}

