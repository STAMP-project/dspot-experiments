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


import KeyValue.Type.Delete;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.hbasestore.serialisation.ElementSerialisation;
import uk.gov.gchq.gaffer.hbasestore.serialisation.LazyElementCell;
import uk.gov.gchq.gaffer.hbasestore.util.CellUtil;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;


public class FilterProcessorTest {
    private static final Schema SCHEMA = new Schema.Builder().type("string", String.class).type("type", Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").build()).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").build()).vertexSerialiser(new StringSerialiser()).build();

    private static final List<Element> ELEMENTS = Arrays.asList(new Entity.Builder().group(ENTITY).vertex("vertexI").build(), new Edge.Builder().group(EDGE).source("vertexA").dest("vertexB").directed(true).build(), new Edge.Builder().group(EDGE).source("vertexD").dest("vertexC").directed(true).build(), new Edge.Builder().group(EDGE).source("vertexE").dest("vertexE").directed(true).build(), new Edge.Builder().group(EDGE).source("vertexF").dest("vertexG").directed(false).build(), new Edge.Builder().group(EDGE).source("vertexH").dest("vertexH").directed(false).build());

    private final ElementSerialisation serialisation = new ElementSerialisation(FilterProcessorTest.SCHEMA);

    @Test
    public void shouldSkipDeletedAndRemoveInvalidElements() throws SerialisationException, OperationException {
        // Given
        final FilterProcessor processor = new FilterProcessor() {
            @Override
            public boolean test(final LazyElementCell elementCell) {
                return (elementCell.getElement()) instanceof Entity;
            }
        };
        final List<LazyElementCell> lazyCells = CellUtil.getLazyCells(FilterProcessorTest.ELEMENTS, serialisation);
        final Cell deletedCell = Mockito.mock(Cell.class);
        BDDMockito.given(deletedCell.getTypeByte()).willReturn(Delete.getCode());
        lazyCells.add(new LazyElementCell(deletedCell, serialisation, false));
        // When
        final List<LazyElementCell> result = processor.process(lazyCells);
        // When / Then
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(FilterProcessorTest.ELEMENTS.get(0), result.get(0).getElement());
        Assert.assertEquals(deletedCell, result.get(1).getCell());
    }
}

