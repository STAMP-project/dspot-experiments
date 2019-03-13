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
package uk.gov.gchq.gaffer.operation.export.graph.handler;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.integration.store.TestStore;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.export.graph.ExportToOtherGraph;
import uk.gov.gchq.gaffer.operation.export.graph.OtherGraphExporter;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.library.GraphLibrary;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.user.User;


public class ExportToOtherGraphHandlerTest {
    private static final String GRAPH_ID = "graphId";

    private static final String STORE_PROPS_ID = "storePropsId";

    public static final String STORE_PROPS_ID_1 = (ExportToOtherGraphHandlerTest.STORE_PROPS_ID) + 1;

    private static final String SCHEMA_ID = "schemaId";

    private static final String EXCEPTION_EXPECTED = "Exception expected";

    public static final String SCHEMA_ID_2 = (ExportToOtherGraphHandlerTest.SCHEMA_ID) + 2;

    public static final String SCHEMA_ID_1 = (ExportToOtherGraphHandlerTest.SCHEMA_ID) + 1;

    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    private final Store store = Mockito.mock(Store.class);

    private final Schema schema = new Schema.Builder().build();

    private GraphLibrary graphLibrary;

    private StoreProperties storeProperties;

    @Test
    public void shouldGetExporterClass() {
        final ExportToOtherGraphHandler handler = new ExportToOtherGraphHandler();
        Assert.assertEquals(OtherGraphExporter.class, handler.getExporterClass());
    }

