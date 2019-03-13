/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.state.providers;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.components.state.StateMap;
import org.apache.nifi.components.state.StateProvider;
import org.junit.Assert;
import org.junit.Test;


/**
 * <p>
 * Abstract class that provides a suite of test for State Providers. Each State Provider implementation can simply extend this class,
 * implement the getProvider() method, and have the entire suite of tests run against the provider.
 * </p>
 *
 * <p>
 * It is recommended that implementations create a new provider in a method annotated with @Before and cleanup in a method annotated with @After.
 * </p>
 */
public abstract class AbstractTestStateProvider {
    protected final String componentId = "111111111-1111-1111-1111-111111111111";

    @Test
    public void testSetAndGet() throws IOException {
        getProvider().setState(Collections.singletonMap("testSetAndGet", "value"), componentId);
        Assert.assertEquals("value", getProvider().getState(componentId).get("testSetAndGet"));
    }

    @Test
    public void testReplaceSuccessful() throws IOException {
        final String key = "testReplaceSuccessful";
        final StateProvider provider = getProvider();
        StateMap map = provider.getState(componentId);
        Assert.assertNotNull(map);
        Assert.assertEquals((-1), map.getVersion());
        Assert.assertNotNull(map.toMap());
        Assert.assertTrue(map.toMap().isEmpty());
        provider.setState(Collections.singletonMap(key, "value1"), componentId);
        map = provider.getState(componentId);
        Assert.assertNotNull(map);
        Assert.assertEquals(0, map.getVersion());
        Assert.assertEquals("value1", map.get(key));
        Assert.assertEquals("value1", map.toMap().get(key));
        final Map<String, String> newMap = new HashMap(map.toMap());
        newMap.put(key, "value2");
        Assert.assertTrue(provider.replace(map, newMap, componentId));
        map = provider.getState(componentId);
        Assert.assertEquals("value2", map.get(key));
        Assert.assertEquals(1L, map.getVersion());
    }

    @Test
    public void testReplaceWithWrongVersion() throws IOException {
        final String key = "testReplaceWithWrongVersion";
        final StateProvider provider = getProvider();
        provider.setState(Collections.singletonMap(key, "value1"), componentId);
        StateMap stateMap = provider.getState(componentId);
        Assert.assertNotNull(stateMap);
        Assert.assertEquals("value1", stateMap.get(key));
        Assert.assertEquals(0, stateMap.getVersion());
        provider.setState(Collections.singletonMap(key, "intermediate value"), componentId);
        Assert.assertFalse(provider.replace(stateMap, Collections.singletonMap(key, "value2"), componentId));
        stateMap = provider.getState(componentId);
        Assert.assertEquals(key, stateMap.toMap().keySet().iterator().next());
        Assert.assertEquals(1, stateMap.toMap().size());
        Assert.assertEquals("intermediate value", stateMap.get(key));
        Assert.assertEquals(1, stateMap.getVersion());
    }

    @Test
    public void testToMap() throws IOException {
        final String key = "testKeySet";
        final StateProvider provider = getProvider();
        Map<String, String> map = provider.getState(componentId).toMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.isEmpty());
        provider.setState(Collections.singletonMap(key, "value"), componentId);
        map = provider.getState(componentId).toMap();
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("value", map.get(key));
        provider.setState(Collections.<String, String>emptyMap(), componentId);
        final StateMap stateMap = provider.getState(componentId);
        map = stateMap.toMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.isEmpty());
        Assert.assertEquals(1, stateMap.getVersion());
    }

    @Test
    public void testClear() throws IOException {
        final StateProvider provider = getProvider();
        StateMap stateMap = provider.getState(componentId);
        Assert.assertNotNull(stateMap);
        Assert.assertEquals((-1L), stateMap.getVersion());
        Assert.assertTrue(stateMap.toMap().isEmpty());
        provider.setState(Collections.singletonMap("testClear", "value"), componentId);
        stateMap = provider.getState(componentId);
        Assert.assertNotNull(stateMap);
        Assert.assertEquals(0, stateMap.getVersion());
        Assert.assertEquals("value", stateMap.get("testClear"));
        provider.clear(componentId);
        stateMap = provider.getState(componentId);
        Assert.assertNotNull(stateMap);
        Assert.assertEquals(1L, stateMap.getVersion());
        Assert.assertTrue(stateMap.toMap().isEmpty());
    }

    @Test
    public void testReplaceWithNonExistingValue() throws Exception {
        final StateProvider provider = getProvider();
        StateMap stateMap = provider.getState(componentId);
        Assert.assertNotNull(stateMap);
        final Map<String, String> newValue = new HashMap<>();
        newValue.put("value", "value");
        final boolean replaced = provider.replace(stateMap, newValue, componentId);
        Assert.assertFalse(replaced);
    }

    @Test
    public void testReplaceWithNonExistingValueAndVersionGreaterThanNegativeOne() throws Exception {
        final StateProvider provider = getProvider();
        final StateMap stateMap = new StateMap() {
            @Override
            public long getVersion() {
                return 4;
            }

            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public Map<String, String> toMap() {
                return Collections.emptyMap();
            }
        };
        final Map<String, String> newValue = new HashMap<>();
        newValue.put("value", "value");
        final boolean replaced = provider.replace(stateMap, newValue, componentId);
        Assert.assertFalse(replaced);
    }

    @Test
    public void testOnComponentRemoved() throws IOException, InterruptedException {
        final StateProvider provider = getProvider();
        final Map<String, String> newValue = new HashMap<>();
        newValue.put("value", "value");
        provider.setState(newValue, componentId);
        final StateMap stateMap = provider.getState(componentId);
        Assert.assertEquals(0L, stateMap.getVersion());
        provider.onComponentRemoved(componentId);
        // wait for the background process to complete
        Thread.sleep(1000L);
        final StateMap stateMapAfterRemoval = provider.getState(componentId);
        // version should be -1 because the state has been removed entirely.
        Assert.assertEquals((-1L), stateMapAfterRemoval.getVersion());
    }
}

