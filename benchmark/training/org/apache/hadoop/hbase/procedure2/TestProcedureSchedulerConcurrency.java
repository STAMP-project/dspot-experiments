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


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestProcedureSchedulerConcurrency {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureSchedulerConcurrency.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureEvents.class);

    private SimpleProcedureScheduler procSched;

    @Test
    public void testConcurrentWaitWake() throws Exception {
        testConcurrentWaitWake(false);
    }

    @Test
    public void testConcurrentWaitWakeBatch() throws Exception {
        testConcurrentWaitWake(true);
    }

    public static class TestProcedureWithEvent extends ProcedureTestingUtility.NoopProcedure<Void> {
        private final ProcedureEvent event;

        public TestProcedureWithEvent(long procId) {
            setProcId(procId);
            event = new ProcedureEvent(("test-event procId=" + procId));
        }

        public ProcedureEvent getEvent() {
            return event;
        }
    }
}

