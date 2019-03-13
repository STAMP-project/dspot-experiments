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
package org.apache.hadoop.hdfs.server.datanode;


import DatanodeStorage.State.NORMAL;
import StorageType.DEFAULT;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.hdfs.server.protocol.SlowDiskReports;
import org.apache.hadoop.hdfs.server.protocol.SlowPeerReports;
import org.apache.hadoop.hdfs.server.protocol.StorageReport;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestStorageReport {
    public static final Logger LOG = LoggerFactory.getLogger(TestStorageReport.class);

    private static final short REPL_FACTOR = 1;

    private static final StorageType storageType = StorageType.SSD;// pick non-default.


    private static Configuration conf;

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    static String bpid;

    /**
     * Ensure that storage type and storage state are propagated
     * in Storage Reports.
     */
    @Test
    public void testStorageReportHasStorageTypeAndState() throws IOException {
        // Make sure we are not testing with the default type, that would not
        // be a very good test.
        Assert.assertNotSame(TestStorageReport.storageType, DEFAULT);
        NameNode nn = cluster.getNameNode();
        DataNode dn = cluster.getDataNodes().get(0);
        // Insert a spy object for the NN RPC.
        DatanodeProtocolClientSideTranslatorPB nnSpy = InternalDataNodeTestUtils.spyOnBposToNN(dn, nn);
        // Trigger a heartbeat so there is an interaction with the spy
        // object.
        DataNodeTestUtils.triggerHeartbeat(dn);
        // Verify that the callback passed in the expected parameters.
        ArgumentCaptor<StorageReport[]> captor = ArgumentCaptor.forClass(StorageReport[].class);
        Mockito.verify(nnSpy).sendHeartbeat(ArgumentMatchers.any(DatanodeRegistration.class), captor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), Mockito.anyBoolean(), Mockito.any(SlowPeerReports.class), Mockito.any(SlowDiskReports.class));
        StorageReport[] reports = captor.getValue();
        for (StorageReport report : reports) {
            Assert.assertThat(report.getStorage().getStorageType(), Is.is(TestStorageReport.storageType));
            Assert.assertThat(report.getStorage().getState(), Is.is(NORMAL));
        }
    }
}

