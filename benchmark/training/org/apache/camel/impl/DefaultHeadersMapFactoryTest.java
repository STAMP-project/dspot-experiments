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


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DefaultHeadersMapFactoryTest extends Assert {
    @Test
    public void testLookupCaseAgnostic() {
        Map<String, Object> map = new DefaultHeadersMapFactory().newMap();
        Assert.assertNull(map.get("foo"));
        map.put("foo", "cheese");
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cheese", map.get("Foo"));
        Assert.assertEquals("cheese", map.get("FOO"));
    }

    @Test
    public void testConstructFromOther() {
        Map<String, Object> other = new DefaultHeadersMapFactory().newMap();
        other.put("Foo", "cheese");
        other.put("bar", 123);
        Map<String, Object> map = new DefaultHeadersMapFactory().newMap(other);
        Assert.assertEquals("cheese", map.get("FOO"));
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cheese", map.get("Foo"));
        Assert.assertEquals(123, map.get("BAR"));
        Assert.assertEquals(123, map.get("bar"));
        Assert.assertEquals(123, map.get("BaR"));
    }

    @Test
    public void testIsInstance() {
        Map<String, Object> map = new DefaultHeadersMapFactory().newMap();
        Map<String, Object> other = new DefaultHeadersMapFactory().newMap(map);
        other.put("Foo", "cheese");
        other.put("bar", 123);
        Assert.assertTrue(new DefaultHeadersMapFactory().isInstanceOf(map));
        Assert.assertTrue(new DefaultHeadersMapFactory().isInstanceOf(other));
        Assert.assertFalse(new DefaultHeadersMapFactory().isInstanceOf(new HashMap()));
    }
}

