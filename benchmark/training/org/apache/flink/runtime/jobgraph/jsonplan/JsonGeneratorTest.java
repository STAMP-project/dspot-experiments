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
package org.apache.flink.runtime.jobgraph.jsonplan;


import DistributionPattern.ALL_TO_ALL;
import DistributionPattern.POINTWISE;
import ResultPartitionType.BLOCKING;
import ResultPartitionType.PIPELINED;
import java.util.Iterator;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.operators.testutils.DummyInvokable;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class JsonGeneratorTest {
    @Test
    public void testGeneratorWithoutAnyAttachements() {
        try {
            JobVertex source1 = new JobVertex("source 1");
            JobVertex source2 = new JobVertex("source 2");
            source2.setInvokableClass(DummyInvokable.class);
            JobVertex source3 = new JobVertex("source 3");
            JobVertex intermediate1 = new JobVertex("intermediate 1");
            JobVertex intermediate2 = new JobVertex("intermediate 2");
            JobVertex join1 = new JobVertex("join 1");
            JobVertex join2 = new JobVertex("join 2");
            JobVertex sink1 = new JobVertex("sink 1");
            JobVertex sink2 = new JobVertex("sink 2");
            intermediate1.connectNewDataSetAsInput(source1, POINTWISE, PIPELINED);
            intermediate2.connectNewDataSetAsInput(source2, ALL_TO_ALL, PIPELINED);
            join1.connectNewDataSetAsInput(intermediate1, POINTWISE, BLOCKING);
            join1.connectNewDataSetAsInput(intermediate2, ALL_TO_ALL, BLOCKING);
            join2.connectNewDataSetAsInput(join1, POINTWISE, PIPELINED);
            join2.connectNewDataSetAsInput(source3, POINTWISE, BLOCKING);
            sink1.connectNewDataSetAsInput(join2, POINTWISE, PIPELINED);
            sink2.connectNewDataSetAsInput(join1, ALL_TO_ALL, PIPELINED);
            JobGraph jg = new JobGraph("my job", source1, source2, source3, intermediate1, intermediate2, join1, join2, sink1, sink2);
            String plan = JsonPlanGenerator.generatePlan(jg);
            Assert.assertNotNull(plan);
            // validate the produced JSON
            ObjectMapper m = new ObjectMapper();
            JsonNode rootNode = m.readTree(plan);
            // core fields
            Assert.assertEquals(new org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.TextNode(jg.getJobID().toString()), rootNode.get("jid"));
            Assert.assertEquals(new org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.TextNode(jg.getName()), rootNode.get("name"));
            Assert.assertTrue(rootNode.path("nodes").isArray());
            for (Iterator<JsonNode> iter = rootNode.path("nodes").elements(); iter.hasNext();) {
                JsonNode next = iter.next();
                JsonNode idNode = next.get("id");
                Assert.assertNotNull(idNode);
                Assert.assertTrue(idNode.isTextual());
                checkVertexExists(idNode.asText(), jg);
                String description = next.get("description").asText();
                Assert.assertTrue(((((description.startsWith("source")) || (description.startsWith("sink"))) || (description.startsWith("intermediate"))) || (description.startsWith("join"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

