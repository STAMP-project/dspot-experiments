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
package uk.gov.gchq.gaffer.store.schema;


import IdentifierType.DESTINATION;
import IdentifierType.DIRECTED;
import IdentifierType.SOURCE;
import IdentifierType.VERTEX;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.function.ElementAggregator;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.elementdefinition.exception.SchemaException;
import uk.gov.gchq.gaffer.function.ExampleAggregateFunction;
import uk.gov.gchq.gaffer.function.ExampleFilterFunction;
import uk.gov.gchq.gaffer.function.ExampleTuple2BinaryOperator;
import uk.gov.gchq.koryphe.impl.binaryoperator.StringConcat;
import uk.gov.gchq.koryphe.impl.predicate.Exists;
import uk.gov.gchq.koryphe.impl.predicate.IsXMoreThanY;


public abstract class SchemaElementDefinitionTest<T extends SchemaElementDefinition> {
    public static final String PROPERTY_STRING_TYPE = "property.string";

    @Test
    public void shouldNotBeAbleToAddPropertiesOnceBuilt() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().build();
        // When / Then
        try {
            getPropertyMap().put("new property", "string");
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void shouldNotBeAbleToAddIdentifiersOnceBuilt() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().build();
        // When / Then
        try {
            getIdentifierMap().put(SOURCE, "string");
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void shouldNotBeAbleToModifyGroupByOnceBuilt() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).build();
        // When / Then
        try {
            getGroupBy().add("property");
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void shouldNotBeAbleToModifyParentsOnceBuilt() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).parents("parentGroup1").build();
        // When / Then
        try {
            getParents().add("parentGroup2");
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void shouldReturnValidatorWithNoFunctionsWhenNoVerticesOrProperties() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createEmptyBuilder().build();
        // When
        final ElementFilter validator = elementDef.getValidator();
        // Then
        Assert.assertEquals(0, validator.getComponents().size());
        // Check the validator is cached
        Assert.assertSame(validator, elementDef.getValidator());
    }

    @Test
    public void shouldReturnFullValidator() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).validator(new ElementFilter.Builder().select("property1", "property2").execute(new IsXMoreThanY()).build()).build();
        setupSchema(elementDef);
        // When
        final ElementFilter validator = elementDef.getValidator();
        // Then
        int i = 0;
        Assert.assertEquals(IsXMoreThanY.class, validator.getComponents().get(i).getPredicate().getClass());
        Assert.assertArrayEquals(new String[]{ "property1", "property2" }, validator.getComponents().get(i).getSelection());
        i++;
        if (elementDef instanceof SchemaEdgeDefinition) {
            Assert.assertEquals(Integer.class.getName(), getType());
            Assert.assertArrayEquals(new String[]{ SOURCE.name() }, validator.getComponents().get(i).getSelection());
            i++;
            Assert.assertEquals(Date.class.getName(), getType());
            Assert.assertArrayEquals(new String[]{ DESTINATION.name() }, validator.getComponents().get(i).getSelection());
            i++;
            Assert.assertEquals(Boolean.class.getName(), getType());
            Assert.assertArrayEquals(new String[]{ DIRECTED.name() }, validator.getComponents().get(i).getSelection());
            i++;
        } else {
            Assert.assertArrayEquals(new String[]{ VERTEX.name() }, validator.getComponents().get(i).getSelection());
            i++;
        }
        Assert.assertEquals(String.class.getName(), getType());
        Assert.assertArrayEquals(new String[]{ "property" }, validator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(Exists.class, validator.getComponents().get(i).getPredicate().getClass());
        Assert.assertArrayEquals(new String[]{ "property" }, validator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(String.class.getName(), getType());
        Assert.assertArrayEquals(new String[]{ "property1" }, validator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(Exists.class, validator.getComponents().get(i).getPredicate().getClass());
        Assert.assertArrayEquals(new String[]{ "property1" }, validator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(String.class.getName(), getType());
        Assert.assertArrayEquals(new String[]{ "property2" }, validator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(Exists.class, validator.getComponents().get(i).getPredicate().getClass());
        Assert.assertArrayEquals(new String[]{ "property2" }, validator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(i, validator.getComponents().size());
        // Check the validator is cached
        Assert.assertSame(validator, elementDef.getValidator());
        Assert.assertNotSame(validator, getValidator(false));
    }

    @Test
    public void shouldReturnFullValidatorWithoutIsA() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).validator(new ElementFilter.Builder().select("property1", "property2").execute(new IsXMoreThanY()).build()).build();
        setupSchema(elementDef);
        // When
        final ElementFilter validator = elementDef.getValidator(false);
        // Then
        int i = 0;
        Assert.assertEquals(IsXMoreThanY.class, validator.getComponents().get(i).getPredicate().getClass());
        Assert.assertArrayEquals(new String[]{ "property1", "property2" }, validator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(Exists.class, validator.getComponents().get(i).getPredicate().getClass());
        Assert.assertArrayEquals(new String[]{ "property" }, validator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(Exists.class, validator.getComponents().get(i).getPredicate().getClass());
        Assert.assertArrayEquals(new String[]{ "property1" }, validator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(Exists.class, validator.getComponents().get(i).getPredicate().getClass());
        Assert.assertArrayEquals(new String[]{ "property2" }, validator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(i, validator.getComponents().size());
        // Check the validator is cached
        // Check the validator is cached
        Assert.assertSame(validator, getValidator(false));
        Assert.assertNotSame(validator, getValidator(true));
    }

    @Test
    public void shouldBuildElementDefinition() {
        // Given
        final ElementFilter validator = Mockito.mock(ElementFilter.class);
        // When
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property(PROP_1, "property.integer").property(PROP_2, "property.object").validator(validator).build();
        setupSchema(elementDef);
        // Then
        Assert.assertEquals(2, getProperties().size());
        Assert.assertEquals(Integer.class, elementDef.getPropertyClass(PROP_1));
        Assert.assertEquals(Object.class, elementDef.getPropertyClass(PROP_2));
        Assert.assertSame(validator, getOriginalValidator());
    }

    @Test
    public void shouldReturnFullAggregator() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property3", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("visibility", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("timestamp", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).groupBy("property1", "property2").build();
        setupSchema(elementDef);
        // When
        final ElementAggregator aggregator = getFullAggregator();
        // Then
        Assert.assertEquals(5, aggregator.getComponents().size());
        Assert.assertTrue(((aggregator.getComponents().get(0).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property1" }, aggregator.getComponents().get(0).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(1).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property2" }, aggregator.getComponents().get(1).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(2).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property3" }, aggregator.getComponents().get(2).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(3).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "visibility" }, aggregator.getComponents().get(3).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(4).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "timestamp" }, aggregator.getComponents().get(4).getSelection());
        // Check the aggregator is locked.
        try {
            aggregator.getComponents().add(null);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
        // check the aggregator is cached
        Assert.assertSame(aggregator, elementDef.getFullAggregator());
    }

    @Test
    public void shouldReturnFullAggregatorWithMultiPropertyAggregator() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property3", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property4", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property5", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("visibility", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("timestamp", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).groupBy("property1", "property2").aggregator(new ElementAggregator.Builder().select("property1", "property2").execute(new ExampleTuple2BinaryOperator()).build()).build();
        setupSchema(elementDef);
        // When
        final ElementAggregator aggregator = getFullAggregator();
        // Then
        Assert.assertEquals(6, aggregator.getComponents().size());
        Assert.assertTrue(((aggregator.getComponents().get(0).getBinaryOperator()) instanceof ExampleTuple2BinaryOperator));
        Assert.assertArrayEquals(new String[]{ "property1", "property2" }, aggregator.getComponents().get(0).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(1).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property3" }, aggregator.getComponents().get(1).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(2).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property4" }, aggregator.getComponents().get(2).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(3).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property5" }, aggregator.getComponents().get(3).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(4).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "visibility" }, aggregator.getComponents().get(4).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(5).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "timestamp" }, aggregator.getComponents().get(5).getSelection());
        // Check the aggregator is locked.
        try {
            aggregator.getComponents().add(null);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
        // check the aggregator is cached
        Assert.assertSame(aggregator, elementDef.getFullAggregator());
    }

    @Test
    public void shouldReturnIngestAggregator() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property3", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("visibility", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("timestamp", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).groupBy("property1", "property2").build();
        setupSchema(elementDef);
        // When
        final ElementAggregator aggregator = getIngestAggregator();
        // Then
        Assert.assertEquals(2, aggregator.getComponents().size());
        Assert.assertTrue(((aggregator.getComponents().get(0).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property3" }, aggregator.getComponents().get(0).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(1).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "timestamp" }, aggregator.getComponents().get(1).getSelection());
        // Check the aggregator is locked.
        try {
            aggregator.getComponents().add(null);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
        // check the aggregator is cached
        Assert.assertSame(aggregator, elementDef.getIngestAggregator());
    }

    @Test
    public void shouldReturnIngestAggregatorWithMultiPropertyAggregator() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property3", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property4", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property5", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("visibility", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("timestamp", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).groupBy("property1", "property2").aggregator(new ElementAggregator.Builder().select("property1", "property2").execute(new ExampleTuple2BinaryOperator()).select("property3", "timestamp").execute(new ExampleTuple2BinaryOperator()).build()).build();
        setupSchema(elementDef);
        // When
        final ElementAggregator aggregator = getIngestAggregator();
        // Then
        int i = 0;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleTuple2BinaryOperator));
        Assert.assertArrayEquals(new String[]{ "property3", "timestamp" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property4" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property5" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(i, aggregator.getComponents().size());
        // Check the aggregator is locked.
        try {
            aggregator.getComponents().add(null);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
        // check the aggregator is cached
        Assert.assertSame(aggregator, elementDef.getIngestAggregator());
    }

    @Test
    public void shouldReturnQueryAggregator() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property3", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("visibility", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("timestamp", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).groupBy("property1", "property2").build();
        setupSchema(elementDef);
        // When
        final ElementAggregator aggregator = getQueryAggregator(Sets.newHashSet("property1"), null);
        // Then
        Assert.assertEquals(4, aggregator.getComponents().size());
        Assert.assertTrue(((aggregator.getComponents().get(0).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property2" }, aggregator.getComponents().get(0).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(1).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property3" }, aggregator.getComponents().get(1).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(2).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "visibility" }, aggregator.getComponents().get(2).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(3).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "timestamp" }, aggregator.getComponents().get(3).getSelection());
        // Check the aggregator is locked.
        try {
            aggregator.getComponents().add(null);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
        // check the aggregator is cached
        Assert.assertSame(aggregator, elementDef.getQueryAggregator(Sets.newHashSet("property1"), null));
        // check a different aggregator is returned for different groupBys
        Assert.assertNotSame(aggregator, elementDef.getQueryAggregator(Sets.newHashSet(), null));
    }

    @Test
    public void shouldReturnQueryAggregatorWithMultiPropertyAggregator() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property3", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property4", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property5", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("visibility", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("timestamp", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).groupBy("property1", "property2").aggregator(new ElementAggregator.Builder().select("property1", "property2").execute(new ExampleTuple2BinaryOperator()).select("property3", "property4").execute(new ExampleTuple2BinaryOperator()).build()).build();
        setupSchema(elementDef);
        // When
        final ElementAggregator aggregator = getQueryAggregator(Sets.newHashSet(), null);
        // Then
        Assert.assertEquals(5, aggregator.getComponents().size());
        Assert.assertTrue(((aggregator.getComponents().get(0).getBinaryOperator()) instanceof ExampleTuple2BinaryOperator));
        Assert.assertArrayEquals(new String[]{ "property1", "property2" }, aggregator.getComponents().get(0).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(1).getBinaryOperator()) instanceof ExampleTuple2BinaryOperator));
        Assert.assertArrayEquals(new String[]{ "property3", "property4" }, aggregator.getComponents().get(1).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(2).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property5" }, aggregator.getComponents().get(2).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(3).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "visibility" }, aggregator.getComponents().get(3).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(4).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "timestamp" }, aggregator.getComponents().get(4).getSelection());
        // Check the aggregator is locked.
        try {
            aggregator.getComponents().add(null);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
        // check the aggregator is cached
        Assert.assertSame(aggregator, elementDef.getQueryAggregator(Sets.newHashSet(), null));
    }

    @Test
    public void shouldReturnQueryAggregatorWithViewAggregator() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property3", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property4", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property5", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("visibility", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("timestamp", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).groupBy("property1", "property2").aggregator(new ElementAggregator.Builder().select("property3", "property4").execute(new ExampleTuple2BinaryOperator()).build()).build();
        setupSchema(elementDef);
        final ElementAggregator viewAggregator = new ElementAggregator.Builder().select("property1").execute(new StringConcat()).build();
        // When
        final ElementAggregator aggregator = elementDef.getQueryAggregator(Sets.newHashSet(), viewAggregator);
        // Then
        int i = 0;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof StringConcat));
        Assert.assertArrayEquals(new String[]{ "property1" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleTuple2BinaryOperator));
        Assert.assertArrayEquals(new String[]{ "property3", "property4" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property2" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property5" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "visibility" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "timestamp" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(i, aggregator.getComponents().size());
        // Check the aggregator is locked.
        try {
            aggregator.getComponents().add(null);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
        // check the aggregator is not cached
        Assert.assertNotSame(aggregator, elementDef.getQueryAggregator(Sets.newHashSet(), viewAggregator));
    }

    @Test
    public void shouldReturnQueryAggregatorWithViewAggregatorAndMultipleAgg() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property3", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property4", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property5", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("visibility", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("timestamp", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).groupBy("property1", "property2").aggregator(new ElementAggregator.Builder().select("property1", "property2").execute(new ExampleTuple2BinaryOperator()).select("property3", "property4").execute(new ExampleTuple2BinaryOperator()).build()).build();
        setupSchema(elementDef);
        final ElementAggregator viewAggregator = new ElementAggregator.Builder().select("property1").execute(new StringConcat()).build();
        // When
        final ElementAggregator aggregator = elementDef.getQueryAggregator(Sets.newHashSet(), viewAggregator);
        // Then
        int i = 0;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof StringConcat));
        Assert.assertArrayEquals(new String[]{ "property1" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleTuple2BinaryOperator));
        Assert.assertArrayEquals(new String[]{ "property1", "property2" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleTuple2BinaryOperator));
        Assert.assertArrayEquals(new String[]{ "property3", "property4" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property5" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "visibility" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertTrue(((aggregator.getComponents().get(i).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "timestamp" }, aggregator.getComponents().get(i).getSelection());
        i++;
        Assert.assertEquals(i, aggregator.getComponents().size());
        // Check the aggregator is locked.
        try {
            aggregator.getComponents().add(null);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
        // check the aggregator is not cached
        Assert.assertNotSame(aggregator, elementDef.getQueryAggregator(Sets.newHashSet(), viewAggregator));
    }

    @Test
    public void shouldReturnQueryAggregatorWithMultiPropertyAggregatorWithSingleGroupBy() {
        // Given
        final T elementDef = SchemaElementDefinitionTest.createBuilder().property("property1", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property2", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property3", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property4", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("property5", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("visibility", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).property("timestamp", SchemaElementDefinitionTest.PROPERTY_STRING_TYPE).groupBy("property1").aggregator(new ElementAggregator.Builder().select("property1", "property2").execute(new ExampleTuple2BinaryOperator()).select("property3", "property4").execute(new ExampleTuple2BinaryOperator()).build()).build();
        setupSchema(elementDef);
        // When
        final ElementAggregator aggregator = getQueryAggregator(Sets.newHashSet(), null);
        // Then
        // As the groupBy property - property1 is aggregated alongside property2, this is still required in the aggregator function.
        Assert.assertEquals(5, aggregator.getComponents().size());
        Assert.assertTrue(((aggregator.getComponents().get(0).getBinaryOperator()) instanceof ExampleTuple2BinaryOperator));
        Assert.assertArrayEquals(new String[]{ "property1", "property2" }, aggregator.getComponents().get(0).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(1).getBinaryOperator()) instanceof ExampleTuple2BinaryOperator));
        Assert.assertArrayEquals(new String[]{ "property3", "property4" }, aggregator.getComponents().get(1).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(2).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "property5" }, aggregator.getComponents().get(2).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(3).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "visibility" }, aggregator.getComponents().get(3).getSelection());
        Assert.assertTrue(((aggregator.getComponents().get(4).getBinaryOperator()) instanceof ExampleAggregateFunction));
        Assert.assertArrayEquals(new String[]{ "timestamp" }, aggregator.getComponents().get(4).getSelection());
        // Check the aggregator is locked.
        try {
            aggregator.getComponents().add(null);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
        // check the aggregator is cached
        Assert.assertSame(aggregator, elementDef.getQueryAggregator(Sets.newHashSet(), null));
    }

    @Test
    public void shouldMergeDifferentSchemaElementDefinitions() {
        // Given
        // When
        final T elementDef1 = SchemaElementDefinitionTest.createBuilder().property(PROP_1, "property.integer").validator(new ElementFilter.Builder().select(PROP_1).execute(new ExampleFilterFunction()).build()).build();
        final T elementDef2 = SchemaElementDefinitionTest.createBuilder().property(PROP_2, "property.object").validator(new ElementFilter.Builder().select(PROP_2).execute(new ExampleFilterFunction()).build()).groupBy(PROP_2).build();
        // When
        final T mergedDef = SchemaElementDefinitionTest.createEmptyBuilder().merge(elementDef1).merge(elementDef2).build();
        // Then
        Assert.assertEquals(2, getProperties().size());
        Assert.assertNotNull(mergedDef.getPropertyTypeDef(PROP_1));
        Assert.assertNotNull(mergedDef.getPropertyTypeDef(PROP_2));
        Assert.assertEquals(Sets.newLinkedHashSet(Collections.singletonList(PROP_2)), getGroupBy());
    }

    @Test
    public void shouldThrowExceptionWhenMergeSchemaElementDefinitionWithConflictingProperty() {
        // Given
        // When
        final T elementDef1 = SchemaElementDefinitionTest.createBuilder().property(PROP_1, "string").build();
        final T elementDef2 = SchemaElementDefinitionTest.createBuilder().property(PROP_1, "int").build();
        // When / Then
        try {
            SchemaElementDefinitionTest.createEmptyBuilder().merge(elementDef1).merge(elementDef2).build();
            Assert.fail("Exception expected");
        } catch (final SchemaException e) {
            Assert.assertTrue(e.getMessage().contains("property"));
        }
    }
}

