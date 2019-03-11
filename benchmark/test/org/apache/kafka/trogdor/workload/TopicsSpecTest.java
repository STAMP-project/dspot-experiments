/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.trogdor.workload;


import JsonUtil.JSON_SERDE;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class TopicsSpecTest {
    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    private static final TopicsSpec FOO;

    private static final PartitionsSpec PARTSA;

    private static final PartitionsSpec PARTSB;

    static {
        FOO = new TopicsSpec();
        PARTSA = new PartitionsSpec(3, ((short) (3)), null, null);
        TopicsSpecTest.FOO.set("topicA[0-2]", TopicsSpecTest.PARTSA);
        Map<Integer, List<Integer>> assignmentsB = new HashMap<>();
        assignmentsB.put(0, Arrays.asList(0, 1, 2));
        assignmentsB.put(1, Arrays.asList(2, 3, 4));
        PARTSB = new PartitionsSpec(0, ((short) (0)), assignmentsB, null);
        TopicsSpecTest.FOO.set("topicB", TopicsSpecTest.PARTSB);
    }

    @Test
    public void testMaterialize() {
        Map<String, PartitionsSpec> parts = TopicsSpecTest.FOO.materialize();
        Assert.assertTrue(parts.containsKey("topicA0"));
        Assert.assertTrue(parts.containsKey("topicA1"));
        Assert.assertTrue(parts.containsKey("topicA2"));
        Assert.assertTrue(parts.containsKey("topicB"));
        Assert.assertEquals(4, parts.keySet().size());
        Assert.assertEquals(TopicsSpecTest.PARTSA, parts.get("topicA0"));
        Assert.assertEquals(TopicsSpecTest.PARTSA, parts.get("topicA1"));
        Assert.assertEquals(TopicsSpecTest.PARTSA, parts.get("topicA2"));
        Assert.assertEquals(TopicsSpecTest.PARTSB, parts.get("topicB"));
    }

    @Test
    public void testPartitionNumbers() {
        List<Integer> partsANumbers = TopicsSpecTest.PARTSA.partitionNumbers();
        Assert.assertEquals(Integer.valueOf(0), partsANumbers.get(0));
        Assert.assertEquals(Integer.valueOf(1), partsANumbers.get(1));
        Assert.assertEquals(Integer.valueOf(2), partsANumbers.get(2));
        Assert.assertEquals(3, partsANumbers.size());
        List<Integer> partsBNumbers = TopicsSpecTest.PARTSB.partitionNumbers();
        Assert.assertEquals(Integer.valueOf(0), partsBNumbers.get(0));
        Assert.assertEquals(Integer.valueOf(1), partsBNumbers.get(1));
        Assert.assertEquals(2, partsBNumbers.size());
    }

    @Test
    public void testPartitionsSpec() throws Exception {
        String text = "{\"numPartitions\": 5, \"configs\": {\"foo\": \"bar\"}}";
        PartitionsSpec spec = JSON_SERDE.readValue(text, PartitionsSpec.class);
        Assert.assertEquals(5, spec.numPartitions());
        Assert.assertEquals("bar", spec.configs().get("foo"));
        Assert.assertEquals(1, spec.configs().size());
    }
}

