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


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.storm.elasticsearch.EsLookupResultOutput;
import org.apache.storm.elasticsearch.common.EsConfig;
import org.apache.storm.elasticsearch.common.EsTestUtil;
import org.apache.storm.elasticsearch.common.EsTupleMapper;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class EsLookupBoltTest extends AbstractEsBoltTest<EsLookupBolt> {
    static final Map<String, String> params = new HashMap<>();

    @Mock
    private EsConfig esConfig;

    @Mock
    private EsTupleMapper tupleMapper;

    @Mock
    private EsLookupResultOutput output;

    @Mock
    private Tuple tuple;

    @Mock
    private GetRequest request;

    @Mock
    private RestClient client;

    private RestClient originalClient;

    @Test
    public void failsTupleWhenClientThrows() throws Exception {
        makeRequestAndThrow(EsTestUtil.generateMockResponseException());
        Mockito.verify(outputCollector).fail(tuple);
    }

    @Test
    public void reportsExceptionWhenClientThrows() throws Exception {
        ResponseException responseException = EsTestUtil.generateMockResponseException();
        makeRequestAndThrow(responseException);
        Mockito.verify(outputCollector).reportError(responseException);
    }

    @Test
    public void fieldsAreDeclaredThroughProvidedOutput() throws Exception {
        Fields fields = new Fields(UUID.randomUUID().toString());
        Mockito.when(output.fields()).thenReturn(fields);
        OutputFieldsDeclarer declarer = Mockito.mock(OutputFieldsDeclarer.class);
        bolt.declareOutputFields(declarer);
        ArgumentCaptor<Fields> declaredFields = ArgumentCaptor.forClass(Fields.class);
        Mockito.verify(declarer).declare(declaredFields.capture());
        Assert.assertThat(declaredFields.getValue(), CoreMatchers.is(fields));
    }
}

