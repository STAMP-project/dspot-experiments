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
package org.apache.flink.runtime.executiongraph;


import ExecutionConfig.GlobalJobParameters;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.util.TestLogger;
import org.junit.Test;


/**
 * Tests for the {@link ArchivedExecutionGraph}.
 */
public class ArchivedExecutionGraphTest extends TestLogger {
    private static ExecutionGraph runtimeGraph;

    @Test
    public void testArchive() throws IOException, ClassNotFoundException {
        ArchivedExecutionGraph archivedGraph = ArchivedExecutionGraph.createFrom(ArchivedExecutionGraphTest.runtimeGraph);
        ArchivedExecutionGraphTest.compareExecutionGraph(ArchivedExecutionGraphTest.runtimeGraph, archivedGraph);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        ArchivedExecutionGraph archivedGraph = ArchivedExecutionGraph.createFrom(ArchivedExecutionGraphTest.runtimeGraph);
        ArchivedExecutionGraphTest.verifySerializability(archivedGraph);
    }

    private static class TestJobParameters extends ExecutionConfig.GlobalJobParameters {
        private static final long serialVersionUID = -8118611781035212808L;

        private Map<String, String> parameters;

        private TestJobParameters() {
            this.parameters = new HashMap<>();
            this.parameters.put("hello", "world");
        }

        @Override
        public Map<String, String> toMap() {
            return parameters;
        }
    }
}

