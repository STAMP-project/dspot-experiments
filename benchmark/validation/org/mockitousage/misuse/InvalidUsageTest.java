/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.misuse;


import org.junit.Assume;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class InvalidUsageTest extends TestBase {
    @Mock
    private IMethods mock;

    @Mock
    private IMethods mockTwo;

    @Test(expected = MockitoException.class)
    public void shouldRequireArgumentsWhenVerifyingNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions();
    }

    @Test(expected = MockitoException.class)
    public void shouldRequireArgumentsWhenVerifyingZeroInteractions() {
        Mockito.verifyZeroInteractions();
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test(expected = MockitoException.class)
    public void shouldNotCreateInOrderObjectWithoutMocks() {
        Mockito.inOrder();
    }

    @Test(expected = MockitoException.class)
    public void shouldNotAllowVerifyingInOrderUnfamilarMocks() {
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mockTwo).simpleMethod();
    }

    @Test(expected = MissingMethodInvocationException.class)
    public void shouldReportMissingMethodInvocationWhenStubbing() {
        Mockito.when(mock.simpleMethod()).thenReturn("this stubbing is required to make sure Stubbable is pulled");
        Mockito.when("".toString()).thenReturn("x");
    }

    @Test(expected = MockitoException.class)
    public void shouldNotAllowSettingInvalidCheckedException() throws Exception {
        Mockito.when(mock.simpleMethod()).thenThrow(new Exception());
    }

    @Test(expected = MockitoException.class)
    public void shouldNotAllowSettingNullThrowable() throws Exception {
        Mockito.when(mock.simpleMethod()).thenThrow(new Throwable[]{ null });
    }

    @SuppressWarnings("all")
    @Test(expected = MockitoException.class)
    public void shouldNotAllowSettingNullThrowableVararg() throws Exception {
        Mockito.when(mock.simpleMethod()).thenThrow(((Throwable) (null)));
    }

    @Test(expected = MockitoException.class)
    public void shouldNotAllowSettingNullConsecutiveThrowable() throws Exception {
        Mockito.when(mock.simpleMethod()).thenThrow(new RuntimeException(), null);
    }

    final class FinalClass {}

    @Test(expected = MockitoException.class)
    public void shouldNotAllowMockingFinalClassesIfDisabled() throws Exception {
        Assume.assumeFalse("Inlining mock allows mocking final classes", ((Mockito.mock(InvalidUsageTest.FinalClass.class).getClass()) == (InvalidUsageTest.FinalClass.class)));
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test(expected = MockitoException.class)
    public void shouldNotAllowMockingPrimitives() throws Exception {
        Mockito.mock(Integer.TYPE);
    }

    interface ObjectLikeInterface {
        boolean equals(Object o);

        String toString();

        int hashCode();
    }

    @Test
    public void shouldNotMockObjectMethodsOnInterface() throws Exception {
        InvalidUsageTest.ObjectLikeInterface inter = Mockito.mock(InvalidUsageTest.ObjectLikeInterface.class);
        inter.equals(null);
        inter.toString();
        inter.hashCode();
        Mockito.verifyZeroInteractions(inter);
    }

    @Test
    public void shouldNotMockObjectMethodsOnClass() throws Exception {
        Object clazz = Mockito.mock(InvalidUsageTest.ObjectLikeInterface.class);
        clazz.equals(null);
        clazz.toString();
        clazz.hashCode();
        Mockito.verifyZeroInteractions(clazz);
    }
}

