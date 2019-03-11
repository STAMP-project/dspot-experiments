/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.hive.metastore;


import ConfVars.TRY_DIRECT_SQL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.GetPartitionsProjectionSpec;
import org.apache.hadoop.hive.metastore.api.GetPartitionsRequest;
import org.apache.hadoop.hive.metastore.api.GetPartitionsResponse;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PartitionListComposingSpec;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;
import org.apache.hadoop.hive.metastore.api.PartitionSpecWithSharedSD;
import org.apache.hadoop.hive.metastore.api.PartitionWithoutSD;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for getPartitionsWithSpecs metastore API. This test create some partitions and makes sure
 * that getPartitionsWithSpecs returns results which are comparable with the get_partitions API when
 * various combinations of projection spec are set. Also checks the JDO code path in addition to
 * directSQL code path
 */
@Category(MetastoreCheckinTest.class)
public class TestGetPartitionsUsingProjectionAndFilterSpecs {
    private static final Logger LOG = LoggerFactory.getLogger(TestGetPartitionsUsingProjectionAndFilterSpecs.class);

    protected static Configuration conf = MetastoreConf.newMetastoreConf();

    private static int port;

    private static final String dbName = "test_projection_db";

    private static final String tblName = "test_projection_table";

    private List<Partition> origPartitions;

    private Table tbl;

    private static final String EXCLUDE_KEY_PREFIX = "exclude";

    private HiveMetaStoreClient client;

    @Test
    public void testGetPartitions() throws TException {
        GetPartitionsRequest request = getGetPartitionsRequest();
        GetPartitionsResponse response = client.getPartitionsWithSpecs(request);
        validateBasic(response);
    }

    @Test
    public void testPartitionProjectionEmptySpec() throws Throwable {
        GetPartitionsRequest request = getGetPartitionsRequest();
        GetPartitionsProjectionSpec projectSpec = request.getProjectionSpec();
        projectSpec.setFieldList(new ArrayList(0));
        projectSpec.setExcludeParamKeyPattern("exclude%");
        GetPartitionsResponse response;
        response = client.getPartitionsWithSpecs(request);
        Assert.assertEquals(1, response.getPartitionSpec().size());
        PartitionSpec partitionSpec = response.getPartitionSpec().get(0);
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = partitionSpec.getSharedSDPartitionSpec();
        StorageDescriptor sharedSD = partitionSpecWithSharedSD.getSd();
        Assert.assertNotNull(sharedSD);
        // everything except location in sharedSD should be same
        StorageDescriptor origSd = origPartitions.get(0).getSd().deepCopy();
        origSd.unsetLocation();
        StorageDescriptor sharedSDCopy = sharedSD.deepCopy();
        sharedSDCopy.unsetLocation();
        Assert.assertEquals(origSd, sharedSDCopy);
        List<PartitionWithoutSD> partitionWithoutSDS = partitionSpecWithSharedSD.getPartitions();
        Assert.assertNotNull(partitionWithoutSDS);
        Assert.assertEquals("Unexpected number of partitions returned", origPartitions.size(), partitionWithoutSDS.size());
        for (int i = 0; i < (origPartitions.size()); i++) {
            Partition origPartition = origPartitions.get(i);
            PartitionWithoutSD retPartition = partitionWithoutSDS.get(i);
            Assert.assertEquals(origPartition.getCreateTime(), retPartition.getCreateTime());
            Assert.assertEquals(origPartition.getLastAccessTime(), retPartition.getLastAccessTime());
            Assert.assertEquals(origPartition.getSd().getLocation(), ((sharedSD.getLocation()) + (retPartition.getRelativePath())));
            validateMap(origPartition.getParameters(), retPartition.getParameters());
            validateList(origPartition.getValues(), retPartition.getValues());
        }
    }

