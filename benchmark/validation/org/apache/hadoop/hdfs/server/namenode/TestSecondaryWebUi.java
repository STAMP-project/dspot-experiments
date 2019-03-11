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
package org.apache.hadoop.hdfs.server.namenode;


import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;


public class TestSecondaryWebUi {
    private static MiniDFSCluster cluster;

    private static SecondaryNameNode snn;

    private static final Configuration conf = new Configuration();

    @Test
    public void testSecondaryWebUi() throws IOException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, MalformedObjectNameException, ReflectionException {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName mxbeanName = new ObjectName("Hadoop:service=SecondaryNameNode,name=SecondaryNameNodeInfo");
        String[] checkpointDir = ((String[]) (mbs.getAttribute(mxbeanName, "CheckpointDirectories")));
        Assert.assertArrayEquals(checkpointDir, TestSecondaryWebUi.snn.getCheckpointDirectories());
        String[] checkpointEditlogDir = ((String[]) (mbs.getAttribute(mxbeanName, "CheckpointEditlogDirectories")));
        Assert.assertArrayEquals(checkpointEditlogDir, TestSecondaryWebUi.snn.getCheckpointEditlogDirectories());
    }
}

