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
package uk.gov.gchq.gaffer.store.operation.handler;


import TestGroups.EDGE;
import TestGroups.ENTITY;
import ToVertices.EdgeVertices.SOURCE;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.graph.Walk;
import uk.gov.gchq.gaffer.data.graph.function.walk.ExtractWalkEdgesFromHop;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.Map;
import uk.gov.gchq.gaffer.operation.impl.output.ToSet;
import uk.gov.gchq.gaffer.operation.impl.output.ToVertices;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.operation.OperationChainValidator;
import uk.gov.gchq.gaffer.store.optimiser.OperationChainOptimiser;
import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.koryphe.impl.function.ToString;

import static java.util.Arrays.asList;


public class MapHandlerTest {
    private Context context;

    private Store store;

    private Function<Integer, Integer> function;

    private Integer input;

    private static final Edge EDGE_AB = new Edge.Builder().group(EDGE).source("A").dest("B").directed(true).build();

    private static final Edge EDGE_BC = new Edge.Builder().group(EDGE).source("B").dest("C").directed(true).build();

    private static final Edge EDGE_BD = new Edge.Builder().group(EDGE).source("B").dest("D").directed(true).build();

    private static final Edge EDGE_CA = new Edge.Builder().group(EDGE).source("C").dest("A").directed(true).build();

    private static final Edge EDGE_CB = new Edge.Builder().group(EDGE).source("C").dest("B").directed(true).build();

    private static final Edge EDGE_DA = new Edge.Builder().group(EDGE).source("D").dest("A").directed(true).build();

    private static final Entity ENTITY_B = new Entity.Builder().group(ENTITY).vertex("B").build();

    private static final Entity ENTITY_C = new Entity.Builder().group(ENTITY).vertex("C").build();

    private static final Entity ENTITY_D = new Entity.Builder().group(ENTITY).vertex("D").build();

    private final Walk walk = new Walk.Builder().edge(MapHandlerTest.EDGE_AB).entity(MapHandlerTest.ENTITY_B).edge(MapHandlerTest.EDGE_BC).entity(MapHandlerTest.ENTITY_C).edge(MapHandlerTest.EDGE_CA).build();

    private final Walk walk1 = new Walk.Builder().edge(MapHandlerTest.EDGE_CB).entities(MapHandlerTest.ENTITY_B).edge(MapHandlerTest.EDGE_BD).entities(MapHandlerTest.ENTITY_D).edge(MapHandlerTest.EDGE_DA).build();

