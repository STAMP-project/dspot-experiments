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


import FederatedGraphStorage.USER_IS_ATTEMPTING_TO_OVERWRITE;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.federatedstore.exception.StorageException;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.graph.GraphSerialisable;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.store.library.GraphLibrary;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.user.User;


public class FederatedGraphStorageTest {
    public static final String GRAPH_ID_A = "a";

    public static final String GRAPH_ID_B = "b";

    public static final String EXCEPTION_EXPECTED = "Exception expected";

    public static final String X = "x";

    private FederatedGraphStorage graphStorage;

    private AccumuloProperties accumuloProperties;

    private GraphSerialisable a;

    private GraphSerialisable b;

    private User nullUser;

    private User testUser;

    private User authUser;

    private User blankUser;

    private Context testUserContext;

    private Context authUserContext;

    private Context blankUserContext;

    private FederatedAccess access;

    private FederatedAccess altAccess;

    private FederatedAccess disabledByDefaultAccess;

    private SchemaEntityDefinition e1;

    private SchemaEntityDefinition e2;

    private static final String UNUSUAL_TYPE = "unusualType";

    private static final String GROUP_ENT = "ent";

    private static final String GROUP_EDGE = "edg";

    @Test
    public void shouldStartWithNoGraphs() throws Exception {
        final Collection<Graph> graphs = graphStorage.get(nullUser, null);
        Assert.assertEquals(0, graphs.size());
    }

    @Test
    public void shouldGetIdForAddingUser() throws Exception {
        graphStorage.put(a, access);
        final Collection<String> allIds = graphStorage.getAllIds(testUser);
        Assert.assertEquals(1, allIds.size());
        Assert.assertEquals(FederatedGraphStorageTest.GRAPH_ID_A, allIds.iterator().next());
    }

    @Test
    public void shouldGetIdForDisabledGraphs() throws Exception {
        graphStorage.put(a, disabledByDefaultAccess);
        final Collection<String> allIds = graphStorage.getAllIds(testUser);
        Assert.assertEquals(1, allIds.size());
        Assert.assertEquals(FederatedGraphStorageTest.GRAPH_ID_A, allIds.iterator().next());
    }

    @Test
    public void shouldGetIdForAuthUser() throws Exception {
        graphStorage.put(a, access);
        final Collection<String> allIds = graphStorage.getAllIds(authUser);
        Assert.assertEquals(1, allIds.size());
        Assert.assertEquals(FederatedGraphStorageTest.GRAPH_ID_A, allIds.iterator().next());
    }

    @Test
    public void shouldNotGetIdForBlankUser() throws Exception {
        graphStorage.put(a, access);
        final Collection<String> allIds = graphStorage.getAllIds(blankUser);
        Assert.assertEquals(0, allIds.size());
        Assert.assertFalse(allIds.iterator().hasNext());
    }

    @Test
    public void shouldGetGraphForAddingUser() throws Exception {
        graphStorage.put(a, access);
        final Collection<Graph> allGraphs = graphStorage.getAll(testUser);
        Assert.assertEquals(1, allGraphs.size());
        Assert.assertEquals(a.getGraph(), allGraphs.iterator().next());
    }

    @Test
    public void shouldGetGraphForAuthUser() throws Exception {
        graphStorage.put(a, access);
        final Collection<Graph> allGraphs = graphStorage.getAll(authUser);
        Assert.assertEquals(1, allGraphs.size());
        Assert.assertEquals(a.getGraph(), allGraphs.iterator().next());
    }

    @Test
    public void shouldGetDisabledGraphWhenGetAll() throws Exception {
        graphStorage.put(a, disabledByDefaultAccess);
        final Collection<Graph> allGraphs = graphStorage.getAll(authUser);
        Assert.assertEquals(1, allGraphs.size());
        Assert.assertEquals(a.getGraph(), allGraphs.iterator().next());
    }

    @Test
    public void shouldNotGetGraphForBlankUser() throws Exception {
        graphStorage.put(a, access);
        final Collection<Graph> allGraphs = graphStorage.getAll(blankUser);
        Assert.assertEquals(0, allGraphs.size());
        Assert.assertFalse(allGraphs.iterator().hasNext());
    }

