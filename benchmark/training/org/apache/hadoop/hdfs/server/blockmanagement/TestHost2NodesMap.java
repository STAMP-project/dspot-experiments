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
package org.apache.hadoop.hdfs.server.blockmanagement;


import org.apache.hadoop.hdfs.DFSTestUtil;
import org.junit.Assert;
import org.junit.Test;


public class TestHost2NodesMap {
    private final Host2NodesMap map = new Host2NodesMap();

    private DatanodeDescriptor[] dataNodes;

    @Test
    public void testContains() throws Exception {
        DatanodeDescriptor nodeNotInMap = DFSTestUtil.getDatanodeDescriptor("3.3.3.3", "/d1/r4");
        for (int i = 0; i < (dataNodes.length); i++) {
            Assert.assertTrue(map.contains(dataNodes[i]));
        }
        Assert.assertFalse(map.contains(null));
        Assert.assertFalse(map.contains(nodeNotInMap));
    }

    @Test
    public void testGetDatanodeByHost() throws Exception {
        Assert.assertEquals(map.getDatanodeByHost("1.1.1.1"), dataNodes[0]);
        Assert.assertEquals(map.getDatanodeByHost("2.2.2.2"), dataNodes[1]);
        DatanodeDescriptor node = map.getDatanodeByHost("3.3.3.3");
        Assert.assertTrue(((node == (dataNodes[2])) || (node == (dataNodes[3]))));
        Assert.assertNull(map.getDatanodeByHost("4.4.4.4"));
    }

    @Test
    public void testRemove() throws Exception {
        DatanodeDescriptor nodeNotInMap = DFSTestUtil.getDatanodeDescriptor("3.3.3.3", "/d1/r4");
        Assert.assertFalse(map.remove(nodeNotInMap));
        Assert.assertTrue(map.remove(dataNodes[0]));
        Assert.assertTrue(((map.getDatanodeByHost("1.1.1.1.")) == null));
        Assert.assertTrue(((map.getDatanodeByHost("2.2.2.2")) == (dataNodes[1])));
        DatanodeDescriptor node = map.getDatanodeByHost("3.3.3.3");
        Assert.assertTrue(((node == (dataNodes[2])) || (node == (dataNodes[3]))));
        Assert.assertNull(map.getDatanodeByHost("4.4.4.4"));
        Assert.assertTrue(map.remove(dataNodes[2]));
        Assert.assertNull(map.getDatanodeByHost("1.1.1.1"));
        Assert.assertEquals(map.getDatanodeByHost("2.2.2.2"), dataNodes[1]);
        Assert.assertEquals(map.getDatanodeByHost("3.3.3.3"), dataNodes[3]);
        Assert.assertTrue(map.remove(dataNodes[3]));
        Assert.assertNull(map.getDatanodeByHost("1.1.1.1"));
        Assert.assertEquals(map.getDatanodeByHost("2.2.2.2"), dataNodes[1]);
        Assert.assertNull(map.getDatanodeByHost("3.3.3.3"));
        Assert.assertFalse(map.remove(null));
        Assert.assertTrue(map.remove(dataNodes[1]));
        Assert.assertFalse(map.remove(dataNodes[1]));
    }
}

