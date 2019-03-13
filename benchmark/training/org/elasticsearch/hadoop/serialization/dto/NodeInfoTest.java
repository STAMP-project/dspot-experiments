/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization.dto;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class NodeInfoTest {
    @Test
    public void testV1() throws Exception {
        Map<String, NodeInfo> nodeMap = NodeInfoTest.testNodeInfo(getClass().getResourceAsStream("client-nodes-v1.json"));
        Assert.assertFalse(nodeMap.get("Darkhawk").isIngest());
        Assert.assertFalse(nodeMap.get("Unseen").isIngest());
    }

    @Test
    public void testV2() throws Exception {
        Map<String, NodeInfo> nodeMap = NodeInfoTest.testNodeInfo(getClass().getResourceAsStream("client-nodes-v2.json"));
        Assert.assertFalse(nodeMap.get("Darkhawk").isIngest());
        Assert.assertFalse(nodeMap.get("Unseen").isIngest());
    }

    @Test
    public void testV5() throws Exception {
        Map<String, NodeInfo> nodeMap = NodeInfoTest.testNodeInfo(getClass().getResourceAsStream("client-nodes-v5.json"));
        Assert.assertFalse(nodeMap.get("Darkhawk").isIngest());
        Assert.assertTrue(nodeMap.get("Unseen").isIngest());
    }
}

