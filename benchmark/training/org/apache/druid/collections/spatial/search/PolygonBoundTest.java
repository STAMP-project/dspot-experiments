/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.collections.spatial.search;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class PolygonBoundTest {
    @Test
    public void testCacheKey() {
        Assert.assertArrayEquals(PolygonBound.from(new float[]{ 1.0F, 2.0F, 3.0F }, new float[]{ 0.0F, 2.0F, 0.0F }, 1).getCacheKey(), PolygonBound.from(new float[]{ 1.0F, 2.0F, 3.0F }, new float[]{ 0.0F, 2.0F, 0.0F }, 1).getCacheKey());
        Assert.assertFalse(Arrays.equals(PolygonBound.from(new float[]{ 1.0F, 2.0F, 3.0F }, new float[]{ 0.0F, 2.0F, 0.0F }, 1).getCacheKey(), PolygonBound.from(new float[]{ 1.0F, 2.0F, 3.0F }, new float[]{ 0.0F, 2.0F, 1.0F }, 1).getCacheKey()));
        Assert.assertFalse(Arrays.equals(PolygonBound.from(new float[]{ 1.0F, 2.0F, 3.0F }, new float[]{ 0.0F, 2.0F, 0.0F }, 1).getCacheKey(), PolygonBound.from(new float[]{ 1.0F, 2.0F, 2.0F }, new float[]{ 0.0F, 2.0F, 0.0F }, 1).getCacheKey()));
        Assert.assertFalse(Arrays.equals(PolygonBound.from(new float[]{ 1.0F, 2.0F, 3.0F }, new float[]{ 0.0F, 2.0F, 0.0F }, 1).getCacheKey(), PolygonBound.from(new float[]{ 1.0F, 2.0F, 3.0F }, new float[]{ 0.0F, 2.0F, 0.0F }, 2).getCacheKey()));
    }

    @Test
    public void testContains() {
        final PolygonBound triangle = PolygonBound.from(new float[]{ 1.0F, 4.0F, 7.0F }, new float[]{ 1.0F, 4.0F, 1.0F });
        final float delta = 1.0E-5F;
        Assert.assertTrue(triangle.contains(new float[]{ 1.0F, 1.0F }));
        Assert.assertFalse(triangle.contains(new float[]{ 1.0F, 1.0F - delta }));
        Assert.assertFalse(triangle.contains(new float[]{ 1.0F, 1.0F + delta }));
        Assert.assertTrue(triangle.contains(new float[]{ 1.0F + delta, 1.0F }));
        Assert.assertFalse(triangle.contains(new float[]{ 1.0F - delta, 1.0F }));
        Assert.assertTrue(triangle.contains(new float[]{ 1.0F + delta, 1.0F }));
        Assert.assertFalse(triangle.contains(new float[]{ 1.0F - delta, 1.0F }));
        Assert.assertTrue(triangle.contains(new float[]{ 5.0F, 1.0F }));
        Assert.assertFalse(triangle.contains(new float[]{ 1.0F, 5.0F }));
        Assert.assertTrue(triangle.contains(new float[]{ 3.0F, 2.0F }));
        final PolygonBound rightTriangle = PolygonBound.from(new float[]{ 1.0F, 1.0F, 5.0F }, new float[]{ 1.0F, 5.0F, 1.0F });
        Assert.assertTrue(rightTriangle.contains(new float[]{ 1.0F, 5.0F }));
        Assert.assertTrue(rightTriangle.contains(new float[]{ 2.0F, 4.0F }));
        Assert.assertTrue(rightTriangle.contains(new float[]{ 2.0F - delta, 4.0F }));
        Assert.assertFalse(rightTriangle.contains(new float[]{ 2.0F + delta, 4.0F }));
        Assert.assertTrue(rightTriangle.contains(new float[]{ 2.0F, 4.0F - delta }));
        Assert.assertFalse(rightTriangle.contains(new float[]{ 2.0F, 4.0F + delta }));
        Assert.assertTrue(rightTriangle.contains(new float[]{ 3.0F - delta, 3.0F }));
        Assert.assertFalse(rightTriangle.contains(new float[]{ 3.0F + delta, 3.0F }));
        Assert.assertTrue(rightTriangle.contains(new float[]{ 3.0F, 3.0F - delta }));
        Assert.assertFalse(rightTriangle.contains(new float[]{ 3.0F, 3.0F + delta }));
    }
}

