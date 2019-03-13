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
package org.apache.geode.cache.lucene.internal.partition;


import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class BucketTargetingMapTest {
    private BucketRegion region;

    @Test
    public void getUsesCallbackArg() {
        final BucketTargetingMap map = new BucketTargetingMap(region, 1);
        Mockito.when(region.get(ArgumentMatchers.eq("key"), ArgumentMatchers.eq(1))).thenReturn("value");
        Assert.assertEquals("value", map.get("key"));
    }

    @Test
    public void putIfAbsentUsesCallbackArg() {
        final BucketTargetingMap map = new BucketTargetingMap(region, 1);
        map.putIfAbsent("key", "value");
        Mockito.verify(region).create(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.eq(1));
    }

    @Test
    public void containsKeyUsesCallbackArg() {
        final BucketTargetingMap map = new BucketTargetingMap(region, 1);
        Mockito.when(region.get(ArgumentMatchers.eq("key"), ArgumentMatchers.eq(1))).thenReturn("value");
        Assert.assertEquals(true, map.containsKey("key"));
        Assert.assertEquals(false, map.containsKey("none"));
        Mockito.verify(region).get(ArgumentMatchers.eq("none"), ArgumentMatchers.eq(1));
    }
}

