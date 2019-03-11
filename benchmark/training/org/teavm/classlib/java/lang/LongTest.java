/**
 * Copyright 2017 konsoletyper.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class LongTest {
    @Test
    public void compares() {
        Assert.assertTrue(((Long.compare(10, 5)) > 0));
        Assert.assertTrue(((Long.compare(5, 10)) < 0));
        Assert.assertTrue(((Long.compare(5, 5)) == 0));
        Assert.assertTrue(((Long.compare(Long.MAX_VALUE, Long.MIN_VALUE)) > 0));
        Assert.assertTrue(((Long.compare(Long.MIN_VALUE, Long.MAX_VALUE)) < 0));
    }

    @Test
    @SkipJVM
    public void calculatesHashCode() {
        Assert.assertEquals((23 ^ 42), Long.hashCode(((23L << 32) | 42)));
    }

    @Test
    public void bitsReversed() {
        Assert.assertEquals(0, Long.reverse(0));
        Assert.assertEquals(-9223372036854775808L, Long.reverse(1));
        Assert.assertEquals(9007199254740992L, Long.reverse(1024));
        Assert.assertEquals(1L, Long.reverse(-9223372036854775808L));
        Assert.assertEquals(-8608480567731124088L, Long.reverse(1229782938247303441L));
        Assert.assertEquals(868082074056920076L, Long.reverse(3472328296227680304L));
        Assert.assertEquals(255L, Long.reverse(-72057594037927936L));
        Assert.assertEquals(-72057594037927936L, Long.reverse(255L));
        Assert.assertEquals(-1L, Long.reverse(-1L));
        Assert.assertEquals(-703792994892906496L, Long.reverse(384111));
    }
}