    @Test
    public void testPartitionProjectionAllSingleValuedFields() throws Throwable {
        GetPartitionsRequest request = getGetPartitionsRequest();
        GetPartitionsProjectionSpec projectSpec = request.getProjectionSpec();
        List<String> projectedFields = /* , "sd.serdeInfo.serdeType" */
        Arrays.asList("dbName", "tableName", "createTime", "lastAccessTime", "sd.location", "sd.inputFormat", "sd.outputFormat", "sd.compressed", "sd.numBuckets", "sd.serdeInfo.name", "sd.serdeInfo.serializationLib");
        // TODO directSQL does not support serdeType, serializerClass and deserializerClass in serdeInfo
        projectSpec.setFieldList(projectedFields);
        GetPartitionsResponse response = client.getPartitionsWithSpecs(request);
        Assert.assertEquals(1, response.getPartitionSpec().size());
        PartitionSpec partitionSpec = response.getPartitionSpec().get(0);
        Assert.assertTrue("DbName is not set", partitionSpec.isSetDbName());
        Assert.assertTrue("tableName is not set", partitionSpec.isSetTableName());
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = partitionSpec.getSharedSDPartitionSpec();
        StorageDescriptor sharedSD = partitionSpecWithSharedSD.getSd();
        Assert.assertNotNull(sharedSD);
        List<PartitionWithoutSD> partitionWithoutSDS = partitionSpecWithSharedSD.getPartitions();
        Assert.assertNotNull(partitionWithoutSDS);
        Assert.assertEquals(partitionWithoutSDS.size(), origPartitions.size());
        comparePartitionForSingleValuedFields(projectedFields, sharedSD, partitionWithoutSDS, 0);
    }

    @Test
    public void testProjectionUsingJDO() throws Throwable {
        // disable direct SQL to make sure
        client.setMetaConf(TRY_DIRECT_SQL.getVarname(), "false");
        GetPartitionsRequest request = getGetPartitionsRequest();
        GetPartitionsProjectionSpec projectSpec = request.getProjectionSpec();
        List<String> projectedFields = Collections.singletonList("sd.location");
        projectSpec.setFieldList(projectedFields);
        GetPartitionsResponse response = client.getPartitionsWithSpecs(request);
        Assert.assertEquals(1, response.getPartitionSpec().size());
        PartitionSpec partitionSpec = response.getPartitionSpec().get(0);
        Assert.assertTrue("DbName is not set", partitionSpec.isSetDbName());
        Assert.assertTrue("tableName is not set", partitionSpec.isSetTableName());
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = partitionSpec.getSharedSDPartitionSpec();
        StorageDescriptor sharedSD = partitionSpecWithSharedSD.getSd();
        Assert.assertNotNull(sharedSD);
        List<PartitionWithoutSD> partitionWithoutSDS = partitionSpecWithSharedSD.getPartitions();
        Assert.assertNotNull(partitionWithoutSDS);
        Assert.assertEquals(partitionWithoutSDS.size(), origPartitions.size());
        comparePartitionForSingleValuedFields(projectedFields, sharedSD, partitionWithoutSDS, 0);
        // set all the single-valued fields and try using JDO
        request = getGetPartitionsRequest();
        projectSpec = request.getProjectionSpec();
        projectedFields = Arrays.asList("dbName", "tableName", "createTime", "lastAccessTime", "sd.location", "sd.inputFormat", "sd.outputFormat", "sd.compressed", "sd.numBuckets", "sd.serdeInfo.name", "sd.serdeInfo.serializationLib", "sd.serdeInfo.serdeType", "sd.serdeInfo.serializerClass", "sd.serdeInfo.deserializerClass");
        projectSpec.setFieldList(projectedFields);
        response = client.getPartitionsWithSpecs(request);
        Assert.assertEquals(1, response.getPartitionSpec().size());
        partitionSpec = response.getPartitionSpec().get(0);
        Assert.assertTrue("DbName is not set", partitionSpec.isSetDbName());
        Assert.assertTrue("tableName is not set", partitionSpec.isSetTableName());
        partitionSpecWithSharedSD = partitionSpec.getSharedSDPartitionSpec();
        sharedSD = partitionSpecWithSharedSD.getSd();
        Assert.assertNotNull(sharedSD);
        partitionWithoutSDS = partitionSpecWithSharedSD.getPartitions();
        Assert.assertNotNull(partitionWithoutSDS);
        Assert.assertEquals(partitionWithoutSDS.size(), origPartitions.size());
        comparePartitionForSingleValuedFields(projectedFields, sharedSD, partitionWithoutSDS, 0);
    }

