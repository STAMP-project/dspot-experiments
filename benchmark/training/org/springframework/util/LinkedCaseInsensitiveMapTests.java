/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.util;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class LinkedCaseInsensitiveMapTests {
    private final LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap();

    @Test
    public void putAndGet() {
        Assert.assertNull(map.put("key", "value1"));
        Assert.assertEquals("value1", map.put("key", "value2"));
        Assert.assertEquals("value2", map.put("key", "value3"));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("value3", map.get("key"));
        Assert.assertEquals("value3", map.get("KEY"));
        Assert.assertEquals("value3", map.get("Key"));
        Assert.assertTrue(map.containsKey("key"));
        Assert.assertTrue(map.containsKey("KEY"));
        Assert.assertTrue(map.containsKey("Key"));
        Assert.assertTrue(map.keySet().contains("key"));
        Assert.assertTrue(map.keySet().contains("KEY"));
        Assert.assertTrue(map.keySet().contains("Key"));
    }

    @Test
    public void putWithOverlappingKeys() {
        Assert.assertNull(map.put("key", "value1"));
        Assert.assertEquals("value1", map.put("KEY", "value2"));
        Assert.assertEquals("value2", map.put("Key", "value3"));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("value3", map.get("key"));
        Assert.assertEquals("value3", map.get("KEY"));
        Assert.assertEquals("value3", map.get("Key"));
        Assert.assertTrue(map.containsKey("key"));
        Assert.assertTrue(map.containsKey("KEY"));
        Assert.assertTrue(map.containsKey("Key"));
        Assert.assertTrue(map.keySet().contains("key"));
        Assert.assertTrue(map.keySet().contains("KEY"));
        Assert.assertTrue(map.keySet().contains("Key"));
    }

    @Test
    public void getOrDefault() {
        Assert.assertNull(map.put("key", "value1"));
        Assert.assertEquals("value1", map.put("KEY", "value2"));
        Assert.assertEquals("value2", map.put("Key", "value3"));
        Assert.assertEquals("value3", map.getOrDefault("key", "N"));
        Assert.assertEquals("value3", map.getOrDefault("KEY", "N"));
        Assert.assertEquals("value3", map.getOrDefault("Key", "N"));
        Assert.assertEquals("N", map.getOrDefault("keeeey", "N"));
        Assert.assertEquals("N", map.getOrDefault(new Object(), "N"));
    }

    @Test
    public void getOrDefaultWithNullValue() {
        Assert.assertNull(map.put("key", null));
        Assert.assertNull(map.put("KEY", null));
        Assert.assertNull(map.put("Key", null));
        Assert.assertNull(map.getOrDefault("key", "N"));
        Assert.assertNull(map.getOrDefault("KEY", "N"));
        Assert.assertNull(map.getOrDefault("Key", "N"));
        Assert.assertEquals("N", map.getOrDefault("keeeey", "N"));
        Assert.assertEquals("N", map.getOrDefault(new Object(), "N"));
    }

    @Test
    public void computeIfAbsentWithExistingValue() {
        Assert.assertNull(map.putIfAbsent("key", "value1"));
        Assert.assertEquals("value1", map.putIfAbsent("KEY", "value2"));
        Assert.assertEquals("value1", map.put("Key", "value3"));
        Assert.assertEquals("value3", map.computeIfAbsent("key", ( key) -> "value1"));
        Assert.assertEquals("value3", map.computeIfAbsent("KEY", ( key) -> "value2"));
        Assert.assertEquals("value3", map.computeIfAbsent("Key", ( key) -> "value3"));
    }

    @Test
    public void computeIfAbsentWithComputedValue() {
        Assert.assertEquals("value1", map.computeIfAbsent("key", ( key) -> "value1"));
        Assert.assertEquals("value1", map.computeIfAbsent("KEY", ( key) -> "value2"));
        Assert.assertEquals("value1", map.computeIfAbsent("Key", ( key) -> "value3"));
    }

    @Test
    public void mapClone() {
        Assert.assertNull(map.put("key", "value1"));
        LinkedCaseInsensitiveMap<String> copy = map.clone();
        Assert.assertEquals(map.getLocale(), copy.getLocale());
        Assert.assertEquals("value1", map.get("key"));
        Assert.assertEquals("value1", map.get("KEY"));
        Assert.assertEquals("value1", map.get("Key"));
        Assert.assertEquals("value1", copy.get("key"));
        Assert.assertEquals("value1", copy.get("KEY"));
        Assert.assertEquals("value1", copy.get("Key"));
        copy.put("Key", "value2");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(1, copy.size());
        Assert.assertEquals("value1", map.get("key"));
        Assert.assertEquals("value1", map.get("KEY"));
        Assert.assertEquals("value1", map.get("Key"));
        Assert.assertEquals("value2", copy.get("key"));
        Assert.assertEquals("value2", copy.get("KEY"));
        Assert.assertEquals("value2", copy.get("Key"));
    }
}

