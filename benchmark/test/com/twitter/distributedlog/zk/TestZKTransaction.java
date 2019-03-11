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
package com.twitter.distributedlog.zk;


import KeeperException.Code.CONNECTIONLOSS;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.exceptions.DLIllegalStateException;
import java.util.concurrent.CountDownLatch;
import javax.annotation.Nullable;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.OpResult;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test Case for zookeeper transaction
 */
public class TestZKTransaction {
    static class CountDownZKOp extends ZKOp {
        final CountDownLatch commitLatch;

        final CountDownLatch abortLatch;

        CountDownZKOp(CountDownLatch commitLatch, CountDownLatch abortLatch) {
            super(Mockito.mock(Op.class));
            this.commitLatch = commitLatch;
            this.abortLatch = abortLatch;
        }

        @Override
        protected void commitOpResult(OpResult opResult) {
            this.commitLatch.countDown();
        }

        @Override
        protected void abortOpResult(Throwable t, @Nullable
        OpResult opResult) {
            this.abortLatch.countDown();
        }
    }

    @Test(timeout = 60000)
    public void testProcessNullResults() throws Exception {
        ZooKeeperClient zkc = Mockito.mock(ZooKeeperClient.class);
        ZKTransaction transaction = new ZKTransaction(zkc);
        int numOps = 3;
        final CountDownLatch commitLatch = new CountDownLatch(numOps);
        final CountDownLatch abortLatch = new CountDownLatch(numOps);
        for (int i = 0; i < numOps; i++) {
            transaction.addOp(new TestZKTransaction.CountDownZKOp(commitLatch, abortLatch));
        }
        transaction.processResult(CONNECTIONLOSS.intValue(), "test-path", null, null);
        abortLatch.await();
        Assert.assertEquals(0, abortLatch.getCount());
        Assert.assertEquals(numOps, commitLatch.getCount());
    }

    @Test(timeout = 60000)
    public void testAbortTransaction() throws Exception {
        ZooKeeperClient zkc = Mockito.mock(ZooKeeperClient.class);
        ZKTransaction transaction = new ZKTransaction(zkc);
        int numOps = 3;
        final CountDownLatch commitLatch = new CountDownLatch(numOps);
        final CountDownLatch abortLatch = new CountDownLatch(numOps);
        for (int i = 0; i < numOps; i++) {
            transaction.addOp(new TestZKTransaction.CountDownZKOp(commitLatch, abortLatch));
        }
        transaction.abort(new DLIllegalStateException("Illegal State"));
        abortLatch.await();
        Assert.assertEquals(0, abortLatch.getCount());
        Assert.assertEquals(numOps, commitLatch.getCount());
    }
}

