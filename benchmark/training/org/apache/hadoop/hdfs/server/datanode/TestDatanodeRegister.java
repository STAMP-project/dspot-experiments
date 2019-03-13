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


import HdfsServerConstants.NAMENODE_LAYOUT_VERSION;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.hdfs.server.common.IncorrectVersionException;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.VersionInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDatanodeRegister {
    public static final Logger LOG = LoggerFactory.getLogger(TestDatanodeRegister.class);

    // Invalid address
    private static final InetSocketAddress INVALID_ADDR = new InetSocketAddress("127.0.0.1", 1);

    private BPServiceActor actor;

    NamespaceInfo fakeNsInfo;

    DNConf mockDnConf;

    @Test
    public void testSoftwareVersionDifferences() throws Exception {
        // We expect no exception to be thrown when the software versions match.
        Assert.assertEquals(VersionInfo.getVersion(), actor.retrieveNamespaceInfo().getSoftwareVersion());
        // We expect no exception to be thrown when the min NN version is below the
        // reported NN version.
        Mockito.doReturn("4.0.0").when(fakeNsInfo).getSoftwareVersion();
        Mockito.doReturn("3.0.0").when(mockDnConf).getMinimumNameNodeVersion();
        Assert.assertEquals("4.0.0", actor.retrieveNamespaceInfo().getSoftwareVersion());
        // When the NN reports a version that's too low, throw an exception.
        Mockito.doReturn("3.0.0").when(fakeNsInfo).getSoftwareVersion();
        Mockito.doReturn("4.0.0").when(mockDnConf).getMinimumNameNodeVersion();
        try {
            actor.retrieveNamespaceInfo();
            Assert.fail("Should have thrown an exception for NN with too-low version");
        } catch (IncorrectVersionException ive) {
            GenericTestUtils.assertExceptionContains("The reported NameNode version is too low", ive);
            TestDatanodeRegister.LOG.info("Got expected exception", ive);
        }
    }

    @Test
    public void testDifferentLayoutVersions() throws Exception {
        // We expect no exceptions to be thrown when the layout versions match.
        Assert.assertEquals(NAMENODE_LAYOUT_VERSION, actor.retrieveNamespaceInfo().getLayoutVersion());
        // We expect an exception to be thrown when the NN reports a layout version
        // different from that of the DN.
        Mockito.doReturn(((HdfsServerConstants.NAMENODE_LAYOUT_VERSION) * 1000)).when(fakeNsInfo).getLayoutVersion();
        try {
            actor.retrieveNamespaceInfo();
        } catch (IOException e) {
            Assert.fail("Should not fail to retrieve NS info from DN with different layout version");
        }
    }
}

