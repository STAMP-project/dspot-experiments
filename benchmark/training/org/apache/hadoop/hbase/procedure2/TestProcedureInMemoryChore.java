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
package org.apache.hadoop.hbase.procedure2;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.store.NoopProcedureStore;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureInMemoryChore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureInMemoryChore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureInMemoryChore.class);

    private static final int PROCEDURE_EXECUTOR_SLOTS = 1;

    private TestProcedureInMemoryChore.TestProcEnv procEnv;

    private NoopProcedureStore procStore;

    private ProcedureExecutor<TestProcedureInMemoryChore.TestProcEnv> procExecutor;

    private HBaseCommonTestingUtility htu;

    @Test
    public void testChoreAddAndRemove() throws Exception {
        final int timeoutMSec = 50;
        final int nCountDown = 5;
        // submit the chore and wait for execution
        CountDownLatch latch = new CountDownLatch(nCountDown);
        TestProcedureInMemoryChore.TestLatchChore chore = new TestProcedureInMemoryChore.TestLatchChore(timeoutMSec, latch);
        procExecutor.addChore(chore);
        Assert.assertTrue(isWaiting());
        latch.await();
        // remove the chore and verify it is no longer executed
        Assert.assertTrue(isWaiting());
        procExecutor.removeChore(chore);
        latch = new CountDownLatch(nCountDown);
        chore.setLatch(latch);
        latch.await((timeoutMSec * nCountDown), TimeUnit.MILLISECONDS);
        TestProcedureInMemoryChore.LOG.info(("chore latch count=" + (latch.getCount())));
        Assert.assertFalse(isWaiting());
        Assert.assertTrue(("latchCount=" + (latch.getCount())), ((latch.getCount()) > 0));
    }

    public static class TestLatchChore extends ProcedureInMemoryChore<TestProcedureInMemoryChore.TestProcEnv> {
        private CountDownLatch latch;

        public TestLatchChore(final int timeoutMSec, final CountDownLatch latch) {
            super(timeoutMSec);
            setLatch(latch);
        }

        public void setLatch(final CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        protected void periodicExecute(final TestProcedureInMemoryChore.TestProcEnv env) {
            TestProcedureInMemoryChore.LOG.info(("periodic execute " + (this)));
            latch.countDown();
        }
    }

    private static class TestProcEnv {}
}

