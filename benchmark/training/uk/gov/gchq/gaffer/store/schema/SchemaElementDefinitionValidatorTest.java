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
import IdentifierType.SOURCE;
import IdentifierType.VERTEX;
import TestGroups.ENTITY;
import TestPropertyNames.COUNT;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import TestPropertyNames.STRING;
import TestPropertyNames.TIMESTAMP;
import TestPropertyNames.VISIBILITY;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import uk.gov.gchq.gaffer.commonutil.TestPropertyNames;
import uk.gov.gchq.gaffer.data.element.IdentifierType;
import uk.gov.gchq.gaffer.data.element.function.ElementAggregator;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;

import static com.google.common.collect.Sets.newHashSet;


public class SchemaElementDefinitionValidatorTest {
    @Test
    public void shouldValidateComponentTypesAndReturnTrueWhenNoIdentifiersOrProperties() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        BDDMockito.given(elementDef.getIdentifiers()).willReturn(new HashSet());
        BDDMockito.given(elementDef.getProperties()).willReturn(new HashSet());
        // When
        final ValidationResult result = validator.validateComponentTypes(elementDef);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateComponentTypesAndErrorWhenPropertyNameIsAReservedWord() {
        for (final IdentifierType identifierType : IdentifierType.values()) {
            // Given
            final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
            final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
            final Set<String> properties = new HashSet<>();
            properties.add(COUNT);
            properties.add(identifierType.name());
            BDDMockito.given(elementDef.getIdentifiers()).willReturn(new HashSet());
            BDDMockito.given(elementDef.getProperties()).willReturn(properties);
            // When
            final ValidationResult result = validator.validateComponentTypes(elementDef);
            // Then
            Assert.assertFalse(result.isValid());
            Assert.assertTrue(result.getErrorString().contains("reserved word"));
        }
    }

