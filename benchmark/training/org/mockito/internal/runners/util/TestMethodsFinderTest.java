/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.runners.util;


import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class TestMethodsFinderTest extends TestBase {
    public static class HasTests {
        @Test
        public void someTest() {
        }
    }

    static class DoesNotHaveTests {
        public void someTest() {
        }
    }

    @Test
    public void shouldKnowWhenClassHasTests() {
        Assert.assertTrue(TestMethodsFinder.hasTestMethods(TestMethodsFinderTest.HasTests.class));
        Assert.assertFalse(TestMethodsFinder.hasTestMethods(TestMethodsFinderTest.DoesNotHaveTests.class));
    }
}

