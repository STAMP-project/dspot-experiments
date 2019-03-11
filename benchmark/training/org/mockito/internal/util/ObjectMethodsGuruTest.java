/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util;


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class ObjectMethodsGuruTest extends TestBase {
    private interface HasCompareToButDoesNotImplementComparable {
        int compareTo(ObjectMethodsGuruTest.HasCompareToButDoesNotImplementComparable other);
    }

    private interface HasCompare extends Comparable<ObjectMethodsGuruTest.HasCompare> {
        int foo(ObjectMethodsGuruTest.HasCompare other);

        int compareTo(ObjectMethodsGuruTest.HasCompare other, String redHerring);

        int compareTo(String redHerring);

        int compareTo(ObjectMethodsGuruTest.HasCompare redHerring);
    }

    @Test
    public void shouldKnowToStringMethod() throws Exception {
        Assert.assertFalse(ObjectMethodsGuru.isToStringMethod(Object.class.getMethod("equals", Object.class)));
        Assert.assertFalse(ObjectMethodsGuru.isToStringMethod(IMethods.class.getMethod("toString", String.class)));
        Assert.assertTrue(ObjectMethodsGuru.isToStringMethod(IMethods.class.getMethod("toString")));
    }

    @Test
    public void shouldKnowCompareToMethod() throws Exception {
        Assert.assertFalse(ObjectMethodsGuru.isCompareToMethod(Date.class.getMethod("toString")));
        Assert.assertFalse(ObjectMethodsGuru.isCompareToMethod(ObjectMethodsGuruTest.HasCompare.class.getMethod("foo", ObjectMethodsGuruTest.HasCompare.class)));
        Assert.assertFalse(ObjectMethodsGuru.isCompareToMethod(ObjectMethodsGuruTest.HasCompare.class.getMethod("compareTo", ObjectMethodsGuruTest.HasCompare.class, String.class)));
        Assert.assertFalse(ObjectMethodsGuru.isCompareToMethod(ObjectMethodsGuruTest.HasCompare.class.getMethod("compareTo", String.class)));
        Assert.assertFalse(ObjectMethodsGuru.isCompareToMethod(ObjectMethodsGuruTest.HasCompareToButDoesNotImplementComparable.class.getDeclaredMethod("compareTo", ObjectMethodsGuruTest.HasCompareToButDoesNotImplementComparable.class)));
        Assert.assertTrue(ObjectMethodsGuru.isCompareToMethod(ObjectMethodsGuruTest.HasCompare.class.getMethod("compareTo", ObjectMethodsGuruTest.HasCompare.class)));
    }
}

