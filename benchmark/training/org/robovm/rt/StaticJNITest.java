/**
 * Copyright (C) 2015 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.rt;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests statically linked JNI.
 */
public class StaticJNITest {
    private static boolean called = false;

    @Test
    public void testShortName() {
        Assert.assertEquals(3, StaticJNITest.add(1, 2));
    }

    @Test
    public void testLongName() {
        Assert.assertEquals(20, StaticJNITest.mul(4, 5));
    }

    @Test
    public void testOverloadedMethodLongName() {
        Assert.assertEquals(3, StaticJNITest.sub(6, 3));
        Assert.assertEquals(9.0F, StaticJNITest.sub(13.0F, 4.0F), 0.0F);
    }

    @Test
    public void testNoArgsShortName() {
        StaticJNITest.called = false;
        StaticJNITest.noArgsShort();
        Assert.assertTrue(StaticJNITest.called);
    }

    @Test
    public void testNoArgsLongName() {
        StaticJNITest.called = false;
        StaticJNITest.noArgsLong();
        Assert.assertTrue(StaticJNITest.called);
    }

    @Test(expected = UnsatisfiedLinkError.class)
    public void testNotBoundMethod() {
        StaticJNITest.notBound();
    }
}

