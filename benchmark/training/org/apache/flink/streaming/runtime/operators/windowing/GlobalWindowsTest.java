/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.runtime.operators.windowing;


import GlobalWindows.NeverTrigger;
import WindowAssigner.WindowAssignerContext;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link GlobalWindows}.
 */
public class GlobalWindowsTest extends TestLogger {
    @Test
    public void testWindowAssignment() {
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        GlobalWindows assigner = GlobalWindows.create();
        Assert.assertThat(assigner.assignWindows("String", 0L, mockContext), Matchers.contains(GlobalWindow.get()));
        Assert.assertThat(assigner.assignWindows("String", 4999L, mockContext), Matchers.contains(GlobalWindow.get()));
        Assert.assertThat(assigner.assignWindows("String", 5000L, mockContext), Matchers.contains(GlobalWindow.get()));
    }

    @Test
    public void testProperties() {
        GlobalWindows assigner = GlobalWindows.create();
        Assert.assertFalse(assigner.isEventTime());
        Assert.assertEquals(new GlobalWindow.Serializer(), assigner.getWindowSerializer(new ExecutionConfig()));
        Assert.assertThat(assigner.getDefaultTrigger(Mockito.mock(StreamExecutionEnvironment.class)), Matchers.instanceOf(NeverTrigger.class));
    }
}