    @Test
    public void testPartitionProjectionAllMultiValuedFields() throws Throwable {
        GetPartitionsRequest request = getGetPartitionsRequest();
        GetPartitionsProjectionSpec projectSpec = request.getProjectionSpec();
        List<String> projectedFields = Arrays.asList("values", "parameters", "sd.cols", "sd.bucketCols", "sd.sortCols", "sd.parameters", "sd.skewedInfo", "sd.serdeInfo.parameters");
        projectSpec.setFieldList(projectedFields);
        GetPartitionsResponse response = client.getPartitionsWithSpecs(request);
        Assert.assertEquals(1, response.getPartitionSpec().size());
        PartitionSpec partitionSpec = response.getPartitionSpec().get(0);
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = partitionSpec.getSharedSDPartitionSpec();
        Assert.assertEquals(origPartitions.size(), partitionSpecWithSharedSD.getPartitions().size());
        StorageDescriptor sharedSD = partitionSpecWithSharedSD.getSd();
        for (int i = 0; i < (origPartitions.size()); i++) {
            Partition origPartition = origPartitions.get(i);
            PartitionWithoutSD retPartition = partitionSpecWithSharedSD.getPartitions().get(i);
            for (String projectedField : projectedFields) {
                switch (projectedField) {
                    case "values" :
                        validateList(origPartition.getValues(), retPartition.getValues());
                        break;
                    case "parameters" :
                        validateMap(origPartition.getParameters(), retPartition.getParameters());
                        break;
                    case "sd.cols" :
                        validateList(origPartition.getSd().getCols(), sharedSD.getCols());
                        break;
                    case "sd.bucketCols" :
                        validateList(origPartition.getSd().getBucketCols(), sharedSD.getBucketCols());
                        break;
                    case "sd.sortCols" :
                        validateList(origPartition.getSd().getSortCols(), sharedSD.getSortCols());
                        break;
                    case "sd.parameters" :
                        validateMap(origPartition.getSd().getParameters(), sharedSD.getParameters());
                        break;
                    case "sd.skewedInfo" :
                        if (!(origPartition.getSd().getSkewedInfo().getSkewedColNames().isEmpty())) {
                            validateList(origPartition.getSd().getSkewedInfo().getSkewedColNames(), sharedSD.getSkewedInfo().getSkewedColNames());
                        }
                        if (!(origPartition.getSd().getSkewedInfo().getSkewedColValues().isEmpty())) {
                            for (int i1 = 0; i1 < (origPartition.getSd().getSkewedInfo().getSkewedColValuesSize()); i1++) {
                                validateList(origPartition.getSd().getSkewedInfo().getSkewedColValues().get(i1), sharedSD.getSkewedInfo().getSkewedColValues().get(i1));
                            }
                        }
                        if (!(origPartition.getSd().getSkewedInfo().getSkewedColValueLocationMaps().isEmpty())) {
                            validateMap(origPartition.getSd().getSkewedInfo().getSkewedColValueLocationMaps(), sharedSD.getSkewedInfo().getSkewedColValueLocationMaps());
                        }
                        break;
                    case "sd.serdeInfo.parameters" :
                        validateMap(origPartition.getSd().getSerdeInfo().getParameters(), sharedSD.getSerdeInfo().getParameters());
                        break;
                    default :
                        throw new IllegalArgumentException(("Invalid field " + projectedField));
                }
            }
        }
    }

    @Test
    public void testPartitionProjectionIncludeParameters() throws Throwable {
        GetPartitionsRequest request = getGetPartitionsRequest();
        GetPartitionsProjectionSpec projectSpec = request.getProjectionSpec();
        projectSpec.setFieldList(Arrays.asList("dbName", "tableName", "catName", "parameters", "values"));
        projectSpec.setIncludeParamKeyPattern(((TestGetPartitionsUsingProjectionAndFilterSpecs.EXCLUDE_KEY_PREFIX) + "%"));
        GetPartitionsResponse response = client.getPartitionsWithSpecs(request);
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = response.getPartitionSpec().get(0).getSharedSDPartitionSpec();
        Assert.assertNotNull("All the partitions should be returned in sharedSD spec", partitionSpecWithSharedSD);
        PartitionListComposingSpec partitionListComposingSpec = response.getPartitionSpec().get(0).getPartitionList();
        Assert.assertNull(("Partition list composing spec should be null since all the " + "partitions are expected to be in sharedSD spec"), partitionListComposingSpec);
        for (PartitionWithoutSD retPartion : partitionSpecWithSharedSD.getPartitions()) {
            Assert.assertTrue("included parameter key is not found in the response", retPartion.getParameters().containsKey(((TestGetPartitionsUsingProjectionAndFilterSpecs.EXCLUDE_KEY_PREFIX) + "key1")));
            Assert.assertTrue("included parameter key is not found in the response", retPartion.getParameters().containsKey(((TestGetPartitionsUsingProjectionAndFilterSpecs.EXCLUDE_KEY_PREFIX) + "key2")));
            Assert.assertEquals("Additional parameters returned other than inclusion keys", 2, retPartion.getParameters().size());
        }
    }

