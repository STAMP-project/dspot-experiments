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


import IdentifierType.ADJACENT_MATCHED_VERTEX;
import IdentifierType.MATCHED_VERTEX;
import StoreTrait.POST_AGGREGATION_FILTERING;
import StoreTrait.POST_TRANSFORMATION_FILTERING;
import StoreTrait.PRE_AGGREGATION_FILTERING;
import StoreTrait.QUERY_AGGREGATION;
import StoreTrait.TRANSFORMATION;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import TestPropertyNames.PROP_3;
import TestPropertyNames.TRANSIENT_1;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.function.ElementAggregator;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.element.function.ElementTransformer;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.function.ExampleFilterFunction;
import uk.gov.gchq.gaffer.function.ExampleTransformFunction;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.koryphe.impl.binaryoperator.StringConcat;
import uk.gov.gchq.koryphe.impl.predicate.And;
import uk.gov.gchq.koryphe.impl.predicate.Exists;
import uk.gov.gchq.koryphe.impl.predicate.IsEqual;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;
import uk.gov.gchq.koryphe.impl.predicate.Or;


public class ViewValidatorTest {
    public static final Set<StoreTrait> ALL_STORE_TRAITS = Sets.newHashSet(StoreTrait.values());

    public static final Set<StoreTrait> NO_STORE_TRAITS = Collections.emptySet();

