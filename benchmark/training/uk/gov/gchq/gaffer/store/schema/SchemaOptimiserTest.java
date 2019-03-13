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


import TestGroups.ENTITY_2;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.serialisation.implementation.JavaSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.ordered.OrderedIntegerSerialiser;
import uk.gov.gchq.gaffer.store.SerialisationFactory;


public class SchemaOptimiserTest {
    private TypeDefinition stringType;

    private TypeDefinition intType;

    private Schema schema;

    @Test
    public void shouldRemoveUnusedTypes() {
        // Given
        final SchemaOptimiser optimiser = new SchemaOptimiser();
        final boolean isOrdered = true;
        // When
        final Schema optimisedSchema = optimiser.optimise(schema, isOrdered);
        // Then
        Assert.assertEquals(2, optimisedSchema.getTypes().size());
        Assert.assertEquals(stringType, schema.getType("string"));
        Assert.assertEquals(intType, schema.getType("int"));
    }

    @Test
    public void shouldAddDefaultSerialisers() {
        // Given
        final SerialisationFactory serialisationFactory = Mockito.mock(SerialisationFactory.class);
        final SchemaOptimiser optimiser = new SchemaOptimiser(serialisationFactory);
        final boolean isOrdered = true;
        final StringSerialiser stringSerialiser = Mockito.mock(StringSerialiser.class);
        final OrderedIntegerSerialiser intSerialiser = Mockito.mock(OrderedIntegerSerialiser.class);
        final JavaSerialiser javaSerialiser = Mockito.mock(JavaSerialiser.class);
        BDDMockito.given(serialisationFactory.getSerialiser(String.class, true, true)).willReturn(stringSerialiser);
        BDDMockito.given(serialisationFactory.getSerialiser(Integer.class, false, false)).willReturn(intSerialiser);
        BDDMockito.given(serialisationFactory.getSerialiser(Serializable.class, true, true)).willReturn(javaSerialiser);
        BDDMockito.given(javaSerialiser.canHandle(Mockito.any(Class.class))).willReturn(true);
        schema = new Schema.Builder().merge(schema).type("obj", Serializable.class).entity(ENTITY_2, new SchemaEntityDefinition.Builder().vertex("obj").build()).build();
        // When
        final Schema optimisedSchema = optimiser.optimise(schema, isOrdered);
        // Then
        Assert.assertSame(stringSerialiser, optimisedSchema.getType("string").getSerialiser());
        Assert.assertSame(intSerialiser, optimisedSchema.getType("int").getSerialiser());
        Assert.assertSame(javaSerialiser, optimisedSchema.getVertexSerialiser());
        Mockito.verify(serialisationFactory, Mockito.never()).getSerialiser(String.class, false, true);
        Mockito.verify(serialisationFactory, Mockito.never()).getSerialiser(Serializable.class, false, true);
    }

    @Test
    public void shouldThrowExceptionIfDefaultVertexSerialiserCouldNotBeFound() {
        // Given
        final SchemaOptimiser optimiser = new SchemaOptimiser();
        final boolean isOrdered = true;
        // Add a new entity with vertex that can't be serialised
        schema = new Schema.Builder().merge(schema).type("obj", Object.class).entity(ENTITY_2, new SchemaEntityDefinition.Builder().vertex("obj").build()).build();
        // When / Then
        try {
            optimiser.optimise(schema, isOrdered);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }
}

