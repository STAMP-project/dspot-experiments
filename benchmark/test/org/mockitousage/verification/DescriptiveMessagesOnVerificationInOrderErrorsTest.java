/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class DescriptiveMessagesOnVerificationInOrderErrorsTest extends TestBase {
    private IMethods one;

    private IMethods two;

    private IMethods three;

    private InOrder inOrder;

    @Test
    public void shouldPrintVerificationInOrderErrorAndShowBothWantedAndPrevious() {
        inOrder.verify(one).simpleMethod(1);
        inOrder.verify(two, Mockito.atLeastOnce()).simpleMethod(2);
        try {
            inOrder.verify(one, Mockito.atLeastOnce()).simpleMethod(11);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            String expected = "\n" + (((((("Verification in order failure" + "\n") + "Wanted but not invoked:") + "\n") + "iMethods.simpleMethod(11);") + "\n") + "-> at ");
            assertThat(e).hasMessageContaining(expected);
            String expectedCause = "\n" + (((("Wanted anywhere AFTER following interaction:" + "\n") + "iMethods.simpleMethod(2);") + "\n") + "-> at ");
            assertThat(e).hasMessageContaining(expectedCause);
        }
    }

    @Test
    public void shouldPrintVerificationInOrderErrorAndShowWantedOnly() {
        try {
            inOrder.verify(one).differentMethod();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            String expected = "\n" + (((("Wanted but not invoked:" + "\n") + "iMethods.differentMethod();") + "\n") + "-> at");
            assertThat(e).hasMessageContaining(expected);
        }
    }

    @Test
    public void shouldPrintVerificationInOrderErrorAndShowWantedAndActual() {
        try {
            inOrder.verify(one).simpleMethod(999);
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            assertThat(e).hasMessageContaining("has different arguments");
        }
    }

    @Test
    public void shouldNotSayArgumentsAreDifferent() {
        // this is the last invocation so any next verification in order should simply say wanted but not invoked
        inOrder.verify(three).simpleMethod(3);
        try {
            inOrder.verify(one).simpleMethod(999);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("Wanted but not invoked");
        }
    }

    @Test
    public void shouldPrintMethodThatWasNotInvoked() {
        inOrder.verify(one).simpleMethod(1);
        inOrder.verify(one).simpleMethod(11);
        inOrder.verify(two, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(three).simpleMethod(3);
        try {
            inOrder.verify(three).simpleMethod(999);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            String expectedMessage = "\n" + (((("Verification in order failure" + "\n") + "Wanted but not invoked:") + "\n") + "iMethods.simpleMethod(999);");
            assertThat(e).hasMessageContaining(expectedMessage);
        }
    }

    @Test
    public void shouldPrintTooManyInvocations() {
        inOrder.verify(one).simpleMethod(1);
        inOrder.verify(one).simpleMethod(11);
        try {
            inOrder.verify(two, Mockito.times(1)).simpleMethod(2);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            String expectedMessage = "\n" + (((((("Verification in order failure:" + "\n") + "iMethods.simpleMethod(2);") + "\n") + "Wanted 1 time:") + "\n") + "-> at");
            assertThat(e).hasMessageContaining(expectedMessage);
            String expectedCause = "\n" + (("But was 2 times:" + "\n") + "-> at");
            assertThat(e).hasMessageContaining(expectedCause);
        }
    }

    @Test
    public void shouldPrintTooLittleInvocations() {
        two.simpleMethod(2);
        inOrder.verify(one, Mockito.atLeastOnce()).simpleMethod(ArgumentMatchers.anyInt());
        inOrder.verify(two, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(three, Mockito.atLeastOnce()).simpleMethod(3);
        try {
            inOrder.verify(two, Mockito.times(2)).simpleMethod(2);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            String expectedMessage = "\n" + (((((("Verification in order failure:" + "\n") + "iMethods.simpleMethod(2);") + "\n") + "Wanted 2 times:") + "\n") + "-> at");
            assertThat(e).hasMessageContaining(expectedMessage);
            String expectedCause = "\n" + (("But was 1 time:" + "\n") + "-> at");
            assertThat(e).hasMessageContaining(expectedCause);
        }
    }
}

