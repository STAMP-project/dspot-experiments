/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.hive.metastore.columnstats.merge;


import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.Decimal;
import org.apache.hadoop.hive.metastore.api.utils.DecimalUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MetastoreUnitTest.class)
public class DecimalColumnStatsMergerTest {
    private static final Decimal DECIMAL_3 = DecimalUtils.getDecimal(3, 0);

    private static final Decimal DECIMAL_5 = DecimalUtils.getDecimal(5, 0);

    private static final Decimal DECIMAL_20 = DecimalUtils.getDecimal(2, 1);

    private DecimalColumnStatsMerger merger = new DecimalColumnStatsMerger();

    @Test
    public void testMergeNullMinMaxValues() {
        ColumnStatisticsObj objNulls = new ColumnStatisticsObj();
        createData(objNulls, null, null);
        merger.merge(objNulls, objNulls);
        Assert.assertNull(objNulls.getStatsData().getDecimalStats().getLowValue());
        Assert.assertNull(objNulls.getStatsData().getDecimalStats().getHighValue());
    }

    @Test
    public void testMergeNonNullAndNullLowerValuesOldIsNull() {
        ColumnStatisticsObj oldObj = new ColumnStatisticsObj();
        createData(oldObj, null, null);
        ColumnStatisticsObj newObj = new ColumnStatisticsObj();
        createData(newObj, DecimalColumnStatsMergerTest.DECIMAL_3, null);
        merger.merge(oldObj, newObj);
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, oldObj.getStatsData().getDecimalStats().getLowValue());
    }

    @Test
    public void testMergeNonNullAndNullLowerValuesNewIsNull() {
        ColumnStatisticsObj oldObj = new ColumnStatisticsObj();
        createData(oldObj, DecimalColumnStatsMergerTest.DECIMAL_3, null);
        ColumnStatisticsObj newObj = new ColumnStatisticsObj();
        createData(newObj, null, null);
        merger.merge(oldObj, newObj);
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, oldObj.getStatsData().getDecimalStats().getLowValue());
    }

    @Test
    public void testMergeNonNullAndNullHigherValuesOldIsNull() {
        ColumnStatisticsObj oldObj = new ColumnStatisticsObj();
        createData(oldObj, null, null);
        ColumnStatisticsObj newObj = new ColumnStatisticsObj();
        createData(newObj, null, DecimalColumnStatsMergerTest.DECIMAL_3);
        merger.merge(oldObj, newObj);
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, oldObj.getStatsData().getDecimalStats().getHighValue());
    }

    @Test
    public void testMergeNonNullAndNullHigherValuesNewIsNull() {
        ColumnStatisticsObj oldObj = new ColumnStatisticsObj();
        createData(oldObj, null, DecimalColumnStatsMergerTest.DECIMAL_3);
        ColumnStatisticsObj newObj = new ColumnStatisticsObj();
        createData(newObj, null, null);
        merger.merge(oldObj, newObj);
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, oldObj.getStatsData().getDecimalStats().getHighValue());
    }

    @Test
    public void testMergeLowValuesFirstWins() {
        ColumnStatisticsObj oldObj = new ColumnStatisticsObj();
        createData(oldObj, DecimalColumnStatsMergerTest.DECIMAL_3, null);
        ColumnStatisticsObj newObj = new ColumnStatisticsObj();
        createData(newObj, DecimalColumnStatsMergerTest.DECIMAL_5, null);
        merger.merge(oldObj, newObj);
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, oldObj.getStatsData().getDecimalStats().getLowValue());
    }

    @Test
    public void testMergeLowValuesSecondWins() {
        ColumnStatisticsObj oldObj = new ColumnStatisticsObj();
        createData(oldObj, DecimalColumnStatsMergerTest.DECIMAL_5, null);
        ColumnStatisticsObj newObj = new ColumnStatisticsObj();
        createData(newObj, DecimalColumnStatsMergerTest.DECIMAL_3, null);
        merger.merge(oldObj, newObj);
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, oldObj.getStatsData().getDecimalStats().getLowValue());
    }

    @Test
    public void testMergeHighValuesFirstWins() {
        ColumnStatisticsObj oldObj = new ColumnStatisticsObj();
        createData(oldObj, null, DecimalColumnStatsMergerTest.DECIMAL_5);
        ColumnStatisticsObj newObj = new ColumnStatisticsObj();
        createData(newObj, null, DecimalColumnStatsMergerTest.DECIMAL_3);
        merger.merge(oldObj, newObj);
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_5, oldObj.getStatsData().getDecimalStats().getHighValue());
    }

    @Test
    public void testMergeHighValuesSecondWins() {
        ColumnStatisticsObj oldObj = new ColumnStatisticsObj();
        createData(oldObj, null, DecimalColumnStatsMergerTest.DECIMAL_3);
        ColumnStatisticsObj newObj = new ColumnStatisticsObj();
        createData(newObj, null, DecimalColumnStatsMergerTest.DECIMAL_5);
        merger.merge(oldObj, newObj);
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_5, oldObj.getStatsData().getDecimalStats().getHighValue());
    }

    @Test
    public void testDecimalCompareEqual() {
        Assert.assertTrue(DecimalColumnStatsMergerTest.DECIMAL_3.equals(DecimalColumnStatsMergerTest.DECIMAL_3));
    }

    @Test
    public void testDecimalCompareDoesntEqual() {
        Assert.assertTrue((!(DecimalColumnStatsMergerTest.DECIMAL_3.equals(DecimalColumnStatsMergerTest.DECIMAL_5))));
    }

    @Test
    public void testCompareSimple() {
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_5, merger.getMax(DecimalColumnStatsMergerTest.DECIMAL_3, DecimalColumnStatsMergerTest.DECIMAL_5));
    }

    @Test
    public void testCompareSimpleFlipped() {
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_5, merger.getMax(DecimalColumnStatsMergerTest.DECIMAL_5, DecimalColumnStatsMergerTest.DECIMAL_3));
    }

    @Test
    public void testCompareSimpleReversed() {
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, merger.getMin(DecimalColumnStatsMergerTest.DECIMAL_3, DecimalColumnStatsMergerTest.DECIMAL_5));
    }

    @Test
    public void testCompareSimpleFlippedReversed() {
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, merger.getMin(DecimalColumnStatsMergerTest.DECIMAL_5, DecimalColumnStatsMergerTest.DECIMAL_3));
    }

    @Test
    public void testCompareUnscaledValue() {
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_20, merger.getMax(DecimalColumnStatsMergerTest.DECIMAL_3, DecimalColumnStatsMergerTest.DECIMAL_20));
    }

    @Test
    public void testCompareNullsMin() {
        Assert.assertNull(merger.getMin(null, null));
    }

    @Test
    public void testCompareNullsMax() {
        Assert.assertNull(merger.getMax(null, null));
    }

    @Test
    public void testCompareFirstNullMin() {
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, merger.getMin(null, DecimalColumnStatsMergerTest.DECIMAL_3));
    }

    @Test
    public void testCompareSecondNullMin() {
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, merger.getMin(DecimalColumnStatsMergerTest.DECIMAL_3, null));
    }

    @Test
    public void testCompareFirstNullMax() {
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, merger.getMax(null, DecimalColumnStatsMergerTest.DECIMAL_3));
    }

    @Test
    public void testCompareSecondNullMax() {
        Assert.assertEquals(DecimalColumnStatsMergerTest.DECIMAL_3, merger.getMax(DecimalColumnStatsMergerTest.DECIMAL_3, null));
    }
}

