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
package uk.gov.gchq.gaffer.data.elementdefinition.view;


import TestPropertyNames.COUNT;
import TestPropertyNames.DATE;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import ViewElementDefinition.Builder;
import com.google.common.collect.Sets;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.TestPropertyNames;
import uk.gov.gchq.gaffer.data.element.function.ElementAggregator;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.element.function.ElementTransformer;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.koryphe.function.KorypheFunction;
import uk.gov.gchq.koryphe.impl.binaryoperator.Max;
import uk.gov.gchq.koryphe.impl.predicate.IsEqual;
import uk.gov.gchq.koryphe.impl.predicate.IsLessThan;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;
import uk.gov.gchq.koryphe.tuple.binaryoperator.TupleAdaptedBinaryOperator;
import uk.gov.gchq.koryphe.tuple.function.TupleAdaptedFunction;
import uk.gov.gchq.koryphe.tuple.predicate.TupleAdaptedPredicate;


public class ViewElementDefinitionTest {
    @Test
    public void shouldBuildElementDefinition() {
        // Given
        final ElementTransformer transformer = Mockito.mock(ElementTransformer.class);
        final ElementFilter preFilter = Mockito.mock(ElementFilter.class);
        final ElementAggregator aggregator = Mockito.mock(ElementAggregator.class);
        final ElementFilter postFilter = Mockito.mock(ElementFilter.class);
        // When
        final ViewElementDefinition elementDef = new ViewElementDefinition.Builder().transientProperty(PROP_1, String.class).transientProperty(PROP_2, String.class).properties(COUNT, DATE).preAggregationFilter(preFilter).aggregator(aggregator).postTransformFilter(postFilter).transformer(transformer).build();
        // Then
        Assert.assertEquals(2, elementDef.getTransientProperties().size());
        Assert.assertTrue(elementDef.containsTransientProperty(PROP_1));
        Assert.assertTrue(elementDef.containsTransientProperty(PROP_2));
        Assert.assertEquals(Sets.newHashSet(COUNT, DATE), elementDef.getProperties());
        Assert.assertNull(elementDef.getExcludeProperties());
        Assert.assertSame(preFilter, elementDef.getPreAggregationFilter());
        Assert.assertSame(aggregator, elementDef.getAggregator());
        Assert.assertSame(postFilter, elementDef.getPostTransformFilter());
        Assert.assertSame(transformer, elementDef.getTransformer());
    }

