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
package uk.gov.gchq.gaffer.sparkaccumulo.operation.handler.graphframe;


import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.graphframes.GraphFrame;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.Map;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.spark.data.generator.RowToElementGenerator;
import uk.gov.gchq.gaffer.spark.function.GraphFrameToIterableRow;
import uk.gov.gchq.gaffer.spark.operation.graphframe.GetGraphFrameOfElements;
import uk.gov.gchq.gaffer.user.User;


public class GetGraphFrameOfElementsHandlerTest {
    private static final int NUM_ELEMENTS = 10;

    @Test
    public void shouldGetCorrectElementsInGraphFrame() throws OperationException {
        // Given
        final Graph graph = getGraph("/schema-GraphFrame/elements.json", getSimpleElements());
        final GetGraphFrameOfElements gfOperation = new GetGraphFrameOfElements.Builder().view(new View.Builder().edge(EDGE).edge(EDGE_2).entity(ENTITY).entity(ENTITY_2).build()).build();
        final OperationChain<Iterable<? extends Element>> opChain = new OperationChain.Builder().first(gfOperation).then(new Map.Builder<>().first(new GraphFrameToIterableRow()).then(new RowToElementGenerator()).build()).build();
        // When
        final Iterable<? extends Element> results = graph.execute(opChain, new User());
        // Then
        assertElementEquals(getSimpleElements(), results);
    }

    @Test
    public void shouldGetCorrectElementsInGraphFrameWithVertexProperty() throws OperationException {
        // Given
        final List<Element> elements = getSimpleElements();
        for (final Element element : elements) {
            element.putProperty("vertex", "value");
        }
        final Graph graph = getGraph("/schema-GraphFrame/elementsWithVertexProperty.json", elements);
        final GetGraphFrameOfElements gfOperation = new GetGraphFrameOfElements.Builder().view(new View.Builder().edge(EDGE).edge(EDGE_2).entity(ENTITY).entity(ENTITY_2).build()).build();
        final OperationChain<Iterable<? extends Element>> opChain = new OperationChain.Builder().first(gfOperation).then(new Map.Builder<>().first(new GraphFrameToIterableRow()).then(new RowToElementGenerator()).build()).build();
        // When
        final Iterable<? extends Element> results = graph.execute(opChain, new User());
        // Then
        assertElementEquals(getSimpleElements(), results);
    }

    @Test
    public void shouldGetCorrectElementsInGraphFrameWithNoEdges() throws OperationException {
        // Given
        final Graph graph = getGraph("/schema-GraphFrame/elements.json", getSimpleElements());
        final GetGraphFrameOfElements gfOperation = new GetGraphFrameOfElements.Builder().view(new View.Builder().entity(ENTITY).entity(ENTITY_2).build()).build();
        final OperationChain<Iterable<? extends Element>> opChain = new OperationChain.Builder().first(gfOperation).then(new Map.Builder<>().first(new GraphFrameToIterableRow()).then(new RowToElementGenerator()).build()).build();
        // When
        final Iterable<? extends Element> results = graph.execute(opChain, new User());
        // Then
        assertElementEquals(getSimpleElements().stream().filter(( e) -> e instanceof Entity).collect(Collectors.toList()), results);
    }

    @Test
    public void shouldBehaviourInGraphFrameWithNoEntities() throws OperationException {
        // Given
        final Graph graph = getGraph("/schema-GraphFrame/elements.json", getSimpleElements());
        final GetGraphFrameOfElements gfOperation = new GetGraphFrameOfElements.Builder().view(new View.Builder().edge(EDGE).edge(EDGE_2).build()).build();
        final OperationChain<Iterable<? extends Element>> opChain = new OperationChain.Builder().first(gfOperation).then(new Map.Builder<>().first(new GraphFrameToIterableRow()).then(new RowToElementGenerator()).build()).build();
        // When
        final Iterable<? extends Element> results = graph.execute(opChain, new User());
        // Then
        assertElementEquals(getSimpleElements().stream().filter(( e) -> e instanceof Edge).collect(Collectors.toList()), results);
    }

