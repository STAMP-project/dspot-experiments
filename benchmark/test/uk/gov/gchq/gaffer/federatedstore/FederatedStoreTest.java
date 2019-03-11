/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.federatedstore;


import FederatedAddGraphHandler.ERROR_ADDING_GRAPH_GRAPH_ID_S;
import FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS;
import SingleUseAccumuloStore.TRAITS;
import StoreTrait.ALL_TRAITS;
import StoreTrait.INGEST_AGGREGATION;
import StoreTrait.MATCHED_VERTEX;
import StoreTrait.POST_AGGREGATION_FILTERING;
import StoreTrait.POST_TRANSFORMATION_FILTERING;
import StoreTrait.PRE_AGGREGATION_FILTERING;
import StoreTrait.TRANSFORMATION;
import StoreUser.TEST_USER;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.cache.CacheServiceLoader;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.exception.SchemaException;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.federatedstore.operation.AddGraph;
import uk.gov.gchq.gaffer.federatedstore.operation.GetAllGraphIds;
import uk.gov.gchq.gaffer.federatedstore.operation.RemoveGraph;
import uk.gov.gchq.gaffer.federatedstore.operation.handler.impl.FederatedGetTraitsHandlerTest;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.graph.GraphSerialisable;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.export.graph.handler.GraphDelegate;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.serialisation.IntegerSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.MultiSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.store.library.GraphLibrary;
import uk.gov.gchq.gaffer.store.library.HashMapGraphLibrary;
import uk.gov.gchq.gaffer.store.operation.GetSchema;
import uk.gov.gchq.gaffer.store.operation.GetTraits;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.Schema.Builder;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.gaffer.user.StoreUser;
import uk.gov.gchq.gaffer.user.User;


public class FederatedStoreTest {
    public static final String ID_SCHEMA_ENTITY = "basicEntitySchema";

    public static final String ID_SCHEMA_EDGE = "basicEdgeSchema";

    public static final String ID_PROPS_ACC_1 = "mockAccProps1";

    public static final String ID_PROPS_ACC_2 = "mockAccProps2";

    public static final String ID_PROPS_ACC_ALT = "mockAccProps3";

    public static final String INVALID = "invalid";

    private static final String FEDERATED_STORE_ID = "testFederatedStoreId";

    private static final String ACC_ID_1 = "mockAccGraphId1";

    private static final String ACC_ID_2 = "mockAccGraphId2";

    private static final String MAP_ID_1 = "mockMapGraphId1";

    private static final String PATH_ACC_STORE_PROPERTIES_1 = "properties/singleUseMockAccStore.properties";

    private static final String PATH_ACC_STORE_PROPERTIES_2 = "properties/singleUseMockAccStore.properties";

    private static final String PATH_ACC_STORE_PROPERTIES_ALT = "properties/singleUseMockAccStoreAlt.properties";

    private static final String PATH_BASIC_ENTITY_SCHEMA_JSON = "schema/basicEntitySchema.json";

    private static final String PATH_ENTITY_A_SCHEMA_JSON = "schema/entityASchema.json";

    private static final String PATH_ENTITY_B_SCHEMA_JSON = "schema/entityBSchema.json";

    private static final String PATH_BASIC_EDGE_SCHEMA_JSON = "schema/basicEdgeSchema.json";

    private static final String EXCEPTION_NOT_THROWN = "exception not thrown";

    public static final String UNUSUAL_KEY = "unusualKey";

    public static final String KEY_DOES_NOT_BELONG = (((FederatedStoreTest.UNUSUAL_KEY) + " was added to ") + (FederatedStoreTest.ID_PROPS_ACC_2)) + " it should not be there";

    private static final String ALL_USERS = StoreUser.ALL_USERS;

    private static final HashSet<String> GRAPH_AUTHS = Sets.newHashSet(FederatedStoreTest.ALL_USERS);

    private static final String CACHE_SERVICE_CLASS_STRING = "uk.gov.gchq.gaffer.cache.impl.HashMapCacheService";

    private static final String INVALID_CACHE_SERVICE_CLASS_STRING = "uk.gov.gchq.invalid";

    private static final String CACHE_SERVICE_NAME = "federatedStoreGraphs";

    public static final String PATH_INCOMPLETE_SCHEMA = "/schema/edgeX2NoTypesSchema.json";

    public static final String PATH_INCOMPLETE_SCHEMA_PART_2 = "/schema/edgeTypeSchema.json";

    private FederatedStore store;

    private FederatedStoreProperties federatedProperties;

    private HashMapGraphLibrary library;

    private Context userContext;

    private User blankUser;

    @Test
    public void shouldUseMulti() throws Exception {
        System.out.println(new String(JSONSerialiser.serialise(new Schema.Builder().type("ExampleType", new TypeDefinition.Builder().clazz(String.class).serialiser(new MultiSerialiser().addSerialiser(((byte) (0)), new StringSerialiser(), String.class).addSerialiser(((byte) (1)), new IntegerSerialiser(), Integer.class)).build()).build(), true)));
    }

    @Test
    public void shouldLoadGraphsWithIds() throws Exception {
        // When
        int before = store.getGraphs(blankUser, null).size();
        addGraphWithIds(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.ID_PROPS_ACC_2, FederatedStoreTest.ID_SCHEMA_EDGE);
        addGraphWithIds(FederatedStoreTest.ACC_ID_1, FederatedStoreTest.ID_PROPS_ACC_1, FederatedStoreTest.ID_SCHEMA_ENTITY);
        // Then
        Collection<Graph> graphs = store.getGraphs(blankUser, null);
        int after = graphs.size();
        Assert.assertEquals(0, before);
        Assert.assertEquals(2, after);
        ArrayList<String> graphNames = Lists.newArrayList(FederatedStoreTest.ACC_ID_1, FederatedStoreTest.ACC_ID_2);
        for (Graph graph : graphs) {
            Assert.assertTrue(graphNames.contains(graph.getGraphId()));
        }
    }

