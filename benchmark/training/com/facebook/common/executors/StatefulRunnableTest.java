/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.executors;


import StatefulRunnable.STATE_STARTED;
import java.util.ConcurrentModificationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class StatefulRunnableTest {
    private StatefulRunnable mStatefulRunnable;

    private Object mResult;

    private ConcurrentModificationException mException;

    @Test
    public void testSuccess() throws Exception {
        runSuccess();
        InOrder inOrder = Mockito.inOrder(mStatefulRunnable);
        inOrder.verify(mStatefulRunnable).onSuccess(mResult);
        inOrder.verify(mStatefulRunnable).disposeResult(mResult);
    }

    @Test
    public void testClosesResultWhenOnSuccessThrows() throws Exception {
        Mockito.doThrow(mException).when(mStatefulRunnable).onSuccess(mResult);
        try {
            runSuccess();
            Assert.fail();
        } catch (ConcurrentModificationException cme) {
            // expected
        }
        Mockito.verify(mStatefulRunnable).disposeResult(mResult);
    }

    @Test
    public void testFailure() throws Exception {
        runFailure();
        Mockito.verify(mStatefulRunnable).onFailure(mException);
    }

    @Test
    public void testDoesNotRunAgainAfterStarted() throws Exception {
        mStatefulRunnable.mState.set(STATE_STARTED);
        runSuccess();
        Mockito.verify(mStatefulRunnable, Mockito.never()).getResult();
    }

    @Test
    public void testCancellation() {
        mStatefulRunnable.cancel();
        Mockito.verify(mStatefulRunnable).onCancellation();
    }

    @Test
    public void testDoesNotRunAfterCancellation() throws Exception {
        mStatefulRunnable.cancel();
        runSuccess();
        Mockito.verify(mStatefulRunnable, Mockito.never()).getResult();
    }

    @Test
    public void testDoesNotCancelTwice() {
        mStatefulRunnable.cancel();
        mStatefulRunnable.cancel();
        Mockito.verify(mStatefulRunnable).onCancellation();
    }

    @Test
    public void testDoesNotCancelAfterStarted() {
        mStatefulRunnable.mState.set(STATE_STARTED);
        mStatefulRunnable.cancel();
        Mockito.verify(mStatefulRunnable, Mockito.never()).onCancellation();
    }
}

