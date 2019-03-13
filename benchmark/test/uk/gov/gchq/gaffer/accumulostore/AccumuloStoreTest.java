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
package uk.gov.gchq.gaffer.accumulostore;


import StoreTrait.ORDERED;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestPropertyNames.INT;
import TestPropertyNames.TIMESTAMP;
import TestTypes.DIRECTED_EITHER;
import TestTypes.ID_STRING;
import TestTypes.TIMESTAMP_2;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.operation.impl.GetElementsInRanges;
import uk.gov.gchq.gaffer.accumulostore.operation.impl.SummariseGroupOverRanges;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.elementdefinition.exception.SchemaException;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.serialisation.Serialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.JavaSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.raw.CompactRawLongSerialiser;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.koryphe.impl.binaryoperator.Max;
import uk.gov.gchq.koryphe.impl.binaryoperator.Min;
import uk.gov.gchq.koryphe.impl.binaryoperator.StringConcat;
import uk.gov.gchq.koryphe.impl.binaryoperator.Sum;


public class AccumuloStoreTest {
    private static final String BYTE_ENTITY_GRAPH = "byteEntityGraph";

    private static final String GAFFER_1_GRAPH = "gaffer1Graph";

    private static SingleUseMockAccumuloStore byteEntityStore;

    private static SingleUseMockAccumuloStore gaffer1KeyStore;

