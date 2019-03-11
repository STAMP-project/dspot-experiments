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


import EdgeId.MatchedVertex.SOURCE;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import TestPropertyNames.INT;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import TestPropertyNames.PROP_3;
import TestPropertyNames.PROP_4;
import TestPropertyNames.STRING;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.function.ElementTransformer;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.function.Transform;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.koryphe.impl.function.Divide;
import uk.gov.gchq.koryphe.impl.function.Identity;


public class TransformHandlerTest {
    private List<Element> input;

    private List<Element> expected;

    private Store store;

    private Context context;

    private TransformHandler handler;

    private Schema schema;

    @Test
    public void shouldTransformElementsUsingMockFunction() throws OperationException {
        // Given
        final Function<String, Integer> function = Mockito.mock(Function.class);
        BDDMockito.given(function.apply(STRING)).willReturn(6);
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Edge edge = new Edge.Builder().group(EDGE).property(PROP_1, STRING).build();
        final Entity entity = new Entity.Builder().group(ENTITY).property(PROP_1, STRING).property(PROP_2, 3).build();
        final ElementTransformer transformer = new ElementTransformer.Builder().select(PROP_1).execute(function).project(PROP_3).build();
        final Edge expectedEdge = new Edge.Builder().group(EDGE).property(PROP_3, 6).build();
        final Entity expectedEntity = new Entity.Builder().group(ENTITY).property(PROP_3, 6).property(PROP_2, 3).build();
        final Transform transform = new Transform.Builder().input(input).edge(EDGE, transformer).entity(ENTITY, transformer).build();
        input.add(edge);
        input.add(entity);
        expected.add(expectedEdge);
        expected.add(expectedEntity);
        // When
        final Iterable<? extends Element> results = handler.doOperation(transform, context, store);
        final List<Element> resultsList = Lists.newArrayList(results);
        // Then
        boolean isSame = false;
        for (int i = 0; i < (resultsList.size()); i++) {
            isSame = expected.get(i).getProperty(PROP_3).equals(resultsList.get(i).getProperty(PROP_3));
        }
        Assert.assertTrue(isSame);
    }

    @Test
    public void shouldTransformElementsUsingIdentityFunction() throws OperationException {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Entity entity = new Entity.Builder().group(ENTITY).property(PROP_1, INT).property(PROP_2, STRING).build();
        final Entity entity1 = new Entity.Builder().group(ENTITY).property(PROP_1, INT).build();
        final ElementTransformer transformer = new ElementTransformer.Builder().select(PROP_1).execute(new Identity()).project(PROP_3).build();
        final Entity expectedEntity = new Entity.Builder().group(ENTITY).property(PROP_3, INT).property(PROP_2, STRING).build();
        final Entity expectedEntity1 = new Entity.Builder().group(ENTITY).property(PROP_3, INT).build();
        final Transform transform = new Transform.Builder().input(input).entity(ENTITY, transformer).build();
        input.add(entity);
        input.add(entity1);
        expected.add(expectedEntity);
        expected.add(expectedEntity1);
        // When
        final Iterable<? extends Element> results = handler.doOperation(transform, context, store);
        final List<Element> resultsList = Lists.newArrayList(results);
        // Then
        boolean isSame = false;
        for (int i = 0; i < (expected.size()); i++) {
            isSame = expected.get(i).getProperty(PROP_3).equals(resultsList.get(i).getProperty(PROP_3));
        }
        Assert.assertTrue(isSame);
    }

    @Test
    public void shouldTransformElementsUsingInlineFunction() throws OperationException {
        // Given
        final Function<String, Integer> function = String::length;
        final Map<String, ElementTransformer> entities = new HashMap<>();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Entity entity = new Entity.Builder().group(ENTITY).property(PROP_1, "value").property(PROP_2, 1).build();
        final Entity entity1 = new Entity.Builder().group(ENTITY_2).property(PROP_1, "otherValue").property(PROP_2, 3).build();
        final ElementTransformer transformer = new ElementTransformer.Builder().select(PROP_1).execute(function).project(PROP_3).build();
        entities.put(ENTITY, transformer);
        entities.put(ENTITY_2, transformer);
        final Entity expectedEntity = new Entity.Builder().group(ENTITY).property(PROP_3, 5).property(PROP_2, 1).build();
        final Entity expectedEntity1 = new Entity.Builder().group(ENTITY_2).property(PROP_3, 10).property(PROP_2, 3).build();
        final Transform transform = new Transform.Builder().input(input).entities(entities).build();
        input.add(entity);
        input.add(entity1);
        expected.add(expectedEntity);
        expected.add(expectedEntity1);
        // When
        final Iterable<? extends Element> results = handler.doOperation(transform, context, store);
        final List<Element> resultsList = Lists.newArrayList(results);
        // Then
        boolean isSame = false;
        for (int i = 0; i < (resultsList.size()); i++) {
            isSame = expected.get(i).getProperty(PROP_3).equals(resultsList.get(i).getProperty(PROP_3));
        }
        Assert.assertTrue(isSame);
    }

