/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.server.router.RendezvousHasher;
import org.junit.Assert;
import org.junit.Test;


public class RendezvousHasherTest {
    private static int NUM_ITERATIONS = 10000;

    private static final Logger log = new Logger(RendezvousHasherTest.class);

    @Test
    public void testBasic() {
        RendezvousHasher hasher = new RendezvousHasher();
        Set<String> nodes = new HashSet<>();
        nodes.add("localhost:1");
        nodes.add("localhost:2");
        nodes.add("localhost:3");
        nodes.add("localhost:4");
        nodes.add("localhost:5");
        Map<String, String> uuidServerMap = new HashMap<>();
        for (int i = 0; i < (RendezvousHasherTest.NUM_ITERATIONS); i++) {
            UUID objectId = UUID.randomUUID();
            String targetServer = hasher.chooseNode(nodes, StringUtils.toUtf8(objectId.toString()));
            uuidServerMap.put(objectId.toString(), targetServer);
        }
        // check that the same UUIDs hash to the same servers on subsequent hashStr() calls
        for (int i = 0; i < 2; i++) {
            for (Map.Entry<String, String> entry : uuidServerMap.entrySet()) {
                String targetServer = hasher.chooseNode(nodes, StringUtils.toUtf8(entry.getKey()));
                Assert.assertEquals(entry.getValue(), targetServer);
            }
        }
    }

    @Test
    public void testAddNode() {
        RendezvousHasher hasher = new RendezvousHasher();
        Set<String> nodes = new HashSet<>();
        nodes.add("localhost:1");
        nodes.add("localhost:2");
        nodes.add("localhost:3");
        nodes.add("localhost:4");
        nodes.add("localhost:5");
        Map<String, String> uuidServerMap = new HashMap<>();
        for (int i = 0; i < (RendezvousHasherTest.NUM_ITERATIONS); i++) {
            UUID objectId = UUID.randomUUID();
            String targetServer = hasher.chooseNode(nodes, StringUtils.toUtf8(objectId.toString()));
            uuidServerMap.put(objectId.toString(), targetServer);
        }
        nodes.add("localhost:6");
        int same = 0;
        int diff = 0;
        for (Map.Entry<String, String> entry : uuidServerMap.entrySet()) {
            String targetServer = hasher.chooseNode(nodes, StringUtils.toUtf8(entry.getKey()));
            if (entry.getValue().equals(targetServer)) {
                same += 1;
            } else {
                diff += 1;
            }
        }
        RendezvousHasherTest.log.info(StringUtils.format("testAddNode Total: %s, Same: %s, Diff: %s", RendezvousHasherTest.NUM_ITERATIONS, same, diff));
        double diffRatio = ((double) (diff)) / (RendezvousHasherTest.NUM_ITERATIONS);
        Assert.assertTrue((diffRatio < 0.33));
    }

    @Test
    public void testRemoveNode() {
        RendezvousHasher hasher = new RendezvousHasher();
        Set<String> nodes = new HashSet<>();
        nodes.add("localhost:1");
        nodes.add("localhost:2");
        nodes.add("localhost:3");
        nodes.add("localhost:4");
        nodes.add("localhost:5");
        Map<String, String> uuidServerMap = new HashMap<>();
        for (int i = 0; i < (RendezvousHasherTest.NUM_ITERATIONS); i++) {
            UUID objectId = UUID.randomUUID();
            String targetServer = hasher.chooseNode(nodes, StringUtils.toUtf8(objectId.toString()));
            uuidServerMap.put(objectId.toString(), targetServer);
        }
        nodes.remove("localhost:3");
        int same = 0;
        int diff = 0;
        for (Map.Entry<String, String> entry : uuidServerMap.entrySet()) {
            String targetServer = hasher.chooseNode(nodes, StringUtils.toUtf8(entry.getKey()));
            if (entry.getValue().equals(targetServer)) {
                same += 1;
            } else {
                diff += 1;
            }
        }
        RendezvousHasherTest.log.info(StringUtils.format("testRemoveNode Total: %s, Same: %s, Diff: %s", RendezvousHasherTest.NUM_ITERATIONS, same, diff));
        double diffRatio = ((double) (diff)) / (RendezvousHasherTest.NUM_ITERATIONS);
        Assert.assertTrue((diffRatio < 0.33));
    }

    @Test
    public void testInconsistentView1() {
        Set<String> nodes = new HashSet<>();
        nodes.add("localhost:1");
        nodes.add("localhost:2");
        nodes.add("localhost:3");
        nodes.add("localhost:4");
        nodes.add("localhost:5");
        Set<String> nodes2 = new HashSet<>();
        nodes2.add("localhost:1");
        nodes2.add("localhost:3");
        nodes2.add("localhost:4");
        nodes2.add("localhost:5");
        testInconsistentViewHelper("testInconsistentView1", nodes, nodes2, 0.33);
    }

    @Test
    public void testInconsistentView2() {
        Set<String> nodes = new HashSet<>();
        nodes.add("localhost:1");
        nodes.add("localhost:3");
        nodes.add("localhost:4");
        nodes.add("localhost:5");
        Set<String> nodes2 = new HashSet<>();
        nodes2.add("localhost:1");
        nodes2.add("localhost:2");
        nodes2.add("localhost:4");
        nodes2.add("localhost:5");
        testInconsistentViewHelper("testInconsistentView2", nodes, nodes2, 0.55);
    }

    @Test
    public void testInconsistentView3() {
        Set<String> nodes = new HashSet<>();
        nodes.add("localhost:3");
        nodes.add("localhost:4");
        nodes.add("localhost:5");
        Set<String> nodes2 = new HashSet<>();
        nodes2.add("localhost:1");
        nodes2.add("localhost:4");
        nodes2.add("localhost:5");
        testInconsistentViewHelper("testInconsistentView3", nodes, nodes2, 0.66);
    }

    @Test
    public void testInconsistentView4() {
        Set<String> nodes = new HashSet<>();
        nodes.add("localhost:2");
        nodes.add("localhost:5");
        Set<String> nodes2 = new HashSet<>();
        nodes2.add("localhost:1");
        nodes2.add("localhost:4");
        nodes2.add("localhost:5");
        testInconsistentViewHelper("testInconsistentView4", nodes, nodes2, 0.95);
    }
}

