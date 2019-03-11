/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.mockito.exceptions.verification.TooLittleActualInvocations;
import org.mockito.exceptions.verification.TooManyActualInvocations;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class ExactNumberOfTimesVerificationTest extends TestBase {
    private LinkedList<String> mock;

    @Test
    public void shouldDetectTooLittleActualInvocations() throws Exception {
        mock.clear();
        mock.clear();
        Mockito.verify(mock, Mockito.times(2)).clear();
        try {
            Mockito.verify(mock, Mockito.times(100)).clear();
            Assert.fail();
        } catch (TooLittleActualInvocations e) {
            assertThat(e).hasMessageContaining("Wanted 100 times").hasMessageContaining("was 2");
        }
    }

    @Test
    public void shouldDetectTooManyActualInvocations() throws Exception {
        mock.clear();
        mock.clear();
        Mockito.verify(mock, Mockito.times(2)).clear();
        try {
            Mockito.verify(mock, Mockito.times(1)).clear();
            Assert.fail();
        } catch (TooManyActualInvocations e) {
            assertThat(e).hasMessageContaining("Wanted 1 time").hasMessageContaining("was 2 times");
        }
    }

    @Test
    public void shouldDetectActualInvocationsCountIsMoreThanZero() throws Exception {
        Mockito.verify(mock, Mockito.times(0)).clear();
        try {
            Mockito.verify(mock, Mockito.times(15)).clear();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
    }

    @Test
    public void shouldDetectActuallyCalledOnce() throws Exception {
        mock.clear();
        try {
            Mockito.verify(mock, Mockito.times(0)).clear();
            Assert.fail();
        } catch (NeverWantedButInvoked e) {
            assertThat(e).hasMessageContaining("Never wanted here");
        }
    }

    @Test
    public void shouldPassWhenMethodsActuallyNotCalled() throws Exception {
        Mockito.verify(mock, Mockito.times(0)).clear();
        Mockito.verify(mock, Mockito.times(0)).add("yes, I wasn't called");
    }

    @Test
    public void shouldNotCountInStubbedInvocations() throws Exception {
        Mockito.when(mock.add("test")).thenReturn(false);
        Mockito.when(mock.add("test")).thenReturn(true);
        mock.add("test");
        mock.add("test");
        Mockito.verify(mock, Mockito.times(2)).add("test");
    }

    @Test
    public void shouldAllowVerifyingInteractionNeverHappened() throws Exception {
        mock.add("one");
        Mockito.verify(mock, Mockito.never()).add("two");
        Mockito.verify(mock, Mockito.never()).clear();
        try {
            Mockito.verify(mock, Mockito.never()).add("one");
            Assert.fail();
        } catch (NeverWantedButInvoked e) {
        }
    }

    @Test
    public void shouldAllowVerifyingInteractionNeverHappenedInOrder() throws Exception {
        mock.add("one");
        mock.add("two");
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock, Mockito.never()).add("xxx");
        inOrder.verify(mock).add("one");
        inOrder.verify(mock, Mockito.never()).add("one");
        try {
            inOrder.verify(mock, Mockito.never()).add("two");
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }
}