    @Test
    public void shouldApplyDifferentTransformersToDifferentGroups() throws OperationException {
        // Given
        final Function<String, Integer> function = String::length;
        final Function<String, String> function1 = String::toUpperCase;
        final Map<String, ElementTransformer> edges = new HashMap<>();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Edge edge = new Edge.Builder().group(EDGE).property(PROP_1, "testValue").property(PROP_2, 5).build();
        final Edge edge1 = new Edge.Builder().group(EDGE_2).property(PROP_2, "otherValue").property(PROP_3, 2L).build();
        final ElementTransformer elementTransformer = new ElementTransformer.Builder().select(PROP_1).execute(function).project(PROP_4).build();
        final ElementTransformer elementTransformer1 = new ElementTransformer.Builder().select(PROP_2).execute(function1).project(PROP_4).build();
        final Edge expectedEdge = new Edge.Builder().group(EDGE).property(PROP_4, 9).property(PROP_2, 5).build();
        final Edge expectedEdge1 = new Edge.Builder().group(EDGE_2).property(PROP_4, "OTHERVALUE").property(PROP_3, 2L).build();
        edges.put(EDGE, elementTransformer);
        edges.put(EDGE_2, elementTransformer1);
        input.add(edge);
        input.add(edge1);
        expected.add(expectedEdge);
        expected.add(expectedEdge1);
        final Transform transform = new Transform.Builder().input(input).edges(edges).build();
        // When
        final Iterable<? extends Element> results = handler.doOperation(transform, context, store);
        final List<Element> resultsList = Lists.newArrayList(results);
        // Then
        boolean isSame = false;
        for (int i = 0; i < (resultsList.size()); i++) {
            isSame = expected.get(i).getProperty(PROP_4).equals(resultsList.get(i).getProperty(PROP_4));
        }
        Assert.assertTrue(isSame);
    }

    @Test
    public void shouldFailValidationWhenSchemaElementDefinitionsAreNull() {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(new Schema());
        final Entity entity = new Entity.Builder().group(ENTITY).property(PROP_1, INT).property(PROP_2, STRING).build();
        final Entity entity1 = new Entity.Builder().group(ENTITY).property(PROP_1, INT).build();
        final ElementTransformer transformer = new ElementTransformer.Builder().select(PROP_1).execute(new Identity()).project(PROP_3).build();
        input.add(entity);
        input.add(entity1);
        final Transform transform = new Transform.Builder().input(input).entity(ENTITY, transformer).build();
        // When / Then
        try {
            final Iterable<? extends Element> results = handler.doOperation(transform, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains((("Entity group: " + (TestGroups.ENTITY)) + " does not exist in the schema.")));
        }
    }

    @Test
    public void shouldFailValidationWhenElementTransformerOperationIsNull() {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Entity entity = new Entity.Builder().group(ENTITY).property(PROP_1, INT).property(PROP_2, STRING).build();
        final Entity entity1 = new Entity.Builder().group(ENTITY).property(PROP_1, INT).build();
        final ElementTransformer transformer = new ElementTransformer.Builder().select(PROP_1).execute(null).project(PROP_3).build();
        input.add(entity);
        input.add(entity1);
        final Transform transform = new Transform.Builder().input(input).entity(ENTITY, transformer).build();
        // When / Then
        try {
            final Iterable<? extends Element> results = handler.doOperation(transform, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains(((transformer.getClass().getSimpleName()) + " contains a null function.")));
        }
    }

    @Test
    public void shouldFailValidationWhenFunctionSignatureIsInvalid() {
        // Given
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, STRING).build()).type(STRING, new TypeDefinition(String.class)).build();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Entity entity = new Entity.Builder().group(ENTITY).property(PROP_1, INT).property(PROP_2, STRING).build();
        final Entity entity1 = new Entity.Builder().group(ENTITY).property(PROP_1, INT).build();
        final ElementTransformer transformer = new ElementTransformer.Builder().select(PROP_1).execute(new Divide()).project(PROP_3).build();
        input.add(entity);
        input.add(entity1);
        final Transform transform = new Transform.Builder().input(input).entity(ENTITY, transformer).build();
        // When / Then
        try {
            final Iterable<? extends Element> results = handler.doOperation(transform, context, store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("Incompatible number of types"));
        }
    }

    @Test
    public void shouldSelectMatchedVertexForTransform() throws OperationException {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Edge edge = new Edge.Builder().group(EDGE).source("srcVal").dest("destVal").matchedVertex(SOURCE).build();
        final Transform transform = new Transform.Builder().input(edge).edge(EDGE, new ElementTransformer.Builder().select(IdentifierType.MATCHED_VERTEX.name()).execute(new Identity()).project(PROP_3).build()).build();
        // When
        final Iterable<? extends Element> results = handler.doOperation(transform, context, store);
        // Then
        final Edge expectedEdge = new Edge.Builder().group(EDGE).source("srcVal").dest("destVal").matchedVertex(SOURCE).property(PROP_3, "srcVal").build();
        ElementUtil.assertElementEquals(Collections.singletonList(expectedEdge), results);
    }

    @Test
    public void shouldSelectAdjacentMatchedVertexForTransform() throws OperationException {
        // Given
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final Edge edge = new Edge.Builder().group(EDGE).source("srcVal").dest("destVal").matchedVertex(SOURCE).build();
        final Transform transform = new Transform.Builder().input(edge).edge(EDGE, new ElementTransformer.Builder().select(IdentifierType.ADJACENT_MATCHED_VERTEX.name()).execute(new Identity()).project(PROP_3).build()).build();
        // When
        final Iterable<? extends Element> results = handler.doOperation(transform, context, store);
        // Then
        final Edge expectedEdge = new Edge.Builder().group(EDGE).source("srcVal").dest("destVal").matchedVertex(SOURCE).property(PROP_3, "destVal").build();
        ElementUtil.assertElementEquals(Collections.singletonList(expectedEdge), results);
    }
}

