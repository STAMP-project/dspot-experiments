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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore.utils;


import MetaStoreServerUtils.StorageDescriptorKey.UNSET_KEY;
import MetastoreConf.ConfVars.EVENT_NOTIFICATION_PARAMETERS_EXCLUDE_PATTERNS;
import StatsSetupConst.DO_NOT_UPDATE_STATS;
import StatsSetupConst.TASK;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.hive.common.StatsSetupConst;
import org.apache.hadoop.hive.metastore.Warehouse;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.EnvironmentContext;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;
import org.apache.hadoop.hive.metastore.api.PartitionSpecWithSharedSD;
import org.apache.hadoop.hive.metastore.api.PartitionWithoutSD;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.client.builder.PartitionBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.apache.thrift.TException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(MetastoreUnitTest.class)
public class TestMetaStoreServerUtils {
    private static final String DB_NAME = "db1";

    private static final String TABLE_NAME = "tbl1";

    private final Map<String, String> paramsWithStats = ImmutableMap.of(StatsSetupConst.NUM_FILES, "1", StatsSetupConst.TOTAL_SIZE, "2", StatsSetupConst.NUM_ERASURE_CODED_FILES, "0");

    private Database db;

    public TestMetaStoreServerUtils() {
        try {
            db = new DatabaseBuilder().setName(TestMetaStoreServerUtils.DB_NAME).build(null);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTrimMapNullsXform() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("akey", "aval");
        m.put("blank", "");
        m.put("null", null);
        Map<String, String> expected = ImmutableMap.of("akey", "aval", "blank", "", "null", "");
        Map<String, String> xformed = MetaStoreServerUtils.trimMapNulls(m, true);
        Assert.assertThat(xformed, Is.is(expected));
    }

    @Test
    public void testTrimMapNullsPrune() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("akey", "aval");
        m.put("blank", "");
        m.put("null", null);
        Map<String, String> expected = ImmutableMap.of("akey", "aval", "blank", "");
        Map<String, String> pruned = MetaStoreServerUtils.trimMapNulls(m, false);
        Assert.assertThat(pruned, Is.is(expected));
    }

    @Test
    public void testcolumnsIncludedByNameType() {
        FieldSchema col1 = new FieldSchema("col1", "string", "col1 comment");
        FieldSchema col1a = new FieldSchema("col1", "string", "col1 but with a different comment");
        FieldSchema col2 = new FieldSchema("col2", "string", "col2 comment");
        FieldSchema col3 = new FieldSchema("col3", "string", "col3 comment");
        Assert.assertTrue(MetaStoreServerUtils.columnsIncludedByNameType(Arrays.asList(col1), Arrays.asList(col1)));
        Assert.assertTrue(MetaStoreServerUtils.columnsIncludedByNameType(Arrays.asList(col1), Arrays.asList(col1a)));
        Assert.assertTrue(MetaStoreServerUtils.columnsIncludedByNameType(Arrays.asList(col1, col2), Arrays.asList(col1, col2)));
        Assert.assertTrue(MetaStoreServerUtils.columnsIncludedByNameType(Arrays.asList(col1, col2), Arrays.asList(col2, col1)));
        Assert.assertTrue(MetaStoreServerUtils.columnsIncludedByNameType(Arrays.asList(col1, col2), Arrays.asList(col1, col2, col3)));
        Assert.assertTrue(MetaStoreServerUtils.columnsIncludedByNameType(Arrays.asList(col1, col2), Arrays.asList(col3, col2, col1)));
        Assert.assertFalse(MetaStoreServerUtils.columnsIncludedByNameType(Arrays.asList(col1, col2), Arrays.asList(col1)));
    }

