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
package org.apache.geode.internal.cache.control;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


public class FilterByPathJUnitTest {
    @Test
    public void testDefault() {
        FilterByPath filter = new FilterByPath(null, null);
        Assert.assertTrue(filter.include(createRegion("a")));
        Assert.assertTrue(filter.include(createRegion("b")));
        Assert.assertTrue(filter.include(createRegion("c")));
    }

    @Test
    public void testInclude() {
        HashSet<String> included = new HashSet<String>();
        included.add("a");
        included.add("b");
        FilterByPath filter = new FilterByPath(included, null);
        Assert.assertTrue(filter.include(createRegion("a")));
        Assert.assertTrue(filter.include(createRegion("b")));
        Assert.assertFalse(filter.include(createRegion("c")));
    }

    @Test
    public void testExclude() {
        HashSet<String> excluded = new HashSet<String>();
        excluded.add("a");
        excluded.add("b");
        FilterByPath filter = new FilterByPath(null, excluded);
        Assert.assertFalse(filter.include(createRegion("a")));
        Assert.assertFalse(filter.include(createRegion("b")));
        Assert.assertTrue(filter.include(createRegion("c")));
    }

    @Test
    public void testBoth() {
        HashSet<String> included = new HashSet<String>();
        included.add("a");
        included.add("b");
        HashSet<String> excluded = new HashSet<String>();
        excluded.add("a");
        excluded.add("b");
        FilterByPath filter = new FilterByPath(included, excluded);
        Assert.assertTrue(filter.include(createRegion("a")));
        Assert.assertTrue(filter.include(createRegion("b")));
        Assert.assertFalse(filter.include(createRegion("c")));
    }

    private static class RegionHandler implements InvocationHandler {
        private String name;

        public RegionHandler(String name) {
            this.name = "/" + name;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return name;
        }
    }
}