    @Test
    public void testPartitionProjectionIncludeExcludeParameters() throws Throwable {
        GetPartitionsRequest request = getGetPartitionsRequest();
        GetPartitionsProjectionSpec projectSpec = request.getProjectionSpec();
        projectSpec.setFieldList(Arrays.asList("dbName", "tableName", "catName", "parameters", "values"));
        // test parameter key inclusion using setIncludeParamKeyPattern
        projectSpec.setIncludeParamKeyPattern(((TestGetPartitionsUsingProjectionAndFilterSpecs.EXCLUDE_KEY_PREFIX) + "%"));
        projectSpec.setExcludeParamKeyPattern("%key1%");
        GetPartitionsResponse response = client.getPartitionsWithSpecs(request);
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = response.getPartitionSpec().get(0).getSharedSDPartitionSpec();
        Assert.assertNotNull("All the partitions should be returned in sharedSD spec", partitionSpecWithSharedSD);
        PartitionListComposingSpec partitionListComposingSpec = response.getPartitionSpec().get(0).getPartitionList();
        Assert.assertNull(("Partition list composing spec should be null since all the " + "partitions are expected to be in sharedSD spec"), partitionListComposingSpec);
        for (PartitionWithoutSD retPartion : partitionSpecWithSharedSD.getPartitions()) {
            Assert.assertFalse("excluded parameter key is found in the response", retPartion.getParameters().containsKey(((TestGetPartitionsUsingProjectionAndFilterSpecs.EXCLUDE_KEY_PREFIX) + "key1")));
            Assert.assertTrue("included parameter key is not found in the response", retPartion.getParameters().containsKey(((TestGetPartitionsUsingProjectionAndFilterSpecs.EXCLUDE_KEY_PREFIX) + "key2")));
            Assert.assertEquals("Additional parameters returned other than inclusion keys", 1, retPartion.getParameters().size());
        }
    }

    @Test
    public void testPartitionProjectionExcludeParameters() throws Throwable {
        GetPartitionsRequest request = getGetPartitionsRequest();
        GetPartitionsProjectionSpec projectSpec = request.getProjectionSpec();
        projectSpec.setFieldList(Arrays.asList("dbName", "tableName", "catName", "parameters", "values"));
        projectSpec.setExcludeParamKeyPattern(((TestGetPartitionsUsingProjectionAndFilterSpecs.EXCLUDE_KEY_PREFIX) + "%"));
        GetPartitionsResponse response = client.getPartitionsWithSpecs(request);
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = response.getPartitionSpec().get(0).getSharedSDPartitionSpec();
        Assert.assertNotNull("All the partitions should be returned in sharedSD spec", partitionSpecWithSharedSD);
        PartitionListComposingSpec partitionListComposingSpec = response.getPartitionSpec().get(0).getPartitionList();
        Assert.assertNull("Partition list composing spec should be null", partitionListComposingSpec);
        for (PartitionWithoutSD retPartion : partitionSpecWithSharedSD.getPartitions()) {
            Assert.assertFalse("excluded parameter key is found in the response", retPartion.getParameters().containsKey(((TestGetPartitionsUsingProjectionAndFilterSpecs.EXCLUDE_KEY_PREFIX) + "key1")));
            Assert.assertFalse("excluded parameter key is found in the response", retPartion.getParameters().containsKey(((TestGetPartitionsUsingProjectionAndFilterSpecs.EXCLUDE_KEY_PREFIX) + "key2")));
        }
    }

