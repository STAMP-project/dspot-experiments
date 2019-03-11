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
package uk.gov.gchq.gaffer.mapstore.impl;


import DirectedType.DIRECTED;
import DirectedType.EITHER;
import DirectedType.UNDIRECTED;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.commonutil.stream.Streams;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;


public class GetAllElementsHandlerTest {
    static final String BASIC_ENTITY = "BasicEntity";

    static final String BASIC_EDGE1 = "BasicEdge";

    static final String BASIC_EDGE2 = "BasicEdge2";

    static final String PROPERTY1 = "property1";

    static final String PROPERTY2 = "property2";

    static final String COUNT = "count";

    private static final int NUM_LOOPS = 10;

    @Test
    public void testAddAndGetAllElementsNoAggregation() throws OperationException, StoreException {
        // Given
        final Graph graph = GetAllElementsHandlerTest.getGraph();
        final AddElements addElements = new AddElements.Builder().input(GetAllElementsHandlerTest.getElements()).build();
        graph.execute(addElements, new User());
        // When
        final GetAllElements getAllElements = new GetAllElements.Builder().build();
        final CloseableIterable<? extends Element> results = graph.execute(getAllElements, new User());
        // Then
        final Set<Element> resultsSet = new HashSet<>();
        Streams.toStream(results).forEach(resultsSet::add);
        Assert.assertEquals(new HashSet(GetAllElementsHandlerTest.getElements()), resultsSet);
        // Repeat to ensure iterator can be consumed twice
        resultsSet.clear();
        Streams.toStream(results).forEach(resultsSet::add);
        Assert.assertEquals(new HashSet(GetAllElementsHandlerTest.getElements()), resultsSet);
    }

    @Test
    public void testAddAndGetAllElementsWithAggregation() throws OperationException, StoreException {
        // Given
        final Graph graph = GetAllElementsHandlerTest.getGraph();
        final AddElements addElements = new AddElements.Builder().input(GetAllElementsHandlerTest.getElementsForAggregation()).build();
        graph.execute(addElements, new User());
        // When
        final GetAllElements getAllElements = new GetAllElements.Builder().build();
        final CloseableIterable<? extends Element> results = graph.execute(getAllElements, new User());
        // Then
        final Set<Element> resultsSet = new HashSet<>();
        Streams.toStream(results).forEach(resultsSet::add);
        final Set<Element> expectedResults = new HashSet<>();
        final Entity entity = new Entity(GetAllElementsHandlerTest.BASIC_ENTITY, "0");
        entity.putProperty(GetAllElementsHandlerTest.PROPERTY1, "p");
        entity.putProperty(GetAllElementsHandlerTest.COUNT, GetAllElementsHandlerTest.NUM_LOOPS);
        expectedResults.add(entity);
        final Edge edge1 = new Edge.Builder().group(GetAllElementsHandlerTest.BASIC_EDGE1).source("A").dest("B").directed(true).build();
        edge1.putProperty(GetAllElementsHandlerTest.PROPERTY1, "q");
        edge1.putProperty(GetAllElementsHandlerTest.COUNT, (2 * (GetAllElementsHandlerTest.NUM_LOOPS)));
        expectedResults.add(edge1);
        final Edge edge2 = new Edge.Builder().group(GetAllElementsHandlerTest.BASIC_EDGE2).source("X").dest("Y").directed(false).build();
        edge2.putProperty(GetAllElementsHandlerTest.PROPERTY1, "r");
        edge2.putProperty(GetAllElementsHandlerTest.PROPERTY2, "s");
        edge2.putProperty(GetAllElementsHandlerTest.COUNT, (3 * ((GetAllElementsHandlerTest.NUM_LOOPS) / 2)));
        expectedResults.add(edge2);
        final Edge edge3 = new Edge.Builder().group(GetAllElementsHandlerTest.BASIC_EDGE2).source("X").dest("Y").directed(false).build();
        edge3.putProperty(GetAllElementsHandlerTest.PROPERTY1, "r");
        edge3.putProperty(GetAllElementsHandlerTest.PROPERTY2, "t");
        edge3.putProperty(GetAllElementsHandlerTest.COUNT, (3 * ((GetAllElementsHandlerTest.NUM_LOOPS) / 2)));
        expectedResults.add(edge3);
        Assert.assertEquals(expectedResults, resultsSet);
    }