    @Test
    public void shouldHandleNullOperation() {
        // Given
        final MapHandler<Integer, Integer> handler = new MapHandler();
        final Map<Integer, Integer> operation = null;
        // When / Then
        try {
            handler.doOperation(operation, context, store);
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains("Operation cannot be null"));
        }
    }

    @Test
    public void shouldHandleNullInput() {
        // Given
        final MapHandler<Integer, Integer> handler = new MapHandler();
        final Map<Integer, Integer> operation = new Map.Builder<Integer>().input(null).first(function).build();
        // When / Then
        try {
            handler.doOperation(operation, context, store);
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains("Input cannot be null"));
        }
    }

    @Test
    public void shouldHandleNullFunction() {
        // Given
        final MapHandler<Integer, Integer> handler = new MapHandler();
        function = null;
        final Map<Integer, Integer> operation = new Map.Builder<Integer>().input(input).first(function).build();
        // When / Then
        try {
            handler.doOperation(operation, context, store);
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains("Function cannot be null"));
        }
    }

    @Test
    public void shouldReturnItemFromOperationWithMockFunction() throws OperationException {
        // Given
        final MapHandler<Integer, Integer> handler = new MapHandler();
        final Map<Integer, Integer> operation = new Map.Builder<Integer>().input(input).first(function).build();
        // When
        final Integer result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertNotNull(result);
        Assert.assertEquals(new Integer(6), result);
    }

    @Test
    public void shouldMapSingleObject() throws OperationException {
        // Given
        final MapHandler<Integer, String> handler = new MapHandler();
        final Map<Integer, String> operation = new Map.Builder<Integer>().input(7).first(Object::toString).build();
        // When
        final String result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertNotNull(result);
        Assert.assertEquals("7", result);
    }

    @Test
    public void shouldMapMultipleObjectsAtOnce() throws OperationException {
        // Given
        final MapHandler<Iterable<Integer>, String> handler = new MapHandler();
        final Map<Iterable<Integer>, String> operation = new Map.Builder<Iterable<Integer>>().input(asList(1, 2)).first(Object::toString).build();
        // When
        final String result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertNotNull(result);
        Assert.assertEquals("[1, 2]", result);
    }

    @Test
    public void shouldMapMultipleObjects() throws OperationException {
        // Given
        final MapHandler<Iterable<Integer>, Iterable<String>> handler = new MapHandler();
        final Map<Iterable<Integer>, Iterable<String>> operation = new Map.Builder<Iterable<Integer>>().input(asList(1, 2)).first(new uk.gov.gchq.koryphe.impl.function.IterableFunction<Integer, String>(Object::toString)).build();
        // When
        final Iterable<String> result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertNotNull(result);
        Assert.assertEquals(asList("1", "2"), Lists.newArrayList(result));
    }

    @Test
    public void shouldExtractFirstItem() throws OperationException {
        // Given
        final MapHandler<Iterable<Iterable<Integer>>, Iterable<Integer>> handler = new MapHandler();
        final Map<Iterable<Iterable<Integer>>, Iterable<Integer>> operation = new Map.Builder<Iterable<Iterable<Integer>>>().input(asList(asList(1, 2), asList(3, 4))).first(new uk.gov.gchq.koryphe.impl.function.FirstItem()).build();
        // When
        final Iterable<Integer> result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertNotNull(result);
        Assert.assertEquals(asList(1, 2), Lists.newArrayList(result));
    }

    @Test
    public void shouldFlatMapMultipleObjects() throws OperationException {
        // Given
        final MapHandler<Iterable<Iterable<Integer>>, Iterable<Integer>> handler = new MapHandler();
        final Map<Iterable<Iterable<Integer>>, Iterable<Integer>> operation = new Map.Builder<Iterable<Iterable<Integer>>>().input(asList(asList(1, 2), asList(3, 4))).first(new uk.gov.gchq.koryphe.impl.function.IterableConcat()).build();
        // When
        final Iterable<Integer> result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertNotNull(result);
        Assert.assertEquals(asList(1, 2, 3, 4), Lists.newArrayList(result));
    }

    @Test
    public void shouldReturnIterableFromOperation() throws OperationException {
        // Given
        final Iterable<Iterable<Integer>> input = asList(asList(1, 2, 3), asList(4, 5, 6), asList(7, 8, 9));
        final MapHandler<Iterable<Iterable<Integer>>, String> handler = new MapHandler();
        final Map<Iterable<Iterable<Integer>>, String> operation = new Map.Builder<Iterable<Iterable<Integer>>>().input(input).first(new uk.gov.gchq.koryphe.impl.function.IterableFunction.Builder<Iterable<Integer>>().first(new uk.gov.gchq.koryphe.impl.function.NthItem(1)).then(Object::toString).build()).then(new uk.gov.gchq.koryphe.impl.function.NthItem(2)).build();
        // When
        final String results = handler.doOperation(operation, context, store);
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals("8", results);
    }

    @Test
    public void shouldProcessWalkObjects() throws OperationException {
        // Given
        final Iterable<Iterable<Set<Edge>>> walks = asList(walk, walk1);
        final Map<Iterable<Iterable<Set<Edge>>>, Iterable<Set<Edge>>> map = new Map.Builder<Iterable<Iterable<Set<Edge>>>>().input(walks).first(new uk.gov.gchq.koryphe.impl.function.IterableFunction.Builder<Iterable<Set<Edge>>>().first(new uk.gov.gchq.koryphe.impl.function.FirstItem()).build()).build();
        final MapHandler<Iterable<Iterable<Set<Edge>>>, Iterable<Set<Edge>>> handler = new MapHandler();
        // When
        final Iterable<Set<Edge>> results = handler.doOperation(map, context, store);
        final Iterable<Iterable<Edge>> expectedResults = asList(Sets.newHashSet(MapHandlerTest.EDGE_AB), Sets.newHashSet(MapHandlerTest.EDGE_CB));
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(expectedResults, Lists.newArrayList(results));
    }

    @Test
    public void shouldProcessWalksInOperationChain() throws OperationException {
        // Given
        final Iterable<Iterable<Set<Edge>>> walks = asList(walk, walk1);
        final Map<Iterable<Iterable<Set<Edge>>>, Iterable<Edge>> map = new Map.Builder<Iterable<Iterable<Set<Edge>>>>().input(walks).first(new uk.gov.gchq.koryphe.impl.function.IterableFunction.Builder<Iterable<Set<Edge>>>().first(new uk.gov.gchq.koryphe.impl.function.FirstItem()).then(new uk.gov.gchq.koryphe.impl.function.FirstItem()).build()).build();
        final ToVertices toVertices = new ToVertices.Builder().edgeVertices(SOURCE).build();
        final ToSet<Object> toSet = new ToSet();
        final OperationChain<Set<?>> opChain = new OperationChain.Builder().first(map).then(toVertices).then(toSet).build();
        final OperationChainValidator opChainValidator = Mockito.mock(OperationChainValidator.class);
        final List<OperationChainOptimiser> opChainOptimisers = Collections.emptyList();
        BDDMockito.given(opChainValidator.validate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(new ValidationResult());
        final OperationChainHandler<Set<?>> opChainHandler = new OperationChainHandler(opChainValidator, opChainOptimisers);
        BDDMockito.given(store.handleOperation(map, context)).willReturn(asList(MapHandlerTest.EDGE_AB, MapHandlerTest.EDGE_CB));
        BDDMockito.given(store.handleOperation(toVertices, context)).willReturn(asList("A", "C"));
        BDDMockito.given(store.handleOperation(toSet, context)).willReturn(Sets.newHashSet("A", "C"));
        // When
        final Iterable<?> results = opChainHandler.doOperation(opChain, context, store);
        // Then
        Assert.assertThat(results, Matchers.containsInAnyOrder("A", "C"));
    }

    @Test
    public void shouldProcessWalksWithEdgeExtraction() throws OperationException {
        // Given
        final Iterable<Walk> walks = asList(walk, walk1);
        final Map<Iterable<Walk>, Iterable<Edge>> map = new Map.Builder<Iterable<Walk>>().input(walks).first(new uk.gov.gchq.koryphe.impl.function.IterableFunction.Builder<Walk>().first(new ExtractWalkEdgesFromHop(1)).then(new uk.gov.gchq.koryphe.impl.function.FirstItem()).build()).build();
        final ToVertices toVertices = new ToVertices.Builder().edgeVertices(SOURCE).build();
        final ToSet<Object> toSet = new ToSet();
        final OperationChain<Set<?>> opChain = new OperationChain.Builder().first(map).then(toVertices).then(toSet).build();
        final OperationChainValidator opChainValidator = Mockito.mock(OperationChainValidator.class);
        final List<OperationChainOptimiser> opChainOptimisers = Collections.emptyList();
        BDDMockito.given(opChainValidator.validate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(new ValidationResult());
        final OperationChainHandler<Set<?>> opChainHandler = new OperationChainHandler(opChainValidator, opChainOptimisers);
        BDDMockito.given(store.handleOperation(map, context)).willReturn(asList(MapHandlerTest.EDGE_BC, MapHandlerTest.EDGE_BD));
        BDDMockito.given(store.handleOperation(toVertices, context)).willReturn(asList("B", "B"));
        BDDMockito.given(store.handleOperation(toSet, context)).willReturn(Sets.newHashSet("B", "B"));
        // When
        final Iterable<?> results = opChainHandler.doOperation(opChain, context, store);
        // Then
        Assert.assertThat(results, Matchers.contains("B"));
    }

    @Test
    public void shouldBuildWithInvalidArgumentsAndFailExecution() throws OperationException {
        final List<Function> functions = new ArrayList<>();
        final Function<Long, Double> func = Double::longBitsToDouble;
        final Function<String, Integer> func1 = Integer::valueOf;
        functions.add(new ToString());
        functions.add(func);
        functions.add(func1);
        final Map map = new Map();
        map.setInput(3);
        map.setFunctions(functions);
        final MapHandler handler = new MapHandler();
        // When / Then
        try {
            final Object result = handler.doOperation(map, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains("The input/output types of the functions were incompatible"));
        }
    }
}