    @Test
    public void shouldThrowExceptionWhenExportingToSameGraph() {
        // Given
        graphLibrary.add(ExportToOtherGraphHandlerTest.GRAPH_ID, ExportToOtherGraphHandlerTest.SCHEMA_ID, schema, ExportToOtherGraphHandlerTest.STORE_PROPS_ID, storeProperties);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(ExportToOtherGraphHandlerTest.GRAPH_ID).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(getErrorMessage(GraphDelegate.CANNOT_EXPORT_TO_THE_SAME_GRAPH_S, ExportToOtherGraphHandlerTest.GRAPH_ID), e.getMessage());
        }
    }

    @Test
    public void shouldCreateExporter() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, OperationException {
        // Given
        graphLibrary.add(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID, schema, ExportToOtherGraphHandlerTest.STORE_PROPS_ID, storeProperties);
        final Context context = Mockito.mock(Context.class);
        final User user = new User();
        BDDMockito.given(context.getUser()).willReturn(user);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).build();
        final ExportToOtherGraphHandler handler = new ExportToOtherGraphHandler();
        // When
        OtherGraphExporter exporter = handler.createExporter(export, context, store);
        // Then
        Assert.assertNotNull(exporter);
        TestStore.mockStore = Mockito.mock(Store.class);
        final Iterable elements = Mockito.mock(Iterable.class);
        exporter.add("key", elements);
        final ArgumentCaptor<OperationChain> opChainCaptor = ArgumentCaptor.forClass(OperationChain.class);
        Mockito.verify(TestStore.mockStore).execute(opChainCaptor.capture(), Mockito.any(Context.class));
        final List<Operation> ops = opChainCaptor.getValue().getOperations();
        Assert.assertEquals(1, ops.size());
        Assert.assertSame(elements, getInput());
        try {
            exporter.get("key");
            Assert.fail("exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldCreateNewGraphWithStoreGraphLibrary() {
        // Given
        graphLibrary.add(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), schema, storeProperties);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).build();
        // When
        Graph graph = createGraph(export);
        // Then
        Assert.assertEquals(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), graph.getGraphId());
        Assert.assertEquals(schema, graph.getSchema());
        Assert.assertEquals(storeProperties, graph.getStoreProperties());
    }

    @Test
    public void shouldCreateNewGraphWithStoresStoreProperties() {
        // Given
        BDDMockito.given(store.getProperties()).willReturn(storeProperties);
        BDDMockito.given(store.getGraphLibrary()).willReturn(null);
        Schema schema1 = new Schema.Builder().build();
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).schema(schema1).build();
        // When
        Graph graph = createGraph(export);
        // Then
        Assert.assertEquals(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), graph.getGraphId());
        Assert.assertEquals(schema1, graph.getSchema());
        Assert.assertEquals(store.getProperties(), graph.getStoreProperties());
    }

    @Test
    public void shouldCreateNewGraphWithStoresSchema() {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getGraphLibrary()).willReturn(null);
        final StoreProperties storeProperties1 = StoreProperties.loadStoreProperties(StreamUtil.storeProps(getClass()));
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).storeProperties(storeProperties1).build();
        // When
        Graph graph = createGraph(export);
        // Then
        Assert.assertEquals(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), graph.getGraphId());
        Assert.assertEquals(schema, graph.getSchema());
        Assert.assertEquals(storeProperties1, graph.getStoreProperties());
    }

    @Test
    public void shouldCreateNewGraphWithParentSchemaId() {
        // Given
        Schema schema1 = new Schema.Builder().build();
        graphLibrary.addOrUpdate(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID, schema, ExportToOtherGraphHandlerTest.STORE_PROPS_ID, storeProperties);
        graphLibrary.addSchema(ExportToOtherGraphHandlerTest.SCHEMA_ID_1, schema1);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 2)).parentSchemaIds(ExportToOtherGraphHandlerTest.SCHEMA_ID_1).storeProperties(storeProperties).build();
        // When
        Graph graph = createGraph(export);
        // Then
        Assert.assertEquals(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 2), graph.getGraphId());
        Assert.assertEquals(schema1, graph.getSchema());
        Assert.assertEquals(storeProperties, graph.getStoreProperties());
    }

    @Test
    public void shouldCreateNewGraphWithMergedParentSchemaIdAndProvidedSchema() {
        // Given
        Schema schema1 = new Schema.Builder().entity("entity", new SchemaEntityDefinition.Builder().vertex("vertex").build()).type("vertex", String.class).build();
        Schema schema2 = new Schema.Builder().edge("edge", new SchemaEdgeDefinition.Builder().source("vertex").destination("vertex").directed(DIRECTED_EITHER).build()).type("vertex", String.class).type(DIRECTED_EITHER, Boolean.class).build();
        graphLibrary.addOrUpdate(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), schema, storeProperties);
        graphLibrary.addSchema(ExportToOtherGraphHandlerTest.SCHEMA_ID_1, schema1);
        graphLibrary.addSchema(ExportToOtherGraphHandlerTest.SCHEMA_ID_2, schema2);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 2)).parentSchemaIds(ExportToOtherGraphHandlerTest.SCHEMA_ID_1, ExportToOtherGraphHandlerTest.SCHEMA_ID_2).schema(schema).storeProperties(storeProperties).build();
        // When
        Graph graph = createGraph(export);
        // Then
        Assert.assertEquals(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 2), graph.getGraphId());
        JsonAssert.assertEquals(new Schema.Builder().entity("entity", new SchemaEntityDefinition.Builder().vertex("vertex").build()).edge("edge", new SchemaEdgeDefinition.Builder().source("vertex").destination("vertex").directed(DIRECTED_EITHER).build()).type("vertex", String.class).type(DIRECTED_EITHER, Boolean.class).build().toJson(false), graph.getSchema().toJson(false));
        Assert.assertEquals(storeProperties, graph.getStoreProperties());
    }

    @Test
    public void shouldCreateNewGraphWithParentStorePropertiesId() {
        // Given
        StoreProperties storeProperties1 = StoreProperties.loadStoreProperties(StreamUtil.storeProps(getClass()));
        graphLibrary.addOrUpdate(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID, schema, ExportToOtherGraphHandlerTest.STORE_PROPS_ID, storeProperties);
        graphLibrary.addProperties(ExportToOtherGraphHandlerTest.STORE_PROPS_ID_1, storeProperties1);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 2)).schema(schema).parentStorePropertiesId(ExportToOtherGraphHandlerTest.STORE_PROPS_ID_1).build();
        // When
        Graph graph = createGraph(export);
        // Then
        Assert.assertEquals(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 2), graph.getGraphId());
        Assert.assertEquals(schema, graph.getSchema());
        Assert.assertEquals(storeProperties1, graph.getStoreProperties());
    }

    @Test
    public void shouldCreateNewGraphWithMergedParentStorePropertiesIdAndProvidedStoreProperties() {
        // Given
        StoreProperties storeProperties1 = StoreProperties.loadStoreProperties(StreamUtil.storeProps(getClass()));
        graphLibrary.addOrUpdate(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID, schema, ExportToOtherGraphHandlerTest.STORE_PROPS_ID, storeProperties);
        graphLibrary.addProperties(ExportToOtherGraphHandlerTest.STORE_PROPS_ID_1, storeProperties1);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 2)).schema(schema).parentStorePropertiesId(ExportToOtherGraphHandlerTest.STORE_PROPS_ID_1).storeProperties(storeProperties).build();
        // When
        Graph graph = createGraph(export);
        // Then
        Assert.assertEquals(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 2), graph.getGraphId());
        Assert.assertEquals(schema, graph.getSchema());
        storeProperties1.merge(storeProperties);
        Assert.assertEquals(storeProperties1, graph.getStoreProperties());
    }

    @Test
    public void shouldValidateGraphIdMustBeDifferent() {
        // Given
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(ExportToOtherGraphHandlerTest.GRAPH_ID).schema(schema).storeProperties(storeProperties).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(getErrorMessage(GraphDelegate.CANNOT_EXPORT_TO_THE_SAME_GRAPH_S, "graphId"), e.getMessage());
        }
    }

    @Test
    public void shouldValidateParentPropsIdCannotBeUsedWithoutGraphLibrary() {
        // Given
        BDDMockito.given(store.getGraphLibrary()).willReturn(null);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).parentStorePropertiesId(ExportToOtherGraphHandlerTest.STORE_PROPS_ID_1).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(getErrorMessage(GraphDelegate.S_CANNOT_BE_USED_WITHOUT_A_GRAPH_LIBRARY, "parentStorePropertiesId"), e.getMessage());
        }
    }

    @Test
    public void shouldValidateParentSchemaIdCannotBeUsedWithoutGraphLibrary() {
        // Given
        BDDMockito.given(store.getGraphLibrary()).willReturn(null);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).parentSchemaIds(ExportToOtherGraphHandlerTest.SCHEMA_ID).parentStorePropertiesId(ExportToOtherGraphHandlerTest.SCHEMA_ID).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(((("Validation errors: \n" + (String.format(GraphDelegate.S_CANNOT_BE_USED_WITHOUT_A_GRAPH_LIBRARY, "parentSchemaIds"))) + '\n') + (String.format(GraphDelegate.S_CANNOT_BE_USED_WITHOUT_A_GRAPH_LIBRARY, GraphDelegate.PARENT_STORE_PROPERTIES_ID))), e.getMessage());
        }
    }

    @Test
    public void shouldValidateParentSchemaIdCannotBeUsedWhenGraphIdAlreadyExists() {
        // Given
        graphLibrary.add(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID_1, new Schema.Builder().edge("edge", new SchemaEdgeDefinition()).build(), ExportToOtherGraphHandlerTest.STORE_PROPS_ID, new StoreProperties());
        graphLibrary.addSchema(ExportToOtherGraphHandlerTest.SCHEMA_ID, new Schema.Builder().build());
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).parentSchemaIds(ExportToOtherGraphHandlerTest.SCHEMA_ID).build();
        // When / Then`
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(("Validation errors: \n" + (String.format(GraphDelegate.GRAPH_S_ALREADY_EXISTS_SO_YOU_CANNOT_USE_A_DIFFERENT_S_DO_NOT_SET_THE_S_FIELD, "graphId1", GraphDelegate.SCHEMA_STRING, "parentSchemaIds"))), e.getMessage());
        }
    }

    @Test
    public void shouldValidateParentSchemaIdCanBeUsedWhenGraphIdAlreadyExistsAndIsSame() {
        // Given
        graphLibrary.add(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID, new Schema.Builder().build(), ExportToOtherGraphHandlerTest.STORE_PROPS_ID, new StoreProperties());
        graphLibrary.addSchema(ExportToOtherGraphHandlerTest.SCHEMA_ID, new Schema.Builder().build());
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).parentSchemaIds(ExportToOtherGraphHandlerTest.SCHEMA_ID).build();
        // When / Then`
        validate(export);
    }

    @Test
    public void shouldValidateSchemaCannotBeUsedWhenGraphIdAlreadyExists() {
        // Given
        graphLibrary.add(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID, new Schema.Builder().edge("testEdge", new SchemaEdgeDefinition()).build(), ExportToOtherGraphHandlerTest.STORE_PROPS_ID, new StoreProperties());
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).schema(new Schema()).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(("Validation errors: \n" + (String.format(GraphDelegate.GRAPH_S_ALREADY_EXISTS_SO_YOU_CANNOT_USE_A_DIFFERENT_S_DO_NOT_SET_THE_S_FIELD, "graphId1", GraphDelegate.SCHEMA_STRING, GraphDelegate.SCHEMA_STRING))), e.getMessage());
        }
    }

    @Test
    public void shouldValidateSchemaUsedWhenGraphIdAlreadyExistsAndIsSame() {
        // Given
        Schema schema1 = new Schema.Builder().edge("testEdge", new SchemaEdgeDefinition()).build();
        graphLibrary.add(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ((ExportToOtherGraphHandlerTest.SCHEMA_ID) + 1), schema1, ExportToOtherGraphHandlerTest.STORE_PROPS_ID, new StoreProperties());
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).schema(schema1).build();
        // When / Then
        validate(export);
    }

    @Test
    public void shouldValidateParentPropsIdCannotBeUsedWhenGraphIdAlreadyExists() {
        // Given
        StoreProperties storeProperties1 = new StoreProperties();
        storeProperties1.set("testKey", "testValue");
        graphLibrary.add(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID, new Schema.Builder().build(), ExportToOtherGraphHandlerTest.STORE_PROPS_ID, storeProperties1);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).parentStorePropertiesId(((ExportToOtherGraphHandlerTest.STORE_PROPS_ID) + 1)).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(("Validation errors: \n" + (String.format(GraphDelegate.GRAPH_S_ALREADY_EXISTS_SO_YOU_CANNOT_USE_A_DIFFERENT_S_DO_NOT_SET_THE_S_FIELD, "graphId1", "StoreProperties", "parentStorePropertiesId"))), e.getMessage());
        }
    }

    @Test
    public void shouldValidateParentPropsIdCanBeUsedWhenGraphIdAlreadyExistsAndIsSame() {
        // Given
        graphLibrary.add(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID, new Schema(), ExportToOtherGraphHandlerTest.STORE_PROPS_ID_1, new StoreProperties());
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).parentStorePropertiesId(ExportToOtherGraphHandlerTest.STORE_PROPS_ID_1).build();
        // When / Then
        validate(export);
    }

    @Test
    public void shouldValidatePropsCannotBeUsedWhenGraphIdAlreadyExists() {
        // Given
        StoreProperties storeProperties1 = new StoreProperties();
        storeProperties1.set("testKey", "testValue");
        graphLibrary.add(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID, new Schema.Builder().build(), ExportToOtherGraphHandlerTest.STORE_PROPS_ID, storeProperties1);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).storeProperties(new StoreProperties()).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(("Validation errors: \n" + (String.format(GraphDelegate.GRAPH_S_ALREADY_EXISTS_SO_YOU_CANNOT_USE_A_DIFFERENT_S_DO_NOT_SET_THE_S_FIELD, "graphId1", GraphDelegate.STORE_PROPERTIES_STRING, GraphDelegate.STORE_PROPERTIES_STRING))), e.getMessage());
        }
    }

    @Test
    public void shouldValidatePropsCanBeUsedWhenGraphIdAlreadyExistsAndIsSame() {
        // Given
        StoreProperties storeProperties1 = new StoreProperties();
        storeProperties1.set("testKey", "testValue");
        graphLibrary.add(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherGraphHandlerTest.SCHEMA_ID, new Schema.Builder().build(), ExportToOtherGraphHandlerTest.STORE_PROPS_ID, storeProperties1);
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).storeProperties(storeProperties1).build();
        // When / Then
        validate(export);
    }

    @Test
    public void shouldThrowExceptionSchemaIdCannotBeFound() {
        // Given
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).parentSchemaIds(ExportToOtherGraphHandlerTest.SCHEMA_ID).storeProperties(new StoreProperties()).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(getErrorMessage(GraphDelegate.SCHEMA_COULD_NOT_BE_FOUND_IN_THE_GRAPH_LIBRARY_WITH_ID_S, "[schemaId]"), e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionPropsIdCannotBeFound() {
        // Given
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).schema(new Schema()).parentStorePropertiesId(ExportToOtherGraphHandlerTest.STORE_PROPS_ID).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(getErrorMessage(GraphDelegate.STORE_PROPERTIES_COULD_NOT_BE_FOUND_IN_THE_GRAPH_LIBRARY_WITH_ID_S, ExportToOtherGraphHandlerTest.STORE_PROPS_ID), e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionPropertiesCannotBeUsedIfNotDefinedOrFound() {
        // Given
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).schema(new Schema()).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(("Validation errors: \n" + (String.format(GraphDelegate.GRAPH_ID_S_CANNOT_BE_CREATED_WITHOUT_DEFINED_KNOWN_S, ((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), "StoreProperties"))), e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionSchemaCannotBeUsedIfNotDefinedOrFound() {
        // Given
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).storeProperties(new StoreProperties()).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail(ExportToOtherGraphHandlerTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(("Validation errors: \n" + (String.format(GraphDelegate.GRAPH_ID_S_CANNOT_BE_CREATED_WITHOUT_DEFINED_KNOWN_S, ((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1), GraphDelegate.SCHEMA_STRING))), e.getMessage());
        }
    }

    @Test
    public void shouldValidateWithSchemaAndStorePropertiesSpecified() {
        // Given
        final ExportToOtherGraph export = new ExportToOtherGraph.Builder().graphId(((ExportToOtherGraphHandlerTest.GRAPH_ID) + 1)).schema(new Schema()).storeProperties(new StoreProperties()).build();
        // When
        validate(export);
        // Then - no exceptions
    }
}

