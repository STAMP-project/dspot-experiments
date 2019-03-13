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


import java.util.LinkedList;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseZKTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ZKTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.ZKUtil.ZKUtilOp;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test ZooKeeper multi-update functionality
 */
@Category({ ZKTests.class, MediumTests.class })
public class TestZKMulti {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZKMulti.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestZKMulti.class);

    private static final HBaseZKTestingUtility TEST_UTIL = new HBaseZKTestingUtility();

    private static ZKWatcher zkw = null;

    @Test
    public void testSimpleMulti() throws Exception {
        // null multi
        ZKUtil.multiOrSequential(TestZKMulti.zkw, null, false);
        // empty multi
        ZKUtil.multiOrSequential(TestZKMulti.zkw, new LinkedList(), false);
        // single create
        String path = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testSimpleMulti");
        LinkedList<ZKUtilOp> singleCreate = new LinkedList<>();
        singleCreate.add(ZKUtilOp.createAndFailSilent(path, new byte[0]));
        ZKUtil.multiOrSequential(TestZKMulti.zkw, singleCreate, false);
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, path)) != (-1)));
        // single setdata
        LinkedList<ZKUtilOp> singleSetData = new LinkedList<>();
        byte[] data = Bytes.toBytes("foobar");
        singleSetData.add(ZKUtilOp.setData(path, data));
        ZKUtil.multiOrSequential(TestZKMulti.zkw, singleSetData, false);
        Assert.assertTrue(Bytes.equals(ZKUtil.getData(TestZKMulti.zkw, path), data));
        // single delete
        LinkedList<ZKUtilOp> singleDelete = new LinkedList<>();
        singleDelete.add(ZKUtilOp.deleteNodeFailSilent(path));
        ZKUtil.multiOrSequential(TestZKMulti.zkw, singleDelete, false);
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, path)) == (-1)));
    }

    @Test
    public void testComplexMulti() throws Exception {
        String path1 = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testComplexMulti1");
        String path2 = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testComplexMulti2");
        String path3 = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testComplexMulti3");
        String path4 = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testComplexMulti4");
        String path5 = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testComplexMulti5");
        String path6 = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testComplexMulti6");
        // create 4 nodes that we'll setData on or delete later
        LinkedList<ZKUtilOp> create4Nodes = new LinkedList<>();
        create4Nodes.add(ZKUtilOp.createAndFailSilent(path1, Bytes.toBytes(path1)));
        create4Nodes.add(ZKUtilOp.createAndFailSilent(path2, Bytes.toBytes(path2)));
        create4Nodes.add(ZKUtilOp.createAndFailSilent(path3, Bytes.toBytes(path3)));
        create4Nodes.add(ZKUtilOp.createAndFailSilent(path4, Bytes.toBytes(path4)));
        ZKUtil.multiOrSequential(TestZKMulti.zkw, create4Nodes, false);
        Assert.assertTrue(Bytes.equals(ZKUtil.getData(TestZKMulti.zkw, path1), Bytes.toBytes(path1)));
        Assert.assertTrue(Bytes.equals(ZKUtil.getData(TestZKMulti.zkw, path2), Bytes.toBytes(path2)));
        Assert.assertTrue(Bytes.equals(ZKUtil.getData(TestZKMulti.zkw, path3), Bytes.toBytes(path3)));
        Assert.assertTrue(Bytes.equals(ZKUtil.getData(TestZKMulti.zkw, path4), Bytes.toBytes(path4)));
        // do multiple of each operation (setData, delete, create)
        LinkedList<ZKUtilOp> ops = new LinkedList<>();
        // setData
        ops.add(ZKUtilOp.setData(path1, Bytes.add(Bytes.toBytes(path1), Bytes.toBytes(path1))));
        ops.add(ZKUtilOp.setData(path2, Bytes.add(Bytes.toBytes(path2), Bytes.toBytes(path2))));
        // delete
        ops.add(ZKUtilOp.deleteNodeFailSilent(path3));
        ops.add(ZKUtilOp.deleteNodeFailSilent(path4));
        // create
        ops.add(ZKUtilOp.createAndFailSilent(path5, Bytes.toBytes(path5)));
        ops.add(ZKUtilOp.createAndFailSilent(path6, Bytes.toBytes(path6)));
        ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, false);
        Assert.assertTrue(Bytes.equals(ZKUtil.getData(TestZKMulti.zkw, path1), Bytes.add(Bytes.toBytes(path1), Bytes.toBytes(path1))));
        Assert.assertTrue(Bytes.equals(ZKUtil.getData(TestZKMulti.zkw, path2), Bytes.add(Bytes.toBytes(path2), Bytes.toBytes(path2))));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, path3)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, path4)) == (-1)));
        Assert.assertTrue(Bytes.equals(ZKUtil.getData(TestZKMulti.zkw, path5), Bytes.toBytes(path5)));
        Assert.assertTrue(Bytes.equals(ZKUtil.getData(TestZKMulti.zkw, path6), Bytes.toBytes(path6)));
    }

    @Test
    public void testSingleFailure() throws Exception {
        // try to delete a node that doesn't exist
        boolean caughtNoNode = false;
        String path = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testSingleFailureZ");
        LinkedList<ZKUtilOp> ops = new LinkedList<>();
        ops.add(ZKUtilOp.deleteNodeFailSilent(path));
        try {
            ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, false);
        } catch (KeeperException nne) {
            caughtNoNode = true;
        }
        Assert.assertTrue(caughtNoNode);
        // try to setData on a node that doesn't exist
        caughtNoNode = false;
        ops = new LinkedList();
        ops.add(ZKUtilOp.setData(path, Bytes.toBytes(path)));
        try {
            ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, false);
        } catch (KeeperException nne) {
            caughtNoNode = true;
        }
        Assert.assertTrue(caughtNoNode);
        // try to create on a node that already exists
        boolean caughtNodeExists = false;
        ops = new LinkedList();
        ops.add(ZKUtilOp.createAndFailSilent(path, Bytes.toBytes(path)));
        ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, false);
        try {
            ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, false);
        } catch (KeeperException nee) {
            caughtNodeExists = true;
        }
        Assert.assertTrue(caughtNodeExists);
    }

    @Test
    public void testSingleFailureInMulti() throws Exception {
        // try a multi where all but one operation succeeds
        String pathA = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testSingleFailureInMultiA");
        String pathB = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testSingleFailureInMultiB");
        String pathC = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testSingleFailureInMultiC");
        LinkedList<ZKUtilOp> ops = new LinkedList<>();
        ops.add(ZKUtilOp.createAndFailSilent(pathA, Bytes.toBytes(pathA)));
        ops.add(ZKUtilOp.createAndFailSilent(pathB, Bytes.toBytes(pathB)));
        ops.add(ZKUtilOp.deleteNodeFailSilent(pathC));
        boolean caughtNoNode = false;
        try {
            ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, false);
        } catch (KeeperException nne) {
            caughtNoNode = true;
        }
        Assert.assertTrue(caughtNoNode);
        // assert that none of the operations succeeded
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathA)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathB)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathC)) == (-1)));
    }

    @Test
    public void testMultiFailure() throws Exception {
        String pathX = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testMultiFailureX");
        String pathY = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testMultiFailureY");
        String pathZ = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testMultiFailureZ");
        // create X that we will use to fail create later
        LinkedList<ZKUtilOp> ops = new LinkedList<>();
        ops.add(ZKUtilOp.createAndFailSilent(pathX, Bytes.toBytes(pathX)));
        ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, false);
        // fail one of each create ,setData, delete
        String pathV = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testMultiFailureV");
        String pathW = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "testMultiFailureW");
        ops = new LinkedList();
        ops.add(ZKUtilOp.createAndFailSilent(pathX, Bytes.toBytes(pathX)));// fail  -- already exists

        ops.add(ZKUtilOp.setData(pathY, Bytes.toBytes(pathY)));// fail -- doesn't exist

        ops.add(ZKUtilOp.deleteNodeFailSilent(pathZ));// fail -- doesn't exist

        ops.add(ZKUtilOp.createAndFailSilent(pathX, Bytes.toBytes(pathV)));// pass

        ops.add(ZKUtilOp.createAndFailSilent(pathX, Bytes.toBytes(pathW)));// pass

        boolean caughtNodeExists = false;
        try {
            ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, false);
        } catch (KeeperException nee) {
            // check first operation that fails throws exception
            caughtNodeExists = true;
        }
        Assert.assertTrue(caughtNodeExists);
        // check that no modifications were made
        Assert.assertFalse(((ZKUtil.checkExists(TestZKMulti.zkw, pathX)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathY)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathZ)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathW)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathV)) == (-1)));
        // test that with multiple failures, throws an exception corresponding to first failure in list
        ops = new LinkedList();
        ops.add(ZKUtilOp.setData(pathY, Bytes.toBytes(pathY)));// fail -- doesn't exist

        ops.add(ZKUtilOp.createAndFailSilent(pathX, Bytes.toBytes(pathX)));// fail -- exists

        boolean caughtNoNode = false;
        try {
            ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, false);
        } catch (KeeperException nne) {
            // check first operation that fails throws exception
            caughtNoNode = true;
        }
        Assert.assertTrue(caughtNoNode);
        // check that no modifications were made
        Assert.assertFalse(((ZKUtil.checkExists(TestZKMulti.zkw, pathX)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathY)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathZ)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathW)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, pathV)) == (-1)));
    }

    @Test
    public void testRunSequentialOnMultiFailure() throws Exception {
        String path1 = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "runSequential1");
        String path2 = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "runSequential2");
        String path3 = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "runSequential3");
        String path4 = ZNodePaths.joinZNode(TestZKMulti.zkw.getZNodePaths().baseZNode, "runSequential4");
        // create some nodes that we will use later
        LinkedList<ZKUtilOp> ops = new LinkedList<>();
        ops.add(ZKUtilOp.createAndFailSilent(path1, Bytes.toBytes(path1)));
        ops.add(ZKUtilOp.createAndFailSilent(path2, Bytes.toBytes(path2)));
        ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, false);
        // test that, even with operations that fail, the ones that would pass will pass
        // with runSequentialOnMultiFailure
        ops = new LinkedList();
        ops.add(ZKUtilOp.setData(path1, Bytes.add(Bytes.toBytes(path1), Bytes.toBytes(path1))));// pass

        ops.add(ZKUtilOp.deleteNodeFailSilent(path2));// pass

        ops.add(ZKUtilOp.deleteNodeFailSilent(path3));// fail -- node doesn't exist

        ops.add(ZKUtilOp.createAndFailSilent(path4, Bytes.add(Bytes.toBytes(path4), Bytes.toBytes(path4))));// pass

        ZKUtil.multiOrSequential(TestZKMulti.zkw, ops, true);
        Assert.assertTrue(Bytes.equals(ZKUtil.getData(TestZKMulti.zkw, path1), Bytes.add(Bytes.toBytes(path1), Bytes.toBytes(path1))));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, path2)) == (-1)));
        Assert.assertTrue(((ZKUtil.checkExists(TestZKMulti.zkw, path3)) == (-1)));
        Assert.assertFalse(((ZKUtil.checkExists(TestZKMulti.zkw, path4)) == (-1)));
    }

    /**
     * Verifies that for the given root node, it should delete all the child nodes
     * recursively using multi-update api.
     */
    @Test
    public void testdeleteChildrenRecursivelyMulti() throws Exception {
        String parentZNode = "/testRootMulti";
        createZNodeTree(parentZNode);
        ZKUtil.deleteChildrenRecursivelyMultiOrSequential(TestZKMulti.zkw, true, parentZNode);
        Assert.assertTrue("Wrongly deleted parent znode!", ((ZKUtil.checkExists(TestZKMulti.zkw, parentZNode)) > (-1)));
        List<String> children = TestZKMulti.zkw.getRecoverableZooKeeper().getChildren(parentZNode, false);
        Assert.assertTrue("Failed to delete child znodes!", (0 == (children.size())));
    }

    /**
     * Verifies that for the given root node, it should delete all the nodes recursively using
     * multi-update api.
     */
    @Test
    public void testDeleteNodeRecursivelyMulti() throws Exception {
        String parentZNode = "/testdeleteNodeRecursivelyMulti";
        createZNodeTree(parentZNode);
        ZKUtil.deleteNodeRecursively(TestZKMulti.zkw, parentZNode);
        Assert.assertTrue("Parent znode should be deleted.", ((ZKUtil.checkExists(TestZKMulti.zkw, parentZNode)) == (-1)));
    }

    @Test
    public void testDeleteNodeRecursivelyMultiOrSequential() throws Exception {
        String parentZNode1 = "/testdeleteNode1";
        String parentZNode2 = "/testdeleteNode2";
        String parentZNode3 = "/testdeleteNode3";
        createZNodeTree(parentZNode1);
        createZNodeTree(parentZNode2);
        createZNodeTree(parentZNode3);
        ZKUtil.deleteNodeRecursivelyMultiOrSequential(TestZKMulti.zkw, false, parentZNode1, parentZNode2, parentZNode3);
        Assert.assertTrue("Parent znode 1 should be deleted.", ((ZKUtil.checkExists(TestZKMulti.zkw, parentZNode1)) == (-1)));
        Assert.assertTrue("Parent znode 2 should be deleted.", ((ZKUtil.checkExists(TestZKMulti.zkw, parentZNode2)) == (-1)));
        Assert.assertTrue("Parent znode 3 should be deleted.", ((ZKUtil.checkExists(TestZKMulti.zkw, parentZNode3)) == (-1)));
    }

    @Test
    public void testDeleteChildrenRecursivelyMultiOrSequential() throws Exception {
        String parentZNode1 = "/testdeleteChildren1";
        String parentZNode2 = "/testdeleteChildren2";
        String parentZNode3 = "/testdeleteChildren3";
        createZNodeTree(parentZNode1);
        createZNodeTree(parentZNode2);
        createZNodeTree(parentZNode3);
        ZKUtil.deleteChildrenRecursivelyMultiOrSequential(TestZKMulti.zkw, true, parentZNode1, parentZNode2, parentZNode3);
        Assert.assertTrue("Wrongly deleted parent znode 1!", ((ZKUtil.checkExists(TestZKMulti.zkw, parentZNode1)) > (-1)));
        List<String> children = TestZKMulti.zkw.getRecoverableZooKeeper().getChildren(parentZNode1, false);
        Assert.assertTrue("Failed to delete child znodes of parent znode 1!", (0 == (children.size())));
        Assert.assertTrue("Wrongly deleted parent znode 2!", ((ZKUtil.checkExists(TestZKMulti.zkw, parentZNode2)) > (-1)));
        children = TestZKMulti.zkw.getRecoverableZooKeeper().getChildren(parentZNode2, false);
        Assert.assertTrue("Failed to delete child znodes of parent znode 1!", (0 == (children.size())));
        Assert.assertTrue("Wrongly deleted parent znode 3!", ((ZKUtil.checkExists(TestZKMulti.zkw, parentZNode3)) > (-1)));
        children = TestZKMulti.zkw.getRecoverableZooKeeper().getChildren(parentZNode3, false);
        Assert.assertTrue("Failed to delete child znodes of parent znode 1!", (0 == (children.size())));
    }
}

