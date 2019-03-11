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
package uk.gov.gchq.gaffer.accumulostore.utils;


import AccumuloStoreConstants.ACCUMULO_ELEMENT_CONVERTER_CLASS;
import AccumuloStoreConstants.AGGREGATOR_ITERATOR_NAME;
import AccumuloStoreConstants.SCHEMA;
import AccumuloStoreConstants.VALIDATOR_ITERATOR_NAME;
import AccumuloStoreConstants.VALIDATOR_ITERATOR_PRIORITY;
import CommonConstants.UTF_8;
import IteratorScope.majc;
import Property.TABLE_FILE_REPLICATION;
import TestGroups.EDGE;
import TestTypes.DIRECTED_TRUE;
import TestTypes.ID_STRING;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.iterators.IteratorUtil.IteratorScope;
import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.accumulostore.SingleUseMockAccumuloStore;
import uk.gov.gchq.gaffer.accumulostore.key.core.impl.byteEntity.ByteEntityAccumuloElementConverter;
import uk.gov.gchq.gaffer.accumulostore.key.impl.ValidatorFilter;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.koryphe.impl.binaryoperator.StringConcat;
import uk.gov.gchq.koryphe.impl.predicate.Exists;


public class TableUtilsTest {
    private static final String GRAPH_ID = "graph1";

    private static final String LOCALITY_GRAPH_ID = "localityTest";

    private static final String NO_AGGREGATORS_GRAPH_ID = "noAggGraph";