    @Test
    public void shouldJsonSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final ViewElementDefinition elementDef = new ViewElementDefinition.Builder().transientProperty(PROP_1, String.class).transientProperty(PROP_2, String.class).properties(COUNT, DATE).preAggregationFilter(new ElementFilter.Builder().select(COUNT).execute(new IsMoreThan(5)).build()).aggregator(new ElementAggregator.Builder().select(COUNT).execute(new Max()).build()).postAggregationFilter(new ElementFilter.Builder().select(COUNT).execute(new IsLessThan(10)).build()).transformer(new ElementTransformer.Builder().select(COUNT).execute(new ViewElementDefinitionTest.TestTransform()).project(PROP_1).build()).postTransformFilter(new ElementFilter.Builder().select(PROP_1).execute(new IsEqual("9")).build()).build();
        // When
        final byte[] json = JSONSerialiser.serialise(elementDef, true);
        final ViewElementDefinition deserialisedElementDef = JSONSerialiser.deserialise(json, ViewElementDefinition.class);
        Assert.assertEquals(Sets.newHashSet(COUNT, DATE), deserialisedElementDef.getProperties());
        Assert.assertNull(deserialisedElementDef.getExcludeProperties());
        final List<TupleAdaptedPredicate<String, ?>> preFilterComponents = deserialisedElementDef.getPreAggregationFilter().getComponents();
        Assert.assertEquals(1, preFilterComponents.size());
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.COUNT }, preFilterComponents.get(0).getSelection());
        Assert.assertEquals(new IsMoreThan(5), preFilterComponents.get(0).getPredicate());
        final List<TupleAdaptedBinaryOperator<String, ?>> aggComponents = deserialisedElementDef.getAggregator().getComponents();
        Assert.assertEquals(1, aggComponents.size());
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.COUNT }, aggComponents.get(0).getSelection());
        Assert.assertEquals(new Max(), aggComponents.get(0).getBinaryOperator());
        final List<TupleAdaptedPredicate<String, ?>> postFilterComponents = deserialisedElementDef.getPostAggregationFilter().getComponents();
        Assert.assertEquals(1, postFilterComponents.size());
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.COUNT }, postFilterComponents.get(0).getSelection());
        Assert.assertEquals(new IsLessThan(10), postFilterComponents.get(0).getPredicate());
        final List<TupleAdaptedFunction<String, ?, ?>> transformComponents = deserialisedElementDef.getTransformer().getComponents();
        Assert.assertEquals(1, transformComponents.size());
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.COUNT }, transformComponents.get(0).getSelection());
        Assert.assertEquals(new ViewElementDefinitionTest.TestTransform(), transformComponents.get(0).getFunction());
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1 }, transformComponents.get(0).getProjection());
        final List<TupleAdaptedPredicate<String, ?>> postTransformFilterComponents = deserialisedElementDef.getPostTransformFilter().getComponents();
        Assert.assertEquals(1, postTransformFilterComponents.size());
        Assert.assertArrayEquals(new String[]{ TestPropertyNames.PROP_1 }, postTransformFilterComponents.get(0).getSelection());
        Assert.assertEquals(new IsEqual("9"), postTransformFilterComponents.get(0).getPredicate());
    }

    public static final class TestTransform extends KorypheFunction<Integer, String> {
        @Override
        public String apply(final Integer integer) {
            return Integer.toString(integer);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToBuildElementDefinitionWhenPreAggregationFilterSpecifiedTwice() {
        // Given
        final ElementTransformer transformer = Mockito.mock(ElementTransformer.class);
        final ElementFilter filter = Mockito.mock(ElementFilter.class);
        // When
        final ViewElementDefinition elementDef = new ViewElementDefinition.Builder().transientProperty(PROP_1, String.class).transientProperty(PROP_2, String.class).transformer(transformer).preAggregationFilter(filter).preAggregationFilter(filter).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToBuildElementDefinitionWhenPostAggregationFilterSpecifiedTwice() {
        // Given
        final ElementTransformer transformer = Mockito.mock(ElementTransformer.class);
        final ElementFilter filter = Mockito.mock(ElementFilter.class);
        // When
        final ViewElementDefinition elementDef = new ViewElementDefinition.Builder().transientProperty(PROP_1, String.class).transientProperty(PROP_2, String.class).transformer(transformer).postAggregationFilter(filter).postAggregationFilter(filter).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToBuildElementDefinitionWhenPostTransformFilterSpecifiedTwice() {
        // Given
        final ElementTransformer transformer = Mockito.mock(ElementTransformer.class);
        final ElementFilter postFilter = Mockito.mock(ElementFilter.class);
        // When
        final ViewElementDefinition elementDef = new ViewElementDefinition.Builder().transientProperty(PROP_1, String.class).transientProperty(PROP_2, String.class).transformer(transformer).postTransformFilter(postFilter).postTransformFilter(postFilter).build();
    }

    @Test
    public void shouldFailToBuildElementDefinitionWhenPropertiesAndExcludePropertiesSet() {
        // When
        final ViewElementDefinition.Builder builder = new ViewElementDefinition.Builder();
        // Then / When
        builder.properties(PROP_1);
        try {
            builder.excludeProperties(PROP_1);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("You cannot set both properties and excludeProperties", e.getMessage());
        }
    }

    @Test
    public void shouldFailToBuildElementDefinitionWhenExcludePropertiesAndPropertiesSet() {
        // When
        final ViewElementDefinition.Builder builder = new ViewElementDefinition.Builder();
        // Then / When
        builder.excludeProperties(PROP_1);
        try {
            builder.properties(PROP_1);
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("You cannot set both properties and excludeProperties", e.getMessage());
        }
    }
}