    @Test
    public void shouldThrowErrorForFailedSchemaID() throws Exception {
        // When / Then
        try {
            addGraphWithIds(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.ID_PROPS_ACC_2, FederatedStoreTest.INVALID);
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (final Exception e) {
            assertContains(e.getCause(), GraphDelegate.SCHEMA_COULD_NOT_BE_FOUND_IN_THE_GRAPH_LIBRARY_WITH_ID_S, Arrays.toString(new String[]{ FederatedStoreTest.INVALID }));
        }
    }

    @Test
    public void shouldThrowErrorForFailedPropertyID() throws Exception {
        // When / Then
        try {
            addGraphWithIds(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.INVALID, FederatedStoreTest.ID_SCHEMA_EDGE);
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (final Exception e) {
            assertContains(e.getCause(), GraphDelegate.STORE_PROPERTIES_COULD_NOT_BE_FOUND_IN_THE_GRAPH_LIBRARY_WITH_ID_S, FederatedStoreTest.INVALID);
        }
    }

    @Test
    public void shouldThrowErrorForMissingProperty() throws Exception {
        // When / Then
        try {
            ArrayList<String> schemas = Lists.newArrayList(FederatedStoreTest.ID_SCHEMA_EDGE);
            store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).isPublic(true).parentSchemaIds(schemas).build(), userContext);
            Assert.fail("a graph was created without a defined properties");
        } catch (final Exception e) {
            assertContains(e.getCause(), GraphDelegate.GRAPH_ID_S_CANNOT_BE_CREATED_WITHOUT_DEFINED_KNOWN_S, FederatedStoreTest.ACC_ID_2, "StoreProperties");
        }
    }

