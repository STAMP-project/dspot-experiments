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
package org.apache.hadoop.hbase.master.assignment;


import org.apache.hadoop.hbase.CallQueueTooBigException;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestAMServerFailedOpen extends TestAssignmentManagerBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAMServerFailedOpen.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestAMServerFailedOpen.class);

    @Test
    public void testServerNotYetRunning() throws Exception {
        testRetriesExhaustedFailure(TableName.valueOf(this.name.getMethodName()), new TestAssignmentManagerBase.ServerNotYetRunningRsExecutor());
    }

    @Test
    public void testDoNotRetryExceptionOnAssignment() throws Exception {
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        testFailedOpen(TableName.valueOf("testDoNotRetryExceptionOnAssignment"), new TestAssignmentManagerBase.FaultyRsExecutor(new DoNotRetryIOException("test do not retry fault")));
        Assert.assertEquals(((assignSubmittedCount) + 1), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(((assignFailedCount) + 1), assignProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testCallQueueTooBigExceptionOnAssignment() throws Exception {
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        testFailedOpen(TableName.valueOf("testCallQueueTooBigExceptionOnAssignment"), new TestAssignmentManagerBase.FaultyRsExecutor(new CallQueueTooBigException("test do not retry fault")));
        Assert.assertEquals(((assignSubmittedCount) + 1), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(((assignFailedCount) + 1), assignProcMetrics.getFailedCounter().getCount());
    }
}

