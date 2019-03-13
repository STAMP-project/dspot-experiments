/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore;


import ColumnType.BIGINT_TYPE_NAME;
import ColumnType.BINARY_TYPE_NAME;
import ColumnType.BOOLEAN_TYPE_NAME;
import ColumnType.DATE_TYPE_NAME;
import ColumnType.DOUBLE_TYPE_NAME;
import ColumnType.STRING_TYPE_NAME;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.BinaryColumnStatsData;
import org.apache.hadoop.hive.metastore.api.BooleanColumnStatsData;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsData;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.Date;
import org.apache.hadoop.hive.metastore.api.DateColumnStatsData;
import org.apache.hadoop.hive.metastore.api.DoubleColumnStatsData;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.LongColumnStatsData;
import org.apache.hadoop.hive.metastore.api.StringColumnStatsData;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MetastoreCheckinTest.class)
public class TestStats {
    private static final Logger LOG = LoggerFactory.getLogger(TestStats.class);

    private static final String NO_CAT = "DO_NOT_USE_A_CATALOG!";

    private IMetaStoreClient client;

    private Configuration conf;

    @Test
    public void tableInHiveCatalog() throws TException {
        String dbName = "db_table_stats";
        String tableName = "table_in_default_db_stats";
        Map<String, TestStats.Column> colMap = buildAllColumns();
        createMetadata(Warehouse.DEFAULT_CATALOG_NAME, dbName, tableName, null, null, colMap);
        compareStatsForTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tableName, colMap);
        dropStats(Warehouse.DEFAULT_CATALOG_NAME, dbName, tableName, null, colMap.keySet());
    }

    @Test
    public void tableOtherCatalog() throws TException {
        String catName = "cat_table_stats";
        String dbName = "other_cat_db_table_stats";
        String tableName = "table_in_default_db_stats";
        Map<String, TestStats.Column> colMap = buildAllColumns();
        createMetadata(catName, dbName, tableName, null, null, colMap);
        compareStatsForTable(catName, dbName, tableName, colMap);
        dropStats(catName, dbName, tableName, null, colMap.keySet());
    }

    @Test
    public void tableDeprecatedCalls() throws TException {
        String dbName = "old_db_table_stats";
        String tableName = "table_in_default_db_stats";
        Map<String, TestStats.Column> colMap = buildAllColumns();
        createMetadata(TestStats.NO_CAT, dbName, tableName, null, null, colMap);
        compareStatsForTable(TestStats.NO_CAT, dbName, tableName, colMap);
        dropStats(TestStats.NO_CAT, dbName, tableName, null, colMap.keySet());
    }

    private abstract class Column {
        final String colName;

        final String colType;

        Random rand = new Random();

        List<Long> maxLens;

        List<Long> numNulls;

        List<Long> numDvs;

        List<Double> avgLens;

        public Column(String colName, String colType) {
            this.colName = colName;
            this.colType = colType;
            maxLens = new ArrayList<>();
            numNulls = new ArrayList<>();
            avgLens = new ArrayList<>();
            numDvs = new ArrayList<>();
        }

        abstract ColumnStatisticsObj generate();

        abstract void compare(ColumnStatisticsData colstats, int offset);

        void compare(ColumnStatisticsObj obj, int offset) {
            compareCommon(obj);
            compare(obj.getStatsData(), offset);
        }

        abstract void compareAggr(ColumnStatisticsObj obj);

        void compareCommon(ColumnStatisticsObj obj) {
            Assert.assertEquals(colName, obj.getColName());
            Assert.assertEquals(colType, obj.getColType());
        }

        void compareCommon(FieldSchema col) {
            Assert.assertEquals(colName, col.getName());
            Assert.assertEquals(colType, col.getType());
        }

        long genMaxLen() {
            return genPositiveLong(maxLens);
        }

        long getMaxLen() {
            return maxLong(maxLens);
        }

        long genNumNulls() {
            return genPositiveLong(numNulls);
        }

        long genNumDvs() {
            return genPositiveLong(numDvs);
        }

        long getNumNulls() {
            return sumLong(numNulls);
        }

        long getNumDvs() {
            return maxLong(numDvs);
        }

        double genAvgLens() {
            return genDouble(avgLens);
        }

        double getAvgLen() {
            return maxDouble(avgLens);
        }

        protected long genNegativeLong(List<Long> addTo) {
            long val = rand.nextInt(100);
            if (val > 0)
                val *= -1;

            addTo.add(val);
            return val;
        }

        protected long genPositiveLong(List<Long> addTo) {
            long val = rand.nextInt(100);
            val = (Math.abs(val)) + 1;// make sure it isn't 0

            addTo.add(val);
            return val;
        }

        protected long maxLong(List<Long> maxOf) {
            long max = Long.MIN_VALUE;
            for (long maybe : maxOf)
                max = Math.max(max, maybe);

            return max;
        }

        protected long sumLong(List<Long> sumOf) {
            long sum = 0;
            for (long element : sumOf)
                sum += element;

            return sum;
        }

        protected double genDouble(List<Double> addTo) {
            double val = (rand.nextDouble()) * (rand.nextInt(100));
            addTo.add(val);
            return val;
        }

        protected double maxDouble(List<Double> maxOf) {
            double max = Double.MIN_VALUE;
            for (double maybe : maxOf)
                max = Math.max(max, maybe);

            return max;
        }
    }

    private class BinaryColumn extends TestStats.Column {
        public BinaryColumn() {
            super("bincol", BINARY_TYPE_NAME);
        }

        @Override
        ColumnStatisticsObj generate() {
            BinaryColumnStatsData binData = new BinaryColumnStatsData(genMaxLen(), genAvgLens(), genNumNulls());
            ColumnStatisticsData data = new ColumnStatisticsData();
            data.setBinaryStats(binData);
            return new ColumnStatisticsObj(colName, colType, data);
        }

        @Override
        void compare(ColumnStatisticsData colstats, int offset) {
            Assert.assertEquals("binary max length", maxLens.get(offset), ((Long) (colstats.getBinaryStats().getMaxColLen())));
            Assert.assertEquals("binary min length", avgLens.get(offset), colstats.getBinaryStats().getAvgColLen(), 0.01);
            Assert.assertEquals("binary num nulls", numNulls.get(offset), ((Long) (colstats.getBinaryStats().getNumNulls())));
        }

        @Override
        void compareAggr(ColumnStatisticsObj obj) {
            compareCommon(obj);
            Assert.assertEquals("aggr binary max length", getMaxLen(), obj.getStatsData().getBinaryStats().getMaxColLen());
            Assert.assertEquals("aggr binary min length", getAvgLen(), obj.getStatsData().getBinaryStats().getAvgColLen(), 0.01);
            Assert.assertEquals("aggr binary num nulls", getNumNulls(), obj.getStatsData().getBinaryStats().getNumNulls());
        }
    }

    private class BooleanColumn extends TestStats.Column {
        private List<Long> numTrues;

        private List<Long> numFalses;

        public BooleanColumn() {
            super("boolcol", BOOLEAN_TYPE_NAME);
            numTrues = new ArrayList<>();
            numFalses = new ArrayList<>();
        }

        @Override
        ColumnStatisticsObj generate() {
            BooleanColumnStatsData boolData = new BooleanColumnStatsData(genNumTrues(), genNumFalses(), genNumNulls());
            ColumnStatisticsData data = new ColumnStatisticsData();
            data.setBooleanStats(boolData);
            return new ColumnStatisticsObj(colName, colType, data);
        }

        @Override
        void compare(ColumnStatisticsData colstats, int offset) {
            Assert.assertEquals("boolean num trues", numTrues.get(offset), ((Long) (colstats.getBooleanStats().getNumTrues())));
            Assert.assertEquals("boolean num falses", numFalses.get(offset), ((Long) (colstats.getBooleanStats().getNumFalses())));
            Assert.assertEquals("boolean num nulls", numNulls.get(offset), ((Long) (colstats.getBooleanStats().getNumNulls())));
        }

        @Override
        void compareAggr(ColumnStatisticsObj obj) {
            compareCommon(obj);
            Assert.assertEquals("aggr boolean num trues", getNumTrues(), obj.getStatsData().getBooleanStats().getNumTrues());
            Assert.assertEquals("aggr boolean num falses", getNumFalses(), obj.getStatsData().getBooleanStats().getNumFalses());
            Assert.assertEquals("aggr boolean num nulls", getNumNulls(), obj.getStatsData().getBooleanStats().getNumNulls());
        }

        private long genNumTrues() {
            return genPositiveLong(numTrues);
        }

        private long genNumFalses() {
            return genPositiveLong(numFalses);
        }

        private long getNumTrues() {
            return sumLong(numTrues);
        }

        private long getNumFalses() {
            return sumLong(numFalses);
        }
    }

    private class DateColumn extends TestStats.Column {
        private List<Date> lowVals;

        private List<Date> highVals;

        public DateColumn() {
            super("datecol", DATE_TYPE_NAME);
            lowVals = new ArrayList();
            highVals = new ArrayList();
        }

        @Override
        ColumnStatisticsObj generate() {
            DateColumnStatsData dateData = new DateColumnStatsData(genNumNulls(), genNumDvs());
            dateData.setLowValue(genLowValue());
            dateData.setHighValue(genHighValue());
            ColumnStatisticsData data = new ColumnStatisticsData();
            data.setDateStats(dateData);
            return new ColumnStatisticsObj(colName, colType, data);
        }

        @Override
        void compare(ColumnStatisticsData colstats, int offset) {
            Assert.assertEquals("date num nulls", numNulls.get(offset), ((Long) (colstats.getDateStats().getNumNulls())));
            Assert.assertEquals("date num dvs", numDvs.get(offset), ((Long) (colstats.getDateStats().getNumDVs())));
            Assert.assertEquals("date low val", lowVals.get(offset), colstats.getDateStats().getLowValue());
            Assert.assertEquals("date high val", highVals.get(offset), colstats.getDateStats().getHighValue());
        }

        @Override
        void compareAggr(ColumnStatisticsObj obj) {
            compareCommon(obj);
            Assert.assertEquals("aggr date num nulls", getNumNulls(), obj.getStatsData().getDateStats().getNumNulls());
            Assert.assertEquals("aggr date num dvs", getNumDvs(), obj.getStatsData().getDateStats().getNumDVs());
            Assert.assertEquals("aggr date low val", getLowVal(), obj.getStatsData().getDateStats().getLowValue());
            Assert.assertEquals("aggr date high val", getHighVal(), obj.getStatsData().getDateStats().getHighValue());
        }

        private Date genLowValue() {
            Date d = new Date(((rand.nextInt(100)) * (-1)));
            lowVals.add(d);
            return d;
        }

        private Date genHighValue() {
            Date d = new Date(rand.nextInt(200));
            highVals.add(d);
            return d;
        }

        private Date getLowVal() {
            long min = Long.MAX_VALUE;
            for (Date d : lowVals)
                min = Math.min(min, d.getDaysSinceEpoch());

            return new Date(min);
        }

        private Date getHighVal() {
            long max = Long.MIN_VALUE;
            for (Date d : highVals)
                max = Math.max(max, d.getDaysSinceEpoch());

            return new Date(max);
        }
    }

    private class DoubleColumn extends TestStats.Column {
        List<Double> lowVals;

        List<Double> highVals;

        public DoubleColumn() {
            super("doublecol", DOUBLE_TYPE_NAME);
            lowVals = new ArrayList<>();
            highVals = new ArrayList<>();
        }

        @Override
        ColumnStatisticsObj generate() {
            DoubleColumnStatsData doubleData = new DoubleColumnStatsData(genNumNulls(), genNumDvs());
            doubleData.setLowValue(genLowVal());
            doubleData.setHighValue(genHighVal());
            ColumnStatisticsData data = new ColumnStatisticsData();
            data.setDoubleStats(doubleData);
            return new ColumnStatisticsObj(colName, colType, data);
        }

        @Override
        void compare(ColumnStatisticsData colstats, int offset) {
            Assert.assertEquals("double num nulls", numNulls.get(offset), ((Long) (colstats.getDoubleStats().getNumNulls())));
            Assert.assertEquals("double num dvs", numDvs.get(offset), ((Long) (colstats.getDoubleStats().getNumDVs())));
            Assert.assertEquals("double low val", lowVals.get(offset), colstats.getDoubleStats().getLowValue(), 0.01);
            Assert.assertEquals("double high val", highVals.get(offset), colstats.getDoubleStats().getHighValue(), 0.01);
        }

        @Override
        void compareAggr(ColumnStatisticsObj obj) {
            compareCommon(obj);
            Assert.assertEquals("aggr double num nulls", getNumNulls(), obj.getStatsData().getDoubleStats().getNumNulls());
            Assert.assertEquals("aggr double num dvs", getNumDvs(), obj.getStatsData().getDoubleStats().getNumDVs());
            Assert.assertEquals("aggr double low val", getLowVal(), obj.getStatsData().getDoubleStats().getLowValue(), 0.01);
            Assert.assertEquals("aggr double high val", getHighVal(), obj.getStatsData().getDoubleStats().getHighValue(), 0.01);
        }

        private double genLowVal() {
            return genDouble(lowVals);
        }

        private double genHighVal() {
            return genDouble(highVals);
        }

        private double getLowVal() {
            double min = Double.MAX_VALUE;
            for (Double d : lowVals)
                min = Math.min(min, d);

            return min;
        }

        private double getHighVal() {
            return maxDouble(highVals);
        }
    }

    private class LongColumn extends TestStats.Column {
        List<Long> lowVals;

        List<Long> highVals;

        public LongColumn() {
            super("bigintcol", BIGINT_TYPE_NAME);
            lowVals = new ArrayList<>();
            highVals = new ArrayList<>();
        }

        @Override
        ColumnStatisticsObj generate() {
            LongColumnStatsData longData = new LongColumnStatsData(genNumNulls(), genNumDvs());
            longData.setLowValue(genLowVal());
            longData.setHighValue(genHighVal());
            ColumnStatisticsData data = new ColumnStatisticsData();
            data.setLongStats(longData);
            return new ColumnStatisticsObj(colName, colType, data);
        }

        @Override
        void compare(ColumnStatisticsData colstats, int offset) {
            Assert.assertEquals("long num nulls", numNulls.get(offset), ((Long) (colstats.getLongStats().getNumNulls())));
            Assert.assertEquals("long num dvs", numDvs.get(offset), ((Long) (colstats.getLongStats().getNumDVs())));
            Assert.assertEquals("long low val", ((long) (lowVals.get(offset))), colstats.getLongStats().getLowValue());
            Assert.assertEquals("long high val", ((long) (highVals.get(offset))), colstats.getLongStats().getHighValue());
        }

        @Override
        void compareAggr(ColumnStatisticsObj obj) {
            compareCommon(obj);
            Assert.assertEquals("aggr long num nulls", getNumNulls(), obj.getStatsData().getLongStats().getNumNulls());
            Assert.assertEquals("aggr long num dvs", getNumDvs(), obj.getStatsData().getLongStats().getNumDVs());
            Assert.assertEquals("aggr long low val", getLowVal(), obj.getStatsData().getLongStats().getLowValue());
            Assert.assertEquals("aggr long high val", getHighVal(), obj.getStatsData().getLongStats().getHighValue());
        }

        private long genLowVal() {
            return genNegativeLong(lowVals);
        }

        private long genHighVal() {
            return genPositiveLong(highVals);
        }

        private long getLowVal() {
            long min = Long.MAX_VALUE;
            for (Long val : lowVals)
                min = Math.min(min, val);

            return min;
        }

        private long getHighVal() {
            return maxLong(highVals);
        }
    }

    private class StringColumn extends TestStats.Column {
        public StringColumn() {
            super("strcol", STRING_TYPE_NAME);
        }

        @Override
        ColumnStatisticsObj generate() {
            StringColumnStatsData strData = new StringColumnStatsData(genMaxLen(), genAvgLens(), genNumNulls(), genNumDvs());
            ColumnStatisticsData data = new ColumnStatisticsData();
            data.setStringStats(strData);
            return new ColumnStatisticsObj(colName, colType, data);
        }

        @Override
        void compare(ColumnStatisticsData colstats, int offset) {
            Assert.assertEquals("str num nulls", numNulls.get(offset), ((Long) (colstats.getStringStats().getNumNulls())));
            Assert.assertEquals("str num dvs", numDvs.get(offset), ((Long) (colstats.getStringStats().getNumDVs())));
            Assert.assertEquals("str low val", ((long) (maxLens.get(offset))), colstats.getStringStats().getMaxColLen());
            Assert.assertEquals("str high val", avgLens.get(offset), colstats.getStringStats().getAvgColLen(), 0.01);
        }

        @Override
        void compareAggr(ColumnStatisticsObj obj) {
            compareCommon(obj);
            Assert.assertEquals("aggr str num nulls", getNumNulls(), obj.getStatsData().getStringStats().getNumNulls());
            Assert.assertEquals("aggr str num dvs", getNumDvs(), obj.getStatsData().getStringStats().getNumDVs());
            Assert.assertEquals("aggr str low val", getMaxLen(), obj.getStatsData().getStringStats().getMaxColLen());
            Assert.assertEquals("aggr str high val", getAvgLen(), obj.getStatsData().getStringStats().getAvgColLen(), 0.01);
        }
    }
}

