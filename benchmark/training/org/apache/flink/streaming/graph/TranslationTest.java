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
package org.apache.flink.streaming.graph;


import CheckpointingMode.AT_LEAST_ONCE;
import CheckpointingMode.EXACTLY_ONCE;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test translation of {@link CheckpointingMode}.
 */
@SuppressWarnings("serial")
public class TranslationTest {
    @Test
    public void testCheckpointModeTranslation() {
        try {
            // with deactivated fault tolerance, the checkpoint mode should be at-least-once
            StreamExecutionEnvironment deactivated = TranslationTest.getSimpleJob();
            for (JobVertex vertex : deactivated.getStreamGraph().getJobGraph().getVertices()) {
                Assert.assertEquals(AT_LEAST_ONCE, getCheckpointMode());
            }
            // with activated fault tolerance, the checkpoint mode should be by default exactly once
            StreamExecutionEnvironment activated = TranslationTest.getSimpleJob();
            activated.enableCheckpointing(1000L);
            for (JobVertex vertex : activated.getStreamGraph().getJobGraph().getVertices()) {
                Assert.assertEquals(EXACTLY_ONCE, getCheckpointMode());
            }
            // explicitly setting the mode
            StreamExecutionEnvironment explicit = TranslationTest.getSimpleJob();
            explicit.enableCheckpointing(1000L, AT_LEAST_ONCE);
            for (JobVertex vertex : explicit.getStreamGraph().getJobGraph().getVertices()) {
                Assert.assertEquals(AT_LEAST_ONCE, getCheckpointMode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

