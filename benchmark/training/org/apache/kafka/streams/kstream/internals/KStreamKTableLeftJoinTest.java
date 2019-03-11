/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.kstream.internals;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.test.MockProcessor;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.kafka.streams.TopologyWrapper.getInternalTopologyBuilder;


public class KStreamKTableLeftJoinTest {
    private final String streamTopic = "streamTopic";

    private final String tableTopic = "tableTopic";

    private final ConsumerRecordFactory<Integer, String> recordFactory = new ConsumerRecordFactory(new IntegerSerializer(), new StringSerializer());

    private TopologyTestDriver driver;

    private MockProcessor<Integer, String> processor;

    private final int[] expectedKeys = new int[]{ 0, 1, 2, 3 };

    private StreamsBuilder builder;

    @Test
    public void shouldRequireCopartitionedStreams() {
        final Collection<Set<String>> copartitionGroups = getInternalTopologyBuilder(builder.build()).copartitionGroups();
        Assert.assertEquals(1, copartitionGroups.size());
        Assert.assertEquals(new HashSet<>(Arrays.asList(streamTopic, tableTopic)), copartitionGroups.iterator().next());
    }

    @Test
    public void shouldJoinWithEmptyTableOnStreamUpdates() {
        // push two items to the primary stream. the table is empty
        pushToStream(2, "X");
        processor.checkAndClearProcessResult("0:X0+null", "1:X1+null");
    }

    @Test
    public void shouldNotJoinOnTableUpdates() {
        // push two items to the primary stream. the table is empty
        pushToStream(2, "X");
        processor.checkAndClearProcessResult("0:X0+null", "1:X1+null");
        // push two items to the table. this should not produce any item.
        pushToTable(2, "Y");
        processor.checkAndClearProcessResult();
        // push all four items to the primary stream. this should produce four items.
        pushToStream(4, "X");
        processor.checkAndClearProcessResult("0:X0+Y0", "1:X1+Y1", "2:X2+null", "3:X3+null");
        // push all items to the table. this should not produce any item
        pushToTable(4, "YY");
        processor.checkAndClearProcessResult();
        // push all four items to the primary stream. this should produce four items.
        pushToStream(4, "X");
        processor.checkAndClearProcessResult("0:X0+YY0", "1:X1+YY1", "2:X2+YY2", "3:X3+YY3");
        // push all items to the table. this should not produce any item
        pushToTable(4, "YYY");
        processor.checkAndClearProcessResult();
    }

    @Test
    public void shouldJoinRegardlessIfMatchFoundOnStreamUpdates() {
        // push two items to the table. this should not produce any item.
        pushToTable(2, "Y");
        processor.checkAndClearProcessResult();
        // push all four items to the primary stream. this should produce four items.
        pushToStream(4, "X");
        processor.checkAndClearProcessResult("0:X0+Y0", "1:X1+Y1", "2:X2+null", "3:X3+null");
    }

    @Test
    public void shouldClearTableEntryOnNullValueUpdates() {
        // push all four items to the table. this should not produce any item.
        pushToTable(4, "Y");
        processor.checkAndClearProcessResult();
        // push all four items to the primary stream. this should produce four items.
        pushToStream(4, "X");
        processor.checkAndClearProcessResult("0:X0+Y0", "1:X1+Y1", "2:X2+Y2", "3:X3+Y3");
        // push two items with null to the table as deletes. this should not produce any item.
        pushNullValueToTable(2);
        processor.checkAndClearProcessResult();
        // push all four items to the primary stream. this should produce four items.
        pushToStream(4, "XX");
        processor.checkAndClearProcessResult("0:XX0+null", "1:XX1+null", "2:XX2+Y2", "3:XX3+Y3");
    }
}

