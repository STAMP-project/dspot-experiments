package org.kairosdb.datastore.cassandra;


import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.junit.Assert;
import org.junit.Test;

import static RowCountEstimatingRowKeyResultSet.SAMPLE_SIZE;


public class RowCountEstimatingRowKeyResultSetTest {
    @Test
    public void testCreate_NumberOfRowsLessThanSampleSize() {
        ResultSet delegateResultSet = singleEntryResultSet();
        RowCountEstimatingRowKeyResultSet rowCountResultSet = RowCountEstimatingRowKeyResultSet.create(ImmutableList.of(delegateResultSet));
        Assert.assertFalse(rowCountResultSet.isEstimated());
        Assert.assertEquals(1, rowCountResultSet.getRowCount());
    }

    @Test
    public void testCreate_NumberOfRowsLargerThanSampleSize() {
        ResultSet delegateResultSet = sampleSizePlusOneResultSet();
        RowCountEstimatingRowKeyResultSet rowCountResultSet = RowCountEstimatingRowKeyResultSet.create(ImmutableList.of(delegateResultSet));
        Assert.assertTrue(rowCountResultSet.isEstimated());
        // The 2**32 keyspace divided up with steps of 1000 works out to a total sample size of ~4470272
        Assert.assertEquals(4470272, rowCountResultSet.getRowCount());
    }

    @Test
    public void testCreate_MultipleDelegateResultSets() {
        ResultSet delegateResultSetA = RowCountEstimatingRowKeyResultSetTest.resultSet(RowCountEstimatingRowKeyResultSetTest.row(0));
        ResultSet delegateResultSetB = RowCountEstimatingRowKeyResultSetTest.resultSet(RowCountEstimatingRowKeyResultSetTest.row(1));
        ResultSet delegateResultSetC = RowCountEstimatingRowKeyResultSetTest.resultSet(RowCountEstimatingRowKeyResultSetTest.row(0));
        RowCountEstimatingRowKeyResultSet rowCountResultSet = RowCountEstimatingRowKeyResultSet.create(ImmutableList.of(delegateResultSetA, delegateResultSetB, delegateResultSetC));
        Assert.assertFalse(rowCountResultSet.isEstimated());
        Assert.assertEquals(3, rowCountResultSet.getRowCount());
        Assert.assertEquals(3, Iterators.size(rowCountResultSet.iterator()));
    }

    @Test
    public void testOne_FullContentsInBuffer() {
        ResultSet resultSet = singleEntryResultSet();
        Assert.assertFalse(resultSet.isExhausted());
        Row firstRow = resultSet.one();
        Assert.assertEquals(0, firstRow.getInt(3));
        Assert.assertNull(resultSet.one());
        Assert.assertTrue(resultSet.isExhausted());
    }

    @Test
    public void testOne_PartialContentsInBuffer() {
        ResultSet resultSet = sampleSizePlusOneResultSet();
        for (int i = 0; i < ((SAMPLE_SIZE) + 1); i++) {
            Assert.assertFalse(resultSet.isExhausted());
            Assert.assertNotNull(resultSet.one());
        }
        Assert.assertTrue(resultSet.isExhausted());
        Assert.assertNull(resultSet.one());
    }

    @Test
    public void testIterator_FullContentsInBuffer() {
        Assert.assertEquals(1, Iterators.size(singleEntryResultSet().iterator()));
    }

    @Test
    public void testIterator_PartialContentsInBuffer() {
        Assert.assertEquals(((SAMPLE_SIZE) + 1), Iterators.size(sampleSizePlusOneResultSet().iterator()));
    }
}

