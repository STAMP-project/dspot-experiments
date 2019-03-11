/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.TooLittleActualInvocations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockitousage.IMethods;
import org.mockitoutil.Stopwatch;
import org.mockitoutil.async.AsyncTesting;


public class VerificationWithTimeoutTest {
    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    private Stopwatch watch = Stopwatch.createNotStarted();

    @Mock
    private IMethods mock;

    private AsyncTesting async;

    @Test
    public void should_verify_with_timeout() {
        // when
        async.runAfter(50, callMock('c'));
        async.runAfter(500, callMock('c'));
        // then
        Mockito.verify(mock, Mockito.timeout(200).only()).oneArg('c');
        Mockito.verify(mock).oneArg('c');// sanity check

    }

    @Test
    public void should_verify_with_timeout_and_fail() {
        // when
        async.runAfter(200, callMock('c'));
        // then
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                Mockito.verify(mock, Mockito.timeout(50).only()).oneArg('c');
            }
        }).isInstanceOf(AssertionError.class).hasMessageContaining("Wanted but not invoked");
        // TODO let's have a specific exception vs. generic assertion error + message
    }

    @Test
    public void should_verify_with_times_x() {
        // when
        async.runAfter(50, callMock('c'));
        async.runAfter(100, callMock('c'));
        async.runAfter(600, callMock('c'));
        // then
        Mockito.verify(mock, Mockito.timeout(300).times(2)).oneArg('c');
    }

    @Test
    public void should_verify_with_times_x_and_fail() {
        // when
        async.runAfter(10, callMock('c'));
        async.runAfter(200, callMock('c'));
        // then
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                Mockito.verify(mock, Mockito.timeout(100).times(2)).oneArg('c');
            }
        }).isInstanceOf(TooLittleActualInvocations.class);
    }

    @Test
    public void should_verify_with_at_least() {
        // when
        async.runAfter(10, callMock('c'));
        async.runAfter(50, callMock('c'));
        // then
        Mockito.verify(mock, Mockito.timeout(200).atLeast(2)).oneArg('c');
    }

    @Test
    public void should_verify_with_at_least_once() {
        // when
        async.runAfter(10, callMock('c'));
        async.runAfter(50, callMock('c'));
        // then
        Mockito.verify(mock, Mockito.timeout(200).atLeastOnce()).oneArg('c');
    }

    @Test
    public void should_verify_with_at_least_and_fail() {
        // when
        async.runAfter(10, callMock('c'));
        async.runAfter(50, callMock('c'));
        // then
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() {
                Mockito.verify(mock, Mockito.timeout(100).atLeast(3)).oneArg('c');
            }
        }).isInstanceOf(TooLittleActualInvocations.class);
    }

    @Test
    public void should_verify_with_only() {
        // when
        async.runAfter(10, callMock('c'));
        async.runAfter(300, callMock('c'));
        // then
        Mockito.verify(mock, Mockito.timeout(100).only()).oneArg('c');
    }
}

