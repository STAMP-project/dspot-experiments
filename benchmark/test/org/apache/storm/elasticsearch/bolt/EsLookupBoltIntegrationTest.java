/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.elasticsearch.bolt;


import org.apache.storm.elasticsearch.common.EsTestUtil;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.elasticsearch.client.ResponseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class EsLookupBoltIntegrationTest extends AbstractEsBoltIntegrationTest<EsLookupBolt> {
    @Captor
    private ArgumentCaptor<Tuple> anchor;

    @Captor
    private ArgumentCaptor<Values> emmitedValues;

    private Tuple tuple = EsTestUtil.generateTestTuple(AbstractEsBoltTest.source, AbstractEsBoltTest.index, AbstractEsBoltTest.type, AbstractEsBoltTest.documentId);

    @Test
    public void anchorsTheTuple() throws Exception {
        bolt.execute(tuple);
        Mockito.verify(outputCollector).emit(anchor.capture(), emmitedValues.capture());
        Assert.assertThat(anchor.getValue(), CoreMatchers.is(tuple));
    }

    @Test
    public void emitsExpectedValues() throws Exception {
        Values expectedValues = expectedValues();
        bolt.execute(tuple);
        Mockito.verify(outputCollector).emit(anchor.capture(), emmitedValues.capture());
        Assert.assertThat(emmitedValues.getValue(), CoreMatchers.is(expectedValues));
    }

    @Test
    public void acksTuple() throws Exception {
        bolt.execute(tuple);
        Mockito.verify(outputCollector).ack(anchor.capture());
        Assert.assertThat(anchor.getValue(), CoreMatchers.is(tuple));
    }

    @Test
    public void indexMissing() throws Exception {
        Tuple tuple = EsTestUtil.generateTestTuple(AbstractEsBoltTest.source, "missing", AbstractEsBoltTest.type, AbstractEsBoltTest.documentId);
        bolt.execute(tuple);
        Mockito.verify(outputCollector, Mockito.never()).emit(ArgumentMatchers.any(Tuple.class), ArgumentMatchers.any(Values.class));
        Mockito.verify(outputCollector).reportError(ArgumentMatchers.any(ResponseException.class));
        Mockito.verify(outputCollector).fail(tuple);
    }
}

