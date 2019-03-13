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
package org.apache.hadoop.hbase.zookeeper;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.testclassification.ZKTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ZKTests.class, SmallTests.class })
public class TestZNodePaths {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZNodePaths.class);

    @Test
    public void testIsClientReadable() {
        ZNodePaths znodePaths = new ZNodePaths(HBaseConfiguration.create());
        Assert.assertTrue(znodePaths.isClientReadable(znodePaths.baseZNode));
        Assert.assertTrue(znodePaths.isClientReadable(znodePaths.getZNodeForReplica(0)));
        Assert.assertTrue(znodePaths.isClientReadable(znodePaths.masterAddressZNode));
        Assert.assertTrue(znodePaths.isClientReadable(znodePaths.clusterIdZNode));
        Assert.assertTrue(znodePaths.isClientReadable(znodePaths.tableZNode));
        Assert.assertTrue(znodePaths.isClientReadable(ZNodePaths.joinZNode(znodePaths.tableZNode, "foo")));
        Assert.assertTrue(znodePaths.isClientReadable(znodePaths.rsZNode));
        Assert.assertFalse(znodePaths.isClientReadable(znodePaths.tableLockZNode));
        Assert.assertFalse(znodePaths.isClientReadable(znodePaths.balancerZNode));
        Assert.assertFalse(znodePaths.isClientReadable(znodePaths.regionNormalizerZNode));
        Assert.assertFalse(znodePaths.isClientReadable(znodePaths.clusterStateZNode));
        Assert.assertFalse(znodePaths.isClientReadable(znodePaths.drainingZNode));
        Assert.assertFalse(znodePaths.isClientReadable(znodePaths.splitLogZNode));
        Assert.assertFalse(znodePaths.isClientReadable(znodePaths.backupMasterAddressesZNode));
    }
}