    /**
     * Verify that updateTableStatsSlow really updates table statistics.
     * The test does the following:
     * <ol>
     *   <li>Create database</li>
     *   <li>Create unpartitioned table</li>
     *   <li>Create unpartitioned table which has params</li>
     *   <li>Call updateTableStatsSlow with arguments which should cause stats calculation</li>
     *   <li>Verify table statistics using mocked warehouse</li>
     *   <li>Create table which already have stats</li>
     *   <li>Call updateTableStatsSlow forcing stats recompute</li>
     *   <li>Verify table statistics using mocked warehouse</li>
     *   <li>Verifies behavior when STATS_GENERATED is set in environment context</li>
     * </ol>
     */
    @Test
    public void testUpdateTableStatsSlow_statsUpdated() throws TException {
        long fileLength = 5;
        // Create database and table
        Table tbl = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").build(null);
        // Set up mock warehouse
        FileStatus fs1 = TestMetaStoreServerUtils.getFileStatus(1, true, 2, 3, 4, "/tmp/0", false);
        FileStatus fs2 = TestMetaStoreServerUtils.getFileStatus(fileLength, false, 3, 4, 5, "/tmp/1", true);
        FileStatus fs3 = TestMetaStoreServerUtils.getFileStatus(fileLength, false, 3, 4, 5, "/tmp/1", false);
        List<FileStatus> fileStatus = Arrays.asList(fs1, fs2, fs3);
        Warehouse wh = Mockito.mock(Warehouse.class);
        Mockito.when(wh.getFileStatusesForUnpartitionedTable(db, tbl)).thenReturn(fileStatus);
        Map<String, String> expected = ImmutableMap.of(StatsSetupConst.NUM_FILES, "2", StatsSetupConst.TOTAL_SIZE, String.valueOf((2 * fileLength)), StatsSetupConst.NUM_ERASURE_CODED_FILES, "1");
        MetaStoreServerUtils.updateTableStatsSlow(db, tbl, wh, false, false, null);
        Assert.assertThat(tbl.getParameters(), Is.is(expected));
        // Verify that when stats are already present and forceRecompute is specified they are recomputed
        Table tbl1 = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").addTableParam(StatsSetupConst.NUM_FILES, "0").addTableParam(StatsSetupConst.TOTAL_SIZE, "0").build(null);
        Mockito.when(wh.getFileStatusesForUnpartitionedTable(db, tbl1)).thenReturn(fileStatus);
        MetaStoreServerUtils.updateTableStatsSlow(db, tbl1, wh, false, true, null);
        Assert.assertThat(tbl1.getParameters(), Is.is(expected));
        // Verify that COLUMN_STATS_ACCURATE is removed from params
        Table tbl2 = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").addTableParam(StatsSetupConst.COLUMN_STATS_ACCURATE, "true").build(null);
        Mockito.when(wh.getFileStatusesForUnpartitionedTable(db, tbl2)).thenReturn(fileStatus);
        MetaStoreServerUtils.updateTableStatsSlow(db, tbl2, wh, false, true, null);
        Assert.assertThat(tbl2.getParameters(), Is.is(expected));
        EnvironmentContext context = new EnvironmentContext(ImmutableMap.of(StatsSetupConst.STATS_GENERATED, TASK));
        // Verify that if environment context has STATS_GENERATED set to task,
        // COLUMN_STATS_ACCURATE in params is set to correct value
        Table tbl3 = // The value doesn't matter
        new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").addTableParam(StatsSetupConst.COLUMN_STATS_ACCURATE, "foo").build(null);
        Mockito.when(wh.getFileStatusesForUnpartitionedTable(db, tbl3)).thenReturn(fileStatus);
        MetaStoreServerUtils.updateTableStatsSlow(db, tbl3, wh, false, true, context);
        Map<String, String> expected1 = ImmutableMap.of(StatsSetupConst.NUM_FILES, "2", StatsSetupConst.TOTAL_SIZE, String.valueOf((2 * fileLength)), StatsSetupConst.NUM_ERASURE_CODED_FILES, "1", StatsSetupConst.COLUMN_STATS_ACCURATE, "{\"BASIC_STATS\":\"true\"}");
        Assert.assertThat(tbl3.getParameters(), Is.is(expected1));
    }

    /**
     * Verify that the call to updateTableStatsSlow() removes DO_NOT_UPDATE_STATS from table params.
     */
    @Test
    public void testUpdateTableStatsSlow_removesDoNotUpdateStats() throws TException {
        // Create database and table
        Table tbl = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").addTableParam(DO_NOT_UPDATE_STATS, "true").build(null);
        Table tbl1 = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").addTableParam(DO_NOT_UPDATE_STATS, "false").build(null);
        Warehouse wh = Mockito.mock(Warehouse.class);
        MetaStoreServerUtils.updateTableStatsSlow(db, tbl, wh, false, true, null);
        Assert.assertThat(tbl.getParameters(), Is.is(Collections.emptyMap()));
        Mockito.verify(wh, Mockito.never()).getFileStatusesForUnpartitionedTable(db, tbl);
        MetaStoreServerUtils.updateTableStatsSlow(db, tbl1, wh, true, false, null);
        Assert.assertThat(tbl.getParameters(), Is.is(Collections.emptyMap()));
        Mockito.verify(wh, Mockito.never()).getFileStatusesForUnpartitionedTable(db, tbl1);
    }

