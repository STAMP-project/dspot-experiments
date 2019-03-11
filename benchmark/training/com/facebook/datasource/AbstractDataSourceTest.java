/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.datasource;


import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class AbstractDataSourceTest {
    public interface Value {
        public void close();
    }

    private static class FakeAbstractDataSource extends AbstractDataSource<AbstractDataSourceTest.Value> {
        @Override
        public boolean setResult(@Nullable
        AbstractDataSourceTest.Value value, boolean isLast) {
            return super.setResult(value, isLast);
        }

        @Override
        public boolean setFailure(Throwable throwable) {
            return super.setFailure(throwable);
        }

        @Override
        public boolean setProgress(float progress) {
            return super.setProgress(progress);
        }

        @Override
        public void closeResult(AbstractDataSourceTest.Value result) {
            result.close();
        }
    }

    private Executor mExecutor1;

    private Executor mExecutor2;

    private DataSubscriber<AbstractDataSourceTest.Value> mDataSubscriber1;

    private DataSubscriber<AbstractDataSourceTest.Value> mDataSubscriber2;

    private AbstractDataSourceTest.FakeAbstractDataSource mDataSource;

    @Test
    public void testInitialState() {
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    @Test
    public void testLifeCycle_LastResult_Close() {
        subscribe();
        // last result
        AbstractDataSourceTest.Value value = Mockito.mock(AbstractDataSourceTest.Value.class);
        mDataSource.setResult(value, DataSourceTestUtils.LAST);
        verifySubscribers(DataSourceTestUtils.ON_NEW_RESULT);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, value, DataSourceTestUtils.NOT_FAILED, null);
        // close
        close();
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    @Test
    public void testLifeCycle_Failure_Close() {
        subscribe();
        // failure
        Throwable throwable = Mockito.mock(Throwable.class);
        mDataSource.setFailure(throwable);
        verifySubscribers(DataSourceTestUtils.ON_FAILURE);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        // close
        close();
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
    }

    @Test
    public void testLifeCycle_IntermediateResult_LastResult_Close() {
        subscribe();
        // intermediate result
        AbstractDataSourceTest.Value value1 = Mockito.mock(AbstractDataSourceTest.Value.class);
        mDataSource.setResult(value1, DataSourceTestUtils.INTERMEDIATE);
        verifySubscribers(DataSourceTestUtils.ON_NEW_RESULT);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, value1, DataSourceTestUtils.NOT_FAILED, null);
        // last result
        AbstractDataSourceTest.Value value = Mockito.mock(AbstractDataSourceTest.Value.class);
        mDataSource.setResult(value, DataSourceTestUtils.LAST);
        verifySubscribers(DataSourceTestUtils.ON_NEW_RESULT);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, value, DataSourceTestUtils.NOT_FAILED, null);
        // close
        close();
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    @Test
    public void testLifeCycle_IntermediateResult_Failure_Close() {
        subscribe();
        // intermediate result
        AbstractDataSourceTest.Value value1 = Mockito.mock(AbstractDataSourceTest.Value.class);
        mDataSource.setResult(value1, DataSourceTestUtils.INTERMEDIATE);
        verifySubscribers(DataSourceTestUtils.ON_NEW_RESULT);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITH_RESULT, value1, DataSourceTestUtils.NOT_FAILED, null);
        // failure
        Throwable throwable = Mockito.mock(Throwable.class);
        mDataSource.setFailure(throwable);
        verifySubscribers(DataSourceTestUtils.ON_FAILURE);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, value1, DataSourceTestUtils.FAILED, throwable);
        // close
        close();
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
    }

    @Test
    public void testLifeCycle_AfterSuccess() {
        subscribe();
        // success
        AbstractDataSourceTest.Value value = Mockito.mock(AbstractDataSourceTest.Value.class);
        mDataSource.setResult(value, DataSourceTestUtils.LAST);
        verifySubscribers(DataSourceTestUtils.ON_NEW_RESULT);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, value, DataSourceTestUtils.NOT_FAILED, null);
        // try intermediate
        mDataSource.setResult(Mockito.mock(AbstractDataSourceTest.Value.class), DataSourceTestUtils.INTERMEDIATE);
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, value, DataSourceTestUtils.NOT_FAILED, null);
        // try last
        mDataSource.setResult(Mockito.mock(AbstractDataSourceTest.Value.class), DataSourceTestUtils.LAST);
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, value, DataSourceTestUtils.NOT_FAILED, null);
        // try failure
        mDataSource.setFailure(Mockito.mock(Throwable.class));
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITH_RESULT, value, DataSourceTestUtils.NOT_FAILED, null);
    }

    @Test
    public void testLifeCycle_AfterFailure() {
        subscribe();
        // failure
        Throwable throwable = Mockito.mock(Throwable.class);
        mDataSource.setFailure(throwable);
        verifySubscribers(DataSourceTestUtils.ON_FAILURE);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        // try intermediate
        mDataSource.setResult(Mockito.mock(AbstractDataSourceTest.Value.class), DataSourceTestUtils.INTERMEDIATE);
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        // try last
        mDataSource.setResult(Mockito.mock(AbstractDataSourceTest.Value.class), DataSourceTestUtils.LAST);
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
        // try failure
        mDataSource.setFailure(Mockito.mock(Throwable.class));
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.NOT_CLOSED, DataSourceTestUtils.FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.FAILED, throwable);
    }

    @Test
    public void testLifeCycle_AfterClose() {
        subscribe();
        // close
        close();
        verifySubscribers(DataSourceTestUtils.ON_CANCELLATION);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        // try intermediate
        mDataSource.setResult(Mockito.mock(AbstractDataSourceTest.Value.class), DataSourceTestUtils.INTERMEDIATE);
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        // try last
        mDataSource.setResult(Mockito.mock(AbstractDataSourceTest.Value.class), DataSourceTestUtils.LAST);
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
        // try failure
        mDataSource.setFailure(Mockito.mock(Throwable.class));
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
        DataSourceTestUtils.verifyState(mDataSource, DataSourceTestUtils.CLOSED, DataSourceTestUtils.NOT_FINISHED, DataSourceTestUtils.WITHOUT_RESULT, null, DataSourceTestUtils.NOT_FAILED, null);
    }

    @Test
    public void testSubscribe_InProgress_WithoutResult() {
        subscribe();
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
    }

    @Test
    public void testSubscribe_InProgress_WithResult() {
        mDataSource.setResult(Mockito.mock(AbstractDataSourceTest.Value.class), DataSourceTestUtils.INTERMEDIATE);
        subscribe();
        verifySubscribers(DataSourceTestUtils.ON_NEW_RESULT);
    }

    @Test
    public void testSubscribe_Finished_WithoutResult() {
        mDataSource.setResult(null, DataSourceTestUtils.LAST);
        subscribe();
        verifySubscribers(DataSourceTestUtils.ON_NEW_RESULT);
    }

    @Test
    public void testSubscribe_Finished_WithResult() {
        mDataSource.setResult(Mockito.mock(AbstractDataSourceTest.Value.class), DataSourceTestUtils.LAST);
        subscribe();
        verifySubscribers(DataSourceTestUtils.ON_NEW_RESULT);
    }

    @Test
    public void testSubscribe_Failed_WithoutResult() {
        mDataSource.setFailure(Mockito.mock(Throwable.class));
        subscribe();
        verifySubscribers(DataSourceTestUtils.ON_FAILURE);
    }

    @Test
    public void testSubscribe_Failed_WithResult() {
        mDataSource.setResult(Mockito.mock(AbstractDataSourceTest.Value.class), DataSourceTestUtils.INTERMEDIATE);
        mDataSource.setFailure(Mockito.mock(Throwable.class));
        subscribe();
        verifySubscribers(DataSourceTestUtils.ON_FAILURE);
    }

    @Test
    public void testSubscribe_Closed_AfterSuccess() {
        mDataSource.setResult(Mockito.mock(AbstractDataSourceTest.Value.class), DataSourceTestUtils.LAST);
        close();
        subscribe();
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
    }

    @Test
    public void testSubscribe_Closed_AfterFailure() {
        mDataSource.setFailure(Mockito.mock(Throwable.class));
        close();
        subscribe();
        verifySubscribers(DataSourceTestUtils.NO_INTERACTIONS);
    }

    @Test
    public void testCloseResult() {
        AbstractDataSourceTest.Value value1 = Mockito.mock(AbstractDataSourceTest.Value.class);
        mDataSource.setResult(value1, false);
        AbstractDataSourceTest.Value value2 = Mockito.mock(AbstractDataSourceTest.Value.class);
        mDataSource.setResult(value2, false);
        Mockito.verify(value1).close();
        Mockito.verify(value2, Mockito.never()).close();
        AbstractDataSourceTest.Value value3 = Mockito.mock(AbstractDataSourceTest.Value.class);
        mDataSource.setResult(value3, false);
        Mockito.verify(value2).close();
        Mockito.verify(value3, Mockito.never()).close();
        close();
        Mockito.verify(value3).close();
    }
}