    @Test
    public void shouldThrowErrorForMissingSchema() throws Exception {
        // When / Then
        try {
            store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).isPublic(true).parentPropertiesId(FederatedStoreTest.ID_PROPS_ACC_2).build(), userContext);
            Assert.fail("a graph was created without a defined schema");
        } catch (final Exception e) {
            assertContains(e.getCause(), GraphDelegate.GRAPH_ID_S_CANNOT_BE_CREATED_WITHOUT_DEFINED_KNOWN_S, FederatedStoreTest.ACC_ID_2, "Schema");
        }
    }

    @Test
    public void shouldNotAllowOverwritingOfGraphWithinFederatedScope() throws Exception {
        // Given
        addGraphWithIds(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.ID_PROPS_ACC_2, FederatedStoreTest.ID_SCHEMA_ENTITY);
        // When / Then
        try {
            addGraphWithIds(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.ID_PROPS_ACC_2, FederatedStoreTest.ID_SCHEMA_EDGE);
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (final Exception e) {
            assertContains(e, "User is attempting to overwrite a graph");
            assertContains(e, "GraphId: ", FederatedStoreTest.ACC_ID_2);
        }
        // When / Then
        try {
            addGraphWithIds(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.ID_PROPS_ACC_ALT, FederatedStoreTest.ID_SCHEMA_ENTITY);
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (final Exception e) {
            assertContains(e, "User is attempting to overwrite a graph");
            assertContains(e, "GraphId: ", FederatedStoreTest.ACC_ID_2);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldDoUnhandledOperation() throws Exception {
        store.doUnhandledOperation(null, null);
    }

    @Test
    public void shouldAlwaysReturnSupportedTraits() throws Exception {
        // Given
        addGraphWithIds(FederatedStoreTest.ACC_ID_1, FederatedStoreTest.ID_PROPS_ACC_1, FederatedStoreTest.ID_SCHEMA_ENTITY);
        Set<StoreTrait> before = store.getTraits();
        // When
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON);
        Set<StoreTrait> after = store.getTraits();
        Assert.assertEquals(values().length, before.size());
        Assert.assertEquals(values().length, after.size());
        Assert.assertEquals(before, after);
    }

    @Test
    public void shouldUpdateSchemaWhenNewGraphIsAdded() throws Exception {
        // Given
        addGraphWithPaths(FederatedStoreTest.ACC_ID_1, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON);
        Schema before = store.getSchema(((Operation) (null)), blankUser);
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON);
        Schema after = store.getSchema(((Operation) (null)), blankUser);
        // Then
        Assert.assertNotEquals(before, after);
    }

    @Test
    public void shouldUpdateSchemaWhenNewGraphIsRemoved() throws Exception {
        // Given
        addGraphWithPaths(FederatedStoreTest.ACC_ID_1, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON);
        Schema was = store.getSchema(((Operation) (null)), blankUser);
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON);
        Schema before = store.getSchema(((Operation) (null)), blankUser);
        // When
        store.remove(FederatedStoreTest.ACC_ID_2, blankUser);
        Schema after = store.getSchema(((Operation) (null)), blankUser);
        Assert.assertNotEquals(before.toString(), after.toString());
        Assert.assertEquals(was.toString(), after.toString());
    }

    @Test
    public void shouldFailWithIncompleteSchema() throws Exception {
        // When / Then
        try {
            addGraphWithPaths(FederatedStoreTest.ACC_ID_1, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_INCOMPLETE_SCHEMA);
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (final Exception e) {
            assertContains(e, ERROR_ADDING_GRAPH_GRAPH_ID_S, FederatedStoreTest.ACC_ID_1);
        }
    }

    @Test
    public void shouldTakeCompleteSchemaFromTwoFiles() throws Exception {
        // Given
        int before = store.getGraphs(blankUser, null).size();
        addGraphWithPaths(FederatedStoreTest.ACC_ID_1, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_INCOMPLETE_SCHEMA, FederatedStoreTest.PATH_INCOMPLETE_SCHEMA_PART_2);
        // When
        int after = store.getGraphs(blankUser, null).size();
        // Then
        Assert.assertEquals(0, before);
        Assert.assertEquals(1, after);
    }

    @Test
    public void shouldAddTwoGraphs() throws Exception {
        // Given
        int sizeBefore = store.getGraphs(blankUser, null).size();
        // When
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON);
        addGraphWithPaths(FederatedStoreTest.ACC_ID_1, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON);
        int sizeAfter = store.getGraphs(blankUser, null).size();
        // Then
        Assert.assertEquals(0, sizeBefore);
        Assert.assertEquals(2, sizeAfter);
    }

    @Test
    public void shouldCombineTraitsToMin() throws Exception {
        // Given
        final GetTraits getTraits = new GetTraits.Builder().currentTraits(true).build();
        // When
        final Set<StoreTrait> before = store.getTraits(getTraits, userContext);
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        store.execute(new AddGraph.Builder().schema(new Schema()).isPublic(true).graphId(FederatedStoreTest.ACC_ID_1).storeProperties(StoreProperties.loadStoreProperties("/properties/singleUseMockAccStore.properties")).build(), new Context(testUser()));
        final Set<StoreTrait> afterAcc = store.getTraits(getTraits, userContext);
        store.execute(new AddGraph.Builder().schema(new Schema()).isPublic(true).graphId(FederatedStoreTest.MAP_ID_1).storeProperties(new FederatedGetTraitsHandlerTest.TestStorePropertiesImpl()).build(), new Context(testUser()));
        final Set<StoreTrait> afterMap = store.getTraits(getTraits, userContext);
        // Then
        Assert.assertNotEquals(TRAITS, new HashSet(Arrays.asList(INGEST_AGGREGATION, PRE_AGGREGATION_FILTERING, POST_AGGREGATION_FILTERING, TRANSFORMATION, POST_TRANSFORMATION_FILTERING, MATCHED_VERTEX)));
        Assert.assertEquals(ALL_TRAITS, before);
        Assert.assertEquals(Sets.newHashSet(TRANSFORMATION, PRE_AGGREGATION_FILTERING, POST_AGGREGATION_FILTERING, POST_TRANSFORMATION_FILTERING, ORDERED, MATCHED_VERTEX), afterAcc);
        Assert.assertEquals(Sets.newHashSet(TRANSFORMATION, PRE_AGGREGATION_FILTERING, POST_AGGREGATION_FILTERING, POST_TRANSFORMATION_FILTERING, MATCHED_VERTEX), afterMap);
    }

    @Test
    public void shouldContainNoElements() throws Exception {
        // When
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON);
        Set<Element> after = getElements();
        // Then
        Assert.assertEquals(0, after.size());
    }

    @Test
    public void shouldAddEdgesToOneGraph() throws Exception {
        // Given
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON);
        AddElements op = new AddElements.Builder().input(new Edge.Builder().group("BasicEdge").source("testSource").dest("testDest").property("property1", 12).build()).build();
        // When
        store.execute(op, userContext);
        // Then
        Assert.assertEquals(1, getElements().size());
    }

    @Test
    public void shouldReturnGraphIds() throws Exception {
        // Given
        addGraphWithPaths(FederatedStoreTest.ACC_ID_1, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON);
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON);
        // When
        Collection<String> allGraphIds = store.getAllGraphIds(blankUser);
        // Then
        Assert.assertEquals(2, allGraphIds.size());
        Assert.assertTrue(allGraphIds.contains(FederatedStoreTest.ACC_ID_1));
        Assert.assertTrue(allGraphIds.contains(FederatedStoreTest.ACC_ID_2));
    }

    @Test
    public void shouldUpdateGraphIds() throws Exception {
        // Given
        addGraphWithPaths(FederatedStoreTest.ACC_ID_1, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON);
        // When
        Collection<String> allGraphId = store.getAllGraphIds(blankUser);
        // Then
        Assert.assertEquals(1, allGraphId.size());
        Assert.assertTrue(allGraphId.contains(FederatedStoreTest.ACC_ID_1));
        Assert.assertFalse(allGraphId.contains(FederatedStoreTest.ACC_ID_2));
        // When
        addGraphWithIds(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.ID_PROPS_ACC_2, FederatedStoreTest.ID_SCHEMA_ENTITY);
        Collection<String> allGraphId2 = store.getAllGraphIds(blankUser);
        // Then
        Assert.assertEquals(2, allGraphId2.size());
        Assert.assertTrue(allGraphId2.contains(FederatedStoreTest.ACC_ID_1));
        Assert.assertTrue(allGraphId2.contains(FederatedStoreTest.ACC_ID_2));
        // When
        store.remove(FederatedStoreTest.ACC_ID_1, blankUser);
        Collection<String> allGraphId3 = store.getAllGraphIds(blankUser);
        // Then
        Assert.assertEquals(1, allGraphId3.size());
        Assert.assertFalse(allGraphId3.contains(FederatedStoreTest.ACC_ID_1));
        Assert.assertTrue(allGraphId3.contains(FederatedStoreTest.ACC_ID_2));
    }

    @Test
    public void shouldGetAllGraphIdsInUnmodifiableSet() throws Exception {
        // Given
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON);
        // When / Then
        Collection<String> allGraphIds = store.getAllGraphIds(blankUser);
        try {
            allGraphIds.add("newId");
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
        try {
            allGraphIds.remove("newId");
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void shouldNotUseSchema() throws Exception {
        // Given
        final Schema unusedMock = Mockito.mock(Schema.class);
        // When
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, unusedMock, federatedProperties);
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON);
        // Then
        Mockito.verifyNoMoreInteractions(unusedMock);
    }

    @Test
    public void shouldAddGraphFromLibrary() throws Exception {
        // Given
        library.add(FederatedStoreTest.ACC_ID_2, library.getSchema(FederatedStoreTest.ID_SCHEMA_ENTITY), library.getProperties(FederatedStoreTest.ID_PROPS_ACC_2));
        // When
        final int before = store.getGraphs(blankUser, null).size();
        store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).build(), new Context(blankUser));
        final int after = store.getGraphs(blankUser, null).size();
        // Then
        Assert.assertEquals(0, before);
        Assert.assertEquals(1, after);
    }

    @Test
    public void shouldAddGraphWithPropertiesFromGraphLibrary() throws Exception {
        // When
        store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).parentPropertiesId(FederatedStoreTest.ID_PROPS_ACC_ALT).isPublic(true).schema(getSchemaFromPath(FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON)).build(), userContext);
        // Then
        Assert.assertEquals(1, store.getGraphs(blankUser, null).size());
        Assert.assertTrue(library.getProperties(FederatedStoreTest.ID_PROPS_ACC_ALT).equals(getPropertiesFromPath(FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT)));
    }

    @Test
    public void shouldAddGraphWithSchemaFromGraphLibrary() throws Exception {
        // When
        store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).storeProperties(getPropertiesFromPath(FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT)).isPublic(true).parentSchemaIds(Lists.newArrayList(FederatedStoreTest.ID_SCHEMA_ENTITY)).build(), userContext);
        // Then
        Assert.assertEquals(1, store.getGraphs(blankUser, null).size());
        Assert.assertTrue(library.getSchema(FederatedStoreTest.ID_SCHEMA_ENTITY).toString().equals(getSchemaFromPath(FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON).toString()));
    }

    @Test
    public void shouldAddGraphWithPropertiesAndSchemaFromGraphLibrary() throws Exception {
        // When
        addGraphWithIds(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.ID_PROPS_ACC_ALT, FederatedStoreTest.ID_SCHEMA_ENTITY);
        // Then
        Assert.assertEquals(1, store.getGraphs(blankUser, null).size());
        Graph graph = store.getGraphs(blankUser, FederatedStoreTest.ACC_ID_2).iterator().next();
        Assert.assertEquals(getSchemaFromPath(FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON).toString(), graph.getSchema().toString());
        Assert.assertEquals(getPropertiesFromPath(FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT), graph.getStoreProperties());
    }

    @Test
    public void shouldAddGraphWithPropertiesFromGraphLibraryOverridden() throws Exception {
        // Given
        Assert.assertFalse(FederatedStoreTest.KEY_DOES_NOT_BELONG, library.getProperties(FederatedStoreTest.ID_PROPS_ACC_2).containsKey(FederatedStoreTest.UNUSUAL_KEY));
        // When
        Builder schema = new Builder();
        for (String path : new String[]{ FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON }) {
            schema.merge(getSchemaFromPath(path));
        }
        store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).storeProperties(getPropertiesFromPath(FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT)).parentPropertiesId(FederatedStoreTest.ID_PROPS_ACC_2).isPublic(true).schema(schema.build()).build(), userContext);
        // Then
        Assert.assertEquals(1, store.getGraphs(blankUser, null).size());
        Assert.assertTrue(store.getGraphs(blankUser, null).iterator().next().getStoreProperties().containsKey(FederatedStoreTest.UNUSUAL_KEY));
        Assert.assertFalse(FederatedStoreTest.KEY_DOES_NOT_BELONG, library.getProperties(FederatedStoreTest.ID_PROPS_ACC_2).containsKey(FederatedStoreTest.UNUSUAL_KEY));
        Assert.assertTrue(((store.getGraphs(blankUser, null).iterator().next().getStoreProperties().getProperties().getProperty(FederatedStoreTest.UNUSUAL_KEY)) != null));
    }

    @Test
    public void shouldAddGraphWithSchemaFromGraphLibraryOverridden() throws Exception {
        ArrayList<String> schemas = Lists.newArrayList(FederatedStoreTest.ID_SCHEMA_ENTITY);
        store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).isPublic(true).schema(getSchemaFromPath(FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON)).parentSchemaIds(schemas).parentPropertiesId(FederatedStoreTest.ID_PROPS_ACC_2).build(), userContext);
        // Then
        Assert.assertEquals(1, store.getGraphs(blankUser, null).size());
        Assert.assertTrue(store.getGraphs(blankUser, null).iterator().next().getSchema().getEntityGroups().contains("BasicEntity"));
    }

    @Test
    public void shouldAddGraphWithPropertiesAndSchemaFromGraphLibraryOverridden() throws Exception {
        // Given
        Assert.assertFalse(FederatedStoreTest.KEY_DOES_NOT_BELONG, library.getProperties(FederatedStoreTest.ID_PROPS_ACC_2).containsKey(FederatedStoreTest.UNUSUAL_KEY));
        // When
        Builder tempSchema = new Builder();
        for (String path : new String[]{ FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON }) {
            tempSchema.merge(getSchemaFromPath(path));
        }
        store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).isPublic(true).storeProperties(getPropertiesFromPath(FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT)).parentPropertiesId(FederatedStoreTest.ID_PROPS_ACC_2).schema(tempSchema.build()).parentSchemaIds(Lists.newArrayList(FederatedStoreTest.ID_SCHEMA_ENTITY)).build(), userContext);
        // Then
        Assert.assertEquals(1, store.getGraphs(blankUser, null).size());
        Assert.assertTrue(store.getGraphs(blankUser, null).iterator().next().getStoreProperties().containsKey(FederatedStoreTest.UNUSUAL_KEY));
        Assert.assertFalse(FederatedStoreTest.KEY_DOES_NOT_BELONG, library.getProperties(FederatedStoreTest.ID_PROPS_ACC_2).containsKey(FederatedStoreTest.UNUSUAL_KEY));
        Assert.assertTrue(((store.getGraphs(blankUser, null).iterator().next().getStoreProperties().getProperties().getProperty(FederatedStoreTest.UNUSUAL_KEY)) != null));
        Assert.assertTrue(store.getGraphs(blankUser, null).iterator().next().getSchema().getEntityGroups().contains("BasicEntity"));
    }

    @Test
    public void shouldNotAllowOverridingOfKnownGraphInLibrary() throws Exception {
        // Given
        library.add(FederatedStoreTest.ACC_ID_2, getSchemaFromPath(FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON), getPropertiesFromPath(FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT));
        // When / Then
        try {
            store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).parentPropertiesId(FederatedStoreTest.ID_PROPS_ACC_1).isPublic(true).build(), userContext);
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (final Exception e) {
            assertContains(e.getCause(), (("Graph: " + (FederatedStoreTest.ACC_ID_2)) + " already exists so you cannot use a different StoreProperties"));
        }
        // When / Then
        try {
            store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).parentSchemaIds(Lists.newArrayList(FederatedStoreTest.ID_SCHEMA_EDGE)).isPublic(true).build(), userContext);
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (final Exception e) {
            assertContains(e.getCause(), (("Graph: " + (FederatedStoreTest.ACC_ID_2)) + " already exists so you cannot use a different Schema"));
        }
    }

    @Test
    public void shouldFederatedIfUserHasCorrectAuths() throws Exception {
        // Given
        store.addGraphs(FederatedStoreTest.GRAPH_AUTHS, null, false, new GraphSerialisable.Builder().config(new GraphConfig.Builder().graphId(FederatedStoreTest.ACC_ID_2).build()).properties(getPropertiesFromPath(FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT)).schema(getSchemaFromPath(FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON)).build());
        // When
        final CloseableIterable<? extends Element> elements = store.execute(new GetAllElements(), new Context(new User.Builder().userId(blankUser.getUserId()).opAuth(FederatedStoreTest.ALL_USERS).build()));
        // Then
        Assert.assertFalse(elements.iterator().hasNext());
        // When - user cannot see any graphs
        final CloseableIterable<? extends Element> elements2 = store.execute(new GetAllElements(), new Context(new User.Builder().userId(blankUser.getUserId()).opAuths("x").build()));
        // Then
        Assert.assertEquals(0, Iterables.size(elements2));
    }

    @Test
    public void shouldReturnSpecificGraphsFromCSVString() throws Exception {
        // Given
        final List<Collection<GraphSerialisable>> graphLists = populateGraphs(1, 2, 4);
        final Collection<GraphSerialisable> expectedGraphs = graphLists.get(0);
        final Collection<GraphSerialisable> unexpectedGraphs = graphLists.get(1);
        // When
        final Collection<Graph> returnedGraphs = store.getGraphs(blankUser, "mockGraphId1,mockGraphId2,mockGraphId4");
        // Then
        Assert.assertTrue(((returnedGraphs.size()) == 3));
        Assert.assertTrue(returnedGraphs.containsAll(toGraphs(expectedGraphs)));
        Assert.assertFalse(checkUnexpected(toGraphs(unexpectedGraphs), returnedGraphs));
    }

    @Test
    public void shouldReturnEnabledByDefaultGraphsForNullString() throws Exception {
        // Given
        populateGraphs();
        // When
        final Collection<Graph> returnedGraphs = store.getGraphs(blankUser, null);
        // Then
        final Set<String> graphIds = returnedGraphs.stream().map(Graph::getGraphId).collect(Collectors.toSet());
        Assert.assertEquals(Sets.newHashSet("mockGraphId0", "mockGraphId2", "mockGraphId4"), graphIds);
    }

    @Test
    public void shouldReturnNotReturnEnabledOrDisabledGraphsWhenNotInCsv() throws Exception {
        // Given
        populateGraphs();
        // When
        final Collection<Graph> returnedGraphs = store.getGraphs(blankUser, "mockGraphId0,mockGraphId1");
        // Then
        final Set<String> graphIds = returnedGraphs.stream().map(Graph::getGraphId).collect(Collectors.toSet());
        Assert.assertEquals(Sets.newHashSet("mockGraphId0", "mockGraphId1"), graphIds);
    }

    @Test
    public void shouldReturnNoGraphsFromEmptyString() throws Exception {
        // Given
        final List<Collection<GraphSerialisable>> graphLists = populateGraphs();
        final Collection<GraphSerialisable> expectedGraphs = graphLists.get(0);
        // When
        final Collection<Graph> returnedGraphs = store.getGraphs(blankUser, "");
        // Then
        Assert.assertTrue(returnedGraphs.toString(), returnedGraphs.isEmpty());
        Assert.assertTrue(expectedGraphs.toString(), expectedGraphs.isEmpty());
    }

    @Test
    public void shouldReturnGraphsWithLeadingCommaString() throws Exception {
        // Given
        final List<Collection<GraphSerialisable>> graphLists = populateGraphs(2, 4);
        final Collection<GraphSerialisable> expectedGraphs = graphLists.get(0);
        final Collection<GraphSerialisable> unexpectedGraphs = graphLists.get(1);
        // When
        final Collection<Graph> returnedGraphs = store.getGraphs(blankUser, ",mockGraphId2,mockGraphId4");
        // Then
        Assert.assertTrue(((returnedGraphs.size()) == 2));
        Assert.assertTrue(returnedGraphs.containsAll(toGraphs(expectedGraphs)));
        Assert.assertFalse(checkUnexpected(toGraphs(unexpectedGraphs), returnedGraphs));
    }

    @Test
    public void shouldAddGraphIdWithAuths() throws Exception {
        // Given
        final Graph fedGraph = new Graph.Builder().config(new GraphConfig.Builder().graphId(FederatedStoreTest.FEDERATED_STORE_ID).library(library).build()).addStoreProperties(federatedProperties).build();
        addGraphWithIds(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.ID_PROPS_ACC_2, FederatedStoreTest.ID_SCHEMA_ENTITY);
        library.add(FederatedStoreTest.ACC_ID_2, getSchemaFromPath(FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON), getPropertiesFromPath(FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT));
        // When
        int before = 0;
        for (String ignore : fedGraph.execute(new GetAllGraphIds(), blankUser)) {
            before++;
        }
        fedGraph.execute(new AddGraph.Builder().graphAuths("auth").graphId(FederatedStoreTest.ACC_ID_2).build(), blankUser);
        int after = 0;
        for (String ignore : fedGraph.execute(new GetAllGraphIds(), blankUser)) {
            after++;
        }
        fedGraph.execute(new AddElements.Builder().input(new Entity.Builder().group("BasicEntity").vertex("v1").build()).build(), blankUser);
        final CloseableIterable<? extends Element> elements = fedGraph.execute(new GetAllElements(), new User.Builder().userId(((TEST_USER) + "Other")).opAuth("auth").build());
        final CloseableIterable<? extends Element> elements2 = fedGraph.execute(new GetAllElements(), new User.Builder().userId(((TEST_USER) + "Other")).opAuths("x").build());
        Assert.assertEquals(0, Iterables.size(elements2));
        // Then
        Assert.assertEquals(0, before);
        Assert.assertEquals(1, after);
        Assert.assertNotNull(elements);
        Assert.assertTrue(elements.iterator().hasNext());
    }

    @Test
    public void shouldThrowWithPropertiesErrorFromGraphLibrary() throws Exception {
        Builder schema = new Builder();
        for (String path : new String[]{ FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON }) {
            schema.merge(getSchemaFromPath(path));
        }
        final GraphLibrary mockLibrary = Mockito.mock(GraphLibrary.class);
        final String error = "test Something went wrong";
        Mockito.when(mockLibrary.getProperties(FederatedStoreTest.ID_PROPS_ACC_2)).thenThrow(new IllegalArgumentException(error));
        store.setGraphLibrary(mockLibrary);
        clearCache();
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        try {
            store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).parentPropertiesId(FederatedStoreTest.ID_PROPS_ACC_2).isPublic(true).schema(schema.build()).build(), userContext);
            Assert.fail("exception not thrown");
        } catch (Exception e) {
            Assert.assertEquals(error, e.getCause().getMessage());
        }
        Mockito.verify(mockLibrary).getProperties(FederatedStoreTest.ID_PROPS_ACC_2);
    }

    @Test
    public void shouldThrowWithSchemaErrorFromGraphLibrary() throws Exception {
        // Given
        final GraphLibrary mockLibrary = Mockito.mock(GraphLibrary.class);
        final String error = "test Something went wrong";
        Mockito.when(mockLibrary.getSchema(FederatedStoreTest.ID_SCHEMA_ENTITY)).thenThrow(new IllegalArgumentException(error));
        store.setGraphLibrary(mockLibrary);
        clearCache();
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        // When / Then
        try {
            store.execute(new AddGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).storeProperties(getPropertiesFromPath(FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT)).isPublic(true).parentSchemaIds(Lists.newArrayList(FederatedStoreTest.ID_SCHEMA_ENTITY)).build(), userContext);
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (Exception e) {
            Assert.assertEquals(error, e.getCause().getMessage());
        }
        Mockito.verify(mockLibrary).getSchema(FederatedStoreTest.ID_SCHEMA_ENTITY);
    }

    @Test
    public void shouldReturnASingleGraph() throws Exception {
        // Given
        final List<Collection<GraphSerialisable>> graphLists = populateGraphs(1);
        final Collection<GraphSerialisable> expectedGraphs = graphLists.get(0);
        final Collection<GraphSerialisable> unexpectedGraphs = graphLists.get(1);
        // When
        final Collection<Graph> returnedGraphs = store.getGraphs(blankUser, "mockGraphId1");
        // Then
        Assert.assertTrue(((returnedGraphs.size()) == 1));
        Assert.assertTrue(returnedGraphs.containsAll(toGraphs(expectedGraphs)));
        Assert.assertFalse(checkUnexpected(toGraphs(unexpectedGraphs), returnedGraphs));
    }

    @Test
    public void shouldThrowExceptionWithInvalidCacheClass() throws StoreException {
        federatedProperties.setCacheProperties(FederatedStoreTest.INVALID_CACHE_SERVICE_CLASS_STRING);
        try {
            clearCache();
            store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Failed to instantiate cache"));
        }
    }

    @Test
    public void shouldReuseGraphsAlreadyInCache() throws Exception {
        // Check cache is empty
        federatedProperties.setCacheProperties(FederatedStoreTest.CACHE_SERVICE_CLASS_STRING);
        Assert.assertNull(CacheServiceLoader.getService());
        // initialise FedStore
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        // add something so it will be in the cache
        GraphSerialisable graphToAdd = new GraphSerialisable.Builder().config(new GraphConfig(FederatedStoreTest.ACC_ID_2)).properties(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT)).schema(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON)).build();
        store.addGraphs(null, TEST_USER, true, graphToAdd);
        // check the store and the cache
        Assert.assertEquals(1, store.getAllGraphIds(blankUser).size());
        Assert.assertTrue(CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME).contains(FederatedStoreTest.ACC_ID_2));
        Assert.assertTrue(CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME).contains(FederatedStoreTest.ACC_ID_2));
        // restart the store
        store = new FederatedStore();
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        // check the graph is already in there from the cache
        Assert.assertTrue(((("Keys: " + (CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME))) + " did not contain ") + (FederatedStoreTest.ACC_ID_2)), CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME).contains(FederatedStoreTest.ACC_ID_2));
        Assert.assertEquals(1, store.getAllGraphIds(blankUser).size());
    }

    @Test
    public void shouldInitialiseWithCache() throws StoreException {
        Assert.assertNull(CacheServiceLoader.getService());
        federatedProperties.setCacheProperties(FederatedStoreTest.CACHE_SERVICE_CLASS_STRING);
        Assert.assertNull(CacheServiceLoader.getService());
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        Assert.assertNotNull(CacheServiceLoader.getService());
    }

    @Test
    public void shouldThrowExceptionWithoutInitialisation() throws StoreException {
        federatedProperties.setCacheProperties(FederatedStoreTest.CACHE_SERVICE_CLASS_STRING);
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        // Given
        GraphSerialisable graphToAdd = new GraphSerialisable.Builder().config(new GraphConfig(FederatedStoreTest.ACC_ID_1)).properties(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT)).schema(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON)).build();
        clearCache();
        // When / Then
        try {
            store.addGraphs(null, TEST_USER, false, graphToAdd);
            Assert.fail(FederatedStoreTest.EXCEPTION_NOT_THROWN);
        } catch (final Exception e) {
            Assert.assertTrue(e.getMessage().contains("No cache has been set"));
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenInitialisedWithNoCacheClassInProperties() throws StoreException {
        // Given
        federatedProperties = new FederatedStoreProperties();
        // When / Then
        try {
            store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        } catch (final StoreException e) {
            Assert.fail("FederatedStore does not have to have a cache.");
        }
    }

    @Test
    public void shouldAddGraphsToCache() throws Exception {
        federatedProperties.setCacheProperties(FederatedStoreTest.CACHE_SERVICE_CLASS_STRING);
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        // Given
        GraphSerialisable graphToAdd = new GraphSerialisable.Builder().config(new GraphConfig(FederatedStoreTest.ACC_ID_1)).properties(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT)).schema(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON)).build();
        // When
        store.addGraphs(null, TEST_USER, true, graphToAdd);
        // Then
        Assert.assertEquals(1, store.getGraphs(blankUser, FederatedStoreTest.ACC_ID_1).size());
        // When
        Collection<Graph> storeGraphs = store.getGraphs(blankUser, null);
        // Then
        Assert.assertTrue(CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME).contains(FederatedStoreTest.ACC_ID_1));
        Assert.assertTrue(storeGraphs.contains(graphToAdd.getGraph()));
        // When
        store = new FederatedStore();
        // Then
        Assert.assertTrue(CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME).contains(FederatedStoreTest.ACC_ID_1));
    }

    @Test
    public void shouldAddMultipleGraphsToCache() throws Exception {
        federatedProperties.setCacheProperties(FederatedStoreTest.CACHE_SERVICE_CLASS_STRING);
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        // Given
        List<GraphSerialisable> graphsToAdd = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            graphsToAdd.add(new GraphSerialisable.Builder().config(new GraphConfig(((FederatedStoreTest.ACC_ID_1) + i))).properties(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT)).schema(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON)).build());
        }
        // When
        store.addGraphs(null, TEST_USER, false, graphsToAdd.toArray(new GraphSerialisable[graphsToAdd.size()]));
        // Then
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME).contains(((FederatedStoreTest.ACC_ID_1) + i)));
        }
        // When
        store = new FederatedStore();
        // Then
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME).contains(((FederatedStoreTest.ACC_ID_1) + i)));
        }
    }

    @Test
    public void shouldAddAGraphRemoveAGraphAndBeAbleToReuseTheGraphId() throws Exception {
        // Given
        // When
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_ENTITY_SCHEMA_JSON);
        store.execute(new RemoveGraph.Builder().graphId(FederatedStoreTest.ACC_ID_2).build(), userContext);
        addGraphWithPaths(FederatedStoreTest.ACC_ID_2, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_ALT, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON);
        // Then
        final Collection<Graph> graphs = store.getGraphs(userContext.getUser(), FederatedStoreTest.ACC_ID_2);
        Assert.assertEquals(1, graphs.size());
        JsonAssert.assertEquals(JSONSerialiser.serialise(Schema.fromJson(StreamUtil.openStream(getClass(), FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON))), JSONSerialiser.serialise(graphs.iterator().next().getSchema()));
    }

    @Test
    public void shouldNotAddGraphToLibraryWhenReinitialisingFederatedStoreWithGraphFromCache() throws Exception {
        // Check cache is empty
        federatedProperties.setCacheProperties(FederatedStoreTest.CACHE_SERVICE_CLASS_STRING);
        Assert.assertNull(CacheServiceLoader.getService());
        // initialise FedStore
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        // add something so it will be in the cache
        GraphSerialisable graphToAdd = new GraphSerialisable.Builder().config(new GraphConfig(FederatedStoreTest.ACC_ID_1)).properties(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_1)).schema(StreamUtil.openStream(FederatedStoreTest.class, FederatedStoreTest.PATH_BASIC_EDGE_SCHEMA_JSON)).build();
        store.addGraphs(null, TEST_USER, true, graphToAdd);
        // check is in the store
        Assert.assertEquals(1, store.getAllGraphIds(blankUser).size());
        // check is in the cache
        Assert.assertTrue(CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME).contains(FederatedStoreTest.ACC_ID_1));
        // check isn't in the library
        Assert.assertNull(store.getGraphLibrary().get(FederatedStoreTest.ACC_ID_1));
        // restart the store
        store = new FederatedStore();
        // clear and set the GraphLibrary again
        store.setGraphLibrary(library);
        // initialise the FedStore
        store.initialise(FederatedStoreTest.FEDERATED_STORE_ID, null, federatedProperties);
        // check is in the cache still
        Assert.assertTrue(((("Keys: " + (CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME))) + " did not contain ") + (FederatedStoreTest.ACC_ID_1)), CacheServiceLoader.getService().getAllKeysFromCache(FederatedStoreTest.CACHE_SERVICE_NAME).contains(FederatedStoreTest.ACC_ID_1));
        // check is in the store from the cache
        Assert.assertEquals(1, store.getAllGraphIds(blankUser).size());
        // check the graph isn't in the GraphLibrary
        Assert.assertNull(store.getGraphLibrary().get(FederatedStoreTest.ACC_ID_1));
    }

    @Test
    public void shouldGetAllElementsWhileHasConflictingSchemasDueToDiffVertexSerialiser() throws OperationException {
        // given
        final Entity A = getEntityA();
        final Entity B = getEntityB();
        final ArrayList<Entity> expectedAB = Lists.newArrayList(A, B);
        addElementsToNewGraph(A, "graphA", FederatedStoreTest.PATH_ENTITY_A_SCHEMA_JSON);
        addElementsToNewGraph(B, "graphB", FederatedStoreTest.PATH_ENTITY_B_SCHEMA_JSON);
        try {
            // when
            store.execute(new GetSchema.Builder().build(), userContext);
            Assert.fail("exception expected");
        } catch (final SchemaException e) {
            // then
            Assert.assertTrue(e.getMessage(), Pattern.compile("Unable to merge the schemas for all of your federated graphs: \\[graph., graph.\\]\\. You can limit which graphs to query for using the operation option: gaffer\\.federatedstore\\.operation\\.graphIds").matcher(e.getMessage()).matches());
        }
        // when
        final CloseableIterable<? extends Element> responseGraphsWithNoView = store.execute(new GetAllElements.Builder().build(), userContext);
        // then
        ElementUtil.assertElementEquals(expectedAB, responseGraphsWithNoView);
    }

    @Test
    public void shouldGetAllElementsFromSelectedRemoteGraphWhileHasConflictingSchemasDueToDiffVertexSerialiser() throws OperationException {
        // given
        final Entity A = getEntityA();
        final Entity B = getEntityB();
        final ArrayList<Entity> expectedAB = Lists.newArrayList(A, B);
        final ArrayList<Entity> expectedA = Lists.newArrayList(A);
        final ArrayList<Entity> expectedB = Lists.newArrayList(B);
        addElementsToNewGraph(A, "graphA", FederatedStoreTest.PATH_ENTITY_A_SCHEMA_JSON);
        addElementsToNewGraph(B, "graphB", FederatedStoreTest.PATH_ENTITY_B_SCHEMA_JSON);
        try {
            // when
            store.execute(new GetSchema.Builder().build(), userContext);
            Assert.fail("exception expected");
        } catch (final SchemaException e) {
            // then
            Assert.assertTrue(e.getMessage(), Pattern.compile("Unable to merge the schemas for all of your federated graphs: \\[graph., graph.\\]\\. You can limit which graphs to query for using the operation option: gaffer\\.federatedstore\\.operation\\.graphIds").matcher(e.getMessage()).matches());
        }
        // when
        final CloseableIterable<? extends Element> responseGraphA = store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, "graphA").build(), userContext);
        final CloseableIterable<? extends Element> responseGraphB = store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, "graphB").build(), userContext);
        // then
        ElementUtil.assertElementEquals(expectedA, responseGraphA);
        ElementUtil.assertElementEquals(expectedB, responseGraphB);
    }

    @Test
    public void shouldGetAllElementsFromSelectedGraphsWithViewOfExistingEntityGroupWhileHasConflictingSchemasDueToDiffVertexSerialiser() throws OperationException {
        // given
        final Entity A = getEntityA();
        final Entity B = getEntityB();
        final ArrayList<Entity> expectedA = Lists.newArrayList(A);
        final ArrayList<Entity> expectedB = Lists.newArrayList(B);
        addElementsToNewGraph(A, "graphA", FederatedStoreTest.PATH_ENTITY_A_SCHEMA_JSON);
        addElementsToNewGraph(B, "graphB", FederatedStoreTest.PATH_ENTITY_B_SCHEMA_JSON);
        try {
            // when
            store.execute(new GetSchema.Builder().build(), userContext);
            Assert.fail("exception expected");
        } catch (final SchemaException e) {
            // then
            Assert.assertTrue(e.getMessage(), Pattern.compile("Unable to merge the schemas for all of your federated graphs: \\[graph., graph.\\]\\. You can limit which graphs to query for using the operation option: gaffer\\.federatedstore\\.operation\\.graphIds").matcher(e.getMessage()).matches());
        }
        // when
        final CloseableIterable<? extends Element> responseGraphAWithAView = store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, "graphA").view(new View.Builder().entity("entityA").build()).build(), userContext);
        final CloseableIterable<? extends Element> responseGraphBWithBView = store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, "graphB").view(new View.Builder().entity("entityB").build()).build(), userContext);
        final CloseableIterable<? extends Element> responseAllGraphsWithAView = store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, "graphA,graphB").view(new View.Builder().entity("entityA").build()).build(), userContext);
        final CloseableIterable<? extends Element> responseAllGraphsWithBView = store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, "graphA,graphB").view(new View.Builder().entity("entityB").build()).build(), userContext);
        // then
        ElementUtil.assertElementEquals(expectedA, responseGraphAWithAView);
        ElementUtil.assertElementEquals(expectedB, responseGraphBWithBView);
        ElementUtil.assertElementEquals(expectedA, responseAllGraphsWithAView);
        ElementUtil.assertElementEquals(expectedB, responseAllGraphsWithBView);
    }

    @Test
    public void shouldFailGetAllElementsFromSelectedGraphsWithViewOfMissingEntityGroupWhileHasConflictingSchemasDueToDiffVertexSerialiser() throws OperationException {
        // given
        final Entity A = getEntityA();
        final Entity B = getEntityB();
        addElementsToNewGraph(A, "graphA", FederatedStoreTest.PATH_ENTITY_A_SCHEMA_JSON);
        addElementsToNewGraph(B, "graphB", FederatedStoreTest.PATH_ENTITY_B_SCHEMA_JSON);
        try {
            // when
            store.execute(new GetSchema.Builder().build(), userContext);
            Assert.fail("exception expected");
        } catch (final SchemaException e) {
            // then
            Assert.assertTrue(e.getMessage(), Pattern.compile("Unable to merge the schemas for all of your federated graphs: \\[graph., graph.\\]\\. You can limit which graphs to query for using the operation option: gaffer\\.federatedstore\\.operation\\.graphIds").matcher(e.getMessage()).matches());
        }
        try {
            // when
            CloseableIterable<? extends Element> responseGraphAWithBView = store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, "graphA").view(new View.Builder().entity("entityB").build()).build(), userContext);
            Assert.fail("exception expected");
        } catch (Exception e) {
            // then
            Assert.assertEquals(("Operation chain is invalid. Validation errors: \n" + (("View is not valid for graphIds:[graphA]\n" + "View for operation uk.gov.gchq.gaffer.operation.impl.get.GetAllElements is not valid. \n") + "Entity group entityB does not exist in the schema")), e.getMessage());
        }
        try {
            // when
            final CloseableIterable<? extends Element> responseGraphBWithAView = store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, "graphB").view(new View.Builder().entity("entityA").build()).build(), userContext);
            Assert.fail("exception expected");
        } catch (Exception e) {
            // then
            Assert.assertEquals(("Operation chain is invalid. Validation errors: \n" + (("View is not valid for graphIds:[graphB]\n" + "View for operation uk.gov.gchq.gaffer.operation.impl.get.GetAllElements is not valid. \n") + "Entity group entityA does not exist in the schema")), e.getMessage());
        }
        addGraphWithPaths("graphC", FederatedStoreTest.PATH_ACC_STORE_PROPERTIES_1, FederatedStoreTest.PATH_ENTITY_B_SCHEMA_JSON);
        try {
            // when
            final CloseableIterable<? extends Element> responseGraphBWithAView = store.execute(new GetAllElements.Builder().option(KEY_OPERATION_OPTIONS_GRAPH_IDS, "graphB,graphC").view(new View.Builder().entity("entityA").build()).build(), userContext);
            Assert.fail("exception expected");
        } catch (Exception e) {
            // then
            Assert.assertEquals(("Operation chain is invalid. Validation errors: \n" + (("View is not valid for graphIds:[graphB,graphC]\n" + "View for operation uk.gov.gchq.gaffer.operation.impl.get.GetAllElements is not valid. \n") + "Entity group entityA does not exist in the schema")), e.getMessage());
        }
    }
}

