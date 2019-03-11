/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.aggregation;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.druid.segment.ColumnSelectorFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TimestampMinMaxAggregatorTest {
    Injector injector;

    ObjectMapper mapper;

    private TimestampAggregatorFactory aggregatorFactory;

    private ColumnSelectorFactory selectorFactory;

    private TestObjectColumnSelector selector;

    private Timestamp[] values = new Timestamp[]{ Timestamp.valueOf("2014-01-02 11:00:00"), Timestamp.valueOf("2014-01-02 01:00:00"), Timestamp.valueOf("2014-01-02 05:00:00"), Timestamp.valueOf("2014-01-02 12:00:00"), Timestamp.valueOf("2014-01-02 12:00:00"), Timestamp.valueOf("2014-01-02 13:00:00"), Timestamp.valueOf("2014-01-02 06:00:00"), Timestamp.valueOf("2014-01-02 17:00:00"), Timestamp.valueOf("2014-01-02 12:00:00"), Timestamp.valueOf("2014-01-02 02:00:00") };

    private String aggType;

    private Class<? extends TimestampAggregatorFactory> aggClass;

    private Long initValue;

    private Timestamp expected;

    public TimestampMinMaxAggregatorTest(String aggType, Class<? extends TimestampAggregatorFactory> aggClass, Long initValue, Timestamp expected) {
        this.aggType = aggType;
        this.aggClass = aggClass;
        this.expected = expected;
        this.initValue = initValue;
    }

    @Test
    public void testAggregator() {
        TimestampAggregator aggregator = ((TimestampAggregator) (aggregatorFactory.factorize(selectorFactory)));
        Assert.assertEquals(initValue, aggregator.get());
        for (Timestamp value : values) {
            aggregate(selector, aggregator);
        }
        Assert.assertEquals(expected, new Timestamp(aggregator.getLong()));
    }

    @Test
    public void testBufferAggregator() {
        TimestampBufferAggregator aggregator = ((TimestampBufferAggregator) (aggregatorFactory.factorizeBuffered(selectorFactory)));
        ByteBuffer buffer = ByteBuffer.wrap(new byte[Long.BYTES]);
        aggregator.init(buffer, 0);
        for (Timestamp value : values) {
            aggregate(selector, aggregator, buffer, 0);
        }
        Assert.assertEquals(expected, new Timestamp(aggregator.getLong(buffer, 0)));
        aggregator.init(buffer, 0);
        Assert.assertEquals(initValue, aggregator.get(buffer, 0));
    }
}

