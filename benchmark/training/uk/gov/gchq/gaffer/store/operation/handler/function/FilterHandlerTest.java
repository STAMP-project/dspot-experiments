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
package uk.gov.gchq.gaffer.store.operation.handler.function;


import EdgeId.MatchedVertex.DESTINATION;
import EdgeId.MatchedVertex.SOURCE;
import IdentifierType.ADJACENT_MATCHED_VERTEX;
import IdentifierType.MATCHED_VERTEX;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.EDGE_3;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import TestGroups.ENTITY_3;
import TestPropertyNames.COUNT;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.commonutil.stream.Streams;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.function.Filter;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.koryphe.impl.predicate.IsIn;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;


public class FilterHandlerTest {
    private static final Schema SCHEMA = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition()).edge(EDGE_2, new SchemaEdgeDefinition()).entity(ENTITY, new SchemaEntityDefinition()).entity(ENTITY_2, new SchemaEntityDefinition()).build();

    private List<Element> input;

    private List<Element> expected;

    private Store store;

    private Context context;

    private FilterHandler handler;

    @Test
    public void shouldFilterByGroup() throws OperationException {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(FilterHandlerTest.SCHEMA);
        final Edge edge = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 2L).build();
        final Edge edge1 = new Edge.Builder().group(EDGE_2).source("junctionA").dest("junctionB").directed(true).property(COUNT, 1L).build();
        final Edge edge2 = new Edge.Builder().group(EDGE).source("junctionB").dest("junctionA").directed(true).property(COUNT, 4L).build();
        final Edge edge3 = new Edge.Builder().group(EDGE_3).source("junctionC").dest("junctionB").directed(true).property(COUNT, 3L).build();
        input.add(edge);
        input.add(edge1);
        input.add(edge2);
        input.add(edge3);
        expected.add(edge1);
        final Filter filter = new Filter.Builder().input(input).edge(EDGE_2).build();
        // When
        final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
        // Then
        final List<Element> resultsList = Lists.newArrayList(results);
        Assert.assertEquals(expected, resultsList);
    }

    @Test
    public void shouldFilterInputBasedOnGroupAndCount() throws OperationException {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(FilterHandlerTest.SCHEMA);
        final Edge edge = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 2L).build();
        final Edge edge1 = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 1L).build();
        final Edge edge2 = new Edge.Builder().group(EDGE).source("junctionB").dest("junctionA").directed(true).property(COUNT, 4L).build();
        final Edge edge3 = new Edge.Builder().group(EDGE_2).source("junctionC").dest("junctionD").directed(true).property(COUNT, 3L).build();
        input.add(edge);
        input.add(edge1);
        input.add(edge2);
        input.add(edge3);
        expected.add(edge);
        expected.add(edge2);
        final Filter filter = new Filter.Builder().input(input).edge(EDGE, new ElementFilter.Builder().select(COUNT).execute(new IsMoreThan(1L)).build()).build();
        // When
        final Iterable<? extends Element> result = handler.doOperation(filter, context, store);
        // Then
        final List<Element> resultList = Streams.toStream(result).collect(Collectors.toList());
        Assert.assertEquals(expected, resultList);
    }

    @Test
    public void shouldReturnAllValuesWithNullElementFilters() throws OperationException {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(FilterHandlerTest.SCHEMA);
        final Edge edge = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 2L).build();
        final Edge edge1 = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 1L).build();
        final Edge edge2 = new Edge.Builder().group(EDGE).source("junctionB").dest("junctionA").directed(true).property(COUNT, 4L).build();
        input.add(edge);
        input.add(edge1);
        input.add(edge2);
        expected.add(edge);
        expected.add(edge1);
        expected.add(edge2);
        final Filter filter = new Filter.Builder().input(input).build();
        // When
        final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
        // Then
        final List<Element> resultsList = Lists.newArrayList(results);
        Assert.assertEquals(expected, resultsList);
    }

    @Test
    public void shouldApplyGlobalFilterAndReturnOnlySpecifiedEdges() throws OperationException {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(FilterHandlerTest.SCHEMA);
        final Edge edge = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 2L).build();
        final Edge edge1 = new Edge.Builder().group(EDGE_2).source("junctionA").dest("junctionB").directed(true).property(COUNT, 1L).build();
        final Edge edge2 = new Edge.Builder().group(EDGE_2).source("junctionB").dest("junctionA").directed(true).property(COUNT, 4L).build();
        final Entity entity = new Entity.Builder().group(ENTITY).property(COUNT, 3L).build();
        final Entity entity1 = new Entity.Builder().group(ENTITY_2).property(COUNT, 4L).build();
        input.add(edge);
        input.add(edge1);
        input.add(edge2);
        input.add(entity);
        input.add(entity1);
        expected.add(edge2);
        final Filter filter = new Filter.Builder().input(input).globalElements(new ElementFilter.Builder().select(COUNT).execute(new IsMoreThan(2L)).build()).edge(EDGE_2).build();
        // When
        final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
        // Then
        final List<Element> resultsList = Lists.newArrayList(results);
        Assert.assertEquals(expected, resultsList);
    }

    @Test
    public void shouldFilterEntitiesAndEdges() throws OperationException {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(FilterHandlerTest.SCHEMA);
        final Edge edge = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 2L).build();
        final Edge edge1 = new Edge.Builder().group(EDGE_2).source("junctionA").dest("junctionB").directed(true).property(COUNT, 1L).build();
        final Edge edge2 = new Edge.Builder().group(EDGE).source("junctionB").dest("junctionA").directed(true).property(COUNT, 4L).build();
        final Entity entity = new Entity.Builder().group(ENTITY).property(COUNT, 3L).build();
        final Entity entity1 = new Entity.Builder().group(ENTITY_2).property(COUNT, 4L).build();
        input.add(edge);
        input.add(edge1);
        input.add(edge2);
        input.add(entity);
        input.add(entity1);
        expected.add(edge2);
        expected.add(entity);
        expected.add(entity1);
        final Filter filter = new Filter.Builder().input(input).globalElements(new ElementFilter.Builder().select(COUNT).execute(new IsMoreThan(2L)).build()).build();
        // When
        final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
        // Then
        final List<Element> resultsList = Lists.newArrayList(results);
        Assert.assertEquals(expected, resultsList);
    }

    @Test
    public void shouldHandleComplexFiltering() throws OperationException {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(FilterHandlerTest.SCHEMA);
        final Edge edge = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 2L).build();
        final Edge edge1 = new Edge.Builder().group(EDGE_2).source("junctionA").dest("junctionB").directed(true).property(COUNT, 1L).build();
        final Edge edge2 = new Edge.Builder().group(EDGE).source("junctionB").dest("junctionA").directed(true).property(COUNT, 4L).build();
        final Entity entity = new Entity.Builder().group(ENTITY).property(COUNT, 3L).build();
        final Entity entity1 = new Entity.Builder().group(ENTITY_2).property(COUNT, 4L).build();
        final Entity entity2 = new Entity.Builder().group(ENTITY_3).property(COUNT, 6L).build();
        final Filter filter = new Filter.Builder().input(input).globalElements(new ElementFilter.Builder().select(COUNT).execute(new IsMoreThan(1L)).build()).edge(EDGE, new ElementFilter.Builder().select(COUNT).execute(new IsMoreThan(2L)).build()).entity(ENTITY_2).build();
        input.add(edge);
        input.add(edge1);
        input.add(edge2);
        input.add(entity);
        input.add(entity1);
        input.add(entity2);
        expected.add(edge2);
        expected.add(entity1);
        // When
        final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
        // Then
        final List<Element> resultsList = Lists.newArrayList(results);
        Assert.assertEquals(expected, resultsList);
    }

    @Test
    public void shouldThrowErrorForNullInput() {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(FilterHandlerTest.SCHEMA);
        final Filter filter = new Filter.Builder().globalElements(new ElementFilter()).build();
        // When / Then
        try {
            handler.doOperation(filter, context, store);
            Assert.fail("Exception expected");
        } catch (OperationException e) {
            Assert.assertEquals("Filter operation has null iterable of elements", e.getMessage());
        }
    }

    @Test
    public void shouldReturnNoResultsWhenGlobalElementsFails() throws OperationException {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(FilterHandlerTest.SCHEMA);
        final Edge edge = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 2L).build();
        final Edge edge1 = new Edge.Builder().group(EDGE_2).source("junctionA").dest("junctionB").directed(true).property(COUNT, 1L).build();
        final Edge edge2 = new Edge.Builder().group(EDGE).source("junctionB").dest("junctionA").directed(true).property(COUNT, 4L).build();
        final Entity entity = new Entity.Builder().group(ENTITY).property(COUNT, 3L).build();
        final Entity entity1 = new Entity.Builder().group(ENTITY_2).property(COUNT, 4L).build();
        final Entity entity2 = new Entity.Builder().group(ENTITY_3).property(COUNT, 6L).build();
        input.add(edge);
        input.add(edge1);
        input.add(edge2);
        input.add(entity);
        input.add(entity1);
        input.add(entity2);
        final Filter filter = new Filter.Builder().input(input).globalElements(new ElementFilter.Builder().select(COUNT).execute(new IsMoreThan(10L)).build()).globalEdges(new ElementFilter.Builder().select(COUNT).execute(new IsMoreThan(2L)).build()).build();
        // When
        final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
        // Then
        final List<Element> resultsList = Lists.newArrayList(results);
        Assert.assertEquals(expected, resultsList);
    }

    @Test
    public void shouldFailValidationWhenSchemaElementDefinitionsAreNull() {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        final Edge edge = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 2L).build();
        final Edge edge1 = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 1L).build();
        input.add(edge);
        input.add(edge1);
        final Filter filter = new Filter.Builder().input(input).edge(EDGE).build();
        // When / Then
        try {
            final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains((("Edge group: " + (TestGroups.EDGE)) + " does not exist in the schema")));
        }
    }

    @Test
    public void shouldFailValidationWhenElementFilterOperationIsNull() {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(FilterHandlerTest.SCHEMA);
        final Edge edge = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 2L).build();
        final Edge edge1 = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").directed(true).property(COUNT, 1L).build();
        input.add(edge);
        input.add(edge1);
        final Filter filter = new Filter.Builder().input(input).edge(EDGE, new ElementFilter.Builder().select(COUNT).execute(null).build()).build();
        try {
            final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains(((filter.getClass().getSimpleName()) + " contains a null function.")));
        }
    }

    @Test
    public void shouldFailValidationWhenTypeArgumentOfPredicateIsIncorrect() {
        // Given
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().source("junctionA").destination("junctionB").property(COUNT, "count.long").build()).edge(EDGE, new SchemaEdgeDefinition.Builder().source("junctionA").destination("junctionB").property(COUNT, "count.long").build()).type("count.long", new TypeDefinition(Long.class)).build();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Edge edge = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").property(COUNT, 2L).build();
        final Edge edge1 = new Edge.Builder().group(EDGE).source("junctionA").dest("junctionB").property(COUNT, 1L).build();
        input.add(edge);
        input.add(edge1);
        final Filter filter = new Filter.Builder().input(input).globalEdges(new ElementFilter.Builder().select(COUNT).execute(new IsMoreThan("abcd")).build()).build();
        try {
            final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains("is not compatible with the input type:"));
        }
    }

    @Test
    public void shouldFilterBasedOnMatchedVertex() throws OperationException {
        // Given
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().source("vertex").destination("vertex").property(COUNT, "count.long").build()).type("vertex", new TypeDefinition(String.class)).type("count.long", new TypeDefinition(Long.class)).build();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Filter filter = new Filter.Builder().input(new Edge.Builder().group(EDGE).source("srcVal1").dest("destVal1").matchedVertex(SOURCE).build(), new Edge.Builder().group(EDGE).source("srcVal2").dest("destVal2").matchedVertex(SOURCE).build(), new Edge.Builder().group(EDGE).source("srcVal3").dest("destVal3").matchedVertex(DESTINATION).build(), new Edge.Builder().group(EDGE).source("srcVal4").dest("destVal4").matchedVertex(DESTINATION).build()).edge(EDGE, new ElementFilter.Builder().select(MATCHED_VERTEX.name()).execute(new IsIn("srcVal1", "destVal3")).build()).build();
        // When
        final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
        // Then
        ElementUtil.assertElementEquals(Arrays.asList(new Edge.Builder().group(EDGE).source("srcVal1").dest("destVal1").matchedVertex(SOURCE).build(), new Edge.Builder().group(EDGE).source("srcVal3").dest("destVal3").matchedVertex(SOURCE).build()), results);
    }

    @Test
    public void shouldFilterBasedOnAdjacentMatchedVertex() throws OperationException {
        // Given
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().source("vertex").destination("vertex").property(COUNT, "count.long").build()).type("vertex", new TypeDefinition(String.class)).type("count.long", new TypeDefinition(Long.class)).build();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Filter filter = new Filter.Builder().input(new Edge.Builder().group(EDGE).source("srcVal1").dest("destVal1").matchedVertex(SOURCE).build(), new Edge.Builder().group(EDGE).source("srcVal2").dest("destVal2").matchedVertex(SOURCE).build(), new Edge.Builder().group(EDGE).source("srcVal3").dest("destVal3").matchedVertex(DESTINATION).build(), new Edge.Builder().group(EDGE).source("srcVal4").dest("destVal4").matchedVertex(DESTINATION).build()).edge(EDGE, new ElementFilter.Builder().select(ADJACENT_MATCHED_VERTEX.name()).execute(new IsIn("destVal1", "srcVal3")).build()).build();
        // When
        final Iterable<? extends Element> results = handler.doOperation(filter, context, store);
        // Then
        ElementUtil.assertElementEquals(Arrays.asList(new Edge.Builder().group(EDGE).source("srcVal1").dest("destVal1").matchedVertex(SOURCE).build(), new Edge.Builder().group(EDGE).source("srcVal3").dest("destVal3").matchedVertex(SOURCE).build()), results);
    }
}

