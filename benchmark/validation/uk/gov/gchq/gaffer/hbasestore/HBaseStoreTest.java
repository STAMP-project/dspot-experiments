/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.hbasestore;


import StoreTrait.ORDERED;
import java.io.IOException;
import java.util.Collection;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.hbasestore.operation.handler.AddElementsHandler;
import uk.gov.gchq.gaffer.hbasestore.operation.handler.GetAllElementsHandler;
import uk.gov.gchq.gaffer.hbasestore.operation.handler.GetElementsHandler;
import uk.gov.gchq.gaffer.hbasestore.operation.hdfs.handler.AddElementsFromHdfsHandler;
import uk.gov.gchq.gaffer.hbasestore.utils.TableUtils;
import uk.gov.gchq.gaffer.hdfs.operation.AddElementsFromHdfs;
import uk.gov.gchq.gaffer.operation.impl.Validate;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateElements;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.store.operation.handler.OperationHandler;
import uk.gov.gchq.gaffer.store.operation.handler.generate.GenerateElementsHandler;
import uk.gov.gchq.gaffer.store.operation.handler.generate.GenerateObjectsHandler;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class HBaseStoreTest {
    private static final Schema SCHEMA = Schema.fromJson(StreamUtil.schemas(HBaseStoreTest.class));

    private static final HBaseProperties PROPERTIES = HBaseProperties.loadStoreProperties(StreamUtil.storeProps(HBaseStoreTest.class));

    private static final String GRAPH_ID = "graphId";

    private static SingleUseMiniHBaseStore store;

    @Test
    public void shouldCreateTableWhenInitialised() throws IOException, StoreException {
        final Connection connection = HBaseStoreTest.store.getConnection();
        final TableName tableName = getTableName();
        try (final Admin admin = connection.getAdmin()) {
            Assert.assertTrue(admin.tableExists(tableName));
        }
    }

    @Test
    public void shouldNotCreateTableWhenInitialisedWithGeneralInitialiseMethod() throws IOException, StoreException {
        final TableName tableName = getTableName();
        Connection connection = HBaseStoreTest.store.getConnection();
        TableUtils.dropTable(HBaseStoreTest.store);
        try (final Admin admin = connection.getAdmin()) {
            Assert.assertFalse(admin.tableExists(tableName));
        }
        HBaseStoreTest.store.preInitialise(HBaseStoreTest.GRAPH_ID, HBaseStoreTest.SCHEMA, HBaseStoreTest.PROPERTIES);
        connection = HBaseStoreTest.store.getConnection();
        try (final Admin admin = connection.getAdmin()) {
            Assert.assertFalse(admin.tableExists(tableName));
        }
        HBaseStoreTest.store.initialise(HBaseStoreTest.GRAPH_ID, HBaseStoreTest.SCHEMA, HBaseStoreTest.PROPERTIES);
        connection = HBaseStoreTest.store.getConnection();
        try (final Admin admin = connection.getAdmin()) {
            Assert.assertTrue(admin.tableExists(tableName));
        }
    }

    @Test
    public void shouldCreateAStoreUsingTableName() throws Exception {
        // Given
        final HBaseProperties properties = HBaseProperties.loadStoreProperties(StreamUtil.storeProps(HBaseStoreTest.class));
        properties.setTable("tableName");
        final SingleUseMiniHBaseStore store = new SingleUseMiniHBaseStore();
        // When
        store.initialise(null, HBaseStoreTest.SCHEMA, properties);
        // Then
        Assert.assertEquals("tableName", getGraphId());
        Assert.assertEquals("tableName", store.getTableName().getNameAsString());
    }

    @Test
    public void shouldBuildGraphAndGetGraphIdFromTableName() throws Exception {
        // Given
        final HBaseProperties properties = HBaseProperties.loadStoreProperties(StreamUtil.storeProps(HBaseStoreTest.class));
        properties.setTable("tableName");
        // When
        final Graph graph = new Graph.Builder().addSchemas(StreamUtil.schemas(getClass())).storeProperties(properties).build();
        // Then
        Assert.assertEquals("tableName", graph.getGraphId());
    }

    @Test
    public void shouldCreateAStoreUsingGraphIdIfItIsEqualToTableName() throws Exception {
        // Given
        final HBaseProperties properties = HBaseProperties.loadStoreProperties(StreamUtil.storeProps(HBaseStoreTest.class));
        properties.setTable("tableName");
        final SingleUseMiniHBaseStore store = new SingleUseMiniHBaseStore();
        // When
        store.initialise("tableName", HBaseStoreTest.SCHEMA, properties);
        // Then
        Assert.assertEquals("tableName", getGraphId());
    }

    @Test
    public void shouldThrowExceptionIfGraphIdAndTableNameAreProvidedAndDifferent() throws Exception {
        // Given
        final HBaseProperties properties = HBaseProperties.loadStoreProperties(StreamUtil.storeProps(HBaseStoreTest.class));
        properties.setTable("tableName");
        final SingleUseMiniHBaseStore store = new SingleUseMiniHBaseStore();
        // When / Then
        try {
            store.initialise("graphId", HBaseStoreTest.SCHEMA, properties);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldCreateAStoreUsingGraphId() throws Exception {
        // Given
        final HBaseProperties properties = HBaseProperties.loadStoreProperties(StreamUtil.storeProps(HBaseStoreTest.class));
        final SingleUseMiniHBaseStore store = new SingleUseMiniHBaseStore();
        // When
        store.initialise("graphId", HBaseStoreTest.SCHEMA, properties);
        // Then
        Assert.assertEquals("graphId", getGraphId());
    }

    @Test
    public void shouldBeAnOrderedStore() {
        Assert.assertTrue(HBaseStoreTest.store.hasTrait(ORDERED));
    }

    @Test
    public void testStoreReturnsHandlersForRegisteredOperations() throws StoreException {
        // Then
        Assert.assertNotNull(HBaseStoreTest.store.getOperationHandlerExposed(Validate.class));
        Assert.assertTrue(((HBaseStoreTest.store.getOperationHandlerExposed(GetElements.class)) instanceof GetElementsHandler));
        Assert.assertTrue(((HBaseStoreTest.store.getOperationHandlerExposed(GetAllElements.class)) instanceof GetAllElementsHandler));
        Assert.assertTrue(((HBaseStoreTest.store.getOperationHandlerExposed(AddElements.class)) instanceof AddElementsHandler));
        Assert.assertTrue(((HBaseStoreTest.store.getOperationHandlerExposed(AddElementsFromHdfs.class)) instanceof AddElementsFromHdfsHandler));
        Assert.assertTrue(((HBaseStoreTest.store.getOperationHandlerExposed(GenerateElements.class)) instanceof GenerateElementsHandler));
        Assert.assertTrue(((HBaseStoreTest.store.getOperationHandlerExposed(GenerateObjects.class)) instanceof GenerateObjectsHandler));
    }

    @Test
    public void testRequestForNullHandlerManaged() {
        final OperationHandler returnedHandler = HBaseStoreTest.store.getOperationHandlerExposed(null);
        Assert.assertNull(returnedHandler);
    }

    @Test
    public void testStoreTraits() {
        final Collection<StoreTrait> traits = getTraits();
        Assert.assertNotNull(traits);
        Assert.assertTrue("Collection size should be 10", ((traits.size()) == 10));
        Assert.assertTrue("Collection should contain INGEST_AGGREGATION trait", traits.contains(INGEST_AGGREGATION));
        Assert.assertTrue("Collection should contain QUERY_AGGREGATION trait", traits.contains(QUERY_AGGREGATION));
        Assert.assertTrue("Collection should contain PRE_AGGREGATION_FILTERING trait", traits.contains(PRE_AGGREGATION_FILTERING));
        Assert.assertTrue("Collection should contain POST_AGGREGATION_FILTERING trait", traits.contains(POST_AGGREGATION_FILTERING));
        Assert.assertTrue("Collection should contain TRANSFORMATION trait", traits.contains(TRANSFORMATION));
        Assert.assertTrue("Collection should contain POST_TRANSFORMATION_FILTERING trait", traits.contains(POST_TRANSFORMATION_FILTERING));
        Assert.assertTrue("Collection should contain STORE_VALIDATION trait", traits.contains(STORE_VALIDATION));
        Assert.assertTrue("Collection should contain ORDERED trait", traits.contains(ORDERED));
        Assert.assertTrue("Collection should contain VISIBILITY trait", traits.contains(VISIBILITY));
    }
}

