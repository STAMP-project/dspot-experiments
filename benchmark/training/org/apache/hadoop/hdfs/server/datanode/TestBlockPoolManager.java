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


import DFSConfigKeys.DFS_INTERNAL_NAMESERVICES_KEY;
import DFSConfigKeys.DFS_NAMESERVICES;
import DFSConfigKeys.FS_DEFAULT_NAME_KEY;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.Whitebox;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestBlockPoolManager {
    private static final Logger LOG = LoggerFactory.getLogger(TestBlockPoolManager.class);

    private final DataNode mockDN = Mockito.mock(DataNode.class);

    private BlockPoolManager bpm;

    private final StringBuilder log = new StringBuilder();

    private int mockIdx = 1;

    @Test
    public void testSimpleSingleNS() throws Exception {
        Configuration conf = new Configuration();
        conf.set(FS_DEFAULT_NAME_KEY, "hdfs://mock1:8020");
        bpm.refreshNamenodes(conf);
        Assert.assertEquals("create #1\n", log.toString());
    }

    @Test
    public void testFederationRefresh() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DFS_NAMESERVICES, "ns1,ns2");
        TestBlockPoolManager.addNN(conf, "ns1", "mock1:8020");
        TestBlockPoolManager.addNN(conf, "ns2", "mock1:8020");
        bpm.refreshNamenodes(conf);
        Assert.assertEquals(("create #1\n" + "create #2\n"), log.toString());
        log.setLength(0);
        // Remove the first NS
        conf.set(DFS_NAMESERVICES, "ns2");
        bpm.refreshNamenodes(conf);
        Assert.assertEquals(("stop #1\n" + "refresh #2\n"), log.toString());
        log.setLength(0);
        // Add back an NS -- this creates a new BPOS since the old
        // one for ns2 should have been previously retired
        conf.set(DFS_NAMESERVICES, "ns1,ns2");
        bpm.refreshNamenodes(conf);
        Assert.assertEquals(("create #3\n" + "refresh #2\n"), log.toString());
    }

    @Test
    public void testInternalNameService() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DFS_NAMESERVICES, "ns1,ns2,ns3");
        TestBlockPoolManager.addNN(conf, "ns1", "mock1:8020");
        TestBlockPoolManager.addNN(conf, "ns2", "mock1:8020");
        TestBlockPoolManager.addNN(conf, "ns3", "mock1:8020");
        conf.set(DFS_INTERNAL_NAMESERVICES_KEY, "ns1");
        bpm.refreshNamenodes(conf);
        Assert.assertEquals("create #1\n", log.toString());
        @SuppressWarnings("unchecked")
        Map<String, BPOfferService> map = ((Map<String, BPOfferService>) (Whitebox.getInternalState(bpm, "bpByNameserviceId")));
        Assert.assertFalse(map.containsKey("ns2"));
        Assert.assertFalse(map.containsKey("ns3"));
        Assert.assertTrue(map.containsKey("ns1"));
        log.setLength(0);
    }
}