    @Test
    public void testGetAllElementsWithViewRestrictedByGroup() throws OperationException {
        // Given
        final Graph graph = GetAllElementsHandlerTest.getGraph();
        final AddElements addElements = new AddElements.Builder().input(GetAllElementsHandlerTest.getElements()).build();
        graph.execute(addElements, new User());
        // When
        final GetAllElements getAllElements = new GetAllElements.Builder().view(new View.Builder().edge(GetAllElementsHandlerTest.BASIC_EDGE1).build()).build();
        final CloseableIterable<? extends Element> results = graph.execute(getAllElements, new User());
        // Then
        final Set<Element> resultsSet = new HashSet<>();
        Streams.toStream(results).forEach(resultsSet::add);
        final Set<Element> expectedResults = new HashSet<>();
        GetAllElementsHandlerTest.getElements().stream().filter(( e) -> e.getGroup().equals(BASIC_EDGE1)).forEach(expectedResults::add);
        Assert.assertEquals(expectedResults, resultsSet);
    }

    @Test
    public void testGetAllElementsWithViewRestrictedByGroupAndAPreAggregationFilter() throws OperationException {
        // Given
        final Graph graph = GetAllElementsHandlerTest.getGraph();
        final AddElements addElements = new AddElements.Builder().input(GetAllElementsHandlerTest.getElements()).build();
        graph.execute(addElements, new User());
        // When
        final GetAllElements getAllElements = new GetAllElements.Builder().view(new View.Builder().edge(GetAllElementsHandlerTest.BASIC_EDGE1, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(GetAllElementsHandlerTest.COUNT).execute(new IsMoreThan(5)).build()).build()).build()).build();
        final CloseableIterable<? extends Element> results = graph.execute(getAllElements, new User());
        // Then
        final Set<Element> resultsSet = new HashSet<>();
        Streams.toStream(results).forEach(resultsSet::add);
        final Set<Element> expectedResults = new HashSet<>();
        GetAllElementsHandlerTest.getElements().stream().filter(( e) -> (e.getGroup().equals(BASIC_EDGE1)) && (((int) (e.getProperty(COUNT))) > 5)).forEach(expectedResults::add);
        Assert.assertEquals(expectedResults, resultsSet);
    }

    @Test
    public void testGetAllElementsWithViewRestrictedByGroupAndAPostAggregationFilter() throws OperationException {
        // Given
        final Graph graph = GetAllElementsHandlerTest.getGraph();
        final AddElements addElements = new AddElements.Builder().input(GetAllElementsHandlerTest.getElements()).build();
        graph.execute(addElements, new User());
        // When
        final GetAllElements getAllElements = new GetAllElements.Builder().view(new View.Builder().edge(GetAllElementsHandlerTest.BASIC_EDGE1, new ViewElementDefinition.Builder().postAggregationFilter(new ElementFilter.Builder().select(GetAllElementsHandlerTest.COUNT).execute(new IsMoreThan(5)).build()).build()).build()).build();
        final CloseableIterable<? extends Element> results = graph.execute(getAllElements, new User());
        // Then
        final Set<Element> resultsSet = new HashSet<>();
        Streams.toStream(results).forEach(resultsSet::add);
        final Set<Element> expectedResults = new HashSet<>();
        GetAllElementsHandlerTest.getElements().stream().filter(( e) -> (e.getGroup().equals(BASIC_EDGE1)) && (((int) (e.getProperty(COUNT))) > 5)).forEach(expectedResults::add);
        Assert.assertEquals(expectedResults, resultsSet);
    }

    @Test
    public void testGetAllElementsWithAndWithEntities() throws OperationException {
        // Given
        final Graph graph = GetAllElementsHandlerTest.getGraph();
        final AddElements addElements = new AddElements.Builder().input(GetAllElementsHandlerTest.getElements()).build();
        graph.execute(addElements, new User());
        // When no entities
        GetAllElements getAllElements = new GetAllElements.Builder().view(new View.Builder().edge(EDGE).edge(EDGE_2).build()).build();
        CloseableIterable<? extends Element> results = graph.execute(getAllElements, new User());
        // Then
        final Set<Element> resultsSet = new HashSet<>();
        Streams.toStream(results).forEach(resultsSet::add);
        final Set<Element> expectedResults = new HashSet<>();
        GetAllElementsHandlerTest.getElements().stream().filter(( e) -> e instanceof Edge).forEach(expectedResults::add);
        Assert.assertEquals(expectedResults, resultsSet);
        // When view has entities
        getAllElements = new GetAllElements.Builder().view(new View.Builder().entity(ENTITY).edge(EDGE).edge(EDGE_2).build()).build();
        results = graph.execute(getAllElements, new User());
        // Then
        resultsSet.clear();
        Streams.toStream(results).forEach(resultsSet::add);
        expectedResults.clear();
        GetAllElementsHandlerTest.getElements().forEach(expectedResults::add);
        Assert.assertEquals(expectedResults, resultsSet);
    }

    @Test
    public void testGetAllElementsDirectedTypeOption() throws OperationException {
        // Given
        final Graph graph = GetAllElementsHandlerTest.getGraph();
        final AddElements addElements = new AddElements.Builder().input(GetAllElementsHandlerTest.getElements()).build();
        graph.execute(addElements, new User());
        // When directedType is ALL
        GetAllElements getAllElements = new GetAllElements.Builder().directedType(EITHER).build();
        CloseableIterable<? extends Element> results = graph.execute(getAllElements, new User());
        // Then
        final Set<Element> resultsSet = new HashSet<>();
        Streams.toStream(results).forEach(resultsSet::add);
        final Set<Element> expectedResults = new HashSet<>();
        GetAllElementsHandlerTest.getElements().forEach(expectedResults::add);
        Assert.assertEquals(expectedResults, resultsSet);
        // When view has no edges
        getAllElements = new GetAllElements.Builder().view(new View.Builder().entity(ENTITY).build()).build();
        results = graph.execute(getAllElements, new User());
        // Then
        resultsSet.clear();
        Streams.toStream(results).forEach(resultsSet::add);
        expectedResults.clear();
        GetAllElementsHandlerTest.getElements().stream().filter(( element) -> element instanceof Entity).forEach(expectedResults::add);
        Assert.assertEquals(expectedResults, resultsSet);
        // When directedType is DIRECTED
        getAllElements = new GetAllElements.Builder().directedType(DIRECTED).build();
        results = graph.execute(getAllElements, new User());
        // Then
        resultsSet.clear();
        Streams.toStream(results).forEach(resultsSet::add);
        expectedResults.clear();
        GetAllElementsHandlerTest.getElements().stream().filter(( element) -> (element instanceof Entity) || (((Edge) (element)).isDirected())).forEach(expectedResults::add);
        Assert.assertEquals(expectedResults, resultsSet);
        // When directedType is UNDIRECTED
        getAllElements = new GetAllElements.Builder().directedType(UNDIRECTED).build();
        results = graph.execute(getAllElements, new User());
        // Then
        resultsSet.clear();
        Streams.toStream(results).forEach(resultsSet::add);
        expectedResults.clear();
        GetAllElementsHandlerTest.getElements().stream().filter(( element) -> (element instanceof Entity) || (!(((Edge) (element)).isDirected()))).forEach(expectedResults::add);
        Assert.assertEquals(expectedResults, resultsSet);
    }
}

