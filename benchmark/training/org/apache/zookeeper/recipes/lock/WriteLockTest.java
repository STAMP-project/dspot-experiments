/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.recipes.lock;


import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Test;


/**
 * test for writelock
 */
public class WriteLockTest extends ClientBase {
    protected int sessionTimeout = 10 * 1000;

    protected String dir = "/" + (getClass().getName());

    protected WriteLock[] nodes;

    protected CountDownLatch latch = new CountDownLatch(1);

    private boolean restartServer = true;

    private boolean workAroundClosingLastZNodeFails = true;

    private boolean killLeader = true;

    @Test
    public void testRun() throws Exception {
        runTest(3);
    }

    class LockCallback implements LockListener {
        public void lockAcquired() {
            latch.countDown();
        }

        public void lockReleased() {
        }
    }
}

