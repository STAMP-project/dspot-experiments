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


import IdentifierType.SOURCE;
import IdentifierType.VERTEX;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.hbasestore.serialisation.ElementSerialisation;
import uk.gov.gchq.gaffer.hbasestore.util.CellUtil;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;


public class ValidationProcessorTest {
    private static final Schema SCHEMA = new Schema.Builder().type("string", String.class).type("type", Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").validateFunctions(new ElementFilter.Builder().select(SOURCE.name()).execute("validVertex"::equals).build()).build()).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").validateFunctions(new ElementFilter.Builder().select(VERTEX.name()).execute("validVertex"::equals).build()).build()).vertexSerialiser(new StringSerialiser()).build();

    private final ElementSerialisation serialisation = new ElementSerialisation(ValidationProcessorTest.SCHEMA);

    @Test
    public void shouldConstructWithSchema() throws SerialisationException, OperationException {
        final ValidationProcessor processor = new ValidationProcessor(ValidationProcessorTest.SCHEMA);
        Assert.assertEquals(ValidationProcessorTest.SCHEMA, processor.getSchema());
    }

    @Test
    public void shouldFilterOutInvalidEntity() throws SerialisationException, OperationException {
        // Given
        final ValidationProcessor processor = new ValidationProcessor(ValidationProcessorTest.SCHEMA);
        // When / Then
        Assert.assertFalse(processor.test(CellUtil.getLazyCell(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "invalidVertex"), serialisation)));
    }

    @Test
    public void shouldNotFilterOutValidEntity() throws SerialisationException, OperationException {
        // Given
        final ValidationProcessor processor = new ValidationProcessor(ValidationProcessorTest.SCHEMA);
        // When / Then
        Assert.assertTrue(processor.test(CellUtil.getLazyCell(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "validVertex"), serialisation)));
    }

    @Test
    public void shouldFilterOutInvalidEdge() throws SerialisationException, OperationException {
        // Given
        final ValidationProcessor processor = new ValidationProcessor(ValidationProcessorTest.SCHEMA);
        // When / Then
        Assert.assertFalse(processor.test(CellUtil.getLazyCell(new Edge.Builder().group(EDGE).source("invalidVertex").dest("dest").directed(true).build(), serialisation)));
    }

    @Test
    public void shouldNotFilterOutValidEdge() throws SerialisationException, OperationException {
        // Given
        final ValidationProcessor processor = new ValidationProcessor(ValidationProcessorTest.SCHEMA);
        // When / Then
        Assert.assertTrue(processor.test(CellUtil.getLazyCell(new Edge.Builder().group(EDGE).source("validVertex").dest("dest").directed(true).build(), serialisation)));
    }
}

