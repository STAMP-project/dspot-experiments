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
package uk.gov.gchq.gaffer.store;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.serialisation.Serialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.BooleanSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.JavaSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.ordered.OrderedIntegerSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.raw.RawDateSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.raw.RawDoubleSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.raw.RawFloatSerialiser;


public class SerialisationFactoryTest {
    @Test
    public void shouldReturnSerialiserForAString() throws SerialisationException {
        // Given
        final SerialisationFactory factory = new SerialisationFactory();
        final Class<?> clazz = String.class;
        // When
        final Serialiser serialiser = factory.getSerialiser(clazz);
        // Then
        Assert.assertTrue(serialiser.canHandle(clazz));
        Assert.assertEquals(StringSerialiser.class, serialiser.getClass());
    }

    @Test
    public void shouldReturnOrderedSerialiserForAString() throws SerialisationException {
        // Given
        final SerialisationFactory factory = new SerialisationFactory();
        final Class<?> clazz = String.class;
        final boolean ordered = true;
        // When
        final Serialiser serialiser = factory.getSerialiser(clazz, ordered, true);
        // Then
        Assert.assertTrue(serialiser.canHandle(clazz));
        Assert.assertEquals(StringSerialiser.class, serialiser.getClass());
    }

    @Test
    public void shouldReturnSerialiserForAnInteger() throws SerialisationException {
        // Given
        final SerialisationFactory factory = new SerialisationFactory();
        final Class<?> clazz = Integer.class;
        // When
        final Serialiser serialiser = factory.getSerialiser(clazz);
        // Then
        Assert.assertTrue(serialiser.canHandle(clazz));
        Assert.assertEquals(OrderedIntegerSerialiser.class, serialiser.getClass());
    }

    @Test
    public void shouldReturnOrderedSerialiserForAnInteger() throws SerialisationException {
        // Given
        final SerialisationFactory factory = new SerialisationFactory();
        final Class<?> clazz = Integer.class;
        final boolean ordered = true;
        // When
        final Serialiser serialiser = factory.getSerialiser(clazz, ordered, true);
        // Then
        Assert.assertTrue(serialiser.canHandle(clazz));
        Assert.assertEquals(OrderedIntegerSerialiser.class, serialiser.getClass());
    }

    @Test
    public void shouldThrowExceptionIfClassIsNull() throws SerialisationException {
        // Given
        final SerialisationFactory factory = new SerialisationFactory();
        final Class<?> clazz = null;
        // When / Then
        try {
            factory.getSerialiser(clazz);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfNoSerialiserFound() throws SerialisationException {
        // Given
        final SerialisationFactory factory = new SerialisationFactory();
        final Class<?> clazz = Object.class;
        // When / Then
        try {
            factory.getSerialiser(clazz);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldReturnCustomSerialiserIfCustomSerialiserFound() throws SerialisationException {
        // Given
        final Serialiser[] serialisers = new Serialiser[]{ new RawDateSerialiser(), new RawDoubleSerialiser(), new RawFloatSerialiser() };
        final SerialisationFactory factory = new SerialisationFactory(serialisers);
        final Class<?> clazz = Double.class;
        // When
        final Serialiser serialiser = factory.getSerialiser(clazz);
        // Then
        Assert.assertTrue(serialiser.canHandle(clazz));
        Assert.assertEquals(RawDoubleSerialiser.class, serialiser.getClass());
    }

    @Test
    public void shouldReturnJavaSerialiserIfNoCustomSerialiserFound() throws SerialisationException {
        // Given
        final Serialiser[] serialisers = new Serialiser[]{ new RawDateSerialiser(), new RawDoubleSerialiser(), new RawFloatSerialiser() };
        final SerialisationFactory factory = new SerialisationFactory(serialisers);
        final Class<?> clazz = String.class;
        // When
        final Serialiser serialiser = factory.getSerialiser(clazz);
        // Then
        Assert.assertTrue(serialiser.canHandle(clazz));
        Assert.assertEquals(JavaSerialiser.class, serialiser.getClass());
    }

    @Test
    public void testAddSerialisers() throws SerialisationException {
        // Given
        final Serialiser[] serialisers = new Serialiser[]{ new RawDateSerialiser(), new RawDoubleSerialiser(), new RawFloatSerialiser() };
        final SerialisationFactory factory = new SerialisationFactory(serialisers);
        final Class<?> clazz = String.class;
        // When
        factory.addSerialisers(new StringSerialiser());
        Serialiser serialiser = factory.getSerialiser(clazz);
        // Then
        Assert.assertTrue(serialiser.canHandle(clazz));
        Assert.assertEquals(StringSerialiser.class, serialiser.getClass());
    }

    @Test
    public void shouldNotReAddClassToFactory() throws SerialisationException {
        // Given / new factory created with only 1 element
        final Serialiser[] serialisers = new Serialiser[]{ new BooleanSerialiser() };
        final SerialisationFactory factory = new SerialisationFactory(serialisers);
        // When
        factory.addSerialisers(new BooleanSerialiser());
        // Then / still has 1 element, BooleanSerialiser already existed
        Assert.assertEquals(1, factory.getSerialisers().size());
    }
}

