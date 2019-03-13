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
package org.apache.hadoop.hbase.monitoring;


import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test case for the MemoryBoundedLogMessageBuffer utility.
 * Ensures that it uses no more memory than it's supposed to,
 * and that it properly deals with multibyte encodings.
 */
@Category({ MiscTests.class, SmallTests.class })
public class TestMemoryBoundedLogMessageBuffer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMemoryBoundedLogMessageBuffer.class);

    private static final long TEN_KB = 10 * 1024;

    private static final String JP_TEXT = "?????";

    @Test
    public void testBuffer() {
        MemoryBoundedLogMessageBuffer buf = new MemoryBoundedLogMessageBuffer(TestMemoryBoundedLogMessageBuffer.TEN_KB);
        for (int i = 0; i < 1000; i++) {
            buf.add(("hello " + i));
        }
        Assert.assertTrue(("Usage too big: " + (buf.estimateHeapUsage())), ((buf.estimateHeapUsage()) < (TestMemoryBoundedLogMessageBuffer.TEN_KB)));
        Assert.assertTrue(("Too many retained: " + (buf.getMessages().size())), ((buf.getMessages().size()) < 100));
        StringWriter sw = new StringWriter();
        buf.dumpTo(new PrintWriter(sw));
        String dump = sw.toString();
        String eol = System.getProperty("line.separator");
        Assert.assertFalse("The early log messages should be evicted", dump.contains(("hello 1" + eol)));
        Assert.assertTrue("The late log messages should be retained", dump.contains(("hello 999" + eol)));
    }

    @Test
    public void testNonAsciiEncoding() {
        MemoryBoundedLogMessageBuffer buf = new MemoryBoundedLogMessageBuffer(TestMemoryBoundedLogMessageBuffer.TEN_KB);
        buf.add(TestMemoryBoundedLogMessageBuffer.JP_TEXT);
        StringWriter sw = new StringWriter();
        buf.dumpTo(new PrintWriter(sw));
        String dump = sw.toString();
        Assert.assertTrue(dump.contains(TestMemoryBoundedLogMessageBuffer.JP_TEXT));
    }
}

