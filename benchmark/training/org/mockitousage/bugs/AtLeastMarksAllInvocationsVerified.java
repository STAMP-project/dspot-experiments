/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockitoutil.TestBase;


// see issue 112
public class AtLeastMarksAllInvocationsVerified extends TestBase {
    public static class SomeMethods {
        public void allowedMethod() {
        }

        public void disallowedMethod() {
        }
    }

    @Test(expected = NoInteractionsWanted.class)
    public void shouldFailBecauseDisallowedMethodWasCalled() {
        AtLeastMarksAllInvocationsVerified.SomeMethods someMethods = Mockito.mock(AtLeastMarksAllInvocationsVerified.SomeMethods.class);
        someMethods.allowedMethod();
        someMethods.disallowedMethod();
        Mockito.verify(someMethods, Mockito.atLeast(1)).allowedMethod();
        Mockito.verifyNoMoreInteractions(someMethods);
    }
}

