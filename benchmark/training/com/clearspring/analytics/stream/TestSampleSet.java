/**
 * Copyright (C) 2011 Clearspring Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */
package com.clearspring.analytics.stream;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestSampleSet {
    private SampleSet<String> set;

    private String[] e;

    @Test
    public void testPeekK() {
        set.put(e[0]);
        for (int i = 0; i < 2; i++) {
            set.put(e[1]);
        }
        for (int i = 0; i < 3; i++) {
            set.put(e[2]);
        }
        List<String> top = null;
        // Negative
        boolean caught = false;
        try {
            top = set.peek((-1));
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        Assert.assertTrue(caught);
        // 0
        top = set.peek(0);
        Assert.assertEquals(0, top.size());
        // 1
        top = set.peek(1);
        Assert.assertEquals(1, top.size());
        Assert.assertEquals(set.peek(), top.get(0));
        // 2 (more than one but less than size)
        top = set.peek(2);
        Assert.assertEquals(2, top.size());
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(e[(2 - i)], top.get(i));
        }
        // 3 (size)
        top = set.peek(3);
        Assert.assertEquals(3, top.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(e[(2 - i)], top.get(i));
        }
        // 4 (more than size)
        top = set.peek(4);
        Assert.assertEquals(3, top.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(e[(2 - i)], top.get(i));
        }
    }

    @Test
    public void testPut() {
        // Empty set
        Assert.assertEquals(1L, set.put(e[0]));
        Assert.assertEquals(e[0], set.peek());
        Assert.assertEquals(e[0], ((SampleSet<String>) (set)).peekMin());
    }

    @Test
    public void testPutWithIncrement() {
        // Empty set
        Assert.assertEquals(10L, set.put(e[0], 10));
        Assert.assertEquals(e[0], set.peek());
        Assert.assertEquals(e[0], ((SampleSet<String>) (set)).peekMin());
    }

    @Test
    public void testRemoveMin() {
        // Empty set
        Assert.assertNull(set.removeMin());
        Assert.assertEquals(0, set.size());
        Assert.assertEquals(0L, set.count());
        // Maintaining order
        set.put(e[0]);
        for (int i = 0; i < 2; i++) {
            set.put(e[1]);
        }
        for (int i = 0; i < 3; i++) {
            set.put(e[2]);
        }
        Assert.assertEquals(3, set.size());
        Assert.assertEquals(6L, set.count());
        Assert.assertEquals(e[0], set.removeMin());
        Assert.assertEquals(2, set.size());
        Assert.assertEquals(5L, set.count());
        Assert.assertEquals(e[1], set.removeMin());
        Assert.assertEquals(1, set.size());
        Assert.assertEquals(3L, set.count());
        Assert.assertEquals(e[2], set.removeMin());
        Assert.assertEquals(0, set.size());
        Assert.assertEquals(0L, set.count());
        Assert.assertEquals(null, set.removeMin());
        Assert.assertEquals(0, set.size());
        Assert.assertEquals(0L, set.count());
    }
}

