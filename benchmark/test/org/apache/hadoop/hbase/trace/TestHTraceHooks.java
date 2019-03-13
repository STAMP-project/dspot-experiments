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
package org.apache.hadoop.hbase.trace;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hbase.thirdparty.com.google.common.collect.Sets;
import org.apache.htrace.core.POJOSpanReceiver;
import org.apache.htrace.core.Span;
import org.apache.htrace.core.TraceScope;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


// We don't support htrace in hbase-2.0.0 and this flakey is a little flakey.
@Ignore
@Category({ MiscTests.class, MediumTests.class })
public class TestHTraceHooks {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHTraceHooks.class);

    private static final byte[] FAMILY_BYTES = "family".getBytes();

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static POJOSpanReceiver rcvr;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testTraceCreateTable() throws Exception {
        Table table;
        Span createTableSpan;
        try (TraceScope scope = TraceUtil.createTrace("creating table")) {
            createTableSpan = scope.getSpan();
            table = TestHTraceHooks.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestHTraceHooks.FAMILY_BYTES);
        }
        // Some table creation is async.  Need to make sure that everything is full in before
        // checking to see if the spans are there.
        TestHTraceHooks.TEST_UTIL.waitFor(10000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (TestHTraceHooks.rcvr) == null ? true : (TestHTraceHooks.rcvr.getSpans().size()) >= 5;
            }
        });
        Collection<Span> spans = Sets.newHashSet(TestHTraceHooks.rcvr.getSpans());
        List<Span> roots = new LinkedList<>();
        TraceTree traceTree = new TraceTree(spans);
        roots.addAll(traceTree.getSpansByParent().find(createTableSpan.getSpanId()));
        // Roots was made 3 in hbase2. It used to be 1. We changed it back to 1 on upgrade to
        // htrace-4.2 just to get the test to pass (traces are not wholesome in hbase2; TODO).
        Assert.assertEquals(1, roots.size());
        Assert.assertEquals("creating table", createTableSpan.getDescription());
        if (spans != null) {
            Assert.assertTrue(((spans.size()) > 5));
        }
        Put put = new Put("row".getBytes());
        put.addColumn(TestHTraceHooks.FAMILY_BYTES, "col".getBytes(), "value".getBytes());
        Span putSpan;
        try (TraceScope scope = TraceUtil.createTrace("doing put")) {
            putSpan = scope.getSpan();
            table.put(put);
        }
        spans = TestHTraceHooks.rcvr.getSpans();
        traceTree = new TraceTree(spans);
        roots.clear();
        roots.addAll(traceTree.getSpansByParent().find(putSpan.getSpanId()));
        Assert.assertEquals(1, roots.size());
    }
}