    /**
     * Verify that updateTableStatsSlow() does not calculate table statistics when
     * <ol>
     *   <li>newDir is true</li>
     *   <li>Table is partitioned</li>
     *   <li>Stats are already present and forceRecompute isn't set</li>
     * </ol>
     */
    @Test
    public void testUpdateTableStatsSlow_doesNotUpdateStats() throws TException {
        // Create database and table
        FieldSchema fs = new FieldSchema("date", "string", "date column");
        List<FieldSchema> cols = Collections.singletonList(fs);
        Table tbl = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").build(null);
        Warehouse wh = Mockito.mock(Warehouse.class);
        // newDir(true) => stats not updated
        MetaStoreServerUtils.updateTableStatsSlow(db, tbl, wh, true, false, null);
        Mockito.verify(wh, Mockito.never()).getFileStatusesForUnpartitionedTable(db, tbl);
        // partitioned table => stats not updated
        Table tbl1 = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").setPartCols(cols).build(null);
        MetaStoreServerUtils.updateTableStatsSlow(db, tbl1, wh, false, false, null);
        Mockito.verify(wh, Mockito.never()).getFileStatusesForUnpartitionedTable(db, tbl1);
        // Already contains stats => stats not updated when forceRecompute isn't set
        Table tbl2 = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").setTableParams(paramsWithStats).build(null);
        MetaStoreServerUtils.updateTableStatsSlow(db, tbl2, wh, false, false, null);
        Mockito.verify(wh, Mockito.never()).getFileStatusesForUnpartitionedTable(db, tbl2);
    }

    @Test
    public void isFastStatsSameWithNullPartitions() {
        Partition partition = new Partition();
        Assert.assertFalse(MetaStoreServerUtils.isFastStatsSame(null, null));
        Assert.assertFalse(MetaStoreServerUtils.isFastStatsSame(null, partition));
        Assert.assertFalse(MetaStoreServerUtils.isFastStatsSame(partition, null));
    }

    @Test
    public void isFastStatsSameWithNoMatchingStats() {
        Partition oldPartition = new Partition();
        Map<String, String> stats = new HashMap<>();
        oldPartition.setParameters(stats);
        Assert.assertFalse(MetaStoreServerUtils.isFastStatsSame(oldPartition, null));
        stats.put("someKeyThatIsNotInFastStats", "value");
        oldPartition.setParameters(stats);
        Assert.assertFalse(MetaStoreServerUtils.isFastStatsSame(oldPartition, null));
    }

    // Test case where one or all of the FAST_STATS parameters are not present in newPart
    @Test
    public void isFastStatsSameMatchingButOnlyOneStat() {
        Partition oldPartition = new Partition();
        Partition newPartition = new Partition();
        Map<String, String> randomParams = new HashMap<String, String>();
        randomParams.put("randomParam1", "randomVal1");
        newPartition.setParameters(randomParams);
        Assert.assertFalse(MetaStoreServerUtils.isFastStatsSame(oldPartition, newPartition));
    }

    // Test case where all parameters are present and their values are same
    @Test
    public void isFastStatsSameMatching() {
        Partition oldPartition = new Partition();
        Partition newPartition = new Partition();
        Map<String, String> stats = new HashMap<>();
        Map<String, String> oldParams = new HashMap<>();
        Map<String, String> newParams = new HashMap<>();
        long testVal = 1;
        for (String key : StatsSetupConst.FAST_STATS) {
            oldParams.put(key, String.valueOf(testVal));
            newParams.put(key, String.valueOf(testVal));
        }
        oldPartition.setParameters(oldParams);
        newPartition.setParameters(newParams);
        Assert.assertTrue(MetaStoreServerUtils.isFastStatsSame(oldPartition, newPartition));
    }

