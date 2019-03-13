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
package org.apache.beam.runners.dataflow.worker.status;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static java.lang.Thread.State.NEW;


/**
 * Tests for {@link Threadzservlet}.
 */
@RunWith(JUnit4.class)
public class ThreadzServletTest {
    @Test
    public void testDeduping() throws Exception {
        Map<Thread, StackTraceElement[]> stacks = ImmutableMap.of(new Thread("Thread1"), new StackTraceElement[]{ new StackTraceElement("Class", "Method1", "File", 11) }, new Thread("Thread2"), new StackTraceElement[]{ new StackTraceElement("Class", "Method1", "File", 11) }, new Thread("Thread3"), new StackTraceElement[]{ new StackTraceElement("Class", "Method2", "File", 17) });
        Map<ThreadzServlet.Stack, List<String>> deduped = ThreadzServlet.deduplicateThreadStacks(stacks);
        Assert.assertEquals(2, deduped.size());
        Assert.assertThat(deduped, Matchers.hasEntry(new ThreadzServlet.Stack(new StackTraceElement[]{ new StackTraceElement("Class", "Method1", "File", 11) }, NEW), Arrays.asList("Thread[Thread1,5,main]", "Thread[Thread2,5,main]")));
        Assert.assertThat(deduped, Matchers.hasEntry(new ThreadzServlet.Stack(new StackTraceElement[]{ new StackTraceElement("Class", "Method2", "File", 17) }, NEW), Arrays.asList("Thread[Thread3,5,main]")));
    }
}

