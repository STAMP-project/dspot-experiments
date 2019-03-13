/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class EqualityTest extends TestBase {
    @Test
    public void shouldKnowIfObjectsAreEqual() throws Exception {
        int[] arr = new int[]{ 1, 2 };
        Assert.assertTrue(Equality.areEqual(arr, arr));
        Assert.assertTrue(Equality.areEqual(new int[]{ 1, 2 }, new int[]{ 1, 2 }));
        Assert.assertTrue(Equality.areEqual(new Double[]{ 1.0 }, new Double[]{ 1.0 }));
        Assert.assertTrue(Equality.areEqual(new String[0], new String[0]));
        Assert.assertTrue(Equality.areEqual(new Object[10], new Object[10]));
        Assert.assertTrue(Equality.areEqual(new int[]{ 1 }, new Integer[]{ 1 }));
        Assert.assertTrue(Equality.areEqual(new Object[]{ "1" }, new String[]{ "1" }));
        Object badequals = new EqualityTest.BadEquals();
        Assert.assertTrue(Equality.areEqual(badequals, badequals));
        Assert.assertFalse(Equality.areEqual(new Object[9], new Object[10]));
        Assert.assertFalse(Equality.areEqual(new int[]{ 1, 2 }, new int[]{ 1 }));
        Assert.assertFalse(Equality.areEqual(new int[]{ 1 }, new double[]{ 1.0 }));
    }

    private final class BadEquals {
        @Override
        public boolean equals(Object oth) {
            throw new RuntimeException();
        }
    }
}