    // Test case where all parameters are present and their values are different
    @Test
    public void isFastStatsSameDifferent() {
        Partition oldPartition = new Partition();
        Partition newPartition = new Partition();
        Map<String, String> stats = new HashMap<>();
        Map<String, String> oldParams = new HashMap<>();
        Map<String, String> newParams = new HashMap<>();
        long testVal = 1;
        for (String key : StatsSetupConst.FAST_STATS) {
            oldParams.put(key, String.valueOf(testVal));
            newParams.put(key, String.valueOf((++testVal)));
        }
        oldPartition.setParameters(oldParams);
        newPartition.setParameters(newParams);
        Assert.assertFalse(MetaStoreServerUtils.isFastStatsSame(oldPartition, newPartition));
    }

    @Test
    public void isFastStatsSameNullStatsInNew() {
        Partition oldPartition = new Partition();
        Partition newPartition = new Partition();
        Map<String, String> oldParams = new HashMap<>();
        Map<String, String> newParams = new HashMap<>();
        long testVal = 1;
        for (String key : StatsSetupConst.FAST_STATS) {
            oldParams.put(key, String.valueOf(testVal));
            newParams.put(key, null);
        }
        oldPartition.setParameters(oldParams);
        newPartition.setParameters(newParams);
        Assert.assertFalse(MetaStoreServerUtils.isFastStatsSame(oldPartition, newPartition));
    }

    @Test
    public void testFilterMapWithPredicates() {
        Map<String, String> testMap = getTestParamMap();
        List<String> excludePatterns = Arrays.asList("lastDdl", "num");
        testMapFilter(testMap, excludePatterns);
        Assert.assertFalse(testMap.containsKey("transient_lastDdlTime"));
        Assert.assertFalse(testMap.containsKey("numFiles"));
        Assert.assertFalse(testMap.containsKey("numFilesErasureCoded"));
        Assert.assertFalse(testMap.containsKey("numRows"));
        Map<String, String> expectedMap = new HashMap<String, String>() {
            {
                put("totalSize", "1024");
                put("rawDataSize", "3243234");
                put("COLUMN_STATS_ACCURATE", "{\"BASIC_STATS\":\"true\"");
                put("COLUMN_STATS_ACCURATED", "dummy");
                put("bucketing_version", "2");
                put("testBucketing_version", "2");
            }
        };
        Assert.assertThat(expectedMap, Is.is(testMap));
        testMap = getTestParamMap();
        excludePatterns = Arrays.asList("^bucket", "ACCURATE$");
        testMapFilter(testMap, excludePatterns);
        expectedMap = new HashMap<String, String>() {
            {
                put("totalSize", "1024");
                put("numRows", "10");
                put("rawDataSize", "3243234");
                put("COLUMN_STATS_ACCURATED", "dummy");
                put("numFiles", "2");
                put("transient_lastDdlTime", "1537487124");
                put("testBucketing_version", "2");
                put("numFilesErasureCoded", "0");
            }
        };
        Assert.assertThat(expectedMap, Is.is(testMap));
        // test that if the config is not set in MetastoreConf, it does not filter any parameter
        Configuration testConf = MetastoreConf.newMetastoreConf();
        testMap = getTestParamMap();
        excludePatterns = Arrays.asList(MetastoreConf.getTrimmedStringsVar(testConf, EVENT_NOTIFICATION_PARAMETERS_EXCLUDE_PATTERNS));
        testMapFilter(testMap, excludePatterns);
        Assert.assertThat(getTestParamMap(), Is.is(testMap));
        // test that if the config is set to empty String in MetastoreConf, it does not filter any parameter
        testConf.setStrings(EVENT_NOTIFICATION_PARAMETERS_EXCLUDE_PATTERNS.getVarname(), "");
        testMap = getTestParamMap();
        excludePatterns = Arrays.asList(MetastoreConf.getTrimmedStringsVar(testConf, EVENT_NOTIFICATION_PARAMETERS_EXCLUDE_PATTERNS));
        testMapFilter(testMap, excludePatterns);
        Assert.assertThat(getTestParamMap(), Is.is(testMap));
    }

    /**
     * Two empty StorageDescriptorKey should be equal.
     */
    @Test
    public void testCompareNullSdKey() {
        Assert.assertThat(UNSET_KEY, Is.is(new MetaStoreServerUtils.StorageDescriptorKey()));
    }

    /**
     * Two StorageDescriptorKey objects with null storage descriptors should be
     * equal iff the base location is equal.
     */
    @Test
    public void testCompareNullSd() {
        Assert.assertThat(new MetaStoreServerUtils.StorageDescriptorKey("a", null), Is.is(new MetaStoreServerUtils.StorageDescriptorKey("a", null)));
        // Different locations produce different objects
        Assert.assertThat(new MetaStoreServerUtils.StorageDescriptorKey("a", null), IsNot.not(CoreMatchers.equalTo(new MetaStoreServerUtils.StorageDescriptorKey("b", null))));
    }

