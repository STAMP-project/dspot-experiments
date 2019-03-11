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


import FSImageFormat.renameReservedMap;
import RollingUpgradeStartupOption.ROLLBACK;
import RollingUpgradeStartupOption.STARTED;
import StartupOption.FORMAT;
import StartupOption.ROLLINGUPGRADE;
import StartupOption.UPGRADE;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestNameNodeOptionParsing {
    @Test(timeout = 10000)
    public void testUpgrade() {
        StartupOption opt = null;
        // UPGRADE is set, but nothing else
        opt = NameNode.parseArguments(new String[]{ "-upgrade" });
        Assert.assertEquals(opt, UPGRADE);
        Assert.assertNull(opt.getClusterId());
        Assert.assertTrue(renameReservedMap.isEmpty());
        // cluster ID is set
        opt = NameNode.parseArguments(new String[]{ "-upgrade", "-clusterid", "mycid" });
        Assert.assertEquals(UPGRADE, opt);
        Assert.assertEquals("mycid", opt.getClusterId());
        Assert.assertTrue(renameReservedMap.isEmpty());
        // Everything is set
        opt = NameNode.parseArguments(new String[]{ "-upgrade", "-clusterid", "mycid", "-renameReserved", ".snapshot=.my-snapshot,.reserved=.my-reserved" });
        Assert.assertEquals(UPGRADE, opt);
        Assert.assertEquals("mycid", opt.getClusterId());
        Assert.assertEquals(".my-snapshot", renameReservedMap.get(".snapshot"));
        Assert.assertEquals(".my-reserved", renameReservedMap.get(".reserved"));
        // Reset the map
        renameReservedMap.clear();
        // Everything is set, but in a different order
        opt = NameNode.parseArguments(new String[]{ "-upgrade", "-renameReserved", ".reserved=.my-reserved,.snapshot=.my-snapshot", "-clusterid", "mycid" });
        Assert.assertEquals(UPGRADE, opt);
        Assert.assertEquals("mycid", opt.getClusterId());
        Assert.assertEquals(".my-snapshot", renameReservedMap.get(".snapshot"));
        Assert.assertEquals(".my-reserved", renameReservedMap.get(".reserved"));
        // Try the default renameReserved
        opt = NameNode.parseArguments(new String[]{ "-upgrade", "-renameReserved" });
        Assert.assertEquals(UPGRADE, opt);
        Assert.assertEquals(((".snapshot." + (HdfsServerConstants.NAMENODE_LAYOUT_VERSION)) + ".UPGRADE_RENAMED"), renameReservedMap.get(".snapshot"));
        Assert.assertEquals(((".reserved." + (HdfsServerConstants.NAMENODE_LAYOUT_VERSION)) + ".UPGRADE_RENAMED"), renameReservedMap.get(".reserved"));
        // Try some error conditions
        try {
            opt = NameNode.parseArguments(new String[]{ "-upgrade", "-renameReserved", ".reserved=.my-reserved,.not-reserved=.my-not-reserved" });
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Unknown reserved path", e);
        }
        try {
            opt = NameNode.parseArguments(new String[]{ "-upgrade", "-renameReserved", ".reserved=.my-reserved,.snapshot=.snapshot" });
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Invalid rename path", e);
        }
        try {
            opt = NameNode.parseArguments(new String[]{ "-upgrade", "-renameReserved", ".snapshot=.reserved" });
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Invalid rename path", e);
        }
        opt = NameNode.parseArguments(new String[]{ "-upgrade", "-cid" });
        Assert.assertNull(opt);
    }

    @Test(timeout = 10000)
    public void testRollingUpgrade() {
        {
            final String[] args = new String[]{ "-rollingUpgrade" };
            final StartupOption opt = NameNode.parseArguments(args);
            Assert.assertNull(opt);
        }
        {
            final String[] args = new String[]{ "-rollingUpgrade", "started" };
            final StartupOption opt = NameNode.parseArguments(args);
            Assert.assertEquals(ROLLINGUPGRADE, opt);
            Assert.assertEquals(STARTED, opt.getRollingUpgradeStartupOption());
            Assert.assertTrue(STARTED.matches(opt));
        }
        {
            final String[] args = new String[]{ "-rollingUpgrade", "rollback" };
            final StartupOption opt = NameNode.parseArguments(args);
            Assert.assertEquals(ROLLINGUPGRADE, opt);
            Assert.assertEquals(ROLLBACK, opt.getRollingUpgradeStartupOption());
            Assert.assertTrue(ROLLBACK.matches(opt));
        }
        {
            final String[] args = new String[]{ "-rollingUpgrade", "foo" };
            try {
                NameNode.parseArguments(args);
                Assert.fail();
            } catch (IllegalArgumentException iae) {
                // the exception is expected.
            }
        }
    }

    @Test
    public void testFormat() {
        String[] args = new String[]{ "-format" };
        StartupOption opt = NameNode.parseArguments(args);
        Assert.assertEquals(FORMAT, opt);
        Assert.assertEquals(true, opt.getInteractiveFormat());
        Assert.assertEquals(false, opt.getForceFormat());
        args = new String[]{ "-format", "-nonInteractive" };
        opt = NameNode.parseArguments(args);
        Assert.assertEquals(FORMAT, opt);
        Assert.assertEquals(false, opt.getInteractiveFormat());
        Assert.assertEquals(false, opt.getForceFormat());
        args = new String[]{ "-format", "-nonInteractive", "-force" };
        opt = NameNode.parseArguments(args);
        Assert.assertEquals(FORMAT, opt);
        Assert.assertEquals(false, opt.getInteractiveFormat());
        Assert.assertEquals(true, opt.getForceFormat());
        // test error condition
        args = new String[]{ "-nonInteractive" };
        opt = NameNode.parseArguments(args);
        Assert.assertNull(opt);
    }
}

