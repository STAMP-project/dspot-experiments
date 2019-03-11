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
package uk.gov.gchq.gaffer.accumulostore.key.impl;


import AccumuloStoreConstants.ACCUMULO_ELEMENT_CONVERTER_CLASS;
import AccumuloStoreConstants.SCHEMA;
import TestGroups.ENTITY;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.accumulo.core.data.ByteSequence;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.IteratorEnvironment;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.accumulostore.key.AccumuloElementConverter;
import uk.gov.gchq.gaffer.accumulostore.key.MockAccumuloElementConverter;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.commonutil.StringUtil;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;


public class AggregatorIteratorTest {
    private static final Schema schema = Schema.fromJson(StreamUtil.schemas(AggregatorIteratorTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AggregatorIteratorTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(AggregatorIteratorTest.class, "/accumuloStoreClassicKeys.properties"));

    private static AccumuloStore byteEntityStore;

    private static AccumuloStore gaffer1KeyStore;

    @Test
    public void test() throws OperationException {
        test(AggregatorIteratorTest.byteEntityStore);
        test(AggregatorIteratorTest.gaffer1KeyStore);
    }

    @Test
    public void shouldGetGroupFromElementConverter() throws IOException {
        MockAccumuloElementConverter.cleanUp();
        // Given
        MockAccumuloElementConverter.mock = Mockito.mock(AccumuloElementConverter.class);
        final Key key = Mockito.mock(Key.class);
        final List<Value> values = Arrays.asList(Mockito.mock(Value.class), Mockito.mock(Value.class));
        final Schema schema = new Schema.Builder().edge(ENTITY, new SchemaEdgeDefinition()).build();
        final ByteSequence colFamData = Mockito.mock(ByteSequence.class);
        final byte[] colFam = StringUtil.toBytes(ENTITY);
        final SortedKeyValueIterator sortedKeyValueIterator = Mockito.mock(SortedKeyValueIterator.class);
        final IteratorEnvironment iteratorEnvironment = Mockito.mock(IteratorEnvironment.class);
        final Map<String, String> options = new HashMap();
        options.put("columns", "test");
        options.put(SCHEMA, new String(schema.toCompactJson()));
        options.put(ACCUMULO_ELEMENT_CONVERTER_CLASS, MockAccumuloElementConverter.class.getName());
        BDDMockito.given(colFamData.getBackingArray()).willReturn(colFam);
        BDDMockito.given(key.getColumnFamilyData()).willReturn(colFamData);
        BDDMockito.given(MockAccumuloElementConverter.mock.getGroupFromColumnFamily(colFam)).willReturn(ENTITY);
        final AggregatorIterator aggregatorIterator = new AggregatorIterator();
        // When
        aggregatorIterator.init(sortedKeyValueIterator, options, iteratorEnvironment);
        aggregatorIterator.reduce(key, values.iterator());
        // Then
        Mockito.verify(MockAccumuloElementConverter.mock, Mockito.times(1)).getGroupFromColumnFamily(colFam);
        MockAccumuloElementConverter.cleanUp();
    }
}

