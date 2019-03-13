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
package uk.gov.gchq.gaffer.data.element.function;


import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import java.util.List;
import java.util.function.Predicate;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.JSONSerialisationTest;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.koryphe.impl.predicate.IsEqual;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;
import uk.gov.gchq.koryphe.impl.predicate.Or;
import uk.gov.gchq.koryphe.tuple.predicate.TupleAdaptedPredicate;


public class ElementFilterTest extends JSONSerialisationTest<ElementFilter> {
    @Test
    public void shouldTestElementOnPredicate2() {
        // Given
        final ElementFilter filter = new ElementFilter.Builder().select("prop1", "prop2").execute(new uk.gov.gchq.koryphe.tuple.predicate.KoryphePredicate2<String, String>() {
            @Override
            public boolean test(final String o, final String o2) {
                return ("value".equals(o)) && ("value2".equals(o2));
            }
        }).build();
        final Entity element1 = new Entity.Builder().property("prop1", "value").property("prop2", "value2").build();
        final Entity element2 = new Entity.Builder().property("prop1", "unknown").property("prop2", "value2").build();
        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);
        // Then
        Assert.assertTrue(result1);
        Assert.assertFalse(result2);
    }

    @Test
    public void shouldTestElementOnPredicate2WithValidationResult() {
        // Given
        final uk.gov.gchq.koryphe.tuple.predicate.KoryphePredicate2<String, String> predicate2 = new uk.gov.gchq.koryphe.tuple.predicate.KoryphePredicate2<String, String>() {
            @Override
            public boolean test(final String o, final String o2) {
                return ("value".equals(o)) && ("value2".equals(o2));
            }
        };
        final ElementFilter filter = new ElementFilter.Builder().select("prop1", "prop2").execute(predicate2).build();
        final Entity element1 = new Entity.Builder().property("prop1", "value").property("prop2", "value2").build();
        final Entity element2 = new Entity.Builder().property("prop1", "unknown").property("prop2", "value2").build();
        // When
        final ValidationResult result1 = filter.testWithValidationResult(element1);
        final ValidationResult result2 = filter.testWithValidationResult(element2);
        // Then
        Assert.assertTrue(result1.isValid());
        Assert.assertFalse(result2.isValid());
        Assert.assertTrue(("Result was: " + (result2.getErrorString())), result2.getErrorString().contains("{prop1: <java.lang.String>unknown, prop2: <java.lang.String>value2}"));
    }

    @Test
    public void shouldTestElementOnInlinePredicate() {
        // Given
        final ElementFilter filter = new ElementFilter.Builder().select("prop1").execute("value"::equals).build();
        final Entity element1 = new Entity.Builder().property("prop1", "value").build();
        final Entity element2 = new Entity.Builder().property("prop1", "unknown").build();
        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);
        // Then
        Assert.assertTrue(result1);
        Assert.assertFalse(result2);
    }

    @Test
    public void shouldTestElementOnLambdaPredicate() {
        // Given
        final Predicate<Object> predicate = ( p) -> (null == p) || (String.class.isAssignableFrom(p.getClass()));
        final ElementFilter filter = new ElementFilter.Builder().select("prop1").execute(predicate).build();
        final Entity element1 = new Entity.Builder().property("prop1", "value").build();
        final Entity element2 = new Entity.Builder().property("prop1", 1).build();
        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);
        // Then
        Assert.assertTrue(result1);
        Assert.assertFalse(result2);
    }

    @Test
    public void shouldTestElementOnComplexLambdaPredicate() {
        // Given
        final Predicate<Object> predicate1 = ( p) -> Integer.class.isAssignableFrom(p.getClass());
        final Predicate<Object> predicate2 = "value"::equals;
        final ElementFilter filter = new ElementFilter.Builder().select("prop1").execute(predicate1.negate().and(predicate2)).build();
        final Entity element1 = new Entity.Builder().property("prop1", "value").build();
        final Entity element2 = new Entity.Builder().property("prop1", 1).build();
        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);
        // Then
        Assert.assertTrue(result1);
        Assert.assertFalse(result2);
    }

    @Test
    public void shouldBuildFilter() {
        // Given
        final String property1 = "property 1";
        final String property2a = "property 2a";
        final String property2b = "property 2b";
        final String property3 = "property 3";
        final Predicate func1 = Mockito.mock(Predicate.class);
        final Predicate func2 = Mockito.mock(Predicate.class);
        final Predicate func3 = Mockito.mock(Predicate.class);
        // When - check you can build the selection/function in any order,
        // although normally it will be done - select then execute.
        final ElementFilter filter = new ElementFilter.Builder().select(property1).execute(func1).select(property2a, property2b).execute(func2).select(property3).execute(func3).build();
        // Then
        int i = 0;
        TupleAdaptedPredicate<String, ?> adaptedFunction = filter.getComponents().get((i++));
        Assert.assertEquals(1, adaptedFunction.getSelection().length);
        Assert.assertEquals(property1, adaptedFunction.getSelection()[0]);
        TestCase.assertSame(func1, adaptedFunction.getPredicate());
        adaptedFunction = filter.getComponents().get((i++));
        Assert.assertEquals(2, adaptedFunction.getSelection().length);
        Assert.assertEquals(property2a, adaptedFunction.getSelection()[0]);
        Assert.assertEquals(property2b, adaptedFunction.getSelection()[1]);
        TestCase.assertSame(func2, adaptedFunction.getPredicate());
        adaptedFunction = filter.getComponents().get((i++));
        TestCase.assertSame(func3, adaptedFunction.getPredicate());
        Assert.assertEquals(1, adaptedFunction.getSelection().length);
        Assert.assertEquals(property3, adaptedFunction.getSelection()[0]);
        Assert.assertEquals(i, filter.getComponents().size());
    }

    @Test
    public void shouldExecuteOrPredicates() {
        final ElementFilter filter = new ElementFilter.Builder().select(PROP_1, PROP_2).execute(new Or.Builder<>().select(0).execute(new IsMoreThan(2)).select(1).execute(new IsEqual("some value")).build()).build();
        final Entity element1 = new Entity.Builder().property(PROP_1, 3).property(PROP_2, "some value").build();
        final Entity element2 = new Entity.Builder().property(PROP_1, 1).property(PROP_2, "some value").build();
        final Entity element3 = new Entity.Builder().property(PROP_1, 3).property(PROP_2, "some invalid value").build();
        final Entity element4 = new Entity.Builder().property(PROP_1, 1).property(PROP_2, "some invalid value").build();
        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);
        final boolean result3 = filter.test(element3);
        final boolean result4 = filter.test(element4);
        // Then
        Assert.assertTrue(result1);
        Assert.assertTrue(result2);
        Assert.assertTrue(result3);
        Assert.assertFalse(result4);
    }

    @Test
    public void shouldExecuteNotPredicates() {
        final ElementFilter filter = new ElementFilter.Builder().select(PROP_1, PROP_2).execute(new uk.gov.gchq.koryphe.impl.predicate.Not(new Or.Builder<>().select(0).execute(new IsMoreThan(2)).select(1).execute(new IsEqual("some value")).build())).build();
        final Entity element1 = new Entity.Builder().property(PROP_1, 3).property(PROP_2, "some value").build();
        final Entity element2 = new Entity.Builder().property(PROP_1, 1).property(PROP_2, "some value").build();
        final Entity element3 = new Entity.Builder().property(PROP_1, 3).property(PROP_2, "some invalid value").build();
        final Entity element4 = new Entity.Builder().property(PROP_1, 1).property(PROP_2, "some invalid value").build();
        // When
        final boolean result1 = filter.test(element1);
        final boolean result2 = filter.test(element2);
        final boolean result3 = filter.test(element3);
        final boolean result4 = filter.test(element4);
        // Then
        Assert.assertFalse(result1);
        Assert.assertFalse(result2);
        Assert.assertFalse(result3);
        Assert.assertTrue(result4);
    }

    @Test
    public void shouldReturnUnmodifiableComponentsWhenLocked() {
        // Given
        final ElementFilter filter = getTestObject();
        // When
        filter.lock();
        final List<TupleAdaptedPredicate<String, ?>> components = filter.getComponents();
        // Then
        try {
            components.add(null);
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void shouldReturnModifiableComponentsWhenNotLocked() {
        // Given
        final ElementFilter filter = getTestObject();
        // When
        final List<TupleAdaptedPredicate<String, ?>> components = filter.getComponents();
        // Then - no exceptions
        components.add(null);
    }
}

