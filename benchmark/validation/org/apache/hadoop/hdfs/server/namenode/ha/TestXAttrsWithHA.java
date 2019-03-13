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


import XAttrSetFlag.CREATE;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.XAttr;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests interaction of XAttrs with HA failover.
 */
public class TestXAttrsWithHA {
    private static final Path path = new Path("/file");

    // XAttrs
    protected static final String name1 = "user.a1";

    protected static final byte[] value1 = new byte[]{ 49, 50, 51 };

    protected static final byte[] newValue1 = new byte[]{ 49, 49, 49 };

    protected static final String name2 = "user.a2";

    protected static final byte[] value2 = new byte[]{ 55, 56, 57 };

    protected static final String name3 = "user.a3";

    private MiniDFSCluster cluster;

    private NameNode nn0;

    private NameNode nn1;

    private FileSystem fs;

    /**
     * Test that xattrs are properly tracked by the standby
     */
    @Test(timeout = 60000)
    public void testXAttrsTrackedOnStandby() throws Exception {
        fs.create(TestXAttrsWithHA.path).close();
        fs.setXAttr(TestXAttrsWithHA.path, TestXAttrsWithHA.name1, TestXAttrsWithHA.value1, EnumSet.of(CREATE));
        fs.setXAttr(TestXAttrsWithHA.path, TestXAttrsWithHA.name2, TestXAttrsWithHA.value2, EnumSet.of(CREATE));
        HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
        List<XAttr> xAttrs = nn1.getRpcServer().getXAttrs("/file", null);
        Assert.assertEquals(2, xAttrs.size());
        cluster.shutdownNameNode(0);
        // Failover the current standby to active.
        cluster.shutdownNameNode(0);
        cluster.transitionToActive(1);
        Map<String, byte[]> xattrs = fs.getXAttrs(TestXAttrsWithHA.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(TestXAttrsWithHA.value1, xattrs.get(TestXAttrsWithHA.name1));
        Assert.assertArrayEquals(TestXAttrsWithHA.value2, xattrs.get(TestXAttrsWithHA.name2));
        fs.delete(TestXAttrsWithHA.path, true);
    }
}

