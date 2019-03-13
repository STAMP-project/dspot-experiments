/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.internal;


import org.junit.Assert;
import org.junit.Test;


public class FIFOCacheTest {
    @Test
    public void test() {
        FIFOCache<String> cache = new FIFOCache<String>(3);
        Assert.assertTrue(((cache.size()) == 0));
        cache.add("k1", "v1");
        Assert.assertTrue(((cache.size()) == 1));
        cache.add("k1", "v11");
        Assert.assertTrue(((cache.size()) == 1));
        cache.add("k2", "v2");
        Assert.assertTrue(((cache.size()) == 2));
        cache.add("k3", "v3");
        Assert.assertTrue(((cache.size()) == 3));
        Assert.assertEquals("v11", cache.get("k1"));
        Assert.assertEquals("v2", cache.get("k2"));
        Assert.assertEquals("v3", cache.get("k3"));
        cache.add("k4", "v4");
        Assert.assertTrue(((cache.size()) == 3));
        Assert.assertNull(cache.get("k1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroSize() {
        new FIFOCache<Object>(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgument() {
        new FIFOCache<Object>((-1));
    }

    @Test
    public void testSingleEntry() {
        FIFOCache<String> cache = new FIFOCache<String>(1);
        Assert.assertTrue(((cache.size()) == 0));
        cache.add("k1", "v1");
        Assert.assertTrue(((cache.size()) == 1));
        cache.add("k1", "v11");
        Assert.assertTrue(((cache.size()) == 1));
        Assert.assertEquals("v11", cache.get("k1"));
        cache.add("k2", "v2");
        Assert.assertTrue(((cache.size()) == 1));
        Assert.assertEquals("v2", cache.get("k2"));
        Assert.assertNull(cache.get("k1"));
        cache.add("k3", "v3");
        Assert.assertTrue(((cache.size()) == 1));
        Assert.assertEquals("v3", cache.get("k3"));
        Assert.assertNull(cache.get("k2"));
    }
}

