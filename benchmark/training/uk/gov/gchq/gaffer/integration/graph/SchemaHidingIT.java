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
package uk.gov.gchq.gaffer.integration.graph;


import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestPropertyNames.COUNT;
import TestPropertyNames.PROP_1;
import TestPropertyNames.TIMESTAMP;
import TestPropertyNames.VISIBILITY;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.Graph.Builder;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.user.User;


/**
 * Integration tests to check that an store can be configured with a schema
 * containing groups 1 and 2. Then a new Graph can be constructed with a limited
 * schema, perhaps just containing group 1. The graph should then just
 * completely hide group 2 and never read any group 2 data from the store.
 */
public abstract class SchemaHidingIT {
    private static final User USER = new User.Builder().dataAuth("public").build();

    protected final String storePropertiesPath;

    public SchemaHidingIT(final String storePropertiesPath) {
        this.storePropertiesPath = storePropertiesPath;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCreateStoreWithFullSchemaAndThenBeAbleUseASubsetOfTheSchema() throws Exception {
        // Add some data to the full graph
        final Store fullStore = createStore(createFullSchema());
        final Graph fullGraph = new Builder().store(fullStore).build();
        final Edge edge1a = new Edge.Builder().source("source1a").dest("dest1a").directed(true).group(EDGE).property(COUNT, 1).property(PROP_1, "1a").property(VISIBILITY, "public").property(TIMESTAMP, 1L).build();
        final Edge edge1b = new Edge.Builder().source("source1b").dest("dest1b").directed(true).group(EDGE).property(COUNT, 1).property(PROP_1, "1b").property(VISIBILITY, "public").property(TIMESTAMP, 1L).build();
        final Edge edge2 = new Edge.Builder().source("source2").dest("dest2").directed(true).group(EDGE_2).property(COUNT, 1).property(PROP_1, "2").property(VISIBILITY, "public").property(TIMESTAMP, 1L).build();
        fullGraph.execute(new AddElements.Builder().input(edge1a, edge1b, edge2).build(), SchemaHidingIT.USER);
        // Create a graph with a hidden group backed by the same store
        final Store filteredStore = createStore(createFilteredSchema());
        final Graph filteredGraph = new Builder().store(filteredStore).build();
        final List<Edge> fullExpectedResults = Arrays.asList(edge1a, edge1b, edge2);
        final List<Edge> filteredExpectedResults = Arrays.asList(edge1a, edge1b);
        // Run operations and check the hidden group is hidden when the operation is run on the filtered graph
        testOperations(fullGraph, filteredGraph, fullExpectedResults, filteredExpectedResults);
    }
}

