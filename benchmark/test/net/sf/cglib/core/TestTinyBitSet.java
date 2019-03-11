/**
 * Copyright 2003 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.cglib.core;


import junit.framework.TestCase;


public class TestTinyBitSet extends TestCase {
    private TinyBitSet b = new TinyBitSet();

    public void testGetSetClear() {
        // get, set, clear, set bits 0 - 31
        for (int index = 0; index < 32; index++) {
            TestCase.assertFalse(b.get(index));
            b.set(index);
            TestCase.assertTrue(b.get(index));
            b.clear(index);
            TestCase.assertFalse(b.get(index));
            b.set(index);
        }
        // after setting bits 0-31, get bits 32 and above reports true;
        TestCase.assertTrue(b.get(32));
        TestCase.assertTrue(b.get(255));
        TestCase.assertTrue(b.get(256));
        TestCase.assertTrue(b.get(1000000));
    }

    public void testGetSetClear2() {
        for (int index = 2; index > 0; index *= 2) {
            b.set(index);
            TestCase.assertTrue(b.get(index));
            b.clear(index);
            TestCase.assertFalse(b.get(index));
            b.set(index);
        }
    }

    public void testLength() {
        TestCase.assertEquals(0, b.length());
        b.set(10);
        TestCase.assertEquals(11, b.length());
        b.set(15);
        TestCase.assertEquals(16, b.length());
        b.set(14);
        TestCase.assertEquals(16, b.length());
    }

    public void testCardinality() {
        TestCase.assertEquals(0, b.cardinality());
        b.set(1);
        TestCase.assertEquals(1, b.cardinality());
        b.set(4);
        TestCase.assertEquals(2, b.cardinality());
        b.set(10);
        TestCase.assertEquals(3, b.cardinality());
        b.set(10);
        TestCase.assertEquals(3, b.cardinality());
        b.clear(10);
        TestCase.assertEquals(2, b.cardinality());
    }

    public TestTinyBitSet(String testName) {
        super(testName);
    }
}

