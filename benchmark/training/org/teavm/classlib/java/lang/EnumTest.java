/**
 * Copyright 2014 Alexey Andreev.
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
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class EnumTest {
    private enum Foo {

        A,
        B,
        C;}

    private enum Bar {

        D,
        E;}

    @Test
    public void sameConstantsAreEqual() {
        Assert.assertEquals(EnumTest.Foo.A, EnumTest.Foo.A);
    }

    @Test
    public void differentConstansAreNotEqual() {
        Assert.assertNotEquals(EnumTest.Foo.A, EnumTest.Foo.B);
    }

    @Test
    public void constantsOfDifferentEnumsAreNotEqual() {
        Assert.assertNotEquals(EnumTest.Foo.A, EnumTest.Bar.D);
    }

    @Test
    public void declaringClassComputed() {
        Assert.assertEquals(EnumTest.Foo.class, EnumTest.Foo.A.getDeclaringClass());
    }

    @Test
    public void comparisonGivesZeroForSameConstants() {
        Assert.assertEquals(0, EnumTest.Foo.A.compareTo(EnumTest.Foo.A));
    }

    @Test
    public void comparisonGivesPositiveForLaterConstant() {
        Assert.assertTrue(((EnumTest.Foo.B.compareTo(EnumTest.Foo.A)) > 0));
    }

    @Test
    public void comparisonGivesNegativeForEarlierConstant() {
        Assert.assertTrue(((EnumTest.Foo.A.compareTo(EnumTest.Foo.B)) < 0));
    }

    @Test
    public void valueOfReturnsConstant() {
        Assert.assertEquals("A", Enum.valueOf(EnumTest.Foo.class, "A").name());
    }
}

