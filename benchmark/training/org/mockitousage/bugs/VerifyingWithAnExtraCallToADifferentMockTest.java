/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


// see bug 138
public class VerifyingWithAnExtraCallToADifferentMockTest extends TestBase {
    @Mock
    IMethods mock;

    @Mock
    IMethods mockTwo;

    @Test
    public void shouldAllowVerifyingWhenOtherMockCallIsInTheSameLine() {
        // given
        Mockito.when(mock.otherMethod()).thenReturn("foo");
        // when
        mockTwo.simpleMethod("foo");
        // then
        Mockito.verify(mockTwo).simpleMethod(mock.otherMethod());
        try {
            Mockito.verify(mockTwo, Mockito.never()).simpleMethod(mock.otherMethod());
            Assert.fail();
        } catch (NeverWantedButInvoked e) {
        }
    }
}

