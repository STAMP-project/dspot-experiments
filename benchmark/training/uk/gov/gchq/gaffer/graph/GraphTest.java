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
package uk.gov.gchq.gaffer.graph;


import StoreTrait.INGEST_AGGREGATION;
import StoreTrait.TRANSFORMATION;
import StreamUtil.ELEMENTS_SCHEMA;
import StreamUtil.STORE_PROPERTIES;
import StreamUtil.TYPES_SCHEMA;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.EDGE_4;
import TestGroups.EDGE_5;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import TestTypes.PROP_INTEGER;
import TestTypes.PROP_STRING;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.commonutil.pair.Pair;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.data.elementdefinition.exception.SchemaException;
import uk.gov.gchq.gaffer.data.elementdefinition.view.GlobalViewElementDefinition;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.graph.hook.AddOperationsToChain;
import uk.gov.gchq.gaffer.graph.hook.GraphHook;
import uk.gov.gchq.gaffer.graph.hook.Log4jLogger;
import uk.gov.gchq.gaffer.graph.hook.NamedOperationResolver;
import uk.gov.gchq.gaffer.graph.hook.NamedViewResolver;
import uk.gov.gchq.gaffer.graph.hook.OperationAuthoriser;
import uk.gov.gchq.gaffer.graph.hook.OperationChainLimiter;
import uk.gov.gchq.gaffer.graph.hook.UpdateViewHook;
import uk.gov.gchq.gaffer.integration.store.TestStore;
import uk.gov.gchq.gaffer.jobtracker.JobDetail;
import uk.gov.gchq.gaffer.named.operation.NamedOperation;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.graph.OperationView;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.io.Output;
import uk.gov.gchq.gaffer.serialisation.Serialiser;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.raw.RawDoubleSerialiser;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.store.library.GraphLibrary;
import uk.gov.gchq.gaffer.store.library.HashMapGraphLibrary;
import uk.gov.gchq.gaffer.store.operation.handler.OperationHandler;
import uk.gov.gchq.gaffer.store.operation.handler.OutputOperationHandler;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.impl.binaryoperator.StringConcat;
import uk.gov.gchq.koryphe.impl.binaryoperator.Sum;


public class GraphTest {
    private static final String GRAPH_ID = "graphId";

    public static final String SCHEMA_ID_1 = "schemaId1";

    public static final String STORE_PROPERTIES_ID_1 = "storePropertiesId1";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    private User user;

    private Context context;

    private Context clonedContext;

    private OperationChain opChain;

    private OperationChain clonedOpChain;

    private GetElements operation;

