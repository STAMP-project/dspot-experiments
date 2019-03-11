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
package org.apache.hadoop.hdfs.qjournal.server;


import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.qjournal.MiniJournalCluster;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link JournalNodeMXBean}
 */
public class TestJournalNodeMXBean {
    private static final String NAMESERVICE = "ns1";

    private static final int NUM_JN = 1;

    private MiniJournalCluster jCluster;

    private JournalNode jn;

    @Test
    public void testJournalNodeMXBean() throws Exception {
        // we have not formatted the journals yet, and the journal status in jmx
        // should be empty since journal objects are created lazily
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName mxbeanName = new ObjectName("Hadoop:service=JournalNode,name=JournalNodeInfo");
        // getJournalsStatus
        String journalStatus = ((String) (mbs.getAttribute(mxbeanName, "JournalsStatus")));
        Assert.assertEquals(jn.getJournalsStatus(), journalStatus);
        Assert.assertFalse(journalStatus.contains(TestJournalNodeMXBean.NAMESERVICE));
        // format the journal ns1
        final NamespaceInfo FAKE_NSINFO = new NamespaceInfo(12345, "mycluster", "my-bp", 0L);
        jn.getOrCreateJournal(TestJournalNodeMXBean.NAMESERVICE).format(FAKE_NSINFO, false);
        // check again after format
        // getJournalsStatus
        journalStatus = ((String) (mbs.getAttribute(mxbeanName, "JournalsStatus")));
        Assert.assertEquals(jn.getJournalsStatus(), journalStatus);
        Map<String, Map<String, String>> jMap = new HashMap<String, Map<String, String>>();
        Map<String, String> infoMap = new HashMap<String, String>();
        infoMap.put("Formatted", "true");
        jMap.put(TestJournalNodeMXBean.NAMESERVICE, infoMap);
        Map<String, String> infoMap1 = new HashMap<>();
        infoMap1.put("Formatted", "false");
        jMap.put(MiniJournalCluster.CLUSTER_WAITACTIVE_URI, infoMap1);
        Assert.assertEquals(JSON.toString(jMap), journalStatus);
        // restart journal node without formatting
        jCluster = new MiniJournalCluster.Builder(new Configuration()).format(false).numJournalNodes(TestJournalNodeMXBean.NUM_JN).build();
        jCluster.waitActive();
        jn = jCluster.getJournalNode(0);
        // re-check
        journalStatus = ((String) (mbs.getAttribute(mxbeanName, "JournalsStatus")));
        Assert.assertEquals(jn.getJournalsStatus(), journalStatus);
        Assert.assertEquals(JSON.toString(jMap), journalStatus);
    }
}