    @Test
    public void testNestedMultiValuedFieldProjection() throws TException {
        GetPartitionsRequest request = getGetPartitionsRequest();
        GetPartitionsProjectionSpec projectSpec = request.getProjectionSpec();
        projectSpec.setFieldList(Arrays.asList("sd.cols.name", "sd.cols.type"));
        GetPartitionsResponse response = client.getPartitionsWithSpecs(request);
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = response.getPartitionSpec().get(0).getSharedSDPartitionSpec();
        StorageDescriptor sharedSD = partitionSpecWithSharedSD.getSd();
        Assert.assertNotNull("sd.cols were requested but was not returned", sharedSD.getCols());
        for (FieldSchema col : sharedSD.getCols()) {
            Assert.assertTrue("sd.cols.name was requested but was not returned", col.isSetName());
            Assert.assertTrue("sd.cols.type was requested but was not returned", col.isSetType());
            Assert.assertFalse("sd.cols.comment was not requested but was returned", col.isSetComment());
        }
    }

    @Test
    public void testParameterExpansion() throws TException {
        GetPartitionsRequest request = getGetPartitionsRequest();
        GetPartitionsProjectionSpec projectSpec = request.getProjectionSpec();
        projectSpec.setFieldList(Arrays.asList("sd.cols", "sd.serdeInfo"));
        GetPartitionsResponse response = client.getPartitionsWithSpecs(request);
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = response.getPartitionSpec().get(0).getSharedSDPartitionSpec();
        StorageDescriptor sharedSD = partitionSpecWithSharedSD.getSd();
        Assert.assertNotNull("sd.cols were requested but was not returned", sharedSD.getCols());
        Assert.assertEquals("Returned serdeInfo does not match with original serdeInfo", origPartitions.get(0).getSd().getCols(), sharedSD.getCols());
        Assert.assertNotNull("sd.serdeInfo were requested but was not returned", sharedSD.getSerdeInfo());
        Assert.assertEquals("Returned serdeInfo does not match with original serdeInfo", origPartitions.get(0).getSd().getSerdeInfo(), sharedSD.getSerdeInfo());
    }

    @Test
    public void testNonStandardPartitions() throws TException {
        String testTblName = "test_non_standard";
        new TableBuilder().setTableName(testTblName).setDbName(TestGetPartitionsUsingProjectionAndFilterSpecs.dbName).addCol("ns_c1", "string", "comment 1").addCol("ns_c2", "int", "comment 2").addPartCol("part", "string").addPartCol("city", "string").addBucketCol("ns_c1").addSortCol("ns_c2", 1).addTableParam("tblparamKey", "Partitions of this table are not located within table directory").create(client, TestGetPartitionsUsingProjectionAndFilterSpecs.conf);
        Table table = client.getTable(TestGetPartitionsUsingProjectionAndFilterSpecs.dbName, testTblName);
        Assert.assertNotNull("Unable to create a test table ", table);
        List<Partition> partitions = new ArrayList<>();
        partitions.add(createPartition(Arrays.asList("p1", "SanFrancisco"), table));
        partitions.add(createPartition(Arrays.asList("p1", "PaloAlto"), table));
        partitions.add(createPartition(Arrays.asList("p2", "Seattle"), table));
        partitions.add(createPartition(Arrays.asList("p2", "Phoenix"), table));
        client.add_partitions(partitions);
        // change locations of two of the partitions outside table directory
        List<Partition> testPartitions = client.listPartitions(TestGetPartitionsUsingProjectionAndFilterSpecs.dbName, testTblName, ((short) (-1)));
        Assert.assertEquals(4, testPartitions.size());
        Partition p1 = testPartitions.get(2);
        p1.getSd().setLocation("/tmp/some_other_location/part=p2/city=Seattle");
        Partition p2 = testPartitions.get(3);
        p2.getSd().setLocation("/tmp/some_other_location/part=p2/city=Phoenix");
        client.alter_partitions(TestGetPartitionsUsingProjectionAndFilterSpecs.dbName, testTblName, Arrays.asList(p1, p2));
        GetPartitionsRequest request = getGetPartitionsRequest();
        request.getProjectionSpec().setFieldList(Arrays.asList("values", "sd"));
        request.setDbName(TestGetPartitionsUsingProjectionAndFilterSpecs.dbName);
        request.setTblName(testTblName);
        GetPartitionsResponse response = client.getPartitionsWithSpecs(request);
        Assert.assertNotNull("Response should have returned partition specs", response.getPartitionSpec());
        Assert.assertEquals("We should have two partition specs", 2, response.getPartitionSpec().size());
        Assert.assertNotNull("One SharedSD spec is expected", response.getPartitionSpec().get(0).getSharedSDPartitionSpec());
        Assert.assertNotNull("One composing spec is expected", response.getPartitionSpec().get(1).getPartitionList());
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = response.getPartitionSpec().get(0).getSharedSDPartitionSpec();
        Assert.assertNotNull("sd was requested but not returned", partitionSpecWithSharedSD.getSd());
        Assert.assertEquals("shared SD should have table location", table.getSd().getLocation(), partitionSpecWithSharedSD.getSd().getLocation());
        List<List<String>> expectedVals = new ArrayList<>(2);
        expectedVals.add(Arrays.asList("p1", "PaloAlto"));
        expectedVals.add(Arrays.asList("p1", "SanFrancisco"));
        for (int i = 0; i < (partitionSpecWithSharedSD.getPartitions().size()); i++) {
            PartitionWithoutSD retPartition = partitionSpecWithSharedSD.getPartitions().get(i);
            Assert.assertEquals(2, retPartition.getValuesSize());
            validateList(expectedVals.get(i), retPartition.getValues());
            Assert.assertNull("parameters were not requested so should have been null", retPartition.getParameters());
        }
        PartitionListComposingSpec composingSpec = response.getPartitionSpec().get(1).getPartitionList();
        Assert.assertNotNull("composing spec should have returned 2 partitions", composingSpec.getPartitions());
        Assert.assertEquals("composing spec should have returned 2 partitions", 2, composingSpec.getPartitionsSize());
        expectedVals.clear();
        expectedVals.add(Arrays.asList("p2", "Phoenix"));
        expectedVals.add(Arrays.asList("p2", "Seattle"));
        for (int i = 0; i < (composingSpec.getPartitions().size()); i++) {
            Partition partition = composingSpec.getPartitions().get(i);
            Assert.assertEquals(2, partition.getValuesSize());
            validateList(expectedVals.get(i), partition.getValues());
            Assert.assertNull("parameters were not requested so should have been null", partition.getParameters());
        }
    }

