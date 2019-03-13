/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


// see issue 101
public class CovariantOverrideTest extends TestBase {
    public interface ReturnsObject {
        Object callMe();
    }

    public interface ReturnsString extends CovariantOverrideTest.ReturnsObject {
        // Java 5 covariant override of method from parent interface
        String callMe();
    }

    @Test
    public void returnFoo1() {
        CovariantOverrideTest.ReturnsObject mock = Mockito.mock(CovariantOverrideTest.ReturnsObject.class);
        Mockito.when(mock.callMe()).thenReturn("foo");
        Assert.assertEquals("foo", mock.callMe());// Passes

    }

    @Test
    public void returnFoo2() {
        CovariantOverrideTest.ReturnsString mock = Mockito.mock(CovariantOverrideTest.ReturnsString.class);
        Mockito.when(mock.callMe()).thenReturn("foo");
        Assert.assertEquals("foo", mock.callMe());// Passes

    }

    @Test
    public void returnFoo3() {
        CovariantOverrideTest.ReturnsObject mock = Mockito.mock(CovariantOverrideTest.ReturnsString.class);
        Mockito.when(mock.callMe()).thenReturn("foo");
        Assert.assertEquals("foo", mock.callMe());// Passes

    }

    @Test
    public void returnFoo4() {
        CovariantOverrideTest.ReturnsString mock = Mockito.mock(CovariantOverrideTest.ReturnsString.class);
        mock.callMe();// covariant override not generated

        CovariantOverrideTest.ReturnsObject mock2 = mock;// Switch to base type to call covariant override

        Mockito.verify(mock2).callMe();// Fails: java.lang.AssertionError: expected:<foo> but was:<null>

    }
}

