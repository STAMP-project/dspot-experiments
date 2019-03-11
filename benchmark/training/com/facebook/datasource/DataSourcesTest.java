/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.datasource;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
@PrepareOnlyThisForTest({ DataSources.class })
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
public class DataSourcesTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private CountDownLatch mCountDownLatch;

    private Exception mException;

    private DataSource<Object> mDataSource;

    private final Object mFinalResult = "final";

    private final Object mIntermediateResult = "intermediate";

    @Test
    public void testImmediateFailedDataSource() {
        DataSource<?> dataSource = DataSources.immediateFailedDataSource(mException);
        Assert.assertTrue(dataSource.isFinished());
        Assert.assertTrue(dataSource.hasFailed());
        Assert.assertEquals(mException, dataSource.getFailureCause());
        Assert.assertFalse(dataSource.hasResult());
        Assert.assertFalse(dataSource.isClosed());
    }

    @Test
    public void testWaitForFinalResult_whenFinalResult_thenReturnFinalResult() throws Throwable {
        Mockito.when(mDataSource.isFinished()).thenReturn(true);
        Mockito.when(mDataSource.getResult()).thenReturn(mFinalResult);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                DataSubscriber dataSubscriber = ((DataSubscriber) (args[0]));
                dataSubscriber.onNewResult(mDataSource);
                return null;
            }
        }).when(mDataSource).subscribe(ArgumentMatchers.any(DataSubscriber.class), ArgumentMatchers.any(Executor.class));
        final Object actual = DataSources.waitForFinalResult(mDataSource);
        Assert.assertEquals(mFinalResult, actual);
        Mockito.verify(mCountDownLatch, Mockito.times(1)).await();
        Mockito.verify(mCountDownLatch, Mockito.times(1)).countDown();
    }

    @Test
    public void testWaitForFinalResult_whenOnlyIntermediateResult_thenNoUpdate() throws Throwable {
        Mockito.when(mDataSource.isFinished()).thenReturn(false);
        Mockito.when(mDataSource.getResult()).thenReturn(mIntermediateResult);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                DataSubscriber dataSubscriber = ((DataSubscriber) (args[0]));
                dataSubscriber.onNewResult(mDataSource);
                return null;
            }
        }).when(mDataSource).subscribe(ArgumentMatchers.any(DataSubscriber.class), ArgumentMatchers.any(Executor.class));
        // the mocked one falls through, but the real one waits with the countdown latch for isFinished
        final Object actual = DataSources.waitForFinalResult(mDataSource);
        Assert.assertEquals(null, actual);
        Mockito.verify(mCountDownLatch, Mockito.times(1)).await();
        Mockito.verify(mCountDownLatch, Mockito.never()).countDown();
    }

    @Test
    public void testWaitForFinalResult_whenCancelled_thenReturnNull() throws Throwable {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                DataSubscriber dataSubscriber = ((DataSubscriber) (args[0]));
                dataSubscriber.onCancellation(mDataSource);
                return null;
            }
        }).when(mDataSource).subscribe(ArgumentMatchers.any(DataSubscriber.class), ArgumentMatchers.any(Executor.class));
        final Object actual = DataSources.waitForFinalResult(mDataSource);
        Assert.assertEquals(null, actual);
        Mockito.verify(mCountDownLatch, Mockito.times(1)).await();
        Mockito.verify(mCountDownLatch, Mockito.times(1)).countDown();
    }

    @Test
    public void testWaitForFinalResult_whenFailed_thenThrow() throws Throwable {
        final Exception expectedException = new IOException("failure failure");
        Mockito.when(mDataSource.getFailureCause()).thenReturn(expectedException);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                DataSubscriber dataSubscriber = ((DataSubscriber) (args[0]));
                dataSubscriber.onFailure(mDataSource);
                return null;
            }
        }).when(mDataSource).subscribe(ArgumentMatchers.any(DataSubscriber.class), ArgumentMatchers.any(Executor.class));
        try {
            DataSources.waitForFinalResult(mDataSource);
            Assert.fail("expected exception");
        } catch (Exception exception) {
            Assert.assertEquals(expectedException, exception);
        }
        Mockito.verify(mCountDownLatch, Mockito.times(1)).await();
        Mockito.verify(mCountDownLatch, Mockito.times(1)).countDown();
    }
}