    @Test
    public void shouldGetGraphForAddingUserWithCorrectId() throws Exception {
        graphStorage.put(a, access);
        final Collection<Graph> allGraphs = graphStorage.get(testUser, Lists.newArrayList(FederatedGraphStorageTest.GRAPH_ID_A));
        Assert.assertEquals(1, allGraphs.size());
        Assert.assertEquals(a.getGraph(), allGraphs.iterator().next());
    }

    @Test
    public void shouldGetGraphForAuthUserWithCorrectId() throws Exception {
        graphStorage.put(a, access);
        final Collection<Graph> allGraphs = graphStorage.get(authUser, Lists.newArrayList(FederatedGraphStorageTest.GRAPH_ID_A));
        Assert.assertEquals(1, allGraphs.size());
        Assert.assertEquals(a.getGraph(), allGraphs.iterator().next());
    }

    @Test
    public void shouldGetDisabledGraphForAuthUserWithCorrectId() throws Exception {
        graphStorage.put(a, disabledByDefaultAccess);
        final Collection<Graph> allGraphs = graphStorage.get(authUser, Lists.newArrayList(FederatedGraphStorageTest.GRAPH_ID_A));
        Assert.assertEquals(1, allGraphs.size());
        Assert.assertEquals(a.getGraph(), allGraphs.iterator().next());
    }

    @Test
    public void shouldNotGetDisabledGraphForAuthUserWhenNoIdsProvided() throws Exception {
        graphStorage.put(a, disabledByDefaultAccess);
        final Collection<Graph> allGraphs = graphStorage.get(authUser, null);
        Assert.assertEquals(0, allGraphs.size());
    }

