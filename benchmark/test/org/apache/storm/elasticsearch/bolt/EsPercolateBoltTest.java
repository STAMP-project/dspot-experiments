/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.elasticsearch.bolt;


import PercolateResponse.Match;
import org.apache.storm.elasticsearch.common.EsTestUtil;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.elasticsearch.client.ResponseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EsPercolateBoltTest extends AbstractEsBoltIntegrationTest<EsPercolateBolt> {
    private final String source = "{\"user\":\"user1\"}";

    @Test
    public void testEsPercolateBolt() throws Exception {
        Tuple tuple = EsTestUtil.generateTestTuple(source, AbstractEsBoltTest.index, AbstractEsBoltTest.type, null);
        bolt.execute(tuple);
        Mockito.verify(outputCollector).ack(tuple);
        ArgumentCaptor<Values> emitCaptor = ArgumentCaptor.forClass(Values.class);
        Mockito.verify(outputCollector).emit(emitCaptor.capture());
        Assert.assertThat(emitCaptor.getValue().get(0), CoreMatchers.is(source));
        Assert.assertThat(emitCaptor.getValue().get(1), CoreMatchers.instanceOf(Match.class));
    }

    @Test
    public void noDocumentsMatch() throws Exception {
        Tuple tuple = EsTestUtil.generateTestTuple("{\"user\":\"user2\"}", AbstractEsBoltTest.index, AbstractEsBoltTest.type, null);
        bolt.execute(tuple);
        Mockito.verify(outputCollector).ack(tuple);
        Mockito.verify(outputCollector, Mockito.never()).emit(ArgumentMatchers.any(Values.class));
    }

    @Test
    public void indexMissing() throws Exception {
        String index = "missing";
        Tuple tuple = EsTestUtil.generateTestTuple(source, index, AbstractEsBoltTest.type, null);
        bolt.execute(tuple);
        Mockito.verify(outputCollector, Mockito.never()).emit(ArgumentMatchers.any(Values.class));
        Mockito.verify(outputCollector).reportError(ArgumentMatchers.any(ResponseException.class));
        Mockito.verify(outputCollector).fail(tuple);
    }
}

