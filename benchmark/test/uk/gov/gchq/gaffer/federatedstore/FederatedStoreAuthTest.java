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


import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.federatedstore.operation.handler.impl.FederatedAddGraphHandler;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.user.User;


public class FederatedStoreAuthTest {
    private static final String FEDERATEDSTORE_GRAPH_ID = "federatedStore";

    private static final String EXPECTED_GRAPH_ID = "testGraphID";

    private static final String CACHE_SERVICE_CLASS_STRING = "uk.gov.gchq.gaffer.cache.impl.HashMapCacheService";

    private final FederatedAddGraphHandler federatedAddGraphHandler = new FederatedAddGraphHandler();

    private User testUser;

    private User authUser;

    private FederatedStore federatedStore;

    private FederatedStoreProperties federatedStoreProperties;

    private AccumuloProperties graphStoreProperties;

    private Schema schema;

    @Test
    public void shouldAddGraphWithAuth() throws Exception {
        federatedStore.initialise(FederatedStoreAuthTest.FEDERATEDSTORE_GRAPH_ID, null, federatedStoreProperties);
        federatedAddGraphHandler.doOperation(new uk.gov.gchq.gaffer.federatedstore.operation.AddGraph.Builder().graphId(FederatedStoreAuthTest.EXPECTED_GRAPH_ID).schema(schema).storeProperties(graphStoreProperties).graphAuths("auth1").build(), new uk.gov.gchq.gaffer.store.Context(testUser), federatedStore);
        Collection<Graph> graphs = federatedStore.getGraphs(authUser, null);
        Assert.assertEquals(1, graphs.size());
        Graph next = graphs.iterator().next();
        Assert.assertEquals(FederatedStoreAuthTest.EXPECTED_GRAPH_ID, next.getGraphId());
        Assert.assertEquals(schema, next.getSchema());
        graphs = federatedStore.getGraphs(blankUser(), null);
        Assert.assertNotNull(graphs);
        Assert.assertTrue(graphs.isEmpty());
    }

    @Test
    public void shouldNotShowHiddenGraphsInError() throws Exception {
        federatedStore.initialise(FederatedStoreAuthTest.FEDERATEDSTORE_GRAPH_ID, null, federatedStoreProperties);
        final String unusualType = "unusualType";
        final String groupEnt = "ent";
        final String groupEdge = "edg";
        schema = new Schema.Builder().type(unusualType, String.class).type(DIRECTED_EITHER, Boolean.class).entity(groupEnt, new SchemaEntityDefinition.Builder().vertex(unusualType).build()).edge(groupEdge, new SchemaEdgeDefinition.Builder().source(unusualType).destination(unusualType).directed(DIRECTED_EITHER).build()).build();
        federatedAddGraphHandler.doOperation(new uk.gov.gchq.gaffer.federatedstore.operation.AddGraph.Builder().graphId(FederatedStoreAuthTest.EXPECTED_GRAPH_ID).schema(schema).storeProperties(graphStoreProperties).graphAuths("auth1").build(), new uk.gov.gchq.gaffer.store.Context(authUser), federatedStore);
        Assert.assertEquals(1, federatedStore.getGraphs(authUser, null).size());
        try {
            federatedAddGraphHandler.doOperation(new uk.gov.gchq.gaffer.federatedstore.operation.AddGraph.Builder().graphId(FederatedStoreAuthTest.EXPECTED_GRAPH_ID).schema(schema).storeProperties(graphStoreProperties).graphAuths("nonMatchingAuth").build(), new uk.gov.gchq.gaffer.store.Context(testUser), federatedStore);
            Assert.fail("exception expected");
        } catch (final OperationException e) {
            Assert.assertEquals(String.format("Error adding graph %s to storage due to: User is attempting to overwrite a graph within FederatedStore. GraphId: %s", FederatedStoreAuthTest.EXPECTED_GRAPH_ID, FederatedStoreAuthTest.EXPECTED_GRAPH_ID), e.getCause().getMessage());
            String message = "error message should not contain details about schema";
            Assert.assertFalse(message, e.getMessage().contains(unusualType));
            Assert.assertFalse(message, e.getMessage().contains(groupEdge));
            Assert.assertFalse(message, e.getMessage().contains(groupEnt));
        }
        Assert.assertTrue(federatedStore.getGraphs(testUser(), null).isEmpty());
    }
}