    @Test
    public void shouldValidateAndReturnTrueWhenEmptyFunctions() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().build();
        final Schema schema = new Schema();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenEntityTransientPropertyIsInSchema() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transientProperty(PROP_1, String.class).build()).build();
        final Schema schema = new Schema.Builder().type("prop1", Object.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "prop1").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenEntityTransientPropertyIsNotInSchema() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transientProperty(PROP_1, String.class).build()).build();
        final Schema schema = new Schema.Builder().type("obj", Object.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_2, "obj").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenEntityFilterSelectionUnknownProperty() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transientProperty(PROP_3, String.class).preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new ExampleFilterFunction()).build()).build()).build();
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenEntityTransformerSelectionUnknownProperty() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transientProperty(PROP_3, String.class).transformer(new ElementTransformer.Builder().select(PROP_1, PROP_2).execute(new ExampleTransformFunction()).project(PROP_3).build()).build()).build();
        final Schema schema = new Schema.Builder().type("obj", Object.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "obj").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenEntityTransformerProjectsToUnknownProperty() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transformer(new ElementTransformer.Builder().select(PROP_1, PROP_2).execute(new ExampleTransformFunction()).project(PROP_3).build()).build()).build();
        final Schema schema = new Schema.Builder().type("string", Object.class).type("int", Object.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "string").property(PROP_2, "int").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenEntityTransformerResult() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transformer(new ElementTransformer.Builder().select(PROP_1, PROP_2).execute(new ExampleTransformFunction()).project(PROP_3).build()).build()).build();
        final Schema schema = new Schema.Builder().type("double", Double.class).type("int", Integer.class).type("string", String.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "double").property(PROP_2, "int").property(PROP_3, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWithMatchedVertexFilter() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(MATCHED_VERTEX.name()).execute(new IsMoreThan("testValue1")).build()).postAggregationFilter(new ElementFilter.Builder().select(ADJACENT_MATCHED_VERTEX.name()).execute(new IsMoreThan("testValue2")).build()).postTransformFilter(new ElementFilter.Builder().select(MATCHED_VERTEX.name()).execute(new IsMoreThan("testValue3")).build()).build()).build();
        Schema schema = new Schema.Builder().type("double", Double.class).type("int", Integer.class).type("string", String.class).type("true", Boolean.class).type("vertex", String.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("vertex").destination("vertex").directed("true").property(PROP_1, "double").property(PROP_2, "int").property(PROP_3, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.getErrorString(), result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenEdgeTransientPropertyIsInSchema() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().transientProperty(PROP_1, String.class).build()).build();
        final Schema schema = new Schema.Builder().type("obj", Object.class).edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "obj").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenEdgeTransientPropertyIsNotInSchema() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().transientProperty(PROP_1, String.class).build()).build();
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_2, "obj").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenEdgeFilterSelectionUnknownProperty() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().transientProperty(PROP_3, String.class).preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new ExampleFilterFunction()).build()).build()).build();
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenEdgeTransformerSelectionUnknownProperty() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().transientProperty(PROP_3, String.class).transformer(new ElementTransformer.Builder().select(PROP_1, PROP_2).execute(new ExampleTransformFunction()).project(PROP_3).build()).build()).build();
        final Schema schema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "obj").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenEdgeTransformerProjectsToUnknownProperty() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().transformer(new ElementTransformer.Builder().select(PROP_1, PROP_2).execute(new ExampleTransformFunction()).project(PROP_3).build()).build()).build();
        final Schema schema = new Schema.Builder().type("int", Integer.class).type("string", String.class).edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "int").property(PROP_2, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenEdgeTransformerResult() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().transformer(new ElementTransformer.Builder().select(PROP_1, PROP_2).execute(new ExampleTransformFunction()).project(PROP_3).build()).build()).build();
        final Schema schema = new Schema.Builder().type("double", Double.class).type("int", Integer.class).type("string", String.class).edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "double").property(PROP_2, "int").property(PROP_3, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueNoGroupByProperties() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY).edge(EDGE).build();
        final Schema schema = new Schema.Builder().type("vertex", String.class).type("true", Boolean.class).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("vertex").build()).edge(EDGE, new SchemaEdgeDefinition.Builder().source("vertex").destination("vertex").directed("true").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueForNullView() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().build();
        final Schema schema = new Schema.Builder().type("vertex", String.class).type("true", Boolean.class).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("vertex").build()).edge(EDGE, new SchemaEdgeDefinition.Builder().source("vertex").destination("vertex").directed("true").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenGroupByPropertiesInSchema() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy(PROP_1).build()).edge(EDGE, new ViewElementDefinition.Builder().groupBy(PROP_1, PROP_2).build()).build();
        final Schema schema = new Schema.Builder().type("vertex", String.class).type("string|ColumnQualifier", new TypeDefinition.Builder().clazz(String.class).build()).type("string|Value", new TypeDefinition.Builder().clazz(String.class).build()).type("true", Boolean.class).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("vertex").property(PROP_1, "string|ColumnQualifier").property(PROP_2, "string|ColumnQualifier").property(PROP_3, "string|Value").groupBy(PROP_1).build()).edge(EDGE, new SchemaEdgeDefinition.Builder().source("vertex").destination("vertex").directed("true").property(PROP_1, "string|ColumnQualifier").property(PROP_2, "string|ColumnQualifier").property(PROP_3, "string|Value").groupBy(PROP_1, PROP_2).build()).visibilityProperty(PROP_2).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenTimestampGroupByPropertyUsedInEntity() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy(PROP_1, PROP_2).build()).edge(EDGE, new ViewElementDefinition.Builder().groupBy(PROP_1, PROP_2).build()).build();
        final Schema schema = new Schema.Builder().type("vertex", String.class).type("string|Timestamp", new TypeDefinition.Builder().clazz(String.class).build()).type("string|Value", new TypeDefinition.Builder().clazz(String.class).build()).type("true", Boolean.class).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("vertex").property(PROP_1, "string|Timestamp").property(PROP_2, "string|Value").build()).edge(EDGE, new SchemaEdgeDefinition.Builder().source("vertex").destination("vertex").directed("true").property(PROP_2, "string|Value").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenGroupByPropertyNotInSchema() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy(PROP_1).build()).edge(EDGE, new ViewElementDefinition.Builder().groupBy(PROP_2).build()).build();
        final Schema schema = new Schema.Builder().type("vertex", String.class).type("string|Timestamp", new TypeDefinition.Builder().clazz(String.class).build()).type("string|Value", new TypeDefinition.Builder().clazz(String.class).build()).type("true", Boolean.class).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("vertex").property(PROP_2, "string|Value").groupBy(PROP_2).build()).edge(EDGE, new SchemaEdgeDefinition.Builder().source("vertex").destination("vertex").directed("true").property(PROP_1, "string|Timestamp").property(PROP_2, "string|Value").groupBy(PROP_1).build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenPostTransformerFilterSet() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transientProperty(PROP_3, String.class).transformer(new ElementTransformer.Builder().select(PROP_1, PROP_2).execute(new ExampleTransformFunction()).project(PROP_3).build()).postTransformFilter(new ElementFilter.Builder().select(PROP_3).execute(new ExampleFilterFunction()).build()).build()).build();
        final Schema schema = new Schema.Builder().type("obj", Object.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "obj").property(PROP_2, "obj").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenPostTransformerSelectionIsUnknown() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transientProperty(PROP_3, String.class).transformer(new ElementTransformer.Builder().select(PROP_1, PROP_2).execute(new ExampleTransformFunction()).project(PROP_3).build()).postTransformFilter(new ElementFilter.Builder().select(TRANSIENT_1).execute(new ExampleFilterFunction()).build()).build()).build();
        final Schema schema = new Schema.Builder().type("obj", Object.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "obj").property(PROP_2, "obj").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueForOrFilter() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1, PROP_2).execute(new Or.Builder().select(0).execute(new IsEqual("some value")).select(1).execute(new IsEqual("some other value")).build()).build()).build()).build();
        final Schema schema = new Schema.Builder().type("obj", Object.class).type("string", String.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "obj").property(PROP_2, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.getErrorString(), result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseForOrFilterWithIncompatibleProperties() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1, PROP_2).execute(new Or.Builder().select(0).execute(new IsMoreThan(2)).select(1).execute(new IsEqual("some other value")).build()).build()).build()).build();
        final Schema schema = new Schema.Builder().type("obj", Object.class).type("string", String.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "obj").property(PROP_2, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueForAndFilter() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1, PROP_2).execute(new And.Builder().select(0).execute(new IsEqual("some value")).select(1).execute(new IsEqual("some other value")).build()).build()).build()).build();
        final Schema schema = new Schema.Builder().type("obj", Object.class).type("string", String.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "obj").property(PROP_2, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.getErrorString(), result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseForAndFilterWithIncompatibleProperties() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1, PROP_2).execute(new And.Builder().select(0).execute(new IsMoreThan(2)).select(1).execute(new IsEqual("some other value")).build()).build()).build()).build();
        final Schema schema = new Schema.Builder().type("obj", Object.class).type("string", String.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "obj").property(PROP_2, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueForNotFilter() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1, PROP_2).execute(new uk.gov.gchq.koryphe.impl.predicate.Not(new Or.Builder<>().select(0).execute(new IsEqual("some value")).select(1).execute(new IsEqual("some other value")).build())).build()).build()).build();
        final Schema schema = new Schema.Builder().type("obj", Object.class).type("string", String.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "obj").property(PROP_2, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.getErrorString(), result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseForNotFilterWithIncompatibleProperties() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1, PROP_2).execute(new uk.gov.gchq.koryphe.impl.predicate.Not(new Or.Builder<>().select(0).execute(new IsMoreThan("abcd")).select(1).execute(new IsEqual("some other value")).build())).build()).build()).build();
        final Schema schema = new Schema.Builder().type("int", Integer.class).type("string", String.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "int").property(PROP_2, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenAggregatorSelectionUnknownProperty() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().aggregator(new ElementAggregator.Builder().select(PROP_1).execute(new StringConcat()).build()).build()).build();
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.ALL_STORE_TRAITS);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenMissingTraits() {
        // Given
        final ViewValidator validator = new ViewValidator();
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().transientProperty(PROP_3, String.class).preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).aggregator(new ElementAggregator.Builder().select(PROP_1).execute(new StringConcat()).build()).postAggregationFilter(new ElementFilter.Builder().select(PROP_2).execute(new Exists()).build()).transformer(new ElementTransformer.Builder().select(PROP_1, PROP_2).execute(new ExampleTransformFunction()).project(PROP_3).build()).postTransformFilter(new ElementFilter.Builder().select(PROP_3).execute(new Exists()).build()).build()).build();
        final Schema schema = new Schema.Builder().type("obj", String.class).type("string", String.class).entity(ENTITY, new SchemaEntityDefinition.Builder().property(PROP_1, "obj").property(PROP_2, "string").build()).build();
        // When
        final ValidationResult result = validator.validate(view, schema, ViewValidatorTest.NO_STORE_TRAITS);
        // Then
        final String errPrefix = "This store does not currently support ";
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(Sets.newHashSet((errPrefix + (PRE_AGGREGATION_FILTERING.name())), (errPrefix + (QUERY_AGGREGATION.name())), (errPrefix + (POST_AGGREGATION_FILTERING.name())), (errPrefix + (TRANSFORMATION.name())), (errPrefix + (POST_TRANSFORMATION_FILTERING.name()))), result.getErrors());
    }
}

