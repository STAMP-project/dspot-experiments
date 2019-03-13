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
package uk.gov.gchq.gaffer.accumulostore.operation.hdfs.reducer;


import AccumuloStoreConstants.ACCUMULO_ELEMENT_CONVERTER_CLASS;
import Reducer.Context;
import TestGroups.ENTITY;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.accumulo.core.data.ByteSequence;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Reducer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.accumulostore.key.AccumuloElementConverter;
import uk.gov.gchq.gaffer.accumulostore.key.MockAccumuloElementConverter;
import uk.gov.gchq.gaffer.commonutil.StringUtil;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;


public class AccumuloKeyValueReducerTest {
    @Test
    public void shouldGetGroupFromElementConverter() throws IOException, InterruptedException {
        // Given
        MockAccumuloElementConverter.mock = Mockito.mock(AccumuloElementConverter.class);
        final Key key = Mockito.mock(Key.class);
        final List<Value> values = Arrays.asList(Mockito.mock(Value.class), Mockito.mock(Value.class));
        final Reducer.Context context = Mockito.mock(Context.class);
        final Configuration conf = Mockito.mock(Configuration.class);
        final Schema schema = new Schema.Builder().edge(ENTITY, new SchemaEdgeDefinition()).build();
        final ByteSequence colFamData = Mockito.mock(ByteSequence.class);
        final byte[] colFam = StringUtil.toBytes(ENTITY);
        BDDMockito.given(context.nextKey()).willReturn(true, false);
        BDDMockito.given(context.getCurrentKey()).willReturn(key);
        BDDMockito.given(context.getValues()).willReturn(values);
        BDDMockito.given(context.getConfiguration()).willReturn(conf);
        BDDMockito.given(context.getCounter(ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(Mockito.mock(Counter.class));
        BDDMockito.given(conf.get(SCHEMA)).willReturn(StringUtil.toString(schema.toCompactJson()));
        BDDMockito.given(conf.get(ACCUMULO_ELEMENT_CONVERTER_CLASS)).willReturn(MockAccumuloElementConverter.class.getName());
        BDDMockito.given(colFamData.getBackingArray()).willReturn(colFam);
        BDDMockito.given(key.getColumnFamilyData()).willReturn(colFamData);
        BDDMockito.given(MockAccumuloElementConverter.mock.getGroupFromColumnFamily(colFam)).willReturn(ENTITY);
        final AccumuloKeyValueReducer reducer = new AccumuloKeyValueReducer();
        // When
        reducer.run(context);
        // Then
        Mockito.verify(MockAccumuloElementConverter.mock, Mockito.times(1)).getGroupFromColumnFamily(colFam);
    }
}