    @Test
    public void shouldValidateComponentTypesAndReturnTrueWhenIdentifiersAndPropertiesHaveClasses() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        BDDMockito.given(elementDef.getIdentifiers()).willReturn(Sets.newSet(DESTINATION, SOURCE));
        BDDMockito.given(elementDef.getProperties()).willReturn(Sets.newSet(PROP_1, PROP_2));
        BDDMockito.given(elementDef.getIdentifierClass(DESTINATION)).willReturn(((Class) (Double.class)));
        BDDMockito.given(elementDef.getIdentifierClass(SOURCE)).willReturn(((Class) (Long.class)));
        BDDMockito.given(elementDef.getPropertyClass(PROP_1)).willReturn(((Class) (Integer.class)));
        BDDMockito.given(elementDef.getPropertyClass(PROP_2)).willReturn(((Class) (String.class)));
        // When
        final ValidationResult result = validator.validateComponentTypes(elementDef);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateComponentTypesAndReturnFalseForInvalidPropertyClass() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        BDDMockito.given(elementDef.getIdentifiers()).willReturn(new HashSet());
        BDDMockito.given(elementDef.getProperties()).willReturn(Sets.newSet(PROP_1));
        BDDMockito.given(elementDef.getPropertyClass(PROP_1)).willThrow(new IllegalArgumentException());
        // When
        final ValidationResult result = validator.validateComponentTypes(elementDef);
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertEquals("Validation errors: \nClass null for property property1 could not be found", result.getErrorString());
    }

    @Test
    public void shouldValidateFunctionSelectionsAndReturnFalseWhenAFunctionIsNull() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final ElementFilter elementFilter = new ElementFilter.Builder().select("selection").execute(null).build();
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        // When
        final ValidationResult result = validator.validateFunctionArgumentTypes(elementFilter, elementDef);
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertTrue(result.getErrorString().contains("null function"));
    }

    @Test
    public void shouldValidateFunctionSelectionsAndReturnTrueWhenNoFunctionsSet() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final ElementFilter elementFilter = new ElementFilter();
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        // When
        final ValidationResult result = validator.validateFunctionArgumentTypes(elementFilter, elementDef);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateFunctionSelectionsAndReturnTrueWhenElementFilterIsNull() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final ElementFilter elementFilter = null;
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        // When
        final ValidationResult result = validator.validateFunctionArgumentTypes(elementFilter, elementDef);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateFunctionSelectionsAndReturnFalseWhenFunctionTypeDoesNotEqualSelectionType() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        BDDMockito.given(elementDef.getPropertyClass("selection")).willReturn(((Class) (String.class)));
        final IsMoreThan function = new IsMoreThan(5);
        final ElementFilter elementFilter = new ElementFilter.Builder().select("selection").execute(function).build();
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        // When
        final ValidationResult result = validator.validateFunctionArgumentTypes(elementFilter, elementDef);
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(("Validation errors: \nControl value class java.lang.Integer is not compatible" + " with the input type: class java.lang.String"), result.getErrorString());
    }

    @Test
    public void shouldValidateFunctionSelectionsAndReturnTrueWhenAllFunctionsAreValid() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        BDDMockito.given(elementDef.getPropertyClass("selectionStr")).willReturn(((Class) (String.class)));
        BDDMockito.given(elementDef.getPropertyClass("selectionInt")).willReturn(((Class) (Integer.class)));
        final Predicate<String> function1 = ( a) -> a.contains("true");
        final Predicate<Integer> function2 = ( a) -> a > 0;
        final ElementFilter elementFilter = new ElementFilter.Builder().select("selectionStr").execute(function1).select("selectionInt").execute(function2).build();
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        // When
        final ValidationResult result = validator.validateFunctionArgumentTypes(elementFilter, elementDef);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenAggregationIsDisabled() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final Map<String, String> properties = new HashMap<>();
        properties.put(PROP_1, "int");
        properties.put(PROP_2, "string");
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final ElementAggregator aggregator = new ElementAggregator.Builder().select(PROP_1).execute(function1).build();
        BDDMockito.given(elementDef.getIdentifiers()).willReturn(new HashSet());
        BDDMockito.given(elementDef.getProperties()).willReturn(properties.keySet());
        BDDMockito.given(elementDef.getPropertyMap()).willReturn(properties);
        BDDMockito.given(elementDef.getValidator()).willReturn(Mockito.mock(ElementFilter.class));
        BDDMockito.given(elementDef.getFullAggregator()).willReturn(aggregator);
        BDDMockito.given(elementDef.getPropertyClass(PROP_1)).willReturn(((Class) (Integer.class)));
        BDDMockito.given(elementDef.getPropertyClass(PROP_2)).willReturn(((Class) (String.class)));
        BDDMockito.given(elementDef.isAggregate()).willReturn(false);
        // When
        final ValidationResult result = validator.validate(elementDef);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenAggregatorIsValid() {
        // Given
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final BinaryOperator function2 = Mockito.mock(BinaryOperator.class);
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("int1").property(PROP_1, "int1").property(PROP_2, "int2").build()).type("int1", new TypeDefinition.Builder().clazz(Integer.class).aggregateFunction(function1).build()).type("int2", new TypeDefinition.Builder().clazz(Integer.class).aggregateFunction(function2).build()).build();
        // When
        final ValidationResult result = validator.validate(schema.getEntity(ENTITY));
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenAggregatorIsInvalid() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final Map<String, String> properties = new HashMap<>();
        properties.put(PROP_1, "int");
        properties.put(PROP_2, "string");
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final ElementAggregator aggregator = new ElementAggregator.Builder().select(PROP_1).execute(function1).build();
        BDDMockito.given(elementDef.getIdentifiers()).willReturn(new HashSet());
        BDDMockito.given(elementDef.getProperties()).willReturn(properties.keySet());
        BDDMockito.given(elementDef.getPropertyMap()).willReturn(properties);
        BDDMockito.given(elementDef.getValidator()).willReturn(Mockito.mock(ElementFilter.class));
        BDDMockito.given(elementDef.getFullAggregator()).willReturn(aggregator);
        BDDMockito.given(elementDef.getPropertyClass(PROP_1)).willReturn(((Class) (Integer.class)));
        BDDMockito.given(elementDef.getPropertyClass(PROP_2)).willReturn(((Class) (String.class)));
        BDDMockito.given(elementDef.isAggregate()).willReturn(true);
        // When
        final ValidationResult result = validator.validate(elementDef);
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertTrue(result.getErrorString().contains("No aggregator found for properties"));
        Mockito.verify(elementDef, Mockito.atLeastOnce()).getPropertyClass(PROP_1);
        Mockito.verify(elementDef, Mockito.atLeastOnce()).getPropertyClass(PROP_2);
    }

    @Test
    public void shouldValidateAndReturnFalseWhenNoAggregatorByGroupBysSet() {
        // Given
        Set<String> groupBys = new HashSet<>();
        groupBys.add("int");
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final Map<String, String> properties = new HashMap<>();
        properties.put(PROP_1, "int");
        properties.put(PROP_2, "string");
        BDDMockito.given(elementDef.getGroupBy()).willReturn(groupBys);
        BDDMockito.given(elementDef.getProperties()).willReturn(properties.keySet());
        BDDMockito.given(elementDef.getPropertyMap()).willReturn(properties);
        BDDMockito.given(elementDef.getValidator()).willReturn(Mockito.mock(ElementFilter.class));
        BDDMockito.given(elementDef.getPropertyClass(PROP_1)).willReturn(((Class) (Integer.class)));
        BDDMockito.given(elementDef.getPropertyClass(PROP_2)).willReturn(((Class) (String.class)));
        BDDMockito.given(elementDef.isAggregate()).willReturn(false);
        // When
        final ValidationResult result = validator.validate(elementDef);
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertEquals("Validation errors: \nGroups with aggregation disabled should not have groupBy properties.", result.getErrorString());
        Mockito.verify(elementDef, Mockito.atLeastOnce()).getPropertyClass(PROP_1);
        Mockito.verify(elementDef, Mockito.atLeastOnce()).getPropertyClass(PROP_2);
    }

    @Test
    public void shouldValidateAndReturnTrueWhenNoPropertiesSoAggregatorIsValid() {
        // Given
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        BDDMockito.given(elementDef.getIdentifiers()).willReturn(new HashSet());
        BDDMockito.given(elementDef.getPropertyMap()).willReturn(Collections.emptyMap());
        BDDMockito.given(elementDef.getValidator()).willReturn(Mockito.mock(ElementFilter.class));
        BDDMockito.given(elementDef.getFullAggregator()).willReturn(null);
        BDDMockito.given(elementDef.isAggregate()).willReturn(true);
        // When
        final ValidationResult result = validator.validate(elementDef);
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenAggregatorProvidedWithNoProperties() {
        // Given
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("id").aggregator(new ElementAggregator.Builder().select(VERTEX.name()).execute(function1).build()).build()).type("id", new TypeDefinition.Builder().clazz(String.class).build()).build();
        // When
        final ValidationResult result = validator.validate(schema.getElement(ENTITY));
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(newHashSet("Groups with no properties should not have any aggregators"), result.getErrors());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenAggregatorHasIdentifierInSelection() {
        // Given
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("id").property(STRING, "string").aggregator(new ElementAggregator.Builder().select(VERTEX.name()).execute(function1).build()).build()).type("id", new TypeDefinition.Builder().clazz(String.class).build()).type("string", new TypeDefinition.Builder().clazz(String.class).aggregateFunction(function1).build()).build();
        // When
        final ValidationResult result = validator.validate(schema.getElement(ENTITY));
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(newHashSet(("Identifiers cannot be selected for aggregation: " + (VERTEX.name()))), result.getErrors());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenVisibilityIsAggregatedWithOtherProperty() {
        // Given
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("id").property(STRING, "string").property(VISIBILITY, "string").aggregator(new ElementAggregator.Builder().select(STRING, VISIBILITY).execute(function1).build()).build()).visibilityProperty(VISIBILITY).type("id", new TypeDefinition.Builder().clazz(String.class).build()).type("string", new TypeDefinition.Builder().clazz(String.class).aggregateFunction(function1).build()).build();
        // When
        final ValidationResult result = validator.validate(schema.getElement(ENTITY));
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(newHashSet(("The visibility property must be aggregated by itself. It is currently aggregated in the tuple: [stringProperty, visibility], by aggregate function: " + (function1.getClass().getName()))), result.getErrors());
    }

    @Test
    public void shouldValidateAndReturnTrueWhenTimestampIsAggregatedWithANonGroupByProperty() {
        // Given
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("id").property(STRING, "string").property(TIMESTAMP, "string").aggregator(new ElementAggregator.Builder().select(STRING, TIMESTAMP).execute(function1).build()).build()).timestampProperty(TIMESTAMP).type("id", new TypeDefinition.Builder().clazz(String.class).build()).type("string", new TypeDefinition.Builder().clazz(String.class).aggregateFunction(function1).build()).build();
        // When
        final ValidationResult result = validator.validate(schema.getElement(ENTITY));
        // Then
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenTimestampIsAggregatedWithAGroupByProperty() {
        // Given
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("id").property(STRING, "string").property(TIMESTAMP, "string").groupBy(STRING).aggregator(new ElementAggregator.Builder().select(STRING, TIMESTAMP).execute(function1).build()).build()).timestampProperty(TIMESTAMP).type("id", new TypeDefinition.Builder().clazz(String.class).build()).type("string", new TypeDefinition.Builder().clazz(String.class).aggregateFunction(function1).build()).build();
        // When
        final ValidationResult result = validator.validate(schema.getElement(ENTITY));
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(newHashSet(("groupBy properties and non-groupBy properties (including timestamp) must be not be aggregated using the same BinaryOperator. Selection tuple: [stringProperty, timestamp], is aggregated by: " + (function1.getClass().getName()))), result.getErrors());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenGroupByIsAggregatedWithNonGroupByProperty() {
        // Given
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("id").property(PROP_1, "string").property(PROP_2, "string").groupBy(PROP_1).aggregator(new ElementAggregator.Builder().select(PROP_1, PROP_2).execute(function1).build()).build()).type("id", new TypeDefinition.Builder().clazz(String.class).build()).type("string", new TypeDefinition.Builder().clazz(String.class).aggregateFunction(function1).build()).build();
        // When
        final ValidationResult result = validator.validate(schema.getElement(ENTITY));
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(newHashSet(("groupBy properties and non-groupBy properties (including timestamp) must be not be aggregated using the same BinaryOperator. Selection tuple: [property1, property2], is aggregated by: " + (function1.getClass().getName()))), result.getErrors());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenAggregatorHasNoFunction() {
        // Given
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("id").property(PROP_1, "string").aggregator(new ElementAggregator.Builder().select(PROP_1).execute(null).build()).build()).type("id", new TypeDefinition.Builder().clazz(String.class).build()).type("string", new TypeDefinition.Builder().clazz(String.class).build()).build();
        // When
        final ValidationResult result = validator.validate(schema.getElement(ENTITY));
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(newHashSet("ElementAggregator contains a null function."), result.getErrors());
    }

    @Test
    public void shouldValidateAndReturnFalseWhenAggregatorSelectionHasUnknownProperty() {
        // Given
        final SchemaElementDefinitionValidator validator = new SchemaElementDefinitionValidator();
        final BinaryOperator<Integer> function1 = Mockito.mock(BinaryOperator.class);
        final Schema schema = new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("id").property(STRING, "string").aggregator(new ElementAggregator.Builder().select(STRING, PROP_1).execute(function1).build()).build()).type("id", new TypeDefinition.Builder().clazz(String.class).build()).type("string", new TypeDefinition.Builder().clazz(String.class).aggregateFunction(function1).build()).build();
        // When
        final ValidationResult result = validator.validate(schema.getElement(ENTITY));
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertEquals(newHashSet(("Unknown property used in an aggregator: " + (TestPropertyNames.PROP_1))), result.getErrors());
    }
}

