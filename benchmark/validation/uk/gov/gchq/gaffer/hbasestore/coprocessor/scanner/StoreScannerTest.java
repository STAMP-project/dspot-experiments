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
package uk.gov.gchq.gaffer.hbasestore.coprocessor.scanner;


import HBaseStoreConstants.EXTRA_PROCESSORS;
import HBaseStoreConstants.VIEW;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.StringUtil;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.ElementDedupeFilterProcessor;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.GafferScannerProcessor;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.StoreAggregationProcessor;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.ValidationProcessor;
import uk.gov.gchq.gaffer.hbasestore.serialisation.ElementSerialisation;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.koryphe.impl.binaryoperator.StringConcat;


public class StoreScannerTest {
    private static final Schema SCHEMA = new Schema.Builder().type("string", new TypeDefinition.Builder().clazz(String.class).aggregateFunction(new StringConcat()).build()).type("type", Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").build()).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").build()).vertexSerialiser(new StringSerialiser()).build();

    private static final Schema SCHEMA_NO_AGGREGATION = new Schema.Builder().type("string", String.class).type("type", Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").aggregate(false).build()).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").aggregate(false).build()).vertexSerialiser(new StringSerialiser()).build();

    private static final View VIEW = new View.Builder().entity(ENTITY_2).edge(EDGE_2).build();

    private final ElementSerialisation serialisation = new ElementSerialisation(StoreScannerTest.SCHEMA);

    @Test
    public void shouldConstructProcessors() throws IOException, OperationException {
        // Given
        final Scan scan = Mockito.mock(Scan.class);
        BDDMockito.given(scan.getAttribute(HBaseStoreConstants.VIEW)).willReturn(StoreScannerTest.VIEW.toCompactJson());
        BDDMockito.given(scan.getAttribute(EXTRA_PROCESSORS)).willReturn(StringUtil.toCsv(ElementDedupeFilterProcessor.class));
        // When
        final List<GafferScannerProcessor> processors = StoreScanner.createProcessors(StoreScannerTest.SCHEMA, serialisation);
        // Then
        Assert.assertEquals(2, processors.size());
        int i = 0;
        Assert.assertTrue(((processors.get(i)) instanceof StoreAggregationProcessor));
        Assert.assertEquals(StoreScannerTest.SCHEMA, getSchema());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof ValidationProcessor));
        Assert.assertEquals(StoreScannerTest.SCHEMA, getSchema());
    }

    @Test
    public void shouldConstructProcessorsWithNoAggregation() throws IOException, OperationException {
        // Given
        final Scan scan = Mockito.mock(Scan.class);
        BDDMockito.given(scan.getAttribute(HBaseStoreConstants.VIEW)).willReturn(StoreScannerTest.VIEW.toCompactJson());
        BDDMockito.given(scan.getAttribute(EXTRA_PROCESSORS)).willReturn(StringUtil.toCsv(ElementDedupeFilterProcessor.class));
        // When
        final List<GafferScannerProcessor> processors = StoreScanner.createProcessors(StoreScannerTest.SCHEMA_NO_AGGREGATION, serialisation);
        // Then
        Assert.assertEquals(1, processors.size());
        int i = 0;
        Assert.assertTrue(((processors.get(i)) instanceof ValidationProcessor));
        Assert.assertEquals(StoreScannerTest.SCHEMA_NO_AGGREGATION, getSchema());
    }
}

