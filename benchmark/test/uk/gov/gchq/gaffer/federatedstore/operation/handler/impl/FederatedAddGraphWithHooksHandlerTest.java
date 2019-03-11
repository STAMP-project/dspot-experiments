/**
 * Copyright 2018-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.federatedstore.operation.handler.impl;


import FederatedAddGraphWithHooksHandler.USER_IS_LIMITED_TO_ONLY_USING_PARENT_PROPERTIES_ID_FROM_GRAPHLIBRARY_BUT_FOUND_STORE_PROPERTIES_S;
import TestGroups.ENTITY;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.SingleUseMockAccumuloStore;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.federatedstore.FederatedGraphStorage;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.federatedstore.FederatedStoreProperties;
import uk.gov.gchq.gaffer.federatedstore.operation.AddGraphWithHooks;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.hook.GraphHook;
import uk.gov.gchq.gaffer.graph.hook.Log4jLogger;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.store.library.GraphLibrary;
import uk.gov.gchq.gaffer.store.library.HashMapGraphLibrary;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.user.User;


public class FederatedAddGraphWithHooksHandlerTest {
    private static final String FEDERATEDSTORE_GRAPH_ID = "federatedStore";

    private static final String EXPECTED_GRAPH_ID = "testGraphID";

    private static final String EXPECTED_GRAPH_ID_2 = "testGraphID2";

    private static final String CACHE_SERVICE_CLASS_STRING = "uk.gov.gchq.gaffer.cache.impl.HashMapCacheService";

    private static final String EXCEPTION_EXPECTED = "Exception expected";

    private static final AccumuloProperties storeProperties = new AccumuloProperties();

    private User testUser;

    private User authUser;

    private FederatedStore store;

    private FederatedStoreProperties federatedStoreProperties;

    @Test
    public void shouldAddGraph() throws Exception {
        store.initialise(FederatedAddGraphWithHooksHandlerTest.FEDERATEDSTORE_GRAPH_ID, null, federatedStoreProperties);
        Schema expectedSchema = new Schema.Builder().build();
        Assert.assertEquals(0, store.getGraphs(testUser, null).size());
        FederatedAddGraphWithHooksHandler federatedAddGraphWithHooksHandler = new FederatedAddGraphWithHooksHandler();
        federatedAddGraphWithHooksHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID).schema(expectedSchema).storeProperties(FederatedAddGraphWithHooksHandlerTest.storeProperties).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
        Collection<Graph> graphs = store.getGraphs(testUser, null);
        Assert.assertEquals(1, graphs.size());
        Graph next = graphs.iterator().next();
        Assert.assertEquals(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID, next.getGraphId());
        Assert.assertEquals(expectedSchema, next.getSchema());
        federatedAddGraphWithHooksHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID_2).schema(expectedSchema).storeProperties(FederatedAddGraphWithHooksHandlerTest.storeProperties).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
        graphs = store.getGraphs(testUser, null);
        Assert.assertEquals(2, graphs.size());
        Iterator<Graph> iterator = graphs.iterator();
        final HashSet<String> set = Sets.newHashSet();
        while (iterator.hasNext()) {
            set.add(iterator.next().getGraphId());
        } 
        Assert.assertTrue(set.contains(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID));
        Assert.assertTrue(set.contains(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID_2));
    }

    @Test
    public void shouldAddGraphUsingLibrary() throws Exception {
        store.initialise(FederatedAddGraphWithHooksHandlerTest.FEDERATEDSTORE_GRAPH_ID, null, federatedStoreProperties);
        Schema expectedSchema = new Schema.Builder().build();
        FederatedAddGraphWithHooksHandlerTest.storeProperties.setStorePropertiesClass(AccumuloProperties.class);
        Assert.assertEquals(0, store.getGraphs(testUser, null).size());
        Assert.assertEquals(0, store.getGraphs(testUser, null).size());
        FederatedAddGraphWithHooksHandler federatedAddGraphWithHooksHandler = new FederatedAddGraphWithHooksHandler();
        federatedAddGraphWithHooksHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID).schema(expectedSchema).storeProperties(FederatedAddGraphWithHooksHandlerTest.storeProperties).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
        Collection<Graph> graphs = store.getGraphs(testUser, null);
        Assert.assertEquals(1, graphs.size());
        Graph next = graphs.iterator().next();
        Assert.assertEquals(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID, next.getGraphId());
        Assert.assertEquals(expectedSchema, next.getSchema());
        final GraphLibrary library = new HashMapGraphLibrary();
        library.add(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID_2, expectedSchema, FederatedAddGraphWithHooksHandlerTest.storeProperties);
        store.setGraphLibrary(library);
        federatedAddGraphWithHooksHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID_2).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
        graphs = store.getGraphs(testUser, null);
        Assert.assertEquals(2, graphs.size());
        Iterator<Graph> iterator = graphs.iterator();
        final HashSet<String> set = Sets.newHashSet();
        while (iterator.hasNext()) {
            set.add(iterator.next().getGraphId());
        } 
        Assert.assertTrue(set.contains(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID));
        Assert.assertTrue(set.contains(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID_2));
    }

    @Test
    public void shouldThrowWhenOverwriteGraphIsDifferent() throws Exception {
        Schema expectedSchema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").build()).type("string", String.class).build();
        Assert.assertEquals(0, store.getGraphs(testUser, null).size());
        store.initialise(FederatedAddGraphWithHooksHandlerTest.FEDERATEDSTORE_GRAPH_ID, new Schema(), federatedStoreProperties);
        FederatedAddGraphWithHooksHandler federatedAddGraphWithHooksHandler = new FederatedAddGraphWithHooksHandler();
        federatedAddGraphWithHooksHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID).schema(expectedSchema).storeProperties(FederatedAddGraphWithHooksHandlerTest.storeProperties).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
        try {
            federatedAddGraphWithHooksHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID).schema(expectedSchema).schema(new Schema.Builder().type("unusual", String.class).build()).storeProperties(FederatedAddGraphWithHooksHandlerTest.storeProperties).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
            Assert.fail(FederatedAddGraphWithHooksHandlerTest.EXCEPTION_EXPECTED);
        } catch (final Exception e) {
            Assert.assertTrue(e.getMessage().contains(String.format(FederatedGraphStorage.USER_IS_ATTEMPTING_TO_OVERWRITE, FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID)));
        }
    }

    @Test
    public void shouldThrowWhenOverwriteGraphIsSameAndAccessIsDifferent() throws Exception {
        Schema expectedSchema = new Schema.Builder().build();
        Assert.assertEquals(0, store.getGraphs(testUser, null).size());
        store.initialise(FederatedAddGraphWithHooksHandlerTest.FEDERATEDSTORE_GRAPH_ID, new Schema(), federatedStoreProperties);
        FederatedAddGraphWithHooksHandler federatedAddGraphWithHooksHandler = new FederatedAddGraphWithHooksHandler();
        federatedAddGraphWithHooksHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID).schema(expectedSchema).storeProperties(FederatedAddGraphWithHooksHandlerTest.storeProperties).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
        try {
            federatedAddGraphWithHooksHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID).schema(expectedSchema).graphAuths("X").storeProperties(FederatedAddGraphWithHooksHandlerTest.storeProperties).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
            Assert.fail(FederatedAddGraphWithHooksHandlerTest.EXCEPTION_EXPECTED);
        } catch (final Exception e) {
            Assert.assertTrue(e.getMessage().contains(String.format(FederatedGraphStorage.USER_IS_ATTEMPTING_TO_OVERWRITE, FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID)));
        }
    }

    @Test
    public void shouldAddGraphIDOnlyWithAuths() throws Exception {
        federatedStoreProperties.setCustomPropertyAuths("auth1,auth2");
        store.initialise(FederatedAddGraphWithHooksHandlerTest.FEDERATEDSTORE_GRAPH_ID, null, federatedStoreProperties);
        Schema expectedSchema = new Schema.Builder().build();
        Assert.assertEquals(0, store.getGraphs(testUser, null).size());
        FederatedAddGraphWithHooksHandler federatedAddGraphWithHooksHandler = new FederatedAddGraphWithHooksHandler();
        try {
            federatedAddGraphWithHooksHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID).schema(expectedSchema).storeProperties(FederatedAddGraphWithHooksHandlerTest.storeProperties).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
            Assert.fail(FederatedAddGraphWithHooksHandlerTest.EXCEPTION_EXPECTED);
        } catch (OperationException e) {
            Assert.assertEquals(String.format(USER_IS_LIMITED_TO_ONLY_USING_PARENT_PROPERTIES_ID_FROM_GRAPHLIBRARY_BUT_FOUND_STORE_PROPERTIES_S, "{gaffer.store.class=uk.gov.gchq.gaffer.accumulostore.SingleUseMockAccumuloStore, gaffer.store.properties.class=uk.gov.gchq.gaffer.accumulostore.AccumuloProperties}"), e.getMessage());
        }
        federatedAddGraphWithHooksHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID).schema(expectedSchema).storeProperties(FederatedAddGraphWithHooksHandlerTest.storeProperties).build(), new uk.gov.gchq.gaffer.store.Context(authUser), store);
        final Collection<Graph> graphs = store.getGraphs(authUser, null);
        Assert.assertEquals(1, graphs.size());
        Assert.assertEquals(0, store.getGraphs(testUser, null).size());
        Assert.assertEquals(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID, graphs.iterator().next().getGraphId());
    }

    /**
     * Replicating a bug condition when setting auths the
     * FederatedAddGraphWithHooksHandler didn't set the adding user.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void shouldAddGraphWithAuthsAndAddingUser() throws Exception {
        store.initialise(FederatedAddGraphWithHooksHandlerTest.FEDERATEDSTORE_GRAPH_ID, null, federatedStoreProperties);
        Schema expectedSchema = new Schema.Builder().build();
        Assert.assertEquals(0, store.getGraphs(testUser, null).size());
        AccumuloProperties storeProperties = new AccumuloProperties();
        storeProperties.setStorePropertiesClass(AccumuloProperties.class);
        storeProperties.setStoreClass(SingleUseMockAccumuloStore.class);
        new FederatedAddGraphWithHooksHandler().doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID).schema(expectedSchema).storeProperties(storeProperties).graphAuths("testAuth").build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
        final CloseableIterable<? extends Element> elements = new FederatedGetAllElementsHandler().doOperation(new GetAllElements(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
        Assert.assertNotNull(elements);
    }

    @Test
    public void shouldAddGraphWithHooks() throws Exception {
        store.initialise(FederatedAddGraphWithHooksHandlerTest.FEDERATEDSTORE_GRAPH_ID, null, federatedStoreProperties);
        Schema expectedSchema = new Schema.Builder().build();
        Assert.assertEquals(0, store.getGraphs(testUser, null).size());
        FederatedAddGraphWithHooksHandler federatedAddGraphHandler = new FederatedAddGraphWithHooksHandler();
        federatedAddGraphHandler.doOperation(new AddGraphWithHooks.Builder().graphId(FederatedAddGraphWithHooksHandlerTest.EXPECTED_GRAPH_ID).schema(expectedSchema).storeProperties(FederatedAddGraphWithHooksHandlerTest.storeProperties).hooks(new Log4jLogger()).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
        Collection<Graph> graphs = store.getGraphs(testUser, null);
        List<Class<? extends GraphHook>> graphHooks = graphs.iterator().next().getGraphHooks();
        Assert.assertTrue(graphHooks.contains(Log4jLogger.class));
    }
}