    /**
     * Two StorageDescriptorKey objects with the same base location but different
     * SD location should be equal
     */
    @Test
    public void testCompareWithSdSamePrefixDifferentLocation() throws MetaException {
        Partition p1 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l1").addCol("a", "int").addValue("val1").build(null);
        Partition p2 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l2").addCol("a", "int").addValue("val1").build(null);
        Assert.assertThat(new MetaStoreServerUtils.StorageDescriptorKey("a", p1.getSd()), Is.is(new MetaStoreServerUtils.StorageDescriptorKey("a", p2.getSd())));
    }

    /**
     * Two StorageDescriptorKey objects with the same base location
     * should be equal iff their columns are equal
     */
    @Test
    public void testCompareWithSdSamePrefixDifferentCols() throws MetaException {
        Partition p1 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l1").addCol("a", "int").addValue("val1").build(null);
        Partition p2 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l2").addCol("b", "int").addValue("val1").build(null);
        Partition p3 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l2").addCol("a", "int").addValue("val1").build(null);
        Assert.assertThat(new MetaStoreServerUtils.StorageDescriptorKey("a", p1.getSd()), IsNot.not(new MetaStoreServerUtils.StorageDescriptorKey("a", p2.getSd())));
        Assert.assertThat(new MetaStoreServerUtils.StorageDescriptorKey("a", p1.getSd()), Is.is(new MetaStoreServerUtils.StorageDescriptorKey("a", p3.getSd())));
    }

    /**
     * Two StorageDescriptorKey objects with the same base location
     * should be equal iff their output formats are equal
     */
    @Test
    public void testCompareWithSdSamePrefixDifferentOutputFormat() throws MetaException {
        Partition p1 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l1").addCol("a", "int").addValue("val1").setOutputFormat("foo").build(null);
        Partition p2 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l2").addCol("a", "int").setOutputFormat("bar").addValue("val1").build(null);
        Partition p3 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l2").addCol("a", "int").setOutputFormat("foo").addValue("val1").build(null);
        Assert.assertThat(new MetaStoreServerUtils.StorageDescriptorKey("a", p1.getSd()), IsNot.not(new MetaStoreServerUtils.StorageDescriptorKey("a", p2.getSd())));
        Assert.assertThat(new MetaStoreServerUtils.StorageDescriptorKey("a", p1.getSd()), Is.is(new MetaStoreServerUtils.StorageDescriptorKey("a", p3.getSd())));
    }

    /**
     * Two StorageDescriptorKey objects with the same base location
     * should be equal iff their input formats are equal
     */
    @Test
    public void testCompareWithSdSamePrefixDifferentInputFormat() throws MetaException {
        Partition p1 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l1").addCol("a", "int").addValue("val1").setInputFormat("foo").build(null);
        Partition p2 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l2").addCol("a", "int").setInputFormat("bar").addValue("val1").build(null);
        Partition p3 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("l1").addCol("a", "int").addValue("val1").setInputFormat("foo").build(null);
        Assert.assertThat(new MetaStoreServerUtils.StorageDescriptorKey("a", p1.getSd()), IsNot.not(new MetaStoreServerUtils.StorageDescriptorKey("a", p2.getSd())));
        Assert.assertThat(new MetaStoreServerUtils.StorageDescriptorKey("a", p1.getSd()), Is.is(new MetaStoreServerUtils.StorageDescriptorKey("a", p3.getSd())));
    }

    /**
     * Test getPartitionspecsGroupedByStorageDescriptor() for partitions with null SDs.
     */
    @Test
    public void testGetPartitionspecsGroupedBySDNullSD() throws MetaException {
        // Create database and table
        Table tbl = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").setLocation("/foo").build(null);
        Partition p1 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("a", "int").addValue("val1").setInputFormat("foo").build(null);
        // Set SD to null
        p1.unsetSd();
        Assert.assertThat(p1.getSd(), Is.is(((StorageDescriptor) (null))));
        List<PartitionSpec> result = MetaStoreServerUtils.getPartitionspecsGroupedByStorageDescriptor(tbl, Collections.singleton(p1));
        Assert.assertThat(result.size(), Is.is(1));
        PartitionSpec ps = result.get(0);
        Assert.assertThat(ps.getRootPath(), Is.is(((String) (null))));
        List<PartitionWithoutSD> partitions = ps.getSharedSDPartitionSpec().getPartitions();
        Assert.assertThat(partitions.size(), Is.is(1));
        PartitionWithoutSD partition = partitions.get(0);
        Assert.assertThat(partition.getRelativePath(), Is.is(((String) (null))));
        Assert.assertThat(partition.getValues(), Is.is(Collections.singletonList("val1")));
    }

