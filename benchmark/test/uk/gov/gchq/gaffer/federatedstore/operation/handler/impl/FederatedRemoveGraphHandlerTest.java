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
package uk.gov.gchq.gaffer.federatedstore.operation.handler.impl;


import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.SingleUseMockAccumuloStore;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.federatedstore.FederatedStoreProperties;
import uk.gov.gchq.gaffer.federatedstore.operation.RemoveGraph;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.graph.GraphSerialisable;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public class FederatedRemoveGraphHandlerTest {
    private static final String FEDERATEDSTORE_GRAPH_ID = "federatedStore";

    private static final String EXPECTED_GRAPH_ID = "testGraphID";

    private static final String CACHE_SERVICE_CLASS_STRING = "uk.gov.gchq.gaffer.cache.impl.HashMapCacheService";

    private User testUser;

    @Test
    public void shouldRemoveGraph() throws Exception {
        FederatedStore store = new FederatedStore();
        final FederatedStoreProperties federatedStoreProperties = new FederatedStoreProperties();
        federatedStoreProperties.setCacheProperties(FederatedRemoveGraphHandlerTest.CACHE_SERVICE_CLASS_STRING);
        store.initialise(FederatedRemoveGraphHandlerTest.FEDERATEDSTORE_GRAPH_ID, null, federatedStoreProperties);
        AccumuloProperties storeProperties = new AccumuloProperties();
        storeProperties.setStoreClass(SingleUseMockAccumuloStore.class);
        store.addGraphs(testUser.getOpAuths(), null, false, new GraphSerialisable.Builder().config(new GraphConfig(FederatedRemoveGraphHandlerTest.EXPECTED_GRAPH_ID)).schema(new Schema.Builder().build()).properties(storeProperties).build());
        Assert.assertEquals(1, store.getGraphs(testUser, null).size());
        new FederatedRemoveGraphHandler().doOperation(new RemoveGraph.Builder().graphId(FederatedRemoveGraphHandlerTest.EXPECTED_GRAPH_ID).build(), new uk.gov.gchq.gaffer.store.Context(testUser), store);
        Collection<Graph> graphs = store.getGraphs(testUser, null);
        Assert.assertEquals(0, graphs.size());
    }
}

