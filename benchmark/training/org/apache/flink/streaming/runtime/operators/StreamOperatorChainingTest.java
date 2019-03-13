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
package org.apache.flink.streaming.runtime.operators;


import java.util.List;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Test;


/**
 * Tests for stream operator chaining behaviour.
 */
@SuppressWarnings("serial")
public class StreamOperatorChainingTest {
    // We have to use static fields because the sink functions will go through serialization
    private static List<String> sink1Results;

    private static List<String> sink2Results;

    private static List<String> sink3Results;

    @Test
    public void testMultiChainingWithObjectReuse() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().enableObjectReuse();
        testMultiChaining(env);
    }

    @Test
    public void testMultiChainingWithoutObjectReuse() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().disableObjectReuse();
        testMultiChaining(env);
    }

    @Test
    public void testMultiChainingWithSplitWithObjectReuse() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().enableObjectReuse();
        testMultiChainingWithSplit(env);
    }

    @Test
    public void testMultiChainingWithSplitWithoutObjectReuse() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().disableObjectReuse();
        testMultiChainingWithSplit(env);
    }
}

