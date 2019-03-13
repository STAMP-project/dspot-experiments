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


import java.util.Arrays;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.SingleUseMockAccumuloStore;
import uk.gov.gchq.gaffer.federatedstore.operation.AddGraph;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.Graph.Builder;
import uk.gov.gchq.gaffer.store.library.HashMapGraphLibrary;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.user.User;


public class FederatedStoreGraphVisibilityTest {
    private static final String CACHE_SERVICE_CLASS_STRING = "uk.gov.gchq.gaffer.cache.impl.HashMapCacheService";

    private static final String TEST_STORE_PROPS_ID = "testStorePropsId";

    private static final String TEST_SCHEMA_ID = "testSchemaId";

    private static final String TEST_GRAPH_ID = "testGraphId";

    private static final String TEST_FED_GRAPH_ID = "testFedGraphId";

    private static User addingUser;

    private static User nonAddingUser;

    private static User authNonAddingUser;

    private Graph fedGraph;

    private FederatedStoreProperties fedProperties;

    private HashMapGraphLibrary library;

    @Test
    public void shouldNotShowHiddenGraphIdWithIDs() throws Exception {
        final Schema aSchema = new Schema.Builder().entity("e1", new SchemaEntityDefinition.Builder().vertex("string").build()).type("string", String.class).build();
        final AccumuloProperties accProp = new AccumuloProperties();
        accProp.setStoreClass(SingleUseMockAccumuloStore.class.getName());
        accProp.setStorePropertiesClass(AccumuloProperties.class);
        library.add(FederatedStoreGraphVisibilityTest.TEST_GRAPH_ID, FederatedStoreGraphVisibilityTest.TEST_SCHEMA_ID, aSchema, FederatedStoreGraphVisibilityTest.TEST_STORE_PROPS_ID, accProp);
        fedGraph = new Builder().config(new uk.gov.gchq.gaffer.graph.GraphConfig.Builder().graphId(FederatedStoreGraphVisibilityTest.TEST_FED_GRAPH_ID).library(library).build()).addStoreProperties(fedProperties).build();
        fedGraph.execute(// <- with ID
        // <- with ID
        new AddGraph.Builder().graphId("g1").parentPropertiesId(FederatedStoreGraphVisibilityTest.TEST_STORE_PROPS_ID).parentSchemaIds(Arrays.asList(FederatedStoreGraphVisibilityTest.TEST_SCHEMA_ID)).build(), FederatedStoreGraphVisibilityTest.addingUser);
        fedGraph.execute(// <- with ID
        // <- with ID
        new AddGraph.Builder().graphId("g2").parentPropertiesId(FederatedStoreGraphVisibilityTest.TEST_STORE_PROPS_ID).parentSchemaIds(Arrays.asList(FederatedStoreGraphVisibilityTest.TEST_SCHEMA_ID)).graphAuths("auth1").build(), FederatedStoreGraphVisibilityTest.addingUser);
        commonAssertions();
    }

    /* Adhoc test to make sure that the naming of props and schemas without ID's
    is still retrievable via the name of the graph that is was added to the library.
     */
    @Test
    public void shouldNotShowHiddenGraphIdWithoutIDs() throws Exception {
        final Schema aSchema = // <- without ID
        new Schema.Builder().entity("e1", new SchemaEntityDefinition.Builder().vertex("string").build()).type("string", String.class).build();
        final AccumuloProperties accProp = new AccumuloProperties();// <- without ID

        accProp.setStoreClass(SingleUseMockAccumuloStore.class.getName());
        accProp.setStorePropertiesClass(AccumuloProperties.class);
        library.add(FederatedStoreGraphVisibilityTest.TEST_GRAPH_ID, aSchema, accProp);
        fedGraph = new Builder().config(new uk.gov.gchq.gaffer.graph.GraphConfig.Builder().graphId(FederatedStoreGraphVisibilityTest.TEST_FED_GRAPH_ID).library(library).build()).addStoreProperties(fedProperties).build();
        fedGraph.execute(// <- without ID
        // <- without ID
        new AddGraph.Builder().graphId("g1").parentPropertiesId(FederatedStoreGraphVisibilityTest.TEST_GRAPH_ID).parentSchemaIds(Arrays.asList(FederatedStoreGraphVisibilityTest.TEST_GRAPH_ID)).build(), FederatedStoreGraphVisibilityTest.addingUser);
        fedGraph.execute(// <- without ID
        // <- without ID
        new AddGraph.Builder().graphId("g2").parentPropertiesId(FederatedStoreGraphVisibilityTest.TEST_GRAPH_ID).parentSchemaIds(Arrays.asList(FederatedStoreGraphVisibilityTest.TEST_GRAPH_ID)).graphAuths("auth1").build(), FederatedStoreGraphVisibilityTest.addingUser);
        commonAssertions();
    }
}

