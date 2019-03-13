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
package org.apache.hadoop.hive.metastore;


import PartitionSpecProxy.PartitionIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.partition.spec.CompositePartitionSpecProxy;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test to check PartitionSpec support in HiveMetaStore.
 */
@Category(MetastoreCheckinTest.class)
public class TestHiveMetaStorePartitionSpecs {
    private static final Logger LOG = LoggerFactory.getLogger(TestHiveMetaStorePartitionSpecs.class);

    private static int msPort;

    private static Configuration conf;

    private static String dbName = "testpartitionspecs_db";

    private static String tableName = "testpartitionspecs_table";

    private static int nDates = 10;

    private static String datePrefix = "2014010";

    /**
     * Test for HiveMetaStoreClient.listPartitionSpecs() and HiveMetaStoreClient.listPartitionSpecsByFilter().
     * Check behaviour with and without Partition-grouping enabled.
     */
    @Test
    public void testGetPartitionSpecs_WithAndWithoutPartitionGrouping() {
        testGetPartitionSpecs(true);
        testGetPartitionSpecs(false);
    }

    /**
     * Test to confirm that partitions can be added using PartitionSpecs.
     */
    @Test
    public void testAddPartitions() {
        try {
            // Create source table.
            HiveMetaStoreClient hmsc = new HiveMetaStoreClient(TestHiveMetaStorePartitionSpecs.conf);
            TestHiveMetaStorePartitionSpecs.clearAndRecreateDB(hmsc);
            TestHiveMetaStorePartitionSpecs.createTable(hmsc, true);
            Table table = hmsc.getTable(TestHiveMetaStorePartitionSpecs.dbName, TestHiveMetaStorePartitionSpecs.tableName);
            Assert.assertTrue(table.isSetId());
            table.unsetId();
            TestHiveMetaStorePartitionSpecs.populatePartitions(hmsc, table, Arrays.asList("isLocatedInTablePath", "isLocatedOutsideTablePath"));
            // Clone the table,
            String targetTableName = "cloned_" + (TestHiveMetaStorePartitionSpecs.tableName);
            Table targetTable = new Table(table);
            targetTable.setTableName(targetTableName);
            StorageDescriptor targetTableSd = new StorageDescriptor(targetTable.getSd());
            targetTableSd.setLocation(targetTableSd.getLocation().replace(TestHiveMetaStorePartitionSpecs.tableName, targetTableName));
            hmsc.createTable(targetTable);
            // Get partition-list from source.
            PartitionSpecProxy partitionsForAddition = hmsc.listPartitionSpecsByFilter(TestHiveMetaStorePartitionSpecs.dbName, TestHiveMetaStorePartitionSpecs.tableName, "blurb = \"isLocatedInTablePath\"", (-1));
            partitionsForAddition.setTableName(targetTableName);
            partitionsForAddition.setRootLocation(targetTableSd.getLocation());
            Assert.assertEquals("Unexpected number of partitions added. ", partitionsForAddition.size(), hmsc.add_partitions_pspec(partitionsForAddition));
            // Check that the added partitions are as expected.
            PartitionSpecProxy clonedPartitions = hmsc.listPartitionSpecs(TestHiveMetaStorePartitionSpecs.dbName, targetTableName, (-1));
            Assert.assertEquals("Unexpected number of partitions returned. ", partitionsForAddition.size(), clonedPartitions.size());
            PartitionSpecProxy.PartitionIterator sourceIterator = partitionsForAddition.getPartitionIterator();
            PartitionSpecProxy.PartitionIterator targetIterator = clonedPartitions.getPartitionIterator();
            while (targetIterator.hasNext()) {
                Partition sourcePartition = sourceIterator.next();
                Partition targetPartition = targetIterator.next();
                Assert.assertEquals("Mismatched values.", sourcePartition.getValues(), targetPartition.getValues());
                Assert.assertEquals("Mismatched locations.", sourcePartition.getSd().getLocation(), targetPartition.getSd().getLocation());
            } 
        } catch (Throwable t) {
            TestHiveMetaStorePartitionSpecs.LOG.error("Unexpected Exception!", t);
            t.printStackTrace();
            Assert.assertTrue("Unexpected Exception!", false);
        }
    }