    @Test
    public void shouldConstructGraphFromSchemaModules() {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final Schema schemaModule1 = new Schema.Builder().type(PROP_STRING, new TypeDefinition.Builder().clazz(String.class).build()).type("vertex", new TypeDefinition.Builder().clazz(String.class).build()).edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, PROP_STRING).aggregate(false).source("vertex").destination("vertex").directed(DIRECTED_EITHER).build()).build();
        final Schema schemaModule2 = new Schema.Builder().type(PROP_INTEGER, new TypeDefinition.Builder().clazz(Integer.class).build()).type("vertex2", new TypeDefinition.Builder().clazz(String.class).build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().property(PROP_2, PROP_INTEGER).aggregate(false).source("vertex2").destination("vertex2").directed(DIRECTED_EITHER).build()).build();
        final Schema schemaModule3 = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, PROP_STRING).aggregate(false).vertex("vertex3").build()).type("vertex3", new TypeDefinition.Builder().clazz(String.class).build()).build();
        final Schema schemaModule4 = new Schema.Builder().entity(ENTITY_2, new SchemaEntityDefinition.Builder().property(PROP_2, PROP_INTEGER).aggregate(false).vertex("vertex4").build()).type("vertex4", new TypeDefinition.Builder().clazz(String.class).build()).type(DIRECTED_EITHER, Boolean.class).build();
        // When
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().description("testDescription").graphId(GraphTest.GRAPH_ID).build()).storeProperties(storeProperties).addSchema(schemaModule1).addSchema(schemaModule2).addSchema(schemaModule3).addSchema(schemaModule4).build();
        // Then
        final Schema schema = graph.getSchema();
        schema.getEntity(ENTITY);
        Assert.assertEquals("testDescription", graph.getDescription());
    }

    @Test
    public void shouldConstructGraphFromSchemaFolderPath() throws IOException {
        // Given
        final Schema expectedSchema = new Schema.Builder().json(StreamUtil.elementsSchema(getClass()), StreamUtil.typesSchema(getClass())).build();
        Graph graph = null;
        File schemaDir = null;
        try {
            schemaDir = createSchemaDirectory();
            // When
            graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(StreamUtil.storeProps(getClass())).addSchema(Paths.get(schemaDir.getPath())).build();
        } finally {
            if (null != schemaDir) {
                FileUtils.deleteDirectory(schemaDir);
            }
        }
        // Then
        JsonAssert.assertEquals(expectedSchema.toJson(true), graph.getSchema().toJson(true));
    }

    @Test
    public void shouldConstructGraphFromSchemaURI() throws IOException, URISyntaxException {
        // Given
        final URI typeInputUri = getResourceUri(TYPES_SCHEMA);
        final URI schemaInputUri = getResourceUri(ELEMENTS_SCHEMA);
        final URI storeInputUri = getResourceUri(STORE_PROPERTIES);
        final Schema expectedSchema = new Schema.Builder().json(StreamUtil.elementsSchema(getClass()), StreamUtil.typesSchema(getClass())).build();
        Graph graph = null;
        File schemaDir = null;
        try {
            schemaDir = createSchemaDirectory();
            // When
            graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(storeInputUri).addSchemas(typeInputUri, schemaInputUri).build();
        } finally {
            if (schemaDir != null) {
                FileUtils.deleteDirectory(schemaDir);
            }
        }
        // Then
        JsonAssert.assertEquals(expectedSchema.toJson(true), graph.getSchema().toJson(true));
    }

    @Test
    public void shouldCreateNewContextInstanceWhenExecuteOperation() throws IOException, OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(new Schema.Builder().build()).build();
        // When
        graph.execute(operation, context);
        // Then
        Mockito.verify(store).execute(Mockito.any(Output.class), ArgumentMatchers.eq(clonedContext));
    }

    @Test
    public void shouldCreateNewContextInstanceWhenExecuteOutputOperation() throws IOException, OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(new Schema.Builder().build()).build();
        // When
        graph.execute(opChain, context);
        // Then
        Mockito.verify(store).execute(clonedOpChain, clonedContext);
    }

    @Test
    public void shouldCreateNewContextInstanceWhenExecuteJob() throws IOException, OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(new Schema.Builder().build()).build();
        // When
        graph.executeJob(opChain, context);
        // Then
        Mockito.verify(store).executeJob(clonedOpChain, clonedContext);
    }

    @Test
    public void shouldCloseAllOperationInputsWhenExceptionIsThrownWhenExecuted() throws IOException, OperationException {
        // Given
        final Exception exception = Mockito.mock(RuntimeException.class);
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.execute(clonedOpChain, clonedContext)).willThrow(exception);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(new Schema.Builder().build()).build();
        // When / Then
        try {
            graph.execute(opChain, context);
            Assert.fail("Exception expected");
        } catch (final Exception e) {
            Assert.assertSame(exception, e);
            Mockito.verify(clonedOpChain).close();
        }
    }

    @Test
    public void shouldCloseAllOperationInputsWhenExceptionIsThrownWhenJobExecuted() throws IOException, OperationException {
        // Given
        final Exception exception = Mockito.mock(RuntimeException.class);
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.executeJob(clonedOpChain, clonedContext)).willThrow(exception);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(new Schema.Builder().build()).build();
        // When / Then
        try {
            graph.executeJob(opChain, context);
            Assert.fail("Exception expected");
        } catch (final Exception e) {
            Assert.assertSame(exception, e);
            Mockito.verify(clonedOpChain).close();
        }
    }

    @Test
    public void shouldCallAllGraphHooksBeforeOperationChainExecuted() throws OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(new Schema.Builder().build()).build();
        // When
        graph.execute(opChain, context);
        // Then
        final InOrder inOrder = Mockito.inOrder(hook1, hook2, operation);
        inOrder.verify(hook1).preExecute(clonedOpChain, clonedContext);
        inOrder.verify(hook2).preExecute(clonedOpChain, clonedContext);
        inOrder.verify(operation).setView(Mockito.any(View.class));
        Mockito.verify(context).setOriginalOpChain(opChain);
    }

    @Test
    public void shouldCallAllGraphHooksBeforeJobExecuted() throws OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(new Schema.Builder().build()).build();
        // When
        graph.executeJob(opChain, context);
        // Then
        final InOrder inOrder = Mockito.inOrder(hook1, hook2, operation);
        inOrder.verify(hook1).preExecute(clonedOpChain, clonedContext);
        inOrder.verify(hook2).preExecute(clonedOpChain, clonedContext);
        inOrder.verify(operation).setView(Mockito.any(View.class));
        Mockito.verify(context).setOriginalOpChain(opChain);
    }

    @Test
    public void shouldCallAllGraphHooksAfterOperationExecuted() throws OperationException {
        // Given
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Store store = Mockito.mock(Store.class);
        final Object result1 = Mockito.mock(Object.class);
        final Object result2 = Mockito.mock(Object.class);
        final Object result3 = Mockito.mock(Object.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        BDDMockito.given(hook1.postExecute(result1, clonedOpChain, clonedContext)).willReturn(result2);
        BDDMockito.given(hook2.postExecute(result2, clonedOpChain, clonedContext)).willReturn(result3);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(schema).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        final ArgumentCaptor<Context> contextCaptor1 = ArgumentCaptor.forClass(Context.class);
        BDDMockito.given(store.execute(captor.capture(), contextCaptor1.capture())).willReturn(result1);
        // When
        final Object actualResult = graph.execute(opChain, context);
        // Then
        final InOrder inOrder = Mockito.inOrder(hook1, hook2);
        inOrder.verify(hook1).postExecute(result1, captor.getValue(), clonedContext);
        inOrder.verify(hook2).postExecute(result2, captor.getValue(), clonedContext);
        final List<Operation> ops = captor.getValue().getOperations();
        Assert.assertEquals(1, ops.size());
        Assert.assertSame(operation, ops.get(0));
        Assert.assertSame(actualResult, result3);
        Mockito.verify(context).setOriginalOpChain(opChain);
    }

    @Test
    public void shouldCallAllGraphHooksAfterOperationChainExecuted() throws OperationException {
        // Given
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        final Object result1 = Mockito.mock(Object.class);
        final Object result2 = Mockito.mock(Object.class);
        final Object result3 = Mockito.mock(Object.class);
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        BDDMockito.given(hook1.postExecute(result1, clonedOpChain, clonedContext)).willReturn(result2);
        BDDMockito.given(hook2.postExecute(result2, clonedOpChain, clonedContext)).willReturn(result3);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(schema).build();
        BDDMockito.given(store.execute(clonedOpChain, clonedContext)).willReturn(result1);
        // When
        final Object actualResult = graph.execute(opChain, context);
        // Then
        final InOrder inOrder = Mockito.inOrder(hook1, hook2);
        inOrder.verify(hook1).postExecute(result1, clonedOpChain, clonedContext);
        inOrder.verify(hook2).postExecute(result2, clonedOpChain, clonedContext);
        Assert.assertSame(actualResult, result3);
        Mockito.verify(context).setOriginalOpChain(opChain);
    }

    @Test
    public void shouldCallAllGraphHooksAfterJobExecuted() throws OperationException {
        // Given
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        final JobDetail result1 = Mockito.mock(JobDetail.class);
        final JobDetail result2 = Mockito.mock(JobDetail.class);
        final JobDetail result3 = Mockito.mock(JobDetail.class);
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        BDDMockito.given(hook1.postExecute(result1, clonedOpChain, clonedContext)).willReturn(result2);
        BDDMockito.given(hook2.postExecute(result2, clonedOpChain, clonedContext)).willReturn(result3);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(schema).build();
        BDDMockito.given(store.executeJob(clonedOpChain, clonedContext)).willReturn(result1);
        // When
        final JobDetail actualResult = graph.executeJob(opChain, context);
        // Then
        final InOrder inOrder = Mockito.inOrder(hook1, hook2);
        inOrder.verify(hook1).postExecute(result1, clonedOpChain, clonedContext);
        inOrder.verify(hook2).postExecute(result2, clonedOpChain, clonedContext);
        Assert.assertSame(actualResult, result3);
        Mockito.verify(context).setOriginalOpChain(opChain);
    }

    @Test
    public void shouldCallAllGraphHooksOnGraphHookPreExecuteFailure() throws OperationException {
        // Given
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final RuntimeException e = new RuntimeException("Hook2 failed in postExecute");
        Mockito.doThrow(e).when(hook1).preExecute(clonedOpChain, clonedContext);
        BDDMockito.given(hook1.onFailure(null, clonedOpChain, clonedContext, e)).willThrow(new RuntimeException("Hook1 failed in onFailure"));
        BDDMockito.given(hook2.onFailure(null, clonedOpChain, clonedContext, e)).willReturn(null);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(schema).build();
        // When / Then
        try {
            graph.execute(opChain, context);
            Assert.fail("Exception expected");
        } catch (final RuntimeException runtimeE) {
            final InOrder inOrder = Mockito.inOrder(context, hook1, hook2);
            inOrder.verify(context).setOriginalOpChain(opChain);
            inOrder.verify(hook2, Mockito.never()).preExecute(ArgumentMatchers.any(), ArgumentMatchers.any());
            inOrder.verify(hook1, Mockito.never()).postExecute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            inOrder.verify(hook2, Mockito.never()).postExecute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            inOrder.verify(hook1).onFailure(ArgumentMatchers.eq(null), ArgumentMatchers.any(), ArgumentMatchers.eq(clonedContext), ArgumentMatchers.eq(e));
            inOrder.verify(hook2).onFailure(ArgumentMatchers.eq(null), ArgumentMatchers.any(), ArgumentMatchers.eq(clonedContext), ArgumentMatchers.eq(e));
        }
    }

    @Test
    public void shouldCallAllGraphHooksOnGraphHookPostExecuteFailure() throws OperationException {
        // Given
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Store store = Mockito.mock(Store.class);
        final Object result1 = Mockito.mock(Object.class);
        final Object result2 = Mockito.mock(Object.class);
        final Object result3 = Mockito.mock(Object.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        BDDMockito.given(hook1.postExecute(result1, clonedOpChain, clonedContext)).willReturn(result2);
        final RuntimeException e = new RuntimeException("Hook2 failed in postExecute");
        BDDMockito.given(hook2.postExecute(result2, clonedOpChain, clonedContext)).willThrow(e);
        BDDMockito.given(hook1.onFailure(result2, clonedOpChain, clonedContext, e)).willThrow(new RuntimeException("Hook1 failed in onFailure"));
        BDDMockito.given(hook2.onFailure(result2, clonedOpChain, clonedContext, e)).willReturn(result3);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(schema).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        BDDMockito.given(store.execute(captor.capture(), ArgumentMatchers.eq(clonedContext))).willReturn(result1);
        // When / Then
        try {
            graph.execute(opChain, context);
            Assert.fail("Exception expected");
        } catch (final RuntimeException runtimeE) {
            final InOrder inOrder = Mockito.inOrder(context, hook1, hook2);
            inOrder.verify(context).setOriginalOpChain(opChain);
            inOrder.verify(hook1).postExecute(result1, captor.getValue(), clonedContext);
            inOrder.verify(hook2).postExecute(result2, captor.getValue(), clonedContext);
            inOrder.verify(hook1).onFailure(result2, captor.getValue(), clonedContext, e);
            inOrder.verify(hook2).onFailure(result2, captor.getValue(), clonedContext, e);
            final List<Operation> ops = captor.getValue().getOperations();
            Assert.assertEquals(1, ops.size());
            Assert.assertSame(operation, ops.get(0));
        }
    }

    @Test
    public void shouldCallAllGraphHooksOnExecuteFailure() throws OperationException {
        // Given
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final RuntimeException e = new RuntimeException("Store failed to execute operation chain");
        BDDMockito.given(hook1.onFailure(null, clonedOpChain, clonedContext, e)).willThrow(new RuntimeException("Hook1 failed in onFailure"));
        BDDMockito.given(hook2.onFailure(null, clonedOpChain, clonedContext, e)).willReturn(null);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(schema).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        BDDMockito.given(store.execute(captor.capture(), ArgumentMatchers.eq(clonedContext))).willThrow(e);
        // When / Then
        try {
            graph.execute(opChain, context);
            Assert.fail("Exception expected");
        } catch (final RuntimeException runtimeE) {
            final InOrder inOrder = Mockito.inOrder(context, clonedContext, hook1, hook2);
            inOrder.verify(context).setOriginalOpChain(opChain);
            inOrder.verify(hook1, Mockito.never()).postExecute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            inOrder.verify(hook2, Mockito.never()).postExecute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            inOrder.verify(hook1).onFailure(null, captor.getValue(), clonedContext, e);
            inOrder.verify(hook2).onFailure(null, captor.getValue(), clonedContext, e);
            final List<Operation> ops = captor.getValue().getOperations();
            Assert.assertEquals(1, ops.size());
            Assert.assertSame(operation, ops.get(0));
        }
    }

    @Test
    public void shouldCallAllGraphHooksOnGraphHookPreExecuteFailureWhenRunningJob() throws OperationException {
        // Given
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final RuntimeException e = new RuntimeException("Hook2 failed in postExecute");
        Mockito.doThrow(e).when(hook1).preExecute(clonedOpChain, clonedContext);
        BDDMockito.given(hook1.onFailure(null, clonedOpChain, clonedContext, e)).willThrow(new RuntimeException("Hook1 failed in onFailure"));
        BDDMockito.given(hook2.onFailure(null, clonedOpChain, clonedContext, e)).willReturn(null);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(schema).build();
        // When / Then
        try {
            graph.executeJob(opChain, context);
            Assert.fail("Exception expected");
        } catch (final RuntimeException runtimeE) {
            final InOrder inOrder = Mockito.inOrder(context, hook1, hook2);
            inOrder.verify(context).setOriginalOpChain(opChain);
            inOrder.verify(hook2, Mockito.never()).preExecute(ArgumentMatchers.any(), ArgumentMatchers.any());
            inOrder.verify(hook1, Mockito.never()).postExecute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            inOrder.verify(hook2, Mockito.never()).postExecute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            inOrder.verify(hook1).onFailure(ArgumentMatchers.eq(null), ArgumentMatchers.any(), ArgumentMatchers.eq(clonedContext), ArgumentMatchers.eq(e));
            inOrder.verify(hook2).onFailure(ArgumentMatchers.eq(null), ArgumentMatchers.any(), ArgumentMatchers.eq(clonedContext), ArgumentMatchers.eq(e));
        }
    }

    @Test
    public void shouldCallAllGraphHooksOnGraphHookPostExecuteFailureWhenRunningJob() throws OperationException {
        // Given
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Store store = Mockito.mock(Store.class);
        final JobDetail result1 = Mockito.mock(JobDetail.class);
        final JobDetail result2 = Mockito.mock(JobDetail.class);
        final JobDetail result3 = Mockito.mock(JobDetail.class);
        final Schema schema = new Schema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        BDDMockito.given(hook1.postExecute(result1, clonedOpChain, clonedContext)).willReturn(result2);
        final RuntimeException e = new RuntimeException("Hook2 failed in postExecute");
        BDDMockito.given(hook2.postExecute(result2, clonedOpChain, clonedContext)).willThrow(e);
        BDDMockito.given(hook1.onFailure(result2, clonedOpChain, clonedContext, e)).willThrow(new RuntimeException("Hook1 failed in onFailure"));
        BDDMockito.given(hook2.onFailure(result2, clonedOpChain, clonedContext, e)).willReturn(result3);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(schema).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        BDDMockito.given(store.executeJob(captor.capture(), ArgumentMatchers.eq(clonedContext))).willReturn(result1);
        // When / Then
        try {
            graph.executeJob(opChain, context);
            Assert.fail("Exception expected");
        } catch (final RuntimeException runtimeE) {
            final InOrder inOrder = Mockito.inOrder(context, hook1, hook2);
            inOrder.verify(context).setOriginalOpChain(opChain);
            inOrder.verify(hook1).postExecute(result1, captor.getValue(), clonedContext);
            inOrder.verify(hook2).postExecute(result2, captor.getValue(), clonedContext);
            inOrder.verify(hook1).onFailure(result2, captor.getValue(), clonedContext, e);
            inOrder.verify(hook2).onFailure(result2, captor.getValue(), clonedContext, e);
            final List<Operation> ops = captor.getValue().getOperations();
            Assert.assertEquals(1, ops.size());
            Assert.assertSame(operation, ops.get(0));
        }
    }

    @Test
    public void shouldCallAllGraphHooksOnExecuteFailureWhenRunningJob() throws OperationException {
        // Given
        final Operation operation = Mockito.mock(Operation.class);
        final OperationChain opChain = Mockito.mock(OperationChain.class);
        final OperationChain clonedOpChain = Mockito.mock(OperationChain.class);
        BDDMockito.given(opChain.shallowClone()).willReturn(clonedOpChain);
        BDDMockito.given(clonedOpChain.getOperations()).willReturn(Lists.newArrayList(operation));
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema();
        final RuntimeException e = new RuntimeException("Store failed to execute operation chain");
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        BDDMockito.given(hook1.onFailure(null, clonedOpChain, clonedContext, e)).willThrow(new RuntimeException("Hook1 failed in onFailure"));
        BDDMockito.given(hook2.onFailure(null, clonedOpChain, clonedContext, e)).willReturn(null);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(hook1).addHook(hook2).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).addSchema(schema).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        BDDMockito.given(store.executeJob(captor.capture(), ArgumentMatchers.eq(clonedContext))).willThrow(e);
        // When / Then
        try {
            graph.executeJob(opChain, context);
            Assert.fail("Exception expected");
        } catch (final RuntimeException runtimeE) {
            final InOrder inOrder = Mockito.inOrder(context, hook1, hook2);
            inOrder.verify(context).setOriginalOpChain(opChain);
            inOrder.verify(hook1, Mockito.never()).postExecute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            inOrder.verify(hook2, Mockito.never()).postExecute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            inOrder.verify(hook1).onFailure(null, captor.getValue(), clonedContext, e);
            inOrder.verify(hook2).onFailure(null, captor.getValue(), clonedContext, e);
            final List<Operation> ops = captor.getValue().getOperations();
            Assert.assertEquals(1, ops.size());
            Assert.assertSame(operation, ops.get(0));
        }
    }

    @Test
    public void shouldConstructGraphAndCreateViewWithGroups() {
        // Given
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getGraphId()).willReturn(GraphTest.GRAPH_ID);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        Map<String, SchemaEdgeDefinition> edges = new HashMap<>();
        edges.put("edge1", new SchemaEdgeDefinition());
        edges.put("edge2", new SchemaEdgeDefinition());
        edges.put("edge3", new SchemaEdgeDefinition());
        edges.put("edge4", new SchemaEdgeDefinition());
        Map<String, SchemaEntityDefinition> entities = new HashMap<>();
        entities.put("entity1", new SchemaEntityDefinition());
        entities.put("entity2", new SchemaEntityDefinition());
        entities.put("entity3", new SchemaEntityDefinition());
        entities.put("entity4", new SchemaEntityDefinition());
        Schema schema = new Schema.Builder().edges(edges).entities(entities).build();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        // When
        final View resultView = new Graph.Builder().store(store).build().getView();
        // Then
        Assert.assertNotSame(schema, resultView);
        Assert.assertArrayEquals(entities.keySet().toArray(), resultView.getEntityGroups().toArray());
        Assert.assertArrayEquals(edges.keySet().toArray(), resultView.getEdgeGroups().toArray());
        for (final ViewElementDefinition resultElementDef : resultView.getEntities().values()) {
            Assert.assertNotNull(resultElementDef);
            Assert.assertEquals(0, resultElementDef.getTransientProperties().size());
            Assert.assertNull(resultElementDef.getTransformer());
        }
        for (final ViewElementDefinition resultElementDef : resultView.getEdges().values()) {
            Assert.assertNotNull(resultElementDef);
            Assert.assertEquals(0, resultElementDef.getTransientProperties().size());
            Assert.assertNull(resultElementDef.getTransformer());
        }
    }

    @Test
    public void shouldExposeGetTraitsMethod() throws OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final View view = Mockito.mock(View.class);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).view(view).build()).store(store).build();
        // When
        final Set<StoreTrait> storeTraits = new HashSet(Arrays.asList(INGEST_AGGREGATION, TRANSFORMATION));
        BDDMockito.given(store.getTraits()).willReturn(storeTraits);
        final Collection<StoreTrait> returnedTraits = graph.getStoreTraits();
        // Then
        Assert.assertEquals(returnedTraits, storeTraits);
    }

    @Test
    public void shouldGetSchemaFromStoreIfSchemaIsEmpty() throws OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").build()).type("string", String.class).build();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getOriginalSchema()).willReturn(schema);
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final View view = Mockito.mock(View.class);
        new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).view(view).build()).addSchema(new Schema()).store(store).build();
        // When
        Mockito.verify(store).setOriginalSchema(schema);
    }

    @Test
    public void shouldSetGraphViewOnOperationAndDelegateDoOperationToStore() throws OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final View view = new View.Builder().entity(ENTITY).edge(EDGE).build();
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).view(view).build()).store(store).build();
        final Integer expectedResult = 5;
        final GetElements operation = new GetElements();
        final OperationChain<Integer> opChain = Mockito.mock(OperationChain.class);
        final OperationChain clonedOpChain = Mockito.mock(OperationChain.class);
        BDDMockito.given(opChain.shallowClone()).willReturn(clonedOpChain);
        BDDMockito.given(clonedOpChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(store.execute(clonedOpChain, clonedContext)).willReturn(expectedResult);
        // When
        Integer result = graph.execute(opChain, context);
        // Then
        Assert.assertEquals(expectedResult, result);
        Mockito.verify(store).execute(clonedOpChain, clonedContext);
        JsonAssert.assertEquals(view.toJson(false), operation.getView().toJson(false));
    }

    @Test
    public void shouldNotSetGraphViewOnOperationWhenOperationViewIsNotNull() throws OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final View opView = Mockito.mock(View.class);
        final View view = Mockito.mock(View.class);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).view(view).build()).store(store).build();
        final Integer expectedResult = 5;
        BDDMockito.given(operation.getView()).willReturn(opView);
        final OperationChain<Integer> opChain = Mockito.mock(OperationChain.class);
        final OperationChain<Integer> clonedOpChain = Mockito.mock(OperationChain.class);
        BDDMockito.given(opChain.shallowClone()).willReturn(clonedOpChain);
        BDDMockito.given(opChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(store.execute(clonedOpChain, clonedContext)).willReturn(expectedResult);
        // When
        Integer result = graph.execute(opChain, context);
        // Then
        Assert.assertEquals(expectedResult, result);
        Mockito.verify(store).execute(clonedOpChain, clonedContext);
        Mockito.verify(operation, Mockito.never()).setView(view);
    }

    @Test
    public void shouldNotSetGraphViewOnOperationWhenOperationIsNotAGet() throws OperationException {
        // Given
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final View view = Mockito.mock(View.class);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).view(view).build()).store(store).build();
        final int expectedResult = 5;
        final Operation operation = Mockito.mock(Operation.class);
        final OperationChain<Integer> opChain = Mockito.mock(OperationChain.class);
        final OperationChain<Integer> clonedOpChain = Mockito.mock(OperationChain.class);
        BDDMockito.given(opChain.shallowClone()).willReturn(clonedOpChain);
        BDDMockito.given(clonedOpChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(store.execute(clonedOpChain, clonedContext)).willReturn(expectedResult);
        // When
        int result = graph.execute(opChain, context);
        // Then
        Assert.assertEquals(expectedResult, result);
        Mockito.verify(store).execute(clonedOpChain, clonedContext);
    }

    @Test
    public void shouldThrowExceptionIfStoreClassPropertyIsNotSet() throws OperationException {
        try {
            new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).addSchema(new Schema()).storeProperties(new StoreProperties()).build();
            Assert.fail("exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(((("The Store class name was not found in the store properties for key: " + (StoreProperties.STORE_CLASS)) + ", GraphId: ") + (GraphTest.GRAPH_ID)), e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfSchemaIsInvalid() throws OperationException {
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        try {
            new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).addSchema(new Schema.Builder().type("int", // invalid serialiser
            new TypeDefinition.Builder().clazz(Integer.class).aggregateFunction(new Sum()).serialiser(new RawDoubleSerialiser()).build()).type("string", new TypeDefinition.Builder().clazz(String.class).aggregateFunction(new StringConcat()).build()).type("boolean", Boolean.class).edge("EDGE", new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("boolean").build()).entity("ENTITY", new SchemaEntityDefinition.Builder().vertex("string").property("p2", "int").build()).build()).storeProperties(storeProperties).build();
            Assert.fail("exception expected");
        } catch (final SchemaException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldDelegateGetNextOperationsToStore() {
        // Given
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).store(store).build();
        final Set<Class<? extends Operation>> expectedNextOperations = Mockito.mock(Set.class);
        BDDMockito.given(store.getNextOperations(GetElements.class)).willReturn(expectedNextOperations);
        // When
        final Set<Class<? extends Operation>> nextOperations = graph.getNextOperations(GetElements.class);
        // Then
        Assert.assertSame(expectedNextOperations, nextOperations);
    }

    @Test
    public void shouldDelegateIsSupportedToStore() {
        // Given
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).store(store).build();
        BDDMockito.given(store.isSupported(GetElements.class)).willReturn(true);
        BDDMockito.given(store.isSupported(GetAllElements.class)).willReturn(false);
        // When / Then
        Assert.assertTrue(graph.isSupported(GetElements.class));
        Assert.assertFalse(graph.isSupported(GetAllElements.class));
    }

    @Test
    public void shouldDelegateGetSupportedOperationsToStore() {
        // Given
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).store(store).build();
        final Set<Class<? extends Operation>> expectedSupportedOperations = Mockito.mock(Set.class);
        BDDMockito.given(store.getSupportedOperations()).willReturn(expectedSupportedOperations);
        // When
        final Set<Class<? extends Operation>> supportedOperations = graph.getSupportedOperations();
        // Then
        Assert.assertSame(expectedSupportedOperations, supportedOperations);
    }

    @Test
    public void shouldThrowExceptionWithInvalidSchema() {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        // When / Then
        try {
            new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).addSchema(new Schema.Builder().edge("group", new SchemaEdgeDefinition()).entity("group", new SchemaEntityDefinition()).build()).storeProperties(storeProperties).build();
        } catch (final SchemaException e) {
            Assert.assertTrue(e.getMessage().contains("Schema is not valid"));
        }
    }

    @Test
    public void shouldThrowExceptionWithNullSchema() {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        // When / Then
        try {
            new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(storeProperties).build();
        } catch (final SchemaException e) {
            Assert.assertTrue(e.getMessage().contains("Schema is missing"));
        }
    }

    @Test
    public void shouldThrowExceptionIfGraphIdIsInvalid() throws Exception {
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        try {
            new Graph.Builder().config(new GraphConfig.Builder().graphId("invalid-id").build()).build();
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldBuildGraphUsingGraphIdAndLookupSchema() throws Exception {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final Schema schemaModule1 = new Schema.Builder().type(PROP_STRING, new TypeDefinition.Builder().clazz(String.class).build()).type("vertex", new TypeDefinition.Builder().clazz(String.class).build()).edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, PROP_STRING).aggregate(false).source("vertex").destination("vertex").directed(DIRECTED_EITHER).build()).build();
        final Schema schemaModule2 = new Schema.Builder().type(PROP_INTEGER, new TypeDefinition.Builder().clazz(Integer.class).build()).type("vertex2", new TypeDefinition.Builder().clazz(String.class).build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().property(PROP_2, PROP_INTEGER).aggregate(false).source("vertex2").destination("vertex2").directed(DIRECTED_EITHER).build()).build();
        final Schema schemaModule3 = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, PROP_STRING).aggregate(false).vertex("vertex3").build()).type("vertex3", new TypeDefinition.Builder().clazz(String.class).build()).build();
        final Schema schemaModule4 = new Schema.Builder().entity(ENTITY_2, new SchemaEntityDefinition.Builder().property(PROP_2, PROP_INTEGER).aggregate(false).vertex("vertex4").build()).type("vertex4", new TypeDefinition.Builder().clazz(String.class).build()).type(DIRECTED_EITHER, Boolean.class).build();
        // When
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).library(new HashMapGraphLibrary()).build()).addSchema(schemaModule1).addSchema(schemaModule2).addSchema(schemaModule3).addSchema(schemaModule4).storeProperties(storeProperties).build();
        final Graph graph2 = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).library(new HashMapGraphLibrary()).build()).storeProperties(storeProperties).build();
        // Then
        JsonAssert.assertEquals(graph.getSchema().toJson(false), graph2.getSchema().toJson(false));
    }

    @Test
    public void shouldAddHooksVarArgsAndGetGraphHooks() throws Exception {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final GraphHook graphHook1 = Mockito.mock(GraphHook.class);
        final Log4jLogger graphHook2 = Mockito.mock(Log4jLogger.class);
        // When
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId("graphId").addHooks(graphHook1, graphHook2).build()).storeProperties(storeProperties).addSchemas(StreamUtil.schemas(getClass())).build();
        // Then
        Assert.assertEquals(Arrays.asList(NamedViewResolver.class, graphHook1.getClass(), graphHook2.getClass()), graph.getGraphHooks());
    }

    @Test
    public void shouldAddHookAndGetGraphHooks() throws Exception {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(TestStore.class.getName());
        TestStore.mockStore = Mockito.mock(Store.class);
        BDDMockito.given(TestStore.mockStore.isSupported(NamedOperation.class)).willReturn(true);
        final GraphHook graphHook1 = Mockito.mock(GraphHook.class);
        final NamedOperationResolver graphHook2 = new NamedOperationResolver();
        final Log4jLogger graphHook3 = Mockito.mock(Log4jLogger.class);
        // When
        final Graph graph = new Graph.Builder().graphId("graphId").storeProperties(storeProperties).addSchemas(StreamUtil.schemas(getClass())).addHook(graphHook1).addHook(graphHook2).addHook(graphHook3).build();
        // Then
        Assert.assertEquals(Arrays.asList(NamedViewResolver.class, graphHook1.getClass(), graphHook2.getClass(), graphHook3.getClass()), graph.getGraphHooks());
    }

    @Test
    public void shouldAddNamedViewResolverHookAfterNamedOperationResolver() throws Exception {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(TestStore.class.getName());
        TestStore.mockStore = Mockito.mock(Store.class);
        BDDMockito.given(TestStore.mockStore.isSupported(NamedOperation.class)).willReturn(true);
        final GraphHook graphHook1 = Mockito.mock(GraphHook.class);
        final Log4jLogger graphHook2 = Mockito.mock(Log4jLogger.class);
        // When
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId("graphId").addHook(graphHook1).addHook(graphHook2).build()).storeProperties(storeProperties).addSchemas(StreamUtil.schemas(getClass())).build();
        // Then
        Assert.assertEquals(Arrays.asList(NamedOperationResolver.class, NamedViewResolver.class, graphHook1.getClass(), graphHook2.getClass()), graph.getGraphHooks());
    }

    @Test
    public void shouldAddHooksFromPathAndGetGraphHooks() throws Exception {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final File graphHooks = tempFolder.newFile("graphHooks.json");
        FileUtils.writeLines(graphHooks, IOUtils.readLines(StreamUtil.openStream(getClass(), "graphHooks.json")));
        // When
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId("graphId").addHooks(Paths.get(graphHooks.getPath())).build()).storeProperties(storeProperties).addSchemas(StreamUtil.schemas(getClass())).build();
        // Then
        Assert.assertEquals(Arrays.asList(NamedViewResolver.class, OperationChainLimiter.class, AddOperationsToChain.class, OperationAuthoriser.class), graph.getGraphHooks());
    }

    @Test
    public void shouldAddHookFromPathAndGetGraphHooks() throws Exception {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final File graphHook1File = tempFolder.newFile("opChainLimiter.json");
        FileUtils.writeLines(graphHook1File, IOUtils.readLines(StreamUtil.openStream(getClass(), "opChainLimiter.json")));
        final File graphHook2File = tempFolder.newFile("opAuthoriser.json");
        FileUtils.writeLines(graphHook2File, IOUtils.readLines(StreamUtil.openStream(getClass(), "opAuthoriser.json")));
        // When
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId("graphId").addHook(Paths.get(graphHook1File.getPath())).addHook(Paths.get(graphHook2File.getPath())).build()).storeProperties(storeProperties).addSchemas(StreamUtil.schemas(getClass())).build();
        // Then
        Assert.assertEquals(Arrays.asList(NamedViewResolver.class, OperationChainLimiter.class, OperationAuthoriser.class), graph.getGraphHooks());
    }

    @Test
    public void shouldBuildGraphFromConfigFile() throws Exception {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        // When
        final Graph graph = new Graph.Builder().config(StreamUtil.graphConfig(getClass())).storeProperties(storeProperties).addSchemas(StreamUtil.schemas(getClass())).build();
        // Then
        Assert.assertEquals("graphId1", graph.getGraphId());
        Assert.assertEquals(new View.Builder().globalElements(new GlobalViewElementDefinition.Builder().groupBy().build()).build(), graph.getView());
        Assert.assertEquals(HashMapGraphLibrary.class, graph.getGraphLibrary().getClass());
        Assert.assertEquals(Arrays.asList(NamedViewResolver.class, OperationChainLimiter.class, AddOperationsToChain.class), graph.getGraphHooks());
    }

    @Test
    public void shouldBuildGraphFromConfigAndMergeConfigWithExistingConfig() throws Exception {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final String graphId1 = "graphId1";
        final String graphId2 = "graphId2";
        final GraphLibrary library1 = Mockito.mock(GraphLibrary.class);
        final GraphLibrary library2 = Mockito.mock(GraphLibrary.class);
        final View view1 = new View.Builder().entity(ENTITY).build();
        final View view2 = new View.Builder().edge(EDGE).build();
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final GraphHook hook3 = Mockito.mock(GraphHook.class);
        // When
        final GraphConfig config = new GraphConfig.Builder().graphId(graphId2).library(library2).addHook(hook2).view(view2).build();
        final Graph graph = new Graph.Builder().graphId(graphId1).library(library1).view(view1).storeProperties(storeProperties).addSchemas(StreamUtil.schemas(getClass())).addHook(hook1).config(config).addHook(hook3).build();
        // Then
        Assert.assertEquals(graphId2, graph.getGraphId());
        Assert.assertEquals(view2, graph.getView());
        Assert.assertEquals(library2, graph.getGraphLibrary());
        Assert.assertEquals(Arrays.asList(NamedViewResolver.class, hook1.getClass(), hook2.getClass(), hook3.getClass()), graph.getGraphHooks());
    }

    @Test
    public void shouldBuildGraphFromConfigAndOverrideFields() throws Exception {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final String graphId1 = "graphId1";
        final String graphId2 = "graphId2";
        final GraphLibrary library1 = Mockito.mock(GraphLibrary.class);
        final GraphLibrary library2 = Mockito.mock(GraphLibrary.class);
        final View view1 = new View.Builder().entity(ENTITY).build();
        final View view2 = new View.Builder().edge(EDGE).build();
        final GraphHook hook1 = Mockito.mock(GraphHook.class);
        final GraphHook hook2 = Mockito.mock(GraphHook.class);
        final GraphHook hook3 = Mockito.mock(GraphHook.class);
        // When
        final GraphConfig config = new GraphConfig.Builder().graphId(graphId2).library(library2).addHook(hook2).view(view2).build();
        final Graph graph = new Graph.Builder().config(config).graphId(graphId1).library(library1).view(view1).storeProperties(storeProperties).addSchemas(StreamUtil.schemas(getClass())).addHook(hook1).addHook(hook3).build();
        // Then
        Assert.assertEquals(graphId1, graph.getGraphId());
        Assert.assertEquals(view1, graph.getView());
        Assert.assertEquals(library1, graph.getGraphLibrary());
        Assert.assertEquals(Arrays.asList(NamedViewResolver.class, hook2.getClass(), hook1.getClass(), hook3.getClass()), graph.getGraphHooks());
    }

    @Test
    public void shouldReturnClonedViewFromConfig() throws Exception {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final String graphId = "graphId";
        final View view = new View.Builder().entity(ENTITY).build();
        // When
        final GraphConfig config = new GraphConfig.Builder().graphId(graphId).view(view).build();
        final Graph graph = new Graph.Builder().config(config).storeProperties(storeProperties).addSchemas(StreamUtil.schemas(getClass())).build();
        // Then
        Assert.assertEquals(graphId, graph.getGraphId());
        Assert.assertEquals(view, graph.getView());
        Assert.assertNotSame(view, graph.getView());
    }

    @Test
    public void shouldBuildGraphFromConfigAndSetIdsToGraphsWhenDifferent() {
        // Given
        final StoreProperties libraryStoreProperties = new StoreProperties();
        libraryStoreProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final StoreProperties graphStoreProperties = new StoreProperties();
        graphStoreProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final Schema librarySchema = new Schema.Builder().build();
        final Schema graphSchema = new Schema.Builder().build();
        final String graphId1 = "graphId1";
        final HashMapGraphLibrary library = new HashMapGraphLibrary();
        library.addSchema(GraphTest.SCHEMA_ID_1, librarySchema);
        library.addProperties(GraphTest.STORE_PROPERTIES_ID_1, libraryStoreProperties);
        // When
        final GraphConfig config = new GraphConfig.Builder().graphId(graphId1).library(library).build();
        final Graph graph1 = new Graph.Builder().config(config).addToLibrary(true).parentStorePropertiesId("storePropertiesId1").storeProperties(graphStoreProperties).addParentSchemaIds(GraphTest.SCHEMA_ID_1).addSchemas(graphSchema).build();
        // Then
        Assert.assertEquals(graphId1, graph1.getGraphId());
        JsonAssert.assertEquals(library.getSchema(GraphTest.SCHEMA_ID_1).toJson(false), librarySchema.toJson(false));
        final Pair<String, String> ids = library.getIds(graphId1);
        // Check that the schemaIds are different between the parent and supplied schema
        Assert.assertEquals(graphId1, ids.getFirst());
        // Check that the storePropsIds are different between the parent and supplied storeProps
        Assert.assertEquals(graphId1, ids.getSecond());
    }

    @Test
    public void shouldBuildGraphFromConfigAndSetIdsToGraphsWhenIdentical() {
        // Given
        final StoreProperties storeProperties = new StoreProperties();
        storeProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        String storePropertiesId1 = "storePropertiesId1";
        final Schema schema = new Schema.Builder().build();
        final String graphId1 = "graphId1";
        final HashMapGraphLibrary library = new HashMapGraphLibrary();
        library.addSchema(GraphTest.SCHEMA_ID_1, schema);
        library.addProperties(storePropertiesId1, storeProperties);
        // When
        final GraphConfig config = new GraphConfig.Builder().graphId(graphId1).library(library).build();
        final Graph graph1 = new Graph.Builder().config(config).addToLibrary(true).parentStorePropertiesId(storePropertiesId1).storeProperties(storeProperties).addParentSchemaIds(GraphTest.SCHEMA_ID_1).addSchemas(schema).build();
        // Then
        Assert.assertEquals(graphId1, graph1.getGraphId());
        JsonAssert.assertEquals(library.getSchema(GraphTest.SCHEMA_ID_1).toJson(false), schema.toJson(false));
        // Check that the schemaId = schemaId1 as both the parent and supplied schema have same id's
        Assert.assertTrue(library.getIds(graphId1).getFirst().equals(graphId1));
        // Check that the storePropsId = storePropertiesId1 as both parent and supplied storeProps have same id's
        Assert.assertTrue(library.getIds(graphId1).getSecond().equals(graphId1));
    }

    @Test
    public void shouldBuildGraphFromConfigAndSetIdsToGraphsWhenGraphSchemaAndStorePropertiesIdsAreNull() {
        // Given
        final StoreProperties libraryStoreProperties = new StoreProperties();
        libraryStoreProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final StoreProperties graphStoreProperties = new StoreProperties();
        graphStoreProperties.setStoreClass(GraphTest.TestStoreImpl.class.getName());
        final Schema librarySchema = new Schema.Builder().build();
        final Schema graphSchema = new Schema.Builder().build();
        final String graphId1 = "graphId1";
        final HashMapGraphLibrary library = new HashMapGraphLibrary();
        library.addSchema(GraphTest.SCHEMA_ID_1, librarySchema);
        library.addProperties(GraphTest.STORE_PROPERTIES_ID_1, libraryStoreProperties);
        // When
        final GraphConfig config = new GraphConfig.Builder().graphId(graphId1).library(library).build();
        final Graph graph1 = new Graph.Builder().config(config).addToLibrary(true).parentStorePropertiesId("storePropertiesId1").storeProperties(graphStoreProperties).addParentSchemaIds(GraphTest.SCHEMA_ID_1).addSchemas(graphSchema).build();
        // Then
        Assert.assertEquals(graphId1, graph1.getGraphId());
        JsonAssert.assertEquals(library.getSchema(GraphTest.SCHEMA_ID_1).toJson(false), librarySchema.toJson(false));
        // Check that the schemaId = schemaId1 as both the supplied schema id is null
        Assert.assertTrue(library.getIds(graphId1).getFirst().equals(graphId1));
        // Check that the storePropsId = storePropertiesId1 as the supplied storeProps id is null
        Assert.assertTrue(library.getIds(graphId1).getSecond().equals(graphId1));
    }

    @Test
    public void shouldCorrectlySetViewForNestedOperationChain() throws OperationException {
        // Given
        final Store store = new TestStore();
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(new StoreProperties()).addSchema(new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, PROP_INTEGER).aggregate(false).source("vertex2").destination("vertex2").directed(DIRECTED_EITHER).build()).type(PROP_INTEGER, new TypeDefinition.Builder().clazz(Integer.class).build()).type("vertex2", new TypeDefinition.Builder().clazz(String.class).build()).type(DIRECTED_EITHER, Boolean.class).build()).store(store).build();
        final User user = new User();
        final Context context = new Context(user);
        final OperationChain<Iterable<? extends Element>> nestedChain = new OperationChain(Arrays.asList(new GetAllElements(), new uk.gov.gchq.gaffer.operation.impl.Limit(3, true)));
        final OperationChain<Iterable<? extends Element>> outerChain = new OperationChain(nestedChain);
        graph.execute(outerChain, context);
        // Then
        Assert.assertNotNull(graph.getView());
    }

    @Test
    public void shouldThrowExceptionOnExecuteWithANullContext() throws OperationException {
        // Given
        final Context context = null;
        final OperationChain opChain = Mockito.mock(OperationChain.class);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(StreamUtil.storeProps(getClass())).addSchemas(StreamUtil.schemas(getClass())).build();
        // When / Then
        try {
            graph.execute(opChain, context);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("A context containing a user is required", e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionOnExecuteJobWithANullContext() throws OperationException {
        // Given
        final Context context = null;
        final OperationChain opChain = Mockito.mock(OperationChain.class);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(StreamUtil.storeProps(getClass())).addSchemas(StreamUtil.schemas(getClass())).build();
        // When / Then
        try {
            graph.executeJob(opChain, context);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("A context containing a user is required", e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionOnExecuteWithANullUser() throws OperationException {
        // Given
        final User user = null;
        final OperationChain opChain = Mockito.mock(OperationChain.class);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(StreamUtil.storeProps(getClass())).addSchemas(StreamUtil.schemas(getClass())).build();
        // When / Then
        try {
            graph.execute(opChain, user);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("A user is required", e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionOnExecuteJobWithANullUser() throws OperationException {
        // Given
        final User user = null;
        final OperationChain opChain = Mockito.mock(OperationChain.class);
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).build()).storeProperties(StreamUtil.storeProps(getClass())).addSchemas(StreamUtil.schemas(getClass())).build();
        // When / Then
        try {
            graph.executeJob(opChain, user);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("A user is required", e.getMessage());
        }
    }

    @Test
    public void shouldManipulateViewRemovingBlacklistedEdgeUsingUpdateViewHook() throws OperationException {
        // Given
        operation = new GetElements.Builder().view(new View.Builder().edge(EDGE_5).edge(EDGE).build()).build();
        final UpdateViewHook updateViewHook = new UpdateViewHook.Builder().blackListElementGroups(Collections.singleton(EDGE)).build();
        BDDMockito.given(opChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(opChain.shallowClone()).willReturn(clonedOpChain);
        BDDMockito.given(clonedOpChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(clonedOpChain.flatten()).willReturn(Arrays.asList(operation));
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(updateViewHook).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        final ArgumentCaptor<Context> contextCaptor1 = ArgumentCaptor.forClass(Context.class);
        BDDMockito.given(store.execute(captor.capture(), contextCaptor1.capture())).willReturn(new ArrayList());
        // When / Then
        graph.execute(opChain, user);
        final List<Operation> ops = captor.getValue().getOperations();
        JsonAssert.assertEquals(new View.Builder().edge(EDGE_5).build().toCompactJson(), getView().toCompactJson());
    }

    @Test
    public void shouldManipulateViewRemovingBlacklistedEdgeLeavingEmptyViewUsingUpdateViewHook() throws OperationException {
        // Given
        operation = new GetElements.Builder().view(new View.Builder().edge(EDGE).build()).build();
        final UpdateViewHook updateViewHook = new UpdateViewHook.Builder().blackListElementGroups(Collections.singleton(EDGE)).build();
        BDDMockito.given(opChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(opChain.shallowClone()).willReturn(clonedOpChain);
        BDDMockito.given(clonedOpChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(clonedOpChain.flatten()).willReturn(Arrays.asList(operation));
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(updateViewHook).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        final ArgumentCaptor<Context> contextCaptor1 = ArgumentCaptor.forClass(Context.class);
        BDDMockito.given(store.execute(captor.capture(), contextCaptor1.capture())).willReturn(new ArrayList());
        // When / Then
        graph.execute(opChain, user);
        final List<Operation> ops = captor.getValue().getOperations();
        JsonAssert.assertEquals(new View.Builder().build().toCompactJson(), getView().toCompactJson());
    }

    @Test
    public void shouldFillSchemaViewAndManipulateViewRemovingBlacklistedEdgeUsingUpdateViewHook() throws OperationException {
        // Given
        operation = new GetElements.Builder().build();
        final UpdateViewHook updateViewHook = new UpdateViewHook.Builder().blackListElementGroups(Collections.singleton(EDGE)).build();
        BDDMockito.given(opChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(opChain.shallowClone()).willReturn(clonedOpChain);
        BDDMockito.given(clonedOpChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(clonedOpChain.flatten()).willReturn(Arrays.asList(operation));
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition()).edge(EDGE_5, new SchemaEdgeDefinition()).build());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(updateViewHook).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        final ArgumentCaptor<Context> contextCaptor1 = ArgumentCaptor.forClass(Context.class);
        BDDMockito.given(store.execute(captor.capture(), contextCaptor1.capture())).willReturn(new ArrayList());
        // When / Then
        graph.execute(opChain, user);
        final List<Operation> ops = captor.getValue().getOperations();
        JsonAssert.assertEquals(new View.Builder().edge(EDGE_5).build().toCompactJson(), getView().toCompactJson());
    }

    @Test
    public void shouldFillSchemaViewAndManipulateViewRemovingBlacklistedEdgeLeavingEmptyViewUsingUpdateViewHook() throws OperationException {
        // Given
        operation = new GetElements.Builder().build();
        final UpdateViewHook updateViewHook = new UpdateViewHook.Builder().blackListElementGroups(Collections.singleton(EDGE)).build();
        BDDMockito.given(opChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(opChain.shallowClone()).willReturn(clonedOpChain);
        BDDMockito.given(clonedOpChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(clonedOpChain.flatten()).willReturn(Arrays.asList(operation));
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema.Builder().edge(EDGE_5, new SchemaEdgeDefinition()).edge(EDGE, new SchemaEdgeDefinition()).build());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(updateViewHook).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        final ArgumentCaptor<Context> contextCaptor1 = ArgumentCaptor.forClass(Context.class);
        BDDMockito.given(store.execute(captor.capture(), contextCaptor1.capture())).willReturn(new ArrayList());
        // When / Then
        graph.execute(opChain, user);
        final List<Operation> ops = captor.getValue().getOperations();
        JsonAssert.assertEquals(new View.Builder().edge(EDGE_5).build().toCompactJson(), getView().toCompactJson());
    }

    @Test
    public void shouldCorrectlyAddExtraGroupsFromSchemaViewWithUpdateViewHookWhenNotInBlacklist() throws OperationException {
        // Given
        operation = new GetElements.Builder().build();
        final UpdateViewHook updateViewHook = new UpdateViewHook.Builder().addExtraGroups(true).blackListElementGroups(Collections.singleton(EDGE)).build();
        BDDMockito.given(opChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(opChain.shallowClone()).willReturn(clonedOpChain);
        BDDMockito.given(clonedOpChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(clonedOpChain.flatten()).willReturn(Arrays.asList(operation));
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema.Builder().edge(EDGE_4, new SchemaEdgeDefinition()).edge(EDGE_5, new SchemaEdgeDefinition()).edge(EDGE, new SchemaEdgeDefinition()).build());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(updateViewHook).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        final ArgumentCaptor<Context> contextCaptor1 = ArgumentCaptor.forClass(Context.class);
        BDDMockito.given(store.execute(captor.capture(), contextCaptor1.capture())).willReturn(new ArrayList());
        // When / Then
        graph.execute(opChain, user);
        final List<Operation> ops = captor.getValue().getOperations();
        JsonAssert.assertEquals(new View.Builder().edge(EDGE_5).edge(EDGE_4).build().toCompactJson(), getView().toCompactJson());
    }

    @Test
    public void shouldNotAddExtraGroupsFromSchemaViewWithUpdateViewHookWhenInBlacklist() throws OperationException {
        // Given
        operation = new GetElements.Builder().build();
        final UpdateViewHook updateViewHook = new UpdateViewHook.Builder().addExtraGroups(true).blackListElementGroups(Sets.newHashSet(EDGE_4, EDGE)).build();
        BDDMockito.given(opChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(opChain.shallowClone()).willReturn(clonedOpChain);
        BDDMockito.given(clonedOpChain.getOperations()).willReturn(Lists.newArrayList(operation));
        BDDMockito.given(clonedOpChain.flatten()).willReturn(Arrays.asList(operation));
        final Store store = Mockito.mock(Store.class);
        BDDMockito.given(store.getSchema()).willReturn(new Schema.Builder().edge(EDGE_4, new SchemaEdgeDefinition()).edge(EDGE_5, new SchemaEdgeDefinition()).edge(EDGE, new SchemaEdgeDefinition()).build());
        BDDMockito.given(store.getProperties()).willReturn(new StoreProperties());
        final Graph graph = new Graph.Builder().config(new GraphConfig.Builder().graphId(GraphTest.GRAPH_ID).addHook(updateViewHook).build()).storeProperties(StreamUtil.storeProps(getClass())).store(store).build();
        final ArgumentCaptor<OperationChain> captor = ArgumentCaptor.forClass(OperationChain.class);
        final ArgumentCaptor<Context> contextCaptor1 = ArgumentCaptor.forClass(Context.class);
        BDDMockito.given(store.execute(captor.capture(), contextCaptor1.capture())).willReturn(new ArrayList());
        // When / Then
        graph.execute(opChain, user);
        final List<Operation> ops = captor.getValue().getOperations();
        JsonAssert.assertEquals(new View.Builder().edge(EDGE_5).build().toCompactJson(), getView().toCompactJson());
    }

    public static class TestStoreImpl extends Store {
        @Override
        public Set<StoreTrait> getTraits() {
            return new HashSet<>(0);
        }

        @Override
        protected void addAdditionalOperationHandlers() {
        }

        @Override
        protected OutputOperationHandler<GetElements, CloseableIterable<? extends Element>> getGetElementsHandler() {
            return null;
        }

        @Override
        protected OutputOperationHandler<GetAllElements, CloseableIterable<? extends Element>> getGetAllElementsHandler() {
            return null;
        }

        @Override
        protected OutputOperationHandler<? extends GetAdjacentIds, CloseableIterable<? extends EntityId>> getAdjacentIdsHandler() {
            return null;
        }

        @Override
        protected OperationHandler<? extends AddElements> getAddElementsHandler() {
            return null;
        }

        @Override
        protected Class<? extends Serialiser> getRequiredParentSerialiserClass() {
            return ToBytesSerialiser.class;
        }
    }

    private static class ViewCheckerGraphHook implements GraphHook {
        @Override
        public void preExecute(final OperationChain<?> opChain, final Context context) {
            for (Operation operation : opChain.getOperations()) {
                if ((operation instanceof OperationView) && (null == (getView()))) {
                    throw new IllegalArgumentException("View should not be null");
                }
            }
        }

        @Override
        public <T> T postExecute(final T result, final OperationChain<?> opChain, final Context context) {
            return result;
        }

        @Override
        public <T> T onFailure(final T result, final OperationChain<?> opChain, final Context context, final Exception e) {
            return result;
        }
    }
}

