/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.impl;


import java.util.Map;
import org.apache.camel.support.SimpleRegistry;
import org.junit.Assert;
import org.junit.Test;


public class SimpleRegistryTest extends Assert {
    private SimpleRegistry registry;

    @Test
    public void testLookupByName() {
        Assert.assertEquals("b", registry.lookupByName("a"));
    }

    @Test
    public void testLookupByWrongName() {
        Assert.assertNull(registry.lookupByName("x"));
    }

    @Test
    public void testLookupByNameAndType() {
        Assert.assertEquals("b", registry.lookupByNameAndType("a", String.class));
    }

    @Test
    public void testLookupByNameAndWrongType() {
        Object answer = registry.lookupByNameAndType("a", Float.class);
        Assert.assertNull(answer);
    }

    @Test
    public void testLookupByType() {
        Map<?, ?> map = registry.findByTypeWithName(String.class);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("b", map.get("a"));
        map = registry.findByTypeWithName(Object.class);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("b", map.get("a"));
        Assert.assertEquals(1, map.get("c"));
    }

    @Test
    public void testLookupByWrongType() {
        Map<?, ?> map = registry.findByTypeWithName(Float.class);
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void testBindDual() {
        String foo = "foo";
        // bind a 2nd c but its a different type
        registry.bind("c", foo);
        Assert.assertEquals(2, registry.size());
        // should return the original entry if no specific type given
        Assert.assertSame(1, registry.lookupByName("c"));
        Assert.assertSame(1, registry.lookupByNameAndType("c", Integer.class));
        // should return the string type
        Assert.assertSame("foo", registry.lookupByNameAndType("c", String.class));
        Map<String, Integer> map = registry.findByTypeWithName(Integer.class);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(Integer.valueOf(1), map.get("c"));
        Map<String, String> map2 = registry.findByTypeWithName(String.class);
        Assert.assertEquals(2, map2.size());
        Assert.assertEquals("foo", map2.get("c"));
        Assert.assertEquals("b", map2.get("a"));
    }
}

