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
package org.apache.zookeeper.test;


import CreateMode.PERSISTENT;
import Ids.OPEN_ACL_UNSAFE;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.common.Time;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientHammerTest extends ClientBase {
    protected static final Logger LOG = LoggerFactory.getLogger(ClientHammerTest.class);

    private static final long HAMMERTHREAD_LATENCY = 5;

    private abstract static class HammerThread extends Thread {
        protected final int count;

        protected volatile int current = 0;

        HammerThread(String name, int count) {
            super(name);
            this.count = count;
        }
    }

    private static class BasicHammerThread extends ClientHammerTest.HammerThread {
        private final ZooKeeper zk;

        private final String prefix;

        BasicHammerThread(String name, ZooKeeper zk, String prefix, int count) {
            super(name, count);
            this.zk = zk;
            this.prefix = prefix;
        }

        public void run() {
            byte[] b = new byte[256];
            try {
                for (; (current) < (count); (current)++) {
                    // Simulate a bit of network latency...
                    Thread.sleep(ClientHammerTest.HAMMERTHREAD_LATENCY);
                    zk.create(((prefix) + (current)), b, OPEN_ACL_UNSAFE, PERSISTENT);
                }
            } catch (Throwable t) {
                ClientHammerTest.LOG.error("Client create operation Assert.failed", t);
            } finally {
                try {
                    zk.close();
                } catch (InterruptedException e) {
                    ClientHammerTest.LOG.warn("Unexpected", e);
                }
            }
        }
    }

    private static class SuperHammerThread extends ClientHammerTest.HammerThread {
        private final ClientHammerTest parent;

        private final String prefix;

        SuperHammerThread(String name, ClientHammerTest parent, String prefix, int count) {
            super(name, count);
            this.parent = parent;
            this.prefix = prefix;
        }

        public void run() {
            byte[] b = new byte[256];
            try {
                for (; (current) < (count); (current)++) {
                    ZooKeeper zk = parent.createClient();
                    try {
                        zk.create(((prefix) + (current)), b, OPEN_ACL_UNSAFE, PERSISTENT);
                    } finally {
                        try {
                            zk.close();
                        } catch (InterruptedException e) {
                            ClientHammerTest.LOG.warn("Unexpected", e);
                        }
                    }
                }
            } catch (Throwable t) {
                ClientHammerTest.LOG.error("Client create operation Assert.failed", t);
            }
        }
    }

    /**
     * Separate threads each creating a number of nodes. Each thread
     * is using a non-shared (owned by thread) client for all node creations.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testHammerBasic() throws Throwable {
        runHammer(10, 1000);
    }

    /**
     * Separate threads each creating a number of nodes. Each thread
     * is creating a new client for each node creation.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testHammerSuper() throws Throwable {
        try {
            final int threadCount = 5;
            final int childCount = 10;
            ClientHammerTest.HammerThread[] threads = new ClientHammerTest.HammerThread[threadCount];
            long start = Time.currentElapsedTime();
            for (int i = 0; i < (threads.length); i++) {
                String prefix = "/test-" + i;
                {
                    ZooKeeper zk = createClient();
                    try {
                        zk.create(prefix, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
                    } finally {
                        zk.close();
                    }
                }
                prefix += "/";
                ClientHammerTest.HammerThread thread = new ClientHammerTest.SuperHammerThread(("SuperHammerThread-" + i), this, prefix, childCount);
                thread.start();
                threads[i] = thread;
            }
            verifyHammer(start, threads, childCount);
        } catch (Throwable t) {
            ClientHammerTest.LOG.error("test Assert.failed", t);
            throw t;
        }
    }
}

