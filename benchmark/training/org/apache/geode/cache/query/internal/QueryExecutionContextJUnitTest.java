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
package org.apache.geode.cache.query.internal;


import org.junit.Assert;
import org.junit.Test;


public class QueryExecutionContextJUnitTest {
    @Test
    public void testNullReturnedFromCacheGetWhenNoValueWasPut() {
        Object key = new Object();
        QueryExecutionContext context = new QueryExecutionContext(null, null);
        Assert.assertNull(context.cacheGet(key));
    }

    @Test
    public void testPutValueReturnedFromCacheGet() {
        Object key = new Object();
        Object value = new Object();
        QueryExecutionContext context = new QueryExecutionContext(null, null);
        context.cachePut(key, value);
        Assert.assertEquals(value, context.cacheGet(key));
    }

    @Test
    public void testDefaultReturnedFromCacheGetWhenNoValueWasPut() {
        Object key = new Object();
        Object value = new Object();
        QueryExecutionContext context = new QueryExecutionContext(null, null);
        Assert.assertEquals(value, context.cacheGet(key, value));
    }

    @Test
    public void testExecCachesCanBePushedAndValuesRetrievedAtTheCorrectLevel() {
        Object key = new Object();
        Object value = new Object();
        QueryExecutionContext context = new QueryExecutionContext(null, null);
        context.pushExecCache(1);
        context.cachePut(key, value);
        context.pushExecCache(2);
        Assert.assertNull(context.cacheGet(key));
        context.popExecCache();
        Assert.assertEquals(value, context.cacheGet(key));
    }
}

