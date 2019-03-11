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
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.hbasestore.serialisation.ElementSerialisation;
import uk.gov.gchq.gaffer.hbasestore.util.CellUtil;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;


public class PostAggregationFilterProcessorTest {
    private static final Schema SCHEMA = new Schema.Builder().type("string", String.class).type("type", Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").build()).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").build()).vertexSerialiser(new StringSerialiser()).build();

    private static final View VIEW = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(VERTEX.name()).execute("validPreAggVertex"::equals).build()).postAggregationFilter(new ElementFilter.Builder().select(VERTEX.name()).execute("validPostAggVertex"::equals).build()).postTransformFilter(new ElementFilter.Builder().select(VERTEX.name()).execute("validPostTransformVertex"::equals).build()).build()).edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(SOURCE.name()).execute("validPreAggVertex"::equals).build()).postAggregationFilter(new ElementFilter.Builder().select(SOURCE.name()).execute("validPostAggVertex"::equals).build()).postTransformFilter(new ElementFilter.Builder().select(SOURCE.name()).execute("validPostTransformVertex"::equals).build()).build()).build();

    private final ElementSerialisation serialisation = new ElementSerialisation(PostAggregationFilterProcessorTest.SCHEMA);

    @Test
    public void shouldConstructWithView() throws SerialisationException, OperationException {
        final PostAggregationFilterProcessor processor = new PostAggregationFilterProcessor(PostAggregationFilterProcessorTest.VIEW);
        Assert.assertEquals(PostAggregationFilterProcessorTest.VIEW, processor.getView());
    }

    @Test
    public void shouldFilterOutInvalidEntity() throws SerialisationException, OperationException {
        // Given
        final PostAggregationFilterProcessor processor = new PostAggregationFilterProcessor(PostAggregationFilterProcessorTest.VIEW);
        // When / Then
        Assert.assertFalse(processor.test(CellUtil.getLazyCell(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "invalidVertex"), serialisation)));
        Assert.assertFalse(processor.test(CellUtil.getLazyCell(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "validPreAggVertex"), serialisation)));
        Assert.assertFalse(processor.test(CellUtil.getLazyCell(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "validPostTransformVertex"), serialisation)));
    }

    @Test
    public void shouldNotFilterOutValidEntity() throws SerialisationException, OperationException {
        // Given
        final PostAggregationFilterProcessor processor = new PostAggregationFilterProcessor(PostAggregationFilterProcessorTest.VIEW);
        // When / Then
        Assert.assertTrue(processor.test(CellUtil.getLazyCell(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "validPostAggVertex"), serialisation)));
    }

    @Test
    public void shouldFilterOutInvalidEdge() throws SerialisationException, OperationException {
        // Given
        final PostAggregationFilterProcessor processor = new PostAggregationFilterProcessor(PostAggregationFilterProcessorTest.VIEW);
        // When / Then
        Assert.assertFalse(processor.test(CellUtil.getLazyCell(new Edge.Builder().group(EDGE).source("invalidVertex").dest("dest").directed(true).build(), serialisation)));
        Assert.assertFalse(processor.test(CellUtil.getLazyCell(new Edge.Builder().group(EDGE).source("validPreAggVertex").dest("dest").directed(true).build(), serialisation)));
        Assert.assertFalse(processor.test(CellUtil.getLazyCell(new Edge.Builder().group(EDGE).source("validPostTransformVertex").dest("dest").directed(true).build(), serialisation)));
    }

    @Test
    public void shouldNotFilterOutValidEdge() throws SerialisationException, OperationException {
        // Given
        final PostAggregationFilterProcessor processor = new PostAggregationFilterProcessor(PostAggregationFilterProcessorTest.VIEW);
        // When / Then
        Assert.assertTrue(processor.test(CellUtil.getLazyCell(new Edge.Builder().group(EDGE).source("validPostAggVertex").dest("dest").directed(true).build(), serialisation)));
    }
}

