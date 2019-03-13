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


public class SimpleRegistryWrapTest extends Assert {
    private SimpleRegistry registry = new SimpleRegistry() {
        @Override
        public Object wrap(Object value) {
            return "wrap" + value;
        }

        @Override
        public Object unwrap(Object value) {
            return "unwrap" + value;
        }
    };

    @Test
    public void testLookupByName() {
        Assert.assertEquals("unwrapwrap123", registry.lookupByName("a"));
    }

    @Test
    public void testLookupByNameAndType() {
        Assert.assertEquals("unwrapwrap456", registry.lookupByNameAndType("b", String.class));
    }

    @Test
    public void testLookupByType() {
        Map<?, ?> map = registry.findByTypeWithName(String.class);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("unwrapwrap123", map.get("a"));
        Assert.assertEquals("unwrapwrap456", map.get("b"));
    }
}

