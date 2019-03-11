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
package org.apache.flink.api.common.operators;


import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Future;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.TaskInfo;
import org.apache.flink.api.common.accumulators.Accumulator;
import org.apache.flink.api.common.operators.util.TestIOData;
import org.apache.flink.api.common.operators.util.TestNonRichInputFormat;
import org.apache.flink.api.common.operators.util.TestNonRichOutputFormat;
import org.apache.flink.api.common.operators.util.TestRichOutputFormat;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.core.fs.Path;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.apache.flink.types.Nothing;
import org.junit.Assert;
import org.junit.Test;


/**
 * Checks the GenericDataSinkBase operator for both Rich and non-Rich output formats.
 */
@SuppressWarnings("serial")
public class GenericDataSinkBaseTest implements Serializable {
    private static TestNonRichInputFormat in = new TestNonRichInputFormat();

    GenericDataSourceBase<String, TestNonRichInputFormat> source = new GenericDataSourceBase<String, TestNonRichInputFormat>(GenericDataSinkBaseTest.in, new OperatorInformation<String>(BasicTypeInfo.STRING_TYPE_INFO), "testSource");

    @Test
    public void testDataSourcePlain() {
        try {
            TestNonRichOutputFormat out = new TestNonRichOutputFormat();
            GenericDataSinkBase<String> sink = new GenericDataSinkBase<String>(out, new UnaryOperatorInformation<String, Nothing>(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.getInfoFor(Nothing.class)), "test_sink");
            sink.setInput(source);
            ExecutionConfig executionConfig = new ExecutionConfig();
            executionConfig.disableObjectReuse();
            GenericDataSinkBaseTest.in.reset();
            sink.executeOnCollections(Arrays.asList(TestIOData.NAMES), null, executionConfig);
            Assert.assertEquals(out.output, Arrays.asList(TestIOData.NAMES));
            executionConfig.enableObjectReuse();
            out.clear();
            GenericDataSinkBaseTest.in.reset();
            sink.executeOnCollections(Arrays.asList(TestIOData.NAMES), null, executionConfig);
            Assert.assertEquals(out.output, Arrays.asList(TestIOData.NAMES));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDataSourceWithRuntimeContext() {
        try {
            TestRichOutputFormat out = new TestRichOutputFormat();
            GenericDataSinkBase<String> sink = new GenericDataSinkBase<String>(out, new UnaryOperatorInformation<String, Nothing>(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.getInfoFor(Nothing.class)), "test_sink");
            sink.setInput(source);
            ExecutionConfig executionConfig = new ExecutionConfig();
            final HashMap<String, Accumulator<?, ?>> accumulatorMap = new HashMap<String, Accumulator<?, ?>>();
            final HashMap<String, Future<Path>> cpTasks = new HashMap<>();
            final TaskInfo taskInfo = new TaskInfo("test_sink", 1, 0, 1, 0);
            executionConfig.disableObjectReuse();
            GenericDataSinkBaseTest.in.reset();
            sink.executeOnCollections(Arrays.asList(TestIOData.NAMES), new org.apache.flink.api.common.functions.util.RuntimeUDFContext(taskInfo, null, executionConfig, cpTasks, accumulatorMap, new UnregisteredMetricsGroup()), executionConfig);
            Assert.assertEquals(out.output, Arrays.asList(TestIOData.RICH_NAMES));
            executionConfig.enableObjectReuse();
            out.clear();
            GenericDataSinkBaseTest.in.reset();
            sink.executeOnCollections(Arrays.asList(TestIOData.NAMES), new org.apache.flink.api.common.functions.util.RuntimeUDFContext(taskInfo, null, executionConfig, cpTasks, accumulatorMap, new UnregisteredMetricsGroup()), executionConfig);
            Assert.assertEquals(out.output, Arrays.asList(TestIOData.RICH_NAMES));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