    @Test
    public void shouldNotGetGraphForBlankUserWithCorrectId() throws Exception {
        graphStorage.put(a, access);
        try {
            graphStorage.get(blankUser, Lists.newArrayList(FederatedGraphStorageTest.GRAPH_ID_A));
            Assert.fail(FederatedGraphStorageTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(String.format(FederatedGraphStorage.GRAPH_IDS_NOT_VISIBLE, Sets.newHashSet(FederatedGraphStorageTest.GRAPH_ID_A)), e.getMessage());
        }
    }

    @Test
    public void shouldNotGetGraphForAddingUserWithIncorrectId() throws Exception {
        graphStorage.put(a, access);
        try {
            graphStorage.get(testUser, Lists.newArrayList(FederatedGraphStorageTest.X));
            Assert.fail(FederatedGraphStorageTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(String.format(FederatedGraphStorage.GRAPH_IDS_NOT_VISIBLE, Sets.newHashSet(FederatedGraphStorageTest.X)), e.getMessage());
        }
    }

    @Test
    public void shouldNotGetGraphForAuthUserWithIncorrectId() throws Exception {
        graphStorage.put(a, access);
        try {
            graphStorage.get(authUser, Lists.newArrayList(FederatedGraphStorageTest.X));
            Assert.fail(FederatedGraphStorageTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(String.format(FederatedGraphStorage.GRAPH_IDS_NOT_VISIBLE, Sets.newHashSet(FederatedGraphStorageTest.X)), e.getMessage());
        }
    }

    @Test
    public void shouldNotGetGraphForBlankUserWithIncorrectId() throws Exception {
        graphStorage.put(a, access);
        try {
            graphStorage.get(blankUser, Lists.newArrayList(FederatedGraphStorageTest.X));
            Assert.fail(FederatedGraphStorageTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals(String.format(FederatedGraphStorage.GRAPH_IDS_NOT_VISIBLE, Sets.newHashSet(FederatedGraphStorageTest.X)), e.getMessage());
        }
    }

    @Test
    public void shouldSchemaShouldChangeWhenAddingGraphB() throws Exception {
        graphStorage.put(a, access);
        final Schema schemaA = graphStorage.getSchema(((Map<String, String>) (null)), testUserContext);
        Assert.assertEquals(1, schemaA.getTypes().size());
        Assert.assertEquals(String.class, schemaA.getType("string").getClazz());
        Assert.assertEquals(e1, schemaA.getElement("e1"));
        graphStorage.put(b, access);
        final Schema schemaAB = graphStorage.getSchema(((Map<String, String>) (null)), testUserContext);
        Assert.assertNotEquals(schemaA, schemaAB);
        Assert.assertEquals(2, schemaAB.getTypes().size());
        Assert.assertEquals(String.class, schemaAB.getType("string").getClazz());
        Assert.assertEquals(String.class, schemaAB.getType("string2").getClazz());
        Assert.assertEquals(e1, schemaAB.getElement("e1"));
        Assert.assertEquals(e2, schemaAB.getElement("e2"));
    }

    @Test
    public void shouldGetSchemaForAddingUser() throws Exception {
        graphStorage.put(a, access);
        graphStorage.put(b, new FederatedAccess(Sets.newHashSet(FederatedGraphStorageTest.X), FederatedGraphStorageTest.X));
        final Schema schema = graphStorage.getSchema(((Map<String, String>) (null)), testUserContext);
        Assert.assertNotEquals("Revealing hidden schema", 2, schema.getTypes().size());
        Assert.assertEquals(1, schema.getTypes().size());
        Assert.assertEquals(String.class, schema.getType("string").getClazz());
        Assert.assertEquals(e1, schema.getElement("e1"));
    }

    @Test
    public void shouldGetSchemaForAuthUser() throws Exception {
        graphStorage.put(a, access);
        graphStorage.put(b, new FederatedAccess(Sets.newHashSet(FederatedGraphStorageTest.X), FederatedGraphStorageTest.X));
        final Schema schema = graphStorage.getSchema(((Map<String, String>) (null)), authUserContext);
        Assert.assertNotEquals("Revealing hidden schema", 2, schema.getTypes().size());
        Assert.assertEquals(1, schema.getTypes().size());
        Assert.assertEquals(String.class, schema.getType("string").getClazz());
        Assert.assertEquals(e1, schema.getElement("e1"));
    }

    @Test
    public void shouldNotGetSchemaForBlankUser() throws Exception {
        graphStorage.put(a, access);
        graphStorage.put(b, new FederatedAccess(Sets.newHashSet(FederatedGraphStorageTest.X), FederatedGraphStorageTest.X));
        final Schema schema = graphStorage.getSchema(((Map<String, String>) (null)), blankUserContext);
        Assert.assertNotEquals("Revealing hidden schema", 2, schema.getTypes().size());
        Assert.assertEquals("Revealing hidden schema", 0, schema.getTypes().size());
    }

    @Test
    public void shouldGetTraitsForAddingUser() throws Exception {
        graphStorage.put(a, new FederatedAccess(Sets.newHashSet(FederatedGraphStorageTest.X), FederatedGraphStorageTest.X));
        graphStorage.put(b, access);
        final Set<StoreTrait> traits = graphStorage.getTraits(null, testUser);
        Assert.assertNotEquals("Revealing hidden traits", 5, traits.size());
        Assert.assertEquals(10, traits.size());
    }

    @Test
    public void shouldGetTraitsForAuthUser() throws Exception {
        graphStorage.put(a, new FederatedAccess(Sets.newHashSet(FederatedGraphStorageTest.X), FederatedGraphStorageTest.X));
        graphStorage.put(b, access);
        final Set<StoreTrait> traits = graphStorage.getTraits(null, authUser);
        Assert.assertNotEquals("Revealing hidden traits", 5, traits.size());
        Assert.assertEquals(10, traits.size());
    }

    @Test
    public void shouldNotGetTraitsForBlankUser() throws Exception {
        graphStorage.put(a, new FederatedAccess(Sets.newHashSet(FederatedGraphStorageTest.X), FederatedGraphStorageTest.X));
        graphStorage.put(b, access);
        final Set<StoreTrait> traits = graphStorage.getTraits(null, blankUser);
        Assert.assertEquals("Revealing hidden traits", 0, traits.size());
    }

    @Test
    public void shouldRemoveForAddingUser() throws Exception {
        graphStorage.put(a, access);
        final boolean remove = graphStorage.remove(FederatedGraphStorageTest.GRAPH_ID_A, testUser);
        Assert.assertTrue(remove);
    }

    @Test
    public void shouldRemoveForAuthUser() throws Exception {
        graphStorage.put(a, access);
        final boolean remove = graphStorage.remove(FederatedGraphStorageTest.GRAPH_ID_A, authUser);
        Assert.assertTrue(remove);
    }

    @Test
    public void shouldNotRemoveForBlankUser() throws Exception {
        graphStorage.put(a, access);
        final boolean remove = graphStorage.remove(FederatedGraphStorageTest.GRAPH_ID_A, blankUser);
        Assert.assertFalse(remove);
    }

    @Test
    public void shouldGetGraphsInOrder() throws Exception {
        // Given
        graphStorage.put(Lists.newArrayList(a, b), access);
        final List<String> configAB = Arrays.asList(a.getDeserialisedConfig().getGraphId(), b.getDeserialisedConfig().getGraphId());
        final List<String> configBA = Arrays.asList(b.getDeserialisedConfig().getGraphId(), a.getDeserialisedConfig().getGraphId());
        // When
        final Collection<Graph> graphsAB = graphStorage.get(authUser, configAB);
        final Collection<Graph> graphsBA = graphStorage.get(authUser, configBA);
        // Then
        // A B
        final Iterator<Graph> itrAB = graphsAB.iterator();
        Assert.assertSame(a.getGraph(), itrAB.next());
        Assert.assertSame(b.getGraph(), itrAB.next());
        Assert.assertFalse(itrAB.hasNext());
        // B A
        final Iterator<Graph> itrBA = graphsBA.iterator();
        Assert.assertSame(b.getGraph(), itrBA.next());
        Assert.assertSame(a.getGraph(), itrBA.next());
        Assert.assertFalse(itrBA.hasNext());
    }

    @Test
    public void shouldNotAddGraphWhenLibraryThrowsExceptionDuringAdd() throws Exception {
        // given
        GraphLibrary mock = Mockito.mock(GraphLibrary.class);
        String testMockException = "testMockException";
        String graphId = a.getDeserialisedConfig().getGraphId();
        Mockito.doThrow(new RuntimeException(testMockException)).when(mock).checkExisting(graphId, a.getDeserialisedSchema(), a.getDeserialisedProperties());
        graphStorage.setGraphLibrary(mock);
        try {
            graphStorage.put(a, access);
            Assert.fail(FederatedGraphStorageTest.EXCEPTION_EXPECTED);
        } catch (final Exception e) {
            Assert.assertTrue((e instanceof StorageException));
            Assert.assertEquals(testMockException, e.getCause().getMessage());
        }
        try {
            // when
            graphStorage.get(testUser, Lists.newArrayList(graphId));
            Assert.fail(FederatedGraphStorageTest.EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            // then
            Assert.assertEquals(String.format(FederatedGraphStorage.GRAPH_IDS_NOT_VISIBLE, Arrays.toString(new String[]{ graphId })), e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingNullSchema() {
        // Given
        GraphSerialisable nullGraph = null;
        // When / Then
        try {
            graphStorage.put(nullGraph, access);
        } catch (StorageException e) {
            Assert.assertEquals("Graph cannot be null", e.getMessage());
        }
    }

    @Test
    public void checkSchemaNotLeakedWhenOverwritingExistingGraph() throws Exception {
        // Given
        graphStorage.setGraphLibrary(Mockito.mock(GraphLibrary.class));
        final String unusualType = "unusualType";
        final String groupEnt = "ent";
        final String groupEdge = "edg";
        Schema schemaNotToBeExposed = new Schema.Builder().type(unusualType, String.class).type(DIRECTED_EITHER, Boolean.class).entity(groupEnt, new SchemaEntityDefinition.Builder().vertex(unusualType).build()).edge(groupEdge, new SchemaEdgeDefinition.Builder().source(unusualType).destination(unusualType).directed(DIRECTED_EITHER).build()).build();
        final GraphSerialisable graph1 = new GraphSerialisable.Builder().config(new GraphConfig.Builder().graphId(FederatedGraphStorageTest.GRAPH_ID_A).build()).properties(accumuloProperties).schema(schemaNotToBeExposed).build();
        graphStorage.put(graph1, access);
        final GraphSerialisable graph2 = new GraphSerialisable.Builder().config(new GraphConfig.Builder().graphId(FederatedGraphStorageTest.GRAPH_ID_A).build()).schema(new Schema.Builder().entity("e2", e2).type("string2", String.class).build()).properties(accumuloProperties).build();
        // When / Then
        try {
            graphStorage.put(graph2, access);
            Assert.fail(FederatedGraphStorageTest.EXCEPTION_EXPECTED);
        } catch (StorageException e) {
            Assert.assertEquals(((("Error adding graph " + (FederatedGraphStorageTest.GRAPH_ID_A)) + " to storage due to: ") + (String.format(USER_IS_ATTEMPTING_TO_OVERWRITE, FederatedGraphStorageTest.GRAPH_ID_A))), e.getMessage());
            testNotLeakingContents(e, unusualType, groupEdge, groupEnt);
        }
    }

    @Test
    public void checkSchemaNotLeakedWhenAlreadyExistsUnderDifferentAccess() throws Exception {
        // Given
        Schema schemaNotToBeExposed = new Schema.Builder().type(FederatedGraphStorageTest.UNUSUAL_TYPE, String.class).type(DIRECTED_EITHER, Boolean.class).entity(FederatedGraphStorageTest.GROUP_ENT, new SchemaEntityDefinition.Builder().vertex(FederatedGraphStorageTest.UNUSUAL_TYPE).build()).edge(FederatedGraphStorageTest.GROUP_EDGE, new SchemaEdgeDefinition.Builder().source(FederatedGraphStorageTest.UNUSUAL_TYPE).destination(FederatedGraphStorageTest.UNUSUAL_TYPE).directed(DIRECTED_EITHER).build()).build();
        final GraphSerialisable graph1 = new GraphSerialisable.Builder().config(new GraphConfig.Builder().graphId(FederatedGraphStorageTest.GRAPH_ID_A).build()).properties(accumuloProperties).schema(schemaNotToBeExposed).build();
        graphStorage.put(graph1, access);
        // When / Then
        try {
            graphStorage.put(graph1, altAccess);
        } catch (StorageException e) {
            Assert.assertEquals(((("Error adding graph " + (FederatedGraphStorageTest.GRAPH_ID_A)) + " to storage due to: ") + (String.format(USER_IS_ATTEMPTING_TO_OVERWRITE, FederatedGraphStorageTest.GRAPH_ID_A))), e.getMessage());
            testNotLeakingContents(e, FederatedGraphStorageTest.UNUSUAL_TYPE, FederatedGraphStorageTest.GROUP_EDGE, FederatedGraphStorageTest.GROUP_ENT);
        }
    }

    @Test
    public void checkSchemaNotLeakedWhenAlreadyExistsUnderDifferentAccessWithOtherGraphs() throws Exception {
        // Given
        final String unusualType = "unusualType";
        final String groupEnt = "ent";
        final String groupEdge = "edg";
        Schema schemaNotToBeExposed = new Schema.Builder().type(unusualType, String.class).type(DIRECTED_EITHER, Boolean.class).entity(groupEnt, new SchemaEntityDefinition.Builder().vertex(unusualType).build()).edge(groupEdge, new SchemaEdgeDefinition.Builder().source(unusualType).destination(unusualType).directed(DIRECTED_EITHER).build()).build();
        final GraphSerialisable graph1 = new GraphSerialisable.Builder().config(new GraphConfig.Builder().graphId(FederatedGraphStorageTest.GRAPH_ID_A).build()).properties(accumuloProperties).schema(schemaNotToBeExposed).build();
        graphStorage.put(graph1, access);
        final GraphSerialisable graph2 = new GraphSerialisable.Builder().config(new GraphConfig.Builder().graphId(FederatedGraphStorageTest.GRAPH_ID_B).build()).schema(new Schema.Builder().merge(schemaNotToBeExposed).entity("e2", e2).type("string2", String.class).build()).properties(accumuloProperties).build();
        graphStorage.put(graph2, altAccess);
        // When / Then
        try {
            graphStorage.put(graph2, access);
        } catch (StorageException e) {
            Assert.assertEquals(((("Error adding graph " + (FederatedGraphStorageTest.GRAPH_ID_B)) + " to storage due to: ") + (String.format(USER_IS_ATTEMPTING_TO_OVERWRITE, FederatedGraphStorageTest.GRAPH_ID_B))), e.getMessage());
            testNotLeakingContents(e, unusualType, groupEdge, groupEnt);
        }
    }
}

