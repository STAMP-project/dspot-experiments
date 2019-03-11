/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.datasource;


import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.datasource.DataSubscriber;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;


@RunWith(RobolectricTestRunner.class)
public class ListDataSourceTest {
    private SettableDataSource<Integer> mSettableDataSource1;

    private SettableDataSource<Integer> mSettableDataSource2;

    private ListDataSource<Integer> mListDataSource;

    private CloseableReference<Integer> mRef1;

    private CloseableReference<Integer> mRef2;

    private RuntimeException mRuntimeException;

    @Mock
    public ResourceReleaser<Integer> mResourceReleaser;

    @Mock
    public DataSubscriber<List<CloseableReference<Integer>>> mDataSubscriber;

    @Test
    public void testFirstResolvedSecondNot() {
        resolveFirstDataSource();
        assertDataSourceNotResolved();
    }

    @Test
    public void testSecondResolvedFirstNot() {
        resolveSecondDataSource();
        assertDataSourceNotResolved();
    }

    @Test
    public void testFirstCancelledSecondNot() {
        cancelFirstDataSource();
        assertDataSourceCancelled();
    }

    @Test
    public void testSecondCancelledFirstNot() {
        cancelSecondDataSource();
        assertDataSourceCancelled();
    }

    @Test
    public void testFirstFailedSecondNot() {
        failFirstDataSource();
        assertDataSourceFailed();
    }

    @Test
    public void testSecondFailedFirstNot() {
        failSecondDataSource();
        assertDataSourceFailed();
    }

    @Test
    public void testFirstResolvedSecondFailed() {
        resolveFirstDataSource();
        failSecondDataSource();
        assertDataSourceFailed();
    }

    @Test
    public void testSecondResolvedFirstFailed() {
        failFirstDataSource();
        resolveSecondDataSource();
        assertDataSourceFailed();
    }

    @Test
    public void testFirstResolvedSecondCancelled() {
        resolveFirstDataSource();
        cancelSecondDataSource();
        assertDataSourceCancelled();
    }

    @Test
    public void testSecondResolvedFirstCancelled() {
        resolveSecondDataSource();
        cancelFirstDataSource();
        assertDataSourceCancelled();
    }

    @Test
    public void testFirstAndSecondResolved() {
        resolveFirstDataSource();
        resolveSecondDataSource();
        assertDataSourceResolved();
    }

    @Test
    public void testCloseClosesAllDataSources() {
        mListDataSource.close();
        Assert.assertTrue(mSettableDataSource1.isClosed());
        Assert.assertTrue(mSettableDataSource2.isClosed());
    }
}

