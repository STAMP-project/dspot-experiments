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
package org.apache.ambari.server.api.services;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * NamedPropertySet unit tests.
 */
public class NamedPropertySetTest {
    @Test
    public void testGetters() {
        Map<String, Object> mapProps = new HashMap<>();
        mapProps.put("foo", "bar");
        NamedPropertySet propertySet = new NamedPropertySet("foo", mapProps);
        Assert.assertEquals("foo", propertySet.getName());
        Assert.assertEquals(mapProps, propertySet.getProperties());
    }

    @Test
    public void testEquals() {
        Map<String, Object> mapProps = new HashMap<>();
        mapProps.put("foo", "bar");
        NamedPropertySet propertySet = new NamedPropertySet("foo", mapProps);
        NamedPropertySet propertySet2 = new NamedPropertySet("foo", mapProps);
        Assert.assertEquals(propertySet, propertySet2);
        NamedPropertySet propertySet3 = new NamedPropertySet("bar", mapProps);
        Assert.assertFalse(propertySet.equals(propertySet3));
        NamedPropertySet propertySet4 = new NamedPropertySet("foo", new HashMap());
        Assert.assertFalse(propertySet.equals(propertySet4));
    }

    @Test
    public void testHashCode() {
        Map<String, Object> mapProps = new HashMap<>();
        NamedPropertySet propertySet = new NamedPropertySet("foo", mapProps);
        NamedPropertySet propertySet2 = new NamedPropertySet("foo", mapProps);
        Assert.assertEquals(propertySet.hashCode(), propertySet2.hashCode());
    }
}

