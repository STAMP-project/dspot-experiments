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


import DirectedType.DIRECTED;
import HBaseStoreConstants.DIRECTED_TYPE;
import HBaseStoreConstants.EXTRA_PROCESSORS;
import HBaseStoreConstants.VIEW;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.regionserver.RegionScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.StringUtil;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.ElementDedupeFilterProcessor;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.GafferScannerProcessor;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.GroupFilterProcessor;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.PostAggregationFilterProcessor;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.PreAggregationFilterProcessor;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.QueryAggregationProcessor;
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


public class QueryScannerTest {
    private static final Schema SCHEMA = new Schema.Builder().type("string", new TypeDefinition.Builder().clazz(String.class).aggregateFunction(new StringConcat()).build()).type("type", Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").build()).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").build()).vertexSerialiser(new StringSerialiser()).build();

    private static final Schema SCHEMA_NO_AGGREGATION = new Schema.Builder().type("string", String.class).type("type", Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").aggregate(false).build()).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").aggregate(false).build()).vertexSerialiser(new StringSerialiser()).build();

    private static final View VIEW = new View.Builder().entity(ENTITY_2).edge(EDGE_2).build();

    private final ElementSerialisation serialisation = new ElementSerialisation(QueryScannerTest.SCHEMA);

    @Test
    public void shouldConstructProcessors() throws IOException, OperationException {
        // Given
        final Scan scan = Mockito.mock(Scan.class);
        BDDMockito.given(scan.getAttribute(HBaseStoreConstants.VIEW)).willReturn(QueryScannerTest.VIEW.toCompactJson());
        BDDMockito.given(scan.getAttribute(EXTRA_PROCESSORS)).willReturn(StringUtil.toCsv(ElementDedupeFilterProcessor.class));
        BDDMockito.given(scan.getAttribute(DIRECTED_TYPE)).willReturn(Bytes.toBytes(DIRECTED.name()));
        // When
        final List<GafferScannerProcessor> processors = QueryScanner.createProcessors(scan, QueryScannerTest.SCHEMA, serialisation);
        // Then
        Assert.assertEquals(7, processors.size());
        int i = 0;
        Assert.assertTrue(((processors.get(i)) instanceof GroupFilterProcessor));
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof ElementDedupeFilterProcessor));
        Assert.assertTrue(isEntities());
        Assert.assertTrue(isEdges());
        Assert.assertTrue(isDirectedEdges());
        Assert.assertFalse(isUnDirectedEdges());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof StoreAggregationProcessor));
        Assert.assertEquals(QueryScannerTest.SCHEMA, getSchema());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof ValidationProcessor));
        Assert.assertEquals(QueryScannerTest.SCHEMA, getSchema());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof PreAggregationFilterProcessor));
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof QueryAggregationProcessor));
        Assert.assertEquals(QueryScannerTest.SCHEMA, getSchema());
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof PostAggregationFilterProcessor));
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
    }

    @Test
    public void shouldConstructProcessorsWithNoAggregation() throws IOException, OperationException {
        // Given
        final Scan scan = Mockito.mock(Scan.class);
        BDDMockito.given(scan.getAttribute(HBaseStoreConstants.VIEW)).willReturn(QueryScannerTest.VIEW.toCompactJson());
        BDDMockito.given(scan.getAttribute(EXTRA_PROCESSORS)).willReturn(StringUtil.toCsv(ElementDedupeFilterProcessor.class));
        // When
        final List<GafferScannerProcessor> processors = QueryScanner.createProcessors(scan, QueryScannerTest.SCHEMA_NO_AGGREGATION, serialisation);
        // Then
        Assert.assertEquals(5, processors.size());
        int i = 0;
        Assert.assertTrue(((processors.get(i)) instanceof GroupFilterProcessor));
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof ElementDedupeFilterProcessor));
        Assert.assertTrue(isEntities());
        Assert.assertTrue(isEdges());
        Assert.assertFalse(isDirectedEdges());
        Assert.assertFalse(isUnDirectedEdges());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof ValidationProcessor));
        Assert.assertEquals(QueryScannerTest.SCHEMA_NO_AGGREGATION, getSchema());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof PreAggregationFilterProcessor));
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof PostAggregationFilterProcessor));
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
    }

    @Test
    public void shouldConstructProcessorsWhenViewIsNull() throws IOException, OperationException {
        // Given
        final Scan scan = Mockito.mock(Scan.class);
        BDDMockito.given(scan.getAttribute(HBaseStoreConstants.VIEW)).willReturn(null);
        // When
        final List<GafferScannerProcessor> processors = QueryScanner.createProcessors(scan, QueryScannerTest.SCHEMA, serialisation);
        // Then
        Assert.assertEquals(2, processors.size());
        int i = 0;
        Assert.assertTrue(((processors.get(i)) instanceof StoreAggregationProcessor));
        Assert.assertEquals(QueryScannerTest.SCHEMA, getSchema());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof ValidationProcessor));
        Assert.assertEquals(QueryScannerTest.SCHEMA, getSchema());
    }

    @Test
    public void shouldConstructProcessorsWithNoExtras() throws IOException, OperationException {
        // Given
        final Scan scan = Mockito.mock(Scan.class);
        BDDMockito.given(scan.getAttribute(HBaseStoreConstants.VIEW)).willReturn(QueryScannerTest.VIEW.toCompactJson());
        BDDMockito.given(scan.getAttribute(EXTRA_PROCESSORS)).willReturn(null);
        // When
        final List<GafferScannerProcessor> processors = QueryScanner.createProcessors(scan, QueryScannerTest.SCHEMA, serialisation);
        // Then
        Assert.assertEquals(6, processors.size());
        int i = 0;
        Assert.assertTrue(((processors.get(i)) instanceof GroupFilterProcessor));
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof StoreAggregationProcessor));
        Assert.assertEquals(QueryScannerTest.SCHEMA, getSchema());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof ValidationProcessor));
        Assert.assertEquals(QueryScannerTest.SCHEMA, getSchema());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof PreAggregationFilterProcessor));
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof QueryAggregationProcessor));
        Assert.assertEquals(QueryScannerTest.SCHEMA, getSchema());
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
        i++;
        Assert.assertTrue(((processors.get(i)) instanceof PostAggregationFilterProcessor));
        Assert.assertEquals(QueryScannerTest.VIEW, getView());
    }

    @Test
    public void shouldThrowErrorWhenInvalidExtras() throws IOException, OperationException {
        // Given
        final Scan scan = Mockito.mock(Scan.class);
        BDDMockito.given(scan.getAttribute(HBaseStoreConstants.VIEW)).willReturn(null);
        BDDMockito.given(scan.getAttribute(EXTRA_PROCESSORS)).willReturn(StringUtil.toCsv(ElementDedupeFilterProcessor.class));
        // When / Then
        try {
            QueryScanner.createProcessors(scan, QueryScannerTest.SCHEMA, serialisation);
            Assert.fail("Exception expected");
        } catch (final RuntimeException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldDelegateMethodsToInternalScanner() throws IOException {
        final RegionScanner scanner = Mockito.mock(RegionScanner.class);
        final Scan scan = Mockito.mock(Scan.class);
        final QueryScanner queryScanner = new QueryScanner(scanner, scan, QueryScannerTest.SCHEMA, serialisation);
        Assert.assertSame(scanner, queryScanner.getScanner());
        final HRegionInfo regionInfo = Mockito.mock(HRegionInfo.class);
        BDDMockito.given(scanner.getRegionInfo()).willReturn(regionInfo);
        Assert.assertSame(regionInfo, queryScanner.getRegionInfo());
        Mockito.verify(scanner).getRegionInfo();
        BDDMockito.given(scanner.isFilterDone()).willReturn(true);
        Assert.assertTrue(queryScanner.isFilterDone());
        Mockito.verify(scanner).isFilterDone();
        final byte[] bytes = new byte[]{ 0, 1, 2, 3 };
        BDDMockito.given(scanner.reseek(bytes)).willReturn(true);
        Assert.assertTrue(queryScanner.reseek(bytes));
        Mockito.verify(scanner).reseek(bytes);
        BDDMockito.given(scanner.getMaxResultSize()).willReturn(100L);
        Assert.assertEquals(100L, queryScanner.getMaxResultSize());
        Mockito.verify(scanner).getMaxResultSize();
        BDDMockito.given(scanner.getMvccReadPoint()).willReturn(200L);
        Assert.assertEquals(200L, queryScanner.getMvccReadPoint());
        Mockito.verify(scanner).getMvccReadPoint();
        BDDMockito.given(scanner.getBatch()).willReturn(2);
        Assert.assertEquals(2, queryScanner.getBatch());
        Mockito.verify(scanner).getBatch();
    }
}

