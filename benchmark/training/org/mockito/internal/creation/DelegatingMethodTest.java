/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class DelegatingMethodTest extends TestBase {
    private Method someMethod;

    private Method otherMethod;

    private DelegatingMethod delegatingMethod;

    @Test
    public void equals_should_return_false_when_not_equal() throws Exception {
        DelegatingMethod notEqual = new DelegatingMethod(otherMethod);
        Assert.assertFalse(delegatingMethod.equals(notEqual));
    }

    @Test
    public void equals_should_return_true_when_equal() throws Exception {
        DelegatingMethod equal = new DelegatingMethod(someMethod);
        Assert.assertTrue(delegatingMethod.equals(equal));
    }

    @Test
    public void equals_should_return_true_when_self() throws Exception {
        Assert.assertTrue(delegatingMethod.equals(delegatingMethod));
    }

    @Test
    public void equals_should_return_false_when_not_equal_to_method() throws Exception {
        Assert.assertFalse(delegatingMethod.equals(otherMethod));
    }

    @Test
    public void equals_should_return_true_when_equal_to_method() throws Exception {
        Assert.assertTrue(delegatingMethod.equals(someMethod));
    }

    private interface Something {
        Object someMethod(Object param);

        Object otherMethod(Object param);
    }
}