    /**
     * Test getPartitionspecsGroupedByStorageDescriptor() for partitions with a single
     * partition which is located under table location.
     */
    @Test
    public void testGetPartitionspecsGroupedBySDOnePartitionInTable() throws MetaException {
        // Create database and table
        Table tbl = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").setLocation("/foo").build(null);
        Partition p1 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("/foo/bar").addCol("a", "int").addValue("val1").setInputFormat("foo").build(null);
        List<PartitionSpec> result = MetaStoreServerUtils.getPartitionspecsGroupedByStorageDescriptor(tbl, Collections.singleton(p1));
        Assert.assertThat(result.size(), Is.is(1));
        PartitionSpec ps = result.get(0);
        Assert.assertThat(ps.getRootPath(), Is.is(tbl.getSd().getLocation()));
        List<PartitionWithoutSD> partitions = ps.getSharedSDPartitionSpec().getPartitions();
        Assert.assertThat(partitions.size(), Is.is(1));
        PartitionWithoutSD partition = partitions.get(0);
        Assert.assertThat(partition.getRelativePath(), Is.is("/bar"));
        Assert.assertThat(partition.getValues(), Is.is(Collections.singletonList("val1")));
    }

    /**
     * Test getPartitionspecsGroupedByStorageDescriptor() for partitions with a single
     * partition which is located outside table location.
     */
    @Test
    public void testGetPartitionspecsGroupedBySDonePartitionExternal() throws MetaException {
        // Create database and table
        Table tbl = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").setLocation("/foo").build(null);
        Partition p1 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("/a/b").addCol("a", "int").addValue("val1").setInputFormat("foo").build(null);
        List<PartitionSpec> result = MetaStoreServerUtils.getPartitionspecsGroupedByStorageDescriptor(tbl, Collections.singleton(p1));
        Assert.assertThat(result.size(), Is.is(1));
        PartitionSpec ps = result.get(0);
        Assert.assertThat(ps.getRootPath(), Is.is(((String) (null))));
        List<Partition> partitions = ps.getPartitionList().getPartitions();
        Assert.assertThat(partitions.size(), Is.is(1));
        Partition partition = partitions.get(0);
        Assert.assertThat(partition.getSd().getLocation(), Is.is("/a/b"));
        Assert.assertThat(partition.getValues(), Is.is(Collections.singletonList("val1")));
    }

