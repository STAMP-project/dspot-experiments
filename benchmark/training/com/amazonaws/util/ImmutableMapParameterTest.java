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
package com.amazonaws.util;


import ImmutableMapParameter.Builder;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the ImmutableMapParameterTest class.
 */
public class ImmutableMapParameterTest {
    @Test
    public void testMapBuilder() {
        Map<Integer, String> builtMap = new Builder<Integer, String>().put(1, "one").put(2, "two").put(3, "three").build();
        Assert.assertEquals(3, builtMap.size());
        Assert.assertEquals("one", builtMap.get(1));
        Assert.assertEquals("two", builtMap.get(2));
        Assert.assertEquals("three", builtMap.get(3));
    }

    @Test
    public void testOfBuilder() {
        Map<Integer, String> builtMap = ImmutableMapParameter.of(1, "one");
        Assert.assertEquals(1, builtMap.size());
        Assert.assertEquals("one", builtMap.get(1));
        builtMap = ImmutableMapParameter.of(1, "one", 2, "two");
        Assert.assertEquals(2, builtMap.size());
        Assert.assertEquals("one", builtMap.get(1));
        Assert.assertEquals("two", builtMap.get(2));
        builtMap = ImmutableMapParameter.of(1, "one", 2, "two", 3, "three");
        Assert.assertEquals(3, builtMap.size());
        Assert.assertEquals("one", builtMap.get(1));
        Assert.assertEquals("two", builtMap.get(2));
        Assert.assertEquals("three", builtMap.get(3));
        builtMap = ImmutableMapParameter.of(1, "one", 2, "two", 3, "three", 4, "four");
        Assert.assertEquals(4, builtMap.size());
        Assert.assertEquals("one", builtMap.get(1));
        Assert.assertEquals("two", builtMap.get(2));
        Assert.assertEquals("three", builtMap.get(3));
        Assert.assertEquals("four", builtMap.get(4));
        builtMap = ImmutableMapParameter.of(1, "one", 2, "two", 3, "three", 4, "four", 5, "five");
        Assert.assertEquals(5, builtMap.size());
        Assert.assertEquals("one", builtMap.get(1));
        Assert.assertEquals("two", builtMap.get(2));
        Assert.assertEquals("three", builtMap.get(3));
        Assert.assertEquals("four", builtMap.get(4));
        Assert.assertEquals("five", builtMap.get(5));
    }

    @Test
    public void testErrorOnDuplicateKeys() {
        try {
            Map<Integer, String> builtMap = new Builder<Integer, String>().put(1, "one").put(1, "two").build();
            Assert.fail("IllegalArgumentException expected.");
        } catch (IllegalArgumentException iae) {
        } catch (Exception e) {
            Assert.fail("IllegalArgumentException expected.");
        }
    }

    @Test
    public void testMapOperations() {
        Map<Integer, String> builtMap = new Builder<Integer, String>().put(1, "one").put(2, "two").put(3, "three").build();
        Assert.assertTrue(builtMap.containsKey(1));
        Assert.assertTrue(builtMap.containsValue("one"));
        Assert.assertTrue(builtMap.values().contains("one"));
        Assert.assertEquals("one", builtMap.get(1));
        Assert.assertEquals(3, builtMap.entrySet().size());
        Assert.assertEquals(3, builtMap.values().size());
        Assert.assertEquals(3, builtMap.size());
        /**
         * Unsupported methods *
         */
        try {
            builtMap.clear();
            Assert.fail("UnsupportedOperationException expected.");
        } catch (UnsupportedOperationException iae) {
        } catch (Exception e) {
            Assert.fail("UnsupportedOperationException expected.");
        }
        try {
            builtMap.put(4, "four");
            Assert.fail("UnsupportedOperationException expected.");
        } catch (UnsupportedOperationException iae) {
        } catch (Exception e) {
            Assert.fail("UnsupportedOperationException expected.");
        }
        try {
            builtMap.putAll(Collections.singletonMap(4, "four"));
            Assert.fail("UnsupportedOperationException expected.");
        } catch (UnsupportedOperationException iae) {
        } catch (Exception e) {
            Assert.fail("UnsupportedOperationException expected.");
        }
        try {
            builtMap.remove(1);
            Assert.fail("UnsupportedOperationException expected.");
        } catch (UnsupportedOperationException iae) {
        } catch (Exception e) {
            Assert.fail("UnsupportedOperationException expected.");
        }
    }
}

