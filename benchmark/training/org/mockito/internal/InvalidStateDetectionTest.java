/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.exceptions.misusing.UnfinishedStubbingException;
import org.mockito.exceptions.misusing.UnfinishedVerificationException;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


/**
 * invalid state happens if:
 *
 *    -unfinished stubbing
 *    -unfinished doReturn()
 *    -stubbing without actual method call
 *    -verify without actual method call
 *
 * we should aim to detect invalid state in following scenarios:
 *
 *    -on method call on mock
 *    -on verify
 *    -on verifyZeroInteractions
 *    -on verifyNoMoreInteractions
 *    -on verify in order
 *    -on stub
 */
@SuppressWarnings({ "unchecked", "deprecation" })
public class InvalidStateDetectionTest extends TestBase {
    @Mock
    private IMethods mock;

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldDetectUnfinishedStubbing() {
        Mockito.when(mock.simpleMethod());
        detectsAndCleansUp(new InvalidStateDetectionTest.OnMethodCallOnMock(), UnfinishedStubbingException.class);
        Mockito.when(mock.simpleMethod());
        detectsAndCleansUp(new InvalidStateDetectionTest.OnStub(), UnfinishedStubbingException.class);
        Mockito.when(mock.simpleMethod());
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerify(), UnfinishedStubbingException.class);
        Mockito.when(mock.simpleMethod());
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyInOrder(), UnfinishedStubbingException.class);
        Mockito.when(mock.simpleMethod());
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyZeroInteractions(), UnfinishedStubbingException.class);
        Mockito.when(mock.simpleMethod());
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyNoMoreInteractions(), UnfinishedStubbingException.class);
        Mockito.when(mock.simpleMethod());
        detectsAndCleansUp(new InvalidStateDetectionTest.OnDoAnswer(), UnfinishedStubbingException.class);
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldDetectUnfinishedDoAnswerStubbing() {
        Mockito.doAnswer(null);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnMethodCallOnMock(), UnfinishedStubbingException.class);
        Mockito.doAnswer(null);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnStub(), UnfinishedStubbingException.class);
        Mockito.doAnswer(null);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerify(), UnfinishedStubbingException.class);
        Mockito.doAnswer(null);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyInOrder(), UnfinishedStubbingException.class);
        Mockito.doAnswer(null);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyZeroInteractions(), UnfinishedStubbingException.class);
        Mockito.doAnswer(null);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyNoMoreInteractions(), UnfinishedStubbingException.class);
        Mockito.doAnswer(null);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnDoAnswer(), UnfinishedStubbingException.class);
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldDetectUnfinishedVerification() {
        Mockito.verify(mock);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnStub(), UnfinishedVerificationException.class);
        Mockito.verify(mock);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerify(), UnfinishedVerificationException.class);
        Mockito.verify(mock);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyInOrder(), UnfinishedVerificationException.class);
        Mockito.verify(mock);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyZeroInteractions(), UnfinishedVerificationException.class);
        Mockito.verify(mock);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyNoMoreInteractions(), UnfinishedVerificationException.class);
        Mockito.verify(mock);
        detectsAndCleansUp(new InvalidStateDetectionTest.OnDoAnswer(), UnfinishedVerificationException.class);
    }

    @Test
    public void shouldDetectMisplacedArgumentMatcher() {
        ArgumentMatchers.anyObject();
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerify(), InvalidUseOfMatchersException.class);
        ArgumentMatchers.anyObject();
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyInOrder(), InvalidUseOfMatchersException.class);
        ArgumentMatchers.anyObject();
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyZeroInteractions(), InvalidUseOfMatchersException.class);
        ArgumentMatchers.anyObject();
        detectsAndCleansUp(new InvalidStateDetectionTest.OnVerifyNoMoreInteractions(), InvalidUseOfMatchersException.class);
        ArgumentMatchers.anyObject();
        detectsAndCleansUp(new InvalidStateDetectionTest.OnDoAnswer(), InvalidUseOfMatchersException.class);
    }

    @Test
    public void shouldCorrectStateAfterDetectingUnfinishedStubbing() {
        Mockito.doThrow(new RuntimeException()).when(mock);
        try {
            Mockito.doThrow(new RuntimeException()).when(mock).oneArg(true);
            Assert.fail();
        } catch (UnfinishedStubbingException e) {
        }
        Mockito.doThrow(new RuntimeException()).when(mock).oneArg(true);
        try {
            mock.oneArg(true);
            Assert.fail();
        } catch (RuntimeException e) {
        }
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void shouldCorrectStateAfterDetectingUnfinishedVerification() {
        mock.simpleMethod();
        Mockito.verify(mock);
        try {
            Mockito.verify(mock).simpleMethod();
            Assert.fail();
        } catch (UnfinishedVerificationException e) {
        }
        Mockito.verify(mock).simpleMethod();
    }

    private interface DetectsInvalidState {
        void detect(IMethods mock);
    }

    private static class OnVerify implements InvalidStateDetectionTest.DetectsInvalidState {
        @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
        public void detect(IMethods mock) {
            Mockito.verify(mock);
        }
    }

    private static class OnVerifyInOrder implements InvalidStateDetectionTest.DetectsInvalidState {
        @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
        public void detect(IMethods mock) {
            Mockito.inOrder(mock).verify(mock);
        }
    }

    private static class OnVerifyZeroInteractions implements InvalidStateDetectionTest.DetectsInvalidState {
        @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
        public void detect(IMethods mock) {
            Mockito.verifyZeroInteractions(mock);
        }
    }

    private static class OnVerifyNoMoreInteractions implements InvalidStateDetectionTest.DetectsInvalidState {
        @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
        public void detect(IMethods mock) {
            Mockito.verifyNoMoreInteractions(mock);
        }
    }

    private static class OnDoAnswer implements InvalidStateDetectionTest.DetectsInvalidState {
        @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
        public void detect(IMethods mock) {
            Mockito.doAnswer(null);
        }
    }

    private static class OnStub implements InvalidStateDetectionTest.DetectsInvalidState {
        @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
        public void detect(IMethods mock) {
            Mockito.when(mock);
        }
    }

    private static class OnMethodCallOnMock implements InvalidStateDetectionTest.DetectsInvalidState {
        public void detect(IMethods mock) {
            mock.simpleMethod();
        }
    }

    private static class OnMockCreation implements InvalidStateDetectionTest.DetectsInvalidState {
        @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
        public void detect(IMethods mock) {
            Mockito.mock(IMethods.class);
        }
    }

    private static class OnSpyCreation implements InvalidStateDetectionTest.DetectsInvalidState {
        @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
        public void detect(IMethods mock) {
            Mockito.spy(new Object());
        }
    }
}

