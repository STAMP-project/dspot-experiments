/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import org.junit.Assert;
import org.junit.Test;


public class PathElementTest {
    private static final PathElement PE_1 = PathElement.of("k1");

    private static final PathElement PE_2 = PathElement.of("k2", "n");

    private static final PathElement PE_3 = PathElement.of("k3", 1);

    @Test
    public void testKind() throws Exception {
        Assert.assertEquals("k1", PathElementTest.PE_1.getKind());
        Assert.assertEquals("k2", PathElementTest.PE_2.getKind());
        Assert.assertEquals("k3", PathElementTest.PE_3.getKind());
    }

    @Test
    public void testHasId() throws Exception {
        Assert.assertFalse(PathElementTest.PE_1.hasId());
        Assert.assertFalse(PathElementTest.PE_2.hasId());
        Assert.assertTrue(PathElementTest.PE_3.hasId());
    }

    @Test
    public void testId() throws Exception {
        Assert.assertNull(PathElementTest.PE_1.getId());
        Assert.assertNull(PathElementTest.PE_2.getId());
        Assert.assertEquals(Long.valueOf(1), PathElementTest.PE_3.getId());
    }

    @Test
    public void testHasName() throws Exception {
        Assert.assertFalse(PathElementTest.PE_1.hasName());
        Assert.assertTrue(PathElementTest.PE_2.hasName());
        Assert.assertFalse(PathElementTest.PE_3.hasName());
    }

    @Test
    public void testName() throws Exception {
        Assert.assertNull(PathElementTest.PE_1.getName());
        Assert.assertEquals("n", PathElementTest.PE_2.getName());
        Assert.assertNull(PathElementTest.PE_3.getName());
    }

    @Test
    public void testNameOrId() throws Exception {
        Assert.assertNull(PathElementTest.PE_1.getNameOrId());
        Assert.assertEquals("n", PathElementTest.PE_2.getNameOrId());
        Assert.assertEquals(Long.valueOf(1), PathElementTest.PE_3.getNameOrId());
    }
}

