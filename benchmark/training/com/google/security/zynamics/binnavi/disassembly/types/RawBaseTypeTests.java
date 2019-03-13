/**
 * Copyright 2014 Google Inc. All Rights Reserved.
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
package com.google.security.zynamics.binnavi.disassembly.types;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static BaseTypeCategory.ATOMIC;
import static BaseTypeCategory.POINTER;


/**
 * This class contains tests related to {@link RawBaseType raw base types}.
 */
@RunWith(JUnit4.class)
public class RawBaseTypeTests {
    private static final int ID = 12345;

    private static final String NAME = "test_name";

    private static final int SIZE = 100;

    private static final Integer POINTER_ID = 500;

    @Test(expected = NullPointerException.class)
    public void testConstructor2() {
        new RawBaseType(RawBaseTypeTests.ID, null, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, null);
    }

    @Test
    public void testEqualsContracts() {
        // Test reflexivity, symmetry and transitivity.
        final RawBaseType rawType0 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        final RawBaseType rawType1 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        final RawBaseType rawType2 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        Assert.assertEquals(rawType0, rawType0);
        Assert.assertEquals(rawType0, rawType1);
        Assert.assertEquals(rawType1, rawType0);
        Assert.assertEquals(rawType1, rawType2);
        Assert.assertEquals(rawType0, rawType2);
    }

    @Test
    public void testEqualsFalse0() {
        final RawBaseType rawType0 = new RawBaseType(((RawBaseTypeTests.ID) + 1), RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        final RawBaseType rawType1 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        Assert.assertFalse(rawType0.equals(rawType1));
    }

    @Test
    public void testEqualsFalse1() {
        final RawBaseType rawType0 = new RawBaseType(RawBaseTypeTests.ID, ((RawBaseTypeTests.NAME) + "narf"), RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        final RawBaseType rawType1 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        Assert.assertFalse(rawType0.equals(rawType1));
    }

    @Test
    public void testEqualsFalse2() {
        final RawBaseType rawType0 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, ((RawBaseTypeTests.SIZE) + 1), RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        final RawBaseType rawType1 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        Assert.assertFalse(rawType0.equals(rawType1));
    }

    @Test
    public void testEqualsFalse3() {
        final RawBaseType rawType0 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, ((RawBaseTypeTests.POINTER_ID) + 1), true, ATOMIC);
        final RawBaseType rawType1 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        Assert.assertFalse(rawType0.equals(rawType1));
    }

    @Test
    public void testEqualsFalse4() {
        final RawBaseType rawType0 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, false, ATOMIC);
        final RawBaseType rawType1 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, ATOMIC);
        Assert.assertFalse(rawType0.equals(rawType1));
    }

    @Test
    public void testEqualsFalse5() {
        final RawBaseType rawType0 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, false, ATOMIC);
        final RawBaseType rawType1 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, true, POINTER);
        Assert.assertFalse(rawType0.equals(rawType1));
    }

    @Test
    public void testEqualsFalse6() {
        final RawBaseType rawType0 = new RawBaseType(RawBaseTypeTests.ID, RawBaseTypeTests.NAME, RawBaseTypeTests.SIZE, RawBaseTypeTests.POINTER_ID, false, ATOMIC);
        Assert.assertFalse(rawType0.equals(null));
    }
}