    @Test
    public void testGetPartitionsWithFilterExpr() throws TException {
        runGetPartitionsUsingExpr();
    }

    @Test
    public void testGetPartitionsUsingNames() throws Exception {
        runGetPartitionsUsingNames();
    }

    @Test
    public void testGetPartitionsUsingValues() throws Exception {
        runGetPartitionsUsingVals();
    }

    @Test
    public void testGetPartitionsUsingExprWithJDO() throws Exception {
        // disable direct SQL to make sure
        client.setMetaConf(TRY_DIRECT_SQL.getVarname(), "false");
        runGetPartitionsUsingExpr();
    }

    @Test
    public void testGetPartitionsUsingValuesWithJDO() throws Exception {
        // disable direct SQL to make sure
        client.setMetaConf(TRY_DIRECT_SQL.getVarname(), "false");
        runGetPartitionsUsingVals();
    }

    @Test
    public void testGetPartitionsUsingNamesWithJDO() throws Exception {
        // disable direct SQL to make sure
        client.setMetaConf(TRY_DIRECT_SQL.getVarname(), "false");
        runGetPartitionsUsingNames();
    }

    @Test(expected = MetaException.class)
    public void testInvalidFilterByNames() throws Exception {
        runWithInvalidFilterByNames();
    }

    @Test(expected = MetaException.class)
    public void testInvalidFilterByNamesWithJDO() throws Exception {
        // disable direct SQL to make sure
        client.setMetaConf(TRY_DIRECT_SQL.getVarname(), "false");
        runWithInvalidFilterByNames();
    }

    @Test(expected = MetaException.class)
    public void testInvalidProjectFieldNames() throws TException {
        runWithInvalidFieldNames(Arrays.asList("values", "invalid.field.name"));
    }

    @Test(expected = MetaException.class)
    public void testInvalidProjectFieldNames2() throws TException {
        runWithInvalidFieldNames(Arrays.asList(""));
    }

    @Test(expected = MetaException.class)
    public void testInvalidProjectFieldNamesWithJDO() throws TException {
        // disable direct SQL to make sure
        client.setMetaConf(TRY_DIRECT_SQL.getVarname(), "false");
        runWithInvalidFieldNames(Arrays.asList("values", "invalid.field.name"));
    }

    @Test(expected = MetaException.class)
    public void testInvalidProjectFieldNames2WithJDO() throws TException {
        // disable direct SQL to make sure
        client.setMetaConf(TRY_DIRECT_SQL.getVarname(), "false");
        runWithInvalidFieldNames(Arrays.asList(""));
    }
}

