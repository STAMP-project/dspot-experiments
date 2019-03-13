/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core;


import org.junit.Assert;
import org.junit.Test;


public class MapIdFieldTest {
    private MapIdField mapIdField;

    @Test
    public void testPut() {
        mapIdField.put("aMapId", "aMapIdValue");
        Assert.assertEquals("aMapIdValue", mapIdField.get("aMapId"));
    }

    @Test
    public void testGet() {
        mapIdField.put(null, "nullMapId");
        mapIdField.put("aMapId", "aMapIdValue");
        Assert.assertEquals("nullMapId", mapIdField.get(null));
        Assert.assertEquals("aMapIdValue", mapIdField.get("aMapId"));
    }

    @Test
    public void testContainsMapId() {
        mapIdField.put("aMapId", "aMapIdValue");
        Assert.assertTrue(mapIdField.containsMapId("aMapId"));
        Assert.assertFalse(mapIdField.containsMapId(null));
    }
}

