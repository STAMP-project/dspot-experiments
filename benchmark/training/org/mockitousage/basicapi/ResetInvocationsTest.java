/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.basicapi;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class ResetInvocationsTest extends TestBase {
    @Mock
    IMethods methods;

    @Mock
    IMethods moarMethods;

    @Test
    public void reset_invocations_should_reset_only_invocations() {
        Mockito.when(methods.simpleMethod()).thenReturn("return");
        methods.simpleMethod();
        Mockito.verify(methods).simpleMethod();
        Mockito.clearInvocations(methods);
        Mockito.verifyNoMoreInteractions(methods);
        Assert.assertEquals("return", methods.simpleMethod());
    }

    @Test
    public void should_reset_invocations_on_multiple_mocks() {
        methods.simpleMethod();
        moarMethods.simpleMethod();
        Mockito.clearInvocations(methods, moarMethods);
        Mockito.verifyNoMoreInteractions(methods, moarMethods);
    }

    @Test(expected = NotAMockException.class)
    public void resettingNonMockIsSafe() {
        Mockito.clearInvocations("");
    }

    @Test(expected = NotAMockException.class)
    public void resettingNullIsSafe() {
        Mockito.clearInvocations(new Object[]{ null });
    }
}

