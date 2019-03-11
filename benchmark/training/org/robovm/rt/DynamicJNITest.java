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
 * Tests dynamically linked JNI.
 */
public class DynamicJNITest {
    private static boolean ignoreAllTests = false;

    private static boolean called = false;

    @Test
    public void testShortName() {
        Assert.assertEquals(3, DynamicJNITest.add(1, 2));
    }

    @Test
    public void testLongName() {
        Assert.assertEquals(20, DynamicJNITest.mul(4, 5));
    }

    @Test
    public void testOverloadedMethodLongName() {
        Assert.assertEquals(3, DynamicJNITest.sub(6, 3));
        Assert.assertEquals(9.0F, DynamicJNITest.sub(13.0F, 4.0F), 0.0F);
    }

    @Test
    public void testNoArgsShortName() {
        DynamicJNITest.called = false;
        DynamicJNITest.noArgsShort();
        Assert.assertTrue(DynamicJNITest.called);
    }

    @Test
    public void testNoArgsLongName() {
        DynamicJNITest.called = false;
        DynamicJNITest.noArgsLong();
        Assert.assertTrue(DynamicJNITest.called);
    }

    @Test(expected = UnsatisfiedLinkError.class)
    public void testNotBoundMethod() {
        DynamicJNITest.notBound();
    }

    @Test
    public void testRegisterNatives() {
        DynamicJNITest.called = false;
        try {
            DynamicJNITest.boundUsingRegisterNatives();
            Assert.fail("UnsatisfiedLinkError");
        } catch (UnsatisfiedLinkError e) {
        }
        Assert.assertFalse(DynamicJNITest.called);
        DynamicJNITest.registerNatives();
        DynamicJNITest.boundUsingRegisterNatives();
        Assert.assertTrue(DynamicJNITest.called);
    }
}