    @Test
    public void shouldCreateTableWithAllRequiredIterators() throws Exception {
        // Given
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Schema schema = new Schema.Builder().type(ID_STRING, new TypeDefinition.Builder().aggregateFunction(new StringConcat()).clazz(String.class).build()).type(DIRECTED_TRUE, Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source(ID_STRING).destination(ID_STRING).directed(DIRECTED_TRUE).build()).build();
        final AccumuloProperties props = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(TableUtilsTest.class));
        store.initialise(TableUtilsTest.GRAPH_ID, schema, props);
        // When
        TableUtils.createTable(store);
        // Then - this call will check the table is configured properly
        TableUtils.ensureTableExists(store);
    }

    @Test
    public void shouldFailTableValidationWhenMissingValidatorIterator() throws Exception {
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Runnable invalidateTable = () -> {
            try {
                AddUpdateTableIterator.removeIterator(store, VALIDATOR_ITERATOR_NAME);
            } catch (final StoreException e) {
                throw new RuntimeException(e);
            }
        };
        shouldFailTableValidationWhenTableInvalid(store, invalidateTable);
    }

    @Test
    public void shouldFailTableValidationWhenMissingAggregatorIterator() throws Exception {
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Runnable invalidateTable = () -> {
            try {
                AddUpdateTableIterator.removeIterator(store, AGGREGATOR_ITERATOR_NAME);
            } catch (final StoreException e) {
                throw new RuntimeException(e);
            }
        };
        shouldFailTableValidationWhenTableInvalid(store, invalidateTable);
    }

    @Test
    public void shouldCreateTableWithCorrectLocalityGroups() throws Exception {
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Schema schema = new Schema.Builder().type(ID_STRING, String.class).type(DIRECTED_TRUE, Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source(ID_STRING).destination(ID_STRING).directed(DIRECTED_TRUE).build()).build();
        final AccumuloProperties props = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(TableUtilsTest.class));
        store.initialise(TableUtilsTest.LOCALITY_GRAPH_ID, schema, props);
        // When
        TableUtils.createTable(store);
        final Map<String, Set<Text>> localityGroups = store.getConnection().tableOperations().getLocalityGroups(TableUtilsTest.LOCALITY_GRAPH_ID);
        Assert.assertEquals(1, localityGroups.size());
        Set<Text> localityGroup = localityGroups.get(EDGE);
        Assert.assertEquals(1, localityGroup.size());
        Assert.assertEquals(new Text(TestGroups.EDGE), localityGroup.toArray()[0]);
    }

    @Test
    public void shouldCreateTableCorrectlyIfSchemaContainsNoAggregators() throws Exception {
        // Given
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Schema schema = new Schema.Builder().type(ID_STRING, new TypeDefinition.Builder().clazz(String.class).validateFunctions(new Exists()).build()).type(DIRECTED_TRUE, Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source(ID_STRING).destination(ID_STRING).directed(DIRECTED_TRUE).aggregate(false).build()).build();
        final AccumuloProperties props = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(TableUtilsTest.class));
        store.initialise(TableUtilsTest.NO_AGGREGATORS_GRAPH_ID, schema, props);
        // When
        TableUtils.createTable(store);
        // Then
        final Map<String, EnumSet<IteratorScope>> itrs = store.getConnection().tableOperations().listIterators(TableUtilsTest.NO_AGGREGATORS_GRAPH_ID);
        Assert.assertEquals(1, itrs.size());
        final EnumSet<IteratorScope> validator = itrs.get(VALIDATOR_ITERATOR_NAME);
        Assert.assertEquals(EnumSet.allOf(IteratorScope.class), validator);
        final IteratorSetting validatorSetting = store.getConnection().tableOperations().getIteratorSetting(TableUtilsTest.NO_AGGREGATORS_GRAPH_ID, VALIDATOR_ITERATOR_NAME, majc);
        Assert.assertEquals(VALIDATOR_ITERATOR_PRIORITY, validatorSetting.getPriority());
        Assert.assertEquals(ValidatorFilter.class.getName(), validatorSetting.getIteratorClass());
        final Map<String, String> validatorOptions = validatorSetting.getOptions();
        Assert.assertNotNull(Schema.fromJson(validatorOptions.get(SCHEMA).getBytes(UTF_8)).getEdge(EDGE));
        Assert.assertEquals(ByteEntityAccumuloElementConverter.class.getName(), validatorOptions.get(ACCUMULO_ELEMENT_CONVERTER_CLASS));
        final EnumSet<IteratorScope> aggregator = itrs.get(AGGREGATOR_ITERATOR_NAME);
        Assert.assertNull(aggregator);
        final IteratorSetting aggregatorSetting = store.getConnection().tableOperations().getIteratorSetting(TableUtilsTest.NO_AGGREGATORS_GRAPH_ID, AGGREGATOR_ITERATOR_NAME, majc);
        Assert.assertNull(aggregatorSetting);
        final Map<String, String> tableProps = new HashMap<>();
        for (final Map.Entry<String, String> entry : store.getConnection().tableOperations().getProperties(TableUtilsTest.NO_AGGREGATORS_GRAPH_ID)) {
            tableProps.put(entry.getKey(), entry.getValue());
        }
        Assert.assertEquals(0, Integer.parseInt(tableProps.get(TABLE_FILE_REPLICATION.getKey())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfTableNameIsNotSpecified() throws StoreException {
        // Given
        final Schema schema = new Schema.Builder().type("int", Integer.class).type("string", String.class).type("boolean", Boolean.class).edge("EDGE", new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("boolean").build()).build();
        final AccumuloProperties properties = new AccumuloProperties();
        properties.setStoreClass(SingleUseMockAccumuloStore.class.getName());
        final AccumuloStore store = new AccumuloStore();
        store.initialise(null, schema, properties);
        // When
        TableUtils.ensureTableExists(store);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfTableNameIsNotSpecifiedWhenCreatingTable() throws TableExistsException, StoreException {
        // Given
        final Schema schema = new Schema.Builder().type("int", Integer.class).type("string", String.class).type("boolean", Boolean.class).edge("EDGE", new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("boolean").build()).build();
        final AccumuloProperties properties = new AccumuloProperties();
        properties.setStoreClass(SingleUseMockAccumuloStore.class.getName());
        final AccumuloStore store = new AccumuloStore();
        store.initialise(null, schema, properties);
        // When
        TableUtils.createTable(store);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfTableNameIsNotSpecifiedWhenCreatingAGraph() {
        // Given
        final Schema schema = new Schema.Builder().type("int", Integer.class).type("string", String.class).type("boolean", Boolean.class).edge("EDGE", new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("boolean").build()).build();
        final AccumuloProperties properties = new AccumuloProperties();
        properties.setStoreClass(SingleUseMockAccumuloStore.class.getName());
        // When
        new Graph.Builder().config(new GraphConfig.Builder().graphId(null).build()).addSchema(schema).storeProperties(properties).build();
    }
}