    /**
     * Test getPartitionspecsGroupedByStorageDescriptor() multiple partitions:
     * <ul>
     *   <li>Partition with null SD</li>
     *   <li>Two partitions under the table location</li>
     *   <li>One partition outside of table location</li>
     * </ul>
     */
    @Test
    public void testGetPartitionspecsGroupedBySDonePartitionCombined() throws MetaException {
        // Create database and table
        String sharedInputFormat = "foo1";
        Table tbl = new TableBuilder().setDbName(TestMetaStoreServerUtils.DB_NAME).setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("id", "int").setLocation("/foo").build(null);
        Partition p1 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("/foo/bar").addCol("a1", "int").addValue("val1").setInputFormat(sharedInputFormat).build(null);
        Partition p2 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).setLocation("/a/b").addCol("a2", "int").addValue("val2").setInputFormat("foo2").build(null);
        Partition p3 = new PartitionBuilder().setDbName("DB_NAME").setTableName(TestMetaStoreServerUtils.TABLE_NAME).addCol("a3", "int").addValue("val3").setInputFormat("foo3").build(null);
        Partition p4 = new PartitionBuilder().setDbName("DB_NAME").setTableName("TABLE_NAME").setLocation("/foo/baz").addCol("a1", "int").addValue("val4").setInputFormat(sharedInputFormat).build(null);
        p3.unsetSd();
        List<PartitionSpec> result = MetaStoreServerUtils.getPartitionspecsGroupedByStorageDescriptor(tbl, Arrays.asList(p1, p2, p3, p4));
        Assert.assertThat(result.size(), Is.is(3));
        PartitionSpec ps1 = result.get(0);
        Assert.assertThat(ps1.getRootPath(), Is.is(((String) (null))));
        Assert.assertThat(ps1.getPartitionList(), Is.is(((List<Partition>) (null))));
        PartitionSpecWithSharedSD partSpec = ps1.getSharedSDPartitionSpec();
        List<PartitionWithoutSD> partitions1 = partSpec.getPartitions();
        Assert.assertThat(partitions1.size(), Is.is(1));
        PartitionWithoutSD partition1 = partitions1.get(0);
        Assert.assertThat(partition1.getRelativePath(), Is.is(((String) (null))));
        Assert.assertThat(partition1.getValues(), Is.is(Collections.singletonList("val3")));
        PartitionSpec ps2 = result.get(1);
        Assert.assertThat(ps2.getRootPath(), Is.is(tbl.getSd().getLocation()));
        Assert.assertThat(ps2.getPartitionList(), Is.is(((List<Partition>) (null))));
        List<PartitionWithoutSD> partitions2 = ps2.getSharedSDPartitionSpec().getPartitions();
        Assert.assertThat(partitions2.size(), Is.is(2));
        PartitionWithoutSD partition2_1 = partitions2.get(0);
        PartitionWithoutSD partition2_2 = partitions2.get(1);
        if (partition2_1.getRelativePath().equals("baz")) {
            // Swap p2_1 and p2_2
            PartitionWithoutSD tmp = partition2_1;
            partition2_1 = partition2_2;
            partition2_2 = tmp;
        }
        Assert.assertThat(partition2_1.getRelativePath(), Is.is("/bar"));
        Assert.assertThat(partition2_1.getValues(), Is.is(Collections.singletonList("val1")));
        Assert.assertThat(partition2_2.getRelativePath(), Is.is("/baz"));
        Assert.assertThat(partition2_2.getValues(), Is.is(Collections.singletonList("val4")));
        PartitionSpec ps4 = result.get(2);
        Assert.assertThat(ps4.getRootPath(), Is.is(((String) (null))));
        Assert.assertThat(ps4.getSharedSDPartitionSpec(), Is.is(((PartitionSpecWithSharedSD) (null))));
        List<Partition> partitions = ps4.getPartitionList().getPartitions();
        Assert.assertThat(partitions.size(), Is.is(1));
        Partition partition = partitions.get(0);
        Assert.assertThat(partition.getSd().getLocation(), Is.is("/a/b"));
        Assert.assertThat(partition.getValues(), Is.is(Collections.singletonList("val2")));
    }

    @Test
    public void testAnonymizeConnectionURL() {
        String connectionURL = null;
        String expectedConnectionURL = null;
        String result = MetaStoreServerUtils.anonymizeConnectionURL(connectionURL);
        Assert.assertEquals(expectedConnectionURL, result);
        connectionURL = "jdbc:mysql://localhost:1111/db?user=user&password=password";
        expectedConnectionURL = "jdbc:mysql://localhost:1111/db?user=****&password=****";
        result = MetaStoreServerUtils.anonymizeConnectionURL(connectionURL);
        Assert.assertEquals(expectedConnectionURL, result);
        connectionURL = "jdbc:derby:sample;user=jill;password=toFetchAPail";
        expectedConnectionURL = "jdbc:derby:sample;user=****;password=****";
        result = MetaStoreServerUtils.anonymizeConnectionURL(connectionURL);
        Assert.assertEquals(expectedConnectionURL, result);
        connectionURL = "jdbc:mysql://[(host=myhost1,port=1111,user=sandy,password=secret)," + "(host=myhost2,port=2222,user=finn,password=secret)]/db";
        expectedConnectionURL = "jdbc:mysql://[(host=myhost1,port=1111,user=****,password=****)," + "(host=myhost2,port=2222,user=****,password=****)]/db";
        result = MetaStoreServerUtils.anonymizeConnectionURL(connectionURL);
        Assert.assertEquals(expectedConnectionURL, result);
        connectionURL = "jdbc:derby:memory:${test.tmp.dir}/junit_metastore_db;create=true";
        result = MetaStoreServerUtils.anonymizeConnectionURL(connectionURL);
        Assert.assertEquals(connectionURL, result);
    }
}

