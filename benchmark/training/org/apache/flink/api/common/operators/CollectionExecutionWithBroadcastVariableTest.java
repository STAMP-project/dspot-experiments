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


import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.functions.RichCrossFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link CollectionExecutor} with broadcast variables.
 */
@SuppressWarnings("serial")
public class CollectionExecutionWithBroadcastVariableTest {
    private static final String BC_VAR_NAME = "BC";

    private static final String[] TEST_DATA = new String[]{ "A", "B", "C", "D" };

    private static final String SUFFIX = "-suffixed";

    @Test
    public void testUnaryOp() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.createCollectionsEnvironment();
            DataSet<String> bcData = env.fromElements(CollectionExecutionWithBroadcastVariableTest.SUFFIX);
            List<String> result = new ArrayList<String>();
            env.fromElements(CollectionExecutionWithBroadcastVariableTest.TEST_DATA).map(new CollectionExecutionWithBroadcastVariableTest.SuffixAppender()).withBroadcastSet(bcData, CollectionExecutionWithBroadcastVariableTest.BC_VAR_NAME).output(new org.apache.flink.api.java.io.LocalCollectionOutputFormat<String>(result));
            env.execute();
            Assert.assertEquals(CollectionExecutionWithBroadcastVariableTest.TEST_DATA.length, result.size());
            for (String s : result) {
                Assert.assertTrue(((s.indexOf(CollectionExecutionWithBroadcastVariableTest.SUFFIX)) > 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testBinaryOp() {
        try {
            ExecutionEnvironment env = ExecutionEnvironment.createCollectionsEnvironment();
            DataSet<String> bcData = env.fromElements(CollectionExecutionWithBroadcastVariableTest.SUFFIX);
            DataSet<String> inData = env.fromElements(CollectionExecutionWithBroadcastVariableTest.TEST_DATA);
            List<String> result = new ArrayList<String>();
            inData.cross(inData).with(new CollectionExecutionWithBroadcastVariableTest.SuffixCross()).withBroadcastSet(bcData, CollectionExecutionWithBroadcastVariableTest.BC_VAR_NAME).output(new org.apache.flink.api.java.io.LocalCollectionOutputFormat<String>(result));
            env.execute();
            Assert.assertEquals(((CollectionExecutionWithBroadcastVariableTest.TEST_DATA.length) * (CollectionExecutionWithBroadcastVariableTest.TEST_DATA.length)), result.size());
            for (String s : result) {
                Assert.assertTrue(((s.indexOf(CollectionExecutionWithBroadcastVariableTest.SUFFIX)) == 2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private static final class SuffixAppender extends RichMapFunction<String, String> {
        private String suffix;

        @Override
        public void open(Configuration parameters) {
            suffix = getRuntimeContext().<String>getBroadcastVariable(CollectionExecutionWithBroadcastVariableTest.BC_VAR_NAME).get(0);
        }

        @Override
        public String map(String value) {
            return value + (suffix);
        }
    }

    private static final class SuffixCross extends RichCrossFunction<String, String, String> {
        private String suffix;

        @Override
        public void open(Configuration parameters) {
            suffix = getRuntimeContext().<String>getBroadcastVariable(CollectionExecutionWithBroadcastVariableTest.BC_VAR_NAME).get(0);
        }

        @Override
        public String cross(String s1, String s2) {
            return (s1 + s2) + (suffix);
        }
    }
}