    /**
     * Test to confirm that Partition-grouping behaves correctly when Table-schemas evolve.
     * Partitions must be grouped by location and schema.
     */
    @Test
    public void testFetchingPartitionsWithDifferentSchemas() {
        try {
            // Create source table.
            HiveMetaStoreClient hmsc = new HiveMetaStoreClient(TestHiveMetaStorePartitionSpecs.conf);
            TestHiveMetaStorePartitionSpecs.clearAndRecreateDB(hmsc);
            TestHiveMetaStorePartitionSpecs.createTable(hmsc, true);
            Table table = hmsc.getTable(TestHiveMetaStorePartitionSpecs.dbName, TestHiveMetaStorePartitionSpecs.tableName);
            // Blurb list.
            TestHiveMetaStorePartitionSpecs.populatePartitions(hmsc, table, Arrays.asList("isLocatedInTablePath", "isLocatedOutsideTablePath"));
            // Modify table schema. Add columns.
            List<FieldSchema> fields = table.getSd().getCols();
            fields.add(new FieldSchema("goo", "string", "Entirely new column. Doesn't apply to older partitions."));
            table.getSd().setCols(fields);
            hmsc.alter_table(TestHiveMetaStorePartitionSpecs.dbName, TestHiveMetaStorePartitionSpecs.tableName, table);
            // Check that the change stuck.
            table = hmsc.getTable(TestHiveMetaStorePartitionSpecs.dbName, TestHiveMetaStorePartitionSpecs.tableName);
            Assert.assertEquals("Unexpected number of table columns.", 3, table.getSd().getColsSize());
            // Add partitions with new schema.
            // Mark Partitions with new schema with different blurb.
            TestHiveMetaStorePartitionSpecs.populatePartitions(hmsc, table, Arrays.asList("hasNewColumn"));
            // Retrieve *all* partitions from the table.
            PartitionSpecProxy partitionSpecProxy = hmsc.listPartitionSpecs(TestHiveMetaStorePartitionSpecs.dbName, TestHiveMetaStorePartitionSpecs.tableName, (-1));
            Assert.assertEquals("Unexpected number of partitions.", ((TestHiveMetaStorePartitionSpecs.nDates) * 3), partitionSpecProxy.size());
            // Confirm grouping.
            Assert.assertTrue("Unexpected type of PartitionSpecProxy.", (partitionSpecProxy instanceof CompositePartitionSpecProxy));
            CompositePartitionSpecProxy compositePartitionSpecProxy = ((CompositePartitionSpecProxy) (partitionSpecProxy));
            List<PartitionSpec> partitionSpecs = compositePartitionSpecProxy.toPartitionSpec();
            Assert.assertTrue("PartitionSpec[0] should have been a SharedSDPartitionSpec.", partitionSpecs.get(0).isSetSharedSDPartitionSpec());
            Assert.assertEquals("PartitionSpec[0] should use the table-path as the common root location. ", table.getSd().getLocation(), partitionSpecs.get(0).getRootPath());
            Assert.assertTrue("PartitionSpec[1] should have been a SharedSDPartitionSpec.", partitionSpecs.get(1).isSetSharedSDPartitionSpec());
            Assert.assertEquals("PartitionSpec[1] should use the table-path as the common root location. ", table.getSd().getLocation(), partitionSpecs.get(1).getRootPath());
            Assert.assertTrue("PartitionSpec[2] should have been a ListComposingPartitionSpec.", partitionSpecs.get(2).isSetPartitionList());
            // Categorize the partitions returned, and confirm that all partitions are accounted for.
            PartitionSpecProxy.PartitionIterator iterator = partitionSpecProxy.getPartitionIterator();
            Map<String, List<Partition>> blurbToPartitionList = new HashMap<>(3);
            while (iterator.hasNext()) {
                Partition partition = iterator.next();
                String blurb = partition.getValues().get(1);
                if (!(blurbToPartitionList.containsKey(blurb))) {
                    blurbToPartitionList.put(blurb, new ArrayList(TestHiveMetaStorePartitionSpecs.nDates));
                }
                blurbToPartitionList.get(blurb).add(partition);
            } // </Classification>

            // All partitions with blurb="isLocatedOutsideTablePath" should have 2 columns,
            // and must have locations outside the table directory.
            for (Partition partition : blurbToPartitionList.get("isLocatedOutsideTablePath")) {
                Assert.assertEquals("Unexpected number of columns.", 2, partition.getSd().getCols().size());
                Assert.assertEquals("Unexpected first column.", "foo", partition.getSd().getCols().get(0).getName());
                Assert.assertEquals("Unexpected second column.", "bar", partition.getSd().getCols().get(1).getName());
                String partitionLocation = partition.getSd().getLocation();
                String tableLocation = table.getSd().getLocation();
                Assert.assertTrue((((("Unexpected partition location: " + partitionLocation) + ". ") + "Partition should have been outside table location: ") + tableLocation), (!(partitionLocation.startsWith(tableLocation))));
            }
            // All partitions with blurb="isLocatedInTablePath" should have 2 columns,
            // and must have locations within the table directory.
            for (Partition partition : blurbToPartitionList.get("isLocatedInTablePath")) {
                Assert.assertEquals("Unexpected number of columns.", 2, partition.getSd().getCols().size());
                Assert.assertEquals("Unexpected first column.", "foo", partition.getSd().getCols().get(0).getName());
                Assert.assertEquals("Unexpected second column.", "bar", partition.getSd().getCols().get(1).getName());
                String partitionLocation = partition.getSd().getLocation();
                String tableLocation = table.getSd().getLocation();
                Assert.assertTrue((((("Unexpected partition location: " + partitionLocation) + ". ") + "Partition should have been within table location: ") + tableLocation), partitionLocation.startsWith(tableLocation));
            }
            // All partitions with blurb="hasNewColumn" were added after the table schema changed,
            // and must have 3 columns. Also, the partition locations must lie within the table directory.
            for (Partition partition : blurbToPartitionList.get("hasNewColumn")) {
                Assert.assertEquals("Unexpected number of columns.", 3, partition.getSd().getCols().size());
                Assert.assertEquals("Unexpected first column.", "foo", partition.getSd().getCols().get(0).getName());
                Assert.assertEquals("Unexpected second column.", "bar", partition.getSd().getCols().get(1).getName());
                Assert.assertEquals("Unexpected third column.", "goo", partition.getSd().getCols().get(2).getName());
                String partitionLocation = partition.getSd().getLocation();
                String tableLocation = table.getSd().getLocation();
                Assert.assertTrue((((("Unexpected partition location: " + partitionLocation) + ". ") + "Partition should have been within table location: ") + tableLocation), partitionLocation.startsWith(tableLocation));
            }
        } catch (Throwable t) {
            TestHiveMetaStorePartitionSpecs.LOG.error("Unexpected Exception!", t);
            t.printStackTrace();
            Assert.assertTrue("Unexpected Exception!", false);
        }
    }
}

