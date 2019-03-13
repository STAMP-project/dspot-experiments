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
package uk.gov.gchq.gaffer.federatedstore.util;


import FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;


public class FederatedStoreUtilTest {
    @Test
    public void shouldGetGraphIds() {
        // Given
        final Map<String, String> config = new HashMap<>();
        config.put(KEY_OPERATION_OPTIONS_GRAPH_IDS, "graph1,graph2,graph3");
        // When
        final List<String> graphIds = FederatedStoreUtil.getGraphIds(config);
        // Then
        Assert.assertEquals(Arrays.asList("graph1", "graph2", "graph3"), graphIds);
    }

    @Test
    public void shouldGetGraphIdsAndSkipEmptiesAndWhitespace() {
        // Given
        final Map<String, String> config = new HashMap<>();
        config.put(KEY_OPERATION_OPTIONS_GRAPH_IDS, " graph1 , graph2,,graph3 ");
        // When
        final List<String> graphIds = FederatedStoreUtil.getGraphIds(config);
        // Then
        Assert.assertEquals(Arrays.asList("graph1", "graph2", "graph3"), graphIds);
    }

    @Test
    public void shouldGetEmptyGraphIdsWhenEmptyCsvValue() {
        // Given
        final Map<String, String> config = new HashMap<>();
        config.put(KEY_OPERATION_OPTIONS_GRAPH_IDS, "");
        // When
        final List<String> graphIds = FederatedStoreUtil.getGraphIds(config);
        // Then
        Assert.assertEquals(Collections.emptyList(), graphIds);
    }

    @Test
    public void shouldGetNullGraphIdsWhenNullCsvValue() {
        // Given
        final Map<String, String> config = new HashMap<>();
        config.put(KEY_OPERATION_OPTIONS_GRAPH_IDS, null);
        // When
        final List<String> graphIds = FederatedStoreUtil.getGraphIds(config);
        // Then
        Assert.assertNull(graphIds);
    }

    @Test
    public void shouldGetNullGraphIdsWhenNoCsvEntry() {
        // Given
        final Map<String, String> config = new HashMap<>();
        config.put("some other key", "some value");
        // When
        final List<String> graphIds = FederatedStoreUtil.getGraphIds(config);
        // Then
        Assert.assertNull(graphIds);
    }

    @Test
    public void shouldGetNullStringsWhenNullCsv() {
        // Given
        final String csv = null;
        // When
        final List<String> values = FederatedStoreUtil.getCleanStrings(csv);
        // Then
        Assert.assertNull(values);
    }

    @Test
    public void shouldGetEmptyStringsWhenEmptyCsv() {
        // Given
        final String csv = "";
        // When
        final List<String> values = FederatedStoreUtil.getCleanStrings(csv);
        // Then
        Assert.assertEquals(Collections.emptyList(), values);
    }

    @Test
    public void shouldGetCleanStrings() {
        // Given
        final String csv = " 1,2, 3";
        // When
        final List<String> values = FederatedStoreUtil.getCleanStrings(csv);
        // Then
        Assert.assertEquals(Arrays.asList("1", "2", "3"), values);
    }

    @Test
    public void shouldGetCleanStringsWithNoEmptiesAndWhitespace() {
        // Given
        final String csv = ", 1 ,2 ,, 3, ";
        // When
        final List<String> values = FederatedStoreUtil.getCleanStrings(csv);
        // Then
        Assert.assertEquals(Arrays.asList("1", "2", "3"), values);
    }

    @Test
    public void shouldNotUpdateOperationViewIfNotRequired() {
        // Given
        final Graph graph = createGraph();
        final GetElements operation = new GetElements.Builder().view(new View.Builder().edge(EDGE).build()).build();
        // When
        final GetElements updatedOp = FederatedStoreUtil.updateOperationForGraph(operation, graph);
        // Then
        Assert.assertSame(operation, updatedOp);
        Assert.assertSame(operation.getView(), updatedOp.getView());
    }

    @Test
    public void shouldUpdateOperationView() {
        // Given
        final Graph graph = createGraph();
        final GetElements operation = new GetElements.Builder().view(new View.Builder().edge(EDGE, new ViewElementDefinition()).edge(EDGE_2, new ViewElementDefinition()).entity(ENTITY, new ViewElementDefinition()).entity(ENTITY_2, new ViewElementDefinition()).build()).build();
        // When
        final GetElements updatedOp = FederatedStoreUtil.updateOperationForGraph(operation, graph);
        // Then
        Assert.assertNotSame(operation, updatedOp);
        Assert.assertNotSame(operation.getView(), updatedOp.getView());
        Assert.assertEquals(Sets.newHashSet(ENTITY), updatedOp.getView().getEntityGroups());
        Assert.assertEquals(Sets.newHashSet(EDGE), updatedOp.getView().getEdgeGroups());
        Assert.assertSame(operation.getView().getEntity(ENTITY), updatedOp.getView().getEntity(ENTITY));
        Assert.assertSame(operation.getView().getEdge(EDGE), updatedOp.getView().getEdge(EDGE));
    }

