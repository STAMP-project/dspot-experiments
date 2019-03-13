/**
 * Copyright 2018-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.sparkaccumulo.operation.utils;


import java.util.Arrays;
import java.util.List;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.admin.TableOperations;
import org.apache.accumulo.core.client.impl.ConnectorImpl;
import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;


public class AccumuloKeyRangePartitionerTest {
    @Test
    public void shouldGetSplitsInOrder() throws Exception {
        // Given
        final AccumuloStore store = Mockito.mock(AccumuloStore.class);
        final Connector connector = Mockito.mock(ConnectorImpl.class);
        final TableOperations tableOperations = Mockito.mock(TableOperations.class);
        final String testTableName = "tableName";
        final List<Text> unsortedTextCollection = Arrays.asList(new Text("z"), new Text("f"), new Text("g"), new Text("a"));
        final List<String> sortedStringCollection = Arrays.asList("a", "f", "g", "z");
        BDDMockito.given(store.getConnection()).willReturn(connector);
        BDDMockito.given(store.getTableName()).willReturn(testTableName);
        BDDMockito.given(connector.tableOperations()).willReturn(tableOperations);
        BDDMockito.given(connector.tableOperations().listSplits(testTableName)).willReturn(unsortedTextCollection);
        // When
        final List<String> splits = Arrays.asList(AccumuloKeyRangePartitioner.getSplits(store));
        // Then
        Assert.assertEquals(sortedStringCollection, splits);
    }
}

