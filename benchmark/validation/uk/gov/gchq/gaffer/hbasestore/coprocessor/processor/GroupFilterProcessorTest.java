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
package uk.gov.gchq.gaffer.hbasestore.coprocessor.processor;


import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.hbasestore.serialisation.ElementSerialisation;
import uk.gov.gchq.gaffer.hbasestore.util.CellUtil;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;


public class GroupFilterProcessorTest {
    private static final Schema SCHEMA = new Schema.Builder().type("string", String.class).type("type", Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").build()).edge(EDGE_2, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").build()).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").build()).entity(ENTITY_2, new SchemaEntityDefinition.Builder().vertex("string").build()).vertexSerialiser(new StringSerialiser()).build();

    private static final View VIEW = new View.Builder().entity(ENTITY_2).edge(EDGE_2).build();

    private final ElementSerialisation serialisation = new ElementSerialisation(GroupFilterProcessorTest.SCHEMA);

    @Test
    public void shouldConstructWithView() throws SerialisationException, OperationException {
        final GroupFilterProcessor processor = new GroupFilterProcessor(GroupFilterProcessorTest.VIEW);
        Assert.assertEquals(GroupFilterProcessorTest.VIEW, processor.getView());
    }

    @Test
    public void shouldFilterOutEntityNotInView() throws SerialisationException, OperationException {
        // Given
        final GroupFilterProcessor processor = new GroupFilterProcessor(GroupFilterProcessorTest.VIEW);
        // When
        final boolean result = processor.test(CellUtil.getLazyCell(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "vertexI"), serialisation));
        // Then
        Assert.assertFalse(result);
    }

    @Test
    public void shouldNotFilterOutEntityInView() throws SerialisationException, OperationException {
        // Given
        final GroupFilterProcessor processor = new GroupFilterProcessor(GroupFilterProcessorTest.VIEW);
        // When
        final boolean result = processor.test(CellUtil.getLazyCell(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY_2, "vertexI"), serialisation));
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldFilterOutEdgeNotInView() throws SerialisationException, OperationException {
        // Given
        final GroupFilterProcessor processor = new GroupFilterProcessor(GroupFilterProcessorTest.VIEW);
        // When
        final boolean result = processor.test(CellUtil.getLazyCell(new Edge.Builder().group(EDGE).source("A").dest("B").directed(true).build(), serialisation));
        // Then
        Assert.assertFalse(result);
    }

    @Test
    public void shouldNotFilterOutEdgeInView() throws SerialisationException, OperationException {
        // Given
        final GroupFilterProcessor processor = new GroupFilterProcessor(GroupFilterProcessorTest.VIEW);
        // When
        final boolean result = processor.test(CellUtil.getLazyCell(new Edge.Builder().group(EDGE_2).source("A").dest("B").directed(true).build(), serialisation));
        // Then
        Assert.assertTrue(result);
    }
}

