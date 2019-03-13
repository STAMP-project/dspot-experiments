/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal;


import Token.INVALID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.geode.cache.operations.PutAllOperationContext;
import org.apache.geode.internal.cache.CachedDeserializable;
import org.apache.geode.internal.cache.CachedDeserializableFactory;
import org.junit.Assert;
import org.junit.Test;


public class PutAllOperationContextJUnitTest {
    @Test
    public void testIllegalMapMods() {
        LinkedHashMap<String, String> m = new LinkedHashMap<>();
        m.put("1", "1");
        m.put("2", "2");
        m.put("3", "3");
        PutAllOperationContext paoc = new PutAllOperationContext(m);
        Map<String, String> opMap = paoc.getMap();
        try {
            paoc.setMap(null);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            opMap.remove("1");
            Assert.fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        try {
            opMap.put("4", "4");
            Assert.fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        {
            // change order and make sure paoc map order is unchanged
            LinkedHashMap<String, String> m2 = new LinkedHashMap<>();
            m2.put("1", "1");
            try {
                paoc.setMap(m2);
                Assert.fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
        }
        {
            // change order and make sure paoc map order is unchanged
            LinkedHashMap<String, String> m2 = new LinkedHashMap<>();
            try {
                paoc.setMap(m2);
                Assert.fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
        }
        {
            // change order and make sure paoc map order is unchanged
            LinkedHashMap<String, String> m2 = new LinkedHashMap<>();
            m2.put("4", "4");
            m2.put("1", "1");
            m2.put("2", "2");
            try {
                paoc.setMap(m2);
                Assert.fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
            }
        }
    }

    /**
     * Make sure that we do not expose the internal Token.INVALID to customers
     */
    @Test
    public void testInvalidToken() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<>();
        m.put("INVALID_TOKEN", INVALID);
        PutAllOperationContext paoc = new PutAllOperationContext(m);
        Map<String, Object> opMap = paoc.getMap();
        Assert.assertEquals(1, opMap.size());
        Assert.assertEquals(true, opMap.containsKey("INVALID_TOKEN"));
        Assert.assertEquals(null, opMap.get("INVALID_TOKEN"));
        Assert.assertEquals(true, opMap.containsValue(null));
        Assert.assertEquals(false, opMap.containsValue("junk"));
        Collection<Object> values = opMap.values();
        Assert.assertEquals(1, values.size());
        Assert.assertEquals(null, values.iterator().next());
        Set<Map.Entry<String, Object>> entries = opMap.entrySet();
        Assert.assertEquals(1, entries.size());
        Map.Entry me = entries.iterator().next();
        Assert.assertEquals("INVALID_TOKEN", me.getKey());
        Assert.assertEquals(null, me.getValue());
        Assert.assertEquals(INVALID, m.get("INVALID_TOKEN"));
    }

    /**
     * Make sure that we do not expose the internal CachedDeserializable to customers
     */
    @Test
    public void testCachedDeserializable() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<>();
        Object v = Integer.valueOf(99);
        CachedDeserializable cd = CachedDeserializableFactory.create(v, 24, null);
        m.put("cd", cd);
        PutAllOperationContext paoc = new PutAllOperationContext(m);
        Map<String, Object> opMap = paoc.getMap();
        Assert.assertEquals(1, opMap.size());
        Assert.assertEquals(true, opMap.containsKey("cd"));
        Assert.assertEquals(v, opMap.get("cd"));
        Assert.assertEquals(true, opMap.containsValue(v));
        Assert.assertEquals(false, opMap.containsValue("junk"));
        Collection<Object> values = opMap.values();
        Assert.assertEquals(1, values.size());
        Assert.assertEquals(v, values.iterator().next());
        Set<Map.Entry<String, Object>> entries = opMap.entrySet();
        Assert.assertEquals(1, entries.size());
        Map.Entry me = entries.iterator().next();
        Assert.assertEquals("cd", me.getKey());
        Assert.assertEquals(v, me.getValue());
        Assert.assertEquals(cd, m.get("cd"));
        String opMapStr = opMap.toString();
        Assert.assertEquals((("expected " + opMapStr) + " to not contain CachedDeserializable"), false, opMapStr.contains("CachedDeserializable"));
        HashMap<String, Object> hm = new HashMap<>(opMap);
        Assert.assertEquals(hm, opMap);
        Assert.assertEquals(opMap, hm);
        Assert.assertEquals(hm.hashCode(), opMap.hashCode());
    }

    @Test
    public void testLegalMapMods() {
        LinkedHashMap<String, String> m = new LinkedHashMap<>();
        m.put("1", "1");
        m.put("2", "2");
        m.put("3", "3");
        PutAllOperationContext paoc = new PutAllOperationContext(m);
        Map<String, String> opMap = paoc.getMap();
        Assert.assertEquals(m, opMap);
        {
            // change order and make sure paoc map order is unchanged
            LinkedHashMap<String, String> m2 = new LinkedHashMap<>();
            m2.put("3", "3");
            m2.put("1", "1");
            m2.put("2", "2");
            paoc.setMap(m2);
            Assert.assertEquals(Arrays.asList("1", "2", "3"), new ArrayList<>(opMap.keySet()));
            Assert.assertEquals(m, opMap);
        }
        Assert.assertEquals(false, opMap.isEmpty());
        Assert.assertEquals(3, opMap.size());
        Assert.assertEquals(true, opMap.containsKey("1"));
        Assert.assertEquals(false, opMap.containsKey("4"));
        Assert.assertEquals("1", opMap.get("1"));
        Assert.assertEquals(Arrays.asList("1", "2", "3"), new ArrayList<String>(opMap.values()));
        opMap.put("1", "1b");
        opMap.put("2", "2b");
        opMap.put("3", "3b");
        m = new LinkedHashMap<>();
        m.put("1", "1b");
        m.put("2", "2b");
        m.put("3", "3b");
        Assert.assertEquals(m, opMap);
        m.put("2", "2c");
        paoc.setMap(m);
        Assert.assertEquals(m, opMap);
        for (Map.Entry<String, String> me : opMap.entrySet()) {
            if (me.getKey().equals("1")) {
                me.setValue("1d");
            }
        }
        m.put("1", "1d");
        Assert.assertEquals(m, opMap);
        paoc.setMap(opMap);
        // check that none of updates changed to key order
        Assert.assertEquals(Arrays.asList("1", "2", "3"), new ArrayList<>(opMap.keySet()));
        opMap.toString();
    }
}