    private static final Schema SCHEMA = Schema.fromJson(StreamUtil.schemas(AccumuloStoreTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloStoreTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(AccumuloStoreTest.class, "/accumuloStoreClassicKeys.properties"));

    @Test
    public void shouldNotCreateTableWhenInitialisedWithGeneralInitialiseMethod() throws AccumuloException, AccumuloSecurityException, TableNotFoundException, StoreException {
        Connector connector = AccumuloStoreTest.byteEntityStore.getConnection();
        connector.tableOperations().delete(AccumuloStoreTest.byteEntityStore.getTableName());
        Assert.assertFalse(connector.tableOperations().exists(AccumuloStoreTest.byteEntityStore.getTableName()));
        AccumuloStoreTest.byteEntityStore.preInitialise(AccumuloStoreTest.BYTE_ENTITY_GRAPH, AccumuloStoreTest.SCHEMA, AccumuloStoreTest.PROPERTIES);
        connector = AccumuloStoreTest.byteEntityStore.getConnection();
        Assert.assertFalse(connector.tableOperations().exists(AccumuloStoreTest.byteEntityStore.getTableName()));
        AccumuloStoreTest.byteEntityStore.initialise(AccumuloStoreTest.GAFFER_1_GRAPH, AccumuloStoreTest.SCHEMA, AccumuloStoreTest.PROPERTIES);
        connector = AccumuloStoreTest.byteEntityStore.getConnection();
        Assert.assertTrue(connector.tableOperations().exists(AccumuloStoreTest.byteEntityStore.getTableName()));
    }

    @Test
    public void shouldCreateAStoreUsingTableName() throws Exception {
        // Given
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloStoreTest.class));
        properties.setTable("tableName");
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        // When
        store.initialise(null, AccumuloStoreTest.SCHEMA, properties);
        // Then
        Assert.assertEquals("tableName", store.getTableName());
        Assert.assertEquals("tableName", store.getGraphId());
    }

    @Test
    public void shouldBuildGraphAndGetGraphIdFromTableName() {
        // Given
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloStoreTest.class));
        properties.setTable("tableName");
        // When
        final Graph graph = new Graph.Builder().addSchemas(StreamUtil.schemas(getClass())).storeProperties(properties).build();
        // Then
        Assert.assertEquals("tableName", graph.getGraphId());
    }

    @Test
    public void shouldCreateAStoreUsingGraphIdIfItIsEqualToTableName() throws Exception {
        // Given
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloStoreTest.class));
        properties.setTable("tableName");
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        // When
        store.initialise("tableName", AccumuloStoreTest.SCHEMA, properties);
        // Then
        Assert.assertEquals("tableName", store.getTableName());
    }

    @Test
    public void shouldThrowExceptionIfGraphIdAndTableNameAreProvidedAndDifferent() throws Exception {
        // Given
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloStoreTest.class));
        properties.setTable("tableName");
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        // When / Then
        try {
            store.initialise("graphId", AccumuloStoreTest.SCHEMA, properties);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldCreateAStoreUsingGraphId() throws Exception {
        // Given
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloStoreTest.class));
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        // When
        store.initialise("graphId", AccumuloStoreTest.SCHEMA, properties);
        // Then
        Assert.assertEquals("graphId", store.getTableName());
    }

    @Test
    public void shouldBeAnOrderedStore() {
        Assert.assertTrue(AccumuloStoreTest.byteEntityStore.hasTrait(ORDERED));
        Assert.assertTrue(AccumuloStoreTest.gaffer1KeyStore.hasTrait(ORDERED));
    }

    @Test
    public void shouldAllowRangeScanOperationsWhenVertexSerialiserDoesPreserveObjectOrdering() throws StoreException {
        // Given
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Serialiser serialiser = new StringSerialiser();
        store.initialise(AccumuloStoreTest.BYTE_ENTITY_GRAPH, new Schema.Builder().vertexSerialiser(serialiser).build(), AccumuloStoreTest.PROPERTIES);
        // When
        final boolean isGetElementsInRangesSupported = store.isSupported(GetElementsInRanges.class);
        final boolean isSummariseGroupOverRangesSupported = store.isSupported(SummariseGroupOverRanges.class);
        // Then
        Assert.assertTrue(isGetElementsInRangesSupported);
        Assert.assertTrue(isSummariseGroupOverRangesSupported);
    }

    @Test
    public void shouldNotAllowRangeScanOperationsWhenVertexSerialiserDoesNotPreserveObjectOrdering() throws StoreException {
        // Given
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Serialiser serialiser = new CompactRawLongSerialiser();
        store.initialise(AccumuloStoreTest.BYTE_ENTITY_GRAPH, new Schema.Builder().vertexSerialiser(serialiser).build(), AccumuloStoreTest.PROPERTIES);
        // When
        final boolean isGetElementsInRangesSupported = store.isSupported(GetElementsInRanges.class);
        final boolean isSummariseGroupOverRangesSupported = store.isSupported(SummariseGroupOverRanges.class);
        // Then
        Assert.assertFalse(isGetElementsInRangesSupported);
        Assert.assertFalse(isSummariseGroupOverRangesSupported);
    }

    @Test
    public void testAbleToInsertAndRetrieveEntityQueryingEqualAndRelatedGaffer1() throws OperationException {
        testAbleToInsertAndRetrieveEntityQueryingEqualAndRelated(AccumuloStoreTest.gaffer1KeyStore);
    }

    @Test
    public void testAbleToInsertAndRetrieveEntityQueryingEqualAndRelatedByteEntity() throws OperationException {
        testAbleToInsertAndRetrieveEntityQueryingEqualAndRelated(AccumuloStoreTest.byteEntityStore);
    }

    @Test
    public void testStoreReturnsHandlersForRegisteredOperationsGaffer1() {
        testStoreReturnsHandlersForRegisteredOperations(AccumuloStoreTest.gaffer1KeyStore);
    }

    @Test
    public void testStoreReturnsHandlersForRegisteredOperationsByteEntity() {
        testStoreReturnsHandlersForRegisteredOperations(AccumuloStoreTest.byteEntityStore);
    }

    @Test
    public void testRequestForNullHandlerManagedGaffer1() {
        testRequestForNullHandlerManaged(AccumuloStoreTest.gaffer1KeyStore);
    }

    @Test
    public void testRequestForNullHandlerManagedByteEntity() {
        testRequestForNullHandlerManaged(AccumuloStoreTest.byteEntityStore);
    }

    @Test
    public void testStoreTraitsGaffer1() {
        testStoreTraits(AccumuloStoreTest.gaffer1KeyStore);
    }

    @Test
    public void testStoreTraitsByteEntity() {
        testStoreTraits(AccumuloStoreTest.byteEntityStore);
    }

    @Test(expected = SchemaException.class)
    public void shouldFindInconsistentVertexSerialiser() throws StoreException {
        final Schema inconsistentSchema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("false").property(INT, "int").groupBy(INT).build()).type("string", new TypeDefinition.Builder().clazz(String.class).serialiser(new JavaSerialiser()).aggregateFunction(new StringConcat()).build()).type("int", new TypeDefinition.Builder().clazz(Integer.class).serialiser(new JavaSerialiser()).aggregateFunction(new Sum()).build()).type("false", Boolean.class).vertexSerialiser(new JavaSerialiser()).build();
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        store.preInitialise("graphId", inconsistentSchema, AccumuloStoreTest.PROPERTIES);
        try {
            store.validateSchemas();
            Assert.fail("Exception expected");
        } catch (final SchemaException e) {
            assert e.getMessage().contains("serialisers to be consistent.");
        }
    }

    @Test
    public void shouldValidateTimestampPropertyHasMaxAggregator() throws Exception {
        // Given
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloStoreTest.class));
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex(ID_STRING).property(TIMESTAMP, TestTypes.TIMESTAMP).build()).edge(EDGE, new SchemaEdgeDefinition.Builder().source(ID_STRING).destination(ID_STRING).directed(DIRECTED_EITHER).property(TIMESTAMP, TIMESTAMP_2).build()).type(ID_STRING, String.class).type(DIRECTED_EITHER, new TypeDefinition.Builder().clazz(Boolean.class).build()).type(TestTypes.TIMESTAMP, new TypeDefinition.Builder().clazz(Long.class).aggregateFunction(new Max()).build()).type(TIMESTAMP_2, new TypeDefinition.Builder().clazz(Long.class).aggregateFunction(new Max()).build()).timestampProperty(TIMESTAMP).build();
        // When
        store.initialise("graphId", schema, properties);
        // Then - no validation exceptions
    }

    @Test
    public void shouldPassSchemaValidationWhenTimestampPropertyDoesNotHaveAnAggregator() throws Exception {
        // Given
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloStoreTest.class));
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex(ID_STRING).property(TIMESTAMP, TestTypes.TIMESTAMP).aggregate(false).build()).edge(EDGE, new SchemaEdgeDefinition.Builder().source(ID_STRING).destination(ID_STRING).directed(DIRECTED_EITHER).property(TIMESTAMP, TIMESTAMP_2).aggregate(false).build()).type(ID_STRING, String.class).type(DIRECTED_EITHER, new TypeDefinition.Builder().clazz(Boolean.class).build()).type(TestTypes.TIMESTAMP, new TypeDefinition.Builder().clazz(Long.class).build()).type(TIMESTAMP_2, new TypeDefinition.Builder().clazz(Long.class).build()).timestampProperty(TIMESTAMP).build();
        // When
        store.initialise("graphId", schema, properties);
        // Then - no validation exceptions
    }

    @Test
    public void shouldFailSchemaValidationWhenTimestampPropertyDoesNotHaveMaxAggregator() throws Exception {
        // Given
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloStoreTest.class));
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex(ID_STRING).property(TIMESTAMP, TestTypes.TIMESTAMP).build()).edge(EDGE, new SchemaEdgeDefinition.Builder().source(ID_STRING).destination(ID_STRING).property(TIMESTAMP, TIMESTAMP_2).build()).type(ID_STRING, String.class).type(TestTypes.TIMESTAMP, new TypeDefinition.Builder().clazz(Long.class).aggregateFunction(new Max()).build()).type(TIMESTAMP_2, new TypeDefinition.Builder().clazz(Long.class).aggregateFunction(new Min()).build()).timestampProperty(TIMESTAMP).build();
        // When / Then
        try {
            store.initialise("graphId", schema, properties);
            Assert.fail("Exception expected");
        } catch (final SchemaException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains(("The aggregator for the timestamp property must be set to: " + (Max.class.getName()))));
        }
    }
}

