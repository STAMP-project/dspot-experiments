/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.misuse;


import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.mockito.exceptions.misusing.UnfinishedVerificationException;
import org.mockitoutil.TestBase;


public class DetectingFinalMethodsTest extends TestBase {
    class WithFinal {
        final int foo() {
            return 0;
        }
    }

    @Mock
    private DetectingFinalMethodsTest.WithFinal withFinal;

    @Test
    public void shouldFailWithUnfinishedVerification() {
        Assume.assumeTrue("Does not apply for inline mocks", ((withFinal.getClass()) != (DetectingFinalMethodsTest.WithFinal.class)));
        Mockito.verify(withFinal).foo();
        try {
            Mockito.verify(withFinal).foo();
            Assert.fail();
        } catch (UnfinishedVerificationException e) {
        }
    }

    @Test
    public void shouldFailWithUnfinishedStubbing() {
        Assume.assumeTrue("Does not apply for inline mocks", ((withFinal.getClass()) != (DetectingFinalMethodsTest.WithFinal.class)));
        withFinal = Mockito.mock(DetectingFinalMethodsTest.WithFinal.class);
        try {
            Mockito.when(withFinal.foo()).thenReturn(null);
            Assert.fail();
        } catch (MissingMethodInvocationException e) {
        }
    }
}

