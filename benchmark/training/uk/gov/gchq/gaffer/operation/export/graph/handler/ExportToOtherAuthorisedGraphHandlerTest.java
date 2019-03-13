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


import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.export.graph.ExportToOtherAuthorisedGraph;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.library.GraphLibrary;
import uk.gov.gchq.gaffer.store.operation.declaration.OperationDeclaration;
import uk.gov.gchq.gaffer.store.operation.declaration.OperationDeclarations;
import uk.gov.gchq.gaffer.store.operation.handler.OperationHandler;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public class ExportToOtherAuthorisedGraphHandlerTest {
    private static final String GRAPH_ID = "graphId";

    private static final String STORE_PROPS_ID = "storePropsId";

    private static final String SCHEMA_ID = "schemaId";

    public static final String SCHEMA_ID_1 = (ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID) + 1;

    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    private final Store store = Mockito.mock(Store.class);

    private final User user = new User.Builder().opAuths("auth1", "auth2").build();

    private GraphLibrary graphLibrary;

    private Schema schema = new Schema.Builder().build();

    private StoreProperties storeProperties;

    private Map<String, List<String>> idAuths = new HashMap<>();

    @Test
    public void shouldThrowExceptionWhenExportingToSameGraph() {
        // Given
        BDDMockito.given(store.getGraphLibrary()).willReturn(graphLibrary);
        final ExportToOtherAuthorisedGraph export = new ExportToOtherAuthorisedGraph.Builder().graphId(ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Cannot export to the same Graph"));
        }
    }

    @Test
    public void shouldThrowExceptionWithNullGraphLibrary() {
        // Given
        final ExportToOtherAuthorisedGraph export = new ExportToOtherAuthorisedGraph.Builder().graphId(ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Store GraphLibrary is null"));
        }
    }

    @Test
    public void shouldCreateGraphWithGraphIdInLibraryAndAuths() {
        // Given
        graphLibrary.addOrUpdate(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1), schema, storeProperties);
        BDDMockito.given(store.getGraphLibrary()).willReturn(graphLibrary);
        List<String> graphIdAuths = new ArrayList<>();
        graphIdAuths.add("auth1");
        idAuths.put(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1), graphIdAuths);
        final ExportToOtherAuthorisedGraph export = new ExportToOtherAuthorisedGraph.Builder().graphId(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1)).build();
        // When
        Graph graph = createGraph(export);
        // Then
        Assert.assertEquals(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1), graph.getGraphId());
        Assert.assertEquals(schema, graph.getSchema());
        Assert.assertEquals(storeProperties, graph.getStoreProperties());
    }

    @Test
    public void shouldCreateGraphWithParentSchemaIdAndStorePropertiesIdAndAuths() {
        // Given
        Schema schema1 = new Schema.Builder().build();
        graphLibrary.addOrUpdate(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID, schema, ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID, storeProperties);
        graphLibrary.addSchema(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, schema1);
        BDDMockito.given(store.getGraphLibrary()).willReturn(graphLibrary);
        List<String> opAuths = Lists.newArrayList("auth1");
        idAuths.put(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2), opAuths);
        idAuths.put(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, opAuths);
        idAuths.put(ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID, opAuths);
        final ExportToOtherAuthorisedGraph export = new ExportToOtherAuthorisedGraph.Builder().graphId(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2)).parentSchemaIds(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1).parentStorePropertiesId(ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID).build();
        // When
        Graph graph = createGraph(export);
        // Then
        Assert.assertEquals(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2), graph.getGraphId());
        Assert.assertEquals(schema1, graph.getSchema());
        Assert.assertEquals(storeProperties, graph.getStoreProperties());
    }

    @Test
    public void shouldThrowExceptionWithParentSchemaIdAndStorePropertiesIdAndNoGraphAuths() {
        // Given
        Schema schema1 = new Schema.Builder().build();
        graphLibrary.addOrUpdate(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID, schema, ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID, storeProperties);
        graphLibrary.addSchema(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, schema1);
        BDDMockito.given(store.getGraphLibrary()).willReturn(graphLibrary);
        List<String> opAuths = Lists.newArrayList("auth1");
        idAuths.put(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, opAuths);
        idAuths.put(ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID, opAuths);
        final ExportToOtherAuthorisedGraph export = new ExportToOtherAuthorisedGraph.Builder().graphId(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2)).parentSchemaIds(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1).parentStorePropertiesId(ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("User is not authorised to export using graphId"));
        }
    }

    @Test
    public void shouldThrowExceptionWithParentSchemaIdAndStorePropertiesIdAndNoSchemaAuths() {
        // Given
        Schema schema1 = new Schema.Builder().build();
        graphLibrary.addOrUpdate(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID, schema, ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID, storeProperties);
        graphLibrary.addSchema(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, schema1);
        BDDMockito.given(store.getGraphLibrary()).willReturn(graphLibrary);
        List<String> opAuths = Lists.newArrayList("auth1");
        idAuths.put(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2), opAuths);
        idAuths.put(ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID, opAuths);
        final ExportToOtherAuthorisedGraph export = new ExportToOtherAuthorisedGraph.Builder().graphId(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2)).parentSchemaIds(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1).parentStorePropertiesId(ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("User is not authorised to export using schemaId"));
        }
    }

    @Test
    public void shouldCreateGraphWithParentSchemaIdAndStorePropertiesIdAndNoStorePropsAuths() {
        // Given
        Schema schema1 = new Schema.Builder().build();
        graphLibrary.addOrUpdate(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID, schema, ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID, storeProperties);
        graphLibrary.addSchema(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, schema1);
        BDDMockito.given(store.getGraphLibrary()).willReturn(graphLibrary);
        List<String> opAuths = Lists.newArrayList("auth1");
        idAuths.put(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2), opAuths);
        idAuths.put(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, opAuths);
        final ExportToOtherAuthorisedGraph export = new ExportToOtherAuthorisedGraph.Builder().graphId(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2)).parentSchemaIds(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1).parentStorePropertiesId(ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("User is not authorised to export using storePropertiesId"));
        }
    }

    @Test
    public void shouldThrowExceptionWithParentSchemaIdWithNoParentStorePropertiesIdAndAuths() {
        // Given
        Schema schema1 = new Schema.Builder().build();
        graphLibrary.addOrUpdate(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1), ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID, schema, ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID, storeProperties);
        graphLibrary.addSchema(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, schema1);
        BDDMockito.given(store.getGraphLibrary()).willReturn(graphLibrary);
        List<String> opAuths = Lists.newArrayList("auth1");
        idAuths.put(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2), opAuths);
        idAuths.put(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, opAuths);
        final ExportToOtherAuthorisedGraph export = new ExportToOtherAuthorisedGraph.Builder().graphId(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2)).parentSchemaIds(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("parentStorePropertiesId must be specified with parentSchemaId"));
        }
    }

    @Test
    public void shouldThrowExceptionWithParentStorePropertiesIdWithNoParentSchemaIdAndAuths() {
        // Given
        Schema schema1 = new Schema.Builder().build();
        graphLibrary.addOrUpdate(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1), schema, storeProperties);
        graphLibrary.addSchema(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, schema1);
        BDDMockito.given(store.getGraphLibrary()).willReturn(graphLibrary);
        List<String> opAuths = Lists.newArrayList("auth1");
        idAuths.put(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2), opAuths);
        idAuths.put(ExportToOtherAuthorisedGraphHandlerTest.SCHEMA_ID_1, opAuths);
        idAuths.put(ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID, opAuths);
        final ExportToOtherAuthorisedGraph export = new ExportToOtherAuthorisedGraph.Builder().graphId(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 2)).parentStorePropertiesId(ExportToOtherAuthorisedGraphHandlerTest.STORE_PROPS_ID).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("parentSchemaIds must be specified with parentStorePropertiesId"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenGraphIdCannotBeFound() {
        // Given
        BDDMockito.given(store.getGraphLibrary()).willReturn(graphLibrary);
        List<String> graphIdAuths = new ArrayList<>();
        graphIdAuths.add("auth1");
        idAuths.put(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1), graphIdAuths);
        final ExportToOtherAuthorisedGraph export = new ExportToOtherAuthorisedGraph.Builder().graphId(((ExportToOtherAuthorisedGraphHandlerTest.GRAPH_ID) + 1)).build();
        // When / Then
        try {
            createGraph(export);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("GraphLibrary cannot be found with graphId"));
        }
    }

    @Test
    public void shouldGetHandlerFromJson() throws OperationException {
        // Given
        OperationDeclarations opDeclarations = OperationDeclarations.fromPaths("src/test/resources/ExportToOtherAuthorisedGraphOperationDeclarations.json");
        OperationDeclaration opDeclaration = opDeclarations.getOperations().get(0);
        OperationHandler handler = opDeclaration.getHandler();
        // When / Then
        Assert.assertTrue(handler.getClass().getName().contains("AuthorisedGraph"));
    }
}

