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
package org.apache.zookeeper;


import CreateMode.PERSISTENT;
import EventType.ChildWatchRemoved;
import EventType.DataWatchRemoved;
import EventType.NodeDeleted;
import Ids.OPEN_ACL_UNSAFE;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing remove watches using command line
 */
public class RemoveWatchesCmdTest extends ClientBase {
    private static final Logger LOG = LoggerFactory.getLogger(RemoveWatchesCmdTest.class);

    private ZooKeeper zk;

    private ZooKeeperMain zkMain;

    /**
     * Test verifies default options. When there is no passed options,
     * removewatches command will use default options - WatcherType.ANY and
     * local=false
     */
    @Test(timeout = 30000)
    public void testRemoveWatchesWithNoPassedOptions() throws Exception {
        List<EventType> expectedEvents = new ArrayList<Watcher.Event.EventType>();
        expectedEvents.add(ChildWatchRemoved);
        expectedEvents.add(DataWatchRemoved);
        RemoveWatchesCmdTest.MyWatcher myWatcher = new RemoveWatchesCmdTest.MyWatcher("/testnode1", expectedEvents, 2);
        zk.create("/testnode1", "data".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        zk.create("/testnode2", "data".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        RemoveWatchesCmdTest.LOG.info("Adding childwatcher to /testnode1 and /testnode2");
        zk.getChildren("/testnode1", myWatcher);
        zk.getChildren("/testnode2", myWatcher);
        RemoveWatchesCmdTest.LOG.info("Adding datawatcher to /testnode1 and /testnode2");
        zk.getData("/testnode1", myWatcher, null);
        zk.getData("/testnode2", myWatcher, null);
        String cmdstring = "removewatches /testnode1";
        RemoveWatchesCmdTest.LOG.info("Remove watchers using shell command : {}", cmdstring);
        zkMain.cl.parseCommand(cmdstring);
        Assert.assertTrue("Removewatches cmd fails to remove child watches", zkMain.processZKCmd(zkMain.cl));
        RemoveWatchesCmdTest.LOG.info("Waiting for the DataWatchRemoved event");
        myWatcher.matches();
        // verifying that other path child watches are not affected
        Assert.assertTrue("Failed to find child watches for the path testnode2", zk.getChildWatches().contains("/testnode2"));
        Assert.assertTrue("Failed to find data watches for the path testnode2", zk.getDataWatches().contains("/testnode2"));
    }

    /**
     * Test verifies deletion of NodeDataChanged watches
     */
    @Test(timeout = 30000)
    public void testRemoveNodeDataChangedWatches() throws Exception {
        RemoveWatchesCmdTest.LOG.info("Adding data watcher using getData()");
        List<EventType> expectedEvents = new ArrayList<Watcher.Event.EventType>();
        expectedEvents.add(DataWatchRemoved);
        RemoveWatchesCmdTest.MyWatcher myWatcher = new RemoveWatchesCmdTest.MyWatcher("/testnode1", expectedEvents, 1);
        zk.create("/testnode1", "data".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        zk.getData("/testnode1", myWatcher, null);
        String cmdstring = "removewatches /testnode1 -d";
        RemoveWatchesCmdTest.LOG.info("Remove watchers using shell command : {}", cmdstring);
        zkMain.cl.parseCommand(cmdstring);
        Assert.assertTrue("Removewatches cmd fails to remove data watches", zkMain.processZKCmd(zkMain.cl));
        RemoveWatchesCmdTest.LOG.info("Waiting for the DataWatchRemoved event");
        myWatcher.matches();
        // verifying that other path data watches are removed
        Assert.assertEquals(("Data watches are not removed : " + (zk.getDataWatches())), 0, zk.getDataWatches().size());
    }

    /**
     * Test verifies deletion of NodeCreated data watches
     */
    @Test(timeout = 30000)
    public void testRemoveNodeCreatedWatches() throws Exception {
        List<EventType> expectedEvents = new ArrayList<Watcher.Event.EventType>();
        expectedEvents.add(DataWatchRemoved);
        RemoveWatchesCmdTest.MyWatcher myWatcher1 = new RemoveWatchesCmdTest.MyWatcher("/testnode1", expectedEvents, 1);
        RemoveWatchesCmdTest.MyWatcher myWatcher2 = new RemoveWatchesCmdTest.MyWatcher("/testnode1/testnode2", expectedEvents, 1);
        // Adding pre-created watcher
        RemoveWatchesCmdTest.LOG.info("Adding NodeCreated watcher");
        zk.exists("/testnode1", myWatcher1);
        zk.exists("/testnode1/testnode2", myWatcher2);
        String cmdstring1 = "removewatches /testnode1 -d";
        RemoveWatchesCmdTest.LOG.info("Remove watchers using shell command : {}", cmdstring1);
        zkMain.cl.parseCommand(cmdstring1);
        Assert.assertTrue("Removewatches cmd fails to remove pre-create watches", zkMain.processZKCmd(zkMain.cl));
        myWatcher1.matches();
        Assert.assertEquals(("Failed to remove pre-create watches :" + (zk.getExistWatches())), 1, zk.getExistWatches().size());
        Assert.assertTrue(("Failed to remove pre-create watches :" + (zk.getExistWatches())), zk.getExistWatches().contains("/testnode1/testnode2"));
        String cmdstring2 = "removewatches /testnode1/testnode2 -d";
        RemoveWatchesCmdTest.LOG.info("Remove watchers using shell command : {}", cmdstring2);
        zkMain.cl.parseCommand(cmdstring2);
        Assert.assertTrue("Removewatches cmd fails to remove data watches", zkMain.processZKCmd(zkMain.cl));
        myWatcher2.matches();
        Assert.assertEquals(("Failed to remove pre-create watches : " + (zk.getExistWatches())), 0, zk.getExistWatches().size());
    }

    /**
     * Test verifies deletion of NodeChildrenChanged watches
     */
    @Test(timeout = 30000)
    public void testRemoveNodeChildrenChangedWatches() throws Exception {
        List<EventType> expectedEvents = new ArrayList<Watcher.Event.EventType>();
        expectedEvents.add(ChildWatchRemoved);
        RemoveWatchesCmdTest.MyWatcher myWatcher = new RemoveWatchesCmdTest.MyWatcher("/testnode1", expectedEvents, 1);
        zk.create("/testnode1", "data".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        RemoveWatchesCmdTest.LOG.info("Adding child changed watcher");
        zk.getChildren("/testnode1", myWatcher);
        String cmdstring = "removewatches /testnode1 -c";
        RemoveWatchesCmdTest.LOG.info("Remove watchers using shell command : {}", cmdstring);
        zkMain.cl.parseCommand(cmdstring);
        Assert.assertTrue("Removewatches cmd fails to remove child watches", zkMain.processZKCmd(zkMain.cl));
        myWatcher.matches();
        Assert.assertEquals(("Failed to remove child watches : " + (zk.getChildWatches())), 0, zk.getChildWatches().size());
    }

    /**
     * Test verifies deletion of NodeDeleted watches
     */
    @Test(timeout = 30000)
    public void testRemoveNodeDeletedWatches() throws Exception {
        RemoveWatchesCmdTest.LOG.info("Adding NodeDeleted watcher");
        List<EventType> expectedEvents = new ArrayList<Watcher.Event.EventType>();
        expectedEvents.add(ChildWatchRemoved);
        expectedEvents.add(NodeDeleted);
        RemoveWatchesCmdTest.MyWatcher myWatcher = new RemoveWatchesCmdTest.MyWatcher("/testnode1", expectedEvents, 1);
        zk.create("/testnode1", "data".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        zk.create("/testnode1/testnode2", "data".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        zk.getChildren("/testnode1/testnode2", myWatcher);
        zk.getChildren("/testnode1", myWatcher);
        String cmdstring = "removewatches /testnode1 -c";
        RemoveWatchesCmdTest.LOG.info("Remove watchers using shell command : {}", cmdstring);
        zkMain.cl.parseCommand(cmdstring);
        Assert.assertTrue("Removewatches cmd fails to remove child watches", zkMain.processZKCmd(zkMain.cl));
        RemoveWatchesCmdTest.LOG.info("Waiting for the ChildWatchRemoved event");
        myWatcher.matches();
        Assert.assertEquals(("Failed to remove child watches : " + (zk.getChildWatches())), 1, zk.getChildWatches().size());
        Assert.assertTrue(("Failed to remove child watches :" + (zk.getChildWatches())), zk.getChildWatches().contains("/testnode1/testnode2"));
        // verify node delete watcher
        zk.delete("/testnode1/testnode2", (-1));
        myWatcher.matches();
    }

    /**
     * Test verifies deletion of any watches
     */
    @Test(timeout = 30000)
    public void testRemoveAnyWatches() throws Exception {
        verifyRemoveAnyWatches(false);
    }

    /**
     * Test verifies deletion of watches locally when there is no server
     * connection
     */
    @Test(timeout = 30000)
    public void testRemoveWatchesLocallyWhenNoServerConnection() throws Exception {
        verifyRemoveAnyWatches(true);
    }

    private class MyWatcher implements Watcher {
        private final String path;

        private String eventPath;

        private final CountDownLatch latch;

        private final List<EventType> expectedEvents = new ArrayList<EventType>();

        public MyWatcher(String path, List<EventType> expectedEvents, int count) {
            this.path = path;
            this.latch = new CountDownLatch(count);
            this.expectedEvents.addAll(expectedEvents);
        }

        public void process(WatchedEvent event) {
            RemoveWatchesCmdTest.LOG.debug(("Event path : {}, eventPath : {}" + new Object[]{ path, event.getPath() }));
            this.eventPath = event.getPath();
            if (expectedEvents.contains(event.getType())) {
                latch.countDown();
            }
        }

        public boolean matches() throws InterruptedException {
            if (!(latch.await(((ClientBase.CONNECTION_TIMEOUT) / 3), TimeUnit.MILLISECONDS))) {
                RemoveWatchesCmdTest.LOG.error("Failed to get watch notifications!");
                return false;
            }
            RemoveWatchesCmdTest.LOG.debug("Client path : {} eventPath : {}", new Object[]{ path, eventPath });
            return path.equals(eventPath);
        }
    }
}

