/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import Consumer.IS_PLACEHOLDER;
import Consumer.NO_FLAGS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Consumer.DO_NOT_CACHE_ENCODED;
import static Consumer.IS_LAST;
import static Consumer.IS_PARTIAL_RESULT;
import static Consumer.IS_PLACEHOLDER;


@RunWith(RobolectricTestRunner.class)
public class BaseConsumerTest {
    @Mock
    public Consumer mDelegatedConsumer;

    private Object mResult;

    private Object mResult2;

    private Exception mException;

    private BaseConsumer mBaseConsumer;

    @Test
    public void testOnNewResultDoesNotThrow() {
        Mockito.doThrow(new RuntimeException()).when(mDelegatedConsumer).onNewResult(ArgumentMatchers.anyObject(), ArgumentMatchers.anyInt());
        mBaseConsumer.onNewResult(mResult, 0);
        Mockito.verify(mDelegatedConsumer).onNewResult(mResult, 0);
    }

    @Test
    public void testOnFailureDoesNotThrow() {
        Mockito.doThrow(new RuntimeException()).when(mDelegatedConsumer).onFailure(ArgumentMatchers.any(Throwable.class));
        mBaseConsumer.onFailure(mException);
        Mockito.verify(mDelegatedConsumer).onFailure(mException);
    }

    @Test
    public void testOnCancellationDoesNotThrow() {
        Mockito.doThrow(new RuntimeException()).when(mDelegatedConsumer).onCancellation();
        mBaseConsumer.onCancellation();
        Mockito.verify(mDelegatedConsumer).onCancellation();
    }

    @Test
    public void testDoesNotForwardAfterFinalResult() {
        mBaseConsumer.onNewResult(mResult, IS_LAST);
        mBaseConsumer.onFailure(mException);
        mBaseConsumer.onCancellation();
        Mockito.verify(mDelegatedConsumer).onNewResult(mResult, IS_LAST);
        Mockito.verifyNoMoreInteractions(mDelegatedConsumer);
    }

    @Test
    public void testDoesNotForwardAfterOnFailure() {
        mBaseConsumer.onFailure(mException);
        mBaseConsumer.onNewResult(mResult, IS_LAST);
        mBaseConsumer.onCancellation();
        Mockito.verify(mDelegatedConsumer).onFailure(mException);
        Mockito.verifyNoMoreInteractions(mDelegatedConsumer);
    }

    @Test
    public void testDoesNotForwardAfterOnCancellation() {
        mBaseConsumer.onCancellation();
        mBaseConsumer.onNewResult(mResult, IS_LAST);
        mBaseConsumer.onFailure(mException);
        Mockito.verify(mDelegatedConsumer).onCancellation();
        Mockito.verifyNoMoreInteractions(mDelegatedConsumer);
    }

    @Test
    public void testDoesForwardAfterIntermediateResult() {
        mBaseConsumer.onNewResult(mResult, 0);
        mBaseConsumer.onNewResult(mResult2, IS_LAST);
        Mockito.verify(mDelegatedConsumer).onNewResult(mResult2, IS_LAST);
    }

    @Test
    public void testIsLast() {
        assertThat(BaseConsumer.isLast(IS_LAST)).isTrue();
        assertThat(BaseConsumer.isLast(NO_FLAGS)).isFalse();
    }

    @Test
    public void testIsNotLast() {
        assertThat(BaseConsumer.isNotLast(IS_LAST)).isFalse();
        assertThat(BaseConsumer.isNotLast(NO_FLAGS)).isTrue();
    }

    @Test
    public void testTurnOnStatusFlag() {
        int turnedOn = BaseConsumer.turnOnStatusFlag(NO_FLAGS, IS_LAST);
        assertThat(BaseConsumer.isLast(turnedOn)).isTrue();
    }

    @Test
    public void testTurnOffStatusFlag() {
        int turnedOff = BaseConsumer.turnOffStatusFlag(IS_LAST, IS_LAST);
        assertThat(BaseConsumer.isNotLast(turnedOff)).isTrue();
    }

    @Test
    public void testStatusHasFlag() {
        assertThat(BaseConsumer.statusHasFlag(((IS_PLACEHOLDER) | (IS_LAST)), IS_PLACEHOLDER)).isTrue();
        assertThat(BaseConsumer.statusHasFlag(((DO_NOT_CACHE_ENCODED) | (IS_LAST)), IS_PLACEHOLDER)).isFalse();
    }

    @Test
    public void testStatusHasAnyFlag() {
        assertThat(BaseConsumer.statusHasAnyFlag(((IS_PLACEHOLDER) | (IS_LAST)), ((IS_PLACEHOLDER) | (DO_NOT_CACHE_ENCODED)))).isTrue();
        assertThat(BaseConsumer.statusHasAnyFlag(((IS_PLACEHOLDER) | (IS_LAST)), ((IS_PARTIAL_RESULT) | (DO_NOT_CACHE_ENCODED)))).isFalse();
    }
}

