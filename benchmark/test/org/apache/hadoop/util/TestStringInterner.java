/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests string interning {@link StringInterner}
 */
public class TestStringInterner {
    /**
     * Test different references are returned for any of string
     * instances that are equal to each other but not interned.
     */
    @Test
    public void testNoIntern() {
        String literalABC = "ABC";
        String substringABC = "ABCDE".substring(0, 3);
        String heapABC = new String("ABC");
        Assert.assertNotSame(literalABC, substringABC);
        Assert.assertNotSame(literalABC, heapABC);
        Assert.assertNotSame(substringABC, heapABC);
    }

    /**
     * Test the same strong reference is returned for any
     * of string instances that are equal to each other.
     */
    @Test
    public void testStrongIntern() {
        String strongInternLiteralABC = strongIntern("ABC");
        String strongInternSubstringABC = strongIntern("ABCDE".substring(0, 3));
        String strongInternHeapABC = strongIntern(new String("ABC"));
        Assert.assertSame(strongInternLiteralABC, strongInternSubstringABC);
        Assert.assertSame(strongInternLiteralABC, strongInternHeapABC);
        Assert.assertSame(strongInternSubstringABC, strongInternHeapABC);
    }

    /**
     * Test the same weak reference is returned for any
     * of string instances that are equal to each other.
     */
    @Test
    public void testWeakIntern() {
        String weakInternLiteralABC = weakIntern("ABC");
        String weakInternSubstringABC = weakIntern("ABCDE".substring(0, 3));
        String weakInternHeapABC = weakIntern(new String("ABC"));
        Assert.assertSame(weakInternLiteralABC, weakInternSubstringABC);
        Assert.assertSame(weakInternLiteralABC, weakInternHeapABC);
        Assert.assertSame(weakInternSubstringABC, weakInternHeapABC);
    }
}