    @Test
    public void shouldGetCorrectElementsInGraphFrameWithNoElements() throws OperationException {
        // Given
        final Graph graph = getGraph("/schema-GraphFrame/elements.json", new ArrayList<>());
        final GetGraphFrameOfElements gfOperation = new GetGraphFrameOfElements.Builder().view(new View.Builder().edge(EDGE).edge(EDGE_2).entity(ENTITY).entity(ENTITY_2).build()).build();
        // When
        final GraphFrame graphFrame = graph.execute(gfOperation, new User());
        // Then
        Assert.assertTrue(graphFrame.edges().javaRDD().isEmpty());
        Assert.assertTrue(graphFrame.vertices().javaRDD().isEmpty());
    }

    @Test
    public void shouldGetCorrectElementsInGraphFrameWithMultipleEdgesBetweenVertices() throws OperationException {
        // Given
        final Graph graph = getGraph("/schema-GraphFrame/elementsComplex.json", getElements());
        final Edge edge1 = new Edge.Builder().group(EDGE_2).source("1").dest("B").directed(true).property("columnQualifier", 1).property("property1", 2).property("property2", 3.0F).property("property3", 4.0).property("property4", 5L).property("count", 100L).build();
        final Edge edge2 = new Edge.Builder().group(EDGE_2).source("1").dest("C").directed(true).property("columnQualifier", 6).property("property1", 7).property("property2", 8.0F).property("property3", 9.0).property("property4", 10L).property("count", 200L).build();
        graph.execute(new AddElements.Builder().input(edge1, edge2).build(), new User());
        final GetGraphFrameOfElements gfOperation = new GetGraphFrameOfElements.Builder().view(new View.Builder().edge(EDGE).edge(EDGE_2).entity(ENTITY).entity(ENTITY_2).build()).build();
        final OperationChain<Iterable<? extends Element>> opChain = new OperationChain.Builder().first(gfOperation).then(new Map.Builder<>().first(new GraphFrameToIterableRow()).then(new RowToElementGenerator()).build()).build();
        // When
        final Iterable<? extends Element> results = graph.execute(opChain, new User());
        // Then
        final List<Element> expected = getElements();
        expected.add(edge1);
        expected.add(edge2);
        assertElementEquals(expected, results);
    }

    @Test
    public void shouldGetCorrectElementsInGraphFrameWithLoops() throws OperationException {
        // Given
        final Graph graph = getGraph("/schema-GraphFrame/elementsComplex.json", getElements());
        final Edge edge1 = new Edge.Builder().group(EDGE).source("B").dest("1").directed(true).property("columnQualifier", 1).property("property1", 2).property("property2", 3.0F).property("property3", 4.0).property("property4", 5L).property("count", 100L).build();
        final Edge edge2 = new Edge.Builder().group(EDGE).source("C").dest("1").directed(true).property("columnQualifier", 6).property("property1", 7).property("property2", 8.0F).property("property3", 9.0).property("property4", 10L).property("count", 200L).build();
        graph.execute(new AddElements.Builder().input(edge1, edge2).build(), new User());
        final GetGraphFrameOfElements gfOperation = new GetGraphFrameOfElements.Builder().view(new View.Builder().edge(EDGE).edge(EDGE_2).entity(ENTITY).entity(ENTITY_2).build()).build();
        final OperationChain<Iterable<? extends Element>> opChain = new OperationChain.Builder().first(gfOperation).then(new Map.Builder<>().first(new GraphFrameToIterableRow()).then(new RowToElementGenerator()).build()).build();
        // When
        final Iterable<? extends Element> results = graph.execute(opChain, new User());
        // Then
        final List<Element> expected = getElements();
        expected.add(edge1);
        expected.add(edge2);
        assertElementEquals(expected, results);
    }

    @Test
    public void shouldGetCorrectElementsInGraphFrameWithRepeatedElements() throws OperationException {
        // Given
        final Graph graph = getGraph("/schema-GraphFrame/elements.json", getSimpleElements());
        graph.execute(new AddElements.Builder().input(getSimpleElements()).build(), new User());
        final GetGraphFrameOfElements gfOperation = new GetGraphFrameOfElements.Builder().view(new View.Builder().edge(EDGE).edge(EDGE_2).entity(ENTITY).entity(ENTITY_2).build()).build();
        final OperationChain<Iterable<? extends Element>> opChain = new OperationChain.Builder().first(gfOperation).then(new Map.Builder<>().first(new GraphFrameToIterableRow()).then(new RowToElementGenerator()).build()).build();
        // When
        final Iterable<? extends Element> results = graph.execute(opChain, new User());
        // Then
        assertElementEquals(getSimpleElements(), results);
    }
}