    @Test
    public void shouldUpdateOperationViewAndReturnNullIfViewHasNoGroups() {
        // Given
        final Graph graph = createGraph();
        final GetElements operation = new GetElements.Builder().view(new View.Builder().edge(EDGE_2, new ViewElementDefinition()).entity(ENTITY_2, new ViewElementDefinition()).build()).build();
        // When
        final GetElements updatedOp = FederatedStoreUtil.updateOperationForGraph(operation, graph);
        // Then
        Assert.assertNull(updatedOp);
    }

    @Test
    public void shouldUpdateOperationChainAndReturnNullIfNestedOperationViewHasNoGroups() {
        // Given
        final Graph graph = createGraph();
        final OperationChain<?> operation = new OperationChain.Builder().first(new GetElements.Builder().view(new View.Builder().edge(EDGE_2, new ViewElementDefinition()).entity(ENTITY_2, new ViewElementDefinition()).build()).build()).build();
        // When
        final OperationChain<?> updatedOp = FederatedStoreUtil.updateOperationForGraph(operation, graph);
        // Then
        Assert.assertNull(updatedOp);
    }

    @Test
    public void shouldUpdateNestedOperations() {
        // Given
        final Graph graph = createGraph();
        final HashMap<String, String> options = new HashMap<>();
        options.put("key", "value");
        final HashMap<String, String> options2 = new HashMap<>();
        options2.put("key", "value");
        final GetElements operation = new GetElements.Builder().view(new View.Builder().edge(EDGE, new ViewElementDefinition()).edge(EDGE_2, new ViewElementDefinition()).entity(ENTITY, new ViewElementDefinition()).entity(ENTITY_2, new ViewElementDefinition()).build()).options(options2).build();
        final OperationChain opChain = new OperationChain.Builder().first(operation).options(options).build();
        // When
        final OperationChain<?> updatedOpChain = FederatedStoreUtil.updateOperationForGraph(opChain, graph);
        // Then
        Assert.assertNotSame(opChain, updatedOpChain);
        Assert.assertEquals(options, updatedOpChain.getOptions());
        Assert.assertEquals(1, updatedOpChain.getOperations().size());
        final GetElements updatedOperation = ((GetElements) (updatedOpChain.getOperations().get(0)));
        Assert.assertNotSame(operation, updatedOperation);
        Assert.assertEquals(options2, updatedOperation.getOptions());
        Assert.assertNotSame(operation.getView(), updatedOperation.getView());
        Assert.assertEquals(Sets.newHashSet(ENTITY), updatedOperation.getView().getEntityGroups());
        Assert.assertEquals(Sets.newHashSet(EDGE), updatedOperation.getView().getEdgeGroups());
        Assert.assertSame(operation.getView().getEntity(ENTITY), updatedOperation.getView().getEntity(ENTITY));
        Assert.assertSame(operation.getView().getEdge(EDGE), updatedOperation.getView().getEdge(EDGE));
    }

    @Test
    public void shouldNotUpdateAddElementsFlagsWhenNotRequired() {
        // Given
        final Graph graph = createGraph();
        final AddElements operation = new AddElements.Builder().validate(true).skipInvalidElements(true).build();
        // When
        final AddElements updatedOp = FederatedStoreUtil.updateOperationForGraph(operation, graph);
        // Then
        Assert.assertSame(operation, updatedOp);
        Assert.assertNull(updatedOp.getInput());
    }

    @Test
    public void shouldUpdateAddElementsFlagsWhenNullInputAndValidateFalse() {
        // Given
        final Graph graph = createGraph();
        final AddElements operation = new AddElements.Builder().validate(false).skipInvalidElements(true).build();
        // When
        final AddElements updatedOp = FederatedStoreUtil.updateOperationForGraph(operation, graph);
        // Then
        Assert.assertNotSame(operation, updatedOp);
        Assert.assertNull(updatedOp.getInput());
        Assert.assertTrue(updatedOp.isValidate());
        Assert.assertTrue(updatedOp.isSkipInvalidElements());
    }

    @Test
    public void shouldUpdateAddElementsFlagsWhenNullInputAndSkipFalse() {
        // Given
        final Graph graph = createGraph();
        final AddElements operation = new AddElements.Builder().validate(true).skipInvalidElements(false).build();
        // When
        final AddElements updatedOp = FederatedStoreUtil.updateOperationForGraph(operation, graph);
        // Then
        Assert.assertNotSame(operation, updatedOp);
        Assert.assertNull(updatedOp.getInput());
        Assert.assertTrue(updatedOp.isValidate());
        Assert.assertTrue(updatedOp.isSkipInvalidElements());
    }

    @Test
    public void shouldUpdateAddElementsInput() {
        // Given
        final Graph graph = createGraph();
        final AddElements operation = new AddElements.Builder().input(new Entity.Builder().group(ENTITY).build(), new Entity.Builder().group(ENTITY_2).build(), new Edge.Builder().group(EDGE).build(), new Edge.Builder().group(EDGE_2).build()).build();
        // When
        final AddElements updatedOp = FederatedStoreUtil.updateOperationForGraph(operation, graph);
        // Then
        Assert.assertNotSame(operation, updatedOp);
        Assert.assertNotSame(operation.getInput(), updatedOp.getInput());
        final List<Element> updatedInput = Lists.newArrayList(updatedOp.getInput());
        Assert.assertEquals(Arrays.asList(new Entity.Builder().group(ENTITY).build(), new Edge.Builder().group(EDGE).build()), updatedInput);
    }
}

